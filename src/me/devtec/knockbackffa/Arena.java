package me.devtec.knockbackffa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.ItemCreatorAPI;
import me.devtec.theapi.blocksapi.BlocksAPI;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.User;

public class Arena {
	public static ItemStack arrow = hide(ItemCreatorAPI.create(Material.ARROW, 1, "&7Sip"));
	
	public Location spawn;
	public Position a,b;
	public ItemStack[] itemStacks =
            Arrays.asList(
            		unb(addEnchants(ItemCreatorAPI.create(Material.STICK, 1, "&cKnockback tycka"))),
                    unb(addEnchants2(ItemCreatorAPI.create(Material.BOW, 1, "&3Luk"))),
                    ItemCreatorAPI.create(Material.WHITE_TERRACOTTA, 64, "&cBlocky"),
                    ItemCreatorAPI.create(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 1, "&eJumpPad"),
                    new ItemStack(Material.AIR),
                    new ItemStack(Material.AIR),
                    new ItemStack(Material.AIR),
                    arrow,
                    ItemCreatorAPI.create(Material.ENDER_PEARL, 1, "&5Ender Perla")).toArray(new ItemStack[0]);

    public Arena(Data data) {
    	spawn=data.getAs("spawn", Location.class);
    	a=data.getAs("a", Position.class);
    	b=data.getAs("b", Position.class);
	}

	private ItemStack unb(ItemStack addEnchants2) {
		ItemMeta a= addEnchants2.getItemMeta();
		a.setUnbreakable(true);
		a.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		addEnchants2.setItemMeta(a);
		return addEnchants2;
	}

	private static ItemStack hide(ItemStack addEnchants2) {
		addEnchants2.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 0);
		ItemMeta a= addEnchants2.getItemMeta();
		a.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		addEnchants2.setItemMeta(a);
		return addEnchants2;
	}

	private ItemStack addEnchants(ItemStack create) {
		create.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
		return create;
	}

	private ItemStack addEnchants2(ItemStack create) {
		create.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
		return create;
	}
	
	public boolean isInRegion(Location loc) {
		return BlocksAPI.isInside(new Position(loc), a, b);
	}
	
	public void join(Player dead) {
    	dead.teleport(spawn);
    	dead.setHealth(20);
    	dead.setFireTicks(-20);
    	dead.getInventory().clear();
    	dead.getInventory().setContents(itemStacks);
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
    		killer.getInventory().addItem(arrow);
    		killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 3, false, false));
    		TheAPI.msg("&b▪&8| &bKBFFA &8› &7Zabil jsi hrace &b"+dead.getName(), killer);
    		TheAPI.msg("&b▪&8| &bKBFFA &8› &7Byl jsi zabit hracem &b"+killer.getName(), dead);
        	User user = TheAPI.getUser(killer);
        	user.setAndSave("kbffa.kills", user.getInt("kbffa.kills")+1);
    	}else {
    		TheAPI.msg("&b▪&8| &bKBFFA &8› &7Zemrel jsi", dead);
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
