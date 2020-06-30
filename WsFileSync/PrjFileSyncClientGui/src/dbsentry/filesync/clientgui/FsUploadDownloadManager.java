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
 
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsFolderHolder;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.listeners.FsDownloadListener;
import dbsentry.filesync.common.listeners.FsUploadListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;

import java.io.File;
import java.io.IOException;

import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;

import org.apache.log4j.Logger;


//import dbsentry.filesync.client.FsTableCellRenderer;
//import dbsentry.filesync.client.FsTableModel;


/* Used by FileSyncClientMDI.java. */

/**
 * @author Saurabh Gupta
 * @version 1.1
 * @Date of creation:
 * @Last Modfied by : Saurabh Gupta
 * @Last Modfied Date: 01-08-2006
 */
public class FsUploadDownloadManager
  extends JInternalFrame {


  private JTable table4Manager = new JTable();

  private JScrollPane scrollPane = new JScrollPane();

  private JPanel panel4Manager = new JPanel(new BorderLayout());

  private JToolBar toolBar = new JToolBar();

  private JButton butCancel = new JButton();

  private JButton butDelete = new JButton();
  
  private JButton butHide = new JButton();

  private FsTableModel fsTableModel;

  private Logger logger;

  private FsExplorer explorer;

  private Vector dataRow = new Vector(1000);

  

  public FsUploadDownloadManager(Logger logger, FsExplorer explorer) {

    super("File Sync Upload/Download", true, true, true, true);
    this.logger = logger;
    this.explorer = explorer;
    try {

      moveToFront();
      jbInit();
      createTable();

    }
    catch (Exception e) {
      e.printStackTrace();
    }


  }

  private void jbInit()
    throws Exception {


    //Upload's  Delete Button 
    butDelete.setToolTipText("Delete Item From List");
    butDelete.setIcon(FsImage.imageProcessDelete);
    butDelete.setOpaque(false);
    butDelete.setMargin(new Insets(0, 0, 0, 0));
    butDelete.setPreferredSize(new Dimension(30, 30));
    butDelete.setMinimumSize(new Dimension(30, 30));
    butDelete.setMaximumSize(new Dimension(30, 30));
    butDelete.setSize(new Dimension(30, 30));
    butDelete.setBorder(BorderFactory.createEmptyBorder());
    butDelete.addMouseListener(new MouseAdapter() {
                                 public void mouseEntered(MouseEvent e) {
                                   button_mouseEntered(e);
                                 }

                                 public void mouseExited(MouseEvent e) {
                                   button_mouseExited(e);
                                 }

                                 public void mousePressed(MouseEvent e) {
                                   button_mousePressed(e);

                                 }

                                 public void mouseReleased(MouseEvent e) {
                                   button_mouseReleased(e);
                                 }
                               });
    butDelete.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                    butDelete_actionPerformed();
                                  }
                                });

    //Upload's  Cancel Button 
    butCancel.setToolTipText("Cancel The Operation ");
    butCancel.setIcon(FsImage.imageProcessCancel);
    butCancel.setOpaque(false);
    butCancel.setMargin(new Insets(0, 0, 0, 0));
    butCancel.setPreferredSize(new Dimension(30, 30));
    butCancel.setMinimumSize(new Dimension(30, 30));
    butCancel.setMaximumSize(new Dimension(30, 30));
    butCancel.setSize(new Dimension(30, 30));
    butCancel.setBorder(BorderFactory.createEmptyBorder());
    butCancel.addMouseListener(new MouseAdapter() {
                                 public void mouseEntered(MouseEvent e) {
                                   button_mouseEntered(e);
                                 }

                                 public void mouseExited(MouseEvent e) {
                                   button_mouseExited(e);
                                 }

                                 public void mousePressed(MouseEvent e) {
                                   button_mousePressed(e);
                                 }

                                 public void mouseReleased(MouseEvent e) {
                                   button_mouseReleased(e);
                                 }
                               });
    butCancel.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                    butCancel_actionPerformed();
                                  }
                                });
                                
    //upload/download manager close button
     butHide.setToolTipText("Hide Upload/Download Manager ");
     butHide.setOpaque(false);
     butHide.setIcon(FsImage.imageProcessHide);
     butHide.setMargin(new Insets(0, 0, 0, 0));
     butHide.setPreferredSize(new Dimension(30, 30));
     butHide.setMinimumSize(new Dimension(30, 30));
     butHide.setMaximumSize(new Dimension(30, 30));
     butHide.setSize(new Dimension(30, 30));
     butHide.setBorder(BorderFactory.createEmptyBorder());
     butHide.addMouseListener(new MouseAdapter() {
                                  public void mouseEntered(MouseEvent e) {
                                    button_mouseEntered(e);
                                  }

                                  public void mouseExited(MouseEvent e) {
                                    button_mouseExited(e);
                                  }

                                  public void mousePressed(MouseEvent e) {
                                    button_mousePressed(e);
                                  }

                                  public void mouseReleased(MouseEvent e) {
                                    button_mouseReleased(e);
                                  }
                                });
     butHide.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                     butHide_actionPerformed();
                                   }
                                 });
                                

    //Preparing Upload  Toolbar
    toolBar.setFloatable(false);
    toolBar.setBorder(BorderFactory.createEtchedBorder());
    toolBar.add(butDelete, null);
    toolBar.add(butCancel, null);
    toolBar.add(butHide, null);
    
    //Scrollpane for Upload 
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getViewport().add(table4Manager, null);
    
    //Panel for Total Upload Download Manager
    panel4Manager.setBackground(Color.white);
    panel4Manager.setBorder(BorderFactory.createEmptyBorder());
    panel4Manager.setBounds(new Rectangle(0, 30, 890, 530));
    panel4Manager.add(scrollPane, BorderLayout.CENTER);
    
    this.setBorder(BorderFactory.createEmptyBorder());
    this.setSize(new Dimension(593, 465));
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(panel4Manager, BorderLayout.CENTER);
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
    
  }


  /**
    * Gives a raised effect to button when mouse entered init.
    * @param e MouseEvent object
    */
  private void button_mouseEntered(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    if (button.isEnabled()) {
      button.setBorder(BorderFactory.createEtchedBorder());
    }
  }


  /**
    * Makes button border normal when mouse exited from it.
    * @param e MouseEvent object
    */
  private void button_mouseExited(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createEmptyBorder());
  }


  /**
    * Gives a lowered effect to button when mouse pressed on it.
    * @param e MouseEvent object
    */
  private void button_mousePressed(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
  }

  /**
    * Makes normal to a button when mouse released from it.
    * @param e MouseEvent object
    */
  private void button_mouseReleased(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createEmptyBorder());
  }


  private void createTable() {
    
    fsTableModel = new FsTableModel(EnumManagerlTable.COLUMN_NAMES, dataRow);
    table4Manager.setModel(fsTableModel);
    table4Manager.setAutoCreateColumnsFromModel(false);
    table4Manager.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    FsTableCellRenderer fsTableCellRenderer = new FsTableCellRenderer();
    table4Manager.setDefaultRenderer(Object.class, fsTableCellRenderer);
    
    //add click event listener for table header
    JTableHeader tableHeader = table4Manager.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(),18));
    
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.TYPE).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.TYPE]));
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.NAME).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.NAME]));
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.PROGRESS).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.PROGRESS]));
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.STATUS).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.STATUS]));
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.TOTAL_SIZE).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.TOTAL_SIZE]));
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.DATA_TRANSFERED).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.DATA_TRANSFERED]));
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.DATA_REMAINING).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.DATA_REMAINING]));
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.TOTAL_TIME).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.TOTAL_TIME]));
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.TIME_ELAPSED).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.TIME_ELAPSED]));
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.TIME_REMAINING).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.TIME_REMAINING]));
    tableHeader.getColumnModel().getColumn(EnumManagerlTable.SPEED).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumManagerlTable.COLUMN_NAMES[EnumManagerlTable.SPEED]));
    
    DefaultTableColumnModel colModel = (DefaultTableColumnModel)table4Manager.getColumnModel();
    colModel.getColumn(EnumManagerlTable.TYPE).setPreferredWidth(80);
    colModel.getColumn(EnumManagerlTable.NAME).setPreferredWidth(140);
    colModel.getColumn(EnumManagerlTable.PROGRESS).setPreferredWidth(70);
    colModel.getColumn(EnumManagerlTable.STATUS).setPreferredWidth(90);
    colModel.getColumn(EnumManagerlTable.TOTAL_SIZE).setPreferredWidth(80);
    colModel.getColumn(EnumManagerlTable.DATA_TRANSFERED).setPreferredWidth(80);
    colModel.getColumn(EnumManagerlTable.DATA_REMAINING).setPreferredWidth(80);
    colModel.getColumn(EnumManagerlTable.TOTAL_TIME).setPreferredWidth(95);
    colModel.getColumn(EnumManagerlTable.TIME_ELAPSED).setPreferredWidth(100);
    colModel.getColumn(EnumManagerlTable.TIME_REMAINING).setPreferredWidth(100);
    colModel.getColumn(EnumManagerlTable.SPEED).setPreferredWidth(80);

    
    //hide table coulmn contiaining non displayable info
    if(table4Manager.getColumnCount() > EnumManagerlTable
           .COLUMN_DISPLAY_LENGTH){
     for(int index = EnumManagerlTable.COLUMN_DISPLAY_LENGTH; index < EnumManagerlTable
                .COLUMN_LENGTH; index++){
       table4Manager.removeColumn(tableHeader.getColumnModel().getColumn(EnumManagerlTable
                                                                    .COLUMN_DISPLAY_LENGTH));
     }
    } 
  }

  private void butHide_actionPerformed(){
    boolean isCloseUpUpDownManager = false;
    int totalRowIndex[] = new int[table4Manager.getRowCount()];
    //selectedRowIndex = table4Manager.getRowCount();
    for (int i = 0; i < totalRowIndex.length; i++) {
      
      String status = ((JLabel)(fsTableModel.getValueAt(totalRowIndex[i], EnumManagerlTable.STATUS))).getText();

      if ((status.equals("Canceled") || status.equals("Completed") || status.equals("Failed"))) {
          isCloseUpUpDownManager=true;
      }else{
        isCloseUpUpDownManager=false;
      }
    }
    if(isCloseUpUpDownManager){
      int close =
        JOptionPane.showConfirmDialog(explorer, "Do you want to Hide Upload/Download Manager ", "Hide", JOptionPane
                                                 .YES_NO_OPTION);
      if (close == JOptionPane.NO_OPTION) {
      //continue;
      }
      else {
      this.setVisible(false);
      }
      isCloseUpUpDownManager=false;
    }
    
  }

     private void statusChkdUploadDownloadManager(){
       int numberOfRows=table4Manager.getRowCount();
       boolean upDownManagerStatus=false;
       for (int i = 0; i < numberOfRows; i++) {
         String status = ((JLabel)(fsTableModel.getValueAt(i, EnumManagerlTable.STATUS))).getText().trim();
         upDownManagerStatus=(status.equals("Canceled") || status.equals("Completed") || status.equals("Failed"));
         if(!upDownManagerStatus){
          break;
         }
       }
       
       if(upDownManagerStatus){
         logger.debug("upload/download manager value is set close after completed..........."+ explorer.getMdiParent().closeUploadDnloadManager);
         if(explorer.getMdiParent().closeUploadDnloadManager){
           explorer.getMdiParent().getUploadDownloadManager().setVisible(false);
         }
       }
       
     }
  
  private void butCancel_actionPerformed() {
    
    int selectedRowIndex[] = new int[table4Manager.getSelectedRowCount()];
    selectedRowIndex = table4Manager.getSelectedRows();

    if(selectedRowIndex.length==0){
      JOptionPane.showConfirmDialog(explorer,"For cancel operation u should select at lease one row...");
    }

    for (int i = 0; i < selectedRowIndex.length; i++) {

    
      String superRequestCode =
        ((JLabel)(fsTableModel.getValueAt(selectedRowIndex[i], EnumManagerlTable.SUPER_REQUEST_CODE))).getText();
      String status = ((JLabel)(fsTableModel.getValueAt(selectedRowIndex[i], EnumManagerlTable.STATUS))).getText();
      String type = ((JLabel)(fsTableModel.getValueAt(selectedRowIndex[i], EnumManagerlTable.HIDDEN_TYPE))).getText();


      if (!(status.equals("Canceled") || status.equals("Completed") || status.equals("Failed"))) {

        int cancel =
          JOptionPane.showConfirmDialog(explorer, "Do you want to cancel this operation ", "Cancel", JOptionPane
                                                   .YES_NO_OPTION);

        if (cancel == JOptionPane.NO_OPTION) {
          continue;
        }
        else {
          if (type.equals("Upload")) {
            explorer.getMdiParent().getFsClient().uploadCancel(superRequestCode);
          }
          if (type.equals("Download")) {
            explorer.getMdiParent().getFsClient().downloadCancel(superRequestCode);
          }

        }
      }else{
        if (status.equals("Canceled")) {
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("Job Status : already Canceled..........");
        }
        if(status.equals("Completed")){
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("Job Status : Completed, So can not be cancel..........");
        }if(status.equals("Failed")){
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("Job Status : Failed, So can not be cancel.............");
        }
      }
    }

    //explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
  }


  private void butDelete_actionPerformed() {

    int selectedRowIndex[] = new int[table4Manager.getSelectedRowCount()];
    selectedRowIndex = table4Manager.getSelectedRows();
    int noOfRowDeleted = 0;
    for (int i = 0; i < selectedRowIndex.length; i++) {

      

      String superRequestCode =
        ((JLabel)(fsTableModel.getValueAt((selectedRowIndex[i]-noOfRowDeleted ), EnumManagerlTable.SUPER_REQUEST_CODE)))
        .getText();
      String status =
        ((JLabel)(fsTableModel.getValueAt((selectedRowIndex[i]-noOfRowDeleted ), EnumManagerlTable.STATUS))).getText();
      String type =
        ((JLabel)(fsTableModel.getValueAt((selectedRowIndex[i]-noOfRowDeleted ), EnumManagerlTable.HIDDEN_TYPE)))
        .getText();

      if (!(status.equals("Canceled") || status.equals("Completed") || status.equals("Failed"))) {

        int cancel =
          JOptionPane.showConfirmDialog(explorer, "Do you want to cancel this operation ", "Cancel", JOptionPane
                                                   .YES_NO_OPTION);

        if (cancel == JOptionPane.NO_OPTION) {
          continue;
        }
        else {
          if (type.equals("Upload")) {
            explorer.getMdiParent().getFsClient().uploadCancel(superRequestCode);
          }
          if (type.equals("Download")) {
            explorer.getMdiParent().getFsClient().downloadCancel(superRequestCode);
          }
        }
      }

      try {
      
        dataRow.removeElementAt((selectedRowIndex[i]-noOfRowDeleted));
        table4Manager.tableChanged(new TableModelEvent(fsTableModel));
        noOfRowDeleted++;
      }
      catch (Exception e) {
        ;
      }
      
    }
       

  }


  public FsUploadListener createUploadListener(int uploadCode) {
    return (new UploadListener(uploadCode));
  }


  public FsDownloadListener createDownloadListener(int downloadCode) {
    return (new DownloadListener(downloadCode));
  }

  private class UploadListener
    implements FsUploadListener, ActionListener {

    private int uploadCode;

    private Long totalSizeUpload = null;

    private GeneralUtil generalUtil;


    private Timer timer;

    private File fileUpload;

    private long totalData = 0;

    private long dataTransfered = 0;

    private long dataTransferedOld = 0;

    private long dataRemaining;

    private int previousByteRead;

    private long totalTime;

    private long timeElapsed;

    private long timeRemaining;

    private long currentTime;

    private long previousTime;

    private int timeCount;

    private long speed = 0;
    

    private Vector dataCol = new Vector(EnumManagerlTable.COLUMN_LENGTH);


    public UploadListener(int uploadCode) {

      this.uploadCode = uploadCode;
      
      this.generalUtil = new GeneralUtil();
      timer = new Timer(1000, this);
    }

    public void actionPerformed(ActionEvent evt) {

      int rowIndex = getCurrentRowIndex();
      
      if (rowIndex != -1) {
        timeCount += 1;
        currentTime = new Date().getTime();

        //Setting the value of DataTransfered
        
        fsTableModel
        .setValueAt(new JLabel(generalUtil.getTransferSizeForDisplay(dataTransfered / 1024) + "  ", JLabel.RIGHT),
                                rowIndex, EnumManagerlTable.DATA_TRANSFERED);


        //Setting the value of DataRemaining

        this.dataRemaining = totalData - dataTransfered;
        fsTableModel
        .setValueAt(new JLabel(generalUtil.getTransferSizeForDisplay(dataRemaining / 1024) + "  ", JLabel.RIGHT),
                                rowIndex, EnumManagerlTable.DATA_REMAINING);

        //Setting the value of Speed

        
        this.speed = (dataTransfered - dataTransferedOld) / (currentTime - previousTime);
        dataTransferedOld = dataTransfered;
        fsTableModel
        .setValueAt(new JLabel(((speed * 1000) / 1024) + " ", JLabel.RIGHT), rowIndex, EnumManagerlTable.SPEED);

        //Setting the value of TimeElapsed

        this.timeElapsed = timeCount * 1000;
        fsTableModel
        .setValueAt(new JLabel(generalUtil.setTime(timeElapsed) + "", JLabel.CENTER), rowIndex, EnumManagerlTable
                                .TIME_ELAPSED);

        //Setting the value of TimeRemaining

        try {
          this.timeRemaining = dataRemaining / ((long)speed);
          fsTableModel
          .setValueAt(new JLabel(generalUtil.setTime(timeRemaining) + "", JLabel.CENTER), rowIndex, EnumManagerlTable
                                  .TIME_REMAINING);
        }
        catch (Exception ex) {
          fsTableModel
          .setValueAt(new JLabel(generalUtil.setTime(0) + "", JLabel.CENTER), rowIndex, EnumManagerlTable.TIME_REMAINING);
        }
        //Setting the value of TotalTime

        try {
          this.totalTime = timeElapsed + timeRemaining;
          fsTableModel
          .setValueAt(new JLabel(generalUtil.setTime(timeElapsed + timeRemaining) + "", JLabel.CENTER), rowIndex,
                                  EnumManagerlTable.TOTAL_TIME);
        }
        catch (Exception ex) {
          fsTableModel
          .setValueAt(new JLabel(generalUtil.setTime(0) + "", JLabel.CENTER), rowIndex, EnumManagerlTable.TOTAL_TIME);
        }


        previousTime = currentTime;
        previousByteRead = 0;
        //Setting the value of Progress
        if (totalData > 0) {
          long percentProgress = ((dataTransfered * 100) / (totalData));
          fsTableModel
          .setValueAt(new JLabel((int)percentProgress + " % ", JLabel.CENTER), rowIndex, EnumManagerlTable.PROGRESS);
        }
      }
    }

    private int getCurrentRowIndex() {
      int currentRowIndex = 0;
      //logger.debug("table4Manager.getRowCount() in Upload " + table4Manager.getRowCount());
      while (currentRowIndex < table4Manager.getRowCount()) {
        int superRequestCode =
          Integer.parseInt(((JLabel)(fsTableModel.getValueAt(currentRowIndex, EnumManagerlTable.SUPER_REQUEST_CODE)))
                                                .getText());
//        logger.debug("Upload code is " + uploadCode);
//        logger.debug("Super Request code is " + superRequestCode);
        if (uploadCode == superRequestCode) {
          return currentRowIndex;
        }
        else {
          currentRowIndex++;
        }
      }
      return -1;
    }
    

    
    public void propertyChange(PropertyChangeEvent evt) {

      int rowIndex = 0;
      int propertyName = Integer.valueOf(evt.getPropertyName());
      FsResponse fsResponse;
      FsExceptionHolder fsExceptionHolder;
      fsResponse = (FsResponse)evt.getNewValue();
      String uploadFilePath = null;

      if (fsResponse.getSuperResponseCode().equals(Integer.toString(uploadCode))) {
        switch (propertyName) {
        case STARTED :
          totalSizeUpload = (Long)fsResponse.getData();
          for (int counter = 0; counter < EnumManagerlTable.COLUMN_LENGTH; counter++) {
            dataCol.add(counter, "");
          }
          totalData = totalSizeUpload.longValue();
          dataCol.set(EnumManagerlTable.SUPER_REQUEST_CODE, new JLabel(uploadCode + "", JLabel.RIGHT));
          dataCol.set(EnumManagerlTable.HIDDEN_TYPE, new JLabel("Upload", JLabel.CENTER));
          dataCol.set(EnumManagerlTable.TYPE, new JLabel("Upload", JLabel.CENTER));
          dataCol.set(EnumManagerlTable.NAME, new JLabel(" ", JLabel.LEFT));
          dataCol.set(EnumManagerlTable.PROGRESS, new JLabel("", JLabel.CENTER));
          dataCol.set(EnumManagerlTable.STATUS, new JLabel("Uploading...", JLabel.CENTER));
          dataCol.set(EnumManagerlTable.HIDDEN_STATUS, new JLabel("Uploading", JLabel.CENTER));
          dataCol.set(EnumManagerlTable.TOTAL_SIZE, new JLabel(totalData / 1024 + "  ", JLabel.RIGHT));
          dataCol.set(EnumManagerlTable.DATA_TRANSFERED, new JLabel("" + dataTransfered, JLabel.RIGHT));
          dataCol.set(EnumManagerlTable.DATA_REMAINING, new JLabel("" + dataRemaining, JLabel.RIGHT));
          dataCol.set(EnumManagerlTable.TOTAL_TIME, new JLabel("" + totalTime, JLabel.CENTER));
          dataCol.set(EnumManagerlTable.TIME_ELAPSED, new JLabel("" + timeElapsed, JLabel.CENTER));
          dataCol.set(EnumManagerlTable.TIME_REMAINING, new JLabel("" + timeRemaining, JLabel.CENTER));
          dataCol.set(EnumManagerlTable.SPEED, new JLabel("" + speed, JLabel.RIGHT));          
          dataRow.add(dataCol);
          
          fsTableModel = (FsTableModel)table4Manager.getModel();
          fsTableModel.setDataVector(dataRow);
          timer.start();
          break;
        
        case SOCKET_CLOSE_FILE:
          logger.debug("sending request to server 4 close file");
          explorer.getMdiParent().getFsClient().uploadSocketCloseFile(fsResponse.getSuperResponseCode());
          rowIndex = getCurrentRowIndex();
          fsTableModel.setValueAt(new JLabel("Uploading...", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
         break;
//        case SOCKET_COMPLETE:
//          logger.debug("upload socket ended.................");
//          explorer.getMdiParent().getFsClient().uploadSocketComplete(fsResponse.getSuperResponseCode());
//          break;
//         case OVERWRITE_NO:
//           logger.debug("Upload over right no..");
//           rowIndex = getCurrentRowIndex();
//           fsTableModel.setValueAt(new JLabel("OverRight No", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
//           
//           explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
//           explorer.getMdiParent().fsStatusBar.setLblSetMessage("Download Job Status :"+(String)fileUpload.getName()+".."+"OverRight No");
//           break;
        case COMPLETED:
          {
            logger.debug("Upload Completed....");
            rowIndex = getCurrentRowIndex();
            fsTableModel.setValueAt(new JLabel("Completed", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Upload Job Status :"+(String)fileUpload.getName()+".."+"Completed");
            try {
              Thread.sleep(1000);
            }
            catch (InterruptedException e) {
              ;
            }

          }
          timer.stop();
          statusChkdUploadDownloadManager();
//          ChkUploadDownloadManager chkUploadDownloadManager= new ChkUploadDownloadManager(table4Manager.getRowCount());
//          chkUploadDownloadManager.status();
          explorer.getFsRemoteView().remoteRefreshOperation();
          break;
        case FAILED :
          {
            rowIndex = getCurrentRowIndex();

            fsTableModel.setValueAt(new JLabel("Failed", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Upload Job Status :"+(String)fileUpload.getName()+".."+"Failed");
            timer.stop();
            
          }
          break;
        case CANCLED :
          logger.info("Upload canceled");
          {
            rowIndex = getCurrentRowIndex();
            if(rowIndex!=-1){
            fsTableModel.setValueAt(new JLabel("Canceled", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
            }
            explorer.getFsRemoteView().remoteRefreshOperation();
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Upload Job Status :"+(String)fileUpload.getName()+".."+"Caneled.");
            timer.stop();
          }

          break;
        case OVERWRITE_FOLDER :
          try {
            logger.debug("ask for overwrite Folder");
            OverwriteOptionDialog overWriteDialog =
              new OverwriteOptionDialog(explorer.getMdiParent(), "Folder Overwrite", true);
            String folderName = (String)fsResponse.getData();
            String msg = "This folder contains a folder named  " + folderName + " '";
            overWriteDialog.setTaOverwriteMessage(msg);
            overWriteDialog.setLblExistingFileSize(fsResponse.getData1() + " KB");
            overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsResponse.getData2());
            File[] itemFiles = new File[1];
            itemFiles[0] = fileUpload;
            overWriteDialog.setLblReplaceFileSize(generalUtil.findFoldersFilesInfo(itemFiles).getSize() + " KB");
            overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + new Date(fileUpload.lastModified()));
            overWriteDialog.setVisible(true);
            Integer overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
            explorer.getMdiParent().getFsClient().overWriteUploadFolder(overWriteValue, fsResponse.getSuperResponseCode());
          }
          catch (IOException e) {
            ;
          }
          break;
        case OVERWRITE_FILE :
          logger.debug("ask for overwrite");
          OverwriteOptionDialog overWriteDialog =
            new OverwriteOptionDialog(explorer.getMdiParent(), "File Overwrite", true);
          String fileName = (String)fsResponse.getData();
          String msg = "This folder already contains a file named ' " + fileName + " '";
          overWriteDialog.setTaOverwriteMessage(msg);
          overWriteDialog.setLblExistingFileSize(fsResponse.getData1() + " KB");
          overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsResponse.getData2());
          overWriteDialog.setLblReplaceFileSize(fileUpload.length() + " KB");
          overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + new Date(fileUpload.lastModified()));
          overWriteDialog.setVisible(true);
          Integer overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
          explorer.getMdiParent().getFsClient().overWriteUploadFile(overWriteValue, fsResponse.getSuperResponseCode());

          break;
        case CREATE_SOCKET_4_UPLOAD_FILE :
          fileUpload = (File)fsResponse.getData();
          logger.debug("inside create empty File.........................." + fileUpload.getName());
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("Upload Job Status :"+(String)fileUpload.getName()+".."+"Uploading...");
          break;
        case SOCKET_4_UPLOAD_FILE_CREATED :
          rowIndex = getCurrentRowIndex();
          fsTableModel
          .setValueAt(new JLabel((String)fileUpload.getName() + "  ", JLabel.LEFT), rowIndex, EnumManagerlTable.NAME);
          break;
        case CREATE_FOLDER :
          fileUpload = (File)fsResponse.getData();
          logger.debug("inside create Folder.........................." + fileUpload.getName());
          break;
        case PROGRESS :
          int byteRead;
          byteRead = ((Integer)fsResponse.getData()).intValue();
          previousByteRead = byteRead;
          dataTransfered += previousByteRead;
          //explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
          //explorer.getMdiParent().fsStatusBar.setLblSetMessage("Upload Job Status :"+(String)fileUpload.getName()+".."+"Uploading..");
          break;
        case PROGRESS_BUILDING :
          {
            rowIndex = getCurrentRowIndex();
            fsTableModel.setValueAt(new JLabel("Building...", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
          }
          break;
        case FILE_CURRUPTED :
          JOptionPane
          .showInternalMessageDialog(explorer, "Upload Failure Due to File Curruption.. Please Retry Again..", "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                                
          explorer.getMdiParent().getFsClient().uploadFileCurrupted(fsResponse.getSuperResponseCode());                                      
          break;
        case PROGRESS_ERROR :
          JOptionPane
          .showInternalMessageDialog(explorer, "Can not access the file " + uploadFilePath, "Error", JOptionPane
                                                .ERROR_MESSAGE);
          break;
        case ERROR_MESSAGE :
          Object objects[] = fsResponse.getDatas();
          fsExceptionHolder = fsResponse.getFsExceptionHolder();
          if (fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041 ||
              fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 ||
              fsExceptionHolder.getErrorCode() == 30041 || fsExceptionHolder.getErrorCode() == 32211) {
            rowIndex = getCurrentRowIndex();
            fsTableModel.setValueAt(new JLabel("Access denied", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
            timer.stop();
            JOptionPane
            .showInternalMessageDialog(explorer, fsExceptionHolder.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Upload Job Status :"+(String)fileUpload.getName()+".."+"Error");
          }
          else if (fsExceptionHolder.getErrorCode() == 68005) {
            String errorMsg = fsExceptionHolder.getErrorMessage();
            logger.debug("Error Message :" + errorMsg);
            timer.stop();
            JOptionPane
            .showInternalMessageDialog(explorer, "A Folder/file with the name you specified already exists specify a different filename.",
                                                  "Error Renaming File/Folder", JOptionPane.ERROR_MESSAGE);
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Upload Job Status :"+(String)fileUpload.getName()+".."+"Error");
          }
          else {
            rowIndex = getCurrentRowIndex();
            fsTableModel
            .setValueAt(new JLabel("" + fsExceptionHolder, JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
            explorer.getFsRemoteView().fillRemoteFolderFileList(objects);
            timer.stop();
            JOptionPane
            .showInternalMessageDialog(explorer, fsExceptionHolder.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Upload Job Status :"+(String)fileUpload.getName()+".."+"Error");
            
            }

          break;
        case FETAL_ERROR :
          logger.error("Fetal Error");
          fsTableModel.setValueAt(new JLabel("Fatal Error", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("Upload Job Status :"+(String)fileUpload.getName()+".."+"Fetal Error");
          timer.stop();
          break;
        }
      }

    }
  }

  private class DownloadListener
    implements FsDownloadListener, ActionListener {
    //private FsExplorer explorer;

    private int downloadCode;

    private GeneralUtil generalUtil;

    private Timer timer;

    private long totalData = 0;

    private long dataTransfered = 0;

    private long dataTransferedOld = 0;

    private long dataRemaining;

    private int previousByteRead;

    private long totalTime;

    private long timeElapsed;

    private long timeRemaining;

    private long currentTime;

    private long previousTime = 0;

    private int timeCount = 0;

    private long speed;

    private Vector dataCol = new Vector(EnumManagerlTable.COLUMN_LENGTH);

    public DownloadListener(int downloadCode) {
      this.downloadCode = downloadCode;
      this.generalUtil = new GeneralUtil();
      timer = new Timer(1000, this);
      timer.start();
    }

    public void actionPerformed(ActionEvent evt) {
      int rowIndex = getCurrentRowIndex();
      if (rowIndex != -1) {
        timeCount += 1;
        currentTime = new Date().getTime();
        //Setting the value of DataTransfered
        //logger.debug("Inside action Performed value of previousByteRead" + previousByteRead);
        //dataTransfered += previousByteRead;
        //logger.debug("Inside action Performed value of dataTransfered" + dataTransfered);
        fsTableModel
        .setValueAt(new JLabel(generalUtil.getTransferSizeForDisplay(dataTransfered / 1024) + "  ", JLabel.RIGHT),
                                rowIndex, EnumManagerlTable.DATA_TRANSFERED);


        //Setting the value of DataRemaining

        this.dataRemaining = totalData - dataTransfered;
        fsTableModel
        .setValueAt(new JLabel(generalUtil.getTransferSizeForDisplay(dataRemaining / 1024) + "  ", JLabel.RIGHT),
                                rowIndex, EnumManagerlTable.DATA_REMAINING);

        //Setting the value of Speed

        //this.speed = previousByteRead / (currentTime - previousTime);
        this.speed = (dataTransfered - dataTransferedOld) / (currentTime - previousTime);
        dataTransferedOld = dataTransfered;

        fsTableModel
        .setValueAt(new JLabel(((speed * 1000) / 1024) + "  ", JLabel.RIGHT), rowIndex, EnumManagerlTable.SPEED);

        //Setting the value of TimeElapsed

        this.timeElapsed = timeCount * 1000;
        fsTableModel
        .setValueAt(new JLabel(generalUtil.setTime(timeElapsed) + "", JLabel.CENTER), rowIndex, EnumManagerlTable
                                .TIME_ELAPSED);

        //Setting the value of TimeRemaining

        try {
          this.timeRemaining = dataRemaining / ((long)speed);
          fsTableModel
          .setValueAt(new JLabel(generalUtil.setTime(timeRemaining) + "", JLabel.CENTER), rowIndex, EnumManagerlTable
                                  .TIME_REMAINING);

        }
        catch (Exception ex) {
          fsTableModel
          .setValueAt(new JLabel(generalUtil.setTime(0) + "", JLabel.CENTER), rowIndex, EnumManagerlTable.TIME_REMAINING);
        }
        //Setting the value of TotalTime

        try {
          this.totalTime = timeElapsed + timeRemaining;
          fsTableModel
          .setValueAt(new JLabel(generalUtil.setTime(timeElapsed + timeRemaining) + "", JLabel.CENTER), rowIndex,
                                  EnumManagerlTable.TOTAL_TIME);

        }
        catch (Exception ex) {
          fsTableModel
          .setValueAt(new JLabel(generalUtil.setTime(0) + "", JLabel.CENTER), rowIndex, EnumManagerlTable.TOTAL_TIME);
        }


        previousTime = currentTime;
        previousByteRead = 0;
        //Setting the value of Progress
        if (totalData > 0) {
          long percentProgress = ((dataTransfered * 100) / (totalData));
          fsTableModel
          .setValueAt(new JLabel((int)percentProgress + " % ", JLabel.CENTER), rowIndex, EnumManagerlTable.PROGRESS);

        }
      }
    }

    private int getCurrentRowIndex() {
      int currentRowIndex = 0;
     // logger.debug("table4Manager.getRowCount() in download is  " + table4Manager.getRowCount());
      while (currentRowIndex < table4Manager.getRowCount()) {
        int superRequestCode =
          Integer.parseInt(((JLabel)(fsTableModel.getValueAt(currentRowIndex, EnumManagerlTable.SUPER_REQUEST_CODE)))
                                                .getText());
//        logger.debug("Download code is " + downloadCode);
//        logger.debug("Super Request code is " + superRequestCode);
        if (downloadCode == superRequestCode) {
          return currentRowIndex;
        }
        else {
          currentRowIndex++;
        }
      }
      return -1;
    }
    

    
    public void propertyChange(PropertyChangeEvent evt) {
      int propertyName = Integer.valueOf(evt.getPropertyName());
      int rowIndex = 0;
      File fileDownload = null;
      FsResponse fsResponse;
      fsResponse = (FsResponse)evt.getNewValue();
      String downloadFilePath = null;
      String downloadFileName = null;
      String msg = null;
      Integer overWriteValue;
      OverwriteOptionDialog overWriteDialog;
      logger.debug("fsResponse " + fsResponse);
      logger.debug("fsResponse.getSuperResponseCode() " + fsResponse.getSuperResponseCode());
      if (fsResponse.getSuperResponseCode().equals(Integer.toString(downloadCode))) {
        switch (propertyName) {
        case STARTED :
          Long totalSizeDownload = (Long)fsResponse.getData();

          for (int counter = 0; counter < EnumManagerlTable.COLUMN_LENGTH; counter++) {
            dataCol.add(counter, "");
          }
          totalData = totalSizeDownload.longValue();
          dataCol.set(EnumManagerlTable.SUPER_REQUEST_CODE, new JLabel(downloadCode + ""));
          dataCol.set(EnumManagerlTable.HIDDEN_TYPE, new JLabel("Download", JLabel.CENTER));
          dataCol.set(EnumManagerlTable.HIDDEN_STATUS, new JLabel("Downloading", JLabel.CENTER));
          dataCol.set(EnumManagerlTable.TYPE, new JLabel("Download", JLabel.CENTER));
          dataCol.set(EnumManagerlTable.NAME, new JLabel(" ", JLabel.LEFT));
          dataCol.set(EnumManagerlTable.PROGRESS, new JLabel("", JLabel.CENTER));
          dataCol.set(EnumManagerlTable.STATUS, new JLabel("Downloading...", JLabel.CENTER));
          dataCol.set(EnumManagerlTable.TOTAL_SIZE, new JLabel(totalData / 1024 + "  ", JLabel.RIGHT));
          dataCol.set(EnumManagerlTable.DATA_TRANSFERED, new JLabel("" + dataTransfered, JLabel.RIGHT));
          dataCol.set(EnumManagerlTable.DATA_REMAINING, new JLabel("" + dataRemaining, JLabel.RIGHT));
          dataCol.set(EnumManagerlTable.TOTAL_TIME, new JLabel("" + totalTime, JLabel.CENTER));
          dataCol.set(EnumManagerlTable.TIME_ELAPSED, new JLabel("" + timeElapsed, JLabel.CENTER));
          dataCol.set(EnumManagerlTable.TIME_REMAINING, new JLabel("" + timeRemaining, JLabel.CENTER));
          dataCol.set(EnumManagerlTable.SPEED, new JLabel("" + speed, JLabel.RIGHT));

          dataRow.add(dataCol);
          fsTableModel = (FsTableModel)table4Manager.getModel();
          fsTableModel.setDataVector(dataRow);
          timer.start();

          break;
        case OVERWRITE_NO:
          logger.debug("Download over right no..");
          rowIndex = getCurrentRowIndex();
          fsTableModel.setValueAt(new JLabel("OverRight No", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
          
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("Download Job Status :"+downloadFileName+".."+"OverRight No");
          break;
        case COMPLETED :
          logger.debug("Download Completed....");
          
          explorer.getFsLocalView().localRefreshOperation();
          {
            rowIndex = getCurrentRowIndex();
            fsTableModel.setValueAt(new JLabel("Completed", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
            
            try {
              Thread.sleep(1000);
            }
            catch (InterruptedException e) {
              ;
            }
            timer.stop();
            statusChkdUploadDownloadManager();
//            ChkUploadDownloadManager chkUploadDownloadManager= new ChkUploadDownloadManager(table4Manager.getRowCount());
//            chkUploadDownloadManager.status();
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Download Job Status :"+downloadFileName+".."+"Completed");
            
          }

          break;
        case FAILED :
          {
            logger.info("Download failure");
            explorer.getFsLocalView().localRefreshOperation();

            rowIndex = getCurrentRowIndex();
            fsTableModel.setValueAt(new JLabel("Failed", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Download Job Status :"+downloadFileName+".."+"Failed");
            timer.stop();
          }
          break;
        case CANCLED :
          {
            logger.info("Download canceled");
            rowIndex = getCurrentRowIndex();
            if(rowIndex!=-1){
              fsTableModel.setValueAt(new JLabel("Canceled", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
              explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
              explorer.getMdiParent().fsStatusBar.setLblSetMessage("Download Job Status :"+downloadFileName+".."+"Canceled");
            }
          }
          timer.stop();
          explorer.getFsLocalView().localRefreshOperation();
          break;
        case CREATE_EMPTY_FILE :
          {
            downloadFilePath = (fsResponse.getData()).toString();
            downloadFileName = (fsResponse.getData1()).toString();
            logger.debug("Create Empty file");
            rowIndex = getCurrentRowIndex();
            fsTableModel.setValueAt(new JLabel(downloadFileName + "  ", JLabel.LEFT), rowIndex, EnumManagerlTable.NAME);
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Downloading...."+downloadFileName+"..");
          }
          break;
        case PROGRESS :
          {
            int byteRead;
            byteRead = ((Integer)fsResponse.getData()).intValue();
           // downloadFilePath = (String)fsResponse.getData1();
            previousByteRead = byteRead;
            dataTransfered += previousByteRead;


          }
          break;
        case PROGRESS_BUILDING :
          {
            rowIndex = getCurrentRowIndex();
            fsTableModel.setValueAt(new JLabel("Building...", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
          }
          break;
        case CLOSE_FILE:
          explorer.getMdiParent().getFsClient().downloadSocketCloseFile(fsResponse.getSuperResponseCode());
          
          rowIndex = getCurrentRowIndex();
          fsTableModel.setValueAt(new JLabel("Downloading...", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
          break;
        case OVERWRITE_FILE :
          overWriteDialog = new OverwriteOptionDialog(explorer.getMdiParent(), "Overwrite File", true);
          FsFileHolder fsFileHolder = (FsFileHolder)fsResponse.getData();
          fileDownload = (File)fsResponse.getData1();
          rowIndex = getCurrentRowIndex();
          fsTableModel
          .setValueAt(new JLabel(fileDownload.getName() + "  ", JLabel.LEFT), rowIndex, EnumManagerlTable.NAME);

          msg = "This folder already contains a file named ' " + fsFileHolder.getName() + " '";
          overWriteDialog.setTaOverwriteMessage(msg);
          overWriteDialog.setLblExistingFileSize(fileDownload.length() + " KB");
          overWriteDialog.setLblExistingFileModifiedDate("Modified : " + new Date(fileDownload.lastModified()));
          overWriteDialog.setLblReplaceFileSize(fsFileHolder.getSize() + " KB");
          overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFileHolder.getModifiedDate());
          overWriteDialog.setVisible(true);
          overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
          explorer.getMdiParent().getFsClient().overWriteDownloadFile(overWriteValue, fsResponse.getSuperResponseCode());
          break;
        case OVERWRITE_FOLDER :
          overWriteDialog = new OverwriteOptionDialog(explorer.getMdiParent(), "Overwrite Folder", true);
          fileDownload = (File)fsResponse.getData1();
          FsFolderHolder fsFolderHolder = (FsFolderHolder)fsResponse.getData();
          rowIndex = getCurrentRowIndex();
          fsTableModel
          .setValueAt(new JLabel(fileDownload.getName() + "  ", JLabel.LEFT), rowIndex, EnumManagerlTable.NAME);
          msg = "This folder already contains a folder named ' " + fsFolderHolder.getName() + " '";
          overWriteDialog.setTaOverwriteMessage(msg);
          overWriteDialog.setLblExistingFileSize(fileDownload.length() + " KB");
          //TODO Calculated Size  should be displayed
          overWriteDialog.setLblExistingFileModifiedDate("Modified : " + new Date(fileDownload.lastModified()));
          overWriteDialog.setLblReplaceFileSize("4 KB");
          //TODO Calculated Size  should be displayed
          overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFolderHolder.getModifiedDate());
          overWriteDialog.setVisible(true);
          overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
          explorer.getMdiParent().getFsClient().overWriteDownloadFolder(overWriteValue, fsResponse.getSuperResponseCode());
          break;
        case ERROR_MESSAGE :
          Object objects[] = fsResponse.getDatas();
          FsExceptionHolder fsExceptionHolder = fsResponse.getFsExceptionHolder();
          if (fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041 ||
              fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 ||
              fsExceptionHolder.getErrorCode() == 30041) {
            rowIndex = getCurrentRowIndex();
            fsTableModel.setValueAt(new JLabel("Access denied", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
            timer.stop();
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Download Job Status :"+downloadFileName+".."+"Access denied");
            JOptionPane
            .showInternalMessageDialog(explorer, fsExceptionHolder.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
          else if (fsExceptionHolder.getErrorCode() == 68005) {
            String errorMsg = fsExceptionHolder.getErrorMessage();
            logger.debug("Error Message :" + errorMsg);
            timer.stop();
            JOptionPane
            .showInternalMessageDialog(explorer, "A Folder/file with the name you specified already exists specify a different filename.",
                                                  "Error Renaming File/Folder", JOptionPane.ERROR_MESSAGE);
          }
          else {
            rowIndex = getCurrentRowIndex();
            fsTableModel
            .setValueAt(new JLabel("" + fsExceptionHolder, JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
            explorer.getFsRemoteView().fillRemoteFolderFileList(objects);
            timer.stop();
            JOptionPane
            .showInternalMessageDialog(explorer, fsExceptionHolder.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
            explorer.getMdiParent().fsStatusBar.setLblSetMessage("Download Job Status :"+downloadFileName+".."+"Error");
          }
          explorer.getMdiParent().getFsClient().downloadSocketErrorMessage(fsResponse.getSuperResponseCode());
          break;
        case FETAL_ERROR :
          logger.error("Fetal Error");
          fsTableModel.setValueAt(new JLabel("Failed", JLabel.CENTER), rowIndex, EnumManagerlTable.STATUS);
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("");
          explorer.getMdiParent().fsStatusBar.setLblSetMessage("Download Job Status :"+downloadFileName+".."+"Fetal Error");
          timer.stop();
          break;
        }
      }
    }
  }

  
  private interface EnumManagerlTable {
    public static final String[] COLUMN_NAMES =
    { "Type", "Name Of Item", "Progress", "Status", "Size(KB)",
                                                  "Transfered(KB)", "Remaining(KB)", "Time", "Elapsed", "Remaining",
                                                  "Speed(KB/s)","Super Request code", "Hidden Type","Hidden Status" };

    public static final int COLUMN_LENGTH = 14;

    public static final int COLUMN_DISPLAY_LENGTH = 11;

    public static final int TYPE = 0;

    public static final int NAME = 1;

    public static final int PROGRESS = 2;

    public static final int STATUS = 3;

    public static final int TOTAL_SIZE = 4;

    public static final int DATA_TRANSFERED = 5;

    public static final int DATA_REMAINING = 6;

    public static final int TOTAL_TIME = 7;

    public static final int TIME_ELAPSED = 8;

    public static final int TIME_REMAINING = 9;

    public static final int SPEED = 10;
    
    public static final int SUPER_REQUEST_CODE = 11;
    
    public static final int HIDDEN_TYPE = 12;

    public static final int HIDDEN_STATUS = 13;
  }


  
}


