package uk.co.domaincraft.minecraft.plugins.zombie_arrival.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


    public static ItemStack[] getStackOfOnlinePlayers(ZombieArrival plugin) {
        List<ItemStack> players = new ArrayList<ItemStack>();

        for(Player p : plugin.getServer().getOnlinePlayers()) {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

            meta.setOwningPlayer(p);
            meta.setDisplayName(p.getDisplayName());
            if(p.isOp()) {
                meta.setDisplayName(ChatColor.RED + p.getDisplayName());
                meta.setLore(Collections.singletonList("Server Operator"));
            }
            playerHead.setItemMeta(meta);
            players.add(playerHead);
        }

        return players.toArray(new ItemStack[players.size()]);
    }



}
