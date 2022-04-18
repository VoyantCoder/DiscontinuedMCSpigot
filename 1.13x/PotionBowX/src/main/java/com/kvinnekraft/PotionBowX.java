// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class PotionBowX extends JavaPlugin
{
    private class CommandListener implements CommandExecutor
    {
        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only type this as a player.");
                return false;
            }

            final Player p = (Player) s;

            if (p.isOp())
            {
                if (as.length >= 1)
                {
                    if (as[0].equalsIgnoreCase("give"))
                    {
                        Player r = p;

                        if (as.length > 1)
                        {
                            r = getServer().getPlayerExact(as[1]);

                            if (r == null)
                            {
                                p.sendMessage(color("&cYou must specify an online player!"));
                                return false;
                            }
                        }

                        r.getInventory().addItem(PotionBow);

                        if (r.equals(p))
                        {
                            p.sendMessage(color("&6>>> &aYou have given yourself a Potion Bow!"));
                        }

                        else
                        {
                            p.sendMessage(color("&6>>> &aYou have given &e" + r.getName() + " &aa Potion Bow!"));
                            r.sendMessage(color("&6>>> &aYou have been given a Potion Bow!"));
                        }

                        return true;
                    }
                }

                p.sendMessage(color("&cSyntax: &e/PotionBowX give <player>"));
                return true;
            }

            p.sendMessage(color("&6>>> &aAuthor: Dashie"));
            p.sendMessage(color("&6>>> &aVersion: 1.0"));
            p.sendMessage(color("&6>>> &aGitHub: https://github.com/KvinneKraft"));
            p.sendMessage(color("&6>>> &aEmail: KvinneKraft@protonmail.com"));

            return true;
        }
    }

    private class EventListener implements Listener
    {
        @EventHandler final void onProjectileLaunch(final ProjectileLaunchEvent e)
        {
            if (ArrowPots.size() > 0)
            {
                if ((!(e.getEntity() instanceof Arrow)) || (!(e.getEntity().getShooter() instanceof Player)))
                    return;

                final Player p = (Player) e.getEntity().getShooter();

                if (!p.getInventory().getItemInMainHand().isSimilar(PotionBow))
                {
                    if (!p.getInventory().getItemInOffHand().isSimilar(PotionBow))
                    {
                        return;
                    }
                }

                final Arrow arrow = (Arrow) e.getEntity();

                arrow.clearCustomEffects();
                arrow.addCustomEffect(ArrowPots.get(new Random().nextInt(ArrowPots.size())), true);
                arrow.setColor(Color.PURPLE);
            }
        }
    }

    private final List<PotionEffect> ArrowPots = new ArrayList<>();
    private final ItemStack PotionBow = new ItemStack(Material.BOW, 1);

    private void loadSettings()
    {
        saveDefaultConfig();

        ArrowPots.clear();

        plugin.reloadConfig();

        try
        {
            for (String node : new String[] { "arrow-effects" })
            {
                node = "core-tweaks.potion-effect." + node;

                for (final String effect : getConfig().getStringList(node))
                {
                    try
                    {
                        final List<String> disassembled = Arrays.asList(effect.split(" "));

                        if (disassembled.size() < 1)
                        {
                            throw new Exception("!");
                        }

                        final PotionEffectType type = PotionEffectType.getByName(disassembled.get(0));

                        if (type == null)
                        {
                            throw new Exception("!");
                        }

                        final int multiplier = Integer.parseInt(disassembled.get(1));
                        final int duration = Integer.parseInt(disassembled.get(2)) * 20;

                        if (multiplier < 1 || duration < 1)
                        {
                            throw new Exception("!");
                        }

                        final PotionEffect potionEffect = new PotionEffect(type, duration, multiplier);

                        ArrowPots.add(potionEffect);
                    }

                    catch (final Exception e)
                    {
                        print("Invalid configuration detected at " + node + " -> " + effect + " !");
                    }
                }

                final ItemMeta meta = PotionBow.getItemMeta();

                meta.setDisplayName(color("&3&k&l:::&r &aPotion Bow &3&k&l:::&r"));
                meta.setLore(Arrays.asList(color("&7&oShoot it man, what the heck.")));

                PotionBow.setItemMeta(meta);
            }
        }

        catch (final Exception e)
        {
            shutdownPlugin("Invalid configuration detected.");
        }
    }

    final JavaPlugin plugin = this;

    boolean hasAutoReload = false;
    int reloadInterval = 5;

    @Override
    public final void onEnable()
    {
        try
        {
            final FileConfiguration config = getConfig();

            hasAutoReload = config.getBoolean("startup-tweaks.auto-reload");

            if (hasAutoReload)
            {
                saveDefaultConfig();

                try
                {
                    reloadInterval = config.getInt("startup-tweaks.reload-interval") * 20;

                    getServer().getScheduler().runTaskTimerAsynchronously
                    (
                        plugin,

                        this::loadSettings,

                        reloadInterval,
                        reloadInterval
                    );
                }

                catch (final Exception e)
                {
                    shutdownPlugin("The asynchronous reload failed.  Shutting down ....");
                }

                getServer().getPluginManager().registerEvents(new EventListener(), plugin);
                getCommand("potionbowx").setExecutor(new CommandListener());
            }

            else
            {
                loadSettings();
            }
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

    @Override
    public final void onDisable()
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
        System.out.println("(Potion Bow X): " + data);
    }
}