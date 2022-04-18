
// Author: Dashie
// Version: 1.0


package com.dashchat;


import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


// To-Do:
//
// Command Support
// Colour Permissions
// Use Permissions
// Use Money Cost
// Use Firework or Lightning Effect
// Use Sound
// Custom display Format
// Custom display Commands


public class ItemDisplay extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    public static DisplayCommands commands = new DisplayCommands();
    public static DisplayItem events = new DisplayItem();
    
    
    @Override
    public void onEnable()
    {
        Moon.print("Plugin is loading ....");
        
        saveDefaultConfig();
        plugin = this;
        
        commands.refresh_data();
        events.refresh_data();
        
        getServer().getPluginManager().registerEvents(events, plugin);
        getCommand("itemdisplay").setExecutor(commands);
        
        Moon.print("Plugin has been loaded!");
    };
    
    
    @Override
    public void onDisable()
    {
        Moon.print("Plugin has been disabled!");
    };
};



class DisplayItem implements Listener
{   
    List<String> commands = new ArrayList<String>();
    
    
    public void refresh_data()
    {
        ItemDisplay.plugin.reloadConfig();
        ItemDisplay.config = ItemDisplay.plugin.getConfig();
        
        FileConfiguration config = ItemDisplay.config;
        
        if(commands.size() > 0)
        {
            commands.clear();
        };
        
        for(String str : config.getStringList("display-item-properties.commands"))
        {
            if(str != null)
            {
                if(str.length() > 0)
                {
                    commands.add(str.toUpperCase());
                };
            };
        };
        
        permission_denied_message = Moon.colors(config.getString("display-item-properties.messages.permission-denied"));
        display_format = Moon.colors(config.getString("display-item-properties.messages.display-format"));
        
        colour_permission = config.getString("display-item-properties.permissions.color-permission");
        use_permission = config.getString("display-item-properties.permissions.use-permission");
        
        cooldown = config.getInt("display-item-properties.command-cooldown") * 20;
    };

    
    List<Player> players = new ArrayList<>();
    
    String display_format, use_permission, colour_permission, permission_denied_message;
    Integer cooldown;
    
    
    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e)
    {
        Player p = e.getPlayer();
        
        boolean has_command = false;
        //String command;
        
        for(String str : e.getMessage().split(" "))
        {
            if(commands.contains(str.toUpperCase()))
            {
                has_command = true;
                //command = commands.get(commands.indexOf(str.toUpperCase()));
                        
                break;
            };
        };
        
        if((!has_command) || (players.contains(p)))
        {
            return;
        }
        
        else if(!p.hasPermission(use_permission))
        {
            p.sendMessage(permission_denied_message);
            e.setCancelled(true);
            
            return;
        };
        
        ItemStack item = p.getInventory().getItemInMainHand();
        String name = p.getCustomName() + Moon.colors("\'s &7&ohand!");
        
        if((name.contains("null")) || (name.length() < 4))
        {
            name = Moon.colors("&e" + p.getName() + "\'s &7&ohand!");
        };
            
        if(item.getType() != Material.AIR)
        {
            name = item.getType().toString().toLowerCase().replace("_", " ");
            name = display_format.replace("%i%", name).replace("%c%", String.valueOf(item.getAmount()));
            
            /*if((item.hasItemMeta()) && (item.getItemMeta().hasDisplayName()))
            {
                name = item.getItemMeta().getDisplayName();
        
                if(!p.hasPermission(colour_permission) && (!ChatColor.stripColor(name).equals(name)))
                {
                    name = ChatColor.stripColor(name);
                };        
            };
        
            if(item.hasItemMeta())
            {
                ItemMeta meta = item.getItemMeta();
            
                if(meta.hasEnchants())
                {
                    for(int id = 0; id < meta.getEnchants().size(); id += 1)
                    {
                        if(id == 0)
                        {
                            name += "\n \n" + meta.getLore().get(0);
                            continue;
                        };                    
                    
                        name += "\n" + meta.getEnchants().get(id).toString();
                    };
                };
            
                if(meta.hasLore())
                {
                    for(int id = 0; id < meta.getLore().size(); id += 1)
                    {
                        if((meta.hasEnchants()) && (id == 0))
                        {
                            name += "\n \n" + meta.getLore().get(0);
                            continue;
                        };
                    
                        name += "\n" + meta.getLore().get(id);
                    };
                };
            };*/
        };
        
        //.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(data).create()));
        
        Bukkit.getScheduler().runTaskLaterAsynchronously(ItemDisplay.plugin,
            new Runnable()
            {
                @Override
                public void run()
                {
                    if(players.contains(p))
                    {
                        players.remove(p);
                    };
                };
            },
            
            cooldown
        );
        
        if(!p.isOp())
        {
            players.add(p);
        };
        
        e.setMessage(name);
    };
};



class DisplayCommands implements CommandExecutor
{
    String developer_message, permission_denied_message, required_permission, correct_syntax, reloading_message, reloaded_message;    
    
    boolean t = true, f = false, developer_plug;    
    
    public void refresh_data()
    {
        ItemDisplay.plugin.reloadConfig();
        ItemDisplay.config = ItemDisplay.plugin.getConfig();
        
        FileConfiguration config = ItemDisplay.config;
        
        permission_denied_message = Moon.colors(config.getString("display-item-properties.optionals.permission-denied"));
        developer_message = Moon.colors("&eHai, I am Dashie, the Developer of this plugin.\n \n&eSee some more of my work at my Github, here: &dhttps://github.com/KvinneKraft/");        
        correct_syntax = Moon.colors("&cCorrect Syntax: &e/");
        
        required_permission = config.getString("display-item-properties.optionals.admin-permission");
    };
    
    public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
            return f;
        
        Player p = (Player) s;
        
        if(!p.hasPermission(required_permission))
        {
            if(developer_plug)
            {
                p.sendMessage(developer_message);
            }
            
            else
            {
                p.sendMessage(permission_denied_message);
            };
            
            return f;
        }
        
        else if(as.length < 1)
        {
            p.sendMessage(correct_syntax);
            return f;
        }
        
        else if(a.equalsIgnoreCase("reload"))
        {
            p.sendMessage(reloading_message);
            
            ItemDisplay.commands.refresh_data();
            ItemDisplay.events.refresh_data();
            
            p.sendMessage(reloaded_message);
        }
        
        else
        {
            p.sendMessage(correct_syntax);
            return f;
        };
        
        return t;
    };
};



class Moon 
{
    public static String colors(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
    
    public static void print(String str)
    {
        System.out.println("(Item Display): " + str);
    };
};