package oneblock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Location;

public final class IslandCoordinateCalculator {

	/**
	 * Spatial index: packed (cellX, cellZ) &rarr; island id. Each island occupies
	 * exactly one cell of size {@code Oneblock.offset}, so exact-hit lookups are
	 * O(1). Populated lazily on first use after {@link #invalidateCellIndex()}
	 * is called by a {@link PlayerInfo} mutation.
	 */
	private static volatile ConcurrentMap<Long, Integer> cellIndex = null;

	/** Mark the spatial index dirty. Called from PlayerInfo on membership change. */
	public static void invalidateCellIndex() { cellIndex = null; }

	private static ConcurrentMap<Long, Integer> ensureIndex() {
		ConcurrentMap<Long, Integer> idx = cellIndex;
		if (idx != null) return idx;
		synchronized (IslandCoordinateCalculator.class) {
			if (cellIndex != null) return cellIndex;
			int size = PlayerInfo.size();
			IslandOrigin o = Oneblock.origin();
			int offset = o.offset();
			if (offset == 0) { cellIndex = new ConcurrentHashMap<>(); return cellIndex; }
			int half = offset >> 1;
			int baseX = o.x(), baseZ = o.z();
			boolean circle = Oneblock.CircleMode;
			ConcurrentMap<Long, Integer> fresh = new ConcurrentHashMap<>(Math.max(16, size * 2));
			for (int i = 0; i < size; i++) {
				int[] c = getById(i, baseX, baseZ, offset, circle);
				int cellX = Math.floorDiv(c[0] - baseX + half, offset);
				int cellZ = Math.floorDiv(c[1] - baseZ + half, offset);
				fresh.put(packCell(cellX, cellZ), i);
			}
			cellIndex = fresh;
			return fresh;
		}
	}

	private static long packCell(int cellX, int cellZ) {
		return ((long) cellX << 32) | (cellZ & 0xFFFFFFFFL);
	}

	/**
	 * @return the id of the island whose cell contains {@code loc}, or the
	 *         spatially-nearest id if the location is outside every occupied cell.
	 *         <p><b>Caveat:</b> returns {@code 0} also for the degenerate cases
	 *         {@code loc == null}, {@code PlayerInfo.size() == 0}, and
	 *         {@code offset == 0}, which collides with the id of the first
	 *         island. Callers that need to distinguish "no islands" from
	 *         "nearest is id 0" must guard on {@code PlayerInfo.size() > 0}
	 *         before interpreting the return value.
	 */
	public static int findNearestRegionId(Location loc) {
	    if (loc == null) return 0;
	    int size = PlayerInfo.size();
	    if (size == 0) return 0;
	    IslandOrigin o = Oneblock.origin();
	    int offset = o.offset();
	    if (offset == 0) return 0;
	    int locX = loc.getBlockX();
	    int locZ = loc.getBlockZ();
	    int baseX = o.x(), baseZ = o.z();
	    int half = offset >> 1;
	    int cellX = Math.floorDiv(locX - baseX + half, offset);
	    int cellZ = Math.floorDiv(locZ - baseZ + half, offset);

	    // Fast path: the player stands inside some island's own cell.
	    Integer hit = ensureIndex().get(packCell(cellX, cellZ));
	    if (hit != null) return hit;

	    // Fallback: original O(N) spiral-walk nearest-neighbour scan. Reached
	    // only when the player is standing in a cell that no island occupies
	    // (e.g. far outside the populated area, or between IDs in CircleMode).
	    int nearestId = 0;
	    int minDistSq = Integer.MAX_VALUE;
	    int halfDiameterSquared = (offset * offset) >> 2;
	    int X = 0, Z = 0;
	    boolean CircleMode = Oneblock.CircleMode;
	    for (int i = 0; i < size; i++) {
	        int dx = (X * offset + baseX) - locX;
	        int dz = (Z * offset + baseZ) - locZ;
	        int distSq = dx * dx + dz * dz;
	        if (distSq < minDistSq) {
	            minDistSq = distSq;
	            nearestId = i;
	            if (minDistSq <= halfDiameterSquared) break;
	        }
	        if (CircleMode) {
		    	if (X > Z)
		    		if (X > -Z)
		    			Z--;
		    		else
		    			X--;
		    	else if (-X > Z || X == Z && Z < 0)
		    		Z++;
		    	else
		    		X++;
	    	}
	    	else X++;
	    }
	    return nearestId;
	}

	public static int[] getById(int id, int x, int z, int diameter, boolean CircleMode) {
		if (!CircleMode) return new int[] {id * diameter + x, z, id};

		return getByIdHybrid(id, x, z, diameter);
	}

	private static int[] getByIdIter(int id, int x, int z, int diameter) {
		int X = 0, Z = 0;
		for (int i = 0; i < id; i++) {
			if (X > Z)
			    if (X > -Z)
				    Z--;
				else
				    X--;
			else if (-X > Z || X == Z && Z < 0)
				Z++;
			else
				X++;
		}
		X = X * diameter + x;
		Z = Z * diameter + z;
		return new int[] {X, Z, id};
	}

	/**
	 * Spiral-ring island coordinate resolver. Uses iterative walk for ids
	 * &le; 30 and closed-form ring math for larger ids.
	 */
	private static int[] getByIdHybrid(int id, int x, int z, int diameter) {
	    if (id <= 30) return getByIdIter(id, x, z, diameter);

	    int ring = (int) Math.floor((Math.sqrt(id) + 1) / 2);
	    int firstInRing = (2 * ring - 1) * (2 * ring - 1) + 1;
	    int posInRing = id + 1 - firstInRing;
	    int sideLength = 2 * ring;
	    int side = posInRing / sideLength;
	    int offset = posInRing % sideLength;

	    int X, Z;

	    switch (side) {
	        case 0:
	            X = ring;
	            Z = ring - 1 - offset;
	            break;
	        case 1:
	            X = ring - 1 - offset;
	            Z = -ring;
	            break;
	        case 2:
	            X = -ring;
	            Z = -ring + 1 + offset;
	            break;
	        default:
	        	X = -ring + 1 + offset;
	            Z = ring;
	    }

	    X = X * diameter + x;
	    Z = Z * diameter + z;
	    return new int[] {X, Z, id};
	}
}
