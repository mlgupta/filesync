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
 * $Id: FsClientHandler.java,v 1.92 2006/09/08 09:13:04 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

//import dbsentry.filesync.common.FsConstructRequest;
import dbsentry.filesync.common.FsConstructMessage;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsMessage;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsObjectHolder;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.FsUser;
import dbsentry.filesync.common.PacketCarrier;
import dbsentry.filesync.common.constants.FsDisconnectionConstants;
import dbsentry.filesync.common.constants.FsDownloadConstants;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.constants.FsUploadConstants;
import dbsentry.filesync.common.listeners.FsAuthenticationListener;
import dbsentry.filesync.common.listeners.FsDownloadListener;
import dbsentry.filesync.common.listeners.FsMessageListener;
import dbsentry.filesync.common.listeners.FsRemoteCommandListener;
import dbsentry.filesync.common.listeners.FsRemoteCopyListener;
import dbsentry.filesync.common.listeners.FsRemoteMoveListener;
import dbsentry.filesync.common.listeners.FsSyncCommandListener;
import dbsentry.filesync.common.listeners.FsSyncDownloadListener;
import dbsentry.filesync.common.listeners.FsSyncUploadListener;
import dbsentry.filesync.common.listeners.FsUploadListener;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsDataSource;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFolder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;

import net.jxta.util.JxtaBiDiPipe;

import org.apache.log4j.Logger;


/**
 * To handle the requests from client.
 * @author Jeetendra Prasad
 * @version 1.0
 * 	Date of creation:    7-05-2005
 * 	Last Modfied by : Saurabh Gupta   
 * 	Last Modfied Date:  12-04-2006   
 */
public class FsClientHandler implements PipeMsgListener, FsMessageListener {

  private FsConnection fsConnection;

  private Logger logger;

  private FsDataSource fsDataSource;

  private FsMessageSender fsMessageSender;

  private ClientHandlerHelper clientHandlerHelper;

  private PropertyChangeSupport propertyChangeSupport;

  private ArrayList arrayListPacketCarrier = new ArrayList();

  private ServerUtil serverUtil;

  private PropertyChangeSupport propertyChangeSupport4Upload;

  private PropertyChangeSupport propertyChangeSupport4Download;

  private PropertyChangeSupport propertyChangeSupport4RemoteCopy;

  private PropertyChangeSupport propertyChangeSupport4RemoteMove;

  private PropertyChangeSupport propertyChangeSupport4RemoteCommand;

  private PropertyChangeSupport propertyChangeSupport4SyncCommand;

  private PropertyChangeSupport propertyChangeSupport4SyncUpload;

  private PropertyChangeSupport propertyChangeSupport4SyncDownload;

  private PropertyChangeSupport propertyChangeSupport4FsMessageConstruct;

  private FsServer fsServer;

  private String userId;

  /**
   * To construct a FsClientHandler object.
   * @param logger used to log.
   * @param jxtaBiDiPipe used to send message. 
   * @param fsDataSource used to establish a connection. 
   */
  public FsClientHandler(Logger logger, FsServer fsServer, JxtaBiDiPipe jxtaBiDiPipe, FsDataSource fsDataSource) {
    this.logger = logger;
    logger.debug("Client Handler Instance Created");
    this.fsDataSource = fsDataSource;
    this.fsMessageSender = new FsMessageSender(jxtaBiDiPipe, logger);
    this.serverUtil = new ServerUtil(logger);
    this.fsServer = fsServer;
  }

  /**
   * To validate the userid and password.If unable to connect with database using specified 
   * userid and password then validUser is set to false.
   * @param fsUser contains userid and password to be validated.
   * @return validUser true if valid userid and password.
   */
  public FsResponse validateUser(FsUser fsUser) {
    logger.info("User Id : " + fsUser.getUserId());
    FsResponse fsResponse = null;
    try {
      fsResponse = new FsResponse();
      this.fsConnection = fsDataSource.getConnection(fsUser.getUserId(), fsUser.getUserPassword());
      this.clientHandlerHelper = new ClientHandlerHelper(fsConnection, Logger.getLogger("ServerLogger"));
      logger.info("User connected");
      fsResponse.setResponseCode(FsAuthenticationListener.AUTHORISED);

      userId = fsUser.getUserId();

      FsUserInfo fsUserInfo = new FsUserInfo();
      fsUserInfo.setUserid(userId);
      fsUserInfo.setLogginTime(new Date());
      fsUserInfo.setFsConnection(fsConnection);
      fsServer.getConnectedUsers().put(userId, fsUserInfo);

    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      logger.debug("fex.getErrorCode() " + fex.getErrorCode());
      if (fex.getErrorCode() == 10170 || fex.getErrorCode() == 21008) {
        logger.error("Invalid User");
        fsResponse = new FsResponse();
        FsExceptionHolder fsExceptionHolder = new FsExceptionHolder();
        fsExceptionHolder.setErrorCode(FsExceptionHolder.INVALID_USER_CODE);
        fsExceptionHolder.setErrorMessage(FsExceptionHolder.INVALID_USER_MSG);
        fsResponse.setFsExceptionHolder(fsExceptionHolder);
        fsResponse.setResponseCode(FsAuthenticationListener.UNAUTHORISED);
        logger.debug(fsExceptionHolder);
      }
    } catch (Exception ex) {
      logger.error(ex.getMessage());
      logger.error(serverUtil.getStackTrace(ex));
    } finally {
      return fsResponse;
    }
  }

  public void addUploadListener(FsUploadListener uploadListener) {
    if (propertyChangeSupport4Upload == null) {
      propertyChangeSupport4Upload = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4Upload.addPropertyChangeListener(uploadListener);
  }

  public void removeUploadListener(FsUploadListener uploadListener) {
    if (propertyChangeSupport4Upload != null) {
      propertyChangeSupport4Upload.removePropertyChangeListener(uploadListener);
    }
  }

  public void addDownloadListener(FsDownloadListener downloadListener) {
    if (propertyChangeSupport4Download == null) {
      propertyChangeSupport4Download = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4Download.addPropertyChangeListener(downloadListener);
  }

  public void removeDownloadListener(FsDownloadListener downloadListener) {
    if (propertyChangeSupport4Download != null) {
      propertyChangeSupport4Download.removePropertyChangeListener(downloadListener);
    }
  }

  public void addRemoteCopyListener(FsRemoteCopyListener copyListener) {
    if (propertyChangeSupport4RemoteCopy == null) {
      propertyChangeSupport4RemoteCopy = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4RemoteCopy.addPropertyChangeListener(copyListener);
  }

  public void removeRemoteCopyListener(FsRemoteCopyListener copyListener) {
    if (propertyChangeSupport4RemoteCopy != null) {
      propertyChangeSupport4RemoteCopy.removePropertyChangeListener(copyListener);
    }
  }

  public void addRemoteMoveListener(FsRemoteMoveListener moveListener) {
    if (propertyChangeSupport4RemoteMove == null) {
      propertyChangeSupport4RemoteMove = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4RemoteMove.addPropertyChangeListener(moveListener);
  }

  public void removeRemoteMoveListener(FsRemoteMoveListener moveListener) {
    if (propertyChangeSupport4RemoteMove != null) {
      propertyChangeSupport4RemoteMove.removePropertyChangeListener(moveListener);
    }
  }

  public void setRemoteCommandListener(FsRemoteCommandListener remoteCommandListener) {
    if (propertyChangeSupport4RemoteCommand != null) {
      propertyChangeSupport4RemoteCommand.removePropertyChangeListener(remoteCommandListener);
    }
    propertyChangeSupport4RemoteCommand.addPropertyChangeListener(remoteCommandListener);
  }

  public void addSyncCommandListener(FsSyncCommandListener syncCommandListener) {
    if (propertyChangeSupport4SyncCommand == null) {
      propertyChangeSupport4SyncCommand = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4SyncUpload.addPropertyChangeListener(syncCommandListener);
  }

  public void removeSyncCommandListener(FsSyncCommandListener syncCommandListener) {
    if (propertyChangeSupport4SyncCommand != null) {
      propertyChangeSupport4SyncCommand.removePropertyChangeListener(syncCommandListener);
    }
  }

  public void addSyncUploadListener(FsSyncUploadListener syncUploadListener) {
    if (propertyChangeSupport4SyncUpload == null) {
      propertyChangeSupport4SyncUpload = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4SyncUpload.addPropertyChangeListener(syncUploadListener);
  }

  public void removeSyncUploadListener(FsSyncUploadListener syncUploadListener) {
    if (propertyChangeSupport4SyncUpload != null) {
      propertyChangeSupport4SyncUpload.removePropertyChangeListener(syncUploadListener);
    }
  }

  public void addSyncDownloadListener(FsSyncDownloadListener syncDownloadListener) {
    if (propertyChangeSupport4SyncDownload == null) {
      propertyChangeSupport4SyncDownload = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4SyncDownload.addPropertyChangeListener(syncDownloadListener);
  }

  public void removeSyncDownloadListener(FsSyncDownloadListener syncDownloadListener) {
    if (propertyChangeSupport4SyncDownload != null) {
      propertyChangeSupport4SyncDownload.removePropertyChangeListener(syncDownloadListener);
    }
  }

  public void addFsConstructMessageListener(FsMessageListener fsMessageListener) {
    if (propertyChangeSupport4FsMessageConstruct == null) {
      propertyChangeSupport4FsMessageConstruct = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4FsMessageConstruct.addPropertyChangeListener(fsMessageListener);
  }

  public void removeFsConstructMessageListener(FsMessageListener fsMessageListener) {
    if (propertyChangeSupport4FsMessageConstruct != null) {
      propertyChangeSupport4FsMessageConstruct.removePropertyChangeListener(fsMessageListener);
    }
  }

  /**
   * 
   * @param event PipeMsgEvent object which contains message from server.
   */
  public synchronized void pipeMsgEvent(PipeMsgEvent event) {

    PacketCarrier packetCarrier;
    String carrierCode;
    Message msg = null;
    int streamLength = 0;
    int carrierTotalLength = 0;

    try {
      msg = event.getMessage();

      if (msg != null) {
        ListIterator list = msg.getMessageElements();

        if (list.hasNext()) {
          MessageElement msgElement;

          // Getting First Element Which contains Packet Length
          msgElement = (MessageElement)list.next();
          streamLength = Integer.parseInt(msgElement.toString());

          // Getting  Packet Data element
          msgElement = (MessageElement)list.next();
          InputStream is = null;
          ObjectInputStream ois = null;

          try {
            is = msgElement.getStream();
            ois = new ObjectInputStream(is);
            packetCarrier = (PacketCarrier)ois.readObject();


            carrierCode = packetCarrier.getCarrierCode();
            carrierTotalLength = packetCarrier.getTotalLength();
            if (!arrayListPacketCarrier.contains(carrierCode)) {
              Logger fsHandleJxtaRequestLogger = Logger.getLogger("ServerLogger");

              // FsConstructRequest fsConstructRequest = new FsConstructRequest(fsHandleJxtaRequestLogger, packetCarrier);
              FsConstructMessage fsConstructMessage =
                new FsConstructMessage(fsHandleJxtaRequestLogger, carrierCode, carrierTotalLength);
              fsConstructMessage.addFsMessageListener(this);
              addFsConstructMessageListener(fsConstructMessage);
              arrayListPacketCarrier.add(carrierCode);
            }
            propertyChangeSupport4FsMessageConstruct
            .firePropertyChange(new PropertyChangeEvent(carrierCode, "", null, packetCarrier));

          } catch (Exception ex) {
            logger.error(serverUtil.getStackTrace(ex));
          } finally {
            try {
              if (ois != null) {
                ois.close();
              }
              if (is != null) {
                is.close();
              }
            } catch (IOException e) {
              ;
            }
          }
        }
      } else {
        logger.debug("Null Message Received");
      }
    } catch (Exception e) {
      logger.error(serverUtil.getStackTrace(e));
    }
  }

  /**
   * Handles PropertyChangeEvent fired by FsHandleJxtaResponse.   
   * @param evt PropertyChangeEvent object.
   */
  public synchronized void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals(Integer.toString(FSMESSAGE))) {
      handleFsMessage((FsRequest)evt.getNewValue());
    } else if (evt.getPropertyName().equals(Integer.toString(REMOVE_CARRIER_CODE))) {
      arrayListPacketCarrier.remove((String)evt.getNewValue());
    } else if (evt.getPropertyName().equals(Integer.toString(REMOVE_PROPERTY_CHANGE_LISTENER))) {
      removeFsConstructMessageListener((FsConstructMessage)evt.getNewValue());
    }
  }

  /**
   * To handle various client requests.
   * @param fsRequest FsRequest object from client.
   */
  private void handleFsMessage(FsRequest fsRequest) {


    FsResponse fsResponse = null;
    FsFolder fsFolder;
    String folderPath;
    String currFolderPath;
    try {
      int requestCode = fsRequest.getRequestCode();
      int operationCode = fsRequest.getOperation();
      if (operationCode == (FsRemoteOperationConstants.AUTHENTICATION)) {
        authentication(fsRequest);
      } else if (operationCode == (FsRemoteOperationConstants.COMMAND)) {
        remoteOperationCommand(fsRequest);
      } else if (operationCode == (FsRemoteOperationConstants.DISCONNECTION)) {
        disconnection(fsRequest);
      } else if (operationCode == (FsRemoteOperationConstants.UPLOAD)) {
        uploadOperation(fsRequest);
      } else if (operationCode == (FsRemoteOperationConstants.DOWNLOAD)) {
        downloadOperation(fsRequest);
      } else if (operationCode == (FsRemoteOperationConstants.SYNC_COMMAND)) {
        syncCommandOperation(fsRequest);
      } else if (operationCode == (FsRemoteOperationConstants.SYNC_UPLOAD)) {
        syncUploadOperation(fsRequest);
      } else if (operationCode == (FsRemoteOperationConstants.SYNC_DOWNLOAD)) {
        syncDownloadOperation(fsRequest);
      } else if (operationCode == (FsRemoteOperationConstants.REMOTE_BROWSER)) {
        remoteOperationCommand(fsRequest);
      } else if (operationCode == (FsRemoteOperationConstants.COPY)) {
        copyOperation(fsRequest);
      } else if (operationCode == (FsRemoteOperationConstants.MOVE)) {
        moveOperation(fsRequest);
      } else {
        switch (requestCode) {


          case FsMessage.GET_SUB_FOLDERS :
            fsResponse = clientHandlerHelper.getSubFolders(fsRequest);
            fsMessageSender.send(fsResponse);
            break;

          case FsMessage.GET_FOLDER_CONTENT_RECURSIVE :
            fsResponse = clientHandlerHelper.getFolderContentRecursive(fsRequest);
            fsMessageSender.send(fsResponse);
            break;


            /*  case FsMessage.DOWNLOAD_START :
        {
          logger.info("Download Started");
          FsHandleDownloadServer fsHandleDownloadServer =
            new FsHandleDownloadServer(fsRequest.getSuperRequestCode(), this);
          //fsHandleDownloadServer.addPropertyChangeListener(this);
          addPropertyChangeListener(fsHandleDownloadServer);
          propertyChangeSupport
          .firePropertyChange(new PropertyChangeEvent(fsRequest.getSuperRequestCode(), "fsRequest", null, fsRequest));
        }
        break;*/
            /*case FsMessage.SYNC_DOWNLOAD_START :
        {
          logger.info("Sync Download Started");
          FsHandleSyncDownloadServer fsHandleSyncDownloadServer =
            new FsHandleSyncDownloadServer(fsRequest.getSuperRequestCode(), this);
          //fsHandleSyncDownloadServer.addPropertyChangeListener(this);
          addPropertyChangeListener(fsHandleSyncDownloadServer);
          propertyChangeSupport
          .firePropertyChange(new PropertyChangeEvent(fsRequest.getSuperRequestCode(), "fsRequest", null, fsRequest));
        }
        break;*/
            /*case FsMessage.UPLOAD_START :
        {
          logger.info("Upload Started");
          FsHandleUploadServer fsHandleUploadServer =
            new FsHandleUploadServer(fsRequest.getSuperRequestCode(), this);
         // fsHandleUploadServer.addPropertyChangeListener(this);
          addPropertyChangeListener(fsHandleUploadServer);
          propertyChangeSupport
          .firePropertyChange(new PropertyChangeEvent(fsRequest.getSuperRequestCode(), "fsRequest", null, fsRequest));
        }
        break;*/
            /* case FsMessage.SYNC_UPLOAD_START :
        {
          logger.info("Sync Upload Started");
          FsHandleSyncUploadServer fsHandleSyncUploadServer =
            new FsHandleSyncUploadServer(fsRequest.getSuperRequestCode(), this);
          //fsHandleSyncUploadServer.addPropertyChangeListener(this);
          addPropertyChangeListener(fsHandleSyncUploadServer);
          propertyChangeSupport
          .firePropertyChange(new PropertyChangeEvent(fsRequest.getSuperRequestCode(), "fsRequest", null, fsRequest));
        }
        break;*/
            /* case FsMessage.REMOTE_ITEM_COPY :
        {
          logger.info("Copy operation Started");
          FsHandleRemoteCopyServer fsHandleRemoteCopyServer =
            new FsHandleRemoteCopyServer(fsRequest.getSuperRequestCode(), this);
          //fsHandleCopyMoveServer.addPropertyChangeListener(this);
          addPropertyChangeListener(fsHandleRemoteCopyServer);
          propertyChangeSupport
          .firePropertyChange(new PropertyChangeEvent(fsRequest.getSuperRequestCode(), "fsRequest", null, fsRequest));
        }
        break;*/
          case FsMessage.REMOTE_ITEM_MOVE :
            {
              logger.info("Move operation Started");
              FsHandleRemoteMoveServer fsHandleRemoteMoveServer =
                new FsHandleRemoteMoveServer(fsRequest.getSuperRequestCode(), this);
              //fsHandleCopyMoveServer.addPropertyChangeListener(this);
              addPropertyChangeListener(fsHandleRemoteMoveServer);
              propertyChangeSupport
              .firePropertyChange(new PropertyChangeEvent(fsRequest.getSuperRequestCode(), "fsRequest", null,
                                                                               fsRequest));
            }
            break;
          case FsMessage.CHANGE_DIRECTORY :
            folderPath = (String)fsRequest.getData();
            folderPath =
              fsConnection.getAbsolutePath(folderPath, clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath());
            clientHandlerHelper.getFolderDocInfoRemote().addFolderPath(folderPath);
            break;

          default :
            if (propertyChangeSupport != null) {
              logger.debug("fsRequest.getRequestCode() : " + fsRequest.getRequestCode());
              propertyChangeSupport
              .firePropertyChange(new PropertyChangeEvent(fsRequest.getSuperRequestCode(), "fsRequest", null,
                                                                               fsRequest));
            }
        }
      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
        fsResponse.setResponseCode(FsMessage.ERROR_MESSAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsMessage.FETAL_ERROR);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
        fsResponse.setResponseCode(FsMessage.ERROR_MESSAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsMessage.FETAL_ERROR);
        fsMessageSender.send(fsResponse);
      }
    }

  }

  private void authentication(FsRequest fsRequest) {
    FsResponse fsResponse;
    int requestCode = fsRequest.getRequestCode();
    if (requestCode == FsAuthenticationListener.AUTHENTICATE) {
      logger.debug("Connect user");
      fsResponse = validateUser((FsUser)fsRequest.getData());
      fsResponse.setOperation(FsRemoteOperationConstants.AUTHENTICATION);
      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
      fsMessageSender.send(fsResponse);
    }
  }

  private void disconnection(FsRequest fsRequest) {
    FsResponse fsResponse = null;
    FsFolder fsFolder;
    String currFolderPath;
    try {
      int requestCode = fsRequest.getRequestCode();
      if (requestCode == FsDisconnectionConstants.DISCONNECT) {
        fsResponse = clientHandlerHelper.disconnectUser(fsRequest);
        fsMessageSender.send(fsResponse);
      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
        fsResponse.setResponseCode(FsMessage.ERROR_MESSAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsMessage.FETAL_ERROR);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
        fsResponse.setResponseCode(FsMessage.ERROR_MESSAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsMessage.FETAL_ERROR);
        fsMessageSender.send(fsResponse);
      }
    }

  }

  private void remoteOperationCommand(FsRequest fsRequest) {
    FsResponse fsResponse = null;
    FsFolder fsFolder;
    String currFolderPath;
    try {
      int requestCode = fsRequest.getRequestCode();
      switch (requestCode) {
        case FsRemoteCommandListener.GET_ROOT_FOLDERS :
          fsResponse = clientHandlerHelper.getRootFolders(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsRemoteCommandListener.GET_HOME_FOLDER :
          fsResponse = clientHandlerHelper.getHomeFolder(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsRemoteCommandListener.GET_FOLDER_ROOT :
          fsResponse = clientHandlerHelper.getFolderRoot(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsRemoteCommandListener.GET_FLAT_FOLDER_TREE :
          fsResponse = clientHandlerHelper.getFlatFolderTree(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsRemoteCommandListener.GETCONTENTOFFOLDER :
          fsResponse = clientHandlerHelper.getContentOfFolder(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsRemoteCommandListener.SEARCH :
          fsResponse = clientHandlerHelper.getContentOfFolder(fsRequest);
          logger.debug("fsResponse.getResponseCode() : " + fsResponse.getResponseCode());
          fsMessageSender.send(fsResponse);
          if (fsResponse.getResponseCode() != FsRemoteCommandListener.SEARCH_FAILED) {
            fsResponse = clientHandlerHelper.getFlatFolderTree(fsRequest);
          }
          break;
        case FsRemoteCommandListener.NAVIGATE_FORWARD :
          fsResponse = clientHandlerHelper.navigateForward(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsRemoteCommandListener.GETCONTENTOFPARENTFOLDER :
          fsResponse = clientHandlerHelper.getContentOfParentFolder(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsRemoteCommandListener.NAVIGATE_BACK :
          fsResponse = clientHandlerHelper.navigateBack(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsRemoteCommandListener.RENAME :
          fsResponse = clientHandlerHelper.rename(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsRemoteCommandListener.DELETE :
          fsResponse = clientHandlerHelper.deleteItems(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsRemoteCommandListener.GET_FILE_FOLDER_PROPERTIES :
          fsResponse = clientHandlerHelper.showProperties(fsRequest);
          fsMessageSender.send(fsResponse);
          logger.debug("sending response for Property Page File......... from server Side......");
          break;
        case FsRemoteCommandListener.MK_DIR :
          logger.debug("request reached on server :" + fsRequest.getRequestCode());
          clientHandlerHelper.createNewFolder(fsRequest);
          break;
        case FsRemoteCommandListener.CHANGE_PROPERTIES_OF_FILE :
          clientHandlerHelper.changePropertiesOfFile(fsRequest);
          break;
        case FsRemoteCommandListener.CHANGE_PROPERTIES_OF_FOLDER :
          clientHandlerHelper.changePropertiesOfFolder(fsRequest);
          break;
      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
        fsResponse.setResponseCode(FsRemoteCommandListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsRemoteCommandListener.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setResponseCode(FsRemoteCommandListener.ERROR_MESSAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setResponseCode(FsRemoteCommandListener.FETAL_ERROR);
        fsMessageSender.send(fsResponse);
      }
    }

  }

  private void uploadOperation(FsRequest fsRequest) {
    FsResponse fsResponse = null;
    FsFolder fsFolder;
    String currFolderPath;
    try {
      int requestCode = fsRequest.getRequestCode();
      if (requestCode == FsUploadConstants.START) {
        logger.info("Upload Started");
        FsHandleUploadServer fsHandleUploadServer = new FsHandleUploadServer(fsRequest.getSuperRequestCode(), this);
        addUploadListener(fsHandleUploadServer);
      }
      propertyChangeSupport4Upload.firePropertyChange(fsRequest.getSuperRequestCode(), null, fsRequest);
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setResponseCode(FsUploadConstants.ERROR_MESSAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsUploadConstants.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setResponseCode(FsUploadConstants.ERROR_MESSAGE);
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setResponseCode(FsUploadConstants.FETAL_ERROR);
        fsMessageSender.send(fsResponse);
      }
    }

  }

  private void downloadOperation(FsRequest fsRequest) {
    FsResponse fsResponse = null;
    FsFolder fsFolder;
    String currFolderPath;
    try {
      int requestCode = fsRequest.getRequestCode();
      if (requestCode == FsDownloadConstants.START) {
        logger.info("Download Started");
        FsHandleDownloadServer fsHandleDownloadServer =
          new FsHandleDownloadServer(fsRequest.getSuperRequestCode(), this);
        addDownloadListener(fsHandleDownloadServer);
      }
      propertyChangeSupport4Download.firePropertyChange(fsRequest.getSuperRequestCode(), null, fsRequest);
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
        fsResponse.setResponseCode(FsDownloadConstants.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsDownloadConstants.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);

        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
        fsResponse.setResponseCode(FsDownloadConstants.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsDownloadConstants.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsMessageSender.send(fsResponse);
      }
    }
  }

  private void syncCommandOperation(FsRequest fsRequest) {
    FsResponse fsResponse = null;
    FsFolder fsFolder;
    String currFolderPath;
    try {
      int requestCode = fsRequest.getRequestCode();
      switch (requestCode) {
        case FsSyncCommandListener.PREVIEW :
          fsResponse = clientHandlerHelper.getSyncXML(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
        case FsSyncCommandListener.REMOTE_DELETE :
          fsResponse = clientHandlerHelper.syncDeleteItem(fsRequest);
          fsMessageSender.send(fsResponse);
          break;
      }
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
        fsResponse.setResponseCode(FsSyncCommandListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsSyncCommandListener.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
        fsResponse.setResponseCode(FsSyncCommandListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsSyncCommandListener.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    }
  }


  private void syncUploadOperation(FsRequest fsRequest) {
    FsResponse fsResponse = null;
    FsFolder fsFolder;
    String currFolderPath;
    try {
      int requestCode = fsRequest.getRequestCode();
      if (requestCode == FsSyncUploadListener.START) {
        logger.info("Sync Upload Started");
        FsHandleSyncUploadServer fsHandleSyncUploadServer =
          new FsHandleSyncUploadServer(fsRequest.getSuperRequestCode(), this);
        addSyncUploadListener(fsHandleSyncUploadServer);
      }
      propertyChangeSupport4SyncUpload.firePropertyChange(fsRequest.getSuperRequestCode(), null, fsRequest);
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
        fsResponse.setResponseCode(FsSyncUploadListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsSyncUploadListener.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
        fsResponse.setResponseCode(FsSyncUploadListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsSyncUploadListener.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    }
  }

  private void syncDownloadOperation(FsRequest fsRequest) {
    FsResponse fsResponse = null;
    FsFolder fsFolder;
    String currFolderPath;
    try {
      int requestCode = fsRequest.getRequestCode();
      if (requestCode == FsSyncDownloadListener.START) {
        logger.info("Sync Download Started");
        FsHandleSyncDownloadServer fsHandleSyncDownloadServer =
          new FsHandleSyncDownloadServer(fsRequest.getSuperRequestCode(), this);
        addSyncDownloadListener(fsHandleSyncDownloadServer);
      }
      propertyChangeSupport4SyncDownload.firePropertyChange(fsRequest.getSuperRequestCode(), null, fsRequest);
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
        fsResponse.setResponseCode(FsSyncDownloadListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsSyncDownloadListener.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
        fsResponse.setResponseCode(FsSyncDownloadListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsSyncDownloadListener.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsMessageSender.send(fsResponse);
      }
    }
  }

  private void copyOperation(FsRequest fsRequest) {
    FsResponse fsResponse = null;
    FsFolder fsFolder;
    String currFolderPath;
    try {
      int requestCode = fsRequest.getRequestCode();
      if (requestCode == FsRemoteCopyListener.START) {
        logger.info("Copy Started");
        FsHandleRemoteCopyServer fsHandleRemoteCopyServer =
          new FsHandleRemoteCopyServer(fsRequest.getSuperRequestCode(), this);
        addRemoteCopyListener(fsHandleRemoteCopyServer);
      }
      propertyChangeSupport4RemoteCopy.firePropertyChange(fsRequest.getSuperRequestCode(), null, fsRequest);
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
        fsResponse.setResponseCode(FsRemoteCopyListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsRemoteCopyListener.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
        fsResponse.setResponseCode(FsRemoteCopyListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        ;
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsRemoteCopyListener.FETAL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    }
  }

  private void moveOperation(FsRequest fsRequest) {
    FsResponse fsResponse = null;
    FsFolder fsFolder;
    String currFolderPath;
    try {
      int requestCode = fsRequest.getRequestCode();
      if (requestCode == FsRemoteMoveListener.START) {
        logger.info("Move Started");
        FsHandleRemoteMoveServer fsHandleRemoteMoveServer =
          new FsHandleRemoteMoveServer(fsRequest.getSuperRequestCode(), this);
        addRemoteMoveListener(fsHandleRemoteMoveServer);
      }
      propertyChangeSupport4RemoteMove.firePropertyChange(fsRequest.getSuperRequestCode(), null, fsRequest);
    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(fex));
        fsResponse.setResponseCode(FsRemoteMoveListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsRemoteMoveListener.FATEL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      try {
        fsResponse = new FsResponse();
        currFolderPath = clientHandlerHelper.getFolderDocInfoRemote().getCurrentFolderPath();
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currFolderPath);
        FsObjectHolder[] fsObjectHolders = clientHandlerHelper.getFolderContent(fsFolder);
        fsResponse.setDatas(fsObjectHolders);
        fsResponse.setFsFolderDocInfoHolder(clientHandlerHelper.getFolderDocInfoRemote().getFolderDocInfoHolder());

        fsResponse.setFsExceptionHolder(serverUtil.getFsExceptionHolder(ex));
        fsResponse.setResponseCode(FsRemoteMoveListener.ERROR_MESSAGE);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      } catch (FsException fex1) {
        fsResponse = new FsResponse();
        fsResponse.setResponseCode(FsRemoteMoveListener.FATEL_ERROR);
        fsResponse.setOperation(fsRequest.getOperation());
        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
        fsMessageSender.send(fsResponse);
      }
    }
  }

  /**
   * To add a PropertyChangeListener that will listen for the PropertyChangeEvent fired 
   * by this class.
   * @param propertyChangeListener PropertyChangeListener which will listen to the PropertyChangeEvent
   * fired by this class.
   */
  public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
    }
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }

  public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    if (propertyChangeSupport != null) {
      propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
    }

  }


  /**
   * returns String representation of FsClientHandler object.
   * @return String repersentation of FsClientHandler object.
   */
  public String toString() {
    return this.toString();
  }


  public FsConnection getFsConnection() {
    return fsConnection;
  }

  public FsMessageSender getFsMessageSender() {
    return fsMessageSender;
  }


}

