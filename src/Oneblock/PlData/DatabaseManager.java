package Oneblock.PlData;

import static Oneblock.Oneblock.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import Oneblock.PlayerInfo;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DatabaseManager {
    private static HikariDataSource dataSource;
    
    public static String dbType = "json"; // json, h2, mysql
    public static String host = "localhost";
    public static int port = 3306;
    public static String database = "oneblock";
    public static String username = "root";
    public static String password = "";
    public static boolean useSSL = false;
    public static boolean autoReconnect = true;
    
    public static void initialize() {
        if ("json".equals(dbType)) {
        	plugin.getLogger().info("Database usage is turned off in the configuration");
            plugin.getLogger().info("Using JSON storage");
            return;
        }
        
        try {HikariConfig config = new HikariConfig();
            
            if ("mysql".equals(dbType)) {
                config.setJdbcUrl(String.format(
                    "jdbc:mysql://%s:%d/%s?useSSL=%s&autoReconnect=%s",
                    host, port, database, useSSL, autoReconnect
                ));
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            } else { // h2
                String h2Path = plugin.getDataFolder().getAbsolutePath() + "/PlData";
                config.setJdbcUrl("jdbc:h2:" + h2Path + ";MODE=MySQL");
                config.setDriverClassName("org.h2.Driver");
            }
            
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            
            plugin.getLogger().info("Database initialized successfully (" + dbType + ")");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize Database", e);
            plugin.getLogger().info("Using JSON storage");
            dataSource = null;
        }
        
        createTable();
    }
    
    private static void createTable() {
        if (!isConnected()) return;
        
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
            "island_id INT PRIMARY KEY, " +
            "uuid VARCHAR(36) NULL, " + 
            "level INT NOT NULL DEFAULT 1, " +
            "breaks INT NOT NULL DEFAULT 0, " +
            "allow_visit BOOLEAN DEFAULT FALSE, " +
            "invited_players TEXT" +
            ")";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create table", e);
        }
    }
    
    public static List<PlayerInfo> load() {
        List<PlayerInfo> players = new ArrayList<>();
        if (!isConnected()) return players;
        
        String sql = "SELECT island_id, uuid, level, breaks, allow_visit, invited_players " +
                     "FROM player_data ORDER BY island_id";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String uuidStr = rs.getString("uuid");
                UUID uuid = (uuidStr != null && !uuidStr.trim().isEmpty()) ? UUID.fromString(uuidStr.trim()) : null;
                PlayerInfo player = new PlayerInfo(uuid);
                
                if (uuid != null) {
	                player.lvl = rs.getInt("level");
	                player.breaks = rs.getInt("breaks");
	                player.allow_visit = rs.getBoolean("allow_visit");
	                
	                String invitedStr = rs.getString("invited_players");
	                if (invitedStr != null && !invitedStr.trim().isEmpty()) {
	                    player.uuids.addAll(Arrays.stream(invitedStr.split(","))
	                        .map(String::trim)
	                        .filter(s -> !s.isEmpty())
	                        .map(UUID::fromString)
	                        .collect(Collectors.toList()));
	                }
                }
                players.add(player);
            }
            
            plugin.getLogger().info("Loaded " + players.size() + " island slots from database");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load player data from Database", e);
        }
        
        return players;
    }
    
    public static boolean save(List<PlayerInfo> players) {
        if (!isConnected() || players == null) return false;
        
        String upsertSQL = "INSERT INTO player_data (island_id, uuid, level, breaks, allow_visit, invited_players) " +
                          "VALUES (?, ?, ?, ?, ?, ?) " +
                          "ON DUPLICATE KEY UPDATE " +
                          "uuid = VALUES(uuid), " +
                          "level = VALUES(level), " +
                          "breaks = VALUES(breaks), " +
                          "allow_visit = VALUES(allow_visit), " +
                          "invited_players = VALUES(invited_players)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(upsertSQL)) {
            
            conn.setAutoCommit(false);
            int savedCount = 0;
            
            for (int i = 0; i < players.size(); i++) {
                PlayerInfo player = players.get(i);
                if (player == null) continue;
                
                pstmt.setInt(1, i); // island_id
                
                if (player.uuid != null) {
                    pstmt.setString(2, player.uuid.toString());
                } else {
                    pstmt.setNull(2, java.sql.Types.VARCHAR);
                }
                
                pstmt.setInt(3, player.lvl);
                pstmt.setInt(4, player.breaks);
                pstmt.setBoolean(5, player.allow_visit);
                
                String invitedStr = player.uuids.stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(","));
                pstmt.setString(6, invitedStr.isEmpty() ? null : invitedStr);
                
                pstmt.addBatch();
                savedCount++;
                
                if (savedCount % 100 == 0) {
                    pstmt.executeBatch();
                }
            }
            
            if (savedCount > 0) {
                pstmt.executeBatch();
            }
            conn.commit();
            
            plugin.getLogger().info("Saved " + savedCount + " islands to database");
            return true;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data", e);
            return false;
        }
    }
    
    public static boolean isConnected() { 
        return dataSource != null && !dataSource.isClosed(); 
    }
    
    public static void close() {
        if (isConnected()) dataSource.close();
    }
}