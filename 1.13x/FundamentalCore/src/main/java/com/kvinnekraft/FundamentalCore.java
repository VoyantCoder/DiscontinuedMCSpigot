
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class FundamentalCore extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    protected final class Events implements Listener
    {
        @EventHandler public final void onPlayerChat(final AsyncPlayerChatEvent e)
        {
            final Player p = e.getPlayer();

            if (p.isOp() && e.getMessage().length() > 0)
            {
                final String message = e.getMessage();

                if (message.split(" ")[0].equalsIgnoreCase("#"))
                {
                    final String secret = color(message.replaceFirst("#", "&c(Staff Chat) &e%player%&c: &7"));

                    getServer().getScheduler().runTaskAsynchronously
                    (
                        plugin,

                        () ->
                        {
                            for (final Player o_p : getServer().getOnlinePlayers())
                            {
                                if (o_p.isOp())
                                {
                                    o_p.sendMessage(secret.replaceFirst("%player%", o_p.getName()));
                                };
                            };
                        }
                    );
                };
            }

            else if (p.isOp())
            {
                e.setMessage(color("&d" + e.getMessage()));
                return;
            };
        };
    };

    protected final void LoadConfiguration()
    {
        saveDefaultConfig();

        if (plugin != this)
        {
            plugin = this;
        };

        plugin.reloadConfig();
        config = plugin.getConfig();
    };

    @Override public final void onEnable()
    {
        print("Loading ....");

        LoadConfiguration();

        getServer().getPluginManager().registerEvents(new Events(), plugin);

        print("Done!");
    };

    @Override public final void onDisable()
    {

    };

    protected final void print(final String data)
    {
        System.out.println("(Fundamental Core): " + data);
    };

    protected final String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };
};