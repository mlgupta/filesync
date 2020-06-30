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
 * @description This Interface is illustrates for MoveConstants for File/Folder.
 * @Date of creation: 09-03-2006
 * @Last Modfied by : saurabh Gupta
 * @Last Modfied Date: 12-04-2006
 */
public interface FsMoveConstants extends FsOverWriteConstants {

  public static final int MOVE=501;
  
  public static final int START=502;
  
  public static final int STARTED=503;
  
  public static final int MOVED=504;
  
  public static final int CANCEL=505;
  
  public static final int CANCLED=506;
  
  public static final int FAILED=507;
  
  public static final int PROGRESS=508;
  
  public static final int NEXT_ITEM=509;
  
  public static final int COMPLETED=510;
  
  public static final int ERROR_MESSAGE=511;
  
  public static final int FATEL_ERROR=512;
}
