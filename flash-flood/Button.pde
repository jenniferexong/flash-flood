/** Buttons to return Screen when pressed */
class Button {

  final color HOVER_COLOUR = color(#69C42D);
  final float RAD = 15; 

  float centreX, centreY, w, h;
  String text;
  color outline = color(15, 90, 17);
  boolean small = false;

  /** Constructor */
  Button(String text, float x, float y, float w, float h, boolean small) {
    centreX = x;
    centreY = y;
    this.text = text;
    this.w = w;
    this.h = h;
    this.small = small;
    if (small) outline = color(0);
  }

  /** Draws the button */
  void display() {
    if (small) {
      fill(255, 255, 255, 0);
      strokeWeight(2);
    } else {
      fill(255, 255, 255, 180);
      strokeWeight(5);
    }

    stroke(onHover() ? HOVER_COLOUR : outline);
    rectMode(CENTER);
    rect(centreX, centreY, w, h, RAD, RAD, RAD, RAD);
    textAlign(CENTER, TOP);
    fill(onHover() ? HOVER_COLOUR : outline);
    textSize(small? h - 8 : h - 10);
    text(text, centreX, centreY - (h/2) + 10);
  }

  /** Checks if mouse is over the button */
  boolean onHover() {
    return(on(mouseX, mouseY));
  }

  /** Checks if mouse has clicked on the button, returning the button's text on click */
  String onClick(float x, float y) {
    if (on(x, y)) return text;
    else return "null";
  }

  /** Checks if a position is on the button */
  boolean on(float x, float y) {
    float left = centreX - (w/2);
    float right = centreX + (w/2);
    float top = centreY - (h/2);
    float bot = centreY + (h/2);
    return(x >= left && x <= right && y >= top && y <= bot);
  }
}
