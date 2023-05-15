package org.reeedev.invmanager.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.reeedev.invmanager.InvManager;
import org.reeedev.invmanager.Classes.IEHelper;

import java.io.IOException;
import java.util.ArrayList;

public class OnJoin implements Listener {
    @EventHandler
    public void Event(PlayerJoinEvent e) throws IOException {
        ArrayList<String> invWorlds = (ArrayList<String>) InvManager.getConfigValue("auto_load_inventory_when_joining_server_in_world");
        ArrayList<String> ecWorlds = (ArrayList<String>) InvManager.getConfigValue("auto_load_enderchest_when_joining_server_in_world");
        Player p = e.getPlayer();

        InvManager.PlayerInit(p);

        if (invWorlds.get(0).equals("*") || invWorlds.contains(p.getWorld().getName())) {
            ArrayList<IEHelper> inv = InvManager.receiveInventory(p.getName(), p.getWorld());

            p.getInventory().clear();

            if (inv != null) {
                for (IEHelper IEHelper : inv) {
                    p.getInventory().setItem(IEHelper.index, IEHelper.is);
                }
            }
        }

        if (ecWorlds.get(0).equals("*") || ecWorlds.contains(p.getWorld().getName())) {
            ArrayList<IEHelper> inv = InvManager.receiveEnderChest(p.getName(), p.getWorld());

            p.getEnderChest().clear();

            if (inv != null) {
                for (IEHelper IEHelper : inv) {
                    p.getEnderChest().setItem(IEHelper.index, IEHelper.is);
                }
            }
        }
    }
}
