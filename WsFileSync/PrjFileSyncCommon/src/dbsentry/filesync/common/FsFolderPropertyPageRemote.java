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
 * $Id: FsFolderPropertyPageRemote.java,v 1.10 2006/02/20 15:36:11 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import java.io.Serializable;

import java.util.Date;


/**
 *	To hold properties of remote folder.
 *  @author            Deepali Chitkulwar.
 *  @version           1.0
 * 	Date of creation:  13-07-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class

FsFolderPropertyPageRemote implements Serializable {
  private String name;

  private String oldFolderName;

  private String folderType;

  private String location;

  private long size;

  private int folderCount;

  private int fileCount;

  private FsPermissionHolder fsPermissionHolder;

  private Date creationDate;

  private Date modifiedDate;

  /**
   * To set the folder name.
   * @param name String to be set as name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * To get folder name.
   * @return name name of folder.
   */
  public String getName() {
    return name;
  }

  /**
   * To set the folderType.
   * @param folderType 
   */
  public void setFolderType(String folderType) {
    this.folderType = folderType;
  }

  /**
   * To get the folderType.
   * @return folderType 
   */
  public String getFolderType() {
    return folderType;
  }


  /**
   * To set the location(absolute path) of folder.
   * @param location absolute path String.
   */
  public void setLocation(String location) {
    this.location = location;
  }


  /**
   * To get folder Location.
   * @return absolute path of folder.
   */
  public String getLocation() {
    return location;
  }


  /**
   * To set the total size of folder.
   * @param size long total size of folder.
   */
  public void setSize(long size) {
    this.size = size;
  }


  /**
   * To get the total size of folder.
   * @return total size of folder.
   */
  public long getSize() {
    return size;
  }

  /**
   * To set the folderCount.
   * @param folderCount total folders inside the folder.
   */
  public void setFolderCount(int folderCount) {
    this.folderCount = folderCount;
  }


  /**
   * To get folderCount(total folders inside the folder).
   * @return no. of folders inside the folder.
   */
  public int getFolderCount() {

    return folderCount;
  }


  /**
   * To set the fileCount(total files inside the folder).
   * @param fileCount total files inside the folder.
   */
  public void setFileCount(int fileCount) {
    this.fileCount = fileCount;
  }


  /**
   * To get fileCount(total files in a folder).
   * @return no. of files inside the folder.
   */
  public int getFileCount() {
    return fileCount;
  }

  /**
   * To set the creationDate of folder.
   * @param creationDate creation Date of folder.
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * To get the creationDate of folder.
   * @return creation Date of folder.
   */
  public Date getCreationDate() {
    return creationDate;
  }


  /**
   * To set the modifiedDate of folder.
   * @param modifiedDate modification Date of folder.
   */
  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }


  /**
   * To get modifiedDate of folder.
   * @return modification Date of folder.
   */
  public Date getModifiedDate() {
    return modifiedDate;
  }


  /**
   * To set the fsPermissionHolder. 
   * @param fsPermissionHolder FsPermissionHolder containing permissions.
   */
  public void setFsPermissionHolder(FsPermissionHolder fsPermissionHolder) {
    this.fsPermissionHolder = fsPermissionHolder;
  }


  /**
   * To get fsPermissionHolder.
   * @return fsPermissionHolder which holds permissions of folder.
   */
  public FsPermissionHolder getFsPermissionHolder() {
    return fsPermissionHolder;
  }


  /**
   * To set the oldFolderName.
   * @param oldFolderName name of folder.
   */
  public void setOldFolderName(String oldFolderName) {
    this.oldFolderName = oldFolderName;
  }


  /**
   * To get the oldFolderName.
   * @return old name of folder.
   */
  public String getOldFolderName() {
    return oldFolderName;
  }

  /**
   * gives String representation of FsFolderPropertyPageRemote object.
   * @return String repersentation of FsFolderPropertyPageRemote object.
   */
  public String toString() {
    String strTemp = "";
    strTemp += "\n\t name : " + name;
    strTemp += "\n\t oldFolderName : " + oldFolderName;
    strTemp += "\n\t folderType : " + folderType;
    strTemp += "\n\t location : " + location;
    strTemp += "\n\t size : " + size;
    strTemp += "\n\t folderCount : " + folderCount;
    strTemp += "\n\t fileCount : " + fileCount;
    strTemp += "\n\t fsPermissionHolder : " + fsPermissionHolder;
    strTemp += "\n\t creationDate : " + creationDate;
    strTemp += "\n\t modifiedDate : " + modifiedDate;

    return strTemp;
  }
}
