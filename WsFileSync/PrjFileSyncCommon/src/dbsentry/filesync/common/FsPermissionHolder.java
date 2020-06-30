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
 * $Id: FsPermissionHolder.java,v 1.9 2006/02/20 15:36:19 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import java.io.Serializable;


/**
 * To create a serializable FsPermissionHolder object which could pass through a stream carrying 
 * permissions of documet(s) and folder(s).
 * @author            Jeetendra Prasad
 * @version           1.0
 * Date of creation:  12-07-2005
 * Last Modfied by :    
 * Last Modfied Date:   
 */
public class FsPermissionHolder implements Serializable {

  private String permissions[];

  /**
   * To set the permissions to the specified String array.
   * @param permissions String array to be set as permissions.
   */
  public void setPermissions(String[] permissions) {
    this.permissions = permissions;
  }

  /**
   * To get the stored permissions.
   * @return String array of permissions.
   */
  public String[] getPermissions() {
    return permissions;
  }

  /**
   * gives String representation of FsPermissionHolder object.
   * @return String repersentation of FsPermissionHolder object.
   */
  public String toString() {
    String strTemp = "";
    for (int index = 0; index < permissions.length; index++) {
      strTemp += "\n\t Permissions[ " + index + "] : " + permissions[index];
    }
    return strTemp;
  }
}
