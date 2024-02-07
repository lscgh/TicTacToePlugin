package mavenmcserver.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.LightningStrikeEvent;

public class GameListener implements Listener {

	Game game;
	
	/**
	 * If set to *false*, the game's players cannot mark any fields anymore.
	 * True to *false* when the game ends.
	 */
	public boolean allowMarkingFields = true;
	
	public GameListener(Game game) {
		this.game = game;
	}
	
	public void activate() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this.game.plugin);
	}
	
	public void deactivate() {
		HandlerList.unregisterAll(this);
	}
	
	private boolean isAuthorizedPlayer(Player player) {
		return player == this.game.config.mainPlayer || player == this.game.config.opponentPlayer;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(this.game.gameArea.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(this.game.gameArea.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntitySummon(EntitySpawnEvent event) {
		if(this.game.gameArea.contains(event.getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if(this.game.gameArea.contains(event.getLocation())) {
			event.setCancelled(true);
		}
		
		event.blockList().removeIf((block) -> this.game.gameArea.contains(block.getLocation()));
	}
	
	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		if(this.game.gameArea.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
		
		event.blockList().removeIf((block) -> this.game.gameArea.contains(block.getLocation()));
	}
	
	@EventHandler
	public void onLightningStrike(LightningStrikeEvent event) {
		if(this.game.gameArea.contains(event.getLightning().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(this.game.gameArea.contains(event.getTo())) {
			event.setCancelled(!this.isAuthorizedPlayer(event.getPlayer()));
		}
	}
		
	@EventHandler
	public void onPlayerDamaged(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		Player player = (Player)event.getEntity();
		if(this.game.gameArea.contains(player.getLocation())) {
			event.setCancelled(this.isAuthorizedPlayer(player));
		}
	}
	
	@EventHandler
	public void onBlockMove(BlockFromToEvent event) {
		if(this.game.gameArea.contains(event.getToBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler 
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.LEFT_CLICK_BLOCK) return;
		
			
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && this.game.getPlayerInTurn() == event.getPlayer()) {
			
			if(!this.allowMarkingFields) {
				event.setCancelled(true);
				return;
			}
			
			if(this.game.gameArea.contains(event.getClickedBlock().getLocation())) {
				
				try {
					FieldPoint position = this.game.state.blockLocationToFieldPoint(this.game.location, event.getClickedBlock().getLocation());

					if(this.game.state.fieldPointIsValid(position)) {
						this.game.placeAt(position);
					}
				} catch (IllegalArgumentException e) {}
				
			}
			
			event.setCancelled(true);
		}
	}
	
}
