package mavenmcserver.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CommandTicTacToeTest {

	@Test
	public void testExtractIntegerArgs() {
		
		assertArrayEquals(new int[]{3, 1, 3, 3}, CommandTicTacToe.extractIntegerArgs(new String[]{"OpponentName", "3", "1", "3", "3"}));
		assertArrayEquals(new int[]{0, 0, 0, 0}, CommandTicTacToe.extractIntegerArgs(new String[]{"OpponentName", "0", "0", "0", "0"}));
		assertArrayEquals(new int[]{1, 2, 3, 4}, CommandTicTacToe.extractIntegerArgs(new String[]{"OpponentName", "1", "2", "3", "4"}));
		assertArrayEquals(new int[]{-1, 2, -3, 4}, CommandTicTacToe.extractIntegerArgs(new String[]{"OpponentName", "-1", "2", "-3", "4"}));
		
		try {
			CommandTicTacToe.extractIntegerArgs(new String[]{"OpponentName", "e", "2", "3", "4"});
			assertTrue(false, "CommandTicTacToe.extractIntegerArgs didn't throw an exception with {'OpponentName', 'e', '2', '3', '4'}");
		} catch(NumberFormatException e) {}
		
		try {
			CommandTicTacToe.extractIntegerArgs(new String[]{"OpponentName", "10", "2", "3", "4."});
			assertTrue(false, "CommandTicTacToe.extractIntegerArgs didn't throw an exception with {'OpponentName', '10', '2', '3', '4.'}");
		} catch(NumberFormatException e) {}
		
	}
	
}
