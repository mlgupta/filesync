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
 * $Id: FsFileFolderPropertyPageRemote.java,v 1.7 2006/02/20 15:36:19 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import java.io.Serializable;


/**
 *	To create a Serializable object which holds information regarding a set of remote files and folders,
 *  such as no.of files and folders ,type ,location. 
 *  @author              Deepali Chitkulwar.
 *  @version             1.0
 * 	Date of creation:    07-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class FsFileFolderPropertyPageRemote implements Serializable {
  private int noOfFiles;

  private int noOfFolders;

  private String type;

  private String location;

  private long size;

  /**
   * sets the noOfFiles to specified value.
   * @param noOfFiles integer value to be set as noOfFiles.
   */
  public void setNoOfFiles(int noOfFiles) {
    this.noOfFiles = noOfFiles;
  }


  /**
   * retrieves the noOfFiles.
   * @return integer value containing noOfFiles.
   */
  public int getNoOfFiles() {
    return noOfFiles;
  }


  /**
   * sets the noOfFolders to specified value.
   * @param noOfFolders integer value to be set as noOfFolders.
   */
  public void setNoOfFolders(int noOfFolders) {
    this.noOfFolders = noOfFolders;
  }


  /**
   * retrieves the noOfFolders.
   * @return integer value containing noOfFolders.
   */
  public int getNoOfFolders() {
    return noOfFolders;
  }


  /**
   * sets the type to specified String.
   * @param type String to be set as type.
   */
  public void setType(String type) {
    this.type = type;
  }


  /**
   * retrieves the type.
   * @return String type.
   */
  public String getType() {
    return type;
  }


  /**
   * sets the location to specified String.
   * @param location absolute path string.
   */
  public void setLocation(String location) {
    this.location = location;
  }


  /**
   * retrieves the absolute path.
   * @return absolute path String
   */
  public String getLocation() {
    return location;
  }


  /**
   * sets the total size to specified size value.
   * @param size total size of set of files and folders.
   */
  public void setSize(long size) {
    this.size = size;
  }


  /**
   * retrieves the total size.
   * @return long total size.
   */
  public long getSize() {
    return size;
  }

  /**
   * gives String representation of FsFileFolderPropertyPageRemote object.
   * @return String repersentation of FsFileFolderPropertyPageRemote object.
   */
  public String toString() {
    String strTemp = "";
    strTemp += "\n\tnoOfFiles : " + noOfFiles;
    strTemp += "\n\ttype : " + type;
    strTemp += "\n\tlocation : " + location;
    strTemp += "\n\tsize : " + size;

    return strTemp;
  }

}
