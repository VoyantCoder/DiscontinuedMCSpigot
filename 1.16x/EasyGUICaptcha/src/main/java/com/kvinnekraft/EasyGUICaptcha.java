
package com.kvinnekraft;

import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.net.InetSocketAddress;
import java.util.*;

@SuppressWarnings("ALL")
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
        public int attemptTime = 30;
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
            Setting.notifyStaff = (getBoolean(node, "notify-ops"));
            Setting.attemptTime = (getInt(node, "attempt-time")) * 20;

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
            Setting.completeMessage = (Colorize(getString(node, "message")));

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

            ReloadConfiguration();
        }

        catch (final Exception E)
        {
            ErrorHandler(E);
        }
    }


    class EventHandlers implements Listener
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


        final HashMap<UUID, Inventory> playerInventories = new HashMap<>();
        final HashMap<Player, ItemStack> playerKeys = new HashMap<>();
        final HashMap<Player, Integer> playerTries = new HashMap<>();
        final HashMap<UUID, String> playerCache = new HashMap<>();

        private void ResetPlayer(final Player p)
        {
            final String ip = getPlayerIP(p);

            playerInventories.remove(p.getUniqueId());
            playerCache.remove(p.getUniqueId());
            playerTries.remove(p);
            playerKeys.remove(p);
        }

        private Boolean hasPerm(final Player player, final String permission)
        {
            return player.hasPermission(permission);
        }

        private final List<FireworkEffect.Type> fireworkTypes = new ArrayList<>();

        private FireworkEffect.Type getFireworkType()
        {
            if (fireworkTypes.size() < 1)
            {
                fireworkTypes.addAll
                (
                    Arrays.asList
                    (
                        FireworkEffect.Type.BALL_LARGE,
                        FireworkEffect.Type.BALL,
                        FireworkEffect.Type.STAR,
                        FireworkEffect.Type.BURST
                    )
                );
            }

            return (fireworkTypes.get(rand.nextInt(fireworkTypes.size())));
        }

        private FireworkEffect getFireworkEffect()
        {
            final int r = rand.nextInt(255);
            final int g = rand.nextInt(255);
            final int b = rand.nextInt(255);

            final Color fireworkColor = Color.fromRGB(r, g, b);

            return FireworkEffect.builder()
                    .withColor(fireworkColor)
                    .withFlicker().withTrail()
                    .with(getFireworkType())
                    .flicker(true).build();
        }

        private void SuccessHandler(final Player p)
        {
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
                final Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                final FireworkMeta fireworkMeta = firework.getFireworkMeta();

                fireworkMeta.addEffect(getFireworkEffect());
                fireworkMeta.setPower(0);

                firework.setFireworkMeta(fireworkMeta);

                p.setInvulnerable(true);

                firework.detonate();

                p.setInvulnerable(false);
            }

            if (hasPerm(p, Setting.soundPermission))
            {
                if (Setting.completeSound != null)
                {
                    p.playSound(location, Setting.completeSound, 30, 1);
                }
            }

            ResetPlayer(p);
            p.closeInventory();
        }

        private void ReplenishCaptcha(final Player p)
        {
            final ItemStack keyItem = getRandomKeyItem();

            playerKeys.put(p, keyItem);

            final String title = Setting.title.replace("{key}", keyItem.getType().toString());
            final Inventory GUI = getServer().createInventory(null, 27, title);

            for (int s = 0; s < 27; s += 1)
            {
                if ((s < 10) || (s > 16))
                {
                    GUI.setItem(s, getRandomOtherItem());
                }
            }

            final int u = rand.nextInt(7) + 10;

            for (int s = 10; s < 17; s += 1)
            {
                final ItemStack item = getRandomKeyItem();

                if (s == u)
                {
                    GUI.setItem(s, keyItem);
                    continue;
                }

                else if (!item.getType().equals(keyItem.getType()))
                {
                    GUI.setItem(s, item);
                    continue;
                }

                s -= 1;
            }

            playerInventories.put(p.getUniqueId(), GUI);
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
            return playerInventories.get(p.getUniqueId());
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


        final List<UUID> blockQueue = new ArrayList<>();


        @EventHandler
        public void PlayerAuthenticateEvent(final PlayerLoginEvent E)
        {
            final Player p = E.getPlayer();

            if (blockQueue.contains(p.getUniqueId()))
            {
                E.disallow(PlayerLoginEvent.Result.KICK_OTHER, (Colorize("&cPlease wait before connecting again.")));
            }
        }


        final List<UUID> joinQueue = new ArrayList<>();

        @EventHandler
        public void PlayerJoinEvent(final PlayerJoinEvent E)
        {
            final Player p = E.getPlayer();

            if (!playerCache.containsKey(p.getUniqueId()))
            {
                if (playerCache.containsValue(getPlayerIP(p)))
                {
                    if (Setting.hasIpLock)
                    {
                        p.kickPlayer(Colorize("&cSomeone is already being authenticated using your network.  Please wait."));
                    }
                }

                playerCache.put(p.getUniqueId(), getPlayerIP(p));
            }

            if (Setting.joinPotionEffects.size() > 0)
            {
                p.addPotionEffects(Setting.joinPotionEffects);
            }

            if (!joinQueue.contains(p.getUniqueId()))
            {
                joinQueue.add(p.getUniqueId());

                getServer().getScheduler().runTaskLater
                (
                    Parent,

                    () ->
                    {
                        if (joinQueue.contains(p.getUniqueId()))
                        {
                            p.kickPlayer((Colorize("&cYou took too long.")));
                            joinQueue.remove(p.getUniqueId());
                        }
                    },

                    Setting.attemptTime
                );
            }

            activateCaptchaPopup(p);
        }


        @EventHandler
        public void PlayerQuit(final PlayerQuitEvent E)
        {
            final Player p = E.getPlayer();

            if (joinQueue.contains(p.getUniqueId()))
            {
                joinQueue.remove(p.getUniqueId());
            }
        }


        @EventHandler
        public void PlayerOnChat(final AsyncPlayerChatEvent E)
        {
            final Player p = E.getPlayer();

            if (playerCache.containsKey(p.getUniqueId()))
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
            final Player p = E.getPlayer();

            if (playerCache.containsKey(p.getUniqueId()))
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

            if (playerCache.containsKey(p.getUniqueId()))
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

            if (playerCache.containsKey(p.getUniqueId()))
            {
                if (Setting.preventItemDrop)
                {
                    E.setCancelled(true);
                }
            }
        }


        @EventHandler
        public void PlayerInventoryClick(final InventoryClickEvent E)
        {
            if ((E.getViewers().size() < 1) || !(E.getViewers().get(0) instanceof Player))
            {
                return;
            }

            final Player p = (Player) E.getViewers().get(0);

            if (!playerCache.containsKey(p.getUniqueId()))
            {
                return;
            }

            final ItemStack Item = E.getCurrentItem();

            if (Item != null)
            {
                if (playerKeys.get(p).getType() != Item.getType())
                {
                    int c = 1;

                    if (playerTries.containsKey(p))
                    {
                        c = playerTries.get(p) + 1;
                    }

                    playerTries.put(p, c);

                    if (playerTries.get(p) >= Setting.maximumAttempts)
                    {
                        p.kickPlayer(Colorize("&cYou have exceeded maximum attempts!"));

                        getServer().getScheduler().runTaskAsynchronously
                        (
                            Parent,

                            () ->
                            {
                                final String format = (Colorize("&7&o" + p.getName() + " >>> exceeded captcha tries!"));

                                for (final Player sp : getServer().getOnlinePlayers())
                                {
                                    if (p.isOp() || hasPerm(p, "*") || hasPerm(p, "*.*"))
                                    {
                                        sp.sendMessage(format);
                                    }
                                }
                            }
                        );

                        if (Setting.disallowDuration > 1)
                        {
                            final int Duration = Setting.disallowDuration;

                            blockQueue.add(p.getUniqueId());

                            getServer().getScheduler().runTaskLater
                            (
                                Parent,

                                () -> blockQueue.remove(p.getUniqueId()),

                                Duration
                            );
                        }
                    }
                }

                else
                {
                    SuccessHandler(p);
                }
            }

            if (playerCache.containsKey(p.getUniqueId()))
            {
                if (playerInventories.get(p.getUniqueId()) == E.getInventory())
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

            if (playerCache.containsKey(p.getUniqueId()))
            {
                activateCaptchaPopup(p);
            }
        }


        @EventHandler
        public void PlayerDeath(final PlayerDeathEvent E)
        {
            final Player p = E.getEntity();

            if (playerCache.containsKey(p.getUniqueId()))
            {
                activateCaptchaPopup(p);
            }
        }


        @EventHandler
        public void PlayerTeleport(final PlayerTeleportEvent E)
        {
            final Player p = E.getPlayer();

            if (playerCache.containsKey(p.getUniqueId()))
            {
                activateCaptchaPopup(p);
            }
        }


        @EventHandler
        public void EntityDamageEvent(final EntityDamageByEntityEvent E)
        {
            if (!(E.getEntity() instanceof Player))
            {
                return;
            }

            final Player p = (Player) E.getEntity();

            if (playerCache.containsKey(p.getUniqueId()))
            {
                if (Setting.preventDamage)
                {
                    E.setCancelled(true);
                }
            }
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