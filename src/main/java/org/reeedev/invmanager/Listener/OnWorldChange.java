package org.reeedev.invmanager.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.reeedev.invmanager.InvManager;
import org.reeedev.invmanager.Classes.IEHelper;

import java.io.IOException;
import java.util.ArrayList;

public class OnWorldChange implements Listener {
    @EventHandler
    public void Event(PlayerChangedWorldEvent e) throws IOException {
        ArrayList<String> invWorldsLW = (ArrayList<String>) InvManager.getConfigValue("auto_save_inventory_when_leaving_world");
        ArrayList<String> invWorldsJW = (ArrayList<String>) InvManager.getConfigValue("auto_load_inventory_when_joining_world");
        ArrayList<String> ecWorldsLW = (ArrayList<String>) InvManager.getConfigValue("auto_save_enderchest_when_leaving_world");
        ArrayList<String> ecWorldsJW = (ArrayList<String>) InvManager.getConfigValue("auto_load_enderchest_when_joining_world");

        Player p = e.getPlayer();

        if (invWorldsLW.get(0).equals("*") || invWorldsLW.contains(e.getFrom().getName())) {
            InvManager.saveInventory(p, e.getFrom());
        }

        if (invWorldsJW.get(0).equals("*") || invWorldsJW.contains(p.getWorld().getName())) {
            ArrayList<IEHelper> inv = InvManager.receiveInventory(p.getName(), p.getWorld());

            p.getInventory().clear();

            if (inv != null) {
                for (IEHelper IEHelper : inv) {
                    p.getInventory().setItem(IEHelper.index, IEHelper.is);
                }
            }
        }

        if (ecWorldsLW.get(0).equals("*") || ecWorldsLW.contains(e.getFrom().getName())) {
            InvManager.saveEnderChest(p, e.getFrom());
        }

        if (ecWorldsJW.get(0).equals("*") || ecWorldsJW.contains(p.getWorld().getName())) {
            ArrayList<IEHelper> inv = InvManager.receiveEnderChest(p.getName(), p.getWorld());

            p.getEnderChest().clear();

            if (inv != null) {
                for (IEHelper IEHelper : inv) {
                    p.getEnderChest().setItem(IEHelper.index, IEHelper.is);
                }
            }
        }

        // DO LOGIC AFTER SWITCHING THE WORLD LIKE CLEARING THE INVENTORY OR SETTING A CUSTOM INVENTORY
    }
}
