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
 * @description This Interface is illustrates for Connection Constants for Availability In LAN/INTERNET.
 * @Date of creation: 09-03-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public interface FsConnectionConstants {

  public static final int CONNECT=601;
  
  public static final int LOCATING_IN_LAN=602;
  
  public static final int EXIST_IN_LAN = 603;
  
  public static final int CONNECTING_TO_INTERNET=604;
  
  public static final int IS_INTERNET_AVAILABLE=605;
  
  public static final int LOCATING_IN_INTERNET=606;
  
  public static final int EXIST_IN_INTERNET =607;
  
  public static final int CONNECTING = 608;
  
  public static final int CONNECTED=609;
}
