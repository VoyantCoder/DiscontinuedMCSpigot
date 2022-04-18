
// Author: Dashie
// Version: 1.0

package com.kvinne.wordscramble;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class WordScramble extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;

    final List<String> scrambled_words = new ArrayList<>();
    final List<String> scramble_list = new ArrayList<>();
    final List<String> solved_words = new ArrayList<>();

    Integer broadcast_interval, scramble_timeout;
    String broadcast_format, success_format, timeout_format, scramble_permission;

    boolean solve_cancel = false;

    protected class Start
    {
        final List<Character> chars = new ArrayList<>();

        final Random rand = new Random();

        protected void Scrambler()
        {
            String word = scramble_list.get(rand.nextInt(scramble_list.size()));

            chars.clear();

            for (final char c : word.toCharArray())
                chars.add(c);

            Collections.shuffle(chars);

            final StringBuilder builder = new StringBuilder();

            for (final char c : chars)
                builder.append(c);

            final String scrambled = builder.toString();

            scrambled_words.add(scrambled);
            solved_words.add(word);

            getServer().getScheduler().runTaskLaterAsynchronously
            (
                plugin,

                new Runnable()
                {
                    @Override public void run()
                    {
                        if (scrambled_words.contains(scrambled))
                        {
                            getServer().broadcastMessage(timeout_format.replace("%scrambled%", scrambled).replace("%solved%", word));

                            scrambled_words.remove(scrambled);
                            solved_words.remove(word);
                        };
                    };
                },

                scramble_timeout * 20
            );

            getServer().broadcastMessage(broadcast_format.replace("%scrambled%", scrambled));
        };
    };

    protected void startScrambler()
    {
        getServer().getScheduler().runTaskTimerAsynchronously
        (
            plugin,

            new Runnable()
            {
                final Start start = new Start();

                @Override public void run()
                {
                    if (getServer().getOnlinePlayers().size() > 0)
                    {
                        start.Scrambler();
                    };
                };
            },

            broadcast_interval * 20,
            broadcast_interval * 20
        );
    };

    protected class Events implements Listener
    {
        @EventHandler public void onPlayerChat(final AsyncPlayerChatEvent e)
        {
            final Player p = (Player) e.getPlayer();

            if (p.hasPermission(scramble_permission))
            {
                for (final String piece : e.getMessage().split(" "))
                {
                    if (solved_words.contains(piece))
                    {
                        final int index = solved_words.indexOf(piece);

                        getServer().broadcastMessage(success_format.replace("%scrambled%", scrambled_words.get(index)).replace("%successor%", p.getName()).replace("%solved%", piece));

                        scrambled_words.remove(index);
                        solved_words.remove(index);

                        if (solve_cancel)
                        {
                            e.setCancelled(true);
                            return;
                        };
                    };
                };
            };
        };
    };

    protected void stopTasks()
    {
        if (getServer().getScheduler().getActiveWorkers().size() > 0)
        {
            getServer().getScheduler().cancelTasks(plugin);
        };
    };

    protected void LoadConfiguration()
    {
        if (plugin != this)
        {
            plugin = (JavaPlugin) this;
        };

        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();

        stopTasks();

        try
        {
            scramble_permission = config.getString("word-scramble.permissions.plugin-control-permission");
            admin_permission = config.getString("word-scramble.permissions.scramble-permission");

            try
            {
                broadcast_interval = config.getInt("word-scramble.configuration.broadcast-interval");
                scramble_timeout = config.getInt("word-scramble.configuration.scramble-timeout");
            }

            catch (final Exception e)
            {
                print("Invalid integral values found in the configuration file. Using default ones!");

                broadcast_interval = 240;
                scramble_timeout = 240;
            };

            broadcast_format = color(config.getString("word-scramble.configuration.scramble-broadcast-format"));
            success_format = color(config.getString("word-scramble.configuration.scramble-success-format"));
            timeout_format = color(config.getString("word-scramble.configuration.scramble-timeout-format"));

            solve_cancel = config.getBoolean("word-scramble.configuration.scramble-stop-chat");

            scramble_list.clear();
            scramble_list.addAll(config.getStringList("word-scramble.configuration.scramble-list"));
        }

        catch (final Exception e)
        {
            print("Hey, I have found an unknown error. Please reach out to me about this at KvinneKraft@protonmail.com.");
        };

        startScrambler();
    };

    String admin_permission;

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

            if (p.hasPermission(admin_permission))
            {
                if (as.length >= 1)
                {
                    if (as[0].equalsIgnoreCase("reload"))
                    {
                        LoadConfiguration();
                        p.sendMessage(color("&aDone!"));

                        return true;
                    }

                    else if (as[0].equalsIgnoreCase("new"))
                    {
                        p.sendMessage(color("&aGenerating a scrambled word ...."));

                        stopTasks();

                        Start start = new Start();
                        start.Scrambler();

                        return true;
                    };
                };

                p.sendMessage(color("&cCorrect syntax: &7/wordscramble [reload | new]"));
                return false;
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
        getCommand("wordscramble").setExecutor(new Commands());

        print("\n---------------------------\n Author: Dashie \n Version: 1.0 \n Github: https://github.com/KvinneKraft \n Email: KvinneKraft@protonmail.com \n---------------------------");
        print("I am now out of my nest!");
    };

    @Override public void onDisable()
    {
        stopTasks();

        print("I am now dead!");
    };

    protected String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };

    protected void print(final String d)
    {
        System.out.println("(Word Scramble): " + d);
    };
};
