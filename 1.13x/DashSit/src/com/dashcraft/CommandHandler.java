
package DashSit.src.com.dashcraft;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor
{    
    Luna luna = new Luna();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String arg, String[] args)
    {
        if(!(sender instanceof Player))
        {
            Luna.print("You may only use this in-game.");
            return false;
        };
        
        Player p = (Player)sender;
        
        Location location = p.getLocation();
        location.setY(location.getBlockY()-1.7);        
        
        luna.SitDown(p, location);
        
        return true;
    };
};
