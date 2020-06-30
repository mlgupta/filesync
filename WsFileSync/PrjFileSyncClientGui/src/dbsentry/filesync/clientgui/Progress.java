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

import dbsentry.filesync.clientgui.constants.FsLocalOperationConstants;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;

import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;

import org.apache.log4j.Logger;


/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description To create a progress dialog having a determinate progressbar which
 * shows progress of data transfer operations such as upload, download etc.
 * @Date of creation: 21-03-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public class Progress extends JDialog implements ActionListener {
  private JPanel jPanel1 = new JPanel();

  private JPanel jpTableHolder = new JPanel();

  private FsTableModel fsTableModelDetail;

  private String[] columnNames = { "Property", "Value" };

  private String[] properties =
  { "Total Size ( KB )", "Data Transfered ( KB )", "Data Remaining ( KB )", "Total Time", "Time Elapsed",
                                  "Time Remaining", "Speed (KB/s)" };

  private Logger logger;

  
  private long totaldata;

  private long dataTransfered = 0;

  private long dataRemaining;

  private long totalTime;

  private long timeElapsed;

  private long timeRemaining;

  private long speed;

  private long currentTime;

  private long prevTime = 0;

  private int timeCount = 0;

  private int prevByteRead;

  //private boolean operationCancelled;

  private String superRequestCode;
  
  private int operation;

  private Timer timer;

  private static final int ONE_SECOND = 1000;

  private GeneralUtil generalUtil;

  private BorderLayout borderLayout2 = new BorderLayout();

  private JPanel jPanel4 = new JPanel();

  private JPanel jPanel5 = new JPanel();

  private BorderLayout borderLayout4 = new BorderLayout();

  private BorderLayout borderLayout1 = new BorderLayout();

  private JScrollPane scrpTableDetail = new JScrollPane();

  private JPanel jPanel3 = new JPanel();

  private JPanel jPanel6 = new JPanel();

  private JPanel jPanel7 = new JPanel();

  private JPanel jPanel8 = new JPanel();

  private BorderLayout borderLayout3 = new BorderLayout();

  private JPanel jPanel11 = new JPanel();

  private BorderLayout borderLayout6 = new BorderLayout();

  private JLabel lblFilePath = new JLabel();

  private JPanel jPanel12 = new JPanel();

  private BorderLayout borderLayout5 = new BorderLayout();

  private JLabel lblProgress = new JLabel();

  private JProgressBar jProgressBar = new JProgressBar();

  private JPanel jPanel9 = new JPanel();

  private JPanel jPanel10 = new JPanel();

  private JPanel jPanel13 = new JPanel();

  private FlowLayout flowLayout1 = new FlowLayout();

  private JButton butCancel = new JButton();

  private JToggleButton butDetail = new JToggleButton();

  private JPanel jPanel14 = new JPanel();

  private JPanel jPanel15 = new JPanel();

  private JTable tabDetail = new JTable();

  private JPanel jPanel16 = new JPanel();

  private JPanel jPanel17 = new JPanel();

  private JLabel lblOperation = new JLabel();

  private FlowLayout flowLayout2 = new FlowLayout();
  
  private FileSyncClientMDI mdiParent;


   /**
    * To create a dialog box.
    * @param mdiParent parent of the progress bar
    * @param title title of the progress bar
    * @param modal specifies if this window is modal
    * @param logger logger object
    */
  public Progress(FileSyncClientMDI mdiParent, String title, boolean modal, Logger logger, String superRequestCode,int operation) {
    super(mdiParent, title, modal);
    try {
      jbInit();
      this.logger = logger;
      this.generalUtil = new GeneralUtil();
      generalUtil.centerForm(mdiParent, this);
      this.mdiParent=mdiParent;
      this.superRequestCode = superRequestCode;
      this.operation=operation;
      initializeTableDetail();

      //operationCancelled = false;

      timer = new Timer(ONE_SECOND, this);
      timer.start();
      
      

    } catch (Exception e) {
      logger.error(generalUtil.getStackTrace(e));
    }
  }
  
  /**
   * To add the controls over the dialog and customize it
   * @throws Exception - if operation fails.
   */ 
  private void jbInit() throws Exception {
    this.setSize(new Dimension(329, 163));
    this.getContentPane().setLayout(borderLayout2);
    this.setResizable(true);
    this.addWindowListener(new java.awt.event.WindowAdapter() {

                             public void windowClosed(WindowEvent e) {
                               this_windowClosed();
                             }
                           }
    );
    jPanel1.setLayout(borderLayout4);
    jPanel1.setMinimumSize(new Dimension(325, 30));
    jPanel1.setMaximumSize(new Dimension(2147483647, 139));
    jPanel1.setSize(new Dimension(319, 150));
    jPanel1.setPreferredSize(new Dimension(312, 130));
    jpTableHolder.setLayout(borderLayout1);
    jpTableHolder.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    jpTableHolder.setPreferredSize(new Dimension(335, 100));
    jpTableHolder.setBounds(new Rectangle(0, 154, 335, 1));
    jpTableHolder.setBackground(Color.white);
    jPanel4.setPreferredSize(new Dimension(5, 10));
    jPanel5.setSize(new Dimension(5, 262));
    jPanel5.setPreferredSize(new Dimension(5, 10));
    scrpTableDetail.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrpTableDetail.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    scrpTableDetail.setPreferredSize(new Dimension(335, 134));
    scrpTableDetail.setSize(new Dimension(311, 208));
    scrpTableDetail.setMinimumSize(new Dimension(0, 0));
    scrpTableDetail.getViewport().setBackground(Color.white);
    jPanel3.setPreferredSize(new Dimension(10, 5));
    jPanel6.setPreferredSize(new Dimension(5, 10));
    jPanel7.setPreferredSize(new Dimension(5, 10));
    jPanel8.setLayout(borderLayout3);
    jPanel8.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jPanel11.setLayout(borderLayout6);
    lblFilePath.setText(" File Path");
    lblFilePath.setFont(new Font("Tahoma", 0, 12));
    lblFilePath.setPreferredSize(new Dimension(315, 15));
    lblFilePath.setMinimumSize(new Dimension(56, 10));
    lblFilePath.setVerticalAlignment(SwingConstants.TOP);
    lblFilePath.setVerticalTextPosition(SwingConstants.TOP);
    lblFilePath.setHorizontalAlignment(SwingConstants.LEFT);
    jPanel12.setLayout(borderLayout5);
    jPanel12.setPreferredSize(new Dimension(328, 48));
    lblProgress.setText("   Progress ");
    lblProgress.setFont(new Font("Tahoma", 1, 12));
    lblProgress.setSize(new Dimension(317, 30));
    lblProgress.setPreferredSize(new Dimension(61, 23));
    jProgressBar.setStringPainted(true);
    jProgressBar.setValue(0);
    jProgressBar.setSize(new Dimension(310, 21));
    jProgressBar.setPreferredSize(new Dimension(308, 21));
    jPanel9.setPreferredSize(new Dimension(10, 14));
    jPanel13.setLayout(flowLayout1);
    flowLayout1.setAlignment(2);
    butCancel.setText("Cancel");
    butCancel.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                    butCancel_actionPerformed();
                                  }
                                }
    );
    butDetail.setText("Detail...");
    butDetail.addItemListener(new ItemListener() {
                                public void itemStateChanged(ItemEvent e) {
                                  butDetail_itemStateChanged(e);
                                }
                              }
    );
    jPanel14.setPreferredSize(new Dimension(10, 5));
    jPanel15.setPreferredSize(new Dimension(10, 5));
    tabDetail.setMaximumSize(new Dimension(32767, 32767));
    tabDetail.setShowHorizontalLines(false);
    tabDetail.setShowVerticalLines(false);
    jPanel16.setSize(new Dimension(10, 30));
    jPanel16.setBounds(new Rectangle(0, 0, 10, 15));
    jPanel16.setPreferredSize(new Dimension(8, 10));
    jPanel17.setLayout(flowLayout2);
    jPanel17.setPreferredSize(new Dimension(83, 27));
    jPanel17.setSize(new Dimension(307, 27));
    lblOperation.setText("Uploading");
    lblOperation.setFont(new Font("Tahoma", 1, 14));
    lblOperation.setPreferredSize(new Dimension(90, 23));
    lblOperation.setVerticalTextPosition(SwingConstants.TOP);
    lblOperation.setMaximumSize(new Dimension(69, 19));
    lblOperation.setVerticalAlignment(SwingConstants.TOP);
    lblOperation.setHorizontalAlignment(SwingConstants.LEFT);
    flowLayout2.setAlignment(0);
    flowLayout2.setHgap(12);
    jPanel11.add(lblFilePath, BorderLayout.CENTER);
    jPanel11.add(jPanel16, BorderLayout.WEST);
    jPanel17.add(lblOperation, null);
    jPanel11.add(jPanel17, BorderLayout.NORTH);
    jPanel12.add(lblProgress, BorderLayout.NORTH);
    jPanel12.add(jProgressBar, BorderLayout.CENTER);
    jPanel12.add(jPanel9, BorderLayout.WEST);
    jPanel12.add(jPanel10, BorderLayout.EAST);
    jPanel13.add(butCancel, null);
    jPanel13.add(butDetail, null);
    scrpTableDetail.getViewport().add(tabDetail, null);
    jpTableHolder.add(scrpTableDetail, BorderLayout.CENTER);
    this.getContentPane().add(jpTableHolder, BorderLayout.CENTER);
    jPanel1.add(jPanel3, BorderLayout.NORTH);
    jPanel1.add(jPanel6, BorderLayout.EAST);
    jPanel1.add(jPanel7, BorderLayout.WEST);
    jPanel8.add(jPanel11, BorderLayout.NORTH);
    jPanel8.add(jPanel12, BorderLayout.CENTER);
    jPanel8.add(jPanel13, BorderLayout.SOUTH);
    jPanel1.add(jPanel8, BorderLayout.CENTER);
    jPanel1.add(jPanel14, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel1, BorderLayout.NORTH);
    this.getContentPane().add(jPanel4, BorderLayout.WEST);
    this.getContentPane().add(jPanel5, BorderLayout.EAST);
    this.getContentPane().add(jPanel15, BorderLayout.SOUTH);
  }

   /**
    * Sets the text property of lblFilePath to specified String.
    * @param filePath path of file.
    */
  public void setFilePath(String filePath) {
    lblFilePath.setText(filePath);
  }

   /**
    * Sets the text property of lblOperation to specified String.
    * @param operation name of operation which is in progress.
    */
  public void setOperation(String operation) {
    lblOperation.setText(operation);
  }

   /**
    * Sets the maximum property of jProgressBar to the specified value.
    * @param maxValue maximum value to be set.
    */
  public void setMaxProgress(int maxValue) {
    jProgressBar.setMaximum(maxValue);
  }
  
  /**
   * handles the Cancel operation by user.
   */
  private void butCancel_actionPerformed() {
    int cancel =
      JOptionPane.showConfirmDialog(this, "Do you want to cancel this operation ", "Cancel", JOptionPane.YES_NO_OPTION);
    if (cancel == JOptionPane.NO_OPTION) {
      return;
    } else {
      if(operation==FsRemoteOperationConstants.UPLOAD){
        mdiParent.getFsClient().uploadCancel(superRequestCode);
        this.dispose();
      }else if(operation==FsRemoteOperationConstants.DOWNLOAD){
        mdiParent.getFsClient().downloadCancel(superRequestCode);
        this.dispose();
      }else if(operation==FsRemoteOperationConstants.COPY){
        mdiParent.getFsClient().copyCancel(superRequestCode);
        this.dispose();
      }else if(operation==FsRemoteOperationConstants.MOVE){
        mdiParent.getFsClient().moveCancel(superRequestCode);
        this.dispose();
      }else if(operation==FsLocalOperationConstants.MOVE){
        this.dispose();
      }
    }
  }
  
  /**
   * to initialize the table which holds the details of data transfer.
   */
  private void initializeTableDetail() {

    Vector dataRow = new Vector(1);
    Vector dataCol;
    fsTableModelDetail = new FsTableModel(columnNames, dataRow);
    tabDetail.setModel(fsTableModelDetail);
    tabDetail.setAutoCreateColumnsFromModel(false);
    //set default cell renderer
    FsTableCellRenderer fsTableCellRenderer = new FsTableCellRenderer();
    tabDetail.setDefaultRenderer(Object.class, fsTableCellRenderer);

    DefaultTableColumnModel colModel = (DefaultTableColumnModel)tabDetail.getColumnModel();
    colModel.getColumn(0).setPreferredWidth(211);
    colModel.getColumn(1).setPreferredWidth(112);

    dataRow = new Vector(7);
    for (int index = 0; index < 7; index++) {
      dataCol = new Vector(2);
      dataCol.add(0, new JLabel(properties[index], JLabel.LEFT));
      dataCol.add(1, new JLabel("0", JLabel.CENTER));
      dataRow.add(dataCol);
    }
    JTableHeader tableHeader = tabDetail.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    fsTableModelDetail = (FsTableModel)tabDetail.getModel();
    fsTableModelDetail.setDataVector(dataRow);
  }
  
  /**
   * to handle the actionEvent of timer.
   * @param evt ActionEvent
   */
  public void actionPerformed(ActionEvent evt) {
    timeCount += 1;
    currentTime = new Date().getTime();
    setDataTransfered();
    setDataRemaining();
    setSpeed();
    setTimeElapsed();
    setTimeRemaining();
    setTotalTime();
    prevTime = currentTime;
    prevByteRead = 0;
    setProgressBar();
  }
  
  /**
   * to set the value property of progressbar.
   */
  private void setProgressBar() {
    if (totaldata > 0) {
      long percentProgress = ((dataTransfered * 100) / (totaldata));
      jProgressBar.setValue((int)percentProgress);
    }
  }
  
  /**
   * to set the totalData to total transfer size and show same in the table interms of KBs..
   * @param totaldata total data
   */
  public void setTotalData(long totaldata) {
    this.totaldata += totaldata;
    tabDetail
    .setValueAt(new JLabel(generalUtil.getTransferSizeForDisplay(totaldata / 1024) + "  ", JLabel.RIGHT), 0, 1);
  }
  
  /**
  * to set the dataTransfered and show same in the table interms of KBs.
  */
  private void setDataTransfered() {
    this.dataTransfered += prevByteRead;
    tabDetail
    .setValueAt(new JLabel(generalUtil.getTransferSizeForDisplay(dataTransfered / 1024) + "  ", JLabel.RIGHT), 1, 1);
  }
  
  /**
  * to set the dataRemaining and show same in the table interms of KBs.
  */
  private void setDataRemaining() {
    this.dataRemaining = totaldata - dataTransfered;
    tabDetail
    .setValueAt(new JLabel(generalUtil.getTransferSizeForDisplay(dataRemaining / 1024) + "  ", JLabel.RIGHT), 2, 1);
  }
  
  /**
  * to set the totalTime required for data transfer and show same in the table in 
  * hh:mm:ss format.
  */
  private void setTotalTime() {
    try {
      this.totalTime = timeElapsed + timeRemaining;
      tabDetail.setValueAt(new JLabel(generalUtil.setTime(timeElapsed + timeRemaining) + "  ", JLabel.RIGHT), 3, 1);

    } catch (Exception ex) {
      tabDetail.setValueAt(new JLabel(generalUtil.setTime(0) + "  ", JLabel.RIGHT), 3, 1);
    }
  }
  
  /**
  * to initialize the timeElapsed with time, since the data transfer started and to show
  * same in the table in hh:mm:ss format.
  */
  private void setTimeElapsed() {
    this.timeElapsed = timeCount * 1000;
    tabDetail.setValueAt(new JLabel(generalUtil.setTime(timeElapsed) + "  ", JLabel.RIGHT), 4, 1);
  }
  
  /**
  * to initialize the timeRemaining with time,required for remaining data transfer 
  * and to show same in the table in hh:mm:ss format.
  */
  private void setTimeRemaining() {
    try {
      this.timeRemaining = dataRemaining / ((long)speed);
      tabDetail.setValueAt(new JLabel(generalUtil.setTime(timeRemaining) + "  ", JLabel.RIGHT), 5, 1);

    } catch (Exception ex) {
      tabDetail.setValueAt(new JLabel(generalUtil.setTime(0) + "  ", JLabel.RIGHT), 5, 1);
    }
  }
  
  /**
  * to set the speed of data transfer and to show same in  the table in terms of Kbps.
  */
  private void setSpeed() {
    this.speed = prevByteRead / (currentTime - prevTime);
    tabDetail.setValueAt(new JLabel(((speed * 1000) / 1024) + "  ", JLabel.RIGHT), 6, 1);

  }
  
  /**
  * to set the no. of bytes transferred in one second 
  * @param prevByteRead long no. of bytes read for transfer in one go.
  */
  public void setPrevByteRead(long prevByteRead) {
    this.prevByteRead += prevByteRead;
  }
  
  /**
   * to handle the click ItemEvent of toggle button Detail/Hide Detail. 
   * @param e ItemEvent object.
   */
  private void butDetail_itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      butDetail.setText("Hide Detail");
      this.setSize(new Dimension(329, 305));
      this.validate();
    } else {
      butDetail.setText("Detail...");
      this.setSize(new Dimension(329, 163));
      this.validate();
    }
  }
  
  /**
  * to handle the windowClosed event. 
  */
  private void this_windowClosed() {
    timer.stop();
  }
  
  
}
