/** Creates an arraylist of Drops to give a raining effect to a Screen */
class Rain {
  final int DROP_COUNT = 200;

  ArrayList<RainDrop> drops = new ArrayList<RainDrop>();

  /** Constructor */
  Rain() {
    for (int i = 0; i < DROP_COUNT; i ++)  drops.add(new RainDrop());
  }

  /** Updates the position of the rain drops */
  void update(Water water) {
    for (int i = 0; i < drops.size(); i ++)  drops.get(i).update(water);
  }

  /** Draws all the rain drops */
  void display() {
    for (int i = 0; i < drops.size(); i ++)  drops.get(i).display();
  }

  /** Moves display position of each drop */
  void translateXY(float x, float y) {
    for (int i = 0; i < drops.size(); i ++)  drops.get(i).translateXY(x, y);
  }

  /** Removes all rain drops from the arraylist */
  void clearRain() {
    drops.clear();
  }
}

class RainDrop {
  final color COL = color(196, 230, 235, 40);  // Colour of a raindrop
  final float SIZE = 4;
  final float AY = 0.1;

  PVector pos;
  float vy = 0.5;
  float dropLength;

  /** Creates a raindrop with randomised position and size (length) */
  RainDrop() {
    pos = new PVector(random(0, width), random(-height, height));
    dropLength = random(8, 15);
  }

  /** Updates position of raindrop - resetting its height if it falls off the screen or hits the water surface */
  void update(Water water) {
    vy += AY;        // adding vertical acceleration
    pos.y += vy;
    if (pos.y + dropLength > water.top || pos.y + dropLength > height) {
      pos.y = random(-height, 0);
      vy = 1;
    }
    if (pos.x > width)  pos.x -= width;
    if (pos.x < 0)      pos.x += width;
  }

  void translateXY(float x, float y) {
    pos.set(pos.x + x, pos.y + y);
  }

  /** Draw the raindrop */
  void display() {
    strokeWeight(SIZE);
    stroke(COL);
    line(pos.x, pos.y, pos.x, pos.y + dropLength);
  }
}
