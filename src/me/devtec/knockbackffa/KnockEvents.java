package me.devtec.knockbackffa;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;

public class KnockEvents implements Listener {
    
    Map<Player, Player> lastHit = new HashMap<>();
    
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
}