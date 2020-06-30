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
 * $Id: FsHandleSyncUploadServer.java,v 1.47 2006/08/04 05:56:21 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsSyncUploadListener;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsObject;

import dbsentry.plugin.cmsdk.CmsdkException;

import java.beans.PropertyChangeEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;

/**
 * To handle sync upload operation at server side.
 * @author Jeetendra Prasad
 * @version 1.0
 * Date of creation:    02-07-2005
 * Last Modfied by : Saurabh Gupta   
 * Last Modfied Date:  04-08-2006 
 */
public class FsHandleSyncUploadServer implements FsSyncUploadListener {
  private String superResponseCode;

  private Logger logger;

  private FsMessageSender fsMessageSender;

  private FsConnection fsConnection;

  private String absFilePathUpload;

  private String uploadBaseFolderPath;

  //private FileOutputStream fosUpload;

 // private FsFileHolder fsFileHolderUpload;

  private FsFileHolder fsFileHolder;

  //private String tempFileUpload;

  private Document document;

  private String syncOperation;

  private ServerUtil serverUtil;

  private CommonUtil commonUtil;

  private FsClientHandler fsClientHandler;

  private String clientMD5SumOfCurrentUploadFile = null;


  public FsHandleSyncUploadServer(String superResponseCode, FsClientHandler fsClientHandler) {
    this.logger = Logger.getLogger("ServerLogger");
    this.superResponseCode = superResponseCode;
    this.logger.debug("superResponseCode : " + superResponseCode);
    this.fsMessageSender = fsClientHandler.getFsMessageSender();
    this.fsConnection = fsClientHandler.getFsConnection();
    //this.tempFileUpload = "temp" + (new Random(new Date().getTime())).nextInt() + ".fs";
    this.serverUtil = new ServerUtil(logger);
    this.commonUtil = new CommonUtil(logger);
    this.fsClientHandler = fsClientHandler;
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
        handleUpload(fsRequest);
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
      handleUpload(fsRequest);
    }
  }

  /**
   * To handle the upload related requests of sync operation.
   * @param fsRequest FsRequest object containing upload related request.
   */
  public void handleUpload(FsRequest fsRequest) {
    FsResponse fsResponse;
    Object folders[];
    try {
      int requestCode = fsRequest.getRequestCode();
      switch (requestCode) {
        case START :
          document = commonUtil.getEmptyDocumentObject();
          syncOperation = (String)fsRequest.getData1();
          uploadBaseFolderPath = (String)fsRequest.getData();
          absFilePathUpload = "";
          fsResponse = new FsResponse();
          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
          fsResponse.setResponseCode(STARTED);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case COMPLETE :
          logger.info("Upload complete : ");
          fsResponse = new FsResponse();
          fsResponse.setData(commonUtil.getXMLStringFromDocument(document));
          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
          fsResponse.setResponseCode(COMPLETED);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          fsClientHandler.removeSyncUploadListener(this);
//          File fileTemp = new File(tempFileUpload);
//          if (fileTemp.exists()) {
//            fileTemp.delete();
//          }
          break;
        case FAILED :
          logger.info("Upload Failed");
          //propertyChangeSupport.firePropertyChange("fsHandleSyncUploadServer", null, this);
//          new File(tempFileUpload).delete();
          fsClientHandler.removeSyncUploadListener(this);
          break;
        case CREATE_FOLDER :
          {
            logger.debug("Create folder");
            folders = fsRequest.getDatas();

            String folderPathRelative = serverUtil.getRelativePath(fsConnection, folders);
            String absFolderPath = fsConnection.getAbsolutePath(folderPathRelative, uploadBaseFolderPath);
            logger.debug("absFolderPath : " + absFolderPath);
            fsResponse = new FsResponse();
            FsObject fsObject = fsConnection.findFsObjectByPath(absFolderPath);
            if (fsObject == null) {
              fsObject = fsConnection.createFolder(uploadBaseFolderPath, folderPathRelative);
              logger.debug("Folder created successfully");
            }
            fsObject = fsConnection.findFsObjectByPath(absFolderPath);
            serverUtil.addDocumentElement(document, fsObject, syncOperation);

            fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
            fsResponse.setResponseCode(FOLDER_CREATED);
            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            fsMessageSender.send(fsResponse);
          }
          break;
        case CREATE_SOCKET_4_SYNC_UPLOAD :
          {
            fsResponse = new FsResponse();
            FileInputStream fis;
            try {
              //fsFileHolderUpload = (FsFileHolder)fsRequest.getData();
              //clientMD5SumOfCurrentUploadFile = (String)fsRequest.getData1();
              folders = fsRequest.getDatas();
              String filePathRelative = serverUtil.getRelativePath(fsConnection, folders);
              this.absFilePathUpload = fsConnection.getAbsolutePath(filePathRelative, uploadBaseFolderPath);
              logger.debug("absFilePath : " + this.absFilePathUpload);
//              FsObject fsObject = fsConnection.findFsObjectByPath(this.absFilePathUpload);
//              if (fsObject != null) {
//                Object obj[] = { absFilePathUpload };
//                fsConnection.deleteItems(obj);
//              }
              //fileTemp = new File(tempFileUpload);
//              if (fileTemp.exists()) {
//                fileTemp.delete();
//              }
              //fileTemp.createNewFile();
              //fis = new FileInputStream(new File(tempFileUpload));
              //fsConnection.createFile(absFilePathUpload, fis, fsFileHolderUpload.getMimeType());
              //fsConnection.deleteItem(absFilePathUpload);
              //fosUpload = new FileOutputStream(fileTemp);
              fsResponse.setData(absFilePathUpload);
              fsResponse.setData1(syncOperation);
              fsResponse.setResponseCode(SOCKET_4_SYNC_UPLOAD_CREATED);
              //fis.close();
            } catch (CmsdkException cmsdkException) {
              fsResponse.setResponseCode(FAILED);
              String errorMessage = "Unable to create new file " + absFilePathUpload + ",permission denied";
              fsResponse.setData(errorMessage);
            } catch (Exception ex) {
              fsResponse.setResponseCode(FAILED);
              fsResponse.setData(ex.getMessage());
              logger.error(serverUtil.getStackTrace(ex));
            }
            fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            fsMessageSender.send(fsResponse);
          }
          break;
        case STOP :
          {
            logger.debug("Sync Upload STOP");
//            if (fosUpload != null) {
//              fosUpload.close();
//            }

            //fsConnection.deleteItem(absFilePathUpload);
//            if (tempFileUpload != null) {
//              new File(tempFileUpload).delete();
//            }

            fsResponse = new FsResponse();
            fsResponse.setResponseCode(STOPPED);
            fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            fsMessageSender.send(fsResponse);
            //propertyChangeSupport.firePropertyChange("fsHandleSyncUploadServer", null, this);
            fsClientHandler.removeSyncUploadListener(this);
          }
          break;
        case APPEND_TO_FILE :
//          fsResponse = new FsResponse();
//          fsFileHolder = (FsFileHolder)fsRequest.getData();
//          fosUpload.write(fsFileHolder.getData(), 0, (int)fsFileHolder.getSize());
//          fsResponse.setResponseCode(APPENDED_TO_FILE);
//          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
//          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
//          fsMessageSender.send(fsResponse);
          break;
        case SOCKET_CLOSE_FILE:
          document = (Document)fsRequest.getData1();
          Document documentOfChanges = (Document)fsRequest.getData();
          fsResponse = new FsResponse();
          fsResponse.setResponseCode(FILE_CLOSED);
          fsResponse.setData(documentOfChanges);
          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case CLOSE_FILE :
//          fsResponse = new FsResponse();
//          try {
//            //fosUpload.close();
//            String serverMD5SumOfCurrentUploadFile = commonUtil.generateMD5Sum(new FileInputStream(tempFileUpload));
//            logger.debug("serverMD5SumOfCurrentUploadFile " + serverMD5SumOfCurrentUploadFile);
//
//            logger.debug("clientMD5SumOfCurrentUploadFile " + clientMD5SumOfCurrentUploadFile);
//            if (serverMD5SumOfCurrentUploadFile.equals(clientMD5SumOfCurrentUploadFile)) {
//              logger.debug("File Transfer is successful");
//
//              FileInputStream fis = new FileInputStream(new File(tempFileUpload));
//              FsFile fsFile = fsConnection.createFile(absFilePathUpload, fis, fsFileHolderUpload.getMimeType());
//              fis.close();
//              serverUtil.addDocumentElement(document, fsFile, syncOperation);
//              fsResponse.setResponseCode(FILE_CLOSED);
//              logger.debug("File Created");
//            } else {
//              logger.debug("File Transfer is not successful due to Curruption");
//
//              fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
//              fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
//              fsResponse.setResponseCode(FILE_CURRUPTED);
//              fsMessageSender.send(fsResponse);
//            }
//            //new File(tempFileUpload).delete();
//            serverMD5SumOfCurrentUploadFile = null;
//            clientMD5SumOfCurrentUploadFile = null;
//
//          } catch (CmsdkException cmsdkException) {
//            fsResponse.setResponseCode(FAILED);
//            fsResponse.setData(cmsdkException.getStackTraceString());
//            cmsdkException.getErrorCode();
//          } catch (Exception ex) {
//            fsResponse.setResponseCode(FAILED);
//            fsResponse.setData(ex.getMessage());
//            logger.error(serverUtil.getStackTrace(ex));
//          }
//          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
//          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
//          fsMessageSender.send(fsResponse);
          break;
      }
    } catch (FsException fex) {
      try {
//        if (fosUpload != null) {
//          fosUpload.close();
//        }
      } catch (Exception e) {
        ;
      }

      //fsConnection.deleteItem(absFilePathUpload);
//      if (tempFileUpload != null) {
//        new File(tempFileUpload).delete();
//      }


      logger.error(serverUtil.getStackTrace(fex));
      fsResponse = new FsResponse();
      fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
      fsResponse.setResponseCode(ERROR_MESSAGE);
      fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
      fsMessageSender.send(fsResponse);

      //propertyChangeSupport.firePropertyChange("fsHandleSyncUploadServer", null, this);
      fsClientHandler.removeSyncUploadListener(this);
    } catch (Exception ex) {
      try {
//        if (fosUpload != null) {
//          fosUpload.close();
//        }
      } catch (Exception e) {
        ;
      }

      //fsConnection.deleteItem(absFilePathUpload);
//      if (tempFileUpload != null) {
//        new File(tempFileUpload).delete();
//      }

      logger.error(serverUtil.getStackTrace(ex));

      fsResponse = new FsResponse();
      fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
      fsResponse.setResponseCode(ERROR_MESSAGE);
      fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
      fsMessageSender.send(fsResponse);

      //propertyChangeSupport.firePropertyChange("fsHandleSyncUploadServer", null, this);
      fsClientHandler.removeSyncUploadListener(this);
    }
  }


  /* public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
    }
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }*/

  /**
  * gives String representation of FsHandleSyncUploadServer object.
  * @return String repersentation of FsHandleSyncUploadServer object.
  */
  public String toString() {
    return this.toString();
  }

}
