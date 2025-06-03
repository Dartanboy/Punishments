package me.dartanboy.punishments;

import me.dartanboy.punishments.commands.*;
import me.dartanboy.punishments.db.PunishmentDB;
import me.dartanboy.punishments.db.impl.MemoryPunishmentDB;
import me.dartanboy.punishments.listeners.PunishmentListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Punishments extends JavaPlugin {
    private PunishmentDB punishmentDB;

    @Override
    public void onEnable() {
        punishmentDB = new MemoryPunishmentDB();

        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("tempban").setExecutor(new TempBanCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("tempmute").setExecutor(new TempMuteCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));

        getServer().getPluginManager().registerEvents(new PunishmentListener(this), this);
    }

    public PunishmentDB getPunishmentDB() {
        return punishmentDB;
    }
}
