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
 * $Id: UnixFile.java,v 1.2 2005/04/02 14:55:53 manish Exp $
 *****************************************************************************
 */
package dbsentry.plugin.unix;
import java.io.InputStream;

public class UnixFile extends UnixObject  {

  int size;
  String mimeType;
  InputStream inputStream;
  
  public UnixFile() {
  }
  
  public InputStream getInputStream() {
    return this.inputStream;
  }

  public String getMimeType() {
    return mimeType;
  }

  public int getSize() {
    return size;
  }  
}
