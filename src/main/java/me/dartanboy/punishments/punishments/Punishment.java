package me.dartanboy.punishments.punishments;

import java.util.UUID;

public class Punishment {

    private final PunishmentType punishmentType;
    private final UUID playerUUID;
    private final UUID punishmentId;
    private final String reason;
    private final long expiryTime;
    private boolean active;

    public Punishment(PunishmentType punishmentType, UUID playerUUID, String reason, long expiryTime) {
        this.punishmentType = punishmentType;
        this.playerUUID = playerUUID;
        this.reason = reason;
        this.expiryTime = expiryTime;
        this.punishmentId = UUID.randomUUID();
        this.active = true;
    }

    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getReason() {
        return reason;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public UUID getPunishmentId() {
        return punishmentId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}
