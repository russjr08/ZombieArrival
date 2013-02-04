package uk.co.domaincraft.minecraft.plugins.zombie_arrival;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners.EntityListener;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners.ServerListener;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class ZombieArrival extends JavaPlugin {
	public static final String pluginName = "Zombie Arrival";
	public ZombieArrival instance;

    public static List<String> blueTeam = new ArrayList();
    public static List<String> redTeam = new ArrayList();
	
	@Override
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new EntityListener(this), this);
		pm.registerEvents(new ServerListener(this), this);
		
		ShapelessRecipe zombieFleshLeather = new ShapelessRecipe(new ItemStack(Material.LEATHER, 2));
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		zombieFleshLeather.addIngredient(Material.ROTTEN_FLESH);
		
		getServer().addRecipe(zombieFleshLeather);
		Logger.log("ZombieFlesh-Leather Recipe added!");
		
		
	}
	
	@Override
	public void onDisable(){
		
	}

}
