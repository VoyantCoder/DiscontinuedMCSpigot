// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.xml.stream.events.Namespace;
import java.util.ArrayList;
import java.util.List;

public final class PotionObtainablesX extends JavaPlugin
{
    private PotionEffectType getPotionEffectType(final String data)
    {
        return (PotionEffectType.getByName(data));
    }

    private Enchantment getEnchantment(final String data)
    {
        return (Enchantment.getByKey(NamespacedKey.minecraft(data)));
    }

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

    final List<List<PotionEffect>> effects = new ArrayList<>();
    final List<List<String>> commands = new ArrayList<>();

    final List<ItemStack> items = new ArrayList<>();

    final List<String> permissions = new ArrayList<>();
    final List<String> messages = new ArrayList<>();
    final List<String> names = new ArrayList<>();

    private void loadSettings()
    {
        saveDefaultConfig();
        reloadConfig();

        try
        {
            final FileConfiguration config = getConfig();

            effects.clear();
            items.clear();
            commands.clear();
            permissions.clear();
            messages.clear();
            names.clear();

            for (int k = 1; ;k += 1)
            {
                String node = "core-tweaks.potion-items.";

                if (config.contains(node + k))
                {
                    node = node + k;

                    try
                    {
                        try
                        {
                            final ItemStack item = new ItemStack(Material.STICK, 1);
                            ItemMeta meta = item.getItemMeta();

                            try
                            {
                                item.setType(Material.valueOf(config.getString(node + ".item.type").toUpperCase().replace(" ", "_")));

                                meta = item.getItemMeta();

                                final String name = color(config.getString(node + ".item.name"));
                                names.add(ChatColor.stripColor(name.toLowerCase()));

                                meta.setDisplayName(name);

                                final List<String> lore = new ArrayList<>();

                                for (final String line : config.getStringList(node + ".item.lore"))
                                {
                                    lore.add(color(line));
                                }

                                meta.setLore(lore);
                            }

                            catch (final Exception e)
                            {
                                throw new Exception("type || name || lore");
                            }

                            item.setItemMeta(meta);

                            try
                            {//add in another try-catch to allow the correct enchantments to be added regardless.
                                for (final String line : config.getStringList(node + ".item.enchantments"))
                                {
                                    final String[] enchantmentData = line.split(" ");

                                    if (enchantmentData.length < 2)
                                    {
                                        throw new Exception("!");
                                    }

                                    final Enchantment enchantment = getEnchantment(enchantmentData[0].toLowerCase());
                                    final Integer level = getInteger(enchantmentData[1]);

                                    if (level < 1 || enchantment == null)
                                    {
                                        throw new Exception("!");
                                    }

                                    item.addUnsafeEnchantment(enchantment, level);
                                }
                            }

                            catch (final Exception e)
                            {
                                throw new Exception("enchantments");
                            }

                            items.add(item);
                        }

                        catch (final Exception e)
                        {
                            throw new Exception(node + ".item." + e.getMessage());
                        }

                        try
                        {
                            permissions.add(config.getString(node + ".consumption.permission"));
                            messages.add(color(config.getString(node + ".consumption.message")));

                            try
                            {
                                final List<PotionEffect> effects = new ArrayList<>();

                                for (final String line : config.getStringList(node + ".consumption.effects"))
                                {
                                    final String[] effectData = line.split(" ");

                                    if (effectData.length < 3)
                                    {
                                        throw new Exception("!");
                                    }

                                    final PotionEffectType effect = getPotionEffectType(effectData[0]);
                                    final Integer amplifier = getInteger(effectData[1]);
                                    final Integer duration = getInteger(effectData[2]);

                                    if (effect == null || amplifier < 1 || duration < 1)
                                    {
                                        throw new Exception("!");
                                    }

                                    effects.add(new PotionEffect(effect, duration * 20, amplifier));
                                }

                                this.effects.add(effects);
                            }

                            catch (final Exception e)
                            {
                                throw new Exception("effects");
                            }

                            commands.add(config.getStringList(node + ".consumption.commands"));
                        }

                        catch (final Exception e)
                        {
                            throw new Exception(node + ".consumption." + e.getMessage());
                        }
                    }

                    catch (final Exception e)
                    {
                        print("Invalid configuration found at: " + e.getMessage() + " Skipping....");
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

    boolean autoReload = true;
    int reloadInterval = 5;

    @Override public final void onEnable()
    {
        try
        {
            final FileConfiguration config = getConfig();

            autoReload = config.getBoolean("startup-tweaks.auto-reload");

            if (autoReload)
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

            loadSettings();

            getServer().getPluginManager().registerEvents(new EventListener(), plugin);
            getCommand("potionobtainablesx").setExecutor(new CommandListener());
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

    private class EventListener implements Listener
    {
        @EventHandler final void onItemInteract(final PlayerInteractEvent e)
        {
            final Player p = e.getPlayer();

            if (e.getItem() != null && items.contains(e.getItem()))
            {
                final int index = items.indexOf(e.getItem());

                if (!permissions.get(index).equalsIgnoreCase("none"))
                {
                    if (!p.hasPermission(permissions.get(index)))
                    {
                        return;
                    }
                }

                if (!messages.get(index).equalsIgnoreCase("none"))
                {
                    p.sendMessage(messages.get(index));
                }

                if (effects.get(index).size() > 0)
                {
                    p.addPotionEffects(effects.get(index));
                }

                if (commands.get(index).size() > 0)
                {
                    getServer().getScheduler().runTaskAsynchronously
                    (
                        plugin,

                        () ->
                        {
                            for (String command : commands.get(index))
                            {
                                if (command.length() > 1)
                                {
                                    command = command.replace("%p%", p.getName());

                                    if (command.charAt(0) == '/')
                                    {
                                        final String c1 = command.substring(1);

                                        getServer().getScheduler().runTask(plugin, () -> p.performCommand(c1));
                                    }

                                    else if (command.charAt(0) == '~')
                                    {
                                        final String c1 = command.substring(1);

                                        getServer().getScheduler().runTask
                                        (
                                            plugin, () -> getServer().dispatchCommand(getServer().getConsoleSender(), c1)
                                        );
                                    }
                                }
                            }
                        }
                    );
                }
            }
        }
    }

    private class CommandListener implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You must be a player when using this command!");
                return false;
            }

            final Player p = (Player) s;

            if (p.isOp())
            {
                if (as.length >= 2)
                {
                    if (as[0].equalsIgnoreCase("get"))
                    {
                        final String name = as[1].toLowerCase();

                        if (!names.contains(name))
                        {
                            p.sendMessage(color("&7That name was not recognized.  Loading valid name(s) ...."));

                            getServer().getScheduler().runTaskAsynchronously
                            (
                                plugin,

                                () ->
                                {
                                    String collection = "&7names: ";

                                    for (final String neme : names)
                                    {
                                        collection += "&a" + neme + " ";
                                    }

                                    if (collection.equalsIgnoreCase("&7names: "))
                                    {
                                        collection = color("&cNo names were found.");
                                    }

                                    else
                                    {
                                        collection = color(collection);
                                    }

                                    p.sendMessage(collection);
                                }
                            );

                            return false;
                        }

                        p.getInventory().addItem(items.get(names.indexOf(name)));
                        p.sendMessage(color("&aYou have given yourself a &e" + name + "&a!"));

                        return true;
                    }
                }

                p.sendMessage(color("&cOh, that is not correct.  Did you perhaps mean &7/potionox get [NAME] &c?"));
                return false;
            }

            p.sendMessage(color("&6>>> &aAuthor: Dashie"));
            p.sendMessage(color("&6>>> &aVersion: 1.0"));
            p.sendMessage(color("&6>>> &aGithub: https://github.com/KvinneKraft"));

            return false;
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
        System.out.println("(Potion Obtainables X): " + data);
    }
}