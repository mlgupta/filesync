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
 * $Id: FsHandleSyncDownloadServer.java,v 1.45 2006/08/04 13:01:51 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsSyncDownloadListener;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;

import java.beans.PropertyChangeEvent;

import java.io.InputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;


/**
 * To handle sync download operation at server side.
 * @author Jeetendra Prasad
 * @version 1.0
 * Date of creation:    02-07-2005
 * Last Modfied by :  Saurabh Gupta  
 * Last Modfied Date: 03-04-2006  
 */
public class FsHandleSyncDownloadServer implements FsSyncDownloadListener {

  private Logger logger;

  private String superResponseCode;

  private FsMessageSender fsMessageSender;

  private FsConnection fsConnection;

  private LinkedList itemsToDownload;

  private String downloadBaseFolderPath;

  //private InputStream downloadFileInputStream;

  private FsFile downloadFile;

  private FsFileHolder fsFileHolder;

  private String itemToDownloadPath;

  private String syncOperation;

  private Document document;

  private FsObject fsObjectUnderOperation;

  private CommonUtil commonUtil;

  private ServerUtil serverUtil;

  private FsClientHandler fsClientHandler;


  public FsHandleSyncDownloadServer(String superResponseCode, FsClientHandler fsClientHandler) {
    this.logger = Logger.getLogger("ServerLogger");
    this.superResponseCode = superResponseCode;
    this.fsClientHandler = fsClientHandler;
    this.fsMessageSender = fsClientHandler.getFsMessageSender();
    this.fsConnection = fsClientHandler.getFsConnection();
    this.itemsToDownload = new LinkedList();
    this.serverUtil = new ServerUtil(logger);
    this.commonUtil = new CommonUtil(logger);
    this.document = commonUtil.getEmptyDocumentObject();
  }

  /**
   * handles the PropertyChangeEvent.
   * @param evt PropertyChangeEvent object.
   */
  public
  /* public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(superResponseCode)) {
      if (evt.getPropertyName().equals("fsRequest")) {
        FsRequest fsRequest = (FsRequest)evt.getNewValue();
        handleDownloadRequest(fsRequest);
      }
      if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);
        //handle exception
      }
    }
  }*/
  void propertyChange(PropertyChangeEvent evt) {
    logger.debug("Property Name as super Request Code :" + evt.getPropertyName());
    logger.debug("super Request Code :" + superResponseCode);
    if (evt.getPropertyName().equals(superResponseCode)) {
      FsRequest fsRequest = (FsRequest)evt.getNewValue();
      handleDownloadRequest(fsRequest);
    }
  }

  /**
   * To handle the download related requests of sync operation.
   * @param fsRequest FsRequest object containing download related request.
   */
  private void handleDownloadRequest(FsRequest fsRequest) {
    FsResponse fsResponse = null;
    int fsRequestValue = fsRequest.getRequestCode();
    try {
      switch (fsRequestValue) {
        case START :
          fsResponse = new FsResponse();
          downloadBaseFolderPath = (String)fsRequest.getData();
          itemToDownloadPath = (String)fsRequest.getData1();
          syncOperation = (String)fsRequest.getData2();
          itemsToDownload.add(itemToDownloadPath);
          Long fileSize = new Long(fsConnection.calculateFolderDocSize(itemsToDownload.toArray()));
          fsResponse.setData(fileSize);
          fsResponse.setResponseCode(STARTED);
          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          handleDownload(fsRequest.getSuperRequestCode());
          break;
        case FOLDER_CREATED :
          serverUtil.addDocumentElement(document, fsObjectUnderOperation, syncOperation);
          handleDownload(fsRequest.getSuperRequestCode());
          break;
        case EMPTY_FILE_CREATED :
//          downloadFileInputStream = downloadFile.getInputStream();
          //BREAK REMOVED INTENTIONALLY TO EXECUTE FOLLOWING CODE
        case APPENDED_TO_FILE :
//          fsFileHolder = new FsFileHolder();
//          fsResponse = new FsResponse();
//          int byteRead;
//          byte datas[] = new byte[CommonUtil.JXTA_BUFFER_SIZE];
//          byteRead = downloadFileInputStream.read(datas);
//          if (byteRead != -1) {
//            fsFileHolder.setSize(byteRead);
//            fsFileHolder.setData(datas);
//            fsResponse.setData(fsFileHolder);
//            fsResponse.setResponseCode(APPEND_TO_FILE);
//            fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
//            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
//            fsMessageSender.send(fsResponse);
//          } else {
//            downloadFileInputStream.close();
//            fsResponse.setResponseCode(CLOSE_FILE);
//            fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
//            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
//            fsMessageSender.send(fsResponse);
//          }
          break;
        case SOCKET_CLOSE_FILE:
          logger.debug("respond to closing file....");
          fsResponse = new FsResponse();
          fsResponse.setResponseCode(CLOSE_FILE);
          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case STOP :
          logger.debug("Sync Download STOP");
//          if (downloadFileInputStream != null) {
//            downloadFileInputStream.close();
//          }
          fsResponse = new FsResponse();
          fsResponse.setResponseCode(STOPPED);
          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          fsClientHandler.removeSyncDownloadListener(this);
          break;
        case FILE_CLOSED :
          //downloadFileInputStream.close();
          serverUtil.addDocumentElement(document, fsObjectUnderOperation, syncOperation);
          logger.debug("File closed");
          handleDownload(fsRequest.getSuperRequestCode());
          break;
        case FAILED :
          logger.debug("Download failure");
          itemsToDownload.clear();
          fsClientHandler.removeSyncDownloadListener(this);
          break;
      }
    } catch (FsException fex) {
      try {
//        if (downloadFileInputStream != null) {
//          downloadFileInputStream.close();
//        }
      } catch (Exception e) {
        ;
      }
      itemsToDownload.clear();
      logger.error(serverUtil.getStackTrace(fex));
      fsResponse = new FsResponse();
      fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
      fsResponse.setResponseCode(ERROR_MESSAGE);
      fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
      fsMessageSender.send(fsResponse);
      fsClientHandler.removeSyncDownloadListener(this);
    } catch (Exception ex) {
      try {
//        if (downloadFileInputStream != null) {
//          downloadFileInputStream.close();
//        }
      } catch (Exception e) {
        ;
      }
      logger.error(serverUtil.getStackTrace(ex));
      fsResponse = new FsResponse();
      fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
      fsResponse.setResponseCode(ERROR_MESSAGE);
      fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
      fsMessageSender.send(fsResponse);
      fsClientHandler.removeSyncDownloadListener(this);
    }
  }

  /**
   * Handles download related possiblities.
   * @param superResponseCode String to be sent back to client to distinguish among the responses.
   */
  private void handleDownload(String superResponseCode) {
    FsResponse fsResponse = new FsResponse();
    String itemPath;
    FsFolder fsFolder;
    try {
      if (itemsToDownload.size() == 0) {
        fsResponse.setData(commonUtil.getXMLStringFromDocument(document));
        logger.info("Download Complete");
        fsResponse.setResponseCode(COMPLETED);
        fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
        fsResponse.setSuperResponseCode(superResponseCode);
        fsMessageSender.send(fsResponse);
        fsClientHandler.removeSyncDownloadListener(this);
      } else {
        itemPath = (String)itemsToDownload.removeFirst();
        fsObjectUnderOperation = fsConnection.findFsObjectByPath(itemPath);
        if (fsObjectUnderOperation instanceof FsFolder) {
          logger.debug("Create folder");
          fsFolder = (FsFolder)fsObjectUnderOperation;
          logger.debug("Folder Path : " + fsFolder.getPath());

          FsObject fsObjects[] = fsFolder.listContentOfFolder();
          Arrays.sort(fsObjects, new FsObjectComparator());
          int itemCount = fsObjects.length;
          logger.debug("itemCount : " + itemCount);
          for (int index = 0; index < itemCount; index++) {
            logger.debug("fsObjects[index].getPath() : " + fsObjects[index].getPath());
            itemsToDownload.add(fsObjects[index].getPath());
          }

          logger.debug("downloadBaseFolderPath : " + downloadBaseFolderPath);
          String relativeFolderPath = fsConnection.getRelativePath(fsFolder.getPath(), downloadBaseFolderPath);
          logger.debug("relativeFolderPath : " + relativeFolderPath);
          fsResponse.setDatas(relativeFolderPath.split(fsConnection.getSeperator()));

          fsResponse.setResponseCode(CREATE_FOLDER);
          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
          fsResponse.setSuperResponseCode(superResponseCode);
          fsMessageSender.send(fsResponse);
          logger.debug("Folder created");
        } else if (fsObjectUnderOperation instanceof FsFile) {

          logger.debug("File download started");
          FsFile fsFile = (FsFile)fsObjectUnderOperation;
          logger.debug("File Path : " + fsFile.getPath());
          logger.debug("downloadBaseFolderPath : " + downloadBaseFolderPath);
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
         /* downloadFile = fsFile;

          fsResponse.setData(fsFileHolder);
          fsResponse.setData2(commonUtil.generateMD5Sum(fsFile.getInputStream()));
          fsResponse.setResponseCode(CREATE_EMPTY_FILE);
          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
          fsResponse.setSuperResponseCode(superResponseCode);
          fsMessageSender.send(fsResponse);*/
          fsResponse.setData(fsFileHolder);
          fsResponse.setData2(itemPath);
          fsResponse.setResponseCode(CREATE_SOCKET_4_SYNC_DOWNLOAD_FILE);
          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
          fsResponse.setSuperResponseCode(superResponseCode);
          fsMessageSender.send(fsResponse);
        }
      }
    } catch (FsException fex) {
      logger.info("Download Failure");
      fsResponse.setResponseCode(FAILED);
      fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
      fsResponse.setSuperResponseCode(superResponseCode);
      fsMessageSender.send(fsResponse);
    }
  }


  /* public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
    }
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  } */

  /**
   * gives String representation of FsHandleSyncDownloadServer object.
   * @return String repersentation of FsHandleSyncDownloadServer object.
   */
  public String toString() {
    return this.toString();
  }

}
