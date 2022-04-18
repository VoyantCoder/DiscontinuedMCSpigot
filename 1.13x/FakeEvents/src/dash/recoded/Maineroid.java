// Author: Dashie
// Version: 1.0

package dash.recoded;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Maineroid extends JavaPlugin implements CommandExecutor
{
    void LoadConfiguration()
    {
        saveDefaultConfig();

        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();

        messages.add(color(config.getString("messages.ban")));
        messages.add(color(config.getString("messages.ipban")));
        messages.add(color(config.getString("messages.tempban")));
        messages.add(color(config.getString("messages.kick")));
        messages.add(color(config.getString("messages.mute")));
        messages.add(color(config.getString("messages.op")));
        messages.add(color(config.getString("messages.deop")));
        messages.add(color(config.getString("messages.vote")));
        messages.add(color(config.getString("messages.donation")));

        command_permission = config.getString("permissions.normal");
        admin_permission = config.getString("permissions.admin");
    };

    /*final*/FileConfiguration config = (FileConfiguration) null;
    /*final*/JavaPlugin plugin = (JavaPlugin) this;

    @Override public void onEnable()
    {
        print("I am getting up ....");

        LoadConfiguration();

        getCommand("f").setExecutor(this);

        print("I am now alive!");
    };

    final List<String> messages = new ArrayList<>();
    final List<String> commands = Arrays.asList
    (
        new String[]
        {
            "ban", "ipban", "tempban", "kick", "mute", "op", "deop", "vote", "donation"
        }
    );

    String admin_permission, command_permission;

    @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
    {
        if (!(s instanceof Player))
        {
            print("I am unable to do this as a console, I must be executed as a humanoid player!");
            return false;
        };

        final Player p = (Player) s;

        if (as.length >= 1)
        {
            final String arg = as[0].toLowerCase();

            if (p.hasPermission(command_permission))
            {
                if (arg.equals("modules"))
                {
                    p.sendMessage(color("&aAvailable Modules: &7&oban [player] [reason], ipban [player] [reason], tempban [player] [reason] [time], kick [player] [reason], mute [player] [reason], op [player], deop [player], vote [player] &7and &7&odonation [player]&7!"));
                }

                else if (commands.contains(arg))
                {
                    if (as.length < 2)
                    {
                        p.sendMessage(color("&cHey, you must specify a player."));
                        return true;
                    };

                    String message = messages.get(commands.indexOf(arg)).replace("%player%", as[1]);

                    if (arg.equals("mute") || arg.equals("tempban"))
                    {
                        if (as.length < 4)
                        {
                            p.sendMessage(color("&cYou forgot to add a reason and or time."));
                            return true;
                        };
                        
                        String resun = "";
                        
                        for (int id = 2; id < as.length; id += 1)
                        {
                            resun += as[id] + " ";                        
                        };
                        
                        message = message.replace("%duration%", as[3]).replace("%reason%", resun);
                    }

                    else if (arg.equals("ban") || arg.equals("ipban") || arg.equals("kick"))
                    {
                        if (as.length < 3)
                        {
                            p.sendMessage(color("&cYou forgot to add a reason."));
                            return true;
                        };
                        
                        String resun = "";
                        
                        for (int id = 2; id < as.length; id += 1)
                        {
                            resun += as[id] + " ";                        
                        };
                        
                        message = message.replace("%reason%", resun);
                    };

                    getServer().broadcastMessage(color(message));
                    
                    return true;
                }

                else if (!arg.equals("reload"))
                {
                    p.sendMessage(color("&cPerhaps check the valid modules using &4/f modules &c?"));
                    
                    return true;
                };
            };            
            
            if (p.hasPermission(admin_permission))
            {
                if (arg.equals("reload"))
                {
                    p.sendMessage(color("&eI am working on it ...."));

                    LoadConfiguration();

                    p.sendMessage(color("&eGuess what, I already finished!"));
                }

                else
                {
                    p.sendMessage(color("&cDid you mean to type &4/f reload &c?"));
                };

                return true;
            };
        };

        p.sendMessage(color("&cTry adding an argument or are you not supposed to do this?"));
        
        return true;
    };

    @Override public void onDisable()
    {
        print("I am no longer alive!");
    };

    String color(final String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };

    void print(final String line)
    {
        System.out.println("(Fake Events): " + line);
    };
};