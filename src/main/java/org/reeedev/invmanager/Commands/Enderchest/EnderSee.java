package org.reeedev.invmanager.Commands.Enderchest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.reeedev.invmanager.InvManager;

import java.io.IOException;

public class EnderSee implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player p = (Player) sender;

        if (p.hasPermission("invmanager.endersee")) {
            if (args.length == 1) {
                if (InvManager.hasPlayerJoinedOnce(args[0])) {
                    try {
                        InvManager.createTemp(p, args[0], "INFORMATION");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Inventory optionInv = Bukkit.createInventory(null, InventoryType.CHEST, "§7EnderSee Options");

                    ItemStack analytics = new ItemStack(Material.COMPASS);
                    ItemMeta itemMeta = analytics.getItemMeta();
                    itemMeta.setDisplayName("Analytics (SOON)");
                    analytics.setItemMeta(itemMeta);
                    optionInv.setItem(10, analytics);

                    ItemStack logs = new ItemStack(Material.PAPER);
                    ItemMeta itemMeta2 = logs.getItemMeta();
                    itemMeta2.setDisplayName("Logs (Enabled)");
                    logs.setItemMeta(itemMeta2);
                    optionInv.setItem(12, logs);

                    ItemStack clearInv = new ItemStack(Material.BARRIER);
                    ItemMeta itemMeta3 = clearInv.getItemMeta();
                    itemMeta3.setDisplayName("Clear Inventory");
                    clearInv.setItemMeta(itemMeta3);
                    optionInv.setItem(14, clearInv);

                    ItemStack enderchest = new ItemStack(Material.ENDER_CHEST);
                    ItemMeta itemMeta4 = enderchest.getItemMeta();
                    itemMeta4.setDisplayName("Ender Chest");
                    enderchest.setItemMeta(itemMeta4);
                    optionInv.setItem(16, enderchest);

                    p.openInventory(optionInv);
                } else {
                    p.sendMessage(InvManager.getConfigValue("player_exists_not_message").toString());
                }
            } else {
                p.sendMessage(InvManager.getConfigValue("target_missing_message").toString().replace("{command}", "/endersee"));
            }
        } else {
            p.sendMessage(InvManager.getConfigValue("missing_permissions").toString().replace("{command}", "/endersee"));
        }

        return false;
    }
}
