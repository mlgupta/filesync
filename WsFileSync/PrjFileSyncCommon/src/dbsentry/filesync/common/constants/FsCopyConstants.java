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
 * @description This Interface is illustrates for CopyConstants for File/Folder.
 * @Date of creation: 09-03-2006
 * @Last Modfied by : Saurabh Gupta
 * @Last Modfied Date: 12-04-2006
 */
public interface FsCopyConstants  extends FsOverWriteConstants {

  public static final int COPY=401;
  
  public static final int START=402;
  
  public static final int STARTED=403;
  
  public static final int COMPLETED=404;
  
  public static final int CANCEL=405;
  
  public static final int CANCLED=406;
  
  public static final int FAILED=407;
  
  public static final int COPY_PROGRESS=408;
  
  public static final int NEXT_ITEM=409;
  
  public static final int ERROR_MESSAGE=410;
  
  public static final int FETAL_ERROR=411;

}
