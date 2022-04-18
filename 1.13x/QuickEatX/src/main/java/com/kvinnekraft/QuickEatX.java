// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class QuickEatX extends JavaPlugin
{
    private Integer getInteger(final String data)
    {
        try
        {
            return Integer.valueOf(data);
        }

        catch (final Exception e)
        {
            return -1;
        }
    }

    private PotionEffectType getPotionEffect(final String data)
    {
        try
        {
            return PotionEffectType.getByName(data);
        }

        catch (final Exception e)
        {
            return null;
        }
    }

    private Material getMaterial(final String data)
    {
        try
        {
            return Material.valueOf(data);
        }

        catch (final Exception e)
        {
            return null;
        }
    }

    final HashMap<Material, List<PotionEffect>> effects = new HashMap<>();
    final HashMap<Material, Integer> cooldowns = new HashMap<>();
    final HashMap<Material, Integer> bars = new HashMap<>();
    final HashMap<Material, String> nodes = new HashMap<>();

    final List<Material> consumables = new ArrayList<>();

    private void loadSettings()
    {
        saveDefaultConfig();
        reloadConfig();

        try
        {
            final FileConfiguration config = getConfig();

            for (int k = 1; ;k += 1)
            {
                String node = "plugin-properties.quick-consumables.";

                if (config.contains(node + k))
                {
                    try
                    {
                        node += k + ".";

                        final ItemStack item = new ItemStack(Material.STICK, 1);

                        try//item:
                        {
                            final String s_node = node + ".item.";
                            final String p_node = config.getString(s_node + "permission");

                            final Integer cooldown = getInteger(config.getString(s_node + "cooldown"));
                            final Integer bars = getInteger(config.getString(s_node + "food-bars"));

                            final Material material = getMaterial(config.getString(s_node + "type"));

                            if (material == null || cooldown < 0 || bars < 0 || p_node == null)
                            {
                                throw new Exception("!");
                            }

                            cooldowns.put(material, cooldown * 20);
                            nodes.put(material, p_node);
                            consumables.add(material);

                            this.bars.put(material, bars);

                            item.setType(material);
                        }

                        catch (final Exception e)
                        {
                            throw new Exception("!");
                        }

                        try//effects:
                        {
                            final List<PotionEffect> effects = new ArrayList<>();
                            final String s_node = node + "effects";

                            for (final String effect : config.getStringList(s_node))
                            {
                                try
                                {
                                    final String[] potionData = effect.split(" ");

                                    if (potionData.length < 3)
                                    {
                                        throw new Exception("!");
                                    }

                                    final PotionEffectType type = getPotionEffect(potionData[0]);

                                    final int amplifier = getInteger(potionData[1]);
                                    final int duration = getInteger(potionData[2]) * 20;

                                    if (type == null || amplifier < 1 || duration / 20 < 1)
                                    {
                                        throw new Exception("!");
                                    }

                                    effects.add(new PotionEffect(type, duration, amplifier));
                                }

                                catch (final Exception e)
                                {
                                    print("Invalid effect found at node: " + s_node);
                                    e.printStackTrace();
                                    shutdownPlugin("!");
                                }
                            }

                            this.effects.put(item.getType(), effects);
                        }

                        catch (final Exception e)
                        {
                            throw new Exception("!");
                        }
                    }

                    catch (final Exception e)
                    {
                        print("Invalid configuration detected at node: " + node + k);
                        print("Skipping section ....");
                    }

                    continue;
                }

                break;
            }
        }

        catch (final Exception e)
        {
            shutdownPlugin("Invalid configuration detected.");
        }
    }

    final JavaPlugin plugin = this;

    boolean autoReload = false;
    int reloadInterval = 200;

    @Override public final void onEnable()
    {
        try
        {
            final FileConfiguration config = getConfig();

            autoReload = config.getBoolean("startup-properties.auto-reload");

            if (autoReload)
            {
                reloadInterval = config.getInt("startup-properties.reload-interval") * 20;

                getServer().getScheduler().runTaskTimerAsynchronously
                (
                    plugin,

                    this::loadSettings,

                    reloadInterval,
                    reloadInterval
                );
            }

            loadSettings();

            getServer().getPluginManager().registerEvents(new EventListener(), plugin);
            getCommand("QuickEatX").setExecutor(new CommandListener());
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

    private class CommandListener implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player.");
                return false;
            }

            final Player p = (Player) s;

            p.sendMessage(color("&6-----------&e[&a&lQuickEatX&e]&6-----------"));
            p.sendMessage(color("&6>>> &eAuthor: &6Dashie"));
            p.sendMessage(color("&6>>> &eVersion: &61.0"));
            p.sendMessage(color("&6>>> &eGitHub: &6https://github.com/KvinneKraft"));

            return false;
        }
    }

    private class EventListener implements Listener
    {
        private HashMap<Material, Player> players = new HashMap<>();

        @EventHandler final void onInteract(final PlayerInteractEvent e)
        {
            final ItemStack i = e.getItem();

            if (i != null && consumables.contains(i.getType()))
            {
                final Material m = e.getMaterial();
                final Player p = e.getPlayer();

                if (!nodes.get(m).equalsIgnoreCase("none"))
                {
                    if (!p.hasPermission(nodes.get(m)))
                    {
                        return;
                    }
                }

                if (players.containsKey(m))
                {
                    if (players.get(m) == p)
                    {
                        return;
                    }
                }

                if (bars.get(m) > 0)
                {
                    p.setFoodLevel(p.getFoodLevel() + bars.get(m));
                }

                if (effects.get(m).size() > 0)
                {
                    p.addPotionEffects(effects.get(m));
                }

                e.getItem().setAmount(e.getItem().getAmount() - 1);

                if (cooldowns.get(m) > 0)
                {
                    getServer().getScheduler().runTaskLater
                    (
                        plugin,

                        () -> players.remove(m),

                        cooldowns.get(m)
                    );
                }

                players.put(m, p);
            }
        }
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
        System.out.println("(QuickEatX): " + data);
    }
}