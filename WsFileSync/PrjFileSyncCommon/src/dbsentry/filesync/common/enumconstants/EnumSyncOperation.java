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
 package dbsentry.filesync.common.enumconstants;

public interface EnumSyncOperation {
  public static final String NEW_FILE = "New File";

  public static final String NEW_FOLDER = "New Folder";

  public static final String FILE_CHANGED = "File Changed";

  public static final String FILE_DELETED = "File Deleted";

  public static final String FILE_UNCHANGED = "";

  public static final String FOLDER_UNCHANGED = "";

  public static final String FOLDER_DELETED = "Folder Deleted";

  public static final String NEW_FOLDER_REMOTE = "Create New folder on remote system";

  public static final String NEW_FILE_REMOTE = "Create New file on remote system";

  public static final String DELETE_FOLDER_REMOTE = "Delete folder from remote system";

  public static final String DELETE_FILE_REMOTE = "Delete file from remote system";

  public static final String UPDATE_FILE_REMOTE = "Update file on remote system";

  public static final String NEW_FOLDER_LOCAL = "Create New folder on local system";

  public static final String NEW_FILE_LOCAL = "Create New file on local system";

  public static final String DELETE_FOLDER_LOCAL = "Delete folder from local system";

  public static final String DELETE_FILE_LOCAL = "Delete file from local system";

  public static final String UPDATE_FILE_LOCAL = "Update file on local system";

  public static final String DO_NOTHING = "Do nothing";

}
