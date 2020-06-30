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
 * $Id: CmsdkConnectionFactory.java,v 1.6 2006/02/20 15:37:43 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.plugin.cmsdk;

import dbsentry.filesync.filesystem.specs.FsDataSource;
import dbsentry.filesync.filesystem.specs.FsException;

import dms.beans.DbsException;
import dms.beans.DbsLibraryService;

public abstract class CmsdkConnectionFactory {
  public static FsDataSource getDataSource(String ifsService, String ifsSchemaPassword, String serviceConfiguration,
                                           String domain) throws FsException, Exception {
    CmsdkDataSource cmsdkDataSource = new CmsdkDataSource();
    DbsLibraryService dbsLibraryService;
    try {
      if (DbsLibraryService.isServiceStarted(ifsService)) {
        dbsLibraryService = DbsLibraryService.findService(ifsService);
      } else {
        dbsLibraryService =
          DbsLibraryService.startService(ifsService, ifsSchemaPassword, serviceConfiguration, domain);
      }
      cmsdkDataSource.setDbsLibraryService(dbsLibraryService);

    } catch (DbsException dex) {
      throw new CmsdkException(dex);
    } catch (Exception ex) {
      throw ex;
    }
    return cmsdkDataSource;
  }
}
