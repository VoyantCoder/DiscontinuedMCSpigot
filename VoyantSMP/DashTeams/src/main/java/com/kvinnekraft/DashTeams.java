package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DashTeams extends JavaPlugin
{
    final class EventsHandler implements Listener
    {
        @EventHandler
        public void onPlayerDamage(final EntityDamageByEntityEvent E)
        {
            // Make sure team-mates can not hit each other.
        }
    }


    // Player : Is In Faction
    final HashMap<UUID, Boolean> FactionPlayers = new HashMap<>();
    // Faction Name
    final List<String> FactionNames = new ArrayList<>();

    FileConfiguration config;

    final void ReloadConfiguration()
    {
        try
        {
            saveDefaultConfig();

            parent.reloadConfig();
            config = parent.getConfig();

            // Load configuration data, hopefully some factions.
        }

        catch (final Exception E)
        {
            throw (E);
        }
    }


    final JavaPlugin parent = this;

    @Override
    public final void onEnable()
    {
        try
        {

        }

        catch (final Exception E)
        {
            Print("An error occurred.  Shutting down plugin ....");
            getServer().getPluginManager().disablePlugin(parent);
        }
    }

    @Override
    public final void onDisable()
    {

    }


    final String Colorize(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    final void Print(final String data)
    {
        System.out.println("(Dash Teams): " + data);
    }
}