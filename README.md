# TicTacToePlugin


## Description
A minecraft spigot plugin for playing "tic tac toe" against other players.
Games can be both *two-dimensional* and *three-dimensional*.

## Usage
Use the `/tictactoe` command and provide an opponent player (and *optionally* the game's size) to start a new game.

Usage: /tictactoe <opponent: Player> \[sizeX = 3\] \[sizeY = 1\] \[sizeZ = 3\] \[winRequiredAmount = 3\]


The smallest possible game has the size `(2, 1, 2)`.

`winRequiredAmount` is the amount of fields that have to be marked by one player for that player to win. This number must not be larger than the biggest dimension of the game.

## State

Still **early** in development.
