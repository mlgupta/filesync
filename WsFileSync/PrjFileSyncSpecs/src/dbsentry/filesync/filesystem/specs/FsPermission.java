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
 * $Id: FsPermission.java,v 1.6 2006/02/20 15:37:37 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.filesystem.specs;

/**
 *	To manipulate and access the permissions of folder or file.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public interface FsPermission {

  /**
   * Get permission list.
   * @return String array of permissions.
   */
  public String[] getPermissions();

  /**
   * Check if there is write permission.
   * @return canWrite true or false.
   */
  public boolean canWrite();

  /**
   * @param permissions
   */
  public void setPermissions(String[] permissions);

  /**
   * @param writeEnabled
   */
  public void setWrite(boolean writeEnabled);

}
