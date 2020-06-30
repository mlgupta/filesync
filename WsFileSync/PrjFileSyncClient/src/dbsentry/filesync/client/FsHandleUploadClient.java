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
 * $Id: FsHandleUploadClient.java,v 1.62 2006/09/08 09:13:18 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.client;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.constants.FsSocketConstants;
import dbsentry.filesync.common.constants.FsUploadConstants;
import dbsentry.filesync.common.listeners.FsUploadListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Logger;


/**
 *	To Handle upload at client side.Sends and receive all requests and responses 
 *  related to upload. 
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    6-05-2005
 * 	Last Modfied by :    Saurabh Gupta
 * 	Last Modfied Date:   24-07-2006
 */
public class FsHandleUploadClient implements FsUploadListener, FsSocketConstants {
  private PropertyChangeSupport propertyChangeSupport4FsUpload;
  
  private FsServerHandler fsServerHandler;
  
  private int superRequestCode;

  private FsMessageSender fsMessageSender;

  private Logger logger;

  private Long totalSizeUpload;

  private Stack itemToUpload;

  private String srcUploadFilePath;

  private File fileUpload;

  private String currentFolderPathUpload;

  private ClientUtil clientUtil;

  private long uploadStartTime;
  
  //private FileInputStream uploadFileInputStream=null;

  private CommonUtil commonUtil=null;
  
  private FsSocketClient fsSocketClient;
  
//  private FsUploadListener uploadListener;
  
  /**
   * A constructor to initialize FsHandleUploadClient.
   * 
   * @param logger a logger object to log messages
   * @param fsServerHandler FsServerHandler object to access various communication function
   */
  public FsHandleUploadClient( Logger logger, FsServerHandler fsServerHandler) {
    this.logger = logger;
    this.fsServerHandler=fsServerHandler;
    this.fsMessageSender = fsServerHandler.getFsMessagesender();
    this.clientUtil = new ClientUtil(logger);
    this.commonUtil=new CommonUtil(logger);
    this.fsSocketClient=fsServerHandler.getFsSocketClient();
  }

  /**
   * Initiates the upload process.
   * @param itemToUpload stack of item to upload
   * @param currentFolderPathLocal local base bath of the item
   * @param currentFolderPathRemote remote base path
   * 
   */
  public void uploadItem(Stack itemToUpload, String currentFolderPathLocal, String currentFolderPathRemote,int superRequestCode) {
    this.itemToUpload = itemToUpload;
    this.currentFolderPathUpload = currentFolderPathLocal;
    this.superRequestCode = superRequestCode;
    
    
    FsRequest fsRequest;
    fsRequest = new FsRequest();
    fsRequest.setData(currentFolderPathRemote);
    fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
    fsRequest.setRequestCode(START);
    fsRequest.setSuperRequestCode(Integer.toString(superRequestCode));
    fsMessageSender.send(fsRequest);
    uploadStartTime = new Date().getTime();
    logger.info("Upload Started");
  }

  /**
   * handles the PropertyChange event fired by FsFileSystemOperationsRemote class.
   * checks for the source of the event,and handles the event only if (it is meant for
   * this class)the source matches with the superRequestCodeForUpload.
   * @param evt PropertyChangeEvent object
   */
  public void propertyChange(PropertyChangeEvent evt) {
    int propertyName=Integer.valueOf(evt.getPropertyName());
    FsResponse fsResponse = (FsResponse)evt.getNewValue();
    if(fsResponse.getSuperResponseCode().equals(Integer.toString(superRequestCode))){
      handleUpload( propertyName,fsResponse);
    }
    
    /*if (evt.getSource().equals(this.superRequestCodeForUpload)) {
      if (evt.getPropertyName().equals("fsResponse")) {
        FsResponse fsResponse = (FsResponse)evt.getNewValue();
        handleUpload(fsResponse);
      } else if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);
        //handle exception
      } else if (evt.getPropertyName().equals("operationCancelled")) {
        if (guiFrame != null) {
          if (uploadThread != null) {
            uploadFileRecursively.stopUploadThread = true;
          }
          uploadProgress.dispose();
        }
      }
    }*/
  }

  /**
   * handles the upload related responses from server,which are 
   * redirected by FsFileSystemOperationsRemote class.
   * @param fsResponse FsResponse object
   */
   
   private void handleUpload(int propertyName,FsResponse fsResponse){
    int responseCode = fsResponse.getResponseCode();
    File subFiles[]=null;
    int itemCount;
//    int prevByteRead;
//    FsResponse fsResponse4ClientGui;
    switch(propertyName){
      case STARTED :
      try {
        totalSizeUpload = new Long(clientUtil.calculateFolderDocSize(itemToUpload.toArray()));
        logger.debug("Total Size : " + totalSizeUpload);
        fsResponse.setData(totalSizeUpload);
       } catch (IOException ioe) {
        logger.error(clientUtil.getStackTrace(ioe));
       }
        
        propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        uploadFile(fsResponse.getSuperResponseCode());
        break;
      case COMPLETED:
        logger.debug("Upload items 4 Completed....");
        propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(COMPLETED),null,fsResponse);
        fsServerHandler.removeUploadListener(this);
        logger.info("upload command complete........");
        break;
      case FAILED:
        logger.info("Upload Failure");
        itemToUpload.clear();
        propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeUploadListener(this);
        break;
      case CANCLED:
        logger.info("Upload Cancelled");
        itemToUpload.clear();
//        try {
//          if (uploadFileInputStream!=null) {
//            uploadFileInputStream.close();
//          }
//        }
//        catch (IOException e) {
//          ;
//        }
        logger.debug("Inside client cancel option...............");
        propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeUploadListener(this);
        break;
      case FILE_CURRUPTED:
        logger.info("Upload Failure Due to File Curruption...");
        itemToUpload.clear();
        //propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(FILE_CURRUPTED),null,fsResponse);
        fsServerHandler.removeUploadListener(this);
        break;
//      case PROGRESS_BUILDING:
//        fsResponse.setData(uploadFilePath);
//        propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(PROGRESS_BUILDING),null,fsResponse);
//        break;
      case CREATE_FOLDER:
        logger.debug("Folder created");
        
        subFiles = fileUpload.listFiles();
        itemCount = subFiles.length;
        logger.debug("itemCount : " + itemCount);
        for (int index = 0; index < itemCount; index++) {
          logger.debug("fsObjects[index].getPath() : " + subFiles[index].getAbsolutePath());
          itemToUpload.push(subFiles[index].getAbsolutePath());
        }      
        uploadFile(fsResponse.getSuperResponseCode());
        break;
      case FOLDER_CREATED:
        logger.debug("Folder created");
        
        subFiles = fileUpload.listFiles();
        itemCount = subFiles.length;
        logger.debug("itemCount : " + itemCount);
        for (int index = 0; index < itemCount; index++) {
          logger.debug("fsObjects[index].getPath() : " + subFiles[index].getAbsolutePath());
          itemToUpload.push(subFiles[index].getAbsolutePath());
        }
        
        uploadFile(fsResponse.getSuperResponseCode());
        break;
      case FILE_CLOSED:
//        //Response to Client Gui...........          
//        FsResponse fsResponse4ClientGui = new FsResponse();
//        fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
//        fsResponse4ClientGui.setResponseCode(OVERWRITE_NO);
//        fsResponse4ClientGui.setSuperResponseCode(fsResponse.getSuperResponseCode());
//        propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(OVERWRITE_NO),null,fsResponse4ClientGui);
        
        uploadFile(fsResponse.getSuperResponseCode());
        break;
      case FOLDER_NOT_CREATED:
        uploadFile(fsResponse.getSuperResponseCode());
        break;
      case OVERWRITE_FOLDER:
        propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case OVERWRITE_FILE:
         propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case SOCKET_4_UPLOAD_FILE_CREATED:
        FsRequest fsRequest = new FsRequest();
        String destFilePathUpload =fsResponse.getData().toString();
        //String tempFileUpload=fsResponse.getData2().toString();
        int operationUploadData=(Integer)fsResponse.getData1();
        try {
          //uploadFileInputStream = new FileInputStream(uploadFilePath);
          propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
          
//          fsSocketClient.addUploadListener(uploadListener);
//          fsSocketClient.addUploadListener(fsSocketClient.createUploadListener4Socket(superRequestCode));
//          //fsServerHandler.addUploadListener(fsSocketClient);
          
          fsServerHandler.addUploadListener(fsSocketClient.createUploadSocket(operationUploadData, destFilePathUpload, srcUploadFilePath, fsResponse.getSuperResponseCode(), propertyChangeSupport4FsUpload ));
            
         // uploadFileInputStream.close();
       /* fsRequest.setRequestCode(CLOSE_FILE); 
          fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
          fsMessageSender.send(fsRequest); */
          
         }catch(Exception ex){
                logger.error(clientUtil.getStackTrace(ex));
                fsRequest.setRequestCode(FAILED);
                fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
                fsMessageSender.send(fsRequest);
           }
        break;
//      case APPENDED_TO_FILE:
//        
//        try{
//          if(responseCode == EMPTY_FILE_CREATED){
//            
//          }
//          if(fileUpload!=null){
//          fsSocketClient.createSocket(operationUploadData,absFilePathUpload, fileUpload);
//          }else {
//            uploadFileInputStream.close();
//            fsRequest.setRequestCode(CLOSE_FILE); 
//            fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
//            fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//            fsMessageSender.send(fsRequest);
//          }
//      }catch(Exception ex){
//        logger.error(clientUtil.getStackTrace(ex));
//        fsRequest.setRequestCode(FAILED);
//        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//        fsMessageSender.send(fsRequest);
//      }
       
     
     /*   FsRequest fsRequest = new FsRequest();
      try{
        if(responseCode == EMPTY_FILE_CREATED){
          uploadFileInputStream = new FileInputStream(uploadFilePath);
          propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
       
        }
        FsFileHolder fsFileHolder = new FsFileHolder();
        int byteRead;
        byte datas[] = new byte[CommonUtil.JXTA_BUFFER_SIZE];
        if((byteRead =  uploadFileInputStream.read(datas)) != -1){
          fsFileHolder.setSize(byteRead);
          fsFileHolder.setData(datas);
          fsRequest.setData(fsFileHolder);
          fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsRequest.setRequestCode(APPEND_TO_FILE);
          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
          fsMessageSender.send(fsRequest);
          prevByteRead = byteRead;
          
          //fire to Client Gui..........
          fsResponse4ClientGui = new FsResponse();
          fsResponse4ClientGui.setData(new Integer(byteRead));
          fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse4ClientGui.setResponseCode(PROGRESS);
          fsResponse4ClientGui.setSuperResponseCode(fsRequest.getSuperRequestCode());
          propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(PROGRESS),null,fsResponse4ClientGui);
          
        }else{
          uploadFileInputStream.close();
          fsRequest.setRequestCode(CLOSE_FILE); 
          fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
          fsMessageSender.send(fsRequest);
        }
      }catch(IOException ioe){
          fsResponse = new FsResponse();
          fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setResponseCode(PROGRESS_ERROR);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsResponse.setData(uploadFilePath);
          propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(PROGRESS_ERROR),null,fsResponse);
       
      }catch(Exception ex){
        logger.error(clientUtil.getStackTrace(ex));
        fsRequest.setRequestCode(FAILED);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode()); 
        fsMessageSender.send(fsRequest);
      }
      break;*/
    case ERROR_MESSAGE:
      logger.info("Upload Failure");
      itemToUpload.clear();
      propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
      fsServerHandler.removeUploadListener(this);
      break;
    case FETAL_ERROR:
      logger.info("Upload Failure");
      itemToUpload.clear();
      propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
      fsServerHandler.removeUploadListener(this);
      break;
    }
   } 

  /**
   * Handles upload related possiblities.
   * @param superRequestCode a token used to distinguish which thread will handle the response   * @logic  : Pops an item to be uploaded from the stack and if it is file then sends request
   * of create file else create folder request is sent.
   */
  private void uploadFile(String superRequestCode) {
    FsRequest fsRequest = new FsRequest();
    FsResponse fsResponse4ClientGui;
    String itemPath;
    FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    try {
      if (itemToUpload.empty()) {
        logger.info("Items are empty 4 upload.... ");
        fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
        fsRequest.setRequestCode(COMPLETE);
        fsRequest.setSuperRequestCode(superRequestCode);
        fsMessageSender.send(fsRequest);

        long uploadEndTime = new Date().getTime();
        long totalTime = (uploadEndTime - uploadStartTime) / 1000;
        logger.debug("Total Time : " + totalTime);
        if (totalTime != 0) {
          logger.debug("Upload Speed: " + ((totalSizeUpload.longValue() / 1024) / totalTime) + "Kbps.");
        }
      } else {
        File currentFolderPathFile = new File(currentFolderPathUpload);
        
        
        itemPath = (String)itemToUpload.pop();
        logger.info("itemPath : " + itemPath);
        
        fileUpload = new File(itemPath);
        
        File itemAbsPathFile = new File(itemPath);
        ArrayList datas = new ArrayList();
        
        while (!itemAbsPathFile.getAbsolutePath().equals(currentFolderPathFile.getAbsolutePath())) {
          logger.debug("currentFolderPathFile.getAbsolutePath() : " + currentFolderPathFile.getAbsolutePath());
          logger.debug("itemAbsPathFile.getAbsolutePath() : " + itemAbsPathFile.getAbsolutePath());
          logger.debug("itemAbsPathFile.getName() : " + itemAbsPathFile.getName());

          datas.add(0, itemAbsPathFile.getName());
          itemAbsPathFile = itemAbsPathFile.getParentFile();
        }
        
        logger.debug("datas : " + datas);
        
        fsRequest.setDatas(datas.toArray());

        if (fileUpload.isDirectory()) {
          logger.debug("Create folder");
          logger.debug("Folder Path : " + fileUpload.getAbsolutePath());
          logger.debug("Creating folder : " + fileUpload.getAbsolutePath());
          
          fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsRequest.setRequestCode(CREATE_FOLDER);
          fsRequest.setSuperRequestCode(superRequestCode);
          fsMessageSender.send(fsRequest);
          
          //Response to Client Gui...........          
          fsResponse4ClientGui = new FsResponse();
          fsResponse4ClientGui.setData(fileUpload);
          fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse4ClientGui.setResponseCode(CREATE_FOLDER);
          fsResponse4ClientGui.setSuperResponseCode(superRequestCode);
          propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(CREATE_FOLDER),null,fsResponse4ClientGui);

          logger.debug("Folder created");
        } else {
          logger.debug("File upload started");
          FsFileHolder fsFileHolder = new FsFileHolder();
          fsFileHolder.setName(fileUpload.getName());
          fsFileHolder.setPath(fileUpload.getAbsolutePath());
          fsFileHolder.setOwner(null);
          fsFileHolder.setCreationDate(null);

          fsFileHolder.setModifiedDate(new Date(fileUpload.lastModified()));
          fsFileHolder.setDescription(fileSystemView.getSystemDisplayName(fileUpload));
          fsFileHolder.setMimeType(fileSystemView.getSystemTypeDescription(fileUpload));
          fsFileHolder.setSize(fileUpload.length());
          srcUploadFilePath = fileUpload.getAbsolutePath();

          fsRequest.setData(fsFileHolder);
         // fsRequest.setData1( commonUtil.generateMD5Sum(new FileInputStream(fileUpload)));
          fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsRequest.setRequestCode(CREATE_SOCKET_4_UPLOAD_FILE);
          fsRequest.setSuperRequestCode( superRequestCode);
          fsMessageSender.send(fsRequest);
          
          //Responce To clientGui
          fsResponse4ClientGui = new FsResponse();
          fsResponse4ClientGui.setData(fileUpload);
          fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse4ClientGui.setResponseCode(CREATE_SOCKET_4_UPLOAD_FILE);
          fsResponse4ClientGui.setSuperResponseCode(superRequestCode);
          propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(CREATE_SOCKET_4_UPLOAD_FILE),null,fsResponse4ClientGui);
        }
      }
    } catch (Exception ex) {

      logger.info("Upload Failure");
      fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
      fsRequest.setRequestCode(FsUploadConstants.FAILED);
      fsRequest.setSuperRequestCode(superRequestCode);
      fsMessageSender.send(fsRequest);
      logger.error(clientUtil.getStackTrace(ex));
    }
  }


  /**
   * @param uploadListener FsUploadListener
   * @description Adds a listener that will listen to the property Change event that 
   * is fired by this class.
   */
  public void addUploadListener(FsUploadListener uploadListener) {
//    this.uploadListener=uploadListener;
    if (propertyChangeSupport4FsUpload == null) {
      propertyChangeSupport4FsUpload = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4FsUpload.addPropertyChangeListener(uploadListener);
  }

}

