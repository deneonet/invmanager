package org.reriva.invstorage.Commands.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.reriva.invstorage.Classes.IEHelper;
import org.reriva.invstorage.InvStorage;

import java.io.IOException;
import java.util.ArrayList;

public class InvSee implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        Player p = (Player) sender;

        if (p.hasPermission("invstorage.invsee")) {
            if (args.length == 1) {
                Player t = Bukkit.getPlayer(args[0]);

                if (t == null) {
                    if (InvStorage.hasPlayerJoinedOnce(args[0])) {
                        ArrayList<IEHelper> inv = InvStorage.receiveInventory(args[0], p.getWorld());
                        Inventory customInv = Bukkit.createInventory(null, InventoryType.PLAYER, "§6" + args[0]);

                        try {
                            InvStorage.createTemp(p, args[0], "INVENTORY");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if (inv != null) {
                            for (IEHelper IEHelper : inv) {
                                customInv.setItem(IEHelper.index, IEHelper.is);
                            }
                        }

                        p.openInventory(customInv);
                    } else {
                        p.sendMessage(InvStorage.getConfigValue("player_exists_not_message").toString());
                    }
                } else {
                    p.openInventory(t.getInventory());
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
