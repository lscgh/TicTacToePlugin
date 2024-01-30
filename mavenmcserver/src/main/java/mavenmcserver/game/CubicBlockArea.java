package mavenmcserver.game;

import org.bukkit.Location;
import org.joml.Vector3i;

public class CubicBlockArea {

	Location startBlock;
	Location endBlock;
	
	public CubicBlockArea(Location startBlock, Location endBlock) {
		if(startBlock.getWorld() != endBlock.getWorld()) {
			throw new IllegalArgumentException("Attempted to create a CubicBlockArea with two Locations in different worlds. Have '" + startBlock.getWorld().getName() + "' and '" + endBlock.getWorld().getName() + "'!");
		}
		
		this.startBlock = startBlock;
		this.endBlock = endBlock;
	}
	
	
	boolean contains(Location block) {
		if(block.getWorld() != this.startBlock.getWorld()) {
			throw new IllegalArgumentException("Attempted to execute contains() on a CubicBlockArea in world '" + this.startBlock.getWorld().getName() + "' using a location in world '" + block.getWorld().getName() + "'");
		}
		
		boolean containedOnXAxis = block.getBlockX() >= this.startBlock.getBlockX() && block.getBlockX() <= this.endBlock.getBlockX();
		boolean containedOnYAxis = block.getBlockY() >= this.startBlock.getBlockY() && block.getBlockY() <= this.endBlock.getBlockY();
		boolean containedOnZAxis = block.getBlockZ() >= this.startBlock.getBlockZ() && block.getBlockZ() <= this.endBlock.getBlockZ();
		
		return containedOnXAxis && containedOnYAxis && containedOnZAxis;
	}
	
	
	private int getPositiveDifference(int a, int b) {
		if(a > b) {
			return a - b;
		} else {
			return b - a;
		}
	}
	
	Vector3i size() {
		int width = this.getPositiveDifference(this.startBlock.getBlockX(), this.endBlock.getBlockX()) + 1;
		int height = this.getPositiveDifference(this.startBlock.getBlockY(), this.endBlock.getBlockY()) + 1;
		int depth = this.getPositiveDifference(this.startBlock.getBlockZ(), this.endBlock.getBlockZ()) + 1;
		
		return new Vector3i(width, height, depth);
	}
	
}
