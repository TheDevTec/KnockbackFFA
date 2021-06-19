package me.devtec.knockbackffa;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.devtec.theapi.utils.Position;

@SuppressWarnings("deprecation")
public class KnockEvents implements Listener {
    protected static Map<Position, BlockStateRemove> blocky = new HashMap<>();
    protected static Map<Position, BlockStateRemove> jumps = new HashMap<>();
    protected static Map<Player, Player> lastHit = new HashMap<>();
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
    	if(API.arena!=null)
        API.arena.join(e.getPlayer());
    }

    @EventHandler
    public void onHit(EntityDamageEvent e) {
        if(API.arena.isInRegion(e.getEntity().getLocation()))
        	e.setCancelled(true);
    	if(API.arena.dead.contains(e.getEntity()))e.setCancelled(true);
        if(e.getCause()==DamageCause.ENTITY_ATTACK||e.getCause()==DamageCause.PROJECTILE)return;
        if(e.getCause()==DamageCause.VOID||e.getCause()==DamageCause.LAVA||e.getCause()==DamageCause.FIRE||e.getCause()==DamageCause.FIRE_TICK) {
        	API.arena.notifyDeath((Player)e.getEntity());
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onHitByPlayer(EntityDamageByEntityEvent e) {
        if(API.arena.isInRegion(e.getEntity().getLocation()))
        	e.setCancelled(true);
        if(e.getDamager() instanceof Projectile)
        	if(API.arena.isInRegion(((Player)((Projectile)e.getDamager()).getShooter()).getLocation()))
        		e.setCancelled(true);
        	else
        if(API.arena.isInRegion(e.getDamager().getLocation()))
        	e.setCancelled(true);
        
        if(e.getEntity()instanceof Player) {
            if(e.getDamager() instanceof Player) {
	            lastHit.put((Player)e.getEntity(), (Player)e.getDamager());
	    		if(((Damageable) e.getEntity()).getHealth()-0.5<=0) {
	    			API.arena.notifyDeath((Player) e.getEntity());
	    		}else
	    			e.setDamage(0.5);
            }else if(e.getDamager() instanceof Projectile) {
            	if((Player)((Projectile)e.getDamager()).getShooter()==e.getEntity()) {
            		e.setCancelled(true);
            		return;
            	}
	                lastHit.put((Player)e.getEntity(), (Player)((Projectile)e.getDamager()).getShooter());
        		if(((Damageable) e.getEntity()).getHealth()-10<=0) {
        			API.arena.notifyDeath((Player) e.getEntity());
        		}else
        			e.setDamage(10);
        	}
        }
    }

    @EventHandler
    public void onFoodChange(PlayerJoinEvent e) {
        e.setJoinMessage("");
    }

    @EventHandler
    public void onFoodChange(PlayerQuitEvent e) {
        e.setQuitMessage("");
    }

    @EventHandler
    public void onFoodChange(EntityPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(EntityRegainHealthEvent e) {
    	if(!((Player)e.getEntity()).hasPotionEffect(PotionEffectType.REGENERATION))
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(EntityAirChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(PlayerChatEvent e) {
    	e.setFormat(e.getPlayer().getName()+" §8> §7"+e.getMessage());
    }

    @EventHandler
    public void onFoodChange(BlockBreakEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(EntityShootBowEvent e) {
        if(API.arena.isInRegion(e.getEntity().getLocation()))
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(API.arena.isInRegion(e.getBlock().getLocation())) {
        	e.setCancelled(true);
        	return;
        }
        if(e.getBlock().getType().equals(Material.WHITE_TERRACOTTA))
            blocky.put(new Position(e.getBlock().getLocation()),new BlockStateRemove(e.getPlayer(),3));
        if(e.getBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)){
        	jumps.put(new Position(e.getBlock().getLocation()), new BlockStateRemove(e.getPlayer(),15));
        }
    }
    @EventHandler
    public void onLaunchpad(PlayerInteractEvent e){
        if(e.getAction()== Action.PHYSICAL){
            if(e.getClickedBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)){
                e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().normalize().add(new Vector(0,1.75,0)));
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
