package me.devtec.knockbackffa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.scheduler.Tasker;

public class KnockEvents implements Listener {
    protected static Map<Location, BlockStateRemove> blocky = new HashMap<>();
    protected static Map<Location, BlockStateRemove> jumps = new HashMap<>();
    protected static Map<Player, Player> lastHit = new HashMap<>();
    
    protected static Map<Player, List<Entity>> projectiles = new HashMap<>();

    @EventHandler
    public void hit(EntityDamageEvent e) {
    	e.setDamage(0);
        e.setCancelled(true);
        if(!API.arena.isInRegion(e.getEntity().getLocation())||API.arena.dead.contains(e.getEntity())) {
        	e.setCancelled(true);
            if(e.getCause()==DamageCause.VOID||e.getCause()==DamageCause.LAVA||e.getCause()==DamageCause.FIRE||e.getCause()==DamageCause.FIRE_TICK) {
            	API.arena.notifyDeath((Player)e.getEntity());
            }
    		return;
        }
        if(e.getCause()==DamageCause.ENTITY_ATTACK||e.getCause()==DamageCause.PROJECTILE)
            e.setCancelled(false);
        if(e.getCause()==DamageCause.VOID||e.getCause()==DamageCause.LAVA||e.getCause()==DamageCause.FIRE||e.getCause()==DamageCause.FIRE_TICK) {
        	API.arena.notifyDeath((Player)e.getEntity());
        }
    }

    @EventHandler
    public void hitByEntity(EntityDamageByEntityEvent e) {
        if(!API.arena.isInRegion(e.getEntity().getLocation()))
        	e.setCancelled(true);
        if(e.getDamager() instanceof Projectile)
        	if(!API.arena.isInRegion(((Player)((Projectile)e.getDamager()).getShooter()).getLocation()))
        		e.setCancelled(true);
        	else
        if(!API.arena.isInRegion(e.getDamager().getLocation()))
        	e.setCancelled(true);
        if(e.isCancelled())return;
		e.setDamage(0);
        if(e.getEntity()instanceof Player) {
            if(e.getDamager() instanceof Player) {
	            lastHit.put((Player)e.getEntity(), (Player)e.getDamager());
            }else if(e.getDamager() instanceof Projectile) {
            	if((Player)((Projectile)e.getDamager()).getShooter()==e.getEntity()) {
            		e.setCancelled(true);
            		return;
            	}
	            lastHit.put((Player)e.getEntity(), (Player)((Projectile)e.getDamager()).getShooter());
        	}
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        e.setJoinMessage("");
        for(Player p : TheAPI.getOnlinePlayers())
        	if(p!=e.getPlayer()) {
        		p.hidePlayer(e.getPlayer());
        		new Tasker() {
					public void run() {
		        		p.showPlayer(e.getPlayer());
					}
				}.runLater(5);
        	}
    	if(API.arena!=null)
            API.arena.join(e.getPlayer());
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        e.setQuitMessage("");
        KillStreaks.reset(e.getPlayer());
        API.arena.notifyLeave(e.getPlayer());
    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent e) {
    	if(e.getPlayer().getGameMode()==GameMode.CREATIVE)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void food(FoodLevelChangeEvent e) {
    	e.setFoodLevel(20);
    }

    @EventHandler
    public void regain(EntityRegainHealthEvent e) {
    	e.setAmount(20);
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
    	if(e.getPlayer().getGameMode()==GameMode.CREATIVE)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void shoot(EntityShootBowEvent e) {
        if(!API.arena.isInRegion(e.getEntity().getLocation())) {
        	e.setCancelled(true);
        	((Player) e.getEntity()).updateInventory();
        }
    }

    @EventHandler
    public void shoot(ProjectileLaunchEvent e) {
    	List<Entity> ee = projectiles.get(e.getEntity().getShooter());
    	if(ee==null) {
    		projectiles.put((Player)e.getEntity().getShooter(), ee=new ArrayList<>());
    	}
    	ee.add(e.getEntity());
    }

    @EventHandler
    public void weather(WeatherChangeEvent e) {
    	e.setCancelled(true);
    }

    @EventHandler
    public void weather(ThunderChangeEvent e) {
    	e.setCancelled(true);
    }

    @EventHandler
    public void leaves(LeavesDecayEvent e) {
    	e.setCancelled(true);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if(e.isCancelled())return;
    	if(e.getPlayer().getGameMode()==GameMode.CREATIVE)return;
        if(!API.arena.isInRegion(e.getBlock().getLocation())) {
        	e.setCancelled(true);
        	return;
        }
        if(e.getBlock().getType().equals(Material.HARD_CLAY))
            blocky.put(e.getBlock().getLocation(),new BlockStateRemove(e.getPlayer(),3));
        if(e.getBlock().getType().equals(Material.GOLD_PLATE)){
        	jumps.put(e.getBlock().getLocation(), new BlockStateRemove(e.getPlayer(),5));
        }
    }
    
    @EventHandler
    public void spawn(CreatureSpawnEvent e) {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void spawn(ItemSpawnEvent e) {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void interact(PlayerInteractEvent e) {
    	if(e.getPlayer().getGameMode()==GameMode.CREATIVE)return;
        if(e.getClickedBlock()!=null) {
	        if(!API.arena.isInRegion(e.getClickedBlock().getLocation())) {
	        	e.setCancelled(true);
	        	e.setUseItemInHand(Result.DENY);
	        	e.setUseInteractedBlock(Result.DENY);
	        }
        }else {
	        if(!API.arena.isInRegion(e.getPlayer().getLocation())) {
	        	e.setCancelled(true);
	        	e.setUseItemInHand(Result.DENY);
	        	e.setUseInteractedBlock(Result.DENY);
	        }
        }
    }
}

 class BlockStateRemove {
    Player player;
    boolean giveBack=true;
    long placeTime;
    int tickTime;
    int i;
    public BlockStateRemove(Player p,int time){
        player=p;
        tickTime=time;
        placeTime=System.currentTimeMillis()/1000+time;
    }
}
