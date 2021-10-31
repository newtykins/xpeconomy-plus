package com.newtykins.xpcap.events;

import com.newtykins.xpcap.DB;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class XPChange implements Listener {
    private DB db;

    public XPChange(DB dbInstance) {
        db = dbInstance;
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        int xpChange = event.getAmount();
        Player player = event.getPlayer();

        // If the change is positive, add it to the player's usage for the day
        if (xpChange > 0) {
            db.executeQuery(String.format("""
                UPDATE players
                SET xpToday = xpToday + %d
                WHERE playerId = '%s' AND worldId = '%s';
            """, xpChange, player.getUniqueId(), player.getWorld().getUID()));
        }
    }
}
