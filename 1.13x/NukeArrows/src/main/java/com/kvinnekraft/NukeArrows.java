
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
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class NukeArrows extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    protected void LoadConfiguration()
    {
        if (plugin != this)
        {
            plugin = (JavaPlugin) this;
        };

        saveDefaultConfig();

        plugin.reloadConfig();
        config = plugin.getConfig();

        try
        {
            explosion_damage = config.getBoolean("settings.explosion-damage");
            explosion_flames = config.getBoolean("settings.explosion-flames");

            explosion_radius = config.getInt("settings.explosion-radius");

            admin_permission = config.getString("settings.admin-permission");
            nuke_permission = config.getString("settings.use-permission");

            final ItemMeta meta = nuclear_bow.getItemMeta();

            meta.setLore(Arrays.asList(color(config.getString("settings.bow-lore"))));
            meta.setUnbreakable(config.getBoolean("settings.bow-unbreakable"));
            meta.setDisplayName(color(config.getString("settings.bow-name")));

            nuclear_bow.setItemMeta(meta);
        }

        catch (final Exception e)
        {
            print("An unknown configuration error has occurred, this may cause the plugin to malfunction, please contact me at KvinneKraft@protonmail.com if issues persist!");
        };
    };

    @Override public void onEnable()
    {
        print("Loading .....");

        LoadConfiguration();

        print("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\nAuthor: Dashie\nVersion: 1.0\nGithub: https://github.com/KvinneKraft\nEmail: KvinneKraft@protonmail.com\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("nukearrows").setExecutor(new Commands());

        print("Done!");
    };

    final ItemStack nuclear_bow = new ItemStack(Material.BOW, 1);

    boolean explosion_flames, explosion_damage;
    int explosion_radius;

    String nuke_permission, admin_permission;

    protected class Events implements Listener
    {
        @EventHandler public void onProjectileHit(final ProjectileHitEvent e)
        {
            if (e.getEntity() instanceof Arrow)
            {
                if (e.getEntity().getShooter() instanceof Player)
                {
                    final Player p = (Player) e.getEntity().getShooter();

                    if (p.hasPermission(nuke_permission))
                    {
                        if (p.getInventory().getItemInMainHand().equals(nuclear_bow))
                        {
                            final Entity entity = e.getEntity();
                            final Location location = entity.getLocation();

                            location.getWorld().createExplosion(location, explosion_radius, explosion_flames, explosion_damage);
                        };
                    };
                };
            };
        };
    };

    protected class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player!");
                return false;
            };

            final Player p = (Player) s;

            if (!p.hasPermission(admin_permission))
            {
                p.sendMessage(color("&cYou may not do this!"));
                return false;
            }

            else if (as.length > 0)
            {
                if (as[0].equalsIgnoreCase("reload"))
                {
                    LoadConfiguration();
                    p.sendMessage(color("&aDone!"));
                    return true;
                }

                else if (as[0].equalsIgnoreCase("give"))
                {
                    p.sendMessage(color("&aYou have given yourself a " + nuclear_bow.getItemMeta().getDisplayName() + " &a!"));
                    p.getInventory().addItem(nuclear_bow);
                    return true;
                };
            };

            p.sendMessage(color("&cDid you perhaps mean: &7/nukearrows reload &cor &7give &c?"));
            return false;
        };
    };

    @Override public void onDisable()
    {
        print("I have been disabled.");
    };

    void print(final String d)
    {
        System.out.println("(Nuke Arrows): " + d);
    };

    String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };
};
