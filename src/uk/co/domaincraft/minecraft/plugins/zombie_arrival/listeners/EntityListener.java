package uk.co.domaincraft.minecraft.plugins.zombie_arrival.listeners;

import com.kronosad.api.internet.ReadURL;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.ZombieArrival;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.Constants;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.InventoryManagement;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.Logger;
import uk.co.domaincraft.minecraft.plugins.zombie_arrival.util.ZombieUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;

public class EntityListener implements Listener{
	
	private final ZombieArrival plugin;

	private static ArrayList<Class<? extends Entity>> blacklistedMobTypes = new ArrayList<Class<? extends Entity>>();

    public EntityListener(ZombieArrival plugin){
		this.plugin = plugin;
		blacklistedMobTypes.add(Phantom.class);
		blacklistedMobTypes.add(Slime.class);
	}
	
	
	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent event){
        for(Class clazz : blacklistedMobTypes) {

            //noinspection unchecked
            if(clazz.isAssignableFrom(event.getEntity().getClass())) {
                event.setCancelled(true);
                event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.ZOMBIE);
            }
        }

        if(event.getEntity() instanceof Ghast || event.getEntity() instanceof WitherSkeleton || event.getEntity() instanceof Blaze
                || event.getEntity() instanceof Piglin) {
            return; // Don't replace Nether mobs... for now.
        }

		if((event.getEntity() instanceof Monster) && !(event.getEntity() instanceof Zombie
                || event.getEntity() instanceof Giant)){
			event.setCancelled(true);
            Zombie zombie = (Zombie) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.ZOMBIE);
            if(event.getEntity() instanceof Silverfish) {
                zombie.setBaby(true);
            }
        }else if(event.getEntityType() == EntityType.ZOMBIE){
			Zombie zombie = (Zombie) event.getEntity();
			ItemStack zombieHelmet = new ItemStack(Material.LEATHER_HELMET);
			Random rand = new Random();

			int color1, color2, color3;

			color1 = rand.nextInt(255);
			color2 = rand.nextInt(255);
			color3 = rand.nextInt(255);

			World world = event.getLocation().getWorld();

			if(world.getEnvironment() == World.Environment.NETHER) {
                ZombieUtils.spawnHordeWithChance(0.02f, zombie.getLocation(), event.getSpawnReason(), plugin.getServer());

            } else {
			    ZombieUtils.spawnHordeWithChance(0.02f, zombie.getLocation(), event.getSpawnReason(), plugin.getServer());

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
                    theDamaged.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 25));
                    if(randChance == 1){
                        theDamaged.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 500, 500));
                    }else if(randChance == 2){
                        theDamaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500, 500));
                    } else if(randChance == 3) {
                        theDamaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 250, 250));
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
				}
			}
		}
	}

	
	@EventHandler
	public void deathEvent(EntityDeathEvent event){
		LivingEntity entity = event.getEntity();
		if(entity instanceof Zombie){

            for(Map.Entry<ItemStack, Double> entry : plugin.zombieLoot.entrySet()) {
                ItemStack stack = entry.getKey();
                double chance = entry.getValue();
                double rand = Math.random();
                if(chance >= rand) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), stack);
                }
            }

            if(entity.getMetadata("class").size() >= 1 && entity.getMetadata("class")
                    .get(0).asString().equalsIgnoreCase("kamikaze")) {
                if(entity.getWorld().getGameRuleValue(GameRule.MOB_GRIEFING)) {
                    entity.getWorld().createExplosion(entity.getLocation(), 2F);
                } else {
                    entity.getWorld().createExplosion(entity.getLocation().getX(), entity.getLocation().getY(),
                            entity.getLocation().getZ(), 2F, false, false);

                }
            }
			
		}else if(entity instanceof Player){
			Player player = (Player)entity;

            Zombie zombie = (Zombie)player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
            zombie.setCustomName(player.getName());
            zombie.setCustomNameVisible(true);
            zombie.setCanPickupItems(true);
            if(!entity.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY)){
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

                for(Block block : ZombieUtils.getNearbyBlocks(event.getTarget().getLocation(), Constants.WARDING_DISTANCE)) {
                    if(block.getType() == Material.JACK_O_LANTERN) {
                        event.setCancelled(true);
                        event.setTarget(null);
                        break;
                    }
                }

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
                        Logger.warn("Couldn't check for updates, the ZombieArrival API might be offline: "
                                + e.getMessage());
                    }
                }
            });
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                ReadURL urlReader = new ReadURL("https://api.kronosad.com/minecraft/ZombieArrival/motd.txt");
                try {
                    String motd = urlReader.read();
                    player.sendMessage(String.format("%s%s%s", ChatColor.GOLD, ChatColor.ITALIC, motd));

                } catch (Exception e) {
                    Logger.warn("Couldn't check for the current MOTD, the ZombieArrival API might be offline: "
                            + e.getMessage());
                }
            }
        });




    }
	
	@EventHandler
	public void playerLogin(PlayerLoginEvent event){
        if(event.getResult() == Result.KICK_WHITELIST){
			plugin.getServer().broadcastMessage(event.getPlayer().getName() + " tried to join" +
					" but isn't whitelisted!");
		}

		// TODO: Make this dynamic (pull from API)

		if(event.getPlayer().getName().equalsIgnoreCase("russjr08")) {
            event.getPlayer().setDisplayName(ChatColor.GOLD + "[Developer] russjr08");
            event.getPlayer().setPlayerListName(ChatColor.GOLD + "[Dev] russjr08");
        }

        if(event.getPlayer().getName().equalsIgnoreCase("michaelthesad")) {
            event.getPlayer().setDisplayName(ChatColor.BLUE + "[ApSci] michaelthesad");
            event.getPlayer().setPlayerListName(ChatColor.BLUE + "[QA] michaelthesad");
        }

        if(event.getPlayer().getName().equalsIgnoreCase("Redwolfyami")) {
            event.getPlayer().setDisplayName(ChatColor.DARK_PURPLE + "[QA] Redwolfyami");
            event.getPlayer().setPlayerListName(ChatColor.DARK_PURPLE + "[QA] Redwolfyami");
        }

        if(event.getPlayer().getName().equalsIgnoreCase("NearlyVikki")) {
            event.getPlayer().setDisplayName(ChatColor.GREEN + "[QA] NearlyVikki");
            event.getPlayer().setPlayerListName(ChatColor.GREEN + "[QA] NearlyVikki");
        }

	}


	@EventHandler
    public void blockPlaceEvent(final BlockPlaceEvent event) {
        if(event.getBlock().getType() == Material.JACK_O_LANTERN) {
            event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Ward created.");

            ZombieUtils.showWardingBoundary(event.getPlayer(), plugin, event.getBlockPlaced().getLocation());

        }
    }


	@EventHandler
	public void chatEvent(AsyncPlayerChatEvent event){
		if(event.getPlayer().isOp()){
			if(event.getMessage().equalsIgnoreCase("!kill")){
			    int numOfNamedZombies = 0;
				for(Entity e : event.getPlayer().getWorld().getEntities()){
					if((e instanceof Zombie || e instanceof Giant)){
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
					if(e instanceof Item){
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
        }else if(event.getPlayer().getName().equalsIgnoreCase("_the_meme_king_")){
            String message = ChatColor.BLUE + event.getMessage();
            event.setMessage(message);
        }else if(event.getPlayer().getName().equalsIgnoreCase("The_Flame98")){
            String message = ChatColor.AQUA + event.getMessage();
            event.setMessage(message);
        }

	}
	
	@EventHandler
    public void playerConsumptionEvent(PlayerItemConsumeEvent event) {
        if(event.getItem().getType() != Material.PUMPKIN_PIE) {
            return;
        }
        boolean hasNegativeEffect = false;
        for(PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
            if(effect.getType().equals(PotionEffectType.BLINDNESS) || effect.getType().equals(PotionEffectType.HUNGER)
                    || effect.getType().equals(PotionEffectType.CONFUSION) || effect.getType().equals(PotionEffectType.POISON)) {
                event.getPlayer().removePotionEffect(effect.getType());
                hasNegativeEffect = true;
            }
        }

        if(hasNegativeEffect) {
            TextComponent healMessage = new TextComponent("You suddenly feel ready to fight again.");
            healMessage.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
            healMessage.setItalic(true);
            event.getPlayer().sendMessage(healMessage.toLegacyText());
        }

    }
	
	@EventHandler
	public void interactEvent(PlayerInteractEvent event){
		if(event.getAction() == Action.RIGHT_CLICK_AIR){
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.CLOCK){
				if(event.getPlayer().isOp()) {
					event.getPlayer().getWorld().setTime(event.getPlayer().getWorld().getTime() + 200L);
				}
			}
		}else if(event.getAction() == Action.LEFT_CLICK_AIR){
            if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPASS){
                Inventory inv = Bukkit.createInventory(event.getPlayer(), 27,
                        "Player List");
                inv.setContents(InventoryManagement.getStackOfOnlinePlayers(plugin));
                event.getPlayer().openInventory(inv);
            }
        } else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.FIREWORK_ROCKET) {
                ZombieUtils.spawnHordeWithChance(0.10f, event.getPlayer().getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM, event.getPlayer().getServer());
            }
            if(event.getClickedBlock() != null && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SHEARS) {
                if(event.getClickedBlock().getType() == Material.BONE_BLOCK) {
                    Random rand = new Random();
                    event.getClickedBlock().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), new ItemStack(Material.BONE, rand.nextInt(5) + 1));
                    event.getClickedBlock().setType(Material.AIR);
                    damageHeldItem(event.getPlayer());
                }
            }
        }

        if(!event.getPlayer().isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)){
            if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.CRAFTING_TABLE){
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

        if(event.getHand() == EquipmentSlot.HAND && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.JACK_O_LANTERN) {
            ZombieUtils.showWardingBoundary(event.getPlayer(), plugin, event.getClickedBlock().getLocation());
            event.setCancelled(true);

        }
	}

    private void damageHeldItem(Player player) {
        if(player.getGameMode() != GameMode.CREATIVE) {

            Damageable itemMeta = (Damageable) player.getInventory().getItemInMainHand().getItemMeta();

            itemMeta.setDamage(itemMeta.getDamage() + 5);

            // Check to see if the player's pickaxe will be past the max durability, and delete it if so.
            if (itemMeta.getDamage() > player.getInventory().getItemInMainHand().getType().getMaxDurability()) {
                player.getInventory().getItemInMainHand().setAmount(0);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
            } else {
                player.getInventory().getItemInMainHand().setItemMeta((ItemMeta) itemMeta);
            }
        }
    }



    @EventHandler
	public void inventoryClicked(InventoryClickEvent event) {
		if(event.getView().getTitle().equalsIgnoreCase("Player List")) {
			event.setCancelled(true);
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {

                for(Player p : plugin.getServer().getOnlinePlayers()) {
                    if(p.getDisplayName().contains(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()))) {
                        ((Player) event.getWhoClicked()).setCompassTarget(p.getLocation());
                        break;
                    }
                }
            }

		}
	}

    @EventHandler
    public void inventoryClosed(InventoryCloseEvent event){
        if(event.getView().getTitle().equalsIgnoreCase("Portable Chest")) {
            event.getInventory();
            InventoryManagement.saveInventory(plugin, (Player) event.getPlayer(), event.getInventory());
            ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.BLOCK_CHEST_CLOSE, 100f, 0f);

        }
    }

	private ItemStack colorArmor(ItemStack stack, int r, int g, int b){
		
		LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();

		if(meta != null) {
            meta.setColor(org.bukkit.Color.fromRGB(r, g, b));
            stack.setItemMeta(meta);
        }

		return stack;
	}


    private ItemStack tieredGiantLoot(){

        // TODO: Implement tiers of various loot collected from Giants.

        return new ItemStack(Material.POTATO);
    }



	

}
