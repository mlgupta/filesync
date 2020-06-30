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
 * $Id: FsFolder.java,v 1.4 2006/02/20 15:37:37 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.filesystem.specs;


/**
 *	To manipulate the folder.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public interface FsFolder extends FsObject {

  /**
   * To list the contents of this folder.
   * @return FsObject array ,which holds contents of this folder.
   * @throws dbsentry.filesync.filesystem.specs.FsException if failed to list the 
   * contents of folder.
   */
  public FsObject[] listContentOfFolder() throws FsException;

  /**
   * To check if this folder has sub folders.
   * @return boolean hasubfolders true/false 
   * @throws dbsentry.filesync.filesystem.specs.FsException if failed to check whether 
   * this folder has subfolders.
   */
  public boolean hasSubfolders() throws FsException;
}
