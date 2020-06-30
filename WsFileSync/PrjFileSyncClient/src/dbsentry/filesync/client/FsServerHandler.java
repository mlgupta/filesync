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

import dbsentry.filesync.common.FsConstructMessage;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.PacketCarrier;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsAuthenticationListener;
import dbsentry.filesync.common.listeners.FsDisconnectionListener;
import dbsentry.filesync.common.listeners.FsDownloadListener;
import dbsentry.filesync.common.listeners.FsMessageListener;
import dbsentry.filesync.common.listeners.FsSyncCommandListener;
import dbsentry.filesync.common.listeners.FsRemoteCommandListener;
import dbsentry.filesync.common.listeners.FsRemoteCopyListener;
import dbsentry.filesync.common.listeners.FsRemoteMoveListener;
import dbsentry.filesync.common.listeners.FsSyncDownloadListener;
import dbsentry.filesync.common.listeners.FsSyncUploadListener;
import dbsentry.filesync.common.listeners.FsUploadListener;

import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.util.JxtaBiDiPipe;
import net.jxta.peer.PeerID;

import java.beans.PropertyChangeEvent;

//import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.InputStream;
import java.io.ObjectInputStream;

import java.util.ArrayList;
import java.util.ListIterator;

import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.protocol.PipeAdvertisement;

import net.jxta.socket.JxtaSocket;

import org.apache.log4j.Logger;



/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description
 * @Date of creation: 13-03-2006
 * @Last Modfied by :Saurabh Gupta 
 * @Last Modfied Date:03-04-2006
 */
public class FsServerHandler implements PipeMsgListener,FsMessageListener{
  private FsMessageSender fsMessagesender;
  
  private FsSocketClient fsSocketClient;
  
  private Logger logger;
  
  private ClientUtil clientUtil;
  
  private ArrayList arrayListPacketCarrier = new ArrayList();
  
  //private PropertyChangeSupport propertyChangeSupport=null;
  
  private PropertyChangeSupport propertyChangeSupport4Upload=null;
  
  private PropertyChangeSupport propertyChangeSupport4Download=null;
  
  private PropertyChangeSupport propertyChangeSupport4RemoteCopy=null;
  
  private PropertyChangeSupport propertyChangeSupport4RemoteMove=null;
  
  private PropertyChangeSupport propertyChangeSupport4RemoteCommand=null;
  
  private PropertyChangeSupport propertyChangeSupport4Disconnection=null;
  
  private PropertyChangeSupport propertyChangeSupport4SyncCommand=null;
  
  private PropertyChangeSupport propertyChangeSupport4SyncUpload=null;
  
  private PropertyChangeSupport propertyChangeSupport4SyncDownload=null;
  
  private PropertyChangeSupport propertyChangeSupport4FsConstructMessage=null;
 
  private PropertyChangeSupport propertyChangeSupport4Authentication=null;
  
  private PropertyChangeSupport propertyChangeSupport4RemoteBrowser=null;
  
 

  /**
   * @param bidipipe
   * @param netPeerGroup
   * @param peerId
   * @param socketAdv
   * @param logger
   * @description
   */
  public FsServerHandler(JxtaBiDiPipe bidipipe, PeerGroup netPeerGroup,PeerID peerId, PipeAdvertisement socketAdv,String userName, Logger logger) {        
    this.logger=logger;
    logger.debug("Inside FsServerHandler.........");
    
    this.fsMessagesender = new FsMessageSender(bidipipe, logger);
    bidipipe.setMessageListener(this);
    
    this.fsSocketClient = new FsSocketClient(netPeerGroup, socketAdv, peerId, userName, logger, this);
    
    propertyChangeSupport4RemoteCommand =new PropertyChangeSupport(this);      
  }


  /**
   * @param event
   * @description
   * @author Saurabh Gupta
   * @version 1.0
   * @Date of creation: 02-03-2006
   * @Last Modfied by : Saurabh Gupta
   * @Last Modfied Date:11-07-2006
   */
  
  public synchronized void pipeMsgEvent(PipeMsgEvent event) {
    
    PacketCarrier packetCarrier;
    String carrierCode;
    Message msg = null;
    int streamLength = 0;
    int carrierTotalLength=0;
    try {
      logger.debug("Inside PipeMessage Event :");
      msg = event.getMessage();
      if (msg != null){
        ListIterator list = msg.getMessageElements();
        if (list.hasNext()){
          try{
            MessageElement msgElement;

            //Gets the Packet Carrier Length
            msgElement = (MessageElement)list.next();
            streamLength = Integer.parseInt(msgElement.toString());

            //Gets the Packet Carrier Data          
            msgElement = (MessageElement)list.next();

            InputStream is = msgElement.getStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            packetCarrier = (PacketCarrier)ois.readObject();
            ois.close();
            is.close();

            carrierCode = packetCarrier.getCarrierCode();
            carrierTotalLength=packetCarrier.getTotalLength();
            if (!arrayListPacketCarrier.contains(carrierCode)){
              Logger fsHandleJxtaResponseLogger = Logger.getLogger("ClientLogger");
              FsConstructMessage fsConstructMessage =
                new FsConstructMessage(fsHandleJxtaResponseLogger, carrierCode ,carrierTotalLength);
              fsConstructMessage.addFsMessageListener(this);
              addFsConstructMessageListener(fsConstructMessage);
              arrayListPacketCarrier.add(carrierCode); 
            }
            propertyChangeSupport4FsConstructMessage
            .firePropertyChange(new PropertyChangeEvent(carrierCode, "", null, packetCarrier));
            return;
          }catch (Exception ex){
            logger.error(clientUtil.getStackTrace(ex));
          }
          return;
        }
      }else {
        logger.debug("Null Message Received");
      }
    
    }catch (Exception e) {
      logger.error(clientUtil.getStackTrace(e));
    }
  }

  public synchronized void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals(Integer.toString(FSMESSAGE))) {
      handleFsMessage((FsResponse)evt.getNewValue());      
    } else if (evt.getPropertyName().equals(Integer.toString(REMOVE_CARRIER_CODE))) {
      arrayListPacketCarrier.remove((String)evt.getNewValue());
    } else if (evt.getPropertyName().equals(Integer.toString(REMOVE_PROPERTY_CHANGE_LISTENER))) {
      removeFsConstructMessageListener((FsConstructMessage)evt.getNewValue());
    }
  }

  public void addAuthenticationListener(FsAuthenticationListener authenticationListener){
    if (propertyChangeSupport4Authentication == null) {
      propertyChangeSupport4Authentication = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4Authentication.addPropertyChangeListener(authenticationListener);
  }
  
  public void removeAuthenticationListener(FsAuthenticationListener authenticationListener){
    if (propertyChangeSupport4Authentication != null) {
      propertyChangeSupport4Authentication.removePropertyChangeListener(authenticationListener);
    }
  }

  public void addDisconnetionListener(FsDisconnectionListener disconnectionListener){
    if (propertyChangeSupport4Disconnection == null) {
      propertyChangeSupport4Disconnection = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4Disconnection.addPropertyChangeListener(disconnectionListener);
  }

  public void removeDisconnetionListener(FsDisconnectionListener disconnectionListener){
    if (propertyChangeSupport4Disconnection != null) {
      propertyChangeSupport4Disconnection.removePropertyChangeListener(disconnectionListener);
    }
  }
  
  public void addUploadListener(FsUploadListener uploadListener) {
    if (propertyChangeSupport4Upload == null) {
      propertyChangeSupport4Upload = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4Upload.addPropertyChangeListener(uploadListener);
  }
  
  public void removeUploadListener(FsUploadListener uploadListener){
    if (propertyChangeSupport4Upload != null) {
      propertyChangeSupport4Upload.removePropertyChangeListener(uploadListener);
    }
  }

  public void addDownloadListener(FsDownloadListener downloadListener) {
    if (propertyChangeSupport4Download == null){
      propertyChangeSupport4Download = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4Download.addPropertyChangeListener(downloadListener);  
  }

  public void removeDownloadListener(FsDownloadListener downloadListener){
    if (propertyChangeSupport4Download != null){
      propertyChangeSupport4Download.removePropertyChangeListener(downloadListener); 
    }
  }

  public void addRemoteCopyListener(FsRemoteCopyListener copyListener){
    if(propertyChangeSupport4RemoteCopy == null){
      propertyChangeSupport4RemoteCopy = new PropertyChangeSupport(this); 
    }
    propertyChangeSupport4RemoteCopy.addPropertyChangeListener(copyListener);
  }

  public void removeRemoteCopyListener(FsRemoteCopyListener copyListener){
    if(propertyChangeSupport4RemoteCopy != null){
      propertyChangeSupport4RemoteCopy.removePropertyChangeListener(copyListener); 
    }
  }

  public void addRemoteMoveListener(FsRemoteMoveListener moveListener){
    if(propertyChangeSupport4RemoteMove == null){
      propertyChangeSupport4RemoteMove = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4RemoteMove.addPropertyChangeListener(moveListener);
  }

  public void removeRemoteMoveListener(FsRemoteMoveListener moveListener){
    if(propertyChangeSupport4RemoteMove !=null){
      propertyChangeSupport4RemoteMove.removePropertyChangeListener(moveListener);
    }
  }

  public void setRemoteCommandListener(FsRemoteCommandListener remoteCommandListener){
    if(propertyChangeSupport4RemoteCommand != null){
      PropertyChangeListener[] propertyChangeListeners=null;
      propertyChangeListeners=propertyChangeSupport4RemoteCommand.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        propertyChangeSupport4RemoteCommand.removePropertyChangeListener(propertyChangeListeners[i]);
      }
    }
    logger.debug("Adding RemoteCommandListener");
    propertyChangeSupport4RemoteCommand.addPropertyChangeListener(remoteCommandListener);   
  }
  
  public void addRemoteBrowserCommandListener(FsRemoteCommandListener remoteCommandListener){  
    if(propertyChangeSupport4RemoteBrowser == null){
      propertyChangeSupport4RemoteBrowser = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4RemoteBrowser.addPropertyChangeListener(remoteCommandListener);
  }
  
  public void removeRemoteBrowserCommandListener(FsRemoteCommandListener remoteCommandListener){   
    if(propertyChangeSupport4RemoteBrowser != null){
      propertyChangeSupport4RemoteBrowser.removePropertyChangeListener(remoteCommandListener);
    }
  }
  
  public void addSyncCommandListener(FsSyncCommandListener syncCommandListener){
    if(propertyChangeSupport4SyncCommand == null){
      propertyChangeSupport4SyncCommand = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4SyncCommand.addPropertyChangeListener(syncCommandListener);
  }
  
  public void removeSyncCommandListener(FsSyncCommandListener syncCommandListener){
    if(propertyChangeSupport4SyncCommand != null){
      propertyChangeSupport4SyncCommand.removePropertyChangeListener(syncCommandListener);
    }
  }
  
  public void addSyncUploadListener(FsSyncUploadListener syncUploadListener){
    if(propertyChangeSupport4SyncUpload == null){
      propertyChangeSupport4SyncUpload = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4SyncUpload.addPropertyChangeListener(syncUploadListener);
  }

  public void removeSyncUploadListener(FsSyncUploadListener syncUploadListener){
    if(propertyChangeSupport4SyncUpload != null){
      propertyChangeSupport4SyncUpload.removePropertyChangeListener(syncUploadListener);
    }
  }

  public void addSyncDownloadListener(FsSyncDownloadListener syncDownloadListener){
    if(propertyChangeSupport4SyncDownload == null){
      propertyChangeSupport4SyncDownload = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4SyncDownload.addPropertyChangeListener(syncDownloadListener);
  }

  public void removeSyncDownloadListener(FsSyncDownloadListener syncDownloadListener){
    if(propertyChangeSupport4SyncDownload != null){
      propertyChangeSupport4SyncDownload.removePropertyChangeListener(syncDownloadListener);
    }
  }
  
  public void addFsConstructMessageListener(FsMessageListener fsMessageListener){
    if(propertyChangeSupport4FsConstructMessage == null){
      propertyChangeSupport4FsConstructMessage = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4FsConstructMessage.addPropertyChangeListener(fsMessageListener);
  }

  public void removeFsConstructMessageListener(FsMessageListener fsMessageListener){
    if(propertyChangeSupport4FsConstructMessage != null){
      propertyChangeSupport4FsConstructMessage.removePropertyChangeListener(fsMessageListener);
    }
  }

  
  
  public FsMessageSender getFsMessagesender() {
    return fsMessagesender;
  }

  public PropertyChangeSupport getPropertyChangeSupport4Upload() {
    return propertyChangeSupport4Upload;
  }

  public PropertyChangeSupport getPropertyChangeSupport4Download() {
    return propertyChangeSupport4Download;
  }

  public PropertyChangeSupport getPropertyChangeSupport4RemoteCopy() {
    return propertyChangeSupport4RemoteCopy;
  }

  public PropertyChangeSupport getPropertyChangeSupport4RemoteMove() {
    return propertyChangeSupport4RemoteMove;
  }

  public PropertyChangeSupport getPropertyChangeSupport4SyncUpload() {
    return propertyChangeSupport4SyncUpload;
  }

  public PropertyChangeSupport getPropertyChangeSupport4SyncDownload() {
    return propertyChangeSupport4SyncDownload;
  }

  private void handleFsMessage(FsResponse fsResponse) {
    int operation = fsResponse.getOperation();
    logger.debug("operation : " + operation);
    int responseCode = fsResponse.getResponseCode();
    logger.debug("responseCode : " + responseCode);
    switch (operation){
      case FsRemoteOperationConstants.AUTHENTICATION :
        propertyChangeSupport4Authentication.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;      
      case FsRemoteOperationConstants.COMMAND :
        propertyChangeSupport4RemoteCommand.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        logger.debug("Firing For COMMAND");
        break;      
      case FsRemoteOperationConstants.UPLOAD :
        propertyChangeSupport4Upload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case FsRemoteOperationConstants.DOWNLOAD :
        propertyChangeSupport4Download.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case FsRemoteOperationConstants.SYNC_COMMAND:
        propertyChangeSupport4SyncCommand.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case FsRemoteOperationConstants.SYNC_UPLOAD :
        propertyChangeSupport4SyncUpload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case FsRemoteOperationConstants.SYNC_DOWNLOAD :
        propertyChangeSupport4SyncDownload.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case FsRemoteOperationConstants.COPY :
        propertyChangeSupport4RemoteCopy.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case FsRemoteOperationConstants.MOVE :
        propertyChangeSupport4RemoteMove.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case FsRemoteOperationConstants.DISCONNECTION :
        propertyChangeSupport4Disconnection.firePropertyChange(Integer.toString(responseCode),null,fsResponse); 
        cleanUpOnDisconnect();
        break;
      case FsRemoteOperationConstants.REMOTE_BROWSER:
        propertyChangeSupport4RemoteBrowser.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
    }
    
  }
  
  private void cleanUpOnDisconnect(){
    
    PropertyChangeListener[] propertyChangeListeners=null;
    
    //Removing PropertyChangeListeners from propertyChangeSupport4Upload
    if(propertyChangeSupport4Upload!=null){
      propertyChangeListeners=propertyChangeSupport4Upload.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        removeUploadListener((FsUploadListener)propertyChangeListeners[i]);
      }
    }
    
    //Removing PropertyChangeListeners from propertyChangeSupport4Download
    if (propertyChangeSupport4Download!=null) {
      propertyChangeListeners=propertyChangeSupport4Download.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        removeDownloadListener((FsDownloadListener)propertyChangeListeners[i]);
      }
    }
    
    //Removing PropertyChangeListeners from propertyChangeSupport4RemoteCopy
    if (propertyChangeSupport4RemoteCopy!=null) {
      propertyChangeListeners=propertyChangeSupport4RemoteCopy.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        removeRemoteCopyListener((FsRemoteCopyListener)propertyChangeListeners[i]);
      }
    }
    
    //Removing PropertyChangeListeners from propertyChangeSupport4RemoteMove
    if (propertyChangeSupport4RemoteMove!=null) {
      propertyChangeListeners=propertyChangeSupport4RemoteMove.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        removeRemoteMoveListener((FsRemoteMoveListener)propertyChangeListeners[i]);
      }
    }
    
    //Removing PropertyChangeListeners from propertyChangeSupport4RemoteCommand
    if (propertyChangeSupport4RemoteCommand!=null) {
      propertyChangeListeners=propertyChangeSupport4RemoteCommand.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        propertyChangeSupport4RemoteCommand.removePropertyChangeListener(propertyChangeListeners[i]);
      }
    }
    
    //Removing PropertyChangeListeners from propertyChangeSupport4SyncCommand
    if (propertyChangeSupport4SyncCommand!=null) {
      propertyChangeListeners=propertyChangeSupport4SyncCommand.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        propertyChangeSupport4SyncCommand.removePropertyChangeListener(propertyChangeListeners[i]);
      }
    }
    
    //Removing PropertyChangeListeners from propertyChangeSupport4Disconnection
    if (propertyChangeSupport4Disconnection!=null) {
      propertyChangeListeners=propertyChangeSupport4Disconnection.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        removeDisconnetionListener((FsDisconnectionListener)propertyChangeListeners[i]);
      }
    }
    
    //Removing PropertyChangeListeners from propertyChangeSupport4SyncUpload
    if (propertyChangeSupport4SyncUpload!=null) {
      propertyChangeListeners=propertyChangeSupport4SyncUpload.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        removeSyncUploadListener((FsSyncUploadListener)propertyChangeListeners[i]);
      }
    }
    
    //Removing PropertyChangeListeners from propertyChangeSupport4SyncDownload
    if (propertyChangeSupport4SyncDownload!=null) {
      propertyChangeListeners=propertyChangeSupport4SyncDownload.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        removeSyncDownloadListener((FsSyncDownloadListener)propertyChangeListeners[i]);
      }
    }
    
    //Removing PropertyChangeListeners from propertyChangeSupport4FsConstructMessage
    if (propertyChangeSupport4FsConstructMessage!=null) {
      propertyChangeListeners=propertyChangeSupport4FsConstructMessage.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        removeFsConstructMessageListener((FsMessageListener)propertyChangeListeners[i]);
      }
    }
    
    //Removing PropertyChangeListeners from propertyChangeSupport4Authentication
    if (propertyChangeSupport4Authentication!=null) {
      propertyChangeListeners=propertyChangeSupport4Authentication.getPropertyChangeListeners();
      for(int i=0;propertyChangeListeners!=null && i<propertyChangeListeners.length;i++){
        removeAuthenticationListener((FsAuthenticationListener)propertyChangeListeners[i]);
      }
    }
    
    
  }

  public FsSocketClient getFsSocketClient() {
    return fsSocketClient;
  }
}
