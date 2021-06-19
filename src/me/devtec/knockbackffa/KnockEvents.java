package me.devtec.knockbackffa;

import java.util.HashMap;
import java.util.Map;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.blocksapi.BlocksAPI;
import me.devtec.theapi.scheduler.Tasker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import me.devtec.theapi.utils.Position;
import org.bukkit.util.Vector;

public class KnockEvents implements Listener {
    protected static Map<Position, BlockStateRemove> blocky = new HashMap<>();
    protected static Map<Player, Player> lastHit = new HashMap<>();
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
    	if(API.arena!=null)
        API.arena.join(e.getPlayer());
    }

    @EventHandler
    public void onHit(EntityDamageEvent e) {
        if(e.getCause()==DamageCause.ENTITY_ATTACK)return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onHitByPlayer(EntityDamageByEntityEvent e) {
        if(e.getEntity()instanceof Player) {
            e.setDamage(0);
            if(e.getDamager() instanceof Player)
            lastHit.put((Player)e.getEntity(), (Player)e.getDamager());
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(EntityAirChangeEvent e) {
        e.setCancelled(true);
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(Material.WHITE_TERRACOTTA))
            blocky.put(new Position(e.getBlock().getLocation()),new BlockStateRemove(e.getPlayer()));
        if(e.getBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)){
            new Tasker(){
                @Override
                public void run() {
                    BlocksAPI.set(e.getBlock(), Material.AIR);
                    TheAPI.giveItem(e.getPlayer(), ItemCreatorAPI.create(Material.LIGHT_WEIGHTED_PRESSURE_PLATE,1,"&eJumpPad"));
                }
            }.runLater(15*20);
        }
    }
    @EventHandler
    public void onLaunchpad(PlayerInteractEvent e){
        if(e.getAction()== Action.PHYSICAL){
            if(e.getClickedBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)){
                e.getPlayer().setVelocity(e.getPlayer().getVelocity().add(new Vector(0,2,0)));
            }
        }
    }
}

 class BlockStateRemove {
    Player player;
    boolean giveBack=true;
    long placeTime = System.currentTimeMillis()/1000+5;
    int i;
    public BlockStateRemove(Player p){
        player=p;
    }
}
