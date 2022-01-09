package www.pugpawz.com;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;


public final class EventListener implements Listener {
    @EventHandler public final void onPlayerInteract(final PlayerInteractEvent E) {
        final Block clickedBlock = E.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        final Material blockType = clickedBlock.getType();
        if (!Base.sitObjectWhitelist.contains(blockType)) {
            return;
        }
        final boolean rightClick = E.getAction().equals(Action.RIGHT_CLICK_BLOCK);
        final boolean leftClick = E.getAction().equals(Action.LEFT_CLICK_BLOCK);
        final Location blockLocation = clickedBlock.getLocation();
        final ItemStack itemInHand = E.getItem();
        if (itemInHand != null) {
            if (rightClick) {
                if (Base.sitTriggerWhitelist.contains(itemInHand.getType())) {
                    Sit.PlayerSit(E.getPlayer(), false, blockLocation, clickedBlock);
                }
            }
        }
        else {
            if ((Base.sitByLeftHand && leftClick) || (Base.sitByRightHand && rightClick)) {
                Sit.PlayerSit(E.getPlayer(), false, blockLocation, clickedBlock);
            }
        }
    }   // future Dashie: Automatically reposition the ArmorStand according to
        //  the user movement; that will fix the inability to sit righteously.
    @EventHandler public final void onEntityDismount(final EntityDismountEvent E) {
        if (E.getDismounted() instanceof ArmorStand) {
            if (E.getEntity() instanceof Player) {
                final ArmorStand armorStand = (ArmorStand) E.getDismounted();
                final Player p = (Player) E.getEntity();
                if (armorStand.hasMetadata("identifier")) {
                    armorStand.remove();
                    Sit.PlayerUnsit(p, armorStand);
                }
            }
        }
    }
}
