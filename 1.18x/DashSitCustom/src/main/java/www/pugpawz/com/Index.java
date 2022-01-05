package www.pugpawz.com;


import org.bukkit.plugin.java.JavaPlugin;


public final class Index extends JavaPlugin {
    @Override public void onEnable() {
        this.saveDefaultConfig();

        getCommand("sit").setExecutor(new CommandHandler());
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> Base.GetConfigData(this), 0,200);

        Base.Print("I am up and running!");
    }


    @Override public void onDisable() {
        Base.Print("I am no longer up and running!");
    }
}
