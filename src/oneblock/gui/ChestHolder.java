package oneblock.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * {@link InventoryHolder} for the chest-edit GUI opened by {@code /ob chest <name> edit}. The
 * {@code type} field holds the chest-alias name so the {@link InventoryCloseEvent} listener can
 * persist the modified contents back to {@link oneblock.ChestItems}.
 */
public class ChestHolder implements InventoryHolder {
	private final String type;

	public ChestHolder(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}
}
