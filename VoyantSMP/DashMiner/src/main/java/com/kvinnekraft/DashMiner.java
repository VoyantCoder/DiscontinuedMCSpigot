// Author: Dashie
// Version: 1.0
package com.kvinnekraft;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DashMiner extends JavaPlugin
{
    private String Colorize(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    private void SendLog(final String msg)
    {
        System.out.println("(Dash Miner): " + msg);
    }

    private UUID getUUID(final Player p)
    {
        return p.getUniqueId();
    }

    private void Error(Exception E)
    {
        SendLog("An error has occurred, making the plugin unusable.  I will have to disable myself to save your server unnecessary resources.  Bye! ;c");
        SendLog("If you want to help me solve this issue, perhaps send this:\r\n" + E.getMessage() + "\r\nto my email at KvinneKraft@protonmail.com.");

        inst.getPluginLoader().disablePlugin(inst, true);
    }


    private Material getMaterial(final String item)
    {
        try
        {
            return Material.valueOf(item);
        }

        catch (final Exception E)
        {
            SendLog(item);
            return null;
        }
    }

    private Integer getInteger(final String item)
    {
        try
        {
            return Integer.valueOf(item);
        }

        catch (final Exception E)
        {
            return -1;
        }
    }


    private List<List<ItemStack>> blockRewards = new ArrayList<>();
    private List<List<Integer>> rewardChances = new ArrayList<>();
    private List<Material> rewardBlockTypes = new ArrayList<>();

    private List<Material> notifyBlockTypes = new ArrayList<>();
    private List<UUID> notifyPlayers = new ArrayList<>();

    private FileConfiguration config = null;

    private void LoadConfiguration()
    {
        try
        {
            saveDefaultConfig();

            inst.reloadConfig();
            config = inst.getConfig();

            notifyBlockTypes = new ArrayList<>();

            for (final String item : config.getStringList("notify-blocks"))
            {
                final Material material = getMaterial(item);

                if (material == null)
                {
                    SendLog("Found invalid block-type [" + item + "] while reading notify-blocks.");
                    SendLog("Skipping ....");

                    continue;
                }

                notifyBlockTypes.add(material);
            }

            rewardBlockTypes = new ArrayList<>();
            rewardChances = new ArrayList<>();
            blockRewards = new ArrayList<>();

            for (int n = 1; ;n += 1)
            {
                String node = ("block-rewards.");

                if (!config.contains(node + n))
                {
                    break;
                }

                node = ("block-rewards." + n + ".block-type");

                Material bulb0 = getMaterial(config.getString(node));

                if (bulb0 == null)
                {
                    SendLog("Found invalid block-type while reading " + node + ".");
                    SendLog("Skipping ....");

                    continue;
                }

                node = ("block-rewards." + n + ".rewards");

                final List<Integer> chances = new ArrayList<>();
                final List<ItemStack> items = new ArrayList<>();

                for (final String item : config.getStringList(node))
                {
                    final String[] arr = item.split(" ");

                    if (arr.length != 3)
                    {
                        SendLog("An error occurred while reading one of the rewards at " + node + ".");
                        SendLog("Skipping ....");

                        continue;
                    }

                    Material bulb1 = getMaterial(arr[0]);

                    int bulb2 = getInteger(arr[1]);
                    int bulb3 = getInteger(arr[2]);

                    if (bulb1 == null || bulb2 < 1 || bulb3 < 1)
                    {
                        SendLog("An error occurred while reading one of the rewards at " + node + ".");
                        SendLog("Skipping ....");

                        continue;
                    }

                    items.add(new ItemStack(bulb1, bulb2));
                    chances.add(bulb3);
                }

                rewardBlockTypes.add(bulb0);
                rewardChances.add(chances);
                blockRewards.add(items);
            }
        }

        catch (final Exception E)
        {
            Error(E);
        }
    }


    class EventHandlers implements Listener
    {
        private void XRayListener(final BlockBreakEvent E)
        {
            final Material BlockType = E.getBlock().getType();
            final Player m = E.getPlayer();

            if (notifyBlockTypes.contains(BlockType))
            {
                getServer().getScheduler().runTaskAsynchronously
                (
                    inst,

                    () ->
                    {
                        for (final Player p : getServer().getOnlinePlayers())
                        {
                            if (notifyPlayers.contains(p.getUniqueId()))
                            {
                                p.sendMessage(Colorize("&a7" + m.getName() + " &abroke a &b" + BlockType.toString() + "&a!"));
                            }
                        }
                    }
               );
            }
        }

        final Random rand = new Random();

        private void RewardListener(final BlockBreakEvent E)
        {
            final Material BlockType = E.getBlock().getType();

            if (rewardBlockTypes.contains(BlockType))
            {
                final int id = rewardBlockTypes.indexOf(BlockType);

                getServer().getScheduler().runTaskAsynchronously
                (
                    inst,

                    () ->
                    {
                        final List<ItemStack> r = new ArrayList<>();

                        for (int s = 0; s < blockRewards.get(id).size(); s += 1)
                        {
                            if (rand.nextInt(100) <= rewardChances.get(id).get(s))
                            {
                                r.add(blockRewards.get(id).get(s));
                            }
                        }

                        if (r.size() > 0)
                        {
                            E.getBlock().getDrops().addAll(r);

                            final Location loca = E.getBlock().getLocation();
                            final World world = loca.getWorld();

                            getServer().getScheduler().runTask
                            (
                                inst,

                                () ->
                                {
                                    for (final ItemStack item : r)
                                    {
                                        world.dropItemNaturally(loca, item);
                                    }
                                }
                            );
                        }
                    }
                );
            }
        }


        @EventHandler
        final void PlayerBlockBreak(final BlockBreakEvent E)
        {
            if (E.getPlayer().getGameMode() != GameMode.CREATIVE)
            {
                RewardListener(E);
                XRayListener(E);
            }
        }
    }


    class CommandsHandler implements CommandExecutor
    {
        @Override
        public boolean onCommand(@NotNull CommandSender s, @NotNull Command c, @NotNull String l, @NotNull String[] a)
        {
            if (!(s instanceof Player))
            {
                SendLog("You cannot do this.  Players can though.");
                return false;
            }

            final Player p = (Player) s;

            if (!p.isOp())
            {
                p.sendMessage(Colorize("&cYou may not do this, unfortunately."));
                return false;
            }

            if (a.length < 1)
            {
                p.sendMessage(Colorize("&aYou must give me something to work with. Give help for help."));
                return false;
            }

            final String argument = a[0].toLowerCase();

            if (argument.equals("help"))
            {
                p.sendMessage(Colorize("&e/dashminer xray -=- toggle x-xray detection mode.  get notified about ore mining."));
                p.sendMessage(Colorize("&e/dashminer blockrewards [add | remove] [block type <reward item:amount:chance(1-100)>] -=- add or remove bonus drop rules."));
                return true;
            }

            else if (argument.equals("add") || argument.equals("remove"))
            {
                if (a.length < 2)
                {
                    p.sendMessage(Colorize("&aUsage: &e/dashminer blockrewards [add | remove] [block type <reward item:amount:chance(1-100)>]"));
                    return false;
                }

                else if (a.length < 2)
                {

                }
            }

            return true;
        }
    }


    final JavaPlugin inst = this;

    @Override
    public final void onEnable()
    {
        try
        {
            getServer().getScheduler().runTaskTimerAsynchronously(inst, this::LoadConfiguration, 100, 100);

            LoadConfiguration();

            // Add toggle command | check if player UUID already in list, if not, turn on else off

            getServer().getPluginManager().registerEvents(new EventHandlers(), inst);
            getCommand("DashMiner").setExecutor(new CommandsHandler(), inst);
        }

        catch (Exception E)
        {
            Error(E);
        }
    }

    @Override
    public final void onDisable()
    {
        try
        {
            SendLog("Bye!!");
        }

        catch (Exception E)
        {
            Error(E);
        }
    }
}