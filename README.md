# othello

*Goal:* _Implement a playable Othello game_

Two players 'O' and 'X'

Players take alternate turns.

'X' goes first and *must* place an 'X' on the board, in such a position that there exists at least one straight (horizontal, vertical, or diagonal) occupied line of 'O's between the new 'X' and another 'X' on the board.

After placing the piece, all 'O's lying on all straight lines between the new 'X' and any existing 'X' are captured (i.e. they turn into 'X's )

Now 'O' plays. 'O' operates under the same rules, with the roles reversed: 'O' places an 'O' on the board in such a position where *at least one* 'X' is captured

Moves are specified as coordinates. Column+row or row+column (e.g. 3d or d3)

If a player cannot make a valid move (_capturing at least one of the opposing player's pieces along a straight line_), play passes back to the other player.

The game ends when either
 1. neither player can make a valid move
 2. the board is full

The player with the most pieces on the board at the end of the game wins.

For more detail: https://en.wikipedia.org/wiki/Reversi
- translate dark = 'X' and light = 'O'
- ignore the bit about the clock

__How-To  Run__
1) Under the project folder, we will use the maven to build
run "mvn clean package" and it will build the jar in the target folder.
* Make sure you setup M2_HOME, MAVEN_HOME in your path variable. Also Add the mvn binary path in the path variable

2) run "java -jar ./target/othello.jar" in the command prompt

__Sample Output__
##### *a player wins*
