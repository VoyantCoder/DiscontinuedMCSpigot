
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
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NoCraftX extends JavaPlugin
{
    class CommandListener implements CommandExecutor
    {
        private final void saveSettings()
        {
            saveDefaultConfig();

            final FileConfiguration config = getConfig();
            final List<String> rawRecipes = new ArrayList<>();

            for (final Material material : blockedRecipes)
            {
                final String name = material.toString();

                if (!rawRecipes.contains(name))
                {
                    rawRecipes.add(name);
                }
            }

            config.set("craft-settings.blocked-recipes", rawRecipes);

            plugin.saveConfig();
            plugin.reloadConfig();
        }

        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only use this as a player!");
                return false;
            }

            final Player p = (Player) s;

            if (p.isOp())
            {
                if (as.length > 0)
                {
                    if (as.length > 1)
                    {
                        try
                        {
                            final Material material = Material.valueOf(as[1]);

                            if (as[0].equalsIgnoreCase("add"))
                            {
                                if (blockedRecipes.contains(material))
                                {
                                    p.sendMessage(color("&cThis item is already in your blacklist."));
                                    return false;
                                }

                                blockedRecipes.add(material);

                                p.sendMessage(color("&aYou have added &e" + as[1] + " &ato the recipe block list."));
                            }

                            else if (as[0].equalsIgnoreCase("del"))
                            {
                                if (!blockedRecipes.contains(material))
                                {
                                    p.sendMessage(color("&cThis item could not be found in your blacklist!"));
                                    return false;
                                }

                                blockedRecipes.remove(material);

                                p.sendMessage(color("&aYou have removed &e" + as[1] + " &afrom the recipe block list."));
                            }

                            else
                            {
                                p.sendMessage(color("&cYou can choose from &7add &cand &7del &c, try again."));
                                return false;
                            }

                            saveSettings();
                        }

                        catch (final Exception e)
                        {
                            p.sendMessage(color("&cYou must specify a valid item name."));
                        }

                        return true;
                    }
                }

                p.sendMessage(color("&aValid syntax: &7/nocraft [add | del] [material name]"));
                p.sendMessage(color("&aExample: &7/nocraft add IRON_BLOCK"));

                return false;
            }

            p.sendMessage(color("&6>>> &eAuthor: Dashie"));
            p.sendMessage(color("&6>>> &eVersion: 2.0"));
            p.sendMessage(color("&6>>> &eGithub: https://github.com/KvinneKraft"));

            return false;
        }
    }

    class EventListener implements Listener
    {
        @EventHandler public final void onPlayerCraft(final CraftItemEvent e)
        {
            if ((e.getViewers().size() > 0) || (!(e.getViewers().get(0) instanceof Player)))
            {
                return;
            }

            final Player p = (Player) e.getViewers().get(0);

            if (!p.hasPermission(ignorePermission))
            {
                final ItemStack result = e.getInventory().getResult();

                if (result != null && result.getType() != Material.AIR)
                {
                    if (blockedRecipes.contains(result.getType()))
                    {
                        p.sendMessage(color("&cYou may not craft this!"));
                        p.closeInventory();

                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    final List<Material> blockedRecipes = new ArrayList<>();

    String ignorePermission = "craft.bypass";
    Boolean disableCrafting = false;

    final void loadSettings()
    {
        saveDefaultConfig();
        reloadConfig();

        try
        {
            final FileConfiguration config = getConfig();

            disableCrafting = config.getBoolean("craft-settings.disable-crafting");

            if (!disableCrafting)
            {
                ignorePermission = config.getString("craft-settings.ignore-permission");

                for (final String text : config.getStringList("craft-settings.blocked-recipes"))
                {
                    try
                    {
                        final Material material = Material.valueOf(text.toUpperCase().replace(" ", "_"));
                        blockedRecipes.add(material);
                    }

                    catch (final Exception e)
                    {
                        print("Invalid recipe found at: craft-settings.blocked-recipes." + text);
                    }
                }
            }
        }

        catch (final Exception e)
        {
            shutdownPlugin("Unable to initialize configuration from config.yml!");
        }
    }

    final JavaPlugin plugin = this;

    boolean autoReload = false;
    int reloadInterval = 5;

    @Override public final void onEnable()
    {
        try
        {
            final FileConfiguration config = getConfig();

            autoReload = config.getBoolean("properties.auto-reload");

            if (autoReload)
            {
                reloadInterval = config.getInt("properties.reload-interval") * 20;

                getServer().getScheduler().runTaskTimerAsynchronously
                (
                    plugin,

                    this::loadSettings,

                    0,
                    reloadInterval
                );
            }

            else
            {
                loadSettings();
            }

            getServer().getPluginManager().registerEvents(new EventListener(), plugin);
            getCommand("nocraftx").setExecutor(new CommandListener());
        }

        catch (final Exception e)
        {
            shutdownPlugin("An error has occurred which has made the plugin unable to function.");
        }

        print("Author: Dashie");
        print("Version: 2.0");
        print("Email: KvinneKraft@protonmail.com");
        print("Github: https://github.com/KvinneKraft");
    }

    final void shutdownPlugin(final String reason)
    {
        print(reason);
        getServer().getPluginManager().disablePlugin(plugin);
    }

    @Override public final void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("I am dead.");
    }

    final String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    final void print(final String data)
    {
        System.out.println("(NoCraftX): " + data);
    }
}