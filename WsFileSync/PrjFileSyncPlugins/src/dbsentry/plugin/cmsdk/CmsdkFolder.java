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
 * $Id: CmsdkFolder.java,v 1.9 2006/09/08 09:13:11 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.plugin.cmsdk;

import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;

import dms.beans.DbsAttributeQualification;
import dms.beans.DbsAttributeSearchSpecification;
import dms.beans.DbsAttributeValue;
import dms.beans.DbsException;
import dms.beans.DbsFileSystem;
import dms.beans.DbsFolder;
import dms.beans.DbsFolderRestrictQualification;
import dms.beans.DbsLibrarySession;
import dms.beans.DbsPublicObject;
import dms.beans.DbsSearch;
import dms.beans.DbsSearchClassSpecification;
import dms.beans.DbsSearchClause;
import dms.beans.DbsSearchQualification;
import dms.beans.DbsSearchSortSpecification;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 *	To manipulate the folder of cmsdk database.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class CmsdkFolder extends CmsdkObject implements FsFolder {
  private DbsLibrarySession dbsLibrarySession;

  /**
   * To construct a CmsdkFolder object.
   * @param dbsLibrarySession DbsLibrarySession object.
   */
  public CmsdkFolder(DbsLibrarySession dbsLibrarySession) {
    this.dbsLibrarySession = dbsLibrarySession;
  }

  /**
   * To get parent of this folder.
   * @return FsFolder object representing parent of this folder.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to find the parent.
   */
  public FsFolder getParent() throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsFolder dbsThisFolder = (DbsFolder)dbsFileSystem.findPublicObjectByPath(getPath());
      DbsFolder parentFolder = dbsThisFolder.getFolderReferences(0);
      CmsdkFolder cmsdkFolder = CmsdkGenUtil.constructCmsdkFolder(dbsLibrarySession, parentFolder);
      FsFolder fsFolder = cmsdkFolder;
      return fsFolder;
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To list the contents of this folder.
   * @return FsObject array ,which holds contents of this folder.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if failed to list the 
   * contents of folder.
   */
  public FsObject[] listContentOfFolder() throws CmsdkException {
    //Variable Declaration
    DbsPublicObject dbsPublicObject;

    FsObject fsObjects[] = new FsObject[0];
    DbsFolder dbsFolder;
    List searchQualificationList = new ArrayList();
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsFolder dbsThisFolder = (DbsFolder)dbsFileSystem.findPublicObjectByPath(getPath());

      DbsAttributeSearchSpecification dbsAttributeSearchSpecification = new DbsAttributeSearchSpecification();
      DbsSearchSortSpecification dbsSearchSortSpecification = new DbsSearchSortSpecification();
      DbsFolderRestrictQualification dbsFolderRestrictQualification = new DbsFolderRestrictQualification();
      DbsSearchClassSpecification dbsSearchClassSpecification = new DbsSearchClassSpecification();

      dbsFolder = (DbsFolder)(new DbsFileSystem(dbsLibrarySession).findPublicObjectById((dbsThisFolder.getId())));
      dbsFolderRestrictQualification.setStartFolder(dbsFolder);
      dbsFolderRestrictQualification.setSearchClassname(DbsPublicObject.CLASS_NAME);
      dbsFolderRestrictQualification.setMultiLevel(false);
      searchQualificationList.add(dbsFolderRestrictQualification);

      // array of class to be searched
      String[] searchClasses = new String[] { DbsPublicObject.CLASS_NAME };
      dbsSearchClassSpecification.addSearchClasses(searchClasses);
      dbsSearchClassSpecification.addResultClass(DbsPublicObject.CLASS_NAME);

      // Array of classes involved in the order by clause
      String[] sortClasses = new String[] { DbsPublicObject.CLASS_NAME };
      // Array of Attribute Names to match class names.
      String[] attNames = new String[] { "NAME" };
      // Order of Sort for each sort element
      boolean[] orders = new boolean[] { true };
      // Case insensitive Sort for each sort element
      String[] caseSorts = new String[] { "nls_upper" };
      dbsSearchSortSpecification.add(sortClasses, attNames, orders, caseSorts);

      DbsAttributeQualification folderDocIdAttrbQual = new DbsAttributeQualification();
      String searchColumn = "ID";
      folderDocIdAttrbQual.setAttribute(searchColumn);
      folderDocIdAttrbQual.setOperatorType(DbsAttributeQualification.NOT_EQUAL);
      folderDocIdAttrbQual.setValue(DbsAttributeValue.newAttributeValue(dbsThisFolder.getId()));
      searchQualificationList.add(folderDocIdAttrbQual);

      //And together all the dbsSearch qualifications
      DbsSearchQualification dbsSearchQualification = null;
      Iterator iterator = searchQualificationList.iterator();
      while (iterator.hasNext()) {
        DbsSearchQualification nextSearchQualification = (DbsSearchQualification)iterator.next();
        if (dbsSearchQualification == null) {
          dbsSearchQualification = nextSearchQualification;
        } else {
          dbsSearchQualification =
            new DbsSearchClause(dbsSearchQualification, nextSearchQualification, DbsSearchClause.AND);
        }
      }

      dbsAttributeSearchSpecification.setSearchQualification(dbsSearchQualification);
      dbsAttributeSearchSpecification.setSearchClassSpecification(dbsSearchClassSpecification);
      dbsAttributeSearchSpecification.setSearchSortSpecification(dbsSearchSortSpecification);

      DbsSearch dbsSearch = new DbsSearch(dbsLibrarySession, dbsAttributeSearchSpecification);
      dbsSearch.open();
      int itemCount = dbsSearch.getItemCount();
      CmsdkFolder cmsdkFolder;
      FsFolder fsFolder;
      CmsdkFile cmsdkFile;
      FsFile fsFile;
      fsObjects = new FsObject[itemCount];
      for (int index = 0; index < itemCount; index++) {
        dbsPublicObject = ((DbsPublicObject)dbsSearch.next().getLibraryObject());
        if (dbsPublicObject.getResolvedPublicObject() instanceof DbsFolder) {
          //Resolved public object returns either folder or dbsDocument
          cmsdkFolder = CmsdkGenUtil.constructCmsdkFolder(dbsLibrarySession, dbsPublicObject);
          fsFolder = cmsdkFolder;
          fsObjects[index] = fsFolder;
        } else {
          cmsdkFile = CmsdkGenUtil.constructCmsdkFile(dbsLibrarySession, dbsPublicObject);
          fsFile = cmsdkFile;
          fsObjects[index] = fsFile;
        }
      }
    } catch (DbsException dex) {
      dex.printStackTrace();
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
    return fsObjects;
  }

  /**
   * To check if this folder has sub folders.
   * @return boolean hasubfolders true/false 
   * @throws dbsentry.plugin.cmsdk.CmsdkException if failed to check whether 
   * this folder has subfolders.
   */
  public boolean hasSubfolders() throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsFolder dbsThisFolder = (DbsFolder)dbsFileSystem.findPublicObjectByPath(getPath());
      return dbsThisFolder.hasSubfolders();
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  public void updateCreationDate(Date creationDate) {

  }

  public void updateModifiedDate(Date modifiedDate) {

  }

  /**
   * Returns a string representation of the CmsdkFolder object.
   * @return a string representation of the CmsdkFolder object.
   */
  public String toString() {
    return this.toString();
  }

}
