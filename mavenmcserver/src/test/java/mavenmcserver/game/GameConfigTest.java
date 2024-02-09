package mavenmcserver.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.joml.Vector3i;
import org.junit.jupiter.api.Test;

public class GameConfigTest {

	// This test is a little useless...
	@Test
	public void testValidate() {
		GameConfig config = new GameConfig(null, null, new Vector3i(2, 1, 2), 2);
		List<String> errors = config.validateReturningErrors(100);
		assertTrue(errors.contains(GameConfig.ERROR_MAIN_PLAYER_NULL));
	}
	
	
	@Test
	public void testValidateNumbers() {
		GameConfig config = new GameConfig(null, null, new Vector3i(0, 0, 0), 2);
		List<String> errors = config.validateNumbersReturningErrors(100);
		assertTrue(errors.contains("No dimension of the game can be smaller than 1. The smallest possible game is (" + GameConfig.MIN_X_Z_SIZE + ", " + GameConfig.MIN_HEIGHT + ", " + GameConfig.MIN_X_Z_SIZE + ")."));
		assertTrue(errors.contains("The X and Z size of the game must not be smaller than " + GameConfig.MIN_X_Z_SIZE + "."));
		assertTrue(errors.contains(String.format(GameConfig.ERROR_WIN_REQUIRED_AMOUNT_TOO_LARGE, config.getLargestDimension())));
		
		config = new GameConfig(null, null, new Vector3i(0, 1, 2), 2);
		errors = config.validateNumbersReturningErrors(100);
		assertTrue(errors.contains("No dimension of the game can be smaller than 1. The smallest possible game is (" + GameConfig.MIN_X_Z_SIZE + ", " + GameConfig.MIN_HEIGHT + ", " + GameConfig.MIN_X_Z_SIZE + ")."));

		config = new GameConfig(null, null, new Vector3i(3, 1, 2), 4);
		errors = config.validateNumbersReturningErrors(100);
		assertTrue(errors.contains(String.format(GameConfig.ERROR_WIN_REQUIRED_AMOUNT_TOO_LARGE, config.getLargestDimension())));
		
		config = new GameConfig(null, null, new Vector3i(1, 1, 2), 3);
		errors = config.validateNumbersReturningErrors(100);
		assertTrue(errors.contains("The X and Z size of the game must not be smaller than " + GameConfig.MIN_X_Z_SIZE + "."));
		
	}
	
	
	@Test
	public void testGetLargestDimension() {
		assertEquals(5, new GameConfig(null, null, new Vector3i(1, 4, 5), 0).getLargestDimension());
		assertEquals(3, new GameConfig(null, null, new Vector3i(3, 2, 1), 0).getLargestDimension());
		assertEquals(10, new GameConfig(null, null, new Vector3i(10, 4, 8), 0).getLargestDimension());
		assertEquals(0, new GameConfig(null, null, new Vector3i(0, 0, 0), 0).getLargestDimension());
	}
	
	@Test
	public void testGetSmallestDimension() {
		assertEquals(1, new GameConfig(null, null, new Vector3i(1, 4, 5), 0).getSmallestDimension());
		assertEquals(1, new GameConfig(null, null, new Vector3i(3, 2, 1), 0).getSmallestDimension());
		assertEquals(4, new GameConfig(null, null, new Vector3i(10, 4, 8), 0).getSmallestDimension());
		assertEquals(0, new GameConfig(null, null, new Vector3i(0, 0, 0), 0).getSmallestDimension());
	}
	
}
