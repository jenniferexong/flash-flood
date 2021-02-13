/**
 *  Creates a Water object
 *  Properties:
 *  - Water level rises over time
 *  - If the bug hits the surface, it will die
 *  - Lotuses and the end flower float on the surface of the water
 */
class Water {
  final color COLOUR = color(#369EB2, 50);

  float top, depth, waterWidth;
  float savedTop, savedDepth;
  float riseRate = 0.5;

  /** Constructor */
  Water(float t, float w, float d) {
    depth = d;
    savedDepth = depth;
    waterWidth = w;
    top = t;
    savedTop = top;
  }

  void display() {
    // Drawing rectangle to represent the water
    noStroke();
    fill(COLOUR);
    rectMode(CORNER);
    rect(0, top, waterWidth, depth);
    // drawing white line showing the surface of the water
    strokeWeight(3);
    stroke(255, 255, 255, 100);
    line(0, top, waterWidth, top);
  }

  /** Saves the current state of the water (depth and position of surface of water) */
  void saveState() {
    savedTop = top;
    savedDepth = depth;
  }

  /** Returns water to its saved state */
  void previousState() {
    top = savedTop;
    depth = savedDepth;
  }

  /** Moves the top of the water upward to give the effect of rising water */
  void rise() {
    top -= riseRate;
  }

  /** Adjusts the display position of the water */
  void translateY(float y) {
    top += y;
  }
}
