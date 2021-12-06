package de.cuuky.cfw.utils;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.VersionUtils;
import de.cuuky.cfw.version.types.Materials;

public class BlockUtils {

	private static boolean isGrass(Material type) {
		if (!type.toString().contains("GRASS"))
			return false;

		if (VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_12)) {
			return type.toString().contains("GRASS") && !type.toString().contains("BLOCK");
		}
		else {
			return type.toString().equals("LONG_GRASS");
		}
	}

	public static boolean isAir(Block block) {
		if (block == null)
			return true;

		Material type = block.getType();
		return isGrass(type) || type.name().contains("AIR") || Materials.POPPY.parseMaterial() == type || type == Materials.SUNFLOWER.parseMaterial() || type == Materials.LILY_PAD.parseMaterial() || type.name().contains("LEAVES") || type.name().contains("WOOD") || type == Materials.SNOW.parseMaterial() || type.name().contains("GLASS") || type == Materials.VINE.parseMaterial();
	}

	public static boolean isSame(Materials mat, Block block) {
		if (mat.getData() == block.getData() && mat.parseMaterial().equals(block.getType()))
			return true;

		return false;
	}
	
	public static void setBlock(Block block, Materials mat, boolean applyPhysics) {
		block.setType(mat.parseMaterial(), applyPhysics);

		if (!VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_11)) {
			try {
				block.getClass().getDeclaredMethod("setData", byte.class).invoke(block, (byte) mat.getData());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	public static void setBlock(Block block, Materials mat) {
		setBlock(block, mat, true);
	}
	
	public static void setBlockDelayed(JavaPlugin plugin, World world, int x, int y, int z, Materials mat) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> setBlock(world.getBlockAt(x, y, z), mat, false), 1);
	}
	
	public static void setBlockDelayed(JavaPlugin plugin, Block block, Materials mat) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> setBlock(block, mat, false), 1);
	}

	public static Object getBlockData(Block block, Object from) {
		BlockState blockState = block.getState();
		try {
			return from.getClass().getMethod("getBlockData").invoke(from);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			return blockState.getData();
		}
	}

	public static BlockFace getAttachedSignFace(Object sign) {
		BlockFace attachedFace = null;
		try {
			attachedFace = (BlockFace) sign.getClass().getMethod("getAttachedFace").invoke(sign);
		} catch (ClassCastException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			try {
				Object facing = sign.getClass().getMethod("getFacing").invoke(sign);
				attachedFace = (BlockFace) facing.getClass().getMethod("getOppositeFace").invoke(facing);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return attachedFace;
	}
}
