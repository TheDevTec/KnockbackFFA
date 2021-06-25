package me.devtec.knockbackffa.events;

import me.devtec.knockbackffa.Arena;
import me.devtec.theapi.utils.listener.Event;

public class ArenaSwitchEvent extends Event {
	private final Arena from;
	private Arena to;
	
	public ArenaSwitchEvent(Arena from, Arena to) {
		this.from=from;
		this.to=to;
	}
	
	public Arena getFrom() {
		return from;
	}
	
	public void setArena(Arena switchTo) {
		to=switchTo;
	}
	
	public Arena getTo() {
		return to;
	}
}
