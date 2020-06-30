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
 * $Id: FsHandleCopyMoveClient.java,v 1.15 2006/03/02 05:53:17 sgupta Exp $
 *****************************************************************************
 */
/*package dbsentry.filesync.client;                                             THIS CLASS HAS BEEN REMOVED

import dbsentry.filesync.client.enumconstants.EnumClipBoardOperation;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsMessage;
import dbsentry.filesync.common.FsMessageSender;
import dbsentry.filesync.common.FsRequest;
import dbsentry.filesync.common.FsResponse;

import java.awt.Frame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;


/**
 *  Handles the copy/move operation at client side.
 *  @author Deepali Chitkulwar
 *  @version 1.0
 * 	Date of creation:   05-07-2005
 *  Last Modfied by :    
 * 	Last Modfied Date:   

public class

FsHandleCopyMoveClient implements PropertyChangeListener {
  private Frame guiFrame;

  private Logger logger;

  private boolean copyCancelled = false;

  private boolean moveCancelled = false;

  private PropertyChangeSupport propertyChangeSupport;

  private String superRequestCodeForCopyMove;

  private String superRequestCode;

  private FsMessageSender fsMessageSender;

  private FsFileSystemOperationsRemote fsFileSystemOperationsRemote;

  private Progress copyMoveProgress;

  /**
   * Constructs FsHandleCopyMoveClient object to handle copy/move related requests.
   * @param guiFrame Frame object to which the copy/move progress is redirected.
   * @param logger Logger object.
   * @param fsFileSystemOperationsRemote FsFileSystemOperationsRemote object which implements 
   * PipeMsgListener(which listens for the jxta response) and redirects responses to this class.
  
  public FsHandleCopyMoveClient(Frame guiFrame, Logger logger,
                                FsFileSystemOperationsRemote fsFileSystemOperationsRemote) {
    this.logger = logger;
    this.guiFrame = guiFrame;
    this.fsMessageSender = fsFileSystemOperationsRemote.getFsMessageSender();
    this.fsFileSystemOperationsRemote = fsFileSystemOperationsRemote;
  }

  /**
   * To add a property change listener for this class.
   * @param propertyChangeListener propertyChangeListener object which will listen for 
   * the propertyChange event fired by this class.
  
  public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
    }
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }

  /**
   * handles the PropertyChange event fired by FsFileSystemOperationsRemote class,
   * checks for the source of the event,and handles the event only if (it is meant for
   * this class)the source matches with the superRequestCodeForDownload.
   * @param evt PropertyChangeEvent object
  
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource().equals(superRequestCodeForCopyMove)) {
      if (evt.getPropertyName().equals("fsResponse")) {
        FsResponse fsResponse = (FsResponse)evt.getNewValue();
        logger.debug("fsResponse" + fsResponse.getResponseCode());
        handleCopyMove(fsResponse);
      } else if (evt.getPropertyName().equals("fsExceptionHolder")) {
        FsExceptionHolder fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
        logger.debug(fsExceptionHolder);
        //handle exception
      } else if (evt.getPropertyName().equals("operationCancelled")) {
        if (superRequestCodeForCopyMove.startsWith(FsMessage.FOR_COPY)) {
          logger.debug("Copy cancelled");
          copyCancelled = true;
        } else {
          logger.debug("Move cancelled");
          moveCancelled = true;
        }
      }
    }
  }

  /**
   * Handles the download related responses from server,which are 
   * redirected by FsFileSystemOperationsRemote class.
   * @param fsResponse FsResponse object containing response from server.
  
  private void handleCopyMove(FsResponse fsResponse) {
    int responseCode = fsResponse.getResponseCode();
    switch (responseCode) {
    case FsMessage.REMOTE_COPY_STARTED :
      if (guiFrame != null) {
        Logger progressLogger = Logger.getLogger("ClientLogger");
        copyMoveProgress =
          new Progress(guiFrame, "Copy Progressbar", false, progressLogger, fsResponse.getSuperResponseCode());
        copyMoveProgress.addPropertyChangeListener(this);
        copyMoveProgress.setOperation("Copying");
        copyMoveProgress.setMaxProgress(100);
        copyMoveProgress.setVisible(true);
        long totalSizeCopy = ((Long)fsResponse.getData()).longValue();
        copyMoveProgress.setTotalData(totalSizeCopy);
      }
      break;
    case FsMessage.REMOTE_MOVE_STARTED :
      if (guiFrame != null) {
        Logger progressLogger = Logger.getLogger("ClientLogger");
        copyMoveProgress =
          new Progress(guiFrame, "Move Progressbar", false, progressLogger, fsResponse.getSuperResponseCode());
        copyMoveProgress.addPropertyChangeListener(this);
        copyMoveProgress.setOperation("Moving");
        copyMoveProgress.setMaxProgress(100);
        copyMoveProgress.setVisible(true);
        long totalSizeMove = ((Long)fsResponse.getData()).longValue();
        copyMoveProgress.setTotalData(totalSizeMove);
      }
      break;
    case FsMessage.REMOTE_FILE_COPIED :
      if (guiFrame != null) {
        String copyFilePath = (String)fsResponse.getData();
        Long copyFileSize = (Long)fsResponse.getData1();
        copyMoveProgress.setFilePath(copyFilePath);
        copyMoveProgress.setPrevByteRead(copyFileSize.longValue());

        FsRequest fsRequest = new FsRequest();
        fsRequest.setData(Boolean.valueOf(copyCancelled));
        fsRequest.setRequestCode(FsMessage.REMOTE_COPY_CANCEL);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);

        if (copyCancelled) {
          copyMoveProgress.dispose();
          String destFolderPath = (String)fsResponse.getData();
          Vector remotePasteOperationCancelled = new Vector();
          remotePasteOperationCancelled.add(new Integer(EnumClipBoardOperation.COPY));
          remotePasteOperationCancelled.add(destFolderPath);
          propertyChangeSupport
          .firePropertyChange(new PropertyChangeEvent(superRequestCode, "remotePasteOperationCancelled", null,
                                                                           remotePasteOperationCancelled));
          fsFileSystemOperationsRemote.removePropertyChange(this);
        }
      }
      break;
    case FsMessage.REMOTE_FILE_MOVED :
      if (guiFrame != null) {
        String moveFilePath = (String)fsResponse.getData();
        Long moveFileSize = (Long)fsResponse.getData1();
        copyMoveProgress.setFilePath(moveFilePath);
        copyMoveProgress.setPrevByteRead(moveFileSize.longValue());

        FsRequest fsRequest = new FsRequest();
        fsRequest.setData(Boolean.valueOf(moveCancelled));
        fsRequest.setRequestCode(FsMessage.REMOTE_MOVE_CANCEL);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);

        if (moveCancelled) {
          copyMoveProgress.dispose();
          String destFolderPath = (String)fsResponse.getData();
          String srcFolderPath = (String)fsResponse.getData1();
          Vector remotePasteOperationCancelled = new Vector();
          remotePasteOperationCancelled.add(new Integer(EnumClipBoardOperation.CUT));
          remotePasteOperationCancelled.add(destFolderPath);
          remotePasteOperationCancelled.add(srcFolderPath);
          propertyChangeSupport
          .firePropertyChange(new PropertyChangeEvent(superRequestCode, "remotePasteOperationCancelled", null,
                                                                           remotePasteOperationCancelled));
          fsFileSystemOperationsRemote.removePropertyChange(this);
        }
      }
      break;
    case FsMessage.REMOTE_ITEM_COPIED :
      if (guiFrame != null) {
        copyMoveProgress.dispose();
        String destFolderPath = (String)fsResponse.getData();
        Vector remotePasteOperationComplete = new Vector();
        remotePasteOperationComplete.add(new Integer(EnumClipBoardOperation.COPY));
        remotePasteOperationComplete.add(destFolderPath);
        propertyChangeSupport
        .firePropertyChange(new PropertyChangeEvent(superRequestCode, "remotePasteOperationComplete", null,
                                                                         remotePasteOperationComplete));
        fsFileSystemOperationsRemote.removePropertyChange(this);
      }
      break;
    case FsMessage.REMOTE_ITEM_MOVED :
      if (guiFrame != null) {
        copyMoveProgress.dispose();
        String destFolderPath = (String)fsResponse.getData();
        String srcFolderPath = (String)fsResponse.getData1();
        Vector remotePasteOperationComplete = new Vector();
        remotePasteOperationComplete.add(new Integer(EnumClipBoardOperation.CUT));
        remotePasteOperationComplete.add(destFolderPath);
        remotePasteOperationComplete.add(srcFolderPath);
        propertyChangeSupport
        .firePropertyChange(new PropertyChangeEvent(superRequestCode, "remotePasteOperationComplete", null,
                                                                         remotePasteOperationComplete));
        fsFileSystemOperationsRemote.removePropertyChange(this);
      }
      break;
    case FsMessage.OVERWRITE_OPTION_FOLDER_COPY :
      FsRequest fsRequest = new FsRequest();
      if (guiFrame != null) {
        String folderName = (String)fsResponse.getData();
        FolderOverWriteOptions overWriteDialog = new FolderOverWriteOptions(guiFrame, "Overwrite", true);
        overWriteDialog.getTaOverwriteMessage()
        .setText("This folder already contains a folder named '" + folderName + "'");
        overWriteDialog.setVisible(true);
        Integer overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
        fsRequest.setData(overWriteValue);
        fsRequest.setRequestCode(FsMessage.OVERWRITE_OPTION_FOLDER_COPY);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
      } else {
        fsRequest.setData(new Integer(FsMessage.OVERWRITE_YESALL));
        fsRequest.setRequestCode(FsMessage.OVERWRITE_OPTION_FOLDER_COPY);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
      }
      break;
    case FsMessage.OVERWRITE_OPTION_FILE_COPY :
      fsRequest = new FsRequest();
      if (guiFrame != null) {
        String fileName = (String)fsResponse.getData();
        FileOverwriteOption overWriteDialog = new FileOverwriteOption(guiFrame, "Overwrite", true);
        overWriteDialog.setTaOverwriteMessage("This folder already contains a readonly file named '" + fileName + "'");
        overWriteDialog.setLblExistingFileSize(((Long)fsResponse.getData1()).longValue() + "");
        overWriteDialog.setLblReplaceFileSize(((Long)fsResponse.getData2()).longValue() + "");
        overWriteDialog.setVisible(true);
        Integer overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
        fsRequest.setData(overWriteValue);
        fsRequest.setRequestCode(FsMessage.OVERWRITE_OPTION_FILE_COPY);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
      } else {
        fsRequest.setData(new Integer(FsMessage.OVERWRITE_YESALL));
        fsRequest.setRequestCode(FsMessage.OVERWRITE_OPTION_FILE_COPY);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
      }
      break;
    case FsMessage.OVERWRITE_OPTION_FOLDER_MOVE :
      fsRequest = new FsRequest();
      if (guiFrame != null) {
        String folderName = (String)fsResponse.getData();
        FolderOverWriteOptions overWriteDialog = new FolderOverWriteOptions(guiFrame, "Overwrite", true);
        overWriteDialog.getTaOverwriteMessage()
        .setText("This folder already contains a folder named '" + folderName + "'");
        overWriteDialog.setVisible(true);
        Integer overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
        fsRequest.setData(overWriteValue);
        fsRequest.setRequestCode(FsMessage.OVERWRITE_OPTION_FOLDER_MOVE);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
      } else {
        fsRequest.setData(new Integer(FsMessage.OVERWRITE_YESALL));
        fsRequest.setRequestCode(FsMessage.OVERWRITE_OPTION_FOLDER_MOVE);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
      }
      break;
    case FsMessage.OVERWRITE_OPTION_FILE_MOVE :
      fsRequest = new FsRequest();
      if (guiFrame != null) {
        String fileName = (String)fsResponse.getData();
        FileOverwriteOption overWriteDialog = new FileOverwriteOption(guiFrame, "Overwrite", true);
        overWriteDialog.setTaOverwriteMessage("This folder already contains a file named '" + fileName + "'");
        overWriteDialog.setLblExistingFileSize(((Long)fsResponse.getData1()).longValue() + "");
        overWriteDialog.setLblReplaceFileSize(((Long)fsResponse.getData2()).longValue() + "");
        overWriteDialog.setVisible(true);
        Integer overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
        fsRequest.setData(overWriteValue);
        fsRequest.setRequestCode(FsMessage.OVERWRITE_OPTION_FILE_MOVE);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
      } else {
        fsRequest.setData(new Integer(FsMessage.OVERWRITE_YESALL));
        fsRequest.setRequestCode(FsMessage.OVERWRITE_OPTION_FILE_MOVE);
        fsRequest.setSuperRequestCode(fsResponse.getSuperResponseCode());
        fsMessageSender.send(fsRequest);
      }
      break;
    case FsMessage.REMOTE_COPY_FAILED :
      logger.info("Copy Failure");
      if (guiFrame != null) {
        copyMoveProgress.dispose();
      }
      propertyChangeSupport
      .firePropertyChange(new PropertyChangeEvent(superRequestCode, "fsResponse", null, fsResponse));
      fsFileSystemOperationsRemote.removePropertyChange(this);
    case FsMessage.REMOTE_MOVE_FAILED :
      logger.info("Move Failed");
      if (guiFrame != null) {
        copyMoveProgress.dispose();
      }
      propertyChangeSupport
      .firePropertyChange(new PropertyChangeEvent(superRequestCode, "fsResponse", null, fsResponse));
      fsFileSystemOperationsRemote.removePropertyChange(this);
    }
  }

  /**
   * Sends a request to copy an item to the specified location.
   * @param destinationFolderPath absolute path of destination folder.
   * @param sourcePath path of item whose copy is to be created.
   * @param superRequestCode a token indicating the thread which will handle the response.
  
  public void itemCopy(String destinationFolderPath, String sourcePath, String superRequestCode) {
    this.superRequestCode = superRequestCode;

    logger.debug("In ItemCopy");
    String itemPaths[] = { sourcePath };
    itemCopy(destinationFolderPath, itemPaths, superRequestCode);
  }

  /**
   * Sends a request to copy specified items to specified folder.
   * @param destinationFolderPath absolute path of destination folder.
   * @param itemPaths String array containing absolute paths of items whose copy is 
   * to be created.
   * @param superRequestCode a token indicating the thread which will handle the response.
  
  public void itemCopy(String destinationFolderPath, String[] itemPaths, String superRequestCode) {
    this.superRequestCode = superRequestCode;

    logger.debug("In ItemCopy");
    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(destinationFolderPath);
    fsRequest.setDatas(itemPaths);
    fsRequest.setRequestCode(FsMessage.REMOTE_ITEM_COPY);
    superRequestCodeForCopyMove = FsMessage.FOR_COPY + (new Random()).nextInt();
    fsRequest.setSuperRequestCode(superRequestCodeForCopyMove);
    fsMessageSender.send(fsRequest);

  }

  /**
   * Sends a request to move an item to the specified location.
   * @param destinationFolderPath absolute path of destination folder.
   * @param itemPath absolute path of item to be moved.
   * @param superRequestCode a token indicating the thread which will handle the response.
  
  public void itemMove(String destinationFolderPath, String itemPath, String superRequestCode) {
    this.superRequestCode = superRequestCode;
    superRequestCodeForCopyMove = FsMessage.FOR_MOVE + (new Random()).nextInt();

    logger.debug("In Item Move");
    String itemPaths[] = { itemPath };
    itemMove(destinationFolderPath, itemPaths, superRequestCode);
  }

  /**
   * Sends a request to move specified items to specified folder.
   * @param destinationFolderPath absolute path of destination folder.
   * @param itemPaths String array containing absolute paths of items which are to be moved.
   * @param superRequestCode a token indicating the thread which will handle the response.
  
  public void itemMove(String destinationFolderPath, String[] itemPaths, String superRequestCode) {
    this.superRequestCode = superRequestCode;

    logger.debug("In Item Move");
    FsRequest fsRequest = new FsRequest();
    fsRequest.setData(destinationFolderPath);
    fsRequest.setDatas(itemPaths);
    fsRequest.setRequestCode(FsMessage.REMOTE_ITEM_MOVE);
    superRequestCodeForCopyMove = FsMessage.FOR_MOVE + (new Random()).nextInt();
    fsRequest.setSuperRequestCode(superRequestCodeForCopyMove);
    fsMessageSender.send(fsRequest);
  }

  /**
   * gives String representation of FshandleCopyMoveClient object.
   * @return String repersentation of FshandleCopyMoveClient object.
  
  public String toString() {
    return this.toString();
  }

}*/
