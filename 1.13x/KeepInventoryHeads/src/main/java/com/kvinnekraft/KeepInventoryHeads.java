
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class KeepInventoryHeads extends JavaPlugin
{
    private class EventListener implements Listener
    {
        @EventHandler public void onPlayerDeath(final PlayerDeathEvent e)
        {
            final Player victim = e.getEntity();

            if (victim.getKiller() != null)
            {
                final Player killer = victim.getKiller();

                if (!killer.getInventory().getItemInMainHand().getType().equals(Material.BOW))
                {
                    if (!killer.getInventory().getItemInOffHand().getType().equals(Material.BOW))
                    {
                        if (new Random().nextInt(100) <= Chance)
                        {
                            final ItemStack Skull = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);

                            SkullMeta SkullMeta = (SkullMeta) Skull.getItemMeta();

                            SkullMeta.setOwningPlayer(victim);
                            Skull.setItemMeta(SkullMeta);

                            victim.getWorld().dropItem(victim.getLocation(), Skull);
                        }

                        e.setShouldDropExperience(false);
                        e.setKeepInventory(true);
                        e.setKeepLevel(true);

                        e.setDroppedExp(0);
                    }
                }
            }
        }
    }

    int Chance = 5;

    private void reload()
    {
        saveDefaultConfig();

        Chance = getConfig().getInt("drop-chance");
    }

    final JavaPlugin plugin = this;

    @Override public final void onEnable()
    {
        try
        {
            reload();

            getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::reload,  120, 120);
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
        System.out.println("(KeepInventoryHeads): " + data);
    }
}