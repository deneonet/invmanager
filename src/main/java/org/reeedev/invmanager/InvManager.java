package org.reeedev.invmanager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.reeedev.invmanager.Classes.Log;
import org.reeedev.invmanager.Classes.TOutput;
import org.reeedev.invmanager.Listener.*;
import org.reeedev.invmanager.Classes.IEHelper;
import org.reeedev.invmanager.Commands.Enderchest.EnderSee;
import org.reeedev.invmanager.Commands.Inventory.InvSee;
import org.reeedev.invmanager.Commands.Reload;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class InvManager extends JavaPlugin {

    static String mainPath = "plugins" + File.separator;
    static String InvManagerPath = mainPath + "InvManager" + File.separator;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Starting...");

        Init();

        Bukkit.getPluginManager().registerEvents(new OnJoin(), this);
        Bukkit.getPluginManager().registerEvents(new OnQuit(), this);
        Bukkit.getPluginManager().registerEvents(new OnInvClose(), this);
        Bukkit.getPluginManager().registerEvents(new OnWorldChange(), this);
        Bukkit.getPluginManager().registerEvents(new OnInvClick(), this);
        Bukkit.getPluginManager().registerEvents(new OnPickUp(), this);
        Bukkit.getPluginManager().registerEvents(new OnDrop(), this);
        Bukkit.getPluginManager().registerEvents(new OnDespawn(), this);

        Objects.requireNonNull(getCommand("invsee")).setExecutor(new InvSee());
        Objects.requireNonNull(getCommand("endersee")).setExecutor(new EnderSee());
        Objects.requireNonNull(getCommand("invmanagerreload")).setExecutor(new Reload(this));
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    static String logFilePath = InvManagerPath + "Logs" + File.separator;

    // Creating config.yml file, if needed
    public void Init() {
        saveDefaultConfig();

        ConfigurationSerialization.registerClass(Log.class);

        File logFolder = new File(logFilePath);

        if (!logFolder.exists()) {
            if (!logFolder.mkdir()) {
                Bukkit.getLogger().warning("INVSTORAGE: Couldn't create a folder in 'plugins/InvManager' called 'Logs'!");
                return;
            }
        }

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
        InvManager plugin = (InvManager) Bukkit.getPluginManager().getPlugin("InvManager");
        assert plugin != null;
        return plugin.getConfig().get(id);
    }

    static String invFilePath = InvManagerPath + "Inventories.yml";
    static String ecFilePath = InvManagerPath + "EnderChests.yml";

    // Returning true if the player joined at least once, otherwise false
    public static boolean hasPlayerJoinedOnce(String p) {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new File(invFilePath));
        return cfg.getBoolean(p + "." + "joined");
    }


    // CODE EXAMPLE:
    //
    // ArrayList<IEHelper> inv = InvManager.receiveInventory(p, world);
    //
    // SETTING THE LIST TO A INVENTORY:
    //
    // ArrayList<IEHelper> inv = InvManager.receiveInventory(p, world);
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
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'Inventories.yml'!");
                return;
            }
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack is = new ItemStack(Material.AIR);
        cfg.set(p.getName() + "." + world, null);
        cfg.save(dataFile);
        cfg.set(p.getName() + "." + world + "." + 0, is);
        cfg.set(p.getName() + ".joined", true);
        cfg.save(dataFile);
    }

    // Saving an empty inventory to a player as string with a specified world
    public static void saveEmptyInventory(String p, String world) throws IOException {
        File dataFile = new File(invFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'Inventories.yml'!");
                return;
            }
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack is = new ItemStack(Material.AIR);
        cfg.set(p + "." + world, null);
        cfg.save(dataFile);
        cfg.set(p + "." + world + "." + 0, is);
        cfg.set(p + ".joined", true);
        cfg.save(dataFile);
    }


    // CODE EXAMPLE:
    //
    // try {
    //   InvManager.saveInventory(p (REQUIRED), world (OPTIONAL), inv (OPTIONAL));
    // } catch (IOException e) {
    //   throw new RuntimeException(e);
    // }

    // Saving an inventory to a player with the player's inventory and player's world
    public static void saveInventory(Player p) throws IOException {
        File dataFile = new File(invFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'Inventories.yml'!");
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
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'Inventories.yml'!");
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
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'Inventories.yml'!");
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
    // ArrayList<IEHelper> ec = InvManager.receiveEnderChest(p, world);
    //
    // SETTING THE LIST TO A INVENTORY:
    //
    // ArrayList<IEHelper> ec = InvManager.receiveEnderChest(p, world);
    // Inventory newEC = Bukkit.createInventory.createInventory(null, InventoryType.ENDER_CHEST);
    // for (IEHelper ieHelper : inv) {
    //   newEC.setItem(ieHelper.index, ieHelper.is);
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
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'EnderChests.yml'!");
                return;
            }
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack is = new ItemStack(Material.AIR);
        cfg.set(p + "." + world, null);
        cfg.save(dataFile);
        cfg.set(p.getName() + "." + world + "." + 0, is);
        cfg.set(p.getName() + ".joined", true);
        cfg.save(dataFile);
    }

    // Saving an empty inventory to a player as string with a specified world
    public static void saveEmptyEnderChest(String p, String world) throws IOException {
        File dataFile = new File(ecFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'EnderChests.yml'!");
                return;
            }
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ItemStack is = new ItemStack(Material.AIR);
        cfg.set(p + "." + world, null);
        cfg.save(dataFile);
        cfg.set(p + "." + world + "." + 0, is);
        cfg.set(p + ".joined", true);
        cfg.save(dataFile);
    }


    // CODE EXAMPLE:
    //
    // try {
    //   InvManager.saveEnderChest(p (REQUIRED), world (OPTIONAL), inv (OPTIONAL));
    // } catch (IOException e) {
    //   throw new RuntimeException(e);
    // }

    // Saving an EnderChest to a player with the player's inventory and player's world
    public static void saveEnderChest(Player p) throws IOException {
        File dataFile = new File(ecFilePath);
        if (!dataFile.exists()) {
            if (!dataFile.createNewFile()) {
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'EnderChests.yml'!");
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
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'EnderChests.yml'!");
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
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'EnderChests.yml'!");
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

    static String tempFilePath = InvManagerPath + "Temp.yml";

    // Methods for commands
    public static void createTemp(String key, String value, String extra) throws IOException {
        File tempFile = new File(tempFilePath);
        if (!tempFile.exists()) {
            if (!tempFile.createNewFile()) {
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager' called 'Temp.yml'!");
                return;
            }
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(tempFile);
        cfg.set(key + ".value", value);
        cfg.set(key + ".extra", extra);
        cfg.save(tempFile);
    }
    public static TOutput receiveTemp(String key) {
        File tempFile = new File(tempFilePath);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(tempFile);
        TOutput tOut = new TOutput();
        tOut.key = key;
        tOut.value = cfg.get(key + ".value");
        tOut.extra = cfg.getString(key + ".extra");
        return tOut;
    }
    public static void deleteTemp(String key) throws IOException {
        File tempFile = new File(tempFilePath);
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(tempFile);
        cfg.set(key, null);
        cfg.save(tempFile);
    }

    // Creates a log when a player picked up an item
    public static void createLog(String p, Log log) throws IOException {
        File logFile = new File(logFilePath + p + ".yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(logFile);

        if (!logFile.exists()) {
            if (!logFile.createNewFile()) {
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager/Logs' called '" + p + ".yml'!");
                return;
            }
        }

        List<Log> logs = (List<Log>) cfg.getList("Logs");
        if (logs == null) logs = new ArrayList<>();
        logs.add(log);

        cfg.set("Logs", logs);
        cfg.save(logFile);
    }

    // Receives a log from a player
    public static List<Log> receiveLog(String p) throws IOException {
        File logFile = new File(logFilePath + p + ".yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(logFile);

        if (!logFile.exists()) {
            if (!logFile.createNewFile()) {
                Bukkit.getLogger().warning("InvManager: Couldn't create a yml file in 'plugins/InvManager/Logs' called '" + p + ".yml'!");
                return new ArrayList<>();
            }
        }

        List<Log> logs = (List<Log>) cfg.getList("Logs");
        if (logs == null) logs = new ArrayList<>();

        return logs;
    }

    // Deletes a log from a player
    public static void deleteLog(String p) throws IOException {
        File logFile = new File(logFilePath + p + ".yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(logFile);

        cfg.set("Logs", null);
        cfg.save(logFile);
    }
}
