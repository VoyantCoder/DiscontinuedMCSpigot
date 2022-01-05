package www.pugpawz.com;


import org.bukkit.plugin.java.JavaPlugin;


public final class Index extends JavaPlugin {
    @Override public void onEnable() {
        Base.instance = this;

        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getCommand("sit").setExecutor(new CommandHandler());
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> { Base.GetConfigData(); }, 0,200);

        Base.Print("I am up and running!");
    }


    @Override public void onDisable() {
        Base.Print("I am no longer up and running!");
    }
}
