package mavenmcserver.game;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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

		public UUID uuid = UUID.randomUUID();
		public GameConfig config;
		public Location location;
		boolean opponentPlayersTurn;
		
		public Game(GameConfig config) {
			Game.queuedGames.put(this.uuid, this);
			
			this.config = config;
			this.inviteOpponent();
			// set other members
		}
		
		private void inviteOpponent() {
			this.config.opponentPlayer.sendMessage("Hello " + ChatColor.AQUA + ChatColor.BOLD + this.config.opponentPlayer.getName() + ChatColor.RESET + "! " + ChatColor.AQUA + ChatColor.BOLD + this.config.mainPlayer.getName() + ChatColor.RESET + " would like to play a game of tic-tac-toe with you!");
			BaseComponent[] invitationComponent = new ComponentBuilder("Click ")
					.append("here").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tictactoeaccept " + this.uuid.toString())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to accept")))
					.append(" to accept the game!").reset().create();
			this.config.opponentPlayer.spigot().sendMessage(invitationComponent);
		}
		
		public void start() {
			Game.queuedGames.remove(this.uuid);
			Game.runningGames.put(this.config.mainPlayer, this);
			Game.runningGames.put(this.config.opponentPlayer, this);
			
			this.config.mainPlayer.sendMessage(ChatColor.AQUA + "" + this.config.opponentPlayer.getName() + ChatColor.RESET + " has accepted your game!");
			
			// Tells players who have requested a game with either mainPlayer or opponentPlayer that they are not available anymore
			for(Entry<UUID, Game> queuedGameEntry: Game.queuedGames.entrySet()) {
				Game queuedGame = queuedGameEntry.getValue();
				if(queuedGame.config.opponentPlayer == this.config.opponentPlayer) {
					queuedGame.config.mainPlayer.sendMessage(ChatColor.AQUA + "" + this.config.opponentPlayer.getName() + ChatColor.RESET + " has just accepted another game.");
				} else if(queuedGame.config.opponentPlayer == this.config.mainPlayer) {
					queuedGame.config.mainPlayer.sendMessage(ChatColor.AQUA + "" + this.config.mainPlayer.getName() + ChatColor.RESET + " has just started their own game of tic-tac-toe.");
				}
			}
			
			// Remove redundant games:
			Game.queuedGames.entrySet().removeIf(e -> (e.getValue().config.opponentPlayer == this.config.opponentPlayer || e.getValue().config.opponentPlayer == this.config.mainPlayer));
		}
		
		public enum GameEndCause {
			MAIN_WIN,
			OPPONENT_WIN,
			TIE,
			CANCEL
		}
		
		public void end(GameEndCause cause) {
			Game.runningGames.remove(this.config.mainPlayer);
			Game.runningGames.remove(this.config.opponentPlayer);
		}
		
		
		public Player getPlayerInTurn() {
			return this.opponentPlayersTurn ? this.config.opponentPlayer : this.config.mainPlayer;
		}
	
}
