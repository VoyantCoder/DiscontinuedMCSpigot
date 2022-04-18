package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class BreakRestrict extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    protected void LoadConfiguration()
    {
        if (plugin != this)
        {
            plugin = (JavaPlugin) this;
        };

        saveDefaultConfig();

        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();

        try
        {
            bypass_permission = config.getString("permissions.break-restrictor-bypass");
            admin_permission = config.getString("permissions.break-restrictor-reload");

            block_types.clear();

            for (final String element : config.getStringList("break-restrictor.restricted-blocks"))
            {
                try
                {
                    final Material substance = Material.valueOf(element.toUpperCase().replace(" ", "_"));

                    if (substance == null)
                    {
                        throw new Exception("ERROR");
                    };

                    block_types.add(substance);
                }

                catch (final Exception e)
                {
                    print("An invalid element was found in the 'restricted-blocks' section of the configuration file.");
                    print("Skipping this element ....");
                };
            };

            restriction_message_onscreen = config.getBoolean("break-restrictor.restriction-message-onscreen");
            restriction_message = color(config.getString("break-restrictor.restriction-message"));
        }

        catch (final Exception e)
        {
            print("There was a configuration error while loading the configuration for this plugin.");
            print("Please contact me at KvinneKraft@protonmail about this!");
        };
    };

    @Override public void onEnable()
    {
        print("A.I. is booting up ....");

        LoadConfiguration();

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("breakrestrictor").setExecutor(new Commands());

        print("\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\nAuthor: Dashie\nVersion: 1.0\nEmail: KvinneKraft@protonmail.com\nGithub: https://github.com/KvinneKraft \n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

        print("A.I. is now running!");
    };

    String admin_permission;

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

            if (p.hasPermission(admin_permission))
            {
                if (as.length >= 1)
                {
                    if (as[0].equalsIgnoreCase("reload"))
                    {
                        p.sendMessage(color("&eProcessing configuration data ...."));

                        LoadConfiguration();

                        p.sendMessage(color("&eDone!"));

                        return true;
                    };
                };

                p.sendMessage(color("&cDid you perhaps mean &e&o/breakrestrict reload &c?"));
                return true;
            };

            p.sendMessage(color("&cYou lack sufficient permissions!"));
            return true;
        };
    };

    final List<Material> block_types = new ArrayList<>();

    String bypass_permission, restriction_message;
    boolean restriction_message_onscreen;

    protected class Events implements Listener
    {
        @EventHandler public void onPlayerBreak(final BlockBreakEvent e)
        {
            final Player p = (Player) e.getPlayer();

            if (!p.hasPermission(bypass_permission))
            {
                if (block_types.contains(e.getBlock().getType()))
                {
                    if (restriction_message_onscreen)
                    {
                        p.sendTitle(color("&c&lHey There!"), restriction_message, 10, 120, 10);
                    }

                    else
                    {
                        p.sendMessage(restriction_message);
                    };

                    e.setCancelled(true);
                };
            };
        };
    };

    @Override public void onDisable()
    {
        print("A.I. is now offline!");
    };

    protected String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };

    protected void print(final String d)
    {
        System.out.println("(Break Restrict): " + d);
    };
};
