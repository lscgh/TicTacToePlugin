package mavenmcserver.game;

import org.bukkit.entity.Player;
import org.joml.Vector3i;

public class GameConfig {
	public Player mainPlayer;
	public Player opponentPlayer;
	public Vector3i size;
	public int winRequiredAmount;
	
	public GameConfig(Player mainPlayer, Player opponentPlayer, Vector3i size, int winRequiredAmount) {
		this.mainPlayer = mainPlayer;
		this.opponentPlayer = opponentPlayer;
		this.size = size;
		this.winRequiredAmount = winRequiredAmount;
	}
}
