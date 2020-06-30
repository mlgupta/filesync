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
 * $Id: FsUserInfo.java,v 1.10 2006/08/01 10:14:26 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.filesystem.specs.FsConnection;

import java.io.Serializable;

import java.util.Date;


/**
 *  Stores the current user information who logged in, which includes userid logginTime.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation:   04-07-2005
 * 	Last Modfied by :   saurabh Gupta  
 * 	Last Modfied Date:  06-07-2006 
 */
public class FsUserInfo implements Serializable {
  private String userid;

  private Date logginTime;

  private FsConnection fsConnection;

  /**
   * Sets the userid to the specified string.
   * @param userid string userid entered by the user.
   */
  public void setUserid(String userid) {
    this.userid = userid;
  }


  /**
   * Gets the userid of current user.
   * @return the userid of the current user.
   */
  public String getUserid() {
    return userid;
  }

  /**
   * Sets the logginTime to the specified Date object ,holding the login date of user.
   * @param logginTime the Date object specifying when the user logged in.
   */
  public void setLogginTime(Date logginTime) {
    this.logginTime = logginTime;
  }


  /**
   * Gets the login date of user.
   * @return the login date of user.
   */
  public Date getLogginTime() {
    return logginTime;
  }

  public void setFsConnection(FsConnection fsConnection) {
    this.fsConnection = fsConnection;
  }

  public FsConnection getFsConnection() {
    return fsConnection;
  }

  /**
   * gives String representation of FsUserInfo object.
   * @return String repersentation of FsUserInfo object.
   */
  public String toString() {
    return this.toString();
  }

}
