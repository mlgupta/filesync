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
 * $Id $
 *****************************************************************************
 */
package dbsentry.filesync.client;

import dbsentry.filesync.client.jxta.JxtaSocketClient;
import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.FsSocketPacketCarrier;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.constants.FsSocketConstants;
import dbsentry.filesync.common.listeners.FsDownloadListener;
import dbsentry.filesync.common.listeners.FsSyncDownloadListener;
import dbsentry.filesync.common.listeners.FsSyncUploadListener;
import dbsentry.filesync.common.listeners.FsUploadListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.filechooser.FileSystemView;

import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaSocket;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;


/**
 * @author Saurabh Gupta
 * @version 1.0
 * @Date of creation: 06-07-2006
 * @Last Modfied by :Saurabh gupta  
 * @Last Modfied Date:04-08-2006
 */
public class FsSocketClient implements FsSocketConstants {

  private Logger logger;
  
  private ClientUtil clientUtil;
  
  private CommonUtil commonUtil;
  
  private PeerID peerId=null;
  
  private String userName;
  
  private PeerGroup netPeerGroup;
  
  private PipeAdvertisement socketAdv;
  
  private FsServerHandler fsServerHandler;
 
  public FsSocketClient(PeerGroup netPeerGroup, PipeAdvertisement socketAdv, PeerID peerId,String userName, Logger logger, FsServerHandler fsServerHandler) {
    try {
      this.logger=logger;
      this.clientUtil = new ClientUtil(logger);
      this.commonUtil = new CommonUtil(logger);
      this.peerId=peerId;
      this.netPeerGroup=netPeerGroup;
      this.socketAdv=socketAdv;
      this.userName=userName;
      this.fsServerHandler=fsServerHandler;
    }
    catch (Exception e) {
      logger.error(clientUtil.getStackTrace(e));
    }
  }
  
  public FsUploadListener createUploadSocket(int socketConstant,String absFilePathUpload, String selectedFilePath, String superResponseCode,PropertyChangeSupport propertyChangeSupport4FsUpload){
    DataHandler4UploadClient dataHandler4UploadClient=null;
    try {
      dataHandler4UploadClient = new DataHandler4UploadClient(socketConstant,absFilePathUpload, selectedFilePath, superResponseCode, propertyChangeSupport4FsUpload);
      Thread thread = new Thread(dataHandler4UploadClient, "Connection Handler Thread");
      thread.start();
    }
    catch (Exception e1) {
      logger.error(clientUtil.getStackTrace(e1));
    }
    return dataHandler4UploadClient;
  }
  
   public FsDownloadListener createDownloadSocket(String destFilePathDownload,String srcFilePathToDownload, String superResponseCode,PropertyChangeSupport propertyChangeSupport4FsDownload){
     DataHandler4DownloadClient dataHandler4DownloadClient=null;
     try {
       dataHandler4DownloadClient = new DataHandler4DownloadClient(destFilePathDownload,srcFilePathToDownload, superResponseCode, propertyChangeSupport4FsDownload);
       Thread thread = new Thread(dataHandler4DownloadClient, "Connection Handler Thread");
       thread.start();
     }
     catch (Exception e1) {
       logger.error(clientUtil.getStackTrace(e1));
     }
     return dataHandler4DownloadClient;
   }
   
   public FsSyncUploadListener createSyncUploadSocket(String destFilePathSyncUpload, String srcSyncUploadFilePath, long totalByteToTransfer, String syncOperation, String superResponseCode, PropertyChangeSupport propertyChangeSupport4FsSyncUpload){
     DataHandler4SyncUploadClient dataHandler4SyncUploadClient=null;
     try {
       dataHandler4SyncUploadClient = new DataHandler4SyncUploadClient(destFilePathSyncUpload, srcSyncUploadFilePath, totalByteToTransfer, syncOperation, superResponseCode, propertyChangeSupport4FsSyncUpload);
       Thread thread = new Thread(dataHandler4SyncUploadClient, "Connection Handler Thread");
       thread.start();
     }
     catch (Exception e1) {
       logger.error(clientUtil.getStackTrace(e1));
     }
     return dataHandler4SyncUploadClient;
   }
   
   public FsSyncDownloadListener createSyncDownloadSocket(String destFilePathSyncDownload, String srcSyncDownloadFilePath, long totalByteToTransfer, String syncOperation, String superResponseCode, PropertyChangeSupport propertyChangeSupport4FsSyncDownload){
     DataHandler4SyncDownloadClient dataHandler4SyncDownloadClient=null;
     try {
       dataHandler4SyncDownloadClient = new DataHandler4SyncDownloadClient(destFilePathSyncDownload, srcSyncDownloadFilePath, totalByteToTransfer, syncOperation, superResponseCode, propertyChangeSupport4FsSyncDownload);
       Thread thread = new Thread(dataHandler4SyncDownloadClient, "Connection Handler Thread");
       thread.start();
     }
     catch (Exception e1) {
       logger.error(clientUtil.getStackTrace(e1));
     }
     return dataHandler4SyncDownloadClient;
   }
   
   private class DataHandler4SyncDownloadClient implements Runnable, FsSyncDownloadListener{
     private String destFilePathSyncDownload;
     
     private String srcSyncDownloadFilePath;
     
     private String superResponseCode;
     
     private PropertyChangeSupport propertyChangeSupport4FsSyncDownload;
     
     private JxtaSocket  clientSocket;
     
     private File fileSyncDownload;
     
     private ArrayList syncDownloadEventCollector= new ArrayList();
     
     private long totalByteToTransfered;
     
     private String syncOperation;
     
     private Document documentOfChanges;
     
     private File fileSyncDownloadBackup=null;
     
     private long totalByteTransfered;
     
     public DataHandler4SyncDownloadClient(String destFilePathSyncDownload,String srcSyncDownloadFilePath,long totalByteToTransfer, String syncOperation, String superResponseCode, PropertyChangeSupport propertyChangeSupport4FsSyncDownload){
       try {
         this.destFilePathSyncDownload=destFilePathSyncDownload;
         this.srcSyncDownloadFilePath=srcSyncDownloadFilePath;
         this.superResponseCode=superResponseCode;
         this.propertyChangeSupport4FsSyncDownload=propertyChangeSupport4FsSyncDownload;
         this.clientSocket=(new JxtaSocketClient(netPeerGroup,socketAdv,peerId,logger)).createClientSocket();
         this.clientSocket.setOutputStreamBufferSize(commonUtil.JXTA_SOCKET_BUFFER_SIZE);
         this.totalByteToTransfered=totalByteToTransfer;
         this.syncOperation=syncOperation;
         this.documentOfChanges = commonUtil.getEmptyDocumentObject();
         
       }
       catch (IOException ioe) {
         logger.error(clientUtil.getStackTrace(ioe));
       }
     }
     
     public void propertyChange(PropertyChangeEvent evt){
       logger.debug("events r comming from server for syncDownload command.....");
       int propertyName=Integer.valueOf(evt.getPropertyName());
       FsResponse fsResponse = (FsResponse)evt.getNewValue();
       if(fsResponse.getSuperResponseCode().equals(superResponseCode)){
         logger.debug("SyncDownload code are..............: "+ propertyName);
         //handleUpload( propertyName,fsResponse);
         if(propertyName==STOPPED){
           syncDownloadEventCollector.add(evt);
           logger.debug("Sync Downloading commands r added to array list &&&&&&&&&&&&&&"+propertyName);
         }
       }
     }
     
     public void run(){
       
      logger.debug("insise DataHandler thread...........................start 4 client");
       handleSyncDownloadData();
     }

    private void handleSyncDownloadData() {
      ObjectInputStream ois4SyncDownload=null;
      ObjectOutputStream oos4SyncDownload=null;
      FileOutputStream syncDownloadFileOutputStream=null;
      FsSocketPacketCarrier dataCarrierRequest = null;
      FsSocketPacketCarrier dataCarrierResponse = null;
      
      FsResponse fsResponse4ClientGui=null;
      String serverMD5SumOfCurrentDownloadFile=null;
      
      try{

        logger.debug("inside Client socket sending 4 Sync download...........");
        boolean dontExit=true;
        
        oos4SyncDownload=new ObjectOutputStream(clientSocket.getOutputStream());

        dataCarrierRequest= new FsSocketPacketCarrier();
        dataCarrierRequest.setSuperOperation(SOCKET_SYNC_SEND);
        oos4SyncDownload.writeObject(dataCarrierRequest);
        oos4SyncDownload.flush();
        oos4SyncDownload.reset();

        try {
           ois4SyncDownload = new ObjectInputStream(clientSocket.getInputStream());
         }
         catch (Exception e) {
           logger.error(clientUtil.getStackTrace(e));
         }
        dataCarrierResponse = new FsSocketPacketCarrier();

        while(dontExit){
          int byteRead =-1;
          try{
            try{
               dataCarrierResponse=(FsSocketPacketCarrier)ois4SyncDownload.readObject();
               logger.debug("waiting frm server response..........");
             }catch (Exception e1) {
             dataCarrierResponse=null;
              }
            if(dataCarrierResponse!=null){
              logger.debug("response operation :"+dataCarrierResponse.getOperation());
              switch(dataCarrierResponse.getOperation()){
                
                case SOCKET_SYNC_DOWNLOAD_READY:
                  dataCarrierRequest= new FsSocketPacketCarrier();
                  dataCarrierRequest.setOperation(USER_NAME);
                  dataCarrierRequest.setData(userName.getBytes());
                  dataCarrierRequest.setData2(srcSyncDownloadFilePath);
                 
                  oos4SyncDownload.writeObject(dataCarrierRequest);
                  oos4SyncDownload.flush();
                  oos4SyncDownload.reset();
                
                  logger.debug("sent user name to server 4 socket.................................."); 
                  break;
                case USER_NAME_SET:
                  serverMD5SumOfCurrentDownloadFile=(String)dataCarrierResponse.getData2();
                  
                  dataCarrierRequest= new FsSocketPacketCarrier();
                  dataCarrierRequest.setOperation(SOCKET_START);
                  
                  oos4SyncDownload.writeObject(dataCarrierRequest);
                  oos4SyncDownload.flush();
                  oos4SyncDownload.reset();
                 break;
                case SOCKET_STARTED:
                  fileSyncDownload = new File(destFilePathSyncDownload);
                  if(fileSyncDownload.exists()){
                    logger.debug("download file is exist.................................");
                    fileSyncDownloadBackup= new File(fileSyncDownload.getAbsolutePath()+".fsbak");
                    fileSyncDownload.renameTo(fileSyncDownloadBackup);
                    fileSyncDownload.delete();
                    logger.debug("File Renamed Here " + fileSyncDownloadBackup.getName() + " Size : " +fileSyncDownloadBackup.length() );
//                    fileSyncDownload.createNewFile();
//                    syncDownloadFileOutputStream = new FileOutputStream(fileSyncDownload);
                  }else{
                    if (!fileSyncDownload.getParentFile().exists()) {
                      //                fileDownload.mkdirs();
                    }
                  }
                  fileSyncDownload.createNewFile();
                  syncDownloadFileOutputStream = new FileOutputStream(fileSyncDownload);
                  logger.debug("creating new file 4 download......................");
                  dataCarrierRequest= new FsSocketPacketCarrier();
                  dataCarrierRequest.setOperation(SOCKET_DATA_SENT);
                  oos4SyncDownload.writeObject(dataCarrierRequest);
                  oos4SyncDownload.flush();
                  oos4SyncDownload.reset();
                  break;
                case SOCKET_DATA_RECEIVED:
                  logger.debug("inside client data received.........................");
                  if (syncDownloadFileOutputStream!=null) {
                    logger.debug("inside downloadFileOutputStream not null.........................");
                    byteRead = (int)dataCarrierResponse.getBufferSize();
                    totalByteTransfered += byteRead;
                    Integer percent = new Integer((int)(totalByteTransfered * 100 / totalByteToTransfered));
                    
                    syncDownloadFileOutputStream.write(dataCarrierResponse.getData(),0,byteRead);
                    syncDownloadFileOutputStream.flush();
                    
                    //Fire to Client Gui......
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setData1(percent);
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
                    fsResponse4ClientGui.setResponseCode(PROGRESS);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(PROGRESS),null,fsResponse4ClientGui);
                    
                    dataCarrierRequest= new FsSocketPacketCarrier();
                    dataCarrierRequest.setOperation(SOCKET_DATA_SENT);
                    oos4SyncDownload.writeObject(dataCarrierRequest);
                    oos4SyncDownload.flush();
                    oos4SyncDownload.reset();
                  }
                  break;
                case SOCKET_END:               
                  try{
                   logger.info("File download complete");
                   String clientMD5SumOfCurrentDownloadFile=commonUtil.generateMD5Sum(new FileInputStream(fileSyncDownload));
                   logger.debug("clientMD5SumOfCurrentDownloadFile " + clientMD5SumOfCurrentDownloadFile);
                             
                   logger.debug("serverMD5SumOfCurrentUploadFile " + serverMD5SumOfCurrentDownloadFile);
                    if(clientMD5SumOfCurrentDownloadFile.equals(serverMD5SumOfCurrentDownloadFile)){ 
                      logger.debug("File Downloaded Successful");
                      if (fileSyncDownloadBackup != null) {
                        fileSyncDownloadBackup.delete();
                      }
                      clientMD5SumOfCurrentDownloadFile=null;
                      serverMD5SumOfCurrentDownloadFile=null;
                      
                      //fire 4 client GUI
                      fsResponse4ClientGui = new FsResponse();
                      fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
                      fsResponse4ClientGui.setResponseCode(SOCKET_CLOSE_FILE);
                      fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                      propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(SOCKET_CLOSE_FILE),null,fsResponse4ClientGui);
                      
                      }else{
                        logger.debug("File Downloaded Failure Due To Data Curruption...");
                        if (fileSyncDownloadBackup != null) {
                          fileSyncDownload.delete();
                          fileSyncDownloadBackup.renameTo(fileSyncDownload);
                          fileSyncDownloadBackup.delete();
                        }
                        //response 4 Client  Gui....
                        fsResponse4ClientGui = new FsResponse();
                        fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
                        fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                        fsResponse4ClientGui.setResponseCode(FILE_CURRUPTED);
                        propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(FILE_CURRUPTED),null,fsResponse4ClientGui);
                      }
                      if (syncDownloadFileOutputStream!=null) {
                        syncDownloadFileOutputStream.close();
                      }
                      
                      dataCarrierRequest= new FsSocketPacketCarrier();
                      dataCarrierRequest.setOperation(SOCKET_CLOSED);
                      oos4SyncDownload.writeObject(dataCarrierRequest);
                      oos4SyncDownload.flush();
                      oos4SyncDownload.reset();
                      logger.debug("client is sending socket close...............");
                    }catch (Exception ex) {
                    logger.error(clientUtil.getStackTrace(ex));
                    
                    dataCarrierRequest= new FsSocketPacketCarrier();
                    dataCarrierRequest.setOperation(SOCKET_ERROR);
                    oos4SyncDownload.writeObject(dataCarrierRequest);
                    oos4SyncDownload.flush();
                    oos4SyncDownload.reset();

                    //Response fire to ClientGui........          
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setResponseCode(FAILED);
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(FAILED),null,fsResponse4ClientGui);
                    fsServerHandler.removeSyncDownloadListener(this);
                    }
                    break;
//                case CLOSE_SOCKET:
//                
//                  
//                
//                
//                  dataCarrierRequest= new FsSocketPacketCarrier();
//                  dataCarrierRequest.setOperation(SOCKET_CLOSED);
//                  oos4SyncDownload.writeObject(dataCarrierRequest);
//                  oos4SyncDownload.flush();
//                  oos4SyncDownload.reset();
//                  break;
                case SOCKET_ERROR:
                  //fire 4 client GUI
                  fsResponse4ClientGui = new FsResponse();
                  fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
                  fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
                  fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                  propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse4ClientGui);
                  break;
                
              }//end of switch
            }//end if dataCarrierResponse!=null
             // checking 4 CancleUpload,Exceptions from server......
               if(!syncDownloadEventCollector.isEmpty()){
                 for(int i=0;i<syncDownloadEventCollector.size();i++){
                   PropertyChangeEvent evt = (PropertyChangeEvent)syncDownloadEventCollector.get(i);
                     FsResponse fsResponse = (FsResponse)evt.getNewValue();
                     int propertyName=Integer.valueOf(evt.getPropertyName());
                     switch(propertyName){
                       case STOPPED:
                         logger.debug("handling response 4 cancle.................. ");
                         
                         dataCarrierRequest= new FsSocketPacketCarrier();
                         dataCarrierRequest.setOperation(SOCKET_INTERRUPT);
                         oos4SyncDownload.writeObject(dataCarrierRequest);
                         oos4SyncDownload.flush();
                         oos4SyncDownload.reset();
                         
                         dontExit=false;
                         try {
                           if(syncDownloadFileOutputStream!=null){
                             syncDownloadFileOutputStream.close();
                           }
                           if(ois4SyncDownload!=null){
                             ois4SyncDownload.close();
                           }
                           if(oos4SyncDownload!=null){
                            // oos4SyncDownload.close();
                           }
                           
//                           if(clientSocket!=null){
//                             clientSocket.close();
//                           }
                         }catch (IOException e) {
                           ;
                         }
                         
                         //fire 4 client GUI
                         propertyChangeSupport4FsSyncDownload.firePropertyChange(Integer.toString(STOPPED),null,fsResponse);
                         
                         syncDownloadEventCollector.remove(i);
                         break;
                       case SOCKET_STOP:
                         logger.info("Upload Socket Stop.....");
                         dontExit=false;
                         
                         syncDownloadEventCollector.remove(i);
                         break;
                     }
                 }
                 
               }
          }catch (IOException ioe1) {
             clientUtil.getStackTrace(ioe1); 
             dontExit=false;
           }catch (Exception e) {
             clientUtil.getStackTrace(e);
             dontExit=false;
           } //end of the  upper try
        }//closing while loop..
         syncDownloadEventCollector.clear();
      }catch(IOException ioe2){
        clientUtil.getStackTrace(ioe2);
      }finally {
         try {
           if(syncDownloadFileOutputStream!=null){
             syncDownloadFileOutputStream.close();
           }
           if(oos4SyncDownload!=null){
             oos4SyncDownload.close();
           }
           if(oos4SyncDownload!=null){
             oos4SyncDownload.close();
           }
           
           if(clientSocket!=null){
             //socket.close();
              clientSocket.close();
           }
         }catch (IOException e) {
           ;
         }
       }
    }
    
  }

  private class DataHandler4SyncUploadClient implements Runnable, FsSyncUploadListener{
    private String destFilePathSyncUpload;
    
    private String srcSyncUploadFilePath;
    
    private String superResponseCode;
    
    private PropertyChangeSupport propertyChangeSupport4FsSyncUpload;
    
    private JxtaSocket  clientSocket;
    
    private File fileSyncUpload;
    
    private ArrayList syncUploadEventCollector= new ArrayList();
    
    private long totalByteToTransfered;
    
    private String syncOperation;
    
    private Document documentOfChanges;
    
   // private Document localSyncXMLDocument;
    
    
     public DataHandler4SyncUploadClient(String destFilePathSyncUpload,String srcSyncUploadFilePath,long totalByteToTransfer, String syncOperation, String superResponseCode, PropertyChangeSupport propertyChangeSupport4FsSyncUpload){
       try {
         this.destFilePathSyncUpload=destFilePathSyncUpload;
         this.srcSyncUploadFilePath=srcSyncUploadFilePath;
         this.superResponseCode=superResponseCode;
         this.propertyChangeSupport4FsSyncUpload=propertyChangeSupport4FsSyncUpload;
         this.clientSocket=(new JxtaSocketClient(netPeerGroup,socketAdv,peerId,logger)).createClientSocket();
         this.clientSocket.setOutputStreamBufferSize(commonUtil.JXTA_SOCKET_BUFFER_SIZE);
         this.totalByteToTransfered=totalByteToTransfer;
         this.syncOperation=syncOperation;
         this.documentOfChanges = commonUtil.getEmptyDocumentObject();
         
       }
       catch (IOException ioe) {
         logger.error(clientUtil.getStackTrace(ioe));
       }
     }

     public void propertyChange(PropertyChangeEvent evt){
       logger.debug("events r comming from server for syncUpload command.....");
       int propertyName=Integer.valueOf(evt.getPropertyName());
       FsResponse fsResponse = (FsResponse)evt.getNewValue();
       if(fsResponse.getSuperResponseCode().equals(superResponseCode)){
         logger.debug("Syncupload code are..............: "+ propertyName);
         //handleUpload( propertyName,fsResponse);
         if(propertyName==STOPPED){
           syncUploadEventCollector.add(evt);
           logger.debug("SyncUploading commands r added to array list &&&&&&&&&&&&&&"+propertyName);
         }
       }
     }
 
     public void run(){
       
      logger.debug("insise DataHandler thread...........................start 4 client");
       handleSyncUploadData();
     }

    private void handleSyncUploadData() {
      ObjectInputStream ois4SyncUpload=null;
      ObjectOutputStream oos4SyncUpload=null;
      byte[] buffer  =  new byte[commonUtil.JXTA_SOCKET_BUFFER_SIZE];
      FileInputStream uploadSyncFileInputStream = null;
      FsSocketPacketCarrier dataCarrierRequest = null;
      FsSocketPacketCarrier dataCarrierResponse = null;
      fileSyncUpload = new File(srcSyncUploadFilePath);
      FileSystemView fileSystemView = FileSystemView.getFileSystemView();
      String uploadSyncFilePath=null;
      FsResponse fsResponse4ClientGui=null;
      try{
      
        logger.debug("inside socket 4 Sync Upload...........");
        boolean dontExit=true;
        
        oos4SyncUpload=new ObjectOutputStream(clientSocket.getOutputStream());

        dataCarrierRequest= new FsSocketPacketCarrier();
        dataCarrierRequest.setSuperOperation(SOCKET_SYNC_RECEIVE);
        oos4SyncUpload.writeObject(dataCarrierRequest);
        oos4SyncUpload.flush();
        oos4SyncUpload.reset();
      
        try {
           ois4SyncUpload = new ObjectInputStream(clientSocket.getInputStream());
         }
         catch (Exception e) {
           logger.error(clientUtil.getStackTrace(e));
         }
        dataCarrierResponse = new FsSocketPacketCarrier();
        while(dontExit){
          long byteRead =-1;
          long totalByteTransfered=0;
          Integer percent=0;
          try{
            try{
               dataCarrierResponse=(FsSocketPacketCarrier)ois4SyncUpload.readObject();
               logger.debug("waiting frm server response..........");
             }catch (Exception e1) {
             dataCarrierResponse=null;
              }
            if(dataCarrierResponse!=null){
              logger.debug("response operation :"+dataCarrierResponse.getOperation());
              
              switch(dataCarrierResponse.getOperation()){
                case SOCKET_SYNC_UPLOAD_READY:
                  FsFileHolder fsFileHolder = new FsFileHolder();
                  fsFileHolder.setName(fileSyncUpload.getName());
                  fsFileHolder.setPath(fileSyncUpload.getAbsolutePath());
                  fsFileHolder.setOwner(null);
                  fsFileHolder.setCreationDate(null);
                  fsFileHolder.setModifiedDate(new Date(fileSyncUpload.lastModified()));
                  fsFileHolder.setDescription(fileSystemView.getSystemDisplayName(fileSyncUpload));
                  fsFileHolder.setMimeType(fileSystemView.getSystemTypeDescription(fileSyncUpload));
                  fsFileHolder.setSize(fileSyncUpload.length());
                  
                  logger.debug("fsFileHolder.getPath())....................."+fsFileHolder.getPath());
                  logger.debug("fsFileHolder.getMimeType()....................."+fsFileHolder.getMimeType());
                  logger.debug("fileSystemView.getSystemTypeDescription(fileUpload)....................."+fileSystemView.getSystemTypeDescription(fileSyncUpload));
                           
                  dataCarrierRequest= new FsSocketPacketCarrier();
                  dataCarrierRequest.setOperation(USER_NAME);
                  dataCarrierRequest.setData(userName.getBytes());
                  dataCarrierRequest.setData1(syncOperation.getBytes());
                  dataCarrierRequest.setData2(fsFileHolder);
                  
                  oos4SyncUpload.writeObject(dataCarrierRequest);
                  oos4SyncUpload.flush();
                  oos4SyncUpload.reset();
                  logger.debug("sent user name to server 4 socket.................................."); 
                  break;
                case USER_NAME_SET:
                  logger.debug("user name and fsconnection is set on server......... ");
                  
                  dataCarrierRequest= new FsSocketPacketCarrier();
                  dataCarrierRequest.setOperation(SOCKET_START);
                  dataCarrierRequest.setData2((commonUtil.generateMD5Sum(new FileInputStream(new File(srcSyncUploadFilePath)))));
                  dataCarrierRequest.setTotalDataSize(fileSyncUpload.length());
                  dataCarrierRequest.setAbsolutePath(destFilePathSyncUpload);
                  oos4SyncUpload.writeObject(dataCarrierRequest);
                  oos4SyncUpload.flush();
                  oos4SyncUpload.reset();
                  logger.debug("upload request sent to server socket..................................");
                  break;
                case SOCKET_STARTED:
                  logger.debug("client is sending data to server...............");
                   try {
                     uploadSyncFilePath = fileSyncUpload.getAbsolutePath();
                    
                     uploadSyncFileInputStream = new FileInputStream(uploadSyncFilePath);
                     byteRead = uploadSyncFileInputStream.read(buffer);
                     
                     dataCarrierRequest= new FsSocketPacketCarrier();
                     dataCarrierRequest.setOperation(SOCKET_DATA_SENT);
                     dataCarrierRequest.setData(buffer);
                     dataCarrierRequest.setBufferSize(byteRead);
                     oos4SyncUpload.writeObject(dataCarrierRequest);
                     oos4SyncUpload.flush();
                     oos4SyncUpload.reset();
                   
                   
                     totalByteTransfered += byteRead;
                     percent = new Integer((int)(totalByteTransfered * 100 / totalByteToTransfered));
                     logger.debug("client is sent data to server...............");
                   
                   //Fire to Client Gui......
                   fsResponse4ClientGui = new FsResponse();
                   fsResponse4ClientGui.setData(percent);
                   fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
                   fsResponse4ClientGui.setResponseCode(PROGRESS);
                   fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                   propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(PROGRESS),null,fsResponse4ClientGui);
                    }
                    catch (Exception e) {
                      logger.error(clientUtil.getStackTrace(e));
                    }
                   break;
                case SOCKET_DATA_RECEIVED:
                  byteRead = uploadSyncFileInputStream.read(buffer);
                  dataCarrierRequest= new FsSocketPacketCarrier();
                  
                  if(byteRead!=-1){
                    dataCarrierRequest.setOperation(SOCKET_DATA_SENT);
                    dataCarrierRequest.setData(buffer);
                    dataCarrierRequest.setBufferSize(byteRead);
                    totalByteTransfered += byteRead;
                    percent = new Integer((int)(totalByteTransfered * 100 / totalByteToTransfered));
                    logger.debug("client is sending remaining data to server...............");
                    
                    //Fire to Client Gui......
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setData(percent);
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
                    fsResponse4ClientGui.setResponseCode(PROGRESS);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(PROGRESS),null,fsResponse4ClientGui);
                  }else{
                    dataCarrierRequest.setOperation(SOCKET_END);
                    logger.debug("client is sending Socket END to server...............");
                    
//                    //fire 4 client Gui....
//                    fsResponse4ClientGui = new FsResponse();
//                    fsResponse4ClientGui.setData(uploadFilePath);
//                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
//                    fsResponse4ClientGui.setResponseCode(PROGRESS_BUILDING);
//                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
//                    propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(PROGRESS_BUILDING),null,fsResponse4ClientGui);
                  }
                  oos4SyncUpload.writeObject(dataCarrierRequest);
                  oos4SyncUpload.flush();
                  oos4SyncUpload.reset();  
                  break;
                case SOCKET_ENDED:
                  logger.debug("Socket Close request sent 4 server.....");
                  dontExit=false;
                  File file = new File(srcSyncUploadFilePath);
                  clientUtil.addDocumentElement(documentOfChanges, file, syncOperation);
                  
                  dataCarrierRequest= new FsSocketPacketCarrier();
                  dataCarrierRequest.setOperation(CLOSE_SOCKET);
                  oos4SyncUpload.writeObject(dataCarrierRequest);
                  oos4SyncUpload.flush();
                  oos4SyncUpload.reset();
                  
                  //fire 4 client GUI
                  fsResponse4ClientGui = new FsResponse();
                  fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
                  fsResponse4ClientGui.setResponseCode(SOCKET_CLOSE_FILE);
                  fsResponse4ClientGui.setData(documentOfChanges);
                  fsResponse4ClientGui.setData1(dataCarrierResponse.getData2());
                  fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                  propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(SOCKET_CLOSE_FILE),null,fsResponse4ClientGui);
                  documentOfChanges=null;
                  //fsServerHandler.removeSyncUploadListener(this);
                  break;
                case SOCKET_FILE_CURRUPTED:
                  dontExit=false;
                  
                  fsResponse4ClientGui = new FsResponse();
                  fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
                  fsResponse4ClientGui.setResponseCode(FILE_CURRUPTED);
                  fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                  propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(FILE_CURRUPTED),null,fsResponse4ClientGui);
                  
                  fsServerHandler.removeSyncUploadListener(this);
                  break;
                case SOCKET_ERROR:
                  //fire 4 client GUI
                  fsResponse4ClientGui = new FsResponse();
                  fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
                  fsResponse4ClientGui.setFsExceptionHolder(dataCarrierResponse.getFsExceptionHolder4Socket());
                  fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
                  fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                  propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse4ClientGui);
                  
                  fsServerHandler.removeSyncUploadListener(this);
                  break;
                case SOCKET_FAILED:
                  //fire 4 client GUI
                  fsResponse4ClientGui = new FsResponse();
                  fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
                  fsResponse4ClientGui.setResponseCode(FAILED);
                  fsResponse4ClientGui.setData(dataCarrierResponse.getData());
                  fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                  propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(FAILED),null,fsResponse4ClientGui);
                  
                  fsServerHandler.removeSyncUploadListener(this);
                  break;
                
                
              }//closing switch 
            }// closing dataCarrierResponse!=null
             // checking 4 CancleUpload,Exceptions from server......
               if(!syncUploadEventCollector.isEmpty()){
                 for(int i=0;i<syncUploadEventCollector.size();i++){
                   PropertyChangeEvent evt = (PropertyChangeEvent)syncUploadEventCollector.get(i);
                     FsResponse fsResponse = (FsResponse)evt.getNewValue();
                     int propertyName=Integer.valueOf(evt.getPropertyName());
                     switch(propertyName){
                       case STOPPED:
                         logger.debug("handling response 4 cancle.................. ");
                         
                         dataCarrierRequest= new FsSocketPacketCarrier();
                         dataCarrierRequest.setOperation(SOCKET_INTERRUPT);
                         oos4SyncUpload.writeObject(dataCarrierRequest);
                         oos4SyncUpload.flush();
                         oos4SyncUpload.reset();
                         
                         dontExit=false;
                         try {
                           if(uploadSyncFileInputStream!=null){
                             uploadSyncFileInputStream.close();
                           }
                           if(ois4SyncUpload!=null){
                             ois4SyncUpload.close();
                           }
                           if(oos4SyncUpload!=null){
                             //oos4SyncUpload.close();
                           }
                           
//                           if(clientSocket!=null){
//                             clientSocket.close();
//                           }
                         }catch (IOException e) {
                           ;
                         }
                         
                         //fire 4 client GUI
                         propertyChangeSupport4FsSyncUpload.firePropertyChange(Integer.toString(STOPPED),null,fsResponse);
                         
                         syncUploadEventCollector.remove(i);
                         break;
                       case SOCKET_STOP:
                         logger.info("Upload Socket Stop.....");
                         dontExit=false;
                         
                         syncUploadEventCollector.remove(i);
                         break;
                       

                     }
                 }
                 
               }
          }catch (IOException ioe1) {
             clientUtil.getStackTrace(ioe1); 
             dontExit=false;
           }catch (Exception e) {
             clientUtil.getStackTrace(e);
             dontExit=false;
           } //end of the  upper try 
        }//closing while loop
         syncUploadEventCollector.clear();
        
      }catch (IOException ioe2) {
        clientUtil.getStackTrace(ioe2);
      }finally {
         try {
           if(uploadSyncFileInputStream!=null){
             uploadSyncFileInputStream.close();
           }
           if(ois4SyncUpload!=null){
             ois4SyncUpload.close();
           }
           if(oos4SyncUpload!=null){
             oos4SyncUpload.close();
           }
           
           if(clientSocket!=null){
             //socket.close();
              clientSocket.close();
           }
         }catch (IOException e) {
           ;
         }
       }
    }
     
  }

  private class DataHandler4DownloadClient implements Runnable, FsDownloadListener{
     
     private String destFilePathDownload;
     
     private String srcFilePathToDownload;
     
     private String superResponseCode;
     
     private PropertyChangeSupport propertyChangeSupport4FsDownload;
     
     private JxtaSocket  clientSocket;
     
     private File fileDownload=null;
     
     private ArrayList downloadEventCollector= new ArrayList();
     
     private File fileDownloadBackup=null;
     
     private boolean dontExit=true;
     
     public DataHandler4DownloadClient(String destFilePathDownload,String srcFilePathToDownload, String superResponseCode, PropertyChangeSupport propertyChangeSupport4FsDownload){
       try {
         this.destFilePathDownload=destFilePathDownload;
         this.srcFilePathToDownload=srcFilePathToDownload;
         this.superResponseCode=superResponseCode;
         this.propertyChangeSupport4FsDownload=propertyChangeSupport4FsDownload;
         this.clientSocket=(new JxtaSocketClient(netPeerGroup,socketAdv,peerId,logger)).createClientSocket();
         this.clientSocket.setOutputStreamBufferSize(commonUtil.JXTA_SOCKET_BUFFER_SIZE);
       }
       catch (IOException ioe) {
         logger.error(clientUtil.getStackTrace(ioe));
       }
     }
     
     
     public void propertyChange(PropertyChangeEvent evt){
       logger.debug("events r comming from server for download command.....");
       int propertyName=Integer.valueOf(evt.getPropertyName());
       FsResponse fsResponse = (FsResponse)evt.getNewValue();
       if(fsResponse.getSuperResponseCode().equals(superResponseCode)){
         logger.debug("download code are..............: "+ propertyName);
         //handleUpload( propertyName,fsResponse);
         if(propertyName==CANCLED||propertyName==ERROR_MESSAGE){
           downloadEventCollector.add(evt);
           logger.debug("download commands r added to array list &&&&&&&&&&&&&&"+propertyName);
         }
        
       }
     }
    
     public void run(){
       handleDownloadData();
     }

    private void handleDownloadData() {
      ObjectInputStream ois4Download=null;
      ObjectOutputStream oos4Download=null;
      FileOutputStream downloadFileOutputStream=null;
      FsSocketPacketCarrier dataCarrierRequest = null;
      FsSocketPacketCarrier dataCarrierResponse = null;
      
      FsResponse fsResponse4ClientGui=null;
      String serverMD5SumOfCurrentDownloadFile=null;
      boolean isFile4Overwrite=false;
      
      try{
        
        logger.debug("inside Client socket sending 4 download...........");
        
        
        oos4Download=new ObjectOutputStream(clientSocket.getOutputStream());

        dataCarrierRequest= new FsSocketPacketCarrier();
        dataCarrierRequest.setSuperOperation(SOCKET_SEND);
        oos4Download.writeObject(dataCarrierRequest);
        oos4Download.flush();
        oos4Download.reset();
         
        try {
           ois4Download = new ObjectInputStream(clientSocket.getInputStream());
         }
         catch (Exception e) {
           logger.error(clientUtil.getStackTrace(e));
         }
        dataCarrierResponse = new FsSocketPacketCarrier();
        while(dontExit){
          int byteRead =-1;
          try{
            try{
               dataCarrierResponse=(FsSocketPacketCarrier)ois4Download.readObject();
               logger.debug("waiting frm server response..........");
             }catch (Exception e1) {
             dataCarrierResponse=null;
              }
            if(dataCarrierResponse!=null){
              logger.debug("response operation :"+dataCarrierResponse.getOperation());
              
              switch(dataCarrierResponse.getOperation()){
                case SOCKET_DOWNLOAD_READY:
                  dataCarrierRequest= new FsSocketPacketCarrier();
                  dataCarrierRequest.setOperation(USER_NAME);
                  dataCarrierRequest.setData(userName.getBytes());
                  dataCarrierRequest.setData2(srcFilePathToDownload);
                 
                  oos4Download.writeObject(dataCarrierRequest);
                  oos4Download.flush();
                  oos4Download.reset();
  
                  logger.debug("sent user name to server 4 socket..................................");
                  
                 
                  break;
                case USER_NAME_SET:
                  serverMD5SumOfCurrentDownloadFile=(String)dataCarrierResponse.getData2();
                  
                  dataCarrierRequest= new FsSocketPacketCarrier();
                  dataCarrierRequest.setOperation(SOCKET_START);
                  
                  oos4Download.writeObject(dataCarrierRequest);
                  oos4Download.flush();
                  oos4Download.reset();
                 break;
                case SOCKET_STARTED:
                  fileDownload = new File(destFilePathDownload);
                  if(fileDownload.exists()){
                    logger.debug("download file is exist.................................");
                    isFile4Overwrite=true;
                    fileDownloadBackup= new File(fileDownload.getAbsolutePath()+".fsbak");
                    fileDownload.renameTo(fileDownloadBackup);
                    fileDownload.delete();
                    logger.debug("File Renamed Here " + fileDownloadBackup.getName() + " Size : " +fileDownloadBackup.length() );
                    fileDownload.createNewFile();
                    downloadFileOutputStream = new FileOutputStream(fileDownload);
                  }else{
                    fileDownload.createNewFile();
                    downloadFileOutputStream = new FileOutputStream(fileDownload);
                    logger.debug("creating new file 4 download......................");
                    
                    //firing to client gui........
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setData(fileDownload.getPath());
                    fsResponse4ClientGui.setData1(fileDownload.getName());
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
                    fsResponse4ClientGui.setResponseCode(CREATE_EMPTY_FILE);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(CREATE_EMPTY_FILE),null,fsResponse4ClientGui);
                    
                  }
                  dataCarrierRequest= new FsSocketPacketCarrier();
                  dataCarrierRequest.setOperation(SOCKET_DATA_SENT);
                  
                  oos4Download.writeObject(dataCarrierRequest);
                  oos4Download.flush();
                  oos4Download.reset();
                  break;
                case SOCKET_DATA_RECEIVED:
                  logger.debug("inside client data received.........................");
                  if (downloadFileOutputStream!=null) {
                    logger.debug("inside downloadFileOutputStream not null.........................");
                    byteRead = (int)dataCarrierResponse.getBufferSize();
                    int prevByteRead = byteRead;
                    downloadFileOutputStream.write(dataCarrierResponse.getData(),0,byteRead);
                    downloadFileOutputStream.flush();
                    logger.debug("prevByteRead..................."+prevByteRead);
                   
                    dataCarrierRequest= new FsSocketPacketCarrier();
                    dataCarrierRequest.setOperation(SOCKET_DATA_SENT);
                    
                    oos4Download.writeObject(dataCarrierRequest);
                    oos4Download.flush();
                    oos4Download.reset();
                    
                    //Fire to Client Gui......
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setData(new Integer(prevByteRead));
                   // fsResponse4ClientGui.setData1(fileDownload.getPath());
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
                    fsResponse4ClientGui.setResponseCode(PROGRESS);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(PROGRESS),null,fsResponse4ClientGui);
                  }
                  break;
                case SOCKET_END:               
                  try{
                   logger.info("File download complete");
                   String clientMD5SumOfCurrentDownloadFile=commonUtil.generateMD5Sum(new FileInputStream(fileDownload));
                   logger.debug("clientMD5SumOfCurrentDownloadFile " + clientMD5SumOfCurrentDownloadFile);
                             
                   logger.debug("serverMD5SumOfCurrentUploadFile " + serverMD5SumOfCurrentDownloadFile);
                   
                   if(isFile4Overwrite){
                     if(clientMD5SumOfCurrentDownloadFile.equals(serverMD5SumOfCurrentDownloadFile)){ 
                       
                       fileDownloadBackup.delete();
                       logger.debug("Inside file Download backup delete.... close file..... ");
                     }else{
                       fileDownload.delete();
                       fileDownloadBackup.renameTo(fileDownload);
                       fileDownloadBackup.delete();
                     }          
                   }
                    
                    downloadFileOutputStream.close();
                    
                    //Response fire to ClientGui........          
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setResponseCode(PROGRESS_BUILDING);
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(PROGRESS_BUILDING),null,fsResponse4ClientGui);
                    
                    dataCarrierRequest= new FsSocketPacketCarrier();
                    dataCarrierRequest.setOperation(SOCKET_ENDED);
                    oos4Download.writeObject(dataCarrierRequest);
                    oos4Download.flush();
                    oos4Download.reset();
                    logger.debug("client is sending socket ended...............");
                  }catch (Exception ex) {
                    logger.error(clientUtil.getStackTrace(ex));
                    
                    dataCarrierRequest= new FsSocketPacketCarrier();
                    dataCarrierRequest.setOperation(SOCKET_ERROR);
                    oos4Download.writeObject(dataCarrierRequest);
                    oos4Download.flush();
                    oos4Download.reset();

                    //Response fire to ClientGui........          
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setResponseCode(FAILED);
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(FAILED),null,fsResponse4ClientGui);
                    fsServerHandler.removeDownloadListener(this);
                  }
                  break;
                case CLOSE_SOCKET:
                  dontExit=false;
                  if(isFile4Overwrite){
                    logger.debug("isFile4Overwrite value is true.......... ");
                    fileDownloadBackup.renameTo(fileDownload);
                    fileDownloadBackup.delete();          
                  }
                  else{
                  if (fileDownload.exists()) {
                    logger.debug("fileDownload value is exist else part........... ");
                  //fileDownload.delete();
                  }
                  }
                   isFile4Overwrite=false;
                   
//                   dataCarrierRequest= new FsSocketPacketCarrier();
//                   dataCarrierRequest.setOperation(SOCKET_CLOSED);
//                   oos4Download.writeObject(dataCarrierRequest);
//                   oos4Download.flush();
//                   oos4Download.reset();
                   
                  //fire 4 client GUI
                  fsResponse4ClientGui = new FsResponse();
                  fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
                  fsResponse4ClientGui.setResponseCode(CLOSE_FILE);
                  fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                  propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(CLOSE_FILE),null,fsResponse4ClientGui);
                  
                  try {
                    if (downloadFileOutputStream!=null) {
                      downloadFileOutputStream.close();
                    } if(ois4Download!=null){
                      ois4Download.close();
                    }
                    if(oos4Download!=null){
                     // oos4Download.close();
                    }
                    
//                    if(clientSocket!=null){
//                      clientSocket.close();
//                    }
                    } catch (IOException ioe) {
                      logger.error(clientUtil.getStackTrace(ioe));
                    }
                   break;
                case SOCKET_ERROR:
                  //fire 4 client GUI
                  fsResponse4ClientGui = new FsResponse();
                  fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.DOWNLOAD);
                  fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
                  fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                  propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse4ClientGui);
                  break;
              }//end of swotch case

            }// end of if dataCarrierResponse!=null
             // checking 4 CancleUpload,Exceptions from server......
             
               if(!downloadEventCollector.isEmpty()){
                 for(int i=0;i<downloadEventCollector.size();i++){
                   PropertyChangeEvent evt = (PropertyChangeEvent)downloadEventCollector.get(i);
                     FsResponse fsResponse = (FsResponse)evt.getNewValue();
                     int propertyName=Integer.valueOf(evt.getPropertyName());
                     switch(propertyName){
                       case CANCLED:
                         logger.debug("handling response 4 cancle.................. ");
                         
                         dataCarrierRequest= new FsSocketPacketCarrier();
                         dataCarrierRequest.setOperation(SOCKET_INTERRUPT);
                         oos4Download.writeObject(dataCarrierRequest);
                         oos4Download.flush();
                         oos4Download.reset();
                         
                         dontExit=false;
                         isFile4Overwrite=false;
                         
                         //fire 4 client GUI
                         propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(CANCLED),null,fsResponse);
                         if(isFile4Overwrite){
                           logger.debug("isFile4Overwrite value is true.......... ");
                           fileDownloadBackup.renameTo(fileDownload);
                           fileDownloadBackup.delete();          
                         }else{
                           if (fileDownload.exists()) {
                             logger.debug("fileDownload value is exist else part........... ");
                             fileDownload.delete();
                           }
                         }
                         try {
                           if(downloadFileOutputStream!=null){
                             downloadFileOutputStream.close();
                           }
                           if(ois4Download!=null){
                            // ois4Download.close();
                           }
                           if(oos4Download!=null){
                             //oos4Download.flush();
                           }
                           
//                           if(clientSocket!=null){
//                             clientSocket.close();
//                           }
                         }catch (IOException e) {
                           ;
                         }
                         downloadEventCollector.remove(i);
                         break;
                       case SOCKET_STOP:
                         logger.info("Upload Socket Stop.....");
                         dontExit=false;
                         
                         downloadEventCollector.remove(i);
                         break;
//                      case COMPLETED:
//                      
//                       propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(COMPLETED),null,fsResponse);
//                       
//                       downloadEventCollector.remove(i);
//                       logger.debug("Download Completed...");
//                       break;
                      case ERROR_MESSAGE:
                       propertyChangeSupport4FsDownload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse);
                       downloadEventCollector.remove(i);
                       break;
                    
                     }
                 }
                 
               }
            
          }catch (IOException ioe1) {
             clientUtil.getStackTrace(ioe1); 
             dontExit=false;
           }catch (Exception e) {
             clientUtil.getStackTrace(e);
             dontExit=false;
           } //end of the  upper try  
        }// closing while loop
         downloadEventCollector.clear(); ///clearing the array list.........
        
      }catch(IOException ioe2){
        clientUtil.getStackTrace(ioe2);
      }finally {
         try {
           if(downloadFileOutputStream!=null){
             downloadFileOutputStream.close();
           }
           if(ois4Download!=null){
             ois4Download.close();
           }
           if(oos4Download!=null){
             oos4Download.close();
           }
           
           if(clientSocket!=null){
             clientSocket.close();
           }
         }catch (IOException e) {
           ;
         }
       }
    
    }
    
  }


  private class DataHandler4UploadClient implements Runnable, FsUploadListener{
    
    private int socketConstant;
    
    private JxtaSocket  clientSocket;
    
    private String destFilePathUpload;
    
    private String srcUploadFilePath;
    
    private File fileUpload;
    
    //private FsResponse fsResponse;
    
    private PropertyChangeSupport propertyChangeSupport4FsUpload;
    
    private String superResponseCode;
    
    private ArrayList uploadEventCollector= new ArrayList();
    
   
    public DataHandler4UploadClient(int socketConstant,String destFilePathUpload, String srcUploadFilePath, String superResponseCode, PropertyChangeSupport propertyChangeSupport4FsUpload){
      try {
        this.socketConstant=socketConstant;
        this.clientSocket=(new JxtaSocketClient(netPeerGroup,socketAdv,peerId,logger)).createClientSocket();
        this.clientSocket.setOutputStreamBufferSize(commonUtil.JXTA_SOCKET_BUFFER_SIZE);
        this.destFilePathUpload=destFilePathUpload;
        this.srcUploadFilePath=srcUploadFilePath;
        this.propertyChangeSupport4FsUpload=propertyChangeSupport4FsUpload;
        this.superResponseCode=superResponseCode;
      }
      catch (Exception ioe) {
        logger.error(clientUtil.getStackTrace(ioe));
      }
    }
    
    public void propertyChange(PropertyChangeEvent evt){
      logger.debug("events r comming from server for upload command.....");
      int propertyName=Integer.valueOf(evt.getPropertyName());
      FsResponse fsResponse = (FsResponse)evt.getNewValue();
      if(fsResponse.getSuperResponseCode().equals(superResponseCode)){
        logger.debug("upload code are..............: "+ propertyName);
        //handleUpload( propertyName,fsResponse);
        if(propertyName==CANCLED){
          uploadEventCollector.add(evt);
          logger.debug("uploading commands r added to array list &&&&&&&&&&&&&&"+propertyName);
        }
       
      }
    
    }
    
    public void run(){
      try {
        logger.debug("insise DataHandler thread...........................start 4 client");
        switch(socketConstant){
        case SOCKET_RECEIVE:
          handleUploadData();
          break;
        case SOCKET_SEND:
          //handleDownloadData();
          //code 4 download...
          break;
        }
      }catch (Exception e) {
        clientUtil.getStackTrace(e);
      }
    }

    private void handleUploadData() {
      ObjectInputStream ois4Upload=null;
      ObjectOutputStream oos4Upload=null;
      byte[] buffer  =  new byte[commonUtil.JXTA_SOCKET_BUFFER_SIZE];
      FileInputStream uploadFileInputStream = null;
      FsSocketPacketCarrier dataCarrierRequest = null;
      FsSocketPacketCarrier dataCarrierResponse = null;
      fileUpload = new File(srcUploadFilePath);
      FileSystemView fileSystemView = FileSystemView.getFileSystemView();
      String uploadFilePath=null;
      FsResponse fsResponse4ClientGui=null;
    try {
        
          logger.debug("inside socket receive 4 upload...........");
          boolean dontExit=true;
          
          oos4Upload=new ObjectOutputStream(clientSocket.getOutputStream());

          dataCarrierRequest= new FsSocketPacketCarrier();
          dataCarrierRequest.setSuperOperation(SOCKET_RECEIVE);
          oos4Upload.writeObject(dataCarrierRequest);
          oos4Upload.flush();
          oos4Upload.reset();

         try {
            ois4Upload = new ObjectInputStream(clientSocket.getInputStream());
          }
          catch (Exception e) {
            logger.error(clientUtil.getStackTrace(e));
          }
         dataCarrierResponse = new FsSocketPacketCarrier();
         while(dontExit){
            long byteRead =-1;
            long total=0;
            try{
             try{
                dataCarrierResponse=(FsSocketPacketCarrier)ois4Upload.readObject();
                logger.debug("waiting frm server response..........");
              }catch (Exception e1) {
              dataCarrierResponse=null;
               }
              if(dataCarrierResponse!=null){
                logger.debug("response operation :"+dataCarrierResponse.getOperation());
                
                switch(dataCarrierResponse.getOperation()){
                  
                  case SOCKET_UPLOAD_READY:
                    FsFileHolder fsFileHolder = new FsFileHolder();
                    fsFileHolder.setName(fileUpload.getName());
                    fsFileHolder.setPath(fileUpload.getAbsolutePath());
                    fsFileHolder.setOwner(null);
                    fsFileHolder.setCreationDate(null);
                    fsFileHolder.setModifiedDate(new Date(fileUpload.lastModified()));
                    fsFileHolder.setDescription(fileSystemView.getSystemDisplayName(fileUpload));
                    fsFileHolder.setMimeType(fileSystemView.getSystemTypeDescription(fileUpload));
                    fsFileHolder.setSize(fileUpload.length());
                    
                    logger.debug("fsFileHolder.getPath())....................."+fsFileHolder.getPath());
                    logger.debug("fsFileHolder.getMimeType()....................."+fsFileHolder.getMimeType());
                    logger.debug("fileSystemView.getSystemTypeDescription(fileUpload)....................."+fileSystemView.getSystemTypeDescription(fileUpload));
                             
                    dataCarrierRequest= new FsSocketPacketCarrier();
                    dataCarrierRequest.setOperation(USER_NAME);
                    dataCarrierRequest.setData(userName.getBytes());
                    dataCarrierRequest.setData2(fsFileHolder);
                    
                    oos4Upload.writeObject(dataCarrierRequest);
                    oos4Upload.flush();
                    oos4Upload.reset();
                    
                    logger.debug("sent user name to server 4 socket.................................."); 
                    break;
                  case USER_NAME_SET:
                    logger.debug("user name and fsconnection is set on server......... ");
                    
                    dataCarrierRequest= new FsSocketPacketCarrier();
                    dataCarrierRequest.setOperation(SOCKET_START);
                    dataCarrierRequest.setData2((commonUtil.generateMD5Sum(new FileInputStream(new File(srcUploadFilePath)))));
                    dataCarrierRequest.setTotalDataSize(fileUpload.length());
                    dataCarrierRequest.setAbsolutePath(destFilePathUpload);
                    oos4Upload.writeObject(dataCarrierRequest);
                    oos4Upload.flush();
                    oos4Upload.reset();
                    logger.debug("upload request sent to server socket..................................");
                    break;
                case SOCKET_STARTED:
                   logger.debug("client is sending to data to server...............");
                    uploadFilePath = fileUpload.getAbsolutePath();
                   
                    uploadFileInputStream = new FileInputStream(uploadFilePath);
                    byteRead = uploadFileInputStream.read(buffer);
                    
                    dataCarrierRequest= new FsSocketPacketCarrier();
                    dataCarrierRequest.setOperation(SOCKET_DATA_SENT);
                    dataCarrierRequest.setData(buffer);
                    dataCarrierRequest.setBufferSize(byteRead);
                    oos4Upload.writeObject(dataCarrierRequest);
                    oos4Upload.flush();
                    oos4Upload.reset();
                    total+=byteRead;
                    logger.debug("client is sent data to server...............");
                    
                    //Fire to Client Gui......
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setData(new Integer((int)total));
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
                    fsResponse4ClientGui.setResponseCode(PROGRESS);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(PROGRESS),null,fsResponse4ClientGui);
                    
                    break;
                  case SOCKET_DATA_RECEIVED:
                    byteRead = uploadFileInputStream.read(buffer);
                    dataCarrierRequest= new FsSocketPacketCarrier();
                    
                    if(byteRead!=-1){
                      dataCarrierRequest.setOperation(SOCKET_DATA_SENT);
                      dataCarrierRequest.setData(buffer);
                      dataCarrierRequest.setBufferSize(byteRead);
                      total+=byteRead;
                      logger.debug("client is sending remaining data to server...............");
                      
                      //fire to Client Gui..........
                      fsResponse4ClientGui = new FsResponse();
                      fsResponse4ClientGui.setData(new Integer((int)total));
                      fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
                      fsResponse4ClientGui.setResponseCode(PROGRESS);
                      fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                      propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(PROGRESS),null,fsResponse4ClientGui);
                    }else{
                      dataCarrierRequest.setOperation(SOCKET_END);
                      logger.debug("client is sending Socket END to server...............");
                      
                      //fire 4 client Gui....
                      fsResponse4ClientGui = new FsResponse();
                      fsResponse4ClientGui.setData(uploadFilePath);
                      fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
                      fsResponse4ClientGui.setResponseCode(PROGRESS_BUILDING);
                      fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                      propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(PROGRESS_BUILDING),null,fsResponse4ClientGui);
                    }
                    oos4Upload.writeObject(dataCarrierRequest);
                    oos4Upload.flush();
                    oos4Upload.reset();  
                    break;
                  case SOCKET_INTERRUPT:
                    dontExit=false;
                    break;
                  case SOCKET_ENDED:
                    logger.debug("Socket Close request sent 4 server.....");
                    dontExit=false;
                    
                    dataCarrierRequest= new FsSocketPacketCarrier();
                    dataCarrierRequest.setOperation(CLOSE_SOCKET);
                    oos4Upload.writeObject(dataCarrierRequest);
                    oos4Upload.flush();
                    oos4Upload.reset();
                    
                    //fire 4 client GUI
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
                    fsResponse4ClientGui.setResponseCode(SOCKET_CLOSE_FILE);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(SOCKET_CLOSE_FILE),null,fsResponse4ClientGui);
                    
                    fsServerHandler.removeUploadListener(this);
                    break;
                  case SOCKET_ERROR:
                    //fire 4 client GUI
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
                    fsResponse4ClientGui.setResponseCode(ERROR_MESSAGE);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(ERROR_MESSAGE),null,fsResponse4ClientGui);
                   
                    fsServerHandler.removeUploadListener(this);
                    break;
                  case SOCKET_FILE_CURRUPTED:
                    dontExit=false;
                    
                    fsResponse4ClientGui = new FsResponse();
                    fsResponse4ClientGui.setOperation(FsRemoteOperationConstants.UPLOAD);
                    fsResponse4ClientGui.setResponseCode(FILE_CURRUPTED);
                    fsResponse4ClientGui.setSuperResponseCode(superResponseCode);
                    propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(FILE_CURRUPTED),null,fsResponse4ClientGui);
                    
                    fsServerHandler.removeUploadListener(this);
                    break;
                }
                
                
              } //closing if statement for dataCarrierResponse!=null
              // checking 4 CancleUpload,Exceptions from server......
             
                if(!uploadEventCollector.isEmpty()){
                  for(int i=0;i<uploadEventCollector.size();i++){
                    PropertyChangeEvent evt = (PropertyChangeEvent)uploadEventCollector.get(i);
                      FsResponse fsResponse = (FsResponse)evt.getNewValue();
                      int propertyName=Integer.valueOf(evt.getPropertyName());
                      switch(propertyName){
                        case CANCLED:
                          logger.debug("handling response 4 cancle.................. ");
                          
                          dataCarrierRequest= new FsSocketPacketCarrier();
                          dataCarrierRequest.setOperation(SOCKET_INTERRUPT);
                          oos4Upload.writeObject(dataCarrierRequest);
                          oos4Upload.flush();
                          oos4Upload.reset();
                          
                          dontExit=false;
                          try {
                            if(uploadFileInputStream!=null){
                              uploadFileInputStream.close();
                            }
                            if(ois4Upload!=null){
                              ois4Upload.close();
                            }
                            if(oos4Upload!=null){
                             // oos4Upload.close();
                            }
                            
//                            if(clientSocket!=null){
//                              clientSocket.close();
//                            }
                          }catch (IOException e) {
                            ;
                          }
                          
                          //fire 4 client GUI
                          propertyChangeSupport4FsUpload.firePropertyChange(Integer.toString(CANCLED),null,fsResponse);
                          
                          uploadEventCollector.remove(i);
                          break;
                        case SOCKET_STOP:
                          logger.info("Upload Socket Stop.....");
                          dontExit=false;
                          
                          uploadEventCollector.remove(i);
                          break;

                      }
                  }
                  
                }
              
              
           }catch (IOException ioe1) {
             clientUtil.getStackTrace(ioe1); 
             dontExit=false;
           }catch (Exception e) {
             clientUtil.getStackTrace(e);
             dontExit=false;
           } //end of the  upper try  
          }// closing while loop
          uploadEventCollector.clear(); ///clearing the array list.........
        
      }
      catch (IOException ioe2) {
        clientUtil.getStackTrace(ioe2);
      }finally {
         try {
           if(uploadFileInputStream!=null){
             uploadFileInputStream.close();
           }
           if(ois4Upload!=null){
             ois4Upload.close();
           }
           if(oos4Upload!=null){
             oos4Upload.close();
           }
           
           if(clientSocket!=null){
             clientSocket.close();
             //socket.close();
           }
         }catch (IOException e) {
           ;
         }
       }
     }
   }

 }
