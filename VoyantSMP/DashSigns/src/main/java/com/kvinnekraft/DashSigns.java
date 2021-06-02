// Author: Dashie
// Version: 1.0

package com.kvinnekraft;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DashSigns extends JavaPlugin implements SeparateMethods, Configurator, EventsHandler, CommandsHandler
{
    final String color(final String data) { return plugin.color(data); }
    final void sendLog(final String data) { plugin.sendLog(data); }

    FileConfiguration configInst = this.getConfig();

    @Override
    public void onEnable()
    {
        config.LoadConfiguration(this, configInst);

        getServer().getPluginManager().registerEvents(new EventsHandler.Hook(), this);
        getCommand("DashSigns").setExecutor(new CommandsHandler.Hook());

        getServer().getScheduler().runTaskTimer(this, () -> config.LoadConfiguration(this, configInst), 160, 160);

        plugin.sendLog(plugin.color("Plugin is now running."));
    }

    @Override
    public void onDisable()
    {

    }
}