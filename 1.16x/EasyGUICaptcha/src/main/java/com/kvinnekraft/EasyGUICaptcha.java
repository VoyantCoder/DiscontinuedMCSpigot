
package com.kvinnekraft;

import org.bukkit.plugin.java.JavaPlugin;

public class EasyGUICaptcha extends JavaPlugin
{
    private void ErrorHandler(Exception E)
    {
        final String ErrorFormat = ("(EasyGUICaptcha): An error has occurred: \r\n" + E.getMessage() + "\r\n" + E.getCause().toString());

        System.out.println(ErrorFormat);

        getServer().getPluginManager().disablePlugin(this);
    }


    @Override
    public void onEnable()
    {
        SendLog("Plugin is being enabled.");

        try
        {
            // Design
        }

        catch (Exception E)
        {
            ErrorHandler(E);
        }

        SendLog("Author: Dashie / KvinneKraft");
        SendLog("Version: 1.0");
        SendLog("Github: https://github.com/KvinneKraft");
    }


    @Override
    public void onDisable()
    {
        SendLog("Plugin is being disabled.");
    }

    private void SendLog(final String data)
    {
        System.out.println("(EasyGUICaptcha): " + data);
    }
}