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
 * $Id: FsHandleSyncDownloadClient.java,v 1.47 2006/08/04 13:01:45 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.client;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.enumconstants.EnumSyncOperation;
import dbsentry.filesync.common.listeners.FsSyncDownloadListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;


/**
 *	Purpose: To Handle sync download at client side.Sends and receive all requests and responses 
 *  related to download. 
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    6-05-2005
 * 	Last Modfied by :    Saurabh Gupta
 * 	Last Modfied Date:   03-04-2006
 */
public class

FsHandleSyncDownloadClient implements FsSyncDownloadListener{
  private Logger logger;

  private FsMessageSender fsMessageSender;

  private String superRequestCode;

  private PropertyChangeSupport  propertyChangeSupport4FsSyncDownload;

  private File fileDownload;

 // private FileOutputStream downloadFileOutputStream;

  private long totalByteToTransfer;

  private long totalByteTransfered;

  private String syncOperation;

  private Document documentOfChanges;

  private File currentFolderLocalFile;

  private String currentFolderRemotePath;

  private CommonUtil commonUtil;

  private ClientUtil clientUtil;

  private String itemToDownload;

  //private boolean stopSync = false;
  
  private FsServerHandler fsServerHandler;
  
  private String serverMD5SumOfCurrentDownloadFile=null;
  
  //private File fileDownloadBackup=null;
  
  private String destFilePathToSyncDownload=null;
  
  private FsSocketClient fsSocketClient;


  /**
   * A constructor to initialize FsHandleSyncDownloadClient.
   * @param logger logger object
   * @param syncOperation sync operation type. refer dbsentry.filesync.common.EnumSyncOperation
   * @param itemToDownload folder or file uo download
   * @param currentFolderLocalFile local base folder
   * @param currentFolderRemotePath remote base folder
   * @param superRequestCode a token indicating the thread which will handle the response
   */
  public FsHandleSyncDownloadClient(Logger logger, FsServerHandler fsServerHandler, String syncOperation, String itemToDownload, File currentFolderLocalFile,
                                    String currentFolderRemotePath, String superRequestCode) {

    this.commonUtil = new CommonUtil(logger);
    this.clientUtil = new ClientUtil(logger);
    
    this.logger = logger;
    this.syncOperation = syncOperation;
    this.superRequestCode = superRequestCode;
    this.currentFolderLocalFile = currentFolderLocalFile;
    this.currentFolderRemotePath = currentFolderRemotePath;
    this.itemToDownload = itemToDownload;
    this.documentOfChanges = commonUtil.getEmptyDocumentObject(); 
    this.fsServerHandler=fsServerHandler;
    this.fsMessageSender = fsServerHandler.getFsMessagesender();
    this.fsSocketClient=fsServerHandler.getFsSocketClient();
  }

  /**
   * start the download process.
   */
  public void startDownload() {
    logger.info("Sync Download Started");

    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(currentFolderRemotePath);
    fsRequest.setData1(itemToDownload);
    fsRequest.setData2(syncOperation);
    fsRequest.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
    fsRequest.setRequestCode(START);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsMessageSender.send(fsRequest);
  }

  /**
   * property change listener.
   * @param evt PropertyChangeEvent object
   */
  /*public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(superRequestCodeForDownload)) {
      if (evt.getPropertyName().equals("fsResponse")) {
        FsResponse fsResponse = (FsResponse)evt.getNewValue();
        handleDownload(fsResponse);
      }
      if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);
        //handle exception
      }
    }
  }*/
   public void propertyChange(PropertyChangeEvent evt) {
     int propertyName=Integer.valueOf(evt.getPropertyName());
     FsResponse fsResponse = (FsResponse)evt.getNewValue();
     if(fsResponse.getSuperResponseCode().equals(superRequestCode)){
       handleDownload( propertyName,fsResponse);
     }
   }  


  /**
   * handles download
   * @param fsResponse fsResponse object
   */
  private void handleDownload(int propertyName, FsResponse fsResponse) {
    int responseCode = fsResponse.getResponseCode();
    FsRequest fsRequest;
    FsExceptionHolder fsExceptionHolder;
    try {
      switch (propertyName) {
      case STARTED:
        totalByteToTransfer = ((Long)fsResponse.getData()).longValue();
        break;
      case COMPLETED:
        logger.debug("Sync DownLoad Complete");
        //fsResponse.setData contains remote xml data
        fsResponse.setData1(commonUtil.getXMLStringFromDocument(documentOfChanges));
        propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeSyncDownloadListener(this);
        break;
      case CREATE_FOLDER:
        logger.debug("response from server to create a folder");
        fileDownload = new File(currentFolderLocalFile.getAbsolutePath());
        Object folders[] = fsResponse.getDatas();
        String folderPathRelative = clientUtil.getRelativePath(folders);
        String absFolderPath =
          clientUtil.getAbsolutePath(folderPathRelative, currentFolderLocalFile.getAbsolutePath());
        logger.debug("absFolderPath : " + absFolderPath);
        fileDownload = new File(absFolderPath);
        fsRequest = new FsRequest();
        fileDownload.mkdir();
        syncOperation = EnumSyncOperation.NEW_FOLDER;
        clientUtil.addDocumentElement(documentOfChanges, fileDownload, syncOperation);
        
        fsRequest.setRequestCode(FOLDER_CREATED);
        logger.debug("Folder created successfully");
        fsRequest.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
        break;
      case CREATE_SOCKET_4_SYNC_DOWNLOAD_FILE:
        {
          Object items[] = fsResponse.getDatas();
          //serverMD5SumOfCurrentDownloadFile=(String)fsResponse.getData2();
          
          String filePathRelative = clientUtil.getRelativePath(items);
          destFilePathToSyncDownload = clientUtil.getAbsolutePath(filePathRelative, currentFolderLocalFile.getAbsolutePath());
          logger.debug("destFilePathToSyncDownload : " + destFilePathToSyncDownload);
          fileDownload = new File(destFilePathToSyncDownload);
          //            syncOperation = EnumSyncOperation.NEW_FILE;
          fsRequest = new FsRequest();
//          if (fileDownload.exists()) {
//            //              syncOperation = EnumSyncOperation.FILE_CHANGED;
//             fileDownloadBackup=null;
//             logger.debug("Inside File Download Exist....................................");
//             fileDownloadBackup= new File(fileDownload.getAbsolutePath()+".fsbak");
//             fileDownload.renameTo(fileDownloadBackup);
//             fileDownload.delete();
//             logger.debug("File Renamed Here " + fileDownloadBackup.getName() + " Size : " +fileDownloadBackup.length() ); 
//             
//          } else {
//            if (!fileDownload.getParentFile().exists()) {
//              //                fileDownload.mkdirs();
//            }
//          }
          String srcPathToDownload=null;
          srcPathToDownload=fsResponse.getData2().toString();
          
          fsServerHandler.addSyncDownloadListener(fsSocketClient.createSyncDownloadSocket(destFilePathToSyncDownload, srcPathToDownload, totalByteToTransfer, syncOperation, fsResponse.getSuperResponseCode(), propertyChangeSupport4FsSyncDownload));
          
//          fileDownload.createNewFile();
//          downloadFileOutputStream = new FileOutputStream(fileDownload);
//          
//          fsRequest.setRequestCode(EMPTY_FILE_CREATED);
//          fsRequest.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
//          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//          fsMessageSender.send(fsRequest);
        }
        break;
      case APPEND_TO_FILE:
        /*{
          fsRequest = new FsRequest();
          FsFileHolder fsFileHolder = (FsFileHolder)fsResponse.getData();
          int byteRead = (int)fsFileHolder.getSize();
          byte data[] = fsFileHolder.getData();
          totalByteTransfered += byteRead;
          Integer percent = new Integer((int)(totalByteTransfered * 100 / totalByteToTransfer));
          
          fsResponse.setData1(percent);
          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
          fsResponse.setResponseCode(PROGRESS);
          propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(PROGRESS),null,fsResponse);*/

          /*if (stopSync) {
            fsRequest.setRequestCode(STOP);
            fsRequest.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
            fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
            fsMessageSender.send(fsRequest);
            break;
          }*/
         /* downloadFileOutputStream.write(data, 0, byteRead);
          
          fsRequest.setRequestCode(APPENDED_TO_FILE);
          fsRequest.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
          fsMessageSender.send(fsRequest);
        }*/
        break;
      case STOPPED:
        logger.debug("Sync Download STOPPED");
//        if (downloadFileOutputStream != null) {
//          downloadFileOutputStream.close();
//        }
        logger.debug("Before ...........Inside Stopped for else part...............");
//        if (fileDownloadBackup != null) {
//          fileDownload.delete();
//          fileDownloadBackup.renameTo(fileDownload);
//          fileDownloadBackup.delete();
//        }
//        if(fileDownload.exists()){
//          logger.debug("Inside Stopped for else part...............");
//          fileDownload.delete();
//        }
        propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        logger.debug("After firing 4 Client Gui..............................................");
        fsServerHandler.removeSyncDownloadListener(this);
        break;
      case CLOSE_FILE:
        logger.debug("respond for closing file........");
        //downloadFileOutputStream.close();
        clientUtil.addDocumentElement(documentOfChanges, fileDownload, syncOperation);

        fsRequest = new FsRequest();
        logger.info("File download complete");
        fsRequest.setRequestCode(FILE_CLOSED);
        fsRequest.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
        
//        String clientMD5SumOfCurrentDownloadFile=commonUtil.generateMD5Sum(new FileInputStream(fileDownload));
//        logger.debug("clientMD5SumOfCurrentDownloadFile " + clientMD5SumOfCurrentDownloadFile);
//                  
//        logger.debug("serverMD5SumOfCurrentUploadFile " + serverMD5SumOfCurrentDownloadFile);
//        
//        if(clientMD5SumOfCurrentDownloadFile.equals(serverMD5SumOfCurrentDownloadFile)){ 
//          logger.debug("File Downloaded Successful");
//          if (fileDownloadBackup != null) {
//            fileDownloadBackup.delete();
//          }
//          clientMD5SumOfCurrentDownloadFile=null;
//          serverMD5SumOfCurrentDownloadFile=null;
//        }else{
//          logger.debug("File Downloaded Failure Due To Data Curruption...");
//          if (fileDownloadBackup != null) {
//            fileDownload.delete();
//            fileDownloadBackup.renameTo(fileDownload);
//            fileDownloadBackup.delete();
//          }
//          
//          fsResponse.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
//          fsResponse.setResponseCode(FILE_CURRUPTED);
//          propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(FILE_CURRUPTED),null,fsResponse);
//        }     
        break;
      case FAILED:
        logger.info("Download failure");
//        if (fileDownloadBackup != null) {
//          fileDownload.delete();
//          fileDownloadBackup.renameTo(fileDownload);
//          fileDownloadBackup.delete();
//        }
        propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeSyncDownloadListener(this);
        break;
      case ERROR_MESSAGE:
//        if (fileDownloadBackup != null) {
//          fileDownload.delete();
//          fileDownloadBackup.renameTo(fileDownload);
//          fileDownloadBackup.delete();
//        }
        propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeSyncDownloadListener(this);
        break;
      case FETAL_ERROR:
//        if (fileDownloadBackup != null) {
//          fileDownload.delete();
//          fileDownloadBackup.renameTo(fileDownload);
//          fileDownloadBackup.delete();
//        }
        propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeSyncDownloadListener(this);
        break;
      }
    } catch (Exception ex) {
      logger.error(clientUtil.getStackTrace(ex));
      fsRequest = new FsRequest();
      fsRequest.setRequestCode(FAILED);
      fsRequest.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
      fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
      fsMessageSender.send(fsRequest);

      //raise property change event to the propertychange listner          
//       if (fileDownloadBackup != null) {
//         fileDownload.delete();
//         fileDownloadBackup.renameTo(fileDownload);
//         fileDownloadBackup.delete();
//       }
      
      FsResponse fsResponse4ClientGui = new FsResponse();
      fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
      fsResponse4ClientGui.setSuperResponseCode(fsRequest.getSuperRequestCode());
      fsExceptionHolder = new FsExceptionHolder();
      fsExceptionHolder.setErrorMessage(ex.getMessage());
      fsResponse4ClientGui.setFsExceptionHolder(fsExceptionHolder);
      fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
      propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse);
      fsServerHandler.removeSyncDownloadListener(this);

    }

  }

  
 /* public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
    }
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }*/

  public void addFsSyncDownloadListener(FsSyncDownloadListener fsSyncDownloadListener){
    if (propertyChangeSupport4FsSyncDownload == null) {
      propertyChangeSupport4FsSyncDownload = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4FsSyncDownload.addPropertyChangeListener(fsSyncDownloadListener);
  }
  

}
