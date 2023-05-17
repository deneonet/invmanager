package org.reeedev.invmanager.Classes;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Log implements ConfigurationSerializable {
    public ItemStack itemStack;
    public String fromPlayer;
    public String time;
    public String world;

    public Log(ItemStack itemStack, String fromPlayer, String time, String world) {
        this.itemStack = itemStack;
        this.fromPlayer = fromPlayer;
        this.time = time;
        this.world = world;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("itemStack", itemStack);
        serialized.put("fromPlayer", fromPlayer);
        serialized.put("time", time);
        serialized.put("world", world);
        return serialized;
    }

    public static Log deserialize(Map<String, Object> deserialize) {
        String time = (String) deserialize.get("time");
        ItemStack itemStack = (ItemStack) deserialize.get("itemStack");
        return new Log(itemStack, (String) deserialize.get("fromPlayer"), time, (String) deserialize.get("world"));
    }
}
