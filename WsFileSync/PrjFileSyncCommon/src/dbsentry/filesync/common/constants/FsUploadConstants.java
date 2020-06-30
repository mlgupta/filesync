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
 * $Id$
 *****************************************************************************
 */
package dbsentry.filesync.common.constants;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description This Interface is illustrates for Uploading options for File/Folder.
 * @Date of creation: 09-03-2006
 * @Last Modfied by : Saurabh gupta 
 * @Last Modfied Date: 11-04-2006
 */
public interface FsUploadConstants extends FsOverWriteConstants {

  public static final int START=201;
  
  public static final int STARTED=202;
  
  public static final int CREATE_FOLDER=203;
  
  public static final int FOLDER_CREATED=204;
  
  public static final int CREATE_SOCKET_4_UPLOAD_FILE=205;
  
  public static final int SOCKET_4_UPLOAD_FILE_CREATED=206;
  
  public static final int COMPLETE=207;
  
  public static final int COMPLETED=208;
  
  public static final int FAILED=209;
  
  public static final int APPEND_TO_FILE=210;
  
  public static final int APPENDED_TO_FILE=211;
  
  public static final int CLOSE_FILE=212;
  
  public static final int FILE_CLOSED=213;
  
  public static final int CANCEL=214;
  
  public static final int CANCLED=215;
  
  public static final int PROGRESS=216;
  
  public static final int FOLDER_NOT_CREATED=217;
  
  public static final int PROGRESS_BUILDING=218;
  
  public static final int PROGRESS_ERROR=219;

  public static final int FILE_CURRUPTED=220;
  
  public static final int ERROR_MESSAGE=221;
  
  public static final int FETAL_ERROR=222;
  
  public static final int SOCKET_COMPLETE=223;
  
  public static final int SOCKET_COMPLETED=224;
  
  public static final int SOCKET_CLOSE_FILE=225;

}
