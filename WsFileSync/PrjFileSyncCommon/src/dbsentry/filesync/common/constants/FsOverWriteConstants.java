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
 * @description This Interface is illustrates for All the overwrite options for File/Folder. 
 * @Date of creation: 09-03-2006
 * @Last Modfied by : saurabh Gupta
 * @Last Modfied Date: 09-04-2006
 */
public interface FsOverWriteConstants {

  public static final int OVERWRITE_FOLDER=101;
  
  public static final int OVERWRITE_FILE=102;
  
  public static final int OVERWRITE_YES=103;
  
  public static final int OVERWRITE_YES_TO_ALL=104;
  
  public static final int OVERWRITE_NO=105;
  
  public static final int OVERWRITE_CANCEL=106; 
  
  public static final int OVERWRITE_OPTION_FOLDER=107;
  
  public static final int OVERWRITE_OPTION_FILE=108;
  
  public static final int PROMPT_OVERWRITE_FOLDER=109;
  
  public static final int PROMPT_OVERWRITE_FILE=110;
}
