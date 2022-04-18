
// ==== Pure experimentation.

// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TransactionGUI extends JavaPlugin
{
    protected FileConfiguration config;

    protected final Inventory navigator = Bukkit.createInventory(null,  27, color("&a&lDash Navigator"));

    final List<ItemStack> u_item = Arrays.asList
    (
        new ItemStack(Material.ENCHANTING_TABLE, 1),
        new ItemStack(Material.BOOK, 1),
        new ItemStack(Material.DARK_OAK_SIGN, 1)
    );

    protected final ItemStack empty_slot = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);

    protected final void LoadConfiguration()
    {
        try
        {
            try
            {
                if (inventories.size() < 3)
                {
                    final String[] names = { "&d&lDash Items", "&b&lDeveloper", "&c&lCommands" };

                    final List<String[]> lores = Arrays.asList
                    (
                        new String[]
                        {
                            color("&dBuy custom items and what not.")
                        },

                        new String[]
                        {
                            color("&bThis plugin was made by KvinneKraft."),
                            color("&bhttps://github.com/KvinneKraft")
                        },

                        new String[]
                        {
                            color("&eIn-game purchasable commands?"),
                        }
                    );

                    for (int k = 0; k < u_item.size(); k += 1)
                    {
                        final ItemMeta u_meta = u_item.get(k).getItemMeta();

                        u_meta.setDisplayName(color(names[k]));
                        u_meta.setLore(Arrays.asList(lores.get(k)));

                        u_item.get(k).setItemMeta(u_meta);
                    };

                    final ItemMeta empty_meta = empty_slot.getItemMeta();

                    empty_meta.setDisplayName(" ");
                    empty_meta.setLore(Arrays.asList(" "));

                    empty_slot.setItemMeta(empty_meta);

                    final List<Integer> u_key = Arrays.asList(11, 14, 17);

                    for (int i = 1; i <= 27; i += 1)
                    {
                        if (!u_key.contains(i))
                        {
                            navigator.setItem(i - 1, empty_slot);
                            continue;
                        };

                        navigator.addItem(u_item.get(u_key.indexOf(i)));
                    };

                    inventories.add(navigator);
                };
            }

            catch (final Exception e)
            {
                print("An error has occurred while attempting to setup the Navigation Menu!");
            };
        }

        catch (final Exception e)
        {
            print("An error has occurred and has caused this plugin to malfunction.  Please contact me at KvinneKraft@protonmail.com about this issue if you want it to be fixed!");
        }

        finally
        {
            print("Author: Dashie");
            print("Version: 1.0");
            print("Github: https://github.com/KvinneKraft");
            print("Email: KvinneKraft@protonmail.com");
        };
    };

    @Override public final void onEnable()
    {
        print("Enabling plugin .....");

        LoadConfiguration();
        getServer().getPluginManager().registerEvents(new Events(), this);

        print("Done!");
    };

    protected final List<Inventory> inventories = new ArrayList<>();

    protected final class Events implements Listener
    {
        @EventHandler protected final void onPlayerBlockPlace(final BlockPlaceEvent e)
        {
            e.getPlayer().openInventory(navigator);
        };

        @EventHandler protected final void onPlayerInventoryInteract(final InventoryInteractEvent e)
        {
            if (e.getInventory().getContents().length > 0 && e.getInventory().getContents()[0].equals(empty_slot))
            {
                e.setCancelled(true);
            };
        };
    };

    @Override public final void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        print("I have been disabled ;c");
    };

    protected final String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };

    protected final void print(final String data)
    {
        System.out.println("(Transaction GUI): " + data);
    };
};