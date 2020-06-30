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
 * $Id: FsTotalInfoFoldersDocs.java,v 1.7 2006/02/20 15:37:36 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.filesystem.specs;

/**
 *	Provides an interface to store and access the information such as no.of documents and folders,
 *  total size, permissions of folders and files.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public interface FsTotalInfoFoldersDocs {
  /**
     * To get the stored size value.
     * @return long size
     */
  public long getSize();

  /**
     * To get the stored folderCount.
     * @return integer folderCount.
     */
  public int getFolderCount();

  /**
    * To fetch the documentCount value.
    * @return integer documentCount.
    */
  public int getDocumentCount();

  /**
    * To get the folderDocCount value.
    * @return integer folderDocCount
    */
  public int getFolderDocCount();

  /**
    * To get the stored permissions . 
    * @return permissions String array of permissions. 
    */
  public String[] getPermissions();

  /**
   * gives MimeType of folder/document/set of documents and folders. 
   * @return MimeType of folder/document/set of documents and folders.
   */
  public String getType();

  /**
   * @param size
   */
  public void setSize(long size);

  /**
   * @param folderCount
   */
  public void setFolderCount(int folderCount);

  /**
   * @param documentCount
   */
  public void setDocumentCount(int documentCount);

  /**
   * @param folderDocCount
   */
  public void setFolderDocCount(int folderDocCount);

  /**
   * @param permissions
   */
  public void setPermissions(String[] permissions);

  /**
   * @param type
   */
  public void setType(String type);
}
