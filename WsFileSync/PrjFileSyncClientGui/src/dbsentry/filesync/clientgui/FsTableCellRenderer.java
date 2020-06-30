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
 * $Id: $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description custom TableCellRenderer.
 * @Date of creation: 14-04-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public class FsTableCellRenderer extends DefaultTableCellRenderer{
  public FsTableCellRenderer() {
    super();
  }
  
  /**
   * sets value in the column.
   * @param value value to be set
   */
  public void setValue(Object value) {
    if (value instanceof JLabel) {
      JLabel label = (JLabel)value;
      setText(label.getText());
      setIcon(label.getIcon());
      Font labelFont = label.getFont();
      Font font = new Font(labelFont.getName(), 0, labelFont.getSize());
      setFont(font);
      setHorizontalAlignment(label.getHorizontalAlignment());
    } else {
      super.setValue(value);
    }
  }
  
  /**
   * override validate function.
   */
  public void validate() {
  }
  
  /**
   * overrides firePropertyChange function.
   * @param propertyName name of the property
   * @param oldValue old value of the property
   * @param newValue new value of the property
   */
  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
  }
  
  /**
   * overrides firePropertyChange function.
   * @param propertyName name of the property
   * @param oldValue old value of the property
   * @param newValue new value of the property
   */
  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
  }
  
}
