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
 * $Id: FsConnection.java,v 1.12 2006/02/20 15:37:37 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.filesystem.specs;

import dbsentry.filesync.filesystem.specs.FsException;
import dbsentry.filesync.filesystem.specs.FsFile;
import dbsentry.filesync.filesystem.specs.FsFolder;
import dbsentry.filesync.filesystem.specs.FsObject;
import dbsentry.filesync.filesystem.specs.FsTotalInfoFoldersDocs;

import java.io.InputStream;


/**
 *  Provides an interface to manipulate the filesystem.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation:   04-07-2005
 *  Last Modfied by :    
 * 	Last Modfied Date:   
 */
public interface

FsConnection {
  /**
   * To close the connection.
   * @throws dbsentry.filesync.filesystem.specs.FsException if uanble to close the connection.
   */
  public void close() throws FsException;

  /**
   * To copy an item from source to destination.
   * @param destinationPath destination folder path.
   * @param sourcePath path of item to be copied.
   * @throws dbsentry.filesync.filesystem.specs.FsException if fails to copy.
   */
  public void copyItem(String destinationPath, String sourcePath) throws FsException;

  /**
   * To move an item from source to destination.
   * @param destinationPath destination folder path.
   * @param sourcePath path of item to be moved.
   * @throws dbsentry.filesync.filesystem.specs.FsException if fails to move.
   */
  public void moveItem(String destinationPath, String sourcePath) throws FsException;

  /**
   * To create a folder with specified name at specified location.
   * @param destinationPath destination folder path.
   * @param folderName new folder name.
   * @return created FsFolder object.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to create new folder.
   */
  public FsFolder createFolder(String destinationPath, String folderName) throws FsException;

  /**
   * To create a file with specified path.
   * @param fileAbsPath absolute path of file.
   * @param is InputStream.
   * @param  mimeType mimeType of file.
   * @return created FsFile object.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to create new file.
   */
  public FsFile createFile(String fileAbsPath, InputStream is, String mimeType) throws FsException;

  /**
   * Updates a file with specified path using an InputStream.
   * @param fileAbsPath absolute path of file.
   * @param is InputStream of file.
   * @param mimeType mimeType of file.
   * @return updated FsFile object.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to update file.
   */
  public FsFile updateFile(String fileAbsPath, InputStream is, String mimeType) throws FsException;

  /**
   * To get the home folder.
   * @return  FsFolder object representing home folder.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to fetch the home folder.
   */
  public FsFolder getHomeFolder() throws FsException;

  /**
   * To delete the items with specified item paths.
   * @param itemPaths array of itempaths to be deleted.
   * @throws dbsentry.filesync.filesystem.specs.FsException if failed to delete specified items.
   */
  public void deleteItems(Object[] itemPaths) throws FsException;

  /**
   * To delete the item with specified item path.
   * @param itemPath path of item to be deleted.
   * @throws dbsentry.filesync.filesystem.specs.FsException if failed to delete the specified item.
   */
  public void deleteItem(String itemPath) throws FsException;

  /**
   * To rename an item.
   * @param itemPath path of item to be renamed.
   * @param itemName new name.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to delete the item.
   */
  public void renameItem(String itemPath, String itemName) throws FsException;

  /**
   * To find an object(document/folder) by its path.
   * @param  path path of object to be found.
   * @return FsObject representing specified path.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to find the object with specified path.
   */
  public FsObject findFsObjectByPath(String path) throws FsException;

  /**
   * To get the seperator string.
   * @return seperator seperator string.
   */
  public String getSeperator();

  /**
   * To get the relative path. 
   * @param itemAbsPath absolute path.
   * @param currentFolderPath with respect to which the relative path to be found.
   * @return path relative to the current folder path.
   */
  public String getRelativePath(String itemAbsPath, String currentFolderPath);

  /**
   * Purpose To get the absolute path.
   * @param  relativePath relative path.
   * @param  currentFolderPath path with respect to which the absolute path is to be calculated.
   * @return caculated Absolute path .
   */
  public String getAbsolutePath(String relativePath, String currentFolderPath);

  /**
   * To list the roots.
   * @return List of root folders.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to get the list of roots.
   */
  public FsFolder[] listRoots() throws FsException;

  /**
   * To get the root folder of specified folderPath.
   * @param  folderPath folder path whose root is to be found.
   * @return FsFolder object representing root folder. 
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to found the root.
   */
  public FsFolder getFolderRoot(String folderPath) throws FsException;

  /**
   * To calculate the size of folders and documents with specified paths.
   * @param  itemPaths array of item paths whose size is to be calculated.
   * @return total size as double.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to found the total size.
   */
  public long calculateFolderDocSize(Object[] itemPaths) throws FsException;

  /**
   * To get total information such as totalsize,document count, folder count,permission
   * of items with specified itempaths.
   * @param itemPaths whose total iformation is to be found.
   * @return FsTotalInfoFoldersDocs object holding above mentioned information.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to found the information.
   */
  public FsTotalInfoFoldersDocs findFolderDocInfo(Object[] itemPaths) throws FsException;
}
