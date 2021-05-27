// Author: Dashie
// Version: 1.0
package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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
        System.out.println("(Java Template): " + msg);
    }

    private UUID getUUID(final Player p)
    {
        return p.getUniqueId();
    }

    private void Error(Exception E)
    {
        SendLog("An error has occurred, making the plugin unusable.  I will have to disable myself to save your server unnecessary resources.  Bye! ;c");
        SendLog("If you want to help me solve this issue, perhaps send this:\r\n" + E.getMessage() + "\r\nto my email at KvinneKraft@protonmail.com.");

        inst.getPluginLoader().disablePlugin(inst);
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

            rewardBlockTypes = new ArrayList<>();
            notifyBlockTypes = new ArrayList<>();
            rewardChances = new ArrayList<>();
            notifyPlayers = new ArrayList<>();
            blockRewards = new ArrayList<>();

        }

        catch (Exception E)
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