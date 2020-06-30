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
 * @description This Interface is illustrates for Downloading options for File/Folder.
 * @Date of creation: 09-03-2006
 * @Last Modfied by : Saurabh Gupta 
 * @Last Modfied Date: 12-04-2006
 */
public interface FsDownloadConstants extends FsOverWriteConstants {

  public static final int START=301;
  
  public static final int STARTED=302;
  
  public static final int CREATE_FOLDER=303;
  
  public static final int FOLDER_CREATED=304;
  
  public static final int CREATE_EMPTY_FILE=305;
  
  public static final int EMPTY_FILE_CREATED=306;
  
  public static final int COMPLETE =307;
  
  public static final int COMPLETED=308 ;
  
  public static final int FAILED=309;
  
  public static final int APPEND_TO_FILE=310;
  
  public static final int APPENDED_TO_FILE=311;
  
  public static final int CLOSE_FILE=312;
  
  public static final int FILE_CLOSED=313;
  
  public static final int CANCEL=314;
  
  public static final int CANCLED=315;
  
  public static final int PROGRESS=316;
  
  public static final int ERROR_MESSAGE=317;
  
  public static final int FETAL_ERROR=318;
  
  public static final int CREATE_SOCKET_4_DOWNLOAD_FILE=319;
  
  public static final int SOCKET_4_DOWNLOAD_FILE_CREATED=320;
  
  public static final int SOCKET_CLOSE_FILE=321;
  
  public static final int PROGRESS_BUILDING=322;
    
}
