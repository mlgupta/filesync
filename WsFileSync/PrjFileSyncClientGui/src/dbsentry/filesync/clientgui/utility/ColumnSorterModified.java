
package dbsentry.filesync.clientgui.utility;

import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import javax.swing.JLabel;

public class ColumnSorterModified implements Comparator{
  private int colIndex;

  private boolean ascending;

  private static final int ITEM_TYPE = 7;


  public ColumnSorterModified(int colIndex, boolean ascending) {
    this.colIndex = colIndex;
    this.ascending = ascending;
  }
  
  
  public int compare(Object a, Object b) {
    
    Vector v1 = (Vector)a;
    Vector v2 = (Vector)b;
    String name1 = ((JLabel)v1.get(colIndex)).getText();
    String name2 = ((JLabel)v2.get(colIndex)).getText();
    
    Long longName1=Long.parseLong(name1.trim());
    Long longName2=Long.parseLong(name2.trim());
    Date date1=new Date(longName1);
    Date date2=new Date(longName2);
    

    String itemType1 = ((JLabel)v1.get(ITEM_TYPE)).getText();
    String itemType2 = ((JLabel)v2.get(ITEM_TYPE)).getText();

    if (itemType1.equals(itemType2)) {
      if (ascending) {
        return (date1.compareTo(date2));
      } else {        
        return (date2.compareTo(date1));
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
