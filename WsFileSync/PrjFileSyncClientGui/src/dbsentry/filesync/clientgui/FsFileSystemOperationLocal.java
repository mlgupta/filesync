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
 * $Id: FsFileSystemOperationLocal.java,v 1.53 2006/04/14 17:38:57 skumar Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.ClientUtil;
import dbsentry.filesync.client.TotalInfoFoldersFiles;
import dbsentry.filesync.client.enumconstants.EnumClipBoardOperation;
import dbsentry.filesync.client.utility.FolderFileComparatorLocal;
import dbsentry.filesync.clientgui.constants.FsLocalOperationConstants;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsFolderHolder;
import dbsentry.filesync.common.FsMessage;
import dbsentry.filesync.common.constants.FsOverWriteConstants;
import dbsentry.filesync.common.enumconstants.EnumSyncOperation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.channels.FileChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;


/**
 *	To handle local filesystem related operations such as copy,move,delete.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    Jeetendra Prasad 
 * 	Last Modfied Date:   08-07-2005
 */
public class FsFileSystemOperationLocal implements PropertyChangeListener {

  private Progress copyMoveProgress;

  private FileSyncClientMDI parentFrame;

  private Logger logger;

  private PropertyChangeSupport propertyChangeSupport;

  private GeneralUtil generalUtil;
  
  private ClientUtil clientUtil;

  private CommonUtil commonUtil;

  private Stack deleteFolders = new Stack();
  
  

  /**
   * To initialize a JFrame object to parent frame.
   * @param  parentFrame - a parent JFrame  object
   */
  public FsFileSystemOperationLocal(FileSyncClientMDI parentFrame) {
    this.logger = Logger.getLogger("ClientLogger");
    this.generalUtil = new GeneralUtil();
    this.clientUtil=new ClientUtil(logger);
    this.commonUtil = new CommonUtil(logger);
    this.parentFrame = parentFrame;
  }


  /**
   * To add a listener which will listen when a propertyChange event is fired.
   * @param propertyChangeListener PropertyChangeListener object
   */
  public void addPropertyChangeSupport(PropertyChangeListener propertyChangeListener) {
    propertyChangeSupport = new PropertyChangeSupport(this);
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }

  /**
   * To handle a PropertyChange event.
   * @param evt PropertyChangeEvent object
   */
  public void propertyChange(PropertyChangeEvent evt) {
    propertyChangeSupport.firePropertyChange(evt.getPropertyName(), null, evt.getNewValue());
  }

  /**
   * To do necessary intializations to perform the paste operation and starting 
   * a thread to handle it.
   * @param clipBoard a Stack variable holding filenames to be pasted.
   * @param destFolderPath destination folderpath
   * @param clipBoardOperation holds either copy or move.
   */
  public void pasteItem(Stack clipBoard, String destFolderPath, int clipBoardOperation) {

    final int finalClipBoardOperation = clipBoardOperation;
    final Stack finalClipBoard = clipBoard;
    final String finalDestFolderPath = destFolderPath;
    final String finalSrcFolderPath = new File((String)clipBoard.firstElement()).getParentFile().getPath();
    Thread fsFileSystemOperationLocalThread = new Thread(new Runnable() {
                                                           public void run() {
                                                             //if(fileSystemCommand == EnumFileSystemCommands.PASTE){
                                                             paste(finalClipBoard, finalDestFolderPath,
                                                                   finalClipBoardOperation);
                                                             ArrayList localPasteOperationComplete = new ArrayList();
                                                             localPasteOperationComplete
                                                             .add(new Integer(finalClipBoardOperation));
                                                             localPasteOperationComplete.add(finalSrcFolderPath);
                                                             localPasteOperationComplete.add(finalDestFolderPath);
                                                             propertyChangeSupport
                                                             .firePropertyChange("localPasteOperationComplete", null,
                                                                                                      localPasteOperationComplete);
                                                             //}
                                                           }
                                                         }
      );
    fsFileSystemOperationLocalThread.start();
  }


  private void paste(Stack clipBoard, String destFolderPath, int clipBoardOperation) {
    Object itemPaths[] = clipBoard.toArray();
    String itemPath;
    String srcBasePath;
    File itemFile;
    long totalSizeCopyMove;

    srcBasePath = new File((String)clipBoard.firstElement()).getParent();
    logger.debug("srcBasePath : " + srcBasePath);
    logger.debug("destFolderPath : " + destFolderPath);

    if (srcBasePath.equals(destFolderPath)) {
      logger.debug("Source and destination are same");
      return;
    }
    try {
      totalSizeCopyMove = FsFileSystemOperationLocal.calculateFolderDocSize(itemPaths);
      logger.debug("totalSizeCopyMove : " + totalSizeCopyMove);

      Logger progressLogger = Logger.getLogger("ClientLogger");
      copyMoveProgress = new Progress(parentFrame, "Progress", false, progressLogger,null, FsLocalOperationConstants.MOVE); //TODO saurabh handle this..
      copyMoveProgress.setTotalData(totalSizeCopyMove);
      copyMoveProgress.setVisible(true);

      logger.debug("Current ClipBoard Operation : " + clipBoardOperation);
      Stack tempStack = new Stack();
      Iterator iterator = clipBoard.iterator();
      while (iterator.hasNext()) {
        tempStack.push(iterator.next());
      }
      if (clipBoardOperation == EnumClipBoardOperation.CUT) {
        copyMoveProgress.setOperation("Moving file(s)...");
        logger.debug("Current ClipBoard Operation : " + clipBoardOperation);

        moveItem(tempStack, destFolderPath);
        while (!clipBoard.isEmpty()) {
          itemPath = (String)clipBoard.pop();
          itemFile = new File(itemPath);
          if (itemFile.exists()) {
            deleteItem(itemFile);
          }
        }
      } else {
        copyMoveProgress.setOperation("Copying file(s)...");
        copyItem(tempStack, destFolderPath);
      }
    } catch (Exception ex) {
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
    }
  }

  /**
   * to handle possiblities occuring in copy operation such as checking 
   * for existence of file and folder.
   * @param itemStack - a Stack variable holding filenames to be copied.
   * @param destBaseFilePath - destination base folderpath for copy.
   */
  private void copyItem(Stack itemStack, String destBaseFilePath) {

    File srcBaseFile = null;
    String srcBaseFilePath = null;

    File currSrcFile = null;
    String currSrcFilePath = null;

    File currDestFile = null;
    String currDestFilePath = null;

    int folderOverWriteValue = 0;
    int fileOverwriteValue = 0;

    File destBaseFile = new File(destBaseFilePath);

    File tempFile;
    File tempFiles[];

    if (!itemStack.isEmpty()) {
      tempFile = new File((String)itemStack.firstElement());
      srcBaseFile = tempFile.getParentFile();
      srcBaseFilePath = tempFile.getParent();
    }

    try {
      while (!itemStack.isEmpty()) {
        currSrcFilePath = (String)itemStack.pop();
        currSrcFile = new File(currSrcFilePath);
        logger.debug("currSrcFilePath : " + currSrcFilePath);

        if ((isRoot(destBaseFile) && isRoot(srcBaseFile)) || (!isRoot(destBaseFile) && !isRoot(srcBaseFile))) {
          currDestFilePath = destBaseFilePath + currSrcFilePath.substring(srcBaseFilePath.length());
        } else if (isRoot(srcBaseFile) && !isRoot(destBaseFile)) {
          currDestFilePath = destBaseFilePath + File.separator + currSrcFilePath.substring(srcBaseFilePath.length());
        } else {
          //if(!isRoot(srcBaseFile) && isRoot(destBaseFile)){
          currDestFilePath = destBaseFilePath + currSrcFilePath.substring(srcBaseFilePath.length());
        }
        currDestFile = new File(currDestFilePath);

        if (currSrcFile.isDirectory()) {
          //push the content of the current folder in stack
          tempFiles = currSrcFile.listFiles();
          for (int index = 0; index < tempFiles.length; index++) {
            itemStack.push(tempFiles[index].getPath());
          }
          //check if this current folder exists in the current destination folder

          if (currDestFile.exists()) {
            logger.debug("tempFiles.length : " + tempFiles.length);
            if (folderOverWriteValue == 0 || folderOverWriteValue == FsOverWriteConstants.OVERWRITE_YES) {
              OverwriteOptionDialog folderOverWriteOptions =
                new OverwriteOptionDialog(parentFrame, "Confirm Folder Replace", true);
              folderOverWriteOptions.setTaOverwriteMessage("This folder already contains a folder named '" + currSrcFile.getName() + "'");
              folderOverWriteOptions.setVisible(true);
              folderOverWriteValue = folderOverWriteOptions.getOverWriteValue();
              if (folderOverWriteValue == FsOverWriteConstants.OVERWRITE_CANCEL) {
                copyMoveProgress.dispose();
                return;
              }
            } else if (folderOverWriteValue == FsOverWriteConstants.OVERWRITE_YES_TO_ALL) {
              fileOverwriteValue = FsOverWriteConstants.OVERWRITE_YES_TO_ALL;
            } else {
              //do nothing
            }

          } else {
            //create folder in the destination location
            //find the path of the destination file
            currDestFile.mkdir();
          }
        } else {

          //then check for its existance
          if (currDestFile.exists()) {
            if (folderOverWriteValue == FsOverWriteConstants.OVERWRITE_YES ||
                folderOverWriteValue == FsOverWriteConstants.OVERWRITE_YES_TO_ALL) {
              //check if file is read only
              if (!currDestFile.canWrite()) {
                //ask for overwriting readonly file
                if (fileOverwriteValue == 0 || fileOverwriteValue == FsOverWriteConstants.OVERWRITE_YES) {
                  OverwriteOptionDialog fileOverwriteOption =
                    new OverwriteOptionDialog(parentFrame, "Confirm File Replace", true);
                  fileOverwriteOption
                  .setTaOverwriteMessage("This folder already contains a file named '" + currDestFile.getName() + "'");
                  fileOverwriteOption.setLblExistingFileSize(currDestFile.length() + "KB.");
                  fileOverwriteOption.setLblReplaceFileSize(currSrcFile.length() + "KB.");
                  fileOverwriteOption
                  .setLblExistingFileModifiedDate("Modified : " + new Date(currDestFile.lastModified()) + "");
                  fileOverwriteOption
                  .setLblReplaceFileModifiedDate("Modified : " + new Date(currSrcFile.lastModified()) + "");
                  fileOverwriteOption.setVisible(true);
                  fileOverwriteValue = fileOverwriteOption.getOverWriteValue();
                  if (fileOverwriteValue == FsOverWriteConstants.OVERWRITE_CANCEL) {
                    copyMoveProgress.dispose();
                    return;
                  }
                } else if (fileOverwriteValue == FsOverWriteConstants.OVERWRITE_YES_TO_ALL) {
                  currDestFile.delete();
                  copyMoveProgress.setFilePath(currSrcFile.getPath());
                  copyFile(currSrcFile, currDestFile);
                } else {
                  //do nothing
                }
              } else {
                currDestFile.delete();
                copyMoveProgress.setFilePath(currSrcFile.getPath());
                copyMoveProgress.setPrevByteRead((int)currSrcFile.length());
                copyFile(currSrcFile, currDestFile);
              }
            } else if (folderOverWriteValue == 0) {
              if (fileOverwriteValue != FsOverWriteConstants.OVERWRITE_YES_TO_ALL) {
                OverwriteOptionDialog fileOverwriteOption =
                  new OverwriteOptionDialog(parentFrame, "Confirm File Replace", true);
                fileOverwriteOption
                .setTaOverwriteMessage("This folder already contains a file named '" + currDestFile.getName() + "'");
                fileOverwriteOption.setLblExistingFileSize(currDestFile.length() + "KB.");
                fileOverwriteOption.setLblReplaceFileSize(currSrcFile.length() + "KB.");
                fileOverwriteOption
                .setLblExistingFileModifiedDate("Modified : " + new Date(currDestFile.lastModified()) + "");
                fileOverwriteOption
                .setLblReplaceFileModifiedDate("Modified : " + new Date(currSrcFile.lastModified()) + "");
                fileOverwriteOption.setVisible(true);
                fileOverwriteValue = fileOverwriteOption.getOverWriteValue();
                if (fileOverwriteValue == FsOverWriteConstants.OVERWRITE_CANCEL) {
                  copyMoveProgress.dispose();
                  return;
                } else if (fileOverwriteValue == FsOverWriteConstants.OVERWRITE_NO) {
                  continue;
                }
              }
              currDestFile.delete();
              copyMoveProgress.setFilePath(currSrcFile.getPath());
              copyMoveProgress.setPrevByteRead((int)currSrcFile.length());
              copyFile(currSrcFile, currDestFile);
            }
          } else {
            //if the file does not exist then just copy
            copyMoveProgress.setFilePath(currSrcFile.getPath());
            copyMoveProgress.setPrevByteRead((int)currSrcFile.length());
            copyFile(currSrcFile, currDestFile);
          }
        }
      }
      copyMoveProgress.dispose();
      logger.debug("Copy Operation Completed Successfully");
    } catch (Exception ex) {
      copyMoveProgress.dispose();
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
    }
  }

  /**
  * Moves specified items from source to destination folder and handles
  * various possiblities occuring in move operation such as checking for existence of file/folder.
  * @param itemStack a Stack variable holding filenames to be moved.
  * @param destBaseFilePath destination base folderpath for move.
  */
  private void moveItem(Stack itemStack, String destBaseFilePath) {
    File srcBaseFile = null;
    String srcBaseFilePath = null;


    File currSrcFile = null;
    String currSrcFilePath = null;

    File currDestFile = null;
    String currDestFilePath = null;

    int folderOverWriteValue = 0;
    int fileOverwriteValue = 0;

    File destBaseFile = new File(destBaseFilePath);

    File tempFile;
    File tempFiles[];


    if (!itemStack.isEmpty()) {
      tempFile = new File((String)itemStack.firstElement());
      srcBaseFile = tempFile.getParentFile();
      srcBaseFilePath = tempFile.getParent();
    }

    try {
      while (!itemStack.isEmpty()) {

        currSrcFilePath = (String)itemStack.pop();
        currSrcFile = new File(currSrcFilePath);
        logger.debug("currSrcFilePath : " + currSrcFilePath);

        if ((isRoot(destBaseFile) && isRoot(srcBaseFile)) || (!isRoot(destBaseFile) && !isRoot(srcBaseFile))) {
          currDestFilePath = destBaseFilePath + currSrcFilePath.substring(srcBaseFilePath.length());
        } else if (isRoot(srcBaseFile) && !isRoot(destBaseFile)) {
          currDestFilePath = destBaseFilePath + File.separator + currSrcFilePath.substring(srcBaseFilePath.length());
        } else {
          //if(!isRoot(srcBaseFile) && isRoot(destBaseFile)){
          currDestFilePath = destBaseFilePath + currSrcFilePath.substring(srcBaseFilePath.length());
        }
        currDestFile = new File(currDestFilePath);

        if (currSrcFile.isDirectory()) {
          //push the content of the current folder in stack
          tempFiles = currSrcFile.listFiles();
          for (int index = 0; index < tempFiles.length; index++) {

            itemStack.push(tempFiles[index].getPath());
          }
          //check if this current folder exists in the current destination folder

          if (currDestFile.exists()) {
            if (folderOverWriteValue == 0 || folderOverWriteValue == FsOverWriteConstants.OVERWRITE_YES) {
              OverwriteOptionDialog folderOverWriteOptions =
                new OverwriteOptionDialog(parentFrame, "Confirm Folder Replace", true);
              folderOverWriteOptions.setTaOverwriteMessage("This folder already contains a folder named '" + currSrcFile.getName() + "'");
              folderOverWriteOptions.setVisible(true);
              folderOverWriteValue = folderOverWriteOptions.getOverWriteValue();
              if (folderOverWriteValue == FsOverWriteConstants.OVERWRITE_CANCEL) {
                copyMoveProgress.dispose();
                return;
              }
            } else if (folderOverWriteValue == FsOverWriteConstants.OVERWRITE_YES_TO_ALL) {
              fileOverwriteValue = FsOverWriteConstants.OVERWRITE_YES_TO_ALL;
            } else {
              //do nothing
            }

          } else {
            //create folder in the destination location
            currDestFile.mkdir();
          }
        } else {
          //then check for its existance
          if (currDestFile.exists()) {
            if (folderOverWriteValue == FsOverWriteConstants.OVERWRITE_YES ||
                folderOverWriteValue == FsOverWriteConstants.OVERWRITE_YES_TO_ALL) {
              //check if file is read only
              if (!currDestFile.canWrite()) {
                //ask for overwriting readonly file
                if (fileOverwriteValue == 0 || fileOverwriteValue == FsOverWriteConstants.OVERWRITE_YES) {
                  OverwriteOptionDialog fileOverwriteOption =
                    new OverwriteOptionDialog(parentFrame, "Confirm File Replace", true);
                  fileOverwriteOption
                  .setTaOverwriteMessage("This folder already contains a readonly file named '" + currDestFile
                                                            .getName() + "'");
                  fileOverwriteOption.setLblExistingFileSize(currDestFile.length() + "KB.");
                  fileOverwriteOption.setLblReplaceFileSize(currSrcFile.length() + "KB.");
                  fileOverwriteOption
                  .setLblExistingFileModifiedDate("Modified : " + new Date(currDestFile.lastModified()));
                  fileOverwriteOption
                  .setLblReplaceFileModifiedDate("Modified : " + new Date(currSrcFile.lastModified()) + "");
                  fileOverwriteOption.setVisible(true);
                  fileOverwriteValue = fileOverwriteOption.getOverWriteValue();
                  if (fileOverwriteValue == FsOverWriteConstants.OVERWRITE_CANCEL) {
                    copyMoveProgress.dispose();
                    return;
                  }
                } else if (fileOverwriteValue == FsOverWriteConstants.OVERWRITE_YES_TO_ALL) {
                  currDestFile.delete();
                  copyMoveProgress.setFilePath(currSrcFile.getPath());
                  copyMoveProgress.setPrevByteRead((int)currSrcFile.length());
                  moveFile(currSrcFilePath, currDestFilePath);
                } else if (folderOverWriteValue == 0) {
                  if (fileOverwriteValue != FsOverWriteConstants.OVERWRITE_YES_TO_ALL) {
                    OverwriteOptionDialog fileOverwriteOption =
                      new OverwriteOptionDialog(parentFrame, "Confirm File Replace", true);
                    fileOverwriteOption
                    .setTaOverwriteMessage("This folder already contains a file named '" + currDestFile.getName() +
                                                              "'");
                    fileOverwriteOption.setLblExistingFileSize(currDestFile.length() + "KB.");
                    fileOverwriteOption.setLblReplaceFileSize(currSrcFile.length() + "KB.");
                    fileOverwriteOption
                    .setLblExistingFileModifiedDate("Modified : " + new Date(currDestFile.lastModified()) + "");
                    fileOverwriteOption
                    .setLblReplaceFileModifiedDate("Modified : " + new Date(currSrcFile.lastModified()));
                    fileOverwriteOption.setVisible(true);
                    fileOverwriteValue = fileOverwriteOption.getOverWriteValue();
                    if (fileOverwriteValue == FsOverWriteConstants.OVERWRITE_CANCEL) {
                      copyMoveProgress.dispose();
                      return;
                    }
                  }
                  currDestFile.delete();
                  copyMoveProgress.setFilePath(currSrcFile.getPath());
                  copyMoveProgress.setPrevByteRead((int)currSrcFile.length());
                  moveFile(currSrcFilePath, currDestFilePath);
                }
              } else {
                currDestFile.delete();
                copyMoveProgress.setFilePath(currSrcFile.getPath());
                copyMoveProgress.setPrevByteRead((int)currSrcFile.length());
                moveFile(currSrcFilePath, currDestFilePath);
              }
            } else {
              if (fileOverwriteValue != FsOverWriteConstants.OVERWRITE_YES_TO_ALL) {
                OverwriteOptionDialog fileOverwriteOption =
                  new OverwriteOptionDialog(parentFrame, "Confirm File Replace", true);
                fileOverwriteOption
                .setTaOverwriteMessage("This folder already contains a file named '" + currDestFile.getName() + "'");
                fileOverwriteOption.setLblExistingFileSize(currDestFile.length() + "KB.");
                fileOverwriteOption.setLblReplaceFileSize(currSrcFile.length() + "KB.");
                fileOverwriteOption
                .setLblExistingFileModifiedDate("Modified : " + new Date(currDestFile.lastModified()) + "");
                fileOverwriteOption.setLblReplaceFileSize("Modified : " + new Date(currSrcFile.lastModified()) + "");
                fileOverwriteOption.setVisible(true);
                fileOverwriteValue = fileOverwriteOption.getOverWriteValue();
                if (fileOverwriteValue == FsOverWriteConstants.OVERWRITE_CANCEL) {
                  copyMoveProgress.dispose();
                  return;
                }
              }
              currDestFile.delete();
              copyMoveProgress.setFilePath(currSrcFile.getPath());
              copyMoveProgress.setPrevByteRead((int)currSrcFile.length());
              moveFile(currSrcFilePath, currDestFilePath);
            }
          } else {
            //if the file does not exist then just move

            copyMoveProgress.setFilePath(currSrcFile.getPath());
            copyMoveProgress.setPrevByteRead((int)currSrcFile.length());
            moveFile(currSrcFilePath, currDestFilePath);
          }
        }
      }
      copyMoveProgress.dispose();
    } catch (Exception ex) {
      copyMoveProgress.dispose();
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
    }
  }

  /**
  * to check whether given file is root or not.
  * @param file file object to be checked for root.
  */
  private boolean isRoot(File file) {
    return ((file.getPath().substring(file.getPath().length() - 1)).equals(File.separator));
  }

  /**
  * to perform actual copy at system level.
  * @param src source file.
  * @param dst destination file.
  */
  private void copyFile(File src, File dst) {
    long temp;
    try {
      FileChannel c1 = new RandomAccessFile(src, "r").getChannel();
      FileChannel c2 = new RandomAccessFile(dst, "rw").getChannel();

      long tCount = 0, size = c1.size();
      do {
        temp = c2.transferFrom(c1, 0, size - tCount);
        copyMoveProgress.setPrevByteRead(temp);
        tCount = tCount + temp;
        logger.debug("tCount : " + tCount);
      } while (tCount < size);

      c2.force(true);
      c2.close();
    } catch (FileNotFoundException ex) {
      JOptionPane
      .showMessageDialog(null, "Can not copy:" + src.getName() + "There has been a sharing voilation." + "The source or destination file may be in use.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
      logger.error(GeneralUtil.getStackTrace(ex));
    }
  }

  /**
  * to perform actual move at system level.
  * @param inPath source file path.
  * @param outPath destination file path.
  */
  private int moveFile(String inPath, String outPath) {
    int nReturn = 0;
    File fPath = new File(inPath);
    File fNewPath = new File(outPath);
    final int DIR_INUSE = 1;
    // Simple case
    if (inPath.substring(0, 1).toLowerCase().equals(outPath.substring(0, 1).toLowerCase())) {
      // Move is on same drive so we can use renameTo to adjust the path

      logger.debug("Source and destination In Same Directory");
      if (fPath.canWrite()) {
        fPath.renameTo(fNewPath);
      }
      // At this point, the original path should have been renamed
      // If it was in use at the time of move, then the move wont happen
      if (fPath.exists()) {
        nReturn = DIR_INUSE;
      }
    } else {
      //In this case, we need to create the destination directory structure
      // then move all the files from the source
      // then remove the source.
      logger.debug("Source and destination In Different Directory");
      copyFile(fPath, fNewPath);
      if (fPath.canWrite()) {
        fPath.delete();
      } else {
        nReturn = DIR_INUSE;
      }
    }
    return nReturn;
  }

  /**
   * to delete a file (recursively if it is a folder).
   * @param file file to delete.
   * @return return true is file is deleted successfully otherwise flase.
   */
  public boolean deleteItem(File file) {
    //if file is directory then go inside recursively and delete the items first
    if (file.isDirectory()) {
      File subItems[] = file.listFiles();
      int itemCount = subItems.length;
      for (int index = 0; index < itemCount; index++) {
        if (subItems[index].isDirectory()) {
          deleteFolders.push(subItems[index]);
          deleteItem(subItems[index]);
        } else {
          if (!subItems[index].delete()) {
            JOptionPane.showMessageDialog(parentFrame, "Access denied to" + "\"" + subItems[index].getPath() + "\"");
            return false;
          }
        }
      }
    } else {
      if (!file.delete()) {
        JOptionPane.showMessageDialog(parentFrame, "Access denied to" + "\"" + file.getPath() + "\"");
        return false;
      } else {
        return true;
      }
    }
    File temp;
    while (!deleteFolders.isEmpty()) {
      temp = (File)deleteFolders.pop();
      logger.debug("Deleting ? : " + temp + temp.delete());
    }
    file.delete();
    return true;
  }

  /**
   * to visualize the properties of folders and files.
   * @param itemPaths array of files whose properties to be shown.
   */
  public void showProperties(String[] itemPaths) {

    int folderCount = 0;
    int fileCount = 0;
    final File itemFiles[] = new File[itemPaths.length];
    FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    for (int index = 0; index < itemPaths.length; index++) {
      itemFiles[index] = new File(itemPaths[index]);
      if (itemFiles[index].isDirectory()) {
        folderCount += 1;
      } else {
        fileCount += 1;
      }
    }
    logger.debug("File count : " + fileCount);
    logger.debug("Folder count : " + folderCount);

    if (fileCount == 1 && folderCount == 0) {
      FsFilePropertyPage fsFilePropertyPage = new FsFilePropertyPage(parentFrame, "", false);
      fsFilePropertyPage.addPropertyChangeSupport(this);
      fsFilePropertyPage.setPropertyPageFor(FsMessage.FOR_LOCAL_FILESYSTEM);

      fsFilePropertyPage.setFileName(itemFiles[0].getName());
      fsFilePropertyPage.setJlblFileIcon(fileSystemView.getSystemIcon(itemFiles[0]));
      if (itemFiles[0].length() > 1024 * 1024) {
        fsFilePropertyPage.setSize(itemFiles[0].length() / (1024 * 1024) + " MB(" + itemFiles.length + " bytes )");
      } else {
        fsFilePropertyPage.setSize(itemFiles[0].length() + " bytes");
      }
      fsFilePropertyPage.setLocation(itemFiles[0].getParent());
      fsFilePropertyPage.setTypeOfFile(fileSystemView.getSystemDisplayName(itemFiles[0]));
      fsFilePropertyPage
      .setModifiedDate(dbsentry.filesync.clientgui.utility.GeneralUtil.getDateForDisplay(new Date(itemFiles[0]
                                                                                                                    .lastModified())));
      if (itemFiles[0].canWrite()) {
        fsFilePropertyPage.setPermissions("read ,write");
      } else {
        fsFilePropertyPage.setPermissions("read-only");
      }
      fsFilePropertyPage.setOldFileInfo(itemFiles[0].getName());
      fsFilePropertyPage.setVisible(true);
    } else if (folderCount == 1 && fileCount == 0) {
      Thread showFolderPropertyPage = new Thread(new Runnable() {
                                                   public void run() {
                                                     showFolderPropertyPage(itemFiles);
                                                   }
                                                 }
        );
      showFolderPropertyPage.start();
    } else {
      Thread showFileFolderPropertyPage = new Thread(new Runnable() {
                                                       public void run() {
                                                         showFileFolderPropertyPage(itemFiles);
                                                       }
                                                     }
        );
      showFileFolderPropertyPage.start();
    }
  }

  /**
  * to refresh a local treenode.
  * @param treeView JTree object whose node to be refreshed.
  * @param treeNodeToRefresh DefaultMutableTreeNode object to refresh.
  */
  public static void refreshTreeNode(JTree treeView, DefaultMutableTreeNode treeNodeToRefresh) {
    Logger logger = Logger.getLogger("ClientLogger");
    DefaultMutableTreeNode childNode, subChildNode;
    FsFolderHolder fsFolderHolder = (FsFolderHolder)treeNodeToRefresh.getUserObject();
    File currentFile = new File(fsFolderHolder.getPath());
    File childFiles[] = currentFile.listFiles();
    File subChildFiles[];
    int childFilesLength;
    int subChildFilesLength;
    if (childFiles != null) {
      childFilesLength = childFiles.length;
      logger.debug("childFilesLength : " + childFilesLength);
      treeNodeToRefresh.removeAllChildren();

      Arrays.sort(childFiles, new FolderFileComparatorLocal());
      treeNodeToRefresh.removeAllChildren();
      for (int index = 0; index < childFilesLength; index++) {
        File file = childFiles[index];
        logger.debug(" fileName : " + file.getName());
        if (file.isDirectory()) {
          childNode = new DefaultMutableTreeNode(file.getName());
          fsFolderHolder = new FsFolderHolder();
          fsFolderHolder.setName(file.getName());
          fsFolderHolder.setPath(file.getAbsolutePath());
          childNode.setUserObject(fsFolderHolder);

          treeNodeToRefresh.add(childNode);
          subChildFiles = file.listFiles();
          if (subChildFiles != null) {
            subChildFilesLength = subChildFiles.length;
            logger.debug("subChildFilesLength : " + subChildFilesLength);
            for (int counter = 0; counter < subChildFilesLength; counter++) {
              if (subChildFiles[counter].isDirectory()) {
                subChildNode = new DefaultMutableTreeNode("");
                fsFolderHolder = new FsFolderHolder();
                fsFolderHolder.setName("");
                fsFolderHolder.setPath("");
                subChildNode.setUserObject(fsFolderHolder);
                childNode.add(subChildNode);
                break;
              }
            }
          }
        }
      }
      ((DefaultTreeModel)treeView.getModel()).reload(treeNodeToRefresh);
      ;

      TreeNode treeNodes[] = treeNodeToRefresh.getPath();
      TreePath treePathToSelect = new TreePath(treeNodes);
      treeView.setSelectionPath(treePathToSelect);
    }
  }

  //Find size foldes and documents for a given public object id

  /**
   * to find total size of folder or file.
   * @param itemPath folder/file path.
   * @return total size of folder and files
   * @throws IOException if operation fails.
   */
  public static long findTotalSizeFoldersDocs(String itemPath) throws IOException {
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

  /**
   * to find total size of folder or file.
   * @param itemPaths array of folder/file path.
   * @return folder file size
   * @throws IOException if operation fails.
   */
  public static long calculateFolderDocSize(Object[] itemPaths) throws IOException {
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
   * deletes and syncs the items.
   * @param itemPath path of the item to delete.
   * @return xmlString of the deleted item.
   */
  public String syncDeleteItem(String itemPath) {
    File file = new File(itemPath);
    Document document = commonUtil.getEmptyDocumentObject();
    if (file.isDirectory()) {
      clientUtil.addDocumentElement(document, file, EnumSyncOperation.FOLDER_DELETED);
    } else {
      clientUtil.addDocumentElement(document, file, EnumSyncOperation.FILE_DELETED);
    }
    String xmlString = commonUtil.getXMLStringFromDocument(document);
    deleteItem(file);

    return xmlString;
  }

  /**
   * displays the folder property page.
   * @param itemFiles list of folder whose property is to be displayed.
   */
  public void showFolderPropertyPage(File[] itemFiles) {
    TotalInfoFoldersFiles totalInfoFoldersFiles;
    FsFolderPropertyPage fsFolderPropertyPage = new FsFolderPropertyPage(parentFrame, "", false);
    fsFolderPropertyPage.addPropertyChangeSupport(this);
    fsFolderPropertyPage.setPropertyPageFor(FsMessage.FOR_LOCAL_FILESYSTEM);
    fsFolderPropertyPage.setFolderName(itemFiles[0].getName());
    try {
      totalInfoFoldersFiles = generalUtil.findFoldersFilesInfo(itemFiles);
      fsFolderPropertyPage.setOldAttributes(itemFiles[0].getName());

      long totalSize = totalInfoFoldersFiles.getSize();
      logger.debug("totalSize : " + totalSize);
      totalSize = findTotalSizeFoldersDocs(itemFiles[0].getPath());
      logger.debug("totalSize : " + totalSize);
      if (totalSize > 1024 * 1024) {
        fsFolderPropertyPage.setSize(totalSize / (1024 * 1024) + " MB (" + totalSize + ")");
      } else if (totalSize > 1024) {
        fsFolderPropertyPage.setSize(totalSize / 1024 + " KB (" + totalSize + ")");
      } else {
        fsFolderPropertyPage.setSize(totalSize + " Bytes");
      }
      if (itemFiles[0].canWrite()) {
        fsFolderPropertyPage.setPermissions("read,write");
      } else {
        fsFolderPropertyPage.setPermissions("read-only");
      }

      fsFolderPropertyPage
      .setFileFolderCount(totalInfoFoldersFiles.getFileCount(), totalInfoFoldersFiles.getFolderCount());
      fsFolderPropertyPage.setVisible(true);
    } catch (IOException ioe) {
      logger.debug(generalUtil.getStackTrace(ioe));
    }
    fsFolderPropertyPage.setType("File Folder");
    fsFolderPropertyPage.setLocation(itemFiles[0].getParent());
  }

  /**
   * displays folder file property.
   * @param itemFiles list of items whose property is to be displayed.
   */
  public void showFileFolderPropertyPage(File[] itemFiles) {
    FsFileFolderPropertyPage fsFileFolderPropertyPage = new FsFileFolderPropertyPage(parentFrame, "", false);
    fsFileFolderPropertyPage.setPropertyPageFor(FsMessage.FOR_LOCAL_FILESYSTEM);
    fsFileFolderPropertyPage.setLocation("All in " + itemFiles[0].getParent());
    try {
      TotalInfoFoldersFiles totalInfoFoldersFiles = generalUtil.findFoldersFilesInfo(itemFiles);
      fsFileFolderPropertyPage.setOldAttributes(itemFiles, totalInfoFoldersFiles.getReadOnly().intValue());
      long totalSize = totalInfoFoldersFiles.getSize();
      if (totalSize > 1024 * 1024) {
        fsFileFolderPropertyPage.setSize(totalSize / (1024 * 1024) + " MB(" + totalSize + " bytes )");
      } else {
        fsFileFolderPropertyPage.setSize(totalSize + " bytes");
      }
      fsFileFolderPropertyPage.setType("Multiple Types");
      fsFileFolderPropertyPage
      .setNoOfFilesFolders(totalInfoFoldersFiles.getFileCount(), totalInfoFoldersFiles.getFolderCount());

      fsFileFolderPropertyPage.setVisible(true);
    } catch (Exception ex) {
      logger.debug(generalUtil.getStackTrace(ex));
    }
  }
}
