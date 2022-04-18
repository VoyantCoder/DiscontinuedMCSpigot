
package com.dashie;

import java.util.Arrays;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

//(To-Do):
//- Link the Moony Library up with this.
//- Add Command Support for the Config (base command=dashheads)
//- Make everything configurable

// <Description>
//
// A completely revamped version of Dash Money.
//
// With more functionality, less crap and a more
// user-friendly way of use.

public class DashHeads extends JavaPlugin
{   
    public FileConfiguration config = getConfig();
    public JavaPlugin plugin = this;
    
    public Economy econ;
    
    EventsHandler events_handler = new EventsHandler();
    CommandsHandler commands_handler = new CommandsHandler();
    
    @Override
    public void onEnable()
    {
        Moony.Print("Loading Dash Heads 1.0 ....");
        
        saveDefaultConfig();
        
        if(!hasVault())
        {
            Moony.Print("VAULT could not be found, it is required!");
            this.getPluginLoader().disablePlugin(this);
        };        
        
        econ = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        
        RefreshData();
        
        getServer().getPluginManager().registerEvents(events_handler, plugin);
        getCommand("dashheads").setExecutor(commands_handler);
        
        Moony.Print("Dash Heads 1.0 is now up and running!");
    };
    
    public void RefreshData()
    {
        plugin.getConfig();
        plugin.reloadConfig();
      
        config = plugin.getConfig();
        
        commands_handler.permission_denied_message = Moony.transStr("&cYou may not use this command.");
        commands_handler.correct_syntax_message = Moony.transStr("&aCorrect Syntax: &e/dashheads [reload | info]");
        commands_handler.information_message = Moony.transStr("&eHi, I &d&lD&5a&d&ls&5h&d&li&5e &eam the Developer of this plugin. See more of my work at &bhttps://github.com/KvinneKraft/Dashnarok");
        commands_handler.reloading_message = Moony.transStr("&aReloading Dash Heads 1.0 ....");
        commands_handler.reloaded_message = Moony.transStr("&aDash Heads 1.0 has been reloaded!");        
        
        commands_handler.admin_command_permission = config.getString("dashheads.admin");
    
        events_handler.drop_permission = config.getString("head-drop-permission");
        
        events_handler.victim_message = Moony.transStr(config.getString("victim-message"));        
        events_handler.killer_message = Moony.transStr(config.getString("killer-message"));
        
        events_handler.reward_percentage = config.getDouble("reward-percentage");
        events_handler.minimum_balance = config.getDouble("minimum-balance");        
    };
    
    class CommandsHandler implements CommandExecutor
    {
        boolean t = true, f = false;
        
        String admin_command_permission, permission_denied_message, correct_syntax_message, information_message,
               reloading_message, reloaded_message;
        
        @Override
        public boolean onCommand(CommandSender s, Command c, String a, String[] as)
        {
            if(!(s instanceof Player))
            {
                Moony.Print("You may only use this command in-game.");
                return f;
            }
            
            Player p = (Player) s;
            
            if(!p.hasPermission(admin_command_permission))
            {
                p.sendMessage(permission_denied_message);
                return f;
            }
            
            else if(as.length < 1)
            {
                p.sendMessage(correct_syntax_message);
                return f;
            }
            
            a = as[0].toLowerCase();
            
            if(a.equals("reload"))
            {
                p.sendMessage(reloading_message);
                
                RefreshData();
                
                p.sendMessage(reloaded_message);
            }
            
            else if(a.equals("info"))
            {
                p.sendMessage(information_message);
            }            
            
            else
            {
                p.sendMessage(correct_syntax_message);
                return f;
            };
                
            return t;
        };
    };
    
    @Override
    public void onDisable()
    {
        Moony.Print("Dash Heads 1.0 is now disabled.");
    };
    
    class EventsHandler implements Listener
    {
        String drop_permission, victim_message, killer_message;
        
        double reward_percentage, minimum_balance;
        
        @EventHandler
        public void onPlayerDeath(PlayerDeathEvent e)
        {
            if(!(e.getEntity().getKiller() instanceof Player))
                return;
            
            final Player killer = e.getEntity().getKiller();            
            
            if(!killer.hasPermission(drop_permission))
                return;
            
            final Player victim = e.getEntity();
            
            if(killer.equals(victim))
                return;
            
            int killer_reward = 0; 
            
            if(econ.getBalance(victim) > minimum_balance)
            {
                final double balance = econ.getBalance(victim);
                
                killer_reward = (int)((balance / 100) * 8);
                econ.withdrawPlayer(victim, killer_reward);
            };
            
            victim.sendMessage(victim_message.replace("%k%", killer.getName()).replace("%v%", victim.getName()).replace("%m%", String.valueOf((int)killer_reward)));
            killer.sendMessage(killer_message.replace("%k%", killer.getName()).replace("%v%", victim.getName()).replace("%m%", String.valueOf((int)killer_reward)));
            
            killer.getInventory().addItem(HeadItem(killer_reward, killer.getName(), victim));            
            
            return;
        };
        
        List<String> head_lore = config.getStringList("player-head-lore");
        String head_name = Moony.transStr(config.getString("player-head-name"));
        
        boolean first_run = true;
        
        private ItemStack HeadItem(double killer_reward, String killer_name, Player victim)
        {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta head_meta = (SkullMeta)head.getItemMeta();
            
            for(String line : head_lore)
                head_lore.set(head_lore.indexOf(line), Moony.transStr(line));
            
            String name = head_name.replace("%m%", String.valueOf(killer_reward)).replace("%p%", victim.getName());                        
            List<String> lore = head_lore;
            
            if(first_run)
            {
                for(String line : head_lore)
                {
                    head_lore.set(head_lore.indexOf(line), Moony.transStr(line));                
                };
                
                first_run = false;
            };
            
            for(int id = 0; id < lore.size(); id += 1)
                lore.set(id, lore.get(id).replace("%m%", String.valueOf(killer_reward).replace("%p%", killer_name)));
           
            head_meta.setCustomModelData(2020);            
            head_meta.setOwningPlayer(victim);
            head_meta.setDisplayName(name);
            head_meta.setLore(lore);
            
            head.setItemMeta(head_meta);
            
            return head;
        };
        
        String redeem_message = Moony.transStr(config.getString("redeem-message"));
        
        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent e)
        {
            if((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK))
                return;
            
            if((e.getItem() == null) || (e.getMaterial() == null) || (!e.getItem().hasItemMeta()) || (e.getMaterial() != Material.PLAYER_HEAD) || (e.getItem().getItemMeta().getCustomModelData() != 2020))
                return;
            
            double money = 0;
            
            for(String lore : e.getItem().getItemMeta().getLore())
            {
                if(lore.contains("$"))
                {
                    money = Double.valueOf(lore.replaceAll("[^\\d.]", ""));
                    money = money * e.getItem().getAmount();
                    
                    break;
                };
            };
            
            Player p = e.getPlayer();
                   
            p.sendMessage(redeem_message.replace("%m%", String.valueOf(money)));
            p.getInventory().removeItem(e.getItem());
            
            econ.depositPlayer(p, money);

            return;
        };
    };
    
    public boolean hasVault()
    {
        if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        
        else
            return true;
    };    
};

class Moony
{
    public static void Print(String str)
    {
        System.out.println("(Dash Heads): " + str);
    };
    
    public static String transStr(String str)
    {
        return ChatColor.translateAlternateColorCodes('&', str);
    };
};