package uk.co.domaincraft.minecraft.plugins.zombie_arrival;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.Dye;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners.EntityListener;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.Logger;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.UpdateChecker;

import java.util.ArrayList;
import java.util.List;

public class ZombieArrival extends JavaPlugin {
	public static final String pluginName = "Zombie Arrival";
	public ZombieArrival instance;

    public static ReleaseType releaseType = ReleaseType.ALPHA;
    public static double version = 1.0;

    public static List<String> blueTeam = new ArrayList();
    public static List<String> redTeam = new ArrayList();

    public static UpdateChecker updateChecker = new UpdateChecker();
	
	@Override
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new EntityListener(this), this);
		//pm.registerEvents(new ServerListener(this), this);
		
		ShapelessRecipe zombieFleshLeather = new ShapelessRecipe(new ItemStack(Material.LEATHER, 2));
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		
		getServer().addRecipe(zombieFleshLeather);
		Logger.log("ZombieFlesh-Leather Recipe added!");
        addEnderRecipe();
        try {
            updateChecker.checkForUpdate();
        } catch (Exception e) {
            Logger.log("Error checking for update!");
            e.printStackTrace();
        }

    }

    public void addEnderRecipe(){
        ShapedRecipe enderpearl = new ShapedRecipe(new ItemStack(Material.ENDER_PEARL, 4));
        Dye lapis = new Dye();
        lapis.setColor(DyeColor.BLUE);
        enderpearl.shape(" L ", "LDL", " L ");
        enderpearl.setIngredient('L', lapis);
        enderpearl.setIngredient('D', Material.DIAMOND);

        getServer().addRecipe(enderpearl);
        Logger.log("EnderPearl Recipe Added");


    }
	
	@Override
	public void onDisable(){
		
	}

}
