package com.enderdevelopment.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class Main extends JavaPlugin {
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> summonedUntil = new HashMap<>();
    private RitualListener ritualListener;
    private BukkitTask auraTask;

    @Override
    public void onEnable() {
        ritualListener = new RitualListener(this);
        Bukkit.getPluginManager().registerEvents(ritualListener, this);
        auraTask = Bukkit.getScheduler().runTaskTimer(this, ritualListener::displayAuras, 1L, 8L);
        getLogger().info("The Manifestation is listening for worthy wishes.");
    }

    @Override
    public void onDisable() {
        if (auraTask != null) {
            auraTask.cancel();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This ritual can only be performed by a player.");
            return true;
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("call")) {
            ritualListener.call(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("wish") && args.length >= 2) {
            ritualListener.fulfillWish(player, args[1]);
            return true;
        }
        player.sendMessage("Usage: /manifestation call or /manifestation wish <food|arrows|experience|gold>");
        return true;
    }

    public Map<UUID, Long> cooldowns() {
        return cooldowns;
    }

    public Map<UUID, Long> summonedUntil() {
        return summonedUntil;
    }
}
