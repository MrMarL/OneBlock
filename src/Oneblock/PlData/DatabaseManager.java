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
    
    public static String dbType = "h2"; // h2, mysql
    public static String host = "localhost";
    public static int port = 3306;
    public static String database = "oneblock";
    public static String username = "root";
    public static String password = "";
    public static boolean useSSL = false;
    public static boolean autoReconnect = true;
    
    public static void initialize() {
        HikariConfig config = new HikariConfig();
        
        if ("mysql".equalsIgnoreCase(dbType)) {
            config.setJdbcUrl(String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=%s&autoReconnect=%s",
                host, port, database, useSSL, autoReconnect
            ));
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        } else { // h2
            config.setJdbcUrl("jdbc:h2:./plugins/Oneblock/PlData;MODE=MySQL");
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
        
        try {
        	dataSource = new HikariDataSource(config);
        	createTable();
        } catch (Exception e) {
        	plugin.getLogger().log(Level.SEVERE, "Failed to initialize Database", e);
        	dataSource = null;
        }
    }
    
    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
            "uuid VARCHAR(36) PRIMARY KEY, " +
            "level INT NOT NULL DEFAULT 1, " +
            "breaks INT NOT NULL DEFAULT 0, " +
            "allow_visit BOOLEAN DEFAULT FALSE, " +
            "invited_players TEXT" +
            ")";
        
        if (dataSource != null)
	        try (Connection conn = dataSource.getConnection();
	             Statement stmt = conn.createStatement()) {
	            stmt.execute(sql);
	        } catch (SQLException e) {
	            plugin.getLogger().log(Level.SEVERE, "Failed to create table", e);
	        }
    }
    
    public static List<PlayerInfo> load() {
        List<PlayerInfo> players = new ArrayList<>();
        String sql = "SELECT uuid, level, breaks, allow_visit, invited_players FROM player_data";
        
        if (dataSource != null)
	        try (Connection conn = dataSource.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(sql)) {
	            
	            while (rs.next()) {
	                PlayerInfo player = new PlayerInfo(UUID.fromString(rs.getString("uuid")));
	                player.lvl = rs.getInt("level");
	                player.breaks = rs.getInt("breaks");
	                player.allow_visit = rs.getBoolean("allow_visit");
	                
	                String invitedStr = rs.getString("invited_players");
	                if (invitedStr != null && !invitedStr.isEmpty()) {
	                    player.uuids.addAll(Arrays.stream(invitedStr.split(","))
	                        .map(UUID::fromString)
	                        .collect(Collectors.toList()));
	                }
	                
	                players.add(player);
	            }
	        } catch (SQLException e) {
	            plugin.getLogger().log(Level.SEVERE, "Failed to load player data", e);
	        }
        
        return players;
    }
    
    public static boolean save(List<PlayerInfo> players) {
    	if (dataSource == null) return false;
    	
        // Универсальный запрос для H2 и MySQL
        String sql = "INSERT INTO player_data (uuid, level, breaks, allow_visit, invited_players) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "level = VALUES(level), " +
                    "breaks = VALUES(breaks), " +
                    "allow_visit = VALUES(allow_visit), " +
                    "invited_players = VALUES(invited_players)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            for (PlayerInfo player : players) {
                if (player.uuid == null) continue;
                
                pstmt.setString(1, player.uuid.toString());
                pstmt.setInt(2, player.lvl);
                pstmt.setInt(3, player.breaks);
                pstmt.setBoolean(4, player.allow_visit);
                
                String invitedStr = player.uuids.stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(","));
                pstmt.setString(5, invitedStr.isEmpty() ? null : invitedStr);
                
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data", e);
            return false;
        }
    }
    
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) dataSource.close();
    }
}