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
 * $Id: FsFileHolder.java,v 1.8 2006/02/20 15:36:19 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;


/**
 * Holds the file size ,mimeType and data of file,used specially in case of upload and download 
 * to transfer the contents of file in byte array.
 * @author              Jeetendra Prasad
 * @version             1.0
 * Date of creation:    08-05-2005
 * Last Modfied by :    Jeetendra Prasad
 * Last Modfied Date:   05-07-2005
 */
public class FsFileHolder extends FsObjectHolder {
  private long size;

  private String mimeType;

  private byte data[];

  /**
   * Retrieves the mimeType of associated file.
   * @return String mimeType.
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * Sets the mimeType to specified mime type String.
   * @param mimeType String to be set as mime type.
   */
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * Retrieves the size of associated file.
   * @return long file size.
   */
  public long getSize() {
    return size;
  }

  /**
   * Sets the size value to specified file size.
   * @param size long value to be set as size.
   */
  public void setSize(long size) {
    this.size = size;
  }

  /**
   * To retrieve the contents of associated file.
   * @return byte array containing the contents of file. 
   */
  public byte[] getData() {
    return data;
  }

  /**
   * To store the contents of file in byte array.
   * @param data byte array containing file contents.
   */
  public void setData(byte[] data) {
    this.data = data;
  }

  /**
   * gives String representation of FsFileHolder object.
   * @return String repersentation of FsFileHolder object.
   */
  public String toString() {
    return this.toString();
  }

}
