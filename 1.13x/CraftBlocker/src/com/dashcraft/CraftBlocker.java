
// Author: Dashie
// Version: 1.0


package com.dashcraft;


import java.util.List;
import org.bukkit.Material;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.PrepareItemCraftEvent;


public class CraftBlocker extends JavaPlugin
{
    public FileConfiguration config = getConfig();
    public JavaPlugin plugin = this;
    
    Moony moon = new Moony();
        
    CommandsHandler commands = new CommandsHandler();
    EventsHandler events = new EventsHandler();
   
    @Override
    public void onEnable()
    {
        moon.print("Loading Dash Craft Blocker 1.0 ....");
        
        saveDefaultConfig();
        reloadPlugin();
        
        getServer().getPluginManager().registerEvents(events, plugin);
        getCommand("craftblocker").setExecutor(commands);
        
        moon.print("Author: Dashie");
        moon.print("Version: 1.0");
        moon.print("Email: KvinneKraft@protonmail.com");
        
        moon.print("Dash Craft Blocker 1.0 has been loaded!");
    };
    
    class CommandsHandler implements CommandExecutor
    {
        boolean t = true, f = false;
        
        public String access_denied_message, correct_usage_message, admin_permission;
        
        public String reloading_message = moon.transstr("&aReloading Dash Craft Blocker ....");
        public String reloaded_message = moon.transstr("&aDash Craft Blocker has been reloaded!");
        
        public String invalid_material_message = moon.transstr("&cThat is not a valid material.");
        
        public String it_already_exists = moon.transstr("&cThis item already exists in the list.");
        public String it_does_not_exist = moon.transstr("&cThis item is not in the list.");
        
        public String added_message = moon.transstr("&aAdded item to the list");
        public String delet_message = moon.transstr("&aDeleted item from the list.");
        
        // dashcraft [add | del | reload]
        
        @Override
        public boolean onCommand(CommandSender s, Command c, String a, String[] as)
        {
            if(!(s instanceof Player))
                return f;
            
            Player p = (Player) s;
            
            if(!p.hasPermission(admin_permission))
            {
                p.sendMessage(access_denied_message);
                return f;
            }
            
            else if(as.length < 1)
            {
                p.sendMessage(correct_usage_message);
                return f;
            }
            
            a = as[0].toLowerCase();
            
            if((a.equals("add")) || (a.equals("del")))
            {
                if(as.length < 2)
                {
                    p.sendMessage(correct_usage_message);
                    return f;
                };
                
                Material material = Material.getMaterial(as[1].toUpperCase());
                
                if(material == null)
                {
                    p.sendMessage(invalid_material_message);
                    return f;
                }
                
                else if(events.craft_blacklist.contains(material))
                {
                    if(a.equals("del"))
                    {
                        events.craft_blacklist.remove(material);
                        p.sendMessage(delet_message);
                    }
                    
                    else
                    {
                        p.sendMessage(it_already_exists);
                    };
                }
                
                else
                {
                    if(a.equals("add"))
                    {
                        events.craft_blacklist.add(material);
                        p.sendMessage(added_message);
                    }
                    
                    else
                    {
                        p.sendMessage(it_does_not_exist);
                    };
                };
                
                config.set("item-blacklist", events.craft_blacklist);
                plugin.saveConfig();
            }
            
            else if(a.equals("reload"))
            {
                p.sendMessage(reloading_message);
                
                reloadPlugin();
                
                p.sendMessage(reloaded_message);
            }
            
            else
            {
                p.sendMessage(correct_usage_message);
            };
            
            return true;
        };
    };
    
    private void reloadPlugin()
    {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        events.block_message = moon.transstr(config.getString("message"));        
        
        if(events.craft_blacklist.size() > 0)
        {
            events.craft_blacklist.clear();
        };
        
        for(String mat : config.getStringList("item-blacklist"))
        {
            Material material = Material.getMaterial(mat);
            
            if(material == null)
            {
                moon.print("The material " + mat + " is invalid. Skipping ....");
                continue;
            };
            
            events.craft_blacklist.add(material);
        };
        
        commands.admin_permission = config.getString("admin-permission");
        
        commands.access_denied_message = moon.transstr(config.getString("command-messages.access-denied"));
        commands.correct_usage_message = moon.transstr(config.getString("command-messages.correct-usage"));
    };
    
    class EventsHandler implements Listener
    {
        public List<Material> craft_blacklist = new ArrayList<>();
        public String block_message;
        
        @EventHandler
        public void onCraft(PrepareItemCraftEvent e)
        {
            if(!(e.getViewers().get(0) instanceof Player))
                return;
            
            Player p = (Player) e.getViewers().get(0);            
            
            if(p.hasPermission(commands.admin_permission))
                return;
            
            if((e.getRecipe() != null) && (e.getRecipe().getResult() != null))
            {
                ItemStack item = e.getRecipe().getResult();
                
                if(craft_blacklist.contains(item.getType()))
                {
                    item.setType(Material.AIR);
                    
                    p.sendTitle("", block_message);          
                    p.sendMessage(block_message);
                };
            };
        };
    };
    
    @Override
    public void onDisable()
    {
        moon.print("Dash Craft Blocker 1.0 has been disabled.");
    };
    
    class Moony
    {
        public void print(String str)
        {
            System.out.println("(Dash Craft Block): " + str);
        };
        
        public String transstr(String str)
        {
            return ChatColor.translateAlternateColorCodes('&', str);
        };
    };
};
