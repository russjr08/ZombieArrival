package uk.co.domaincraft.minecraft.plugins.zombie_arrival.util;

import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;

public class Logger {
	static java.util.logging.Logger log = java.util.logging.Logger.getLogger("Minecraft");
	
	public static void log(String contents){
		log.info("[" + ZombieArrival.pluginName + "] " + contents);
	}
	
}
