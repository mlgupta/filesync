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
 * $Id: CmsdkFile.java,v 1.13 2006/08/01 10:14:07 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.plugin.cmsdk;

import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;

import dms.beans.DbsAttributeValue;
import dms.beans.DbsDocument;
import dms.beans.DbsException;
import dms.beans.DbsFileSystem;
import dms.beans.DbsFolder;
import dms.beans.DbsLibrarySession;
import dms.beans.DbsPublicObject;

import java.io.InputStream;

import java.util.Date;


/**
 *	To manipulate the file of cmsdk database.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class CmsdkFile extends CmsdkObject implements FsFile {
  private long size;

  private String mimeType;

  private DbsLibrarySession dbsLibrarySession;

  /**
   * Constructs a CmsdkFile object.
   * @param dbsLibrarySession DbsLibrarySession object used to create a library session.
   */
  public CmsdkFile(DbsLibrarySession dbsLibrarySession) {
    this.dbsLibrarySession = dbsLibrarySession;
  }

  /**
   * To get the InputStream associated with this CmsdkFile.
   * @return inputStream to read from file. 
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to get the InputStream.
   */
  public InputStream getInputStream() throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsPublicObject dbsPublicObject = dbsFileSystem.findPublicObjectByPath(getPath());
      DbsDocument dbsDocument = (DbsDocument)dbsPublicObject.getResolvedPublicObject();
      return dbsDocument.getContentStream();
    } catch (DbsException dex) {
      throw new CmsdkException(dex);
    }
  }

  /**
   * To get the mimetype of CmsdkFile.
   * @return mimeType of file. 
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * To get the size of this CmsdkFile.
   * @return long size of file.
   */
  public long getSize() {
    return size;
  }

  /**
   * To set the mimeType of CmsdkFile.
   * @param mimeType to be set.
   */
  public

  void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * Sets the size of this CmsdkFile to size.
   * @param size size to be set.
   */
  public void setSize(long size) {
    this.size = size;
  }

  /**
   * To get the parent folder of CmsdkFile.
   * @return FsFolder object representing parent folder.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to get the parent folder.
   */
  public FsFolder getParent() throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsDocument dbsThisDocument =
        (DbsDocument)(dbsFileSystem.findPublicObjectByPath(getPath()).getResolvedPublicObject());
      DbsFolder parentFolder = dbsThisDocument.getFolderReferences(0);
      CmsdkFolder cmsdkFolder = CmsdkGenUtil.constructCmsdkFolder(dbsLibrarySession, parentFolder);
      FsFolder fsFolder = cmsdkFolder;
      return fsFolder;
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To modify the creation date of CmsdkFile.
   * @param creationDate Date object to be set as creationDate.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to update the creation date.
   */
  public void updateCreationDate(Date creationDate) throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsDocument dbsThisDocument =
        (DbsDocument)(dbsFileSystem.findPublicObjectByPath(getPath()).getResolvedPublicObject());
      dbsThisDocument.setAttribute("CREATEDATE_ATTRIBUTE", DbsAttributeValue.newAttributeValue(creationDate));
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To modify the modified date of CmsdkFile.
   * @param  modifiedDate Date object to be set as modifiedDate.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to update the creationDate.
   */
  public void updateModifiedDate(Date modifiedDate) throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsDocument dbsThisDocument =
        (DbsDocument)(dbsFileSystem.findPublicObjectByPath(getPath()).getResolvedPublicObject());
      dbsThisDocument.setAttribute("LASTMODIFYDATE_ATTRIBUTE", DbsAttributeValue.newAttributeValue(modifiedDate));
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * Returns a string representation of the CmsdkFile object.
   * @return a string representation of the CmsdkFile object.
   */
  public String toString() {
    return super.toString();
  }

}
