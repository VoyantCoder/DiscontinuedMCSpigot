
// Author: Dashie
// Version: 1.0

package com.dashness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DashTP extends JavaPlugin
{
    public static JavaPlugin plugin;
    public static FileConfiguration config;
    
    Economy econ;
    
    @Override
    public void onEnable()
    {
        Moony.Print("Enabling ze Dash Tp ....");
        
        if(!hasVault())
        {
            Moony.Print("VAULT could not be found, it is required!");
            this.getPluginLoader().disablePlugin(this);
        };
            
        saveDefaultConfig();
        
        config = getConfig();
        plugin = this;
        
        econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        
        getCommand("dashtp").setExecutor(new CommandHandler());
        
        Moony.Print("Dash Tp has been enabled ;D");
    };
    
    class CommandHandler implements CommandExecutor
    {
        List<String> err;
        List<String> scc;
        
        List<String> worlds;
        
        double maxx;
        double minx;
        
        double maxy;
        double miny;
        
        double maxz;
        double minz;
        
        int cooldown;
        
        private void UpdateConfig()
        {
            config.set("allowed-worlds", worlds);            
            
            config.set("coords.max-x", maxx);
            config.set("coords.min-x", minx);
            
            config.set("coords.max-y", maxy);
            config.set("coords.min-y", miny);            
            
            config.set("coords.max-z", maxz);
            config.set("coords.min-z", minz);
            
            config.set("cooldown", cooldown);
            
            plugin.saveConfig();
            
            return;
        };
        
        private void RefreshData()
        {
            plugin.reloadConfig();
            config = plugin.getConfig();  
            
            admin_permission = config.getString("admin-permission");
            no_cost_permission = config.getString("no-cost-permission");
            teleport_permission = config.getString("teleport-permission");
            no_cooldown_permission = config.getString("no-cooldown-permission");
        
            teleport_cost = config.getInt("teleport-cost");
        
            liquid_block = Material.getMaterial(config.getString("liquid-block"));
            
            worlds = config.getStringList("allowed-worlds");
            
            maxx = config.getDouble("coords.max-x");
            minx = config.getDouble("coords.min-x");
            
            maxy = config.getDouble("coords.max-y");
            miny = config.getDouble("coords.min-y");
            
            maxz = config.getDouble("coords.max-z");
            minz = config.getDouble("coords.min-z");
            
            send_title = config.getBoolean("send-title");
            cooldown = config.getInt("cooldown");                      
            
            SetupMessages();
            
            return;
        };
        
        private void SetupMessages()
        {
            scc = config.getStringList("success-messages");
            err = config.getStringList("error-messages");
            
            for(String str : scc)
                scc.set(scc.indexOf(str), Moony.transStr(str));
            
            for(String str : err)
                err.set(err.indexOf(str), Moony.transStr(str));
            
            return; 
        };
        
        boolean send_title = config.getBoolean("send-title");
        
        String admin_permission = config.getString("admin-permission");
        String no_cost_permission = config.getString("no-cost-permission");
        String teleport_permission = config.getString("teleport-permission");
        String no_cooldown_permission = config.getString("no-cooldown-permission");
        
        int teleport_cost = config.getInt("teleport-cost");
        
        List<String> player_cache = new ArrayList<String>();
        
        Material liquid_block = Material.getMaterial(config.getString("liquid-block"));
        
        @Override
        public boolean onCommand(CommandSender s, Command c, String a, String[] as)
        {
            if(!(s instanceof Player))
                return false;
            
            if((scc == null) || (err == null))
            {
                RefreshData();                       
            };
            
            Player p = (Player) s;            
            
            if(as.length < 1)
            {
                p.sendMessage(err.get(0));
                return false;
            };
            
            a = as[0].toLowerCase();
            
            if(a.equals("go"))
            {
                if(!p.hasPermission(teleport_permission))
                {
                    p.sendMessage(err.get(2));
                    return false;
                }
                
                else if(cooldown > 1)
                {
                    if(!p.hasPermission(no_cooldown_permission))
                    {
                        if(player_cache.contains(p.getName()))
                        {
                            p.sendMessage(err.get(7).replace("%c%", String.valueOf(cooldown)));
                            return false;
                        };
                    };
                };
                
                if(!worlds.contains(p.getWorld().getName()))
                {
                    p.sendMessage(err.get(8));
                    return false;
                }
                
                else
                if(teleport_cost > 0)
                {
                    if(!p.hasPermission(no_cost_permission))
                    {
                        if(!econ.has(p, teleport_cost))
                        {
                            p.sendMessage(err.get(6).replace("%m%", String.valueOf(teleport_cost)));
                            return false;
                        };
                    };
                };
                
                double x, y = maxy, z;
                
                Random rand = new Random();
                
                x = minx + (maxx - minx) * rand.nextDouble();//Math.floor(Math.random() * ((maxx - minx) + 1)) + maxx;
                z = minz + (maxz - minz) * rand.nextDouble(); //Math.floor(Math.random() * ((maxz - minz) + 1)) + maxz;
                
                World world = p.getWorld();
                Location location = new Location(world, x, y, z);
                
                boolean isLand = false;
                
                while(!isLand)
                {
                    if(y <= miny)
                    {
                        p.sendMessage(err.get(9));
                        return false;
                    };
                    
                    location.setY(y);
                    
                    if(location.getBlock().getType() != Material.AIR)
                    {
                        if((liquid_block != null) && (!liquid_block.toString().toUpperCase().equals("NONE")))
                        {
                            Material block_type = location.getBlock().getType();
                            
                            if((block_type == Material.WATER) || (block_type == Material.LAVA))
                            {
                                location.getBlock().setType(liquid_block);
                            };
                        };
                            
                        isLand = true;
                    }
                    
                    else y--;
                };
                
                if(cooldown > 1)
                {
                    if(!p.hasPermission(no_cooldown_permission))
                    {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, 
                            new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if(player_cache.contains(p.getName()))
                                    {
                                        player_cache.remove(p.getName());
                                    
                                        if(p.isOnline())
                                        {
                                            p.sendMessage(scc.get(5));
                                        };
                                    };
                                };
                            }, 
                            
                            cooldown * 20
                        );
                        
                        player_cache.add(p.getName());                        
                    };
                }
                
                if(teleport_cost > 0)
                {
                    if(!p.hasPermission(no_cost_permission))
                    {
                        econ.withdrawPlayer(p, teleport_cost);
                        p.sendMessage(scc.get(4).replace("%m%", String.valueOf(teleport_cost)));                    
                    };
                };
                
                location.setY(location.getY() + 3);
                p.teleport(location);
                
                String sx, sy, sz;
                
                sx = String.valueOf((int)x);
                sy = String.valueOf((int)y);
                sz = String.valueOf((int)z);
                
                String message = scc.get(6).replace("%x%", sx).replace("%y%", sy).replace("%z%", sz);
                
                if(send_title)
                {
                    p.sendTitle("", message);
                }
                
                else
                {
                    p.sendMessage(message);
                };
            }
            
            else if(a.equals("reload"))
            {
                if(!p.hasPermission(admin_permission))
                {
                    p.sendMessage(err.get(2));
                    return false;
                };
                    
                p.sendMessage(scc.get(0));
                
                RefreshData();
                
                p.sendMessage(scc.get(1));
            }
            
            else if(a.equals("setrange"))
            {
                if(!p.hasPermission(admin_permission))
                {
                    p.sendMessage(err.get(2));
                    return false;
                }
                
                else if(as.length < 7)
                {
                    p.sendMessage(err.get(1));
                    return false;
                };
                
                List<Double> coords = new ArrayList<Double>();
                
                for(int id = 1; id < 7; id += 1)
                {
                    Double coord = Double.valueOf(as[id]);
                    
                    if(coord == null)
                    {
                        p.sendMessage(err.get(3));
                        return false;
                    };
                    
                    coords.add(coord);
                };
                
                maxx = coords.get(0);
                minx = coords.get(1);
                
                maxy = coords.get(2);
                maxy = coords.get(3);
                
                maxz = coords.get(4);
                minz = coords.get(5);
                
                UpdateConfig();
                RefreshData();
                
                p.sendMessage(scc.get(7));
            }
            
            else if((a.equals("addworld")) || (a.equals("delworld")))
            {
                if(!p.hasPermission(admin_permission))
                {
                    p.sendMessage(err.get(2));
                    return false;
                }
                
                else if(as.length < 2)
                {
                    p.sendMessage(err.get(1));
                    return false;
                };
                
                List<String> world_order = new ArrayList<>();
                world_order.addAll(1, Arrays.asList(as[1].split(",")));
                
                boolean contains_world = false;                
                
                for(String world : world_order)
                {
                    if(worlds.contains(world))
                    {
                        contains_world = true;
                        break;
                    };
                };
                
                if(contains_world)
                {
                    if(a.equals("addworld"))
                    {
                        p.sendMessage(err.get(4).replace("%w%", as[1]));
                        return false;
                    }
                    
                    else//delworld
                    {
                        for(String world : world_order)
                        {
                            worlds.remove(world);               
                        };
                        
                        p.sendMessage(scc.get(3).replace("%w%", as[1]));
                    };
                }
                
                else
                {
                    if(a.equals("addworld"))
                    {
                        for(String world : world_order)
                        {
                            worlds.add(world);
                        };
                        
                        p.sendMessage(scc.get(2).replace("%w%", as[1]));
                    }
                    
                    else//delworld
                    {
                        p.sendMessage(err.get(5).replace("%w%", as[1]));
                        return false;
                    };
                };
                
                UpdateConfig();
            }
            
            else if(a.equals("admin"))
            {
                if(!p.hasPermission(admin_permission))
                {
                    p.sendMessage(err.get(2));
                    return false;
                };
                
                p.sendMessage(err.get(1));
            }
            
            else
            {
                p.sendMessage(err.get(0));
            };
            
            return false;
        };
    };
    
    @Override
    public void onDisable()
    {
        Moony.Print("Dash Tp has been disabled ;c");
    };
    
    public boolean hasVault()
    {
        if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        
        else
            return true;
    };
};
