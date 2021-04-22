
/*
* Author: Dashie
* Version: 1.0
* */

package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class DashFarming extends JavaPlugin
{
    

    @Override
    public void onEnable()
    {

    }

    @Override
    public void onDisable()
    {

    }


    final void HandleError(final Exception E)
    {
        final String P1 = E.getMessage();
        SendLog("Hey there! It seems like an error occurred. Message as follows:\r\n" + P1);
        getServer().getPluginManager().disablePlugin(this);
    }

    final void SendLog(final String M)
    {
        System.out.println("[DashFarming]: " + M);
    }

    final String Colorize(final String M)
    {
        return ChatColor.translateAlternateColorCodes('&', M);
    }
}