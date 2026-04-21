package oneblock;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class PlayerInfo {
	/**
	 * Island-slot storage. Thread-safe for async reads (iterators are snapshot
	 * copies) and main-thread mutations. External callers MUST NOT mutate this
	 * list directly; go through {@link #set(int, PlayerInfo)},
	 * {@link #replaceAll(List)}, {@link #removeUUID(UUID)}, or the instance
	 * {@link #addInvite(UUID)} / {@link #removeInvite(UUID)} methods so the
	 * companion {@link #UUID_INDEX} and top-list version stay consistent.
	 */
	public static final List<PlayerInfo> list = new CopyOnWriteArrayList<>();

	/** Reverse index: owner UUID OR invited-member UUID  →  island id (position in {@link #list}). */
	private static final ConcurrentMap<UUID, Integer> UUID_INDEX = new ConcurrentHashMap<>();

	/**
	 * Monotonic counter bumped whenever the top-list's sort order could have
	 * changed (level-up, slot assignment, bulk reload). Consumers of
	 * {@code gettoplist} compare their cached version to this to decide if a
	 * re-sort is needed.
	 */
	private static final AtomicLong TOP_VERSION = new AtomicLong();

	public static final PlayerInfo not_found = new PlayerInfo(null);

	public UUID uuid;
	public List<UUID> uuids = new ArrayList<UUID>();
	public int lvl = 0;
	public int breaks = 0;
	public BossBar bar = null;
	public boolean allow_visit = false;

	public PlayerInfo(UUID uuid) {
		this.uuid = uuid;
	}

	public Level lvlup() {
		++lvl;
		breaks = 0;
		TOP_VERSION.incrementAndGet();
		return Level.get(lvl);
	}

	public void createBar() {
		Level level = Level.get(lvl);
		createBar(level.name, level.color, level.style);
	}

	public void createBar(String title) {
		Level level = Level.get(lvl);
		createBar(title, level.color, level.style);
	}

	private void createBar(String text, BarColor color, BarStyle style) {
		if (bar == null) {
			bar = Bukkit.createBossBar(text, color, style, BarFlag.DARKEN_SKY);
			return;
		}
		bar.setTitle(text);
		bar.setColor(color);
		bar.setStyle(style);
	}

	public void removeBar(OfflinePlayer p) {
		if (bar == null) return;
		if (!(p instanceof Player)) return;
		bar.removePlayer((Player)p);
	}

	public void removeUUID(UUID deleted) {
		if (deleted == null) return;
		if (uuid != null && uuid.equals(deleted)) {
			UUID_INDEX.remove(deleted);
			if (!uuids.isEmpty()) {
				uuid = uuids.remove(0);
				// New owner UUID is already mapped to this island id via the invite path.
			} else {
				uuid = null;
			}
		} else if (uuids.remove(deleted)) {
			UUID_INDEX.remove(deleted);
		}
	}

	/** Add an invited-member UUID to this island and keep the reverse index consistent. */
	public void addInvite(UUID inviteeUuid) {
		if (inviteeUuid == null) return;
		uuids.add(inviteeUuid);
		if (this.uuid != null) {
			Integer id = UUID_INDEX.get(this.uuid);
			if (id != null) UUID_INDEX.put(inviteeUuid, id);
		}
	}

	/** Remove an invited-member UUID from this island and keep the reverse index consistent. */
	public void removeInvite(UUID inviteeUuid) {
		if (inviteeUuid == null) return;
		if (uuids.remove(inviteeUuid)) {
			UUID_INDEX.remove(inviteeUuid);
		}
	}

	public int getNeed() {
        return Level.get(lvl).length;
    }

	public double getPercent() {
		return (double) breaks / getNeed();
	}

	public static void removeBarStatic(Player p) {
		if (list.isEmpty()) return;
		get(p.getUniqueId()).removeBar(p);
	}

	/**
	 * O(1) lookup of the island id that owns or has invited the given UUID.
	 * Returns -1 if the UUID is not tracked.
	 */
	public static int GetId(UUID uuid) {
		if (uuid == null) return -1;
		Integer id = UUID_INDEX.get(uuid);
		return id == null ? -1 : id;
	}

	public static boolean existsAsOwner(UUID name) {
		if (name == null) return false;
		Integer id = UUID_INDEX.get(name);
		if (id == null) return false;
		PlayerInfo pl = list.get(id);
		return pl != null && name.equals(pl.uuid);
	}

	public static PlayerInfo get(int id) {
		return list.get(id);
	}

	public static PlayerInfo get(UUID uuid) {
		int plID = GetId(uuid);
		if (plID == -1) return not_found;
		return list.get(plID);
	}

	/**
	 * Set or append an island slot. Also registers the PlayerInfo's owner and
	 * invited-member UUIDs in the reverse index under the given id.
	 */
	public static void set(int id, PlayerInfo pInf) {
		if (id < list.size())
			list.set(id, pInf);
		else
			list.add(pInf);
		registerInIndex(pInf, id);
		IslandCoordinateCalculator.invalidateCellIndex();
		TOP_VERSION.incrementAndGet();
	}

	/**
	 * Replace the entire island list in one shot (e.g. on initial load from DB
	 * or legacy JSON/YAML). Rebuilds the reverse index from scratch.
	 */
	public static void replaceAll(List<PlayerInfo> newList) {
		list.clear();
		UUID_INDEX.clear();
		if (newList != null) {
			list.addAll(newList);
			for (int i = 0; i < newList.size(); i++) {
				registerInIndex(newList.get(i), i);
			}
		}
		IslandCoordinateCalculator.invalidateCellIndex();
		TOP_VERSION.incrementAndGet();
	}

	private static void registerInIndex(PlayerInfo inf, int id) {
		if (inf == null) return;
		if (inf.uuid != null) UUID_INDEX.put(inf.uuid, id);
		for (UUID u : inf.uuids) UUID_INDEX.put(u, id);
	}

	/** Current top-list version. Use with {@code gettoplist} snapshot caching. */
	public static long topVersion() { return TOP_VERSION.get(); }

	public static int getFreeId(boolean UseEmptyIslands) {
		if (UseEmptyIslands)
			return PlayerInfo.getNull();
		return PlayerInfo.size();
	}

	public static int size() {
		return list.size();
	}

	private static int getNull() {
		for (int i = 0; list.size() > i; i++)
			if (list.get(i).uuid == null)
				return i;
		return list.size();
	}

	public static final Comparator<PlayerInfo> COMPARE_BY_LVL = new Comparator<PlayerInfo>() {
		@Override
		public int compare(PlayerInfo lhs, PlayerInfo rhs) {
			if (rhs.uuid == null)
				return -1;
			return rhs.lvl - lhs.lvl;
		}
	};
}
