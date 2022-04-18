
// Coded whilst Drunk!

package com.dashcaster;

import com.dashrays.Luna;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitWorker;

public class AutoBroadcaster extends JavaPlugin
{    
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    public static List<String> dashcast_messages = new ArrayList<String>();    
    public static Integer dashcast_interval;          
    
    public void DoConfigReload()
    {
        if(config == null)
        {
            plugin = (JavaPlugin)this;
        };
               
        plugin.reloadConfig();
        plugin.getConfig();
       
        config = this.getConfig();        
        
        dashcast_messages = config.getStringList("messages");
        
        for(String message : dashcast_messages)
        {
            getServer().broadcastMessage(message);
            
            String message_str = Luna.transStr(message);
            int message_id = dashcast_messages.indexOf(message);
            
            dashcast_messages.set(message_id, message_str);
        };
        
        dashcast_interval = config.getInt("interval") * 20;
        
        dashcaster_run = false;        
    };
    
    private void CancelDashcasterTasks()
    {
        for(BukkitWorker bukkit_worker : getServer().getScheduler().getActiveWorkers())
        {
            int task_id = bukkit_worker.getTaskId();
            getServer().getScheduler().cancelTask(task_id);
        };        
    };
    
    boolean dashcaster_run = true;
    
    private void StartDashcaster()
    {
        getServer().getScheduler().runTaskTimerAsynchronously(plugin, 
            new Runnable()
            {      
               Random rand = new Random();
                
                @Override
                public void run()
                {                    
                    if(getServer().getOnlinePlayers().size() < 1)
                    {
                        return;
                    }
                    
                    else if(!dashcaster_run)
                    {
                        CancelDashcasterTasks();
                        
                        dashcaster_run = true;
                        
                        StartDashcaster();                                    
                    };
                    
                    String dashcast_message = dashcast_messages.get(rand.nextInt(dashcast_messages.size()));
                    getServer().broadcastMessage(dashcast_message);
                };
            }, 
            
            dashcast_interval, dashcast_interval
        );
    };
    
    @Override
    public void onEnable()
    {
        Luna.print("Dashies Auto Broadcaster is loading ....");
         
        saveDefaultConfig();
         
        DoConfigReload();
        StartDashcaster();
         
        getCommand("dashcaster").setExecutor(new CommandsHandler());
        
        Luna.print("Dashies Auto Broadcaster is now running.");
    };
    
    class CommandsHandler implements CommandExecutor
    {
        String permission = config.getString("permission");
        
        String denied_message = Luna.transStr("&cYou have insufficient permssions ;c");
        String reload_message = Luna.transStr("&aDash Cast has been reloaded.");
        
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args)
        {
            if(sender instanceof Player)
            {
                if(!((Player)sender).hasPermission(permission))
                {
                    ((Player)sender).sendMessage(denied_message);
                    return false;
                };
            };
            
            DoConfigReload();
            
            if(sender instanceof Player)
                ((Player)sender).sendMessage(reload_message);
            else
                Luna.print(reload_message);
            
            return true;
        };
    };    
    
    @Override
    public void onDisable()
    {
        CancelDashcasterTasks();
        
        Luna.print("Dashies Auto Broadcaster has been disabled!");
    };
};
