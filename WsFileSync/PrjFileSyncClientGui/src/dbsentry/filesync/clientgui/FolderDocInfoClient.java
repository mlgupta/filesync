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
 * $Id: FolderDocInfoClient.java,v 1.12 2006/02/20 15:35:59 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import java.io.File;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 *	To hold information regarding folder and file.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    20-01-2004
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   07-07-2005
 */
public class FolderDocInfoClient {

  private String currentFolderPath;

  private boolean backButtonEnabled;

  private boolean forwardButtonEnabled;

  private boolean parentButtonEnabled;

  private ArrayList navigationHistory;

  private int navigationPointer;

  private ArrayList listOfParents;


  /**
   * A constructs object of FolderDocInfoClient type.
   */
  public FolderDocInfoClient() {
    navigationHistory = new ArrayList();
    navigationPointer = 0;
    backButtonEnabled = false;
    forwardButtonEnabled = false;
    listOfParents = new ArrayList();
  }

  /**
   * Purpose : returns the current folderpath in the navigation.
   * @return :currentFolderPath
   */
  public String getCurrentFolderPath() {
    return currentFolderPath;
  }

  /**
   * sets the current folderpath of navigation to retrieve it later.
   * @param newCurrentFolderPath current folder path
   */
  public void setCurrentFolderPath(String newCurrentFolderPath) {
    currentFolderPath = newCurrentFolderPath;
  }

  /**
   * returns whether to enable or disable the back button.
   * @return backButtonEnabled return true if back button should be enabled
   */
  public boolean isBackButtonEnabled() {
    return backButtonEnabled;
  }

  /**
   * sets whether to enable or disable the back button.
   * @param newBackButtonEnabled a boolean value to enabe or disable back button
   */
  public void setBackButtonEnabled(boolean newBackButtonEnabled) {
    backButtonEnabled = newBackButtonEnabled;
  }

  /**
   * returns whether to enable or disable the forward button.
   * @return forwardButtonEnabled a boolean value which indicates if forward button should be enabled
   */
  public boolean isForwardButtonEnabled() {
    return forwardButtonEnabled;
  }

  /**
   * sets whether to enable or disable the forward button.
   * @param newForwardButtonEnabled a boolean value to enable or disable forward button
   */
  public void setForwardButtonEnabled(boolean newForwardButtonEnabled) {
    forwardButtonEnabled = newForwardButtonEnabled;
  }

  /**
   * To add a folderpath in navigation history while navigating through folders.
   * It disables the back and forward button if only one folder is there in 
   * navigation history.
   * @param newFolderPath new folder path to add to the list
   */
  public void addFolderPath(String newFolderPath) {
    //clear the nevigation history from the current position onward if user finishes the 
    //nevigation in the history and starts the nevigation again

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
  }

  /**
   * To get previous folder path.
   * this function returns the previous folder path. One point to note is that it will 
   * disable the back button if previous to previous folder path is not available.
   * @return previous folder path.
   */
  public

  String getPrevFolderPath() {
    String prevFolderPath = null;
    File file = null;
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
        file = (new File(prevFolderPath)).getParentFile();
      }
    } catch (Exception ex) {
      navigationHistory.remove(navigationPointer);
      prevFolderPath = getPrevFolderPath();
    }
    return prevFolderPath;
  }

  /**
   * To get next folder path
   * this function returns the previous folder path. One point to note is that it will 
   * disable the forward button if next to next folder path is not available.
   * @return nextFolderPath next folder path.
   */
  public String getNextFolderPath() {
    String nextFolderPath = null;
    File file = null;
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
        file = (new File(nextFolderPath)).getParentFile();
      } else {
        nextFolderPath = getPrevFolderPath();
      }
    } catch (Exception ex) {
      navigationHistory.remove(navigationPointer);
      nextFolderPath = getNextFolderPath();
    }

    return nextFolderPath;
  }

  /**
   * to returns the string representation of navigation history. 
   * @return string representation of navigation history.
   */
  public String toString() {
    String strTemp = "";
    Logger logger = Logger.getLogger("ClientLogger");
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
        strTemp += "\n\tnavigationHistory : " + navigationHistory;
      }

      strTemp += "\n\tnavigationPointer : " + navigationPointer;
      strTemp += "\n\tbackButtonEnabled : " + backButtonEnabled;
      strTemp += "\n\tforwardButtonEnabled : " + forwardButtonEnabled;
    }
    return strTemp;
  }

  /**
   * returns whether to enable or disable the parent button.
   * It disables parent button if the current folder is root.
   * @return if parent button should be enabled or disabled
   */
  public boolean isParentButtonEnabled() {
    parentButtonEnabled = true;
    File file = new File(currentFolderPath);
    File roots[] = File.listRoots();
    int rootsLength = roots.length;
    for (int index = 0; index < rootsLength; index++) {
      if (file.getAbsolutePath().equals(roots[index].getAbsolutePath())) {
        parentButtonEnabled = false;
        break;

      }
    }
    return parentButtonEnabled;
  }

}
