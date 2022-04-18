
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class WorldCommands extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    protected void LoadConfiguration()
    {
        saveDefaultConfig();

        if (plugin != this)
        {
            plugin = (JavaPlugin) this;
        };

        plugin.reloadConfig();
        config = plugin.getConfig();

        try
        {
            commandlist.clear();
            worldlist.clear();

            commandlist.addAll(config.getStringList("command-list"));
            worldlist.addAll(config.getStringList("world-list"));
        }

        catch (final Exception e)
        {
            print("A configuration error has occurred. Please contact me at KvinneKraft@protonmail.com if this persists!");
        };
    };

    @Override public void onEnable()
    {
        print("I am being resurrected ....");

        LoadConfiguration();

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("worldcommands").setExecutor(new Commands());

        print("I have been resurrected!");
    };

    final List<String> commandlist = new ArrayList<>();
    final List<String> worldlist = new ArrayList<>();

    protected class Events implements Listener
    {
        @EventHandler public void onPlayerCommand(final PlayerCommandPreprocessEvent e)
        {
            final Player p = (Player) e.getPlayer();

            if (worldlist.contains(p.getWorld().getName()) && !p.isOp())
            {
                if (!commandlist.contains(e.getMessage().split(" ")[0].toLowerCase()))
                {
                    p.sendMessage(color("&cYou may not use this command here!"));
                    e.setCancelled(true);

                    return;
                };
            };
        };
    };

    protected class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player!");
                return false;
            };

            final Player p = (Player) s;

            if (p.isOp())
            {
                if (as.length >= 1)
                {
                    if (as[0].equalsIgnoreCase("reload"))
                    {
                        LoadConfiguration();
                        p.sendMessage(color("&aDone!"));
                        return true;
                    };
                };

                p.sendMessage(color("&cDid you mean: &7/worldcommands reload"));
                return false;
            };

            p.sendMessage(color("&cYou may not do this!"));
            return false;
        };
    };

    @Override public void onDisable()
    {
        print("I am dead now.");
    };

    protected String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };

    protected void print(final String data)
    {
        System.out.println("(World Commands): " + data);
    };
};