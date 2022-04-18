package ${PACKAGE};

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ${CLASS_NAME} extends JavaPlugin
{
    final JavaPlugin plugin = this;

    private void loadSettings()
    {
        saveDefaultConfig();

        try
        {
            final FileConfiguration config = getConfig();

            
        }

        catch (final Exception e)
        {
            shutdownPlugin("Unable to read config.yml. Shutting down ....");
        }
    }

    boolean autoReload = true;
    int reloadInterval = 5;

    @Override public final void onEnable()
    {
        saveDefaultConfig();

        try
        {
            final FileConfiguration config = getConfig();

            if (autoReload)
            {
                reloadInterval = config.getInt("startup-tweaks.reload-interval") * 20;

                getServer().getScheduler().runTaskTimerAsynchronously
                (
                    plugin,

                    this::loadSettings,

                    reloadInterval,
                    reloadInterval
                );
            }

            else
            {
                loadSettings();
            }
        }

        catch (final Exception e)
        {
            shutdownPlugin("The plugin failed to initialize.  Shutting down ....");
        }

        print("Author: Dashie");
        print("Version: 1.0");
        print("Github: https://github.com/KvinneKraft");
        print("Email: KvinneKraft@protonmail.com");
    }

    private void shutdownPlugin(final String reason)
    {
        print(reason);
        getServer().getPluginManager().disablePlugin(plugin);
    }

    @Override public final void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("I am dead!");
    }

    private String color(final String data) {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    private void print(final String data) {
        System.out.println("(No Store X): " + data);
    }
}
