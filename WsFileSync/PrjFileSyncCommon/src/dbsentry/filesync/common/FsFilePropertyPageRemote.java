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
 * $Id: FsFilePropertyPageRemote.java,v 1.9 2006/02/20 15:36:11 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import java.io.Serializable;

import java.util.Date;


/**
 * Constructs a serializable object which holds the properties of file such as name,type,location,
 * size,permissions,creation and modification date.
 * @author             Deepali Chitkulwar.
 * @version            1.0
 * Date of creation:   08-05-2005
 * Last Modfied by :    
 * Last Modfied Date: 
 */
public class

FsFilePropertyPageRemote implements Serializable {

  private String name;

  private String oldName;

  private String fileType;

  private String location;

  private long size;

  private Date creationDate;

  private Date modifiedDate;

  private FsPermissionHolder fsPermissionHolder;

  /**
   * Sets the name to specified file name.
   * @param name String file name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the name of file.
   * @return file name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the fileType to the specified mimetype.
   * @param fileType String mime type.
   */
  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  /**
   * Retrieves the fileType holding mime type of file.
   * @return file mimeType .
   */
  public String getFileType() {
    return fileType;
  }

  /**
   * Sets the location to specified absolute path.
   * @param location absolute path.
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Retrieves the absolute path of file.
   * @return absolute file path.
   */
  public String getLocation() {
    return location;
  }

  /**
   * Sets the size to specified long value.
   * @param size file size.
   */
  public void setSize(long size) {
    this.size = size;
  }

  /**
   * Retrieves the size of file.
   * @return size of file.
   */
  public long getSize() {
    return size;
  }

  /**
   * Sets the creationDate to specified Date.
   * @param creationDate creation date of file.
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Retrieves the creationDate of file.
   * @return creationDate of file.
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * Sets the modifiedDate to specified Date.  
   * @param modifiedDate modification date of file.
   */
  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * Retrieves the modifiedDate of file.
   * @return modifiedDate of file.
   */
  public Date getModifiedDate() {
    return modifiedDate;
  }

  /**
   * Sets the fsPermissionHolder.
   * @param fsPermissionHolder FsPermissionHolder object to be set.
   */
  public void setFsPermissionHolder(FsPermissionHolder fsPermissionHolder) {
    this.fsPermissionHolder = fsPermissionHolder;
  }

  /**
   * Retrieves fsPermissionHolder.
   * @return FsPermissionHolder objectcontaining permissions.
   */
  public FsPermissionHolder getFsPermissionHolder() {
    return fsPermissionHolder;
  }

  /**
   * Sets the old file name.
   * @param oldName file name.
   */
  public void setOldName(String oldName) {
    this.oldName = oldName;
  }

  /**
   * Retrieves the old file name.
   * @return old name of file.
   */
  public String getOldName() {
    return oldName;
  }

  /**
   * gives String representation of FsFilePropertyPageRemote object.
   * @return String repersentation of FsFilePropertyPageRemote object.
   */
  public String toString() {
    String strTemp = "";
    strTemp += "\n\t name : " + name;
    strTemp += "\n\t oldName : " + oldName;
    strTemp += "\n\t fileType : " + fileType;
    strTemp += "\n\t location : " + location;
    strTemp += "\n\t size : " + size;
    strTemp += "\n\t creationDate : " + creationDate;
    strTemp += "\n\t modifiedDate : " + modifiedDate;
    strTemp += "\n\t fsPermissionHolder : " + fsPermissionHolder;

    return strTemp;
  }
}
