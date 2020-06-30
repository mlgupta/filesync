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
 * $Id: EnumRemoteTable.java,v 1.5 2005/07/11 07:35:01 jeet Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui.enumconstants;

/**
 *	An interface which defines a few constants used in remote browser table.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   11-07-2005
 */
public interface EnumRemoteTable {
  public static final String[] COLUMN_NAMES =
  { "Name", "Size", "Type", "Modified", " ","Description", "Permission", "ItemType", "FilePath","AbsSize","AbsModified" };

  public static final int COLUMN_LENGTH = 11;

  public static final int COLUMN_DISPLAY_LENGTH = 5;

  public static final int NAME = 0;

  public static final int SIZE = 1;

  public static final int TYPE = 2;

  public static final int MODIFIED = 3;
  
  public static final int EMPTY_COLUMN = 4;

  public static final int DESCRIPTION = 5;

  public static final int PERMISSION = 6;

  public static final int ITEM_TYPE = 7;

  public static final int ABS_PATH = 8;
  
  public static final int ABS_SIZE = 9;
  
  public static final int ABS_MODIFIED = 10;
}
