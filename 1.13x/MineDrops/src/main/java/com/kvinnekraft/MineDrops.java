
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MineDrops extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    protected final void LoadConfiguration()
    {
        saveDefaultConfig();

        if (plugin != this) plugin = this;

        plugin.reloadConfig();
        config = plugin.getConfig();

        lucky_blocks.clear();
        lucky_drops.clear();

        try
        {
            for (final String rule : config.getStringList("drop-rules"))
            {
                final List<String> data = Arrays.asList(rule.split(" "));

                try
                {
                    if (data.size() < 2)
                    {
                        throw new Exception("!");
                    };

                    lucky_blocks.add(Material.valueOf(data.get(0)));

                    final List<ItemStack> drops = new ArrayList<>();

                    for (int i = 1; i < data.size(); i += 1)
                    {
                        print(data.get(i));

                        if (data.get(i).contains(":"))
                        {
                            final String block_data[] = data.get(i).split(":");

                            final Material drop_material = Material.valueOf(block_data[0]);
                            final int drop_amount = Integer.parseInt(block_data[1]);

                            drops.add(new ItemStack(drop_material, drop_amount));
                        };
                    };

                    lucky_drops.add(drops);
                }

                catch (final Exception e)
                {
                    print("Invalid config rule found in the \'drop-rules\' section in your config.yml. Try to fix this issue and retry.");
                };
            };
        }

        catch (final Exception e)
        {
            print("An unknown configuration error has occurred, please contact me at KvinneKraft@protonmail.com if any issues persist.");
        };
    };

    @Override public final void onEnable()
    {
        print("This plugin is loading .....");

        LoadConfiguration();

        print("Author: Dashie");
        print("Version: 1.0");
        print("Email: KvinneKraft@protonmail.com");
        print("Github: https://github.com/KvinneKraft");

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("minedrops").setExecutor(new Commands());

        print("This plugin has been enabled.");
    };

    protected final List<List<ItemStack>> lucky_drops = new ArrayList<>();
    protected final List<Material> lucky_blocks = new ArrayList<>();

    protected final class Events implements Listener
    {
        protected final String permission = "default";

        @EventHandler public final void onPlayerBreak(final BlockBreakEvent e)
        {
            final Player p = e.getPlayer();

            if (p.hasPermission(permission))
            {
                if (lucky_blocks.contains(e.getBlock().getType()))
                {
                    final int i = lucky_blocks.indexOf(e.getBlock().getType());
                    final Location l = e.getBlock().getLocation();

                    for (final ItemStack item : lucky_drops.get(i))
                    {
                        l.getWorld().dropItemNaturally(l, item);
                    };
                };
            };
        };
    };

    protected final class Commands implements CommandExecutor
    {
        final String admin = "admin";

        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if(!(s instanceof Player))
            {
                print("You may only execute this as a player.");
                return false;
            };

            final Player p = (Player) s;

            if (p.hasPermission(admin))
            {
                if (as.length > 0)
                {
                    if (as[0].equalsIgnoreCase("reload"))
                    {
                        LoadConfiguration();
                        p.sendMessage(color("&aDone!"));
                        return true;
                    };
                };

                p.sendMessage(color("&cDid you perhaps mean: &7/minedrops reload"));
                return false;
            };

            p.sendMessage(color("&cYou may not do this."));
            return false;
        };
    };

    @Override public final void onDisable()
    {
        print("This plugin has been disabled.");
    };

    protected final String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };

    protected final void print(final String data)
    {
        System.out.println("(Mine Drops): " + data);
    };
};