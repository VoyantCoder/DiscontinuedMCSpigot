package www.pugpawz.com;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public final class Sit {
    public static void PlayerUnsit(final Player p, final ArmorStand armorStand) {
        final Location location = p.getLocation();
        location.setY(location.getY() + 3);
        p.sendMessage(Base.sitMessages.get("getup"));
        p.teleport(location);
    }
    public static float BlockFaceToYaw(BlockFace blockFace) {
        if (blockFace == BlockFace.NORTH) {
            return 100.0F;
        } else if (blockFace == BlockFace.NORTH_NORTH_EAST) {
            return 202.5F;
        } else if (blockFace == BlockFace.NORTH_EAST) {
            return 225.0F;
        } else if (blockFace == BlockFace.EAST_NORTH_EAST) {
            return 247.5F;
        } else if (blockFace == BlockFace.EAST) {
            return 270.0F;
        } else if (blockFace == BlockFace.EAST_SOUTH_EAST) {
            return 292.5F;
        } else if (blockFace == BlockFace.SOUTH_EAST) {
            return 315.0F;
        } else if (blockFace == BlockFace.SOUTH_SOUTH_EAST) {
            return 337.5F;
        } else if (blockFace == BlockFace.SOUTH_SOUTH_WEST) {
            return 22.5F;
        } else if (blockFace == BlockFace.SOUTH_WEST) {
            return 45.0F;
        } else if (blockFace == BlockFace.WEST_SOUTH_WEST) {
            return 67.5F;
        } else if (blockFace == BlockFace.WEST) {
            return 90.0F;
        } else if (blockFace == BlockFace.WEST_NORTH_WEST) {
            return 112.5F;
        } else if (blockFace == BlockFace.NORTH_WEST) {
            return 135.0F;
        } else if (blockFace == BlockFace.NORTH_NORTH_WEST) {
            return 157.5F;
        } else {
            return 0.0F;
        }
    }
    public static void PlayerSit(final Player p, final boolean v, final Location location, final Block clickedBlock) {
        if (!Base.PlayerHasPermission(p, v)) {
            return;
        }
        else if (p.isInsideVehicle()) {
            p.leaveVehicle();
        }

        if (clickedBlock != null && clickedBlock.getBlockData() instanceof Stairs) {
            location.setX(clickedBlock.getX() + 0.5);
            location.setY(clickedBlock.getY() - 1.2);
            location.setZ(clickedBlock.getZ() + 0.5);
            location.setYaw(BlockFaceToYaw(clickedBlock.getFace(clickedBlock)));
        }
        else {
            location.setY(location.getY() - 1.7);
        }

        final ArmorStand chair = (ArmorStand) p.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        final FixedMetadataValue fixedMeta = new FixedMetadataValue(Base.instance, "id-2022");

        chair.setMetadata("identifier", fixedMeta);
        chair.setRemoveWhenFarAway(true);
        chair.setInvulnerable(true);
        chair.setCollidable(true);
        chair.setVisible(false);
        chair.setGravity(false);
        chair.addPassenger(p);

        p.sendMessage(Base.sitMessages.get("sitdown"));
    }
}
