# PacMan_Simplified

Description of Game:https://www.google.com/logos/2010/pacman10-i.html:
- Pacman is a game where the user controls a circle shaped character and attempts to eat all dots on the game screen. 
Each dot eaten gives you a certain number of points and there are other ways you can increase your point total
- Pacman can die by bumping into a ghost, which will cause it to one of its lives, or gain points by eating ghosts after consuming an energizer which is placed throughout the board.

**The Rules and Objectives of the Game**
- Pacman is controlled by the user up, down, left and right. Pacman can not move diagonally and is bounded by the game board. 
- Pacman will continue to move in his current direction when no input is given and will stay idle when it bumps into a wall until an input is given
- Pacman will have a certain number of lives entered by the user before the game begins
- Everytime Pacman bumps into a ghost when it is not energized, it will loose a life, each life represents a level
    - The board will restart on every level beside the dots on the board
- Once it looses all its lives, the game is over
- If Pacman consumes all the dots, the game will be over
- For every dot (5 points) or ghost consumed(100 * (2 * number of ghost consumed), pacman will get points towards a score
- Fruits will present themselves based on pacman's score during a level, each fruit has different purposes
    - Green fruit: Get +500 points
    - Red Fruit: Loose -100 points
    - Orange Fruit: Get x2 on all points when enabled

**Controls of the Game**
- Control Pacman with the left, right, up and down arrow keys
- Pressing an arrow key will move pacman at a set speed, this speed will not increase based on how many times you press an arrow key
- The player can press P to pause
- The player can press X to enable the orange fruit
- The game background will stay fixed

**Which Components will be Animated**
- The ghosts will be animated to move to different positions based on their color, the ghost will move at a constant speed 
that is a little slower than pacman's
    - Blue ghosts will animate randomly
    - Red Ghosts will animate to stay in a proximity close to pacman
    - Pink ghosts will animate around corners of the board
    - A White ghost will tail pacman
- When Pacman energiezes Ghosts will begin to move in opposite directions than they previously were
- A new one of the six ghosts will animate out of the ghost box after every 10 seconds, the first ghost will spawn immedietly in the board
- If a ghost dies, it will respawn in the ghost box and follow the same rules as above
- A "Game Over" Text will spawn once a win condition is met
- "Level: x" text will spawn after every time pacman loses a life, where x is 0 + the lives it has lost

**Description of how we will Store the State**
- Points will be stored in an integer variable
- Lives will be stored in an integer variable
- Num Ghosts in play and in the box will be stored with a variable
- Pacman's position will be stored with a x and y variable
- A Ghosts direction of travel will be stored as a boolean (changes when pacman is energized)
- If Pacman is energized or uses an orange fruit ability will be determines by a boolean 
- Number of dots will be stored using an integer varaible
