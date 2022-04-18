
// Author: Dashie
// Version: 1.0

package com.dashsmelty;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class Furnace implements Listener
{
    List<String> raw_melt_recipes;
    
    public boolean doesSourceExist(String source)
    {
        for(String recipe : raw_melt_recipes)
        {
            if(recipe.split(" ")[0].toLowerCase().equals(source.toLowerCase()))
            {
                return true;
            };
        };
        
        return false;
    };
    
    public int indexOfSource(String source)
    {
        for(int i = 0; i < raw_melt_recipes.size(); i += 1)
        {
            if(raw_melt_recipes.get(i).split(" ")[0].toLowerCase().equals(source.toLowerCase()))
            {
                return i;
            };
        };
        
        return -1;
    };
    
    public void SetConfigRecipes()
    {
        DashSmelter.config.set("meltables", raw_melt_recipes);
        
        Moon.SaveConfig();
        RegisterRecipes();
        
        return;
    };
    
    public void RegisterRecipes()
    {   
        Bukkit.getServer().resetRecipes();
        
        DashSmelter.plugin.reloadConfig();
        DashSmelter.plugin.getConfig();
        
        DashSmelter.config = DashSmelter.plugin.getConfig();
        
        raw_melt_recipes = DashSmelter.config.getStringList("meltables");        
        
        for(String raw_recipe : raw_melt_recipes)
        {
            String[] recipe = raw_recipe.split(" ");
                
            if(recipe.length < 3)
                continue;
               
            Material source_item = Material.getMaterial(recipe[0]);
            Material result_item = Material.getMaterial(recipe[1]);
                
            Integer result_amount = Integer.valueOf(recipe[2]);
            
            if((source_item == null) || (result_item == null) || (result_amount == null))
            {
                Moon.Print("Invalid recipe received, skipping ....");
                continue;
            };
            
            ItemStack result_stack = new ItemStack(result_item, result_amount);
            FurnaceRecipe furnace_recipe = new FurnaceRecipe(result_stack, source_item);
            
            Bukkit.getServer().addRecipe(furnace_recipe);
        };
    };
};
