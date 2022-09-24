import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Scanner; 
import javax.imageio.ImageIO; 
import java.awt.Image; 
import javax.swing.JFileChooser; 
import javax.swing.filechooser.FileFilter; 
import java.awt.MouseInfo; 
import java.awt.Point; 
import javax.swing.UIManager; 
import java.time.Instant; 
import java.time.ZoneOffset; 
import java.time.ZonedDateTime; 
import java.util.Date; 
import javax.swing.filechooser.FileNameExtensionFilter; 
import javax.swing.JOptionPane; 
import java.awt.Component; 
import java.awt.Desktop; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Main extends PApplet {

// Need G4P library //<>// //<>// //<>//









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
float ratioThres = 1.3f;
int lastFrameDragged = -10000;
boolean saved = true;
int pointX = 0;
int pointY = 0;
String lastSearchDirectory = "";


int backgroundCol;


//public void settings(){
//  
//  logo = loadImage("logo.png");
//  if(logo != null){
//      PJOGL.setIcon("logo.png");
//  }
//}

public void setup() {
  size(180, 180, P3D);
  surface.setAlwaysOnTop(true);
  try {
  //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
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
        int col = bgImg.pixels[imgLoc];
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

  float minuteFrac = map(secondTime, 0.0f, 60.0f, 0.0f, 1.0f);
  minuteTime += minuteFrac;
  float hourFrac = map(minuteTime, 0.0f, 60.0f, 0.0f, 1.0f);
  hourTime += hourFrac;
  image(clock, 0, 0, width, height);
  translate(width/2, height/2);
  float radians = (hourTime * 30) / 360 * TWO_PI;
  pushMatrix();
  rotateZ(radians);
  image(hour, origWidth, origHeight, width, height);
  popMatrix();
  pushMatrix();
  radians = map(minuteTime, 0.0f, 60.0f, 0.0f, TWO_PI);
  rotateZ(radians);
  image(minute, origWidth, origHeight, width, height);
  popMatrix();
  pushMatrix();
  radians = map(secondTime, 0.0f, 60.0f, 0.0f, TWO_PI);
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
      int col = img.pixels[loc];
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






public static class CalendarHelper {

  private static Instant instant = Instant.now();

  /**
   * Get the day of the week.
   * @return day of week as an integer.
   */
  public static int getDayOfTheWeek() {
    resetInstant();
    int weekday = instant.atZone(ZoneOffset.systemDefault()).getDayOfWeek().getValue();
    return weekday;
  }

  public static String timeSpecFormat() {
    resetInstant();
    String specFormat = "";
    ZonedDateTime zone = instant.atZone(ZoneOffset.systemDefault());
    specFormat += zone.getHour();
    specFormat += ":" + zone.getMinute();
    specFormat += ":" + zone.getSecond();
    return specFormat;
  }

  public static String dayOfYearFormat() {
    resetInstant();
    String format = "";
    ZonedDateTime zone = instant.atZone(ZoneOffset.systemDefault());
    format += zone.getMonthValue() + "/";
    format += zone.getDayOfMonth() + "/";
    format += zone.getYear();
    return format;
  }

  /**
   * Resets the instant object.
   */
  private static void resetInstant() {
    instant = Instant.now();
  }

  /**
   * Get the instant object.
   * @return instant object
   */
  public static Instant getInstant() {
    resetInstant();
    return instant;
  }

  public static int timeToNextSec() {
    long now = Date.from(getInstant()).getTime();
    long timeTill = now % 1000;
    return (int) timeTill;
  }
}





private static FileNameExtensionFilter imgFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());

public static File getImgFile(String title, String currentDirectoryPath) {
  File currentDirectory = null;
  if(currentDirectoryPath != null && currentDirectoryPath != ""){
    currentDirectory = new File(currentDirectoryPath);
  }
  return getFile(JFileChooser.FILES_ONLY, title, imgFilter, currentDirectory);
}

public static String getText(File file) {
  if (!file.isFile()) {
    return "";
  }
  String text = "";
  Scanner in = null;
  try {
    in = new Scanner(file);
    while (in.hasNextLine()) {
      text += in.nextLine();
      text += "\n";
    }
  } 
  catch (Exception ex) {
    return "";
  } 
  finally {
    if (in != null) {
      in.close();
    }
  }
  return text;
}

public static boolean isBlank(String str) {
  return strip(str).isEmpty();
}

public static String strip(String s) {
  return s.trim();
}

public static boolean isImg(File img) {
  try {
    Image image = ImageIO.read(img);
    return image != null;
  } 
  catch(Exception e) {
    return false;
  }
}

public static File getFile(int selectionMode, String title, FileFilter filter, File currentDirectory) {
  File file = null;
  JFileChooser chooser = new JFileChooser();
  if (filter != null) {
    chooser.setFileFilter(filter);
  }
  chooser.setFileSelectionMode(selectionMode);
  chooser.setDialogTitle(title);
  if(currentDirectory != null && currentDirectory.exists()){
      chooser.setCurrentDirectory(currentDirectory);
  }
  if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
    file = chooser.getSelectedFile();
  }
  return file;
}

public static void message(Component frame, Object message, String title) {
  JOptionPane.showMessageDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE);
}
  public void settings() {  size(180, 180, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
