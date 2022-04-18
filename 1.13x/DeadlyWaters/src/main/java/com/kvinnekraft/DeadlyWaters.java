
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeadlyWaters extends JavaPlugin
{
    JavaPlugin plugin = this;

    @Override public void onEnable()
    {
        print("The plugin is loading ....");

        print("Author: Dashie");
        print("Version: 1.0");
        print("Github: https://github.com/KvinneKraft");
        print("Email: KvinneKraft@protonmail.com");

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("deadlywaters").setExecutor(new Commands());

        print("The plugin has been loaded!");
    };

    protected boolean isEnabled = true;

    protected final class Events implements Listener
    {
        @EventHandler public void onPlayerInteraction(final PlayerMoveEvent e)
        {
            final Player p = e.getPlayer();

            if (isEnabled && !p.hasPermission("bypass"))
            {
                if (e.getPlayer().getWorld().getBlockAt(p.getLocation()).getType().equals(Material.WATER))
                {
                    p.setHealth(0);
                };
            };
        };
    };

    protected final class Commands implements CommandExecutor
    {
        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            Player p = null;

            if (s instanceof Player)
            {
                p = (Player) s;

                if (!p.hasPermission("admin"))
                {
                    p.sendMessage(color("&cYou are not allowed to do this."));
                    return false;
                };
            };

            if (as.length > 0)
            {
                if (as[0].equalsIgnoreCase("toggle") || as[0].equalsIgnoreCase("t"))
                {
                    if (isEnabled)
                    {
                        isEnabled = false;

                        if (p != null)
                        {
                            p.sendMessage(color("&aYou have toggled the plugin off.")); // I know that I just turned off a specific part of the plugin but whatever...
                        }

                        else
                        {
                            print("You have toggled the plugin off.");
                        };
                    }

                    else
                    {
                        isEnabled = true;

                        if (p != null)
                        {
                            p.sendMessage(color("&aYou have toggled the plugin on."));
                        }

                        else
                        {
                            print("You have toggled the plugin on.");
                        };
                    };

                    return true;
                };
            };

            if (p == null)
            {
                print("Perhaps try /deadlywaters toggle !");
            }

            else
            {
                p.sendMessage(color("&cPerhaps try &7/deadlywaters toggle &c!"));
            };

            return false;
        };
    };

    @Override public void onDisable()
    {
        print("The plugin has been disabled!");
    };

    protected final String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };

    protected final void print(final String data)
    {
        System.out.println("(Deadly Waters): " + data);
    };
};