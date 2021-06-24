package me.devtec.knockbackffa.events;

import org.bukkit.entity.Player;

import me.devtec.knockbackffa.API;
import me.devtec.knockbackffa.Arena;
import me.devtec.theapi.utils.listener.Event;

public class ArenaDeathEvent extends Event {
	private final Player p, k;
	
	public ArenaDeathEvent(Player s, Player killer) {
		p=s;
		k=killer;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Player getKiller() {
		return k;
	}
	
	public Arena getArena() {
		return API.getArena();
	}
}
