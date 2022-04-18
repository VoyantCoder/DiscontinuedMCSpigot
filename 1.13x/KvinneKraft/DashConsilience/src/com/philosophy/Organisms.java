
// Author: Dashie
// Version: 1.0

package com.philosophy;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Organisms
{
    final static List<PotionEffect> effects = Arrays.asList
    (
        new PotionEffect[]
        {
            new PotionEffect(PotionEffectType.BLINDNESS, 6 * 20, 2),
            new PotionEffect(PotionEffectType.POISON, 6 * 20, 2),
            new PotionEffect(PotionEffectType.WITHER, 6 * 20, 2),
            new PotionEffect(PotionEffectType.HUNGER, 6 * 20, 2),
            new PotionEffect(PotionEffectType.CONFUSION, 6 * 20, 2),
            new PotionEffect(PotionEffectType.HARM, 1 * 20, 2)
        }
    );
    
    final static Random rand = new Random();
    
    public static void onEntityAttack(final EntityDamageByEntityEvent e)
    {
        final Entity damager = (Entity) e.getDamager();        
        final Entity entity = (Entity) e.getEntity();
        
        if (!(entity instanceof LivingEntity))
        {
            return;
        };
        
        final LivingEntity living_entity = (LivingEntity) entity;

        if (damager instanceof Snowball)
        {
            living_entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 4 * 20, 1));                
            living_entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2 * 20, 1));
        }

        else if (damager instanceof Egg)
        {
            if (rand.nextInt(1000) < 250)
            {
                living_entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 4 * 20, 4));                    
                living_entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2 * 20, 1));      
            };
        };
        
        final LivingEntity victim = (LivingEntity) entity;
        
        if (damager instanceof Arrow)
        {
            final Arrow arrow = (Arrow) e.getDamager();
            
            if (!(arrow.getShooter() instanceof Stray) && !(arrow.getShooter() instanceof Skeleton) && !(arrow.getShooter() instanceof Pillager) && !(arrow.getShooter() instanceof Illusioner))
            {
                return;
            };        
            
            final int chance = rand.nextInt(BASE_PERCENTAGE);

            if (chance <= 650)
            {
                victim.addPotionEffect
                (
                    effects.get
                    (
                        rand.nextInt
                        (
                            effects.size()
                        )
                    )
                );
            };
            
            if (chance <= 50)
            {
                damager.getWorld().createExplosion(damager.getLocation(), 2, true, true);
            };            

            return;
        }        

        else if (damager instanceof CaveSpider)
        {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 8 * 20, 4));
        }
        
        else if (damager instanceof Spider)
        {
            victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 4 * 20, 4));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 4 * 20, 4));
        }
        
        else if (damager instanceof Zombie || damager instanceof Husk || damager instanceof Drowned)
        {
            if (rand.nextInt(BASE_PERCENTAGE) < 800)
            {
                victim.addPotionEffect(effects.get(rand.nextInt(effects.size())));
            };
        };
    };
    
    private final static List<ItemStack> armours = Arrays.asList
    (
        new ItemStack[]
        {
            new ItemStack(Material.DIAMOND_HELMET),
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
        }
    );

    private final static List<ItemStack> weapons = Arrays.asList
    (
        new ItemStack[]
        {
            new ItemStack(Material.DIAMOND_SWORD),
            new ItemStack(Material.DIAMOND_AXE),
            new ItemStack(Material.IRON_SWORD),
            new ItemStack(Material.IRON_AXE),
        }
    );     
    
    private static final int BASE_PERCENTAGE = 1000;
    
    public static void onEntitySpawn(final CreatureSpawnEvent e)
    {
        final LivingEntity entity = (LivingEntity) e.getEntity();
        
        if (entity instanceof Wither)
        {
            e.setCancelled(true);
            return;
        };
        
        if (!weapons.get(0).containsEnchantment(Enchantment.DAMAGE_ALL))
        {
            for (final ItemStack item : weapons)
            {
                item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 10);                
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10);
                item.addUnsafeEnchantment(Enchantment.THORNS, 4);
                
                item.getItemMeta().setDisplayName(Freya.color("&b&l&k:::&r &d&lF&dreya&r &b&l&k:::&r"));
            };
            
            for (final ItemStack item : armours)
            {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 10);
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 10);
                item.addUnsafeEnchantment(Enchantment.THORNS, 10);
                item.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 10);
                
                item.getItemMeta().setDisplayName(Freya.color("&b&l&k:::&r &d&lF&dreya&r &b&l&k:::&r"));
            };
        };
        
        if (rand.nextInt(BASE_PERCENTAGE) < 100)
        {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999 * 20, 1));            
            entity.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999 * 20, 4));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999 * 20, 2));
        };
        
        if (entity instanceof Creeper)
        {
            if (rand.nextInt(BASE_PERCENTAGE) < 100)
            {
                ((Creeper)entity).setPowered(true);
            };
            
            return;
        };
        
        if (entity instanceof Skeleton || entity instanceof Stray || entity instanceof Pillager)
        {
            if (rand.nextInt(BASE_PERCENTAGE) < 5)
            {
                final EntityEquipment equipment = (EntityEquipment) entity.getEquipment();
                
                equipment.setHelmet(armours.get(0));
                equipment.setChestplate(armours.get(1));
                equipment.setLeggings(armours.get(2));
                equipment.setBoots(armours.get(3));
                
                final ItemStack bow = new ItemStack(Material.BOW, 1);
                
                bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 10);
                bow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 10);
                bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 10);
            
                equipment.setItemInMainHand(bow);
            };
            
            if (entity instanceof Pillager)
            {
                entity.setMaxHealth(45);
                entity.setHealth(45);

                entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999 * 20, 2));
            };            
            
            return;
        };
        
        if (entity instanceof Zombie || entity instanceof Husk)
        {
            if (rand.nextInt(BASE_PERCENTAGE) < 15)
            {
                final EntityEquipment equipment = (EntityEquipment) entity.getEquipment();
                
                equipment.setHelmet(armours.get(0));
                equipment.setChestplate(armours.get(1));
                equipment.setLeggings(armours.get(2));
                equipment.setBoots(armours.get(3));
                
                equipment.setHelmetDropChance(1);
                equipment.setChestplateDropChance(1);
                equipment.setLeggingsDropChance(1);
                equipment.setBootsDropChance(1);
                
                equipment.setItemInMainHand(weapons.get(rand.nextInt(weapons.size())));
            };
            
            return;
        };
        
        if (entity instanceof CaveSpider)
        {
            entity.setMaxHealth(20);
            entity.setHealth(20);
            
            return;
        };
    };
};
