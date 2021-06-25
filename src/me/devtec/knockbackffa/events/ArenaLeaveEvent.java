package me.devtec.knockbackffa.events;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import me.devtec.knockbackffa.API;
import me.devtec.knockbackffa.Arena;
import me.devtec.theapi.utils.listener.Event;

public class ArenaLeaveEvent extends Event {
	private final Player p, k;
	
	public ArenaLeaveEvent(Player s, Player killer) {
		p=s;
		k=killer;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	@Nullable
	public Player getKiller() {
		return k;
	}
	
	public Arena getArena() {
		return API.getArena();
	}
}
