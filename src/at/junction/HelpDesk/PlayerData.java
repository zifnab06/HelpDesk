package at.junction.HelpDesk;

import java.io.Serializable;

import org.bukkit.Location;
import java.util.Collection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerData implements Serializable {
	public String name;
	public ItemStack[] inventory;
	public Location loc;
	public float fallDamage;
	public Collection<PotionEffect> potionEffects;

	public PlayerData(String name_in, ItemStack[] pi_in, Location loc_in, float fd_in, Collection<PotionEffect> pe_in){
		name = name_in;
		inventory = pi_in;
		loc = loc_in;
		fallDamage = fd_in;
		potionEffects = pe_in;
	}
}
