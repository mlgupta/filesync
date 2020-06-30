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
 * $Id: FsException.java,v 1.4 2006/02/20 15:37:38 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.filesystem.specs;

/**
 *	To convert various type of exceptions to single FsException.  
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public abstract class

FsException extends Throwable {
  /**
   * To get the error code associated with FsException.
   * @return integer errorCode of exception.
   */
  public abstract int getErrorCode();

  /**
   * To get the stack trace of corresponding exception.
   * @return String stackTrace of exception.
   */
  public abstract String getStackTraceString();

  /**
   * To get the message associated with this exception. 
   * @return String message.
   */
  public abstract String getMessage();
}
