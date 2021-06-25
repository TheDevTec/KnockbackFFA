package me.devtec.knockbackffa.events;

import org.bukkit.entity.Player;

import me.devtec.knockbackffa.API;
import me.devtec.knockbackffa.Arena;
import me.devtec.theapi.utils.listener.Event;

public class ArenaJoinEvent extends Event {
	private final Player p;
	
	public ArenaJoinEvent(Player s) {
		p=s;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Arena getArena() {
		return API.getArena();
	}
}
