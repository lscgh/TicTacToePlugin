package mavenmcserver.game;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.joml.Vector3i;

/**
 * Represents a point inside a game, in fields, NOT in blocks.
 */
class FieldPoint {
	public int x = 0;
	public int y = 0;
	public int z = 0;
	
	public FieldPoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public FieldPoint offsetBy(int x, int y, int z) {
		return new FieldPoint(this.x + x, this.y + y, this.z + z);
	}
	
	@Override
	public String toString() {
		return "fp(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof FieldPoint)) return false;
		FieldPoint point = (FieldPoint)obj;
		return point.x == this.x && point.y == this.y && point.z == this.z;
	}
	
	@Override
	public int hashCode() {
		int result = (this.x ^ (this.x >>> 32));
		
		result = 31 * result + (this.y ^ (this.y >>> 32));
		result = 31 * result + (this.z ^ (this.z >>> 32));
		
		return result;
	}
	
	public FieldPoint copy() {
		return new FieldPoint(this.x, this.y, this.z);
	}
}

/**
 * Represents the state of a Game.
 */
public class GameState {
	
	public static int CONVERSION_Y_OFFSET = 1;
	
	public static Vector3i DIRECTIONS_TO_CHECK[] = {
			// Straight
		new Vector3i(1, 0, 0),
		new Vector3i(-1, 0, 0),
		new Vector3i(0, 1, 0),
		new Vector3i(0, -1, 0),
		new Vector3i(0, 0, 1),
		new Vector3i(0, 0, -1),
		// Flat diagonal
		new Vector3i(1, 0, 1),
		new Vector3i(1, 0, -1),
		new Vector3i(-1, 0, 1),
		new Vector3i(-1, 0, -1),
		new Vector3i(1, 1, 0),
		new Vector3i(1, -1, 0),
		new Vector3i(-1, 1,0),
		new Vector3i(-1, -1, 0),
		new Vector3i(0, 1, 1),
		new Vector3i(0, 1, -1),
		new Vector3i(0, -1, 1),
		new Vector3i(0, -1, -1),
		// non-flat diagonal
		new Vector3i(1, 1, 1),
		new Vector3i(1, 1, -1),
		new Vector3i(1, -1, 1),
		new Vector3i(1, -1, -1),
		new Vector3i(-1, 1, 1),
		new Vector3i(-1, 1, -1),
		new Vector3i(-1, -1, 1),
		new Vector3i(-1, -1, -1)
	};
	
	public enum FieldState {
		NEUTRAL, // Not marked yet
		MAIN, // Marked by main player
		OPPONENT // Marked by opponent player
	}

	public Vector3i gameSize;
	
	FieldState blockStates[];
	
	/// The FieldPoint of the last marked field, starting as null
	public FieldPoint lastPlacePosition = null;
	
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
		
		int offsetY = block.getBlockY() - gameStartBlock.getBlockY() - GameState.CONVERSION_Y_OFFSET;
		if(offsetY % 2 != 0 || offsetY < 0) throw new IllegalArgumentException("The y offset (" + offsetY + ") must not be odd or negative");
		
		int offsetZ = block.getBlockZ() - gameStartBlock.getBlockZ();
		if(offsetZ % 2 != 0 || offsetZ < 0) throw new IllegalArgumentException("The z offset (" + offsetZ + ") must not be odd or negative");
		
		return new FieldPoint(offsetX / 2, offsetY / 2, offsetZ / 2);
	}
	
	public Location fieldPointToBlockLocation(Location gameStartBlock, FieldPoint point) {
		return new Location(gameStartBlock.getWorld(), gameStartBlock.getBlockX() + point.x * 2, gameStartBlock.getBlockY() + GameState.CONVERSION_Y_OFFSET + point.y * 2, gameStartBlock.getBlockZ() + point.z * 2);
	}
	
	public boolean fieldPointIsValid(FieldPoint point) {
		if(point == null) return false;
		boolean valuesArePositive = point.x >= 0 && point.y >= 0 && point.z >= 0;
		boolean valuesAreInSize = point.x < this.gameSize.x && point.y < this.gameSize.y && point.z < this.gameSize.z;
		return valuesArePositive && valuesAreInSize;
	}
	
	
	public FieldState getStateAt(FieldPoint position) {
		if(!this.fieldPointIsValid(position)) throw new IllegalArgumentException("position " + position + " is invalid for size " + this.gameSize);
		
		return this.blockStates[position.x * this.gameSize.y * this.gameSize.z + position.y * this.gameSize.z + position.z];
	}
	
	public FieldState getStateAt(int x, int y, int z) {
		return this.getStateAt(new FieldPoint(x, y, z));
	}
	
	/**
	 * @param position The FieldPoint to get the state from.
	 * @return The state at the given FieldPoint. If that is invalid, <i>FieldState.NEUTRAL</i> is returned.
	 */
	public FieldState getStateIfAny(FieldPoint position) {
		try {
			return this.getStateAt(position);
		} catch(IllegalArgumentException e) {
			return FieldState.NEUTRAL;
		}
	}
	
	public void setStateAt(FieldPoint position, FieldState newState) {
		if(!this.fieldPointIsValid(position)) throw new IllegalArgumentException("point " + position + " is invalid for size " + this.gameSize);
		
		this.blockStates[position.x * this.gameSize.y * this.gameSize.z + position.y * this.gameSize.z + position.z] = newState;
		this.lastPlacePosition = position.copy();
	}
	
	public void setStateAt(int x, int y, int z, FieldState newState) {
		this.setStateAt(new FieldPoint(x, y, z), newState);
	}
	
	/**
	 * Advances the physics of this state by one tick. Every marking in air will fall down by a block.
	 * @return Whether any changes were made.
	 */
	public boolean applyGravityTick() {
		boolean didApplyAnyChange = false;
		
		for(int y = 1; y < this.gameSize.y; y++) {
			for(int x = 0; x < this.gameSize.x; x++) {
				for(int z = 0; z < this.gameSize.z; z++) {
					if(this.getStateAt(x, y, z) != FieldState.NEUTRAL) {
						if(this.getStateAt(x, y - 1, z) == FieldState.NEUTRAL) {
							FieldPoint lastPlacePosition = this.lastPlacePosition;
							
							this.setStateAt(x, y - 1, z, this.getStateAt(x, y, z));
							this.setStateAt(x, y, z, FieldState.NEUTRAL);
							
							this.lastPlacePosition = lastPlacePosition;
							
							boolean didModifyBlockAtLastPlacePosition = this.lastPlacePosition.equals(new FieldPoint(x, y, z));
							if(didModifyBlockAtLastPlacePosition) {
								this.lastPlacePosition.y -= 1;
							}
							
							didApplyAnyChange = true;
						}
					}
				}
			}
		}
		
		return didApplyAnyChange;
	}
	
	/**
	 * Updates any changes made to this state visually by placing the blocks (replaces ALL blocks).
	 * @param gameStartBlock The location of the target game.
	 */
	public void applyVisually(Location gameStartBlock) {
		
		World gameWorld = gameStartBlock.getWorld();
		
		for(int y = 0; y < this.gameSize.y; y++) {
			for(int x = 0; x < this.gameSize.x; x++) {
				for(int z = 0; z < this.gameSize.z; z++) {
					
					FieldState stateOfCurrentField = this.getStateAt(x, y, z);
					
					Material newTypeOfBlock = Game.NEUTRAL_MATERIAL;
					if(stateOfCurrentField == FieldState.MAIN) {
						newTypeOfBlock = Game.MAIN_PLAYER_MATERIAL;
					} else if(stateOfCurrentField == FieldState.OPPONENT) {
						newTypeOfBlock = Game.OPPONENT_PLAYER_MATERIAL;
					}
					
					Location inWorldLocationOfCurrentBlock = this.fieldPointToBlockLocation(gameStartBlock, new FieldPoint(x, y, z));
					gameWorld.getBlockAt(inWorldLocationOfCurrentBlock).setType(newTypeOfBlock);
					
				}
			}
		}
		
	}
	
	/**
	 * Checks if a player won the game
	 * @return NEUTRAL if there is no winner yet. MAIN if the mainPlayer has won, OPPONENT if the opponentPlayer has won!
	 */
	public FieldState getWinnerIfAny(int winRequiredAmount) {
		
		for(Vector3i direction: GameState.DIRECTIONS_TO_CHECK) {
			
			int amountOfCorrectFields = this.getFieldsInARowCount(this.lastPlacePosition, direction);
			
			if(amountOfCorrectFields >= winRequiredAmount) return this.getStateAt(this.lastPlacePosition);
			else if(amountOfCorrectFields > 1) {
				
				// Check in opposite direction
				Vector3i oppositeDirection = new Vector3i(-direction.x, -direction.y, -direction.z);
				amountOfCorrectFields += this.getFieldsInARowCount(this.lastPlacePosition, oppositeDirection) - 1; // subtract 1 because the lastChanged Block is counted twice.
				
				
				if(amountOfCorrectFields >= winRequiredAmount) return this.getStateAt(this.lastPlacePosition);
			}
			
		}
		
		return FieldState.NEUTRAL;
	}
	
	/**
	 * Counts how many fields in a row (also diagonally) are marked by the same player.
	 * @param startPoint The starting point to start the counting from.
	 * @param direction The direction in which to go.
	 * @return The count.
	 */
	private int getFieldsInARowCount(FieldPoint startPoint, Vector3i direction) {
		if(this.getStateAt(startPoint) == FieldState.NEUTRAL) throw new IllegalArgumentException("getStateAt(startPoint) == FieldState.NEUTRAL");
		
		int amountOfCorrectFields = 0;
		while(true) {
			FieldPoint point = startPoint.offsetBy(amountOfCorrectFields * direction.x, amountOfCorrectFields * direction.y, amountOfCorrectFields * direction.z);
			if(this.getStateIfAny(point) != this.getStateAt(startPoint)) break;
			
			amountOfCorrectFields++;
		}
		
		return amountOfCorrectFields;
	}
	
	
	public boolean winIsPossible() {
		
		for(FieldState blockState: this.blockStates) {
			if(blockState == FieldState.NEUTRAL) return true;
		}
		
		return false; // TODO: finish
	}
	
	public ArrayList<Location> getWinRowBlockLocations(int winRequiredAmount, Location gameStartBlock) {
		
		for(Vector3i direction : GameState.DIRECTIONS_TO_CHECK) {

			int amountOfCorrectFields = this.getFieldsInARowCount(this.lastPlacePosition, direction) - 1;
			int amountOfCorrectFieldsInOppositeDirection = 0;

			if(amountOfCorrectFields > 0 && amountOfCorrectFields < winRequiredAmount - 1) {
				// Check in opposite direction
				Vector3i oppositeDirection = new Vector3i(-direction.x, -direction.y, -direction.z);
				amountOfCorrectFieldsInOppositeDirection = this.getFieldsInARowCount(this.lastPlacePosition, oppositeDirection) - 1;
			}
			
			if(amountOfCorrectFields + amountOfCorrectFieldsInOppositeDirection < winRequiredAmount - 1) continue;
			
			ArrayList<Location> blockLocations = new ArrayList<Location>();
			
			for(int i = -amountOfCorrectFieldsInOppositeDirection; i <= amountOfCorrectFields; i++) {
				FieldPoint currentPoint = this.lastPlacePosition.offsetBy(i * direction.x, i * direction.y, i * direction.z);
				Location fieldPointAsBlockLocation = this.fieldPointToBlockLocation(gameStartBlock, currentPoint);
				blockLocations.add(fieldPointAsBlockLocation);
			}
			
			return blockLocations;
		}
		
		return null;
	}
	
	
	@Override
	public String toString() {
		return "(GameConfig: " + this.blockStates + " with size (" + this.gameSize + "))";
	}
	
}
