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
 * $Id: FsTableHeaderCellRenderer.java,v 1.8 2006/06/30 11:32:02 sudheer Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;


/**
 *	A custom Table Header Cell Renderer.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   11-07-2005
 */
public class FsTableHeaderCellRenderer extends DefaultTableCellRenderer {
  private String cellLabel;
  
  /**
   * Construct FsTableHeaderCellRenderer object.
   */
  public FsTableHeaderCellRenderer(String cellLabel) {
    super();
    this.cellLabel=cellLabel;
  }

  /**
   * overrides getTableCellRendererComponent function of the super class.
   * @param table table of the renderer
   * @param value value to be rendered
   * @param isSelected indicates if the cell is selected.
   * @param hasFocus indicates if the cell has focus.
   * @param row row index of this cell
   * @param column column index of this cell.
   * @return the rendered component.
   */
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                 int row, int column) {
    // Inherit the colors and font from the header component
    if (table != null) {
      JTableHeader header = table.getTableHeader();
      if (header != null) {
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
      }
    }
    

    if (value instanceof ImageIcon) {
      setIcon((ImageIcon)value);
    }
    setText(cellLabel);
    setHorizontalAlignment(JLabel.CENTER);
    setHorizontalTextPosition(JLabel.LEFT);
    setBorder(BorderFactory.createEtchedBorder());
    return this;
  }

  // The following methods override the defaults for performance reasons

  /**
   * The following methods override the defaults for performance reasons.
   */
  public void validate() {
  }

  /**
   * The following methods override the defaults for performance reasons.
   */
  public void revalidate() {
  }

  /**
   * The following methods override the defaults for performance reasons.
   * @param propertyName property name
   * @param oldValue old value
   * @param newValue new value
   */
  protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
  }

  /**
   * The following methods override the defaults for performance reasons.
   * @param propertyName property name
   * @param oldValue old value
   * @param newValue new value
   */
  public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
  }
}
