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
 * $Id: ClientUtil.java,v 1.52 2006/04/14 17:39:04 skumar Exp $
 *****************************************************************************
 */

package dbsentry.filesync.client;

import dbsentry.filesync.client.utility.FolderFileComparatorLocal;
import dbsentry.filesync.common.CommonUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Arrays;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
  *	To provide utilities at client side.
  * @author               Jeetendra Parasad
  * @version              1.0
  * Date of creation:     14-04-2005.
  * Last Modfied by :     Jeetendra Prasad.
  * Last Modfied Date:    01-07-2005.
  */
public class ClientUtil {
  private CommonUtil commonUtil;

  private Logger logger;

  /**
   * Constructs ClientUtil object.
   * @param logger an object of Logger
   */
  public ClientUtil(Logger logger) {
    this.logger = logger;
    commonUtil = new CommonUtil(logger);
  }

  /**
   *	To get string representation of stack trace of an exception.
   *  @param  ex exception object whose stack trace has to be converted to string
   *  @return stack trace as string
   */
  public String getStackTrace(Exception ex) {
    ByteArrayOutputStream baos = null;
    PrintWriter pw = null;
    String stackTrace = null;
    try {
      baos = new ByteArrayOutputStream();
      pw = new PrintWriter(baos);
      baos.flush();
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

//  /**
//   * converts the size in predefined format for display.
//   * @param size size to be formatted
//   * @return formatted size as string.
//   */
//  public String getDocSizeForDisplay(long size) {
//    String pattern = "###,###,###.##";
//    String output = null;
//    double value = (((double)size) / 1024);
//
//    NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
//    DecimalFormat df = (DecimalFormat)nf;
//    df.applyPattern(pattern);
//    if (value > 1024) {
//      value = (value / 1024);
//      output = df.format(value) + " MB";
//    } else {
//      output = df.format(value) + " KB";
//    }
//
//    //        output = df.format(value);
//    return output;
//  }

//  /**
//     * convert size in predefined format for displaying in upload download progress bar .
//     * @param size size to be formatted.
//     * @return formatted size a string.
//     */
//  public String getTransferSizeForDisplay(long size) {
//    String output = null;
//
//    NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
//    DecimalFormat df = (DecimalFormat)nf;
//
//    output = df.format(size);
//    return output;
//
//  }

//  /**
//     * To get date display in predefined format.
//     * @param date date to be formatted.
//     * @return formatted date as string.
//     */
//  public String getDateForDisplay(Date date) {
//    DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
//    return formatter.format(date);
//  }

//  /**
//   * To display the dialog box in the center of parent frame.
//   * @param parentFrame parent frame of the frame which has to be centered.
//   * @param formToCenter frame to be centered.
//   */
//  public void centerForm(JFrame parentFrame, JDialog formToCenter) {
//    Dimension d = null;
//    // size of what we're positioning against
//    Point p = null;
//
//    if (parentFrame != null) {
//      // w is what we are positioning against, null means desktop
//      d = parentFrame.getSize();
//      p = parentFrame.getLocation();
//    } else {
//      d = Toolkit.getDefaultToolkit().getScreenSize();
//      p = new Point();
//    }
//
//    double centreX = p.getX() + d.getWidth() / 2;
//    double centreY = p.getY() + d.getHeight() / 2;
//
//    formToCenter.getSize(d);
//    p.setLocation(centreX - d.getWidth() / 2, centreY - d.getHeight() / 2);
//    if (p.getX() < 0) {
//      p.setLocation(0, p.getY());
//    }
//    if (p.getY() < 0) {
//      p.setLocation(p.getX(), 0);
//    }
//
//    formToCenter.setLocation(p);
//  }
//
//  /**
//   * displays the frame in the center of parent frame.
//   * @param parentFrame parent frame of the frame to be centered.
//   * @param frameToCenter frame to center.
//   */
//  public void centerForm(JFrame parentFrame, JFrame frameToCenter) {
//    Dimension d = null;
//    // size of what we're positioning against
//    Point p = null;
//
//    if (parentFrame != null) {
//      // w is what we are positioning against, null means desktop
//      d = parentFrame.getSize();
//      p = parentFrame.getLocation();
//    } else {
//      d = Toolkit.getDefaultToolkit().getScreenSize();
//      p = new Point();
//    }
//
//    double centreX = p.getX() + d.getWidth() / 2;
//    double centreY = p.getY() + d.getHeight() / 2;
//
//    frameToCenter.getSize(d);
//    p.setLocation(centreX - d.getWidth() / 2, centreY - d.getHeight() / 2);
//    if (p.getX() < 0) {
//      p.setLocation(0, p.getY());
//    }
//    if (p.getY() < 0) {
//      p.setLocation(p.getX(), 0);
//    }
//
//    frameToCenter.setLocation(p);
//  }

  /**
  * calculates the total size of folders and files.
  * @param itemPaths array of paths of folders and files.
  * @return totalSize as long
  * @throws IOException IOException if operation fails.
  */
  public long calculateFolderDocSize(Object[] itemPaths) throws IOException {
    long totalSize = 0;
    try {
      if (itemPaths != null) {
        int itemCounnt = itemPaths.length;
        for (int index = 0; index < itemCounnt; index++) {

          totalSize += findTotalSizeFoldersDocs((String)itemPaths[index]);
        }
      }
    } catch (IOException ioe) {
      throw ioe;
    }
    return totalSize;
  }


  /**
   * calculates the total size of folder/file.
   * @param itemPath path of folder/file.
   * @return totalSize as long
   * @throws IOException IOException if operation fails.
   */
  public long findTotalSizeFoldersDocs(String itemPath) throws IOException {
    long totalSize = 0;
    File temp = new File(itemPath);
    try {
      if (temp.isFile()) {
        totalSize += temp.length();
      } else {
        if (temp.isDirectory()) {
          File[] itemsInTheFolder = temp.listFiles();
          if (itemsInTheFolder != null) {
            for (int index = 0; index < itemsInTheFolder.length; index++) {
              long tempTotalSize = findTotalSizeFoldersDocs(itemsInTheFolder[index].getAbsolutePath());
              totalSize += tempTotalSize;
            }
          }
        }
      }
    } catch (IOException ioe) {
      throw ioe;
    }
    return totalSize;
  }

//  /**
//   * converts time (milliseconds) in  hh:mm:ss format.
//   * @param time time in milisecond.
//   * @return formatted time as string.
//   */
//  public String setTime(long time) {
//    long hh, mm, ss;
//    String timeTemp;
//
//    long totalSeconds = time / 1000;
//    long totalMinutes;
//
//    String pattern = "00";
//    NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
//    DecimalFormat df = (DecimalFormat)nf;
//    df.applyPattern(pattern);
//
//    ss = totalSeconds % 60;
//    totalMinutes = (totalSeconds - ss) / 60;
//    mm = totalMinutes % 60;
//    hh = (totalMinutes - mm) / 60;
//    timeTemp = df.format(hh);
//    timeTemp = timeTemp + ":" + df.format(mm);
//    timeTemp = timeTemp + ":" + df.format(ss);
//    ;
//    return timeTemp;
//  }


//  /**
//   * Finda DefaultMutableTreeNode whose path matches the given path .
//   * @param parent parent node from where to start the search.
//   * @param pathTobeMatch path of node to be found
//   * @return node which matches the path and null if no matches are found.
//   */
//  public
//
//  DefaultMutableTreeNode findTreeNode(DefaultMutableTreeNode parent, String pathTobeMatch) {
//    Logger logger = Logger.getLogger("ClientLogger");
//    DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode();
//    FsFolderHolder fsFolderHolder = new FsFolderHolder();
//    Enumeration childs;
//    String currentNodePath;
//    DefaultMutableTreeNode prevNode = null;
//    int prevNodeLength = 0;
//    logger.debug(" pathTobeMatch :" + pathTobeMatch);
//    if (parent.getChildCount() > 0) {
//      childs = parent.children();
//      logger.info("Current Parent" + parent);
//      while (childs.hasMoreElements()) {
//        currentNode = (DefaultMutableTreeNode)childs.nextElement();
//        logger.info("Current Child" + currentNode);
//        fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
//        currentNodePath = fsFolderHolder.getPath();
//        logger.info("Current Child Path" + currentNodePath);
//        if (pathTobeMatch.indexOf(currentNodePath) == 0) {
//          if (currentNode.getChildCount() > 0) {
//            if (currentNode.getFirstChild().toString().equals("")) {
//              if (prevNodeLength < currentNodePath.length()) {
//                logger.debug("Current Node Length : " + currentNodePath.length());
//                prevNode = currentNode;
//                prevNodeLength = currentNodePath.length();
//              }
//            } else {
//              if (pathTobeMatch.toString().equals(currentNodePath)) {
//                prevNode = currentNode;
//                break;
//              } else {
//                return findTreeNode(currentNode, pathTobeMatch);
//              }
//            }
//          } else {
//            if (pathTobeMatch.equals(currentNodePath)) {
//              logger.info("Path Matched At Leaf ");
//              prevNode = currentNode;
//              break;
//            } else {
//              prevNode = null;
//              break;
//            }
//          }
//        } else {
//          //go ahead matching the path ;
//        }
//      }
//    } else {
//      prevNode = null;
//    }
//    return prevNode;
//  }


//  /**
//   * Finds total information about folder and file.
//   * @param file file whose info has to be retrieved
//   * @return total info of file
//   * @throws java.io.IOException IOException if operation fails.
//   */
//  public TotalInfoFoldersFiles findTotalInfoFoldersFiles(File file) throws IOException {
//    // change the ACL of the specified folder,
//    // and set all of the items in the folder to that same ACL
//    TotalInfoFoldersFiles total = new TotalInfoFoldersFiles();
//    try {
//      if (file.isFile()) {
//        total.setSize(file.length());
//        total.setFileCount(1);
//        total.setFolderFileCount(1);
//        if (file.canWrite()) {
//          total.setReadOnly(new Integer(FilePermissions.WRITE));
//        } else {
//          total.setReadOnly(new Integer(FilePermissions.READ_ONLY));
//        }
//      } else {
//        if (file.isDirectory()) {
//          total.setFolderCount(1);
//          total.setFolderFileCount(1);
//
//          File[] itemsInTheFolder = file.listFiles();
//          if (itemsInTheFolder != null) {
//            for (int index = 0; index < itemsInTheFolder.length; index++) {
//              TotalInfoFoldersFiles tempTotal = findTotalInfoFoldersFiles(itemsInTheFolder[index]);
//              total.setSize(total.getSize() + tempTotal.getSize());
//              total.setFileCount(total.getFileCount() + tempTotal.getFileCount());
//              total.setFolderCount(total.getFolderCount() + tempTotal.getFolderCount());
//              total.setFolderFileCount(total.getFolderFileCount() + tempTotal.getFolderFileCount());
//              if (tempTotal.getReadOnly() == total.getReadOnly()) {
//                total.setReadOnly(total.getReadOnly());
//              } else if (total.getReadOnly() == null) {
//                total.setReadOnly(tempTotal.getReadOnly());
//              } else {
//                total.setReadOnly(new Integer(FilePermissions.TRI_STATE));
//              }
//            }
//          }
//        }
//      }
//    } catch (IOException ioe) {
//      throw ioe;
//    }
//    return total;
//  }
//
//  /**
//   * Finds infoormatio about folder and file.
//   * @param itemFiles array of files whose info has to be found.
//   * @return folder file info
//   * @throws java.io.IOException IOException if operation fails
//   */
//  public TotalInfoFoldersFiles findFoldersFilesInfo(File[] itemFiles) throws IOException {
//    TotalInfoFoldersFiles total = new TotalInfoFoldersFiles();
//    TotalInfoFoldersFiles tempTotal;
//    try {
//      if (itemFiles != null) {
//        int itemCount = itemFiles.length;
//        for (int index = 0; index < itemCount; index++) {
//          tempTotal = findTotalInfoFoldersFiles(itemFiles[index]);
//          total.setFileCount(total.getFileCount() + tempTotal.getFileCount());
//          total.setFolderCount(total.getFolderCount() + tempTotal.getFolderCount());
//          total.setFolderFileCount(total.getFolderFileCount() + tempTotal.getFolderFileCount());
//          total.setSize(total.getSize() + tempTotal.getSize());
//          if (tempTotal.getReadOnly() == total.getReadOnly()) {
//            total.setReadOnly(total.getReadOnly());
//          } else if (total.getReadOnly() == null) {
//            total.setReadOnly(tempTotal.getReadOnly());
//          } else {
//            total.setReadOnly(new Integer(FilePermissions.TRI_STATE));
//          }
//        }
//      }
//    } catch (IOException ioe) {
//      throw ioe;
//    }
//    return total;
//  }

  /**
   * get document element corresponding to this file.
   * @param document document object to which the element will belong
   * @param file File whose dom element has to be constructed.
   * @return dom element corresponding to the file
   */
  public Element getDomElement(Document document, File file) {
    Element element = null;
    try {
      if (file.isFile()) {
        element = document.createElement("FILE");
        element.setAttribute("PATH", file.getPath());
        element.setAttribute("NAME", file.getName());
        element.setAttribute("SIZE", String.valueOf(file.length()));
        element.setAttribute("MD5SUM", commonUtil.generateMD5Sum(new FileInputStream(file)));
        element.setAttribute("MODIFIED_DATE", String.valueOf(file.lastModified()));
        element.setAttribute("ID", commonUtil.generateMD5Sum(file.getPath()));
      } else {
        element = document.createElement("FOLDER");
        element.setAttribute("PATH", file.getPath());
        element.setAttribute("NAME", file.getName());
        element.setAttribute("ID", commonUtil.generateMD5Sum(file.getPath()));
      }
    } catch (Exception ex) {
      logger.error(getStackTrace(ex));
    }
    return element;
  }

  /**
   * to construct a node tree of a given file object.
   * @param document document ot which this node will belong.
   * @param file file whose contented will be converted in to node tree.
   * @return element representing a node tree.
   */
  public Element constructNodeTree(Document document, File file) {
    Element element = null;
    try {
      if (file.isFile()) {
        element = document.createElement("FILE");
        element.setAttribute("PATH", file.getPath());
        element.setAttribute("NAME", file.getName());
        element.setAttribute("SIZE", String.valueOf(file.length()));
        element.setAttribute("MD5SUM", commonUtil.generateMD5Sum(new FileInputStream(file)));
        element.setAttribute("MODIFIED_DATE", String.valueOf(file.lastModified()));
        element.setAttribute("ID", commonUtil.generateMD5Sum(file.getPath()));
      } else {
        element = document.createElement("FOLDER");
        element.setAttribute("PATH", file.getPath());
        element.setAttribute("NAME", file.getName());
        element.setAttribute("ID", commonUtil.generateMD5Sum(file.getPath()));
        File files[] = file.listFiles();
        Arrays.sort(files, new FolderFileComparatorLocal());
        int filesLength = (int)file.length();
        for (int index = 0; index < filesLength; index++) {
          Element elementTemp = constructNodeTree(document, files[index]);
          element.appendChild(elementTemp);
        }
      }
    } catch (Exception ex) {
      logger.error(getStackTrace(ex));
    }
    return element;
  }

  /**
   * to construct an absolute path.
   * @param itemPath item path (relative or absolute)
   * @param baseFolderPath base folder path
   * @return absolute path
   */
  public String getAbsolutePath(String itemPath, String baseFolderPath) {
    String absolutePath;
    if (isRoot(baseFolderPath)) {
      absolutePath = baseFolderPath + itemPath;
    } else {
      absolutePath = baseFolderPath + File.separator + itemPath;
    }
    return absolutePath;
  }

  /**
   * To find if the given path is root.
   * @return true if the given path is root
   * @param path path of file or folder
   */
  private boolean isRoot(String path) {
    File files[] = File.listRoots();
    for (int index = 0; index < files.length; index++) {
      if (path.equals(files[index].getPath())) {
        return true;
      }
    }
    return false;
  }

  /**
   * To add file information to document object at an appropriate location.
   * @param document document in which we want to add this file
   * @param file file which has to be add to the document 
   * @param syncOperation sync operation like add update or delete, which will act as status of the file
   */
  public void addDocumentElement(Document document, File file, String syncOperation) {
    try {
      File parentFile = file.getParentFile();
      String md5Sum = commonUtil.generateMD5Sum(parentFile.getPath());
      Node root = document.getDocumentElement().getFirstChild();
      Node parentElement = commonUtil.getElementByID(md5Sum, root);
      if (parentElement == null) {
        //add document element corresponding to the fsObjectParent to the document
        Element element = getDomElement(document, parentFile);
        element.setAttribute("STATUS", syncOperation);
        parentElement = document.getDocumentElement();
        parentElement.appendChild(element);
        parentElement = element;
      }

      //add document element corresponding to the fsObject to the document
      Element element = getDomElement(document, file);
      parentElement.appendChild(element);

    } catch (Exception ex) {
      logger.error(getStackTrace(ex));
    }
  }

  /**
   * To construct a relative path from a list of folder names given in order.
   * @param folders array of folder name in the path hierarchy.
   * @return relative path
   */
  public String getRelativePath(Object[] folders) {
    String filePathRelative = "";
    if (folders != null) {
      int folderCount = folders.length;

      for (int index = 0; index < folderCount - 1; index++) {
        filePathRelative += folders[index] + File.separator;
      }
      filePathRelative += folders[folderCount - 1];
    }
    return filePathRelative;
  }

  /**
   * Returns a string representation of the ClientUtil object.
   * @return a string representation of the ClientUtil object.
   */
  public String toString() {
    return this.toString();
  }
}
