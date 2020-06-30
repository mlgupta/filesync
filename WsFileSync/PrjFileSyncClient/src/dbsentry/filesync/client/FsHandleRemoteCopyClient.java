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
 * $Id: JxtaClient.java,v 1.52 2006/03/09 07:41:19 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.client;

import dbsentry.filesync.client.enumconstants.EnumClipBoardOperation;
import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;

import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsRemoteCopyListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;


/**
 * @author Saurabh Gupta
 * @version 1.1
 * @description
 * @Date of creation: 01-03-2006
 * @Last Modfied by : Saurabh Gupta
 * @Last Modfied Date: 12-04-2006
 */
public class FsHandleRemoteCopyClient implements FsRemoteCopyListener {
  private Logger logger;

  private boolean copyCancelled = false;

  private String superRequestCode;

  private FsMessageSender fsMessageSender;
  
  private PropertyChangeSupport  propertyChangeSupport4FsRemoteCopy;
  
  private FsServerHandler fsServerHandler;
  
  private ClientUtil clientUtil;
  
  private CommonUtil commonUtil;
  
  private Integer overWriteValue=0;

  public FsHandleRemoteCopyClient(Logger logger, FsServerHandler fsServerHandler) {
    this.logger = logger;
    this.fsServerHandler=fsServerHandler;
    this.fsMessageSender = fsServerHandler.getFsMessagesender();
    this.clientUtil = new ClientUtil(logger);
    this.commonUtil=new CommonUtil(logger);
  }

  public void addFsRemoteCopyListener(FsRemoteCopyListener copyListener) {
    if (propertyChangeSupport4FsRemoteCopy == null) {
      propertyChangeSupport4FsRemoteCopy = new PropertyChangeSupport(this);
    }
    propertyChangeSupport4FsRemoteCopy.addPropertyChangeListener(copyListener);
  }

  /*public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(superRequestCodeForCopyMove)) {
      if (evt.getPropertyName().equals("fsResponse")) {
        FsResponse fsResponse = (FsResponse)evt.getNewValue();
        logger.debug("fsResponse" + fsResponse.getResponseCode());
        handleRemoteCopy(fsResponse);
      } else if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);
        //handle exception
      } else if (evt.getPropertyName().equals("operationCancelled")) {
        if ((superRequestCodeForCopyMove.startsWith(FsMessage.FOR_COPY))) {
          logger.debug("Copy cancelled");
          copyCancelled = true;
        }
      }
    }
  }*/
   public void propertyChange(PropertyChangeEvent evt) {
     int propertyName=Integer.valueOf(evt.getPropertyName());
     FsResponse fsResponse = (FsResponse)evt.getNewValue();
     if(fsResponse.getSuperResponseCode().equals(superRequestCode)){
       handleRemoteCopy( propertyName,fsResponse);
     }
   }

  private void handleRemoteCopy(int propertyName, FsResponse fsResponse) {
    int responseCode = fsResponse.getResponseCode();
    FsRequest fsRequest=null;
    switch (propertyName) {
      case STARTED:
        long totalSizeCopy = ((Long)fsResponse.getData()).longValue();
        logger.debug("Total Size : " + totalSizeCopy);
        propertyChangeSupport4FsRemoteCopy.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
      case CANCEL:
        copyCancelled=true;
        fsRequest = new FsRequest();
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsRequest.setOperation(FsRemoteOperationConstants.COPY);
        fsRequest.setRequestCode(CANCLED);
        fsMessageSender.send(fsRequest);
        break;
      case NEXT_ITEM:
        String copyFilePath = (String)fsResponse.getData();
        Long copyFileSize = (Long)fsResponse.getData1();
        logger.debug("copyFilePath :" + copyFilePath);
        logger.debug("copyFileSize :" + copyFileSize);
       // copyMoveProgress.setFilePath(copyFilePath);
       // copyMoveProgress.setPrevByteRead(copyFileSize.longValue());
        propertyChangeSupport4FsRemoteCopy.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsRequest = new FsRequest();
        if (copyCancelled) {
        //  copyMoveProgress.dispose();
         // String destFolderPath = (String)fsResponse.getData();
          //Vector remotePasteOperationCancelled = new Vector();
         // remotePasteOperationCancelled.add(new Integer(EnumClipBoardOperation.COPY));
         // remotePasteOperationCancelled.add(destFolderPath);
          propertyChangeSupport4FsRemoteCopy.firePropertyChange(Integer.toString(CANCEL),null,fsResponse);
          
          fsServerHandler.removeRemoteCopyListener(this);
          fsRequest.setRequestCode(CANCLED);
          copyCancelled=false;
        } else {
          fsRequest.setRequestCode(NEXT_ITEM);
        }
        fsRequest.setData(fsResponse.getData());
        fsRequest.setData1(fsResponse.getData1());
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsRequest.setOperation(FsRemoteOperationConstants.COPY);
        fsMessageSender.send(fsRequest);
        break;
      case COMPLETED:
        // copyMoveProgress.dispose();
        //String destFolderPath = (String)fsResponse.getData();
        //Vector remotePasteOperationComplete = new Vector();
        //remotePasteOperationComplete.add(new Integer(EnumClipBoardOperation.COPY));
        //remotePasteOperationComplete.add(destFolderPath);
        fsResponse.setData1(new Integer(EnumClipBoardOperation.COPY));
        propertyChangeSupport4FsRemoteCopy.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeRemoteCopyListener(this);
        break;
      case PROMPT_OVERWRITE_FOLDER:
        if(overWriteValue!=OVERWRITE_YES_TO_ALL){
          propertyChangeSupport4FsRemoteCopy.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        }else{
          fsRequest = new FsRequest();
          fsRequest.setOperation(FsRemoteOperationConstants.COPY);
          fsRequest.setRequestCode(OVERWRITE_FOLDER);
          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
          fsMessageSender.send(fsRequest);
        }
        break;
      case PROMPT_OVERWRITE_FILE:
        if(overWriteValue!=OVERWRITE_YES_TO_ALL){
          propertyChangeSupport4FsRemoteCopy.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        }else{
          fsRequest = new FsRequest();
          fsRequest.setOperation(FsRemoteOperationConstants.COPY);
          fsRequest.setRequestCode(OVERWRITE_FILE);
          fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
          fsMessageSender.send(fsRequest);
        }
        break;
      
      case OVERWRITE_OPTION_FOLDER:
        logger.debug("Over Write Option Folder Copy");
        fsRequest = new FsRequest();
        overWriteValue=(Integer)fsResponse.getData();
        
        logger.debug("FOLDER - overWriteValue "+ overWriteValue);
        if(overWriteValue==OVERWRITE_YES_TO_ALL ||overWriteValue==OVERWRITE_YES){
          fsRequest.setRequestCode(OVERWRITE_FOLDER);
        }else if(overWriteValue==OVERWRITE_NO){
          fsRequest.setRequestCode(NEXT_ITEM);
        }else if(overWriteValue==OVERWRITE_CANCEL){
          fsRequest.setRequestCode(CANCEL);
        }else {
          fsRequest.setRequestCode(OVERWRITE_FOLDER);
        }
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsRequest.setOperation(FsRemoteOperationConstants.COPY);
        fsMessageSender.send(fsRequest);
        break;
      case OVERWRITE_OPTION_FILE:
        logger.debug("Over Write Option File Copy");
        fsRequest = new FsRequest();
        overWriteValue=(Integer)fsResponse.getData();
        
        logger.debug("File - overWriteValue "+ overWriteValue);
        if(overWriteValue==OVERWRITE_YES_TO_ALL ||overWriteValue==OVERWRITE_YES){
          fsRequest.setRequestCode(OVERWRITE_FILE);
        }else if(overWriteValue==OVERWRITE_NO){
          fsRequest.setRequestCode(NEXT_ITEM);
        }else if(overWriteValue==OVERWRITE_CANCEL){
          fsRequest.setRequestCode(CANCEL);
        } else {
          fsRequest.setRequestCode(OVERWRITE_FILE);
        }
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsRequest.setOperation(FsRemoteOperationConstants.COPY);
        fsMessageSender.send(fsRequest);
        break;
      case FAILED:
        logger.info("Copy Failure");
        //  copyMoveProgress.dispose();
        propertyChangeSupport4FsRemoteCopy.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeRemoteCopyListener(this);
        break;
      case ERROR_MESSAGE:
        propertyChangeSupport4FsRemoteCopy.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeRemoteCopyListener(this);
        break;
      case FETAL_ERROR:
        propertyChangeSupport4FsRemoteCopy.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeRemoteCopyListener(this);
        break;
    }
  }

  public void itemCopy(String destinationFolderPath, String sourcePath, String superRequestCode) {
    this.superRequestCode = superRequestCode;
    logger.debug("FsHandleRemoteCopyClient");
    logger.debug("In ItemCopy");
    String itemPaths[] = { sourcePath };
    itemCopy(destinationFolderPath, itemPaths, superRequestCode);
  }

  public void itemCopy(String destinationFolderPath, String[] itemPaths, String superRequestCode) {
    this.superRequestCode = superRequestCode;

    logger.debug("In ItemCopy");
    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(destinationFolderPath);
    fsRequest.setDatas(itemPaths);
    fsRequest.setRequestCode(START);
    fsRequest.setOperation(FsRemoteOperationConstants.COPY);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsMessageSender.send(fsRequest);
  }

  public String toString() {
    return this.toString();
  }

}
