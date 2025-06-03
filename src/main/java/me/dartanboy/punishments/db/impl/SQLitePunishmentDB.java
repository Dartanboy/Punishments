package me.dartanboy.punishments.db.impl;

import me.dartanboy.punishments.db.PunishmentDB;
import me.dartanboy.punishments.punishments.Punishment;
import me.dartanboy.punishments.punishments.PunishmentType;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;


public class SQLitePunishmentDB implements PunishmentDB {

    private Connection connection;
    private final String path;

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        }

        return connection;
    }

    public SQLitePunishmentDB(String path) {
        this.path = path;
        setupTables();
    }

    private void setupTables(){
        try(Statement stmt = getConnection().createStatement()) {
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS punishments (
                id TEXT PRIMARY KEY,
                player_uuid TEXT NOT NULL,
                type TEXT NOT NULL,
                reason TEXT NOT NULL,
                expiry_time INTEGER,
                active INTEGER NOT NULL
            );
        """);
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS ip_bans (
                ip TEXT PRIMARY KEY,
                reason TEXT NOT NULL
            );
        """);
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS player_ips (
                player_uuid TEXT NOT NULL,
                ip TEXT NOT NULL,
                PRIMARY KEY (player_uuid, ip)
            );
        """);
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error setting up SQLite:");
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public List<Punishment> getPunishments(UUID playerUUID) {
        List<Punishment> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM punishments WHERE player_uuid = ?")) {
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Punishment punishment = new Punishment(
                        PunishmentType.valueOf(rs.getString("type")),
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getString("reason"),
                        rs.getLong("expiry_time"),
                        UUID.fromString(rs.getString("id"))
                );
                punishment.setActive(rs.getInt("active") == 1);
                list.add(punishment);
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error fetching punishments from SQLite:");
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void addPunishment(UUID playerUUID, Punishment punishment) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO punishments (id, player_uuid, type, reason, expiry_time, active) VALUES (?, ?, ?, ?, ?, ?)"
        )) {
            stmt.setString(1, punishment.getPunishmentId().toString());
            stmt.setString(2, playerUUID.toString());
            stmt.setString(3, punishment.getPunishmentType().name());
            stmt.setString(4, punishment.getReason());
            stmt.setLong(5, punishment.getExpiryTime());
            stmt.setInt(6, punishment.isActive() ? 1 : 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error adding punishment in SQLite:");
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void removePunishment(UUID punishmentId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE punishments SET active = 0 WHERE id = ?")) {
            stmt.setString(1, punishmentId.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error removing punishment in SQLite:");
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void registerIp(UUID playerUUID, String ip) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR IGNORE INTO player_ips (player_uuid, ip) VALUES (?, ?)"
        )) {
            stmt.setString(1, playerUUID.toString());
            stmt.setString(2, ip);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error registering IP in SQLite:");
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public List<String> getIps(UUID playerUUID) {
        List<String> ips = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT ip FROM player_ips WHERE player_uuid = ?")) {
            stmt.setString(1, playerUUID.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ips.add(rs.getString("ip"));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error getting IPs from SQLite:");
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
        return ips;
    }

    @Override
    public boolean isBanned(String ip) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT 1 FROM ip_bans WHERE ip = ?")) {
            stmt.setString(1, ip);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error checking IP for ban in SQLite:");
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getBanReason(String ip) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT reason FROM ip_bans WHERE ip = ?")) {
            stmt.setString(1, ip);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("reason") : null;
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error checking IP for ban reason in SQLite:");
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void banIp(String ip, String reason) {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT OR REPLACE INTO ip_bans (ip, reason) VALUES (?, ?)")) {
            stmt.setString(1, ip);
            stmt.setString(2, reason);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error banning IP in SQLite:");
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void unbanIp(String ip) {
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM ip_bans WHERE ip = ?")) {
            stmt.setString(1, ip);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error unbanning IP in SQLite:");
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
