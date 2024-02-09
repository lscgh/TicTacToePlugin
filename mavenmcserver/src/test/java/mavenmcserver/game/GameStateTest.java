package mavenmcserver.game;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.joml.Vector3i;
import org.junit.jupiter.api.Test;

import mavenmcserver.game.GameState.FieldState;

public class GameStateTest {
	
	public static final int TEST_WIN_REQUIRED_AMOUNT = 3;

	@Test
	public void testStateAt() {
	
		
		GameState state = new GameState(new Vector3i(3, 1, 3));
		
		state.setStateAt(0, 0, 0, FieldState.MAIN);
		assertEquals(FieldState.MAIN, state.getStateAt(0, 0, 0));
		
		state.setStateAt(1, 0, 2, FieldState.OPPONENT);
		state.setStateAt(2, 0, 1, FieldState.OPPONENT);
		assertEquals(FieldState.MAIN, state.getStateAt(0, 0, 0));
		assertEquals(FieldState.OPPONENT, state.getStateAt(1, 0, 2));
		assertEquals(FieldState.OPPONENT, state.getStateAt(2, 0, 1));
		
		try {
			state.setStateAt(3, 0, 2, FieldState.OPPONENT);
			
			assertTrue(false, "Expected setStateAt with 3, 0, 2 to throw IllegalArgumentException");
		} catch(IllegalArgumentException e) {}
		
		try {
			state.setStateAt(0, -1, 1, FieldState.MAIN);
	
			assertTrue(false, "Expected setStateAt with 0, -1, 1 to throw IllegalArgumentException");
		} catch(IllegalArgumentException e) {}
		
		state = new GameState(new Vector3i(2, 2, 2));
		
		state.setStateAt(0, 0, 0, FieldState.MAIN);
		assertArrayEquals(state.blockStates, new FieldState[]{FieldState.MAIN, FieldState.NEUTRAL, FieldState.NEUTRAL, FieldState.NEUTRAL, FieldState.NEUTRAL, FieldState.NEUTRAL, FieldState.NEUTRAL, FieldState.NEUTRAL});
		
		state.setStateAt(0, 1, 1, FieldState.OPPONENT);
		assertArrayEquals(state.blockStates, new FieldState[]{FieldState.MAIN, FieldState.NEUTRAL, FieldState.NEUTRAL, FieldState.OPPONENT, FieldState.NEUTRAL, FieldState.NEUTRAL, FieldState.NEUTRAL, FieldState.NEUTRAL});
		
		try {
			state.getStateAt(0, -1, 0);
			assertTrue(false, "Expected getStateAt with 0, -1, 0 to throw IllegalArgumentException");
		} catch(IllegalArgumentException e) {}
		
		try {
			state.getStateAt(1, 0, 2);
			assertTrue(false, "Expected getStateAt with 1, 0, 2 to throw IllegalArgumentException");
		} catch(IllegalArgumentException e) {}
		
	}
	
	@Test
	public void testFieldPointIsValid() {
		
		GameState state = new GameState(new Vector3i(3, 3, 3));
		assertTrue(state.fieldPointIsValid(new FieldPoint(0, 0, 0)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(1, 0, 0)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(0, 1, 0)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(0, 0, 1)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(1, 1, 0)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(0, 1, 1)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(1, 0, 1)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(1, 1, 1)));
		
		assertTrue(state.fieldPointIsValid(new FieldPoint(2, 0, 0)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(1, 2, 0)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(0, 1, 2)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(2, 2, 0)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(0, 2, 2)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(2, 0, 2)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(2, 2, 2)));
		assertTrue(state.fieldPointIsValid(new FieldPoint(2, 1, 2)));
		
		assertFalse(state.fieldPointIsValid(new FieldPoint(-1, 0, 0)));
		assertFalse(state.fieldPointIsValid(new FieldPoint(0, -1, 0)));
		assertFalse(state.fieldPointIsValid(new FieldPoint(0, 0, -1)));
		assertFalse(state.fieldPointIsValid(new FieldPoint(3, 0, 0)));
		assertFalse(state.fieldPointIsValid(new FieldPoint(0, 0, 5)));
		assertFalse(state.fieldPointIsValid(new FieldPoint(-2, 6, 0)));
		assertFalse(state.fieldPointIsValid(new FieldPoint(1, 6, 1)));
	}
	
	
	@Test
	public void testGetWinnerIfAny() {
		
		GameState state = new GameState(new Vector3i(3, 3, 3));
		
		state.setStateAt(0, 0, 0, FieldState.OPPONENT);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		state.setStateAt(0, 1, 0, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		state.setStateAt(1, 0, 0, FieldState.OPPONENT);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		state.setStateAt(0, 2, 0, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		state.setStateAt(2, 0, 0, FieldState.OPPONENT);
		assertEquals(FieldState.OPPONENT, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		
		state = new GameState(new Vector3i(3, 3, 3));
		
		state.setStateAt(0, 0, 0, FieldState.OPPONENT);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		state.setStateAt(0, 0, 1, FieldState.OPPONENT);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		state.setStateAt(0, 0, 2, FieldState.OPPONENT);
		assertEquals(FieldState.OPPONENT, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		
		state = new GameState(new Vector3i(3, 3, 3));
		
		state.setStateAt(0, 0, 0, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		state.setStateAt(1, 1, 1, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		state.setStateAt(2, 2, 2, FieldState.MAIN);
		assertEquals(FieldState.MAIN, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		
		state = new GameState(new Vector3i(3, 1, 3));
		
		state.setStateAt(0, 0, 0, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		state.setStateAt(2, 0, 0, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
		state.setStateAt(1, 0, 0, FieldState.MAIN);
		assertEquals(FieldState.MAIN, state.getWinnerIfAny(GameStateTest.TEST_WIN_REQUIRED_AMOUNT));
		
	}
	
	
	@Test
	public void testWinIsPossible() {
		
		GameState state = new GameState(new Vector3i(3, 1, 3));
		assertTrue(state.winIsPossible());
		
		state.setStateAt(1, 0, 1, FieldState.OPPONENT);
		assertTrue(state.winIsPossible());
		
		state.setStateAt(1, 0, 0, FieldState.MAIN);
		assertTrue(state.winIsPossible());
		
		state.setStateAt(2, 0, 0, FieldState.OPPONENT);
		assertTrue(state.winIsPossible());
		
		state.setStateAt(2, 0, 1, FieldState.MAIN);
		assertTrue(state.winIsPossible());
		
		state.setStateAt(0, 0, 1, FieldState.OPPONENT);
		assertTrue(state.winIsPossible());
		
		state.setStateAt(0, 0, 0, FieldState.MAIN);
		assertTrue(state.winIsPossible());
		
		state.setStateAt(2, 0, 2, FieldState.OPPONENT);
		assertTrue(state.winIsPossible());
		
		state.setStateAt(0, 0, 2, FieldState.MAIN);
		assertTrue(state.winIsPossible());
		
		state.setStateAt(1, 0, 2, FieldState.OPPONENT);
		assertFalse(state.winIsPossible());
		
	}
	
	public boolean stateIsNeutralExceptFor(GameState state, Map<FieldPoint, FieldState> exceptions) {
		for(int x = 0; x < state.gameSize.x; x++) {
			for(int y = 0; y < state.gameSize.y; y++) {
				for(int z = 0; z < state.gameSize.z; z++) {
					FieldPoint currentPoint = new FieldPoint(x, y, z);
					FieldState expectedState = exceptions.containsKey(currentPoint) ? exceptions.get(currentPoint) : FieldState.NEUTRAL;
					if(state.getStateAt(currentPoint) != expectedState) return false;
				}
			}
		}
		
		return true;
	}
	
	@Test
	public void testApplyGravityTick() {
		
		// 3 x 3 x 3
		GameState state = new GameState(new Vector3i(3, 3, 3));
		
		state.setStateAt(new FieldPoint(0, 2, 0), FieldState.OPPONENT);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 2, 0), FieldState.OPPONENT))));
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(0, 1, 0), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 1, 0), FieldState.OPPONENT))));
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(0, 0, 0), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 0, 0), FieldState.OPPONENT))));
		
		assertFalse(state.applyGravityTick());
		assertFalse(state.applyGravityTick());
		assertFalse(state.applyGravityTick());
		
		
		// 4 x 4 x 4
		state = new GameState(new Vector3i(4, 4, 4));
		
		
		state.setStateAt(new FieldPoint(0, 3, 0), FieldState.MAIN);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 3, 0), FieldState.MAIN))));
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(0, 2, 0), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 2, 0), FieldState.MAIN))));
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(0, 1, 0), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 1, 0), FieldState.MAIN))));
		
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(0, 0, 0), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 0, 0), FieldState.MAIN))));
		
		assertFalse(state.applyGravityTick());
		assertFalse(state.applyGravityTick());
		assertFalse(state.applyGravityTick());
		
		state.setStateAt(new FieldPoint(1, 3, 3), FieldState.OPPONENT);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 0, 0), FieldState.MAIN), entry(new FieldPoint(1, 3, 3), FieldState.OPPONENT))));
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(1, 2, 3), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 0, 0), FieldState.MAIN), entry(new FieldPoint(1, 2, 3), FieldState.OPPONENT))));
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(1, 1, 3), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 0, 0), FieldState.MAIN), entry(new FieldPoint(1, 1, 3), FieldState.OPPONENT))));
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(1, 0, 3), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(0, 0, 0), FieldState.MAIN), entry(new FieldPoint(1, 0, 3), FieldState.OPPONENT))));
		
		assertFalse(state.applyGravityTick());
		assertFalse(state.applyGravityTick());
		assertFalse(state.applyGravityTick());
		
		
		// 5 x 4 x 5
		state = new GameState(new Vector3i(5, 4, 5));
		
		state.setStateAt(new FieldPoint(4, 3, 0), FieldState.OPPONENT);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(4, 3, 0), FieldState.OPPONENT))));
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(4, 2, 0), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(4, 2, 0), FieldState.OPPONENT))));
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(4, 1, 0), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(4, 1, 0), FieldState.OPPONENT))));
		
		assertTrue(state.applyGravityTick());
		assertEquals(new FieldPoint(4, 0, 0), state.lastPlacePosition);
		assertTrue(this.stateIsNeutralExceptFor(state, Map.ofEntries(entry(new FieldPoint(4, 0, 0), FieldState.OPPONENT))));
		
		assertFalse(state.applyGravityTick());
		assertFalse(state.applyGravityTick());
		assertFalse(state.applyGravityTick());
		
	}
	
}

