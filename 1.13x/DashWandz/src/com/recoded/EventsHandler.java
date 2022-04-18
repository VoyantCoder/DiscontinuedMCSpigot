
// Author: Dashie
// Version: 1.0

package com.recoded;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class EventsHandler implements Listener
{
    public static List<Integer> cooldowns = new ArrayList<>();
    public static List<String> perms = new ArrayList<>();
    
    public static String bypass_permission;
    
    private final List<Player> players = new ArrayList<>();
    
    public void DetonateFirework(Location location, Color mcolor, Color fcolor, FireworkEffect.Type type)
    {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta firework_meta = firework.getFireworkMeta();
        
        firework_meta.addEffect(FireworkEffect.builder().withColor(mcolor).with(type).flicker(true).withFlicker().withTrail().withFade(fcolor).trail(true).build());
        
        firework.setFireworkMeta(firework_meta);
        firework.detonate();
    };        
    
    @EventHandler public void onProjectileHit(final ProjectileHitEvent e)
    {
        if (e.getEntity() instanceof Arrow)
        {
            final Arrow arrow = (Arrow) e.getEntity();
            
            if (arrow.getShooter() instanceof Player)
            {
                final Player p = (Player) arrow.getShooter();
                final ItemStack item = (ItemStack) p.getInventory().getItemInMainHand();
                
                if (DashWandz.wands.contains(item))
                {
                    final Location location = (Location) arrow.getLocation();
                    final Random r = new Random();
                    
                    DetonateFirework(location, Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)), Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)), FireworkEffect.Type.BALL_LARGE);
                    DetonateFirework(location, Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)), Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)), FireworkEffect.Type.BALL);
                    DetonateFirework(location, Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)), Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255)), FireworkEffect.Type.BURST);
                    
                    arrow.getWorld().createExplosion(arrow.getLocation(), 1, true, true);
                    
                    e.getEntity().remove();
                };
            };
        };
    };
    
    @EventHandler public void onInteraction(final PlayerInteractEvent e)
    {
        final Player p = (Player) e.getPlayer();
        final ItemStack wand = (ItemStack) p.getInventory().getItemInMainHand();       
        
        if (wand == null || !DashWandz.wands.contains(wand))
        {
            return;
        }
        
        else if (!e.getAction().equals(e.getAction().RIGHT_CLICK_AIR) && !e.getAction().equals(e.getAction().RIGHT_CLICK_BLOCK))
        {
            return;
        };
            
        final int id = (int) DashWandz.wands.indexOf(wand);
  
        if (players.contains(p))
        {
            p.sendMessage(Kvinne.color("&cYou must wait at least &4" + cooldowns.get(id).toString() + " seconds &c!"));
            return;
        }        
        
        else if (!p.hasPermission(perms.get(id)))
        {
            p.sendMessage(Kvinne.color("&cYou may not use this."));
            return;
        }
        
        else if (wand.equals(DashWandz.wands.get(0)))/*Firework Wand*/
        {
            Bukkit.getServer().getScheduler().runTaskAsynchronously
            (
                DashWandz.plugin,
                
                new Runnable()
                {
                    @Override public void run()
                    {
                        for (int arrow = 0; arrow < 8; arrow += 1)
                        {
                            Bukkit.getServer().getScheduler().runTask
                            (
                                DashWandz.plugin,
                                
                                new Runnable()
                                {
                                    @Override public void run()
                                    {            
                                        Arrow arrow = (Arrow) p.launchProjectile(Arrow.class);

                                        arrow.setVelocity(p.getLocation().getDirection().multiply(3));
                                        arrow.setKnockbackStrength(5);
                                        arrow.setColor(Color.PURPLE);          
                                        arrow.setPierceLevel(100);
                                        arrow.setCritical(true);  
                                        arrow.setFireTicks(60);            
                                        arrow.setDamage(20);     

                                        p.playSound(p.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 30, 30);
                                    }
                                }
                            );
                            
                            try
                            {
                                Thread.sleep(100);
                            } 
                            
                            catch (InterruptedException ex)
                            {
                                /*.-.*/
                            }
                        };
                    }
                }
            );
        }
        
        else if (wand.equals(DashWandz.wands.get(1)))/*Lightning Wand*/
        {
            final Location location = (Location) p.getTargetBlockExact(100).getLocation();
            
            Bukkit.getServer().getScheduler().runTaskAsynchronously
            (
                DashWandz.plugin,
                
                new Runnable()
                {
                    @Override public void run()
                    {
                        for (int strike = 0; strike < 6; strike += 1)
                        {
                            Bukkit.getServer().getScheduler().runTask
                            (
                                DashWandz.plugin,
                                
                                new Runnable()
                                {
                                    @Override public void run()
                                    {
                                        location.getWorld().strikeLightning(location);
                                        location.getWorld().createExplosion(location, 1, true, true);
                                        
                                        p.playSound(p.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 30, 30);
                                    };
                                }
                            );
                        };
                    };
                }
            );
        }
        
        else if (wand.equals(DashWandz.wands.get(3)))/*Fireball Wand*/
        {
            Bukkit.getServer().getScheduler().runTaskAsynchronously
            (
                DashWandz.plugin,
                    
                new Runnable()
                {
                    @Override public void run()
                    {
                        for (int fwb = 0; fwb < 6; fwb += 1)
                        {
                            Bukkit.getServer().getScheduler().runTask
                            (
                                DashWandz.plugin,
                                    
                                new Runnable()
                                {
                                    @Override public void run()
                                    {
                                        Fireball fireball = (Fireball) p.launchProjectile(Fireball.class);

                                        fireball.setVelocity(p.getLocation().getDirection().multiply(1.5));
                                        fireball.setFallDistance(120);            
                                        fireball.setFireTicks(60);
                                        
                                        p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 20, 20);
                                    };
                                }
                            );
                            
                            try
                            {
                                Thread.sleep(200);
                            } 
                            
                            catch (InterruptedException ex)
                            {
                                // Why is this even required? It is so annoying ....
                            }
                        };
                    };
                }
            );
        }
        
        else if (wand.equals(DashWandz.wands.get(2)))/*Wither Wand*/
        {
            Bukkit.getServer().getScheduler().runTaskAsynchronously
            (
                DashWandz.plugin,
                    
                new Runnable()
                {
                    @Override public void run()
                    {
                        final Random r = new Random();
                        
                        for (int fwb = 0; fwb < 6; fwb += 1)
                        {
                            Bukkit.getServer().getScheduler().runTask
                            (
                                DashWandz.plugin,
                                    
                                new Runnable()
                                {
                                    @Override public void run()
                                    {
                                        WitherSkull witherskull = (WitherSkull) p.launchProjectile(WitherSkull.class);

                                        witherskull.setVelocity(p.getLocation().getDirection().multiply(1.5));
                                        witherskull.setFallDistance(120);            
                                        witherskull.setFireTicks(60);
                                        
                                        witherskull.setIsIncendiary(true);      
                                        
                                        if (r.nextInt(100) > 75)
                                        {
                                            witherskull.setCharged(true);
                                        };
                                        
                                        p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 30, 30);                                        
                                    };
                                }
                            );
                            
                            try
                            {
                                Thread.sleep(200);
                            } 
                            
                            catch (InterruptedException ex)
                            {
                                // Why is this even required? It is so annoying ....
                            }
                        };
                    };
                }
            );
        }
        
        else
        {
            return;/*I do not even know how you would get here.*/
        };
        
        p.sendMessage(Kvinne.color("&bYou have used your &r" + DashWandz.wands.get(id).getItemMeta().getDisplayName() + "&b!"));
        
        if (p.hasPermission(bypass_permission) || cooldowns.get(id) < 1)
        {
            return;
        };
        
        players.add(p);
        
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously
        (
            DashWandz.plugin, 
                
            new Runnable() 
            { 
                @Override public void run() 
                {  
                    if (players.contains(p))
                    {
                        players.remove(p);
                    };
                    
                    if (p.isOnline())
                    {
                        p.sendMessage(Kvinne.color("&bYou may now use your &r" + DashWandz.wands.get(id).getItemMeta().getDisplayName() + " &bagain!"));
                    };
                }; 
            },
                
            cooldowns.get(id) * 20
        );
    };
};
