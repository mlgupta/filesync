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
 * $Id: FsHandleSyncUploadClient.java,v 1.49 2006/08/04 05:56:08 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.client;

import dbsentry.filesync.client.utility.FolderFileComparatorLocal;
import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsSyncUploadListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;





/**
 *	To Handle sync upload at client side. Sends and receive all requests and responses 
 *  related to upload. 
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    6-05-2005
 * 	Last Modfied by :    Saurabh Gupta
 * 	Last Modfied Date:   04-08-2006
 */
public class FsHandleSyncUploadClient implements FsSyncUploadListener {
  private PropertyChangeSupport  propertyChangeSupport4FsSyncUpload;

  private FsServerHandler fsServerHandler;
  
  private String superRequestCode;

  private FsMessageSender fsMessageSender;

  private Logger logger;

  private long totalByteToTransfer;

  private LinkedList itemToUpload = new LinkedList();

 // private FileInputStream uploadFileInputStream;

  private long totalByteTransfered;

  private String srcSyncUploadFilePath;

  private File fileUpload;

  private File currFolderLocalFile;

  private String currFolderRemotePath;

  private String syncOperation;

  private Document localSyncXMLDocument;

  private String itemToUploadPath;

  private Document documentOfChanges;

  private String fileOrFolderUnderOperation;

  private CommonUtil commonUtil;

  private ClientUtil clientUtil;
  
  private FsSocketClient fsSocketClient;

  //private boolean stopSync = false;

  /**
   * A constructor to initialize FsHandleSyncUploadClient.
   * @param logger logger object
   * @param localSyncXMLDocument local sync xml sync document
   * @param syncOperation sync operation type. refer dbsentry.filesync.common.EnumSyncOperation
   * @param itemToUploadPath item to upload
   * @param currFolderLocalFile local base file
   * @param currFolderRemotePath remote base file
   * @param superRequestCode a token indicating the thread which will handle the response
   */
  public FsHandleSyncUploadClient(Logger logger,FsServerHandler fsServerHandler, Document localSyncXMLDocument, String syncOperation, String itemToUploadPath,
                                  File currFolderLocalFile, String currFolderRemotePath, String superRequestCode) {

    this.commonUtil = new CommonUtil(logger);
    this.clientUtil = new ClientUtil(logger);
    
    this.logger = logger;
    this.syncOperation = syncOperation;
    this.localSyncXMLDocument = localSyncXMLDocument;
    this.superRequestCode = superRequestCode;
    this.itemToUploadPath = itemToUploadPath;
    this.itemToUpload.add(itemToUploadPath);
    this.currFolderRemotePath = currFolderRemotePath;
    this.currFolderLocalFile = currFolderLocalFile;
    this.documentOfChanges = commonUtil.getEmptyDocumentObject();
    this.fsServerHandler=fsServerHandler;
    this.fsMessageSender = fsServerHandler.getFsMessagesender();
    this.fsSocketClient=fsServerHandler.getFsSocketClient();
  }

  /**
   * start upload process.
   */
  public void startUpload() {
    logger.info("Upload Started");

    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(currFolderRemotePath);
    fsRequest.setData1(syncOperation);
    fsRequest.setRequestCode(START);
    fsRequest.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsMessageSender.send(fsRequest);

  }

  /**
   * property change listener function.
   * @param evt PropertyChangeEvent object
   */
 /* public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(superRequestCodeForUpload)) {
      if (evt.getPropertyName().equals("fsResponse")) {
        FsResponse fsResponse = (FsResponse)evt.getNewValue();
        handleSyncUpload(fsResponse);
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
      handleSyncUpload( propertyName,fsResponse);
    }
  }  

  /**
   * handles sync upload process
   * @param fsResponse FsResponse object
   */
  private void handleSyncUpload(int propertyName, FsResponse fsResponse) {
    int responseCode = fsResponse.getResponseCode();
    FsRequest fsRequest;
    try {
      switch (propertyName) {
      case STARTED:
        // calculate the total size of selected items
        totalByteToTransfer = clientUtil.calculateFolderDocSize(itemToUpload.toArray());
        logger.debug("Total Size : " + totalByteToTransfer);
        propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(STARTED),null,fsResponse);
        uploadFile(fsResponse.getSuperResponseCode());
        break;
      case COMPLETED:
        {
          logger.info("Upload Complete");
          commonUtil.mergeTwoDocument(localSyncXMLDocument, documentOfChanges);
          propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
          fsServerHandler.removeSyncUploadListener(this);
        }
        break;
      case FAILED:
        {
          logger.info("Upload Failure");
          propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
          fsServerHandler.removeSyncUploadListener(this);
        }
        break;
      case FILE_CURRUPTED:
        logger.info("Upload Failure Due to File Curruption...");
        itemToUpload.clear();
        propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(FILE_CURRUPTED),null,fsResponse);
        fsServerHandler.removeSyncUploadListener(this);
        break;
      case FOLDER_CREATED:
        {
          File file = new File(fileOrFolderUnderOperation);
          clientUtil.addDocumentElement(documentOfChanges, file, syncOperation);
          logger.debug("Folder created");
          uploadFile(fsResponse.getSuperResponseCode());
        }
        break;
      case FILE_CLOSED:
        {
          File file = new File(fileOrFolderUnderOperation);
          clientUtil.addDocumentElement(documentOfChanges, file, syncOperation);
          //documentOfChanges = (Document)fsResponse.getData();
          logger.debug("File created");
          uploadFile(fsResponse.getSuperResponseCode());
        }
        break;
      case STOPPED:
        {
          logger.debug("Sync Upload STOPPED");
//          if (uploadFileInputStream != null) {
//            uploadFileInputStream.close();
//          }
          //propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
          fsServerHandler.removeSyncUploadListener(this);
        }
        break;
      case SOCKET_4_SYNC_UPLOAD_CREATED:
        String destFilePathSyncUpload =fsResponse.getData().toString();
        String syncOperation=fsResponse.getData1().toString();
      
        fsServerHandler.addSyncUploadListener(fsSocketClient.createSyncUploadSocket(destFilePathSyncUpload, srcSyncUploadFilePath, totalByteToTransfer, syncOperation, fsResponse.getSuperResponseCode(), propertyChangeSupport4FsSyncUpload));
      
        break;
      case APPENDED_TO_FILE:
//        {
//          fsRequest = new FsRequest();
//          if (responseCode == EMPTY_FILE_CREATED) {
//            uploadFileInputStream = new FileInputStream(srcSyncUploadFilePath);
//          }
//
//          FsFileHolder fsFileHolder = new FsFileHolder();
//          int byteRead;
//          byte datas[] = new byte[CommonUtil.JXTA_BUFFER_SIZE];
//         /* if (stopSync) {
//            fsRequest.setRequestCode(STOP);
//            fsRequest.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
//            fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//            fsMessageSender.send(fsRequest);
//            break;
//          }*/
//
//          if ((byteRead = uploadFileInputStream.read(datas)) != -1) {
//            totalByteTransfered += byteRead;
//            Integer percent = new Integer((int)(totalByteTransfered * 100 / totalByteToTransfer));
//            fsResponse.setData(percent);
//            fsResponse.setResponseCode(PROGRESS);
//            fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
//            propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(PROGRESS),null,fsResponse);
//
//            fsFileHolder.setSize(byteRead);
//            fsFileHolder.setData(datas);
//            fsRequest.setData(fsFileHolder);
//            fsRequest.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
//            fsRequest.setRequestCode(APPEND_TO_FILE);
//            fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//            fsMessageSender.send(fsRequest);
//          } else {
//            uploadFileInputStream.close();
//            fsRequest.setRequestCode(CLOSE_FILE);
//            fsRequest.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
//            fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//            fsMessageSender.send(fsRequest);
//          }
//        }
        break;
      case ERROR_MESSAGE:
        logger.debug("Sync Upload ERROR");
        itemToUpload.clear();
//        if (uploadFileInputStream != null) {
//          uploadFileInputStream.close();
//        }
        propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeSyncUploadListener(this);
        break;
      case FETAL_ERROR:
        logger.debug("Sync Upload ERROR");
        itemToUpload.clear();
//        if (uploadFileInputStream != null) {
//          uploadFileInputStream.close();
//        }
        propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeSyncUploadListener(this);
        break;
      }
    } catch (Exception ex) {
      logger.error(clientUtil.getStackTrace(ex));
      fsRequest = new FsRequest();
      fsRequest.setRequestCode(FAILED);
      fsRequest.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
      fsRequest.setSuperRequestCode(superRequestCode);
      fsMessageSender.send(fsRequest);
    }
  }

  private void uploadFile(String superRequestCode) {
    FsRequest fsRequest = new FsRequest();
    String itemPath;
    FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    try {
      if (itemToUpload.isEmpty()) {
        logger.debug("Stack empty");
        fsRequest.setRequestCode(COMPLETE);
        
        fsRequest.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
        fsRequest.setSuperRequestCode(superRequestCode);
        fsMessageSender.send(fsRequest);
      } else {
        itemPath = (String)itemToUpload.removeFirst();
        fileUpload = new File(itemPath);
  
        fileOrFolderUnderOperation = fileUpload.getAbsolutePath();
        File itemAbsPathFile = new File(fileOrFolderUnderOperation);
        ArrayList datas = new ArrayList();
        while (!itemAbsPathFile.getAbsolutePath().equals(currFolderLocalFile.getAbsolutePath())) {
          datas.add(0, itemAbsPathFile.getName());
          itemAbsPathFile = itemAbsPathFile.getParentFile();
        }
        fsRequest.setDatas(datas.toArray());
        logger.debug("Creating Item : " + fileUpload.getAbsolutePath());
  
        if (fileUpload.isDirectory()) {
          File subFiles[] = fileUpload.listFiles();
          Arrays.sort(subFiles, new FolderFileComparatorLocal());
          int itemCount = subFiles.length;
          logger.debug("itemCount : " + itemCount);
          for (int index = 0; index < itemCount; index++) {
            logger.debug("fsObjects[index].getPath() : " + subFiles[index].getAbsolutePath());
            itemToUpload.add(subFiles[index].getAbsolutePath());
          }
  
          fsRequest.setRequestCode(CREATE_FOLDER);
          fsRequest.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
          fsRequest.setSuperRequestCode(superRequestCode);
          fsMessageSender.send(fsRequest);
        } else {
//          FsFileHolder fsFileHolder = new FsFileHolder();
//          fsFileHolder.setName(fileUpload.getName());
//          fsFileHolder.setPath(fileUpload.getAbsolutePath());
//          fsFileHolder.setOwner(null);
//          fsFileHolder.setCreationDate(null);
//  
//          fsFileHolder.setModifiedDate(new Date(fileUpload.lastModified()));
//          fsFileHolder.setDescription(fileSystemView.getSystemDisplayName(fileUpload));
//          fsFileHolder.setMimeType(fileSystemView.getSystemTypeDescription(fileUpload));
//          fsFileHolder.setSize(fileUpload.length());
            srcSyncUploadFilePath = fileUpload.getAbsolutePath();
  
          //fsRequest.setData(fsFileHolder);
          //fsRequest.setData1( commonUtil.generateMD5Sum(new FileInputStream(fileUpload)));
          fsRequest.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
          fsRequest.setRequestCode(CREATE_SOCKET_4_SYNC_UPLOAD);
          fsRequest.setSuperRequestCode(superRequestCode);
          fsMessageSender.send(fsRequest);
        }
      }
    }
    catch (Exception e) {
      ;
    }
  }

  
 /* public void addPropertyChangeSupport(PropertyChangeListener propertyChangeListener) {
    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
    }
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }*/

  public void addFsSyncUploadListener(FsSyncUploadListener fsSyncUploadListener){
    if (propertyChangeSupport4FsSyncUpload == null) {
      propertyChangeSupport4FsSyncUpload = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4FsSyncUpload.addPropertyChangeListener(fsSyncUploadListener);
    
  }

}
