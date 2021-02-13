/** Main menu of Flash Flood */
class Menu implements Screen {

  ArrayList<Button> buttons = new ArrayList<Button>();
  ArrayList<BasicLeaf> leaves = new ArrayList<BasicLeaf>();
  PImage background;

  /** Constructor */
  Menu() {
    float yStart = height/2 - 110;
    // Creating buttons
    buttons.add(new Button("Tutorial", width/2, yStart, BIG_BUTTON.x, BIG_BUTTON.y, false));
    buttons.add(new Button("Easy", width/2, yStart + 80, BIG_BUTTON.x, BIG_BUTTON.y, false));
    buttons.add(new Button("Hard", width/2, yStart + 160, BIG_BUTTON.x, BIG_BUTTON.y, false));
    buttons.add(new Button("Best Times", width/2, yStart + 280, BIG_BUTTON.x, BIG_BUTTON.y, false));
    buttons.add(new Button("Quit", width/2, yStart + 360, BIG_BUTTON.x, BIG_BUTTON.y, false));
    // Creating leaves to be displayed in the background
    for (int i = 0; i < 5; i ++)  leaves.add(new BasicLeaf(random(0, width), random(0, height), false));
    background = easyBackground;
    background = background.get(0, 620, width, height);
  }

  void run() {
    image(background, 0, 0);
    // Display title text 'Flash Flood'
    textAlign(CENTER, TOP);
    textSize(120);
    fill(0, 0, 0, 50);
    fill(255, 255, 255, 200);
    text("Flash Flood", width/2, 100);
    // Drawing leaves falling in the background
    for (BasicLeaf l : leaves) {
      l.loop(height);
      l.fall();
      l.display();
    }
    for (Button b : buttons)   b.display();
  }

  /** Checks if the mouse clicked on a button and returns a new Screen if it did */
  Screen onClick(float x, float y) {
    for (Button b : buttons) {
      if (b.onClick(x, y).equals("Tutorial"))          return(new Tutorial());
      else if (b.onClick(x, y).equals("Easy"))         return(new Game(false));
      else if (b.onClick(x, y).equals("Hard"))         return(new Game(true));
      else if (b.onClick(x, y).equals("Best Times"))   return(new BestTimes());
      else if (b.onClick(x, y).equals("Quit"))         exit();
    }
    return null;
  }

  Screen gameOver() { 
    return null;
  }

  Scoreboard getScore() { 
    return null;
  }
}

/** Screen displayed upon losing or winning the game */
class GameOver implements Screen {

  ArrayList<Button> buttons = new ArrayList<Button>();
  ArrayList<BreakLeaf> leaves = new ArrayList<BreakLeaf>();
  Scoreboard scoreboard;
  PImage background;
  float yStart = 200;

  GameOver(Scoreboard scoreboard) {
    this.scoreboard = scoreboard;
    // Creating buttons
    float buttonsStart = height/2 - 50;
    buttons.add(new Button("Menu", width/2, buttonsStart + 100, BIG_BUTTON.x, BIG_BUTTON.y, false));
    buttons.add(new Button("Quit", width/2, buttonsStart + 200, BIG_BUTTON.x, BIG_BUTTON.y, false));
    for (int i = 0; i < 5; i ++)   leaves.add(new BreakLeaf(random(0, width), random(0, height)));
    // Getting background
    background = hardBackground;
    background = background.get(0, 620, width, height);
  }

  void run() {
    image(background, 0, 0);
    // brown leaves falling in the background
    displayScore();
    for (BreakLeaf l : leaves) {
      l.loop(height);
      l.fall();
      l.display();
    }
    for (Button b : buttons)  b.display();
  }

  /** Displays the time taken for the player to win, if they won */
  void displayScore() {
    String message = null;
    if (scoreboard.success) {
      textAlign(CENTER, TOP);
      textSize(60);
      fill(#FAF79F);
      float timeY = yStart + 80;
      text("Your time: " + scoreboard.time, width/2, timeY);
      message = "You Won!";
    } else if (!scoreboard.success)  
      message = "You Lost!";
    textSize(100);
    // Drawing text with an outline effect
    fill(#FAF79F, 150);
    text(message, width/2 + 2, yStart + 2);
    text(message, width/2 - 2, yStart - 2);
    text(message, width/2 + 2, yStart - 2);
    text(message, width/2 - 2, yStart + 2);
    fill(0);
    text(message, width/2, yStart);
  }

  /** Check if a button was pressed and returns a new Screen if one was */
  Screen onClick(float x, float y) {
    for (Button b : buttons) {
      if (b.onClick(x, y).equals("Menu"))       return(new Menu());
      else if (b.onClick(x, y).equals("Quit"))  exit();
    }
    return null;
  }

  Scoreboard getScore() {
    return null;
  }

  Screen gameOver() {
    return null;
  }
}
