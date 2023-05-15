package org.reriva.invstorage.Commands.Inventory;

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
import org.reriva.invstorage.Classes.IEHelper;
import org.reriva.invstorage.InvStorage;
import org.reriva.invstorage.Utils.ReverseArrayList;

import java.io.IOException;
import java.util.ArrayList;

public class InvSee implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player p = (Player) sender;

        if (p.hasPermission("invstorage.invsee")) {
            if (args.length == 1) {
                if (InvStorage.hasPlayerJoinedOnce(args[0])) {
                    try {
                        InvStorage.createTemp(p, args[0], "INFORMATION");
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
                }
            } else {
                p.sendMessage(InvStorage.getConfigValue("target_missing_message").toString().replace("{command}", "/invsee"));
            }
        } else {
            p.sendMessage(InvStorage.getConfigValue("missing_permissions").toString().replace("{command}", "/invsee"));
        }

        return false;
    }
}
