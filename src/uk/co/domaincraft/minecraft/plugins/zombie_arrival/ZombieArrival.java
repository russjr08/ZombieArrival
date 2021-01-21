package uk.co.domaincraft.minecraft.plugins.zombie_arrival;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.Dye;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners.CraftingListener;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners.EntityListener;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.Logger;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.LootTableGenerator;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.UpdateChecker;

import java.util.Map;

public class ZombieArrival extends JavaPlugin {
	public static final String pluginName = "Zombie Arrival";
	private static ZombieArrival instance;

    public UpdateChecker updateChecker;

    public Map<ItemStack, Double> zombieLoot;

	@Override
	public void onEnable(){

        instance = this;

        updateChecker = new UpdateChecker(Double.parseDouble(this.getDescription().getVersion()), ReleaseType.ALPHA);

        // Custom Names
        CraftingListener.specialName.put(new ItemStack(Material.CRAFTING_TABLE), ChatColor.GREEN + "(Portable) Workbench");
        CraftingListener.specialName.put(new ItemStack(Material.CHEST), "Backpack");

        // Custom Lores
        CraftingListener.specialLore.put(new ItemStack(Material.COMPASS), ChatColor.GREEN.toString() + ChatColor.ITALIC + "Homing Device");
        CraftingListener.specialLore.put(new ItemStack(Material.CRAFTING_TABLE), ChatColor.GREEN + "Right Click to Open\n" + ChatColor.RED + ChatColor.ITALIC.toString() + "Sneak-Right Click to Place");
        CraftingListener.specialLore.put(new ItemStack(Material.CHEST), ChatColor.GREEN + "Right Click to Open\n"
                + ChatColor.DARK_RED.toString() + ChatColor.ITALIC.toString()
                + "Warning: Isn't valid if placed onto the ground anymore,\n" + ChatColor.DARK_RED.toString()
                + ChatColor.ITALIC.toString() +"rename to 'backpack' to restore its use\n"
                + ChatColor.GOLD.toString() + "Shift-Click to Place");

		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new EntityListener(this), this);
        pm.registerEvents(new CraftingListener(), this);

		ShapelessRecipe zombieFleshLeather = new ShapelessRecipe(new NamespacedKey(this, "zombieflesh"), new ItemStack(Material.LEATHER, 2));
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		
		getServer().addRecipe(zombieFleshLeather);

        ShapelessRecipe potato = new ShapelessRecipe(new NamespacedKey(this, "clean_potato"), new ItemStack(Material.POTATO));
        potato.addIngredient(Material.POISONOUS_POTATO);
        potato.addIngredient(Material.POISONOUS_POTATO);
        potato.addIngredient(Material.POISONOUS_POTATO);
        potato.addIngredient(Material.POISONOUS_POTATO);
        potato.addIngredient(Material.POISONOUS_POTATO);
        potato.addIngredient(Material.POISONOUS_POTATO);
        potato.addIngredient(Material.POISONOUS_POTATO);
        potato.addIngredient(Material.POISONOUS_POTATO);
        potato.addIngredient(Material.POISONOUS_POTATO);

        getServer().addRecipe(potato);

        Logger.log("Generating zombie loot table... Please wait...");
        zombieLoot = LootTableGenerator.generateZombieLootTableFromConfig();

        Logger.log("Zombie loot table generation completed!");
        Logger.log(ArrayUtils.toString(zombieLoot));

        addEnderRecipe();
        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    if(updateChecker.needsUpdate()) {
                        Logger.log(String.format("Plugin is out of date! %s > %s (%s)",
                                updateChecker.getServerVersion(), updateChecker.getLocalVersion(), updateChecker.getReleaseType()));
                    } else {
                        Logger.log(String.format("Plugin is up to date! %s <= %s (%s)",
                                updateChecker.getServerVersion(), updateChecker.getLocalVersion(), updateChecker.getReleaseType()));
                    }
                } catch (Exception e) {
                    Logger.log("Error checking for update!");
                    e.printStackTrace();
                }
            }
        });
        sayHelloToDomainCrafting();

    }

    private void addEnderRecipe(){
        ShapedRecipe enderpearl = new ShapedRecipe(new NamespacedKey(this, "enderpearl"), new ItemStack(Material.ENDER_PEARL, 4));
        Dye lapis = new Dye();
        lapis.setColor(DyeColor.BLUE);
        enderpearl.shape(" L ", "LDL", " L ");
        enderpearl.setIngredient('L', lapis);
        enderpearl.setIngredient('D', Material.DIAMOND);

        getServer().addRecipe(enderpearl);
        Logger.log("EnderPearl Recipe Added");


    }

    private void sayHelloToDomainCrafting() {
	    if(getServer().getPluginManager().getPlugin("DomainCrafting") != null) {
	        Logger.log("Oh, hello there DomainCrafting!");
        }
    }
	
	@Override
	public void onDisable(){
	    instance = null;
	}

    public static ZombieArrival getInstance() {
        return instance;
    }
}
