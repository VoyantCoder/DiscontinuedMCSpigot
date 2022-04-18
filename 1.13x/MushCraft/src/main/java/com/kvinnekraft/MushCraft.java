
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MushCraft extends JavaPlugin
{
    protected JavaPlugin plugin;

    // Do you want to know why I made this plugin?  Then check this page: https://pugpawz.com/blog_article.php?id=mushies

    protected final class Mushy
    {
        public final ItemStack mush = new ItemStack(Material.BROWN_MUSHROOM, 1);

        public Mushy()
        {
            final ItemMeta mush_meta = mush.getItemMeta();

            mush_meta.setDisplayName(color("&b&k|||&r &d&lM&dagi&d&lc &d&lM&dushroo&d&lm &b&k|||&r"));
            mush_meta.setLore(Arrays.asList(color("&7&oEat meh.")));
            mush_meta.setCustomModelData(2020);

            mush.setItemMeta(mush_meta);

            effects.addAll
            (
                Arrays.asList
                (
                    new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 16, 2, true, true),
                    new PotionEffect(PotionEffectType.REGENERATION, 20 * 16, 2, true, true),
                    new PotionEffect(PotionEffectType.WEAKNESS, 20 * 16, 2, true, true),
                    new PotionEffect(PotionEffectType.CONFUSION, 20 * 16, 2, true, true)
                )
            );
        };

        private final List<PotionEffect> effects = new ArrayList<>();

        public final void ApplyEffects(final Player p)
        {
            new BukkitRunnable()
            {
                @Override public final void run()
                {
                    if (!p.hasPotionEffect(PotionEffectType.CONFUSION))
                    {
                        this.cancel();
                        return;
                    };

                    final Location location = p.getLocation();

                    p.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 28, 28);
                    p.playSound(location, Sound.BLOCK_PORTAL_TRAVEL, 28, 28);

                    final int radius = 1;

                    for (double y = 0; y <= 50; y += 0.05)
                    {
                        final double x = radius * Math.cos(y);
                        final double z = radius * Math.sin(y);

                        location.getWorld().spawnParticle(Particle.FLAME, (float) (location.getX() + x), (float) (location.getY() + y), (float) (location.getZ() + z), 1, 0, 3, 0, 1);
                        location.getWorld().spawnParticle(Particle.PORTAL, (float) (location.getX() + x), (float) (location.getY() + y), (float) (location.getZ() + z), 1, 0, 3, 0, 1);
                        location.getWorld().spawnParticle(Particle.TOTEM, (float) (location.getX() + x), (float) (location.getY() + y), (float) (location.getZ() + z), 1, 0, 3, 0, 1);
                    };
                }
            }.runTaskTimer(plugin, 1, (20 * 16));

            p.addPotionEffects(effects);
        };
    };

    protected final class Events implements Listener
    {
        @EventHandler protected final void onPlayerEat(final PlayerInteractEvent e)
        {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if (e.getItem() != null && e.getItem().isSimilar(mushy.mush))
                {
                    final Player p = e.getPlayer();

                    mushy.ApplyEffects(p);

                    final ItemStack item = e.getItem();

                    getServer().getScheduler().runTaskLater
                    (
                        plugin,

                        ()-> item.setAmount(item.getAmount() - 1),

                        1
                    );

                    p.sendMessage(color("&dYou ate meeeeee !!!!!"));
                    e.setCancelled(true);
                };
            };
        };
    };

    protected final Mushy mushy = new Mushy();

    @Override public final void onEnable()
    {
        print("I am enabling myself ....");

        if (plugin != this) plugin=this;

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("mushy").setExecutor(new Commands());

        print("I have enabled myself!");
    };

    protected final class Commands implements CommandExecutor
    {
        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player!");
                return false;
            };

            final Player p = (Player) s;

            if (p.isOp())
            {
                if (as.length > 0 && as[0].equalsIgnoreCase("give"))
                {
                    Integer q = 1;
                    Player r = p;

                    if (as.length > 2)
                    {
                        r = Bukkit.getPlayerExact(as[1]);

                        if (r == null)
                        {
                            p.sendMessage(color("&cThe receiver specified must be online!"));
                            return false;
                        };

                        try
                        {
                            q = Integer.parseInt(as[2]);
                        }

                        catch (final Exception e)
                        {
                            p.sendMessage(color("&cYou must specify an integral value for the quantity!"));
                            return false;
                        };
                    }

                    else
                    {
                        p.sendMessage(color("&cYou must specify a player and an integral quantity!"));
                        return false;
                    };

                    mushy.mush.setAmount(q);

                    if (p != r)
                    {
                        r.sendMessage(color("&aYou have been given some mushies, check your inventory!"));
                    };

                    p.sendMessage(color("&aConsider the job to be done!"));
                    p.getInventory().addItem(mushy.mush);
                    return true;
                };

                p.sendMessage(color("&cDid you perhaps mean &7/mushy give [player-name] [amount] &c?"));
                return false;
            };

            p.sendMessage(color("&cYou may not do this!"));
            return false;
        };
    };

    @Override public final void onDisable()
    {
        print("You put me to sleep.");
    };

    protected final String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };

    protected final void print(final String data)
    {
        System.out.println("(Mush Craft): " + data);
    };
};