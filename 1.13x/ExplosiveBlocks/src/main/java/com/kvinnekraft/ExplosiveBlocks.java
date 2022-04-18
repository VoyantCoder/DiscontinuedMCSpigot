
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ExplosiveBlocks extends JavaPlugin
{
    private final List<Material> charged_blocks = new ArrayList<>();

    private final List<Boolean> charge_break = new ArrayList<>();
    private final List<Boolean> charge_flame = new ArrayList<>();

    private final List<Integer> charge_sizes = new ArrayList<>();
    private final List<Integer> charge_delay = new ArrayList<>();

    private final class Events implements Listener
    {
        @EventHandler protected final void onBlockBreak(final BlockBreakEvent e)
        {
            final Material block = e.getBlock().getType();

            if (charged_blocks.contains(block))
            {
                final Location location = e.getBlock().getLocation();
                final int id = charged_blocks.indexOf(block);

                getServer().getScheduler().runTaskLater
                (
                    base,

                    () ->
                    {
                        final Block check = location.getWorld().getBlockAt(location);

                        if (check.getType().equals(block))
                        {
                            return;
                        };

                        final boolean demolish = charge_break.get(id);
                        final boolean flame = charge_flame.get(id);
                        final int radius = charge_sizes.get(id);

                        location.getWorld().createExplosion(location, radius, flame, demolish);
                    },

                    charge_delay.get(id) * 20
                );
            };
        };
    };

    private final JavaPlugin base = this;

    @Override public final void onEnable()
    {
        print("I am being enabled ....");

        doReload();

        getServer().getScheduler().runTaskTimerAsynchronously(base, this::doReload, 100, 9999999999L);
        getServer().getPluginManager().registerEvents(new Events(), base);

        print("I have been enabled!");
    };

    private void doReload()
    {
        saveDefaultConfig();
        base.reloadConfig();

        FileConfiguration config = base.getConfig();

        try
        {
            charge_break.clear();
            charge_flame.clear();
            charge_delay.clear();
            charge_sizes.clear();

            charged_blocks.clear();

            for (int k = 1; ;k += 1)
            {
                if (config.contains("charged-blocks." + k))
                {
                    final String node = "charged-blocks." + k + ".";

                    try
                    {
                        charge_flame.add(config.getBoolean(node + "explosion-flames"));
                        charge_break.add(config.getBoolean(node + "explode-terrain"));

                        charge_sizes.add(config.getInt(node + "explosion-radius"));
                        charge_delay.add(config.getInt(node + "explosion-delay"));

                        final Material material = Material.valueOf(config.getString(node + "type").toUpperCase().replace(" ", "_"));

                        charged_blocks.add(material);
                    }

                    catch (final Exception e)
                    {
                        print("An invalid entry was found at charged-blocks." + k + "!");
                        print("You can find your configuration file (config.yml) at /plugins/ExplosiveBlocks/config.yml.");
                    };

                    continue;
                };

                break;
            }
        }

        catch (final Exception e)
        {
            print("An error has occurred while trying to setup the configuration of this plugin. If this issue persists, please contact me, the Developer, at KvinneKraft@protonmail.com, thank you!");
        };
    };

    @Override public final void onDisable()
    {
        print("Cancelling pending tasks (if any) ....");

        getServer().getScheduler().cancelTasks(base);

        print("I have been disabled.");
    };

    private String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };

    private void print(final String d)
    {
        System.out.println("[Explosive Blocks]: " + d);
    };
};