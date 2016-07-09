/*
 * [Connect4.java]
 * Description: This program is a replication of the classic Connect 4 game with the option of playing against the
 *              computer, or another person. The AI system utilizes a minimax algorithm to determine the best move it
 *              can make. The GUI uses alternation of panels to change game modes. Each execution of the program will
 *              track how many times each color, player or AI has won. 
 * Authors: Allison Chow and Jack Liu
 * November 18, 2015
 */

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;

public class Connect4 extends JFrame implements ActionListener {
  
  //creates components for the GUI
  JFrame frame = new JFrame();
  static JPanel pan = new JPanel();
  static JPanel panBoard = new JPanel(); //panel for the grid of circles acting as the game board
  static JPanel panPlay = new JPanel(); //panel for the row of buttons to place pieces
  static JPanel panGame = new JPanel(); //panel to place the game on
  static JPanel panInitial = new JPanel(); //panel to pick which game mode to play
  static JPanel panFirst = new JPanel(); //panel to decide which color goes first
  static JPanel panFirstAI = new JPanel(); //panel to decide if the AI or player goes first
  
  static JLabel title = new JLabel("CONNECT 4");
  static JLabel titleCopy = new JLabel("CONNECT 4");
  static JLabel turnLabel = new JLabel(); //outputs the current player's turn
  
  //creates layouts to be applied to panels
  GridLayout layoutBoard = new GridLayout(6, 7, 18, 18);
  GridLayout layoutPlay = new GridLayout(1, 7);
  GridLayout layoutInitial = new GridLayout(1, 2, 20, 20);
  GridLayout layoutFirst = new GridLayout(2, 2, 0, 0);
  
  String turnFirst = " "; //variable to track who was meant to go first
  String mode = " "; //variable to track which mode was selected
  
  static int ROWS = 6;
  static int COLUMNS = 7;
  
  static CircleLabel[][] grid = new CircleLabel[ROWS][COLUMNS]; //new class of circular labels
  static JButton[] play = new JButton[COLUMNS]; //buttons to place pieces
  static int[] playCount = new int[COLUMNS]; //mimics thee play buttons and tracks which rows have been filled per column
  static int[][] gridCheck = new int[ROWS][COLUMNS]; //mirrors the state of the circular label board
  static int turnCount = 1; //variable to track who's turn it is
  
  //blank labels to space GUI
  static JLabel blank = new JLabel(" ");
  static JLabel blank1 = new JLabel(" ");
  static JLabel blank2 = new JLabel(" ");
  static JLabel blank3 = new JLabel(" ");
  static JLabel blank4 = new JLabel(" ");
  
  int count = 0; //to be used in the win methods to count the number of the same pieces in a row found
  
  static int[][] AIGrid = new int[ROWS][COLUMNS]; //to store a copy of the game state to be used in the evaluating the best move method
  
  static int move;
  static JLabel resultLabel = new JLabel(" "); //label to be used when someone wins
  
  private static int maxDepth = 4;
  
  //components for initial panels/screens the player encounters
  static JButton pvpMode = new JButton("Player vs. Player Mode");
  static JButton aiMode = new JButton("Player vs. AI Mode");
  static JButton redFirst = new JButton("Red");
  static JButton yellowFirst = new JButton("Yellow");
  static JLabel goesFirst = new JLabel("Who Goes First:");
  static JLabel goesFirst2 = new JLabel("Who Goes First:");
  static JButton aiFirst = new JButton("AI");
  static JButton playerFirst = new JButton("Player");
  static JButton playAgain = new JButton("Play Again");
  
  //variables to track the number of times each player/color has won per instance of execution
  int redWin = 0;
  int yellowWin = 0;
  int computerWin = 0;
  int playerWin = 0;
  
  /*
   * A method to initialize the play buttons with a web icon, and the counters to the bottom row of each column. This
   * method also adds the components to each panel in the event of multiple plays.
   */
  public static void create() {
    
    turnCount = 1;
    
// ----------------------------------------------------- panPlay --------------------------------------------------- //    
    
    //adds an image to each of the play buttons and changes their backgrounds to blue
    try {
      
      URL icon = new URL("http://i.webch7.com/images/theme2014/sport/arrow-down.png");
      
      for (int i = 0; i < 7; i++) {
        play[i].setIcon(new ImageIcon(icon));
        play[i].setBackground(Color.WHITE);
        play[i].setEnabled(true);
        playCount[i] = 5; //initializes each counter to represent the bottom-most row
      }
      
    } catch (MalformedURLException e) {
      
      System.out.println(e); //outputs and handles any errors in the web code section
      
    }
    
    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 7; j++) {
        gridCheck[i][j] = 0; //initializes each spot on the grid to a 0, to be changed to a 1 or 2 as a piece is played
      }
    }
    
// -------------------------------------------------- end panPlay -------------------------------------------------- //
    
// --------------------------------------------------- panFirstAI -------------------------------------------------- //
    
    //adds the components to the panel for the player vs. ai intro screen
    panFirstAI.add(goesFirst2);
    panFirstAI.add(blank4);
    panFirstAI.add(aiFirst);
    panFirstAI.add(playerFirst);
    
// -------------------------------------------------- end panFirstAI ----------------------------------------------- //
    
// ---------------------------------------------------- panInitial ------------------------------------------------- //
    
    //add components to the pick a mode panel
    panInitial.add(pvpMode);
    panInitial.add(aiMode);
    
// -------------------------------------------------- end panInitial ----------------------------------------------- //  
    
// ----------------------------------------------------- panFirst -------------------------------------------------- //  
    
    //adds the components to the panel for the player vs. player intro     
    panFirst.add(goesFirst);
    panFirst.add(blank3);
    panFirst.add(redFirst);
    panFirst.add(yellowFirst);
    
// --------------------------------------------------- end panFirst ------------------------------------------------ //  
    
// ------------------------------------------------------ panGame -------------------------------------------------- //      
    
    //reset game board
    panPlay.removeAll();
    resultLabel.setText(" ");
    turnLabel.setForeground(Color.GRAY);
    
    for (int i = 0; i < 7; i++) {
      panPlay.add(play[i]);
    }
    
    //adds components to the game panel
    panGame.add(title);
    panGame.add(blank1);
    panGame.add(turnLabel);
    panGame.add(resultLabel);
    panGame.add(blank2);
    panGame.add(panPlay);
    panGame.add(blank);
    panGame.add(panBoard);
    
// --------------------------------------------------- end panGame ------------------------------------------------- //  
  }
  //end create method
  
  /*
   * The constructor to build the GUI.
   */
  public Connect4() {
    
    setLayout(new GridBagLayout());
    
    setTitle("Connect 4");
    setSize(1200, 800);
    
    //set layout for each panel
    pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
    panBoard.setLayout(layoutBoard);
    panPlay.setLayout(layoutPlay);
    panInitial.setLayout(layoutInitial);
    panFirst.setLayout(layoutFirst);
    panFirstAI.setLayout(layoutFirst);
    
    playAgain.addActionListener(this);
    
// --------------------------------------------------- panFirstAI -------------------------------------------------- //
    
    //designs components for the player vs. ai intro screen
    aiFirst.addActionListener(this);
    playerFirst.addActionListener(this);
    aiFirst.setPreferredSize(new Dimension(200, 80));
    playerFirst.setPreferredSize(new Dimension(200, 80));
    goesFirst2.setAlignmentX(Component.CENTER_ALIGNMENT);
    aiFirst.setAlignmentX(Component.CENTER_ALIGNMENT);
    playerFirst.setAlignmentX(Component.CENTER_ALIGNMENT);
    aiFirst.setBackground(Color.WHITE);
    playerFirst.setBackground(Color.WHITE);
    
// -------------------------------------------------- end panFirstAI ----------------------------------------------- //
    
// ----------------------------------------------------- panFirst -------------------------------------------------- //
    
    //designs components for the player vs. player intro panel    
    redFirst.setPreferredSize(new Dimension(200, 80));
    yellowFirst.setPreferredSize(new Dimension(200, 80));
    redFirst.setBackground(Color.WHITE);
    yellowFirst.setBackground(Color.WHITE);
    redFirst.addActionListener(this);
    yellowFirst.addActionListener(this);
    
// -------------------------------------------------- end panFirst ------------------------------------------------- //
    
// ---------------------------------------------------- panInitial ------------------------------------------------- //    
    
    //designs components for the pick a mode panel
    pvpMode.addActionListener(this);
    aiMode.addActionListener(this);
    pvpMode.setPreferredSize(new Dimension(300, 100));
    aiMode.setPreferredSize(new Dimension(300, 100));
    pvpMode.setBackground(Color.WHITE);
    aiMode.setBackground(Color.WHITE);
    titleCopy.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    //add the pick a mode panel to the frame
    pan.add(titleCopy);
    pan.add(panInitial);
    
// ------------------------------------------------- end panInitial ------------------------------------------------ //    
    
// ------------------------------------------------------ panGame -------------------------------------------------- //    
    
    //designs components to add to the game panel
    panGame.setLayout(new BoxLayout(panGame, BoxLayout.PAGE_AXIS));
    
    for (int i = 0; i < 7; i++) {
      play[i].addActionListener(this);
      play[i].setPreferredSize(new Dimension(80, 50));
    }
    
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    //creates the initial grid of circle labels
    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 7; j++) {
        grid[i][j] = new CircleLabel();
        grid[i][j].setForeground(getBackground());
        panBoard.add(grid[i][j]);
      }
    }
    
    turnLabel.setText("player " + turnCount); //initializes the turnLabel to display player 1
    
// ---------------------------------------------------- end panGame ------------------------------------------------ //    
    
    
    add(pan);
    setVisible(true);
    
  }
  //end of constructor
  
  /*
   * A new class created off of the original JLabel to repaint the shape into a circle
   */
  public class CircleLabel extends JLabel {
    
    public CircleLabel() {
      setOpaque(false);
    }
    
    //changes the size of the new labels
    @Override
    public Dimension getPreferredSize() {
      return new Dimension(36, 36);
    }
    
    //determines the available radii' of the new circle labels
    protected int getRadius() {
      return Math.min(getWidth(), getHeight());
    }
    
    @Override
    public Insets getInsets() {
      int radius = getRadius();
      Insets insets = new Insets(radius / 6, radius / 6, radius / 6, radius / 6);
      return insets;
    }
    
    //repaints the borders of the original JLabel to match the dimensions of the circle label
    @Override
    protected void paintComponent(Graphics g) {
      
      super.paintComponent(g);
      
      int radius = getRadius();
      int xOffset = (getWidth() - radius) / 2;
      int yOffset = (getHeight() - radius) / 2;
      
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.fillOval(xOffset, yOffset, (radius - 1), (radius - 1)); //fills oval with nothing so other colors can be layered on top later
      g2d.setColor(Color.GRAY); //draws the outline of a circle on the label
      g2d.drawOval(xOffset, yOffset, (radius - 1), (radius - 1));
      
      g2d.dispose();
      
    }
  }
  //end of the CircleLabel class
  
  /*
   * A method to handle any actions that occur on the GUI.
   */
  public void actionPerformed(ActionEvent event) {
    
    boolean w = false; //initialize win condition to false
    String command = event.getActionCommand();
    
    if (command.equals("Player vs. Player Mode")) {
      
      pan.removeAll(); //empties the main panel
      mode = "pvp";
      pan.add(panFirst); //switches to a new panel
      turnLabel.setText("player " + turnCount);
      setVisible(true);
      
    } else if (command.equals("Player vs. AI Mode")) {
      
      pan.removeAll();
      mode = "ai";
      pan.add(panFirstAI);
      setVisible(true);
      
    } else if (command.equals("Red")) {
      
      pan.removeAll();
      turnFirst = "Red";
      pan.add(panGame);
      setVisible(true);
      
    } else if (command.equals("Yellow")) {
      
      pan.removeAll();
      turnFirst = "Yellow";
      pan.add(panGame);
      setVisible(true);
      
    } else if (command.equals("AI")) {
      
      pan.removeAll();
      pan.add(panGame);
      setVisible(true);
      //plays an AI move first and then switches back to the player as first
      turnCount = 2;
      turnLabel.setForeground(getBackground());
      turnLabel.setText("player " + turnCount);
      int col = doComputerMove(turnCount);
      repaint(grid[playCount[col]][col]);
      playCount[col]--;
      turnCount--;
      
    } else if (command.equals("Player")) {
      
      turnLabel.setText("player " + turnCount);
      pan.removeAll();
      pan.add(panGame); //adds the panel with the game board
      setVisible(true);
      
    } else if (command.equals("Play Again")) {
      
      pan.removeAll();
      pan.add(titleCopy);
      pan.add(panInitial);
      for (int i = 0; i < 6; i++) {
        for (int j = 0; j < 7; j++) {
          grid[i][j].setForeground(getBackground()); //reset the game board backgrounds to no color
        }
      }
      setVisible(true);
      create(); //reinitializes every panel
      
    }
    
    if (mode.equals("pvp")) {
      
      for (int i = 0; i < COLUMNS; i++) {
        
        if (event.getSource() == play[i]) {
          
          if (turnCount == 1) {
            turnLabel.setText("player " + turnCount);
            if (turnFirst.equals("Yellow")) {
              grid[playCount[i]][i].setForeground(Color.YELLOW); // change in color is a visual representation of the board
            } else {
              grid[playCount[i]][i].setForeground(Color.RED);
            }
            gridCheck[playCount[i]][i] = 1; // change from a 0 to a 1 or 2 is a numerical reflection of the board
            
            w = win(turnCount, playCount[i], i, gridCheck); // checks for a win each turn
            
            playCount[i]--; // decreases the column's available row index
            
            // ends the game if a win is found
            if (w) {
              
              panPlay.removeAll(); //removes the play buttons so the users cannot make anymore moves
              panPlay.add(playAgain);
              
              if (turnFirst.equals("Yellow")) {
                resultLabel.setForeground(Color.YELLOW);
                yellowWin++; //yellow has won one more time
              } else {
                resultLabel.setForeground(Color.RED);
                redWin++; //red has won one more time
              }
              resultLabel.setText("Player 1 WINS!");
              turnLabel.setText("Yellow has won : " + yellowWin + " || Red has won : " + redWin); //displays how many times each color has won during this execution of the GUI
            }
            
            //disables the column's button if all rows in the column have been filled
            if (playCount[i] < 0) {
              play[i].setEnabled(false);
            }
            
            if (w == false) {
              //becomes the next player's turn
              turnCount++; 
              turnLabel.setText("player " + turnCount);
            }
            
          } else if (turnCount == 2) {
            if (turnFirst.equals("Yellow")) {
              grid[playCount[i]][i].setForeground(Color.RED);
            } else {
              grid[playCount[i]][i].setForeground(Color.YELLOW);
            }
            gridCheck[playCount[i]][i] = 2;
            w = win(turnCount, playCount[i], i, gridCheck);
            playCount[i]--;
            
            if (w) {
              
              panPlay.removeAll();
              panPlay.add(playAgain);
              
              if (turnFirst.equals("Yellow")) {
                resultLabel.setForeground(Color.RED);
                redWin++;
              } else {
                resultLabel.setForeground(Color.YELLOW);
                yellowWin++;
              }
              resultLabel.setText("Player 2 WINS!");
              turnLabel.setText("Yellow has won : " + yellowWin + " || Red has won : " + redWin);
            }
            
            if (playCount[i] < 0) {
              play[i].setEnabled(false);
            }
            
            if (w == false) {
              turnCount--;
              turnLabel.setText("player " + turnCount);
            }
          }
        }
      }
      
    } else if (mode.equals("ai")) {
      
      //resets status labels
      turnLabel.setForeground(Color.GRAY);
      turnLabel.setText("player " + turnCount);
      
      for (int i = 0; i < COLUMNS; i++) {
        if (event.getSource() == play[i]) {
          if (turnCount == 1) {
            
            //sets up the person's turn and stores the move made
            turnLabel.setText("player " + turnCount);
            gridCheck[playCount[i]][i] = turnCount;
            repaint(grid[playCount[i]][i]);
            
            //raises the row 
            playCount[i]--;
            
            //changes turns 
            turnCount++;
            
            if (resultLabel.getText().equals(" ")) { //will only do a computer move if no one has won
              int col = doComputerMove(turnCount); //computer move
              
              //only plays the move if it is within bounds or valid
              if (playCount[col] >= 0 && playCount[col] <= 5 && col >= 0 && col <= 7) {
                turnLabel.setText("player " + turnCount);
                repaint(grid[playCount[col]][col]);
              }
              
              playCount[col]--;
              turnCount--;
              
              if (resultLabel.getText().equals(" ")) {
                turnLabel.setText("player " + turnCount);
              } else if (!resultLabel.getText().equals(" ")) { //if a win has been found, the total score will be displayed
                turnLabel.setText("computer has won : " + computerWin + " || player has won : " + playerWin);
              }
            }
          }
        }
        
        //if all the rows in a column have been filled, user cannnot use the column's button anymore to make a move
        if (playCount[i] < 0) {
          play[i].setEnabled(false);
        }
        
      }
    }
  }
  
  /*
   * A method to change the board's color after a move has been made for the AI game mode
   */
  public void repaint(JLabel a) {
    if (turnLabel.getText().equals("player 1")) {
      
      a.setForeground(Color.RED);
      
    } else if (turnLabel.getText().equals("player 2")) {
      
      a.setForeground(Color.YELLOW);
      
    } 
  }
  //end repaint method
  
  /*
   * A method to check for a win by calling directionally checking methods
   */
  public boolean win(int turn, int x, int y, int[][] grid) {
    
    boolean winA, winB, winC, winD;
    
    //calls methods to check diagonally both ways, vertically, and horizontally from the initial move made
    winA = winDiagonalA(turn, x, y, grid);
    winB = winDiagonalB(turn, x, y, grid);
    winC = winVertical(turn, x, y, grid);
    winD = winHorizontal(turn, x, y, grid);
    
    if ((winA || winB || winC || winD)) {
      
      return true;
      
    } else {
      
      return false;
      
    }
  }
  //end win method
  
// -------------------------------------------------- win methods -------------------------------------------------- // 
  
  /*
   * These methods recursively check in a direction from the original move until the board's bounds or another person's 
   * move are met. The 2nd method to be called in conjunction with the direction goes backwards recursively and counts
   * the number of spaces that are the same color. If four are found, the method returns true
   */
  
  public boolean winDiagonalA(int turn, int x, int y, int[][] grid) {
    
    int i = (x + 1);
    int j = (y - 1);
    int c = 0;
    
    if (i >= 0 && i < ROWS && j >= 0 && j < COLUMNS && grid[i][j] == turn) {
      
      return winDiagonalA(turn, i, j, grid); //recursively call until bounds are hit or a different coloured/numbered piece is hit
      
    } else {
      
      count = 1;
      c = winDiagonalA2(turn, (i - 1), (j + 1), grid, count); //calls the method to go backwards and count
      
    }
    
    return (c > 3);
    
  }
  //end winDiagonalA method
  
  public int winDiagonalA2(int turn, int x, int y, int[][] grid, int count) {
    
    int i = (x - 1);
    int j = (y + 1);
    
    if (i >= 0 && i < ROWS && j >= 0 && j < COLUMNS && grid[i][j] == turn) {
      
      count++;
      return winDiagonalA2(turn, i, j, grid, count);
      
    } else {
      
      return count;
      
    }
  }
  //end winDiagonalA2 method
  
  public boolean winDiagonalB(int turn, int x, int y, int[][] grid) {
    
    int i = (x + 1);
    int j = (y + 1);
    int c = 0;
    
    if (i >= 0 && i < ROWS && j >= 0 && j < COLUMNS && grid[i][j] == turn) {
      
      return winDiagonalB(turn, i, j, grid);
      
    } else {
      
      count = 1;
      c = winDiagonalB2(turn, (i - 1), (j - 1), grid, count);
      
    }
    
    return (c > 3);
    
  }
  //end winDiagonalB method
  
  public int winDiagonalB2(int turn, int x, int y, int[][] grid, int count) {
    
    int i = (x - 1);
    int j = (y - 1);
    
    if (i >= 0 && i < ROWS && j >= 0 && j < COLUMNS && grid[i][j] == turn) {
      
      count++;
      return winDiagonalB2(turn, i, j, grid, count);
      
    } else {
      
      return count;
      
    }
  }
  //end winDiagonalB2 method
  
  public boolean winVertical(int turn, int x, int y, int[][] grid) {
    
    int i = (x + 1);
    int c = 0;
    
    if (i >= 0 && i < ROWS && y >= 0 && y < COLUMNS && grid[i][y] == turn) {
      
      return winVertical(turn, i, y, grid);
      
    } else {
      
      count = 1;
      c = winVertical2(turn, (i - 1), y, grid, count);
      
    }
    
    return (c > 3);
    
  }
  //end winVertical method
  
  public int winVertical2(int turn, int x, int y, int[][] grid, int count) {
    
    int i = (x - 1);
    
    if (i >= 0 && i < ROWS && y >= 0 && y < COLUMNS && grid[i][y] == turn) {
      
      count++;
      return winVertical2(turn, i, y, grid, count);
      
    } else {
      
      return count;
      
    }
  }
  //end winVertical2 method
  
  public boolean winHorizontal(int turn, int x, int y, int[][] grid) {
    
    int j = (y - 1);
    int c = 0;
    
    if (x >= 0 && x < ROWS && j >= 0 && j < COLUMNS && grid[x][j] == turn) {
      
      return winHorizontal(turn, x, j, grid);
      
    } else {
      
      count = 1;
      c = winHorizontal2(turn, x, (j + 1), grid, count);
      
    }
    
    return (c > 3);
    
  }
  //end winHorizontal method
  
  public int winHorizontal2(int turn, int x, int y, int[][] grid, int count) {
    
    int j = (y + 1);
    
    if (x >= 0 && x < ROWS && j >= 0 && j < COLUMNS && grid[x][j] == turn) {
      
      count++;
      return winHorizontal2(turn, x, j, grid, count);
      
    } else {
      
      return count;
      
    }
  }
  //end winHorizontal2 method
  
// ---------------------------------------------- end win methods -------------------------------------------------- //  
  
// ---------------------------------------------- AI move methods -------------------------------------------------- //    
  
  private int doComputerMove(int turn) {
    
    int col = returnMove(turn);
    putMove(col, turn);
    int gameResult = checkResult();
    if (gameResult == 1) {
      System.out.println("You Win!"); //tracks the game result in the console
      resultLabel.setText("You Win!");
      playerWin++; //player has won one more time
      resultLabel.setForeground(Color.ORANGE);
      turnLabel.setText("computer has won : " + computerWin + " || player has won : " + playerWin); //displays the number of times each player has won
      panPlay.removeAll(); //takes away the buttons that the player uses to make a move
      panPlay.add(playAgain); //allows the player the ability to play the game again
      setVisible(true);
    } else if (gameResult == 2) {
      System.out.println("AI Win!");
      resultLabel.setText("Computer Wins!");
      computerWin++; //computer has won one more time
      resultLabel.setForeground(Color.ORANGE);
      turnLabel.setText("computer has won : " + computerWin + " || player has won : " + playerWin);
      panPlay.removeAll();
      panPlay.add(playAgain);
      setVisible(true);
    } else if (gameResult == 0) {
      System.out.println("Draw!");
      resultLabel.setText("Draw!!");
      turnLabel.setText("computer has won : " + computerWin + " || player has won : " + playerWin);
      panPlay.removeAll();
      panPlay.add(playAgain);
      setVisible(true);
    }
    
    return col;
    
  }
  //end doComputerMove method
  
  /*
   * A method to check the result of each game board's state per new move made
   */
  public int checkResult() {
    int aiScore = 0, humanScore = 0;
    for (int i = 5; i >= 0; --i) {
      for (int j = 0; j <= 6; ++j) {
        if (gridCheck[i][j] == 0)
          continue;
        
        // Checking cells to the right
        if (j <= 3) {
          for (int k = 0; k < 4; ++k) {
            if (gridCheck[i][j + k] == 1)
              aiScore++;
            else if (gridCheck[i][j + k] == 2)
              humanScore++;
            else
              break;
          }
          if (aiScore == 4)
            return 1;
          else if (humanScore == 4)
            return 2;
          aiScore = 0;
          humanScore = 0;
        }
        
        // Checking cells up
        if (i >= 3) {
          for (int k = 0; k < 4; ++k) {
            if (gridCheck[i - k][j] == 1)
              aiScore++;
            else if (gridCheck[i - k][j] == 2)
              humanScore++;
            else
              break;
          }
          if (aiScore == 4)
            return 1;
          else if (humanScore == 4)
            return 2;
          aiScore = 0;
          humanScore = 0;
        }
        
        // Checking diagonal up-right
        if (j <= 3 && i >= 3) {
          for (int k = 0; k < 4; ++k) {
            if (gridCheck[i - k][j + k] == 1)
              aiScore++;
            else if (gridCheck[i - k][j + k] == 2)
              humanScore++;
            else
              break;
          }
          if (aiScore == 4)
            return 1;
          else if (humanScore == 4)
            return 2;
          aiScore = 0;
          humanScore = 0;
        }
        
        // Checking diagonal up-left
        if (j >= 3 && i >= 3) {
          for (int k = 0; k < 4; ++k) {
            if (gridCheck[i - k][j - k] == 1)
              aiScore++;
            else if (gridCheck[i - k][j - k] == 2)
              humanScore++;
            else
              break;
          }
          if (aiScore == 4)
            return 1;
          else if (humanScore == 4)
            return 2;
          aiScore = 0;
          humanScore = 0;
        }
      }
    }
    
    for (int j = 0; j < 7; ++j) { // Game has not ended yet
      if (gridCheck[0][j] == 0)
        return -1;
    } // Game draw!
    return 0;
    
  }
  //end checkResult method
  
  /*
   * A method to undo the move you just placed because they were "pseudo" or possible moves to evaluate
   */
  public void undoMove(int column) {
    for (int i = 0; i <= 5; ++i) {
      if (gridCheck[i][column] != 0) {
        gridCheck[i][column] = 0;
        break;
      }
    }
  }
  //end undoMove method
  
  /*
   * A method to check if the column is filled or not
   */
  public boolean isLegalMove(int column) {
    
    return gridCheck[0][column] == 0;
    
  }
  //end isLegalMove method
  
  /*
   * A method to place the final best move per AI turn
   */
  public boolean putMove(int column, int player) {
    if (!isLegalMove(column)) {
      System.out.println("Illegal move!");
      return false;
    }
    for (int i = 5; i >= 0; --i) {
      if (gridCheck[i][column] == 0) {
        gridCheck[i][column] = (byte) player;
        return true;
      }
    }
    return false;
  }
  //end putMove method
  
  /*
   * A method that copies the integer gridCheck array onto the the AIarray for evaluation
   */
  public static void copyArray() {
    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 7; j++) {
        AIGrid[i][j] = gridCheck[i][j];
      }
    }
  }
  //end copyArray
  
// --------------------------------------------- end AI move methods ----------------------------------------------- //    
  
// ---------------------------------------------- evaluation methods ----------------------------------------------- // 
  
  /*
   * Checks for #-in a row for that gamestate
   * This is for evaluation methods.
   * It looks at the board and sees if there is #'s in a row, for four in a row it returns 1000, three in a row it
   * returns 100, two in a row it returns 10, and 1 in a row it returns 1. It makes sure its in bounds in each check or
   * the program will crash.
   */
  public static int checkRow() {
    
    int value = 0;
    
    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 4; j++) {
        if (AIGrid[i][j] != 0 && AIGrid[i][j] == AIGrid[i][j + 1] && AIGrid[i][j] == AIGrid[i][j + 2]
              && AIGrid[i][j] == AIGrid[i][j + 3]) {
          
          value = value + 1000;
        }
        
        else if (AIGrid[i][j] != 0 && AIGrid[i][j] == AIGrid[i][j + 1] && AIGrid[i][j] == AIGrid[i][j + 2]) {
          value = value + 100;
        } else if (AIGrid[i][j] != 0 && AIGrid[i][j] == AIGrid[i][j + 1]) {
          value = value + 10;
        } else if (AIGrid[i][j] != 0) {
          value = value + 1;
        }
      }
    }
    return value;
  }
  //end checkRow method
  
  //Checks for #-in a row for that gamestate in a column
  public static int checkColumn() {
    int value = 0;
    
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 7; j++) {
        if (AIGrid[i][j] != 0 && i + 3 < 6 && i + 2 < 6 && i + 1 < 6 && AIGrid[i][j] == AIGrid[i + 1][j]
              && AIGrid[i][j] == AIGrid[i + 2][j] && AIGrid[i][j] == AIGrid[i + 3][j]) {
          value = value + 1000;
        } else if (AIGrid[i][j] != 0 && i + 2 < 6 && i + 1 < 6 && AIGrid[i][j] == AIGrid[i + 1][j]
                     && AIGrid[i][j] == AIGrid[i + 2][j]) {
          value = value + 100;
        } else if (AIGrid[i][j] != 0 && i + 2 < 6 && AIGrid[i][j] == AIGrid[i + 1][j]) {
          value = value + 10;
        } else if (AIGrid[i][j] != 0) {
          value = value + 1;
        }
      }
    }
    return value;
  }
  //end checkColumn method
  
  //Checks for #-in a row for that gamestate in a diagonal of positive slope
  public static int checkDiag1() {
    int value = 0;
    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 7; j++) {
        if (AIGrid[i][j] != 0 && i + 1 < 6 && j + 1 < 7 && i + 2 < 7 && j + 2 < 7 && i + 3 < 6 && j + 3 < 7
              && AIGrid[i][j] == AIGrid[i + 1][j + 1] && AIGrid[i][j] == AIGrid[i + 2][j + 2]
              && AIGrid[i][j] == AIGrid[i + 3][j + 3]) {
          value = value + 1000;
        } else if (AIGrid[i][j] != 0 && i + 1 < 6 && j + 1 < 7 && i + 2 < 6 && j + 2 < 7
                     && AIGrid[i][j] == AIGrid[i + 1][j + 1] && AIGrid[i][j] == AIGrid[i + 2][j + 2]) {
          value = value + 100;
        } else if (AIGrid[i][j] != 0 && i + 1 < 6 && j + 1 < 7 && AIGrid[i][j] == AIGrid[i + 1][j + 1]) {
          value = value + 10;
        } else if (AIGrid[i][j] != 0) {
          value = value + 1;
        }
      }
    }
    return value;
  }
  //end checkDiag1 method
  
  //Checks for #-in a row for that gamestate in a diagonal of negative slope
  public static int checkDiag2() {
    int value = 0;
    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 7; j++) {
        if (i > 0 && i < 6 && j > 0 && j < 7) {
          if (AIGrid[i][j] != 0 && i - 3 > 0 && j + 3 < 7 && i - 2 > 0 && j + 2 < 7 && i - 1 > 0 && j + 1 < 7
                && AIGrid[i][j] == AIGrid[i - 1][j + 1] && AIGrid[i][j] == AIGrid[i - 2][j + 2]
                && AIGrid[i][j] == AIGrid[i - 3][j + 3]) {
            value = value + 1000;
          } else if (AIGrid[i][j] != 0 && i - 2 > 0 && j + 2 < 7 && i - 1 < 0 && j + 1 < 7
                       && AIGrid[i][j] == AIGrid[i - 1][j + 1] && AIGrid[i][j] == AIGrid[i - 2][j + 2]) {
            value = value + 100;
          } else if (AIGrid[i][j] != 0 && i - 1 > 0 && j + 1 < 7 && AIGrid[i][j] == AIGrid[i - 1][j + 1]) {
            value = value + 10;
          } else if (AIGrid[i][j] != 0) {
            value = value + 1;
          }
        }
      }
    }
    return value;
  }
  //end checkDiag2 method
  
  //method to evaluate the board state
  public static int evaluate() {
    int value = 0;
    copyArray();
    value = value + checkRow() + checkColumn() + checkDiag1() + checkDiag2(); //value of state = how many in a rows are there when i play a certain move
    return value;
  } 
  //end of evaluate method
  
  /*
   * A method to determine the best move to be made using the minimax algorithm
   */
  public int minimax(int turnN, int depth) {
    int result = checkResult(); //checks the result of a move after its played
    if (result == 2)
      return Integer.MAX_VALUE; //if the board is a winning board then it returns the maximum value possible
    else if (result == 1)        
      return Integer.MIN_VALUE; //if the board is a losing board then it returns the lowest possible value.
    else if (result == 0)
      return 0;
    
    int max = Integer.MIN_VALUE; //sets the minimum value possible for the maximum return so any value that is returned will be bigger
    int min = Integer.MAX_VALUE; //sets the maximum value possible for the minimum return so any value that is returned will be smaller
    if (depth == maxDepth) {     //when depth hits the maximum, evaluate the board.
      return evaluate();
    } else {
      for (int i = 0; i < COLUMNS; i++) {
        
        if (!isLegalMove(i))  
          continue;
        if (turnN == 2) {
          putMove(i, 2);         //puts down a move for evaluation
          int stateValue = minimax(1, depth + 1);  //recursively calls the minimax method to return values to give the AI depth.
          int [] dontGiveUp = new int [7]; // array to store if the method returns all min 
          dontGiveUp[i] = stateValue;      // adds the statevalue into the spaces
          int count = 0;                 //sets count to 0
          for (int k=0;k<6;k++){
            if (dontGiveUp[k]== Integer.MIN_VALUE && dontGiveUp[k] == dontGiveUp[k+1]){
              count++;                    //adds to the counter
            }
          }
          
          max = getMax(stateValue, max);    //Picks the maximum value in between the two
          if (depth == 0) {      // when depth = 0, displays the value returned from the evaluation method in the system
            System.out.println("Count for Column  " + i + " = " + stateValue); 
            
            if (max == stateValue)    // when max  = the value of the state, then the move becomes the column with the maximum value
              move = i; 
            else if(count ==7){       // if every index is the minimum value for an integer, the method returns a different move that's calculated with one less depth.
              move = minimax(turnN,depth-1);
            }
          }
        } else if (turnN == 1) {
          putMove(i, 1);       //plays a move for the opponent
          int stateValue = minimax(2, depth + 1);  //evaluates the board in the system in the same way that it evaluates the maximum
          min = getMin(stateValue, min);          //gets the minimum value.
        }
        undoMove(i);        //takes away the move
      }
    }
    return turnN == 2 ? max : min;      //
  }
  //end minimax method
  
  public int returnMove(int turn) {       //returns the move to the computer to play the move in the game
    move = 0;
    minimax(turn, 0);
    return move;
  }
  //end returnMove method
  
  public int getMin(int a, int b) {
    return (a <= b) ? a : b;
  }
  //end getMin method
  
  public int getMax(int a, int b) {
    return (a >= b) ? a : b;
  } 
  //end getMax method
  
// --------------------------------------------- end evaluate methods ---------------------------------------------- // 
  
  /*
   * The main method to execute and run the GUI
   */
  public static void main(String args[]) throws Exception {
    
    //imports fonts from the internet
    URL fontUrl = new URL("http://www.webpagepublicity.com/free-fonts/b/Bumbazoid.ttf");
    URL fontUrl2 = new URL("http://www.webpagepublicity.com/free-fonts/d/DomCasual-Thin.ttf");
    
    Font font = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
    Font font2 = Font.createFont(Font.TRUETYPE_FONT, fontUrl2.openStream());
    
    font = font.deriveFont(Font.PLAIN, 30);
    font2 = font2.deriveFont(Font.PLAIN, 30);
    Font font1 = font.deriveFont(Font.PLAIN, 50);
    
    //registers the fonts in the GUI's environment
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    ge.registerFont(font);
    ge.registerFont(font2);
    
    //apply custom fonts to each component
    goesFirst.setFont(font);
    goesFirst2.setFont(font);
    title.setFont(font1);
    titleCopy.setFont(font1);
    turnLabel.setFont(font);
    turnLabel.setForeground(Color.darkGray);
    pvpMode.setFont(font2);
    aiMode.setFont(font2);
    aiFirst.setFont(font2);
    playerFirst.setFont(font2);
    redFirst.setFont(font2);
    yellowFirst.setFont(font2);
    resultLabel.setFont(font);
    playAgain.setFont(font2);
    playAgain.setBackground(Color.WHITE);
    
    //creates the play buttons
    for (int i = 0; i < 7; i++) {
      play[i] = new JButton();
    }
    
    //initializes the panels
    create();
    Connect4 connect4 = new Connect4(); //runs the GUI
    
  }
  //end main method
}
//end program