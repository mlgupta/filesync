package dbsentry.plugin.cmsdk;

import dbsentry.filesync.filesystem.specs.FsTotalInfoFoldersDocs;

/**
 *	To store and access the information such as no.of documents and folders,
 *  total size, permissions of folders and files.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class

CmsdkTotalInfoFoldersDocs implements FsTotalInfoFoldersDocs {
  private long size;

  private int folderCount;

  private int documentCount;

  private int folderDocCount;

  private String[] permissions = null;

  private String type = null;

  /**
   * To get the stored size value.
   * @return size long size
   */
  public long getSize() {
    return size;
  }

  /**
   * To set the size value to newSize.
   * @param newSize size to be stored.
   */
  public void setSize(long newSize) {
    size = newSize;
  }

  /**
   * To get the stored folderCount.
   * @return folderCount integer folderCount.
   */
  public int getFolderCount() {
    return folderCount;
  }

  /**
   * To set the folderCount to newFolderCount.
   * @param newFolderCount folderCount to be stored.
   */
  public void setFolderCount(int newFolderCount) {
    folderCount = newFolderCount;
  }

  /**
   * To fetch the documentCount value.
   * @return documentCount int documentCount.
   */
  public int getDocumentCount() {
    return documentCount;
  }

  /**
   * To set documentCount value to newDocumentCount.
   * @param newDocumentCount documentCount to be stored.
   */
  public void setDocumentCount(int newDocumentCount) {
    documentCount = newDocumentCount;
  }

  /**
   * To get the folderDocCount value.
   * @return folderDocCount int folderDocCount
   */
  public int getFolderDocCount() {
    return folderDocCount;
  }

  /**
   * To set the folderDocCount value. 
   * @param newFolderDocCount folderDocCount to be stored.
   */
  public void setFolderDocCount(int newFolderDocCount) {
    folderDocCount = newFolderDocCount;
  }

  /**
   * To get the stored permissions . 
   * @return permissions String array of permissions. 
   */
  public String[] getPermissions() {
    return permissions;
  }


  /**
   * To set the permissions.
   * @param permissions String array of permissions to be stored.
   */
  public void setPermissions(String[] permissions) {
    this.permissions = permissions;
  }

  /**
   * gives String representation of CmsdkTotalInfoFoldersDocs object.
   * @return String repersentation of CmsdkTotalInfoFoldersDocs object.
   */
  public String toString() {
    return this.toString();
  }

  /**
   * sets type of folder/document/set of documents and folders.
   * @param type of folder/document/set of documents and folders.
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * gives MimeType of folder/document/set of documents and folders. 
   * @return MimeType of folder/document/set of documents and folders.
   */
  public String getType() {
    return type;
  }

}
