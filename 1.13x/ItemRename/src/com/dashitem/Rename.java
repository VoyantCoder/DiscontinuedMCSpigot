
// Author: Dashie
// Version: 1.0


package com.dashitem;


import com.sun.tools.classfile.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


public class Rename extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    public static CommandsHandler kraft = new CommandsHandler();
    
    
    @Override
    public void onEnable()
    {
        Kvinne.print("Loading ze plugin ....");
        
        saveDefaultConfig();
        
        plugin = this;
        kraft.refresh_data();             
        
        getCommand("dashrename").setExecutor(kraft);   
        
        Kvinne.print("Plugin has successfully been loaded!");
    };
    
    
    @Override
    public void onDisable()
    {
        Kvinne.print("Plugin has been disabled!");
    }; 
};


class CommandsHandler implements CommandExecutor
{
    boolean t = true, f = false, developer_support, summon_fireworks, send_title;
    
    public void refresh_data()
    {
        Rename.plugin.reloadConfig();
        Rename.config = Rename.plugin.getConfig();
        
        FileConfiguration config = Rename.config;
        
        developer_support = config.getBoolean("optional-properties.developer-support");
        summon_fireworks = config.getBoolean("rename-properties.summon-firework");
        send_title = config.getBoolean("rename-properties.send-title");
        
        rename_permission = config.getString("rename-properties.permission");
        admin_permission = config.getString("optional-properties.permission");
        
        permission_denied_message = Kvinne.color(config.getString("optional-properties.permission-denied"));
        developer_message = Kvinne.color("&e");
        
        reloading_message = Kvinne.color("&aReloading the plugin ....");
        reloaded_message = Kvinne.color("&aThe plugin has been reloaded!");
        
        rename_message = Kvinne.color(config.getString("rename-properties.rename-message"));
        correct_usage = Kvinne.color("&cCorrect Usage: &7/rename [<item name> | reload]");
        
        maximum_length_message = Kvinne.color(config.getString("rename-properties.maximum-length-message"));
        maximum_length = config.getInt("rename-properties.maximum-length");
    
        cannot_name_air_message = Kvinne.color("&cSilly, you can not rename &bAir!");
        
        cooldown_over_message = Kvinne.color(config.getString("rename-properties.cooldown-over-message"));
        cooldown_message = Kvinne.color(config.getString("rename-properties.cooldown-message"));
        cooldown = config.getInt("rename-properties.cooldown");
    };
    
    String cooldown_message, cooldown_over_message, cannot_name_air_message, maximum_length_message, rename_permission, rename_message, admin_permission, permission_denied_message, correct_usage, developer_message, reloading_message, reloaded_message;
    Integer maximum_length, cooldown;
    
    List<Player> players = new ArrayList<>();
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
            return f;
        
        Player p = (Player) s;
        
        if(!p.isOp())
        {
            if(players.contains(p))
            {
                p.sendMessage(cooldown_message);
                return f;
            };
        };
        
        if(!p.hasPermission(rename_permission))
        {
            p.sendMessage(permission_denied_message);
            return f;
        }
            
        else if((as.length < 1) || (as[0].length() < 1))
        {
            p.sendMessage(correct_usage);
            return f;
        }
        
        else if(p.hasPermission(admin_permission))
        {
            a = as[0].toLowerCase();            
            
            if(a.equals("reload"))
            {
                p.sendMessage(reloading_message);
            
                refresh_data();
            
                p.sendMessage(reloaded_message);
            }
        }
        
        else if((as.length > maximum_length) && (!p.isOp()))
        {
            p.sendMessage(maximum_length_message);
            return f;
        };
        
        String name = "";
        
        for(String str : as)
        {
            name += str + " ";
        };
        
        name = Kvinne.color(name.substring(0, name.length() - 1));
        ItemStack item = p.getInventory().getItemInMainHand();
        
        if(item.getType().equals(Material.AIR))
        {
            p.sendMessage(cannot_name_air_message);
            return f;
        };
        
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(name);
        
        p.getInventory().getItemInMainHand().setItemMeta(meta);
        
        Bukkit.getScheduler().runTaskLaterAsynchronously(Rename.plugin,
            new Runnable()
            {
                @Override
                public void run()
                {
                    if(players.contains(p))
                    {
                        players.remove(p);
                        
                        if(p.isOnline())
                        {
                            p.sendMessage(cooldown_over_message);
                        };
                    };
                };
            }, 
            
            cooldown * 20
        );
            
        if(!p.isOp())
        {
            players.add(p);
        };
        
        if(summon_fireworks)
        {
            Integer[] rgb =
            {
                new Random().nextInt(255), 
                new Random().nextInt(255), 
                new Random().nextInt(255),
            };
            
            p.setInvulnerable(true);
            
            Kvinne.dashworks(rgb, p.getLocation());
            
            p.setInvulnerable(false);
        };
        
        String message = rename_message.replace("%n%", name);
        
        if(send_title)
        {
            p.sendTitle("", message);
        }
        
        else
        {
            p.sendMessage(message);
        };
        
        return t;
    };
};


class Kvinne
{
    public static void print(String str)
    {
        System.out.println("(Dashies Rename): " + str);
    };
    
    
    public static String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
    
    
    static List<FireworkEffect.Type> firework_effect_types = new ArrayList<>(
        Arrays.asList(
            new FireworkEffect.Type[]
            {
                FireworkEffect.Type.BALL, 
                FireworkEffect.Type.BALL_LARGE,
                FireworkEffect.Type.BURST,
                FireworkEffect.Type.CREEPER,
                FireworkEffect.Type.STAR,
            }
        )
    );
    
    
    public static void dashworks(Integer[] rgb, Location location)
    {
        Color color = Color.fromRGB(rgb[0], rgb[1], rgb[2]);
        
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta firework_meta = firework.getFireworkMeta();
        
        FireworkEffect.Type firework_effect_type = firework_effect_types.get(new Random().nextInt(firework_effect_types.size()));
        firework_meta.addEffect(FireworkEffect.builder().withColor(color).withFlicker().withTrail().with(firework_effect_type).flicker(true).build());
        
        firework.setFireworkMeta(firework_meta);
        firework.detonate();
    };    
};