/*
 *****************************************************************************
 *                       Confidentiality Information                         *
 *                                                                           *
 * This module is the confidential and proprietary information of            *
 * DBSentry Corp.; it is not to be copied, reproduced, or transmitted in any *
 * form, by any means, in whole or in part, nor is it to be used for any     *
 * purpose other than that for which it is expressly provided without the    *
 * written permission of DBSentry Corp.                                      *
 *                                                                           *
 * Copyright (c) 2004-2005 DBSentry Corp.  All Rights Reserved.              *
 *                                                                           *
 *****************************************************************************
 * $Id: GeneralUtil.java,v 1.14 2006/06/23 13:10:24 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui.utility;

import dbsentry.filesync.client.TotalInfoFoldersFiles;
import dbsentry.filesync.client.enumconstants.FilePermissions;
import dbsentry.filesync.clientgui.FsTableModel;
import dbsentry.filesync.clientgui.enumconstants.EnumLocalTable;
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.clientgui.utility.ColumnSorter;
import dbsentry.filesync.common.FsFolderHolder;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

/**
 *	This class provides general utility function.
 *  @author              Jeetendra Parasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Jeetendra Parasad
 * 	Last Modfied Date:   11-07-2005
 */
public class GeneralUtil {
  private FileSystemView fileSystemView = FileSystemView.getFileSystemView();

  /**
   * gets stack trace as string.
   * @param ex exception object
   * @return string of stack trace
   */
  public static String getStackTrace(Exception ex) {
    ByteArrayOutputStream baos = null;
    PrintWriter pw = null;
    String stackTrace = null;
    try {
      baos = new ByteArrayOutputStream();
      pw = new PrintWriter(baos);
      ex.printStackTrace(pw);
      pw.flush();
      stackTrace = baos.toString();
    } catch (Exception e) {
      ;
    } finally {
      try {
        if (pw != null) {
          pw.close();
        }
        if (baos != null) {
          baos.close();
        }
      } catch (IOException e) {
        ;
      }
      return stackTrace;
    }

  }


  /**
     * To get contentsize of document and others for display in defined format.
     * @param size size to be formatted.
     * @return formatted size.
     */
  public static String getDocSizeForDisplay(long size) {
    String pattern = "###,###,###.##";
    String output = null;
    double value = (((double)size) / 1024);

    NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
    DecimalFormat df = (DecimalFormat)nf;
    df.applyPattern(pattern);
    if (value > 1024) {
      value = (value / 1024);
      output = df.format(value) + " MB";
    } else {
      output = df.format(value) + " KB";
    }
    return output;
  }

  /**
   * gets transfer size for display.
   * @param size size as long
   * @return  size as formatted string
   */
  public static String getTransferSizeForDisplay(long size) {
    String output = null;

    NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
    DecimalFormat df = (DecimalFormat)nf;

    output = df.format(size);
    return output;

  }

  /**
     * To get date display in defined format.
     * @param date date to be formatted.
     * @return output String.
     */
  public static String getDateForDisplay(Date date) {
    DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
    return formatter.format(date);
  }

  // Regardless of sort order (ascending or descending), null values always appear last.
  // colIndex specifies a column in model.

  /**
   * sorts table base on a given column and given order.
   * @param table table to be sorted
   * @param colIndex column index
   * @param ascending order of sorting
   */
  public static void sortAllRowsBy(JTable table, int colIndex, boolean ascending) {
  
 
    FsTableModel model = (FsTableModel)table.getModel();
    Vector data = model.getDataVector();
    
    table.getColumnModel().getColumn(EnumLocalTable.NAME).setHeaderValue(null);
    table.getColumnModel().getColumn(EnumLocalTable.TYPE).setHeaderValue(null);
    table.getColumnModel().getColumn(EnumLocalTable.SIZE).setHeaderValue(null);
    table.getColumnModel().getColumn(EnumLocalTable.MODIFIED).setHeaderValue(null);
    
    if(colIndex==0||colIndex==2){
      ColumnSorter columnSorter = new ColumnSorter(colIndex, ascending);
      Collections.sort(data, columnSorter);
      if (ascending) {
        table.getColumnModel().getColumn(colIndex).setHeaderValue(FsImage.arrow_down);
      }else {
        table.getColumnModel().getColumn(colIndex).setHeaderValue(FsImage.arrow_up);
      }
    }else if(colIndex==8){
      ColumnSorterSize columnSorterSize = new ColumnSorterSize(colIndex, ascending);
      Collections.sort(data, columnSorterSize);
      if (ascending) {
        table.getColumnModel().getColumn(EnumLocalTable.SIZE).setHeaderValue(FsImage.arrow_down);
      }else {
        table.getColumnModel().getColumn(EnumLocalTable.SIZE).setHeaderValue(FsImage.arrow_up);
      }
    }else{
      ColumnSorterModified columnSorterModified = new ColumnSorterModified(colIndex, ascending);
      Collections.sort(data, columnSorterModified);
      if (ascending) {
        table.getColumnModel().getColumn(EnumLocalTable.MODIFIED).setHeaderValue(FsImage.arrow_down);
      }else {
        table.getColumnModel().getColumn(EnumLocalTable.MODIFIED).setHeaderValue(FsImage.arrow_up);
      }
    }
    model.fireTableDataChanged();
    
    

    // Set the text and icon values on the second column for the icon render
   
  }

  /**
   * finds a node in a tree corresponding to a given path.
   * @param parent the tree node
   * @param pathTobeMatch path to be matched
   * @return the found node. else null.
   */
  public static DefaultMutableTreeNode findTreeNode(DefaultMutableTreeNode parent, String pathTobeMatch) {
    Logger logger = Logger.getLogger("ClientLogger");
    DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode();
    FsFolderHolder fsFolderHolder = new FsFolderHolder();
    Enumeration childs;
    String currentNodePath;
    DefaultMutableTreeNode prevNode = null;
    int prevNodeLength = 0;
    logger.debug(" pathTobeMatch :" + pathTobeMatch);
    if (parent.getChildCount() > 0) {
      childs = parent.children();
      logger.debug("Current Parent" + parent);
      while (childs.hasMoreElements()) {
        currentNode = (DefaultMutableTreeNode)childs.nextElement();
        logger.debug("Current Child" + currentNode);
        fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
        currentNodePath = fsFolderHolder.getPath();
        logger.debug("Current Child Path" + currentNodePath);
        if (pathTobeMatch.indexOf(currentNodePath) == 0) {
          if (currentNode.getChildCount() > 0) {
            if (currentNode.getFirstChild().toString().equals("")) {
              if (prevNodeLength < currentNodePath.length()) {
                logger.debug("Current Node Length : " + currentNodePath.length());
                prevNode = currentNode;
                prevNodeLength = currentNodePath.length();
              }
            } else {
              if (pathTobeMatch.toString().equals(currentNodePath) ||
                  pathTobeMatch.toString().equals(currentNodePath + "/") ||
                  pathTobeMatch.toString().equals(currentNodePath + "\\")) {
                prevNode = currentNode;
                break;
              } else {
                return findTreeNode(currentNode, pathTobeMatch);
              }
            }
          } else {
            if (pathTobeMatch.equals(currentNodePath) || pathTobeMatch.toString().equals(currentNodePath + "/") ||
                pathTobeMatch.toString().equals(currentNodePath + "\\")) {
              logger.debug("Path Matched At Leaf ");
              prevNode = currentNode;
              break;
            } else {
              findTreeNode(currentNode, pathTobeMatch);
            }
          }
        } else {
          //go ahead matching the path ;
        }
      }
    } else {
      prevNode = null;
    }
    return prevNode;
  }

  /**
   * gets icon corrseponding to a file.
   * @param fileName file name
   * @return Icon object
   */
  public Icon getFileIcon(String fileName) {
    String fileExtension;
    File file;

    String userHome = System.getProperty("user.home");
    File noExtensionFile = new File(userHome + "/.dbsfs/mimetype/abc");

    Icon noExtensionFileIcon = fileSystemView.getSystemIcon(noExtensionFile);
    Icon fileIcon;

    int lastIndexOfDot = fileName.lastIndexOf(".");
    if (lastIndexOfDot == -1) {
      fileIcon = noExtensionFileIcon;
    } else {
      fileExtension = fileName.substring(lastIndexOfDot + 1);

      file = new File(userHome + "/.dbsfs/mimetype/" + fileExtension + "." + fileExtension);
      if (file.exists()) {
        fileIcon = fileSystemView.getSystemIcon(file);
      } else {
        try {
          file.createNewFile();
          fileIcon = fileSystemView.getSystemIcon(file);
        } catch (Exception ex) {
          fileIcon = noExtensionFileIcon;
          ex.printStackTrace();
        }
      }
    }
    return fileIcon;
  }

  /**
   * gets folder icon.
   * @return Icon object
   */
  public Icon getFolderIcon() {
    String userHome = System.getProperty("user.home");
    File folder = new File(userHome + "/.dbsfs/mimetype");
    Icon folderIcon = fileSystemView.getSystemIcon(folder);
    return folderIcon;
  }
  
  /**
   * To display the dialog box in the center of parent frame.
   * @param parentFrame parent frame of the frame which has to be centered.
   * @param formToCenter frame to be centered.
   */
  public void centerForm(JDialog parentFrame, JDialog formToCenter) {
    Dimension d = null;
    // size of what we're positioning against
    Point p = null;

    if (parentFrame != null) {
      // w is what we are positioning against, null means desktop
      d = parentFrame.getSize();
      p = parentFrame.getLocation();
    } else {
      d = Toolkit.getDefaultToolkit().getScreenSize();
      p = new Point();
    }

    double centreX = p.getX() + d.getWidth() / 2;
    double centreY = p.getY() + d.getHeight() / 2;

    formToCenter.getSize(d);
    p.setLocation(centreX - d.getWidth() / 2, centreY - d.getHeight() / 2);
    if (p.getX() < 0) {
      p.setLocation(0, p.getY());
    }
    if (p.getY() < 0) {
      p.setLocation(p.getX(), 0);
    }

    formToCenter.setLocation(p);
  }
  
  /**
   * To display the dialog box in the center of parent frame.
   * @param parentFrame parent frame of the frame which has to be centered.
   * @param formToCenter frame to be centered.
   */
  public void centerForm(JFrame parentFrame, JDialog formToCenter) {
    Dimension d = null;
    // size of what we're positioning against
    Point p = null;

    if (parentFrame != null) {
      // w is what we are positioning against, null means desktop
      d = parentFrame.getSize();
      p = parentFrame.getLocation();
    } else {
      d = Toolkit.getDefaultToolkit().getScreenSize();
      p = new Point();
    }

    double centreX = p.getX() + d.getWidth() / 2;
    double centreY = p.getY() + d.getHeight() / 2;

    formToCenter.getSize(d);
    p.setLocation(centreX - d.getWidth() / 2, centreY - d.getHeight() / 2);
    if (p.getX() < 0) {
      p.setLocation(0, p.getY());
    }
    if (p.getY() < 0) {
      p.setLocation(p.getX(), 0);
    }

    formToCenter.setLocation(p);
  }

  /**
   * displays the frame in the center of parent frame.
   * @param parentFrame parent frame of the frame to be centered.
   * @param frameToCenter frame to center.
   */
  public void centerForm(JFrame parentFrame, JFrame frameToCenter) {
    Dimension d = null;
    // size of what we're positioning against
    Point p = null;

    if (parentFrame != null) {
      // w is what we are positioning against, null means desktop
      d = parentFrame.getSize();
      p = parentFrame.getLocation();
    } else {
      d = Toolkit.getDefaultToolkit().getScreenSize();
      p = new Point();
    }

    double centreX = p.getX() + d.getWidth() / 2;
    double centreY = p.getY() + d.getHeight() / 2;

    frameToCenter.getSize(d);
    p.setLocation(centreX - d.getWidth() / 2, centreY - d.getHeight() / 2);
    if (p.getX() < 0) {
      p.setLocation(0, p.getY());
    }
    if (p.getY() < 0) {
      p.setLocation(p.getX(), 0);
    }

    frameToCenter.setLocation(p);
  }
  
  /**
   * displays the frame in the center of parent frame.
   * @param parentFrame parent frame of the frame to be centered.
   * @param frameToCenter frame to center.
   */
  public void centerForm(JFrame parentFrame, JInternalFrame frameToCenter) {
    Dimension d = null;
    // size of what we're positioning against
    Point p = null;

    if (parentFrame != null) {
      // w is what we are positioning against, null means desktop
      d = parentFrame.getSize();
      p = parentFrame.getLocation();
    } else {
      d = Toolkit.getDefaultToolkit().getScreenSize();
      p = new Point();
    }

    double centreX = p.getX() + d.getWidth() / 2;
    double centreY = p.getY() + d.getHeight() / 2;

    frameToCenter.getSize(d);
    p.setLocation(centreX - d.getWidth() / 2, centreY - d.getHeight() / 2);
    if (p.getX() < 0) {
      p.setLocation(0, p.getY());
    }
    if (p.getY() < 0) {
      p.setLocation(p.getX(), 0);
    }

    frameToCenter.setLocation(p);
  }
  /**
   * converts time (milliseconds) in  hh:mm:ss format.
   * @param time time in milisecond.
   * @return formatted time as string.
   */
  public String setTime(long time) {
    long hh, mm, ss;
    String timeTemp;

    long totalSeconds = time / 1000;
    long totalMinutes;

    String pattern = "00";
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
    DecimalFormat df = (DecimalFormat)nf;
    df.applyPattern(pattern);

    ss = totalSeconds % 60;
    totalMinutes = (totalSeconds - ss) / 60;
    mm = totalMinutes % 60;
    hh = (totalMinutes - mm) / 60;
    timeTemp = df.format(hh);
    timeTemp = timeTemp + ":" + df.format(mm);
    timeTemp = timeTemp + ":" + df.format(ss);
    ;
    return timeTemp;
  }
  
  /**
   * Finds total information about folder and file.
   * @param file file whose info has to be retrieved
   * @return total info of file
   * @throws java.io.IOException IOException if operation fails.
   */
  public TotalInfoFoldersFiles findTotalInfoFoldersFiles(File file) throws IOException {
    // change the ACL of the specified folder,
    // and set all of the items in the folder to that same ACL
    TotalInfoFoldersFiles total = new TotalInfoFoldersFiles();
    try {
      if (file.isFile()) {
        total.setSize(file.length());
        total.setFileCount(1);
        total.setFolderFileCount(1);
        if (file.canWrite()) {
          total.setReadOnly(new Integer(FilePermissions.WRITE));
        } else {
          total.setReadOnly(new Integer(FilePermissions.READ_ONLY));
        }
      } else {
        if (file.isDirectory()) {
          total.setFolderCount(1);
          total.setFolderFileCount(1);

          File[] itemsInTheFolder = file.listFiles();
          if (itemsInTheFolder != null) {
            for (int index = 0; index < itemsInTheFolder.length; index++) {
              TotalInfoFoldersFiles tempTotal = findTotalInfoFoldersFiles(itemsInTheFolder[index]);
              total.setSize(total.getSize() + tempTotal.getSize());
              total.setFileCount(total.getFileCount() + tempTotal.getFileCount());
              total.setFolderCount(total.getFolderCount() + tempTotal.getFolderCount());
              total.setFolderFileCount(total.getFolderFileCount() + tempTotal.getFolderFileCount());
              if (tempTotal.getReadOnly() == total.getReadOnly()) {
                total.setReadOnly(total.getReadOnly());
              } else if (total.getReadOnly() == null) {
                total.setReadOnly(tempTotal.getReadOnly());
              } else {
                total.setReadOnly(new Integer(FilePermissions.TRI_STATE));
              }
            }
          }
        }
      }
    } catch (IOException ioe) {
      throw ioe;
    }
    return total;
  }

  /**
   * Finds infoormatio about folder and file.
   * @param itemFiles array of files whose info has to be found.
   * @return folder file info
   * @throws java.io.IOException IOException if operation fails
   */
  public TotalInfoFoldersFiles findFoldersFilesInfo(File[] itemFiles) throws IOException {
    TotalInfoFoldersFiles total = new TotalInfoFoldersFiles();
    TotalInfoFoldersFiles tempTotal;
    try {
      if (itemFiles != null) {
        int itemCount = itemFiles.length;
        for (int index = 0; index < itemCount; index++) {
          tempTotal = findTotalInfoFoldersFiles(itemFiles[index]);
          total.setFileCount(total.getFileCount() + tempTotal.getFileCount());
          total.setFolderCount(total.getFolderCount() + tempTotal.getFolderCount());
          total.setFolderFileCount(total.getFolderFileCount() + tempTotal.getFolderFileCount());
          total.setSize(total.getSize() + tempTotal.getSize());
          if (tempTotal.getReadOnly() == total.getReadOnly()) {
            total.setReadOnly(total.getReadOnly());
          } else if (total.getReadOnly() == null) {
            total.setReadOnly(tempTotal.getReadOnly());
          } else {
            total.setReadOnly(new Integer(FilePermissions.TRI_STATE));
          }
        }
      }
    } catch (IOException ioe) {
      throw ioe;
    }
    return total;
  }
  
  
  
}
