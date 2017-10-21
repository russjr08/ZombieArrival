package uk.co.domaincraft.minecraft.plugins.zombie_arrival.util;

import com.google.gson.Gson;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.pojos.LootConfig;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.pojos.LootConfigItem;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LootTableGenerator {

    public static Map<ItemStack, Double> generateZombieLootTableFromConfig() {
        Map<ItemStack, Double> loot = new HashMap<ItemStack, Double>();

        Gson gson = new Gson();

        File configFile = new File(ZombieArrival.getInstance().getDataFolder(), "loot.json");

        if(!configFile.exists()) {
            Logger.log("loot.json was not found... copying from defaults.");
            configFile.getParentFile().mkdirs();
            copy(ZombieArrival.getInstance().getResource("loot.json"), configFile);
        }

        LootConfig config = gson.fromJson(readFile(configFile.getPath()), LootConfig.class);

        for(LootConfigItem item : config.zombies) {
            try {
                Logger.log(String.format("%s => %s", item.item, Material.valueOf(item.item)));
            } catch(IllegalArgumentException e) {
                Logger.log(String.format("Could not match %s to an item in the Minecraft database! Skipping entry.", item.item));
                continue; // If the above item was not found, skip this entry.
            }


            ItemStack stack = new ItemStack(Material.valueOf(item.item), item.quantity);
            ItemMeta meta = stack.getItemMeta();
            if(item.name != null && !item.name.isEmpty()) {
                meta.setDisplayName(item.name);
            }
            meta.setLore(item.lore);

            stack.setItemMeta(meta);

            loot.put(stack, item.chance);
        }

        return loot;

    }

    private static String readFile(String pathname) {

        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    private static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
