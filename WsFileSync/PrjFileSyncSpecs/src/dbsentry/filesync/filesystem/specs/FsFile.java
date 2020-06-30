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
 * $Id: FsFile.java,v 1.5 2006/02/20 15:37:37 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.filesystem.specs;

import java.io.InputStream;


/**
 *	To access the attributes of file.
 *  @author Jeetendra Prasad
 *  @version 1.0
 * 	Date of creation: 10-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public interface FsFile extends FsObject {

  /**
   * To get the InputStream of file.
   * @return InputStream to perform I/O operation.
   * @throws dbsentry.filesync.filesystem.specs.FsException if unable to obtain the inputstream of 
   * file.
   */
  public InputStream getInputStream() throws FsException;

  /**
   * To get the mimeType of file.
   * @return mimeType of file.
   */
  public String getMimeType();

  /**
   * To get the file size in bytes.
   * @return long size in bytes.
   */
  public long getSize();

}
