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
 * $Id: CmsdkConnection.java,v 1.26 2006/02/20 15:37:43 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.plugin.cmsdk;

import dbsentry.filesync.filesystem.specs.FsConnection;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;
import dbsentry.filesync.filesystem.specs.FsTotalInfoFoldersDocs;

import dms.beans.DbsDocument;
import dms.beans.DbsException;
import dms.beans.DbsFileSystem;
import dms.beans.DbsFolder;
import dms.beans.DbsFormat;
import dms.beans.DbsLibrarySession;
import dms.beans.DbsPrimaryUserProfile;
import dms.beans.DbsPublicObject;

import java.io.InputStream;


/**
 *	To perform various operations on cmsdk database.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class CmsdkConnection implements FsConnection {
  private DbsLibrarySession dbsLibrarySession;

  private static final String SEPERATOR = "/";

  /**
   * To get the seperator.
   * @return seperator string.
   */
  public String getSeperator() {
    return SEPERATOR;
  }

  /**
   * To close the connection.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if uanble to close the connection.
   */
  public void close() throws CmsdkException {
    try {
      dbsLibrarySession.disconnect();
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To copy an item from source to destination.
   * @param destinationPath destination folder path.
   * @param sourcePath path of item to be copied.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if fails to copy.
   */
  public void copyItem(String destinationPath, String sourcePath) throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsFolder dbsFolder = (DbsFolder)dbsFileSystem.findPublicObjectByPath(destinationPath);
      DbsPublicObject dbsPublicObject = dbsFileSystem.findPublicObjectByPath(sourcePath);

      dbsFileSystem.copy(dbsPublicObject, dbsFolder, null, true);
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    } catch (Exception ex) {
      CmsdkException cmsdkException = new CmsdkException(ex);
      throw cmsdkException;
    }
  }


  /**
   * To move an item from sorce to destination.
   * @param destinationPath destination folder path.
   * @param sourcePath path of item to be copied.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if fails to move.
   */
  public void moveItem(String destinationPath, String sourcePath) throws CmsdkException {
    try {
      DbsPublicObject dbsPublicObject;
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsFolder dbsFolder = (DbsFolder)dbsFileSystem.findPublicObjectByPath(destinationPath);

      dbsPublicObject = dbsFileSystem.findPublicObjectByPath(sourcePath);
      dbsFileSystem.move(dbsPublicObject.getFolderReferences(0), dbsFolder, dbsPublicObject, null, null, true);
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    } catch (Exception ex) {
      CmsdkException cmsdkException = new CmsdkException(ex);
      throw cmsdkException;
    }
  }

  /**
   * To create a document.
   * @param fileAbsPath absolute path of document to be created.
   * @param is Inputstream.
   * @param mimeType mimeType of document.
   * @return created FsFile object.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to create a document.
   */
  public FsFile createFile(String fileAbsPath, InputStream is, String mimeType) throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      String folderPath = fileAbsPath.substring(0, fileAbsPath.lastIndexOf(SEPERATOR));
      if (folderPath.equals("")) {
        folderPath = SEPERATOR;
      }
      String fileName = fileAbsPath.replaceFirst(folderPath + SEPERATOR, "");

      DbsDocument dbsDocument = dbsFileSystem.createDocument(fileName, is, folderPath, null);
      return CmsdkGenUtil.constructCmsdkFile(dbsLibrarySession, dbsDocument);

    } catch (CmsdkException cex) {
      cex.printStackTrace();
      throw cex;
    } catch (DbsException dex) {
      dex.printStackTrace();
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    } catch (Exception ex) {
      ex.printStackTrace();
      CmsdkException cmsdkException = new CmsdkException(ex);
      throw cmsdkException;
    }
  }

  /**
   * Updates a file with specified path using an InputStream.
   * @param fileAbsPath absolute path of file.
   * @param is InputStream of file.
   * @param mimeType mimeType of file.
   * @return updated FsFile object.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to update file.
   */
  public

  FsFile updateFile(String fileAbsPath, InputStream is, String mimeType) throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      String folderPath = fileAbsPath.substring(0, fileAbsPath.lastIndexOf(SEPERATOR));
      if (folderPath.equals("")) {
        folderPath = SEPERATOR;
      }
      String fileName = fileAbsPath.replaceFirst(folderPath + SEPERATOR, "");

      DbsDocument dbsDocument = dbsFileSystem.updateDocument(fileName, is, folderPath, null);
      return CmsdkGenUtil.constructCmsdkFile(dbsLibrarySession, dbsDocument);

    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    } catch (Exception ex) {
      CmsdkException cmsdkException = new CmsdkException(ex);
      throw cmsdkException;
    }
  }

  /**
    * To create a folder with specified name at specified location.
    * @param destinationPath destination folder path.
    * @param folderName new folder name.
    * @return created FsFolder object.
    * @throws  dbsentry.plugin.cmsdk.CmsdkException if unable to create new folder.
    */
  public FsFolder createFolder(String destinationPath, String folderName) throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsFolder dbsFolder = (DbsFolder)dbsFileSystem.findPublicObjectByPath(destinationPath);
      dbsFolder = dbsFileSystem.createFolder(folderName, dbsFolder, true, null);
      return CmsdkGenUtil.constructCmsdkFolder(dbsLibrarySession, dbsFolder);
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To set the DbsLibrarySession object.
   * @param dbsLibrarySession DbsLibrarySession object to be stored.
   */
  public void setDbsLibrarySession(DbsLibrarySession dbsLibrarySession) {
    this.dbsLibrarySession = dbsLibrarySession;
  }

  /**
   * To get the home folder.
   * @return FsFolder object representing home folder.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to fetch the home folder.
   */
  public FsFolder getHomeFolder() throws CmsdkException {
    try {
      DbsPrimaryUserProfile dbsPrimaryUserProfile = dbsLibrarySession.getUser().getPrimaryUserProfile();
      DbsFolder dbsHomeFolder = dbsPrimaryUserProfile.getHomeFolder();
      return CmsdkGenUtil.constructCmsdkFolder(dbsLibrarySession, dbsHomeFolder);
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To delete the item with specified item path.
   * @param itemPath path of item to be deleted.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if failed to delete the specified item.
   */
  public void deleteItem(String itemPath) throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsPublicObject dbsPublicObject = dbsFileSystem.findPublicObjectByPath(itemPath);
      dbsFileSystem.delete(dbsPublicObject);
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To delete the items with specified item paths.
   * @param itemPaths array of itempaths to be deleted.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if failed to delete specified items.
   */
  public void deleteItems(Object[] itemPaths) throws CmsdkException {
    int itemCount = itemPaths.length;
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      for (int index = 0; index < itemCount; index++) {
        DbsPublicObject dbsPublicObject = dbsFileSystem.findPublicObjectByPath((String)itemPaths[index]);
        dbsFileSystem.delete(dbsPublicObject);
      }
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To rename an item.
   * @param itemPath path of item to be renamed.
   * @param itemName new name.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to delete the item.
   */
  public void renameItem(String itemPath, String itemName) throws CmsdkException {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsPublicObject dbsPublicObject = dbsFileSystem.findPublicObjectByPath(itemPath);
      dbsFileSystem.rename(dbsPublicObject, itemName);
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To find an object(document/folder) by its path.
   * @param  path path of object to be found.
   * @return FsObject representing specified path.
   */
  public FsObject findFsObjectByPath(String path) {
    try {
      DbsFileSystem dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      DbsPublicObject dbsPublicObject = dbsFileSystem.findPublicObjectByPath(path);
      if (dbsPublicObject instanceof DbsFolder) {
        return CmsdkGenUtil.constructCmsdkFolder(dbsLibrarySession, dbsPublicObject);
      } else {
        return CmsdkGenUtil.constructCmsdkFile(dbsLibrarySession, dbsPublicObject);
      }
    } catch (DbsException dex) {
      return null;
    } catch (CmsdkException cex) {
      return null;
    }

  }

  /**
   * To get the relative path. 
   * @param itemAbsPath absolute path.
   * @param currentFolderPath with respect to which the relative path to be found.
   * @return path relative to the current folder path.
   */
  public

  String getRelativePath(String itemAbsPath, String currentFolderPath) {
    if (currentFolderPath.equals(getSeperator())) {
      return (itemAbsPath.replaceFirst(currentFolderPath, ""));
    } else {
      return (itemAbsPath.replaceFirst(currentFolderPath + getSeperator(), ""));
    }
  }

  /**
   * To list the roots.
   * @return List of root folders.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to get the list of roots.
   */
  public FsFolder[] listRoots() throws CmsdkException {
    try {
      CmsdkFolder[] cmsdkFolders = new CmsdkFolder[1];
      CmsdkFolder cmsdkFolder =
        CmsdkGenUtil.constructCmsdkFolder(dbsLibrarySession, dbsLibrarySession.getRootFolder());
      cmsdkFolder.setName(SEPERATOR);
      cmsdkFolders[0] = cmsdkFolder;
      return cmsdkFolders;
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
  }

  /**
   * To calculate the size of folders and documents with specified paths.
   * @param  itemPaths array of item paths whose size is to be calculated.
   * @return total size as double.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to found the total size.
   */
  public long calculateFolderDocSize(Object[] itemPaths) throws CmsdkException {
    long totalSize = 0;
    DbsPublicObject dbsPublicObject;
    DbsFileSystem dbsFileSystem;
    try {
      dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      if (itemPaths != null) {
        int itemCount = itemPaths.length;
        for (int index = 0; index < itemCount; index++) {
          dbsPublicObject = dbsFileSystem.findPublicObjectByPath((String)itemPaths[index]);
          totalSize += findTotalSizeFoldersDocs(dbsPublicObject.getId());
        }
      }
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
    return totalSize;
  }


  //Find size folders and documents for a given public object id

  /**
   * To find the totalsize of folder/document with given public object id.
   * @param  publicObjectId Long publicObjectId of folder/document.
   * @return long size of folder/document with given public object id.
   * @throws dms.beans.DbsException if unable to get the total size.
   */
  private long findTotalSizeFoldersDocs(Long publicObjectId) throws DbsException {
    long totalSize = 0;
    DbsPublicObject dbsPublicObject;
    DbsDocument dbsDocument;
    try {
      dbsPublicObject = dbsLibrarySession.getPublicObject(publicObjectId);
      if (dbsPublicObject.getResolvedPublicObject() instanceof DbsDocument) {
        dbsDocument = (DbsDocument)dbsPublicObject.getResolvedPublicObject();
        totalSize += dbsDocument.getContentSize();
      } else {
        if (dbsPublicObject instanceof DbsFolder) {
          DbsFolder topFolder = (DbsFolder)dbsPublicObject;
          DbsPublicObject[] itemsInTheFolder = topFolder.getItems();
          if (itemsInTheFolder != null) {
            for (int index = 0; index < itemsInTheFolder.length; index++) {
              long tempTotalSize = findTotalSizeFoldersDocs(itemsInTheFolder[index].getId());
              totalSize += tempTotalSize;
            }
          }
        }
      }
    } catch (DbsException dbsException) {
      throw dbsException;
    }
    return totalSize;
  }

  /**
   * Purpose To get the absolute path.
   * @param  relativePath relative path.
   * @param  currentFolderPath path with respect to which the absolute path is to be calculated.
   * @return caculated Absolute path.
   */
  public

  String getAbsolutePath(String relativePath, String currentFolderPath) {
    if (currentFolderPath.equals(this.getSeperator())) {
      return (currentFolderPath + relativePath);
    } else {
      return (currentFolderPath + this.getSeperator() + relativePath);
    }
  }

  /**
   * To get the root folder of specified folderPath.
   * @param  folderPath folder path whose root is to be found.
   * @return FsFolder object representing root folder. 
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to found the root.
   */
  public FsFolder getFolderRoot(String folderPath) throws CmsdkException {
    return (FsFolder)findFsObjectByPath(this.getSeperator());
  }

  /**
   * To get total information such as totalsize,document count, folder count,permission
   * of folder/document with specified public object id.
   * @param  publicObjectId Long publicObjectId whose info has to be found.
   * @return FsTotalInfoFoldersDocs object holding above mentioned information.
   * @throws dms.beans.DbsException if unable to find the information.
   */
  private FsTotalInfoFoldersDocs findTotalInfoFoldersDocs(Long publicObjectId) throws DbsException {
    CmsdkTotalInfoFoldersDocs total = new CmsdkTotalInfoFoldersDocs();
    DbsPublicObject dbsPublicObject;
    DbsDocument dbsDocument;
    try {
      dbsPublicObject = dbsLibrarySession.getPublicObject(publicObjectId);
      dbsPublicObject = dbsPublicObject.getResolvedPublicObject();
      if (dbsPublicObject instanceof DbsDocument) {
        dbsDocument = (DbsDocument)dbsPublicObject.getResolvedPublicObject();
        total.setSize(dbsDocument.getContentSize());
        total.setDocumentCount(1);
        total.setFolderDocCount(1);
      } else {
        DbsFolder topFolder = (DbsFolder)dbsPublicObject.getResolvedPublicObject();
        total.setFolderCount(1);
        total.setDocumentCount(0);
        total.setFolderDocCount(1);
        total.setSize(0);

        DbsPublicObject[] itemsInTheFolder = topFolder.getItems();
        FsTotalInfoFoldersDocs tempTotal;
        if (itemsInTheFolder != null) {
          for (int index = 0; index < itemsInTheFolder.length; index++) {
            tempTotal = findTotalInfoFoldersDocs(itemsInTheFolder[index].getId());

            total.setSize(total.getSize() + tempTotal.getSize());
            total.setDocumentCount(total.getDocumentCount() + tempTotal.getDocumentCount());
            total.setFolderCount(total.getFolderCount() + tempTotal.getFolderCount());
            total.setFolderDocCount(total.getFolderDocCount() + tempTotal.getFolderDocCount());
          }
        }
      }
    } catch (DbsException dbsException) {
      throw dbsException;
    }
    FsTotalInfoFoldersDocs fsTotalInfoFoldersDocs = total;
    return fsTotalInfoFoldersDocs;
  }


  /**
   * To get total information such as totalsize,document count, folder count,permission
   * of folders/documents with specified itempaths.
   * @param itemPaths array of itempaths.
   * @return FsTotalInfoFoldersDocs object holding above mentioned information.
   * @throws dbsentry.plugin.cmsdk.CmsdkException if unable to find the information.
   */
  public FsTotalInfoFoldersDocs findFolderDocInfo(Object[] itemPaths) throws CmsdkException {
    CmsdkTotalInfoFoldersDocs total = new CmsdkTotalInfoFoldersDocs();
    FsTotalInfoFoldersDocs tempTotal;
    DbsPublicObject dbsPublicObject;
    DbsFileSystem dbsFileSystem;
    DbsDocument dbsDocument;
    String mimeType;
    try {
      dbsFileSystem = new DbsFileSystem(dbsLibrarySession);
      if (itemPaths != null) {
        int itemCount = itemPaths.length;
        for (int index = 0; index < itemCount; index++) {
          dbsPublicObject = dbsFileSystem.findPublicObjectByPath((String)itemPaths[index]);
          tempTotal = findTotalInfoFoldersDocs(dbsPublicObject.getId());
          total.setDocumentCount(total.getDocumentCount() + tempTotal.getDocumentCount());
          total.setFolderCount(total.getFolderCount() + tempTotal.getFolderCount());
          total.setFolderDocCount(total.getFolderDocCount() + tempTotal.getFolderDocCount());
          total.setSize(total.getSize() + tempTotal.getSize());
          mimeType = total.getType();
          if (dbsPublicObject instanceof DbsDocument) {
            dbsDocument = (DbsDocument)dbsPublicObject;
            DbsFormat dbsFormat = dbsDocument.getFormat();
            if (dbsFormat != null) {
              if (mimeType != null) {
                if (mimeType.equals(dbsDocument.getFormat().getMimeType())) {
                  total.setType(mimeType);
                } else {
                  total.setType("All types");
                }
              } else {
                total.setType(dbsDocument.getFormat().getMimeType());
              }
            }
          } else {
            if (mimeType != null) {
              if (mimeType.equals("Folder File")) {
                total.setType(mimeType);
              } else {
                total.setType("All types");
              }
            } else {
              total.setType("Folder File");
            }
          }
        }
      }
      System.out.println("Type of item : " + total.getType());
    } catch (DbsException dex) {
      CmsdkException cmsdkException = new CmsdkException(dex);
      throw cmsdkException;
    }
    FsTotalInfoFoldersDocs fsTotalInfoFoldersDocs = total;
    return fsTotalInfoFoldersDocs;
  }

  /**
   * Returns a string representation of the CmsdkConnection object.
   * @return a string representation of the CmsdkConnection object.
   */
  public String toString() {
    return this.toString();
  }
}

