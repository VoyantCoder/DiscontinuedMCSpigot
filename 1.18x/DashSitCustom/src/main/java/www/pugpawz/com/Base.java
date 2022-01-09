package www.pugpawz.com;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;


public final class Base {
    public static void Print(final String message) {
        System.out.println(message);
    }
    public static String Color(final String message) {
        if (message != null) {
            return ChatColor.translateAlternateColorCodes('&', message);
        }
        else {
            return "";
        }
    }
    public static boolean PlayerHasPermission(final Player p, final boolean v) {
        if (!sitAuthorizationPermission.equalsIgnoreCase("none")) {
            if (!p.hasPermission(sitAuthorizationPermission)) {
                if (v) {
                    p.sendMessage(sitMessages.get("denied"));
                }
                return false;
            }
        }
        return true;
    }

    public final static ArrayList<Material> sitTriggerWhitelist = new ArrayList();
    public final static ArrayList<Material> sitObjectWhitelist = new ArrayList();
    public final static HashMap<String, String> sitMessages = new HashMap();

    public static String sitAuthorizationPermission = "";
    public static boolean sitByRightHand = false;
    public static boolean sitByLeftHand = false;

    public static FileConfiguration config = null;
    public static JavaPlugin instance = null;

    private static boolean IsActualMaterial(final String itemName) {
        Material material = Material.getMaterial(itemName);
        if (material == null) {
            material = Material.getMaterial(itemName, true);
            if (material == null) {
                Base.Print("Interaction material name " + itemName + " could not be resolved. Skipping...");
                return false;
            }
        }
        return true;
    }
    private static boolean GetBooleanFromConfig(String path) {
        final String value = config.getString(path);
        if (value != null) {
            switch (value.toLowerCase()) {
                case "enabled":
                case "enable":
                case "true":
                    return true;
            }
        }
        return false;
    }
    public static void GetConfigData() {
        instance.saveDefaultConfig();
        instance.reloadConfig();
        config = instance.getConfig();

        final String sitdown = config.getString("sit-down-message");
        final String getup = config.getString("get-up-message");
        final String denied = config.getString("permission-denied-message");

        sitMessages.put("sitdown", Color(sitdown));
        sitMessages.put("getup", Color(getup));
        sitMessages.put("denied", Color(denied));

        sitTriggerWhitelist.clear();
        sitObjectWhitelist.clear();

        for (final String itemName : config.getStringList("allow-sit-interaction-for")) {
            if (IsActualMaterial(itemName)) {
                sitTriggerWhitelist.add(Material.getMaterial(itemName));
            }
        }
        for (final String itemName : config.getStringList("allow-sitting-on")) {
            if (IsActualMaterial(itemName)) {
                sitObjectWhitelist.add(Material.getMaterial(itemName));
            }
        }

        sitAuthorizationPermission = config.getString("must-have-sit-permission");
        sitByLeftHand = GetBooleanFromConfig("left-hand-sit-interaction");
        sitByRightHand = GetBooleanFromConfig("right-hand-sit-interaction");
    }
}
