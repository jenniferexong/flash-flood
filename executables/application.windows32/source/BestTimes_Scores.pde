/** Screen to display best times achieved by players */
class BestTimes implements Screen {
  Button button;
  PImage background;
  Bug displayBug;

  /** Constructor */
  BestTimes() {
    displayBug = new Bug(width/2 - (Bug.IMG_WIDTH/2), height/2 + (Bug.IMG_HEIGHT/2));
    float yStart = height - 100;
    button = new Button("Back", width/2, yStart, BIG_BUTTON.x, BIG_BUTTON.y, false);
    background = easyBackground;
    background = background.get(0, 620, width, height);
  }

  void run() {
    image(background, 0, 0);
    // updating the animation of the stationary bug
    bugFlyBestTimes.update();
    bugFlyBestTimes.display(displayBug);
    // drawing "Scores" with an outlined effect
    textAlign(CENTER, TOP);
    textSize(90);
    fill(#FAF79F, 150);
    text("Best Times", width/2 + 2, 100 + 2);
    text("Best Times", width/2 - 2, 100 - 2);
    text("Best Times", width/2 + 2, 100 - 2);
    text("Best Times", width/2 - 2, 100 + 2);
    fill(0);
    text("Best Times", width/2, 100);
    button.display();
    scoresList.display();   // displaying all the scores
  }

  // Returns a new Menu Screen if the mouse was clicked on the 'back' button
  Screen onClick(float x, float y) {
    if (button.onClick(x, y).equals("Back"))  return(new Menu());
    return null;
  }

  Screen gameOver() { 
    return null;
  }

  Scoreboard getScore() { 
    return null;
  }
}

/** Used to store players' scores */
class Scores {
  final String FILENAME = "scores.txt";
  HashMap<String, int[]> scores = new HashMap<String, int[]>();         // Array of "easy" and "hard" scores
  HashMap<String, Integer> nextSpace = new HashMap<String, Integer>();  // Index of the next score to be added

  /**  Constructor - Reads scores from files and stores them in an array of Integers */
  Scores() {
    String[] easyLines = loadStrings("easy-" + FILENAME);
    String[] hardLines = loadStrings("hard-" + FILENAME);
    scores.put("easy", new int[easyLines.length + 20]);
    scores.put("hard", new int[hardLines.length + 20]);
    nextSpace.put("easy", easyLines.length);
    nextSpace.put("hard", hardLines.length);
    for (int i = 0; i < easyLines.length; i ++) {
      scores.get("easy")[i] = (Integer.valueOf(easyLines[i]));
      scores.put("easy", sort(scores.get("easy"), getLengthOfArray(scores.get("easy"))));
    }
    for (int i = 0; i < hardLines.length; i ++) {
      scores.get("hard")[i] = (Integer.valueOf(hardLines[i]));
      scores.put("hard", sort(scores.get("hard"), getLengthOfArray(scores.get("hard"))));
    }
  }

  /** Adds a score to the array, sorts it in increasing order, then saves the array of numbers to a file */
  void addScore(int score, boolean hard) { 
    if (!hard) {
      scores.get("easy")[nextSpace.get("easy")] = score;
      nextSpace.put("easy", nextSpace.get("easy") + 1);
      scores.put("easy", sort(scores.get("easy"), getLengthOfArray(scores.get("easy"))));
    } else if (hard) {
      scores.get("hard")[nextSpace.get("hard")] = score;
      nextSpace.put("hard", nextSpace.get("hard") + 1);
      scores.put("hard", sort(scores.get("hard"), getLengthOfArray(scores.get("hard"))));
    }
    saveToFile();
  }

  /** Displays the scores */
  void display() {
    float top = 120;
    float easyX = 200;
    float hardX = width - 190;
    textAlign(CENTER, TOP);

    // drawing titles
    fill(0, 0, 0, 150);
    textSize(40);
    text("Easy", easyX, top);
    text("Hard", hardX, top);

    // display all scores
    textSize(40);
    top = 220;
    for (int i = 0; i < getLengthOfArray(scores.get("easy")); i ++) {
      fill(0);
      text(scores.get("easy")[i], easyX + 2, top + 2);
      fill(#FFFF8E);
      text(scores.get("easy")[i], easyX, top);
      top += 25;
    }
    top = 220;
    for (int i = 0; i < getLengthOfArray(scores.get("hard")); i ++) {
      fill(0);
      text(scores.get("hard")[i], hardX + 2, top + 2);
      fill(#FFFF8E);
      text(scores.get("hard")[i], hardX, top);
      top += 25;
    }
  }

  /** Saves the array of scores to a file - one line for each number */
  void saveToFile() {
    int easyScoresLength = getLengthOfArray(scores.get("easy"));
    int hardScoresLength = getLengthOfArray(scores.get("hard"));
    String[] easyScores = new String[easyScoresLength];
    String[] hardScores = new String[hardScoresLength];
    for (int i = 0; i < easyScoresLength; i ++)  easyScores[i] = String.valueOf(scores.get("easy")[i]);
    for (int i = 0; i < hardScoresLength; i ++)  hardScores[i] = String.valueOf(scores.get("hard")[i]);
    saveStrings(dataPath("easy-scores.txt"), easyScores);
    saveStrings(dataPath("hard-scores.txt"), hardScores);
  }

  /** Helper method to get the number of array slots that are filled with a non-zero integer */
  int getLengthOfArray(int[] scores) {
    int count = 0;
    for (int i = 0; i < scores.length; i ++) 
      if (scores[i] != 0) count ++;
    return count;
  }
}
