// Author: Dashie
// Version: 1.0
package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashSigns extends JavaPlugin
{
    private void SendLog(final String msg)
    {
        System.out.println("(Java Template): " + msg);
    }

    private void Error(Exception E)
    {
        SendLog("An error has occurred, making the plugin unusable.  I will have to disable myself to save your server unnecessary resources.  Bye! ;c");
        SendLog("If you want to help me solve this issue, perhaps send this:\r\n" + E.getMessage() + "\r\nto my email at KvinneKraft@protonmail.com.");
        inst.getPluginLoader().disablePlugin(inst, true);
    }


    private String Colorize(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    }
    private String[] MultiSplit(final String what, final String target, final String specialCharacter)
    {
        return what.replace(target, specialCharacter).split(specialCharacter);
    }

    private int getInt(final String data)
    {
        try
        {
            return (Integer.parseInt(data));
        }

        catch (final Exception E)
        {
            return -1;
        }
    }


    private final List<BlockData> blockData = new ArrayList<>();

    private List<String> GetCommands(final String entryData)
    {
        // Format commands as list
    }

    private Location GetLocation(final String entryData)
    {
        final String[] coord = entryData.split(" ");
        // I am aware of the bug.
        return new Location(null, getInt(coord[0]),
                getInt(coord[1]), getInt(coord[2]));
    }

    private String GetName(final String entryData)
    {
        return entryData.toUpperCase();
    }

    private FileConfiguration config = this.getConfig();

    private class BlockData
    {
        public List<String> blockCommands;
        public Location location;
        public String blockName;
        public int Index;
    }

    private void SetBlockData(final List<String> blockCommands, final Location location, final String blockName)
    {
        final BlockData blockData = new BlockData();

        blockData.blockCommands = blockCommands;
        blockData.blockName = blockName;
        blockData.location = location;

        this.blockData.add(blockData);
    }

    private void LoadConfiguration()
    {
        try
        {
            saveDefaultConfig();

            inst.reloadConfig();
            blockData.clear();

            final List<String> entries = config.getStringList("entries");

            for (final String entry : entries)
            {
                final String[] entryData = MultiSplit(entry, "--", "`");

                if (entryData.length != 3)
                {
                    SendLog("Found an invalid entry in config. Entry: [" + entry + "]");
                    continue;
                }

                final List<String> blockCommands = GetCommands(entryData[1]);
                final Location location = GetLocation(entryData[2]);
                final String blockName = GetName(entryData[0]);

                if (blockName.length() < 3 || location == null || blockCommands.size() < 1)
                {
                    SendLog("Found an invalid entry in config. Entry: [" + entry + "]");
                    continue;
                }

                SetBlockData(blockCommands, location, blockName);
            }
        }

        catch (Exception E)
        {
            Error(E);
        }
    }


    class EventHandlers implements Listener
    {
        final void FormatCommand(final String commands, final BlockData entry)
        {
            entry.blockCommands = Arrays.asList(MultiSplit(commands, ";;", "~"));
            // command usage: dashsigns create <commands separated by a ';;'> <sign_type>
        }

        final Location getBlockLocation(final Block block)
        {
            final Location location = block.getLocation();

            location.setX(location.getBlockX());
            location.setY(location.getBlockY());
            location.setZ(location.getBlockZ());

            return location;
        }


        @EventHandler
        public void onBreaksBlock(final BlockBreakEvent E)
        {

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