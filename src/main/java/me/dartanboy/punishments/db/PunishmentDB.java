package me.dartanboy.punishments.db;

import me.dartanboy.punishments.punishments.Punishment;

import java.util.List;
import java.util.UUID;

public interface PunishmentDB {

    List<Punishment> getPunishments(UUID playerUUID);

    void addPunishment(UUID playerUUID, Punishment punishment);

    void removePunishment(UUID punishmentId);
}
