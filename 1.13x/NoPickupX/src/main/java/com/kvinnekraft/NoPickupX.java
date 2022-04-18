
// Author: Dashie
// Version: 1.0

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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoPickupX extends JavaPlugin
{
    final List<Player> players = new ArrayList<>();

    boolean sendToPlayer = true;
    int notifyInterval = 3;

    private class EventListener implements Listener
    {
        @EventHandler public final void onPlayerItemPickup(final EntityPickupItemEvent e)
        {
            if (!(e.getEntity() instanceof Player))
            {
                return;
            }

            final Player p = (Player) e.getEntity();

            if (!p.hasPermission(bypass))
            {
                final Material item = e.getItem().getItemStack().getType();

                if (blacklist.contains(item))
                {
                    if (!players.contains(p))
                    {
                        getServer().getScheduler().runTaskLaterAsynchronously
                        (
                            plugin,

                            () -> players.remove(p),

                            notifyInterval
                        );

                        players.add(p);

                        p.sendMessage(color("&cYou may not pick this item up!"));
                    }

                    e.setCancelled(true);
                }
            }
        }
    }

    final List<Material> blacklist = new ArrayList<>();

    String bypass = "nopickupx.pickup";

    private void loadSettings()
    {
        try
        {
            saveDefaultConfig();

            final FileConfiguration config = plugin.getConfig();

            bypass = config.getString("core-tweaks.bypass-permission");

            for (final String fragment : config.getStringList("core-tweaks.blacklisted-items"))
            {
                final List<String> fragments = Arrays.asList(fragment.split(" "));

                if (fragments.size() < 1 || !fragments.get(0).contains("item:"))
                {
                    print("Invalid node found at: core-tweaks.blacklisted-items." + fragment + ".  Skipping....");
                    continue;
                }

                final String fragment_a = fragments.get(0);
                final ItemStack item = new ItemStack(Material.AIR, 1);

                try
                {
                    final List<String> mate = Arrays.asList(fragment_a.split(":"));
                    item.setType(Material.valueOf(mate.get(1).toUpperCase()));
                }

                catch (final Exception e)
                {
                    print("An item specified was invalid and thus this line is being skipped.  Proceeding....");
                    continue;
                }

                blacklist.add(item.getType());//detail index of key == index of blacklist
            }

            notifyInterval = config.getInt("core-tweaks.notifications.notify-interval") * 20;
            sendToPlayer = config.getBoolean("core-tweaks.notifications.send-to-player");
        }

        catch (final Exception e)
        {
            for (final StackTraceElement d : e.getStackTrace())
            {
                print(d.toString());
            }

            shutdownPlugin("An error has occurred whilst loading configuration directly from plugin.yml.  Shutting down....");
        }
    }

    private class CommandListener implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only use this as a player!");
                return false;
            }

            final Player p = (Player) s;

            if (!p.isOp())
            {
                p.sendMessage(color("&cNothing here man, sorry."));
                return true;
            }

            p.sendMessage(color("&6>>> &eAuthor: Dashie and KvinneKraft"));
            p.sendMessage(color("&6>>> &eVersion: 1.0"));
            p.sendMessage(color("&6>>> &eGithub: https://github.com/KvinneKraft"));

            return true;
        }
    }

    final JavaPlugin plugin = this;

    boolean autoReload = true;
    int reloadInterval = 5;

    @Override public final void onEnable()
    {
        try
        {
            final FileConfiguration config = plugin.getConfig();

            saveDefaultConfig();

            autoReload = config.getBoolean("global-tweaks.auto-reload");

            if (autoReload)
            {
                try
                {
                    reloadInterval = config.getInt("global-tweaks.reload-interval") * 20;

                    getServer().getScheduler().runTaskTimerAsynchronously
                    (
                        plugin,

                        this::loadSettings,

                        10, reloadInterval
                    );
                }

                catch (final Exception e)
                {
                    print("The auto-reload has crashed itself.  Please make sure the config.yml has been setup correctly!  Disabling auto-reload ....");
                    autoReload = false;
                    return;
                }
            }

            else
            {
                loadSettings();
            }

            getServer().getPluginManager().registerEvents(new EventListener(), plugin);
            getCommand("nopickupx").setExecutor(new CommandListener());
        }

        catch (final Exception e)
        {
            shutdownPlugin("There was an error while initializing the plugin.  Shutting down....");
        }

        print("Author: Dashie");
        print("Version: 1.0");
        print("Github: https://github.com/KvinneKraft");
        print("Email: KvinneKraft@protonmail.com");
    }

    private void shutdownPlugin(final String reason)
    {
        print(reason);
        getServer().getScheduler().runTask(plugin, () -> getServer().getPluginManager().disablePlugin(plugin));
    }

    @Override public final void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("I am dead!");
    }

    private String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    private void print(final String data)
    {
        System.out.println("(No Pickup X): " + data);
    }
}