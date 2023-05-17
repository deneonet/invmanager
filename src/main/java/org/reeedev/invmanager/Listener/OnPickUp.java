package org.reeedev.invmanager.Listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.reeedev.invmanager.Classes.Log;
import org.reeedev.invmanager.Classes.TOutput;
import org.reeedev.invmanager.InvManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OnPickUp implements Listener {
    @EventHandler
    public void Event(EntityPickupItemEvent e) throws IOException {
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getEntity();
            TOutput t = InvManager.receiveTemp(String.valueOf(e.getItem().getEntityId()));

            if (t.value == null) t.value = "Unknown";

            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDateTime = dateTime.format(format);

            InvManager.createLog(p.getName(), new Log(e.getItem().getItemStack(), t.value.toString(), formattedDateTime, p.getWorld().getName()));
            InvManager.deleteTemp(String.valueOf(e.getItem().getEntityId()));
        }
    }
}
