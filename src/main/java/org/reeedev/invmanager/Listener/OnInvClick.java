package org.reeedev.invmanager.Listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.reeedev.invmanager.Classes.IEHelper;
import org.reeedev.invmanager.Classes.Log;
import org.reeedev.invmanager.Classes.TOutput;
import org.reeedev.invmanager.InvManager;
import org.reeedev.invmanager.Utils.ReverseArrayList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OnInvClick implements Listener {
    @EventHandler
    public void Event(InventoryClickEvent e) throws IOException {
        Player p = (Player) e.getWhoClicked();

        if (e.getView().getTitle().equals("§7InvManager LogSystem") && e.getCurrentItem() != null) {
            TOutput tOut = InvManager.receiveTemp(p.getName());
            e.setCancelled(true);

            if (e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                SkullMeta sm = (SkullMeta) e.getCurrentItem().getItemMeta();
                if (sm.getOwner().equals("MHF_ArrowRight")) {
                    showLogPage(p, false, true);
                }
            }

            if (e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                SkullMeta sm = (SkullMeta) e.getCurrentItem().getItemMeta();
                if (sm.getOwner().equals("MHF_ArrowLeft")) {
                    showLogPage(p, false, false);
                }
            }

            if (e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                SkullMeta sm = (SkullMeta) e.getCurrentItem().getItemMeta();
                if (sm.getOwner().equals("MHF_TNT2")) {
                    p.closeInventory();
                    showLogPage(p, true, false);
                    InvManager.deleteLog(tOut.value.toString());
                }
            }
        }

        if (e.getView().getTitle().equals("§7InvSee Options") && e.getCurrentItem() != null) {
            TOutput tOut = InvManager.receiveTemp(p.getName());
            e.setCancelled(true);

            switch (e.getCurrentItem().getType()) {
                case COMPASS:
                    p.sendMessage("§7Analytics coming soon. It tracks if many players got items in a short time.");
                    break;
                case PAPER:
                    if ((boolean) InvManager.getConfigValue("logs_enabled")) {
                        showLogPage(p, true, false);
                    } else {
                        p.sendMessage("§cLogs are not enabled, enable them in the config.yml.");
                    }
                    break;
                case BARRIER:
                    if (tOut.value != null) {
                        InvManager.deleteTemp(p.getName());
                        Player target = Bukkit.getPlayer(tOut.value.toString());

                        if (target == null) {
                            InvManager.saveEmptyInventory(tOut.value.toString(), p.getWorld().getName());
                        } else {
                            target.getInventory().clear();
                        }
                    }
                    break;
                case CHEST:
                    if (tOut.value != null) {
                        Player target = Bukkit.getPlayer(tOut.value.toString());

                        if (target == null) {
                            InvManager.deleteTemp(p.getName());

                            Inventory customInv = Bukkit.createInventory(null, InventoryType.PLAYER);

                            ArrayList<IEHelper> inv = InvManager.receiveInventory(tOut.value.toString(), p.getWorld());

                            if (inv != null) {
                                ReverseArrayList revObj = new ReverseArrayList();

                                for (IEHelper IEHelper : revObj.reverseArrayList(inv)) {
                                    customInv.setItem(IEHelper.index, IEHelper.is);
                                }
                            }

                            p.closeInventory();
                            InvManager.createTemp(p.getName(), tOut.value.toString(), "INVENTORY");
                            p.openInventory(customInv);
                        } else {
                            p.closeInventory();
                            p.openInventory(target.getInventory());
                        }
                    }
                    break;
            }
        }

        if (e.getView().getTitle().equals("§7EnderSee Options") && e.getCurrentItem() != null) {
            TOutput tOut = InvManager.receiveTemp(p.getName());
            e.setCancelled(true);

            switch (e.getCurrentItem().getType()) {
                case COMPASS:
                    p.sendMessage("§7Analytics coming soon. It tracks if many players got items in a short time.");
                    break;
                case PAPER:
                    p.sendMessage("§7Logs coming soon. It tracks when and from who, they got these items.");
                    break;
                case BARRIER:
                    if (tOut.value != null) {
                        InvManager.deleteTemp(p.getName());
                        Player target = Bukkit.getPlayer(tOut.value.toString());

                        if (target == null) {
                            InvManager.saveEmptyEnderChest(tOut.value.toString(), p.getWorld().getName());
                        } else {
                            target.getEnderChest().clear();
                        }
                    }
                    break;
                case ENDER_CHEST:
                    if (tOut.value != null) {
                        Player target = Bukkit.getPlayer(tOut.value.toString());

                        if (target == null) {
                            InvManager.deleteTemp(p.getName());

                            Inventory customInv = Bukkit.createInventory(null, InventoryType.ENDER_CHEST);

                            ArrayList<IEHelper> inv = InvManager.receiveEnderChest(tOut.value.toString(), p.getWorld());

                            if (inv != null) {
                                ReverseArrayList revObj = new ReverseArrayList();

                                for (IEHelper IEHelper : revObj.reverseArrayList(inv)) {
                                    customInv.setItem(IEHelper.index, IEHelper.is);
                                }
                            }

                            p.closeInventory();
                            InvManager.createTemp(p.getName(), tOut.value.toString(), "ENDER_CHEST");
                            p.openInventory(customInv);
                        } else {
                            p.closeInventory();
                            p.openInventory(target.getEnderChest());
                        }
                    }
                    break;
            }
        }
    }

    public void showLogPage(Player p, boolean first, boolean next) throws IOException {
        TOutput tOut = InvManager.receiveTemp(p.getName());
        List<Log> logs = InvManager.receiveLog(tOut.value.toString());

        Double d = (Double) InvManager.getConfigValue("logs_per_page");

        int totalPages = (int) Math.ceil(logs.size() / d);

        InventoryView currentInv = p.getOpenInventory();
        int page = 1;
        if (currentInv.getItem(31) != null && !first) {
            if (next) {
                int nextPage = Integer.parseInt(currentInv.getItem(31).getItemMeta().getLore().get(1).replace("§6", "")) + 1;
                page = nextPage;
                if (nextPage > totalPages) {
                    page = nextPage - 1;
                }
            } else {
                int previousPage = Integer.parseInt(currentInv.getItem(31).getItemMeta().getLore().get(1).replace("§6", "")) - 1;
                if (previousPage > 1) {
                    page = previousPage;
                }
            }
        }

        Inventory inv = Bukkit.createInventory(null, InventoryType.PLAYER, "§7InvManager LogSystem");
        int i = 0;
        if (page > 1) {
            i = d.intValue() * (page - 1);
        }

        for (int nu = 0; i < d.intValue() * page; i++) {
            if (logs.size() >= i + 1) {
                Log log = logs.get(i);

                List<String> lore = new ArrayList<>();
                lore.add("§7Item: §6" + log.itemStack.getType().toString() + "§7 , Amount: §6" + log.itemStack.getAmount());
                lore.add("§7Time: §6" + log.time);
                lore.add("§7World: §6" + log.world);

                ItemStack is = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta im = (SkullMeta) is.getItemMeta();
                im.setOwner(log.fromPlayer);
                im.setDisplayName(log.fromPlayer);
                im.setLore(lore);
                is.setItemMeta(im);
                inv.setItem(nu, is);
            }
            nu++;
        }

        List<String> lore = new ArrayList<>();
        lore.add("§7Current Page:");
        lore.add("§6" + page);

        ItemStack is = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta km = (SkullMeta) is.getItemMeta();
        km.setOwner("MHF_ArrowLeft");
        km.setDisplayName("Previous Page");
        is.setItemMeta(km);
        inv.setItem(29, is);

        ItemStack is2 = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta km2 = (SkullMeta) is2.getItemMeta();
        km2.setOwner("MHF_TNT2");
        km2.setLore(lore);
        km2.setDisplayName("Clear Logs");
        is2.setItemMeta(km2);
        inv.setItem(31, is2);

        ItemStack is3 = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta km3 = (SkullMeta) is3.getItemMeta();
        km3.setOwner("MHF_ArrowRight");
        km3.setDisplayName("Next Page");
        is3.setItemMeta(km3);
        inv.setItem(33, is3);

        p.closeInventory();
        InvManager.createTemp(p.getName(), tOut.value.toString(), "LOG");
        p.openInventory(inv);
    }
}
