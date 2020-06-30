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
 * $Id$
 *****************************************************************************
 */

package dbsentry.filesync.server;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsSocketPacketCarrier;
import dbsentry.filesync.common.constants.FsSocketConstants;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsObject;
import dbsentry.filesync.server.jxta.JxtaSocketServer;

import dbsentry.plugin.cmsdk.CmsdkException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;

import java.util.Hashtable;
import java.util.Random;

import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.socket.JxtaServerSocket;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;


/**
 * @author Saurabh Gupta
 * @version 1.2
 * @Date of creation:10-07-2006
 * @Last Modfied by :Saurabh gupta
 * @Last Modfied Date:04-08-2006
 */
public class FsSocketServer implements FsSocketConstants, Runnable {

  private Logger logger;

  private Hashtable connectedUsers;

  private JxtaServerSocket serverSocket;

  private ServerUtil serverUtil;

  private CommonUtil commonUtil;


  public FsSocketServer(PeerGroup secureNetPeerGroup, Hashtable connectedUsers, Logger logger) {
    FileInputStream fis4socket = null;
    try {
      this.logger = logger;
      this.serverUtil = new ServerUtil(logger);
      this.commonUtil = new CommonUtil(logger);

      //This will be used for ServetSocket
      fis4socket = new FileInputStream("config/socket.adv");
      XMLDocument xmlDocSocket =
        (XMLDocument)StructuredDocumentFactory.newStructuredDocument(MimeMediaType.XMLUTF8, fis4socket);
      PipeAdvertisement socketAdv = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(xmlDocSocket);

      this.serverSocket = (new JxtaSocketServer(secureNetPeerGroup, socketAdv, logger)).createServerSocket();
      this.connectedUsers = connectedUsers;
    } catch (Exception e) {
      logger.error(serverUtil.getStackTrace(e));
    } finally {
      try {
        if (fis4socket != null) {
          fis4socket.close();
        }
      } catch (IOException e) {
        ;
      }
    }
  }


  public void run() {
    //Creating Socket
    logger.info("starting ServerSocket");
    while (true) {
      try {
        logger.debug("Socket Calling accept");
        Socket socket = serverSocket.accept();
        // set reliable
        if (socket != null) {
          logger.debug("socket created");
          Thread thread = new Thread(new DataHandler(socket, connectedUsers), "Connection Handler Thread");
          thread.start();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  private class DataHandler implements Runnable, FsSocketConstants {

    private Socket socket = null;

    private Hashtable connectedUsers = null;


    public DataHandler(Socket socket, Hashtable connectedUsers) {
      this.socket = socket;
      this.connectedUsers = connectedUsers;
    }

    public void run() {
      try {

        logger.debug("inside run method frm server side..................................");
        handleDataFromClient();


      } catch (Exception e) {
        serverUtil.getStackTrace(e);
      }
    }

    private void handleDataFromClient() {
      FsSocketPacketCarrier dataCarrierRequest = null;
      FsSocketPacketCarrier dataCarrierResponse = null;
      ObjectInputStream ois = null;
      ObjectOutputStream oos = null;
      try {
        ois = new ObjectInputStream(socket.getInputStream());
        oos = new ObjectOutputStream(socket.getOutputStream());
        try {
          dataCarrierRequest = (FsSocketPacketCarrier)ois.readObject();
          if (dataCarrierRequest != null) {
            switch (dataCarrierRequest.getSuperOperation()) {
              case SOCKET_RECEIVE :
                dataCarrierResponse = new FsSocketPacketCarrier();
                dataCarrierResponse.setOperation(SOCKET_UPLOAD_READY);
                oos.writeObject(dataCarrierResponse);
                oos.flush();
                oos.reset();

                receiveDataFromClient(ois, oos);
                break;
              case SOCKET_SEND :
                dataCarrierResponse = new FsSocketPacketCarrier();
                dataCarrierResponse.setOperation(SOCKET_DOWNLOAD_READY);
                oos.writeObject(dataCarrierResponse);
                oos.flush();
                oos.reset();

                sendingDataToClient(ois, oos);
                break;
              case SOCKET_SYNC_RECEIVE:
                dataCarrierResponse = new FsSocketPacketCarrier();
                dataCarrierResponse.setOperation(SOCKET_SYNC_UPLOAD_READY);
                oos.writeObject(dataCarrierResponse);
                oos.flush();
                oos.reset();
  
                receiveSyncDataFromClient(ois, oos);
                break;
              case SOCKET_SYNC_SEND:
                dataCarrierResponse = new FsSocketPacketCarrier();
                dataCarrierResponse.setOperation(SOCKET_SYNC_DOWNLOAD_READY);
                oos.writeObject(dataCarrierResponse);
                oos.flush();
                oos.reset();
                
                sendSyncDataForClient(ois, oos);
                break;
            }
          }
        } catch (ClassNotFoundException e) {
          ;
        }


      } catch (IOException e) {
        ;
      }
      /*finally{
        
          try {
            if(ois!=null){
            ois.close();
            }
            if(oos!=null){
              oos.close();
            }
          }
          catch (IOException e) {
            ;
          }
       
      }*/
    }

  private void sendSyncDataForClient(ObjectInputStream ois, ObjectOutputStream oos){
    boolean dontExit = true;
    ObjectInputStream ois4SyncDownload = ois;
    ObjectOutputStream oos4SyncDownload = oos;
    FsExceptionHolder fsExceptionHolder;
    FsConnection fsConnection = null;
    FsObject downloadSyncFsObject;
    FsFile downloadSyncFile = null;
    InputStream downloadSyncFileInputStream = null;
    byte[] buffer = new byte[commonUtil.JXTA_SOCKET_BUFFER_SIZE];

    FsSocketPacketCarrier dataCarrierRequest = null;
    FsSocketPacketCarrier dataCarrierResponse = null;

    logger.debug(" Inside sendingDataForClient()..........................");
    try{
      while(dontExit){
        long byteRead = -1;
        long total = 0;
        try{
          try {
            dataCarrierRequest = (FsSocketPacketCarrier)ois4SyncDownload.readObject();
          } catch (Exception e) {
            dataCarrierRequest = null;
          }
          if (dataCarrierRequest != null){
            logger.debug("Socket operation code is: " + dataCarrierRequest.getOperation());
            switch (dataCarrierRequest.getOperation()){

              case USER_NAME :
                logger.debug("msg frm client to server for username ..........................");
                try {
                  fsConnection =((FsUserInfo)connectedUsers.get(new String(dataCarrierRequest.getData()))).getFsConnection();
              
                  String itemToDownload = dataCarrierRequest.getData2().toString();
                  logger.debug("itemToDownload : " + itemToDownload);
                  downloadSyncFsObject = fsConnection.findFsObjectByPath(itemToDownload);
                  FsFile fsFile = (FsFile)downloadSyncFsObject;
                  logger.debug("File Path : " + fsFile.getPath());
              
                  FsFileHolder fsFileHolder = new FsFileHolder();
                  fsFileHolder.setName(fsFile.getName());
                  fsFileHolder.setPath(fsFile.getPath());
                  fsFileHolder.setOwner(fsFile.getOwner());
                  fsFileHolder.setCreationDate(fsFile.getCreationDate());
                  fsFileHolder.setModifiedDate(fsFile.getModifiedDate());
                  fsFileHolder.setDescription(fsFile.getDescription());
                  fsFileHolder.setMimeType(fsFile.getMimeType());
                  fsFileHolder.setSize(fsFile.getSize());
                  downloadSyncFile = fsFile;
                  logger.debug("downloadFile..............."+downloadSyncFile);
              
                  dataCarrierResponse = new FsSocketPacketCarrier();
                  dataCarrierResponse.setOperation(USER_NAME_SET);
                  dataCarrierResponse.setData1((fsFileHolder).getData());
                  dataCarrierResponse.setData2(commonUtil.generateMD5Sum(fsFile.getInputStream()));
              
                  oos4SyncDownload.writeObject(dataCarrierResponse);
                  oos4SyncDownload.flush();
                  oos4SyncDownload.reset();
                }
                catch (Exception e) {
                  logger.error(serverUtil.getStackTrace(e));
                }
                logger.debug("msg 4 client frm server for username sent ..........................");
                break;
              case SOCKET_START :
                logger.debug("Inside download socket starts ..........................");
                downloadSyncFileInputStream = downloadSyncFile.getInputStream();
                
                dataCarrierResponse = new FsSocketPacketCarrier();
                dataCarrierResponse.setOperation(SOCKET_STARTED);
                oos4SyncDownload.writeObject(dataCarrierResponse);
                oos4SyncDownload.flush();
                oos4SyncDownload.reset();
                logger.debug("download socket is ready ..........................");
                break;
              case SOCKET_DATA_SENT :
                try {
                  if (downloadSyncFileInputStream!=null) {
                    byteRead = downloadSyncFileInputStream.read(buffer);
                    dataCarrierResponse = new FsSocketPacketCarrier();
                    if (byteRead != -1) {
                      dataCarrierResponse.setOperation(SOCKET_DATA_RECEIVED);
                      dataCarrierResponse.setData(buffer);
                      dataCarrierResponse.setBufferSize(byteRead);
                      total += byteRead;
                      logger.debug("server is sending data to Client...............");
                    } else {
                      dataCarrierResponse.setOperation(SOCKET_END);
                      logger.debug("Server is sending Socket END to Client...............");
                    }
                    oos4SyncDownload.writeObject(dataCarrierResponse);
                    oos4SyncDownload.flush();
                    oos4SyncDownload.reset();
                    logger.debug("server is sending data to Client...............");
                  }
                }
                catch (Exception e) {
                  logger.error(serverUtil.getStackTrace(e));
                  logger.info("Download Failure");
                             
                              
                  if (downloadSyncFileInputStream != null) {
                    downloadSyncFileInputStream.close();
                  }
                  
                  fsExceptionHolder = serverUtil.getFsExceptionHolder(e);
                  fsExceptionHolder.setErrorMessage(e.getMessage());
                  dataCarrierResponse.setFsExceptionHolder4Socket(fsExceptionHolder);
                  dataCarrierResponse.setOperation(SOCKET_ERROR);
                  oos4SyncDownload.writeObject(dataCarrierResponse);
                  oos4SyncDownload.flush();
                  oos4SyncDownload.reset();
                 
                }
                break;
//              case SOCKET_ENDED :
//                dataCarrierResponse = new FsSocketPacketCarrier();
//
//                dataCarrierResponse.setOperation(CLOSE_SOCKET);
//                oos4SyncDownload.writeObject(dataCarrierResponse);
//                oos4SyncDownload.flush();
//                oos4SyncDownload.reset();
//                logger.debug("server is sending to client 4 close socket............");
//                break;
              case SOCKET_CLOSED :
                dontExit = false;
                if (ois4SyncDownload != null) {
                  ois4SyncDownload.close();
                }if(downloadSyncFileInputStream!=null){
                  downloadSyncFileInputStream.close();
//                }if (oos4Download != null) {
//                  //oos4Upload.close();
                }if (socket != null) {
                  //socket.close();
                }
               
                logger.debug("Server Sockets r Closed....");
                break;
              case SOCKET_INTERRUPT:
                if (ois4SyncDownload != null) {
                  ois4SyncDownload.close();
                }
                if(downloadSyncFileInputStream!=null){
                  downloadSyncFileInputStream.close();
                }
              
                if (oos4SyncDownload != null) {
                  //oos4Upload.close();
                   //oos4SyncDownload.close();
                }
              
                if (socket != null) {
                  //socket.close();
                }
                dontExit = false;
                logger.debug("server is interrupted by client............");
                break;
              
            }//end of switch...
          }//end of if dataCarrierRequest != null
        }catch (IOException ioe) {
          ioe.printStackTrace();
          dontExit = false;
        } catch (Exception e) {
          e.printStackTrace();
          dontExit = false;
        } catch (FsException fex) {
          logger.info("Download Failure");
          logger.error(serverUtil.getStackTrace(fex));
          fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
          fsExceptionHolder.setErrorMessage(fex.getMessage());
          dataCarrierResponse.setFsExceptionHolder4Socket(fsExceptionHolder);
          dataCarrierResponse.setOperation(SOCKET_ERROR);
          oos4SyncDownload.writeObject(dataCarrierResponse);
          oos4SyncDownload.flush();
          oos4SyncDownload.reset();
        }
      }
    }catch (IOException e) {
        ;
     }finally{
       try {
         if (ois4SyncDownload != null) {
           ois4SyncDownload.close();
         }if(downloadSyncFileInputStream!=null){
           downloadSyncFileInputStream.close();
         //                }if (oos4Download != null) {
         //                  //oos4Upload.close();
         }if (socket != null) {
           //socket.close();
         }if(oos4SyncDownload!=null){
           oos4SyncDownload.close();
         }
       }
       catch (IOException e) {
         ;
       }
     }
    
  }

    private void receiveSyncDataFromClient(ObjectInputStream ois, ObjectOutputStream oos){
      boolean dontExit = true;
      ObjectInputStream ois4SyncUpload = ois;
      ObjectOutputStream oos4SyncUpload = oos;
      String destFilePathSyncUpload = null;
      FsExceptionHolder fsExceptionHolder;
      String tempFileSyncUpload = null;
      FileOutputStream fos4SyncUpload = null;
      FsFileHolder fsFileHolderSyncUpload = null;
      String clientMD5SumOfCurrentSyncUploadFile = null;
      FsConnection fsConnection = null;
      String syncOperation=null;
      Document document=null;
    
      long fileSize = 0;
      FsSocketPacketCarrier dataCarrierRequest = null;
      FsSocketPacketCarrier dataCarrierResponse = null;

      logger.debug(" Inside receiveSyncDataFromClient..........................");
      try{
        while (dontExit){
          try{
            try {
              dataCarrierRequest = (FsSocketPacketCarrier)ois4SyncUpload.readObject();
            } catch (Exception e) {
              dataCarrierRequest = null;
              }
            if(dataCarrierRequest != null){
              logger.debug("Socket operation code 4 SyncUpload is: " + dataCarrierRequest.getOperation());
              switch (dataCarrierRequest.getOperation()){
                case USER_NAME:
                  logger.debug("msg frm client to server for username ..........................");
                  fsConnection = ((FsUserInfo)connectedUsers.get(new String(dataCarrierRequest.getData()))).getFsConnection();
                  fsFileHolderSyncUpload = (FsFileHolder)dataCarrierRequest.getData2();
                  syncOperation = dataCarrierRequest.getData1().toString();
                  document = commonUtil.getEmptyDocumentObject();
  
                  logger.debug("fsFileHolderUpload.getMimeType().........." + fsFileHolderSyncUpload.getMimeType());
  
                  dataCarrierResponse = new FsSocketPacketCarrier();
                  dataCarrierResponse.setOperation(USER_NAME_SET);
                  oos4SyncUpload.writeObject(dataCarrierResponse);
                  oos4SyncUpload.flush();
                  oos4SyncUpload.reset();
                  logger.debug("msg 4 client frm server for username sent ..........................");
                  break;
                case SOCKET_START:
                  logger.debug("request r comming frm client dataCarrierRequest.getData())).trim()...." + dataCarrierRequest.getData());
                  clientMD5SumOfCurrentSyncUploadFile = dataCarrierRequest.getData2().toString();
                  logger.debug("clientMD5SumOfCurrentUploadFile  : " + clientMD5SumOfCurrentSyncUploadFile);
                  tempFileSyncUpload = "temp" + (new Random()).nextInt() + ".fs";
                  fileSize = dataCarrierRequest.getTotalDataSize();
                  destFilePathSyncUpload = dataCarrierRequest.getAbsolutePath();
  
                  try {
                    FsObject fsObject = fsConnection.findFsObjectByPath(destFilePathSyncUpload);
                    
                    if(fsObject != null) {
                    Object obj[] = { destFilePathSyncUpload };
                    fsConnection.deleteItems(obj);
                    }
                    File fileTemp = new File(tempFileSyncUpload);
                    if (fileTemp.exists()) {
                    fileTemp.delete();
                    }
                    fileTemp.createNewFile();
                    FileInputStream fis = new FileInputStream(new File(tempFileSyncUpload));
                    fsConnection.createFile(destFilePathSyncUpload, fis, fsFileHolderSyncUpload.getMimeType());
                    fsConnection.deleteItem(destFilePathSyncUpload);
                    fos4SyncUpload = new FileOutputStream(fileTemp);
                 
                 } catch (Exception ex1) {
                    fsExceptionHolder = serverUtil.getFsExceptionHolder(ex1);
                    fsExceptionHolder.setErrorMessage(ex1.getMessage());
                    logger.error(serverUtil.getStackTrace(ex1));
  
                    if (fos4SyncUpload != null) {
                      fos4SyncUpload.close();
                      fos4SyncUpload = null;
                    }
                    if (tempFileSyncUpload != null) {
                      new File(tempFileSyncUpload).delete();
                    }
  
                    destFilePathSyncUpload = "";
  
                  } catch (FsException fex) {
                    fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
                    fsExceptionHolder.setErrorMessage(fex.getMessage());
                    logger.error(serverUtil.getStackTrace(fex));
  
                    if (fos4SyncUpload != null) {
                      fos4SyncUpload.close();
                      fos4SyncUpload = null;
                    }
                    if (tempFileSyncUpload != null) {
                      new File(tempFileSyncUpload).delete();
                    }
  
                    destFilePathSyncUpload = "";
  
                  }
  
                  dataCarrierResponse = new FsSocketPacketCarrier();
                  dataCarrierResponse.setOperation(SOCKET_STARTED);
                  oos4SyncUpload.writeObject(dataCarrierResponse);
                  oos4SyncUpload.flush();
                  oos4SyncUpload.reset();
                  logger.debug("response to client 4 send data.......................");
                  break;
                case SOCKET_DATA_SENT:
                  if (fos4SyncUpload != null) {
                    fos4SyncUpload.write(dataCarrierRequest.getData(), 0, (int)dataCarrierRequest.getBufferSize());
                    fos4SyncUpload.flush();
                  }
                  logger.debug("Resquested Upload File is Updating.......................");
                  dataCarrierResponse = new FsSocketPacketCarrier();
                  dataCarrierResponse.setOperation(SOCKET_DATA_RECEIVED);
                  oos4SyncUpload.writeObject(dataCarrierResponse);
                  oos4SyncUpload.flush();
                  oos4SyncUpload.reset();
                 // total += dataCarrierRequest.getBufferSize();
                  break;
                case SOCKET_END:
                  dataCarrierResponse = new FsSocketPacketCarrier();
                  try {
                    if (fos4SyncUpload!=null) {
                      fos4SyncUpload.close();
                    }
                    String serverMD5SumOfCurrentSyncUploadFile = commonUtil.generateMD5Sum(new FileInputStream(tempFileSyncUpload));
                    logger.debug("serverMD5SumOfCurrentUploadFile " + serverMD5SumOfCurrentSyncUploadFile);
  
                    logger.debug("clientMD5SumOfCurrentUploadFile " + clientMD5SumOfCurrentSyncUploadFile);
                    if (serverMD5SumOfCurrentSyncUploadFile.equals(clientMD5SumOfCurrentSyncUploadFile)) {
                      logger.debug("File Transfer is successful");
  
                      FileInputStream fis = new FileInputStream(new File(tempFileSyncUpload));
                      FsFile fsFile = fsConnection.createFile(destFilePathSyncUpload, fis, fsFileHolderSyncUpload.getMimeType());
                      fis.close();
                      serverUtil.addDocumentElement(document, fsFile, syncOperation);
                      dataCarrierResponse.setOperation(SOCKET_ENDED);
                      dataCarrierResponse.setData2(document);
                      logger.debug("File Created");
                    } else {
                      logger.debug("File Transfer is not successful due to Curruption");
  
//                      fsResponse.setOperation(FsRemoteOperationConstants.SYNC_UPLOAD);
//                      fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
//                      fsResponse.setResponseCode(FILE_CURRUPTED);
//                      fsMessageSender.send(fsResponse);
                        dataCarrierResponse.setOperation(SOCKET_FILE_CURRUPTED);
                    }
                    new File(tempFileSyncUpload).delete();
                    serverMD5SumOfCurrentSyncUploadFile = null;
                    clientMD5SumOfCurrentSyncUploadFile = null;
  
                  } catch (CmsdkException cmsdkException) {
;
//                    fsResponse.setResponseCode(FAILED);
//                    fsResponse.setData(cmsdkException.getStackTraceString());
//                    cmsdkException.getErrorCode();
                      dataCarrierResponse.setData(cmsdkException.getStackTraceString().getBytes());
                      dataCarrierResponse.setOperation(SOCKET_FAILED);
                      cmsdkException.getErrorCode();
                  } catch (Exception ex) {
;
//                    fsResponse.setResponseCode(FAILED);
//                    fsResponse.setData(ex.getMessage());
//                    logger.error(serverUtil.getStackTrace(ex));
                      dataCarrierResponse.setData(ex.getMessage().getBytes());
                      dataCarrierResponse.setOperation(SOCKET_FAILED);
                  }catch (FsException fsException) {
                    fsExceptionHolder = serverUtil.getFsExceptionHolder(fsException);
                    fsExceptionHolder.setErrorMessage(fsException.getMessage());
                    dataCarrierResponse.setFsExceptionHolder4Socket(fsExceptionHolder);
                    dataCarrierResponse.setOperation(SOCKET_ERROR);

                    if (fos4SyncUpload != null) {
                      fos4SyncUpload.close();
                      fos4SyncUpload = null;
                    }
                    if (tempFileSyncUpload != null) {
                      new File(tempFileSyncUpload).delete();
                    }

                    //isFile4Overwrite=false;
                    destFilePathSyncUpload = "";
                    //   fsClientHandler.removeUploadListener(this);

                  } 
                  oos4SyncUpload.writeObject(dataCarrierResponse);
                  oos4SyncUpload.flush();
                  oos4SyncUpload.reset();
                  break;
                case CLOSE_SOCKET:
                  dontExit = false;
                  if (ois4SyncUpload != null) {
                    ois4SyncUpload.close();
                  }
  
                  if (oos4SyncUpload != null) {
                    //oos4Upload.close();
                  }
  
                  if (socket != null) {
                   // socket.close();
                  }
                  
                  logger.debug("Server Sockets r Closed....");
                  break;
                case SOCKET_INTERRUPT:
                  dontExit = false;
  
                  if (fos4SyncUpload != null) {
                    fos4SyncUpload.flush();
                    fos4SyncUpload.close();
                  }
                  logger.debug("Upload cancelled ");
                  new File(tempFileSyncUpload).delete();
                  destFilePathSyncUpload = "";
                  break;
                
                
                
              }//end of switch
            }//closing if dataCarrierRequest != null
          }catch (IOException ioe) {
            ioe.printStackTrace();
            dontExit = false;
          } catch (Exception e) {
            e.printStackTrace();
            dontExit = false;
          }
        }//closing  while loop
      }catch (IOException e) {
        ;
      }finally{
        try {
          if (ois4SyncUpload != null) {
            ois4SyncUpload.close();
          }
          
          if (oos4SyncUpload != null) {
            //oos4Upload.close();
             oos4SyncUpload.close();
          }if(fos4SyncUpload!=null){
            fos4SyncUpload.flush();
            fos4SyncUpload.close();
          }
          
          if (socket != null) {
           // socket.close();
          }
        }
        catch (IOException e) {
          ;
        }
      }
    }
    
    private void receiveDataFromClient(ObjectInputStream ois, ObjectOutputStream oos) {
      boolean dontExit = true;
      boolean isFile4Overwrite = false;
      ObjectInputStream ois4Upload = ois;
      ObjectOutputStream oos4Upload = oos;
      String destFilePathUpload = null;
      FsExceptionHolder fsExceptionHolder;
      String tempFileUpload = null;
      FileOutputStream fosUpload = null;
      FsFileHolder fsFileHolderUpload = null;
      String clientMD5SumOfCurrentUploadFile = null;
      FsConnection fsConnection = null;
      

      long fileSize = 0;
      long total = 0;
      FsSocketPacketCarrier dataCarrierRequest = null;
      FsSocketPacketCarrier dataCarrierResponse = null;

      logger.debug(" Inside receiveDataFromClient()..........................");

      try {
        //        ois = new ObjectInputStream(socket.getInputStream());
        //        oos=new ObjectOutputStream(socket.getOutputStream());
        
        while (dontExit) {
          try {
            try {
              dataCarrierRequest = (FsSocketPacketCarrier)ois4Upload.readObject();
            } catch (Exception e) {
              dataCarrierRequest = null;
            }
            if (dataCarrierRequest != null) {
              logger.debug("Socket operation code is: " + dataCarrierRequest.getOperation());
              //              if (dataCarrierRequest.getOperation()==SOCKET_RECEIVE) {
              switch (dataCarrierRequest.getOperation()) {
                case USER_NAME :
                  logger.debug("msg frm client to server for username ..........................");
                  fsConnection =
                    ((FsUserInfo)connectedUsers.get(new String(dataCarrierRequest.getData()))).getFsConnection();
                  fsFileHolderUpload = (FsFileHolder)dataCarrierRequest.getData2();

                  logger.debug("fsFileHolderUpload.getMimeType().........." + fsFileHolderUpload.getMimeType());

                  dataCarrierResponse = new FsSocketPacketCarrier();
                  dataCarrierResponse.setOperation(USER_NAME_SET);
                  oos4Upload.writeObject(dataCarrierResponse);
                  oos4Upload.flush();
                  oos4Upload.reset();
                  logger.debug("msg 4 client frm server for username sent ..........................");
                  break;
                case SOCKET_START :
                  logger
                  .debug("request r comming frm client dataCarrierRequest.getData())).trim()...." + dataCarrierRequest
                               .getData());
                  clientMD5SumOfCurrentUploadFile = dataCarrierRequest.getData2().toString();
                  logger.debug("clientMD5SumOfCurrentUploadFile  : " + clientMD5SumOfCurrentUploadFile);
                  tempFileUpload = "temp" + (new Random()).nextInt() + ".fs";
                  fileSize = dataCarrierRequest.getTotalDataSize();
                  destFilePathUpload = dataCarrierRequest.getAbsolutePath();

                  try {
                    FsObject fsObject = fsConnection.findFsObjectByPath(destFilePathUpload);

                    if (fsObject == null) {
                      logger.debug("inside fsobject absFilePathUpload......" + destFilePathUpload);
                      File fileTemp = new File(tempFileUpload);
                      if (fileTemp.exists()) {
                        fileTemp.delete();
                      }
                      fileTemp.createNewFile();

                      //Checking whether the user has rights to create file or not.
                      FileInputStream fis = new FileInputStream(new File(tempFileUpload));

                      FsFile currentFile =
                        fsConnection.createFile(destFilePathUpload, fis, fsFileHolderUpload.getMimeType());
                      fis.close();
                      if (currentFile == null) {
                        logger.debug("File creation failed");
                        fileTemp.delete();
                      } else {
                        //user can create file. 

                        //Check has been done, so deleting the the created file and same is created once 
                        //upload is successfull. Refere CLOSE_FILE
                        Object obj[] = { destFilePathUpload };
                        fsConnection.deleteItems(obj);

                        fosUpload = new FileOutputStream(fileTemp);
                      }
                    } else {
                      isFile4Overwrite = true;
                      File fileTemp = new File(tempFileUpload);
                      if (fileTemp.exists()) {
                        fileTemp.delete();
                      }
                      fileTemp.createNewFile();

                      fosUpload = new FileOutputStream(fileTemp);
                    }

                  } catch (Exception ex1) {
                    fsExceptionHolder = serverUtil.getFsExceptionHolder(ex1);
                    fsExceptionHolder.setErrorMessage(ex1.getMessage());
                    logger.error(serverUtil.getStackTrace(ex1));

                    if (fosUpload != null) {
                      fosUpload.close();
                      fosUpload = null;
                    }
                    if (tempFileUpload != null) {
                      new File(tempFileUpload).delete();
                    }

                    destFilePathUpload = "";

                  } catch (FsException fex) {
                    fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
                    fsExceptionHolder.setErrorMessage(fex.getMessage());
                    logger.error(serverUtil.getStackTrace(fex));

                    if (fosUpload != null) {
                      fosUpload.close();
                      fosUpload = null;
                    }
                    if (tempFileUpload != null) {
                      new File(tempFileUpload).delete();
                    }

                    destFilePathUpload = "";

                  }

                  dataCarrierResponse = new FsSocketPacketCarrier();
                  dataCarrierResponse.setOperation(SOCKET_STARTED);
                  oos4Upload.writeObject(dataCarrierResponse);
                  oos4Upload.flush();
                  oos4Upload.reset();
                  logger.debug("response to client 4 send data.......................");
                  break;
                case SOCKET_DATA_SENT :
                  if (fosUpload != null) {
                    fosUpload.write(dataCarrierRequest.getData(), 0, (int)dataCarrierRequest.getBufferSize());
                    fosUpload.flush();
                  }
                  logger.debug("Resquested Upload File is Updating.......................");
                  dataCarrierResponse = new FsSocketPacketCarrier();
                  dataCarrierResponse.setOperation(SOCKET_DATA_RECEIVED);
                  oos4Upload.writeObject(dataCarrierResponse);
                  oos4Upload.flush();
                  oos4Upload.reset();
                  total += dataCarrierRequest.getBufferSize();
                  break;
                case SOCKET_INTERRUPT :
                  dontExit = false;

                  if (fosUpload != null) {
                    fosUpload.flush();
                    fosUpload.close();
                  }
                  logger.debug("Upload cancelled ");
                  new File(tempFileUpload).delete();
                  destFilePathUpload = "";
                  break;
                case SOCKET_END :
                  try {
                    logger.debug("request r comming from client 4 closeing the socket....... ");

                    //                    
                    String serverMD5SumOfCurrentUploadFile =
                      commonUtil.generateMD5Sum(new FileInputStream(tempFileUpload));
                    //                   
                    logger.debug("serverMD5SumOfCurrentUploadFile " + serverMD5SumOfCurrentUploadFile);
                    //                   
                    logger.debug("clientMD5SumOfCurrentUploadFile " + clientMD5SumOfCurrentUploadFile);
                    //                   
                    if (serverMD5SumOfCurrentUploadFile.equals(clientMD5SumOfCurrentUploadFile)) {
                      logger.debug("File Transfer is successful");

                      //                     fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
                      //                     fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
                      //                     fsResponse.setResponseCode(PROGRESS_BUILDING);
                      //                     fsMessageSender.send(fsResponse);

                      if (isFile4Overwrite) {
                        Object obj[] = { destFilePathUpload };
                        fsConnection.deleteItems(obj);
                      }

                      FileInputStream fis = new FileInputStream(new File(tempFileUpload));
                      fsConnection.createFile(destFilePathUpload, fis, fsFileHolderUpload.getMimeType());
                      fis.close();

                      destFilePathUpload = "";
                      new File(tempFileUpload).delete();


                      //                   fsResponse.setResponseCode(FILE_CLOSED);
                      //                   logger.debug("File Created");
                      dataCarrierResponse = new FsSocketPacketCarrier();
                      dataCarrierResponse.setOperation(SOCKET_ENDED);
                    } else {
                      logger.debug("File Transfer is not successful due to Curruption");
                      //                     
                      //                     //send client a message stating file has been currupted during file transfer. 
                      dataCarrierResponse = new FsSocketPacketCarrier();
                      dataCarrierResponse.setOperation(SOCKET_FILE_CURRUPTED);
                    }
                    isFile4Overwrite = false;


                    //                  try {
                    //                    if (isFile4Overwrite){
                    //                      Object obj[] = { absFilePathUpload };
                    //                      fsConnection.deleteItems(obj);                  
                    //                    }
                    //                    
                    //                    FileInputStream fis = new FileInputStream(new File(tempFileUpload));
                    //                    fsConnection.createFile(absFilePathUpload, fis, fsFileHolderUpload.getMimeType());
                    //                   
                    //                    absFilePathUpload="";
                    //                    new File(tempFileUpload).delete();
                    //                   
                    //                    dataCarrierResponse= new FsSocketPacketCarrier();
                    //                    dataCarrierResponse.setOperation(SOCKET_ENDED);  
                    //                  }
                    //                  catch (Exception e) {
                    //                    logger.error(serverUtil.getStackTrace(e));
                    //                  }

                    serverMD5SumOfCurrentUploadFile = null;
                    clientMD5SumOfCurrentUploadFile = null;

                  } catch (FsException fsException) {
                    fsExceptionHolder = serverUtil.getFsExceptionHolder(fsException);
                    fsExceptionHolder.setErrorMessage(fsException.getMessage());
                    dataCarrierResponse.setFsExceptionHolder4Socket(fsExceptionHolder);
                    dataCarrierResponse.setOperation(SOCKET_ERROR);

                    if (fosUpload != null) {
                      fosUpload.close();
                      fosUpload = null;
                    }
                    if (tempFileUpload != null) {
                      new File(tempFileUpload).delete();
                    }

                    //isFile4Overwrite=false;
                    destFilePathUpload = "";
                    //   fsClientHandler.removeUploadListener(this);

                  } catch (Exception ex) {
                    fsExceptionHolder = serverUtil.getFsExceptionHolder(new Exception("Upload Failure"));
                    dataCarrierResponse.setFsExceptionHolder4Socket(fsExceptionHolder);
                    dataCarrierResponse.setOperation(SOCKET_ERROR);
                    logger.error(serverUtil.getStackTrace(ex));

                    if (fosUpload != null) {
                      fosUpload.close();
                      fosUpload = null;
                    }
                    if (tempFileUpload != null) {
                      new File(tempFileUpload).delete();
                    }

                    //                 isFile4Overwrite=false;
                    destFilePathUpload = "";
                    //                 fsClientHandler.removeUploadListener(this);
                  }
                  if (fosUpload != null) {
                    fosUpload.flush();
                    fosUpload.close();
                  }


                  oos4Upload.writeObject(dataCarrierResponse);
                  oos4Upload.flush();
                  oos4Upload.reset();
                  logger.debug("response to client 4 closing to client socket....... ");
                  break;
                case CLOSE_SOCKET :
                  dontExit = false;
                  if (ois4Upload != null) {
                    ois4Upload.close();
                  }

                  if (oos4Upload != null) {
                    //oos4Upload.close();
                  }

                  if (socket != null) {
                    //socket.close();
                  }
//                  if(fosUpload != null){
//                    fosUpload.flush();
//                    fosUpload.close();
//                  }
                  
                  
                  logger.debug("Server Sockets r Closed....");
                  break;
              }
            }

          } catch (IOException ioe) {
            ioe.printStackTrace();
            dontExit = false;
          } catch (Exception e) {
            e.printStackTrace();
            dontExit = false;
          }
        }

      } catch (IOException e) {
        ;
      }finally{
        try {
          if (ois4Upload != null) {
            ois4Upload.close();
          }
  
          if (oos4Upload != null) {
            oos4Upload.close();
          }
  
          if (socket != null) {
            //socket.close();
          }
//          if(fosUpload != null){
//            fosUpload.flush();
//            fosUpload.close();
//           }
        }
        catch (IOException e) {
          ;
        }
      }
    }


    private void sendingDataToClient(ObjectInputStream ois, ObjectOutputStream oos) {
      boolean dontExit = true;
      ObjectInputStream ois4Download = ois;
      ObjectOutputStream oos4Download = oos;
      FsExceptionHolder fsExceptionHolder;
      FsConnection fsConnection = null;
      FsObject downloadFsObject;
      FsFile downloadFile = null;
      InputStream downloadFileInputStream = null;
      byte[] buffer = new byte[commonUtil.JXTA_SOCKET_BUFFER_SIZE];

      FsSocketPacketCarrier dataCarrierRequest = null;
      FsSocketPacketCarrier dataCarrierResponse = null;

      logger.debug(" Inside sendingDataForClient()..........................");
      try {
        while (dontExit) {
          long byteRead = -1;
          long total = 0;
          try {
            try {
              dataCarrierRequest = (FsSocketPacketCarrier)ois4Download.readObject();
            } catch (Exception e) {
              dataCarrierRequest = null;
            }
            if (dataCarrierRequest != null) {
              logger.debug("Socket operation code is: " + dataCarrierRequest.getOperation());
              switch (dataCarrierRequest.getOperation()) {
                case USER_NAME :
                  logger.debug("msg frm client to server for username ..........................");
                  try {
                    fsConnection =
                      ((FsUserInfo)connectedUsers.get(new String(dataCarrierRequest.getData()))).getFsConnection();
  
                    String itemToDownload = dataCarrierRequest.getData2().toString();
                    logger.debug("itemToDownload : " + itemToDownload);
                    downloadFsObject = fsConnection.findFsObjectByPath(itemToDownload);
                    FsFile fsFile = (FsFile)downloadFsObject;
                    logger.debug("File Path : " + fsFile.getPath());
  
                    FsFileHolder fsFileHolder = new FsFileHolder();
                    fsFileHolder.setName(fsFile.getName());
                    fsFileHolder.setPath(fsFile.getPath());
                    fsFileHolder.setOwner(fsFile.getOwner());
                    fsFileHolder.setCreationDate(fsFile.getCreationDate());
                    fsFileHolder.setModifiedDate(fsFile.getModifiedDate());
                    fsFileHolder.setDescription(fsFile.getDescription());
                    fsFileHolder.setMimeType(fsFile.getMimeType());
                    fsFileHolder.setSize(fsFile.getSize());
                    downloadFile = fsFile;
                    logger.debug("downloadFile..............."+downloadFile);
  
                    dataCarrierResponse = new FsSocketPacketCarrier();
                    dataCarrierResponse.setOperation(USER_NAME_SET);
                    dataCarrierResponse.setData1((fsFileHolder).getData());
                    dataCarrierResponse.setData2(commonUtil.generateMD5Sum(fsFile.getInputStream()));
  
                    oos4Download.writeObject(dataCarrierResponse);
                    oos4Download.flush();
                    oos4Download.reset();
                  }
                  catch (Exception e) {
                    logger.error(serverUtil.getStackTrace(e));
                  }
                  logger.debug("msg 4 client frm server for username sent ..........................");
                  break;
                case SOCKET_START :
                logger.debug("Inside download socket starts ..........................");
                  downloadFileInputStream = downloadFile.getInputStream();

                  
                  dataCarrierResponse = new FsSocketPacketCarrier();
                  dataCarrierResponse.setOperation(SOCKET_STARTED);
                  oos4Download.writeObject(dataCarrierResponse);
                  oos4Download.flush();
                  oos4Download.reset();
                  logger.debug("download socket is ready ..........................");
                  break;
                case SOCKET_DATA_SENT :
                  try {
                    if (downloadFileInputStream!=null) {
                      byteRead = downloadFileInputStream.read(buffer);
                      dataCarrierResponse = new FsSocketPacketCarrier();
                      if (byteRead != -1) {
                        dataCarrierResponse.setOperation(SOCKET_DATA_RECEIVED);
                        dataCarrierResponse.setData(buffer);
                        dataCarrierResponse.setBufferSize(byteRead);
                        total += byteRead;
                        logger.debug("server is sending data to Client...............");
                      } else {
                        dataCarrierResponse.setOperation(SOCKET_END);
                        logger.debug("Server is sending Socket END to Client...............");
                      }
                      oos4Download.writeObject(dataCarrierResponse);
                      oos4Download.flush();
                      oos4Download.reset();
                      logger.debug("server is sending data to Client...............");
                    }
                  }
                  catch (Exception e) {
                    logger.error(serverUtil.getStackTrace(e));
                    logger.info("Download Failure");
                               
                                
                    if (downloadFileInputStream != null) {
                      downloadFileInputStream.close();
                    }
                    
                    fsExceptionHolder = serverUtil.getFsExceptionHolder(e);
                    fsExceptionHolder.setErrorMessage(e.getMessage());
                    dataCarrierResponse.setFsExceptionHolder4Socket(fsExceptionHolder);
                    dataCarrierResponse.setOperation(SOCKET_ERROR);
                    oos4Download.writeObject(dataCarrierResponse);
                    oos4Download.flush();
                    oos4Download.reset();
                   
                  }
                  break;
                case SOCKET_INTERRUPT:
                  if (ois4Download != null) {
                    ois4Download.close();
                  }
                  if(downloadFileInputStream!=null){
                    downloadFileInputStream.close();
                  }
  
                  if (oos4Download != null) {
                    //oos4Upload.close();
                     //oos4Download.close();
                  }
  
                  if (socket != null) {
                    //socket.close();
                  }
                  dontExit = false;
                  logger.debug("server is interrupted by client............");
                  break;
                case SOCKET_ENDED :
                  
                  dataCarrierResponse = new FsSocketPacketCarrier();

                  dataCarrierResponse.setOperation(CLOSE_SOCKET);
                  oos4Download.writeObject(dataCarrierResponse);
                  oos4Download.flush();
                  oos4Download.reset();
                  
                  dontExit = false;
                  if(downloadFileInputStream!=null){
                    downloadFileInputStream.close();
                  }if (ois4Download != null) {
                    ois4Download.close();
                  }
                  logger.debug("Server Sockets r Closed....");
                  break;
//                case SOCKET_FILE_CLOSED:
//                  dataCarrierResponse.setOperation(CLOSE_SOCKET);
//                  oos4Download.writeObject(dataCarrierResponse);
//                  oos4Download.flush();
//                  oos4Download.reset();
                 //break;
//                case SOCKET_CLOSED :
//                 
//                  if(downloadFileInputStream!=null){
//                    downloadFileInputStream.close();
//                  }if (oos4Download != null) {
//                    //oos4Upload.close();
//                     //oos4Download.flush();
//                  }if (socket != null) {
//                    //socket.close();
//                  }
//                 
//                  
//                  break;
              }
            }
          } catch (IOException ioe) {
            ioe.printStackTrace();
            dontExit = false;
          } catch (Exception e) {
            e.printStackTrace();
            dontExit = false;
          } catch (FsException fex) {
            logger.info("Download Failure");
            logger.error(serverUtil.getStackTrace(fex));
            fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
            fsExceptionHolder.setErrorMessage(fex.getMessage());
            dataCarrierResponse.setFsExceptionHolder4Socket(fsExceptionHolder);
            dataCarrierResponse.setOperation(SOCKET_ERROR);
            oos4Download.writeObject(dataCarrierResponse);
            oos4Download.flush();
            oos4Download.reset();
          }
        }
      } catch (IOException e) {
        ;
      }finally{
        try {
          if (ois4Download != null) {
            ois4Download.close();
          }if(downloadFileInputStream!=null){
            downloadFileInputStream.close();
          }if (oos4Download != null) {
            //oos4Upload.close();
             oos4Download.close();
          }if (socket != null) {
            //socket.close();
          }
        }
        catch (IOException e) {
          ;
        }
      }
    }


  }

}
