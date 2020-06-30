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

 * $Id: UnixObject.java,v 1.3 2005/05/29 06:36:03 jeet Exp $

 *****************************************************************************

 */

package dbsentry.plugin.unix;

import dbsentry.filesync.filesystem.specs.FsPermission;

import java.util.Date;





public class UnixObject  {



  String name;

  String path;

  Date modifiedDate;

  Date creationDate;

  String owner;

  FsPermission permission;



  public UnixObject() {

  }

  

  public Date getCreationDate() {

    return creationDate;

  }



  public void setCreationDate(Date creationDate) {

    this.creationDate = creationDate;

  }



  public Date getModifiedDate() {

    return modifiedDate;

  }



  public void setModifiedDate(Date modifiedDate) {

    this.modifiedDate = modifiedDate;

  }



  public String getName() {

    return name;

  }



  public void setName(String name) {

    this.name = name;

  }



  public String getOwner() {

    return owner;

  }



  public void setOwner(String owner) {

    this.owner = owner;

  }



  public String getPath() {

    return path;

  }



  public void setPath(String path) {

    this.path = path;

  }



  public FsPermission getPermission() {

    return permission;

  }



  public void setPermission(FsPermission permission) {

    this.permission = permission;

  }  

}

