
// Author: Dashie
// Version: 1.0


package com.dashrobot;


import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;


//---//
//---// Main Data Refresh Class:
//---//

public class Refresh
{
    public static void reload_action()
    {
        Captcha.plugin.reloadConfig();
        Captcha.config = Captcha.plugin.getConfig();
        
        FileConfiguration config = Captcha.config;

        Captcha.events.timeout_kick_message = Captcha.color(config.getString("dash-captcha.messages.timeout-kick-message"));
        Captcha.events.maximum_succeed_message = Captcha.color(config.getString("dash-captcha.messages.too-many-tries-kick-message"));
        Captcha.events.captcha_title = Captcha.color(config.getString("dash-captcha.messages.captcha-title"));
        Captcha.events.captcha_complete_message = Captcha.color(config.getString("dash-captcha.messages.captcha-complete-message"));        
        
        Captcha.events.inventory_slots = config.getInt("dash-captcha.properties.inventory-slots");
        Captcha.events.verification_timeout = config.getInt("dash-captcha.properties.verification-timeout");
        Captcha.events.maximum_attempts = config.getInt("dash-captcha.properties.maximum-attempts");
        
        Captcha.events.send_as_title = config.getBoolean("dash-captcha.properties.send-as-title");
        Captcha.events.summon_fireworks = config.getBoolean("dash-captcha.properties.summon-fireworks");
        Captcha.events.summon_lightning = config.getBoolean("dash-captcha.properties.summon-lightning");
        Captcha.events.wither_sound = config.getBoolean("dash-captcha.properties.wither-sound");
        Captcha.events.apply_blind_effect = config.getBoolean("dash-captcha.properties.apply-blind-effect");
        Captcha.events.block_movement = config.getBoolean("dash-captcha.properties.block-movement");
        Captcha.events.block_chat = config.getBoolean("dash-captcha.properties.block-chat");
        Captcha.events.block_command = config.getBoolean("dash-captcha.properties.block-command");
        
        if(Captcha.events.verify_cache.size() > 0)
        {
            Captcha.events.verify_cache.clear();
        };
        
        Captcha.events.once_verify = config.getBoolean("dash-captcha.properties.verify-once");
        
        if((Captcha.events.pattern_items != null) && (Captcha.events.pattern_items.size() > 0)) 
        {
            Captcha.events.pattern_items.clear();
        };
        
        for(String item : config.getStringList("dash-captcha.captcha-items"))
        {
            Material material = Material.getMaterial(item.toUpperCase().replace(" ", "_"));
            
            if(material == null)
            {
                Captcha.print("Invalid captcha item received: " + item + "!");
                continue;
            };
            
            Captcha.events.pattern_items.add(material);
        };
        
        if((Captcha.events.key_items != null) && (Captcha.events.key_items.size() > 0))
        {
            Captcha.events.key_items.clear();
        };
        
        for(String item : config.getStringList("dash-captcha.key-items"))
        {
            Material material = Material.getMaterial(item.toUpperCase().replace(" ", "_"));
            
            if(material == null)
            {
                Captcha.print("Invalid captcha key item received: " + item + "!");
                continue;
            };
            
            Captcha.events.key_items.add(material);            
        };
        
        Captcha.events.dash_join = config.getBoolean("dash-join.enabled");
        
        Captcha.events.join_message = Captcha.color(config.getString("dash-join.messages.join"));
        Captcha.events.first_join_message = Captcha.color(config.getString("dash-join.messages.first-join"));
        Captcha.events.quit_message = Captcha.color(config.getString("dash-join.messages.quit"));
        
        Captcha.commands.admin_permission = config.getString("dash-captcha.commands.admin-permission");
        Captcha.commands.developer_support = config.getBoolean("dash-captcha.commands.developer-support");
    };
};
