package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashPermsX extends JavaPlugin
{
    final JavaPlugin plugin = this;

    private void ShutdownPlugin(final String why)
    {
        getServer().getPluginManager().disablePlugin(plugin);
        print(why);
    }

    final List<String> BlockInteractPermissions = new ArrayList<String>();
    final List<Material> BlockInteractMaterials = new ArrayList<Material>();

    final List<String> ItemInteractPermissions = new ArrayList<String>();
    final List<Material> ItemInteractMaterials = new ArrayList<Material>();

    final List<String> BlockPlacePermissions = new ArrayList<String>();
    final List<Material> BlockPlaceMaterials = new ArrayList<Material>();

    final List<String> BlockBreakPermissions = new ArrayList<String>();
    final List<Material> BlockBreakMaterials = new ArrayList<Material>();

    final List<String> CommandPermissions = new ArrayList<String>();
    final List<String> Commands = new ArrayList<String>();

    final List<String> WordPermissions = new ArrayList<String>();
    final List<String> Words = new ArrayList<String>();

    private void loadSettings()
    {
        final FileConfiguration config = plugin.getConfig();

        try
        {
            final List<String> nodes = Arrays.asList( "block-interact", "block-place", "block-break", "item-use", "commands", "words" );

            for (final String node : nodes)
            {
                if (!config.getBoolean(node + ".enabled"))
                {
                    continue;
                }

                for (final String line : config.getStringList(node + ".permissions"))
                {
                    try
                    {
                        final String permission = line.split(":")[1].toLowerCase();
                        final String appliance = line.split(":")[0].toLowerCase();

                        switch (nodes.indexOf(node) + 1)
                        {
                            case 1:
                                BlockInteractPermissions.add(permission);
                                BlockInteractMaterials.add(Material.valueOf(appliance.toUpperCase()));
                                break;

                            case 2:
                                BlockPlacePermissions.add(permission);
                                BlockPlaceMaterials.add(Material.valueOf(appliance.toUpperCase()));
                                break;

                            case 3:
                                BlockBreakPermissions.add(permission);
                                BlockBreakMaterials.add(Material.valueOf(appliance.toUpperCase()));
                                break;

                            case 4:
                                ItemInteractPermissions.add(permission);
                                ItemInteractMaterials.add(Material.valueOf(appliance.toUpperCase()));
                                break;

                            case 5:
                                CommandPermissions.add(permission);
                                Commands.add(appliance.toLowerCase());
                                break;

                            case 6:
                                WordPermissions.add(permission);
                                Words.add(appliance.toLowerCase());
                                break;
                        }
                    }

                    catch (final Exception e)
                    {
                        print("Invalid configuration node at: " + line);
                        print("Skipping ....");
                    }
                }
            }
        }

        catch (final Exception e)
        {
            throw e;
        }
    }

    public final class EventListener implements Listener
    {
        @EventHandler  public final void onPlayerInteract(final PlayerInteractEvent e)
        {
            if (!isEnabled)
            {
                return;
            }

            final Player p = e.getPlayer();

            if (ItemInteractMaterials.size() > 0)
            {
                final Material material = p.getInventory().getItemInMainHand().getType();

                if (ItemInteractMaterials.contains(material))
                {
                    if (!p.hasPermission(ItemInteractPermissions.get(ItemInteractMaterials.indexOf(material))))
                    {
                        p.sendMessage(color("&e>>> &cYou are not allowed to use this item!"));
                        e.setCancelled(true);

                        return;
                    }
                }
            }

            if (BlockInteractMaterials.size() > 0)
            {
                final Block block = e.getClickedBlock();

                if (block != null)
                {
                    final Material material = block.getType();

                    if (BlockInteractMaterials.contains(material))
                    {
                        if (!p.hasPermission(BlockInteractPermissions.get(BlockInteractMaterials.indexOf(material))))
                        {
                            p.sendMessage(color("&e>>> &cYou are not allowed to interact with this block!"));
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }

        @EventHandler public final void onPlayerBlockPlace(final BlockPlaceEvent e)
        {
            if (!isEnabled)
            {
                return;
            }

            final Player p = e.getPlayer();

            if (BlockPlaceMaterials.size() > 0)
            {
                final Material material = e.getBlock().getType();

                if (BlockPlaceMaterials.contains(material))
                {
                    if (!p.hasPermission(BlockPlacePermissions.get(BlockPlaceMaterials.indexOf(material))))
                    {
                        p.sendMessage(color("&e>>> &cYou are not allowed to place this block!"));
                        e.setCancelled(true);
                    }
                }
            }
        }
// I am not even trying....
        @EventHandler public final void onPlayerBlockBreak(final BlockBreakEvent e)
        {
            if (!isEnabled)
            {
                return;
            }

            final Player p = e.getPlayer();

            if (BlockBreakMaterials.size() > 0)
            {
                final Material material = e.getBlock().getType();

                if (BlockBreakMaterials.contains(material))
                {
                    if (!p.hasPermission(BlockBreakPermissions.get(BlockBreakMaterials.indexOf(material))))
                    {
                        p.sendMessage(color("&e>>> &cYou are not allowed to break this block!"));
                        e.setCancelled(true);
                    }
                }
            }
        }

        @EventHandler public final void onPlayerCommand(final PlayerCommandPreprocessEvent e)
        {
            if (!isEnabled)
            {
                return;
            }

            final Player p = e.getPlayer();

            if (Commands.size() > 0)
            {
                final String command = e.getMessage().split(" ")[0].toLowerCase();

                if (Commands.contains(command) || Commands.contains("/" + command))
                {
                    if (!p.hasPermission(CommandPermissions.get(Commands.indexOf(command))))
                    {
                        p.sendMessage(color("&e>>> &cYou are not allowed to execute this command!"));
                        e.setCancelled(true);
                    }
                }
            }
        }

        @EventHandler public final void onPlayerMessage(final AsyncPlayerChatEvent e)
        {
            if (!isEnabled)
            {
                return;
            }

            final Player p = e.getPlayer();

            if (Words.size() > 0)
            {
                for (String word : e.getMessage().split(" "))
                {
                    word = word.toLowerCase();

                    if (Words.contains(word))
                    {
                        if (!p.hasPermission(WordPermissions.get(Words.indexOf(word))))
                        {
                            p.sendMessage(color("&6>>> &cYou are not allowed to use this word!"));
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    boolean mustAutoReload = false;
    Integer reloadInterval = 5;

    @Override public final void onEnable()
    {
        try
        {
            saveDefaultConfig();;

            final FileConfiguration config = getConfig();

            mustAutoReload = config.getBoolean("properties.auto-reload");

            if (mustAutoReload)
            {
                try
                {
                    reloadInterval = config.getInt("properties.reload-interval") * 20;

                    getServer().getScheduler().runTaskTimerAsynchronously
                    (
                        plugin,

                        this::loadSettings,

                        reloadInterval,
                        reloadInterval
                    );
                }

                catch (final Exception e)
                {
                    ShutdownPlugin("An error occurred while auto-loading configuration from ~/plugins/DashPermsX/config.yml !");
                }
            }

            else
            {
                loadSettings();
            }

            getServer().getPluginManager().registerEvents(new EventListener(), plugin);
            getCommand("dashpermsx").setExecutor(new CommandListener());
        }

        catch (final Exception e)
        {
            ShutdownPlugin("An error occurred while initializing the plugin.");
        }

        print("Author: Dashie (Coding) & Xemu (For Testing)");
        print("Version: 1.0");
        print("Email: KvinneKraft@protonmail.com");
        print("Github: https://github.com/KvinneKraft");
    }

    public boolean isEnabled = true;

    public final class CommandListener implements CommandExecutor
    {
        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only do this as a player!");
                return false;
            }

            final Player p = (Player) s;

            if (p.isOp())
            {
                if (as.length > 0)
                {
                    if (as[0].equalsIgnoreCase("toggle") || as[0].equalsIgnoreCase("t"))
                    {
                        if (isEnabled)
                        {
                            p.sendMessage(color("&aYou have disabled &dDashPermsX&a!"));
                            isEnabled = false;
                        }

                        else
                        {
                            p.sendMessage(color("&aYou have enabled &dDashPermsX&a!"));
                            isEnabled = true;
                        }

                        return true;
                    }
                }

                p.sendMessage(color("&cDid you perhaps mean &7/dashpermsx toggle &c?"));
                return true;
            }

            p.sendMessage(color("&e>>> &aAuthor: Dashie A.K.A KvinneKraft"));
            p.sendMessage(color("&e>>> &aVersion: 1.0"));
            p.sendMessage(color("&e>>> &aEmail: KvinneKraft@protonmail.com"));
            p.sendMessage(color("&e>>> &aGithub: https://github.com/KvinneKraft"));

            return true;
        }
    }

    @Override public final void onDisable()
    {
        getServer().getScheduler().cancelTasks(plugin);
        print("I am dead.");
    }

    final String color(String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    final void print(String data)
    {
        System.out.println("(DashPermsX): " + data);
    }
}