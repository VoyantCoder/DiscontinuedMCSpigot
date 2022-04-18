
// Author: Dashie
// Version: 1.0


package com.dashrobot;


import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.FireworkEffect;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import java.util.HashMap;
import org.bukkit.Sound;
import java.util.Random;
import org.bukkit.Color;
import java.util.List;
import org.bukkit.event.player.PlayerRespawnEvent;


//---//
//---// Events Handler Class:
//---//

public class Events implements Listener
{   
    
    //---//
    //---// Dash GUI Methods:
    //---//
    
    public ItemStack get_random_item()
    {
        Material material = pattern_items.get(new Random().nextInt(pattern_items.size()));
        
        if(material.equals(Material.DIAMOND))
        {
            material = Material.EMERALD_BLOCK;
        };
        
        return new ItemStack(material, 1);
    };
    
    
    //List<Player> inventory_cache = new ArrayList<>();
    String captcha_title;

    
    private void new_key(Player p)
    {
        Material material = key_items.get(new Random().nextInt(key_items.size()));
        
        if(verification_cache.containsKey(p))
        {
            verification_cache.remove(p);
        };
        
        verification_cache.put(p, material);          
    };

    
    public void open_captcha_dialog(Player p)
    {
        if(verify_cache.contains(p))
        {
            return;
        };
        
        new_key(p);        
        
        String str = captcha_title.replace("%item%", verification_cache.get(p).toString().replace("_", " ").toLowerCase());
        
        Inventory captcha_dialog = Bukkit.getServer().createInventory(p, inventory_slots, str);
        Integer key_index = new Random().nextInt(inventory_slots);
        
        for(int id = 0; id < inventory_slots; id += 1)
        {
            ItemStack item = get_random_item();
            
            if(id == key_index)
            {
                item.setType(verification_cache.get(p));
            };
            
            //ItemMeta item_meta = item.getItemMeta();
            //item.setTypeId(2020);
            //item_meta.setCustomModelData(2020);
            //item.setItemMeta(item_meta);
            
            captcha_dialog.setItem(id, item);
        };

        p.openInventory(captcha_dialog);
       
        //inventory_cache.add(p);
    };    
    
    
    //---//
    //---// Event Handler Methods:
    //---//
    
    HashMap<Player, Material> verification_cache = new HashMap<>();    
    HashMap<Player, Integer> captcha_cache = new HashMap<>();      
    
    List<Material> pattern_items = new ArrayList<Material>();    
    List<Material> key_items = new ArrayList<>();
    
    List<String> verify_cache = new ArrayList<>();    
    
    String timeout_kick_message, join_message, first_join_message, quit_message;
    
    int inventory_slots, verification_timeout;
    
    boolean dash_join, once_verify;
    
    
    @EventHandler public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();        
        
        if(dash_join)
        {
            if(p.hasPlayedBefore())
            {
                e.setJoinMessage(join_message.replace("%player%", p.getName()));
            }
            
            else
            {
                e.setJoinMessage(first_join_message.replace("%player%", p.getName()));
            };
        };
        
        if(verify_cache.contains(p.getName()))
        {
            System.out.println("Hey");
            return;
        };
            
        Bukkit.getScheduler().scheduleSyncDelayedTask(Captcha.plugin, 
            new Runnable()
            {
                @Override
                public void run()
                {
                    if(verification_cache.containsKey(p))
                    {
                        clear_essence(p);                        
                        p.kickPlayer(timeout_kick_message);
                        return;
                    };
                };
            }, 
            
            verification_timeout * 20
        );        
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(Captcha.plugin,
            new Runnable()
            {
                @Override
                public void run()
                {
                    if(apply_blind_effect)
                    {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 222222, 222222));        
                    };
                    
                    open_captcha_dialog(p);
                };
            },
            
            5
        );
    };
    
    
    Material verification_material;           
    String maximum_succeed_message;        
    
    int maximum_attempts;        
    
    
    @EventHandler public void onPlayerCloseInventory(InventoryCloseEvent e)
    {        
        Player p = (Player)e.getPlayer();        
        
        if(!verification_cache.containsKey(p))
        {
            return;
        };
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(Captcha.plugin, 
            new Runnable() 
            {
                @Override
                public void run()
                {
                    open_captcha_dialog(p);       
                };
            }, 
                
            4
        );
    };
    
    @EventHandler public void onPlayerRespawn(final PlayerRespawnEvent e)
    {
        Player p = (Player)e.getPlayer();        
        
        if(!verification_cache.containsKey(p))
        {
            return;
        };
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(Captcha.plugin, 
            new Runnable() 
            {
                @Override
                public void run()
                {
                    open_captcha_dialog(p);       
                };
            }, 
                
            4
        );        
    };
    
    private void clear_essence(Player p)
    {
        if(verification_cache.containsKey(p))
        {
            verification_cache.remove(p);
        };
        
        if(captcha_cache.containsKey(p))
        {
            captcha_cache.remove(p);
        };  
        
        //if(inventory_cache.contains(p))
        //{
        //    inventory_cache.remove(p);
        //};
        
        if(apply_blind_effect)
        {
            if(p.hasPotionEffect(PotionEffectType.BLINDNESS))
            {
                p.removePotionEffect(PotionEffectType.BLINDNESS);
            };
        };        
    };
    
    
    boolean summon_fireworks, summon_lightning, send_as_title, wither_sound, apply_blind_effect, block_chat, block_command, block_movement;
    String captcha_complete_message;    
    
    
    private void grant_access(Player p)
    {   
        if(!verify_cache.contains(p.getName()))
        {
            verify_cache.add(p.getName());
        };
        
        if((summon_fireworks) || (summon_lightning) || (wither_sound))
        {
            Location location = p.getLocation();
            
            p.setInvulnerable(true);
            
            if(summon_fireworks)
            {
                Firework firework = (Firework)location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                FireworkMeta firework_meta = firework.getFireworkMeta();
            
                Random rand = new Random();
                
                int r = rand.nextInt(255) + 1;
                int g = rand.nextInt(255) + 1;
                int b = rand.nextInt(255) + 1;
                
                Color firework_color = Color.fromRGB(r, g, b);
            
                firework_meta.addEffect(FireworkEffect.builder().withColor(firework_color).withFlicker().withTrail().with(FireworkEffect.Type.BURST).flicker(true).build());
                firework.setFireworkMeta(firework_meta);   
                
                firework.detonate();
            };
        
            if(summon_lightning)
            {
                location.getWorld().strikeLightningEffect(location);
            };
            
            if(wither_sound)
            {
                p.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 30, 30);
            };
            
            Bukkit.getScheduler().scheduleSyncDelayedTask(Captcha.plugin, 
                new Runnable() 
                {
                    @Override
                    public void run()
                    {
                        p.setInvulnerable(false);                        
                    };
                },
                
                20
            );
        };
        
        p.closeInventory();              
        
        if(!send_as_title)
        {
            p.sendMessage(captcha_complete_message);
        }
        
        else
        {
            p.sendTitle("", captcha_complete_message);
        };
    };
    
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e)
    {
        if(verification_cache.containsKey(e.getPlayer()))
        {        
            if(block_chat)
            {
                e.setCancelled(block_chat);
            };
        };
    };
    
    
    @EventHandler public void onPlayerCommand(PlayerCommandPreprocessEvent e)
    {
        if(verification_cache.containsKey(e.getPlayer()))
        {        
            if(block_command)
            {
                e.setCancelled(block_command);
            };
        };
    };
    
    
    @EventHandler public void onPlayerMovement(PlayerMoveEvent e)
    {
        if(verification_cache.containsKey(e.getPlayer()))
        {
            if(block_movement)
            {
                e.setCancelled(block_movement);
            };
        };
    };
    
    
    @EventHandler public void onInventoryClick(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();        
        ItemStack i = e.getCurrentItem();

        
        if((i == null) || (!verification_cache.containsKey(p)))
        {
            return;
        }
       
        else if(captcha_cache.containsKey(p))
        {
            if(captcha_cache.get(p) > maximum_attempts)
            {
                clear_essence(p);                
                p.kickPlayer(maximum_succeed_message);
                
                return;
            };  
        };
        
        if(i.getType().equals(verification_cache.get(p)))
        {   
            clear_essence(p);            
            grant_access(p);
            
            return;
        };
                       
        Integer new_count = 1;     
        e.setCancelled(true);                        
        
        if(captcha_cache.containsKey(p))
        {
            new_count += captcha_cache.get(p);
        };
        
        captcha_cache.put(p, new_count);
    };
    
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        clear_essence(e.getPlayer());
        
        if(dash_join)
        {
            e.setQuitMessage(quit_message.replace("%player%", e.getPlayer().getName()));
        };
    };
};
