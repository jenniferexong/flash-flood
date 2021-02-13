interface Screen {
  void run();
  Scoreboard getScore();
  Screen onClick(float x, float y);
  Screen gameOver();
}

interface Surface {
  float left();
  float right();
  float surface();
  float bottom();
  boolean isEndPoint();
}

interface Mode extends Screen {
  void previousState();
  float distToEnd();
  float waterDistToEnd();
  boolean loseLife();
}

interface Leaf extends Surface {
  void translateXY(float x, float y);
  void fall();
  void display();
  void accelerate();
  void setSpeedZero();
  Leaf duplicate();
  int increaseTime();
  float left();
  float right();
  float surface();
  float bottom();
  boolean dead(float maxY);
  PVector velocity();
}
