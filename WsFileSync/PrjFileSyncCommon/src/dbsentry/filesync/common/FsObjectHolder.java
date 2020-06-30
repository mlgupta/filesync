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
 * $Id: FsObjectHolder.java,v 1.7 2006/02/20 15:36:11 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import dbsentry.filesync.common.FsFolderHolder;

import java.io.Serializable;

import java.util.Date;


/**
 * To create a serializable FsObjectHolder object which could pass through a stream carrying 
 * information of documet/folder .
 * @author            Jeetendra Prasad
 * @version           1.0
 * Date of creation:  12-07-2005
 * Last Modfied by :    
 * Last Modfied Date:   
 */
public class

FsObjectHolder implements Serializable, Comparable {
  private String name;

  private String path;

  private Date modifiedDate;

  private String owner;

  private String description;

  private Date creationDate;

  private FsPermissionHolder permission;

  /**
   * To get the creationDate. 
   * @return stored creationDate.
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * To set the creationDate to specified Date.
   * @param creationDate Date to be set as creationDate.
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * To get the modifiedDate.
   * @return stored modifiedDate.
   */
  public Date getModifiedDate() {
    return modifiedDate;
  }

  /**
   * To set the modifiedDate to specified Date.
   * @param modifiedDate Date to be set as modifiedDate.
   */
  public void setModifiedDate(Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * To get the name of document/folder whose information is being held by this object.
   * @return String name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name to the specified String.
   * @param name String to be set as name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * To get the owner of object(document/folder) whose info. is being held by this object.
   * @return String owner.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * Sets the owner to the specified String.
   * @param owner String to be set as owner.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * To get the absolute path of object(document/folder) which is contained by this object.
   * @return absolute path String.
   */
  public String getPath() {
    return path;
  }

  /**
   * To set the path to specified String.
   * @param path absolute path String.
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * To get the permission.
   * @return FsPermissionHolder object containing permissions.
   */
  public FsPermissionHolder getPermission() {
    return permission;
  }

  /**
   * To set the permission to specified FsPermissionHolder object.
   * @param permission FsPermissionHolder object.
   */
  public void setPermission(FsPermissionHolder permission) {
    this.permission = permission;
  }

  /**
   * To get the description of object(document/folder) which is 
   * contained by this object.
   * @return String containing description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description to the specified String.
   * @param description String to be set as description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Compares the two FsObjectHolder(FsFolderHolder/FsFileHolder) objects 
   * by name.
   * @param o FsObjectHolder object to be compared with this object.
   * @return comparision result if both objects are instance of 
   * same class FsFileHolder or FsFolderHolder.If this object is instance 
   * of FsFolderHolder and comparision object is of type FsFileHolder
   * then it returns 1, else returns -1.
   */
  public int compareTo(Object o) {
    FsObjectHolder fsObjectHolder = (FsObjectHolder)o;
    if (this instanceof FsFolderHolder && fsObjectHolder instanceof FsFolderHolder) {
      return this.getName().compareToIgnoreCase(fsObjectHolder.getName());
    } else if (this instanceof FsFileHolder && fsObjectHolder instanceof FsFileHolder) {
      return this.getName().compareToIgnoreCase(fsObjectHolder.getName());
    } else if (this instanceof FsFolderHolder && fsObjectHolder instanceof FsFileHolder) {
      return 1;
    } else {
      return -1;
    }
  }

  /**
   * gives String representation of FsObjectHolder object.
   * @return String repersentation of FsObjectHolder object.
   */
  public String toString() {
    String strTemp = "";
    strTemp += "\n\t name : " + name;
    strTemp += "\n\t modifiedDate : " + modifiedDate;
    strTemp += "\n\t owner : " + owner;
    strTemp += "\n\t descripiton : " + description;
    strTemp += "\n\t creationDate : " + creationDate;

    return strTemp;
  }
}
