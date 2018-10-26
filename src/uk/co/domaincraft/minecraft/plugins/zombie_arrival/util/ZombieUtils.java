package uk.co.domaincraft.minecraft.plugins.zombie_arrival.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;

import java.util.*;

public class ZombieUtils {

    private static Random rand = new Random();

    public static void createClassedZombie(Zombie zombie, ZombieArrival plugin) {
        switch(rand.nextInt(3)) {
            case 0:
                createLumberjackZombie(zombie, plugin);
                break;
            case 1:
                createAssaultZombie(zombie, plugin);
                break;
            case 2:
                createKamikazeZombie(zombie, plugin);
                break;

        }

    }

    private static void createLumberjackZombie(Zombie zombie, ZombieArrival plugin) {

        ItemStack primaryWeapon, secondaryWeapon;

        switch(rand.nextInt(4)) {
            case 0:
                primaryWeapon = new ItemStack(Material.DIAMOND_AXE);
                secondaryWeapon = new ItemStack(Material.STONE_SWORD);
                break;
            case 1:
                primaryWeapon = new ItemStack(Material.GOLDEN_AXE);
                secondaryWeapon = new ItemStack(Material.WOODEN_SWORD);
                break;
            case 2:
                primaryWeapon = new ItemStack(Material.IRON_AXE);
                secondaryWeapon = new ItemStack(Material.GOLDEN_SWORD);
                break;
            case 3:
                primaryWeapon = new ItemStack(Material.STONE_AXE);
                secondaryWeapon = new ItemStack(Material.WOODEN_SWORD);
                break;
            default:
                primaryWeapon = new ItemStack(Material.WOODEN_AXE);
                secondaryWeapon = new ItemStack(Material.IRON_SWORD);
                break;
        }

        zombie.getEquipment().setItemInMainHand(primaryWeapon);
        zombie.getEquipment().setItemInOffHand(secondaryWeapon);

        zombie.setMetadata("class", new FixedMetadataValue(plugin, "lumberjack"));
    }

    private static void createKamikazeZombie(Zombie zombie, ZombieArrival plugin) {
        zombie.getEquipment().setHelmet(new ItemStack(Material.TNT));

        zombie.setMetadata("class", new FixedMetadataValue(plugin, "kamikaze"));
    }

    private static void createAssaultZombie(Zombie zombie, ZombieArrival plugin) {

        zombie.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
        zombie.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));

        double enchantChance = Math.random();

        if(enchantChance < 0.3) {
            zombie.getEquipment().getItemInMainHand().addEnchantment(Enchantment.DAMAGE_ALL, 2);
        }
        
        double equipChance = Math.random();
        
        if(equipChance < 0.1) {
            zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
            zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
            zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        } else if(equipChance < 0.5) {
            zombie.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
            zombie.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
            zombie.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
            zombie.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        } else if(equipChance < 0.7) {
            zombie.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
            zombie.getEquipment().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
            zombie.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
            zombie.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        } else if(equipChance < 0.9) {
            zombie.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
            zombie.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
            zombie.getEquipment().setLeggings(new ItemStack(Material.GOLDEN_LEGGINGS));
            zombie.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS));
        }

        zombie.setMetadata("class", new FixedMetadataValue(plugin, "assault"));
    }

    public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static void showWardingBoundary(final Player player, ZombieArrival plugin, Location center) {


        Location aboveBlock = center.clone();
        aboveBlock.setY(aboveBlock.getBlockY() + 1);


        final HashMap<Location, BlockData> affectedBlocks = new HashMap<Location, BlockData>();

        for(Location loc : getCornersFromCenter(Constants.WARDING_DISTANCE, center)) {
            affectedBlocks.put(loc, loc.getBlock().getBlockData());
            player.sendBlockChange(loc, Bukkit.createBlockData(Material.GLOWSTONE));
            player.playEffect(loc, Effect.STEP_SOUND, 17);

        }

        affectedBlocks.put(aboveBlock, aboveBlock.getBlock().getBlockData());

        player.sendBlockChange(aboveBlock, Bukkit.createBlockData(Material.ZOMBIE_HEAD));

        player.sendMessage(ChatColor.ITALIC + "An ancient spell temporarily reveals the bounds of this ward...");

        // Reverts block changes to original blocks.
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                for(Map.Entry<Location, BlockData> entry : affectedBlocks.entrySet()) {
                    player.sendBlockChange(entry.getKey(), entry.getValue());
                    player.playEffect(entry.getKey(), Effect.STEP_SOUND, 17);

                }
            }
        }, 100);
    }

    private static ArrayList<Location> getCornersFromCenter(int distance, Location centerPoint) {
        ArrayList<Location> locations = new ArrayList<Location>();
        Location topLeft = centerPoint.clone();
        topLeft.setX(centerPoint.getX() + distance);
        topLeft.setZ(centerPoint.getZ() + distance);

        locations.add(topLeft);

        Location topRight = centerPoint.clone();
        topRight.setX(centerPoint.getX() - distance);
        topRight.setZ(centerPoint.getZ() - distance);

        locations.add(topRight);

        Location loc3 = centerPoint.clone();
        loc3.setZ(centerPoint.getZ() - distance);
        loc3.setX(centerPoint.getX() + distance);

        locations.add(loc3);

        Location loc4 = centerPoint.clone();
        loc4.setZ(centerPoint.getZ() + distance);
        loc4.setX(centerPoint.getX() - distance);

        locations.add(loc4);

        return locations;
    }

}
