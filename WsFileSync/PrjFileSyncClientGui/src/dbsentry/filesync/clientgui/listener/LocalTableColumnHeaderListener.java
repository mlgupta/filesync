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
 * $Id: LocalTableColumnHeaderListener.java,v 1.12 2006/05/08 09:40:24 skumar Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui.listener;

import dbsentry.filesync.clientgui.enumconstants.EnumLocalTable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;


/**
 *	A custom Column Header Listener for local table.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Jeetendra Prasad
 * 	Last Modfied Date:   11-07-2005
 */
public class LocalTableColumnHeaderListener extends MouseAdapter {
  /**
   * sort order for the "name" column in the table
   */
  private boolean sortOrderNameAsc = true;
  private boolean sortOrderSizeAsc = true;
  private boolean sortOrderModifiedAsc = true;
  private boolean sortOrderTypeAsc = true;

  /**
   * handles mouse click event.
   * @param evt MouseEvent object
   */
  public void mousePressed(MouseEvent evt) {
    JTable table = ((JTableHeader)evt.getSource()).getTable();
    TableColumnModel colModel = table.getColumnModel();

    // The index of the column whose header was clicked
    int vColIndex = colModel.getColumnIndexAtX(evt.getX());

    // Return if not clicked on any column header
    if (vColIndex == -1) {
      return;
    } else {
      //sort the table based on this row
      if (vColIndex == EnumLocalTable.NAME) {
        sortOrderNameAsc = !sortOrderNameAsc;
        dbsentry.filesync.clientgui.utility.GeneralUtil.sortAllRowsBy(table, vColIndex, sortOrderNameAsc);
      }
      if (vColIndex == EnumLocalTable.SIZE) {
        sortOrderSizeAsc = !sortOrderSizeAsc;
        dbsentry.filesync.clientgui.utility.GeneralUtil.sortAllRowsBy(table, EnumLocalTable.ABS_SIZE, sortOrderSizeAsc);
      }
      if (vColIndex == EnumLocalTable.TYPE) {
        sortOrderTypeAsc = !sortOrderTypeAsc;
        dbsentry.filesync.clientgui.utility.GeneralUtil.sortAllRowsBy(table, vColIndex, sortOrderTypeAsc);
      }
      if (vColIndex == EnumLocalTable.MODIFIED) {
        sortOrderModifiedAsc = !sortOrderModifiedAsc;
        dbsentry.filesync.clientgui.utility.GeneralUtil.sortAllRowsBy(table, EnumLocalTable.ABS_MODIFIED, sortOrderModifiedAsc);
      }
      
      
    }
  }

  /**
   * check the sort order of the name column.
   * @return boolean value indicating sort order of name column.
   */
  public boolean isSortOrderNameAsc() {
    return sortOrderNameAsc;
  }
  
  public boolean isSortOrderSizeAsc() {
    return sortOrderSizeAsc;
  }
  
  public boolean isSortOrderTypeAsc() {
    return sortOrderTypeAsc;
  }
  
  public boolean isSortOrderModifiedAsc() {
    return sortOrderModifiedAsc;
  }
  
}
