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
 * $Id: FolderDocInfoRemote.java,v 1.15 2006/08/01 10:14:26 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.common.FsFolderDocInfoHolder;
import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFolder;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 *	To hold information regarding folder and document.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:   20-01-2004
 * 	Last Modfied by :     
 * 	Last Modfied Date:    
 */
public class

FolderDocInfoRemote {

  private String currentFolderPath;

  private String currentFolderPathParent;

  private boolean backButtonEnabled;

  private boolean forwardButtonEnabled;

  private boolean parentButtonEnabled;

  private ArrayList navigationHistory;

  private int navigationPointer;

  private FsConnection fsConnection;

  private Logger logger;

  /**
   * To create an object holding the remote folder information.
   * @param fsConnection FsConnection object required to fetch the necessary folder information.
   */
  public FolderDocInfoRemote(FsConnection fsConnection) {
    logger = Logger.getLogger("ServerLogger");
    navigationHistory = new ArrayList();
    navigationPointer = 0;
    backButtonEnabled = false;
    forwardButtonEnabled = false;
    this.fsConnection = fsConnection;
  }

  /**
   * To get current folderpath while navigating through folders.
   * @return currentFolderPath
   */
  public String getCurrentFolderPath() {
    return currentFolderPath;
  }

  public String getCurrentFolderPathParent() {
    return currentFolderPathParent;
  }


  /**
   * To check whether back button is enabled.
   * @return backButtonEnabled
   */
  public boolean isBackButtonEnabled() {
    return backButtonEnabled;
  }

  /**
   * To check whether forward button is enabled.
   * @return forwardButtonEnabled whether forward button is enabled or not.
   */
  public boolean isForwardButtonEnabled() {
    return forwardButtonEnabled;
  }

  /**
   * To add a folder path in the navigation history. It disables back and forward buttons if there 
   * is only one folderpath in the navigation history.
   * @param newFolderPath folder path to be added to the navigation history.
   */
  public void addFolderPath(String newFolderPath) {
    navigationHistory.add(newFolderPath);
    navigationPointer = navigationHistory.size() - 1;
    //disable back and forward button when one one path is there in the nevigation history else enable back 
    //button and disable forward button
    if (navigationHistory.size() == 1) {
      backButtonEnabled = false;
      backButtonEnabled = false;
    } else {
      backButtonEnabled = true;
      forwardButtonEnabled = false;
    }

    currentFolderPath = newFolderPath;
  }

  public void addFolderPathParent(String newFolderPathParent) {
    navigationHistory.add(newFolderPathParent);
    navigationPointer = navigationHistory.size() - 1;
    //disable back and forward button when one one path is there in the nevigation history else enable back 
    //button and disable forward button
    if (navigationHistory.size() == 1) {
      backButtonEnabled = false;
      backButtonEnabled = false;
    } else {
      backButtonEnabled = true;
      forwardButtonEnabled = false;
    }

    currentFolderPathParent = newFolderPathParent;
  }

  /**
   * To get previous folder path from navigation history.One point to note is that it will 
   * disable the back button if previous to previous folder path is not available.
   * @return prevFolderPath previous folder path in the navigation history.
   */
  public String getPrevFolderPath() {
    String prevFolderPath = null;
    FsFolder fsFolder = null;
    FsFolder fsParentFolder = null;

    if (navigationPointer > 0) {
      navigationPointer = navigationPointer - 1;
      prevFolderPath = (String)navigationHistory.get(navigationPointer);
      forwardButtonEnabled = true;
    }
    if (navigationPointer == 0) {
      backButtonEnabled = false;
    }
    try {
      if (prevFolderPath != null) {
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(prevFolderPath);
        fsParentFolder = fsFolder.getParent();
      }
    } catch (FsException ex) {
      navigationHistory.remove(navigationPointer);
      prevFolderPath = getPrevFolderPath();
    }
    currentFolderPath = prevFolderPath;
    return prevFolderPath;
  }

  /**
   * To get next folder path from navigation history.One point to note is that it will 
   * disable the forward button if next to next folder path is not available.
   * @return nextFolderPath next folder path in the navigation history.
   */
  public

  String getNextFolderPath() {
    String nextFolderPath = null;
    FsFolder fsFolder = null;
    FsFolder fsParentFolder = null;

    if (navigationPointer < navigationHistory.size() - 1) {
      navigationPointer = navigationPointer + 1;
      nextFolderPath = (String)navigationHistory.get(navigationPointer);
      backButtonEnabled = true;
    }
    if (navigationPointer == navigationHistory.size() - 1) {
      forwardButtonEnabled = false;
    }
    try {
      if (nextFolderPath != null) {
        fsFolder = (FsFolder)fsConnection.findFsObjectByPath(nextFolderPath);
        fsParentFolder = fsFolder.getParent();
      } else {
        nextFolderPath = getPrevFolderPath();
      }
    } catch (FsException ex) {
      navigationHistory.remove(navigationPointer);
      nextFolderPath = getNextFolderPath();
    }

    currentFolderPath = nextFolderPath;
    return nextFolderPath;
  }

  /**
   * returns String representation of FolderDocInforemote object.
   * @return String repersentation FolderDocInforemote object.
   */
  public String toString() {
    String strTemp = "";
    Logger logger = Logger.getLogger("ServerLogger");
    if (logger.getLevel() == Level.DEBUG) {
      String strArrayValues = "";
      strTemp += "\n\tcurrentFolderPath : " + currentFolderPath;

      if (navigationHistory != null) {
        strArrayValues = "{";
        for (int index = 0; index < navigationHistory.size(); index++) {
          strArrayValues += " " + (Long)navigationHistory.get(index);
        }
        strArrayValues += "}";
        strTemp += "\n\tnavigationHistory : " + strArrayValues;
      } else {
        strTemp += "\n\tnavigationHistory: " + navigationHistory;
      }

      strTemp += "\n\tnavigationPointer : " + navigationPointer;
      strTemp += "\n\tbackButtonEnabled : " + backButtonEnabled;
      strTemp += "\n\tforwardButtonEnabled : " + forwardButtonEnabled;
    }
    return strTemp;
  }

  /**
   * To to check whether parent button is enabled.If current folder path is root folder path 
   * then parent button is disabled.
   * @return parentButtonEnabled
   */
  public boolean isParentButtonEnabled() {
    parentButtonEnabled = true;
    try {
      FsFolder fsFolder = (FsFolder)fsConnection.findFsObjectByPath(currentFolderPath);
      if (fsFolder.getPath().equals(fsConnection.getSeperator())) {
        parentButtonEnabled = false;
      }
    } catch (FsException fex) {
      parentButtonEnabled = false;
    }
    return parentButtonEnabled;
  }

  /**
   * To get the fsFolderDocInfoHolder.
   * @return fsFolderDocInfoHolder FsFolderDocInfoHolder object.
   */
  public FsFolderDocInfoHolder getFolderDocInfoHolder() {
    FsFolderDocInfoHolder fsFolderDocInfoHolder = new FsFolderDocInfoHolder();
    fsFolderDocInfoHolder.setCurrentFolderPath(currentFolderPath);
    fsFolderDocInfoHolder.setCurrentFolderPathParent(currentFolderPathParent);
    fsFolderDocInfoHolder.setEnableBackButton(isBackButtonEnabled());
    fsFolderDocInfoHolder.setEnableForwardButton(isForwardButtonEnabled());
    fsFolderDocInfoHolder.setEnableParentButton(isParentButtonEnabled());
    return fsFolderDocInfoHolder;
  }

}


