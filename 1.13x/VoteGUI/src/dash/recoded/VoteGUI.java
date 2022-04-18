// Author: Dashie
// Version: 1.0

package dash.recoded;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteGUI extends JavaPlugin
{
    final HashMap<ItemStack, String> urls = new HashMap<>();
    
    void LoadConfiguration()
    {
        saveDefaultConfig();        
        
        if (plugin == null)
            plugin = (JavaPlugin) this;
        
        plugin.reloadConfig();
        config = (FileConfiguration) plugin.getConfig();
        
        if (inventory == null)
        {
            inventory = (Inventory) getServer().createInventory(null, 27, color(config.getString("gui-title")));            
        };
            
        final ItemStack vod = new ItemStack(Material.AIR, 1);        
        final Exception exy = new Exception("Exy");
        
        for (final String line : config.getStringList("vote-links"))
        {
            String[] genetics = (String[]) line.split(" ");
            
            if (genetics.length < 5)
            {
                print("ERROR at [" + line + "] Skipping ....");
                continue;
            };
            
            try
            {
                final int sloty = Integer.parseInt(genetics[0]) - 1;
                
                if (sloty < 1 || sloty > 27)
                {
                    throw exy;
                };
                
                final ItemStack item = new ItemStack((Material) Material.getMaterial(genetics[1].toUpperCase()), 1);
                final ItemMeta meta = item.getItemMeta();
                
                final String name = color(genetics[2]).replace("_", " ");
                final String lore = color(genetics[3]).replace("_", " ");                
                
                meta.setDisplayName(name); 
                meta.setLore(Arrays.asList(lore));
                
                item.setItemMeta(meta);
                
                inventory.setItem(sloty, item);
                
                urls.put(item, genetics[4]);
                
                vod.setType(Material.getMaterial(config.getString("empty-space-block")));
            }
            
            catch (final Exception e)
            {
                print("ERROR at [" + line + "] Skipping ....");
            };
        };
        
        final ItemMeta meta = (ItemMeta) vod.getItemMeta();
        meta.setDisplayName(" ");
        
        vod.setItemMeta(meta);
        
        for (int i = 0; i < inventory.getStorageContents().length; i += 1)
        {
            if (inventory.getStorageContents()[i] == null || inventory.getStorageContents()[i].getType().equals(Material.AIR))
            {
                if (i == inventory.getStorageContents().length - 1)
                {
                    final ItemMeta murta = (ItemMeta) vod.getItemMeta();
                    murta.setDisplayName(color("&d&0Plugin by KvinneKraft"));
                    vod.setItemMeta(murta);                    
                };
                
                inventory.setItem(i, vod);
            };
        };
    };
    
    @Override public void onEnable()
    {
        print("I am swimming up ...");
        
        LoadConfiguration();
        
        print
        (
            (
                "\n-=-=-=-=-=-=-=-=-=-=-=-\n" +
                "Author: Dashie, KvinneKraft\n" +
                "Version: 1.0\n" +
                "Github: https://github.com/KvinneKraft\n" +
                "Contact: KvinneKraft@protonmail.com\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-"
            )
        );
        
        getServer().getPluginManager().registerEvents(new Events(), plugin);
        getCommand("votegui").setExecutor(new Commands());
        
        print("I am alive now!");
    };
    
    FileConfiguration config;
    JavaPlugin plugin;
    
    final List<Player> inventory_queue = new ArrayList<>();
    
    class Events implements Listener
    {
        @EventHandler public void onInventoryClick(final InventoryClickEvent e)
        {
            if (e.getWhoClicked() instanceof Player)
            {
                final Player p = (Player) e.getWhoClicked();
                
                if (inventory_queue.contains(p))
                {
                    if (urls.containsKey(e.getCurrentItem()))
                    {
                        p.sendMessage(color("&aLink: &e" + urls.get(e.getCurrentItem())));
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 20, 20);
                    }
                    
                    else
                    {
                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 20, 20);
                    };
                    
                    e.setCancelled(true);
                };
            };
        };
        
        @EventHandler public void onInventoryClose(final InventoryCloseEvent e)
        {
            final Player p = (Player) e.getPlayer();
            
            if (inventory_queue.contains(p))
            {
                inventory_queue.remove(p);
            };
        };
    };
    
    Inventory inventory = (Inventory) null;    
    
    class Commands implements CommandExecutor
    {
        @Override public boolean onCommand(final CommandSender s, final Command c, final String a, final String[] as)
        {
            if (!(s instanceof Player))
            {
                print("You may only use this as a player!");
                return false;
            };
            
            final Player p = (Player) s;
            
            if (p.hasPermission("admin") || p.isOp())
            {
                if (as.length >= 1 && as[0].equalsIgnoreCase("reload"))
                {
                    p.sendMessage(color("&aReloading ...."));
                    
                    LoadConfiguration();
                    
                    p.sendMessage(color("&aDone!"));
                    
                    return true;
                };
            };
            
            p.openInventory(inventory);
            inventory_queue.add(p);

            return true;
        };
    };
    
    void print(final String line)
    {
        System.out.println("(Vote GUI): " + line);
    };
    
    String color(final String line)
    {
        return ChatColor.translateAlternateColorCodes('&', line);
    };
    
    @Override public void onDisable()
    {
        print("I am dead!");
    };
};