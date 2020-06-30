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

import java.util.Vector;

import javax.swing.table.AbstractTableModel;


/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description Custom TableModel
 * @Date of creation: 14-04-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public class FsTableModel extends AbstractTableModel{
  private String columnNames[];

  private Vector data;
  
  /**
   * custom TableModel constructor.
   * @param columnNames array of column names
   * @param data vector of data for the table
   */
  public FsTableModel(String[] columnNames, Vector data) {
    this.columnNames = columnNames;
    this.data = data;
  }
  
  /**
   * getter for column count.
   * @return  column count
   */
  public int getColumnCount() {
    return columnNames.length;
  }
  
  /**
   * getter for row count.
   * @return row count
   */
  public int getRowCount() {
    return data.size();
  }
  
  /**
   * getter for column name.
   * @param col column index
   * @return column name
   */
  public String getColumnName(int col) {
    return columnNames[col];
  }

  /**
   * getter for value at given row column index.
   * @param row row index
   * @param col column index
   * @return value at given row column index
   */
  public Object getValueAt(int row, int col) {
    return ((Vector)data.get(row)).get(col);
  }
  
  /**
   * getter for column class type.
   * @param c column index
   * @return class type of a perticular column
   */
  public Class getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }
  
  /**
   * setter for value at row and column index.
   * @param value value to be set
   * @param row row index
   * @param col column index
   */
  public void setValueAt(Object value, int row, int col) {
    ((Vector)data.get(row)).set(col, value);
    fireTableCellUpdated(row, col);
  }

  /**
   * getter for data vector.
   * @return vector of data
   */
  public Vector getDataVector() {
    return data;
  }

  /**
   * setter for data vector.
   * @param data vector to be set
   */
  public void setDataVector(Vector data) {
    this.data = data;
    this.fireTableDataChanged();
  }
  
}
