package me.devtec.knockbackffa;

import java.util.HashMap;
import java.util.Map;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.placeholderapi.ThePlaceholderAPI;

public class API {
    protected static Map<String, Arena> arenas = new HashMap<>();
    protected static Arena arena;

    public static Arena nextArena() {
        return arena=TheAPI.getRandomFromCollection(arenas.values());
    }


    public static Map<String, Arena> getArenas(){
        return arenas;
    }
}
