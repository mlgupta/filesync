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
 * $Id: FsPreferences.java,v 1.9 2006/05/26 13:23:38 skumar Exp $
 *****************************************************************************
 */
//package dbsentry.filesync.clientgui;
//import java.io.Serializable;
//
///**
// *	A bean which stores user preferences.
// *  @author              Jeetendra Prasad
// *  @version             1.0
// * 	Date of creation:    13-04-2005
// * 	Last Modfied by :    Jeetendra Prasad
// * 	Last Modfied Date:   08-07-2005
// */
//public class FsPreferences implements Serializable {
//
//  private boolean treeLocalVisible = true;
//  private boolean treeRemoteVisible = true;
//  private boolean tileVertically = false;
//  private boolean tileHorizontally = true;
//  private boolean browserRemoteVisible = true;
//  private boolean browserLocalVisible = true;
//  private boolean deleteJxta = false;
//  
//  private boolean rememberUIdPwd ;
//  private byte[] userId;
//  private byte[] password;
//  
//  /**
//   * indicates if remote browser is visible.
//   * @return returns true if visible else false
//   */
//  public boolean isBrowserRemoteVisible() {
//    return browserRemoteVisible;
//  }
//
//  /**
//   * setter for browserRemoteVisible property.
//   * @param browserRemoteVisible sets the visible property of the remote browser 
//   */
//  public void setBrowserRemoteVisible(boolean browserRemoteVisible) {
//    this.browserRemoteVisible = browserRemoteVisible;
//  }
//
//  /**
//   * setter for browserLocalVisible property.
//   * @param browserLocalVisible sets the visible property of the local browser 
//   */
//  public void setBrowserLocalVisible(boolean browserLocalVisible) {
//    this.browserLocalVisible = browserLocalVisible;
//  }
//
//  /**
//   * indicates if local browser is visible.
//   * @return returns true if visible else false
//   */
//  public boolean isBrowserLocalVisible() {
//    return browserLocalVisible;
//  }
//
//
//  /**
//   * setter for tileVertically property.
//   * @param tileVertically boolean value which indicates if local and remote browser 
//   * should be tiled vertically else false
//   */
//  public void setTileVertically(boolean tileVertically) {
//    this.tileVertically = tileVertically;
//  }
//
//
//  /**
//   * indicates if the local and remore browser should be tiled vertically.
//   * @return true if tiled vertically.
//   */
//  public boolean isTileVertically() {
//    return tileVertically;
//  }
//
//  /**
//   * setter for tileHorizontally property.
//   * @param tileHorizontally boolean value which indicates if local and remote browser 
//   * should be tiled horizontally
//   */
//
//  public void setTileHorizontally(boolean tileHorizontally) {
//    this.tileHorizontally = tileHorizontally;
//  }
//
//  /**
//   * indicates if the local and remore browser should be tiled horizontally.
//   * @return true if tiled horizontally.
//   */
//  public boolean isTileHorizontally() {
//    return tileHorizontally;
//  }
//
//
//  /**
//   * setter for treeLocalVisible property.
//   * @param treeLocalVisible true if local tree is visible
//   */
//  public void setTreeLocalVisible(boolean treeLocalVisible) {
//    this.treeLocalVisible = treeLocalVisible;
//  }
//
//
//  /**
//   * getter for treeLocalVisible property.
//   * @return true if local tree is visible.
//   */
//  public boolean isTreeLocalVisible() {
//    return treeLocalVisible;
//  }
//
//  /**
//   * setter for treeRemoteVisible property.
//   * @param treeRemoteVisible true if local tree is visible
//   */
//  public void setTreeRemoteVisible(boolean treeRemoteVisible) {
//    this.treeRemoteVisible = treeRemoteVisible;
//  }
//
//  /**
//   * getter for treeRemoteVisible property.
//   * @return true if remote tree is visible.
//   */
//  public boolean isTreeRemoteVisible() {
//    return treeRemoteVisible;
//  }
//
//
//  /**
//   * setter for rememberUIdPwd property.
//   * @param rememberUIdPwd true if user id and password should be remembered.
//   */
//  public void setRememberUIdPwd(boolean rememberUIdPwd) {
//    this.rememberUIdPwd = rememberUIdPwd;
//  }
//
//
//  /**
//   * getter for rememberUIdPwd property.
//   * @return true if the userid and password is remembered
//   */
//  public boolean isRememberUIdPwd() {
//    return rememberUIdPwd;
//  }
//
//
//  /**
//   * setter for userId property.
//   * @param userId user id
//   */
//  public void setUserId(byte[] userId) {
//    this.userId = userId;
//  }
//
//
//  /**
//   * getter of userId property.
//   * @return user id
//   */
//  public byte[] getUserId() {
//    return userId;
//  }
//
//
//  /**
//   * setter for password property.
//   * @param password user password.
//   */
//  public void setPassword(byte[] password) {
//    this.password = password;
//  }
//
//
//  /**
//   * getter for password property.
//   * @return user password.
//   */
//  public byte[] getPassword() {
//    return password;
//  }
//
//  /**
//   * setter for deleteJxta property.
//   * @param deleteJxta.
//   */
//  public void setDeleteJxta(boolean deleteJxta) {
//    this.deleteJxta = deleteJxta;
//  }
//
//  /**
//   * getter for deleteJxta property.
//   * @return deleteJxta true if jxta preferences changed.
//   */
//  public boolean isDeleteJxta() {
//    return deleteJxta;
//  }
//
//}
