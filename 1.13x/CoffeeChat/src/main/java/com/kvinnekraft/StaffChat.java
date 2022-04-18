
// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class StaffChat extends JavaPlugin
{
    final String color(final String data) { return ChatColor.translateAlternateColorCodes('&', data); }
    final void print(final String data) { System.out.println("(Staff Chat): " + data); }

    private final JavaPlugin plugin = this;

    final class Registrator implements Listener
    {
        @EventHandler public final void onPlayerCommand(final PlayerCommandPreprocessEvent e)
        {
            // Command Work
        }

        final class Prop
        {
            public final String STAFF_CHAT_PERMISSION = "staffchat";
        }

        private final Prop Config = new Prop();

        @EventHandler public final void onPlayerMessage(final AsyncPlayerChatEvent e)
        {
            final Player p = e.getPlayer();

            if (p.hasPermission(Config.STAFF_CHAT_PERMISSION))
            {
                final String message = e.getMessage();

                if (message.length() > 0 && message.charAt(0) == '#')
                {
                    new BukkitRunnable()
                    {
                        @Override public final void run()
                        {
                            for (final Player s_p : getServer().getOnlinePlayers())
                            {
                                if (s_p.hasPermission(Config.STAFF_CHAT_PERMISSION))
                                {
                                    String m = message.replace("# ", "").replace("#", "");

                                    m = color("&b(&3[&eStaff Chat&3] &e" + p.getName() + "&b): &7" + m);

                                    s_p.sendMessage(m);
                                }
                            }
                        }
                    }.runTaskAsynchronously(plugin);

                    e.setCancelled(true);
                }
            }
        }
    }

    @Override public final void onEnable()
    {
        print("I am being enabled ....");

        try
        {
            getServer().getPluginManager().registerEvents(new Registrator(), this);
        }

        catch (final Exception e)
        {
            print("An error occurred.  Reach out to me at KvinneKraft@protonmail.com if you want me to fix it.  Error code:\r\n\r\n" + e.getMessage());
            getPluginLoader().disablePlugin(this);
            return;
        }

        print("I have been enabled!");
    }

    @Override public final void onDisable()
    {
        print("I have been disabled!");
    }
}