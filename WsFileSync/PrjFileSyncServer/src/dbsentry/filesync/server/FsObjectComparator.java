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
 * $Id: FsObjectComparator.java,v 1.6 2006/02/20 15:36:35 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.server;

import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;

import java.util.Comparator;


/**
 *	Implements a comparator that is used in sorting objects by name. 
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation:    7-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class FsObjectComparator implements Comparator {

  /**
   * Compares two objects by names.
   * @param  a the first object to be compared. 
   * @param  b the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first argument is less
   * than, equal to, or greater than the second
   */
  public int compare(Object a, Object b) {
    FsObject fsObjectA = (FsObject)a;
    FsObject fsObjectB = (FsObject)b;
    if (fsObjectA instanceof FsFolder && fsObjectB instanceof FsFolder) {
      return fsObjectA.getName().compareToIgnoreCase(fsObjectB.getName());
    } else if (fsObjectA instanceof FsFile && fsObjectB instanceof FsFile) {
      return fsObjectA.getName().compareToIgnoreCase(fsObjectB.getName());
    } else if (fsObjectA instanceof FsFolder && fsObjectB instanceof FsFile) {
      return -1;
    } else {
      return 1;
    }
  }

  /**
   * gives String representation of FsObjectComparator object.
   * @return String repersentation of FsObjectComparator object.
   */
  public String toString() {
    return this.toString();
  }
}
