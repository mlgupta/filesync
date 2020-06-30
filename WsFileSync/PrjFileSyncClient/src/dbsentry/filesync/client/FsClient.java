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
 * $Id:$
 *****************************************************************************
*/
package dbsentry.filesync.client;

import dbsentry.filesync.client.jxta.JxtaClient;
import dbsentry.filesync.common.FsFilePropertyPageRemote;
import dbsentry.filesync.common.FsFolderPropertyPageRemote;
import dbsentry.filesync.common.FsMessage;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsUser;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsAuthenticationListener;
import dbsentry.filesync.common.listeners.FsConnectionListener;
import dbsentry.filesync.common.listeners.FsDisconnectionListener;
import dbsentry.filesync.common.listeners.FsDownloadListener;
import dbsentry.filesync.common.listeners.FsRemoteCommandListener;
import dbsentry.filesync.common.listeners.FsRemoteCopyListener;
import dbsentry.filesync.common.listeners.FsRemoteMoveListener;
import dbsentry.filesync.common.listeners.FsSyncCommandListener;
import dbsentry.filesync.common.listeners.FsSyncDownloadListener;
import dbsentry.filesync.common.listeners.FsSyncUploadListener;
import dbsentry.filesync.common.listeners.FsUploadListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Stack;

import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;


/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description
 * @Date of creation: 13-03-2006
 * @Last Modfied by :Saurabh Gupta
 * @Last Modfied Date:04-08-2006
 */
public class FsClient{
  private JxtaClient jxtaClient = null;
  
  private FsServerHandler fsServerHandler;
  
  private Logger logger;
  
  private ClientUtil clientUtil;
  
  private String userCache;
  
  private FsClient fsClient=this;
  
  private FsUser fsUser;
  
  private PipeAdvertisement socketAdv=null;

  public FsClient(String userCache, FsUser fsUser) {
    this.userCache = userCache;
    this.logger = Logger.getLogger("ClientLogger");
    logger.debug("inside FsClient Constructor");
    this.clientUtil = new ClientUtil(logger);
    this.fsUser=fsUser;
  }

  public void setRemoteCommandListener(FsRemoteCommandListener remoteCommandListener){
    fsServerHandler.setRemoteCommandListener(remoteCommandListener);
  }
  
  public void addRemoteBrowserCommandListener(FsRemoteCommandListener remoteCommandListener){  
    fsServerHandler.addRemoteBrowserCommandListener(remoteCommandListener);                    
  }
  
   public void removeRemoteBrowserCommandListener(FsRemoteCommandListener remoteCommandListener){  
     fsServerHandler.removeRemoteBrowserCommandListener(remoteCommandListener);                    
   }
     
  public void addSyncCommandListener(FsSyncCommandListener syncCommandListener){
    fsServerHandler.addSyncCommandListener(syncCommandListener);
  }
  
  public void removeSyncCommandListener(FsSyncCommandListener syncCommandListener){
    fsServerHandler.removeSyncCommandListener(syncCommandListener);
  }
  /*
  public PropertyChangeSupport getPropertyChangeSupport4Upload(){
    return fsServerHandler.getPropertyChangeSupport4Upload();
  }

  public PropertyChangeSupport getPropertyChangeSupport4Download(){
    return fsServerHandler.getPropertyChangeSupport4Download();
  }
  
  public PropertyChangeSupport getPropertyChangeSupport4RemoteCopy(){
    return fsServerHandler.getPropertyChangeSupport4RemoteCopy();
  }
  
  public PropertyChangeSupport getPropertyChangeSupport4RemoteMove(){
    return fsServerHandler.getPropertyChangeSupport4RemoteMove();
  }
  */
  public void launchClient(FsConnectionListener connectListener,ArrayList rdvList,ArrayList relayList,String socketIdString){

     try {
       File jxtaGroupConfig = new File("config/jxta_group_config.xml");
  
       File platformConfig = new File("config/PlatformConfig.master");
  
         JxtaClient jxtaClient =
             new JxtaClient(logger, jxtaGroupConfig, platformConfig,rdvList,relayList);
         jxtaClient.addJxtaConnectionListener(new JxtaConnectionListener(connectListener));
         Thread jxtaThread = new Thread(jxtaClient);
         jxtaThread.start();
         
      // Preparing socket Adv 
      String socketAdvString=" <?xml version=\"1.0\"?> ";
      socketAdvString+=" <!DOCTYPE jxta:PipeAdvertisement> ";
      socketAdvString+=" <jxta:PipeAdvertisement xmlns:jxta=\"http://jxta.org\"><Id>";
      socketAdvString+=  socketIdString;
      socketAdvString+=" </Id><Type>JxtaUnicast</Type> ";
      socketAdvString+=" <Name>FileSync</Name> ";
      socketAdvString+=" </jxta:PipeAdvertisement> ";
      
      XMLDocument xmlDoc = (XMLDocument)StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, new ByteArrayInputStream(socketAdvString.getBytes()));
      socketAdv = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(xmlDoc);
     }
     catch (IOException e) {
       clientUtil.getStackTrace(e);
     }
  }
  
  public void connect(String peerIdString,String pipeIdString){
  
    if(jxtaClient!=null){
     
      jxtaClient.createJxtaBiDiPipe(peerIdString,pipeIdString);
      
      
    }
  }
  
  public void closePipe(){
    jxtaClient.closeBidipipe();
  }
  
  public void disconnect(FsDisconnectionListener disconnectionListener){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.DISCONNECTION);
    fsRequest.setRequestCode(FsDisconnectionListener.DISCONNECT);
    fsRequest.setSuperRequestCode(Integer.toString(FsDisconnectionListener.DISCONNECT));
    
    fsServerHandler.addDisconnetionListener(disconnectionListener);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void authenticate(FsAuthenticationListener authenticationListener){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(fsUser);
    fsRequest.setOperation(FsRemoteOperationConstants.AUTHENTICATION);
    fsRequest.setRequestCode(FsAuthenticationListener.AUTHENTICATE);
    fsRequest.setSuperRequestCode(Integer.toString(FsAuthenticationListener.AUTHENTICATE));
    
    fsServerHandler.addAuthenticationListener(authenticationListener);
    fsServerHandler.getFsMessagesender().send(fsRequest);
    
    
  }
  
  public void upload(FsUploadListener uploadListener, Stack uploadItems, String currentFolderLocalPath, String CurrentFolderRemotePath, int uploadCode){
//    FsSocketClient socketClient = new FsSocketClient(jxtaClient.getSecureNetPeerGroup(),socketAdv,jxtaClient.getPeerId(),logger);
//    Thread socketClientThread = new Thread(socketClient);
//    socketClientThread.start();
    
    FsHandleUploadClient fsHandleUploadClient = new FsHandleUploadClient(logger, fsServerHandler);
    fsHandleUploadClient.addUploadListener(uploadListener);
    fsServerHandler.addUploadListener(fsHandleUploadClient);
    fsHandleUploadClient.uploadItem(uploadItems, currentFolderLocalPath, CurrentFolderRemotePath,uploadCode);
    
  }
  
  public void download(FsDownloadListener downloadListener, String[] downloadItems, String currentFolderLocalPath, String currentFolderRemotePath, int downloadCode){
    FsHandleDownloadClient fsHandleDownloadClient = new FsHandleDownloadClient(logger, fsServerHandler);
    fsHandleDownloadClient.addFsDownloadListener(downloadListener);
    fsServerHandler.addDownloadListener(fsHandleDownloadClient);
    fsHandleDownloadClient.downloadItem(downloadItems, currentFolderLocalPath, currentFolderRemotePath, downloadCode);
  }
  
  public void copy(FsRemoteCopyListener copyListener, String destinationFolderpath, String[] copyItems, int remoteCopyCode){
    String superRequestCode = Integer.toString(remoteCopyCode);
    FsHandleRemoteCopyClient fsHandleRemoteCopyClient = new FsHandleRemoteCopyClient(logger, fsServerHandler);
    fsHandleRemoteCopyClient.addFsRemoteCopyListener(copyListener);
    fsServerHandler.addRemoteCopyListener(fsHandleRemoteCopyClient);
    fsHandleRemoteCopyClient.itemCopy(destinationFolderpath, copyItems, superRequestCode);
  }
  
  public void copy(FsRemoteCopyListener copyListener, String destinationFolderpath, String copyItem, int remoteCopyCode){
    String superRequestCode = Integer.toString(remoteCopyCode);
    FsHandleRemoteCopyClient fsHandleRemoteCopyClient = new FsHandleRemoteCopyClient(logger, fsServerHandler);
    fsHandleRemoteCopyClient.addFsRemoteCopyListener(copyListener);
    fsServerHandler.addRemoteCopyListener(fsHandleRemoteCopyClient);
    fsHandleRemoteCopyClient.itemCopy(destinationFolderpath, copyItem, superRequestCode);
  }
  
  public void move(FsRemoteMoveListener moveListener, String destinationFolderpath, String[] moveItems, int remoteMoveCode){
    String superRequestCode = Integer.toString(remoteMoveCode);
    FsHandleRemoteMoveClient fsHandleRemoteMoveClient = new FsHandleRemoteMoveClient(logger, fsServerHandler);
    fsHandleRemoteMoveClient.addFsRemoteMoveListener(moveListener);
    fsServerHandler.addRemoteMoveListener(fsHandleRemoteMoveClient);
    fsHandleRemoteMoveClient.itemMove(destinationFolderpath, moveItems, superRequestCode);
  }
  
  public void move(FsRemoteMoveListener moveListener, String destinationFolderpath, String moveItem, int remoteMoveCode){
    String superRequestCode = Integer.toString(remoteMoveCode);
    FsHandleRemoteMoveClient fsHandleRemoteMoveClient = new FsHandleRemoteMoveClient(logger, fsServerHandler);
    fsHandleRemoteMoveClient.addFsRemoteMoveListener(moveListener);
    fsServerHandler.addRemoteMoveListener(fsHandleRemoteMoveClient);
    fsHandleRemoteMoveClient.itemMove(destinationFolderpath, moveItem, superRequestCode);
  }
  
  public void syncUpload(FsSyncUploadListener syncUploadListener, Document syncXMLDocumentLocal, String uploadItemPath, File currentFolderLocalFile, String currentFolderRemotePath, String syncOperation, int syncUploadCode){
    String superRequestCode = Integer.toString(syncUploadCode);
    FsHandleSyncUploadClient fsHandleSyncUploadClient = new FsHandleSyncUploadClient(logger, fsServerHandler, syncXMLDocumentLocal, syncOperation, uploadItemPath, currentFolderLocalFile, currentFolderRemotePath, superRequestCode);
    fsHandleSyncUploadClient.addFsSyncUploadListener(syncUploadListener);
    fsServerHandler.addSyncUploadListener(fsHandleSyncUploadClient);
    fsHandleSyncUploadClient.startUpload();
  }
  
  public void syncDownload(FsSyncDownloadListener syncDownloadListener, String downloadItemPath, File currentFolderLocalPath, String currentFolderRemotePath, String syncOperation, int syncDownloadCode){
    String superRequestCode = Integer.toString(syncDownloadCode);
    FsHandleSyncDownloadClient fsHandleSyncDownloadClient = new FsHandleSyncDownloadClient(logger, fsServerHandler, syncOperation, downloadItemPath, currentFolderLocalPath, currentFolderRemotePath, superRequestCode);
    fsHandleSyncDownloadClient.addFsSyncDownloadListener(syncDownloadListener);
    fsServerHandler.addSyncDownloadListener(fsHandleSyncDownloadClient);
    fsHandleSyncDownloadClient.startDownload();
  }
  
  public void stopSync(int currentSyncUploadCode, int currentSyncDownloadCode){
    String superRequestCode =null;
    FsRequest fsRequest = null;
    
    
    if (currentSyncUploadCode!=-1) {
      superRequestCode=Integer.toString(currentSyncUploadCode);
      fsRequest = new FsRequest();
      fsRequest.setRequestCode(FsSyncUploadListener.STOP);
      fsRequest.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
      fsRequest.setSuperRequestCode(superRequestCode); 
      fsServerHandler.getFsMessagesender().send(fsRequest);
    }
    
    if (currentSyncDownloadCode!=-1) {
      superRequestCode = Integer.toString(currentSyncDownloadCode);
      fsRequest = new FsRequest();
      fsRequest.setRequestCode(FsSyncDownloadListener.STOP);
      fsRequest.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
      fsRequest.setSuperRequestCode(superRequestCode);
      fsServerHandler.getFsMessagesender().send(fsRequest);
    }
  }
  
  public void getSyncPreview(String folderPath, String xmlForSync, int syncCommandCode){
    String superRequestCode = Integer.toString(syncCommandCode);
    FsRequest fsRequest = new FsRequest();
    fsRequest.setRequestCode(FsSyncCommandListener.PREVIEW);
    fsRequest.setOperation(FsRemoteOperationConstants.SYNC_COMMAND);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsRequest.setData(folderPath);
    fsRequest.setData1(xmlForSync);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void syncRemoteDelete(String itemPath, Integer index, Integer row, Integer col, int syncCommandCode){
    String superRequestCode = Integer.toString(syncCommandCode);
    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(itemPath);
    Object datas[] = new Object[3];
    datas[0] = index;
    datas[1] = row;
    datas[2] = col;
    fsRequest.setDatas(datas);
    fsRequest.setRequestCode(FsSyncCommandListener.REMOTE_DELETE);
    fsRequest.setOperation(FsRemoteOperationConstants.SYNC_COMMAND);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void makedir(String parentFolderpath, String FolderName, int remoteOperationConstant){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(remoteOperationConstant);
    fsRequest.setData(parentFolderpath);
    fsRequest.setData1(FolderName);
    fsRequest.setRequestCode(FsRemoteCommandListener.MK_DIR);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.MK_DIR));
    logger.debug("sending requesrcode to server......" + fsRequest.getRequestCode());
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void rename(String parentFolderpath, String renameItem){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.COMMAND);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.RENAME));
    fsRequest.setData(parentFolderpath);
    fsRequest.setData1(renameItem);
    fsRequest.setRequestCode(FsRemoteCommandListener.RENAME);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void delete(String deleteItempath){
    
  }
  
  public void delete(String[] deleteItempaths){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.COMMAND);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.DELETE));
    fsRequest.setDatas(deleteItempaths);
    fsRequest.setRequestCode(FsRemoteCommandListener.DELETE);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void changeDir(String folderpath){
    
  }
  
  public void navigateBack(){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.COMMAND);
    fsRequest.setRequestCode(FsRemoteCommandListener.NAVIGATE_BACK);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.NAVIGATE_BACK));
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void navigateForward(){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.COMMAND);
    fsRequest.setRequestCode(FsRemoteCommandListener.NAVIGATE_FORWARD);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.NAVIGATE_FORWARD));
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void navigateUp(){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.COMMAND);
    fsRequest.setRequestCode(FsRemoteCommandListener.GETCONTENTOFPARENTFOLDER);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.GETCONTENTOFPARENTFOLDER));
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void searchPath(String arrivedNodePath, String searchPath) {
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.COMMAND);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.SEARCH));
    fsRequest.setData(searchPath);
    fsRequest.setData1(arrivedNodePath);
    fsRequest.setRequestCode(FsRemoteCommandListener.SEARCH);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
 
  public void getRootOfFolder(int remoteOperationConstant){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(remoteOperationConstant);
    fsRequest.setRequestCode(FsRemoteCommandListener.GET_ROOT_FOLDERS);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.GET_ROOT_FOLDERS));
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void getContentOfFolder(String folderpath){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.GETCONTENTOFFOLDER));
    fsRequest.setData(folderpath);
    fsRequest.setRequestCode(FsRemoteCommandListener.GETCONTENTOFFOLDER);
    fsRequest.setOperation(FsRemoteOperationConstants.COMMAND);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void getContentOfFolderRecursively(String folderPath){
    
  }
  
  public void getProperties(String[] Itempaths){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.COMMAND);
    fsRequest.setRequestCode(FsRemoteCommandListener.GET_FILE_FOLDER_PROPERTIES);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.GET_FILE_FOLDER_PROPERTIES));
    fsRequest.setData(Itempaths);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }

  /**
   * To send a request to change the properties of remote file such as 
   * name ,permissions.
   * @param fsFilePropertyPageRemote property change event object
   */
  public void setPropertiesOfFile(FsFilePropertyPageRemote  fsFilePropertyPageRemote) {
      FsRequest fsRequest = new FsRequest();
      fsRequest.setOperation(FsRemoteOperationConstants.COMMAND);
      fsRequest.setRequestCode(FsRemoteCommandListener.CHANGE_PROPERTIES_OF_FILE);
      fsRequest.setSuperRequestCode(FsMessage.FOR_CLIENTGUI);
      fsRequest.setData(fsFilePropertyPageRemote);
      fsServerHandler.getFsMessagesender().send(fsRequest);
  }


  
  public void setPropertiesOfFolder(FsFolderPropertyPageRemote fsFolderPropertyPageRemote) {    
      FsRequest fsRequest = new FsRequest();
      fsRequest.setOperation(FsRemoteOperationConstants.COMMAND);
      fsRequest.setRequestCode(FsRemoteCommandListener.CHANGE_PROPERTIES_OF_FOLDER);
      fsRequest.setData(fsFolderPropertyPageRemote);
      fsServerHandler.getFsMessagesender().send(fsRequest);
  }

  public void getHomeFolder(int remoteOperationConstant){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(remoteOperationConstant);
    fsRequest.setRequestCode(FsRemoteCommandListener.GET_HOME_FOLDER);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.GET_HOME_FOLDER));
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void getFolderRoot(String folderPath, int remoteOperationConstant){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(folderPath);
    fsRequest.setOperation(remoteOperationConstant);
    fsRequest.setRequestCode(FsRemoteCommandListener.GET_FOLDER_ROOT);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.GET_FOLDER_ROOT));
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }

  public void getFlatFolderTree(String startFolderPath, String endFolderPath, int remoteOperationConstant){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(endFolderPath);
    fsRequest.setData1(startFolderPath);
    fsRequest.setOperation(remoteOperationConstant);
    fsRequest.setRequestCode(FsRemoteCommandListener.GET_FLAT_FOLDER_TREE);
    fsRequest.setSuperRequestCode(Integer.toString(FsRemoteCommandListener.GET_FLAT_FOLDER_TREE));
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void setFsUser(FsUser fsUser) {
    this.fsUser = fsUser;
  }

  public void overWriteUploadFolder(Integer overWriteValue, String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(overWriteValue);
    fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
    fsRequest.setRequestCode(FsUploadListener.OVERWRITE_FOLDER);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  
  
  public void overWriteUploadFile(Integer overWriteValue, String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(new Integer(overWriteValue));
    fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
    fsRequest.setRequestCode(FsUploadListener.OVERWRITE_FILE);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  public void syncDownloadCloseFile(String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.SYNC_DOWNLOAD);
    fsRequest.setRequestCode(FsSyncDownloadListener.SOCKET_CLOSE_FILE);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void syncUploadCloseFile(String superRequestCode, Document documentOfChanges,Document document){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
    fsRequest.setRequestCode(FsSyncUploadListener.SOCKET_CLOSE_FILE);
    fsRequest.setData(documentOfChanges);
    fsRequest.setData1(document);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void downloadSocketCloseFile(String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
    fsRequest.setRequestCode(FsDownloadListener.SOCKET_CLOSE_FILE);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void uploadSocketCloseFile(String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
    fsRequest.setRequestCode(FsUploadListener.SOCKET_CLOSE_FILE);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
 }
  
  public void uploadFileCurrupted(String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
    fsRequest.setRequestCode(FsUploadListener.FILE_CURRUPTED);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void uploadCancel(String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.UPLOAD);
    fsRequest.setRequestCode(FsUploadListener.CANCEL);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  public void downloadSocketErrorMessage(String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
    fsRequest.setRequestCode(FsDownloadListener.FAILED);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void downloadCancel(String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
    fsRequest.setRequestCode(FsDownloadListener.CANCEL);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void overWriteDownloadFile(Integer overWriteValue, String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
    fsRequest.setRequestCode(FsDownloadListener.OVERWRITE_FILE);
    fsRequest.setData(new Integer(overWriteValue));
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void overWriteDownloadFolder(Integer overWriteValue, String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.DOWNLOAD);
    fsRequest.setRequestCode(FsDownloadListener.OVERWRITE_FOLDER);
    fsRequest.setData(new Integer(overWriteValue));
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }

  public void overWriteCopyFolder(Integer overWriteValue, String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.COPY);
    fsRequest.setRequestCode(FsRemoteCopyListener.OVERWRITE_OPTION_FOLDER);
    fsRequest.setData(new Integer(overWriteValue));
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
 
  public void overWriteCopyFile(Integer overWriteValue, String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.COPY);
    fsRequest.setRequestCode(FsRemoteCopyListener.OVERWRITE_OPTION_FILE);
    fsRequest.setData(new Integer(overWriteValue));
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void copyCancel(String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.COPY);
    fsRequest.setRequestCode(FsRemoteCopyListener.CANCEL);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void overWriteMoveFolder(Integer overWriteValue, String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.MOVE);
    fsRequest.setRequestCode(FsRemoteMoveListener.OVERWRITE_OPTION_FOLDER);
    fsRequest.setData(new Integer(overWriteValue));
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void overWriteMoveFile(Integer overWriteValue, String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.MOVE);
    fsRequest.setRequestCode(FsRemoteMoveListener.OVERWRITE_OPTION_FILE);
    fsRequest.setData(new Integer(overWriteValue));
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  public void moveCancel(String superRequestCode){
    FsRequest fsRequest = new FsRequest();
    fsRequest.setOperation(FsRemoteOperationConstants.MOVE);
    fsRequest.setRequestCode(FsRemoteMoveListener.CANCEL);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsServerHandler.getFsMessagesender().send(fsRequest);
  }
  
  private class  JxtaConnectionListener implements FsConnectionListener {
    
    private PropertyChangeSupport jxtaConnectionPropertyChangeSupport=null;
    
    
    public JxtaConnectionListener(FsConnectionListener connectionListener){
      jxtaConnectionPropertyChangeSupport = new PropertyChangeSupport(this);
      jxtaConnectionPropertyChangeSupport.addPropertyChangeListener(connectionListener);
    }
    
    public void propertyChange(PropertyChangeEvent evt){
      try {
        jxtaClient = (JxtaClient)evt.getSource();
        String propertyName = evt.getPropertyName();
        boolean propertyValue =((Boolean)evt.getNewValue()).booleanValue();
        if (propertyName.equals(Integer.toString(CONNECTED))) {
          if (propertyValue) {
            
            fsServerHandler = new FsServerHandler(jxtaClient.getBidipipe(),jxtaClient.getSecureNetPeerGroup(),jxtaClient.getPeerId(),socketAdv, fsUser.getUserId(), logger);
            
          }
        }else if (propertyName.equals(Integer.toString(IS_INTERNET_AVAILABLE))) {
          if(!propertyValue){         
            jxtaClient.stopJxta();
          }
        }else if (propertyName.equals(Integer.toString(EXIST_IN_INTERNET))) {
          if(!propertyValue){
            jxtaClient.stopJxta();
          }
        }
        jxtaConnectionPropertyChangeSupport.firePropertyChange(new PropertyChangeEvent(fsClient, propertyName, null,Boolean.valueOf(propertyValue)));
      }
      catch (Exception e) {
        logger.error(clientUtil.getStackTrace(e));
      }
    }
  }

}
