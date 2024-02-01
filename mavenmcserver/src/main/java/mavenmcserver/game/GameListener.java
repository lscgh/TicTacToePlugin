package mavenmcserver.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class GameListener implements Listener {

	Game game;
	
	public GameListener(Game game) {
		this.game = game;
	}
	
	void activate() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this.game.plugin);
	}
	
	void deactivate() {
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(this.game.gameArea.contains(event.getBlock().getLocation())) {
			event.getPlayer().sendMessage("You just placed a block!");
			
			try {
				FieldPoint position = this.game.state.blockLocationToFieldPoint(this.game.location,event.getBlock().getLocation());

				if (this.game.state.fieldPointIsValid(position)) {
					event.getPlayer().sendMessage("This block's location's FieldPoint is " + position + "!");
				} else {
					event.getPlayer().sendMessage("This location could not be converted to a FieldPoint");
				}
			} catch (IllegalArgumentException e) {
				event.getPlayer().sendMessage("This location could not be converted to a FieldPoint: " + ChatColor.DARK_RED + e.getMessage() + ChatColor.RESET);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
	}
	
	@EventHandler
	public void onEntitySummon(EntitySpawnEvent event) {
		
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		
	}
	
	@EventHandler
	public void onPlayerDamaged(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
	}
	
	@EventHandler
	public void onBlockMove(BlockFromToEvent event) {
		
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
	}
	
}
