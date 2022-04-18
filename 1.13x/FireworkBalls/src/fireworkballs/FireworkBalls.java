
// Author: Dashie
// Version: 1.0

package fireworkballs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class FireworkBalls extends JavaPlugin implements Listener
{
    @Override public void onEnable()
    {
        print("This plugin is loading ....");
        
        getServer().getPluginManager().registerEvents(this, this);
        
        print("This plugin has been loaded!");
    };
    
    private final List<FireworkEffect.Type> types = Arrays.asList
    (
        new FireworkEffect.Type[]
        {
            FireworkEffect.Type.BALL,
            FireworkEffect.Type.BALL_LARGE,
            FireworkEffect.Type.BURST,
            FireworkEffect.Type.CREEPER,
            FireworkEffect.Type.STAR
        }
    );
    
    @EventHandler public void onProjectileHit(final ProjectileHitEvent e)
    {
        if(!(e.getEntity() instanceof Snowball))
        {
            return;
        };
        
        final Location location = e.getEntity().getLocation();
        
        Firework firework = (Firework)location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta firework_meta = firework.getFireworkMeta();

        Random rand = new Random();

        int r = rand.nextInt(255) + 1;
        int g = rand.nextInt(255) + 1;
        int b = rand.nextInt(255) + 1;

        Color firework_color = Color.fromRGB(r, g, b);

        firework_meta.addEffect(FireworkEffect.builder().withColor(firework_color).withFlicker().withTrail().with(types.get(new Random().nextInt(types.size()))).flicker(true).build());
        firework.setFireworkMeta(firework_meta);   

        firework.detonate();        
    };
    
    @Override public void onDisable()
    {
        print("This plugin has been disabled!");
    };
    
    private void print(final String str)
    {
        System.out.println("(Firework Balls): " + str);
    };
};