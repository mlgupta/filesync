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
 * @description This Interface is illustrates for Command Constants Using in General Operations.
 * @Date of creation: 09-03-2006
 * @Last Modfied by :Saurabh Gupta
 * @Last Modfied Date:11-04-2006
 */
public interface FsCommandConstants {

  public static final int MK_DIR=801;
  
  public static final int CH_DIR=802;
  
  public static final int DELETE=803;
  
  public static final int RENAME=804;
  
  public static final int NAVIGATE_UP=805;
  
  public static final int NAVIGATE_BACK=806;
  
  public static final int NAVIGATE_FORWARD=807;
  
  public static final int CH_MODE_FILE=808;
  
  public static final int CH_MODE_FOLDER=809;
  
  public static final int CH_MODE_FILE_N_FOLDER=810;
  
  public static final int SEARCH=811;
  
  public static final int SEARCH_FAILED=812;  

  public static final int GET_ROOT_FOLDERS=813;  

  public static final int GET_HOME_FOLDER=814;  

  public static final int GET_FOLDER_ROOT=815;
  
  public static final int GET_FLAT_FOLDER_TREE=816;

  public static final int GETCONTENTOFFOLDER=817;
  
  public static final int GETCONTENTOFPARENTFOLDER=818;
  
  public static final int GET_FILE_FOLDER_PROPERTIES=819;
  
  public static final int FILE_PROPERTYPAGE=820;
  
  public static final int FOLDER_PROPERTYPAGE=821;
  
  public static final int FILE_FOLDER_PROPERTYPAGE=822;

  public static final int CHANGE_PROPERTIES_OF_FOLDER=823;
  
  public static final int CHANGE_PROPERTIES_OF_FILE=824;
  
  public static final int ERROR_MESSAGE=825;
  
  public static final int FETAL_ERROR=826;
  
}
