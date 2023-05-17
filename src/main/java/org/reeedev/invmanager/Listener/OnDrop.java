package org.reeedev.invmanager.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.reeedev.invmanager.InvManager;

import java.io.IOException;

public class OnDrop implements Listener {
    @EventHandler
    public void Event(PlayerDropItemEvent e) throws IOException {
        Player p = e.getPlayer();
        InvManager.createTemp(String.valueOf(e.getItemDrop().getEntityId()), p.getName(), "DROPPED_ITEM");
    }
}
