// Author: Dashie
// Version: 1.0

package com.recoded.iplock;

import com.recoded.DashSec;
import com.recoded.DashSecurity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class IPLock
{
    private final FileConfiguration config = (FileConfiguration) DashSecurity.config;
    private final JavaPlugin plugin = (JavaPlugin) DashSecurity.plugin;
    
    private final DashSec sec = new DashSec();
    private boolean isEnabled = false;
    
    public void Enable()
    {
        isEnabled = config.getBoolean("dash-ip-lock.enabled");
        
        if (!isEnabled)
        {
            sec.print("Dash ip lock has been disabled in the configuration file (config.yml) skipping ....");
            return;
        };
        
        for (final String usr : config.getStringList("dash-ip-lock.ip-map"))
        {
            final String[] arr = usr.replace(" ", "").split(":");
            
            if (arr.length < 2)
            {
                sec.print("Invalid ip lock format found in the configuration file (config.yml) skipping ....");
                continue;
            };
            
            final List<String> ips = new ArrayList<>();
            
            for (final String ip : arr[1].split(","))
            {
                ips.add(ip);
            };
            
            if (ips.size() < 1)
            {
                sec.print("No ips were specified in the configuration file (config.yml) skipping ....");
                continue;
            };
            
            ip_map.put(arr[0], ips);
        };
    };
    
    private final HashMap<String, List<String>> ip_map = new HashMap<>();
    
    public void CheckPlayer(final PlayerPreLoginEvent e)
    {
        if (!isEnabled)
        {
            return;
        };
        
        final String p_na = (String) e.getName();
        
        if (ip_map.containsKey(p_na))
        {
            final String p_ip = e.getAddress().getHostAddress().replace("/", "");
            
            if (!ip_map.get(p_na).contains(p_ip))
            {
                e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, sec.color("&cYou may not connect using this IP Address!"));
            };
        };
    };
};
