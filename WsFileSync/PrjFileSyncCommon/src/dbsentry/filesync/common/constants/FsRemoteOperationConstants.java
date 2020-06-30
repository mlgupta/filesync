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
 * @description This Interface is illustrates for Remote Operation Constants.
 * @Date of creation: 09-03-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public interface FsRemoteOperationConstants {

  public static final int UPLOAD=1101;
  
  public static final int DOWNLOAD=1102;
  
  public static final int COPY=1103;
  
  public static final int MOVE=1104;
  
  public static final int COMMAND=1105;
  
  public static final int SYNC_COMMAND=1106;
  
  public static final int SYNC_UPLOAD=1107;
  
  public static final int SYNC_DOWNLOAD=1108;  

  public static final int AUTHENTICATION=1109;
  
  public static final int DISCONNECTION=1110;
  
  public static final int REMOTE_BROWSER=1111;
}
