package mavenmcserver.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.Location;
import org.joml.Vector3i;
import org.junit.jupiter.api.Test;

public class CubicBlockAreaTest {

	@Test
	public void testContains() {
		
		CubicBlockArea area = new CubicBlockArea(new Location(null, 0, 0, 0), new Location(null, 1, 1, 1));
		
		Location locationToTest = new Location(null, 2, 1, 1);
		assertFalse(area.contains(locationToTest));
		locationToTest = new Location(null, 1, 2, 1);
		assertFalse(area.contains(locationToTest));
		locationToTest = new Location(null, 1, 1, 2);
		assertFalse(area.contains(locationToTest));
		
		
		locationToTest = new Location(null, -1, 0, 0);
		assertFalse(area.contains(locationToTest));
		locationToTest = new Location(null, 0, -1, 0);
		assertFalse(area.contains(locationToTest));
		locationToTest = new Location(null, 0, 0, -1);
		assertFalse(area.contains(locationToTest));
		
		locationToTest = new Location(null, 100, 0, 0);
		assertFalse(area.contains(locationToTest));
		locationToTest = new Location(null, 0, -200, 0);
		assertFalse(area.contains(locationToTest));
		locationToTest = new Location(null, 1, 1, 3);
		assertFalse(area.contains(locationToTest));
		
		locationToTest = new Location(null, 0, 0, 0);
		assertTrue(area.contains(locationToTest));
		locationToTest = new Location(null, 1, 0, 0);
		assertTrue(area.contains(locationToTest));
		locationToTest = new Location(null, 0, 1, 0);
		assertTrue(area.contains(locationToTest));
		locationToTest = new Location(null, 0, 0, 1);
		assertTrue(area.contains(locationToTest));
		locationToTest = new Location(null, 1, 1, 0);
		assertTrue(area.contains(locationToTest));
		locationToTest = new Location(null, 0, 1, 1);
		assertTrue(area.contains(locationToTest));
		locationToTest = new Location(null, 1, 0, 1);
		assertTrue(area.contains(locationToTest));
		locationToTest = new Location(null, 1, 1, 1);
		assertTrue(area.contains(locationToTest));
		
	}
	
	@Test
	public void testSize() {
		
		CubicBlockArea area = new CubicBlockArea(new Location(null, 0, 0, 0), new Location(null, 1, 1, 1));
		assertEquals(new Vector3i(2, 2, 2), area.size());
		
		area = new CubicBlockArea(new Location(null, 0, 0, 0), new Location(null, 0, 0, 0));
		assertEquals(new Vector3i(1, 1, 1), area.size());
		
		area = new CubicBlockArea(new Location(null, 0, 0, 0), new Location(null, 2, 3, 2));
		assertEquals(new Vector3i(3, 4, 3), area.size());
		
		area = new CubicBlockArea(new Location(null, 0, 0, 0), new Location(null, 100, 50, 75));
		assertEquals(new Vector3i(101, 51, 76), area.size());
		
		area = new CubicBlockArea(new Location(null, 1, 1, 1), new Location(null, 0, 0, 0));
		assertEquals(new Vector3i(2, 2, 2), area.size());
		
		area = new CubicBlockArea(new Location(null, 2, 50, 3), new Location(null, 0, 0, 0));
		assertEquals(new Vector3i(3, 51, 4), area.size());
		
		area = new CubicBlockArea(new Location(null, 0, 0, 0), new Location(null, -1, 0, -1));
		assertEquals(new Vector3i(2, 1, 2), area.size());
		
		area = new CubicBlockArea(new Location(null, -2, -3, 0), new Location(null, 0, 0, 5));
		assertEquals(new Vector3i(3, 4, 6), area.size());
		
	}
	
}

