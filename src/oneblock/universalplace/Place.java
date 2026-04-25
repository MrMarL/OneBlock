package oneblock.universalplace;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public abstract class Place {
	
	public enum Type
	{
	    legacy,
	    basic,
	    ItemsAdder,
	    Oraxen,
	    Nexo,
	    CraftEngine
	}
	
	public static Place GetPlacerByType(Type type) {
		switch (type) {
		case legacy:
			return new Place1_8to1_12();
		case ItemsAdder:
			return new PlaceItemsAdder();
		case Oraxen:
			return new PlaceOraxen();
		case Nexo:
			return new PlaceNexo();
		case CraftEngine:
			return new PlaceCraftEngine();
		default:
			return new Place1_13plus();
		}
	}
	
	public abstract boolean setType(Block block, Object material_, boolean physics);
	
	/**
	 * Execute a {@code /command} entry. The command string is the body after the
	 * leading slash and is formatted with the block's {@code (x, y, z)} coordinates.
	 *
	 * <p>The caller is responsible for validating the template string before it
	 * reaches this method; any {@code String.format} validation is performed during
	 * configuration parsing, so this method assumes a well-formed command template.
	 */
	public boolean executeCommand(Block block, String command) {
		if (command.isEmpty() || command.charAt(0) != '/') return false;
		String template = command.substring(1);
		String dispatched = String.format(template, block.getX(), block.getY(), block.getZ());
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), dispatched);
		return true;
	}
}
