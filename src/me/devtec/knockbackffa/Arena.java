package me.devtec.knockbackffa;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.utils.datakeeper.Data;

public class Arena {
	public Location spawn;
	public ItemStack[] itemStacks =
            Arrays.asList(
                    addEnchants(ItemCreatorAPI.create(Material.STICK, 1, "&cKnockback stick")),
                    ItemCreatorAPI.create(Material.BOW, 1, "&3Luk"),
                    ItemCreatorAPI.create(Material.ARROW, 1, "&7Šíp"),
                    ItemCreatorAPI.create(Material.ENDER_PEARL, 1, "&5Ender Pearl"),
                    ItemCreatorAPI.create(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 1, "&eJumpPad"),
                    ItemCreatorAPI.create(Material.WHITE_TERRACOTTA, 64, "&cBlocks")).toArray(new ItemStack[0]);

    public Arena(Data data) {
    	spawn=data.getAs("spawn", Location.class);
	}

	private ItemStack addEnchants(ItemStack create) {
		create.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
		return create;
	}

	public void join(Player target) {
    	target.teleport(spawn);
    	target.getInventory().clear();
    	for(ItemStack st : itemStacks)
    		target.getInventory().addItem(st.clone());
    }

    public void moveAll(Arena arena) {
        if (arena != null)
            for (Player player : TheAPI.getOnlinePlayers())
                arena.join(player);
    }

    public ItemStack[] getItemStacks() {
        return itemStacks;
    }
}
