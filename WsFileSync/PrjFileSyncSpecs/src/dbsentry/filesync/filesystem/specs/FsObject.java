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
 * $Id: FsObject.java,v 1.5 2006/02/20 15:37:37 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.filesystem.specs;

import java.util.Date;


/**
 * To manipulate the  file or folder object.
 * @author Jeetendra Prasad
 * @version 1.0
 * Date of creation: 10-05-2005
 * Last Modfied by :    
 * Last Modfied Date:   
 */
public interface FsObject {
  /**
   * To get the permission of this file/folder object.
   * @return FsPermission permission 
   */
  public FsPermission getPermission();

  /**
   * To get the name of this object.
   * @return String name.
   */
  public String getName();

  /**
   * To get the Id of this object.
   * @return long Document/Folder Id.
   */
  public Long getId();

  /**
   * To get the path of this object.
   * @return String path
   */
  public String getPath();

  /**
   * To get the modified date of this object.
   * @return Date modifiedDate
   */
  public Date getModifiedDate();

  /**
   * Updates modification date of this object.
   * @param date Date with which the modified date is to be updated.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to update the 
   * modified date.
   */
  public void updateModifiedDate(Date date) throws FsException;

  /**
   * To get the creation date of this object.
   * @return Date creationDate
   */
  public Date getCreationDate();

  /**
   * To update the creation date of this object.
   * @param date Date with which the creation date is to be updated.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to update the 
   * creation date.
   */
  public void updateCreationDate(Date date) throws FsException;

  /**
   * To get the owner of this object.
   * @return String owner
   */
  public String getOwner();

  /**
   * To get the description of this object.
   * @return String Description
   */
  public String getDescription();

  /**
   * To get the parent folder of this object.
   * @return parent FsFolder object.
   * @throws dbsentry.filesync.filesystem.specs.FsException  if unable to find the parent.
   */
  public FsFolder getParent() throws FsException;

  /**
   * To set the permission of this object.
   * @param permission FsPermission object containing permission.
   */
  public void setPermission(FsPermission permission);

}
