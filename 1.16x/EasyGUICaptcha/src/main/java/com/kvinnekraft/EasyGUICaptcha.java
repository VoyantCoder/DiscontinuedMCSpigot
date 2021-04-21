
package com.kvinnekraft;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

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
    {
        //Interface:
        public final List<ItemStack> guiOtherItems = new ArrayList<>();
        public final List<ItemStack> guiKeyItems = new ArrayList<>();
        public String title = "";
        //IP Lock:
        public boolean hasIpLock = false;
        //Attempts:
        public boolean notifyStaff = false;
        public int disallowDuration = 30;
        public int maximumAttempts = 3;
        //Restrictions:
        public boolean preventInventoryInteract = false;
        public boolean preventItemDrop = false;
        public boolean preventMovement = false;
        public boolean preventDamage = false;
        public boolean preventChat = false;
        //Potion effects:
        public final List<PotionEffect> joinPotionEffects = new ArrayList<>();
        //On complete:
        public List<String> completeCommands = new ArrayList<>();
        public String lightningPermission = "";
        public String fireworkPermission = "";
        public String soundPermission = "";
        public String completeMessage = "";
        public boolean sendCompleteTitle = false;
        public boolean hasLightning = false;
        public boolean hasFirework = false;
        public Sound completeSound = null;
    }


    FileConfiguration Config = null;

    private boolean isItem(final String Item)
    {
        try
        {
            Material.valueOf(Item);
            return true;
        }

        catch (final Exception E)//Not used;
        {
            return false;
        }
    }

    private boolean getBoolean(final String Node, final String Entry)
    {
        return Config.getBoolean(Node + Entry);
    }

    private int getInt(final String Node, final String Entry)
    {
        return Config.getInt(Node + Entry);
    }

    private List<String> getStringList(final String Node, final String Entry)
    {
        return Config.getStringList(Node + Entry);
    }

    private String getString(final String Node, final String Entry)
    {
        return Config.getString(Node + Entry);
    }

    private void ReloadConfiguration()
    {
        try
        {
            saveDefaultConfig();

            Parent.reloadConfig();
            Config = Parent.getConfig();

            Setting.joinPotionEffects.clear();
            Setting.completeCommands.clear();
            Setting.guiOtherItems.clear();
            Setting.guiKeyItems.clear();

            String node = ("captcha-settings.interface.");

            Setting.title = (Colorize(getString(node, "title")));

            final String otherDisplayName = (Colorize(getString(node, "other-items.display-name")));
            final String otherLore = (Colorize(getString(node, "other-items.lore")));

            final String keyDisplayName = (Colorize(getString(node, "key-items.display-name")));
            final String keyLore = (Colorize(getString(node, "key-items.lore")));

            final String[] itemSections = new String[]
            { "key-items.", "other-items." };

            for (final String section : itemSections)
            {
                for (String item : Config.getStringList(node + section + ".items"))
                {
                    if (isItem(item))
                    {
                        final ItemStack Stack = new ItemStack(Material.valueOf(item), 1);
                        final ItemMeta StackMeta = Stack.getItemMeta();

                        StackMeta.setDisplayName((section.equals("key-items.") ? keyDisplayName : otherDisplayName));
                        StackMeta.setLore(Arrays.asList((section.equals("key-items.") ? keyLore : otherLore)));

                        Stack.setItemMeta(StackMeta);

                        if (!section.equals("key-items."))
                        {
                            Setting.guiOtherItems.add(Stack);
                        }

                        else
                        {
                            Setting.guiKeyItems.add(Stack);
                        }
                    }

                    else
                    {
                        SendLog("Found invalid item at: " + node + section + ".items ->" + item);
                    }
                }
            }

            node = ("captcha-settings.ip-lock.");

            Setting.hasIpLock = getBoolean(node, "enabled");

            node = ("captcha-settings.attempts.");

            Setting.disallowDuration = (getInt(node, "disallow-duration")) * 20;
            Setting.maximumAttempts = (getInt(node, "maximum-tries"));
            Setting.notifyStaff = (getBoolean(node, "notify-staff"));

            node = ("captcha-settings.restrictions.");

            Setting.preventInventoryInteract = (getBoolean(node, "inventory-interaction"));
            Setting.preventItemDrop = (getBoolean(node, "item-drop"));
            Setting.preventMovement = (getBoolean(node, "movement"));
            Setting.preventDamage = (getBoolean(node, "damage"));
            Setting.preventChat = (getBoolean(node, "chat"));

            node = ("captcha-settings.potion-effects.");

            if (getBoolean(node, "enabled"))
            {
                for (String item : getStringList(node, "effects"))
                {
                    try
                    {
                        final PotionEffectType effectType = PotionEffectType.getByName(item);

                        if (effectType == null)
                        {
                            throw (new Exception("!"));
                        }

                        final PotionEffect potionEffect = new PotionEffect(effectType, 10000, 20);

                        Setting.joinPotionEffects.add(potionEffect);
                    }

                    catch (final Exception E)
                    {
                        SendLog("Unable to solve potion id: " + item);
                    }
                }
            }

            node = ("captcha-settings.on-complete.messages.");

            Setting.sendCompleteTitle = (getBoolean(node, "send-title"));
            Setting.completeMessage = (getString(node, "message"));

            node = ("captcha-settings.on-complete.");

            Setting.completeCommands = (getStringList(node, "commands"));

            node = ("captcha-settings.on-complete.sound.");

            if (getBoolean(node, "enabled"))
            {
                try
                {
                    Setting.completeSound = Sound.valueOf(getString(node, "sound-id"));
                    Setting.soundPermission = getString(node, "permission");
                }

                catch (final Exception E)
                {
                    SendLog("Unable to solve completion sound id.");
                }
            }

            node = ("captcha-settings.on-complete.misc.");

            Setting.lightningPermission = getString(node, "lightning-permission");
            Setting.fireworkPermission = getString(node, "firework-permission");
            Setting.hasLightning = getBoolean(node, "lightning-effect");
            Setting.hasFirework = getBoolean(node, "firework-effect");
        }

        catch (final Exception E)
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

        catch (final Exception E)
        {
            ErrorHandler(E);
        }
    }


    final class EventHandlers implements Listener
    {
        final Random rand = new Random();

        private ItemStack getRandomOtherItem()
        {
            final int id = rand.nextInt(Setting.guiOtherItems.size());
            return Setting.guiOtherItems.get(id);
        }

        private ItemStack getRandomKeyItem()
        {
            final int id = rand.nextInt(Setting.guiKeyItems.size());
            return Setting.guiKeyItems.get(id);
        }


        final HashMap<Player, Inventory> playerInventories = new HashMap<>();
        final HashMap<Player, ItemStack> playerKeys = new HashMap<>();
        final HashMap<Player, Integer> playerTries = new HashMap<>();
        final HashMap<Player, String> playerCache = new HashMap<>();

        private void ResetPlayer(final Player p)
        {
            playerInventories.remove(p);
            playerCache.remove(p);
            playerTries.remove(p);
            playerKeys.remove(p);
        }

        private Boolean hasPerm(final Player player, final String permission)
        {
            return player.hasPermission(permission);
        }

        private void SuccessHandler(final Player p)
        {
            ResetPlayer(p);

            if (Setting.sendCompleteTitle)
            {
                p.sendTitle(Setting.completeMessage, "", 10, 100, 10);
            }

            else
            {
                p.sendMessage(Setting.completeMessage);
            }

            if (Setting.completeCommands.size() > 0)
            {
                final ConsoleCommandSender sender = getServer().getConsoleSender();

                for (final String command : Setting.completeCommands)
                {
                    getServer().dispatchCommand(sender, command);
                }
            }

            if (Setting.joinPotionEffects.size() > 0)
            {
                for (final PotionEffect effect : Setting.joinPotionEffects)
                {
                    p.removePotionEffect(effect.getType());
                }
            }

            final Location location = p.getLocation();

            if (hasPerm(p, Setting.lightningPermission))
            {
                location.getWorld().strikeLightning(location);
            }

            if (hasPerm(p, Setting.fireworkPermission))
            {
            }

            if (hasPerm(p, Setting.soundPermission))
            {
                if (Setting.completeSound != null)
                {
                    p.playSound(location, Setting.completeSound, 30, 1);
                }
            }

            // send message; as title?
            // execute commands;
            // remove potion effects;
            // handle fireworks and lightning;
            // sound? check for permissions yeh!
            // Scroll around for more notes;
        }

        private void ReplenishCaptcha(final Player p)
        {
            final Inventory GUI = getServer().createInventory(null, 27, Setting.title);

            for (int s = 0; s < 27; s += 1)
            {
                if ((s < 10) || (s > 16))
                {
                    GUI.setItem(s, getRandomOtherItem());
                }
            }

            final ItemStack keyItem = getRandomKeyItem();

            playerKeys.put(p, keyItem);

            for (int s = 10; s <= 17; s += 1)
            {
                final ItemStack item = getRandomKeyItem();

                if (!item.getType().equals(keyItem.getType()))
                {
                    GUI.setItem(s, getRandomKeyItem());
                    continue;
                }

                s -= 1;
            }

            playerInventories.put(p, GUI);
        }

        private String getPlayerIP(final Player p)
        {
            final InetSocketAddress IP = p.getAddress();

            if (IP == null || IP.getAddress() == null)
            {
                return "none";
            }

            return (IP.getAddress().toString());
        }

        private Inventory getInventory(final Player p)
        {
            return playerInventories.get(p);
        }

        private void activateCaptchaPopup(final Player p)
        {
            getServer().getScheduler().runTaskLater
            (
                Parent,

                () ->
                {
                    if (p.isOnline())
                    {
                        ReplenishCaptcha(p);
                        p.openInventory(getInventory(p));
                    }
                },

                5
            );
        }


        @EventHandler
        public void PlayerInventoryClick(final InventoryClickEvent E)
        {
            if ((E.getViewers().size() < 1) || !(E.getViewers().get(0) instanceof Player))
            {
                return;
            }

            final Player p = (Player) E.getViewers().get(0);

            if (!playerCache.containsKey(p))
            {
                return;
            }

            final ItemStack Item = E.getCurrentItem();

            if (Item != null)
            {
                if (playerKeys.get(p).getType() != Item.getType())
                {
                    playerTries.put(p, playerTries.get(p) + 1);
                }

                else
                {
                    SuccessHandler(p);
                }
            }

            if (playerTries.get(p) >= Setting.maximumAttempts)
            {
                p.kick(Component.text(Colorize("&cYou have exceeded maximum attempts!")));

                // Notify Staff
                // Keep disconnected for disallow duration; add to list, on connect, disconnect if on list; after duration remove from list.
            }
        }


        @EventHandler
        public void PlayerJoinEvent(final PlayerJoinEvent E)
        {
            final Player p = E.getPlayer();

            if (!playerCache.containsKey(p))
            {
                if (playerCache.containsValue(getPlayerIP(p)))
                {
                    if (Setting.hasIpLock)
                    {
                        p.kick(Component.text(Colorize("&cSomeone is already being authenticated using your network.  Please wait.")));
                    }
                }

                playerCache.put(p, getPlayerIP(p));
            }

            // Apply potion effects

            activateCaptchaPopup(p);
        }


        @EventHandler
        public void PlayerOnChat(final AsyncChatEvent E)
        {
            final Player p = E.getPlayer();

            if (playerCache.containsKey(p))
            {
                if (Setting.preventChat)
                {
                    E.setCancelled(true);
                }
            }
        }


        @EventHandler
        public void PlayerOnCommand(final PlayerCommandPreprocessEvent E)
        {
            final Player p = (Player) E.getPlayer();

            if (playerCache.containsKey(p))
            {
                if (Setting.preventChat)
                {
                    E.setCancelled(true);
                }
            }
        }


        @EventHandler
        public void PlayerMovement(final PlayerMoveEvent E)
        {
            final Player p = E.getPlayer();

            if (playerCache.containsKey(p))
            {
                if (Setting.preventMovement)
                {
                    E.setCancelled(true);
                }
            }
        }


        @EventHandler
        public void PlayerItemDrop(final PlayerDropItemEvent E)
        {
            final Player p = E.getPlayer();

            if (playerCache.containsKey(p))
            {
                if (Setting.preventItemDrop)
                {
                    E.setCancelled(true);
                }
            }
        }


        @EventHandler
        public void PlayerInventoryInteract(final InventoryInteractEvent E)
        {
            if ((E.getViewers().size() < 1) || !(E.getViewers().get(0) instanceof Player))
            {
                return;
            }

            final Player p = (Player) E.getViewers().get(0);

            if (playerCache.containsKey(p))
            {
                if (playerInventories.get(p) == E.getInventory())
                {
                    E.setCancelled(true);
                }

                else if (Setting.preventInventoryInteract)
                {
                    E.setCancelled(true);
                }
            }
        }


        @EventHandler
        public void PlayerInventoryClose(final InventoryCloseEvent E)
        {
            if ((E.getViewers().size() < 1) || !(E.getViewers().get(0) instanceof Player))
            {
                return;
            }

            final Player p = (Player) E.getViewers().get(0);

            if (playerCache.containsKey(p))
            {
                activateCaptchaPopup(p);
            }
        }


        @EventHandler
        public void EntityDamageEvent(final EntityDamageByEntityEvent E)
        {
            if (!(E.getDamager() instanceof Player))
            {
                return;
            }

            final Player p = (Player) E.getDamager();

            if (playerCache.containsKey(p))
            {
                if (Setting.preventDamage)
                {
                    E.setCancelled(true);
                }
            }
        }


        @EventHandler
        public void PlayerQuitEvent(final PlayerQuitEvent E)
        {

        }
    }


    @Override
    public void onEnable()
    {
        SendLog("Plugin is being enabled.");

        try
        {
            StartAutoReload();

            getServer().getPluginManager().registerEvents(new EventHandlers(), Parent);
        }

        catch (final Exception E)
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

    private String Colorize(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    private void SendLog(final String data)
    {
        System.out.println("(EasyGUICaptcha): " + data);
    }
}