
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NoStoreX extends JavaPlugin
{
    final JavaPlugin plugin = this;

    private class CommandListener implements CommandExecutor
    {
        private void saveToConfig(String type)
        {
            type = type.toUpperCase();

            final List<String> names = new ArrayList<>();

            for (final Material material : blacklist.get(type))
            {
                names.add(material.toString());
            }

            getConfig().set("core-tweaks." + type.toLowerCase().replace("_", "-") + ".blacklist", names);

            plugin.saveConfig();
        }

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
                p.sendMessage(color("&b[&3NoStoreX 1.0 by Dashie&b]"));
                p.sendMessage(color("&6>>> &eAuthor: Dashie"));
                p.sendMessage(color("&6>>> &eVersion: 1.0"));
                p.sendMessage(color("&6>>> &eGithub: https://github.com/KvinneKraft"));
                p.sendMessage(color("&6>>> &eEmail: KvinneKraft@protonmail.com"));

                return true;
            }

            else if (as.length >= 3)
            {
                final String type = as[0].toUpperCase().replace("_", "-");

                if (blacklist.containsKey(type))
                {
                    final String action = as[1].toLowerCase();

                    if (action.equals("add") || action.equals("new") || action.equals("rem") || action.equals("del"))
                    {
                        Material material = Material.AIR;

                        try
                        {
                            material = Material.valueOf(as[2].toUpperCase());
                        }

                        catch (final Exception e)
                        {
                            p.sendMessage(color("&cThe given material is not valid."));
                            return true;
                        }

                        if (action.equals("add") || action.equals("new"))
                        {
                            if (blacklist.get(type).contains(material))
                            {
                                p.sendMessage(color("&cThis material does already exist in the given blacklist."));
                                return true;
                            }

                            blacklist.get(type).add(material);

                            p.sendMessage(color("&aYou have successfully added &e" + material.toString() + " &ato the &e" + type + " &ablacklist!"));
                        }

                        else
                        {
                            if (!blacklist.get(type).contains(material))
                            {
                                p.sendMessage(color("&cThis material does not exist in the given blacklist."));
                                return true;
                            }

                            blacklist.get(type).remove(material);

                            p.sendMessage(color("&aYou have successfully removed &e" + material.toString() + " &afrom the &e" + type + " &ablacklist!"));
                        }

                        saveToConfig(type);
                        return true;
                    }
                }
            }

            p.sendMessage(color("&cAvailable Syntax: &7/nostorex &8[&7ENDER_CHEST&8|&7CHEST&8|&7DISPENSER&8|&7DROPPER&8|&7PLAYER&8|&7WORKBENCH&8|&7BARREL&8|&7ANVIL&8|&7BEACON&8|&7BLAST_FURNACE&8|&7BREWING&8|&7CARTOGRAPHY&8|&7CRAFTING&8|&7ENCHANTING&8|&7HOPPER&8|&7GRINDSTONE&8|&7LECTERN&8|&7MERCHANT&8|&7SHULKER_BOX&8|&7SMITHING&8|&7SMOKER&8|&7STONECUTTER&8] [&7add&8|&7del&8] <&7material name&8>"));
            return true;
        }
    }

    private class EventListener implements Listener
    {
        @EventHandler private void onInventoryOpen(final InventoryOpenEvent e)
        {
            final String type = e.getInventory().getType().toString();

            if (blacklist.containsKey(type))
            {
                if (blacklist.get(type).size() < 1)
                {
                    final Player p = (Player) e.getPlayer();

                    if (!p.isOp())
                    {
                        p.sendMessage(color("&cYou may not open such inventory!"));
                        p.closeInventory();

                        e.setCancelled(true);
                    }
                }
            }
        }

        @EventHandler private void onInventoryInteract(final InventoryClickEvent e)
        {
            final Inventory openInventory = e.getInventory();

            if (e.getWhoClicked() instanceof Player)
            {
                final String type = openInventory.getType().toString();

                if (blacklist.containsKey(type))
                {
                    final ItemStack item = e.getCurrentItem();

                    if (item != null)
                    {
                        if (blacklist.get(type).contains(item.getType()))
                        {
                            final Player p = (Player) e.getWhoClicked();

                            if (!p.isOp())
                            {
                                p.sendMessage(color("&cYou may not put that there!"));
                                p.closeInventory();

                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }

        @EventHandler private void onInventoryClick(final InventoryClickEvent e)
        {
            final Inventory inventory = e.getInventory();

            if (inventory.getViewers().size() > 0)
            {
                final HumanEntity human = inventory.getViewers().get(0);

                if (human instanceof Player)
                {
                    final Player p = (Player) human;
                    final InventoryView openInventory = p.getOpenInventory();

                    if (blacklist.containsKey(openInventory.getType().toString()))
                    {
                        final ItemStack item = e.getCursor();

                        if (item != null)
                        {
                            if (blacklist.get(openInventory.getType().toString()).contains(item.getType()))
                            {
                                if (!p.isOp())
                                {
                                    p.sendMessage(color("&cYou may not put that there!"));
                                    p.closeInventory();

                                    e.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    final HashMap<String, List<Material>> blacklist = new HashMap<>();

    private void loadSettings()
    {
        saveDefaultConfig();

        plugin.reloadConfig();

        try
        {
            final FileConfiguration config = plugin.getConfig();

            for (final String section : new String[] { "ender-chest", "chest", "dispenser", "dropper", "player", "workbench", "barrel", "anvil", "beacon", "blast-furnace", "brewing", "cartography", "carfting", "enchanting", "hopper", "grindstone", "lectern", "merchant", "shulker-box", "smithing", "smoker", "stonecutter" })
            {
                final String node = "core-tweaks." + section + ".";

                try
                {
                    if (config.getBoolean(node + "enabled"))
                    {
                        final List<Material> materials = new ArrayList<>();

                        for (final String crush : config.getStringList(node + "blacklist"))
                        {
                            final Material material = Material.valueOf(crush.toUpperCase().replace(" ", "_"));
                            materials.add(material);
                        }

                        blacklist.put(section.toUpperCase().replace("-", "_"), materials);
                    }
                }

                catch (final Exception e)
                {
                    print("An error occurred at node: " + node);
                    print("Skipping ....");
                }
            }
        }

        catch (final Exception e)
        {
            shutdownPlugin("An error occurred while initializing the configuration from the config.yml.  Shutting down ....");
        }
    }

    boolean autoReload = true;
    int reloadInterval = 5;

    @Override public final void onEnable()
    {
        try
        {
            final FileConfiguration config = getConfig();

            autoReload = config.getBoolean("startup-tweaks.auto-reload");

            if (autoReload)
            {
                reloadInterval = config.getInt("startup-tweaks.reload-interval") * 20;

                getServer().getScheduler().runTaskTimerAsynchronously
                (
                    plugin,

                    this::loadSettings,

                    10,
                    reloadInterval
                );
            }

            else
            {
                loadSettings();
            }

            getServer().getPluginManager().registerEvents(new EventListener(), plugin);
            getCommand("nostorex").setExecutor(new CommandListener());
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

    private String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    private void print(final String data)
    {
        System.out.println("(No Store X): " + data);
    }
}