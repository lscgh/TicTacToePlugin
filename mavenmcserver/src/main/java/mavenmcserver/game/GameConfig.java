package mavenmcserver.game;

import java.util.ArrayList;
import java.util.List;

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
	
	/// Returns an empty list if everything is OK.
	public List<String> validate() {
		ArrayList<String> errors = new ArrayList<String>();
		
		if(this.winRequiredAmount > this.size.maxComponent()) {
			errors.add("The required win amount must not be larger than the size's largest dimension");
		}
		
		return errors;
	}
}
