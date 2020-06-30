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
 * $Id: CmsdkPermission.java,v 1.10 2006/02/20 15:37:43 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.plugin.cmsdk;

import dbsentry.filesync.filesystem.specs.FsPermission;


/**
 *	To manipulate and access the permissions of CmsdkObject.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class CmsdkPermission implements FsPermission {
  private String[] permissions;

  private boolean writeEnabled = false;

  /**
   * To get array of permission strings.
   * @return string array of permissions.
   */
  public String[] getPermissions() {
    return permissions;
  }

  /**
   * To set array of permission strings as permissions.
   * @return permissions String array of permissions.
   */
  public void setPermissions(String[] permissions) {
    this.permissions = permissions;
  }

  /**
   * To check if write permission is granted.
   * @return boolean canwrite true/false.
   */
  public boolean canWrite() {
    return writeEnabled;
  }

  /**
   * sets the write permission.
   * @param writeEnabled boolean writeEnabled true/false.
   */
  public void setWrite(boolean writeEnabled) {
    this.writeEnabled = writeEnabled;
  }

  /**
    * Returns a string representation of the CmsdkPermission object.
    * @return a string representation of the CmsdkPermission object.   
    */
  public String toString() {
    return this.toString();
  }

}
