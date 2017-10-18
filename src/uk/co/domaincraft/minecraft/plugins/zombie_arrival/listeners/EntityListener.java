package uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.InventoryManagement;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.ZombieUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class EntityListener implements Listener{
	
	private ZombieArrival plugin;


    public EntityListener(ZombieArrival plugin){
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent event){
		if(event.getEntity() instanceof Monster && !(event.getEntity() instanceof Zombie
                || event.getEntity() instanceof Giant)){
			event.setCancelled(true);
            event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.ZOMBIE);
        }else if(event.getEntityType() == EntityType.ZOMBIE){
			Zombie zombie = (Zombie) event.getEntity();
			ItemStack zombieHelmet = new ItemStack(Material.LEATHER_HELMET);
			Random rand = new Random();

			int color1, color2, color3;
			float clusterChance;

			color1 = rand.nextInt(255);
			color2 = rand.nextInt(255);
			color3 = rand.nextInt(255);

			clusterChance = rand.nextFloat();
			
			if(clusterChance <= 0.02F){
				
				for(int i = 0;i < 5; i++){
					event.getEntity().getWorld().spawnEntity(zombie.getLocation(), EntityType.ZOMBIE);
                    event.getEntity().getWorld().strikeLightningEffect(zombie.getLocation());
			
				}
				if(event.getEntity().getLocation().getY() > 60 && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM){
                    event.getEntity().getWorld().spawnEntity(zombie.getLocation(), EntityType.GIANT);

                    for(Player p : plugin.getServer().getOnlinePlayers()) {
                        if(p.getLocation().distance(event.getLocation()) <= 60) {
                            p.sendMessage(ChatColor.DARK_GREEN + ChatColor.ITALIC.toString()
                                    + "A foul essence travels through the air...");
                        }
                    }
                }

				
			}

			
			colorArmor(zombieHelmet, color1, color2, color3);
			
			event.getEntity().getEquipment().setHelmet(zombieHelmet);


			double d = Math.random();
            if(d >= .8) { // 20% chance to create a classed zombie
                ZombieUtils.createClassedZombie(zombie, plugin);
            }

            if((Calendar.getInstance().get(Calendar.MONTH) == Calendar.OCTOBER) &&
                    (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 31)) {
                zombie.setBaby(true);
                zombie.getEquipment().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
            }

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


        if (event.getEntityType() != EntityType.ZOMBIE) {
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
			player.setPlayerListName(ChatColor.DARK_GREEN + player.getName() + " " + player.getHealth());
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
            if(event.getDrops().contains(new ItemStack(Material.POTATO_ITEM))){
                event.getDrops().remove(new ItemStack(Material.POTATO_ITEM));
                ItemStack poisionPotato = new ItemStack(Material.POTATO_ITEM);
                ItemMeta meta = poisionPotato.getItemMeta();
                ArrayList<String> lore = new ArrayList<String>();
                lore.add(ChatColor.DARK_GREEN.toString() + ChatColor.ITALIC.toString() + "Put nine in a crafting table to get a regular potato...");
                meta.setLore(lore);
                poisionPotato.setItemMeta(meta);
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), poisionPotato);

            }

            if(zombie.getMetadata("class").size() >= 1 && zombie.getMetadata("class")
                    .get(0).asString().equalsIgnoreCase("kamikaze")) {
                zombie.getWorld().createExplosion(zombie.getLocation(), 2F);
            }
			
		}else if(entity instanceof Player){
			Player player = (Player)entity;
			if(player.getName().equalsIgnoreCase("pineapple95")){
				specialDeath(player);
				
			}else{
				Zombie zombie = (Zombie)player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
                zombie.setCustomName(player.getName());
                zombie.setCustomNameVisible(true);
                zombie.setCanPickupItems(true);
                if(!entity.getWorld().getGameRuleValue("keepInventory").equalsIgnoreCase("true")){
                    Inventory inventory = Bukkit.createInventory((Player)entity, 27);
                    inventory.setContents(InventoryManagement.loadInventory(plugin, (Player)entity));
                    for(int i = 0; i < inventory.getContents().length; i++){
                        if(inventory.getContents()[i] != null){
                            entity.getWorld().dropItemNaturally(entity.getLocation(), inventory.getContents()[i]);

                        }
                        inventory.setItem(i, new ItemStack(Material.AIR));


                    }
                    InventoryManagement.saveInventory(plugin, (Player)entity, inventory);
                }
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
            if(event.getEntity() instanceof Zombie){
				LivingEntity zombie = (LivingEntity) event.getEntity();


                zombie.getWorld().playEffect(zombie.getLocation(), Effect.SMOKE, 5);



            }


		}
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();


        if(event.getPlayer().isOp()){


            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        if(plugin.updateChecker.needsUpdate()) {
                            player.sendMessage(String.format("[ZombieArrival] Plugin is " +
                                            ChatColor.DARK_RED + "OUT OF date on the %s channel! (Latest: %s > Server: %s)",
                                    plugin.updateChecker.getReleaseType(), plugin.updateChecker.getServerVersion(),
                                    plugin.updateChecker.getLocalVersion()));
                        } else {
                            player.sendMessage(String.format("[ZombieArrival] Plugin is " +
                                            ChatColor.GREEN + "up to date on the %s channel! (Latest: %s <= Server: %s)",
                                    plugin.updateChecker.getReleaseType(), plugin.updateChecker.getServerVersion(),
                                    plugin.updateChecker.getLocalVersion()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
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
		if(event.getPlayer().isOp()){
			if(event.getMessage().equalsIgnoreCase("!kill")){
			    int numOfNamedZombies = 0;
				for(Entity e : event.getPlayer().getWorld().getEntities()){
					if(e instanceof LivingEntity && (e instanceof Zombie || e instanceof Giant)){
						LivingEntity ent = (LivingEntity)e;
						if(e.getCustomName() != null) {
						    numOfNamedZombies++;
						    continue;
                        }
                        ent.getWorld().createExplosion(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ(), .5F, false, false);
                        ent.damage(100);
					}
				}

				event.getPlayer().sendMessage(String.format("%s%sWarning: %d Zombies were not cleared due to being named.", ChatColor.DARK_GRAY, ChatColor.ITALIC, numOfNamedZombies));
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
        }else if(event.getPlayer().getName().equalsIgnoreCase("Krobe_")){
            String message = ChatColor.BLUE + event.getMessage();
            event.setMessage(message);
        }else if(event.getPlayer().getName().equalsIgnoreCase("dwalder01")){
            String message = ChatColor.BLUE + event.getMessage();
            event.setMessage(message);
        }else if(event.getPlayer().getName().equalsIgnoreCase("The_Flame98")){
            String message = ChatColor.AQUA + event.getMessage();
            event.setMessage(message);
        }
	}
	
	
	
	@EventHandler
	public void interactEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_AIR){
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WATCH){
				if(event.getPlayer().isOp()) {
					event.getPlayer().getWorld().setTime(event.getPlayer().getWorld().getTime() + 200L);
				}
			}else if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.PUMPKIN_SEEDS && event.getPlayer().hasPotionEffect(PotionEffectType.POISON)){
				event.getPlayer().removePotionEffect(PotionEffectType.POISON);
				event.getPlayer().sendMessage(ChatColor.AQUA + "You have been cured!");
				ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
				stack.setAmount(stack.getAmount() - 1);
				event.getPlayer().getInventory().setItemInMainHand(stack);
			}else if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPASS){
				if(event.getPlayer().getBedSpawnLocation() != null) {
					event.getPlayer().teleport(event.getPlayer().getBedSpawnLocation());
				} else {
				    event.getPlayer().sendMessage(ChatColor.RED + "You have not slept yet to set your bed, teleport cancelled.");
                }
            } else if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPASS) {

			}
		}else if(event.getAction() == Action.LEFT_CLICK_AIR){
            if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPASS){
                Inventory inv = Bukkit.createInventory(event.getPlayer(), 27,
                        "Player List");
                inv.setContents(InventoryManagement.getStackOfOnlinePlayers(plugin));
                event.getPlayer().openInventory(inv);
            }
        }

        if(!event.getPlayer().isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)){
            if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WORKBENCH){
                event.getPlayer().openWorkbench(event.getPlayer().getLocation(), true);
                event.setCancelled(true);
            }else if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.CHEST && event.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains("Backpack")){
                Inventory inv = Bukkit.createInventory(event.getPlayer(), 27, "Portable Chest");
                if(InventoryManagement.loadInventory(plugin, event.getPlayer()) != null){
                    inv.setContents(InventoryManagement.loadInventory(plugin, event.getPlayer()));

                }
                event.getPlayer().openInventory(inv);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_CHEST_OPEN, 100f, 0f);
                event.setCancelled(true);

            }

        }
	}


	@EventHandler
	public void inventoryHandler(InventoryClickEvent event) {
		if(event.getInventory().getTitle().equalsIgnoreCase("Player List")) {
			event.setCancelled(true);
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                plugin.getServer().getPlayer(event.getWhoClicked().getUniqueId())
                        .setCompassTarget(plugin.getServer()
                                .getPlayer(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()))
                                .getLocation());

            }

		}
	}

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event){
        if(event.getInventory().getName().equalsIgnoreCase("Portable Chest") && event.getInventory() != null){
            InventoryManagement.saveInventory(plugin, (Player)event.getPlayer(), event.getInventory());
            ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.BLOCK_CHEST_CLOSE, 100f, 0f);

        }
    }







	
	

	
	
	private ItemStack colorArmor(ItemStack stack, int r, int g, int b){
		
		LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();
		
		meta.setColor(org.bukkit.Color.fromRGB(r, g, b));
		
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	private void specialDeath(Player player){
		player.setHealth(1);
		player.setExhaustion(5F);
		player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 5F);
		player.teleport(player.getWorld().getSpawnLocation());
	}

    private ItemStack tieredGiantLoot(){

        // TODO: Implement tiers of various loot collected from Giants.

        return new ItemStack(Material.POTATO_ITEM);
    }



	

}
