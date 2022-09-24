// Need G4P library //<>// //<>// //<>//
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.awt.Image;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.awt.MouseInfo;
import java.awt.Point;
import javax.swing.UIManager;

File imgFile;
PImage bgImg;
PImage clock;
PImage hour;
PImage minute;
PImage second;
PImage logo;
boolean makeBorderColor = true;
int borderThres = 20;
int circleDiam;
int imgHeight;
int imgWidth;
float ratioThres = 1.3;
int lastFrameDragged = -10000;
boolean saved = true;
int pointX = 0;
int pointY = 0;
String lastSearchDirectory = "";


color backgroundCol;


//public void settings(){
//  size(180, 180, P3D);
//  logo = loadImage("logo.png");
//  if(logo != null){
//      PJOGL.setIcon("logo.png");
//  }
//}

public void setup() {
  size(180, 180, P3D);
  surface.setAlwaysOnTop(true);
  try {
  //Credit: https://stackoverflow.com/questions/2282211/windows-look-and-feel-for-jfilechooser, User: Luhar
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  } catch (Exception e){
    
  }
  String[] coords = loadStrings("coords.txt");
  if (coords != null && coords.length >= 2) {
    try {
      surface.setLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
    } 
    catch (Exception e) {
    }
  }
  String[] lastSearch = loadStrings("lastSearch.txt");
  if(lastSearch != null && lastSearch.length >= 1){
    lastSearchDirectory = lastSearch[0];
  }
  circleDiam = 150;
  imgWidth = circleDiam;
  //imgWidth = width;
  imgHeight = circleDiam;
  //imgHeight = height;

  bgImg = loadImage("background.jpg");
  formatImg();
  clock = loadImage("clockPiece.png");
  hour = loadImage("hourPiece.png");
  minute = loadImage("minutePiece.png");
  second = loadImage("secondPiece.png");
  backgroundCol = color(230);
  surface.setTitle("Clock");
  //surface.setUndecorated(true);
  //frame.setUndecorated(true);
  
  
}



public void draw() {
  background(backgroundCol);
  bgImg.loadPixels();
  loadPixels();

  float r = 0;
  float g = 0;
  float b = 0;
  float a = 0;
  int count = 0;

  int circleRadius = circleDiam / 2;

  for (int x = width/2 - circleRadius; x < width/2 + circleRadius; x++) {
    for (int y = height/2 - circleRadius; y < height/2 + circleRadius; y++) {
      int relX = x - width/2;
      int relY = y - height/2;
      int check = relX*relX + relY*relY;
      int radSqr = circleRadius*circleRadius;
      if (check <= radSqr) {
        //if(true){
        int programLoc = x + y * width;
        int imgX = imgWidth/2 + relX;
        int imgY = imgHeight/2 + relY;
        int imgLoc = imgX + imgY * imgWidth;
        color col = bgImg.pixels[imgLoc];
        pixels[programLoc] = col;
        if (makeBorderColor && check >= radSqr - borderThres && check <= radSqr + borderThres) {
          count++;
          r += red(col);
          g += green(col);
          b += blue(col);
          a += alpha(col);
        }
      }
    }
  }
  updatePixels();

  if (makeBorderColor) {
    r /= (float)count;
    g /= (float)count;
    b /= (float)count;
    a /= (float)count;

    backgroundCol = color(r, g, b, a);
    makeBorderColor = false;
  }

  String time = CalendarHelper.timeSpecFormat();
  float hourTime = Float.parseFloat(time.substring(0, time.indexOf(":")));
  float minuteTime = Float.parseFloat(time.substring(time.indexOf(":") + 1, time.lastIndexOf(":")));
  float secondTime = Float.parseFloat(time.substring(time.lastIndexOf(":") + 1));

  int origWidth = -width/2;
  int origHeight = -height/2;
  if (hourTime >= 12) {
    hourTime -= 12;
  }

  float minuteFrac = map(secondTime, 0.0, 60.0, 0.0, 1.0);
  minuteTime += minuteFrac;
  float hourFrac = map(minuteTime, 0.0, 60.0, 0.0, 1.0);
  hourTime += hourFrac;
  image(clock, 0, 0, width, height);
  translate(width/2, height/2);
  float radians = (hourTime * 30) / 360 * TWO_PI;
  pushMatrix();
  rotateZ(radians);
  image(hour, origWidth, origHeight, width, height);
  popMatrix();
  pushMatrix();
  radians = map(minuteTime, 0.0, 60.0, 0.0, TWO_PI);
  rotateZ(radians);
  image(minute, origWidth, origHeight, width, height);
  popMatrix();
  pushMatrix();
  radians = map(secondTime, 0.0, 60.0, 0.0, TWO_PI);
  rotateZ(radians);
  image(second, origWidth, origHeight, width, height);
  popMatrix();
  translate(origWidth, origHeight);

  if (!saved && frameCount - lastFrameDragged > frameRate/2) {
    saveStrings("coords.txt", new String[]{pointX + "", pointY + ""});
    saved = true;
  }
}

public void drawImg(PImage img) {
  loadPixels();
  img.loadPixels();
  for (int x = 0; x < img.width; x++) {
    for (int y = 0; y < img.height; y++) {
      int loc = x + y * img.width;
      color col = img.pixels[loc];
      if (alpha(col) > 0) {
        pixels[loc] = img.pixels[loc];
      }
    }
  }
  updatePixels();
}

private void formatImg() {
  bgImg.loadPixels();
  boolean widthSmaller = bgImg.width < bgImg.height;
  float ratio;
  if (widthSmaller) {
    ratio = (float)bgImg.height / (float)bgImg.width;
    if (ratio < ratioThres) {
      bgImg.resize(imgWidth, imgHeight);
    } else {
      bgImg.resize(imgWidth, (int)((float)imgWidth*ratio));
      //bgImg = crop(bgImg, imgHeight, false);
    }
  } else {
    ratio = (float)bgImg.width / (float)bgImg.height;
    if (ratio < ratioThres) {
      bgImg.resize(imgWidth, imgHeight);
    } else {
      bgImg.resize((int)((float)imgHeight*ratio), imgHeight);
      bgImg = crop(bgImg, imgWidth, true);
    }
  }
  //bgImg.resize(imgWidth, imgHeight);
  bgImg.updatePixels();
}

private PImage crop(PImage orig, int dest, boolean isWidth) {
  PImage img = orig.copy();
  img.loadPixels();
  if (isWidth) {
    if ((int)img.width == dest) {
      return img;
    }
    if (img.width < dest) {
      println("ERROR: IMG WIDTH LESS THAN DEST WIDTH");
      return img;
    }
    int diff = img.width - dest;
    int widthStart = 0;

    for (int i = 0; i < diff; i++) {
      if (i % 2 == 0) {
        widthStart++;
      }
    }
    img = img.get(widthStart, 0, dest, img.height);
  } else {
    if ((int)img.height == dest) {
      return img;
    }
    if (img.height < dest) {
      println("ERROR: IMG HEIGHT LESS THAN DEST HEIGHT");
      return img;
    }
    int diff = img.height - dest;
    int heightStart = 0;

    for (int i = 0; i < diff; i++) {
      if (i % 2 == 0) {
        heightStart++;
      }
    }
    img = img.get(0, heightStart, img.width, dest);
  }
  img.updatePixels();
  return img;
}

public void mouseDragged() {
  Point point = MouseInfo.getPointerInfo().getLocation();
  pointX = point.x;
  pointY = point.y;
  surface.setLocation(pointX, pointY);
  saved = false;
}

public void mouseClicked() {
  File file = getImgFile("Choose Background Image", lastSearchDirectory);
  if (file == null) {
    return;
  }
  String filePath = file.toString();
  bgImg = loadImage(filePath);
  formatImg();
  makeBorderColor = true;
  bgImg.save("data/background.jpg");
  saveStrings("lastSearch.txt", new String[]{filePath});
  lastSearchDirectory = filePath;
}
