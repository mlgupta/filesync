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
 * $Id: FsHandleDownloadClient.java,v 1.46 2006/09/08 09:13:18 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.client;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsDownloadListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import java.io.File;

import org.apache.log4j.Logger;


/**
 *	Purpose: To Handle download at client side.Sends and receive all requests and responses 
 *  related to download. 
 *  @author              Jeetendra Prasad
 *  @version             1.2
 * 	Date of creation:    6-05-2005
 * 	Last Modfied by :    Saurabh Gupta
 * 	Last Modfied Date:   01-08-2006
 */
public class FsHandleDownloadClient implements FsDownloadListener {

  private Logger logger;

  private FsMessageSender fsMessageSender;

  private int superRequestCode;
  
  private Object[] itemToDownload;

  private String currentFolderPathDownload;
 
  private PropertyChangeSupport  propertyChangeSupport4FsDownload;

  private File fileDownload;
  
  private File fileDownloadBackup;

  private int overWriteValueDownload=0;

  private ClientUtil clientUtil;
  
  private CommonUtil commonUtil;
  
  private FsServerHandler fsServerHandler;
  
  private boolean isFile4Overwrite=false;
  
  private FsSocketClient fsSocketClient;
  
  private String destFilePathTodownload=null;



  /**
   * A FsHandleDownloadClient constructor.
   * @param logger logger object
   * @param fsServerHandler FsServerHandler object
   */
  public FsHandleDownloadClient(Logger logger, FsServerHandler fsServerHandler) {
    this.logger = logger;
    this.fsServerHandler=fsServerHandler;
    this.fsMessageSender = fsServerHandler.getFsMessagesender();
    this.commonUtil=new CommonUtil(logger);
    this.clientUtil = new ClientUtil(logger);
    this.fsSocketClient=fsServerHandler.getFsSocketClient();
  }

  /**
   * handles the PropertyChange event fired by FsFileSystemOperationsRemote class
   * checks for the source of the event,and handles the event only if (it is meant for
   * this class)the source matches with the superRequestCodeForDownload.
   * @param evt PropertyChangeEvent object
   */
    public void propertyChange(PropertyChangeEvent evt) {
      int propertyName=Integer.valueOf(evt.getPropertyName());
      FsResponse fsResponse = (FsResponse)evt.getNewValue();
      if(fsResponse.getSuperResponseCode().equals(Integer.toString(superRequestCode))){
        handleDownload(propertyName,fsResponse);
      }
  
  
   /* if (evt.getSource().equals(superRequestCodeForDownload)) {
      if (evt.getPropertyName().equals("fsResponse")) {
        FsResponse fsResponse = (FsResponse)evt.getNewValue();
        handleDownload(fsResponse);
      } else if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);
        //handle exception
      } else if (evt.getPropertyName().equals("operationCancelled")) {
        FsRequest fsRequest = new FsRequest();
        fsRequest.setRequestCode(FsMessage.DOWNLOAD_CANCEL);
        fsRequest.setSuperRequestCode(superRequestCodeForDownload);
        fsMessageSender.send(fsRequest);
      }
    }*/
  }

  /**
   * Initiates the download process.
   * @param itemToDownload array of items to download
   * @param currentFolderPathLocal base path on the local system
   * @param currentFolderPathRemote base path on the remote system
   * @param superRequestCode a token indicating the thread which will handle the response
   */
  public void downloadItem(Object[] itemToDownload, String currentFolderPathLocal, String currentFolderPathRemote, int superRequestCode) {
    this.itemToDownload = itemToDownload;
    this.currentFolderPathDownload = currentFolderPathLocal;
    this.superRequestCode = superRequestCode;

    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(currentFolderPathRemote);
    fsRequest.setDatas(itemToDownload);
    fsRequest.setRequestCode(START);
    fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
    fsRequest.setSuperRequestCode(Integer.toString(superRequestCode));
    fsMessageSender.send(fsRequest);
    logger.info("Download Started");
  }


  private void handleDownload(int propertyName,FsResponse fsResponse) {
    int responseCode = fsResponse.getResponseCode();
    FsExceptionHolder fsExceptionHolder;
    
    FsResponse fsResponse4ClientGui = new FsResponse();
    logger.debug("Property Name as super Response Code :" + propertyName);
    logger.debug("super Request Code : "+ fsResponse.getSuperResponseCode());
    switch(propertyName){
      case STARTED:
        logger.debug(" DownLoad Started");
        propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case COMPLETED:
//        try {
//          if (downloadFileOutputStream!=null) {
//            downloadFileOutputStream.close();
//          }
//        } catch (IOException ioe) {
//          logger.error(clientUtil.getStackTrace(ioe));
//        }
//        if(isFile4Overwrite){
//          logger.debug("isFile4Overwrite value is true.......... ");
//          fileDownloadBackup.renameTo(fileDownload);
//          fileDownloadBackup.delete();          
//        }
//        else{
//          if (fileDownload.exists()) {
//            logger.debug("fileDownload value is exist else part........... ");
//            fileDownload.delete();
//          }
//        }
//        isFile4Overwrite=false;
//        
        logger.debug("Download Completed...");
        propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        isFile4Overwrite=false;
        fsServerHandler.removeDownloadListener(this);
        break;
      case CREATE_FOLDER:
        logger.debug("response from server to create a folder");
        Object folders[] = fsResponse.getDatas();
        int folderCount = folders.length;
        fileDownload = new File(currentFolderPathDownload);
        String folderPathRelative = "";
        for (int index = 0; index < folderCount; index++) {
          folderPathRelative += File.separator + folders[index];
        }
        String absFolderPath = currentFolderPathDownload + folderPathRelative;
        logger.debug("absFolderPath : " + absFolderPath);
        fileDownload = new File(absFolderPath);
        FsRequest fsRequest = new FsRequest();
        try {
          if (fileDownload.exists()) {
            if (overWriteValueDownload == OVERWRITE_YES_TO_ALL) {
              fsRequest.setRequestCode(FOLDER_CREATED);
            }else{
              fsResponse.setResponseCode(OVERWRITE_FOLDER);
              fsResponse.setData1(fileDownload);
              propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(OVERWRITE_FOLDER),null,fsResponse);
            }
          } else {
            fileDownload.mkdirs();
            fsRequest.setRequestCode(FOLDER_CREATED);
            logger.debug("Folder created successfully");
          }
        } catch (Exception ex1) {
          fsRequest.setRequestCode(FAILED);
          logger.error(clientUtil.getStackTrace(ex1));
          
          //Response fire to ClientGui...........       
          
          fsResponse4ClientGui = new FsResponse();
          fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
          fsResponse4ClientGui.setSuperResponseCode(fsResponse.getSuperResponseCode());
          fsResponse4ClientGui.setOperation(fsResponse.getOperation());
          fsResponse4ClientGui.setDatas(fsResponse.getDatas());
          fsExceptionHolder = new FsExceptionHolder();
          fsExceptionHolder.setErrorMessage(ex1.getMessage());
          fsResponse4ClientGui.setFsExceptionHolder(fsExceptionHolder);
          propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse4ClientGui);
          fsServerHandler.removeDownloadListener(this);
        }
        fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
        break;
      case OVERWRITE_FOLDER:
        overWriteValueDownload=((Integer)fsResponse.getData()).intValue();
        if(overWriteValueDownload==OVERWRITE_YES || overWriteValueDownload==OVERWRITE_YES_TO_ALL){
          fsRequest= new FsRequest();
          fsRequest.setRequestCode(FOLDER_CREATED);
          fsRequest.setData(new Integer(overWriteValueDownload));
          fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
          fsMessageSender.send(fsRequest); 
        }
        break;
      case CREATE_SOCKET_4_DOWNLOAD_FILE:
        logger.debug("response from server to create empty File...");
        //serverMD5SumOfCurrentDownloadFile=(String)fsResponse.getData2();
        Object items[] = fsResponse.getDatas();
        int itemCount = items.length;
        String filePathRelative = "";
        for (int index = 0; index < itemCount; index++) {
          filePathRelative += File.separator + items[index];
        }
        destFilePathTodownload = currentFolderPathDownload + filePathRelative;
        logger.debug("destFilePathTodownload : " + destFilePathTodownload);
        fileDownload = new File(destFilePathTodownload);
        fsRequest = new FsRequest();
        logger.debug(" File : " + fileDownload.getName());
        
        try {
          if (fileDownload.exists()) {
            if(overWriteValueDownload == OVERWRITE_YES_TO_ALL){
              fsRequest = new FsRequest();
              fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
              fsRequest.setRequestCode(FsDownloadListener.OVERWRITE_FILE);
              fsRequest.setData(new Integer(overWriteValueDownload));
              fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
              fsServerHandler.getFsMessagesender().send(fsRequest);
            }else{
              isFile4Overwrite=true;
//              fileDownloadBackup= new File(fileDownload.getAbsolutePath()+".fsbak");
//              fileDownload.renameTo(fileDownloadBackup);
//              fileDownload.delete();
//              logger.debug("File Renamed Here " + fileDownloadBackup.getName() + " Size : " +fileDownloadBackup.length() );
              
              fsResponse.setResponseCode(OVERWRITE_FILE);
              fsResponse.setData1(fileDownload);
              propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(OVERWRITE_FILE),null,fsResponse);
            }
          } else {
            try {
              //response fire to Client Gui........
              
//              fsResponse4ClientGui = new FsResponse();
//              fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//              fsResponse4ClientGui.setResponseCode(CREATE_EMPTY_FILE);
//              fsResponse4ClientGui.setData(fileDownload.getPath());
//              fsResponse4ClientGui.setData1(fileDownload.getName());
//              fsResponse4ClientGui.setSuperResponseCode(fsResponse.getSuperResponseCode());
//              propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(CREATE_EMPTY_FILE),null,fsResponse4ClientGui);
              
             
              logger.debug("Handling Command To Client Socket................");
              //fileDownload.createNewFile();
             
              String srcPathToDownload=null;
              srcPathToDownload=fsResponse.getData2().toString();
              
               fsServerHandler.addDownloadListener(fsSocketClient.createDownloadSocket(destFilePathTodownload,srcPathToDownload,fsResponse.getSuperResponseCode(), propertyChangeSupport4FsDownload ));
              
              
              
              //downloadProgress.setFilePath("" + fileDownload.getPath());
//              downloadFileOutputStream = new FileOutputStream(fileDownload);
//              fsRequest.setRequestCode(EMPTY_FILE_CREATED);
//              fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//              fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//              fsMessageSender.send(fsRequest);
              
              
            } catch (Exception ioe) {
              logger.error(clientUtil.getStackTrace(ioe));
              
              //Response fire to ClientGui...................         
              fsResponse4ClientGui = new FsResponse();
              fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
              fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
              //fsResponse4ClientGui.setDatas(fsResponse.getDatas());
              fsResponse4ClientGui.setSuperResponseCode(fsResponse.getSuperResponseCode());
              fsExceptionHolder = new FsExceptionHolder();
              fsExceptionHolder.setErrorMessage(ioe.getMessage());
              fsResponse4ClientGui.setFsExceptionHolder(fsExceptionHolder);
              propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse4ClientGui);
              fsServerHandler.removeDownloadListener(this);
  
            }
          }
        } catch (Exception ex1) {
          logger.error(clientUtil.getStackTrace(ex1));
          fsRequest.setRequestCode(FAILED);
          fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
          fsMessageSender.send(fsRequest);
  
          //Fire response to ClientGui.............          
          fsResponse4ClientGui = new FsResponse();
          fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
          fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
          //fsResponse4ClientGui.setDatas(fsResponse.getDatas());
          fsResponse4ClientGui.setSuperResponseCode(fsResponse.getSuperResponseCode());
          fsExceptionHolder = new FsExceptionHolder();
          fsExceptionHolder.setErrorMessage(ex1.getMessage());
          fsResponse4ClientGui.setFsExceptionHolder(fsExceptionHolder);
          propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse4ClientGui);
          fsServerHandler.removeDownloadListener(this);
        }
        break;
      case OVERWRITE_NO:
      //fire to Client Gui..........
        propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case OVERWRITE_FILE:
        fsRequest = new FsRequest();
        try {
          overWriteValueDownload=((Integer)fsResponse.getData()).intValue();
          if(overWriteValueDownload==OVERWRITE_YES || overWriteValueDownload==OVERWRITE_YES_TO_ALL){
//            if (fileDownload.exists()){
//              isFile4Overwrite=true;
//              fileDownloadBackup= new File(fileDownload.getAbsolutePath()+".fsbak");
//              fileDownload.renameTo(fileDownloadBackup);
//              fileDownload.delete();
//              logger.debug("File Renamed Here " + fileDownloadBackup.getName() + " Size : " +fileDownloadBackup.length() );
//            }
//            fileDownload.createNewFile();
//            downloadFileOutputStream = new FileOutputStream(fileDownload);
//            fsRequest.setRequestCode(EMPTY_FILE_CREATED);
//            
//            fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//            fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//            fsMessageSender.send(fsRequest);
            
             String srcPathToDownload=null;
             srcPathToDownload=fsResponse.getData2().toString();
             
             fsServerHandler.addDownloadListener(fsSocketClient.createDownloadSocket(destFilePathTodownload,srcPathToDownload,fsResponse.getSuperResponseCode(), propertyChangeSupport4FsDownload ));
            
            //fire to Client Gui..........
            fsResponse4ClientGui = new FsResponse();
            fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
            fsResponse4ClientGui.setResponseCode(CREATE_EMPTY_FILE);
            fsResponse4ClientGui.setData(fileDownload.getPath());
            fsResponse4ClientGui.setData1(fileDownload.getName());
            fsResponse4ClientGui.setSuperResponseCode(fsResponse.getSuperResponseCode());
            propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(CREATE_EMPTY_FILE),null,fsResponse4ClientGui);            
          }
        }
        catch (Exception e) {
          logger.error(clientUtil.getStackTrace(e));
          
          //Response fire to ClientGui...................         
          fsResponse4ClientGui = new FsResponse();
          fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
          fsResponse4ClientGui.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
          fsExceptionHolder = new FsExceptionHolder();
          fsExceptionHolder.setErrorMessage(e.getMessage());
          fsResponse4ClientGui.setFsExceptionHolder(fsExceptionHolder);
          propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse4ClientGui);
          fsServerHandler.removeDownloadListener(this);
        }
        break;
      case APPEND_TO_FILE:
        fsRequest = new FsRequest();
//        try {
//          FsFileHolder fsFileHolder = (FsFileHolder)fsResponse.getData();
//          int byteRead = (int)fsFileHolder.getSize();
//          byte data[] = fsFileHolder.getData();
//          prevByteRead = byteRead;
//          downloadFileOutputStream.write(data, 0, byteRead);
//          fsRequest.setRequestCode(APPENDED_TO_FILE);
//          fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//          fsMessageSender.send(fsRequest);
//          
//          //Response fire to Client Gui......
//          fsResponse4ClientGui = new FsResponse();
//          fsResponse4ClientGui.setData(new Integer(prevByteRead));
//          fsResponse4ClientGui.setData1(fileDownload.getPath());
//          fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//          fsResponse4ClientGui.setResponseCode(PROGRESS);
//          fsResponse4ClientGui.setSuperResponseCode(fsRequest.getSuperRequestCode());
//          propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(PROGRESS),null,fsResponse4ClientGui);
//        } catch (Exception ex1) {
//          logger.error(clientUtil.getStackTrace(ex1));
//          fsRequest.setRequestCode(FAILED);
//          fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//          fsMessageSender.send(fsRequest);
//  
//          //response fire to ClientGui........         
//          fsResponse4ClientGui = new FsResponse();
//          fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
//          fsResponse4ClientGui.setOperation(fsRequest.getOperation());
//          fsResponse4ClientGui.setSuperResponseCode(fsRequest.getSuperRequestCode());
//          fsExceptionHolder = new FsExceptionHolder();
//          fsExceptionHolder.setErrorMessage(ex1.getMessage());
//          fsResponse4ClientGui.setFsExceptionHolder(fsExceptionHolder);
//          propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse4ClientGui);
//          fsServerHandler.removeDownloadListener(this);
//        }
        break;
      case CLOSE_FILE:
//        fsRequest = new FsRequest();
//        try {
//          logger.info("File download complete");
//          
//          //downloadFileOutputStream.close();
//          fsRequest.setRequestCode(FILE_CLOSED);
//          fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//          fsMessageSender.send(fsRequest);
          
//          String clientMD5SumOfCurrentDownloadFile=commonUtil.generateMD5Sum(new FileInputStream(fileDownload));
//          logger.debug("clientMD5SumOfCurrentDownloadFile " + clientMD5SumOfCurrentDownloadFile);
//                    
//          logger.debug("serverMD5SumOfCurrentUploadFile " + serverMD5SumOfCurrentDownloadFile);
//          
//          if(isFile4Overwrite){
//            if(clientMD5SumOfCurrentDownloadFile.equals(serverMD5SumOfCurrentDownloadFile)){ 
//              fileDownloadBackup.delete();
//              logger.debug("Inside file Download backup delete.... close file..... ");
//            }else{
//              fileDownload.delete();
//              fileDownloadBackup.renameTo(fileDownload);
//              fileDownloadBackup.delete();
//            }          
//          }
//          
//        } catch (Exception ex) {
//          logger.error(clientUtil.getStackTrace(ex));
//          fsRequest.setRequestCode(FAILED);
//          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
//          fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
//          fsMessageSender.send(fsRequest);
//  
//          //Response fire to ClientGui........          
//          fsResponse4ClientGui = new FsResponse();
//          fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
//          fsResponse4ClientGui.setOperation(fsRequest.getOperation());
//          fsResponse4ClientGui.setSuperResponseCode(fsRequest.getSuperRequestCode());
//          fsExceptionHolder = new FsExceptionHolder();
//          fsExceptionHolder.setErrorMessage(ex.getMessage());
//          fsResponse4ClientGui.setFsExceptionHolder(fsExceptionHolder);
//          propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse4ClientGui);
//          fsServerHandler.removeDownloadListener(this);
//        }
        break;
      case CANCLED:
        logger.debug("download cancelled");
//        try {
//          if (downloadFileOutputStream!=null) {
//            downloadFileOutputStream.close();
//          }
//        } catch (IOException ioe) {
//          logger.error(clientUtil.getStackTrace(ioe));
//        }
        logger.debug(" File Name " + fileDownload);
        
//        if(isFile4Overwrite){
//          logger.debug("isFile4Overwrite value is true.......... ");
//          fileDownloadBackup.renameTo(fileDownload);
//          fileDownloadBackup.delete();          
//        }else{
//          if (fileDownload.exists()) {
//            logger.debug("fileDownload value is exist else part........... ");
//            fileDownload.delete();
//          }
//        }
//        isFile4Overwrite=false;
        //propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeDownloadListener(this);
        break;
      case FAILED:
        logger.info("Download failure");
        if(isFile4Overwrite){
          if (fileDownload.exists()) {
            fileDownload.delete();
            fileDownloadBackup.renameTo(fileDownload);
            fileDownloadBackup.delete();          
          }
        }else{
          if (fileDownload.exists()) {
            fileDownload.delete();
          }
        }
        isFile4Overwrite=false;
        propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeDownloadListener(this);
        break;
      case ERROR_MESSAGE:
      if(isFile4Overwrite){
        if (fileDownload.exists()) {
          fileDownload.delete();
          fileDownloadBackup.renameTo(fileDownload);
          fileDownloadBackup.delete();          
        }
      }else{
        if (fileDownload.exists()) {
          fileDownload.delete();
        }
      }
        isFile4Overwrite=false;
        //propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeDownloadListener(this);
        break;
    }
  
  }

  /**
   * Purpose : Adds a listener that will listen to the property Change event that.
   * is fired by this class
   * @param fsDownloadListener PropertyChangeListener object
   */
  public void addFsDownloadListener(FsDownloadListener fsDownloadListener) {
    if (propertyChangeSupport4FsDownload == null) {
      propertyChangeSupport4FsDownload = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4FsDownload.addPropertyChangeListener(fsDownloadListener);
  }

}
