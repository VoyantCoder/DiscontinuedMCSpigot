// Author: Dashie
// Version: 1.0

package com.recoded.login;

import com.recoded.DashSec;
import com.recoded.DashSecurity;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Login implements CommandExecutor
{
    private final FileConfiguration config = (FileConfiguration) DashSecurity.config;
    private final JavaPlugin plugin = (JavaPlugin) DashSecurity.plugin;
    
    private final DashSec sec = new DashSec();
    
    public void Enable()
    {
        if (!config.getBoolean("dash-login.enabled"))
        {
            sec.print("Dash Login has been disabled in the configuration file (config.yml) skipping ....");
            return;
        };
        
        for (final String cmd : "login,register,resetpassword".split(","))
        {
            plugin.getCommand(cmd).setExecutor(this);
        };
    };
    
    private void LoadUsers()
    {
        
    };
    
    private void SaveUsers()
    {
        
    };
    
    private final List<Player> authenticated = new ArrayList<>();
    
    public void onJoin(final PlayerJoinEvent e)
    {
        final Player p = (Player) e.getPlayer();
        
        if (authenticated.contains(p))
        {
            return;
        };
        
        // Write to separate file for logins and such, format be simple.
    };
    
    public void onQuit(final PlayerQuitEvent e)
    {
        final Player p = (Player) e.getPlayer();
        
        if (authenticated.contains(p))
        {
            authenticated.remove(p);
        };
    };
};
