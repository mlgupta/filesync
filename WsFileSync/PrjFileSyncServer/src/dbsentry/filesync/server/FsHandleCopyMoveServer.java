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
 * $Id: FsHandleCopyMoveServer.java,v 1.16 2006/03/02 05:53:31 sgupta Exp $
 *****************************************************************************
 */
/*package dbsentry.filesync.server;
                                                              THIS CLASS HAS BEEN REMOVED
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsMessage;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeSupport;

import java.io.File;

import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;


/**
 * To handle copy/move operation at server side.
 *  @author Deepali Chitkulwar
 *  @version 1.0
 * 	Date of creation:    04-07-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 
public class FsHandleCopyMoveServer implements PropertyChangeListener {

  static Stack clipBoard;

  private Logger logger;

  private String superRequestCode;

  private Object itemToCopyPaths[];

  private FsObject copySrcBaseObject;

  private String copySrcBaseObjectPath;

  private FsObject copyDestBaseObject;

  private String copyDestBaseObjectPath;

  private Object itemToMovePaths[];

  private FsObject moveSrcBaseObject;

  private String moveSrcBaseObjectPath;

  private FsObject moveDestBaseObject;

  private String moveDestBaseObjectPath;

  private FsObject currDestObject;

  private String currDestObjectPath;

  private FsObject currSrcObject;

  private String currSrcObjectPath;

  private FsMessageSender fsMessageSender;

  private FsConnection fsConnection;

  //private PropertyChangeSupport propertyChangeSupport;

  private ServerUtil serverUtil;

  private int folderOverWriteValue;

  private int fileOverwriteValue;

  private boolean operationCancelled;
  
  private FsClientHandler fsClientHandler;

  
  public FsHandleCopyMoveServer(String superRequestCode, FsClientHandler fsClientHandler) {
    this.logger = Logger.getLogger("ServerLogger");
    this.superRequestCode = superRequestCode;
    this.fsClientHandler=fsClientHandler;
    this.fsMessageSender = fsClientHandler.getFsMessageSender();
    this.fsConnection = fsClientHandler.getFsConnection();
    this.serverUtil = new ServerUtil(logger);

  }

  /**
   * To handle the PropertyChangeEvent.
   * @param evt PropertyChangeEvent object.
   
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(superRequestCode)) {
      if (evt.getPropertyName().equals("fsRequest")) {
        FsRequest fsRequest = (FsRequest)evt.getNewValue();
        handleCopyMove(fsRequest);
      }
      if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);
        //handle exception
      }
    }
  }

  /**
   * To add a PropertyChangeListener which will listen to the PropertyChangeEvent fired by 
   * this class.
   * @param propertyChangeListener listener which will listen to the PropertyChangeEvent fired
   * by this class.
   */
  /*public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
    }
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }

  /**
   * Handles copy/move related requests.
   * @param fsRequest FsRequest object containing copy/move related request.
   
  private void handleCopyMove(FsRequest fsRequest) {
    FsResponse fsResponse = new FsResponse();
    int requestCode = fsRequest.getRequestCode();
    try {
      switch (requestCode) {
      case FsMessage.REMOTE_ITEM_COPY :
        clipBoard = new Stack();
        copyDestBaseObjectPath = (String)fsRequest.getData();
        copyDestBaseObject = fsConnection.findFsObjectByPath(copyDestBaseObjectPath);
        itemToCopyPaths = fsRequest.getDatas();
        if (itemToCopyPaths == null) {
          return;
        }
        logger.debug("itemToCopyPaths.length : " + itemToCopyPaths.length);

        long totalSizeCopy = fsConnection.calculateFolderDocSize(itemToCopyPaths);
        fsResponse = new FsResponse();
        fsResponse.setData(new Long(totalSizeCopy));
        fsResponse.setResponseCode(FsMessage.REMOTE_COPY_STARTED);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);

        folderOverWriteValue = 0;
        fileOverwriteValue = 0;
        operationCancelled = false;

        if (itemToCopyPaths != null) {
          int itemCount = itemToCopyPaths.length;
          for (int index = 0; index < itemCount; index++) {
            clipBoard.push(itemToCopyPaths[index]);
          }
          logger.debug("clipBoard.firstElement()" + clipBoard.firstElement());
          if (!clipBoard.isEmpty()) {
            copySrcBaseObject = (fsConnection.findFsObjectByPath((String)clipBoard.firstElement())).getParent();
            copySrcBaseObjectPath = copySrcBaseObject.getPath();
            logger.debug("copySrcBaseObjectPath : " + copySrcBaseObjectPath);
          }
          handleCopy(fsRequest.getSuperRequestCode());
        } else {
          logger.debug("itemToCopyPaths is null");
        }
        break;
      case FsMessage.OVERWRITE_OPTION_FOLDER_COPY :
        folderOverWriteValue = ((Integer)fsRequest.getData()).intValue();
        logger.debug("Overwrite Value Copy : " + folderOverWriteValue);
        handleCopy(fsRequest.getSuperRequestCode());
        break;
      case FsMessage.OVERWRITE_OPTION_FILE_COPY :
        fileOverwriteValue = ((Integer)fsRequest.getData()).intValue();
        logger.debug("Overwrite Value Copy : " + folderOverWriteValue);
        handleCopy(fsRequest.getSuperRequestCode());
        break;
      case FsMessage.REMOTE_COPY_CANCEL :
        if (((Boolean)fsRequest.getData()).booleanValue()) {
          logger.debug(" Copy Cancelled ");
          //propertyChangeSupport.firePropertyChange("fsHandleCopyMoveServer", null, this);
          fsClientHandler.removePropertyChangeListener(this);
        } else {
          handleCopy(fsRequest.getSuperRequestCode());
        }
        break;
      case FsMessage.REMOTE_ITEM_MOVE :
        clipBoard = new Stack();
        moveDestBaseObjectPath = (String)fsRequest.getData();
        moveDestBaseObject = fsConnection.findFsObjectByPath(moveDestBaseObjectPath);
        itemToMovePaths = fsRequest.getDatas();

        long totalSizeCopyMove = fsConnection.calculateFolderDocSize(itemToMovePaths);
        fsResponse = new FsResponse();
        fsResponse.setData(new Long(totalSizeCopyMove));
        fsResponse.setResponseCode(FsMessage.REMOTE_MOVE_STARTED);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);

        folderOverWriteValue = 0;
        fileOverwriteValue = 0;
        operationCancelled = false;

        if (itemToMovePaths != null) {
          int itemCount = itemToMovePaths.length;
          for (int index = 0; index < itemCount; index++) {
            clipBoard.push(itemToMovePaths[index]);
          }
          logger.debug("clipBoard.firstElement()" + clipBoard.firstElement());
          if (!clipBoard.isEmpty()) {
            moveSrcBaseObject = (fsConnection.findFsObjectByPath((String)clipBoard.firstElement())).getParent();
            moveSrcBaseObjectPath = moveSrcBaseObject.getPath();
            logger.debug("moveSrcBaseObjectPath : " + moveSrcBaseObjectPath);
          }
          handleMove(fsRequest.getSuperRequestCode());
        } else {
          logger.debug("itemToMovePaths is null");
        }
        break;
      case FsMessage.OVERWRITE_OPTION_FOLDER_MOVE :
        folderOverWriteValue = ((Integer)fsRequest.getData()).intValue();
        logger.debug("Overwrite Value Move : " + folderOverWriteValue);
        handleMove(fsRequest.getSuperRequestCode());
        break;
      case FsMessage.OVERWRITE_OPTION_FILE_MOVE :
        fileOverwriteValue = ((Integer)fsRequest.getData()).intValue();
        logger.debug("Overwrite Value Move : " + folderOverWriteValue);
        handleMove(fsRequest.getSuperRequestCode());
        break;
      case FsMessage.REMOTE_MOVE_CANCEL :
        if (((Boolean)fsRequest.getData()).booleanValue()) {
          logger.debug("Move Cancelled ");
          //propertyChangeSupport.firePropertyChange("fsHandleCopyMoveServer", null, this);
          fsClientHandler.removePropertyChangeListener(this);
        } else {
          handleMove(fsRequest.getSuperRequestCode());
        }
        break;
      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      //propertyChangeSupport.firePropertyChange("fsHandleCopyMoveServer", null, this);
      fsClientHandler.removePropertyChangeListener(this);
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      //propertyChangeSupport.firePropertyChange("fsHandleCopyMoveServer", null, this);
      fsClientHandler.removePropertyChangeListener(this);
    }
  }

  /**
   * To handle the copy operation.
   * @param superResponseCode String to be sent back to client to distinguish among the responses.
   
  private void handleCopy(String superResponseCode) {
    logger.debug("In HandleCopy");
    logger.debug("super response code : " + superResponseCode);
    FsResponse fsResponse;
    FsFile currSrcFile;

    if (!clipBoard.isEmpty()) {
      try {
        //Constitute Destination object path it may be file or folder

        currSrcObjectPath = (String)clipBoard.pop();
        currSrcObject = fsConnection.findFsObjectByPath(currSrcObjectPath);
        logger.debug("currSrcObjectPath : " + currSrcObjectPath);

        if (isRoot(copySrcBaseObject) && !isRoot(copyDestBaseObject)) {
          currDestObjectPath =
            copyDestBaseObjectPath + File.separator + currSrcObjectPath.substring(copySrcBaseObjectPath.length());
        } else {
          //if(!isRoot(srcBaseFile) && isRoot(destBaseFile)){
          currDestObjectPath = copyDestBaseObjectPath + currSrcObjectPath.substring(copySrcBaseObjectPath.length());
        }
        String currDestBaseObjectPath =
          currDestObjectPath.substring(0, (currDestObjectPath.length() - currSrcObject.getName().length() - 1));
        logger.debug("currDestBaseObjectPath : " + currDestBaseObjectPath);
        logger.debug("currDestObjectPath : " + currDestObjectPath);


        currDestObject = fsConnection.findFsObjectByPath(currDestObjectPath);
        logger.debug("currSrcObject.getName() : " + currSrcObject.getName());

        if (currSrcObject instanceof FsFolder) {
          //push the content of the current folder in stack
          FsObject fsObjects[] = ((FsFolder)currSrcObject).listContentOfFolder();
          for (int index = 0; index < fsObjects.length; index++) {
            clipBoard.push(fsObjects[index].getPath());
          }
          //check if this current folder exists in the current destination folder

          if (currDestObject != null) {
            logger.debug("currDestObject.getName() : " + currDestObject.getName());
            if (folderOverWriteValue == 0 || folderOverWriteValue == FsMessage.OVERWRITE_YES) {
              fsResponse = new FsResponse();
              fsResponse.setData(currDestObject.getPath());
              fsResponse.setResponseCode(FsMessage.OVERWRITE_OPTION_FOLDER_COPY);
              fsResponse.setSuperResponseCode(superResponseCode);
              fsMessageSender.send(fsResponse);
              return;
            } else if (folderOverWriteValue == FsMessage.OVERWRITE_CANCEL) {
              return;
            } else if (folderOverWriteValue == FsMessage.OVERWRITE_YESALL) {
              fileOverwriteValue = FsMessage.OVERWRITE_YESALL;
            } else {
              //do nothing

            }
          } else {
            //create folder in the destination location
            //find the path of the destination file
            fsConnection.createFolder(currDestBaseObjectPath, currSrcObject.getName());
            handleCopy(superResponseCode);
          }
        } else {
          logger.debug("Current source file is NOT directory");

          //check for existance of destination file
          if (currDestObject != null && !currDestObjectPath.equals(currSrcObjectPath)) {
            logger.debug("currSrcObject.getName() : " + currDestObject.getName());
            if (folderOverWriteValue == FsMessage.OVERWRITE_YES ||
                folderOverWriteValue == FsMessage.OVERWRITE_YESALL) {
              //check if file is read only                             
              if (currDestObject.getPermission().canWrite()) {
                currSrcFile = (FsFile)currSrcObject;
                fsResponse = new FsResponse();
                fsResponse.setData(currSrcFile.getPath());
                fsResponse.setData1(new Long(currSrcFile.getSize()));
                fsResponse.setResponseCode(FsMessage.REMOTE_FILE_COPIED);
                fsResponse.setSuperResponseCode(superResponseCode);
                fsMessageSender.send(fsResponse);
                fsConnection.copyItem(currDestBaseObjectPath, currSrcObjectPath);
              } else {
                //ask for overwriting readonly file
                if (fileOverwriteValue == 0 || fileOverwriteValue == FsMessage.OVERWRITE_YES) {
                  fsResponse = new FsResponse();
                  fsResponse.setData(currDestObject.getName());
                  fsResponse.setData1(new Long(((FsFile)currSrcObject).getSize()));
                  fsResponse.setData2(new Long(((FsFile)currDestObject).getSize()));
                  fsResponse.setResponseCode(FsMessage.OVERWRITE_OPTION_FILE_COPY);
                  fsResponse.setSuperResponseCode(superResponseCode);
                  fsMessageSender.send(fsResponse);
                  return;
                } else if (fileOverwriteValue == FsMessage.OVERWRITE_CANCEL) {
                  return;
                } else if (fileOverwriteValue == FsMessage.OVERWRITE_YESALL) {
                  currSrcFile = (FsFile)currSrcObject;
                  fsResponse = new FsResponse();
                  fsResponse.setData(currSrcObject.getPath());
                  fsResponse.setData1(new Long(currSrcFile.getSize()));
                  fsResponse.setResponseCode(FsMessage.REMOTE_FILE_COPIED);
                  fsResponse.setSuperResponseCode(superResponseCode);
                  fsMessageSender.send(fsResponse);
                  fsConnection.copyItem(currDestBaseObjectPath, currSrcObjectPath);
                } else {
                  //do nothing  
                }
              }
            } else {
              if (fileOverwriteValue == 0 && (itemToCopyPaths.length == 1)) {
                if (!currDestObject.getPermission().canWrite()) {
                  currSrcFile = (FsFile)currSrcObject;
                  fsResponse = new FsResponse();
                  fsResponse.setData(currSrcFile.getPath());
                  fsResponse.setData1(new Long(currSrcFile.getSize()));
                  fsResponse.setResponseCode(FsMessage.REMOTE_FILE_COPIED);
                  fsResponse.setSuperResponseCode(superResponseCode);
                  fsMessageSender.send(fsResponse);
                  fsConnection.copyItem(currDestBaseObjectPath, currSrcObjectPath);
                } else {
                  folderOverWriteValue = FsMessage.OVERWRITE_YES;
                  fsResponse = new FsResponse();
                  fsResponse.setData(currDestObject.getName());
                  fsResponse.setData1(new Long(((FsFile)currSrcObject).getSize()));
                  fsResponse.setData2(new Long(((FsFile)currDestObject).getSize()));
                  fsResponse.setResponseCode(FsMessage.OVERWRITE_OPTION_FILE_COPY);
                  fsResponse.setSuperResponseCode(superResponseCode);
                  fsMessageSender.send(fsResponse);
                  return;
                }
              }
            }
          } else {
            //if the file does not exist then just copy 
            currSrcFile = (FsFile)currSrcObject;
            fsResponse = new FsResponse();
            fsResponse.setData(currSrcObject.getPath());
            fsResponse.setData1(new Long(currSrcFile.getSize()));
            fsResponse.setResponseCode(FsMessage.REMOTE_FILE_COPIED);
            fsResponse.setSuperResponseCode(superResponseCode);
            fsMessageSender.send(fsResponse);
            fsConnection.copyItem(currDestBaseObjectPath, currSrcObjectPath);
          }
        }
      } catch (FsException fex) {
        logger.error(serverUtil.getStackTrace(fex));
        FsExceptionHolder fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsMessage.REMOTE_COPY_FAILED);
        fsResponse.setSuperResponseCode(superResponseCode);
        fsResponse.setFsExceptionHolder(fsExceptionHolder);
        fsMessageSender.send(fsResponse);
        //propertyChangeSupport.firePropertyChange("fsHandleCopyMoveServer", null, this);
        fsClientHandler.removePropertyChangeListener(this);
        return;
      } catch (Exception ex) {
        logger.info("Failed To Copy");
        logger.error(serverUtil.getStackTrace(ex));
        return;
      }
    }
    if (clipBoard.isEmpty()) {
      logger.debug("Copy Operation Completed Successfully");
      fsResponse = new FsResponse();
      fsResponse.setResponseCode(FsMessage.REMOTE_ITEM_COPIED);
      fsResponse.setData(copyDestBaseObjectPath);
      fsResponse.setSuperResponseCode(superResponseCode);
      fsMessageSender.send(fsResponse);
      //propertyChangeSupport.firePropertyChange("fsHandleCopyMoveServer", null, this);
      fsClientHandler.removePropertyChangeListener(this);
    }
  }

  /**
   * To handle the move operation.
   * @param superResponseCode String to be sent back to client to distinguish among the responses.
   
  private void handleMove(String superResponseCode) {
    logger.debug("In HandleMove");

    FsResponse fsResponse;
    FsFile currSrcFile;
    try {
      if (!clipBoard.isEmpty()) {

        currSrcObjectPath = (String)clipBoard.pop();
        currSrcObject = fsConnection.findFsObjectByPath(currSrcObjectPath);
        logger.debug("currSrcObjectPath : " + currSrcObjectPath);

        //Constitute Destination object path,it may be folder or file 

        if ((isRoot(moveDestBaseObject) && isRoot(moveSrcBaseObject)) ||
            (!isRoot(moveDestBaseObject) && !isRoot(moveSrcBaseObject))) {
          currDestObjectPath = (moveDestBaseObjectPath + currSrcObjectPath.substring(moveSrcBaseObjectPath.length()));
        } else if (isRoot(moveSrcBaseObject) && !isRoot(moveDestBaseObject)) {
          currDestObjectPath =
            moveDestBaseObjectPath + File.separator + currSrcObjectPath.substring(moveSrcBaseObjectPath.length());
        } else {
          //if(!isRoot(srcBaseFile) && isRoot(destBaseFile)){
          currDestObjectPath = moveDestBaseObjectPath + currSrcObjectPath.substring(moveSrcBaseObjectPath.length());
        }
        String currDestBaseObjectPath =
          currDestObjectPath.substring(0, (currDestObjectPath.length() - currSrcObject.getName().length() - 1));
        currDestObject = fsConnection.findFsObjectByPath(currDestObjectPath);

        logger.debug(" currDestBaseObjectPath : " + currDestBaseObjectPath);
        logger.debug("currDestObjectPath : " + currDestObjectPath);
        logger.debug("currSrcObject.getName() : " + currSrcObject.getName());

        if (currSrcObject instanceof FsFolder) {
          //push the content of the current folder in stack
          FsObject fsObjects[] = ((FsFolder)currSrcObject).listContentOfFolder();
          for (int index = 0; index < fsObjects.length; index++) {
            clipBoard.push(fsObjects[index].getPath());
          }
          //check if this current folder exists in the current destination folder
          if (currDestObject != null) {
            logger.debug("currSrcObject.getName() : " + currDestObject.getName());
            if (folderOverWriteValue == 0 || folderOverWriteValue == FsMessage.OVERWRITE_YES) {
              fsResponse = new FsResponse();
              fsResponse.setData(currDestObject.getName());
              fsResponse.setResponseCode(FsMessage.OVERWRITE_OPTION_FOLDER_MOVE);
              fsResponse.setSuperResponseCode(superResponseCode);
              fsMessageSender.send(fsResponse);
              return;
            } else if (folderOverWriteValue == FsMessage.OVERWRITE_CANCEL) {
              return;
            } else if (folderOverWriteValue == FsMessage.OVERWRITE_YESALL) {
              fileOverwriteValue = FsMessage.OVERWRITE_YESALL;

            } else {
              //do nothing

            }
          } else {
            //create folder in the destination location
            fsConnection.createFolder(currDestBaseObjectPath, currSrcObject.getName());
            handleMove(superResponseCode);
          }
        } else {
          logger.debug("Current source file is NOT directory");

          //check for existance of destination file
          if (currDestObject != null && !currDestObjectPath.equals(currSrcObjectPath)) {
            if (folderOverWriteValue == FsMessage.OVERWRITE_YES ||
                folderOverWriteValue == FsMessage.OVERWRITE_YESALL) {
              //check if file is read only
              if (!currDestObject.getPermission().canWrite()) {
                currSrcFile = (FsFile)currSrcObject;
                fsResponse = new FsResponse();
                fsResponse.setData(currSrcObject.getPath());
                fsResponse.setData1(new Long(currSrcFile.getSize()));
                fsResponse.setResponseCode(FsMessage.REMOTE_FILE_MOVED);
                fsResponse.setSuperResponseCode(superResponseCode);
                fsMessageSender.send(fsResponse);
                fsConnection.moveItem(currDestBaseObjectPath, currSrcObjectPath);

              } else {
                //ask for overwriting readonly file
                if (fileOverwriteValue == 0 || fileOverwriteValue == FsMessage.OVERWRITE_YES) {
                  fsResponse = new FsResponse();
                  fsResponse.setData(currDestObject.getName());
                  fsResponse.setData1(new Long(((FsFile)currSrcObject).getSize()));
                  fsResponse.setData2(new Long(((FsFile)currDestObject).getSize()));
                  fsResponse.setResponseCode(FsMessage.OVERWRITE_OPTION_FILE_MOVE);
                  fsResponse.setSuperResponseCode(superResponseCode);
                  fsMessageSender.send(fsResponse);
                  return;
                } else if (fileOverwriteValue == FsMessage.OVERWRITE_CANCEL) {
                  return;
                } else if (fileOverwriteValue == FsMessage.OVERWRITE_YESALL) {
                  currSrcFile = (FsFile)currSrcObject;
                  fsResponse = new FsResponse();
                  fsResponse.setData(currSrcObject.getPath());
                  fsResponse.setData1(new Long(currSrcFile.getSize()));
                  fsResponse.setResponseCode(FsMessage.REMOTE_FILE_MOVED);
                  fsResponse.setSuperResponseCode(superResponseCode);
                  fsMessageSender.send(fsResponse);
                  fsConnection.moveItem(currDestBaseObjectPath, currSrcObjectPath);
                } else {
                  //do nothing
                }
              }
            } else {
              // this case arises when there is single file to move
              if (fileOverwriteValue == 0 && (itemToMovePaths.length == 1)) {
                if (!currDestObject.getPermission().canWrite()) {
                  currSrcFile = (FsFile)currSrcObject;
                  fsResponse = new FsResponse();
                  fsResponse.setData(currSrcObject.getPath());
                  fsResponse.setData1(new Long(currSrcFile.getSize()));
                  fsResponse.setResponseCode(FsMessage.REMOTE_FILE_MOVED);
                  fsResponse.setSuperResponseCode(superResponseCode);
                  fsMessageSender.send(fsResponse);
                  fsConnection.moveItem(currDestBaseObjectPath, currSrcObjectPath);
                } else {
                  folderOverWriteValue = FsMessage.OVERWRITE_YES;
                  fsResponse = new FsResponse();
                  fsResponse.setData(currDestObject.getName());
                  fsResponse.setData1(new Long(((FsFile)currSrcObject).getSize()));
                  fsResponse.setData2(new Long(((FsFile)currDestObject).getSize()));
                  fsResponse.setResponseCode(FsMessage.OVERWRITE_OPTION_FILE_MOVE);
                  fsResponse.setSuperResponseCode(superResponseCode);
                  fsMessageSender.send(fsResponse);
                  return;
                }
              }
            }
          } else {
            //if the file does not exist then just move
            currSrcFile = (FsFile)currSrcObject;
            fsResponse = new FsResponse();
            fsResponse.setData(currSrcObject.getPath());
            fsResponse.setData1(new Long(currSrcFile.getSize()));
            fsResponse.setResponseCode(FsMessage.REMOTE_FILE_MOVED);
            fsResponse.setSuperResponseCode(superResponseCode);
            fsMessageSender.send(fsResponse);
            fsConnection.moveItem(currDestBaseObjectPath, currSrcObjectPath);
          }
        }
      }
      if (clipBoard.isEmpty()) {
        if (itemToMovePaths != null) {
          Vector temp = new Vector();
          for (int index = 0; index < itemToMovePaths.length; index++) {
            FsObject fsObject = fsConnection.findFsObjectByPath((String)itemToMovePaths[index]);

            if (fsObject instanceof FsFolder) {
              temp.add(itemToMovePaths[index]);
            }
          }
          fsConnection.deleteItems(temp.toArray());
        }
        logger.debug("Move Operation Completed Successfully");
        fsResponse = new FsResponse();
        fsResponse.setData(moveDestBaseObjectPath);
        fsResponse.setData1(moveSrcBaseObjectPath);
        fsResponse.setResponseCode(FsMessage.REMOTE_ITEM_MOVED);
        fsResponse.setSuperResponseCode(superResponseCode);
        fsMessageSender.send(fsResponse);
        //propertyChangeSupport.firePropertyChange("fsHandleCopyMoveServer", null, this);
        fsClientHandler.removePropertyChangeListener(this);
      }
    } catch (FsException fex) {
      logger.info("Failed To Move");
      logger.error(serverUtil.getStackTrace(fex));
      FsExceptionHolder fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
      fsResponse = new FsResponse();
      fsResponse.setResponseCode(FsMessage.REMOTE_MOVE_FAILED);
      fsResponse.setSuperResponseCode(superResponseCode);
      fsResponse.setFsExceptionHolder(fsExceptionHolder);
      fsMessageSender.send(fsResponse);
      //propertyChangeSupport.firePropertyChange("fsHandleCopyMoveServer", null, this);
      fsClientHandler.removePropertyChangeListener(this);
    } catch (Exception ex) {
      logger.info("Failed To Move");
      logger.error(serverUtil.getStackTrace(ex));
    }
  }

  /**
   * To check whether the given fsObject is root. 
   * @param fsObject FsObject to be checked for the root.
   * @return whether specified fsObject is root or not.
   
  private boolean isRoot(FsObject fsObject) {
    return ((fsObject.getPath().substring(fsObject.getPath().length() - 1)).equals(fsConnection.getSeperator()));
  }

  /**
   * gives String representation of FsHandleCopyMove object.
   * @return String repersentation of FsHandleCopyMove object.
   
  public String toString() {
    return this.toString();
  }
}*/
