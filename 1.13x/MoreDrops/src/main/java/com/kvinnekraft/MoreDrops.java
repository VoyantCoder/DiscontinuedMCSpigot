
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class MoreDrops extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    @Override public void onEnable()
    {
        print("I am getting up, hold on a second ....");

        LoadConfiguration();

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("moredrops").setExecutor(new Commands());

        print("You have enabled me!");
    };

    protected final List<List<PotionEffect>> potion_effects = new  ArrayList<>();
    protected final List<List<ItemStack>> mob_drops = new ArrayList<>();
    protected final List<List<Integer>> chances = new ArrayList<>();
    protected final List<List<Sound>> sound_effects = new ArrayList<>();
    protected final List<String> mob_types = new ArrayList<>();

    protected final void LoadConfiguration()
    {
        saveDefaultConfig();

        if (plugin != this)
        {
            plugin = this;
        };

        plugin.reloadConfig();
        config = plugin.getConfig();

        print("Author: Dashie");
        print("Version: 1.0");
        print("Email: KvinneKraft@protonmail.com");
        print("Github: https://github.com/KvinneKraft");

        try
        {
            potion_effects.clear();
            sound_effects.clear();
            mob_drops.clear();
            mob_types.clear();
            chances.clear();

            for (int k = 1 ; ; k += 1)
            {
                if (config.contains("mob-drops." + k))
                {
                    String node = "mob-drops." + k + ".";

                    try
                    {
                        final String mob_type = config.getString(node + "mob-type").toUpperCase().replace(" ", "_");
                        final EntityType entity = EntityType.valueOf(mob_type);

                        mob_types.add(entity.toString());
                    }

                    catch (final Exception e)
                    {
                        print("An invalid mob type was found at " + node + "mob-type !");
                        continue;
                    };

                    final List<Sound> sounds = new ArrayList<>();

                    for (final String data : config.getStringList(node + "sound-effects"))
                    {
                        try
                        {
                            Sound sound = Sound.valueOf(data);
                            sounds.add(sound);
                        }

                        catch (final Exception e)
                        {
                            print("An invalid sound effect was found at " + node + "sound-effects -> " + data + " !");
                        };
                    };

                    sound_effects.add(sounds);

                    final List<PotionEffect> potions = new ArrayList<>();

                    for (final String data : config.getStringList(node + "potion-effects"))
                    {
                        try
                        {
                            final List<String> components = Arrays.asList(data.toUpperCase().split(" "));
                            final PotionEffectType type = PotionEffectType.getByName(components.get(0));

                            final int intensity = Integer.parseInt(components.get(2));
                            final int span = Integer.parseInt(components.get(1)) * 20;

                            final PotionEffect effect = new PotionEffect(type, span, intensity);

                            potions.add(effect);
                        }

                        catch (final Exception e)
                        {
                            print("An invalid potion effect was found at " + node + "potion-effects -> " + data + " !");
                        };
                    };

                    potion_effects.add(potions);

                    final List<ItemStack> items = new ArrayList<>();
                    final List<Integer> probabs = new ArrayList<>();

                    for (int u = 1; ; u += 1)
                    {
                        if (config.contains(node + "item-drops." + u))
                        {
                            try
                            {
                                final String s_node = node + "item-drops." + u + ".";

                                final ItemStack stack = new ItemStack(Material.valueOf(config.getString(s_node + "type").toUpperCase()), config.getInt(s_node + "amount"));

                                final String name = color(config.getString(s_node + "name"));
                                final String lore = color(config.getString(s_node + "lore"));

                                if ((name.length() & lore.length()) > 0)
                                {
                                    final ItemMeta meta = stack.getItemMeta();

                                    if (meta != null)
                                    {
                                        meta.setDisplayName(name);
                                        meta.setLore(Arrays.asList(lore));

                                        stack.setItemMeta(meta);
                                    };
                                };

                                items.add(stack);

                                int chance = config.getInt(s_node + "chance");

                                if (chance > 100) chance = 100;
                                else if (chance < 0) chance = 1;

                                probabs.add(chance);
                                continue;
                            }

                            catch (final Exception e)
                            {
                                print("An invalid item drop was found at item-drops -> " + u + " !");
                            };
                        };

                        break;/*Update Node if Update Future*/
                    };

                    mob_drops.add(items);
                    chances.add(probabs);

                    continue;
                };

                break;
            }
        }

        catch (final Exception e)
        {
            print("An unknown error has occurred, perhaps send the following to me at KvinneKraft@protonmail.com if you want to get this issue fixed!\n" + e.getMessage() + "\n");
        };
    };

    protected final class Events implements Listener
    {
        @EventHandler protected final void onEntityDeath(final EntityDeathEvent e)
        {
            if (e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player)
            {
                final Player p = e.getEntity().getKiller();

                if (p.hasPermission(use))
                {
                    final Entity entity = e.getEntity();

                    if (mob_types.contains(entity.getType().toString()))
                    {
                        try
                        {
                            final int i = mob_types.indexOf(entity.getType().toString());

                            if (potion_effects.get(i).size() > 0)
                            {
                                final ItemStack potion = new ItemStack(Material.LINGERING_POTION, 1);
                                final PotionMeta p_meta = (PotionMeta) potion.getItemMeta();

                                for (final PotionEffect effect : potion_effects.get(i))
                                {
                                    p_meta.addCustomEffect(effect, true);
                                };

                                potion.setItemMeta(p_meta);

                                ThrownPotion thrown = e.getEntity().launchProjectile(ThrownPotion.class);
                                entity.getLocation().setY(-1);

                                thrown.setVelocity(entity.getLocation().getDirection().multiply(0));
                                thrown.setItem(potion);
                            };

                            if (mob_drops.get(i).size() > 0)
                            {
                                final Location location = entity.getLocation();

                                for (final ItemStack item : mob_drops.get(i))
                                {
                                    if (chances.get(i).size() > 0)
                                    {
                                        final int s_i = mob_drops.get(i).indexOf(item);
                                        final int c = chances.get(i).get(s_i);

                                        if (new Random().nextInt(100) > c)
                                        {
                                            continue;
                                        };
                                    };

                                    location.getWorld().dropItemNaturally(location, item);
                                };
                            };

                            if (sound_effects.get(i).size() > 0)
                            {
                                final Location location = entity.getLocation();

                                for (final Sound sound : sound_effects.get(i))
                                {
                                    location.getWorld().playSound(location, sound, 28, 28);
                                };
                            };
                        }

                        catch (final Exception se)
                        {
                            print("A null pointer exception has occurred. Send the following report to KvinneKraft@protonmail.com:\n" + se.getMessage() + "\n");
                        };
                    };
                };
            };
        };
    };

    protected final String admin = "admin";
    protected final String use = "default";

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

            if (p.hasPermission(admin))
            {
                if (as.length > 0 && as[0].equalsIgnoreCase("reload"))
                {
                    p.sendMessage(color("&aConsider the job to be done!"));
                    LoadConfiguration();
                    return true;
                };

                p.sendMessage(color("&cDid you perhaps mean &7/moredrops reload &c?"));
                return false;
            };

            p.sendMessage(color("&cYou are not allowed to do this."));
            return false;
        };
    };

    @Override public void onDisable()
    {
        if (getServer().getScheduler().getActiveWorkers().size() > 0)
        {
            getServer().getScheduler().cancelTasks(plugin);
        };

        print("Aw, you have disabled me!");
    };

    protected final String color(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    };

    protected final void print(final String data)
    {
        System.out.println("(More Drops): " + data);
    };
};

