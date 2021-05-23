// Author: Dashie
// Version: 1.0
package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DashJoin extends JavaPlugin
{
    private String Colorize(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&',data);
    }

    private void SendLog(final String msg)
    {
        System.out.println("(DashJoin): "+msg);
    }

    private void Error(Exception E)
    {
        SendLog("An error has occurred, making the plugin unusable.  I will have to disable myself to save your server unnecessary resources.  Bye! ;c");
        SendLog("If you want to help me solve this issue, perhaps send this:\r\n"+E.getMessage()+"\r\nto my email at KvinneKraft@protonmail.com.");

        inst.getPluginLoader().disablePlugin(inst);
    }


    private FileConfiguration config=null;

    private String motdMessage = null;
    private String helpMessage = null;
    private String ruleMessage = null;

    private void LoadConfiguration()
    {
        try
        {
            saveDefaultConfig();

            inst.reloadConfig();
            config=inst.getConfig();

            motdMessage = ("");
            helpMessage = ("");
            ruleMessage = ("");

            for (final String a : config.getStringList("help-message"))
                helpMessage += (Colorize(a) + "\n");

            for (final String a : config.getStringList("motd-message"))
                motdMessage += (Colorize(a) + "\n");

            for (final String a : config.getStringList("rule-message"))
                ruleMessage += (Colorize(a) + "\n");
        }

        catch(Exception E)
        {
            Error(E);
        }
    }


    private class EventHandlers implements Listener
    {
        @EventHandler
        public final void onPlayerJoin(final PlayerJoinEvent E)
        {
            final Player p = E.getPlayer();
            p.sendMessage(motdMessage);
        }

        @EventHandler
        public final void onPlayerCommand(final PlayerCommandPreprocessEvent E)
        {
            final String cmd = E.getMessage().toLowerCase().replace("/", "");
            final Player p = E.getPlayer();

            if (cmd.equals("help"))
            {
                p.sendMessage(helpMessage);
                E.setCancelled(true);
            }

            else if (cmd.equals("rules") || cmd.equals("rule"))
            {
                p.sendMessage(ruleMessage);
                E.setCancelled(true);
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