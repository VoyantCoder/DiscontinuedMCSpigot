
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public final class IntervalCommands extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    final List<String> commands = new ArrayList<>();

    BukkitTask task = null;
    Integer delay;

    protected void LoadConfiguration()
    {
        saveDefaultConfig();

        if (plugin != this)
            plugin = this;

        plugin.reloadConfig();
        config = plugin.getConfig();

        getServer().getScheduler().cancelTasks(plugin);

        try
        {
            admin_permission = config.getString("command-execution.admin-permission");
            delay = config.getInt("command-execution.interval") * 20;

            commands.clear();
            commands.addAll(config.getStringList("command-execution.commands"));
        }

        catch (final Exception e)
        {
            print("A configuration error has occurred. Contact me at KvinneKraft@protonmail.com.");
        };

        StartRunning();
    };

    protected void StartRunning()
    {
        getServer().getScheduler().runTaskTimer
        (
            plugin,

            new Runnable()
            {
                final ConsoleCommandSender cs = getServer().getConsoleSender();

                @Override public void run()
                {
                    for (final String command : commands)
                    {
                        getServer().dispatchCommand(cs, command);
                    };
                };
            },

            delay,
            delay
        );
    };

    String admin_permission;

    protected final class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("Only players may do this.");
                return false;
            };

            final Player p = (Player) s;

            if (p.hasPermission(admin_permission))
            {
                if (as.length > 0)
                {
                    a = as[0].toLowerCase();

                    if (a.equals("reload"))
                    {
                        LoadConfiguration();
                        p.sendMessage(color("&aDone!"));
                        return true;
                    };
                };

                p.sendMessage(color("&cDid you mean &7/intervalcommands reload &c?"));
                return false;
            };

            p.sendMessage(color("&cYou are not allowed to do this."));
            return false;
        };
    };

    @Override public void onEnable()
    {
        print("I am slowly loading in ....");

        LoadConfiguration();

        getCommand("intervalcommands").setExecutor(new Commands());

        print("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\nAuthor: Dashie \nVersion: 1.0 \nEmail: KvinneKraft@protonmail.com \nGithub: https://github.com/KvinneKraft \n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        print("I have successfully loaded up and am now running in the background!");
    };

    @Override public void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("Oawhhhhh, you killed me.");
    };

    protected void print(final String data)
    {
        System.out.println("(Interval Commands): " + data);
    };

    protected String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };
};
