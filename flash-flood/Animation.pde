class Animation {
  PImage[] frames;
  HashMap<String, PImage[]> directionalFrames = new HashMap<String, PImage[]>();
  int imgCount;
  int frame;

  /** 
   *   Constructor
   *   - Loads all the images needed for an animation sequence and puts them into an array 
   */
  Animation(String folder, String imgName, int count, float w, float h, float scaleDown) {
    imgCount = count;
    frames = new PImage[imgCount];

    for (int i = 0; i < imgCount; i++) {
      String filename = imgName + (i + 1) + ".png";
      frames[i] = loadImage(folder + "/" + filename);
      frames[i].resize((int)(w/scaleDown), (int)(h/scaleDown));
    }
  }

  /** 
   *   Constructor
   *   - Loads all the images needed for an animation sequence and puts them into an array 
   *   - Used for flying animation: displays the same frame number no matter what direction the bug faces
   */
  Animation(String folder, String imgNameR, String imgNameL, int count, float w, float h, float scaleDown) {
    imgCount = count;
    directionalFrames.put("right", new PImage[imgCount]);

    for (int i = 0; i < imgCount; i++) {
      String filename = imgNameR + (i+1) + ".png";
      directionalFrames.get("right")[i] = loadImage(folder + "/" + filename);
      directionalFrames.get("right")[i].resize((int)(w/scaleDown), (int)(h/scaleDown));
    }

    directionalFrames.put("left", new PImage[imgCount]);
    for (int i = 0; i < imgCount; i++) {
      String filename = imgNameL + (i+1) + ".png";
      directionalFrames.get("left")[i] = loadImage(folder + "/" + filename);
      directionalFrames.get("left")[i].resize((int)(w/scaleDown), (int)(h/scaleDown));
    }
  }

  /** Displays a frame of the animation based on the bug's position */
  void display(Bug bug) {
    if (frames != null)  image(frames[frame], bug.pos.x, bug.pos.y - bug.bugH);
    else                 image(directionalFrames.get(bug.direction)[frame], bug.pos.x, bug.pos.y - bug.bugH);
  }

  /** Displays a frame of the animation based on the lotus' position */
  void display(Lotus lotus) {
    image(frames[frame], lotus.pos.x, lotus.pos.y - lotus.IMG_BOT);
  }

  /** Increments the frame number of the animation */
  void update() { 
    frame = (frame+1) % imgCount;
  }

  /** Resets animation to the beginning of the sequence */
  void resetFrame() {
    frame = 0;
  }
}
