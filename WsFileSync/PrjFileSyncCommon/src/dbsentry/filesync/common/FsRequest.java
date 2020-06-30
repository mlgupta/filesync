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
 * $Id: FsRequest.java,v 1.10 2006/02/20 15:36:19 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;


/**
 * To create a serializable FsRequest object which could pass through a stream carrying 
 * clients request.
 * @author            Jeetendra Prasad
 * @version           1.0
 * Date of creation:  6-05-2005
 * Last Modfied by :    
 * Last Modfied Date:   
 */
public class FsRequest extends FsMessage {
  private int requestCode;

  private String superRequestCode;

  /**
   * To get the request code.
   * @return requestCode integer requestCode.
   */
  public int getRequestCode() {
    return requestCode;
  }

  /**
   * To set the requestCode to the specified integer value.
   * @param requestCode integer to be set as requestCode.
   */
  public void setRequestCode(int requestCode) {
    this.requestCode = requestCode;
  }

  /**
   * To set the superRequestCode.
   * @param superRequestCode String to be set as superRequestCode.
   */
  public void setSuperRequestCode(String superRequestCode) {
    this.superRequestCode = superRequestCode;
  }

  /**
   * To get the superRequestCode.
   * @return superRequestCode String superRequestCode.
   */
  public String getSuperRequestCode() {
    return superRequestCode;
  }

  /**
   * gives String representation of FsRequest object.
   * @return String repersentation of FsRequest object.
   */
  public String toString() {
    String strTemp = "";
    strTemp += "\n\t requestCode : " + requestCode;
    strTemp += "\n\t superRequestCode : " + superRequestCode;
    return strTemp;
  }
}
