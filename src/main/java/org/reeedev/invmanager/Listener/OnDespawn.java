package org.reeedev.invmanager.Listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.reeedev.invmanager.Classes.TOutput;
import org.reeedev.invmanager.InvManager;

import java.io.IOException;

public class OnDespawn implements Listener {
    @EventHandler
    public void Event(ItemDespawnEvent e) throws IOException {
        TOutput t = InvManager.receiveTemp(String.valueOf(e.getEntity().getEntityId()));
        if (t.key != null) {
            InvManager.deleteTemp(String.valueOf(e.getEntity().getEntityId()));
        }
    }
}
