package org.reeedev.invmanager.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.reeedev.invmanager.InvManager;

import java.io.IOException;
import java.util.ArrayList;

public class OnQuit implements Listener {
    @EventHandler
    public void Event(PlayerQuitEvent e) throws IOException {
        ArrayList<String> invWorlds = (ArrayList<String>) InvManager.getConfigValue("auto_save_inventory_when_quitting_server_in_world");
        ArrayList<String> ecWorlds = (ArrayList<String>) InvManager.getConfigValue("auto_save_enderchest_when_quitting_server_in_world");
        Player p = e.getPlayer();

        if (invWorlds.get(0).equals("*") || invWorlds.contains(p.getWorld().getName())) {
            InvManager.saveInventory(p);
        }

        if (ecWorlds.get(0).equals("*") || ecWorlds.contains(p.getWorld().getName())) {
            InvManager.saveEnderChest(p);
        }
    }
}
