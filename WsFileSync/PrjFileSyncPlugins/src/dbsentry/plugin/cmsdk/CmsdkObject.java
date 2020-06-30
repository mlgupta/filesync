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
 * $Id: CmsdkObject.java,v 1.10 2006/08/01 10:14:07 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.plugin.cmsdk;

import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;
import dbsentry.filesync.filesystem.specs.FsPermission;

import java.util.Date;


/**
 * To manipulate the  cmsdk file or folder object.
 * @author Jeetendra Prasad
 * @version 1.0
 * Date of creation: 10-05-2005
 * Last Modfied by :    
 * Last Modfied Date:   
 */
public abstract class CmsdkObject implements FsObject {

  private String name;

  private Long id;

  private String path;

  private Date modifiedDate;

  private Date creationDate;

  private String owner;

  private String description;

  private FsPermission permission;

  private FsFolder fsFolder;

  /**
   * To get the creationDate of this CmsdkObject.  
   * @return Date object containing creationDate.
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * To set the creation date of this CmsdkObject.
   * @param creationDate Date object containing creationDate to be set.
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * To get the modifiedDate of this CmsdkObject.  
   * @return Date object containing modifiedDate.
   */
  public Date getModifiedDate() {
    return modifiedDate;
  }

  /**
   * To set the modifiedDate of this CmsdkObject.  
   * @param modifiedDate Date object to be set as modified date.
   */
  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * To get the name of this CmsdkObject.  
   * @return String name 
   */
  public String getName() {
    return name;
  }

  /**
   * To set the name of this CmsdkObject.
   * @param name String name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * To get the Id of this CmsdkObject.  
   * @return long id of CmsdkObject.
   */
  public Long getId() {
    return id;
  }


  /**
   * @param id
   */
  public void setId(Long id) {
    this.id = id;
  }


  /**
   * To get the owner of this CmsdkObject.
   * @return owner String owner of this object.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * To set the owner of this CmsdkObject.
   * @param owner String owner
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * To get the absolute path of this CmsdkObject.
   * @return absolute path String.
   */
  public String getPath() {
    return path;
  }

  /**
   * To set the path of this Cmsdk object.
   * @param path absolute path string.
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * To get the FsPermission object holding the permissions of this CmsdkObject.
   * @return FsPermission object holding permissions.
   */
  public FsPermission getPermission() {
    return permission;
  }

  /**
   * To set the permissions of CmsdkObject.
   * @param permission FsPermission object containing permissions to be set.
   */
  public void setPermission(FsPermission permission) {
    this.permission = permission;
  }

  /**
   * To get the description of this CmsdkObject.
   * @return String description
   */
  public String getDescription() {
    return description;
  }

  /**
   * To set the description of CmsdkObject.
   * @param description description String to be set.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * To get the parent folder of this CmsdkObject.
   * @return fsFolder FsFolder object representing the parent of this CmsdkObject.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to find the parent.
   */
  public FsFolder getParent() throws CmsdkException {
    return fsFolder;
  }

  /**
   * Purpose To update the modifiedDate.
   * @param  date new modifiedDate.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to update the modifiedDate. 
   */
  public abstract void updateModifiedDate(Date date) throws CmsdkException;

  /**
   * To update the CreationDate.
   * @param date new CreationDate.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to modify the CreationDate.
   */
  public abstract void updateCreationDate(Date date) throws CmsdkException;

  /**
    * Returns a string representation of the CmsdkObject object.
    * @return a string representation of the CmsdkObject object.   
    */
  public String toString() {
    return name;
  }

  /**
   * @param fsFolder
   */
  public void setFsFolder(FsFolder fsFolder) {
    this.fsFolder = fsFolder;
  }

  /**
   * @return
   */
  public FsFolder getFsFolder() {
    return fsFolder;
  }
}
