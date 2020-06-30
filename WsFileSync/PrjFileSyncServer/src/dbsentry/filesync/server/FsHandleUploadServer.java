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
 * $Id: FsHandleUploadServer.java,v 1.40 2006/08/01 10:14:26 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.constants.FsSocketConstants;
import dbsentry.filesync.common.listeners.FsUploadListener;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsObject;

import java.beans.PropertyChangeEvent;

import java.util.Date;

import org.apache.log4j.Logger;


/**
 *	To handle upload opeartion at server side.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation:    7-05-2005
 * 	Last Modfied by :    Saurabh Gupta
 * 	Last Modfied Date:   24-07-2006
 */
public class FsHandleUploadServer implements FsUploadListener, FsSocketConstants {
  private String superRequestCode;

  private Logger logger;

  private int overWriteValueUpload;

  private FsMessageSender fsMessageSender;

  private FsConnection fsConnection;

  private ServerUtil serverUtil;

  private CommonUtil commonUtil;

  private long uploadStartTime;

  private String absFilePathUpload;

  private String uploadBaseFolderPath;

  //private FileOutputStream fosUpload;

  private FsFileHolder fsFileHolderUpload;

  // private FsFileHolder fsFileHolder;

  // private String tempFileUpload;

  private FsClientHandler fsClientHandler;

  //private boolean isFile4Overwrite=false;

  // private String clientMD5SumOfCurrentUploadFile=null;


  public FsHandleUploadServer(String superRequestCode, FsClientHandler fsClientHandler) {
    this.logger = Logger.getLogger("ServerLogger");
    this.superRequestCode = superRequestCode;
    this.fsClientHandler = fsClientHandler;
    this.fsMessageSender = fsClientHandler.getFsMessageSender();
    this.fsConnection = fsClientHandler.getFsConnection();
    //this.tempFileUpload = "temp" + (new Random()).nextInt() + ".fs";
    this.serverUtil = new ServerUtil(logger);
    this.commonUtil = new CommonUtil(logger);

  }

  /**
   * To handle the PropertyChangeEvent.
   * @param evt PropertyChangeEvent object.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    logger.debug("Property Name as super Request Code :" + evt.getPropertyName());
    logger.debug("super Request Code :" + superRequestCode);
    if (evt.getPropertyName().equals(superRequestCode)) {
      FsRequest fsRequest = (FsRequest)evt.getNewValue();
      handleUpload(fsRequest);
    }

    /*if (evt.getSource().equals(superRequestCode)) {
      if (evt.getPropertyName().equals("fsRequest")) {
        FsRequest fsRequest = (FsRequest)evt.getNewValue();
        handleUpload(fsRequest);
      }
      if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);
        //handle exception
      }
    }*/
  }

  /**
   * To handle the upload related requests.
   * @param fsRequest FsRequest object containing upload related request.
   */
  public void handleUpload(FsRequest fsRequest) {
    FsResponse fsResponse;
    FsExceptionHolder fsExceptionHolder;
    long uploadEndTime;
    try {
      int requestCode = fsRequest.getRequestCode();
      logger.debug("Handle Upload : Request Code " + requestCode);
      switch (requestCode) {

        case START :
          uploadBaseFolderPath = (String)fsRequest.getData();
          uploadStartTime = new Date().getTime();
          absFilePathUpload = "";
          this.overWriteValueUpload = 0;
          logger.debug("Upload started : " + new Date(uploadStartTime));
          fsResponse = new FsResponse();
          fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setResponseCode(STARTED);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;

        case COMPLETE :
          uploadEndTime = new Date().getTime();
          logger.debug("Upload complete : " + new Date(uploadEndTime));
          logger.debug("Total Time (sec): " + (uploadEndTime - uploadStartTime) / 1000);
          logger.debug("no items r left for uploading....");

          fsResponse = new FsResponse();
          fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setResponseCode(COMPLETED);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          fsClientHandler.removeUploadListener(this);
          break;
        case CANCEL :
          //        fosUpload.close();
          //        fosUpload=null;
          logger.debug("Upload cancelled ");

          //        new File(tempFileUpload).delete();
          // isFile4Overwrite=false;
          absFilePathUpload = "";

          fsResponse = new FsResponse();
          fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setResponseCode(CANCLED);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);

          fsClientHandler.removeUploadListener(this);
          break;
        case FAILED :
          logger.debug("Upload Failed");
          //        fosUpload.close();
          //        fosUpload=null;
          //        new File(tempFileUpload).delete();

          // isFile4Overwrite=false;
          absFilePathUpload = "";
          fsClientHandler.removeUploadListener(this);
          break;
        case FILE_CURRUPTED :
          fsResponse = new FsResponse();
          fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setResponseCode(FILE_CURRUPTED);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case CREATE_FOLDER :
          logger.debug("Create folder");
          Object folders[] = fsRequest.getDatas();
          fsResponse = new FsResponse();
          String folderPathRelative = serverUtil.getRelativePath(fsConnection, folders);
          String absFolderPath = fsConnection.getAbsolutePath(folderPathRelative, uploadBaseFolderPath);
          logger.debug("absFolderPath : " + absFolderPath);

          try {
            FsObject fsObject = fsConnection.findFsObjectByPath(absFolderPath);
            if (fsObject == null) {
              fsConnection.createFolder(uploadBaseFolderPath, folderPathRelative);
              logger.debug("Folder created successfully");
              fsResponse.setResponseCode(FOLDER_CREATED);
            } else {
              if (this.overWriteValueUpload == OVERWRITE_YES_TO_ALL) {
                fsResponse.setResponseCode(FOLDER_CREATED);
              } else {
                fsResponse.setData(fsObject.getName());
                String temp[] = new String[1];
                temp[0] = fsObject.getPath();
                fsResponse.setData1(fsConnection.calculateFolderDocSize(temp) + "");
                fsResponse.setData2(fsObject.getModifiedDate());
                fsResponse.setResponseCode(OVERWRITE_FOLDER);
                logger.debug("Ask if it can be overwritten");
              }
            }
          } catch (FsException fex) {
            fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
            fsExceptionHolder.setErrorMessage(fex.getMessage());
            fsResponse.setFsExceptionHolder(fsExceptionHolder);
            fsResponse.setResponseCode(ERROR_MESSAGE);
            logger.error(serverUtil.getStackTrace(fex));

            //          if (fosUpload!=null) {
            //            fosUpload.close();
            //            fosUpload=null;
            //          }
            //          if (tempFileUpload!=null) {
            //            new File(tempFileUpload).delete();
            //          }

            // isFile4Overwrite=false;
            absFilePathUpload = "";
            fsClientHandler.removeUploadListener(this);
          } catch (Exception ex1) {
            fsExceptionHolder = serverUtil.getFsExceptionHolder(ex1);
            fsExceptionHolder.setErrorMessage(ex1.getMessage());
            fsResponse.setFsExceptionHolder(fsExceptionHolder);
            fsResponse.setResponseCode(ERROR_MESSAGE);
            logger.error(serverUtil.getStackTrace(ex1));

            //          if (fosUpload!=null) {
            //            fosUpload.close();
            //            fosUpload=null;
            //          }
            //          if (tempFileUpload!=null) {
            //            new File(tempFileUpload).delete();
            //          }

            //isFile4Overwrite=false;
            absFilePathUpload = "";
            fsClientHandler.removeUploadListener(this);
          }
          fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case CREATE_SOCKET_4_UPLOAD_FILE :
          fsFileHolderUpload = (FsFileHolder)fsRequest.getData();
          //clientMD5SumOfCurrentUploadFile=(String)fsRequest.getData1();
          logger.debug("Create file");
          folders = fsRequest.getDatas();
          fsResponse = new FsResponse();
          String filePathRelative = serverUtil.getRelativePath(fsConnection, folders);
          this.absFilePathUpload = fsConnection.getAbsolutePath(filePathRelative, uploadBaseFolderPath);
          logger.debug("absFilePath : " + this.absFilePathUpload);

          try {
            FsObject fsObject = fsConnection.findFsObjectByPath(this.absFilePathUpload);
            if (fsObject == null) {
              //            File fileTemp = new File(tempFileUpload);
              //            if (fileTemp.exists()) {
              //              fileTemp.delete();
              //            }
              //            fileTemp.createNewFile();
              //Checking whether the user has rights to create file or not.
              //            FileInputStream fis = new FileInputStream(new File(tempFileUpload));
              //            FsFile currentFile = fsConnection.createFile(absFilePathUpload, fis, fsFileHolderUpload.getMimeType());
              //            fis.close();
              //            if (currentFile == null) {
              //              //User doesn't have any right to create file over here
              //              fsExceptionHolder = new FsExceptionHolder();
              //              fsExceptionHolder.setErrorMessage("File creation failed");
              //              fsResponse.setFsExceptionHolder(fsExceptionHolder);
              //              fsResponse.setResponseCode(FAILED);
              //              logger.debug("File creation failed");
              //              fileTemp.delete();
              //            } else {
              //user can create file. 

              //Check has been done, so deleting the the created file and same is created once 
              //upload is successfull. Refere CLOSE_FILE
              //              Object obj[] = { absFilePathUpload }; 
              //              fsConnection.deleteItems(obj);   

              //              fosUpload = new FileOutputStream(fileTemp);


              fsResponse.setData(absFilePathUpload);
              // fsResponse.setData2(tempFileUpload);
              fsResponse.setData1(SOCKET_RECEIVE);
              fsResponse.setResponseCode(SOCKET_4_UPLOAD_FILE_CREATED);
              //            }
            } else {
              if (this.overWriteValueUpload == OVERWRITE_YES_TO_ALL) {
                // isFile4Overwrite=true;
                //              File fileTemp = new File(tempFileUpload);
                //              if (fileTemp.exists()) {
                //                fileTemp.delete();
                //              }
                //              fileTemp.createNewFile();
                //              
                //              fosUpload = new FileOutputStream(fileTemp);
                fsResponse.setData1(SOCKET_RECEIVE);
                fsResponse.setData(this.absFilePathUpload);
                fsResponse.setResponseCode(SOCKET_4_UPLOAD_FILE_CREATED);
              } else {
                fsResponse.setData(fsObject.getName());
                String temp[] = new String[1];
                temp[0] = fsObject.getPath();
                fsResponse.setData1(fsConnection.calculateFolderDocSize(temp) + "");
                fsResponse.setData2(fsObject.getModifiedDate());
                fsResponse.setResponseCode(OVERWRITE_FILE);
                logger.debug("Ask if it can be overwritten");
              }
            }
          } catch (FsException fex) {
            fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
            fsExceptionHolder.setErrorMessage(fex.getMessage());
            fsResponse.setFsExceptionHolder(fsExceptionHolder);
            fsResponse.setResponseCode(ERROR_MESSAGE);
            logger.error(serverUtil.getStackTrace(fex));

            //          if (fosUpload!=null) {
            //            fosUpload.close();
            //            fosUpload=null;
            //          }
            //          if (tempFileUpload!=null) {
            //            new File(tempFileUpload).delete();
            //          }

            // isFile4Overwrite=false;
            absFilePathUpload = "";
            fsClientHandler.removeUploadListener(this);
          } catch (Exception ex1) {
            fsExceptionHolder = serverUtil.getFsExceptionHolder(ex1);
            fsExceptionHolder.setErrorMessage(ex1.getMessage());
            fsResponse.setFsExceptionHolder(fsExceptionHolder);
            fsResponse.setResponseCode(ERROR_MESSAGE);
            logger.error(serverUtil.getStackTrace(ex1));

            //          if (fosUpload!=null) {
            //            fosUpload.close();
            //            fosUpload=null;
            //          }
            //          if (tempFileUpload!=null) {
            //            new File(tempFileUpload).delete();
            //          }

            // isFile4Overwrite=false;
            absFilePathUpload = "";
            fsClientHandler.removeUploadListener(this);
          }
          fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
          //      case APPEND_TO_FILE:
          //        fsResponse = new FsResponse();
          //        fsFileHolder = (FsFileHolder)fsRequest.getData();
          //        try{
          //          if (fosUpload!=null) {
          //            fosUpload.write(fsFileHolder.getData(),0,(int)fsFileHolder.getSize());
          //          }
          //          fsResponse.setResponseCode(APPENDED_TO_FILE);
          //        }catch(Exception ex){
          //            fsResponse.setResponseCode(ERROR_MESSAGE);
          //            fsExceptionHolder = serverUtil.getFsExceptionHolder(new Exception("Upload Failure"));
          //            fsResponse.setFsExceptionHolder(fsExceptionHolder);
          //            logger.error(serverUtil.getStackTrace(ex));
          //            
          //            if (fosUpload!=null) {
          //              fosUpload.close();
          //              fosUpload=null;
          //            }
          //            if (tempFileUpload!=null) {
          //              new File(tempFileUpload).delete();
          //            }
          //
          //            isFile4Overwrite=false;
          //            absFilePathUpload = "";
          //            fsClientHandler.removeUploadListener(this);
          //          }
          //        fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          //        fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          //        fsMessageSender.send(fsResponse);
          //        break;
        case OVERWRITE_FOLDER :
          fsResponse = new FsResponse();
          this.overWriteValueUpload = ((Integer)fsRequest.getData()).intValue();
          logger.debug("overWriteValueUpload : " + overWriteValueUpload);
          if (overWriteValueUpload == OVERWRITE_CANCEL) {
            logger.debug("Overwrite Cancelled");
            fsResponse = new FsResponse();
            logger.debug("Upload cancelled");
            fsResponse.setResponseCode(CANCLED);
            fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            fsMessageSender.send(fsResponse);
            fsClientHandler.removeUploadListener(this);
            return;
          } else if (overWriteValueUpload == OVERWRITE_YES || overWriteValueUpload == OVERWRITE_YES_TO_ALL) {
            try {
              fsResponse.setResponseCode(FOLDER_CREATED);
            } catch (FsException fex) {
              fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
              fsExceptionHolder.setErrorMessage(fex.getMessage());
              fsResponse.setFsExceptionHolder(fsExceptionHolder);
              fsResponse.setResponseCode(ERROR_MESSAGE);
              logger.error(serverUtil.getStackTrace(fex));

              //             if (fosUpload!=null) {
              //               fosUpload.close();
              //               fosUpload=null;
              //             }
              //             if (tempFileUpload!=null) {
              //               new File(tempFileUpload).delete();
              //             }

              // isFile4Overwrite=false;
              absFilePathUpload = "";
              fsClientHandler.removeUploadListener(this);
            }
          } else if (overWriteValueUpload == OVERWRITE_NO) {
            fsResponse.setResponseCode(FOLDER_NOT_CREATED);
          }
          fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;

        case OVERWRITE_FILE :
          fsResponse = new FsResponse();
          this.overWriteValueUpload = ((Integer)fsRequest.getData()).intValue();
          logger.debug("overWriteValueUpload : " + overWriteValueUpload);
          if (overWriteValueUpload == OVERWRITE_CANCEL) {
            logger.debug("Overwrite Cancelled");
            fsResponse = new FsResponse();
            logger.debug("Upload cancelled");
            //          fosUpload.close();
            //          new File(tempFileUpload).delete();
            absFilePathUpload = "";
            fsResponse.setResponseCode(CANCLED);
            fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            fsMessageSender.send(fsResponse);
            fsClientHandler.removeUploadListener(this);
            return;
          } else if (overWriteValueUpload == OVERWRITE_YES || overWriteValueUpload == OVERWRITE_YES_TO_ALL) {
            try {
              logger.debug("Inside Server side Overwrite Yes And yes all value............");
              //isFile4Overwrite=true;
              //            File fileTemp = new File(tempFileUpload);
              //            if (fileTemp.exists()) {
              //              fileTemp.delete();
              //            }
              //            fileTemp.createNewFile();
              //            fosUpload = new FileOutputStream(fileTemp);

              fsResponse.setData1(SOCKET_RECEIVE);
              fsResponse.setData(this.absFilePathUpload);
              fsResponse.setResponseCode(SOCKET_4_UPLOAD_FILE_CREATED);
              logger.debug("Empty File created option sending by srver.. after overwrite value.......");
            } catch (FsException fex) {
              fsExceptionHolder = serverUtil.getFsExceptionHolder(fex);
              fsExceptionHolder.setErrorMessage(fex.getMessage());
              fsResponse.setFsExceptionHolder(fsExceptionHolder);
              fsResponse.setResponseCode(ERROR_MESSAGE);
              logger.error(serverUtil.getStackTrace(fex));

              //            if (fosUpload!=null) {
              //              fosUpload.close();
              //              fosUpload=null;
              //            }
              //            if (tempFileUpload!=null) {
              //              new File(tempFileUpload).delete();
              //            }

              // isFile4Overwrite=false;
              absFilePathUpload = "";
              fsClientHandler.removeUploadListener(this);
            }
          } else if (overWriteValueUpload == OVERWRITE_NO) {
            fsResponse.setResponseCode(FILE_CLOSED);
          }
          fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
        case SOCKET_CLOSE_FILE :
          // isFile4Overwrite=false;

          //        uploadEndTime = new Date().getTime();
          //        logger.debug("Upload complete : " + new Date(uploadEndTime));
          //        logger.debug("Total Time (sec): " + (uploadEndTime - uploadStartTime) / 1000);

          fsResponse = new FsResponse();
          fsResponse.setResponseCode(FILE_CLOSED);
          fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);

          //fsClientHandler.removeUploadListener(this);
          break;
        case CLOSE_FILE :
          fsResponse = new FsResponse();
          try {
            //          fosUpload.close();
            fsResponse.setResponseCode(FILE_CLOSED);
            //          String serverMD5SumOfCurrentUploadFile=commonUtil.generateMD5Sum(new FileInputStream(tempFileUpload));
            //          
            //          logger.debug("serverMD5SumOfCurrentUploadFile " + serverMD5SumOfCurrentUploadFile);
            //          
            //          logger.debug("clientMD5SumOfCurrentUploadFile " + clientMD5SumOfCurrentUploadFile);

            //          if(serverMD5SumOfCurrentUploadFile.equals(clientMD5SumOfCurrentUploadFile)){
            //            logger.debug("File Transfer is successful");
            //            
            //            fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
            //            fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            //            fsResponse.setResponseCode(PROGRESS_BUILDING);
            //            fsMessageSender.send(fsResponse);
            //            
            //            if (isFile4Overwrite){
            //              Object obj[] = { absFilePathUpload };
            //              fsConnection.deleteItems(obj);                  
            //            }
            //            
            //            FileInputStream fis = new FileInputStream(new File(tempFileUpload));
            //            fsConnection.createFile(absFilePathUpload, fis, fsFileHolderUpload.getMimeType());
            //            //fsConnection.updateFile(absFilePathUpload, fis, fsFileHolderUpload.getMimeType());
            //            fis.close();
            //            fsResponse.setResponseCode(FILE_CLOSED);
            //            logger.debug("File Created");


            //          }else{
            //            logger.debug("File Transfer is not successful due to Curruption");
            //            
            //            //send client a message stating file has been currupted during file transfer. 
            //             fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
            //             fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
            //             fsResponse.setResponseCode(FILE_CURRUPTED);
            //             fsMessageSender.send(fsResponse);
            //            
            //          }
            // isFile4Overwrite=false;
            //          absFilePathUpload="";
            //          new File(tempFileUpload).delete();

            //          serverMD5SumOfCurrentUploadFile=null;
            //          clientMD5SumOfCurrentUploadFile=null;

          } catch (FsException fsException) {
            fsExceptionHolder = serverUtil.getFsExceptionHolder(fsException);
            fsExceptionHolder.setErrorMessage(fsException.getMessage());
            fsResponse.setFsExceptionHolder(fsExceptionHolder);
            fsResponse.setResponseCode(ERROR_MESSAGE);

            //          if (fosUpload!=null) {
            //            fosUpload.close();
            //            fosUpload=null;
            //          }
            //          if (tempFileUpload!=null) {
            //            new File(tempFileUpload).delete();
            //          }
            //
            //          isFile4Overwrite=false;
            //          absFilePathUpload = "";
            fsClientHandler.removeUploadListener(this);

          } catch (Exception ex) {
            fsExceptionHolder = serverUtil.getFsExceptionHolder(new Exception("Upload Failure"));
            fsResponse.setFsExceptionHolder(fsExceptionHolder);
            fsResponse.setResponseCode(ERROR_MESSAGE);
            logger.error(serverUtil.getStackTrace(ex));
            //          
            //          if (fosUpload!=null) {
            //            fosUpload.close();
            //            fosUpload=null;
            //          }
            //          if (tempFileUpload!=null) {
            //            new File(tempFileUpload).delete();
            //          }

            // isFile4Overwrite=false;
            absFilePathUpload = "";
            fsClientHandler.removeUploadListener(this);
          }
          fsResponse.setOperation(FsRemoteOperationConstants.UPLOAD);
          fsResponse.setSuperResponseCode(fsRequest.getSuperRequestCode());
          fsMessageSender.send(fsResponse);
          break;
      }

    } catch (FsException fex) {
      logger.error(serverUtil.getStackTrace(fex));
      fsClientHandler.removeUploadListener(this);
    } catch (Exception ex) {
      logger.error(serverUtil.getStackTrace(ex));
      fsClientHandler.removeUploadListener(this);
    }
  }

  /**
  * gives String representation of FsHandleUploadServer object.
  * @return String repersentation of FsHandleUploadServer object.
  */
  public String toString() {
    return this.toString();
  }

}
