package com.newtykins.xpcap;

import java.sql.*;

public class DB {
    private XPCap plugin;
    private String url;

    public DB(XPCap pluginInstance, String dbName) {
        plugin = pluginInstance;
        url = String.format("jdbc:sqlite:%s\\%s.db", plugin.getDataFolder(), dbName);

        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection != null) {
                plugin.getServer().getConsoleSender().sendMessage("[XPCap] Successfully connected to the database!");

                // Ensure that the players table exists
                executeQuery("""
                    CREATE TABLE IF NOT EXISTS players (
                        playerId text PRIMARY KEY,
                        worldId text NOT NULL,
                        xpToday integer NOT NULL,
                        UNIQUE(playerId, worldId)
                    )
                """);
            }
        } catch (SQLException e) {
            // todo: log this as a spigot error and unload the plugin
            System.out.println(e.getMessage());
        }
    }

    public Connection connect() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public void executeQuery(String sql) {
        try (Connection conn = this.connect(); Statement statement = conn.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            // todo: log this as a spigot error
            System.out.println(e.getMessage());
        }
    }
}
