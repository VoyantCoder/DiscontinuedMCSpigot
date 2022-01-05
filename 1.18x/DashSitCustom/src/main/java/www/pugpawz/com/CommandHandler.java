package www.pugpawz.com;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class CommandHandler implements CommandExecutor {
    @Override public final boolean onCommand(final CommandSender s, final Command c, final String l, final String[] args) {
        if (!(s instanceof Player)) {
            Base.Print("Only players can use this command.");
        }
        else {
            Sit.PlayerSit((Player)s, true, ((Player)s).getLocation(), null);
        }
        return true;
    }
}
