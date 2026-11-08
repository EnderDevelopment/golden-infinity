package com.enderdevelopment.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class RitualListener implements Listener {
    private static final long COOLDOWN = 30L * 60L * 1000L;
    private final Main plugin;
    private final Map<UUID, Location> ritualStart = new HashMap<>();
    private final Map<UUID, Location> lastStep = new HashMap<>();
    private final Map<UUID, Integer> ritualSteps = new HashMap<>();
    private final Map<UUID, Boolean> crossedCenter = new HashMap<>();

    public RitualListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null || event.getFrom().getBlock().equals(event.getTo().getBlock())) return;
        Player player = event.getPlayer();
        Location current = event.getTo().getBlock().getLocation();
        Location previous = lastStep.get(player.getUniqueId());
        if (previous != null && previous.getWorld() == current.getWorld() && previous.distanceSquared(current) < 2) return;
        lastStep.put(player.getUniqueId(), current);
        Location start = ritualStart.computeIfAbsent(player.getUniqueId(), ignored -> current);
        if (!start.getWorld().equals(current.getWorld()) || start.distanceSquared(current) > 100) {
            ritualStart.put(player.getUniqueId(), current);
            ritualSteps.put(player.getUniqueId(), 0);
            crossedCenter.put(player.getUniqueId(), false);
            return;
        }
        int steps = ritualSteps.merge(player.getUniqueId(), 1, Integer::sum);
        double x = current.getX() - start.getX();
        double z = current.getZ() - start.getZ();
        if (steps > 5 && Math.abs(x) < 1.5 && Math.abs(z) < 1.5) crossedCenter.put(player.getUniqueId(), true);
        if (steps >= 16 && Boolean.TRUE.equals(crossedCenter.get(player.getUniqueId())) && start.distanceSquared(current) < 4) {
            call(player);
            ritualStart.remove(player.getUniqueId());
            ritualSteps.remove(player.getUniqueId());
            crossedCenter.remove(player.getUniqueId());
        }
    }

    public void call(Player player) {
        plugin.summonedUntil().put(player.getUniqueId(), System.currentTimeMillis() + 60_000L);
        speak(player, "I am the Manifestation. Speak your desire with restraint, and I shall weigh it.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Long until = plugin.summonedUntil().get(player.getUniqueId());
        if (until == null || until < System.currentTimeMillis()) return;
        String message = event.getMessage().toLowerCase();
        String wish = message.contains("food") || message.contains("bread") ? "food"
                : message.contains("arrow") ? "arrows"
                : message.contains("experience") || message.contains("xp") ? "experience"
                : message.contains("gold") ? "gold" : null;
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (wish == null) speak(player, "Name a modest boon: food, arrows, experience, or gold.");
            else fulfillWish(player, wish);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        plugin.summonedUntil().remove(id);
        ritualStart.remove(id);
        lastStep.remove(id);
        ritualSteps.remove(id);
        crossedCenter.remove(id);
    }

    public void fulfillWish(Player player, String wish) {
        long now = System.currentTimeMillis();
        Long last = plugin.cooldowns().get(player.getUniqueId());
        if (last != null && now - last < COOLDOWN) {
            long minutes = Math.max(1L, (COOLDOWN - (now - last)) / 60_000L);
            speak(player, "Patience is the price of abundance. Return in " + minutes + " minute(s).");
            return;
        }
        switch (wish.toLowerCase()) {
            case "food" -> player.getInventory().addItem(new ItemStack(Material.BREAD, 8));
            case "arrows" -> player.getInventory().addItem(new ItemStack(Material.ARROW, 16));
            case "experience" -> player.giveExp(12);
            case "gold" -> player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 2));
            default -> { speak(player, "I grant only modest gifts: food, arrows, experience, or gold."); return; }
        }
        plugin.cooldowns().put(player.getUniqueId(), now);
        speak(player, "Granted. May this small fortune serve a noble purpose.");
    }

    public void displayAuras() {
        for (Map.Entry<UUID, Long> entry : plugin.summonedUntil().entrySet()) {
            if (entry.getValue() < System.currentTimeMillis()) continue;
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;
            Location center = player.getLocation().add(0, 1.2, 0);
            World world = player.getWorld();
            for (int i = 0; i < 24; i++) {
                double angle = Math.PI * 2 * i / 24.0;
                world.spawnParticle(Particle.DUST, center.clone().add(Math.cos(angle) * 1.1, Math.sin(angle) * 1.1, 0), 1,
                        new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 196, 45), 1.2f));
                world.spawnParticle(Particle.SOUL, center.clone().add(Math.cos(angle) * 0.7, 0.2, Math.sin(angle) * 0.7), 1);
            }
            world.spawnParticle(Particle.DUST, center, 5, 0.35, 0.7, 0.35,
                    new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 215, 70), 1.5f));
        }
    }

    private void speak(Player player, String text) {
        player.sendActionBar(Component.text("✦ Manifestation: ").color(NamedTextColor.GOLD)
                .append(Component.text(text).color(NamedTextColor.WHITE)));
    }
}
