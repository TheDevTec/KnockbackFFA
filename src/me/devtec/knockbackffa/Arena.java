package me.devtec.knockbackffa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.User;

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
    
    List<Player> dead = new ArrayList<>();
    
    public void notifyDeath(Player dead) {
    	if(this.dead.contains(dead))return;
    	this.dead.add(dead);
    	dead.teleport(spawn);
    	dead.setHealth(20);
    	dead.setFireTicks(-20);
    	dead.getInventory().clear();
    	dead.getInventory().setContents(itemStacks);
    	Player killer = KnockEvents.lastHit.remove(dead);
    	if(killer!=null) {
    		killer.getInventory().addItem(itemStacks[2]);
    		killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 3, false, false));
    		TheAPI.msg("&cYou killed player &e"+dead.getName(), killer);
    		TheAPI.msg("&cYou was killed by player &e"+killer.getName(), dead);
        	User user = TheAPI.getUser(killer);
        	user.setAndSave("kbffa.kills", user.getInt("kbffa.kills")+1);
    	}else {
    		TheAPI.msg("&cYou dead", dead);
    	}
    	User user = TheAPI.getUser(dead);
    	user.setAndSave("kbffa.deaths", user.getInt("kbffa.deaths")+1);
    	for(BlockStateRemove r : KnockEvents.blocky.values())
    		if(r.player.equals(dead))r.giveBack=false;
    	for(BlockStateRemove r : KnockEvents.jumps.values())
    		if(r.player.equals(dead))r.giveBack=false;
    	new Tasker() {
			public void run() {
				Arena.this.dead.remove(dead);
			}
		}.runLater(20);
    }
}
