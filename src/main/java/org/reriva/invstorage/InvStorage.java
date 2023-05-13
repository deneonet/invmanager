package org.reriva.invstorage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.reriva.invstorage.Classes.IEHelper;
import org.reriva.invstorage.Commands.Enderchest.EnderSee;
import org.reriva.invstorage.Commands.Inventory.InvSee;
import org.reriva.invstorage.Commands.Reload;
import org.reriva.invstorage.Listener.OnInventoryClose;
import org.reriva.invstorage.Listener.OnJoin;
import org.reriva.invstorage.Listener.OnPlayerChangedWorld;
import org.reriva.invstorage.Listener.OnQuit;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class InvStorage extends JavaPlugin {

    static String mainPath = "plugins" + File.separator;
    static String InvStoragePath = mainPath + "InvStorage" + File.separator;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Starting...");

        Init();

        Bukkit.getPluginManager().registerEvents(new OnJoin(), this);
        Bukkit.getPluginManager().registerEvents(new OnQuit(), this);
        Bukkit.getPluginManager().registerEvents(new OnInventoryClose(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerChangedWorld(), this);

        Objects.requireNonNull(getCommand("invsee")).setExecutor(new InvSee());
        Objects.requireNonNull(getCommand("endersee")).setExecutor(new EnderSee());
        Objects.requireNonNull(getCommand("invstoragereload")).setExecutor(new Reload(this));
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // Creating config.yml file, if needed
    public void Init() {
        saveDefaultConfig();
        Bukkit.getLogger().info("Successfully started...");
    }

    // Setting the inventory from the player in every world to empty (only called one time when the player joins the server first time to avoid errors)
    public static void PlayerInit(Player p) throws IOException {
        File invFile = new File(invFilePath);
        File ecFile = new File(ecFilePath);
        YamlConfiguration invCfg = YamlConfiguration.loadConfiguration(invFile);
        YamlConfiguration ecCfg = YamlConfiguration.loadConfiguration(ecFile);

        ArrayList<String> worldNames = new ArrayList<>();
        List<String> excludeFolders = Arrays.asList("plugins", "logs", "crash-reports", "cache", "config", "libraries", "versions");
        for (File file: Objects.requireNonNull(Bukkit.getServer().getWorldContainer().listFiles())){
            if (file.isDirectory() && !excludeFolders.contains(file.getName())){
                worldNames.add(file.getName());
            }
        }

        if (!invCfg.getBoolean(p.getName() + "." + "joined")) {
            for (String world : worldNames) {
                saveEmptyInventory(p, world);
            }
        }

        if (!ecCfg.getBoolean(p.getName() + "." + "joined")) {
            for (String world : worldNames) {
                saveEmptyEnderChest(p, world);
            }
        }
    }

    // Retrieving values from config.yml
    public static Object getConfigValue(String id) {
        InvStorage plugin = (InvStorage) Bukkit.getPluginManager().getPlugin("InvStorage");
        assert plugin != null;
        return plugin.getConfig().get(id);
    }

    static String invFilePath = InvStoragePath + "Inventories.yml";
    static String ecFilePath = InvStoragePath + "EnderChests.yml";

    // Returning true if the player joined at least once, otherwise false
    public static boolean hasPlayerJoinedOnce(String p) {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new File(invFilePath));
        return cfg.getBoolean(p + "." + "joined");
    }


    // CODE EXAMPLE:
    //
    // ArrayList<IEHelper> inv = InvStorage.receiveInventory(p, world);
    //
    // SETTING THE LIST TO A INVENTORY:
    //
    // ArrayList<IEHelper> inv = InvStorage.receiveInventory(p, world);
    // Inventory newInv = Bukkit.createInventory.createInventory(null, InventoryType.PLAYER);
    // for (IEHelper ieHelper : inv) {
    //   newInv.setItem(ieHelper.index, ieHelper.is);
    // }
    // p.openInventory(newInv);

    // Retrieving an inventory from a user
    public static ArrayList<IEHelper> receiveInventory(String p, World world) {
        File dataFile = new File(invFilePath);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);

        Set<String> sl = Objects.requireNonNull(cfg.getConfigurationSection(p + "." + world.getName())).getKeys(false);

        if (sl.isEmpty()) {
            return null;
        }

        ArrayList<IEHelper> inv = new ArrayList<>();
        for (String s : sl) {
            IEHelper cInv = new IEHelper();
            cInv.index = Integer.parseInt(s);
            cInv.is = cfg.getItemStack(p + "." + world.getName() + "." + s);
            inv.add(cInv);
        }

        return inv;
    }

    // Saving an empty inventory to a player with a specified world
    public static void saveEmptyInventory(Player p, String world) throws IOException {
        File dataFile = new File(invFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("INVSTORAGE: Couldn't create a yml file in 'plugins/InvStorage' called 'Inventories.yml'!");
                return;
            }
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack is = new ItemStack(Material.AIR);
        cfg.set(p.getName() + "." + world + "." + 0, is);
        cfg.set(p.getName() + ".joined", true);
        cfg.save(dataFile);
    }


    // CODE EXAMPLE:
    //
    // try {
    //   InvStorage.saveInventory(p (REQUIRED), world (OPTIONAL), inv (OPTIONAL));
    // } catch (IOException e) {
    //   throw new RuntimeException(e);
    // }

    // Saving an inventory to a player with the player's inventory and player's world
    public static void saveInventory(Player p) throws IOException {
        File dataFile = new File(invFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("INVSTORAGE: Couldn't create a yml file in 'plugins/InvStorage' called 'Inventories.yml'!");
                return;
            }
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack[] i = p.getInventory().getContents();
        int pos = 0;
        for (ItemStack stack : i) {
            cfg.set(p.getName() + "." + p.getWorld().getName() + "." + pos, stack);
            pos++;
        }
        cfg.save(dataFile);
    }

    // Saving an inventory to a player with a different world than the player
    public static void saveInventory(Player p, World world) throws IOException {
        File dataFile = new File(invFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("INVSTORAGE: Couldn't create a yml file in 'plugins/InvStorage' called 'Inventories.yml'!");
                return;
            }
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack[] i = p.getInventory().getContents();
        int pos = 0;
        for (ItemStack stack : i) {
            cfg.set(p.getName() + "." + world.getName() + "." + pos, stack);
            pos++;
        }
        cfg.save(dataFile);
    }

    // Saving an inventory to a player with a different inventory than the player and a different world than the player
    public static void saveInventory(String p, World world, Inventory inv) throws IOException {
        File dataFile = new File(invFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("INVSTORAGE: Couldn't create a yml file in 'plugins/InvStorage' called 'Inventories.yml'!");
                return;
            }
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack[] i = inv.getContents();
        int pos = 0;
        for (ItemStack stack : i) {
            cfg.set(p + "." + world.getName() + "." + pos, stack);
            pos++;
        }
        cfg.save(dataFile);
    }


    // CODE EXAMPLE:
    //
    // ArrayList<IEHelper> ec = InvStorage.receiveEnderChest(p, world);
    //
    // SETTING THE LIST TO A INVENTORY:
    //
    // ArrayList<IEHelper> ec = InvStorage.receiveEnderChest(p, world);
    // Inventory newEC = Bukkit.createInventory.createInventory(null, InventoryType.ENDER_CHEST);
    // for (IEHelper iehelper : inv) {
    //   newEC.setItem(iehelper.index, iehelper.is);
    // }
    // p.openInventory(newEC);

    // Retrieving an EnderChest from a user from a specified world
    public static ArrayList<IEHelper> receiveEnderChest(String p, World world) {
        File dataFile = new File(ecFilePath);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);

        Set<String> sl = Objects.requireNonNull(cfg.getConfigurationSection(p + "." + world.getName())).getKeys(false);

        if (sl.isEmpty()) {
            return null;
        }


        ArrayList<IEHelper> inv = new ArrayList<>();
        for (String s : sl) {
            IEHelper cInv = new IEHelper();
            cInv.index = Integer.parseInt(s);
            cInv.is = cfg.getItemStack(p + "." + world.getName() + "." + s);
            inv.add(cInv);
        }

        return inv;
    }

    // Saving an empty inventory to a player with a specified world
    public static void saveEmptyEnderChest(Player p, String world) throws IOException {
        File dataFile = new File(ecFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("INVSTORAGE: Couldn't create a yml file in 'plugins/InvStorage' called 'EnderChests.yml'!");
                return;
            }
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack is = new ItemStack(Material.AIR);
        cfg.set(p.getName() + "." + world + "." + 0, is);
        cfg.set(p.getName() + ".joined", true);
        cfg.save(dataFile);
    }


    // CODE EXAMPLE:
    //
    // try {
    //   InvStorage.saveEnderChest(p (REQUIRED), world (OPTIONAL), inv (OPTIONAL));
    // } catch (IOException e) {
    //   throw new RuntimeException(e);
    // }

    // Saving an EnderChest to a player with the player's inventory and player's world
    public static void saveEnderChest(Player p) throws IOException {
        File dataFile = new File(ecFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("INVSTORAGE: Couldn't create a yml file in 'plugins/InvStorage' called 'EnderChests.yml'!");
                return;
            }
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack[] i = p.getEnderChest().getContents();
        int pos = 0;
        for (ItemStack stack : i) {
            cfg.set(p.getName() + "." + p.getWorld().getName() + "." + pos, stack);
            pos++;
        }
        cfg.save(dataFile);
    }

    // Saving an EnderChest to a player with a different world than the player
    public static void saveEnderChest(Player p, World world) throws IOException {
        File dataFile = new File(ecFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("INVSTORAGE: Couldn't create a yml file in 'plugins/InvStorage' called 'EnderChests.yml'!");
                return;
            }
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack[] i = p.getEnderChest().getContents();
        int pos = 0;
        for (ItemStack stack : i) {
            cfg.set(p.getName() + "." + world.getName() + "." + pos, stack);
            pos++;
        }
        cfg.save(dataFile);
    }

    // Saving an EnderChest to a player with a different EnderChest than the player and a different world than the player
    public static void saveEnderChest(String p, World world, Inventory ec) throws IOException {
        File dataFile = new File(ecFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("INVSTORAGE: Couldn't create a yml file in 'plugins/InvStorage' called 'EnderChests.yml'!");
                return;
            }
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack[] i = ec.getContents();
        int pos = 0;
        for (ItemStack stack : i) {
            cfg.set(p + "." + world.getName() + "." + pos, stack);
            pos++;
        }
        cfg.save(dataFile);
    }

    static String tempFilePath = InvStoragePath + "Temp.yml";

    // Methods for commands
    public static void createTemp(Player p, String target, String type) throws IOException {
        File tempFile = new File(tempFilePath);
        if (!tempFile.exists()) {
            if (!tempFile.createNewFile()) {
                Bukkit.getLogger().warning("INVSTORAGE: Couldn't create a yml file in 'plugins/InvStorage' called 'Temp.yml'!");
                return;
            }
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(tempFile);
        cfg.set(p.getName() + ".target", target);
        cfg.set(p.getName() + ".type", type);
        cfg.save(tempFile);
    }
    public static ArrayList<String> receiveTemp(Player p) {
        File tempFile = new File(tempFilePath);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(tempFile);
        ArrayList<String> list = new ArrayList<>();
        list.add(cfg.getString(p.getName() + ".target"));
        list.add(cfg.getString(p.getName() + ".type"));
        return list;
    }
    public static void deleteTemp(Player p) throws IOException {
        File tempFile = new File(tempFilePath);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(tempFile);
        cfg.set(p.getName(), null);
        cfg.save(tempFile);
    }
}
