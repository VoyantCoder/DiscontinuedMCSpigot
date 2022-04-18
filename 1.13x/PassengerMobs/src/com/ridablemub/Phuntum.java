
// Author: Dashie
// Version: 1.0

package com.ridablemub;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.entity.EntityDismountEvent;

public class Phuntum extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    
    @Override
    public void onEnable()
    {
        Moony.Print("The plugin is currently loading ....");
        
        saveDefaultConfig();
        
        plugin = (JavaPlugin)this;
        config = plugin.getConfig();
        
        getServer().getPluginManager().registerEvents(new EventsHandler(), this);
        getCommand("phuntums").setExecutor(new CommandsHandler());
        
        Moony.Print("The plugin is now running :D");
    };
    
    class EventsHandler implements Listener
    {
        String success_dismounted = Moony.transStr("&aYou are now no longer riding &b%m% &a!");
        
        @EventHandler
        public void onDismount(EntityDismountEvent e)
        {
            if(e.getDismounted().hasMetadata("phuntumz"))
            {
                ((Player)e.getEntity()).sendMessage(success_dismounted.replace("%m%", e.getDismounted().getType().toString().replace("-", " ")));
                e.getDismounted().remove();            
            };
        };
    };
    
    class CommandsHandler implements CommandExecutor
    {
        String error_syntax = Moony.transStr("&cproper syntax: &7/phuntum ride <mob>");
        String error_notfound = Moony.transStr("&cThis mob could not be found ;c");
        String error_perms = Moony.transStr("&cYou lack sufficient permissions.");
        
        String success_mounted = Moony.transStr("&aYou are now riding a &b%m% &a!");
        
        String permission = config.getString("phuntum.command-permission");
        
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args)
        {
            if(!(sender instanceof Player))
                return false;
            
            Player p = (Player)sender;
            
            if(args.length < 1)
            {
                p.sendMessage(error_syntax);
            }
            
            else if(!p.hasPermission(permission))
            {
                p.sendMessage(error_perms);
            }
            
            else if((args[0].equalsIgnoreCase("ride")) && (args.length > 1))
            {
                Location location = p.getLocation();
                String mob = "";
                
                for (int id = 1; id < args.length; id += 1)
                {
                    mob += args[id] + " ";
                };
                
                mob = Moony.removeLastChar(mob);
                mob = mob.replace(" ", "_");
                
                if(EntityType.fromName(mob) != null)
                {
                    LivingEntity entity = (LivingEntity)location.getWorld().spawnEntity(location, EntityType.fromName(mob.toUpperCase()));
                    
                    if((entity instanceof Creature) && (!(entity instanceof Player)) && (entity != null))
                    {
                        entity.setMetadata("phuntumz", new FixedMetadataValue(plugin, "phuntumz"));
                        entity.setInvulnerable(true);
                        entity.addPassenger(p);
                    
                        p.sendMessage(success_mounted.replace("%m%", entity.getType().toString().replace("-", " ")));
                    }
                }
            
                else
                {
                    p.sendMessage(error_notfound);
                };                
            }
            
            else
            {
                p.sendMessage(error_syntax);
            };
            
            return true;
        };
    };
    
    @Override
    public void onDisable()
    {
        Moony.Print("The plugin has been disabled ;c");
    };
};
