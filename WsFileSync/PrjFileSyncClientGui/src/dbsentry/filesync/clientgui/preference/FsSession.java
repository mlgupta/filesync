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
 * $Id$
 *****************************************************************************
 */

package dbsentry.filesync.clientgui.preference;

import java.io.Serializable;

import java.util.Random;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @Date of creation:16-05-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public class FsSession implements Serializable{

  private String sessionName;
  
  private String peerId;
  
  private String pipeId;
  
  private String socketId;
  
  private String userId;
  
  private String password;
  
  public void setSessionName(String sessionName) {
    this.sessionName = sessionName;
  }

  public String getSessionName() {
    return sessionName;
  }

  
  public void setPeerId(String peerId) {
   this.peerId = peerId;
  }
  
  public String getPeerId() {
   return peerId;
  }
   
  public void setPipeId(String pipeId) {
    this.pipeId = pipeId;
  }

  public String getPipeId() {
    return pipeId;
  }

  public void setSocketId(String socketId) {
    this.socketId = socketId;
  }

  public String getSocketId() {
    return socketId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }
 
}
