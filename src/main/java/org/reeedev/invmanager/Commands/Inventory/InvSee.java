package org.reeedev.invmanager.Commands.Inventory;

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

public class InvSee implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player p = (Player) sender;

        if (p.hasPermission("invmanager.invsee")) {
            if (args.length == 1) {
                if (InvManager.hasPlayerJoinedOnce(args[0])) {
                    try {
                        InvManager.createTemp(p.getName(), args[0], "INFORMATION");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Inventory optionInv = Bukkit.createInventory(null, InventoryType.CHEST, "§7InvSee Options");

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

                    ItemStack inventory = new ItemStack(Material.CHEST);
                    ItemMeta itemMeta4 = inventory.getItemMeta();
                    itemMeta4.setDisplayName("Inventory");
                    inventory.setItemMeta(itemMeta4);
                    optionInv.setItem(16, inventory);

                    p.openInventory(optionInv);
                } else {
                    p.sendMessage(InvManager.getConfigValue("player_exists_not_message").toString());
                }
            } else {
                p.sendMessage(InvManager.getConfigValue("target_missing_message").toString().replace("{command}", "/invsee"));
            }
        } else {
            p.sendMessage(InvManager.getConfigValue("missing_permissions").toString().replace("{command}", "/invsee"));
        }

        return false;
    }
}
