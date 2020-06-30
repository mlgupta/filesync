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
 * $Id: FsHandleDownloadServer.java,v 1.35 2006/09/08 09:13:04 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsFolderHolder;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsDownloadListener;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;


import java.beans.PropertyChangeEvent;

import java.io.InputStream;

import java.util.Date;
import java.util.Stack;

import org.apache.log4j.Logger;


/**
 * To handle download operation at server side.
 * @author Jeetendra Prasad
 * @version 1.2
 * Date of creation:    7-05-2005
 * Last Modfied by :   Saurabh gupta 
 * Last Modfied Date: 01-08-2006  
 */
public class

FsHandleDownloadServer implements FsDownloadListener {

  private long downloadStartTime;

  private Logger logger;

  private String superResponseCode;

  private FsMessageSender fsMessageSender;

  private FsConnection fsConnection;

  private String downloadBaseFolderPath;

  private Stack itemsToDownload;

  private FsObject downloadFsObject;

  private ServerUtil serverUtil;

  private CommonUtil commonUtil = null;

  private Long totalSizeDownload;

  private FsClientHandler fsClientHandler;

  private int overWriteValueDownload;

  //private InputStream downloadFileInputStream;

  private FsFile downloadFile;

  private FsFileHolder fsFileHolder;

  private FsExceptionHolder fsExceptionHolder;

  private String itemPath;

  public FsHandleDownloadServer(String superResponseCode, FsClientHandler fsClientHandler) {
    this.logger = Logger.getLogger("ServerLogger");
    this.superResponseCode = superResponseCode;
    this.fsClientHandler = fsClientHandler;
    this.fsMessageSender = fsClientHandler.getFsMessageSender();
    this.fsConnection = fsClientHandler.getFsConnection();
    this.itemsToDownload = new Stack();
    this.serverUtil = new ServerUtil(logger);
    this.commonUtil = new CommonUtil(logger);
  }

  /**
   * To listen the PropertyChangeEvent.
   * @param evt PropertyChangeEvent.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    logger.debug("Property Name as super Request Code :" + evt.getPropertyName());
    logger.debug("super Response Code : " + superResponseCode);
    if (evt.getPropertyName().equals(superResponseCode)) {
      FsRequest fsRequest = (FsRequest)evt.getNewValue();
      handleDownload(fsRequest);
    }
  }
  /*public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(superResponseCode)) {
      if (evt.getPropertyName().equals("fsRequest")) {
        FsRequest fsRequest = (FsRequest)evt.getNewValue();
        handleDownloadRequest(fsRequest);
      }
      if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);
        //handle exception
      } else if (evt.getPropertyName().equals("operationCancelled")) {
            downloadFileRecursively.stopDownloadThread = true;
      }
    }
  }*/

  /**
   * To handle the download related requests.
   * @param fsRequest FsRequest object containing download related request.
   */
  private void handleDownload(FsRequest fsRequest) {

    FsResponse fsResponse = null;
    //String itemPath; 
    int fsRequestValue = fsRequest.getRequestCode();
    this.overWriteValueDownload = 0;
    try {
      switch (fsRequestValue) {
        case START :
          downloadStartTime = (new Date()).getTime();
          downloadBaseFolderPath = (String)fsRequest.getData();
          fsResponse = new FsResponse();
          Object itemToDownloadPaths[] = fsRequest.getDatas();
          if (itemToDownloadPaths != null) {
            int itemCount = itemToDownloadPaths.length;
            for (int index = 0; index < itemCount; index++) {
              itemsToDownload.push(itemToDownloadPaths[index]);
            }
          }
          totalSizeDownload = new Long(fsConnection.calculateFolderDocSize(itemToDownloadPaths));

          fsResponse.setData(totalSizeDownload);
          fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
          fsResponse.setResponseCode(STARTED);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          downloadFile(fsRequest.getSuperRequestCode());
          break;
        case FOLDER_CREATED :
          FsFolder fsFolder = (FsFolder)downloadFsObject;
          logger.debug("Folder Path : " + fsFolder.getPath());
          FsObject fsObjects[] = fsFolder.listContentOfFolder();
          int itemCount = fsObjects.length;
          logger.debug("itemCount : " + itemCount);
          for (int index = 0; index < itemCount; index++) {
            logger.debug("fsObjects[index].getPath() : " + fsObjects[index].getPath());
            itemsToDownload.push(fsObjects[index].getPath());
          }
          downloadFile(fsRequest.getSuperRequestCode());
          break;
        case OVERWRITE_FOLDER :
          this.overWriteValueDownload = ((Integer)fsRequest.getData()).intValue();
          if (overWriteValueDownload == OVERWRITE_CANCEL) {
            logger.info("Download Cancelled");
            itemsToDownload.clear();
            fsResponse = new FsResponse();
            fsResponse.setResponseCode(CANCLED);
            fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            fsMessageSender.send(fsResponse);
            fsClientHandler.removeDownloadListener(this);
          } else if (overWriteValueDownload == OVERWRITE_NO) {
            downloadFile(fsRequest.getSuperRequestCode());
          } else if (overWriteValueDownload == OVERWRITE_YES || overWriteValueDownload == OVERWRITE_YES_TO_ALL) {
            fsResponse = new FsResponse();
            fsResponse.setResponseCode(OVERWRITE_FOLDER);
            fsResponse.setData(overWriteValueDownload);
            fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            fsMessageSender.send(fsResponse);
          }
          break;
        case OVERWRITE_FILE :
          this.overWriteValueDownload = ((Integer)fsRequest.getData()).intValue();
          if (overWriteValueDownload == OVERWRITE_CANCEL) {
            logger.info("Download Cancelled");
            itemsToDownload.clear();
            fsResponse = new FsResponse();
            fsResponse.setResponseCode(CANCLED);
            fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            fsMessageSender.send(fsResponse);
            fsClientHandler.removeDownloadListener(this);
          } else if (overWriteValueDownload == OVERWRITE_NO) {
            fsResponse = new FsResponse();
            fsResponse.setResponseCode(OVERWRITE_NO);
            fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            fsMessageSender.send(fsResponse);
          
            downloadFile(fsRequest.getSuperRequestCode());
          } else if (overWriteValueDownload == OVERWRITE_YES || overWriteValueDownload == OVERWRITE_YES_TO_ALL) {
            fsResponse = new FsResponse();
            fsResponse.setResponseCode(OVERWRITE_FILE);
            fsResponse.setData2(itemPath);
            fsResponse.setData(((Integer)fsRequest.getData()).intValue());
            fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            logger.debug("Inside OVERWRITE_FILE before sending..... from server side............");
            fsMessageSender.send(fsResponse);
          }
          break;
        case EMPTY_FILE_CREATED :
         // downloadFileInputStream = downloadFile.getInputStream();
          //BREAK REMOVED INTENTIONALLY TO EXECUTE FOLLOWING CODE
        case APPENDED_TO_FILE :
//          fsFileHolder = new FsFileHolder();
//          fsResponse = new FsResponse();
//          int byteRead;
//          byte datas[] = new byte[commonUtil.JXTA_BUFFER_SIZE];
//          try {
//            //byteRead = downloadFileInputStream.read(datas);
//            if (byteRead != -1) {
//              fsFileHolder.setSize(byteRead);
//              fsFileHolder.setData(datas);
//              fsResponse.setData(fsFileHolder);
//              fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//              fsResponse.setResponseCode(APPEND_TO_FILE);
//              fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
//              fsMessageSender.send(fsResponse);
//            } else {
//              //downloadFileInputStream.close();
//              fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//              fsResponse.setResponseCode(CLOSE_FILE);
//              fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
//              fsMessageSender.send(fsResponse);
//            }
//          } catch (Exception ex) {
//            logger.info("Download Failure");
//            logger.error(serverUtil.getStackTrace(ex));
//            if (itemsToDownload != null) {
//              itemsToDownload.clear();
//            }
////            if (downloadFileInputStream != null) {
////              downloadFileInputStream.close();
////            }
//
//            fsExceptionHolder = serverUtil.getFsExceptionHolder(ex);
//            fsExceptionHolder.setErrorMessage(ex.getMessage());
//            fsResponse.setFsExceptionHolder(fsExceptionHolder);
//            fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//            fsResponse.setResponseCode(ERROR_MESSAGE);
//            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
//            fsMessageSender.send(fsResponse);
//            fsClientHandler.removeDownloadListener(this);
//          }
          break;
        case SOCKET_CLOSE_FILE:
          try {
            //downloadFileInputStream.close();
          } catch (Exception ex) {
            logger.error(ex.getMessage());
          }
          logger.debug("File closed");
          downloadFile(fsRequest.getSuperRequestCode());
          break;
        
//        case SOCKET_CLOSE_FILE :
//          fsResponse = new FsResponse();
//          fsResponse.setResponseCode(CLOSE_FILE);
//          fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
//          fsMessageSender.send(fsResponse);
//          break;
        case CANCEL :
          logger.info("Download canceled");
          itemsToDownload.clear();
          //downloadFileInputStream.close();

          fsResponse = new FsResponse();
          fsResponse.setResponseCode(CANCLED);
          fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          fsClientHandler.removeDownloadListener(this);
          break;
        case FAILED :
          logger.info("Download failure");
          itemsToDownload.clear();
//          if (downloadFileInputStream != null) {
//            downloadFileInputStream.close();
//          }
          fsClientHandler.removeDownloadListener(this);
          break;
      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      fsClientHandler.removeDownloadListener(this);
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      fsClientHandler.removeDownloadListener(this);
    }
  }


  /**
   * Handles download related possiblities.
   * @param superResponseCode String to be sent back to client to distinguish among the responses.
   */
  private void downloadFile(String superResponseCode) {
    FsResponse fsResponse = new FsResponse();
    //String itemPath;
    long downloadEndTime;
    try {
      if (itemsToDownload.empty()) {
        downloadEndTime = new Date().getTime();
        long totalTime = (downloadEndTime - downloadStartTime) / 1000;

        logger.info("Download Complete");
        if (totalTime > 0) {
          logger.debug("Download speed : " + (totalSizeDownload.longValue() / 1024) / totalTime + " Kbps.");
        }
        fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
        fsResponse.setResponseCode(COMPLETED);
        fsResponse.setSuperResponseCode(superResponseCode);
        fsMessageSender.send(fsResponse);
        fsClientHandler.removeDownloadListener(this);
      } else {
        itemPath = (String)itemsToDownload.pop();
        logger.debug("itemPath : " + itemPath);
        downloadFsObject = fsConnection.findFsObjectByPath(itemPath);
        if (downloadFsObject instanceof FsFolder) {
          logger.debug("Create folder");
          FsFolder fsFolder = (FsFolder)downloadFsObject;
          logger.debug("downloadBaseFolderPath : " + downloadBaseFolderPath);
          String relativeFolderPath = fsConnection.getRelativePath(fsFolder.getPath(), downloadBaseFolderPath);
          logger.debug("relativeFolderPath : " + relativeFolderPath);
          FsFolderHolder fsFolderHolder = new FsFolderHolder();
          fsFolderHolder.setName(fsFolder.getName());
          fsFolderHolder.setPath(fsFolder.getPath());
          fsFolderHolder.setOwner(fsFolder.getOwner());
          fsFolderHolder.setCreationDate(fsFolder.getCreationDate());
          fsFolderHolder.setModifiedDate(fsFolder.getModifiedDate());
          fsFolderHolder.setDescription(fsFolder.getDescription());

          fsResponse.setData(fsFolderHolder);
          fsResponse.setDatas(relativeFolderPath.split(fsConnection.getSeperator()));
          fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
          fsResponse.setResponseCode(CREATE_FOLDER);
          fsResponse.setSuperResponseCode(superResponseCode);
          fsMessageSender.send(fsResponse);
        } else if (downloadFsObject instanceof FsFile) {

          logger.debug("File download started");
          FsFile fsFile = (FsFile)downloadFsObject;
          logger.debug("File Path : " + fsFile.getPath());
          String relativeFilePath = fsConnection.getRelativePath(fsFile.getPath(), downloadBaseFolderPath);
          logger.debug("relativeFilePath : " + relativeFilePath);
          fsResponse.setDatas(relativeFilePath.split(fsConnection.getSeperator()));

          FsFileHolder fsFileHolder = new FsFileHolder();
          fsFileHolder.setName(fsFile.getName());
          fsFileHolder.setPath(fsFile.getPath());
          fsFileHolder.setOwner(fsFile.getOwner());
          fsFileHolder.setCreationDate(fsFile.getCreationDate());
          fsFileHolder.setModifiedDate(fsFile.getModifiedDate());
          fsFileHolder.setDescription(fsFile.getDescription());
          fsFileHolder.setMimeType(fsFile.getMimeType());
          fsFileHolder.setSize(fsFile.getSize()); 
        /*  downloadFile = fsFile;

          fsResponse.setData(fsFileHolder);
          fsResponse.setData2(commonUtil.generateMD5Sum(fsFile.getInputStream()));
          fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
          fsResponse.setResponseCode(CREATE_EMPTY_FILE);
          fsResponse.setSuperResponseCode(superResponseCode);
          fsMessageSender.send(fsResponse);*/

          fsResponse.setData2(itemPath);
          fsResponse.setData(fsFileHolder);
          //fsResponse.setData2(commonUtil.generateMD5Sum(fsFile.getInputStream()));
          fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
          fsResponse.setResponseCode(CREATE_SOCKET_4_DOWNLOAD_FILE);
          fsResponse.setSuperResponseCode(superResponseCode);
          fsMessageSender.send(fsResponse);
        }
      }
    } catch (FsException fex) {
      logger.info("Download Failure");
      logger.error(serverUtil.getStackTrace(fex));
      fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
      fsExceptionHolder.setErrorMessage(fex.getMessage());
      fsResponse.setFsExceptionHolder(fsExceptionHolder);
      fsResponse.setOperation(FsRemoteOperationConstants.DOWNLOAD);
      fsResponse.setResponseCode(ERROR_MESSAGE);
      fsResponse.setSuperResponseCode(superResponseCode);
      fsMessageSender.send(fsResponse);
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
    }
  }


  /**
   * adds a PropertyChangeListener which will listen to the PropertyChangeEvent
   * fired by this class.
   * @param propertyChangeListener listener class which will listen to the PropertyChangeEvent.
   */
  /* 
  public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    
    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
    }
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
    
  }
  */

  /**
   * gives String representation of FsHandleDownloadServer object.
   * @return String repersentation of FsHandleDownloadServer object.
   */
  public String toString() {
    return this.toString();
  }

}
