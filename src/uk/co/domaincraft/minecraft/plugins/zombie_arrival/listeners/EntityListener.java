package uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ReleaseType;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;

import java.util.ArrayList;
import java.util.Random;

public class EntityListener implements Listener{
	
	private ZombieArrival plugin;


    public EntityListener(ZombieArrival plugin){
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent event){
		if(!(event.getEntityType() == EntityType.ZOMBIE || event.getEntityType() == EntityType.CHICKEN || event.getEntityType() == EntityType.COW
                || event.getEntityType() == EntityType.SHEEP || event.getEntityType() == EntityType.WOLF || event.getEntityType() == EntityType.SNOWMAN
                || event.getEntityType() == EntityType.GIANT || event.getEntityType() == EntityType.BLAZE || event.getEntityType() == EntityType.OCELOT)
                || event.getEntityType() == EntityType.HORSE){
			event.setCancelled(true);
			//Logger.log("The following entity spawned: " + event.getEntityType());
		}else if(event.getEntityType() == EntityType.ZOMBIE){
			LivingEntity zombie = event.getEntity();
			ItemStack zombieHelmet = new ItemStack(Material.LEATHER_HELMET);
			Random rand = new Random();
			int color1, color2, color3, clusterChance;
			color1 = rand.nextInt(15);
			color2 = rand.nextInt(15);
			color3 = rand.nextInt(15);
			clusterChance = rand.nextInt(7);
			
			if(clusterChance == 1){
				
				for(int i = 0;i < 5; i++){
					event.getEntity().getWorld().spawnEntity(zombie.getLocation(), EntityType.ZOMBIE);
                    //event.getEntity().getWorld().strikeLightningEffect(zombie.getLocation());
			
				}
				if(event.getEntity().getLocation().getY() > 60){
                    event.getEntity().getWorld().spawnEntity(zombie.getLocation(), EntityType.GIANT);

                   //plugin.getServer().broadcastMessage(ChatColor.ITALIC + "" + ChatColor.RED + "A foul essence travels through the air..");
                }

				
			}
			
			//Logger.log("Cluster Chance: " + clusterChance);
				
			
			colorArmor(zombieHelmet, color1, color2, color3);
			
			event.getEntity().getEquipment().setHelmet(zombieHelmet);
			//Logger.log("Zombie at " + zombie.getLocation() + " now has a leather helmet.");
			
			
			
			ItemStack sword = new ItemStack(Material.SKULL); // Place holder. We don't want a chance of NPE!
			
			int choice = rand.nextInt(5);
			//Logger.log("Zombie sword: " + choice);
			if(choice == 0){
				sword = new ItemStack(Material.WOOD_SWORD);
			}else if(choice == 1){
				sword = new ItemStack(Material.STONE_SWORD);
				
			}else if (choice == 2){
				sword = new ItemStack(Material.IRON_SWORD);
			}else if (choice == 3){
				sword = new ItemStack(Material.GOLD_SWORD);
			}else if (choice == 4){
				sword = new ItemStack(Material.DIAMOND_SWORD);
				sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                zombie.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                zombie.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));

			}
			zombie.getEquipment().setItemInHandDropChance(0F);
			zombie.getEquipment().setItemInHand(sword);


        }
	}

    @EventHandler
    public void teleportEvent(PlayerTeleportEvent event){
        event.getFrom().getWorld().playEffect(event.getFrom(), Effect.ENDER_SIGNAL, 100);
        event.getTo().getWorld().playEffect(event.getTo(), Effect.ENDER_SIGNAL, 100);


    }
	
	@EventHandler
	public void damageByEntityEvent(EntityDamageByEntityEvent event){
		Entity entity = event.getEntity();
        Random rand = new Random();
        int randChance = rand.nextInt(5);


		if(event.getEntityType() == EntityType.ZOMBIE){
			
		}else{
			if(event.getCause() == DamageCause.ENTITY_ATTACK){
				if(event.getDamager() instanceof Zombie){
					LivingEntity theDamaged = (LivingEntity)event.getEntity();	
					theDamaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 500, 500));
                    if(randChance == 1){
                        theDamaged.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 500, 500));
                    }else if(randChance == 2){
                        theDamaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500, 500));
                    }

				}
			}
		}
			
	}


	
	@EventHandler
	public void damageEvent(EntityDamageEvent event){
		if(event.getEntity().getWorld().getTime() < 12300 || event.getEntity().getWorld().getTime() > 23850){
			if(event.getEntityType() == EntityType.ZOMBIE){
				if(event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK){
					event.setCancelled(true);
					event.getEntity().setFireTicks(0);
					//Logger.log("Day time. Zombie can't be burned!");
				}
			}
		}
		
		if(event.getEntityType() == EntityType.PLAYER){
			Player player = (Player)event.getEntity();
			//player.setPlayerListName(ChatColor.DARK_GREEN + player.getName() + " " + player.getHealth());
			if(player.getName().equalsIgnoreCase("russjr08") || player.getName().equalsIgnoreCase("mac889")){
				//event.setCancelled(true);
			}else if(player.getName().equalsIgnoreCase("pineapple95")){
				event.setCancelled(true);
				if(player.getHealth() > 3){
				event.setDamage(2);
				}
			}
		}
	}

	
	@EventHandler
	public void deathEvent(EntityDeathEvent event){
		Entity entity = event.getEntity();
		if(entity instanceof Zombie){
			Random rand = new Random();
			int chance = rand.nextInt(10);
			int superChance = rand.nextInt(1000);
			//Logger.log("Death chance: " + chance);
			ItemStack drop, regDrop;
			LivingEntity zombie = (LivingEntity)entity;
			if(chance == 1){
				drop = new ItemStack(Material.SULPHUR);
				
				
				zombie.getWorld().dropItemNaturally(zombie.getLocation(), drop);
			}
			if(chance == 1000){
				drop = new ItemStack(Material.PUMPKIN);
				zombie.getWorld().dropItemNaturally(zombie.getLocation(), drop);	
				
			}else if(chance == 50){
				drop = new ItemStack(Material.PUMPKIN_SEEDS);
				zombie.getWorld().dropItemNaturally(zombie.getLocation(), drop);
				plugin.getServer().broadcastMessage(ChatColor.AQUA + "A CURE HAS BEEN FOUND FOR POISON!");
			}
			regDrop = new ItemStack(Material.BONE);
			zombie.getWorld().dropItemNaturally(zombie.getLocation(), regDrop);
			
		}else if(entity instanceof Player){
			Player player = (Player)entity;
			if(player.getName().equalsIgnoreCase("pineapple95")){
				specialDeath(player);
				
			}else{
				Zombie zombie = (Zombie)player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
                zombie.setCustomName(player.getName());
                zombie.setCustomNameVisible(true);
                zombie.setCanPickupItems(true);
			}
		}else if(entity instanceof Giant){
            ItemStack treasure = tieredGiantLoot();
            ItemMeta treasureMeta = treasure.getItemMeta();
            ArrayList<String> lore = new ArrayList<String>();
            lore.add(ChatColor.ITALIC + " " + ChatColor.GOLD + "Treasure Loot");
            treasureMeta.setLore(lore);
            treasure.setItemMeta(treasureMeta);


            Giant giant = (Giant)event.getEntity();

            giant.getWorld().dropItemNaturally(giant.getLocation(), treasure);

        }
		
	}
	
	@EventHandler
	public void targetEvent(EntityTargetEvent event){
		if(event.getTarget() instanceof Player){
			Player player = (Player)event.getTarget();
            LivingEntity zombie = null;
            if(event.getEntity() instanceof Zombie){
                zombie = (LivingEntity)event.getEntity();


                zombie.getWorld().playEffect(zombie.getLocation(), Effect.SMOKE, 5);




                if(player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType() == Material.PUMPKIN || player.getInventory().getHelmet().getType() == Material.JACK_O_LANTERN){

                    //event.setCancelled(true);

                    // -- This is fun! zombie.getWorld().createExplosion(zombie.getLocation(), 0.5F, false);
                    if(player.getName().equals("mac889")){
                        Random rand = new Random();
                        zombie.getWorld().createExplosion(zombie.getLocation(), 0.5F, false);
                        player.getInventory().setHelmet(new ItemStack(Material.JACK_O_LANTERN));

                        int chance = rand.nextInt(100);

                        if(chance == 4){
                        }else{

                            zombie.throwSnowball();
                        }

                    }else {}


                }


            }


		}
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		player.sendMessage(ChatColor.GOLD + "This server does not support texture packs");
		player.sendMessage(ChatColor.GOLD + "We will prompt you for a texturepack change in 10 seconds!");

        ZombieArrival.blueTeam.add(player.getName());
        //player.sendMessage(ChatColor.BLUE + "You are now on BLUE team");
			
//		int taskID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
//			@Override
//			public void run(){
//				player.setTexturePack("http://domaincraft.co.uk/TexturePack.zip");
//
//				player.sendMessage(ChatColor.RED + "That was your only warning!");
//			}
//		}, 200L);

        if(event.getPlayer().isOp()){

            if(ZombieArrival.updateChecker.needsUpdate){
                if(ZombieArrival.releaseType == ReleaseType.ALPHA){
                    player.sendMessage("[ZombieArrival] Plugin is " + ChatColor.DARK_RED + "not up to date on the ALPHA channel! " + ChatColor.WHITE + "New Version found: " + ZombieArrival.updateChecker.serverVer );
                }
                else if(ZombieArrival.releaseType == ReleaseType.BETA){
                    player.sendMessage("[ZombieArrival] Plugin is " + ChatColor.DARK_RED + "not up to date on the BETA channel! " + ChatColor.WHITE + "New Version found: " + ZombieArrival.updateChecker.serverVer );
                }
                else if(ZombieArrival.releaseType == ReleaseType.RELEASE){
                    player.sendMessage("[ZombieArrival] Plugin is " + ChatColor.DARK_RED + "not up to date on the RELEASE channel! " + ChatColor.WHITE + "New Version found: " + ZombieArrival.updateChecker.serverVer );
                }
            }else{
                if(ZombieArrival.releaseType == ReleaseType.ALPHA){
                    player.sendMessage("[ZombieArrival] Plugin is " + ChatColor.GREEN + "up to date on the ALPHA channel!");
                }
                else if(ZombieArrival.releaseType == ReleaseType.BETA){
                    player.sendMessage("[ZombieArrival] Plugin is " + ChatColor.GREEN + "up to date on the BETA channel!");
                }
                else if(ZombieArrival.releaseType == ReleaseType.RELEASE){
                    player.sendMessage("[ZombieArrival] Plugin is " + ChatColor.GREEN + "up to date on the RELEASE channel!");
                }
            }
        }
		
	}
	
	@EventHandler
	public void playerLogin(PlayerLoginEvent event){
        if(event.getResult() == Result.KICK_WHITELIST){
			plugin.getServer().broadcastMessage(event.getPlayer().getName() + " tried to join" +
					" but isn't whitelisted!");
		}


	}


	

	
	@EventHandler
	public void chatEvent(PlayerChatEvent event){
		if(event.getPlayer().getName().equalsIgnoreCase("russjr08")  || event.getPlayer().getName().equalsIgnoreCase("QTeamWildCard")){
			if(event.getMessage().equalsIgnoreCase("!kill")){
				for(Entity e:event.getPlayer().getWorld().getEntities()){
					if(e instanceof LivingEntity && (e instanceof Zombie || e instanceof Giant)){
						LivingEntity ent = (LivingEntity)e;
                        ent.getWorld().createExplosion(e.getLocation(), 0.5F);
                        ent.damage(100);
					}
				}
			}else if(event.getMessage().equalsIgnoreCase("!drops")){
				for(Entity e : event.getPlayer().getWorld().getEntities()){
					if(e != null && e instanceof Item){
						e.remove();
					}
				}
				plugin.getServer().broadcastMessage(ChatColor.GOLD + "All Drops Removed");

			}else if(event.getPlayer().getName().equalsIgnoreCase("russjr08")){
				String message = ChatColor.GOLD + event.getMessage();
				event.setMessage(message);
            }
		}
        if(event.getPlayer().getName().equalsIgnoreCase("itsNikki")){
            String message = ChatColor.AQUA + event.getMessage();
            event.setMessage(message);
        }else if(event.getPlayer().getName().equalsIgnoreCase("QTeamWildCard")){
            String message = ChatColor.GREEN + event.getMessage();
            event.setMessage(message);
        }else if(event.getPlayer().getName().equalsIgnoreCase("dwalder01")){
            String message = ChatColor.BLUE + event.getMessage();
            event.setMessage(message);
        }
	}
	
	
	
	@EventHandler
	public void interactEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_AIR){
			if(event.getPlayer().getItemInHand().getType() == Material.WATCH){
				event.getPlayer().getWorld().setTime(event.getPlayer().getWorld().getTime() + 200L);
			}else if(event.getPlayer().getItemInHand().getType() == Material.PUMPKIN_SEEDS && event.getPlayer().hasPotionEffect(PotionEffectType.POISON)){
				event.getPlayer().removePotionEffect(PotionEffectType.POISON);
				event.getPlayer().sendMessage(ChatColor.AQUA + "You have been cured!");
				ItemStack stack = event.getPlayer().getItemInHand();
				stack.setAmount(stack.getAmount() - 1);
				event.getPlayer().setItemInHand(stack);
			}else if(event.getPlayer().getItemInHand().getType() == Material.WORKBENCH){
                event.getPlayer().openWorkbench(event.getPlayer().getLocation(), true);
            }else if(event.getPlayer().getItemInHand().getType() == Material.COMPASS){
                event.getPlayer().teleport(event.getPlayer().getBedSpawnLocation());
            }
		}else if(event.getAction() == Action.LEFT_CLICK_AIR){
            if(event.getPlayer().getItemInHand().getType() == Material.COMPASS){
                event.getPlayer().setCompassTarget(event.getPlayer().getBedSpawnLocation());
            }
        }
	}

    @EventHandler
    public void breakBoat(VehicleDestroyEvent event){
        if(!(event.getAttacker() != null)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void boatMove(VehicleMoveEvent event){
        if(!(event.getVehicle().getPassenger() != null)){
            event.getVehicle().setVelocity(new Vector(0, 0, 0));
        }
    }



	
	

	
	
	public ItemStack colorArmor(ItemStack stack, int r, int g, int b){
		
		LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();
		
		meta.setColor(org.bukkit.Color.fromRGB(r, g, b));
		
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public void specialDeath(Player player){
		player.setHealth(1);
		player.setExhaustion(5F);
		player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 5F);
		player.teleport(player.getWorld().getSpawnLocation());
	}

    public ItemStack tieredGiantLoot(){

        Random random = new Random();
        ItemStack loot = new ItemStack(Material.POTATO_ITEM);

        int tier = random.nextInt(5);

        if(tier == 0){

        }else if(tier == 1){

        }


        return loot;
    }



	

}
