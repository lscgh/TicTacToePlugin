package mavenmcserver.game;

import org.bukkit.Location;
import org.joml.Vector3i;

/**
 * Represents a point inside a game, in fields, NOT in blocks.
 */
class FieldPoint extends Vector3i {
	public FieldPoint(int x, int y, int z) {
		super(x, y, z);
	}
}

/**
 * Represents the state of a Game.
 */
public class GameState {
	
	public enum FieldState {
		NEUTRAL, // Not marked yet
		MAIN, // Marked by main player
		OPPONENT // Marked by opponent player
	}

	public Vector3i gameSize;
	
	FieldState blockStates[];
	
	public GameState(Vector3i gameSize) {
		this.gameSize = gameSize;
		
		int numberOfFields = gameSize.x * gameSize.y * gameSize.z;
		this.blockStates = new FieldState[numberOfFields];
		
		for(int i = 0; i < numberOfFields; i++) {
			this.blockStates[i] = FieldState.NEUTRAL;
		}
	}
	
	public FieldPoint blockLocationToFieldPoint(Location gameStartBlock, Location block) throws IllegalArgumentException {
		if(block.getWorld() != gameStartBlock.getWorld()) throw new IllegalArgumentException("The given location must be in the same world as the Game");
		
		int offsetX = block.getBlockX() - gameStartBlock.getBlockX();
		if(offsetX % 2 != 0 || offsetX < 0) throw new IllegalArgumentException("The x offset (" + offsetX + ") must not be odd or negative");
		
		int offsetY = block.getBlockY() - gameStartBlock.getBlockY() - 1;
		if(offsetY % 2 != 0 || offsetY < 0) throw new IllegalArgumentException("The y offset (" + offsetY + ") must not be odd or negative");
		
		int offsetZ = block.getBlockZ() - gameStartBlock.getBlockZ();
		if(offsetZ % 2 != 0 || offsetZ < 0) throw new IllegalArgumentException("The z offset (" + offsetZ + ") must not be odd or negative");
		
		return new FieldPoint(offsetX / 2, offsetY / 2, offsetZ / 2);
	}
	
	public Location fieldPointToBlockLocation(Location gameStartBlock, FieldPoint point) {
		return new Location(gameStartBlock.getWorld(), gameStartBlock.getBlockX() + point.x * 2, gameStartBlock.getBlockY() + point.y * 2, gameStartBlock.getBlockZ() + point.z * 2);
	}
	
	public boolean fieldPointIsValid(FieldPoint point) {
		boolean valuesArePositive = point.x >= 0 && point.y >= 0 && point.z >= 0;
		boolean valuesAreInSize = point.x < this.gameSize.x && point.y < this.gameSize.y && point.z < this.gameSize.z;
		return valuesArePositive && valuesAreInSize;
	}
	
	
	public FieldState getStateAt(FieldPoint position) {
		if(!this.fieldPointIsValid(position)) throw new IllegalArgumentException("position " + position + " is invalid for size " + this.gameSize);
		
		return this.blockStates[position.x * this.gameSize.x * this.gameSize.y + position.y * this.gameSize.y + position.z];
	}
	
	public FieldState getStateAt(int x, int y, int z) {
		return this.getStateAt(new FieldPoint(x, y, z));
	}
	
	public void setStateAt(FieldPoint position, FieldState newState) {
		if(!this.fieldPointIsValid(position)) throw new IllegalArgumentException("point " + position + " is invalid for size " + this.gameSize);
		
		this.blockStates[position.x * this.gameSize.x * this.gameSize.y + position.y * this.gameSize.y + position.z] = newState;
	}
	
	public void setStateAt(int x, int y, int z, FieldState newState) {
		this.setStateAt(new FieldPoint(x, y, z), newState);
	}
	
	
	FieldState getWinnerIfAny() {
		return FieldState.NEUTRAL; // TODO: finish
	}
	
	boolean winIsPossible() {
		return true; // TODO: finish
	}
	
	
	@Override
	public String toString() {
		return "(GameConfig: " + this.blockStates + " with size (" + this.gameSize + "))";
	}
	
}
