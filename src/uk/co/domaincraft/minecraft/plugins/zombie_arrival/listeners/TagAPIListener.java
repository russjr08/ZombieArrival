package uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;


public class TagAPIListener implements Listener {

    @EventHandler
    public void tagGetEvent(PlayerReceiveNameTagEvent event){
        if(ZombieArrival.blueTeam.contains(event.getNamedPlayer().getName())){
            event.setTag(ChatColor.BLUE + event.getNamedPlayer().getName());
        }else if(ZombieArrival.redTeam.contains(event.getNamedPlayer().getName())){
            event.setTag(ChatColor.RED + event.getNamedPlayer().getName());
        }
    }
}
