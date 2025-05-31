package me.dartanboy.punishments;

import me.dartanboy.punishments.commands.BanCommand;
import me.dartanboy.punishments.commands.TempBanCommand;
import me.dartanboy.punishments.db.PunishmentDB;
import me.dartanboy.punishments.db.impl.MemoryPunishmentDB;
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
    }

    public PunishmentDB getPunishmentDB() {
        return punishmentDB;
    }
}
