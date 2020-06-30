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
 * $Id: TotalInfoFoldersFiles.java,v 1.7 2006/02/20 15:37:25 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.client;

/**
 *	its a bean to hold info about folder and file.
 *  @author              Deepali Chitkulwar
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Deepali Chitkulwar
 * 	Last Modfied Date:   06-07-2005
 */
public class TotalInfoFoldersFiles {
  private long size;

  private int folderCount;

  private int fileCount;

  private int folderFileCount;

  private Integer readOnly = null;

  /**
   * a constructor for TotalInfoFoldersFiles.
   */
  public TotalInfoFoldersFiles() {
  }

  /**
   * a getter for size.
   * @return size
   */
  public long getSize() {
    return size;
  }

  /**
   * a setter for size.
   * @param newSize size
   */
  public void setSize(long newSize) {
    size = newSize;
  }

  /**
   * a getter for folder count.
   * @return folder count
   */
  public int getFolderCount() {
    return folderCount;
  }

  /**
   * a setter for folder count.
   * @param newFolderCount folder count
   */
  public void setFolderCount(int newFolderCount) {
    folderCount = newFolderCount;
  }

  /**
   * a getter for file count.
   * @return file count
   */
  public int getFileCount() {
    return fileCount;
  }

  /**
   * a setter for file count.
   * @param newFileCount file count
   */
  public void setFileCount(int newFileCount) {
    fileCount = newFileCount;
  }

  /**
   * getter for folder file count.
   * @return folder file count
   */
  public int getFolderFileCount() {
    return folderFileCount;
  }

  /**
   * setter for folder file count.
   * @param newFolderFileCount folder file count value
   */
  public void setFolderFileCount(int newFolderFileCount) {
    folderFileCount = newFolderFileCount;
  }

  /**
   * set to true if all file are read only.
   * @param readOnly integer value indication if all file are read only
   */
  public void setReadOnly(Integer readOnly) {
    this.readOnly = readOnly;
  }

  /**
   * setter for read only property.
   * @return integer integer value indication if all file are read only
   */
  public Integer getReadOnly() {
    return readOnly;
  }
}
