// Author: Dashie
// Version: 1.0
package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DashCommandOverride extends JavaPlugin
{
    private String Colorize(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&',data);
    }
    private void SendLog(final String msg)
    {
        System.out.println("(DashCommandOverride): "+msg);
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

    private List<String> blockedCommands = new ArrayList<>();

    private String blockMessage = "";
    private String cooldMessage = "";

    private int cooldown1 = 100;
    private int cooldown2 = 100;

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

            blockedCommands = config.getStringList("blocked-commands");

            cooldown1 = config.getInt("command-cooldown") * 20;
            cooldown2 = config.getInt("message-cooldown") * 20;

            cooldMessage = Colorize(config.getString("cooldown-message"));
            blockMessage = Colorize(config.getString("block-message"));
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
            E.getPlayer().sendMessage(motdMessage);
        }


        final List<UUID> messageQueue = new ArrayList<>();

        private void addToMessageQueue(final Player p)
        {
            if (messageQueue.contains(p.getUniqueId()) || p.isOp())
            {
                return;
            }

            getServer().getScheduler().runTaskLaterAsynchronously
            (
                inst,

                () ->
                {
                    messageQueue.remove(p.getUniqueId());
                },

                cooldown2
            );

            messageQueue.add(p.getUniqueId());
        }

        @EventHandler
        public final void onPlayerMessage(final AsyncPlayerChatEvent E)
        {
            final Player p = E.getPlayer();

            if (messageQueue.contains(p.getUniqueId()))
            {
                p.sendMessage(cooldMessage);
                addToMessageQueue(p);
                E.setCancelled(true);
            }
        }


        final List<UUID> commandQueue = new ArrayList<>();

        private void addToCommandQueue(final Player p)
        {
            if (commandQueue.contains(p.getUniqueId()) || p.isOp())
            {
                return;
            }

            getServer().getScheduler().runTaskLaterAsynchronously
            (
                inst,

                () ->
                {
                    commandQueue.remove(p.getUniqueId());
                },

                cooldown1
            );

            commandQueue.add(p.getUniqueId());
        }

        @EventHandler
        public final void onPlayerCommand(final PlayerCommandPreprocessEvent E)
        {
            final String cmd = E.getMessage().toLowerCase().replace("/", "");
            final Player p = E.getPlayer();

            if (commandQueue.contains(p.getUniqueId()))
            {
                p.sendMessage(cooldMessage);
                addToCommandQueue(p);
                E.setCancelled(true);
            }

            else if (cmd.equals("help") || cmd.equals("ehelp"))
            {
                p.sendMessage(helpMessage);
                addToCommandQueue(p);
                E.setCancelled(true);
            }

            else if (cmd.equals("rules") || cmd.equals("rule") || cmd.equals("erules"))
            {
                p.sendMessage(ruleMessage);
                addToCommandQueue(p);
                E.setCancelled(true);
            }

            else if (blockedCommands.contains(cmd) && !p.isOp())
            {
                p.sendMessage(blockMessage);
                addToCommandQueue(p);
                E.setCancelled(true);
            }

            addToCommandQueue(p);
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