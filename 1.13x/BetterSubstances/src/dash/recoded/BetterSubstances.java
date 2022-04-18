
package dash.recoded;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BetterSubstances extends JavaPlugin
{
    FileConfiguration config;
    JavaPlugin plugin;
    
    final HashMap<ItemStack, Integer> substances = new HashMap<>(); 
    final HashMap<String, ItemStack> subsub_s = new HashMap<>();
    
    final List<List<PotionEffect>> substance_genetics = new ArrayList<>();    
    
    final List<Integer> substance_cooldown = new ArrayList<>();    
    final List<String> permission_sets = new ArrayList<>();
    final List<String> substance_ids = new ArrayList<>();
    
    String admin_permission;
    
    ItemStack ObtainSubstance(final String identity)
    {
        try
        {
            final Material substance_m = Material.getMaterial(identity.toUpperCase().replace(" ", "_"));            
            return new ItemStack(substance_m, 1);
        }
        
        catch (final Exception e)
        {
            return null;
        }
    };
    
    PotionEffect ObtainGenetics(final List<String> genetic)
    {
        try
        {
            if (genetic.size() < 3)
                return null;
            
            final PotionEffectType type = (PotionEffectType) PotionEffectType.getByName(genetic.get(0).toUpperCase().replace("EFFECT:", ""));
            final int strength = (int) Integer.parseInt(genetic.get(1).toUpperCase().replace("STRENGTH:", ""));
            final int duration = (int) Integer.parseInt(genetic.get(2).toUpperCase().replace("DURATION:", ""));
            
            return new PotionEffect(type, duration * 20, strength - 1); 
        }
        
        catch (final Exception e)
        {
            return null;
        }
    };
    
    Integer GetInteger(final String string)
    {
        try
        {
            return Integer.parseInt(string);
        }
        
        catch (final Exception e)
        {
            return 30;
        }
    };
    
    void LoadFancyConfiguration()
    {
        saveDefaultConfig();
        
        if (plugin != this)
            plugin = (JavaPlugin) this;
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        admin_permission = config.getString("administrative-permission");
        
        for (final String line : config.getStringList("substance-table"))// One Substance by itself
        {
            final List<String> pure = Arrays.asList(line.replace(" ", "").split(","));
            final ItemStack substance = ObtainSubstance(pure.get(0));
            
            if (pure.size() < 7 || substance == null)
            {
                print("Invalid substance syntax found in config.yml! Skipping ....");
                continue;
            };
            
            final String s_name = color(pure.get(2)).split(":")[1].replace("_", " ");
            final String s_lore = color(pure.get(3)).split(":")[1].replace("_", " ");
            
            final ItemMeta meta = substance.getItemMeta();
            
            meta.setDisplayName(s_name);
            meta.setLore(Arrays.asList(s_lore));          
            
            substance.setItemMeta(meta);
            
            if (substances.containsKey(substance))
            {
                print("Duplicated substance found in config.yml! Skipping....");
                continue;
            };
            
            final List<PotionEffect> genetic_cache = new ArrayList<>();
            
            for (final String genetic_effect : pure.get(4).split("&"))
            {
                final PotionEffect s_genetic = ObtainGenetics(Arrays.asList(genetic_effect.split("-")));

                if (s_genetic == null)
                {
                    print("Invalid substance genetics found in config.yml! Skipping....");
                    continue;
                };

                genetic_cache.add(s_genetic);
            };
            
            substance_genetics.add(genetic_cache);
            
            final int cooldown = GetInteger(pure.get(5).toLowerCase().replace("cooldown:", ""));
            
            switch (cooldown)
            {
                case 30:
                {
                    print("Invalid substance cooldown found in config.yml! Using default (30s)....");                    
                };
                
                default:
                {
                    substance_cooldown.add(cooldown);                    
                    permission_sets.add(pure.get(6).replace("permission:", ""));                                                              
                };
            }; 
            
            pure.set(1, pure.get(1).toLowerCase().replace("give_name:", ""));
            
            substances.put(substance, substances.size());            
            subsub_s.put(ChatColor.stripColor(color(pure.get(1))), substance);            
            
            substance_list += "&e" + pure.get(1) + "&a, &e";            
            substance_ids.add(ChatColor.stripColor(color(pure.get(1))));
        };
        
        substance_list = color(substance_list.substring(0, substance_list.length() - 4) + "&a.");
    };    
    
    @Override public void onEnable()
    {
        print("I am loading ....");
        
        LoadFancyConfiguration();
        
        getServer().getPluginManager().registerEvents(new Events(), this);
        getCommand("bettersubstances").setExecutor(new Commands());
        
        final String greeting = 
        (
            "\r\n-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\r\n" +
            "Author: KvinneKraft (Dashie)\r\n" +
            "Version: 1.0\r\n" +
            "Contact: KvinneKraft@protonmail.com\r\n" +
            "Source: https://github.com/KvinneKraft \r\n" +
            "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-\r\n" 
        );        
        
        print(greeting + "I have been loaded!");
    };
    
    class Events implements Listener
    {
        final List<Map<Player, ItemStack>> queue = new ArrayList<>();
        
        @EventHandler public void PotentialSubstanceUse(final PlayerInteractEvent e)
        {
            final ItemStack substance = (ItemStack) e.getItem();
            
            if (substance == null) 
                return;
            
            final ItemStack sc = new ItemStack(substance.getType(), 1); { final ItemMeta meta = (ItemMeta) substance.getItemMeta(); sc.setItemMeta(meta); };
            
            if (!substances.containsKey(sc))
            {
                //print("No existence");
                return;
            };
            
            final int s_identifier = substances.get(sc);
            final Player p = (Player) e.getPlayer();
            
            if (!p.hasPermission(permission_sets.get(s_identifier)))
                return;
            
            final Map<Player, ItemStack> s_pair = new HashMap<>();
            
            s_pair.put(p, sc);
            
            if (queue.contains(s_pair))
            {
                p.sendMessage(color("&cYou are on a cooldown of &4" + substance_cooldown.get(s_identifier) + " &cseconds !"));
                return;
            }
            
            getServer().getScheduler().runTaskLater
            (
                plugin,
                    
                new Runnable()
                {
                    @Override public void run()
                    {
                        p.addPotionEffects(substance_genetics.get(s_identifier));
                        e.getItem().setAmount(e.getItem().getAmount() - 1);                        
                    };
                },
                    
                3
            );
            
            if (!queue.contains(s_pair) && !p.hasPermission(admin_permission))
            {
                queue.add(s_pair);
                
                getServer().getScheduler().runTaskLaterAsynchronously
                (
                    plugin,

                    new Runnable()
                    {
                        @Override public void run()
                        {
                            if (queue.contains(s_pair))
                            {
                                queue.remove(s_pair);
                            };

                            if (p.isOnline())
                            {
                                p.sendMessage(color("&aYou may consume another one of those &d" + sc.getItemMeta().getDisplayName() + " &a!"));
                            };
                        };
                    },

                    substance_cooldown.get(s_identifier) * 20
                );
            };
        };
    };
    
    String substance_list = color("&aAvailable Substances: ");
    
    class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only use this command as a player!");
                return false;
            };
            
            final Player p = (Player) s;
            
            if (!p.hasPermission(admin_permission))
            {
                p.sendMessage(color("&cYou have insufficient permissions to be doing this!"));
                return false;
            }
            
            else if (as.length >= 1)
            {
                a = as[0].toLowerCase();
                
                if (a.equals("reload"))
                {
                    p.sendMessage(color("&eLoading ...."));
                    
                    LoadFancyConfiguration();
                    
                    p.sendMessage(color("&eDone!"));
                    
                    return true;
                }
                
                else if (a.equals("give"))
                {
                    if (as.length < 3)//give substance amount player
                    {
                        p.sendMessage(color("&cYou lack arguments, try something alike: &4/bs give HyperSugar 10 Dashie"));
                        return false;
                    };
                    
                    if (!substance_ids.contains(as[1].toLowerCase()))
                    {
                        p.sendMessage(color("&cThis substance does not exist, perhaps create it in the config.yml?"));
                        return false;
                    };
                    
                    final ItemStack substance = subsub_s.get(as[1].toLowerCase());
                    
                    if (substance == null) return false;
                    
                    final int s_amount = GetInteger(as[2]);
                    
                    if (s_amount == 30)
                    {
                        p.sendMessage(color("&cYou must specify a numeric give value!"));
                        return false;
                    };
                    
                    substance.setAmount(s_amount);
                    
                    Player receiver = (Player) p;
                    
                    if (as.length > 3)
                    {
                        receiver = (Player) getServer().getPlayerExact(as[3]);
                        
                        if (receiver == null)
                        {
                            p.sendMessage(color("&cThe specified player must be online!"));
                            return false;
                        };
                    };
                    
                    if (receiver.equals(p))
                    {
                        p.sendMessage(color("&aYou have given yourself &e" + s_amount + "x " + substance.getItemMeta().getDisplayName() + " &a!"));
                    }
                    
                    else 
                    {
                        receiver.sendMessage(color("&aYou have been given &e" + s_amount + "x " + substance.getItemMeta().getDisplayName() + " &a!"));                        
                        p.sendMessage(color("&aYou have given the player &e" + receiver.getName() + s_amount + "x " + substance.getItemMeta().getDisplayName() + " &a!"));
                    };
                    
                    receiver.getInventory().addItem(substance);                    
                    return true;
                }
                
                else if (a.equals("list"))
                {
                    p.sendMessage(substance_list);
                    return true;
                };
            };
            
            p.sendMessage(color("&cCorrect syntax: &4/bs [reload | list | give] <substance> <amount> <player>"));            
            return true;
        };
    };
    
    @Override public void onDisable()
    {
        print("Well, now I am dead!");
    };
    
    void print(final String line)
    {
        System.out.println("(Better Substances): " + line);
    };
    
    String color(final String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
};