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
 * $Id: FsMessage.java,v 1.36 2006/04/13 08:45:30 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import java.io.Serializable;


/**
 * To define integer variables that can be used as tokens for the 
 * communication between client and server.
 * @author             Jeetendra Prasad
 * @version            1.0
 * Date of creation:   13-07-2005
 * Last Modfied by :    
 * Last Modfied Date:   
 */
public class FsMessage implements Serializable {
  //code exchanged when establishing connection

  public static final int CONNECT = 100;
  //code exchanged when disconnecting

  public static final int DISCONNECT = 101;
  //code exchanged when closing jxta session

  public static final int CLOSE_JXTA_SESSION = 102;
  //code exchanged when requesting content of home folder

  public static final int GETCONTENTOFHOMEFOLDER = 103;
  //code exchanged when requesting content of a given folder

  public static final int GETCONTENTOFFOLDER = 104;
  //code exchanged when requesting content of the parent folder. parent 
  //folder is the parent of current folder where the user is located.

  public static final int GETCONTENTOFPARENTFOLDER = 105;
  //code exchanged when creating new folder

  public static final int NEWFOLDER = 106;
  //code exchanged when deleting an item(folder/file).

  public static final int DELETEITEMS = 107;
  //code exchanged when client request navigating back in the navigation history.

  public static final int NAVIGATEBACK = 108;
  //code exchanged when client request navigating forward in the navigation history.

  public static final int NAVIGATEFORWARD = 109;
  //code exchanged when requesting subfolders of a given folder

  public static final int GET_SUB_FOLDERS = 110;
  //code exchanged when renaming an item

  public static final int RENAME = 111;
  //code exchanged when requesting change of directory

  public static final int CHANGE_DIRECTORY = 112;
  //code exchanged when requesting flat folder tree structure. 
  //flat folder tree structure means tree starting from the root to a given folder.
  //but it will contain the subfolders which are on the path to the given folder, not the 
  //sub folders which are not on the path.

  public static final int GET_FLAT_FOLDER_TREE = 113;
  //code exchanged when requesting root folders of a file system or repository

  public static final int GET_ROOT_FOLDERS = 114;
  //code exchanged when requesting home folder of a user

  public static final int GET_HOME_FOLDER = 115;
  //code exchanged when requesting root folder of a given folder

  public static final int GET_FOLDER_ROOT = 116;
  //code exchanged when requesting folder content recursively

  public static final int GET_FOLDER_CONTENT_RECURSIVE = 117;
  //request code send when requesting file and folder properties

  public static final int GET_FILE_FOLDER_PROPERTIES = 118;
  //response code send when requestED file and folder properties is just a single file

  public static final int FILE_PROPERTYPAGE = 119;
  //response code send when requested file and folder properties is just a single folder

  public static final int FOLDER_PROPERTYPAGE = 120;
  //response code send when requested file and folder properties is files or folders or file(s) and folder(s)

  public static final int FILE_FOLDER_PROPERTYPAGE = 121;

  //code exchanged when changing property of a file.

  public static final int CHANGE_PROPERTIES_OF_FILE = 122;
  //code exchanged when changing property of a folder.

  public static final int CHANGE_PROPERTIES_OF_FOLDER = 123;
  //code exchanged when changinh property of a file and folder.

  public static final int CHANGE_PROPERTIES_OF_FILENFOLDER = 124;
  //code exchanged to get the updated Xml file necessary for synchronization.

  public static final int GET_SYNC_XML = 125;
  //code exchanged to gets the list of item in the search path. and also the 
  //nodes in the tree which has not arrived

  public static final int SEARCH_PATH = 126;
  //response code send when there is a search failure

  public static final int SEARCH_FAILURE = 127;

  //response code send to initaite download

  public static final int DOWNLOAD_START = 200;
  //request code send after initiating download

  public static final int DOWNLOAD_STARTED = 201;
  //response code send to create folder while downloading

  public static final int DOWNLOAD_CREATE_FOLDER = 202;
  //request code send after creating folder while downloading

  public static final int DOWNLOAD_CREATED_FOLDER = 203;
  //response code send to create empty file while downloading

  public static final int DOWNLOAD_CREATE_EMPTY_FILE = 204;
  //request code send after creating empty file while downloading

  public static final int DOWNLOAD_CREATED_EMPTY_FILE = 205;
  //response send indicating download completion

  public static final int DOWNLOAD_COMPLETE = 206;
  //request send after completion of download

  public static final int DOWNLOAD_COMPLETED = 207;
  //code exchanged incase of download failure

  public static final int DOWNLOAD_FAILURE = 208;
  //response send to append data to file in case of download

  public static final int DOWNLOAD_APPEND_TO_FILE = 209;
  //request send after appending data to file in case of download

  public static final int DOWNLOAD_APPENDED_TO_FILE = 210;
  //response code send asking to close file in case of download

  public static final int DOWNLOAD_CLOSE_FILE = 211;
  //request code send after closing the file in case of download

  public static final int DOWNLOAD_CLOSED_FILE = 212;
  //code exchanged when file is to be overwritten

  public static final int DOWNLOAD_OVERWRITE_FILE = 213;
  //request code send cancel the download
  
   public static final int DOWNLOAD_OVERWRITE_FOLDER = 216;

  public static final int DOWNLOAD_CANCEL = 214;
  //response code send when download is canceled

  public static final int DOWNLOAD_CANCELED = 215;

  //request code send to start the download

  public static final int UPLOAD_START = 300;
  //response code send when upload is started

  public static final int UPLOAD_STARTED = 301;
  //request code send to create a folder

  public static final int UPLOAD_CREATE_FOLDER = 302;
  //response code send after creating a folder

  public static final int UPLOAD_CREATED_FOLDER = 303;
  //request code send to create empty file
  
  public static final int UPLOAD_NOT_CREATED_FOLDER = 316;

  public static final int UPLOAD_CREATE_EMPTY_FILE = 304;
  //response code send to after creating empty file

  public static final int UPLOAD_CREATED_EMPTY_FILE = 305;
  //request code send to complete the upload

  public static final int UPLOAD_COMPLETE = 306;
  //response code send after completing the upload

  public static final int UPLOAD_COMPLETED = 307;
  //code exchanged when there is failure in upload either on client or server

  public static final int UPLOAD_FAILURE = 308;
  //request code send to append data to file

  public static final int UPLOAD_APPEND_TO_FILE = 309;
  //response code send after appending data to file.

  public static final int UPLOAD_APPENDED_TO_FILE = 310;
  //request code send to close the file

  public static final int UPLOAD_CLOSE_FILE = 311;
  //response code send after closing the file

  public static final int UPLOAD_CLOSED_FILE = 312;
  //response code send when asking to overwriting a file

  public static final int UPLOAD_OVERWRITE_FILE = 313;
  //request code send to cancel the upload
  
  public static final int UPLOAD_OVERWRITE_FOLDER = 317;

  public static final int UPLOAD_CANCEL = 314;
  //response code send after canceling the upload

  public static final int UPLOAD_CANCELED = 315;
  //request code send when telling overwrite option to the server in case of copy

 // public static final int OVERWRITE_OPTION_UPLOAD = 501;
  //request code send when telling overwrite option to the server in case of folder copy
  
  public static final int OVERWRITE_OPTION_UPLOAD_FOLDER = 511;
  //request code send when telling overwrite option to the server in case of folder copy
   
  public static final int OVERWRITE_OPTION_UPLOAD_FILE = 512;
  //request code send when telling overwrite option to the server in case of file copy

  public static final int OVERWRITE_OPTION_FOLDER_COPY = 502;
  //request code send when telling overwrite option to the server in case of file copy

  public static final int OVERWRITE_OPTION_FILE_COPY = 503;
  //request code send when telling overwrite option to the server in case of folder move

  public static final int OVERWRITE_OPTION_FOLDER_MOVE = 504;
  //request code send when telling overwrite option to the server in case of file move

  public static final int OVERWRITE_OPTION_FILE_MOVE = 505;
  //various options in case of overwrites

  public static final int OVERWRITE_YES = 506;               

  public static final int OVERWRITE_NO = 507;

  public static final int OVERWRITE_YESALL = 508;

  //public static final int OVERWRITE_NOALL = 509;                            saurabh_remove

  public static final int OVERWRITE_CANCEL = 510;

  //response code send when error occurs
  public static final int COPY_CREATE_FOLDER = 801;
  
  public static final int COPY_CREATED_FOLDER = 802;
  
  public static final int ERROR_MESSAGE = 400;
  //response code send indicating detal error

  public static final int FETAL_ERROR = 401;

  public static final String FOR_BROWSER = "FOR_BROWSER";

  public static final String FOR_CLIENTGUI = "FOR_CLIENTGUI";

  public static final String FOR_FILESYNCPREVIEW = "FOR_FILESYNCPREVIEW";

  public static final String FOR_UPLOAD = "FOR_UPLOAD";

  public static final String FOR_DOWNLOAD = "FOR_DOWNLOAD";

  public static final String FOR_COPY = "FOR_COPY";

  public static final String FOR_MOVE = "FOR_MOVE";

  public static final String FOR_LOCAL_FILESYSTEM = "FOR_LOCAL_FILESYSTEM";

  public static final String FOR_REMOTE_FILESYSTEM = "FOR_REMOTE_FILESYSTEM";

  public static final int REMOTE_ITEM_COPY = 701;

  public static final int REMOTE_COPY_STARTED = 702;

  public static final int REMOTE_FILE_COPIED = 703;

  public static final int REMOTE_ITEM_COPIED = 704;

  public static final int REMOTE_COPY_CANCEL = 705;

  public static final int REMOTE_COPY_CANCELLED = 706;

  public static final int REMOTE_COPY_FAILED = 707;

  public static final int REMOTE_ITEM_MOVE = 708;

  public static final int REMOTE_MOVE_STARTED = 709;

  public static final int REMOTE_FILE_MOVED = 710;

  public static final int REMOTE_ITEM_MOVED = 711;

  public static final int REMOTE_MOVE_CANCEL = 712;

  public static final int REMOTE_MOVE_CANCELLED = 713;

  public static final int REMOTE_MOVE_FAILED = 714;

  //request code send when sync upload is started

  public static final int SYNC_UPLOAD_START = 800;
  //request code send when sync download is started

  public static final int SYNC_DOWNLOAD_START = 801;
  //request code send when sync delete an item

  public static final int SYNC_DELETE_ITEMS = 802;
  //code send when sync delete failure is there

  public static final int SYNC_DELETE_FAILURE = 803;
  //request code send to stop the sync process

  public static final int STOP_SYNC = 804;
  //response code send when sync process is stopped

  public static final int SYNC_STOPPED = 805;

  private Object data;

  private Object data1;

  private Object data2;

  private Object datas[];

  private FsExceptionHolder fsExceptionHolder;

  private int operation;
  /**
   * To set data to specified Object object.
   * @param data Object object to be set as data.
   */
  public void setData(Object data) {
    this.data = data;
  }

  /**
   * To get data.
   * @return Object object containing data.
   */
  public Object getData() {
    return data;
  }

  /**
   * To set data1.
   * @param data1 Object object to be set as data1.
   */
  public void setData1(Object data1) {
    this.data1 = data1;
  }

  /**
   * To get data1.
   * @return data1 Object object.
   */
  public Object getData1() {
    return data1;
  }

  /**
   * To set data2.
   * @param data2 Object object to be set as data2.
   */
  public void setData2(Object data2) {
    this.data2 = data2;
  }

  /**
   * To get data2.
   * @return data2 Object object.
   */
  public Object getData2() {
    return data2;
  }

  /**
   * To set datas.
   * @param datas array of Object object to be set as datas.
   */
  public void setDatas(Object[] datas) {
    this.datas = datas;
  }

  /**
   * To get datas.
   * @return datas array of Object objects..
   */
  public Object[] getDatas() {
    return datas;
  }

  /**
   * To set fsExceptionHolder.
   * @param fsExceptionHolder FsExceptionHolder object to be set 
   * as fsExceptionHolder.
   */
  public void setFsExceptionHolder(FsExceptionHolder fsExceptionHolder) {
    this.fsExceptionHolder = fsExceptionHolder;
  }


  /**
   * To get fsExceptionHolder.
   * @return fsExceptionHolder FsExceptionHolder object.
   */
  public FsExceptionHolder getFsExceptionHolder() {
    return fsExceptionHolder;
  }

  /**
   * gives String representation of FsMessage object.
   * @return String repersentation of FsMessage object.
   */
  public String toString() {
    return this.toString();
  }

  public void setOperation(int operation) {
    this.operation = operation;
  }

  public int getOperation() {
    return operation;
  }
}
