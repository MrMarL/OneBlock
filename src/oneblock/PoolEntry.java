package oneblock;

/**
 * Tagged union representing one entry of a level's block-pool.
 * Mob entries are kept in a separate {@code WeightedPool<EntityType>} on {@link Level},
 * so {@link Kind#MOB} is intentionally absent here.
 */
public final class PoolEntry {
	public enum Kind { BLOCK, CHEST, COMMAND, DEFAULT_GRASS }

	public final Kind kind;
	public final Object value;

	public PoolEntry(Kind kind, Object value) {
		this.kind = kind;
		this.value = value;
	}

	public static final PoolEntry GRASS = new PoolEntry(Kind.DEFAULT_GRASS, null);

	public static PoolEntry block(Object material)       { return new PoolEntry(Kind.BLOCK, material); }
	public static PoolEntry chest(String chest_name) 	 { return new PoolEntry(Kind.CHEST, chest_name); }
	public static PoolEntry command(String cmd)          { return new PoolEntry(Kind.COMMAND, cmd); }

	@Override
	public String toString() {
		switch (kind) {
			case DEFAULT_GRASS: return "GRASS (undefined)";
			case BLOCK:         return value == null ? "null" : value.toString();
			case CHEST:    		return "chest: " + value;
			case COMMAND:       return "command: " + value;
			default:            return "?";
		}
	}
}
