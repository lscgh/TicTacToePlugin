# TicTacToePlugin


## Description
A minecraft spigot plugin for playing "tic tac toe" against other players.
Games can be both *two-dimensional* and *three-dimensional*.

## Usage
Use the `/tictactoe` command and provide an opponent player (and *optionally* the game's size) to start a new game.

Usage: /tictactoe <opponent: Player> \[sizeX = 3\] \[sizeY = 1\] \[sizeZ = 3\] \[winRequiredAmount = 3\]


The smallest possible game has the size `(2, 1, 2)`.

`winRequiredAmount` is the amount of fields that have to be marked by one player for that player to win. This number must not be larger than the biggest dimension of the game.

After this command has been executed, the opponent player reveives a chat invitation message containing the game's size. Using that message (or using the `/tictactoeaccept` command and providing the main player's name) they can join the game.

After the opponent player accepted the game, the plugin places the game into the world (in front of where the player was when first executing the `/tictactoe` command). See below for images.

The opponent player begins. Taking turns, both players can mark one field at a time by right clicking the neutral (white) blocks. The main player has the color **red** and the opponent player has the color **light blue**.

Markings that are *"in the air"*, meaning that there are still neutral fields below them, will fall until they *"hit"* a non-neutral block or the bottom of the game.

As soon as one player marked `winRequiredAmount` fields in a row (or diagonally), the game stops, shows the fields that are in a row and tells both players whether they won or whether they lost. In case of a tie, no player wins and a tie-message appears.

The player who lost (or, in case of a tie, both players) can immediately request a return match using a chat message (or the command `/tictactoe requestReturnMatch`).

During a game, both players can cancel it anytime by executing `/tictactoe cancel`.

## Installation

Download the latest (older releases are very buggy) **JAR**-file from the releases of this repository (or build the plugin yourself using **maven**) and move it to the `plugins` directory of your server. If your server is not hosted locally, you might need to use **FTP** to transfer the file. 

## State

In development. The game should already be playable, but there might still be some bugs!


## Images

![A 3x3x3 game](img/img1.png)

![A 3x1x3 game](img/img2.png)
