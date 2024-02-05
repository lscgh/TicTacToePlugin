package mavenmcserver.game;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mavenmcserver.Plugin;
import mavenmcserver.game.GameState.FieldState;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Game {
	
		/// Contains all  queued games that still have to be accepted / rejected
		public static HashMap<UUID, Game> queuedGames = new HashMap<UUID, Game>();
		
		/// Contains all games that are currently running in connection to their players (every game is in this map twice!)
		public static HashMap<Player, Game> runningGames = new HashMap<Player, Game>();
		
		/// Contains a list of game setups (value) that were lost by player (key) (ties count as well).
		public static HashMap<Player, GameConfig> lostGames = new HashMap<Player, GameConfig>();

		public UUID uuid = UUID.randomUUID();
		public GameConfig config;
		public GameListener listener;
		public Location location;
		public GameState state;
		boolean opponentPlayersTurn = true;
		public CubicBlockArea gameArea; // the area to protect
		public Plugin plugin;
		
		// Repeatedly makes the blocks fall
		public BukkitRunnable grativtyRunnable;
		
		private HashMap<Location, BlockData> beforeGameBlocks = new HashMap<Location, BlockData>();
		
		public Game(GameConfig config, Plugin plugin, boolean isReturnMatch) {
			Game.queuedGames.put(this.uuid, this);
			
			this.config = config;
			this.location = this.generateGameLocation();
			
			this.plugin = plugin;
			
			this.listener = new GameListener(this);
			this.state = new GameState(this.config.size);
			
			Location startBlock = new Location(this.location.getWorld(), this.location.getBlockX() - 2, this.location.getBlockY() - 1, this.location.getBlockZ() - 2);
			Location endBlock = new Location(this.location.getWorld(), this.location.getBlockX() + this.config.size.x * 2, this.location.getBlockY() + this.config.size.y * 2, this.location.getBlockZ() + this.config.size.z * 2);
			this.gameArea = new CubicBlockArea(startBlock, endBlock);
			
			this.grativtyRunnable = new BukkitRunnable() {
				
				@Override
				public void run() {
					plugin.getLogger().info("Applying Gravity Tick to game '" + uuid.toString() + "'");
					state.applyGravityTick();
				}
				
			};
			
			this.inviteOpponent(isReturnMatch);
		}
		
		private Location generateGameLocation() {
			// double type to get rid of casting in the switch statement!
			double gameWidthInBlocks = (double)this.config.size.x * 2 - 1;
			double gameDepthInBlocks = (double)this.config.size.z * 2 - 1;
			
			double offsetX = 0, offsetZ = 0;
			
			switch(this.config.mainPlayer.getFacing()) {
			case NORTH: // towards negative Z
				offsetX = -Math.floor(gameWidthInBlocks / 2);
				offsetZ = -gameDepthInBlocks - 2;
				break;
			case EAST: // towards positive X
				offsetX = 2;
				offsetZ = -Math.floor(gameDepthInBlocks / 2);
				break;
			case SOUTH: // towards positive Z
				offsetX = -Math.floor(gameWidthInBlocks / 2);
				offsetZ = 2;
				break;
			case WEST: // towards negative X
				offsetX = -gameWidthInBlocks - 2;
				offsetZ = -Math.floor(gameDepthInBlocks / 2);
				break;
			default:
					break;
			}
			
			
			Location playerLocation = this.config.mainPlayer.getLocation();
			return new Location(playerLocation.getWorld(), playerLocation.getBlockX() + offsetX, playerLocation.getBlockY(), playerLocation.getBlockZ() + offsetZ);
		}
		
		private void inviteOpponent(boolean isReturnMatch) {
			if(isReturnMatch) {
				this.config.opponentPlayer.sendMessage("Hello " + ChatColor.AQUA + ChatColor.BOLD + this.config.opponentPlayer.getName() + ChatColor.RESET + "! " + ChatColor.AQUA + ChatColor.BOLD + this.config.mainPlayer.getName() + ChatColor.RESET + " would like to play a return match with you!");
			} else {
				this.config.opponentPlayer.sendMessage("Hello " + ChatColor.AQUA + ChatColor.BOLD + this.config.opponentPlayer.getName() + ChatColor.RESET + "! " + ChatColor.AQUA + ChatColor.BOLD + this.config.mainPlayer.getName() + ChatColor.RESET + " would like to play a game of tic-tac-toe with you!");
			}
			
			this.config.opponentPlayer.sendMessage("It has a size of (" + ChatColor.BOLD + this.config.size.x + ChatColor.RESET + ", " + ChatColor.BOLD + this.config.size.y + ChatColor.RESET + ", " + ChatColor.BOLD + this.config.size.z + ChatColor.RESET + ") and you need " + ChatColor.BOLD + this.config.winRequiredAmount + ChatColor.RESET + " fields in a row to win!");
			
			BaseComponent[] invitationComponent = new ComponentBuilder("Click ")
					.append("here").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tictactoeaccept " + this.uuid.toString())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to accept")))
					.append(" to accept the game!").reset().create();
			this.config.opponentPlayer.spigot().sendMessage(invitationComponent);
		}
		
		public void start() {
			this.listener.activate();
			
			// Store old blocks
			this.beforeGameBlocks.clear();
			this.gameArea.forEach((block) -> this.beforeGameBlocks.put(block.getLocation(), block.getBlockData()));
			
			// Fill area with air (except for bottom layer)
			this.gameArea.forEach((block) -> {
				if(block.getLocation().getBlockY() != this.gameArea.startBlock.getBlockY()) {
					block.setType(Material.AIR);
				}
			});
			
			// Base plate
			for(int x = 0; x < this.config.size.x * 2 - 1; x++) {
				for(int z = 0; z < this.config.size.z * 2 - 1; z++) {
					this.location.getWorld().getBlockAt(this.location.getBlockX() + x, this.location.getBlockY(), this.location.getBlockZ() + z).setType(Material.BLACK_CONCRETE);
				}
			}
			
			// Fields
			for(int x = 0; x < this.config.size.x; x++) {
				for(int y = 0; y < this.config.size.y; y++) {
					for(int z = 0; z < this.config.size.z; z++) {
						this.location.getWorld().getBlockAt(this.location.getBlockX() + x * 2, this.location.getBlockY() + 1 + y * 2, this.location.getBlockZ() + z * 2).setType(Material.WHITE_CONCRETE);
					}
				}
			}
			
			this.grativtyRunnable.runTaskTimer(this.plugin, 0, 10);
			
			this.config.mainPlayer.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + this.config.opponentPlayer.getName() + ChatColor.RESET + " has accepted your game!");
			
			this.registerStarted();
		}
		
		private void registerStarted() {
			// Mark this game as runing
			Game.queuedGames.remove(this.uuid);
			Game.runningGames.put(this.config.mainPlayer, this);
			Game.runningGames.put(this.config.opponentPlayer, this);
			
			// Tell players who have requested a game with either mainPlayer or
			// opponentPlayer that they are not available anymore
			for (Entry<UUID, Game> queuedGameEntry : Game.queuedGames.entrySet()) {
				Game queuedGame = queuedGameEntry.getValue();
				if (queuedGame.config.opponentPlayer == this.config.opponentPlayer) {
					queuedGame.config.mainPlayer.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + this.config.opponentPlayer.getName() + ChatColor.RESET + " has just accepted another game.");
				} else if (queuedGame.config.opponentPlayer == this.config.mainPlayer) {
					queuedGame.config.mainPlayer.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + this.config.mainPlayer.getName() + ChatColor.RESET + " has just started their own game of tic-tac-toe.");
				}
			}

			// Remove redundant games:
			Game.queuedGames.entrySet().removeIf(e -> (e.getValue().config.opponentPlayer == this.config.opponentPlayer || e.getValue().config.opponentPlayer == this.config.mainPlayer));
		}
		
		/**
		 * Describes the cause for a tic-tac-toe games ending.
		 */
		public enum GameEndCause {
			MAIN_WIN,
			OPPONENT_WIN,
			TIE,
			CANCEL
		}
		
		public void end(GameEndCause cause) {
			this.listener.deactivate();
			
			// Restore old blocks
			this.gameArea.forEach((block) -> block.setBlockData(this.beforeGameBlocks.get(block.getLocation())));
			
			// Send cause-specific message
			switch(cause) {
			case CANCEL:
				String message = "Your current game of tic-tac-toe was " + ChatColor.YELLOW + ChatColor.BOLD + "cancelled" + ChatColor.RESET + "!";
				this.config.mainPlayer.sendMessage(message);
				this.config.opponentPlayer.sendMessage(message);
				break;
			case MAIN_WIN:
				this.config.mainPlayer.sendMessage("You " + ChatColor.GREEN + ChatColor.BOLD + "won" + ChatColor.RESET + " the game!");
				this.config.opponentPlayer.sendMessage("You " + ChatColor.RED + ChatColor.BOLD + "lost" + ChatColor.RESET + " the game!");
				this.config.opponentPlayer.spigot().sendMessage(new ComponentBuilder("Click ").append("here").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tictactoe requestReturnMatch")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to request another game"))).append(" to request a return match.").reset().create());
				Game.lostGames.put(this.config.opponentPlayer, new GameConfig(this.config.opponentPlayer, this.config.mainPlayer, this.config.size, this.config.winRequiredAmount));
				break;
			case OPPONENT_WIN:
				this.config.opponentPlayer.sendMessage("You " + ChatColor.GREEN + ChatColor.BOLD + "won" + ChatColor.RESET + " the game!");
				this.config.mainPlayer.sendMessage("You " + ChatColor.RED + ChatColor.BOLD + "lost" + ChatColor.RESET + " the game!");
				this.config.mainPlayer.spigot().sendMessage(new ComponentBuilder("Click ").append("here").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tictactoe requestReturnMatch")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to request another game"))).append(" to request a return match.").reset().create());
				Game.lostGames.put(this.config.mainPlayer, new GameConfig(this.config.mainPlayer, this.config.opponentPlayer, this.config.size, this.config.winRequiredAmount));
				break;
			case TIE:
				String tieMessage = "This game ended with a " + ChatColor.YELLOW + ChatColor.BOLD + "tie" + ChatColor.RESET + "!";
				this.config.mainPlayer.sendMessage(tieMessage);
				this.config.opponentPlayer.sendMessage(tieMessage);
				
				BaseComponent returnMatchMessage[] = new ComponentBuilder("Click ").append("here").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tictactoe requestReturnMatch")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to request another game"))).append(" to request another game.").reset().create();
				
				this.config.mainPlayer.spigot().sendMessage(returnMatchMessage);
				this.config.opponentPlayer.spigot().sendMessage(returnMatchMessage);
				
				Game.lostGames.put(this.config.mainPlayer, new GameConfig(this.config.mainPlayer, this.config.opponentPlayer, this.config.size, this.config.winRequiredAmount));
				Game.lostGames.put(this.config.opponentPlayer, new GameConfig(this.config.opponentPlayer, this.config.mainPlayer, this.config.size, this.config.winRequiredAmount));
				break;
			}
			
			this.grativtyRunnable.cancel();
			
			
			this.registerEnded();
		}
		
		private void registerEnded() {
			Game.runningGames.remove(this.config.mainPlayer);
			Game.runningGames.remove(this.config.opponentPlayer);
		}
		
		/**
		 * The current player in turn marks the field at *position*.
		 * @param position
		 */
		public void placeAt(FieldPoint position) {
			
			if(this.state.getStateAt(position) != FieldState.NEUTRAL) return;
			
			this.state.setStateAt(position, this.opponentPlayersTurn ? FieldState.OPPONENT : FieldState.MAIN);
			
			
			Location inWorldLocation = this.state.fieldPointToBlockLocation(this.location, position);
			
			this.location.getWorld().getBlockAt(inWorldLocation).setType(this.opponentPlayersTurn ? Material.LIGHT_BLUE_CONCRETE : Material.RED_CONCRETE);
			
			if(this.state.getWinnerIfAny(this.config.winRequiredAmount, position) != FieldState.NEUTRAL) {
				this.end(this.opponentPlayersTurn ? GameEndCause.OPPONENT_WIN : GameEndCause.MAIN_WIN);
				return;
			}
			
			if(!this.state.winIsPossible()) {
				this.end(GameEndCause.TIE);
				return;
			}
			
			this.opponentPlayersTurn = !this.opponentPlayersTurn;
		}
		
		
		public Player getPlayerInTurn() {
			return this.opponentPlayersTurn ? this.config.opponentPlayer : this.config.mainPlayer;
		}
	
}
