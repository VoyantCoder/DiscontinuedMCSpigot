
// Author: Dashie
// Version: 1.0

package com.dashcash;


import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.command.Command;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandExecutor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.configuration.file.FileConfiguration;


public class DashCash extends JavaPlugin
{
    public static FileConfiguration config;
    public static JavaPlugin plugin;
    public static Economy econ;
    
    public static CommandsHandler commands = new CommandsHandler();
    public static EventsHandler events = new EventsHandler();
    
    private boolean hasVault()
    {
        if(getServer().getPluginManager().getPlugin("Vault") == null)
        {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        
        if(rsp == null)
        {
            return false;
        };
        
        econ = rsp.getProvider();
        return econ != null;
    };
    
    @Override
    public void onEnable()
    {
        KvinneKraft.print("Loading plugin ....");
        
        saveDefaultConfig();
        
        plugin = this;

        if (!hasVault()) 
        {
            KvinneKraft.print("Vault is missing, please install VAULT.");
            getServer().getPluginManager().disablePlugin(plugin);
            
            return;
        };
        
        events.refresh();
        
        getServer().getPluginManager().registerEvents(events, plugin);
        
        commands.refresh();
        
        getCommand("dashcash").setExecutor(commands);
        
        KvinneKraft.print("Plugin has been loaded!");
    };
    
    @Override
    public void onDisable()
    {
        KvinneKraft.print("Plugin has been disabled!");
    };
};


class EventsHandler implements Listener
{    
    List<Integer> min_prices = new ArrayList<>();
    List<Integer> max_prices = new ArrayList<>();
 
    List<String> entities = new ArrayList<>();    
    
    String reward_permission, reward_message;
    
    public void refresh()
    {
        DashCash.plugin.reloadConfig();
        DashCash.config = DashCash.plugin.getConfig();        
        
        reward_permission = DashCash.config.getString("price-properties.price-permission");
        reward_message = KvinneKraft.transStr(DashCash.config.getString("price-properties.price-message"));
        
        if(entities.size() > 0)
        {
            max_prices.clear();
            min_prices.clear();            
            
            entities.clear();
        };
        
        for(String str : DashCash.config.getStringList("price-properties.priced-entity-list"))
        {
            String[] arr = str.split(" ");
            
            if(arr.length < 2)
            {
                KvinneKraft.print("Invalid format specified. Skipping ....");
                continue;
            }
                
            else if(EntityType.fromName(arr[0].toUpperCase()) == null)
            {
                KvinneKraft.print("[" + arr[0] + "]: Invalid entity received. Skipping ....");
                continue;
            }
            
            else if(entities.contains(arr[0].toUpperCase()))
            {
                KvinneKraft.print("[" + arr[0] + "]: The entity was already in list. Skipping....");
                continue;
            };
            
            String[] sarr = arr[1].split("-");
            
            if(sarr.length < 2)
            {
                KvinneKraft.print("[SARR LENGTH: " + sarr.length + "]: Invalid price range specified. Skipping ....");
                continue;
            };
            
            Integer min_price = Integer.valueOf(sarr[0]);
            Integer max_price = Integer.valueOf(sarr[1]);
            
            if((min_price > max_price) || (min_price < 0))
            {
                KvinneKraft.print("[" + sarr[0] + "-" + sarr[1] + "]: Minimum price must be lower than maximum price. Skipping ....");
                continue;
            };
            
            min_prices.add(min_price);
            max_prices.add(max_price);
            
            entities.add(arr[0].toUpperCase());        
        };
    };
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e)
    {
        if(!(e.getEntity().getKiller() instanceof Player))
        {
            return;
        };
        
        Player p = (Player) e.getEntity().getKiller();
        String entity_type = e.getEntity().getType().toString();
        
        if((!p.hasPermission(reward_permission)) || (!entities.contains(entity_type)))
        {
            return;
        };        
        
        String entity_name = entity_type.toLowerCase();
        
        if(e.getEntity() instanceof Player)
        {
            entity_name = ((Player)e.getEntity()).getName();
        };
        
        int entity_id = entities.indexOf(entity_type);
 
        int max_price = max_prices.get(entity_id);
        int min_price = min_prices.get(entity_id);
        
        int reward_cash = new Random().nextInt((max_price - min_price) + 1) + min_price;
        DashCash.econ.depositPlayer(p, reward_cash);
        
        String message = reward_message.replace("%m%", String.valueOf(reward_cash)).replace("%e%", entity_name);
        p.sendMessage(message);
    };
};


class KvinneKraft
{
    public static void print(String str)
    {
        System.out.println("(Dash Cash): " + str);
    };
        
    public static String transStr(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};


class CommandsHandler implements CommandExecutor 
{
    boolean t = true, f = false;
    boolean developer_support;
    
    String admin_permission;
    
    public void refresh()
    {
        DashCash.plugin.reloadConfig();
        DashCash.config = DashCash.plugin.getConfig();        
        
        developer_support = DashCash.config.getBoolean("optional-properties.developer-support");        
        admin_permission = DashCash.config.getString("optional-properties.admin-permission");

        reloading_message = KvinneKraft.transStr("&aDash Cash is now reloading ....");
        reloaded_message = KvinneKraft.transStr("&aDash Cash has been reloaded!");
        
        correct_use_message = KvinneKraft.transStr("&cCorrect use: &7/dashmobs [add | del | list | reload] <entity> <min-price> <max-price>");        
        developer_message = KvinneKraft.transStr("&eMeow Meow, I am Dashie, the Developer of this Plugin, also known as Princess_Freyja!\n\n&eGithub: &ahttps://github.com/KvinneKraft/ \n&eWebsite: &ahttps://pugpawz.com");
        
        removed_message = KvinneKraft.transStr("&aThe specified rule has been removed from the list.");
        added_message = KvinneKraft.transStr("&aThe specified rule has been added to the list.");
        
        invalid_entity_message = KvinneKraft.transStr("&cYou have specified an unknown entity!");        
        invalid_range_message = KvinneKraft.transStr("&cThe range from which the prices vary is invalid.");
        
        already_exists_message = KvinneKraft.transStr("&cThe specified rule already exists.");
        does_not_exist_message = KvinneKraft.transStr("&cThe specified rule does not exist.");
        
        list_is_empty_message = KvinneKraft.transStr("&cYou should add some items first!");
        not_numerical_message = KvinneKraft.transStr("&cYou must specify numerical values for the price range.");
        
        permission_denied_message = KvinneKraft.transStr("&cYou lack sufficient Permissions!");        
        
        done_loading_list_message = KvinneKraft.transStr("&aDone!!");        
        loading_list_message = KvinneKraft.transStr("&aLoading list .....");
    };
    
    String reloading_message, reloaded_message, developer_message, permission_denied_message, correct_use_message, added_message, removed_message, invalid_entity_message, invalid_range_message, already_exists_message, does_not_exist_message, loading_list_message, done_loading_list_message, not_numerical_message, list_is_empty_message;
    
    @Override
    public boolean onCommand(CommandSender s, Command c, String a, String[] as)
    {
        if(!(s instanceof Player))
            return f;
        
        Player p = (Player) s;
        
        if(!p.hasPermission(admin_permission))
        {
            if(developer_support)
            {
                p.sendMessage(developer_message);
            }
            
            else
            {
                p.sendMessage(permission_denied_message);
            };
            
            return f;
        }
        
        else if(as.length < 1)
        {
            p.sendMessage(correct_use_message);
            return f;
        };
        
        a = as[0].toLowerCase();
        
        if(a.equals("reload"))
        {
            p.sendMessage(reloading_message);
            
            DashCash.events.refresh();            
            DashCash.commands.refresh();
            
            p.sendMessage(reloaded_message);
        }
        
        else if(as.length >= 2)
        {
            if(((a.equals("add"))) || (a.equals("del")))
            {
                as[1] = as[1].toUpperCase();
            
                if((as.length >= 4) && (a.equals("add")))
                {
                    if(EntityType.fromName(as[1]) == null)
                    {
                        p.sendMessage(invalid_entity_message);
                        return f;
                    };
                    
                    String rx = "\\d+";
                    
                    if((!as[3].matches(rx)) || (!as[2].matches(rx)))
                    {
                        p.sendMessage(not_numerical_message);
                        return f;
                    };
                    
                    Integer max_price = Integer.valueOf(as[3]);
                    Integer min_price = Integer.valueOf(as[2]);
                    
                    if((min_price > max_price) || (min_price < 0) || (max_price < 1))
                    {
                        p.sendMessage(invalid_range_message);
                        return f;
                    }
                    
                    else if(DashCash.events.entities.contains(as[1]))
                    {
                        p.sendMessage(already_exists_message);
                        return f;
                    };
                
                    DashCash.events.entities.add(as[1]);
                
                    DashCash.events.max_prices.add(max_price);
                    DashCash.events.min_prices.add(min_price);
            
                    p.sendMessage(added_message);
                }
            
                else if (a.equals("del"))
                {
                    if(DashCash.events.entities.contains(as[1]))
                    {
                        int id = DashCash.events.entities.indexOf(as[1]);
                       
                        DashCash.events.max_prices.remove(id);
                        DashCash.events.min_prices.remove(id);
                       
                        DashCash.events.entities.remove(id);

                        p.sendMessage(removed_message);
                    }
                    
                    else
                    {
                        p.sendMessage(does_not_exist_message);
                    };
                }
                
                else
                {
                    p.sendMessage(correct_use_message);
                    return f;
                };
                
                List<String> entity_list = new ArrayList<>();
                
                for(String entity : DashCash.events.entities)
                {
                    int id = DashCash.events.entities.indexOf(entity);
                    
                    String max_price = String.valueOf(DashCash.events.max_prices.get(id));
                    String min_price = String.valueOf(DashCash.events.min_prices.get(id));
                    
                    String priced_line = entity + " " + min_price + "-" + max_price;
                    
                    entity_list.add(priced_line);
                };
                
                DashCash.config.set("price-properties.priced-entity-list", entity_list);
                
                DashCash.plugin.saveConfig();
                DashCash.events.refresh();
            }
        }
        
        else if(a.equals("list"))
        {
            if(DashCash.events.entities.size() < 1)
            {
                p.sendMessage(list_is_empty_message);
                return f;
            };
            
            Bukkit.getScheduler().runTaskAsynchronously(DashCash.plugin,
                new Runnable()
                {//I know I could optimize this...Stawp buggering mehhh!!
                    @Override
                    public void run()
                    {
                        p.sendMessage(loading_list_message + "\n \n");
                        
                        for(String str : DashCash.events.entities)
                        {
                            int id = DashCash.events.entities.indexOf(str);
                            
                            String max_price = String.valueOf(DashCash.events.max_prices.get(id));
                            String min_price = String.valueOf(DashCash.events.min_prices.get(id));
                            
                            str = str + " " + min_price + "$ - " + max_price + "$";
                            
                            p.sendMessage(KvinneKraft.transStr("&7(index:" + String.valueOf(id) + ") &e" + str));
                        };
                        
                        p.sendMessage("\n" + done_loading_list_message);
                    };
                }
            );
        }
        
        else 
        {
            p.sendMessage(correct_use_message);
        };
        
        return t;
    };
};
     