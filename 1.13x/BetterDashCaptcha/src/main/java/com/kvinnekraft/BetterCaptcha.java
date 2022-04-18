
// Author: Dashie
// Version: 2.1

package com.kvinnekraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class BetterCaptcha extends JavaPlugin
{
    FileConfiguration config = null;
    JavaPlugin plugin = null;
    
    protected static final class Fireworks
    {       
        static boolean do_fireworks, random_firework_types, random_firework_color;        
        
        static final List<FireworkEffect.Type> firework_types = new ArrayList<>();
        static final List<Color> rgb_combinations = new ArrayList<>();   
        
        static String permission;
    };

    protected static final class Sounds
    {
        static boolean do_completion_sound = false;
       
        static String permission;
        static Sound completion_sound;
    };
    
    protected static final class Lightning
    {
        static boolean do_lightning = false;
    };
    
    protected static final class Messages
    {
        static String completion_message;
        static boolean send_as_title; 
    };
    
    protected static final class Mechanism
    {
        protected static final class  Security
        {
            static Integer maximum_attempts, attempt_timeout;
            static boolean lock_ip_address;
            
            protected static class Restrictions
            {
                static boolean disable_chat, disable_movement, disable_inventory_interaction, disable_damage, prevent_kill_aura;
            };
            
            protected static class PotionEffects
            {
                protected static final List<PotionEffect> potion_effects = new ArrayList<>();
                protected static boolean apply_potion_effects;
            };
        };
        
        protected static final class Interface
        {
            protected static String title;
            
            protected static final class NormalItems
            {
                protected static final List<ItemStack> items = new ArrayList<>();
                protected static final List<String> lore = new ArrayList<>();
                protected static String display_name;
            };
            
            protected static final class KeyItems
            {
                protected static final List<ItemStack> items = new ArrayList<>();
                protected static final List<String> lore = new ArrayList<>();
                protected static String display_name;
            };
        };
    };
    
    void LoadConfiguration()
    {
        saveDefaultConfig();
        
        if (plugin == null)
            plugin = this;
        
        plugin.reloadConfig();
        config = plugin.getConfig();
    
        Lightning.do_lightning = config.getBoolean("modules.lightning-switch");
        
        final Exception error = new Exception("error");
        final Exception skip = new Exception("skip");
        
        try /*Firework Module*/
        {
            Fireworks.do_fireworks = config.getBoolean("modules.fireworks.enabled");
            
            if (!Fireworks.do_fireworks)
            {
                throw skip;
            };
            
            Fireworks.random_firework_types = config.getBoolean("modules.fireworks.effect-randomizer");
            Fireworks.firework_types.clear();
            
            if (!Fireworks.random_firework_types) 
            {
                for (final String type : config.getStringList("modules.fireworks.effects"))
                {
                    final String[] arr = type.replace(" ", "").toUpperCase().split(",");
                    
                    try
                    {
                        for (final String suspect : arr)
                        {
                            final FireworkEffect.Type buff = FireworkEffect.Type.valueOf(suspect);
                            Fireworks.firework_types.add(buff);
                        };
                    }
                    
                    catch (final Exception e)
                    {
                        print("Invalid effect type settings found. Skipping ....");
                    };
                };
                
                if (Fireworks.firework_types.size() < 1)
                {
                    print("Insufficient Firework Types found, using randomizer!");
                    Fireworks.random_firework_types = true;
                };
            };
            
            if (Fireworks.random_firework_types)
            {
                Fireworks.firework_types.addAll
                (
                    Arrays.asList
                    (
                        FireworkEffect.Type.BALL,
                        FireworkEffect.Type.BALL_LARGE,
                        FireworkEffect.Type.BURST,
                        FireworkEffect.Type.CREEPER,
                        FireworkEffect.Type.STAR
                    )
                );                
            };
            
            Fireworks.random_firework_color = config.getBoolean("modules.fireworks.rgb-randomizer");
            Fireworks.rgb_combinations.clear();
            
            if (!Fireworks.random_firework_color)
            {
                for (final String code : config.getStringList("modules.fireworks.rbg-colours"))
                {
                    final String arr[] = code.replace(" ", "").split(",");
     
                    try
                    {
                        if (arr.length < 3)
                        {
                            print("Invalid R.G.B. colour codes found. Skipping ....");
                            continue;
                        };
                        
                        final int[] rgb = {255,255,255};
                        
                        for (int i = 0; i < 3; i += 1)
                            rgb[i] = Integer.parseInt(arr[i]);
                        
                        Fireworks.rgb_combinations.add
                        (
                            Color.fromRGB
                            (
                                rgb[0], 
                                rgb[1], 
                                rgb[2]
                            )
                        );
                    }
                    
                    catch (final Exception e)
                    {
                        print("Invalid R.G.B. colour format has been found in the configuration file. Skipping ....");
                    };
                };
                
                if (Fireworks.rgb_combinations.size() < 1)
                {
                    print("Insufficient R.G.B. combinations found, using randomizer!");
                    Fireworks.random_firework_color = true;
                };
            };
            
            Fireworks.permission = config.getString("modules.fireworks.permission");
        }
        
        catch (final Exception e)
        {
            if (!e.toString().equals("skip"))
            {
                print("Invalid fireworks settings were detected in the configuration file. Disabling this module!");
                Fireworks.do_fireworks = false;
            };
        }; /*End of Firework Module*/
        
        try /*Sound Module*/
        {
            Sounds.do_completion_sound = config.getBoolean("modules.sounds.enabled");

            if (Sounds.do_completion_sound)
            {
                Sounds.completion_sound = Sound.valueOf(config.getString("modules.sounds.completion-sound"));

                if (Sounds.completion_sound == null)
                {
                    throw error;
                };
                
                Sounds.permission = config.getString("modules.sounds.permission");
            };
        }
        
        catch (final Exception e)
        {
            print("Invalid sound has been found in the configuration file. Using the default one!");
            Sounds.completion_sound = Sound.ENTITY_PLAYER_LEVELUP;
        }; /*End of Sound Module*/   
        
        try /*Messages*/
        {
            Messages.completion_message = color(config.getString("messages.completion-message"));
            Messages.send_as_title = config.getBoolean("messages.send-as-title");
        }
        
        catch (final Exception e)
        {
            print("Invalid message configuration has been found in the configuration file.");
        }; /*End of Messages*/
        
        try /*Mechanism*/
        {
            try /*Security*/
            {
                Mechanism.Security.maximum_attempts = config.getInt("mechanism.security.maximum-attempts");                    
                Mechanism.Security.attempt_timeout = config.getInt("mechanism.security.attempt-timeout");

                Mechanism.Security.lock_ip_address = config.getBoolean("mechanism.security.lock-ip-address");                
                
                try /*Restrictions*/
                {
                    Mechanism.Security.Restrictions.disable_chat = config.getBoolean("mechanism.security.restrictions.disable-chat");
                    Mechanism.Security.Restrictions.disable_movement = config.getBoolean("mechanism.security.restrictions.disable-movement");
                    Mechanism.Security.Restrictions.disable_inventory_interaction = config.getBoolean("mechanism.security.restrictions.disable-inventory-interaction");
                    Mechanism.Security.Restrictions.disable_damage = config.getBoolean("mechanism.security.restrictions.disable-damage");                    
                    Mechanism.Security.Restrictions.prevent_kill_aura = config.getBoolean("mechanism.security.restrictions.prevent-kill-aura");
                }
                
                catch (final Exception e)
                {
                    throw error;
                }; /*End of Restrictions*/
                
                try /*Potions*/
                {
                    Mechanism.Security.PotionEffects.apply_potion_effects = config.getBoolean("mechanism.security.potion-appliance.enabled");
                    
                    if (Mechanism.Security.PotionEffects.apply_potion_effects)
                    {    
                        Mechanism.Security.PotionEffects.potion_effects.clear();
                        
                        for (final String effect : config.getStringList("mechanism.security.potion-appliance.effects"))
                        {
                            try
                            {
                                final PotionEffect finalized = new PotionEffect(PotionEffectType.getByName(effect), 99999, 1);

                                if (finalized == null)
                                {
                                    throw error;
                                };

                                Mechanism.Security.PotionEffects.potion_effects.add(finalized);
                            }
                            
                            catch (final Exception e)
                            {
                                print("Invalid potion-appliance effect found in the configuration file. Skipping ....");
                            };
                        };
                    };
                }
                
                catch (final Exception e)
                {
                    print("Invalid settings have been found in the potion-appliance section of the plugin.yml. Disabling this feature ....");
                    Mechanism.Security.PotionEffects.apply_potion_effects = false;
                }; /*End of Potions*/                
            }
            
            catch (final Exception e)
            {
                print("There was an error in the security part of the config.yml. Certain features may not function as expected!");
            }; /*End of Security*/
            
            try /*Interface*/
            {
                Mechanism.Interface.title = color(config.getString("mechanism.interface.title"));
                
                Mechanism.Interface.NormalItems.items.clear();
                Mechanism.Interface.NormalItems.lore.clear();
                
                Mechanism.Interface.NormalItems.display_name = color(config.getString("mechanism.interface.none-key-items.display-name"));                
                Mechanism.Interface.NormalItems.lore.add(color(config.getString("mechanism.interface.none-key-items.lore")));                
                
                for (final String item : config.getStringList("mechanism.interface.none-key-items.items"))
                {
                    try
                    {
                        final ItemStack substance = new ItemStack(Material.getMaterial(item), 1);
                        
                        if (substance == null)
                        {
                            throw error;
                        };
                        
                        final ItemMeta meta = (ItemMeta) substance.getItemMeta();

                        meta.setDisplayName(Mechanism.Interface.NormalItems.display_name);
                        meta.setLore(Mechanism.Interface.NormalItems.lore);

                        substance.setItemMeta(meta);
                        
                        Mechanism.Interface.NormalItems.items.add(substance);
                    }
                    
                    catch (final Exception e)
                    {
                        print("There was an invalid item found in the none-key-items section in the config.yml! Skipping ....");
                    };
                };                
                
                Mechanism.Interface.KeyItems.items.clear();
                Mechanism.Interface.KeyItems.lore.clear();
                
                Mechanism.Interface.KeyItems.display_name = color(config.getString("mechanism.interface.key-items.display-name"));                
                Mechanism.Interface.KeyItems.lore.add(color(config.getString("mechanism.interface.key-items.lore")));
                
                for (final String item : config.getStringList("mechanism.interface.key-items.items"))
                {
                    try
                    {
                        final ItemStack substance = new ItemStack(Material.getMaterial(item), 1);
                        
                        if (substance == null)
                        {
                            throw error;
                        };
                        
                        final ItemMeta meta = (ItemMeta) substance.getItemMeta();

                        meta.setDisplayName(Mechanism.Interface.KeyItems.display_name);
                        meta.setLore(Mechanism.Interface.KeyItems.lore);

                        substance.setItemMeta(meta);                        
                        
                        Mechanism.Interface.KeyItems.items.add(substance);
                    }
                    
                    catch (final Exception e)
                    {
                        print("There was an invalid item found in the key-items section in the config.yml! Skipping ....");
                    };
                };
            }
            
            catch (final Exception e)
            {
                print("There was an error in the interface part of the config.yml. Certain features may not function as expected!");
            }; /*End of Interface*/
        }
        
        catch (final Exception e)
        {
            print("An unknown error has occurred.  If this error persists please contact me at KvinneKraft@protonmail.com.  Be sure to send me the following:\n" + e.getMessage());
        }; /*End of Mechanism*/
    };
    
    protected final class Events implements Listener
    {
        protected final class Cache
        { 
            protected final HashMap<Player, Integer> player_attempts = new HashMap<>();
            protected final HashMap<Player, ItemStack> player_keys = new HashMap<>();
            protected final HashMap<Player, Inventory> player_guis = new HashMap<>();
            protected final HashMap<UUID, String> player_ips = new HashMap<>();
        };
        
        final Cache cache = new Cache();
        
        @EventHandler public final void onPlayerDamage(final EntityDamageEvent e)
        {
            final Entity entity = (Entity) e.getEntity();
            
            if (Mechanism.Security.Restrictions.disable_damage)
            {
                if (entity instanceof Player)
                {
                    final Player p = (Player) entity;

                    if (cache.player_guis.containsKey(p))
                    {
                        e.setCancelled(true);
                    };
                };
            };
        };
        
        @EventHandler public final void onEntityDamage(final EntityDamageByEntityEvent e)
        {
            final Entity entity = (Entity) e.getEntity();
            
            if (Mechanism.Security.Restrictions.prevent_kill_aura)
            {
                if (entity instanceof Player && e.getDamager() instanceof Player)
                {
                    final Player p = (Player) entity;
                    
                    if (cache.player_guis.containsKey(p))
                    {
                        p.kickPlayer(color("You should be unable to hit entities while you have to solve your captcha!"));
                        e.setCancelled(true);
                    };
                };
            };
        };
        
        @EventHandler public final void onPlayerMovement(final PlayerMoveEvent e)
        {
            if (cache.player_guis.containsKey(e.getPlayer()))
            {
                if(Mechanism.Security.Restrictions.disable_movement)
                {
                    e.setCancelled(true);
                };
            };
        };
        
        @EventHandler public final void onPlayerCommand(final PlayerCommandPreprocessEvent e)
        {
            if (cache.player_guis.containsKey(e.getPlayer()))
            {
                if (Mechanism.Security.Restrictions.disable_chat)
                {
                    e.setCancelled(true);
                };
            };
        };
        
        @EventHandler public final void onPlayerChat(final AsyncPlayerChatEvent e)
        {
            if (cache.player_guis.containsKey(e.getPlayer()))
            {
                if (Mechanism.Security.Restrictions.disable_chat)
                {
                    e.setCancelled(true);
                };
            };
        };
        
        @EventHandler public final void onPlayerRespawn(final PlayerRespawnEvent e)
        {
            final Player p = (Player) e.getPlayer();                

            if (cache.player_guis.containsKey(p))
            {
                getServer().getScheduler().runTaskLater
                (
                    plugin, 

                    new Runnable() 
                    { 
                        @Override public final void run()
                        {
                            if (cache.player_guis.containsKey(p))
                            {
                                p.openInventory(cache.player_guis.get(p));
                            };
                        };
                    }, 

                    1
                );
            };            
        };
        
        @EventHandler public final void onInventoryClose(final InventoryCloseEvent e)
        {
            if (Mechanism.Security.Restrictions.disable_inventory_interaction)
            {
                final Player p = (Player) e.getPlayer();                
                
                if (cache.player_guis.containsKey(p))
                {
                    getServer().getScheduler().runTaskLater
                    (
                        plugin, 

                        new Runnable() 
                        { 
                            @Override public final void run()
                            {
                                if (cache.player_guis.containsKey(p))
                                {
                                    p.openInventory(cache.player_guis.get(p));
                                };
                            };
                        }, 

                        1
                    );
                };
            };
        };
        
        @EventHandler public final void onPlayerQuit(final PlayerQuitEvent e)
        {
            final Player p = (Player) e.getPlayer();
            
            if (cache.player_attempts.containsKey(p))
            {
                if (Mechanism.Security.lock_ip_address && cache.player_ips.containsKey(p.getUniqueId())) { cache.player_ips.remove(p.getUniqueId()); };                
                
                cache.player_attempts.remove(p);
                cache.player_guis.remove(p);
                cache.player_keys.remove(p);            
            };
        };
        
        @EventHandler public final void onInventoryInteract(final InventoryClickEvent e)
        {
            if(!(e.getWhoClicked() instanceof Player))
            {
                return;
            };
            
            final Player p = (Player) e.getWhoClicked();
            
            if (cache.player_guis.containsKey(p))
            {
                if (e.getCurrentItem() != null && e.getCurrentItem().equals(cache.player_keys.get(p)))
                {
                    if (Lightning.do_lightning) p.getWorld().strikeLightningEffect(p.getLocation());
                    
                    if (Fireworks.do_fireworks && p.hasPermission(Fireworks.permission))
                    {
                        final Random rand = new Random();
                        
                        FireworkEffect.Type type = type = Fireworks.firework_types.get(rand.nextInt(Fireworks.firework_types.size())); 
                        Color color = null;
                                
                        if (Fireworks.random_firework_color)
                        {
                            color = Color.fromRGB(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
                        }
                        
                        else
                        {
                            color = Fireworks.rgb_combinations.get(rand.nextInt(Fireworks.rgb_combinations.size()));                            
                        };

                        p.setInvulnerable(true);

                        Detonate.Firework(p.getLocation(), color, color, type);

                        p.setInvulnerable(false);
                    };
                    
                    if (p.hasPermission(Sounds.permission) && Sounds.do_completion_sound) p.playSound(p.getLocation(), Sounds.completion_sound, 30, 30);
                    if (Mechanism.Security.lock_ip_address && cache.player_ips.containsKey(p.getUniqueId())) cache.player_ips.remove(p.getUniqueId());                    
                    
                    cache.player_attempts.remove(p);
                    cache.player_guis.remove(p);
                    cache.player_keys.remove(p);
                    
                    if (Mechanism.Security.PotionEffects.apply_potion_effects)
                    {
                        for (final PotionEffect effect : Mechanism.Security.PotionEffects.potion_effects)
                        {
                            if (p.hasPotionEffect(effect.getType()))
                            {
                                p.removePotionEffect(effect.getType());
                            };
                        };
                    };
                    
                    p.closeInventory();
                    
                    if (Messages.send_as_title)
                    {
                        p.sendTitle(Messages.completion_message, "", 20 * 3, 5 * 20, 3 * 20);
                    }
                    
                    else
                    {
                        p.sendMessage(Messages.completion_message);
                    };
                }

                else
                {
                    int attempts = 1;

                    if (cache.player_attempts.containsKey(p))
                    {
                        attempts = cache.player_attempts.get(p);

                        if (attempts >= Mechanism.Security.maximum_attempts)
                        {
                            p.kickPlayer(color("&cYou have exceeded the maximum attempts!\nYou may relog in order to retry."));
                        };

                        attempts += 1;
                    };            

                    cache.player_attempts.put(p, attempts);
                };
                
                if (Mechanism.Security.Restrictions.disable_inventory_interaction)
                {
                    e.setCancelled(true);
                };
            };
        };
        
        @EventHandler public final void onPlayerAuthenticate(final AsyncPlayerPreLoginEvent e)
        {
            if (Mechanism.Security.lock_ip_address)
            {
                final UUID uuid = (UUID) e.getUniqueId();
                
                if (cache.player_ips.containsKey(uuid))
                {
                    if (!e.getAddress().getAddress().toString().equalsIgnoreCase(cache.player_ips.get(uuid)))
                    {
                        e.setKickMessage(color("&cYou may not authenticate with another account using the same IP Address!"));
                        e.disallow(Result.KICK_OTHER, e.getKickMessage());
                    };
                };
            };
        };
        
        @EventHandler public final void onPlayerJoin(final PlayerJoinEvent e)
        {   
            final Player p = (Player) e.getPlayer();                                 
            
            getServer().getScheduler().runTaskAsynchronously
            (
                plugin,
                    
                new Runnable()
                {
                    @Override public void run()
                    {
                        final Inventory gui = getInventory(p);
                       
                        cache.player_guis.put(p, gui);
                        
                        if (Mechanism.Security.lock_ip_address)
                        {
                            cache.player_ips.put(p.getUniqueId(), p.getAddress().getAddress().toString());
                        };
                        
                        getServer().getScheduler().runTaskLater
                        (
                            plugin, 
                                
                            new Runnable() 
                            { 
                                @Override public void run() 
                                { 
                                    if (Mechanism.Security.PotionEffects.apply_potion_effects)
                                    {
                                        p.addPotionEffects(Mechanism.Security.PotionEffects.potion_effects);
                                    };
                                    
                                    p.openInventory(gui);
                                   
                                    getServer().getScheduler().runTaskLater
                                    (
                                        plugin, 
                                            
                                        new Runnable() 
                                        {
                                            @Override public void run()
                                            {
                                                if (p.isOnline() && cache.player_guis.containsKey(p))
                                                {
                                                    p.kickPlayer(color("&cYou took to long to solve the captcha.\nYou may relog in order to retry."));
                                                };
                                            };
                                        }, 
                                        
                                        Mechanism.Security.attempt_timeout * 20
                                    );
                                }; 
                            },
                            
                            1
                        );
                    };
                }
            );
        };
        
        protected final Inventory getInventory(final Player p)
        {
            final Random rand = new Random();
            final int sacred_entry = rand.nextInt(27);
            final List<ItemStack> items = new ArrayList<>();
            
            for (int entry = 0; entry < 27; entry += 1)
            {
                ItemStack item = (ItemStack) Mechanism.Interface.NormalItems.items.get(rand.nextInt(Mechanism.Interface.NormalItems.items.size()));
                
                if (entry == sacred_entry)
                {
                    item = (ItemStack) Mechanism.Interface.KeyItems.items.get(rand.nextInt(Mechanism.Interface.KeyItems.items.size()));
                    cache.player_keys.put(p, item);
                };                
                
                items.add(item);
            };            
            
            final String item_id = cache.player_keys.get(p).getType().toString().replace("_", " ");
            final Inventory gui = Bukkit.createInventory(null, 27, Mechanism.Interface.title.replace("{key}", item_id)); 
            
            for (int id = 0; id < gui.getSize(); id += 1) gui.setItem(id, items.get(id));
            
            return gui;
        };
    };    
    
    protected final static class Detonate
    {
        public static void Firework(Location location, Color mcolor, Color fcolor, FireworkEffect.Type type)
        {
            Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta firework_meta = firework.getFireworkMeta();

            firework_meta.addEffect(FireworkEffect.builder().withColor(mcolor).with(type).flicker(true).withFlicker().withTrail().withFade(fcolor).trail(true).build());

            firework.setFireworkMeta(firework_meta);
            firework.detonate();
        };          
    };
    
    @Override public final void onEnable()
    {
        print("Trying to catch my breath here, hold on ....");
        
        LoadConfiguration();
        
        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("dashcaptcha").setExecutor(new Commands());
        
        print
        (
            (
                "\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\n" +
                "Author: Dashie A.K.A. KvinneKraft\n" +
                "Version: 2.1\n" +
                "Email: KvinneKraft@protonmail.com\n" +
                "Github: https://github.com/KvinneKraft\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
            )
        );
        
        print("I am now breathing!");
    };
    
    protected final class Commands implements CommandExecutor
    {
        @Override public final boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("I may only be commanded by a player!");
                return false;
            };
            
            final Player p = (Player) s;
            
            if (p.hasPermission("admin"))
            {
                if (as.length > 0 && as[0].equalsIgnoreCase("reload"))
                {
                    p.sendMessage(color("&eReloading ...."));
                    LoadConfiguration();
                    p.sendMessage(color("&eDone!"));
                }
                
                else
                {
                    p.sendMessage(color("&cPerhaps try &7/captcha reload &c?"));
                };
                
                return true;
            };
            
            p.sendMessage(color("&cYou may not use this command!"));
            return true;
        };
    };    
    
    @Override public void onDisable()
    {
        print("I think that I died?");
    };
    
    protected final String color(final String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
    
    protected final void print(final String line)
    {
        System.out.println("(Better Captcha): " + line);
    };
};