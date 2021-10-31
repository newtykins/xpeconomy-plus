package com.newtykins.xpcap.events;

import com.newtykins.xpcap.DB;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class HandleDB implements Listener {
    private DB db;

    public HandleDB(DB dbInstance) {
        db = dbInstance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<World> worlds = player.getServer().getWorlds();

        // For each world, make the player a row in the database
        for (World world : worlds) {
            db.executeQuery(String.format("""
                INSERT OR IGNORE INTO players(playerId, worldId, xpToday) VALUES('%s', '%s', 0)
            """, player.getUniqueId(), world.getUID()));
        }
    }

    @EventHandler
    public void onPlayerBan(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // If the player was banned, clean up the database
        // todo: test
        if (player.isBanned()) {
            db.executeQuery(String.format("""
                DELETE OR IGNORE FROM players WHERE userId = '%s'
            """, player.getUniqueId()));
        }
    }
}
