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

 * $Id: UnixDataSource.java,v 1.3 2005/05/29 06:36:03 jeet Exp $

 *****************************************************************************

 */

package dbsentry.plugin.unix;

import dbsentry.filesync.filesystem.specs.FsConnection;

import java.io.PrintWriter;



public class UnixDataSource  {



  public UnixDataSource() {

  }



  public FsConnection getConnection() {

    return null;

  }



  public FsConnection getConnection(String username, String password) {

    return null;

  }



  public PrintWriter getLogWriter() {

    return null;

  }



  public void setLogWriter(PrintWriter out) {

  }  

}

