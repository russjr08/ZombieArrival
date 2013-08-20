package uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CraftingListener implements Listener {

    public static HashMap<ItemStack, String> specialLore = new HashMap<ItemStack, String>();
    public static HashMap<ItemStack, String> specialName = new HashMap<ItemStack, String>();


    @EventHandler
    public void craftingEvent(PrepareItemCraftEvent event){
        ItemStack crafted = event.getRecipe().getResult();

        event.getInventory().setResult(getCustomItem(crafted));
    }

    public ItemStack getCustomItem(ItemStack result){
        ItemStack crafted = result;
        for(Map.Entry<ItemStack, String> entry : specialLore.entrySet()){

            ItemStack thisStack = entry.getKey();

            if(crafted.getType() == thisStack.getType()){
                ItemMeta meta = crafted.getItemMeta();
                ArrayList<String> lores = new ArrayList<String>();
                String[] linesOfLores = entry.getValue().split("\n");
                for(String lore : linesOfLores){
                    lores.add(lore);
                }

//                if(meta.hasLore()){
//                    for(String lore : meta.getLore()){
//                        lores.add(lore);
//                    }
//                }
                meta.setLore(lores);
                crafted.setItemMeta(meta);
            }
        }

        for(Map.Entry<ItemStack, String> entry : specialName.entrySet()){
            ItemStack thisStack = entry.getKey();
            if(crafted.getType() == thisStack.getType()){
                ItemMeta meta = crafted.getItemMeta();
                meta.setDisplayName(entry.getValue());
                crafted.setItemMeta(meta);

            }
        }
        return crafted;
    }

}
