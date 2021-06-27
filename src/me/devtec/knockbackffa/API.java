package me.devtec.knockbackffa;

import java.util.HashMap;
import java.util.Map;

import me.devtec.theapi.TheAPI;

public class API {
    protected static Map<String, Arena> arenas = new HashMap<>();
    protected static Arena arena;

    public static Arena nextArena() {
    	if(arenas.size()<=1) {
        	return TheAPI.getRandomFromCollection(arenas.values());
    	}
    	Arena next = TheAPI.getRandomFromCollection(arenas.values());
    	while(next.equals(arena))
    		next=TheAPI.getRandomFromCollection(arenas.values());
    	return next;
    }
    
    public static void setArena(Arena current) {
    	arena=arena.moveAll(current);
    }
    
    public static Map<String, Arena> getArenas(){
        return arenas;
    }

	public static Arena getArena() {
		return arena;
	}
}
