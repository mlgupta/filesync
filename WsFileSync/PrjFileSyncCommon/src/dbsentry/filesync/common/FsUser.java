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
 * $Id: FsUser.java,v 1.7 2006/02/20 15:36:11 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import java.io.Serializable;


/**
 *	Creates a serializable object whisch holds userid and password which can pass through stream, to
 *  authenticate user remotely.
 *  @author             Jeetendra Prasad
 *  @version            1.0
 * 	Date of creation:    6-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class FsUser implements Serializable {
  private String userId;

  private String userPassword;

  private String fileSystem;

  /**
   * To retrieve userId.
   * @return userId stored userId.
   */
  public String getUserId() {
    return userId;
  }

  /**
   * To set userid to specified String.
   * @param userId String to be set as userId.
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * To get userPassword.
   * @return userPassword stored userPassword.
   */
  public String getUserPassword() {
    return userPassword;
  }

  /**
   * To set userPassword to specified String.
   * @param userPassword String to be set as userPassword.
   */
  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public void setFileSystem(String fileSystem) {
    this.fileSystem = fileSystem;
  }

  public String getFileSystem() {
    return fileSystem;
  }
}
