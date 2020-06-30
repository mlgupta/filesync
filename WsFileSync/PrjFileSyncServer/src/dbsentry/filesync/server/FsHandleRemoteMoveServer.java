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
 * $Id$
 *****************************************************************************
 */

package dbsentry.filesync.server;

import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsFolderHolder;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsRemoteMoveListener;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;

import java.beans.PropertyChangeEvent;

import java.io.File;

import java.util.Stack;

import org.apache.log4j.Logger;


/**
 * @author Saurabh Gupta
 * @version 1.1
 * @description
 * @Date of creation: 01-03-2006
 * @Last Modfied by : Saurabh gupta
 * @Last Modfied Date: 10-04-2006
 */
public class FsHandleRemoteMoveServer implements FsRemoteMoveListener {
  static Stack clipBoard;

  private Logger logger;

  private String superRequestCode;

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

  private ServerUtil serverUtil;

  private boolean operationCancelled;

  private FsClientHandler fsClientHandler;

  private String currDestBaseObjectPath;


  public FsHandleRemoteMoveServer(String superRequestCode, FsClientHandler fsClientHandler) {
    this.logger = Logger.getLogger("ServerLogger");
    this.superRequestCode = superRequestCode;
    this.fsClientHandler = fsClientHandler;
    this.fsMessageSender = fsClientHandler.getFsMessageSender();
    this.fsConnection = fsClientHandler.getFsConnection();
    this.serverUtil = new ServerUtil(logger);
  }

  /* public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(superRequestCode)) {
      if (evt.getPropertyName().equals("fsRequest")) {
        FsRequest fsRequest = (FsRequest)evt.getNewValue();
        handleRemoteMove(fsRequest);
      }
      if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);

      }
    }
  }*/

  public void propertyChange(PropertyChangeEvent evt) {
    logger.debug("Property Name as super Request Code :" + evt.getPropertyName());
    logger.debug("super Request Code :" + superRequestCode);
    if (evt.getPropertyName().equals(superRequestCode)) {
      FsRequest fsRequest = (FsRequest)evt.getNewValue();
      handleRemoteMove(fsRequest);
    }
  }

  private void handleRemoteMove(FsRequest fsRequest) {
    FsResponse fsResponse = new FsResponse();
    int requestCode = fsRequest.getRequestCode();
    try {
      switch (requestCode) {
        case START :
          clipBoard = new Stack();
          moveDestBaseObjectPath = (String)fsRequest.getData();
          moveDestBaseObject = fsConnection.findFsObjectByPath(moveDestBaseObjectPath);
          itemToMovePaths = fsRequest.getDatas();
          if (itemToMovePaths == null) {
            return;
          }
          long totalSizeMove = fsConnection.calculateFolderDocSize(itemToMovePaths);
          fsResponse = new FsResponse();
          fsResponse.setData(new Long(totalSizeMove));
          fsResponse.setResponseCode(STARTED);
          fsResponse.setOperation(FsRemoteOperationConstants.MOVE);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);

          //folderOverWriteValue = 0;
          //fileOverwriteValue = 0;
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
        case OVERWRITE_OPTION_FOLDER :
          fsResponse = new FsResponse();
          fsResponse.setResponseCode(OVERWRITE_OPTION_FOLDER);
          fsResponse.setOperation(FsRemoteOperationConstants.MOVE);
          fsResponse.setData(fsRequest.getData());
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case OVERWRITE_OPTION_FILE :
          fsResponse = new FsResponse();
          fsResponse.setResponseCode(OVERWRITE_OPTION_FILE);
          fsResponse.setOperation(FsRemoteOperationConstants.MOVE);
          fsResponse.setData(fsRequest.getData());
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case OVERWRITE_FOLDER :
          //push the content of the current folder in stack
          logger.debug("Overwrite the folder");
          FsObject fsObjects[] = ((FsFolder)currSrcObject).listContentOfFolder();
          for (int index = 0; index < fsObjects.length; index++) {
            clipBoard.push(fsObjects[index].getPath());
          }
          handleMove(fsRequest.getSuperRequestCode());
          break;
          /* folderOverWriteValue = ((Integer)fsRequest.getData()).intValue();
        logger.debug("Overwrite Value Move : " + folderOverWriteValue);
        handleMove(fsRequest.getSuperRequestCode());
        break;*/
        case OVERWRITE_FILE :
          logger.debug("Overwrite the file");

          logger.debug("currSrcObjectPath " + currSrcObjectPath);
          logger.debug("currDestBaseObjectPath " + currDestBaseObjectPath);

          FsFile currSrcFile = (FsFile)currSrcObject;
          fsResponse = new FsResponse();
          fsResponse.setData(currSrcObject.getPath());
          fsResponse.setData1(new Long(currSrcFile.getSize()));
          fsResponse.setOperation(FsRemoteOperationConstants.MOVE);
          fsResponse.setResponseCode(NEXT_ITEM);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);

          fsConnection.moveItem(currDestBaseObjectPath, currSrcObjectPath);

          // handleMove(fsRequest.getSuperRequestCode());
          /* fileOverwriteValue = ((Integer)fsRequest.getData()).intValue();
        logger.debug("Overwrite Value Move : " + folderOverWriteValue);
        handleMove(fsRequest.getSuperRequestCode());
        break;*/
        case CANCEL :
          fsResponse = new FsResponse();
          fsResponse.setOperation(FsRemoteOperationConstants.MOVE);
          fsResponse.setResponseCode(CANCEL);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case CANCLED :
          /* if (((Boolean)fsRequest.getData()).booleanValue()) {
          logger.debug("Move Cancelled ");
          fsClientHandler.removePropertyChangeListener(this);
        } else {
          handleMove(fsRequest.getSuperRequestCode());
        }*/
          logger.debug(" Move Cancelled ");
          fsClientHandler.removePropertyChangeListener(this);
          break;
        case NEXT_ITEM :
          handleMove(fsRequest.getSuperRequestCode());
          break;

      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      fsClientHandler.removePropertyChangeListener(this);
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      fsClientHandler.removePropertyChangeListener(this);
    }
  }

  private void handleMove(String superResponseCode) {
    logger.debug("In HandleMove");

    FsResponse fsResponse;
    FsFile currSrcFile;

    if (!clipBoard.isEmpty()) {
      try {
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
        currDestBaseObjectPath =
          currDestObjectPath.substring(0, (currDestObjectPath.length() - currSrcObject.getName().length() - 1));
        currDestObject = fsConnection.findFsObjectByPath(currDestObjectPath);

        logger.debug(" currDestBaseObjectPath : " + currDestBaseObjectPath);
        logger.debug("currDestObjectPath : " + currDestObjectPath);
        logger.debug("currSrcObject.getName() : " + currSrcObject.getName());

        if (currSrcObject instanceof FsFolder) {
          //push the content of the current folder in stack
          logger.debug("Folder......");
          /*FsObject fsObjects[] = ((FsFolder)currSrcObject).listContentOfFolder();
          for (int index = 0; index < fsObjects.length; index++) {
            clipBoard.push(fsObjects[index].getPath());
          }*/
          //check if this current folder exists in the current destination folder
          if (currDestObject != null) {
            logger.debug("currSrcObject.getName() : " + currDestObject.getName());
            FsFolder fsFolderCurrentDest = (FsFolder)currDestObject;
            FsFolderHolder fsFolderHolderCurrentDest = new FsFolderHolder();
            fsFolderHolderCurrentDest.setName(fsFolderCurrentDest.getName());
            fsFolderHolderCurrentDest.setPath(fsFolderCurrentDest.getPath());
            fsFolderHolderCurrentDest.setOwner(fsFolderCurrentDest.getOwner());
            fsFolderHolderCurrentDest.setCreationDate(fsFolderCurrentDest.getCreationDate());
            fsFolderHolderCurrentDest.setModifiedDate(fsFolderCurrentDest.getModifiedDate());
            fsFolderHolderCurrentDest.setDescription(fsFolderCurrentDest.getDescription());

            FsFolder fsFolderCurrentSrc = (FsFolder)currSrcObject;
            FsFolderHolder fsFolderHolderCurrentSrc = new FsFolderHolder();
            fsFolderHolderCurrentSrc.setName(fsFolderCurrentSrc.getName());
            fsFolderHolderCurrentSrc.setPath(fsFolderCurrentSrc.getPath());
            fsFolderHolderCurrentSrc.setOwner(fsFolderCurrentSrc.getOwner());
            fsFolderHolderCurrentSrc.setCreationDate(fsFolderCurrentSrc.getCreationDate());
            fsFolderHolderCurrentSrc.setModifiedDate(fsFolderCurrentSrc.getModifiedDate());
            fsFolderHolderCurrentSrc.setDescription(fsFolderCurrentSrc.getDescription());

            fsResponse = new FsResponse();
            fsResponse.setData(fsFolderHolderCurrentDest);
            fsResponse.setData1(fsFolderHolderCurrentSrc);
            fsResponse.setResponseCode(PROMPT_OVERWRITE_FOLDER);
            fsResponse.setOperation(FsRemoteOperationConstants.MOVE);
            fsResponse.setSuperResponseCode(superResponseCode);
            fsMessageSender.send(fsResponse);

            /*if (folderOverWriteValue == 0 || folderOverWriteValue == FsMessage.OVERWRITE_YES) {
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
            }*/
          } else {
            //create folder in the destination location
            fsConnection.createFolder(currDestBaseObjectPath, currSrcObject.getName());
            FsObject fsObjects[] = ((FsFolder)currSrcObject).listContentOfFolder();
            for (int index = 0; index < fsObjects.length; index++) {
              clipBoard.push(fsObjects[index].getPath());
            }
            handleMove(superResponseCode);
          }
        } else {
          logger.debug("File........");

          //check for existance of destination file
          if (currDestObject != null && !currDestObjectPath.equals(currSrcObjectPath)) {
            FsFile fsFileCurrentDest = (FsFile)currDestObject;
            FsFileHolder fsFileHolderCurrentDest = new FsFileHolder();
            fsFileHolderCurrentDest.setName(fsFileCurrentDest.getName());
            fsFileHolderCurrentDest.setPath(fsFileCurrentDest.getPath());
            fsFileHolderCurrentDest.setOwner(fsFileCurrentDest.getOwner());
            fsFileHolderCurrentDest.setSize(fsFileCurrentDest.getSize());
            fsFileHolderCurrentDest.setCreationDate(fsFileCurrentDest.getCreationDate());
            fsFileHolderCurrentDest.setModifiedDate(fsFileCurrentDest.getModifiedDate());
            fsFileHolderCurrentDest.setDescription(fsFileCurrentDest.getDescription());

            FsFile fsFileCurrentSrc = (FsFile)currSrcObject;
            FsFileHolder fsFileHolderCurrentSrc = new FsFileHolder();
            fsFileHolderCurrentSrc.setName(fsFileCurrentSrc.getName());
            fsFileHolderCurrentSrc.setPath(fsFileCurrentSrc.getPath());
            fsFileHolderCurrentSrc.setOwner(fsFileCurrentSrc.getOwner());
            fsFileHolderCurrentSrc.setSize(fsFileCurrentSrc.getSize());
            fsFileHolderCurrentSrc.setCreationDate(fsFileCurrentSrc.getCreationDate());
            fsFileHolderCurrentSrc.setModifiedDate(fsFileCurrentSrc.getModifiedDate());
            fsFileHolderCurrentSrc.setDescription(fsFileCurrentSrc.getDescription());

            fsResponse = new FsResponse();
            fsResponse.setData(fsFileHolderCurrentDest);
            fsResponse.setData1(fsFileHolderCurrentSrc);
            fsResponse.setResponseCode(PROMPT_OVERWRITE_FILE);
            fsResponse.setOperation(FsRemoteOperationConstants.MOVE);
            fsResponse.setSuperResponseCode(superResponseCode);
            fsMessageSender.send(fsResponse);

            /* if (folderOverWriteValue == FsMessage.OVERWRITE_YES ||
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
            }*/
          } else {
            //if the file does not exist then just move
            currSrcFile = (FsFile)currSrcObject;

            fsResponse = new FsResponse();
            fsResponse.setData(currSrcObject.getPath());
            fsResponse.setData1(new Long(currSrcFile.getSize()));
            fsResponse.setResponseCode(NEXT_ITEM);
            fsResponse.setOperation(FsRemoteOperationConstants.MOVE);
            fsResponse.setSuperResponseCode(superResponseCode);
            fsMessageSender.send(fsResponse);
            fsConnection.moveItem(currDestBaseObjectPath, currSrcObjectPath);
          }
        }
      } catch (FsException fex) {
        logger.info("Failed To Move");
        logger.error(serverUtil.getStackTrace(fex));
        FsExceptionHolder fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(ERROR_MESSAGE);
        fsResponse.setOperation(FsRemoteOperationConstants.MOVE);
        fsResponse.setSuperResponseCode(superResponseCode);
        fsResponse.setFsExceptionHolder(fsExceptionHolder);
        fsMessageSender.send(fsResponse);
        fsClientHandler.removePropertyChangeListener(this);
      } catch (Exception ex) {
        logger.info("Failed To Move");
        logger.error(serverUtil.getStackTrace(ex));
      }
    } else {
      logger.debug("Move Operation Completed Successfully");

      fsResponse = new FsResponse();
      fsResponse.setResponseCode(COMPLETED);
      fsResponse.setData(moveDestBaseObjectPath);
      fsResponse.setData1(moveSrcBaseObjectPath);
      fsResponse.setOperation(FsRemoteOperationConstants.MOVE);
      fsResponse.setSuperResponseCode(superResponseCode);
      fsMessageSender.send(fsResponse);

      fsClientHandler.removeRemoteMoveListener(this);
    }
  }


  private boolean isRoot(FsObject fsObject) {
    return ((fsObject.getPath().substring(fsObject.getPath().length() - 1)).equals(fsConnection.getSeperator()));
  }

  public String toString() {
    return this.toString();
  }

}
