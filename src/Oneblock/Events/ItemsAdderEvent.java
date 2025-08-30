package Oneblock.Events;

import static Oneblock.Oneblock.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;

public class ItemsAdderEvent implements Listener {
	@EventHandler
    public void ItemsAdderLoad(ItemsAdderLoadDataEvent event) {
		configManager.Blockfile();
    }
}
