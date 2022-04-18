package com.kvinnekraft.autodox;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class AutoDoX extends JavaPlugin
{
    final JavaPlugin plugin = this;

    private class CommandListener implements CommandExecutor
    {
        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player.");
                return false;
            }

            final Player p = (Player) s;

            if (!p.isOp())
            {
                p.sendMessage(color("&b[&3Auto Do X 1.0 by Dashie&b]"));
                p.sendMessage(color("&6>>> &eAuthor: Dashie"));
                p.sendMessage(color("&6>>> &eVersion: 1.0"));
                p.sendMessage(color("&6>>> &eGithub: https://github.com/KvinneKraft"));
                p.sendMessage(color("&6>>> &eEmail: KvinneKraft@protonmail.com"));

                return true;
            }

            if (as.length > 0)
            {
                if (as[0].equalsIgnoreCase("toggle") || as[0].equalsIgnoreCase("t"))
                {
                    if (isRunning)
                    {
                        isRunning = false;

                        p.sendMessage(color("&aYou have turned all timers off."));

                        getServer().getScheduler().cancelTasks(plugin);
                    }

                    else
                    {
                        isRunning = true;

                        p.sendMessage(color("&aYou have turned all timers on."));

                        loadSettings();
                    }

                    return true;
                }
            }

            p.sendMessage(color("Correct syntax: &7/AutoDoX [toggle | t]"));
            return true;
        }
    }

    boolean isRunning = false;

    private void loadSettings()
    {
        saveDefaultConfig();

        try
        {
            final FileConfiguration config = getConfig();

            for (int k = 1; ; k += 1)
            {
                if (config.contains("schedule-settings." + k))
                {
                    final String node = "schedule-settings." + k + ".";

                    int interval = config.getInt(node + "interval") * 20;

                    getServer().getScheduler().runTaskTimerAsynchronously
                    (
                        plugin,

                        () ->
                        {
                            final List<String> commands = config.getStringList(node + "commands");

                            getServer().getScheduler().runTask
                            (
                                plugin,

                                () ->
                                {
                                    for (final String command : commands)
                                    {
                                        getServer().dispatchCommand(getServer().getConsoleSender(), command);
                                    }
                                }
                            );
                        },

                        interval,
                        interval
                    );

                    continue;
                }

                break;
            }

            isRunning = true;
        }

        catch (final Exception e)
        {
            shutdownPlugin("Unable to read config.yml. Shutting down ....");
        }
    }

    @Override public final void onEnable()
    {
        saveDefaultConfig();

        try
        {
            loadSettings();

            getCommand("autodox").setExecutor(new CommandListener());
        }

        catch (final Exception e)
        {
            shutdownPlugin("The plugin failed to initialize.  Shutting down ....");
        }

        print("Author: Dashie");
        print("Version: 1.0");
        print("Github: https://github.com/KvinneKraft");
        print("Email: KvinneKraft@protonmail.com");
    }

    private void shutdownPlugin(final String reason)
    {
        print(reason);
        getServer().getPluginManager().disablePlugin(plugin);
    }

    @Override public final void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("I am dead!");
    }

    private String color(final String data) {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    private void print(final String data) {
        System.out.println("(No Store X): " + data);
    }
}