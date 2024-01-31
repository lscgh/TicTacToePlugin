package mavenmcserver.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
			TextComponent component = new TextComponent("hello");
			component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/say hi"));
			this.config.opponentPlayer.spigot().sendMessage(component);
		}
		
		
		public Player getPlayerInTurn() {
			return this.opponentPlayersTurn ? this.config.opponentPlayer : this.config.mainPlayer;
		}
	
}
