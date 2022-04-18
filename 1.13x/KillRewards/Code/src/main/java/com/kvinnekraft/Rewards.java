// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Rewards extends JavaPlugin
{
    public class EventsHandler implements Listener
    {
        final HashMap<Player, Integer> PlayerCache = new HashMap<>();

        @EventHandler public final void onPlayerDeath(final PlayerDeathEvent E)
        {
            final Player k = E.getEntity().getKiller();
            final Player P = E.getEntity();

            if (PlayerCache.containsKey((P)))
            {
                switch (PlayerCache.get(P))
                {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }

                PlayerCache.put(P, PlayerCache.get(P) + 1);
            }

            else
            {
                PlayerCache.put(P, 1);
            }

            if (PlayerCache.containsKey(k))
            {
                PlayerCache.remove(k);
            }
        }
    }

    @Override public final void onEnable()
    {
        // Register Here
    }

    @Override public final void onDisable()
    {
        // Unregister Here
    }
}