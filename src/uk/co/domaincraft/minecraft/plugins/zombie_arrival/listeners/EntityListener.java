package uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
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
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Door;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.Logger;

import java.util.Random;

public class EntityListener implements Listener{
	
	private ZombieArrival plugin;
	
	public EntityListener(ZombieArrival plugin){
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent event){
		if(!(event.getEntityType() == EntityType.ZOMBIE || event.getEntityType() == EntityType.CHICKEN || event.getEntityType() == EntityType.COW
                || event.getEntityType() == EntityType.SHEEP || event.getEntityType() == EntityType.WOLF || event.getEntityType() == EntityType.SNOWMAN)){
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
			
				}
				event.getEntity().getWorld().spawnEntity(zombie.getLocation(), EntityType.GIANT);
				
			}
			
			//Logger.log("Cluster Chance: " + clusterChance);
				
			
			zombieHelmet = this.colorArmor(zombieHelmet, color1, color2, color3);
			
			event.getEntity().getEquipment().setHelmet(zombieHelmet);
			//Logger.log("Zombie at " + zombie.getLocation() + " now has a leather helmet.");
			
			
			
			ItemStack sword = new ItemStack(Material.SKULL);
			
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
			}
			zombie.getEquipment().setItemInHandDropChance(0F);
			zombie.getEquipment().setItemInHand(sword);
		
		}
	}
	
	@EventHandler
	public void damageByEntityEvent(EntityDamageByEntityEvent event){
		Entity entity = event.getEntity();
		Logger.log(entity.toString());
		if(event.getEntityType() == EntityType.ZOMBIE){
			
		}else{
			if(event.getCause() == DamageCause.ENTITY_ATTACK){
				if(event.getDamager() instanceof Zombie){
					LivingEntity theDamaged = (LivingEntity)event.getEntity();	
					theDamaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 500, 500));
					if(theDamaged instanceof Player){
						event.setCancelled(true);
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
			player.setPlayerListName(ChatColor.DARK_GREEN + player.getName() + " " + player.getHealth());
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
    public void healthRegain(EntityRegainHealthEvent event){
        if(event.getEntityType() == EntityType.PLAYER){
            Player player = (Player)event.getEntity();
            player.setPlayerListName(ChatColor.DARK_GREEN + player.getName() + " " + player.getHealth() );
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
				player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
			}
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

                if(player.getName().equalsIgnoreCase("QTeamWildCard")){
                    //zombie.getWorld().strikeLightning(player.getLocation());
                    player.sendMessage(ChatColor.GRAY + "YOU BUTT SLUT!!");

                }


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
        player.sendMessage(ChatColor.BLUE + "You are now on BLUE team");
			
		int taskID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run(){
				player.setTexturePack("http://domaincraft.co.uk/TexturePack.zip");
				
				player.sendMessage(ChatColor.RED + "That was your only warning!");
			}
		}, 200L);
		
	}
	
	@EventHandler
	public void playerLogin(PlayerLoginEvent event){
		if(event.getResult() == Result.KICK_WHITELIST){
			plugin.getServer().broadcastMessage(event.getPlayer().getName() + " tried to join" +
					" but isn't whitelisted!");
		}
	}
	
	@EventHandler
	public void explosionEvent(ExplosionPrimeEvent event){
		event.setCancelled(true);
		
	}
	
	@EventHandler
	public void chatEvent(PlayerChatEvent event){
		if(event.getPlayer().getName().equalsIgnoreCase("russjr08")){
			if(event.getMessage().equalsIgnoreCase("!kill")){
				for(Entity e:event.getPlayer().getWorld().getEntities()){
					e.getWorld().createExplosion(e.getLocation(), 0.5F);
					if(e instanceof LivingEntity){
						LivingEntity ent = (LivingEntity)e;
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
			}else if(event.getPlayer().getName().equalsIgnoreCase("Steveb175")){
                String message = ChatColor.AQUA + event.getMessage();
                event.setMessage(message);
            }
		}
	}
	
	
	
	@EventHandler
	public void interactEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_AIR){
			if(event.getPlayer().getItemInHand().getType() == Material.WATCH){
				event.getPlayer().getWorld().setTime(event.getPlayer().getWorld().getTime() + 200L);
				plugin.getServer().getPlayer("QTeamWildCard").setPlayerTime(15000, false);
			}else if(event.getPlayer().getItemInHand().getType() == Material.PUMPKIN_SEEDS || event.getPlayer().hasPotionEffect(PotionEffectType.POISON)){
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
        }else if(event.getClickedBlock().getType() == Material.IRON_DOOR){
                System.out.println("Block clicked");
                BlockState state = event.getClickedBlock().getState();
                Door door = (Door)state.getData();
                if(door.isOpen()){
                    door.setOpen(true);
                    state.update();
                }else{
                    door.setOpen(false);
                    state.update();
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
	
	
	@EventHandler
	public void teleportEvent(PlayerTeleportEvent event){
		Location toTP = event.getTo();
		Random rand = new Random();
		double randX, randY, randZ;
		randX = rand.nextInt(255);
		randY = rand.nextInt(100);
		randZ = rand.nextInt(255);
	
		
		
		if(toTP.equals(plugin.getServer().getPlayer("QTeamWildCard").getLocation())){
			if(!event.getPlayer().getName().equals("QTeamWildCard")){


               // event.setTo(new Location(event.getPlayer().getWorld(), randX, randY, randZ));
                plugin.getServer().getPlayer("QTeamWildCard").sendMessage(ChatColor.RED + "Following player" +
                        "tried to teleport to you: " + event.getPlayer().getName());

                event.getPlayer().sendMessage("Access to Michael is " + ChatColor.RED + "DENIED.");
            }
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
	

}
