
package com.kvinnekraft;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class EasyGUICaptcha extends JavaPlugin
{
    private void ErrorHandler(Exception E)
    {
        final String ErrorFormat = ("(EasyGUICaptcha): An error has occurred: \r\n" + E.getMessage() + "\r\n" + E.getCause().toString());

        System.out.println(ErrorFormat);

        getServer().getPluginManager().disablePlugin(this);
    }


    final Configuration Setting = new Configuration();
    final JavaPlugin Parent = this;

    class Configuration
    {//Determine true or false by null values.
        //Interface:
        final List<ItemStack> guiOtherItems = new ArrayList<>();
        final List<ItemStack> guiKeyItems = new ArrayList<>();
        String title = "";
        //IP Lock:
        boolean hasIpLock = false;
        int cacheDuration = 360;
        //Attempts:
        boolean disallowAccess = false;
        boolean notifyStaff = false;
        boolean kickPlayer = false;
        int disallowDuration = 30;
        int maximumAttempts = 3;
        //Restrictions:
        boolean preventInventoryInteract = false;
        boolean preventItemDrop = false;
        boolean preventMovement = false;
        boolean preventDamage = false;
        boolean preventChat = false;
        //Potion effects:
        final List<PotionEffect> joinPotionEffects = new ArrayList<>();
        //On complete:
        List<String> completeCommands = new ArrayList<>();
        String lightningPermission = "";
        String fireworkPermission = "";
        String soundPermission = "";
        String completeMessage = "";
        boolean hasLightning = false;
        boolean hasFirework = false;
        Sound completeSound = null;
    }

    FileConfiguration Config = null;

    private void ReloadConfiguration()
    {
        try
        {

        }

        catch (Exception E)
        {
            ErrorHandler(E);
        }
    }

    private void StartAutoReload()
    {
        try
        {
            getServer().getScheduler().runTaskTimerAsynchronously
            (
                Parent,

                this::ReloadConfiguration,

                100,
                100
            );
        }

        catch (Exception E)
        {
            ErrorHandler(E);
        }
    }

    @Override
    public void onEnable()
    {
        SendLog("Plugin is being enabled.");

        try
        {
            StartAutoReload();
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