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
 * $Id: EnumClipBoardOperation.java,v 1.5 2005/07/06 12:18:05 jeet Exp $
 *****************************************************************************
 */
package dbsentry.filesync.client.enumconstants;

/**
 *	refers clipboard operations by integer values . 
 *  @author            Jeetendra Prasad
 *  @version            1.0
 * 	Date of creation:    6-05-2005
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   06-07-2005
 */
public interface EnumClipBoardOperation {
  /**
   * represents clip board operation cut.
   */
  public static final int CUT = 1;

  /**
   * represents clip board operation copy.
   */
  public static final int COPY = 2;
}
