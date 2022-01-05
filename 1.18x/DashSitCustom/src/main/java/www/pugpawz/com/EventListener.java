package www.pugpawz.com;


import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;


public final class EventListener implements Listener {
    @EventHandler public final void onPlayerInteract(final PlayerInteractEvent E) {
        final ItemStack itemInHand = E.getItem();
        if (itemInHand == null) {
            return;
        }
        if (Base.sitTriggerWhitelist.contains(itemInHand.getType())) {
            final Block clickedBlock = E.getClickedBlock();
            if (clickedBlock != null) {
                if (Base.sitObjectWhitelist.contains(clickedBlock.getType())) {
                    Sit.PlayerSit(E.getPlayer(), false, clickedBlock.getLocation(),  clickedBlock);
                }
            }
        }
    }
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
