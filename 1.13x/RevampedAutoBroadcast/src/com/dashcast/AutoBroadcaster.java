

// Author: Dashie
// Version: 3.0


package com.dashcast;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender; 
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;


public class AutoBroadcaster extends JavaPlugin implements CommandExecutor
{
    public static String color(String str) { return ChatColor.translateAlternateColorCodes('&', str); };
    public static void print(String str) { System.out.println("(Auto Broadcaster): " + str); };
    
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    @Override public void onEnable()
    {
        print("Enabling plugin ....");
        
        saveDefaultConfig();
        
        config = (FileConfiguration)getConfig();
        plugin = (JavaPlugin)this;
        
        configuration_data();
        
        getCommand("dashcaster").setExecutor(plugin);
        getCommand("bc").setExecutor(plugin);
        
        start_cast();
        
        print("Author: Dashie");
        print("Version: 3.0");
        print("Email: KvinneKraft@protonmail.com");
        print("Github: https://github.com/KvinneKraft");
        
        print("Plugin has been enabled!");
    };
    
    private List<String> messages = new ArrayList<String>();
    
    private boolean randomly, sounds;
    private int interval;
    
    private String permission;
    private Sound sound;
    
    private void configuration_data()
    {
        if( messages.size() > 0 ) messages.clear();
        
        for( String message : config.getStringList("properties.messages") )
        {
            messages.add(color(message));
        };
        
        if ( messages.size() < 1 )
        {
            print("No messages to broadcast? Check the config.yml!");
        };
        
        randomly = config.getBoolean("properties.choose-randomly");
        sounds = config.getBoolean("properties.sounds");
        
        if ( sounds )
        {
            sound = Sound.valueOf(config.getString("properties.sound-effect-id"));
            
            if( sound == null )
            {
                print("You can not use some invalid sound type! Make sure the type applies to your server version.");
                print("Disabling sound effects ....");
                
                sounds = false;
            };
        };
        
        interval = config.getInt("properties.broadcast-interval") * 20;
        
        if ( interval <= 20 )
        {
            print("The config tells me that you want me to broadcast messages with an interval of less than a second? Waw!");
        };
        
        permission = config.getString("properties.admin-command-permission");
        
        if ( permission.length() < 1 )
        {
            print("I do not know if you had done this intentionally, but there is no permission set for the admin command.");
        };
    };
    
    private void start_cast()
    {
        BukkitScheduler runnable = getServer().getScheduler();
        
        runnable.runTaskTimerAsynchronously(plugin, 
            new Runnable()
            {
                private int message_id = 0;
                private String message;
                
                @Override
                public void run()
                {
                    if (getServer().getOnlinePlayers().size() < 1)
                    {
                        return;
                    };
                    
                    message = messages.get(message_id);
                    
                    if(randomly)
                    {
                        message = messages.get(new Random().nextInt(messages.size()));
                    }
                    
                    else
                    {
                        if ( message_id >= messages.size() )
                        {
                            message_id = 0;
                        }
                        
                        else
                        {
                            message_id += 1;  
                        };
                    };
                    
                    if ( sounds )
                    {
                        for ( Player player : getServer().getOnlinePlayers() )
                        {
                            player.playSound(player.getLocation(), sound, 30, 30);
                        };
                    };
                    
                    runnable.runTask(plugin, 
                        new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                getServer().broadcastMessage(message);
                            }   
                        }
                    );
                }
            },
            
            interval, interval
        );
    };
    
    @Override public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player) && c.toString().toLowerCase().contains("bc"))
        {
            String message = "";
            
            for (final String line : as)
            {
                message += line + " ";
            };
            
            getServer().broadcastMessage(color(message));
            return false;
        };
        
        if(!(s instanceof Player)) return false;
        
        Player p = (Player) s;
        
        if(!p.hasPermission(permission))
        {
            p.sendMessage(color("&cYe are neh supposed to use this, are ye?"));
            return false;
        }
        
        else if(as.length < 1)
        {
            p.sendMessage(color("&eInvalid arguments provided, apply &7reload &eor something."));
        }
        
        else if(as[0].toLowerCase().equals("reload"))
        {
            p.sendMessage(color("&6>>> &a&oreloading configuration data ...."));
            
            configuration_data();
            
            p.sendMessage(color("&6>>> &a&oconfiguration data has been reloaded successfully!"));
        }
        
        else
        {
            p.sendMessage(color("&cCorrect usage: &7/dashcast reload"));
        };
        
        return true;
    };
    
    @Override public void onDisable() { };
};
