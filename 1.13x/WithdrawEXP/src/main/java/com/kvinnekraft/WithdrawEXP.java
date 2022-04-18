
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class WithdrawEXP extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    String redeem_permission;

    protected class Events implements Listener
    {
        protected boolean isTicket(final ItemStack substance)
        {
            if (substance != null && substance.getType().equals(ticket_material))
            {
                if (substance.hasItemMeta())
                {
                    if (substance.getItemMeta().hasLore())
                    {
                        if (substance.getItemMeta().hasCustomModelData())
                        {
                            if (substance.getItemMeta().getLore().get(0).equals(ticket_lore_format.replace("%experience%", String.valueOf(substance.getItemMeta().getCustomModelData())))) /*Could check for experience amounts by storing experience levels in HashMaps*/
                            {
                                return true;
                            };
                        };
                    };
                };
            };

            return false;
        };

        final List<Player> player_queue = new ArrayList<>();

        @EventHandler protected void onPlayerInteract(final PlayerInteractEvent e)
        {
            final Player p = e.getPlayer();

            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                if (p.hasPermission(redeem_permission))
                {
                    if (isTicket(e.getItem()))
                    {
                        if (player_queue.contains(p))
                        {
                            p.sendMessage(color("&cYou must wait at least " + redeem_cooldown + " seconds before doing this again!"));
                            return;
                        };

                        final int levels = e.getItem().getItemMeta().getCustomModelData();

                        p.sendMessage(color("&aYou have successfully redeemed &e" + levels + " &alevels of EXP!"));
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 32, 26);

                        /*for some odd reason your server crashes without this*/
                        getServer().getScheduler().runTaskLater
                         (
                            plugin,

                            new Runnable()
                            {
                                @Override public void run()
                                {
                                    e.getItem().setAmount(e.getItem().getAmount() - 1);
                                };
                            },

                            2
                        );

                        p.setLevel(p.getLevel() + levels);

                        if (!p.hasPermission(bypass_permission))
                        {
                            player_queue.add(p);

                            getServer().getScheduler().runTaskLaterAsynchronously
                            (
                                plugin,

                                new Runnable()
                                {
                                    @Override public void run()
                                    {
                                        if (player_queue.contains(p))
                                        {
                                            if (p.isOnline())
                                            {
                                                p.sendMessage(color("&aYou may now redeem EXP again!"));
                                            };

                                            player_queue.remove(p);
                                        };
                                    };
                                },

                                redeem_cooldown * 20
                            );
                        };
                    };
                };
            };
        };
    };

    protected void LoadConfiguration()
    {
        saveDefaultConfig();

        if (plugin != this)
        {
            plugin = this;
        }

        plugin.reloadConfig();
        config = plugin.getConfig();

        try
        {
            redeem_permission = config.getString("withdrawexp-permissions.redeem-permission");
            admin_permission = config.getString("withdrawexp-permissions.admin-permission");
            withdraw_permission = config.getString("withdrawexp-permissions.withdraw-permission");
            bypass_permission = config.getString("withdrawexp-permissions.bypass-cooldown-permission");

            try
            {
                ticket_material = Material.valueOf(config.getString("withdrawexp-functionality.ticket-item-material").toUpperCase().replace(" ", "_"));
            }

            catch (final Exception e)
            {
                print("An invalid material was specified for the ticket-item-material in the configuration file.");
                print("Using the default value PAPER now....");

                ticket_material = Material.PAPER;
            };

            redeem_cooldown = config.getInt("withdrawexp-functionality.ticket-redeem-cooldown");
            minimum_levels = config.getInt("withdrawexp-functionality.minimum-redeem-levels");
            maximum_levels = config.getInt("withdrawexp-functionality.maximum-redeem-levels");

            ticket_name_format = color(config.getString("withdrawexp-functionality.ticket-name-format"));
            ticket_lore_format = color(config.getString("withdrawexp-functionality.ticket-lore-format"));
        }

        catch (final Exception e)
        {
            print("There was an error while initializing the plugin.");
            print("Please contact me at KvinneKraft@protonmail.com about this!");
        };
    };

    String admin_permission, withdraw_permission, ticket_name_format, ticket_lore_format, bypass_permission;

    Integer minimum_levels, maximum_levels, redeem_cooldown;
    Material ticket_material;

    protected void WithdrawEXP(final Player p, final Integer a)
    {
        final ItemStack substance = new ItemStack(ticket_material, 1);
        final ItemMeta metainfo = substance.getItemMeta();

        if (metainfo == null)
        {
            p.sendMessage(color("&cThere was an error, please try again."));
            return;
        };

        metainfo.setLore(Arrays.asList(ticket_lore_format.replace("%experience%", a.toString())));
        metainfo.setDisplayName(ticket_name_format.replace("%experience%", a.toString()));
        metainfo.setCustomModelData(a);

        substance.setItemMeta(metainfo);

        p.setLevel(p.getLevel() - a);
        p.getInventory().addItem(substance);

        p.sendMessage(color("&aYou have successfully withdrawn &e" + a + " &alevels of EXP!"));
        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 28, 28);
    }

    protected class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("Only players may do this!");
                return false;
            };

            final Player p = (Player) s;

            if (p.hasPermission(admin_permission) || p.hasPermission(withdraw_permission))
            {
                if (as.length >= 1)
                {
                    if (as[0].equalsIgnoreCase("reload"))
                    {
                        if (p.hasPermission(admin_permission))
                        {
                            LoadConfiguration();
                            p.sendMessage(color("&aDone!"));
                            return true;
                        }

                        p.sendMessage(color("&cYou lack sufficient permissions."));
                        return false;
                    }
                }

                Integer e = p.getLevel();

                if (e < minimum_levels)
                {
                    p.sendMessage(color("&cYou own insufficient EXP levels!"));
                    return false;
                }

                else if (as.length >= 1)
                {
                    try
                    {
                        e = Integer.parseInt(as[0]);

                        if (e > p.getLevel())
                        {
                            p.sendMessage(color("&cYou own insufficient EXP levels!"));
                            return false;
                        }

                        else if (e > maximum_levels)
                        {
                            p.sendMessage(color("&cYou may not withdraw more than &4" + maximum_levels + " &cof &4EXP&c!"));
                            return false;
                        }

                        else if (e < minimum_levels)
                        {
                            p.sendMessage(color("&cYou may not withdraw less than &4" + minimum_levels + " &cof &4EXP&c!"));
                            return false;
                        };
                    }

                    catch (final Exception r)
                    {
                        p.sendMessage(color("&cYou must give me an integral value!"));
                        return false;
                    };
                }

                else
                {
                    if (e > maximum_levels)
                    {
                        e = maximum_levels;
                    }
                };

                WithdrawEXP(p, e);
                return true;
            };

            p.sendMessage(color("&cYou lack sufficient permissions."));
            return false;
        };
    };

    @Override public void onEnable()
    {
        print("I am crawling out of my nest ....");

        LoadConfiguration();

        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("withdrawexp").setExecutor(new Commands());

        print("\n---------------------------\n Author: Dashie \n Version: 1.0 \n Github: https://github.com/KvinneKraft \n Email: KvinneKraft@protonmail.com \n---------------------------");
        print("I am now out of my nest!");
    };

    @Override public void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("I am now dead!");
    };

    protected String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };

    protected void print(final String d)
    {
        System.out.println("(Withdraw EXP): " + d);
    };
};
