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
 * $Id: FsFolderHolder.java,v 1.6 2006/02/20 15:36:11 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;


public class FsFolderHolder extends FsObjectHolder {
  private Object[] items = new Object[0];

  private boolean hasSubDirectory;

  public String toString() {
    return getName();
  }

  public boolean hasSubDirectory() {
    return hasSubDirectory;
  }

  public void setHasSubDirectory(boolean hasSubDirectory) {
    this.hasSubDirectory = hasSubDirectory;
  }

  public void setItems(Object[] items) {
    this.items = items;
  }

  public Object[] getItems() {
    return items;
  }

}
