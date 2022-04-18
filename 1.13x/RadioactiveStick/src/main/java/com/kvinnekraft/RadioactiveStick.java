
// Big Dashie Code
// 1.0 >


package com.kvinnekraft;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;


public class RadioactiveStick extends JavaPlugin
{
    boolean useRequiresOP = false;
    boolean getRequiresOP = false;

    boolean autoReload = false;

    final JavaPlugin plugin = this;

    @Override public final void onEnable()
    {
        print("Author: Dashie A.K.A KvinneKraft");
        print("Version: 1.0");
        print("Github: https://github.com/KvinneKraft");
        print("Email: KvinneKraft@protonmail.com");

        try
        {
            saveDefaultConfig();

            final FileConfiguration config = getConfig();

            useRequiresOP = config.getBoolean("properties.use-requires-op");
            getRequiresOP = config.getBoolean("properties.get-requires-op");
            autoReload = config.getBoolean("properties.auto-reload");

            if (autoReload)
            {
                getServer().getScheduler().runTaskTimerAsynchronously(plugin,
                    new Runnable()
                    {
                        @Override public final void run()
                        {
                            final FileConfiguration config = plugin.getConfig();

                            useRequiresOP = config.getBoolean("properties.use-requires-op");
                            getRequiresOP = config.getBoolean("properties.get-requires-op");
                            autoReload = config.getBoolean("properties.auto-reload");
                        }
                    },

                    20, 20
                );
            }

            final ItemMeta meta = RadStick.getItemMeta();

            meta.setDisplayName(color("&b( &e&lR&eadioactive &e&lS&etick &b)"));
            meta.setLore(Arrays.asList("&a&oSome tried but most failed."));
            meta.setCustomModelData(2020);

            RadStick.setItemMeta(meta);

            getServer().getPluginManager().registerEvents(new EventHandler(), plugin);
            getCommand("radioactivestick").setExecutor(new CommandHandler());
        }

        catch (final Exception e)
        {
            getServer().getPluginManager().disablePlugin(this);

            print("Plugin has been disabled due to a error.");
            print("Contact me at KvinneKraft@protonmail.com if you want me to fix this.");
        }
    }

    final ItemStack RadStick = new ItemStack(Material.STICK, 1);

    public class EventHandler implements Listener
    {
        public void DetonateFirework(Location location, Color mcolor, Color fcolor, FireworkEffect.Type type)
        {
            Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta firework_meta = firework.getFireworkMeta();

            firework_meta.addEffect(FireworkEffect.builder().withColor(mcolor).with(type).flicker(true).withFlicker().withTrail().withFade(fcolor).trail(true).build());

            firework.setFireworkMeta(firework_meta);
            firework.detonate();
        }

        @org.bukkit.event.EventHandler final void onPlayerInteract(final PlayerInteractEvent e)
        {
            final Player p = (Player) e.getPlayer();

            if (useRequiresOP)
            {
                if (!p.isOp())
                {
                    return ;
                }
            }

            if (p.getInventory().getItemInMainHand().isSimilar(RadStick))
            {
                if (e.getAction() == Action.RIGHT_CLICK_AIR)
                {
                    final Location loca = p.getTargetBlockExact(250).getLocation();

                    getServer().getScheduler().runTaskLater(plugin,
                        () ->
                        {
                            if (p.isOnline())
                            {
                                p.getWorld().createExplosion(loca, 175, true, true);
                                p.sendMessage(color("&aTango down, tango down!"));
                            }
                        },

                        5 * 20
                    );

                    p.sendMessage(color("&aNuke incoming at x:" + (int) loca.getX() + " y:" + (int) loca.getY() + " z:" + (int) loca.getZ() + " in 5 seconds!"));
                }
            }
        }
    }

    public class CommandHandler implements CommandExecutor
    {
        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only use this as a player.");
                return false;
            }

            final Player p = (Player) s;

            if (getRequiresOP)
            {
                if (!p.isOp())
                {
                    p.sendMessage(color("&cYou may not do this man!"));
                    return false;
                }
            }

            if (as.length >= 2)
            {
                if (as[0].equalsIgnoreCase("give"))
                {
                    Player r = null;

                    if (as.length >= 3)
                    {
                        r = Bukkit.getPlayerExact(as[1]);

                        if (r == null)
                        {
                            p.sendMessage(color("&cYou must specify a online player!"));
                            return false;
                        }
                    }

                    int amount = 1;

                    try
                    {
                        int i = 1;

                        if (r != null) i += 1;
                        else r = p;

                        amount = Integer.parseInt(as[i]);

                        if (amount < 1 || amount > 60000)
                        {
                            throw new Exception("!");
                        }
                    }

                    catch (final Exception e)
                    {
                        p.sendMessage(color("&cYou must specify a valid integral value."));
                        return false;
                    }

                    RadStick.setAmount(amount);
                    r.getInventory().addItem(RadStick);

                    if (r != p)
                    {
                        r.sendMessage(color("&aYou have been given a Radioactive Stick!"));
                        p.sendMessage(color("&aYou have given &e" + r.getName() + " &6x&e" + amount + " &aRadioactive Sticks!"));
                    }

                    else
                    {
                        p.sendMessage(color("&aYou have given yourself the Radioactive Stick!"));
                    }

                    return true;
                }
            }

            p.sendMessage(color("&cCorrect usage: &7/radstick give [player] [amount]"));
            return false;
        }
    }

    @Override public final void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        print("You have disabled me!");
    }

    final void print(String d)
    {
        System.out.println("(Radioactive Stick): " + d);
    }

    final String color(String d)
    {
        return ChatColor.translateAlternateColorCodes('&', d);
    }
}