package org.reeedev.invmanager.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.reeedev.invmanager.InvManager;

import java.io.IOException;
import java.util.ArrayList;

public class OnInventoryClose implements Listener {
    @EventHandler
    public void Event(InventoryCloseEvent e) throws IOException {
        Player p = (Player) e.getPlayer();
        ArrayList<String> list = InvManager.receiveTemp(p);

        if (list.get(1) != null) {
            if (list.get(1).equals("INVENTORY")) {
                InvManager.saveInventory(list.get(0), p.getWorld(), e.getInventory());
            } else if(list.get(1).equals("ENDER_CHEST")) {
                InvManager.saveEnderChest(list.get(0), p.getWorld(), e.getInventory());
            }

            InvManager.deleteTemp((Player) e.getPlayer());
        }
    }
}
