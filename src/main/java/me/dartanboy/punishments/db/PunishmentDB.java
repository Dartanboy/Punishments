package me.dartanboy.punishments.db;

import me.dartanboy.punishments.punishments.Punishment;

import java.util.List;
import java.util.UUID;

public interface PunishmentDB {

    List<Punishment> getPunishments(UUID playerUUID);

    void addPunishment(UUID playerUUID, Punishment punishment);

    boolean isBanned(String ip);

    void banIp(String ip);

    void unbanIp(String ip);

    void removePunishment(UUID punishmentId);

    void registerIp(UUID playerUUID, String ip);

    List<String> getIps(UUID playerUUID);
}
