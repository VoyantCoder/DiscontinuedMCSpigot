// Author: Dashie
// Version: 1.0
package com.kvinnekraft;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Creature;
import org.bukkit.event.Listener;
import org.bukkit.ChatColor;
import org.bukkit.Material;

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


    // Triple Class with multi lists?
    private List<List<PotionEffect>> potionEffects = new ArrayList<>();
    private List<List<EntityType>> entityTypes = new ArrayList();
    private List<List<ItemStack>> itemData = new ArrayList();
    private List<Integer> entityHealths = new ArrayList<>();
    private List<Integer> spawnChances = new ArrayList();

    private FileConfiguration config = null;

    private void LoadConfiguration()
    {
        try
        {
            saveDefaultConfig();

            inst.reloadConfig();
            config = inst.getConfig();

            potionEffects = new ArrayList<>();
            entityHealths = new ArrayList<>();
            spawnChances = new ArrayList<>();
            entityTypes = new ArrayList<>();
            itemData = new ArrayList<>();

            String[] equipmentNodes = new String[]
            {
                "handslot", "leggings", "chestplate", "helmet", "boots"
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

                final int health = GetInteger(config.getString(node + ".mob-health"));

                if (health < 1)
                {
                    SendLog("The health value specified at node " + node + ".mob-health was found to be invalid.");
                    SendLog("Skipping ....");

                    continue;
                }

                final List<PotionEffect> potEffects = new ArrayList<>();

                try
                {
                    final String potionNode = node + ".potion-effects";
                    final List<String> potionData = config.getStringList(potionNode);

                    for (int s = 0; s < potionData.size(); s += 1)
                    {
                        final String[] rawData = potionData.get(s).toUpperCase().split(" ");

                        if (rawData.length != 2)
                        {
                            SendLog("One or more potion effects at node " + node + " appears to be invalid.");
                            SendLog("Skipping ....");

                            continue;
                        }

                        final PotionEffectType effectType = PotionEffectType.getByName(rawData[0]);
                        final int power = GetInteger(rawData[1]);

                        if (effectType == null || power <= 0)
                        {
                            SendLog("One or more potion effects at node " + node + " appears to be invalid.");
                            SendLog("Skipping ....");

                            continue;
                        }

                        final PotionEffect potionEffect = new PotionEffect(effectType, 9999 * 20, power);

                        potEffects.add(potionEffect);
                    }
                }

                catch (final Exception E)
                {
                    SendLog("There was an error while trying to load potion data at node " + k + "!");
                    SendLog("Skipping ....");

                    continue;
                }

                final List<EntityType> entities = new ArrayList<>();

                try
                {
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
                    SendLog("There was an error while trying to load entity related data at node " + k + "!");
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
                                final String[] entryData = entry.toUpperCase().split(" ");

                                final Enchantment enchantment = Enchantment.getByName(entryData[0].toUpperCase());
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
                    SendLog("There was an error while trying to load item related data at node " + k + "!");
                    SendLog("Skipping ....");
                }

                this.potionEffects.add(potEffects);
                this.entityHealths.add(health);
                this.spawnChances.add(probability);
                this.entityTypes.add(entities);
                this.itemData.add(items);
            }
        }

        catch (Exception E)
        {
            Error(E);
        }
    }


    class EventHandlers implements Listener
    {
        final ItemStack[] ToArray(List<ItemStack> itemStack, int startIndex)
        {
            final ItemStack[] data = new ItemStack[itemStack.size()];

            for (int k = startIndex; k < data.length; k += 1)
            {
                data[k] = itemStack.get(k);
            }

            return data;
        }

        final Random rand = new Random();

        @EventHandler
        final void onMobSpawn(final CreatureSpawnEvent E)
        {
            if (!(E.getEntity() instanceof Creature))
            {
                return;
            }

            Creature critter = (Creature) E.getEntity();

            getServer().getScheduler().runTaskAsynchronously
            (
                inst,

                () ->
                {
                    for (int k = 0, r = 0; k < entityTypes.size(); k += 1)
                    {
                        for (int s = 0; s < entityTypes.get(k).size(); s += 1)
                        {
                            final EntityType entityType = entityTypes.get(k).get(s);

                            if (entityTypes.get(k).get(s) != entityType)
                            {
                                continue;
                            }

                            if (rand.nextInt(100) > spawnChances.get(k))
                            {
                                continue;
                            }

                            if (itemData.get(k).size() > 0)
                            {
                                final ItemStack weapon = itemData.get(k).get(0);

                                if (weapon != null)
                                {
                                    critter.getEquipment().setItemInMainHand(itemData.get(k).get(0));
                                }

                                final ItemStack[] armor = ToArray(itemData.get(k), 1);

                                if (armor != null && armor.length > 0)
                                {
                                    critter.getEquipment().setArmorContents(armor);
                                }
                                
                                final int _k = k;

                                getServer().getScheduler().runTask
                                (
                                    inst,

                                    () ->
                                    {
                                        critter.addPotionEffects(potionEffects.get(_k));
                                        critter.setMaxHealth(entityHealths.get(_k));
                                        critter.setHealth(entityHealths.get(_k));
                                    }
                                );

                                r = 1;
                                break;
                            }
                        }

                        if (r == 1)
                        {
                            break;
                        }
                    }
                }
            );
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