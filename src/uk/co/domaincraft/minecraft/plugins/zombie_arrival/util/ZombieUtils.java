package uk.co.domaincraft.minecraft.plugins.zombie_arrival.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;

import java.util.Random;

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
                primaryWeapon = new ItemStack(Material.GOLD_AXE);
                secondaryWeapon = new ItemStack(Material.WOOD_SWORD);
                break;
            case 2:
                primaryWeapon = new ItemStack(Material.IRON_AXE);
                secondaryWeapon = new ItemStack(Material.GOLD_SWORD);
                break;
            case 3:
                primaryWeapon = new ItemStack(Material.STONE_AXE);
                secondaryWeapon = new ItemStack(Material.WOOD_SWORD);
                break;
            default:
                primaryWeapon = new ItemStack(Material.WOOD_AXE);
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
            zombie.getEquipment().setHelmet(new ItemStack(Material.GOLD_HELMET));
            zombie.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
            zombie.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
            zombie.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
        }

        zombie.setMetadata("class", new FixedMetadataValue(plugin, "assault"));
    }

}
