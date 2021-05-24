// Author: Dashie
// Version: 1.0
package com.kvinnekraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DashMobSpawn extends JavaPlugin
{
    private String Colorize(final String data)
    {
        return ChatColor.translateAlternateColorCodes('&', data);
    }

    private void SendLog(final String msg)
    {
        System.out.println("(DashMobSpawn): " + msg);
    }

    private void Error(Exception E)
    {
        SendLog("An error has occurred, making the plugin unusable.  I will have to disable myself to save your server unnecessary resources.  Bye! ;c");
        SendLog("If you want to help me solve this issue, perhaps send this:\r\n" + E.getMessage() + "\r\nto my email at KvinneKraft@protonmail.com.");

        inst.getPluginLoader().disablePlugin(inst);
    }

    private int GetInteger(final String data)
    {
        try
        {
            return Integer.parseInt(data);
        }

        catch (final Exception E)
        {
            return -1;
        }
    }


    private List<List<EntityType>> EntityTypes = new ArrayList();
    private List<List<ItemStack>> ItemData = new ArrayList();
    private List<Integer> SpawnChances = new ArrayList();

    private FileConfiguration config = null;

    private void LoadConfiguration()
    {
        try
        {
            saveDefaultConfig();

            inst.reloadConfig();
            config = inst.getConfig();

            EntityTypes = new ArrayList<>();
            ItemData = new ArrayList<>();

            String[] equipmentNodes = new String[]
            {
                "handslot", "helmet", "chestplate", "leggings", "boots"
            };

            for (int k = 1; ;k += 1)
            {
                String node = ("mobs." + k);

                if (!config.contains(node))
                {
                    break;
                }

                final int probability = GetInteger(config.getString(node + ".spawn-chance"));

                if (probability < 1)
                {
                    SendLog("The probability specified at node " + node + ".spawn-chance was found to be invalid.");
                    SendLog("Skipping ....");

                    continue;
                }

                final List<EntityType> entities = new ArrayList<>();

                try
                {
                    /*[Mob Handling]*/
                    final String entityNode = node + ".mob-types";
                    final String[] entityStrings = config.getString(entityNode).toUpperCase().replace(" ", "").split(",");

                    for (final String entityString : entityStrings)
                    {
                        try
                        {
                            entities.add(EntityType.valueOf(entityString));
                        }

                        catch (final Exception E)
                        {
                            SendLog("Invalid entity type specified at node " + k + "!");
                            SendLog("Skipping ....");
                        }
                    }
                }

                catch (final Exception E)
                {
                    SendLog("There was an error while trying to load entity related data in config at node " + k + "!");
                    SendLog("Skipping ....");

                    continue;
                }

                final List<ItemStack> items = new ArrayList<>();

                try
                {
                    node = node + ".mob-layout.";

                    for (int s = 0; s < equipmentNodes.length; s += 1)
                    {
                        if (!config.contains(node + equipmentNodes[s]))
                        {
                            throw new Exception("!");
                        }

                        /*[Item Handling]*/
                        final String materialName = config.getString(node + equipmentNodes[s] + ".item");
                        final Material material = Material.getMaterial(materialName);

                        if (material == null)
                        {
                            throw new Exception("!");
                        }

                        final String enchantmentNode = (node + equipmentNodes[s] + ".enchants");
                        final ItemStack item = new ItemStack(material, 1);

                        for (final String entry : config.getStringList(enchantmentNode))
                        {
                            try
                            {
                                final String[] entryData = entry.toUpperCase().replace(" ", "").split(" ");

                                final Enchantment enchantment = Enchantment.getByName(entryData[0]);
                                final Integer enchantmentLvl = GetInteger(entryData[1]);

                                if (enchantment == null || enchantmentLvl < 1)
                                {
                                    throw new Exception("!");
                                }

                                item.addUnsafeEnchantment(enchantment, enchantmentLvl);
                            }

                            catch (final Exception E)
                            {
                                SendLog("Enchantment [" + entry + "] at " + enchantmentNode + " is invalid.");
                                SendLog("Skipping ....");
                            }
                        }

                        items.add(item);
                    }
                }

                catch (final Exception E)
                {
                    SendLog("There was an error while trying to load item related data in config at node " + k + "!");
                    SendLog("Skipping ....");
                }

                SpawnChances.add(probability);
                EntityTypes.add(entities);
                ItemData.add(items);
            }
        }

        catch (Exception E)
        {
            Error(E);
        }
    }


    class EventHandlers implements Listener
    {
        final Random rand = new Random();

        final void onMobSpawn(final EntitySpawnEvent E)
        {
            if (!(E instanceof LivingEntity))
            {
                return;
            }

            // Test if configuration loader works
            // Handle spawn events
            // Handle probabilities
        }
    }


    final JavaPlugin inst = this;

    @Override
    public final void onEnable()
    {
        try
        {
            getServer().getScheduler().runTaskTimerAsynchronously(inst, this::LoadConfiguration, 100, 100);

            LoadConfiguration();

            getServer().getPluginManager().registerEvents(new EventHandlers(), inst);
        }

        catch (Exception E)
        {
            Error(E);
        }
    }

    @Override
    public final void onDisable()
    {
        try
        {
            SendLog("Bye!!");
        }

        catch (Exception E)
        {
            Error(E);
        }
    }
}