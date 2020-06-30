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
 * $Id: CmsdkDataSource.java,v 1.7 2006/02/20 15:37:43 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.plugin.cmsdk;

import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsDataSource;

import dms.beans.DbsCleartextCredential;
import dms.beans.DbsException;
import dms.beans.DbsLibraryService;
import dms.beans.DbsLibrarySession;

/**
 *	Purpose : To establish a connection with cmsdk database.
 *  @author : Jeetendra Prasad
 *  @version: 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */


/**
 *	Purpose : To establish a connection with cmsdk database.
 *  @author : Jeetendra Prasad
 *  @version: 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class CmsdkDataSource implements FsDataSource {
  private DbsLibraryService dbsLibraryService;


  /**
   * @param username
   * @param password
   * @return cmsdkConnection
   * @throws CmsdkException
   */
  public FsConnection getConnection(String username, String password) throws CmsdkException {
    CmsdkConnection cmsdkConnection;
    DbsCleartextCredential dbsCleartextCredential;
    DbsLibrarySession dbsLibrarySession;
    try {
      dbsCleartextCredential = new DbsCleartextCredential(username, password);
      dbsLibrarySession = dbsLibraryService.connect(dbsCleartextCredential, null);
      cmsdkConnection = new CmsdkConnection();
      cmsdkConnection.setDbsLibrarySession(dbsLibrarySession);
    } catch (DbsException dex) {
      dex.printStackTrace();
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    } catch (Exception ex) {
      ex.printStackTrace();
      CmsdkException cmsdkException = new CmsdkException(ex);
      throw cmsdkException;
    }
    return cmsdkConnection;
  }

  public void setDbsLibraryService(DbsLibraryService dbsLibraryService) {
    this.dbsLibraryService = dbsLibraryService;
  }
}

