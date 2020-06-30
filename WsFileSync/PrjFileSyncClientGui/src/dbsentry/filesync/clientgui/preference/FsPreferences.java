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
package dbsentry.filesync.clientgui.preference;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @Date of creation:16-05-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public class FsPreferences implements Serializable{

  private Hashtable sessionList=null;
  
  private UploadDnloadManagerPref uploadDnloadManagerPref=null;
  
  private ArrayList rdvList=null;
  
  private ArrayList relayList=null;
  
  private boolean treeLocalVisible = true;
  private boolean treeRemoteVisible = true;
  private boolean tileVertically = false;
  private boolean tileHorizontally = true;
  private boolean browserRemoteVisible = true;
  private boolean browserLocalVisible = true;
  
  public FsPreferences() {
  }

  public void setSessionList(Hashtable sessionList) {
    this.sessionList = sessionList;
  }

  public Hashtable getSessionList() {
    return sessionList;
  }

  public void setUploadDnloadManagerPref(UploadDnloadManagerPref uploadDnloadManagerPref) {
    this.uploadDnloadManagerPref = uploadDnloadManagerPref;
  }

  public UploadDnloadManagerPref getUploadDnloadManagerPref() {
    return uploadDnloadManagerPref;
  }


  public void setRdvList(ArrayList rdvList) {
    this.rdvList = rdvList;
  }

  public ArrayList getRdvList() {
    return rdvList;
  }

  public void setRelayList(ArrayList relayList) {
    this.relayList = relayList;
  }

  public ArrayList getRelayList() {
    return relayList;
  }
  /**
   * indicates if remote browser is visible.
   * @return returns true if visible else false
   */
  public boolean isBrowserRemoteVisible() {
    return browserRemoteVisible;
  }

  /**
   * setter for browserRemoteVisible property.
   * @param browserRemoteVisible sets the visible property of the remote browser 
   */
  public void setBrowserRemoteVisible(boolean browserRemoteVisible) {
    this.browserRemoteVisible = browserRemoteVisible;
  }

  /**
   * setter for browserLocalVisible property.
   * @param browserLocalVisible sets the visible property of the local browser 
   */
  public void setBrowserLocalVisible(boolean browserLocalVisible) {
    this.browserLocalVisible = browserLocalVisible;
  }

  /**
   * indicates if local browser is visible.
   * @return returns true if visible else false
   */
  public boolean isBrowserLocalVisible() {
    return browserLocalVisible;
  }


  /**
   * setter for tileVertically property.
   * @param tileVertically boolean value which indicates if local and remote browser 
   * should be tiled vertically else false
   */
  public void setTileVertically(boolean tileVertically) {
    this.tileVertically = tileVertically;
  }


  /**
   * indicates if the local and remore browser should be tiled vertically.
   * @return true if tiled vertically.
   */
  public boolean isTileVertically() {
    return tileVertically;
  }

  /**
   * setter for tileHorizontally property.
   * @param tileHorizontally boolean value which indicates if local and remote browser 
   * should be tiled horizontally
   */

  public void setTileHorizontally(boolean tileHorizontally) {
    this.tileHorizontally = tileHorizontally;
  }

  /**
   * indicates if the local and remore browser should be tiled horizontally.
   * @return true if tiled horizontally.
   */
  public boolean isTileHorizontally() {
    return tileHorizontally;
  }


  /**
   * setter for treeLocalVisible property.
   * @param treeLocalVisible true if local tree is visible
   */
  public void setTreeLocalVisible(boolean treeLocalVisible) {
    this.treeLocalVisible = treeLocalVisible;
  }


  /**
   * getter for treeLocalVisible property.
   * @return true if local tree is visible.
   */
  public boolean isTreeLocalVisible() {
    return treeLocalVisible;
  }

  /**
   * setter for treeRemoteVisible property.
   * @param treeRemoteVisible true if local tree is visible
   */
  public void setTreeRemoteVisible(boolean treeRemoteVisible) {
    this.treeRemoteVisible = treeRemoteVisible;
  }

  /**
   * getter for treeRemoteVisible property.
   * @return true if remote tree is visible.
   */
  public boolean isTreeRemoteVisible() {
    return treeRemoteVisible;
  }

  

}
