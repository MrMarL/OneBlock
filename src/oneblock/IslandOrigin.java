package oneblock;

import org.bukkit.World;
import java.util.Objects;

/**
 * Immutable snapshot of the island-layout anchor.
 * (Since records are unavailable in Java 8, this is implemented manually as a final class).
 * <p>
 * The snapshot {@code (world, x, y, z, offset)} is treated as a single atomically-replaceable unit.
 * Writers mutate the origin via
 * {@link java.util.concurrent.atomic.AtomicReference#updateAndGet} on {@code Oneblock.ORIGIN};
 * readers call {@link Oneblock#origin()} once and use the captured snapshot so that a concurrent
 * {@code /ob set} cannot produce a mixed (torn) view where, say, {@code x} is new but {@code offset} is old.
 * <p>
 * The zero/null sentinel {@link #EMPTY} is used both as the initial ORIGIN state (before config.yml load)
 * and as an uninitialized marker; callers that need to distinguish "not set yet" from "set to (0,0,0,0)"
 * guard on {@code world() == null} (the typical shape of {@code EMPTY}) or on {@code offset() == 0}
 * (the legacy convention that {@code /ob set} rejects {@code offset == 0}).
 */
public final class IslandOrigin {

    /** Uninitialized / pre-config sentinel: null world, all ints zero. */
    public static final IslandOrigin EMPTY = new IslandOrigin(null, 0, 0, 0, 0);

    private final World world;
    private final int x;
    private final int y;
    private final int z;
    private final int offset;

    public IslandOrigin(World world, int x, int y, int z, int offset) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offset = offset;
    }

    public World world() {
        return world;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    public int offset() {
        return offset;
    }

    /**
     * Returns a copy with the world / x / y / z replaced and the current offset preserved.
     * A null {@code newWorld} keeps the existing world reference (matches legacy {@code setPosition} semantics).
     */
    public IslandOrigin withPosition(World newWorld, int newX, int newY, int newZ) {
        return new IslandOrigin(
                newWorld != null ? newWorld : this.world,
                newX, newY, newZ,
                this.offset
        );
    }

    /** Returns a copy with only the offset replaced; world / x / y / z kept. */
    public IslandOrigin withOffset(int newOffset) {
        return new IslandOrigin(this.world, this.x, this.y, this.z, newOffset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IslandOrigin that = (IslandOrigin) o;
        return x == that.x &&
               y == that.y &&
               z == that.z &&
               offset == that.offset &&
               Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z, offset);
    }

    @Override
    public String toString() {
        return "IslandOrigin{" +
               "world=" + (world != null ? world.getName() : "null") +
               ", x=" + x +
               ", y=" + y +
               ", z=" + z +
               ", offset=" + offset +
               '}';
    }
}