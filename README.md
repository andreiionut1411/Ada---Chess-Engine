


**

## Overview

**Ada** is my first functional chess engine. It has a simple Graphical User Interface where you can play the engine. For the moment, the version 1.0 is not yet UCI compatible. It is written in Java, so it is a little slower then other engines in C++. The engine at the moment doesn't feature an opening book for the begining of the game. For the moment it has only one difficulty, but as I update the engine further, I might add different difficulties based on previous weaker versions of the engine.
In the folder src is the code for the engine, while in versions there are .jar files with all the versions, if one wishes just to start and play the game.

**Versions**
Ada 1.0: It is based on the negamax algorithm with alpha-beta pruning, calculating 6 ply deep. It uses the Piece-Square Tables Only evaluation function by Ronald Friederich.
ELO: 1500 - 1550
Ada 1.1: The evaluation function now is now "lazy" and is more accurate. It also looks at pawn structure, piece mobility, center control, and also king position during endgames. For speeding up the alpha beta, I ordered the moves and also used the Killer heuristic.
ELO: 1625 - 1650

