package me.devtec.knockbackffa;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.blocksapi.BlocksAPI;
import me.devtec.theapi.scheduler.Tasker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;

public class KnockEvents implements Listener {
    protected static Map<Location, BlockStateRemove> blocky = new HashMap<>();
    protected static Map<Player, Player> lastHit = new HashMap<>();
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
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
    public void onBlockyEvenTi(BlockPlaceEvent e){
        blocky.put(e.getBlock().getLocation(),new BlockStateRemove(e.getPlayer()));
        new Tasker(){
            @Override
            public void run() {
                if(e.getBlock().getType().equals(Material.WHITE_TERRACOTTA)){
                    BlocksAPI.set(e.getBlock(),Material.YELLOW_TERRACOTTA);
                    return;
                }
                if(e.getBlock().getType().equals(Material.YELLOW_TERRACOTTA)){
                    BlocksAPI.set(e.getBlock(),Material.ORANGE_TERRACOTTA);
                    return;
                }
                if(e.getBlock().getType().equals(Material.ORANGE_TERRACOTTA)){
                    BlocksAPI.set(e.getBlock(),Material.RED_TERRACOTTA);
                    return;
                }
                if(e.getBlock().getType().equals(Material.PINK_TERRACOTTA)){
                    BlocksAPI.set(e.getBlock(),Material.LIGHT_BLUE_TERRACOTTA);
                    return;
                }
                if(e.getBlock().getType().equals(Material.RED_TERRACOTTA)){
                    BlocksAPI.set(e.getBlock(), Material.AIR);
                    TheAPI.giveItem(e.getPlayer(), ItemCreatorAPI.create(Material.WHITE_TERRACOTTA,1,"&cBlocks"));
                    blocky.remove(e.getBlock().getLocation());
                }
            }
        }.runRepeating(40,40);
    }
}
class BlockStateRemove {
    Player player;
    boolean giveBack=true;
    public BlockStateRemove(Player p){
        player=p;
    }
}