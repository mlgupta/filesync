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

 * $Id: UnixFolder.java,v 1.3 2005/05/29 06:36:03 jeet Exp $

 *****************************************************************************

 */

package dbsentry.plugin.unix;

import dbsentry.filesync.filesystem.specs.FsFolder;

import dbsentry.filesync.filesystem.specs.FsObject;



public class UnixFolder extends UnixObject  {

  public UnixFolder() {

  }



  public FsFolder getParentFolder() {

    return null;

  }



  public FsObject[] listContentOfFolder() {

    return null;

  }



  public FsObject[] listContentOfHomeFolder() {

    return null;

  }



  public void setParentFolder(FsFolder parentFolder) {

  }  

}

