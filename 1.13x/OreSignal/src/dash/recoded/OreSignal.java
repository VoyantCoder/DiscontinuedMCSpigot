
// Author: Dashie
// Version: 1.0

package dash.recoded;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

// ORE NOTIFIER

public class OreSignal extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;
    
    protected void LoadConfiguration()
    {
        if (plugin != this)
        {
            plugin = (JavaPlugin) this;
        };
        
        saveDefaultConfig();
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        try
        {
            default_toggle_on = config.getBoolean("ore-signal.default-toggle-on");
            
            bypass_permission = config.getString("ore-signal.bypass-permission");
            admin_permission = config.getString("ore-signal.admin-permission");
            
            place_blocks.clear();
            
            for (final String dub : config.getStringList("ore-signal.signal-placement-blocks"))
            {
                try
                {
                    final Material substance = Material.getMaterial(dub.toUpperCase().replace(" ", "_"));
                    
                    if (substance == null)
                    {
                        throw new Exception("");
                    };
                    
                    place_blocks.add(substance);
                }
                
                catch (final Exception e)
                {
                    print("Invalid block found in the block placement section of the configuration file. Skipping....");
                };
            };
            
            place_message = color(config.getString("ore-signal.signal-place-message"));
            
            break_blocks.clear();
            
            for (final String dub : config.getStringList("ore-signal.signal-break-blocks"))
            {
                try
                {
                    final Material substance = Material.getMaterial(dub.toUpperCase().replace(" ", "_"));
                    
                    if (substance == null)
                    {
                        throw new Exception("");
                    };
                    
                    break_blocks.add(substance);
                }
                
                catch (final Exception e)
                {
                    print("Invalid block found in the block break section of the configuration file. Skipping....");
                };                
            };
            
            break_message = color(config.getString("ore-signal.signal-break-message"));
            
            sounds_enabled = config.getBoolean("ore-signal.sound-manager.enabled");
            
            if (sounds_enabled)
            {
                try
                {
                    notify_sound = Sound.valueOf(config.getString("ore-signal.sound-manager.sound"));
                }

                catch (final Exception e)
                {
                    print("Invalid sound found in the sound manager section. Disabling this feature ....");
                    sounds_enabled = false;
                };
            };
            
            staff_mates.clear();
        }
        
        catch (final Exception e)
        {
            print("An error has been found in the configuration file!");
        };
    };
    
    @Override public void onEnable()
    {
        print("The Dash A.I. is now booting ....");
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("oresignal").setExecutor(new Commands());
        
        print
        (
            (
                "\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
                " Author: Dashie \n" +
                " Version: 1.0 \n" +
                " Contact: KvinneKraft@protonmail.com \n" +
                " Github: https://github.com/KvinneKraft \n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
            )
        );
        
        print("The Dash A.I. is now up and running!");
    };

    final List<Material> place_blocks = new ArrayList<>();
    final List<Material> break_blocks = new ArrayList<>();

    final List<Player> staff_mates = new ArrayList<>();
    
    boolean default_toggle_on, sounds_enabled;    
    
    String bypass_permission, admin_permission, break_message, place_message;
    Sound notify_sound;
    
    protected class Events implements Listener
    {
        protected void SignalStaff(final String message)
        {
            getServer().getScheduler().runTaskAsynchronously
            (
                plugin, 
                    
                new Runnable() 
                { 
                    @Override public void run() 
                    { 
                        for (final Player p : staff_mates)
                        {
                            if (p.isOnline())
                            {
                                if (sounds_enabled)
                                {
                                    p.playSound(p.getLocation(), notify_sound, 28, 28);
                                };

                                p.sendMessage(message);
                            };
                        };
                    }; 
                }
            );
        };
        
        @EventHandler public void onBlockBreak(final BlockBreakEvent e)
        {
            final Player p = (Player) e.getPlayer();
            
            if (!p.hasPermission(bypass_permission))
            {
                if (place_blocks.contains(e.getBlock().getType()))
                {
                    SignalStaff(color(place_message.replace("%player%", p.getName()).replace("%block%", e.getBlock().getType().toString())));
                };
            };
        };
        
        @EventHandler public void onBlockPlace(final BlockPlaceEvent e)
        {
            final Player p = (Player) e.getPlayer();
            
            if (!p.hasPermission(bypass_permission))
            {
                if (break_blocks.contains(e.getBlock().getType()))
                {
                    SignalStaff(color(break_message.replace("%player%", p.getName()).replace("%block%", e.getBlock().getType().toString())));
                };
            };            
        };
    
        @EventHandler public void onQuit(final PlayerQuitEvent e)
        {
            final Player p = (Player) e.getPlayer();
            
            if (staff_mates.contains(p))
            {
                staff_mates.remove(p);
            };
        };
        
        @EventHandler public void onJoin(final PlayerJoinEvent e)
        {
            final Player p = (Player) e.getPlayer();
            
            if (default_toggle_on)
            {
                if (p.hasPermission(admin_permission))
                {
                    staff_mates.add(p);
                };
            };
        };
    };
    
    protected class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player!");
                return false;
            };
            
            final Player p = (Player) s;
            
            if (!p.hasPermission(admin_permission))
            {
                p.sendMessage(color("&cYou possess insufficient permissions!"));
                return false;
            };
            
            if (as.length >= 1)
            {
                if (as[0].equalsIgnoreCase("reload"))
                {
                    p.sendMessage(color("&aReloading ...."));
                    
                    LoadConfiguration();
                    
                    p.sendMessage(color("&aDone!"));
                }
                
                else
                {
                    p.sendMessage(color("&cInvalid syntax, did you mean: &4/oresignal reload &c?"));
                };
                
                return false;
            }
            
            else if (as.length <= 0)
            {
                if (staff_mates.contains(p))
                {
                    p.sendMessage(color("&aYou have turned off &eOreSignal&a!"));
                    staff_mates.remove(p);
                }
                
                else
                {
                    p.sendMessage(color("&aYou have turned on &eOreSignal&a!"));
                    staff_mates.add(p);
                };
            };
            
            return true;
        };
    };
    
    @Override public void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("The Dash A.I. is now offline!");
    };
    
    protected void print(final String d) 
    {
        System.out.println("(Ore Signal): " + d);
    };
    
    protected String color(final String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    };
};