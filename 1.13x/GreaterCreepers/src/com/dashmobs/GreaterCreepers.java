
// Author: Dashie
// Version: 1.0

package com.dashmobs;

import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GreaterCreepers extends JavaPlugin implements Listener
{
    @Override public void onEnable()
    {
        print("The plugin is loading ....");
        
        LoadConfiguration();
        getServer().getPluginManager().registerEvents(this, plugin);
        
        print("The plugin has been enabled!");
    };
    
    private FileConfiguration config = (FileConfiguration) null;
    private final JavaPlugin plugin = (JavaPlugin) this;
    
    private void LoadConfiguration()
    {
        saveDefaultConfig();
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        chargedcreepers = config.getBoolean("properties.charged-creepers");
        explosionflames = config.getBoolean("properties.explosion-radius");
        summonlightning = config.getBoolean("properties.summon-lightning");
        summonfireworks = config.getBoolean("properties.summon-fireworks");
        
        explosionradius = config.getInt("properties.explosion-radius");
        newhealth = config.getInt("properties.new-health");
    };
    
    private boolean chargedcreepers,explosionflames,summonlightning,summonfireworks;
    private int explosionradius,newhealth;
    
    @EventHandler public void CreatureSpawn(final CreatureSpawnEvent e)
    {
        if(!(e.getEntity() instanceof Creeper))
        {
            return;
        };
        
        final Creeper creeper = (Creeper) e.getEntity();
        
        creeper.setPowered(chargedcreepers);
        creeper.setExplosionRadius(explosionradius);
        creeper.setMaxHealth(newhealth);
        creeper.setHealth(newhealth);
    };
    
    @EventHandler public void EntityExplodeEventThing(final EntityExplodeEvent e)
    {
        if(!(e.getEntity() instanceof Creeper))
        {
            return;
        };
        
        final Location location = (Location) e.getEntity().getLocation();
        
        if(explosionflames)
        {
            location.getWorld().createExplosion(location, explosionradius, explosionflames, false);
        };        
        
        if(summonfireworks)
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
        
        if(summonlightning)
        {
            location.getWorld().strikeLightning(location);
        };
    };
    
    @Override public void onDisable()
    {
        print("The plugin has been disabled!");
    };
    
    private void print(String str)
    {
        System.out.println("(Greater Creepers): " + str);
    };
    
    private String color(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};