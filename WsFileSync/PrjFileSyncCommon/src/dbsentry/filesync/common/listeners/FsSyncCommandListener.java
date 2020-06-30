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
 * $Id: FsSyncCommandListener.java,v 1.1 2006/04/10 11:25:38 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common.listeners;
 
import dbsentry.filesync.common.constants.FsSyncCommandConstants;

import java.beans.PropertyChangeListener;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description
 * @Date of creation: 31-03-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public interface FsSyncCommandListener extends PropertyChangeListener,FsSyncCommandConstants {
}
