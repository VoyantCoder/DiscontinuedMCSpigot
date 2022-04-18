
// Author: Dashie
// Version: 1.0

package com.dashcollection;


import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class EventsHandler implements Listener
{   
    List<BukkitTask> runnables = new ArrayList<>();
    List<Integer> chances = new ArrayList<>();      
    List<String> commands = new ArrayList<>();  
    List<UUID> p_uuid = new ArrayList<>();
    
    Server server = Bukkit.getServer();    
    BukkitScheduler scheduler = server.getScheduler();
    
    String reward_permission;    
    String standard_command;
    
    String reward_lucky_message;    
    String reward_message;
    
    Integer reward_interval;
    
    Integer min_chance;
    Integer max_chance;
    
    public void add_reward_task(Player p, UUID uuid)
    {
        if((p_uuid.contains(uuid)) || (!p.hasPermission(reward_permission)))
        {
            return;
        };
        
        runnables.add(
            scheduler.runTaskTimer(Session.plugin, 
                new Runnable()
                {
                    String player = p.getName();
                    
                    @Override
                    public void run()
                    {
                        int rand_num = new Random().nextInt(max_chance + min_chance) + 1;                        
                        int id = new Random().nextInt(chances.size());
                        int chance = chances.get(id);
                        
                        String command = "";                        
                        
                        if(chance >= rand_num)
                        {
                            command = commands.get(id);                                             
                            
                            p.sendTitle(" ", reward_lucky_message);                        
                            p.sendMessage(reward_lucky_message);   
                        }
                            
                        else
                        {
                            command = standard_command;                            
                            
                            p.sendTitle(" ", reward_message);                        
                            p.sendMessage(reward_message);
                        };
                        
                        server.dispatchCommand(server.getConsoleSender(), command.replace("%player%", player));//
                    };
                },
                
                reward_interval * 20, reward_interval * 20
            )
        );
        
        p_uuid.add(uuid);        
    };
    
    public void suspend_all_threads()
    {
        for(BukkitTask runnable : runnables)
        {
            runnable.cancel();
        };  
        
        runnables.clear();        
    };
    
    public void Refresh()
    {
        suspend_all_threads();
        
        p_uuid.clear();            
      
        reward_permission = Session.config.getString("reward-properties.reward-permission");
        reward_interval = Session.config.getInt("reward-properties.reward-interval");       
        
        reward_lucky_message = Session.moon.transStr(Session.config.getString("reward-properties.special-reward-message"));
        reward_message = Session.moon.transStr(Session.config.getString("reward-properties.reward-message"));          
        
        standard_command = Session.config.getString("reward-properties.standard-reward");
        
        min_chance = Session.config.getInt("reward-properties.max-chance");
        max_chance = Session.config.getInt("reward-properties.min-chance");
        
        if(commands.size() > 0)
        {
            commands.clear();
            chances.clear();
        };
        
        for(String str : Session.config.getStringList("reward-properties.rewards"))
        {
            str = str.replace("(chance):", "`");
            String[] arr = str.split("`");
            
            if(arr.length < 2)
            {
                Session.moon.print("Invalid format received. Skipping ....");
                continue;
            };
            
            Integer chance = Integer.valueOf(arr[1]);
            
            if(chance < 1)
            {
                Session.moon.print("Invalid chance specified (" + arr[1] + ") in config. Skipping ....");
                continue;
            };
            
            commands.add(arr[0]);            
            chances.add(chance);
        };
               
        for(Player p : server.getOnlinePlayers())
        {
            if(!p.hasPermission(reward_permission))
            {
                return;
            };
                
            add_reward_task(p, p.getUniqueId());
        };        
    };
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        add_reward_task(e.getPlayer(), e.getPlayer().getUniqueId());
    };
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        
        if(!p_uuid.contains(uuid))
        {
            return;
        };
        
        int id_index = p_uuid.indexOf(uuid);
        
        runnables.get(id_index).cancel();
        runnables.remove(id_index);
        
        p_uuid.remove(id_index);
    };
};
