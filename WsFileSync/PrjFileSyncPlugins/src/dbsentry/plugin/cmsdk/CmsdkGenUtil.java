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
 * $Id: CmsdkGenUtil.java,v 1.9 2006/02/20 15:37:43 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.plugin.cmsdk;

import dms.beans.DbsAccessControlList;
import dms.beans.DbsAccessLevel;
import dms.beans.DbsDocument;
import dms.beans.DbsException;
import dms.beans.DbsFolder;
import dms.beans.DbsFormat;
import dms.beans.DbsLibrarySession;
import dms.beans.DbsPublicObject;


/**
 *	To encapsulate database object into Cmsdk object.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 */
public class CmsdkGenUtil {

  /**
   * To construct CmsdkFolder object from given public object(DbsFolder object).
   * @param dbsLibrarySession DbsLibrarySession object.
   * @param dbsPublicObject out of which CmsdkFolder object is to be constructed.
   * @return cmsdkFolder object representing specified DbsPublicObject.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to construct a CmsdkFolder object.
   */
  public static CmsdkFolder constructCmsdkFolder(DbsLibrarySession dbsLibrarySession,
                                                 DbsPublicObject dbsPublicObject) throws CmsdkException {
    try {
      DbsFolder dbsFolder = (DbsFolder)dbsPublicObject;
      CmsdkFolder cmsdkFolder = new CmsdkFolder(dbsLibrarySession);
      cmsdkFolder.setName(dbsFolder.getName());
      cmsdkFolder.setId(dbsFolder.getId());
      cmsdkFolder.setPath(dbsFolder.getAnyFolderPath());
      cmsdkFolder.setOwner(dbsFolder.getOwner().getName());
      cmsdkFolder.setCreationDate(dbsFolder.getCreateDate());
      cmsdkFolder.setModifiedDate(dbsFolder.getLastModifyDate());
      cmsdkFolder.setDescription(dbsFolder.getDescription());
      cmsdkFolder.setPath(dbsFolder.getAnyFolderPath());

      cmsdkFolder.setPermission(CmsdkGenUtil.getCmsdkPermission(dbsLibrarySession, dbsFolder));

      return cmsdkFolder;
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To get the CmsdkPermission object which holds permissions of specified publicObject.
   * @param dbsLibrarySession DbsLibrarySession object.
   * @param dbsPublicObject whose permission is to be fetched.
   * @return cmsdkPermission constructed CmsdkPermission object.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to get the permissioons of 
   * specified publicObject.
   */
  public static CmsdkPermission getCmsdkPermission(DbsLibrarySession dbsLibrarySession,
                                                   DbsPublicObject dbsPublicObject) throws CmsdkException {
    CmsdkPermission cmsdkPermission = new CmsdkPermission();
    try {
      DbsAccessControlList dbsAccessControlList = dbsPublicObject.getAcl();
      String permissions[] = { dbsAccessControlList.getName() };
      DbsAccessLevel dbsAccessLevel = dbsAccessControlList.getGrantedAccessLevel();
      cmsdkPermission.setPermissions(permissions);

      DbsAccessLevel dbsAccessLevelWrite = new DbsAccessLevel(DbsAccessLevel.ACCESSLEVEL_DISCOVER);
      dbsAccessLevelWrite.add(new DbsAccessLevel(DbsAccessLevel.ACCESSLEVEL_GET_CONTENT), dbsLibrarySession);
      dbsAccessLevelWrite.add(new DbsAccessLevel(DbsAccessLevel.ACCESSLEVEL_SET_CONTENT), dbsLibrarySession);
      dbsAccessLevelWrite.add(new DbsAccessLevel(DbsAccessLevel.ACCESSLEVEL_DELETE), dbsLibrarySession);

      if (dbsAccessLevelWrite.isSufficientlyEnabled(dbsAccessLevel, dbsLibrarySession)) {
        cmsdkPermission.setWrite(true);
      }

    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
    return cmsdkPermission;
  }

  /**
   * To construct CmsdkFile object from given publicObject(DbsFile object).
   * @param dbsLibrarySession DbsLibrarySession object.
   * @param dbsPublicObject out of which CmsdkFile object is to be constructed.
   * @return cmsdkFile representing specified DbsPublicObject.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to construct a CmsdkFile object.  
   */
  public static CmsdkFile constructCmsdkFile(DbsLibrarySession dbsLibrarySession,
                                             DbsPublicObject dbsPublicObject) throws CmsdkException {
    try {
      DbsDocument dbsDocument = (DbsDocument)dbsPublicObject.getResolvedPublicObject();
      CmsdkFile cmsdkFile = new CmsdkFile(dbsLibrarySession);
      cmsdkFile.setName(dbsPublicObject.getName());
      cmsdkFile.setPath(dbsPublicObject.getAnyFolderPath());
      cmsdkFile.setOwner(dbsPublicObject.getOwner().getName());
      cmsdkFile.setCreationDate(dbsPublicObject.getCreateDate());
      cmsdkFile.setModifiedDate(dbsPublicObject.getLastModifyDate());
      cmsdkFile.setDescription(dbsPublicObject.getDescription());
      DbsFormat dbsFormat = dbsDocument.getFormat();
      cmsdkFile.setPermission(CmsdkGenUtil.getCmsdkPermission(dbsLibrarySession, dbsPublicObject));

      if (dbsFormat != null) {
        cmsdkFile.setMimeType(dbsFormat.getMimeType());
      } else {
        cmsdkFile.setMimeType("unknown");
      }
      if (dbsFormat != null) {
        cmsdkFile.setSize(dbsDocument.getContentSize());
      }
      return cmsdkFile;
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * Returns a string representation of the CmsdkGenUtil object.
   * @return a string representation of the CmsdkGenUtil object.
   */
  public String toString() {
    return this.toString();
  }

}
