/**
 *  Creates a background object that moves:
 *  - Base image is a horizontally looping image, so when it is translated and redrawn, it has the effect of an infinite background.
 */
class Background {
  color baseColour;                   // Colour of the sky
  PImage background;                  // Horizontally looping image
  PVector bgPos1, bgPos2;             // Current position of top-left of section of background displayed
  PVector savedBgPos1, savedBgPos2;   // Position of background last time it was saved

  /** Constructor */
  Background(String difficulty, color baseCol) {
    if (difficulty.equals("easy"))  background = easyBackground; 
    if (difficulty.equals("hard"))  background = hardBackground;
    background.resize(1000, 0);
    // Split background vertically into two halves
    bgPos1 = new PVector(0, 3450 - height); 
    bgPos2 = new PVector(width/2, 3450 - height);
    savedBgPos1 = new PVector(0, 3450 - height); 
    savedBgPos2 = new PVector(width/2, 3450 - height);
    baseColour = baseCol;
  }

  /** 
   *  Moves the position of the background 
   *  - If one half is off the screen, set its position to be on the other side to give a looping effect
   */
  void translateXY(float x, float y) {
    bgPos1.set(bgPos1.x + x, bgPos1.y + y);
    bgPos2.set(bgPos2.x + x, bgPos2.y + y);
    if (bgPos1.x < -(width/2))   bgPos1.set(bgPos1.x + width, bgPos1.y);
    if (bgPos1.x > width)        bgPos1.set(bgPos1.x - width, bgPos1.y);
    if (bgPos2.x < -(width/2))   bgPos2.set(bgPos2.x + width, bgPos2.y);
    if (bgPos2.x > width)        bgPos2.set(bgPos2.x - width, bgPos2.y);
  }

  /** 
   *  Background is displayed in three parts:
   *   - bgSection3 is the section of the image that is cut off one side 
   *   - Gives infinite background effect
   */
  void display() {
    background(baseColour);
    PImage bgSection1 = background.get(0, (int)bgPos1.y, width/2, height);
    PImage bgSection2 = background.get(width/2, (int)bgPos2.y, width/2, height);
    PImage bgSection3 = null;
    float bgSection3x = 0;
    if (bgPos1.x < 0) {
      bgSection3 = bgSection1.get(0, 0, (int)-bgPos1.x, height);
      bgSection3x = bgPos2.x + (width/2);
    } else if (bgPos2.x < 0) {
      bgSection3 = bgSection2.get(0, 0, (int)-bgPos2.x, height);
      bgSection3x = bgPos1.x + (width/2);
    } else if (bgPos1.x > width/2) {
      bgSection3 = bgSection1.get((int)((width/2) - (bgPos1.x - width/2)), 0, (int)(bgPos1.x - width/2), height);
      bgSection3x = 0;
    } else if (bgPos2.x > width/2) {
      bgSection3 = bgSection2.get((int)((width/2) - (bgPos2.x - width/2)), 0, (int)(bgPos2.x - width/2), height);
      bgSection3x = 0;
    }

    image(bgSection1, bgPos1.x, 0);
    image(bgSection2, bgPos2.x, 0);
    if (bgSection3 != null) image(bgSection3, bgSection3x, 0);
  }

  /** Saves current position of background */
  void saveState() {
    savedBgPos1 = new PVector(bgPos1.x, bgPos1.y);
    savedBgPos2 = new PVector(bgPos2.x, bgPos2.y);
  }

  /** Sets position back to when it was last saved */
  void previousState() {
    bgPos1.set(savedBgPos1.x, savedBgPos1.y);
    bgPos2.set(savedBgPos2.x, savedBgPos2.y);
  }
}
