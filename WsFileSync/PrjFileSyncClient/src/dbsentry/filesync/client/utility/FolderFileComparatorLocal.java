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
 * $Id: FolderFileComparatorLocal.java,v 1.6 2006/02/20 15:37:04 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.client.utility;

import java.io.File;

import java.util.Comparator;


/**
 *	Implements a comparator that is used in sorting local files and folders 
 *  by name. 
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation:    6-05-2005
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   06-07-2005
 */
public class FolderFileComparatorLocal implements Comparator {

  /**
   * Compares two File or Folder objects by names.
   * @param a object to compare
   * @param b object to compare
   * @return integer value.
   */
  public int compare(Object a, Object b) {
    File fileA = (File)a;
    File fileB = (File)b;
    if (fileA.isDirectory() && fileB.isDirectory()) {
      return fileA.getName().compareToIgnoreCase(fileB.getName());
    } else if (fileA.isFile() && fileB.isFile()) {
      return fileA.getName().compareToIgnoreCase(fileB.getName());
    } else if (fileA.isDirectory() && fileB.isFile()) {
      return -1;
    } else {
      return 1;
    }
  }

}
