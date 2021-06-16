package me.devtec.knockbackffa;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Arena {
	public Location spawn;
	public ItemStack[] itemStacks =
            Arrays.asList(
                    new ItemStack(Material.STICK, 1),
                    new ItemStack(Material.BOW, 1),
                    new ItemStack(Material.ARROW, 1),
                    new ItemStack(Material.ENDER_PEARL, 1),
                    new ItemStack(Material.STONE_PRESSURE_PLATE, 1),
                    new ItemStack(Material.RED_TERRACOTTA, 64)
            ).toArray(new ItemStack[0]);

    public void join(final Player target) {
    	target.teleport(spawn);
    }

    public void moveAll(final Arena arena) {

        if (arena != null)
            for (final Player player : Bukkit.getOnlinePlayers())
                arena.join(player);

    }

    public ItemStack[] getItemStacks() {
        return itemStacks;
    }

    public static Arena getArena() {
        return new Arena();
    }
}
