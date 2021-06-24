package me.devtec.knockbackffa;

import java.util.HashMap;
import java.util.Map;

import me.devtec.theapi.TheAPI;

public class API {
    protected static Map<String, Arena> arenas = new HashMap<>();
    protected static Arena arena;

    public static Arena nextArena() {
    	if(arenas.size()<=1) {
        	if(arena==null)return arena=TheAPI.getRandomFromCollection(arenas.values());
    		return arena;
    	}
    	Arena next = TheAPI.getRandomFromCollection(arenas.values());
    	while(next.equals(arena))
    		next=TheAPI.getRandomFromCollection(arenas.values());
    	return arena=next;
    }
    
    public static Map<String, Arena> getArenas(){
        return arenas;
    }

	public static Arena getArena() {
		// TODO Auto-generated method stub
		return arena;
	}
}
