package mavenmcserver.game;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3i;
import org.junit.jupiter.api.Test;

import mavenmcserver.game.GameState.FieldState;

public class GameStateTest {

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
	public void testGetWinnerIfAny() {
		
		GameState state = new GameState(new Vector3i(3, 3, 3));
		
		state.setStateAt(0, 0, 0, FieldState.OPPONENT);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(3, new FieldPoint(0, 0, 0)));
		
		state.setStateAt(0, 1, 0, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(3, new FieldPoint(0, 1, 0)));
		
		state.setStateAt(1, 0, 0, FieldState.OPPONENT);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(3, new FieldPoint(1, 0, 0)));
		
		state.setStateAt(0, 2, 0, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(3, new FieldPoint(0, 2, 0)));
		
		state.setStateAt(2, 0, 0, FieldState.OPPONENT);
		assertEquals(FieldState.OPPONENT, state.getWinnerIfAny(3, new FieldPoint(2, 0, 0)));
		
		
		state = new GameState(new Vector3i(3, 3, 3));
		
		state.setStateAt(0, 0, 0, FieldState.OPPONENT);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(3, new FieldPoint(0, 0, 0)));
		
		state.setStateAt(0, 0, 1, FieldState.OPPONENT);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(3, new FieldPoint(0, 0, 1)));
		
		state.setStateAt(0, 0, 2, FieldState.OPPONENT);
		assertEquals(FieldState.OPPONENT, state.getWinnerIfAny(3, new FieldPoint(0, 0, 2)));
		
		
		state = new GameState(new Vector3i(3, 3, 3));
		
		state.setStateAt(0, 0, 0, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(3, new FieldPoint(0, 0, 0)));
		
		state.setStateAt(1, 1, 1, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(3, new FieldPoint(1, 1, 1)));
		
		state.setStateAt(2, 2, 2, FieldState.MAIN);
		assertEquals(FieldState.MAIN, state.getWinnerIfAny(3, new FieldPoint(2, 2, 2)));
		
		
		state = new GameState(new Vector3i(3, 1, 3));
		
		state.setStateAt(0, 0, 0, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(3, new FieldPoint(0, 0, 0)));
		
		state.setStateAt(2, 0, 0, FieldState.MAIN);
		assertEquals(FieldState.NEUTRAL, state.getWinnerIfAny(3, new FieldPoint(2, 0, 0)));
		
		state.setStateAt(1, 0, 0, FieldState.MAIN);
		assertEquals(FieldState.MAIN, state.getWinnerIfAny(3, new FieldPoint(1, 0, 0)));
		
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
	
}

