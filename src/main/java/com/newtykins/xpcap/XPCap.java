package com.newtykins.xpcap;

import com.newtykins.xpcap.events.HandleDB;
import com.newtykins.xpcap.events.XPChange;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/*
 todo: look into whether worldId is really needed considering the planned vault economy integration
 */
public final class XPCap extends JavaPlugin {
    private DB db;

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[XPCap] Plugin is enabled!");

        // Ensure that the data folder exists
        getDataFolder().mkdir();

        // Connect to the database
        db = new DB(this, "data");

        // Register events
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new XPChange(db), this);
        pluginManager.registerEvents(new HandleDB(db), this);

        // Every day in a world, reset user xpToday counts
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            List<World> worlds = getServer().getWorlds();

            for (World world : worlds) {
                if (world.getTime() == 0) {
                    db.executeQuery(String.format("""
                        UPDATE players
                        SET xpToday = 0
                        WHERE worldId = '%s';
                    """, world.getUID()));
                }
            }
        }, 0, 1);
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[XPCap] Plugin is disabled!");
    }
}
