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
import dbsentry.filesync.common.listeners.FsRemoteMoveListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;


/**
 * @author Saurabh Gupta
 * @version 1.1
 * @description
 * @Date of creation: 01-03-2006
 * @Last Modfied by : Saurabh gupta
 * @Last Modfied Date: 08-03-2006
 */
public class FsHandleRemoteMoveClient implements FsRemoteMoveListener {

  private Logger logger;

  private boolean moveCancelled = false;
  
  private PropertyChangeSupport  propertyChangeSupport4FsRemoteMove;

  private String superRequestCode;

  private FsMessageSender fsMessageSender;
  
  private FsServerHandler fsServerHandler;
  
  private ClientUtil clientUtil;
  
  private CommonUtil commonUtil;
  
  private Integer overWriteValue=0;

  public FsHandleRemoteMoveClient( Logger logger, FsServerHandler fsServerHandler) {
    this.logger = logger;
    this.fsServerHandler=fsServerHandler;
    this.fsMessageSender = fsServerHandler.getFsMessagesender();
    this.clientUtil = new ClientUtil(logger);
    this.commonUtil=new CommonUtil(logger);
  }

   public void addFsRemoteMoveListener(FsRemoteMoveListener moveListener) {
     if (propertyChangeSupport4FsRemoteMove == null) {
       propertyChangeSupport4FsRemoteMove = new PropertyChangeSupport(this);
     }
     propertyChangeSupport4FsRemoteMove.addPropertyChangeListener(moveListener);
   }

 /* public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(superRequestCodeForCopyMove)) {
      if (evt.getPropertyName().equals("fsResponse")) {
        FsResponse fsResponse = (FsResponse)evt.getNewValue();
        logger.debug("fsResponse" + fsResponse.getResponseCode());
        handleRemoteMove(fsResponse);
      } else if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);
        //handle exception
      } else if (evt.getPropertyName().equals("operationCancelled")) {
        if (superRequestCodeForCopyMove.startsWith(FsMessage.FOR_MOVE)) {
          logger.debug("Move cancelled");
          moveCancelled = true;
        }
      }
    }
  }*/
  public void propertyChange(PropertyChangeEvent evt) {
    int propertyName=Integer.valueOf(evt.getPropertyName());
    FsResponse fsResponse = (FsResponse)evt.getNewValue();
    if(fsResponse.getSuperResponseCode().equals(superRequestCode)){
      handleRemoteMove( propertyName,fsResponse);
    }
  }

  private void handleRemoteMove(int propertyName, FsResponse fsResponse) {
    int responseCode = fsResponse.getResponseCode();
    FsRequest fsRequest=null;
    switch (propertyName) {
    case STARTED:
        long totalSizeMove = ((Long)fsResponse.getData()).longValue();
        logger.debug("Total Size : " + totalSizeMove);
        propertyChangeSupport4FsRemoteMove.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        break;
    case CANCEL:
      moveCancelled=true;
      fsRequest = new FsRequest();
      fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
      fsRequest.setOperation(FsRemoteOperationConstants.MOVE);
      fsRequest.setRequestCode(CANCLED);
      fsMessageSender.send(fsRequest);
      break;
    case NEXT_ITEM:
        String moveFilePath = (String)fsResponse.getData();
        Long moveFileSize = (Long)fsResponse.getData1();
        logger.debug("moveFilePath :" + moveFilePath);
        logger.debug("moveFileSize :" + moveFileSize);
        propertyChangeSupport4FsRemoteMove.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsRequest = new FsRequest();
        if (moveCancelled) {
          propertyChangeSupport4FsRemoteMove.firePropertyChange(Integer.toString(CANCEL),null,fsResponse);
          fsServerHandler.removeRemoteMoveListener(this);
          fsRequest.setRequestCode(CANCLED);
          moveCancelled=false;
        } else {
          fsRequest.setRequestCode(NEXT_ITEM);
        }        
        fsRequest.setData(fsResponse.getData());
        fsRequest.setData1(fsResponse.getData1());
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsRequest.setOperation(FsRemoteOperationConstants.MOVE);
        fsMessageSender.send(fsRequest);  
        break;
    case COMPLETED:
        fsResponse.setData2(new Integer(EnumClipBoardOperation.CUT));
        propertyChangeSupport4FsRemoteMove.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
        fsServerHandler.removeRemoteMoveListener(this);
        break;
    case PROMPT_OVERWRITE_FOLDER:
      if(overWriteValue!=OVERWRITE_YES_TO_ALL){
        propertyChangeSupport4FsRemoteMove.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
      }else{
        fsRequest = new FsRequest();
        fsRequest.setOperation(FsRemoteOperationConstants.MOVE);
        fsRequest.setRequestCode(OVERWRITE_FOLDER);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
      }
      break;
    case PROMPT_OVERWRITE_FILE:
      if(overWriteValue!=OVERWRITE_YES_TO_ALL){
        propertyChangeSupport4FsRemoteMove.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
      }else{
        fsRequest = new FsRequest();
        fsRequest.setOperation(FsRemoteOperationConstants.MOVE);
        fsRequest.setRequestCode(OVERWRITE_FILE);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
      }
      break;
    case OVERWRITE_OPTION_FOLDER:
      logger.debug("Over Write Option Folder Move");
      fsRequest = new FsRequest();
      overWriteValue=(Integer)fsResponse.getData();
      
      logger.debug("File - overWriteValue "+ overWriteValue);
      if(overWriteValue==OVERWRITE_YES_TO_ALL ||overWriteValue==OVERWRITE_YES){
        fsRequest.setRequestCode(OVERWRITE_FOLDER);
      }else if(overWriteValue==OVERWRITE_NO){
        fsRequest.setRequestCode(NEXT_ITEM);
      }else if(overWriteValue==OVERWRITE_CANCEL){
        fsRequest.setRequestCode(CANCEL);
      }else{
        fsRequest.setRequestCode(OVERWRITE_FILE);
      }
      fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
      fsRequest.setOperation(FsRemoteOperationConstants.MOVE);
      fsMessageSender.send(fsRequest);
      break;
    case OVERWRITE_OPTION_FILE:
      logger.debug("Over Write Option File Move");
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
      fsRequest.setOperation(FsRemoteOperationConstants.MOVE);
      fsMessageSender.send(fsRequest);
      break;
    case FAILED:
      logger.info("Copy Failure");
      //  copyMoveProgress.dispose();
      propertyChangeSupport4FsRemoteMove.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
      fsServerHandler.removeRemoteMoveListener(this);
      break;
    case ERROR_MESSAGE:
      propertyChangeSupport4FsRemoteMove.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
      fsServerHandler.removeRemoteMoveListener(this);
      break;
    case FATEL_ERROR:
      propertyChangeSupport4FsRemoteMove.firePropertyChange(Integer.toString(responseCode),null,fsResponse);
      fsServerHandler.removeRemoteMoveListener(this);
      break;
    }
  }

  public void itemMove(String destinationFolderPath, String itemPath, String superRequestCode) {
    this.superRequestCode = superRequestCode;
    String itemPaths[] = { itemPath };
    itemMove(destinationFolderPath, itemPaths, superRequestCode);
  }

  public void itemMove(String destinationFolderPath, String[] itemPaths, String superRequestCode) {
    this.superRequestCode = superRequestCode;

    logger.debug("In ItemMove");
    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(destinationFolderPath);
    fsRequest.setDatas(itemPaths);
    fsRequest.setRequestCode(START);
    fsRequest.setOperation(FsRemoteOperationConstants.MOVE);
    fsRequest.setSuperRequestCode(superRequestCode);
    fsMessageSender.send(fsRequest);
  }

  public String toString() {
    return this.toString();
  }

}
