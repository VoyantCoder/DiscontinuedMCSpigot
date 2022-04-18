package dash.recoded;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

// I am very busy with life right now so, yes, updates are coming through quite slowely, but do not worry
// they will be shooting all over the place like a turd flying out of my anus when I am back at my computer
// like I was before I got busy.
//

public class BetterSpawn extends JavaPlugin
{
    @Override public void onEnable()
    {
        print("I am crawling up ....");

        LoadConfiguration();

        print("I am now alive!");
    };

    FileConfiguration config = (FileConfiguration) null;
    final JavaPlugin plugin = (JavaPlugin) this;

    void LoadConfiguration()
    {
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();


    };

    class Events implements Listener
    {
        @EventHandler public void onPlayerJoin(final PlayerJoinEvent e)
        {
            // Join Message, Teleport to spawn, always teleport to spawn, effects, first gifts, rewards
        };

        @EventHandler public void onPlayerQuit(final PlayerQuitEvent e)
        {
            // Quit Message
        };

        @EventHandler public void onPlayerDeath(final PlayerDeathEvent e)
        {
            // Teleport to Spawn or player home if exists
        };
    };

    class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("Executor != Player == true");
                print("Access Denied!");
                
                return false;
            };

            final Player p = (Player) s;
            
            // SetSpawn, GoSpawn & Spawn and Reload

            return true;
        };
    };

    @Override public void onDisable()
    {
        print("Oh, I am now dead!");
    };

    void print(final String line)
    {
        System.out.println("(Better Spawn): " + line);
    };

    String color(final String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
};