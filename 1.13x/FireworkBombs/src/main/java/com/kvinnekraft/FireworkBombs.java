
// Author: Dashie
// Version: 1.0

// UGLY CODE

package com.kvinnekraft;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class FireworkBombs extends JavaPlugin
{
    protected final JavaPlugin plugin = this;

    public final class Fireworks
    {
        public final ItemStack FireCracker = new ItemStack(Material.SNOWBALL, 1);

        public Fireworks()
        {
            final ItemMeta FireMeta = FireCracker.getItemMeta();

            FireMeta.setDisplayName(color("&6F&ei&6r&ee&6c&er&6a&ec&6k&ee&6r"));
            FireMeta.setLore(Arrays.asList("&eHeavily explosive!"));
            FireMeta.setCustomModelData(2020);

            FireCracker.setItemMeta(FireMeta);
        };
    };

    protected final class Events implements Listener
    {
        protected final void DetonateFirework(Location location, Color mcolor, Color fcolor, FireworkEffect.Type type)
        {
            try
            {
                Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                FireworkMeta firework_meta = firework.getFireworkMeta();

                firework_meta.addEffect(FireworkEffect.builder().withColor(mcolor).with(type).flicker(true).withFlicker().withTrail().withFade(fcolor).trail(true).build());

                firework.setFireworkMeta(firework_meta);
                firework.detonate();
            }

            catch (final Exception e)
            {
                print("There was an error launching a firework!  Please make sure that your server is running on a version greater than 1.12.2, thank you!");
            };
        };

        final Random rand = new Random();

        @EventHandler public final void ProjectileHit(final ProjectileHitEvent e)
        {
            if (e.getEntity() instanceof Snowball)
            {
                final Snowball snowball = (Snowball) e.getEntity();

                if (snowball.getCustomName() != null && !snowball.getCustomName().equalsIgnoreCase(color("dashsnowballs")))
                {
                    return;
                };

                final Location location = snowball.getLocation();

                location.getWorld().dropItemNaturally(location, new ItemStack(Material.SNOWBALL, 1));

                getServer().getScheduler().runTaskLater
                (
                    plugin,

                    () ->
                    {
                        //if (location.getNearbyEntities(5, 5, 5).contains(snowball))
                        //{
                            snowball.remove();

                            for ( int t = 0; t < 8; t += 1)
                            {
                                getServer().getScheduler().runTaskLater
                                (
                                    plugin,

                                    () ->
                                    {
                                        int min = -4, max = 4;

                                        double x = ThreadLocalRandom.current().nextInt(min, max + 1);
                                        double y = location.getY() + ThreadLocalRandom.current().nextInt(min, max + 1);
                                        double z = ThreadLocalRandom.current().nextInt(min, max + 1);

                                        if (rand.nextInt(2) == 1) x -= location.getX();
                                         else x += location.getX();
                                        if (rand.nextInt(2) == 1) z -= location.getZ();
                                         else z += location.getZ();

                                        DetonateFirework(new Location(location.getWorld(), x, y, z), Color.fromRGB(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)), Color.fromRGB(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)), FireworkEffect.Type.BURST);
                                    },

                                    1
                                );
                            };
                        //};
                    },

                    120
                );
            };
        };

        @EventHandler public final void ProjectileLaunch(final PlayerInteractEvent e)
        {
            if (e.getItem() != null && e.getItem().isSimilar(firework.FireCracker))
            {
                final Snowball snowball = e.getPlayer().launchProjectile(Snowball.class);

                snowball.setCustomName(color("dashsnowballs"));
                snowball.setCustomNameVisible(false);
                snowball.setFireTicks(60 * 20);
            };
        };
    };

    protected final Fireworks firework = new Fireworks();

    @Override public final void onEnable()
    {
        print("I am being enabled ....");

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("fireworkbombs").setExecutor(new Commands());

        print("Author: Dashie");
        print("Version: 1.0");
        print("Email: KvinneKraft@protonmail.com");
        print("Github: https://github.com/KvinneKraft");

        print("I have been enabled!");
    };

    protected final class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player!");
                return false;
            };

            final Player p = (Player) s;

            if (!p.isOp())
            {
                p.sendMessage(color("&cYou may not do this."));
                return false;
            };

            if (as.length > 0)
            {
                if (as[0].equalsIgnoreCase("get"))
                {
                    int amount = 1;

                    if (as.length > 1)
                    {
                        try
                        {
                            amount = Integer.parseInt(as[1]);
                        }

                        catch (final Exception e)
                        {
                            p.sendMessage(color("&cThe amount must be integral!"));
                            return false;
                        };
                    };

                    firework.FireCracker.setAmount(amount);

                    p.getInventory().addItem(firework.FireCracker);
                    p.sendMessage(color("&aYou got yourself some firework!"));

                    return false;
                };
            };

            p.sendMessage(color("&cInvalid syntax, correct syntax: &7/fireworkbombs get <amount>"));
            return false;
        };
    };

    @Override public final void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("I have been disabled.");
    };

    protected final String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };

    protected final void print(final String data)
    {
        System.out.println("(Firework Bombs): " + data);
    };
};