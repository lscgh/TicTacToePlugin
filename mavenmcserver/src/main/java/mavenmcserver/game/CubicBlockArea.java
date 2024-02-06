package mavenmcserver.game;

import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.joml.Vector3i;

public class CubicBlockArea {

	public Location startBlock;
	public Location endBlock;
	
	public boolean contains(Location block) {
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
	
	public Vector3i size() {
		int width = this.getPositiveDifference(this.startBlock.getBlockX(), this.endBlock.getBlockX()) + 1;
		int height = this.getPositiveDifference(this.startBlock.getBlockY(), this.endBlock.getBlockY()) + 1;
		int depth = this.getPositiveDifference(this.startBlock.getBlockZ(), this.endBlock.getBlockZ()) + 1;
		
		return new Vector3i(width, height, depth);
	}
	
	
	public void forEach(Consumer<Block> action) {
		
		Vector3i size = this.size();
		for(int x = 0; x < size.x; x++) {
			for(int y = 0; y < size.y; y++) {
				for(int z = 0; z < size.z; z++) {
					
					Location currentLocation = new Location(this.startBlock.getWorld(), this.startBlock.getBlockX() + x, this.startBlock.getBlockY() + y, this.startBlock.getBlockZ() + z);
					Block currentBlock = currentLocation.getBlock();
					action.accept(currentBlock);
					
				}
			}
		}
		
	}
	
}
