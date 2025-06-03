package me.dartanboy.punishments.db.impl;

import me.dartanboy.punishments.db.PunishmentDB;
import me.dartanboy.punishments.punishments.Punishment;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class MemoryPunishmentDB implements PunishmentDB {

    private final Map<UUID, List<Punishment>> punishments = new HashMap<>();
    private final Map<UUID, List<String>> ipMap = new HashMap<>();
    private final List<String> bannedIps = new ArrayList<>();

    @Override
    public List<Punishment> getPunishments(UUID playerUUID) {
        return punishments.getOrDefault(playerUUID, new ArrayList<>());
    }

    @Override
    public void addPunishment(UUID playerUUID, Punishment punishment) {
        List<Punishment> punishmentList = getPunishments(playerUUID);
        punishmentList.add(punishment);
        punishments.put(playerUUID, punishmentList);
    }

    @Override
    public boolean isBanned(String ip) {
        return bannedIps.contains(ip);
    }

    @Override
    public void banIp(String ip) {
        bannedIps.add(ip);
    }

    @Override
    public void unbanIp(String ip) {
        bannedIps.remove(ip);
    }

    @Override
    public void removePunishment(UUID punishmentId) {
        for (List<Punishment> list : punishments.values()) {
            for (int i = 0; i < list.size(); i++) {
                Punishment punishment = list.get(i);
                list.remove(punishment);
                punishment.setActive(false);
                list.add(punishment);
            }
        }
    }

    @Override
    public void registerIp(UUID playerUUID, String ip) {
        List<String> ipList = ipMap.getOrDefault(playerUUID, new ArrayList<>());
        ipList.add(ip);
        ipMap.put(playerUUID, ipList);
    }

    @Override
    public List<String> getIps(UUID playerUUID) {
        return ipMap.getOrDefault(playerUUID, null);
    }
}
