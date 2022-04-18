
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class DamageTeleport extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    protected void LoadConfiguration()
    {
        if (plugin != this)
            plugin = this;

        saveDefaultConfig();

        plugin.reloadConfig();
        config = plugin.getConfig();

        try
        {
            max_x = config.getInt("maximum-x");
            min_x = config.getInt("minimum-x");

            max_z = config.getInt("maximum-z");
            min_z = config.getInt("minimum-z");

            tp_perm = config.getString("teleportation-permission");
            admin_perm = config.getString("reload-permission");
        }

        catch (final Exception e)
        {
            print("An unknown configuration error has occurred, please contact me at KvinneKraft@protonmail.com if this issue persists.");
        };
    };

    int max_x, max_z, min_x, min_z;
    String tp_perm, admin_perm;

    protected final class Events implements Listener
    {
        final List<Player> player_queue = new ArrayList<>();

        @EventHandler public void onEntityHurt(final EntityDamageByEntityEvent e)
        {
            if ((isEnabled) && ((e.getEntity() instanceof Player)))
            {
                final Player p = (Player) e.getEntity();

                if (player_queue.contains(p) || !p.hasPermission(tp_perm))
                {
                    return;
                };

                player_queue.add(p);

                getServer().getScheduler().runTaskAsynchronously
                (
                    plugin,

                    new Runnable()
                    {
                        private int random(int max, int min)
                        {
                            return min + (int)(Math.random() * ((max - min) + 1));
                        };

                        @Override public void run()
                        {
                            int x = random(max_x, min_x);

                            if (random(1, 0) == 0)
                                x += p.getLocation().getX();
                            else
                                x = (int) p.getLocation().getX() - x;

                            int z = random(max_z, min_z);

                            if (random(1, 0) == 0)
                                z += p.getLocation().getX();
                            else
                                z = (int) p.getLocation().getX() - x;

                            final Location l = new Location(p.getWorld(), x, 25, z);

                            while (true)
                            {
                                l.setY(l.getY() - 1);

                                if (!l.getBlock().getType().equals(Material.AIR))
                                {
                                    l.setY(l.getY() + 1);

                                    if (l.getBlock().getType().equals(Material.AIR))
                                    {
                                        l.setY(l.getY() + 1);

                                        if (l.getBlock().getType().equals(Material.AIR))
                                        {
                                            l.setY(l.getY() - 1);

                                            getServer().getScheduler().runTask
                                            (
                                                plugin,

                                                new Runnable()
                                                {
                                                    @Override public void run()
                                                    {
                                                        player_queue.remove(p);
                                                        p.teleport(l);
                                                    };
                                                }
                                            );

                                            break;
                                        };
                                    };
                                };

                                if (l.getY() > 200)
                                {
                                    print("Unable to teleport the player due to invalid environment.");
                                    break;
                                };

                                l.setY(l.getY() + 1);
                            };
                        };
                    }
                );
            };
        };
    };

    @Override public void onEnable()
    {
        print("I am crawling out of my grave ....");

        LoadConfiguration();

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("damageteleport").setExecutor(new Commands());

        print("I am now alive! UNDEAD!");
    };

    boolean isEnabled = true;

    protected final class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player.");
                return false;
            };

            final Player p = (Player) s;

            if (p.hasPermission(admin_perm))
            {
                if (as.length > 0)
                {
                    if (as[0].equalsIgnoreCase("reload"))
                    {
                        LoadConfiguration();
                        p.sendMessage(color("&aDone!"));
                        return true;
                    }

                    else if (as[0].equalsIgnoreCase("toggle"))
                    {
                        if (isEnabled)
                        {
                            isEnabled = false;
                            p.sendMessage(color("&aDamage Triggers have been disabled."));
                        }
// I mean, you can also just ask me to recode it.
                        else
                        {
                            isEnabled = true;
                            p.sendMessage(color("&aDamage Triggers have been enabled."));
                        };

                        return true;
                    };
                };

                p.sendMessage(color("&cValid syntax: &7/damageteleport [reload | toggle]"));
                return false;
            };

            p.sendMessage(color("&cYou may not do this."));
            return false;
        };
    };

    @Override public void onDisable()
    {
        print("The plugin is now no longer running.");
    };

    protected String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };

    protected void print(final String data)
    {
        System.out.println("(Damage Teleport): " + data);
    };
};