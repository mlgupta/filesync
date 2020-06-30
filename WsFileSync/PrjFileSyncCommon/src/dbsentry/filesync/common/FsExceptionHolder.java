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
 * $Id: FsExceptionHolder.java,v 1.7 2006/02/20 15:36:19 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import java.io.Serializable;


/**
 *	To create holder which holds information regarding an exception such as errorCode,errorMessage. 
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    07-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class FsExceptionHolder implements Serializable {

  public static int INVALID_USER_CODE = 50000;

  public static String INVALID_USER_MSG = "Invalid userid/password";

  private int errorCode;

  private String errorMessage;

  /**
   * retrieves the errorCode.
   * @return integer error code.
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * sets the errorCode to specified value.
   * @param errorCode integer value to be set as errorCode.
   */
  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * retrieves the errorMessage.
   * @return errorMessage string .
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * sets the errorMessage to speccified string.
   * @param errorMessage string to be set as errorMessage.
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * gives String representation of FsExceptionHolder object.
   * @return String repersentation of FsExceptionHolder object.
   */
  public String toString() {
    String strTemp = "";
    strTemp += "\n\terrorCode : " + errorCode;
    strTemp += "\n\terrorMessage : " + errorMessage;

    return strTemp;
  }

}
