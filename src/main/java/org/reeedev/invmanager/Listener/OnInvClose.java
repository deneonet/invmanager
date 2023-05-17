package org.reeedev.invmanager.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.reeedev.invmanager.Classes.TOutput;
import org.reeedev.invmanager.InvManager;

import java.io.IOException;

public class OnInvClose implements Listener {
    @EventHandler
    public void Event(InventoryCloseEvent e) throws IOException {
        Player p = (Player) e.getPlayer();
        TOutput tOut = InvManager.receiveTemp(p.getName());

        if (tOut.value != null) {
            if (tOut.extra.equals("INVENTORY")) {
                InvManager.saveInventory(tOut.value.toString(), p.getWorld(), e.getInventory());
            } else if(tOut.extra.equals("ENDER_CHEST")) {
                InvManager.saveEnderChest(tOut.value.toString(), p.getWorld(), e.getInventory());
            }

            InvManager.deleteTemp(p.getName());
        }
    }
}
