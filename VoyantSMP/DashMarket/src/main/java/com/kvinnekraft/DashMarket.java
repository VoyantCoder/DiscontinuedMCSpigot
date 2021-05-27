// Author: Dashie
// Version: 1.0
package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;

public class DashMarket extends JavaPlugin
{
    private String Colorize(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    private void SendLog(final String msg)
    {
        System.out.println("(Java Template): " + msg);
    }

    private void Error(Exception E)
    {
        SendLog("An error has occurred, making the plugin unusable.  I will have to disable myself to save your server unnecessary resources.  Bye! ;c");
        SendLog("If you want to help me solve this issue, perhaps send this:\r\n" + E.getMessage() + "\r\nto my email at KvinneKraft@protonmail.com.");

        inst.getPluginLoader().disablePlugin(inst);
    }


    private FileConfiguration config = null;

    private void LoadConfiguration()
    {
        try
        {
            saveDefaultConfig();

            inst.reloadConfig();
            config = inst.getConfig();

            // Configuration Read-Functionality
        }

        catch (Exception E)
        {
            Error(E);
        }
    }


    class EventHandlers implements Listener
    {
        class Drinks
        {
            // Get Methodology, return all values as preferred.   Instead of using a list, using this.
            // Also, use PotionMeta and set it to the Potion Item.
            //
            // List<Drinks> drinks = new ArrayList<>();
            // drinks.potionName = ;
            // drinks.potionItem = ;
            // drinks.potionColor = ;
            // drinks.effects = ;
            // drinks.particleEffects = ;
            // drinks.soundEffects = ;
            // drinks.add(<Drinks>);
        }
    }


    final JavaPlugin inst = this;

    @Override
    public final void onEnable()
    {
        try
        {
            getServer().getScheduler().runTaskTimerAsynchronously(inst, this::LoadConfiguration, 100, 100);

            LoadConfiguration();

            getServer().getPluginManager().registerEvents(new EventHandlers(), inst);
        }

        catch (Exception E)
        {
            Error(E);
        }
    }

    @Override
    public final void onDisable()
    {
        try
        {
            SendLog("Bye!!");
        }

        catch (Exception E)
        {
            Error(E);
        }
    }
}