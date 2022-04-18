

// Author: Dashie
// Version: 1.0


package com.dashkraft;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import java.util.Arrays;
import java.util.Random;
import org.bukkit.World;
import org.bukkit.Color;
import java.util.List;


public class Wand extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;    
    
    public static CommandsHandler commands = new CommandsHandler();
    public static EventsHandler events = new EventsHandler();
    
    
    @Override
    public void onEnable()
    {
        Kvinne.print("Plugin is loading ....");
        
        config = (FileConfiguration) getConfig();
        plugin = (JavaPlugin) this;
        
        saveDefaultConfig();
        
        reload_plugin();
        
        getServer().getPluginManager().registerEvents(events, plugin);
        getCommand("dashwand").setExecutor(commands);
        
        Kvinne.print("----------------------");        
        Kvinne.print("Author: Dashie");
        Kvinne.print("Version: 1.0");       
        Kvinne.print("----------------------");
        Kvinne.print("Email: KvinneKraft@protonmail.com");
        Kvinne.print("Github: https://github.com/KvinneKraft");        
        Kvinne.print("----------------------");        
        
        Kvinne.print("Plugin has been enabled!");
    };
    
    @Override
    public void onDisable()
    {
        Kvinne.print("Plugin has been disabled!");
    };
    
    
    public static void reload_plugin()
    {
        plugin.reloadConfig();
        config = plugin.getConfig();
    
        commands.developer_support = config.getBoolean("dash-wand.optionals.developer-support");        
        
        events.explosions = config.getBoolean("dash-wand.magick.explosion.enabled");
        events.terrain = config.getBoolean("dash-wand.magick.explosion.terrain");
        events.fire = config.getBoolean("dash-wand.magick.explosion.fire");
        events.fireworks = config.getBoolean("dash-wand.magick.fireworks");
        events.lightning = config.getBoolean("dash-wand.magick.lightning");
        
        events.explosion_radius = config.getInt("dash-wand.magick.explosion.radius");
        events.max_distance = config.getInt("dash-wand.optionals.max-distance");
        events.cooldown = config.getInt("dash-wand.optionals.cooldown");
        
        events.use_permission = config.getString("dash-wand.optionals.use-permission");
        commands.admin_permission = config.getString("dash-wand.optionals.admin-permission");
    };
};


class EventsHandler implements Listener
{
    static boolean explosions, terrain, fire, fireworks, lightning;    
    static int explosion_radius, max_distance, cooldown;
    
    final private String denied_message = Kvinne.color("&cYou are not supposed to use this item, are ya?");
    final private String cooldown_message = Kvinne.color("&cCalm down a bit, you can use this soon!");    
    
    private List<Player> pdb = new ArrayList<>();
    
    static String use_permission;
    
    
    @EventHandler
    public void onPlayerItemInteract(PlayerInteractEvent e)
    {
        final Player p = e.getPlayer();
        
        if(pdb.contains(p))
        {
            p.sendMessage(cooldown_message);
            return;
        };
        
        final ItemStack item = e.getItem();
        
        if((item == null) || (item.getType().equals(Material.AIR)) || (!item.hasItemMeta()) || (!item.getItemMeta().hasCustomModelData()) || (item.getItemMeta().getCustomModelData() != 2020))
        {
            return;
        }
        
        else if(!p.hasPermission(use_permission))
        {
            p.sendMessage(denied_message);
            return;
        }
        
        else if((e.getAction() != Action.RIGHT_CLICK_AIR) && (e.getAction() != Action.RIGHT_CLICK_BLOCK))
        {
            return;
        };
        
        if((explosions) || (fireworks) || (lightning))
        {
            Location location = p.getTargetBlock(null, max_distance).getLocation();
            World world = location.getWorld();
        
            if(explosions)
                world.createExplosion(location, explosion_radius, fire, terrain);
            
            if(fireworks)
            {
                Firework firework = (Firework)location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                FireworkMeta firework_meta = firework.getFireworkMeta();
            
                Random rand = new Random();
                
                int r = rand.nextInt(255) + 1;
                int g = rand.nextInt(255) + 1;
                int b = rand.nextInt(255) + 1;
                
                Color firework_color = Color.fromRGB(r, g, b);
            
                firework_meta.addEffect(FireworkEffect.builder().withColor(firework_color).withFlicker().withTrail().with(FireworkEffect.Type.BURST).with(FireworkEffect.Type.BALL_LARGE).flicker(true).build());
                firework.setFireworkMeta(firework_meta);   
                
                firework.detonate();                
            };
            
            if(lightning)
                for(int i = 0; i < 4; i += 1)
                    world.strikeLightning(location);
        };
        
        if(!p.isOp())
        {
            pdb.add(p);
        
            Bukkit.getScheduler().scheduleSyncDelayedTask(Wand.plugin,
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(pdb.contains(p))
                        {
                            pdb.remove(p);
                        
                            if(p.isOnline())
                            {
                                p.sendMessage(Kvinne.color("&aYou may now use the Dash Wand again."));
                            };
                        };
                    };
                },
            
                cooldown * 20
            );
        };
    };
};


class CommandsHandler implements CommandExecutor
{
    private final String display_name = Kvinne.color("&b&l&k::&r &d&l&nD&5a&d&ls&5h&r &6&l&nW&e&la&6n&e&ld&r &b&l&k::&r");
    
    
    private ItemStack get_wand(final int amount)
    {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD, amount);
        ItemMeta meta = wand.getItemMeta();
        
        meta.setLore(
            Arrays.asList(
                new String[] 
                { 
                    Kvinne.color("&e&oA magicians wand....") 
                }
            )
        );
        
        meta.setDisplayName(display_name);
        meta.setCustomModelData(2020);
        
        wand.setItemMeta(meta);
        
        return wand;
    };
    
    
    static boolean developer_support;
    
    private final String give_message = Kvinne.color("&aYou have given yourself &d%a% &awand(s)!");
    private final String number_message = Kvinne.color("&cYou must specify a numeric amount if any.");
    private final String correct_syntax = Kvinne.color("&cCorrect syntax: &7/dashwand [reload | get]");
    private final String developer_message = Kvinne.color("&eHey there, this plugin has been coded by the one and only &d&lDashie&e, you can see some more of my work by clicking the following links: \n \n&eGithub: &bhttps://github.com/KvinneKraft \n&eWebsite: &bhttps://pugpawz.com");
    private final String denied_message = Kvinne.color("&cIt seems like you lack sufficient permissions.");
    private final String reloading_message = Kvinne.color("&aReloading plugin ....");
    private final String reloaded_message = Kvinne.color("&aPlugin has been reloaded!");
    
    static String admin_permission;
    
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
        {
            return false;
        };
        
        Player p = (Player) s;
        
        if(!p.hasPermission(admin_permission))
        {
            if(developer_support)
            {
                p.sendMessage(developer_message);
            }
            
            else
            {
                p.sendMessage(denied_message);
            }
            
            return false;
        }
        
        else if(as.length > 0)
        {
            a = as[0].toLowerCase();
            
            if(a.equals("get"))
            {
                int amount = 1;
                
                if(as.length >= 2)
                {
                    try
                    {
                        amount = Integer.valueOf(as[1]);
                    }
                    
                    catch (Exception e)
                    {
                        p.sendMessage(number_message);
                        return false;
                    }
                };
                
                p.getInventory().addItem(get_wand(amount));
                p.sendMessage(give_message.replace("%a%", String.valueOf(amount)));
            }
            
            else if(a.equals("reload"))
            {
                p.sendMessage(reloading_message);
                
                Wand.reload_plugin();
                
                p.sendMessage(reloaded_message);
            }
            
            else
            {
                p.sendMessage(correct_syntax);
            };
        }
        
        else
        {
            p.sendMessage(correct_syntax);
        };
        
        return true;
    };
};


class Kvinne
{
    public static void print(String str)
    {
        System.out.println("(Dash Kraft): " + str);
    };
    
    
    public static String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};
