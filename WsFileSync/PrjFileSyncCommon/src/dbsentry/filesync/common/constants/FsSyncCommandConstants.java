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
 * $Id: FsSyncCommandConstants.java,v 1.2 2006/04/13 08:45:38 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common.constants;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description
 * @Date of creation: 31-03-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public interface FsSyncCommandConstants {

  public static final int PREVIEW=1401;
  
  public static final int STOPPED=1402;
  
  public static final int REMOTE_DELETE=1403;
  
  public static final int REMOTE_DELETE_FAILURE=1404;
  
  public static final int FAILED=1405;
  
  public static final int ERROR_MESSAGE=1406;
  
  public static final int FETAL_ERROR=1407;
}
