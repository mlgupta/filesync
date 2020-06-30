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
 * $Id: FsFolderDocInfoHolder.java,v 1.7 2006/05/19 10:04:14 skumar Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import java.io.Serializable;


/**
 *	To save the states of navigation buttons for a remote folder.
 *  @author            Deepali Chitkulwar.
 *  @version           1.0
 * 	Date of creation:  14-07-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class

FsFolderDocInfoHolder implements Serializable {
  private String currentFolderPath;
  
  private String currentFolderPathParent;


  private boolean enableBackButton;

  private boolean enableForwardButton;

  private boolean enableParentButton;


  /**
   * To get the absolute path of current folder while navigating.
   * @return absolute path of current folder.
   */
  public String getCurrentFolderPath() {
    return currentFolderPath;
  }
  public String getCurrentFolderPathParent() {
    return currentFolderPathParent;
  }

  /**
   * Sets the current folder path to the specified folder path.
   * @param currentFolderPath absolute path of folder.
   */
  public void setCurrentFolderPath(String currentFolderPath) {
    this.currentFolderPath = currentFolderPath;
  }
  
  public void setCurrentFolderPathParent(String currentFolderPathParent) {
    this.currentFolderPathParent = currentFolderPathParent;
  }

  /**
   * Sets whether to enable or disable the back button.
   * @param enableBackButton true/false value to be set.
   */
  public void setEnableBackButton(boolean enableBackButton) {
    this.enableBackButton = enableBackButton;
  }

  /**
   * Checks whether to enable or disable the back button.
   * @return enableBackButton true if any folder exists previous to this folder 
   * in the navigation history else returns false. 
   */
  public boolean isEnableBackButton() {
    return enableBackButton;
  }

  /**
   * Sets whether to enable or disable the forward button.
   * @param enableForwardButton true/false value to be set.
   */
  public void setEnableForwardButton(boolean enableForwardButton) {
    this.enableForwardButton = enableForwardButton;
  }

  /**
   * Checks whether to enable or disable the forward button.
   * @return enableForwardButton true if any folder exists next to this folder 
   * in the navigation history else returns false. 
   */
  public boolean isEnableForwardButton() {
    return enableForwardButton;
  }

  /**
   * Sets whether to enable or disable the parent button.
   * @param enableParentButton true/false value to be set.
   */
  public void setEnableParentButton(boolean enableParentButton) {
    this.enableParentButton = enableParentButton;
  }

  /**
   * Checks whether to enable or disable the parent button.
   * @return enableParentButton true if parent exists for this folder else returns false. 
   */
  public boolean isEnableParentButton() {
    return enableParentButton;
  }

  /**
   * gives String representation of FsFolderDocInfoHolder object.
   * @return String repersentation of FsFolderDocInfoHolder object.
   */
  public String toString() {
    String strTemp = "";
    strTemp += "\n\t currentFolderPath : " + currentFolderPath;
    strTemp += "\n\t enableBackButton : " + enableBackButton;
    strTemp += "\n\t enableForwardButton : " + enableForwardButton;
    strTemp += "\n\t enableParentButton : " + enableParentButton;

    return strTemp;
  }
}
