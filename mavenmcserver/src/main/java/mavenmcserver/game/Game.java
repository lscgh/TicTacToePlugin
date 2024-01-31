package mavenmcserver.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Game {

		GameConfig config;
		Location location;
		boolean opponentPlayersTurn;
		
		public Game(GameConfig config) {
			this.config = config;
			this.inviteOpponent();
			// set other members
		}
		
		private void inviteOpponent() {
			this.config.opponentPlayer.sendMessage("Hello " + ChatColor.AQUA + ChatColor.BOLD + this.config.opponentPlayer.getName() + ChatColor.RESET + "! " + ChatColor.AQUA + ChatColor.BOLD + this.config.mainPlayer.getName() + ChatColor.RESET + " would like to play a game of tic-tac-toe with you!");
			BaseComponent[] invitationComponent = new ComponentBuilder("Click ")
					.append("here").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/about")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to accept")))
					.append(" to accept the game!").reset().create();
			this.config.opponentPlayer.spigot().sendMessage(invitationComponent);
		}
		
		
		public Player getPlayerInTurn() {
			return this.opponentPlayersTurn ? this.config.opponentPlayer : this.config.mainPlayer;
		}
	
}
