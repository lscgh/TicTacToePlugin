package mavenmcserver.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandTicTacToe implements CommandExecutor {
	
	public static String commandName = "tictactoe";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		sender.sendMessage("You just executed /tictactoe");
		
		return true;
	}

	
	
}
