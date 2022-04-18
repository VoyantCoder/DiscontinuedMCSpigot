
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class SChunk extends JavaPlugin
{
    final String color(final String data) { return ChatColor.translateAlternateColorCodes('&', data); }
    final void print(final String data) { System.out.println("(SChunk): " + data); }

    final class RegisterMe implements Listener
    {
        private void PM(final Player p, final String m) { p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(m)); }

        private final List<Player> players = new ArrayList<>();
        private final List<Player> oplayer = new ArrayList<>();

        final void registerCooldownCooldown(final Player p)
        {
            oplayer.add(p);

            new BukkitRunnable()
            {
                @Override public final void run()
                {
                    oplayer.remove(p);
                }
            }.runTaskLater(plugin, 500);
        }

        @EventHandler public final void onPlayerInteract(final PlayerInteractEvent e)
        {
            final Player p = e.getPlayer();

            if (!p.hasPermission(Config.PERMISSION))
            {

            }

            else if (players.contains(p))
            {
                if (!oplayer.contains(p))
                {
                    p.sendMessage(Config.COOLDOWN_MESSAGE);
                    registerCooldownCooldown(p);
                }
            }

            else
            {
                final ItemStack i = e.getItem();

                if (i != null && i.getType().equals(Material.SLIME_BALL))
                {
                    final Location l = p.getLocation();

                    if (l.getChunk().isSlimeChunk())
                    {
                        PM(p, Config.MESSAGES[0]);
                        p.sendMessage(Config.MESSAGES[0]);
                    }

                    else
                    {
                        PM(p, Config.MESSAGES[1]);
                        p.sendMessage(Config.MESSAGES[1]);
                    }

                    registerCooldownCooldown(p);
                    players.add(p);

                    new BukkitRunnable()
                    {
                        @Override public final void run()
                        {
                            players.remove(p);
                        }
                    }.runTaskLater(plugin, Config.COOLDOWN);
                }
            }
        }
    }

    public static final class Config
    {
        public static String[] MESSAGES = { "FOUND", "NOT FOUND" };
        public static String PERMISSION = "NONE";

        public static String COOLDOWN_MESSAGE = "YA";
        public static Integer COOLDOWN = 0;
    }

    private FileConfiguration config;
    private JavaPlugin plugin = this;

    final void LoadConfiguration()
    {
        saveDefaultConfig();
        reloadConfig();

        if (config != getConfig())
        {
            config = getConfig();
        }

        final String a1 = color(config.getString("found-message"));
        final String a2 = color(config.getString("not-found-message"));

        Config.MESSAGES[0] = a1;
        Config.MESSAGES[1] = a2;

        final String a3 = config.getString("finder-permission");
        Config.PERMISSION = a3;

        final Integer a4 = config.getInt("finder-cooldown") * 20;
        Config.COOLDOWN = a4;

        final String a5 = color(config.getString("finder-cooldown-message"));
        Config.COOLDOWN_MESSAGE = a5;
    }

    @Override public final void onEnable()
    {
        print("I am being enabled ....");

        try
        {
            LoadConfiguration();

            getServer().getPluginManager().registerEvents(new RegisterMe(), this);
        }

        catch (final Exception e)
        {
            print("There was an error while starting the plugin!\r\n\r\nStack-trace:\r\n" + e.getMessage());
            getPluginLoader().disablePlugin(this);
            return;
        }

        print("I have been enabled!");
    }

    @Override public final void onDisable()
    {
        print("I have been disabled!");
    }
}