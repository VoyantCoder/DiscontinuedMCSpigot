
// Author: Dashie
// Version: 1.0

package com.dash.security;

public class Configuration
{
    
    
    public static Boolean Load()
    {
        DashSecurity.plugin.reloadConfig();
        DashSecurity.config = DashSecurity.plugin.getConfig();
        
        
        
        return true;
    };
};
