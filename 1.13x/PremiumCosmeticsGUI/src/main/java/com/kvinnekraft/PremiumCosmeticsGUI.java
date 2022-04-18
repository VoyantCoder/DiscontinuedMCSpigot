
// author: dashie
// version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class PremiumCosmeticsGUI extends JavaPlugin
{
    private class EventListener implements Listener
    {
        @EventHandler final void onPlayerChat(final AsyncPlayerChatEvent e)
        {
            e.getPlayer().openInventory(CosmeticMenu);
        }
    }

    private Material getMaterial(final String material)
    {
        try
        {
            return Material.valueOf(material);
        }

        catch (final Exception e)
        {
            return null;
        }
    }

    private Inventory CosmeticMenu;

    private void loadSettings()
    {
        saveDefaultConfig();

        final FileConfiguration config = getConfig();

        try
        {
            final Material empty = getMaterial(config.getString("properties.empty"));

            if (empty == null)
            {
                throw new Exception("!");
            }

            final int slots = config.getInt("properties.slots");

            for (int k = 9; k <= 54; k += 9)
            {
                if (k == 54)
                {
                    if (slots != k)
                    {
                        throw new Exception("cosmetic-gui-slots: " + slots);
                    }
                }
            }

            final String title = color(config.getString("properties.title"));

            CosmeticMenu = getServer().createInventory(null, slots, title);

            for (int k = 1 ; ; k += 1)
            {
                String node = "properties.items.";

                if (config.contains(node + k))
                {
                    node = node + k + ".";

                    final int slot = config.getInt(node + "slot");

                    if (slot < 1)
                    {
                        throw new Exception("items." + k + ".invalid-slot: " + slot);
                    }

                    final Material material = getMaterial(config.getString(node + "material"));
                    final ItemStack item = new ItemStack(Material.AIR, 1);

                    if (material == null)
                    {
                        throw new Exception("items." + k + ".material");
                    }

                    item.setType(material);

                    final ItemMeta meta = item.getItemMeta();
                    final String name = color(config.getString(node + "display-name"));

                    meta.setDisplayName(name);

                    final List<String> lores = new ArrayList<>();

                    for (final String lore : config.getStringList(node + "display-lore"))
                    {
                        if (lore.length() < 1)
                        {
                            break;
                        }

                        lores.add(color(lore));
                    }

                    meta.setLore(lores);
                    item.setItemMeta(meta);

                    CosmeticMenu.setItem(slot-1, item);

                    continue;
                }

                break;
            }

            for (int k = 0; k < CosmeticMenu.getStorageContents().length; k += 1)
            {
                final ItemStack item = CosmeticMenu.getItem(k);

                if (item != null && !item.getType().equals(empty))
                {
                    item.setAmount(1);
                    item.setType(empty);

                    CosmeticMenu.setItem(k, item);
                }
            }
        }

        catch (final Exception e)
        {
            shutdownPlugin("Invalid configuration detected at: " + e.getMessage());
        }
    }

    final JavaPlugin plugin = this;

    @Override public final void onEnable()
    {
        try
        {
            loadSettings();

            getServer().getPluginManager().registerEvents(new EventListener(), plugin);
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