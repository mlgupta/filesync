///*
// *****************************************************************************
// *                       Confidentiality Information                         *
// *                                                                           *
// * This module is the confidential and proprietary information of            *
// * DBSentry Corp.; it is not to be copied, reproduced, or transmitted in any *
// * form, by any means, in whole or in part, nor is it to be used for any     *
// * purpose other than that for which it is expressly provided without the    *
// * written permission of DBSentry Corp.                                      *
// *                                                                           *
// * Copyright (c) 2004-2005 DBSentry Corp.  All Rights Reserved.              *
// *                                                                           *
// *****************************************************************************
// * $Id: FsFileSystemOperationsRemote.java,v 1.110 2006/04/14 14:01:04 sgupta Exp $
// *****************************************************************************
// */
///*package dbsentry.filesync.client;
//
//import dbsentry.filesync.client.jxta.JxtaClient;
////import dbsentry.filesync.common.FsConstructResponse;
//import dbsentry.filesync.common.FsConstructMessage;
//import dbsentry.filesync.common.FsMessage;
//import dbsentry.filesync.common.FsMessageSender;
//import dbsentry.filesync.common.FsRequest;
//import dbsentry.filesync.common.FsResponse;
//import dbsentry.filesync.common.FsUser;
//import dbsentry.filesync.common.PacketCarrier;
//
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeSupport;
//
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//
//import java.util.ArrayList;
//import java.util.ListIterator;
//import java.util.Stack;
//
//import javax.swing.JFrame;
//
//import net.jxta.endpoint.Message;
//import net.jxta.endpoint.MessageElement;
//import net.jxta.pipe.PipeMsgEvent;
//import net.jxta.pipe.PipeMsgListener;
//import net.jxta.util.JxtaBiDiPipe;
//
//import org.apache.log4j.Logger;
//
//
///**
// *	To handle various operations on remote filesystem and to redirect the responses 
// *  from server to different classes.
// *  @author              Jeetendra Prasad
// *  @version             1.0
// * 	Date of creation:    11-05-2005
// * 	Last Modfied by :    Jeetendra Prasad
// * 	Last Modfied Date:   01-07-2005
// */
//public class FsFileSystemOperationsRemote {
//  private Logger logger;
//
//  private PropertyChangeSupport propertyChangeSupport;
//
//  private PropertyChangeSupport propertyChangeSupportForPacketCarrier;
//
//  private ArrayList arrayListPacketCarrier = new ArrayList();
//
//  private FsMessageSender fsMessageSender;
//
//  private ClientUtil clientUtil;
//
//  /**
//   * a constructor.
//   * @param logger an instance of logger object
//   */
//  public FsFileSystemOperationsRemote(Logger logger, JxtaClient jxtaClient) {
//    this.logger = logger;
//
//    this.clientUtil = new ClientUtil(logger);
//
//    //code to initialize this class for jxta comminication
//    JxtaBiDiPipe pipe = jxtaClient.getBidipipe();
//    this.fsMessageSender = new FsMessageSender(pipe, logger);
//    pipe.setMessageListener(new PipeMsgListener() {
//                              public synchronized void pipeMsgEvent(PipeMsgEvent event) {
//                                myPipeMsgEvent(event);
//                              }
//                            }
//    );
//  }
//
//  /**
//   * even listener for the jxta pipe.
//   * @param event pipe message event generated when a jxta message arrives
//   */
//  public synchronized void myPipeMsgEvent(PipeMsgEvent event) {
//    PacketCarrier packetCarrier;
//    String carrierCode;
//    Message msg = null;
//    int streamLength = 0;
//    int carrierTotalLength=0;
//    try {
//      msg = event.getMessage();
//      if (msg != null) {
//        ListIterator list = msg.getMessageElements();
//        if (list.hasNext()) {
//          try {
//            MessageElement msgElement;
//
//            //Gets the Packet Carrier Length
//            msgElement = (MessageElement)list.next();
//            streamLength = Integer.parseInt(msgElement.toString());
//
//            //Gets the Packet Carrier Data          
//            msgElement = (MessageElement)list.next();
//
//            InputStream is = msgElement.getStream();
//            ObjectInputStream ois = new ObjectInputStream(is);
//            packetCarrier = (PacketCarrier)ois.readObject();
//            ois.close();
//            is.close();
//
//            carrierCode = packetCarrier.getCarrierCode();
//            carrierTotalLength=packetCarrier.getTotalLength();
//            if (!arrayListPacketCarrier.contains(carrierCode)) {
//              Logger fsHandleJxtaResponseLogger = Logger.getLogger("ClientLogger");
//              FsConstructMessage fsConstructMessage =
//                new FsConstructMessage(fsHandleJxtaResponseLogger, carrierCode ,carrierTotalLength);
//              /*fsConstructMessage.addPropertyChangeListener(new PropertyChangeListener() {
//                                                              public synchronized void propertyChange(PropertyChangeEvent evt) {
//                                                                propertyChangeJxtaResponse(evt);
//                                                              }
//                                                            }
//              );*/
//              addPropertyChangeListenerForPacketCarrier(fsConstructMessage);
//              arrayListPacketCarrier.add(carrierCode);
//            }
//            propertyChangeSupportForPacketCarrier
//            .firePropertyChange(new PropertyChangeEvent(carrierCode, "packetCarrier", null, packetCarrier));
//            return;
//          } catch (Exception ex) {
//            logger.error(clientUtil.getStackTrace(ex));
//          }
//          return;
//        }
//      } else {
//        logger.debug("Null Message Received");
//      }
//    } catch (Exception e) {
//      logger.error(clientUtil.getStackTrace(e));
//    }
//  }
//
//  /**
//   * property change handler for the jxta response received.
//   * @param evt property change even containing the jxta response
//   */
//  public synchronized void propertyChangeJxtaResponse(PropertyChangeEvent evt) {
//    if (evt.getPropertyName().equals("fsMessage")) {
//      //this propertyChange is fired by FsHandleJxtaResponse
//      FsResponse fsResponse = (FsResponse)evt.getNewValue();
//      int responseCode = fsResponse.getResponseCode();
//      logger.debug("responseCode : " + responseCode);
//      handleGeneralResponse(fsResponse);
//    } else if (evt.getPropertyName().equals("removeCarrierCode")) {
//      arrayListPacketCarrier.remove((String)evt.getNewValue());
//    } else if (evt.getPropertyName().equals("removePropertyChangeSupportForPacketCarrier")) {
//      propertyChangeSupportForPacketCarrier.removePropertyChangeListener((FsConstructMessage)evt.getNewValue());
//    }
//  }
//
//  /**
//   * Sends a request to copy an item to the specified location..
//   * @param guiFrame object of the JFrame class which will act as parent of the dialog box
//   * @param folderPath folder where the item has to be copied
//   * @param itemPath path of the item to be copied
//   * @param superRequestCode a token used to distinguish which thread will handle the response
//   */
// /* public void itemCopy(JFrame guiFrame, String folderPath, String itemPath, String superRequestCode) {
//
//    Logger logger = Logger.getLogger("ClientLogger");
//    logger.debug("In ItemCopy");
//    FsHandleRemoteCopyClient fsHandleRemoteCopyClient = new FsHandleRemoteCopyClient(guiFrame, logger, this);
//    addPropertyChangeListener(fsHandleRemoteCopyClient);
//    fsHandleRemoteCopyClient.addPropertyChangeListener(new PropertyChangeListener() {
//                                                       public synchronized void propertyChange(PropertyChangeEvent evt) {
//                                                         propertyChangeForRemoteOperations(evt);
//                                                       }
//                                                     }
//    );
//    fsHandleRemoteCopyClient.itemCopy(folderPath, itemPath, superRequestCode);
//  }
//
//  /**
//   * Sends a request to copy specified items to specified folder.
//   * @param guiFrame object of the JFrame class which will act as parent of the dialog box
//   * @param folderPath folder where the item has to be copied
//   * @param itemPaths array of paths of the items to be copied
//   * @param superRequestCode a token used to distinguish which thread will handle the response
//   */
///*  public void itemCopy(JFrame guiFrame, String folderPath, String[] itemPaths, String superRequestCode) {
//
//    Logger logger = Logger.getLogger("ClientLogger");
//    logger.debug("In ItemCopy");
//    FsHandleRemoteCopyClient fsHandleRemoteCopyClient = new FsHandleRemoteCopyClient(guiFrame, logger, this);
//    addPropertyChangeListener(fsHandleRemoteCopyClient);
//    fsHandleRemoteCopyClient.addPropertyChangeListener(new PropertyChangeListener() {
//                                                       public synchronized void propertyChange(PropertyChangeEvent evt) {
//                                                         propertyChangeForRemoteOperations(evt);
//                                                       }
//                                                     }
//    );
//    fsHandleRemoteCopyClient.itemCopy(folderPath, itemPaths, superRequestCode);
//  }*/
//
//  /**
//   * Sends a request to move an item to the specified location.
//   * @param guiFrame object of the JFrame class which will act as parent of the dialog box
//   * @param folderPath folder where the item has to be moved
//   * @param itemPath path of the item to be moved
//   * @param superRequestCode a token used to distinguish which thread will handle the response
//   */
//  /*public void itemMove(JFrame guiFrame, String folderPath, String itemPath, String superRequestCode) {
//
//    Logger logger = Logger.getLogger("ClientLogger");
//    logger.debug("In Item Move");
//    FsHandleRemoteMoveClient fsHandleRemoteMoveClient = new FsHandleRemoteMoveClient(guiFrame, logger, this);
//    addPropertyChangeListener(fsHandleRemoteMoveClient);
//    fsHandleRemoteMoveClient.addPropertyChangeListener(new PropertyChangeListener() {
//                                                       public synchronized void propertyChange(PropertyChangeEvent evt) {
//                                                         propertyChangeForRemoteOperations(evt);
//                                                       }
//                                                     }
//    );
//    fsHandleRemoteMoveClient.itemMove(folderPath, itemPath, superRequestCode);
//  }
//
//  /**
//   * Sends a request to move specified items to specified folder.
//   * @param guiFrame object of the JFrame class which will act as parent of the dialog box
//   * @param folderPath folder where the item has to be moved
//   * @param itemPaths array of paths of the items to be moved
//   * @param superRequestCode a token used to distinguish which thread will handle the response
//   */
//  /*public void itemMove(JFrame guiFrame, String folderPath, String[] itemPaths, String superRequestCode) {
//
//    Logger logger = Logger.getLogger("ClientLogger");
//    logger.debug("In Item Move");
//    FsHandleRemoteMoveClient fsHandleRemoteMoveClient = new FsHandleRemoteMoveClient(guiFrame, logger, this);
//    addPropertyChangeListener(fsHandleRemoteMoveClient);
//    fsHandleRemoteMoveClient.addPropertyChangeListener(new PropertyChangeListener() {
//                                                       public synchronized void propertyChange(PropertyChangeEvent evt) {
//                                                         propertyChangeForRemoteOperations(evt);
//                                                       }
//                                                     }
//    );
//    fsHandleRemoteMoveClient.itemMove(folderPath, itemPaths, superRequestCode);
//
//  }*/
//
//
//  /**
//   * To add a property change listener for this class.
//   * @param propertyChangeListener propertyChangeListener object which will listen for the property change of objects in this class
//   */
//  public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
//    if (propertyChangeSupport == null) {
//      propertyChangeSupport = new PropertyChangeSupport(this);
//    }
//    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
//  }
//
//  /**
//   * To add a property change listener for packet carrier message.
//   * @param propertyChangeListener propertyChangeListener object which will handle the packet carrier message received
//   */
//  public void addPropertyChangeListenerForPacketCarrier(PropertyChangeListener propertyChangeListener) {
//    if (propertyChangeSupportForPacketCarrier == null) {
//      propertyChangeSupportForPacketCarrier = new PropertyChangeSupport(this);
//    }
//    propertyChangeSupportForPacketCarrier.addPropertyChangeListener(propertyChangeListener);
//  }
//
//  /**
//   * To remove a PropertyChangeListener.
//   * @param listener to be removed 
//   */
//  public void removePropertyChange(PropertyChangeListener listener) {
//    logger.debug("Removing from listener list: " + listener);
//    propertyChangeSupport.removePropertyChangeListener(listener);
//  }
//
//  /**
//   * Prepares to upload the specified items.
//   * @param guiFrame frame that will act as the parent of the dialog box
//   * @param itemToUpload stack of items to upload
//   * @param currentFolderPathLocal current folder path on local system
//   * @param currentFolderPathRemote current folder path on remote system
//   * @param superRequestCode a token indicating the thread which will handle the response
//   */ /*
//  public void uploadItem(JFrame guiFrame, Stack itemToUpload, String currentFolderPathLocal,
//                         String currentFolderPathRemote, String superRequestCode) {
//    Logger logger = Logger.getLogger("ClientLogger");
//    FsHandleUploadClient fsHandleUploadClient = new FsHandleUploadClient(logger, this);
//    addPropertyChangeListener(fsHandleUploadClient);
//    fsHandleUploadClient.addPropertyChangeListener(new PropertyChangeListener() {
//                                                     public synchronized void propertyChange(PropertyChangeEvent evt) {
//                                                       propertyChangeForRemoteOperations(evt);
//                                                     }
//                                                   }
//    );
//    fsHandleUploadClient.uploadItem(itemToUpload, currentFolderPathLocal, currentFolderPathRemote);
//  }*/
//
//  /**
//   * To handle property change fired in upload or download operation.
//   * @param evt property change event
//   */
//  public void propertyChangeForRemoteOperations(PropertyChangeEvent evt) {
//    logger.debug("evt.getPropertyName() : " + evt.getPropertyName());
//    propertyChangeSupport.firePropertyChange(evt);
//  }
//
//  /**
//   * Purpose : Prepares to download the specified items.
//   * @param guiFrame frame that will act as the parent of the dialog box
//   * @param itemToDownload stack of items to upload
//   * @param currentFolderPathLocal current folder path on local system
//   * @param currentFolderPathRemote current folder path on remote system
//   * @param superRequestCode a token indicating the thread which will handle the response
//   */
// /* public void downloadItem(JFrame guiFrame, String[] itemToDownload, String currentFolderPathLocal,
//                           String currentFolderPathRemote, String superRequestCode) {
//    Logger logger = Logger.getLogger("ClientLogger");
//    FsHandleDownloadClient fsHandleDownloadClient = new FsHandleDownloadClient(guiFrame, logger, this);
//    addPropertyChangeListener(fsHandleDownloadClient);
//    fsHandleDownloadClient.addPropertyChangeListener(new PropertyChangeListener() {
//                                                       public synchronized void propertyChange(PropertyChangeEvent evt) {
//                                                         propertyChangeForRemoteOperations(evt);
//                                                       }
//                                                     }
//    );
//    fsHandleDownloadClient
//    .downloadItem(itemToDownload, currentFolderPathLocal, currentFolderPathRemote, superRequestCode);
//  }*/
//
//  /**
//   * Handles the responses from the server which are meant for this and redirects 
//   * remaining to other classes.
//   * @param fsResponse a response object to be handled
//   */
//  private void handleGeneralResponse(FsResponse fsResponse) {
//    logger.debug("fsResponse.getResponseCode() : " + fsResponse.getResponseCode());
//    propertyChangeSupport
//    .firePropertyChange(new PropertyChangeEvent(fsResponse.getSuperResponseCode(), "fsResponse", null, fsResponse));
//  }
//
//  /**
//   * Sends a request to navigate back in the navigation history.
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void navigateBack(String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setRequestCode(FsRequest.NAVIGATEBACK);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to navigate forward in the navigation history.
//   * @param  superRequestCode a token indicating which thread will handle the response
//   */
//  public void navigateForward(String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setRequestCode(FsRequest.NAVIGATEFORWARD);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to navigate up to the parent.
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void navigateUp(String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setRequestCode(FsRequest.GETCONTENTOFPARENTFOLDER);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to get the contents of specified folder.
//   * @param folderPath path of the folder whose content has to be retrieved
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void getContentOfFolder(String folderPath, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setData(folderPath);
//    fsRequest.setRequestCode(FsMessage.GETCONTENTOFFOLDER);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to fetch folder contents strating at start folderPath till the 
//   * endFolderPath. 
//   * @param startFolderPath path of the starting folder
//   * @param endFolderPath path of the end folder
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void getFlatFolderTree(String startFolderPath, String endFolderPath, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setData(endFolderPath);
//    fsRequest.setData1(startFolderPath);
//    fsRequest.setRequestCode(FsMessage.GET_FLAT_FOLDER_TREE);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to get the sub folders of specified folder.
//   * @param folderPath get sub folders of this folder
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void getSubFolders(String folderPath, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setData(folderPath);
//    fsRequest.setRequestCode(FsMessage.GET_SUB_FOLDERS);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to create a folder with specified name at specified location.
//   * @param parentFolderPath parent folder where new folder has to be created
//   * @param folderName name of the new folder
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void createFolder(String parentFolderPath, String folderName, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setData(parentFolderPath);
//    fsRequest.setData1(folderName);
//    fsRequest.setRequestCode(FsRequest.NEWFOLDER);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to rename the specified item.
//   * @param itemPath path of the file or folder which has to be renamed
//   * @param itemName new name of the file or folder
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void renameItem(String itemPath, String itemName, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setData(itemPath);
//    fsRequest.setData1(itemName);
//    fsRequest.setRequestCode(FsRequest.RENAME);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to delete the items with specified itemPaths.
//   * @param itemPaths array of paths of file or folder which has to be renamed
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void deleteItems(String[] itemPaths, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setDatas(itemPaths);
//    fsRequest.setRequestCode(FsRequest.DELETEITEMS);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * delete and sync the file or folder. used while synchronizing a frofile
//   * @param itemPath path of the file or folder which has to be deleted
//   * @param superRequestCode a token indicating which thread will handle the response
//   * @param index index in the object to be synchronised list
//   * @param row row in the sync preview table
//   * @param col column in the sync preview table
//   */
//  public void syncDeleteItem(String itemPath, String superRequestCode, Integer index, Integer row, Integer col) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setData(itemPath);
//    Object datas[] = new Object[3];
//    datas[0] = index;
//    datas[1] = row;
//    datas[2] = col;
//    fsRequest.setDatas(datas);
//    fsRequest.setRequestCode(FsRequest.SYNC_DELETE_ITEMS);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a connect request to connect with the cmsdk database.
//   * @param fsUser user which is connecting
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void connectUser(FsUser fsUser, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setData(fsUser);
//    fsRequest.setRequestCode(FsRequest.CONNECT);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a connect request to connect with the cmsdk database.
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void disconnectUser(String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setRequestCode(FsRequest.DISCONNECT);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to change the current directory to the specified directoryPath.
//   * @param directoryPath the current working directory on the server
//   */
//  public void changeDirectory(String directoryPath) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setRequestCode(FsRequest.CHANGE_DIRECTORY);
//    fsRequest.setData(directoryPath);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to list the folders of filesystem root.
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void getRootFolders(String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setRequestCode(FsRequest.GET_ROOT_FOLDERS);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to get the home folder.
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void getHomeFolder(String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setRequestCode(FsMessage.GET_HOME_FOLDER);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to get the root folder.
//   * @param folderPath folder path whose root has to be found
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void getFolderRoot(String folderPath, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setData(folderPath);
//    fsRequest.setRequestCode(FsMessage.GET_FOLDER_ROOT);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to fetch the contents of specified remote folder recursively.
//   * @param folderPath folder whose content has to be fetched
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void getFolderContentRecursive(String folderPath, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setRequestCode(FsMessage.GET_FOLDER_CONTENT_RECURSIVE);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setData(folderPath);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to get the updated Xml file necessary for sync. purpose.
//   * @param folderPath folder whose updated syncXml has to be fetched
//   * @param xmlForSync xml file whth respect to which updated sync xml has to be fetched
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void getUpdatedSyncXML(String folderPath, String xmlForSync, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setRequestCode(FsMessage.GET_SYNC_XML);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setData(folderPath);
//    fsRequest.setData1(xmlForSync);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * Sends a request to get the properties of remote folder such as name, permissions,
//   * folder count,total size etc.
//   * @param itemPaths array of paths of files or folder whose property has to be fetched
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void showProperties(String[] itemPaths, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setRequestCode(FsMessage.GET_FILE_FOLDER_PROPERTIES);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setData(itemPaths);
//    fsMessageSender.send(fsRequest);
//  }
//
//  /**
//   * To send a request to change the properties of remote file such as 
//   * name ,permissions.
//   * @param evt property change event object
//   */
//  public void changePropertiesOfFile(PropertyChangeEvent evt) {
//    if (evt.getSource().equals(FsMessage.FOR_REMOTE_FILESYSTEM)) {
//      FsRequest fsRequest = new FsRequest();
//      fsRequest.setRequestCode(FsMessage.CHANGE_PROPERTIES_OF_FILE);
//      fsRequest.setSuperRequestCode(FsMessage.FOR_CLIENTGUI);
//      fsRequest.setData(evt.getNewValue());
//      fsMessageSender.send(fsRequest);
//    }
//  }
//
//  /**
//   * Purpose : To send a request to change the properties of remote folder such as 
//   * name ,permissions.
//   * @param evt property change event object
//   */
//  public void changePropertiesOfFolder(PropertyChangeEvent evt) {
//    if (evt.getSource().equals(FsMessage.FOR_REMOTE_FILESYSTEM)) {
//      FsRequest fsRequest = new FsRequest();
//      fsRequest.setRequestCode(FsMessage.CHANGE_PROPERTIES_OF_FOLDER);
//      fsRequest.setData(evt.getNewValue());
//      fsMessageSender.send(fsRequest);
//    }
//  }
//
//  /**
//   * setter for the fsMessageSender object.
//   * @param fsMessageSender fsMessageSender object
//   */
//  public void setFsMessageSender(FsMessageSender fsMessageSender) {
//    this.fsMessageSender = fsMessageSender;
//  }
//
//
//  /**
//   * getter for the fsMessageSender.
//   * @return fsMessageSender fsMessageSender object
//   */
//  public FsMessageSender getFsMessageSender() {
//    return fsMessageSender;
//  }
//
//  /**
//   * gets the list of item in the search path. and also the nodes in the tree which has not arrived
//   * @param arrivedNodePath nodes which has arrived
//   * @param searchPath path whose content has to be fetched
//   * @param superRequestCode a token indicating which thread will handle the response
//   */
//  public void searchPath(String arrivedNodePath, String searchPath, String superRequestCode) {
//    FsRequest fsRequest = new FsRequest();
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsRequest.setData(searchPath);
//    fsRequest.setData1(arrivedNodePath);
//    fsRequest.setRequestCode(FsMessage.SEARCH_PATH);
//    fsRequest.setSuperRequestCode(superRequestCode);
//    fsMessageSender.send(fsRequest);
//  }
//
//}
