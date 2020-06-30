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
import dbsentry.filesync.common.FsMessage;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsRemoteCopyListener;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

import java.util.Stack;

import org.apache.log4j.Logger;


/**
 * @author Saurabh Gupta
 * @version 1.1
 * @description
 * @Date of creation: 01-03-2006
 * @Last Modfied by : Saurabh gupta
 * @Last Modfied Date: 12-04-2006
 */
public class

FsHandleRemoteCopyServer implements FsRemoteCopyListener {

  static Stack clipBoard;

  private Logger logger;

  private String superRequestCode;

  private Object itemToCopyPaths[];

  private FsObject copySrcBaseObject;

  private String copySrcBaseObjectPath;

  private FsObject copyDestBaseObject;

  private String copyDestBaseObjectPath;

  private FsObject currDestObject;

  private String currDestObjectPath;

  private FsObject currSrcObject;

  private String currSrcObjectPath;

  private String currDestBaseObjectPath;

  private FsMessageSender fsMessageSender;

  private FsConnection fsConnection;

  private ServerUtil serverUtil;

  private boolean operationCancelled;

  private FsClientHandler fsClientHandler;

  public FsHandleRemoteCopyServer(String superRequestCode, FsClientHandler fsClientHandler) {
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
        handleRemoteCopy(fsRequest);
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
      handleRemoteCopy(fsRequest);
    }
  }

  private void handleRemoteCopy(FsRequest fsRequest) {
    FsResponse fsResponse = new FsResponse();
    int requestCode = fsRequest.getRequestCode();
    try {
      switch (requestCode) {
        case START :
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
          fsResponse.setResponseCode(STARTED);
          fsResponse.setOperation(FsRemoteOperationConstants.COPY);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);

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
        case OVERWRITE_OPTION_FOLDER :
          fsResponse = new FsResponse();
          fsResponse.setResponseCode(OVERWRITE_OPTION_FOLDER);
          fsResponse.setOperation(FsRemoteOperationConstants.COPY);
          fsResponse.setData(fsRequest.getData());
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case OVERWRITE_OPTION_FILE :
          fsResponse = new FsResponse();
          fsResponse.setResponseCode(OVERWRITE_OPTION_FILE);
          fsResponse.setOperation(FsRemoteOperationConstants.COPY);
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
          handleCopy(fsRequest.getSuperRequestCode());
          break;
        case OVERWRITE_FILE :
          logger.debug("Overwrite the file");

          logger.debug("currSrcObjectPath " + currSrcObjectPath);
          logger.debug("currDestBaseObjectPath " + currDestBaseObjectPath);

          FsFile currSrcFile = (FsFile)currSrcObject;
          fsResponse = new FsResponse();
          fsResponse.setData(currSrcObject.getPath());
          fsResponse.setData1(new Long(currSrcFile.getSize()));
          fsResponse.setOperation(FsRemoteOperationConstants.COPY);
          fsResponse.setResponseCode(NEXT_ITEM);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);

          fsConnection.copyItem(currDestBaseObjectPath, currSrcObjectPath);

          //handleCopy(fsRequest.getSuperRequestCode());
          break;
        case CANCEL :
          fsResponse = new FsResponse();
          fsResponse.setOperation(FsRemoteOperationConstants.COPY);
          fsResponse.setResponseCode(CANCEL);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case CANCLED :
          logger.debug(" Copy Cancelled ");
          fsClientHandler.removePropertyChangeListener(this);
        case NEXT_ITEM :
          handleCopy(fsRequest.getSuperRequestCode());
          break;
      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      fsClientHandler.removeRemoteCopyListener(this);
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      fsClientHandler.removeRemoteCopyListener(this);
    }
  }

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
        currDestBaseObjectPath =
          currDestObjectPath.substring(0, (currDestObjectPath.length() - currSrcObject.getName().length() - 1));
        logger.debug("currDestBaseObjectPath : " + currDestBaseObjectPath);
        logger.debug("currDestObjectPath : " + currDestObjectPath);


        currDestObject = fsConnection.findFsObjectByPath(currDestObjectPath);
        logger.debug("currSrcObject.getName() : " + currSrcObject.getName());

        if (currSrcObject instanceof FsFolder) {
          //check if this current folder exists in the current destination folder
          logger.debug("Folder......");
          if (currDestObject != null) {
            logger.debug("currDestObject.getName() : " + currDestObject.getName());
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
            fsResponse.setOperation(FsRemoteOperationConstants.COPY);
            fsResponse.setSuperResponseCode(superResponseCode);
            fsMessageSender.send(fsResponse);
          } else {
            //create folder in the destination location
            //find the path of the destination file
            fsConnection.createFolder(currDestBaseObjectPath, currSrcObject.getName());
            //push the content of the current folder in stack
            FsObject fsObjects[] = ((FsFolder)currSrcObject).listContentOfFolder();
            for (int index = 0; index < fsObjects.length; index++) {
              clipBoard.push(fsObjects[index].getPath());
            }
            handleCopy(superResponseCode);
          }
        } else {
          logger.debug("File........");
          //check for existance of destination file

          if (currDestObject != null && !currDestObjectPath.equals(currSrcObjectPath)) {
            logger.debug("currSrcObject.getName() : " + currDestObject.getName());
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
            fsResponse.setOperation(FsRemoteOperationConstants.COPY);
            fsResponse.setSuperResponseCode(superResponseCode);
            fsMessageSender.send(fsResponse);
          } else {
            //if the file does not exist then just copy 
            currSrcFile = (FsFile)currSrcObject;

            fsResponse = new FsResponse();
            fsResponse.setData(currSrcObject.getPath());
            fsResponse.setData1(new Long(currSrcFile.getSize()));
            fsResponse.setResponseCode(NEXT_ITEM);
            fsResponse.setOperation(FsRemoteOperationConstants.COPY);
            fsResponse.setSuperResponseCode(superResponseCode);
            fsMessageSender.send(fsResponse);
            fsConnection.copyItem(currDestBaseObjectPath, currSrcObjectPath);
          }
        }
      } catch (FsException fex) {
        logger.error(serverUtil.getStackTrace(fex));
        FsExceptionHolder fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);

        fsResponse = new FsResponse();
        fsResponse.setResponseCode(ERROR_MESSAGE);
        fsResponse.setOperation(FsRemoteOperationConstants.COPY);
        fsResponse.setSuperResponseCode(superResponseCode);
        fsResponse.setFsExceptionHolder(fsExceptionHolder);
        fsMessageSender.send(fsResponse);

        fsClientHandler.removeRemoteCopyListener(this);
        return;
      } catch (Exception ex) {
        logger.info("Failed To Copy");
        logger.error(serverUtil.getStackTrace(ex));
        return;
      }
    } else {
      logger.debug("Copy Operation Completed Successfully");

      fsResponse = new FsResponse();
      fsResponse.setResponseCode(COMPLETED);
      fsResponse.setData(copyDestBaseObjectPath);
      fsResponse.setOperation(FsRemoteOperationConstants.COPY);
      fsResponse.setSuperResponseCode(superResponseCode);
      fsMessageSender.send(fsResponse);

      fsClientHandler.removeRemoteCopyListener(this);
    }
  }

  private boolean isRoot(FsObject fsObject) {
    return ((fsObject.getPath().substring(fsObject.getPath().length() - 1)).equals(fsConnection.getSeperator()));
  }

  public String toString() {
    return this.toString();
  }

}

/*
public class FsHandleRemoteCopyServer implements PropertyChangeListener {

  static Stack clipBoard;

  private Logger logger;

  private String superRequestCode;

  private Object itemToCopyPaths[];

  private FsObject copySrcBaseObject;

  private String copySrcBaseObjectPath;

  private FsObject copyDestBaseObject;

  private String copyDestBaseObjectPath;

  private FsObject currDestObject;

  private String currDestObjectPath;

  private FsObject currSrcObject;

  private String currSrcObjectPath;

  private FsMessageSender fsMessageSender;

  private FsConnection fsConnection;

  private ServerUtil serverUtil;

  private int folderOverWriteValue;

  private int fileOverwriteValue;

  private boolean operationCancelled;

  private FsClientHandler fsClientHandler;

  public FsHandleRemoteCopyServer(String superRequestCode, FsClientHandler fsClientHandler) {
    this.logger = Logger.getLogger("ServerLogger");
    this.superRequestCode = superRequestCode;
    this.fsClientHandler = fsClientHandler;
    this.fsMessageSender = fsClientHandler.getFsMessageSender();
    this.fsConnection = fsClientHandler.getFsConnection();
    this.serverUtil = new ServerUtil(logger);
  }

  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(superRequestCode)) {
      if (evt.getPropertyName().equals("fsRequest")) {
        FsRequest fsRequest = (FsRequest)evt.getNewValue();
        handleRemoteCopy(fsRequest);
      }
      if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);

      }
    }
  }

  private void handleRemoteCopy(FsRequest fsRequest) {
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
            handleCopy(fsRequest.getSuperRequestCode());            
          } 
        } else {
            logger.debug("itemToCopyPaths is null");            
          } 
        break;
      case FsMessage.COPY_CREATED_FOLDER :
        //push the content of the current folder in stack
        FsObject fsObjects[] = ((FsFolder)currSrcObject).listContentOfFolder();
        for (int index = 0; index < fsObjects.length; index++) {
          clipBoard.push(fsObjects[index].getPath());
        }
        handleCopy(fsRequest.getSuperRequestCode());        
        break;  
      }
    }catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      fsClientHandler.removePropertyChangeListener(this);
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      fsClientHandler.removePropertyChangeListener(this);
    } 
  }
  
  private void handleCopy(String superResponseCode){
    logger.debug("In HandleCopy");
    logger.debug("super response code : " + superResponseCode);
    FsResponse fsResponse;
    FsFile currSrcFile;
    if (clipBoard.isEmpty()){
      logger.debug("Copy Operation Completed Successfully");
      fsResponse = new FsResponse();
      fsResponse.setResponseCode(FsMessage.REMOTE_ITEM_COPIED);
      fsResponse.setData(copyDestBaseObjectPath);
      fsResponse.setSuperResponseCode(superResponseCode);
      fsMessageSender.send(fsResponse);

      fsClientHandler.removePropertyChangeListener(this);      
    }else {
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
        if (currSrcObject instanceof FsFolder){
          logger.debug("Create folder");
          fsResponse.setResponseCode(FsRequest.COPY_CREATE_FOLDER);
          fsResponse.setSuperResponseCode(superRequestCode);
          fsMessageSender.send(fsResponse);
          logger.debug("Folder created");          
        } else{
          
          
          
          
          
        }
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      } catch (FsException fex) {
              logger.error(serverUtil.getStackTrace(fex));
              FsExceptionHolder fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
              fsResponse = new FsResponse();
              fsResponse.setResponseCode(FsMessage.REMOTE_COPY_FAILED);
              fsResponse.setSuperResponseCode(superResponseCode);
              fsResponse.setFsExceptionHolder(fsExceptionHolder);
              fsMessageSender.send(fsResponse);

              fsClientHandler.removePropertyChangeListener(this);
              return;
            } catch (Exception ex) {
              logger.info("Failed To Copy");
              logger.error(serverUtil.getStackTrace(ex));
              return;
            }
    }
  }

  private boolean isRoot(FsObject fsObject) {
    return ((fsObject.getPath().substring(fsObject.getPath().length() - 1)).equals(fsConnection.getSeperator()));
  }

  public String toString() {
    return this.toString();
  }

}  */