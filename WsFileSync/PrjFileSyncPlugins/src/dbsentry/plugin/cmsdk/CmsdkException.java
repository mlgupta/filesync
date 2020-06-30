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
 * $Id: CmsdkException.java,v 1.8 2006/02/20 15:37:43 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.plugin.cmsdk;

import dbsentry.filesync.filesystem.specs.FsException;

import dms.beans.DbsException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


/**
 *	To convert various type of exceptions to single CmsdkException.  
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class CmsdkException extends FsException {

  private int errorCode;

  private String stackTrace;

  private String message;

  /**
   * To encapsulate DbsException 
   * @param dex DbsException object.
   */
  public CmsdkException(DbsException dex) {
    errorCode = dex.getErrorCode();
    ByteArrayOutputStream baos = null;
    PrintWriter pw = null;
    try {
      baos = new ByteArrayOutputStream();
      pw = new PrintWriter(baos);
      dex.printStackTrace(pw);
      pw.flush();
      baos.flush();
      stackTrace = baos.toString();
      message = dex.getMessage();
    } catch (Exception e) {
      ;
    } finally {
      try {
        if (pw != null) {
          pw.close();
        }
        if (baos != null) {
          baos.close();
        }
      } catch (IOException ioe) {
        ;
      }
    }

  }

  /**
   * To encapsulate generic exception 
   * @param ex generic exception object.
   */
  public CmsdkException(Exception ex) {
    errorCode = 0;
    ByteArrayOutputStream baos = null;
    PrintWriter pw = null;
    try {
      baos = new ByteArrayOutputStream();
      pw = new PrintWriter(baos);
      ex.printStackTrace(pw);
      pw.flush();
      baos.flush();
      stackTrace = baos.toString();
      message = ex.getMessage();
    } catch (Exception e) {
      ;
    } finally {
      try {
        if (pw != null) {
          pw.close();
        }
        if (baos != null) {
          baos.close();
        }
      } catch (IOException ioe) {
        ;
      }
    }


  }

  /**
   * To get the error code associated with CmsdkException.
   * @return integer errorCode.
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * To get the stack trace of corresponding exception.
   * @return String stackTrace.
   */
  public String getStackTraceString() {
    return stackTrace;
  }

  /**
   * To get the message associated with corresponding exception.
   * @return String message.
   */
  public String getMessage() {
    return message;
  }
}
