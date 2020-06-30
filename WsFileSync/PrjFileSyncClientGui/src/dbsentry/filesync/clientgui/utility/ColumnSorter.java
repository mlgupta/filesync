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
 * $Id: ColumnSorter.java,v 1.6 2006/04/25 13:56:34 skumar Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui.utility;

import java.util.Comparator;
import java.util.Vector;

import javax.swing.JLabel;


/**
 *	This class is used to sort vectors of data.
 *  @author              Jeetendra Parasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Jeetendra Parasad
 * 	Last Modfied Date:   11-07-2005
 */
public class ColumnSorter implements Comparator {
  private int colIndex;

  private boolean ascending;

  private static final int ITEM_TYPE = 6;

  /**
   * Constructs ColumnSorter object.
   * @param colIndex column index to be used for sorting
   * @param ascending order of sorting
   */
  public ColumnSorter(int colIndex, boolean ascending) {
    this.colIndex = colIndex;
    this.ascending = ascending;
    
  }

  /**
   * Compares the two file object.
   * @param a object one
   * @param b object two
   * @return the result of comparision
   */
  public int compare(Object a, Object b) {
    
    Vector v1 = (Vector)a;
    Vector v2 = (Vector)b;
    String name1 = ((JLabel)v1.get(colIndex)).getText();
    String name2 = ((JLabel)v2.get(colIndex)).getText();

    String itemType1 = ((JLabel)v1.get(ITEM_TYPE)).getText();
    String itemType2 = ((JLabel)v2.get(ITEM_TYPE)).getText();

    if (itemType1.equals(itemType2)) {
      if (ascending) {      
        return (name1.compareToIgnoreCase(name2));
      }else {        
          return (name2.compareToIgnoreCase(name1));
       }
    } else {
      if (ascending) {
        if (itemType1.equals("Folder")) {
          return "File".compareTo("Folder");
        } else {
          return "Folder".compareTo("File");
        }
      } else {
        if (itemType1.equals("Folder")) {
          return "Folder".compareTo("File");
        } else {
          return "File".compareTo("Folder");
        }
      }
    }
  }

}
