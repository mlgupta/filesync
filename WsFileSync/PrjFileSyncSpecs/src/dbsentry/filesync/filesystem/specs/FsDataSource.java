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
 * $Id: FsDataSource.java,v 1.4 2006/02/20 15:37:36 sudheer Exp $
 *****************************************************************************
 */

package dbsentry.filesync.filesystem.specs;

public interface FsDataSource {
  /**
   * To get a connection with cmsdk database.
   * @param  username
   * @param  password
   * @return FsConnection object.
   * @throws dbsentry.filesync.filesystem.specs.FsException
   */
  public FsConnection getConnection(String username, String password) throws FsException;

}
