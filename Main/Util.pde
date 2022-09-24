import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Desktop;

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
