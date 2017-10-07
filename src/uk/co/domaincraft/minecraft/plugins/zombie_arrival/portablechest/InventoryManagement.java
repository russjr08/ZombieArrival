package uk.co.domaincraft.minecraft.plugins.zombie_arrival.portablechest;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;

import java.util.ArrayList;

public class InventoryManagement {

    public static ItemStack[] loadInventory(ZombieArrival plugin, Player player){

        if(plugin.getConfig().get("portablechest." + player.getName()) instanceof ArrayList<?>){
            //noinspection unchecked
            ArrayList<ItemStack> items = (ArrayList<ItemStack>)plugin.getConfig().get("portablechest." + player.getName());
            ItemStack[] itemStacks = new ItemStack[27];
            if(items != null){
                if(!items.isEmpty()){
                    for(int i = 0; i < items.size(); i++){
                        itemStacks[i] = items.get(i);
                    }
                }
            }
            return itemStacks;
        }else if(plugin.getConfig().get("portablechest." + player.getName()) instanceof ItemStack[]){
            return (ItemStack[])plugin.getConfig().get("portablechest." + player.getName());
        }
        return new ItemStack[27];


    }

    public static void saveInventory(ZombieArrival plugin, Player player, Inventory inv){

        plugin.getConfig().set("portablechest." + player.getName(), inv.getContents());
        plugin.saveConfig();
    }






}
