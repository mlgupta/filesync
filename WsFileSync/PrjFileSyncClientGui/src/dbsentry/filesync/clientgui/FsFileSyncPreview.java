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
 * $Id: FsFileSyncPreview.java,v 1.153 2006/08/31 10:46:47 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.ClientUtil;
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.clientgui.sync.FsSyncObject;
import dbsentry.filesync.clientgui.sync.FsSynchronizerClient;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFolderDocInfoHolder;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.enumconstants.EnumSyncOperation;
import dbsentry.filesync.common.listeners.FsSyncCommandListener;
import dbsentry.filesync.common.listeners.FsSyncDownloadListener;
import dbsentry.filesync.common.listeners.FsSyncUploadListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;

import java.io.File;

import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 *	An interface to display sync preview and synchronize it.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    Saurabh Gupta
 * 	Last Modfied Date:   04-08-2006 
 */
public class FsFileSyncPreview extends JInternalFrame {
  private Logger logger;

  private boolean uploadFailure = false;
  
  private int syncThreadCount;
  private int syncThreadCounter;
  
  private int rowCount;
  private int rowCounter;

  private String syncErrorMsgString = "";
  
  private Document syncXMLDocumentLocal = null;
  private Document syncXMLDocumentRemote = null;

  private Document syncXMLDocumentUpdatedLocal;
  
  private LinkedList linkedListOfFsSyncObject = new LinkedList();
  
  
  private CommonUtil commonUtil;
  private ClientUtil clientUtil;
  private GeneralUtil generalUtil;
  
  private FsProfile fsProfile;
  private FsFileSync fsFileSync;
  
  private FsTableModel fsTableModelSyncPreview;

  private JPanel jPanel1 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JPanel jPanel3 = new JPanel();
  private JPanel jPanel4 = new JPanel();
  private JPanel jPanel5 = new JPanel();
  private JPanel jPanel6 = new JPanel();
  private JPanel jPanel7 = new JPanel();
  private JPanel jPanel8 = new JPanel();
  private JPanel jPanel9 = new JPanel();
  private JPanel jPanel10 = new JPanel();
  private JPanel jPanel11 = new JPanel();
  private JPanel jPanel12 = new JPanel();
  private JPanel jPanel13 = new JPanel();
  private JPanel jPanel14 = new JPanel();
  private JPanel jPanel15 = new JPanel();
  private JPanel jPanel16 = new JPanel();
  private JPanel jPanel17 = new JPanel();
  private JPanel jPanel18 = new JPanel();
  private JPanel jPanel19 = new JPanel();
  private JPanel jPanel20 = new JPanel();
  
  private JLabel jLabel1 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JLabel jLabel3 = new JLabel();
  
  private JButton butStart = new JButton();
  private JButton butClose = new JButton();
  private JButton butRefresh = new JButton();
  private JButton butStop = new JButton();
  
  private JTextField tfAction = new JTextField();
  private JTextField syncStatus = new JTextField();
  private JTextField txtLocalFolderPath = new JTextField();
  private JTextField txtRemoteFolderPath = new JTextField();

  private JProgressBar syncProgressBar = new JProgressBar();
  
  private JScrollPane scrpFolderFileList = new JScrollPane();
  private JScrollPane scrpItemDetail = new JScrollPane();
  
  private JTable tblSyncPreview = new JTable();
  private JTable tblItemDetail = new JTable();
  
  private SyncCommandListener syncCommandListener=null;
 
  private int syncCommandCode;

  private JTextField tfStatus = new JTextField();
  
  private int currentSyncUploadCode=-1;
  
  private int currentSyncDownloadCode=-1;
  
  public FileSyncClientMDI mainmdiFrame;
  
  private FsFileSyncPreview fsFileSyncPreview=this;
  
  /**
   * Constructs FsFileSyncPreview object.
   * @param mainmdiFrame the frame which will act as parent of this frame. 
   * @param fsFileSync . 
   * @param fsProfile the profile object to be synched
   */
  public FsFileSyncPreview(FileSyncClientMDI mainmdiFrame,FsFileSync fsFileSync, FsProfile fsProfile) {
    super("Synchronization Preview for the Profile" , 
          true, //resizable
          true, //closable
          true, //maximizable
          true);//iconifiable
  
  
    try {
      jbInit();
      this.mainmdiFrame=mainmdiFrame;
      this.fsFileSync = fsFileSync;
      this.logger = Logger.getLogger("ClientLogger");
      this.generalUtil = new GeneralUtil();
      
      this.commonUtil = new CommonUtil(logger);
      this.clientUtil = new ClientUtil(logger);
      this.generalUtil.centerForm(mainmdiFrame, this);
      this.fsProfile = fsProfile;
      
      this.txtLocalFolderPath.setText(fsProfile.getLocalFolderPath());
      this.txtLocalFolderPath.setToolTipText(fsProfile.getLocalFolderPath());
      this.txtRemoteFolderPath.setText(fsProfile.getRemoteFolderPath());
      this.txtRemoteFolderPath.setToolTipText(fsProfile.getRemoteFolderPath());

      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      initializeTheControls();

      this.setTitle( this.fsProfile.getProfileName() + " [Synchronization Preview for the Profile]");
      
      syncCommandCode=new Random().nextInt();
      syncCommandListener=new SyncCommandListener(mainmdiFrame);
      mainmdiFrame.getFsClient().addSyncCommandListener(syncCommandListener);
      butRefresh_actionPerformed(); //Preview will be called........
      
      
    } catch (Exception e) {
      logger.error(clientUtil.getStackTrace(e));
    }
  }

  private void jbInit() throws Exception {
    
    jPanel2.setBounds(new Rectangle(0, 0, 734, 5));
    jPanel2.setPreferredSize(new Dimension(10, 5));

    jPanel3.setPreferredSize(new Dimension(10, 0));

    jPanel4.setPreferredSize(new Dimension(5, 5));
    jPanel4.setMinimumSize(new Dimension(10, 5));

    jPanel5.setPreferredSize(new Dimension(5, 5));
    jPanel5.setMinimumSize(new Dimension(10, 5));
    jPanel5.setSize(new Dimension(5, 487));
    jPanel5.setBounds(new Rectangle(598, 5, 5, 422));

    jPanel6.setLayout(new BorderLayout());

    jPanel7.setLayout(new BorderLayout());
    jPanel7.setPreferredSize(new Dimension(500, 40));
    jPanel7.setEnabled(false);
    jPanel7.setSize(new Dimension(712, 25));
    jPanel7.setBounds(new Rectangle(1, 1, 712, 35));
    jPanel7.setBorder(BorderFactory.createLineBorder(Color.black, 1));

    jPanel8.setLayout(new BorderLayout());

    jPanel9.setLayout(new BorderLayout());

    jPanel10.setLayout(new BorderLayout());
    jPanel10.setSize(new Dimension(350, 58));
    jPanel10.setPreferredSize(new Dimension(350, 120));

    jPanel16.setLayout(new BorderLayout());
    jPanel16.setBounds(new Rectangle(0, 0, 708, 71));

    jPanel20.setLayout(new BorderLayout());

    syncStatus.setBounds(new Rectangle(0, 0, 547, 25));
    syncStatus.setEditable(false);
    
    syncProgressBar.setBounds(new Rectangle(547, 0, 148, 25));
    syncProgressBar.setIndeterminate(true);
    
    jLabel1.setText("  Local Folder : ");
    jLabel1.setSize(new Dimension(34, 20));
    jLabel1.setMaximumSize(new Dimension(100, 25));
    jLabel1.setPreferredSize(new Dimension(101, 20));
    jLabel1.setMinimumSize(new Dimension(100, 20));
    jLabel1.setFont(new Font("Tahoma", 1, 12));
    
    txtLocalFolderPath.setText("D:\\jeet\\prasad");
    txtLocalFolderPath.setSize(new Dimension(200, 20));
    txtLocalFolderPath.setPreferredSize(new Dimension(59, 25));
    txtLocalFolderPath.setMaximumSize(new Dimension(200, 25));
    txtLocalFolderPath.setEditable(false);
    txtLocalFolderPath.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    txtLocalFolderPath.setFont(new Font("Tahoma", 0, 12));
    
    jLabel3.setText("  Remote Folder : ");
    jLabel3.setMaximumSize(new Dimension(100, 25));
    jLabel3.setPreferredSize(new Dimension(117, 14));
    jLabel3.setFont(new Font("Tahoma", 1, 12));
    
    txtRemoteFolderPath.setText("/home/jeet/prasad");
    txtRemoteFolderPath.setPreferredSize(new Dimension(100, 25));
    txtRemoteFolderPath.setMaximumSize(new Dimension(200, 25));
    txtRemoteFolderPath.setEditable(false);
    txtRemoteFolderPath.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    txtRemoteFolderPath.setFont(new Font("Tahoma", 0, 12));
    
    jPanel11.setPreferredSize(new Dimension(10, 5));
    
    jPanel14.setBounds(new Rectangle(1, 1, 710, 5));
    jPanel14.setPreferredSize(new Dimension(10, 5));
    
    jPanel15.setLayout(new BorderLayout());
    jPanel15.setPreferredSize(new Dimension(50, 35));
    jPanel15.setSize(new Dimension(727, 35));
    
    butStart.setText("Sync");
    butStart.setIcon(FsImage.imgSyncRun);
    butStart.setPreferredSize(new Dimension(90, 25));
    butStart.setMinimumSize(new Dimension(0, 0));
    butStart.setMaximumSize(new Dimension(90, 25));
    butStart.setMargin(new Insets(0, 0, 0, 0));
    butStart.addActionListener(new ActionListener() {
                                 public void actionPerformed(ActionEvent e) {
                                   butStart_actionPerformed();
                                 }
                               });
    
    butClose.setText("Close");
    butClose.setIcon(FsImage.imgSyncClose);
    butClose.setPreferredSize(new Dimension(90, 25));
    butClose.setMinimumSize(new Dimension(0, 0));
    butClose.setMaximumSize(new Dimension(100, 25));
    butClose.setSize(new Dimension(100, 25));
    butClose.setMargin(new Insets(0, 0, 0, 0));
    butClose.addActionListener(new ActionListener() {
                                 public void actionPerformed(ActionEvent e) {
                                   butClose_actionPerformed();
                                 }
                               });

    scrpFolderFileList.setPreferredSize(new Dimension(727, 300));
    scrpFolderFileList.setBorder(BorderFactory.createLineBorder(Color.black, 1));

    tblSyncPreview.setRowHeight(18);
    tblSyncPreview.setFont(new Font("Tahoma", 0, 12));
    tblSyncPreview.addKeyListener(new java.awt.event.KeyAdapter() {


                                    public void keyReleased(KeyEvent e) {
                                      tblSyncPreview_keyReleased();
                                    }
                                  });
    tblSyncPreview.addMouseListener(new MouseAdapter() {
                                      public void mouseClicked(MouseEvent e) {
                                        tblSyncPreview_mouseClicked(e);
                                      }
                                    });

    scrpItemDetail.setSize(new Dimension(727, 100));
    scrpItemDetail.setPreferredSize(new Dimension(727, 60));
    scrpItemDetail.setMaximumSize(new Dimension(32767, 60));
    scrpItemDetail.setMinimumSize(new Dimension(100, 60));
    scrpItemDetail.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    scrpItemDetail.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrpItemDetail.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    tblItemDetail.setRowHeight(18);
    tblItemDetail.setFont(new Font("Tahoma", 0, 12));

    jLabel2.setText("Action : ");
    jLabel2.setFont(new Font("Tahoma", 1, 12));

    tfAction.setText("No action");
    tfAction.setEditable(false);
    tfAction.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    tfAction.setFont(new Font("Tahoma", 1, 12));
    tfAction.setForeground(Color.blue);

    butRefresh.setIcon(FsImage.imgSyncRefresh);
    butRefresh.setText("Refresh");
    butRefresh.setPreferredSize(new Dimension(90, 25));
    butRefresh.setMargin(new Insets(0, 0, 0, 0));
    butRefresh.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                     butRefresh_actionPerformed();
                                   }
                                 });

    butStop.setText("Stop");
    butStop.setPreferredSize(new Dimension(90, 25));
    butStop.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                  butStop_actionPerformed();
                                }
                              });
    butStop.setIcon(FsImage.imgSyncStop);

    scrpItemDetail.getViewport();

    jPanel20.add(syncStatus, BorderLayout.CENTER);
    jPanel20.add(syncProgressBar, BorderLayout.EAST);

    jPanel16.add(jPanel20, BorderLayout.SOUTH);

    jPanel10.add(jLabel1, BorderLayout.WEST);
    jPanel10.add(txtLocalFolderPath, BorderLayout.CENTER);

    jPanel13.setLayout(new BorderLayout());
    jPanel13.setMinimumSize(new Dimension(200, 40));
    jPanel13.setPreferredSize(new Dimension(350, 40));
    jPanel13.add(jLabel3, BorderLayout.WEST);
    jPanel13.add(txtRemoteFolderPath, BorderLayout.CENTER);

    jPanel7.add(jPanel10, BorderLayout.WEST);
    jPanel7.add(jPanel13, BorderLayout.CENTER);
    jPanel7.add(jPanel11, BorderLayout.SOUTH);
    jPanel7.add(jPanel14, BorderLayout.NORTH);

    jPanel6.add(jPanel7, BorderLayout.NORTH);
    jPanel6.add(jPanel8, BorderLayout.SOUTH);

    jPanel8.add(jPanel16, BorderLayout.SOUTH);

    jPanel17.add(butRefresh, null);
    jPanel17.add(butStart, null);
    jPanel17.add(butStop, null);
    jPanel17.add(butClose, null);

    jPanel15.add(jPanel17, BorderLayout.EAST);

    scrpFolderFileList.getViewport().add(tblSyncPreview, null);

    scrpItemDetail.getViewport().add(tblItemDetail, null);

    jPanel19.setLayout(new BorderLayout());
    jPanel19.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
    jPanel19.add(jLabel2, BorderLayout.WEST);
    jPanel19.add(tfAction, BorderLayout.CENTER);
    
    jPanel18.setLayout(new BorderLayout());
    jPanel18.add(scrpItemDetail, BorderLayout.CENTER);
    jPanel18.add(jPanel19, BorderLayout.SOUTH);
    
    
    jPanel12.setLayout(new BorderLayout());
    jPanel12.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    jPanel12.add(scrpFolderFileList, BorderLayout.CENTER);
    jPanel12.add(jPanel18, BorderLayout.SOUTH);

    
    jPanel9.add(jPanel15, BorderLayout.SOUTH);
    jPanel9.add(jPanel12, BorderLayout.CENTER);

    jPanel6.add(jPanel9, BorderLayout.CENTER);

    jPanel1.setLayout(new BorderLayout());
    jPanel1.add(jPanel2, BorderLayout.NORTH);
    jPanel1.add(jPanel3, BorderLayout.SOUTH);
    jPanel1.add(jPanel4, BorderLayout.WEST);
    jPanel1.add(jPanel5, BorderLayout.EAST);
    jPanel1.add(jPanel6, BorderLayout.CENTER);
    
    //this.setIconImage(FsImage.imageTitle);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setBounds(new Rectangle(10, 10, 610, 460));
    this.addInternalFrameListener(new InternalFrameAdapter() {
      public void internalFrameClosed(InternalFrameEvent e) {
           this_windowClosed();
      }
    });
    
    this.getContentPane().setLayout(new BorderLayout());
    
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED,Color.DARK_GRAY,Color.DARK_GRAY));
  }

  private void butClose_actionPerformed() {
    this_windowClosed();
    this.dispose();
  }

  /** Initializes tblSyncPreview table to display the Sync preview.
     */
  private void initializeFolderFileList() {
    Vector dataRow = new Vector(1);

    fsTableModelSyncPreview = new FsTableModel(EnumSyncPreviewTable.COLUMN_NAMES, dataRow);
    tblSyncPreview.setModel(fsTableModelSyncPreview);
    tblSyncPreview.setAutoCreateColumnsFromModel(false);
    tblSyncPreview.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    //set default cell renderer
    FsTableCellRenderer fsTableCellRenderer = new FsTableCellRenderer();
    tblSyncPreview.setDefaultRenderer(Object.class, fsTableCellRenderer);

    DefaultTableColumnModel colModel = (DefaultTableColumnModel)tblSyncPreview.getColumnModel();
    colModel.getColumn(EnumSyncPreviewTable.SL_NO).setPreferredWidth(50);
    colModel.getColumn(EnumSyncPreviewTable.STATUS_LOCAL).setPreferredWidth(100);
    colModel.getColumn(EnumSyncPreviewTable.ACTION_DISPLAY).setPreferredWidth(75);
    colModel.getColumn(EnumSyncPreviewTable.STATUS_REMOTE).setPreferredWidth(100);
    colModel.getColumn(EnumSyncPreviewTable.RELATIVE_PATH).setPreferredWidth(650);

    //hide table coulmn contiaining non displayable info
    JTableHeader tableHeader = tblSyncPreview.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 18));

    int columnCount = tableHeader.getColumnModel().getColumnCount();
    logger.debug("columnCount : " + columnCount);
    if (columnCount > EnumSyncPreviewTable.ACTION) {
      for (int index = EnumSyncPreviewTable.ACTION; index < EnumSyncPreviewTable.COLUMN_LENGTH; index++) {
        tblSyncPreview.removeColumn(tableHeader.getColumnModel().getColumn(EnumSyncPreviewTable.ACTION));
      }
    }
  }

  /** Initializes tblItemDetail table to display the information about selected item from 
     * tblSyncPreview.
     */
  private void initializeItemDetailList() {
    Vector dataRow = new Vector(1);

    FsTableModel tableModelItemDetail = new FsTableModel(EnumItemDetailTable.COLUMN_NAMES, dataRow);
    tblItemDetail.setModel(tableModelItemDetail);
    tblItemDetail.setAutoCreateColumnsFromModel(false);
    tblItemDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    //set default cell renderer

    FsTableCellRenderer fsTableCellRenderer = new FsTableCellRenderer();
    tblItemDetail.setDefaultRenderer(Object.class, fsTableCellRenderer);

    DefaultTableColumnModel colModel = (DefaultTableColumnModel)tblItemDetail.getColumnModel();
    colModel.getColumn(EnumItemDetailTable.ROW_HEADING).setPreferredWidth(50);
    colModel.getColumn(EnumItemDetailTable.STATUS).setPreferredWidth(100);
    colModel.getColumn(EnumItemDetailTable.TYPE).setPreferredWidth(70);
    colModel.getColumn(EnumItemDetailTable.SIZE).setPreferredWidth(70);
    colModel.getColumn(EnumItemDetailTable.MODIFIED_DATE).setPreferredWidth(150);
    colModel.getColumn(EnumItemDetailTable.NAME).setPreferredWidth(530);

    JTableHeader tableHeader = tblItemDetail.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 18));

    Vector dataCol;
    dataCol = new Vector(EnumItemDetailTable.COLUMN_LENGTH);
    for (int intI = 1; intI <= EnumItemDetailTable.COLUMN_LENGTH; intI++) {
      dataCol.add(new JLabel(""));
    }
    dataCol.set(EnumItemDetailTable.ROW_HEADING, new JLabel("Remote"));
    dataRow.add(dataCol);

    dataCol = new Vector(EnumItemDetailTable.COLUMN_LENGTH);
    for (int intI = 1; intI <= EnumItemDetailTable.COLUMN_LENGTH; intI++) {
      dataCol.add(new JLabel(""));
    }
    dataCol.set(EnumItemDetailTable.ROW_HEADING, new JLabel("Local"));
    dataRow.add(dataCol);

    tableModelItemDetail.setDataVector(dataRow);

  }

  /** Displays the comparison result of two xml's stored in a LinkedList in tblSyncPreview table.
     */
  private void displayComparisonResultInTable() {
    logger.debug("In the function displayComparisonResultInTable");
    
    Object results[] = linkedListOfFsSyncObject.toArray();
    
    int resultsLength = linkedListOfFsSyncObject.size();
    
    logger.debug("resultsLength : " + resultsLength);
    
    String remoteFolderPath = fsProfile.getRemoteFolderPath();
    String localFolderPath = fsProfile.getLocalFolderPath();
    
    String remoteNodeValue;
    String localNodeValue;
    
    String relativeFolderFilePath;
    
    FsSyncObject fsSyncObject;
    
    Node remoteNode;
    Node localNode;
    
    Icon folderIcon = generalUtil.getFolderIcon();
    Icon fileIcon;

    Vector dataRow = new Vector((int)resultsLength);
    Vector dataCol;

    if (resultsLength == 0) {
      fsTableModelSyncPreview = (FsTableModel)tblSyncPreview.getModel();
      fsTableModelSyncPreview.setDataVector(dataRow);
      return;
    }

    logger.debug("Preparing data vector");
    for (int index = 0; index < resultsLength; index++) {
    
      dataCol = new Vector(EnumSyncPreviewTable.COLUMN_LENGTH);
      for (int intI = 1; intI <= EnumSyncPreviewTable.COLUMN_LENGTH; intI++) {
        dataCol.add(new JLabel(""));
      }

      fsSyncObject = (FsSyncObject)results[(index)];
      localNode = fsSyncObject.getLocalNode();
      remoteNode = fsSyncObject.getRemoteNode();
      dataCol.set(EnumSyncPreviewTable.SL_NO, new JLabel(String.valueOf(index + 1), JLabel.RIGHT));
      if (localNode != null && remoteNode != null) {

        localNodeValue = localNode.getAttributes().getNamedItem("PATH").getNodeValue();
        dataCol.set(EnumSyncPreviewTable.PATH_LOCAL, new JLabel(localNodeValue, JLabel.RIGHT));
        remoteNodeValue = remoteNode.getAttributes().getNamedItem("PATH").getNodeValue();
        dataCol.set(EnumSyncPreviewTable.PATH_REMOTE, new JLabel(remoteNodeValue, JLabel.RIGHT));

        remoteNodeValue = remoteNode.getNodeName();
        if (remoteNodeValue.equals("FOLDER")) {
          localNodeValue = localNode.getAttributes().getNamedItem("STATUS").getNodeValue();
          remoteNodeValue = remoteNode.getAttributes().getNamedItem("STATUS").getNodeValue();

          if (localNodeValue.equals(EnumSyncOperation.FOLDER_UNCHANGED) &&
              remoteNodeValue.equals(EnumSyncOperation.FOLDER_DELETED)) {
            dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.FOLDER_UNCHANGED));
            dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncBack, JLabel.CENTER));
            dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.DELETE_FOLDER_LOCAL));
            dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.FOLDER_DELETED));
            dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.FOLDER_DELETED));
          }

          if (localNodeValue.equals(EnumSyncOperation.FOLDER_DELETED) &&
              remoteNodeValue.equals(EnumSyncOperation.FOLDER_UNCHANGED)) {
            dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.FOLDER_DELETED));
            dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncForward, JLabel.CENTER));
            dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.DELETE_FOLDER_REMOTE));
            dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.FOLDER_UNCHANGED));
            dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.FOLDER_DELETED));
          }

          localNodeValue = localNode.getAttributes().getNamedItem("PATH").getNodeValue();
          relativeFolderFilePath = localNodeValue.substring(localFolderPath.length() + 1);
          localNodeValue = localNode.getAttributes().getNamedItem("NAME").getNodeValue();
          dataCol.set(EnumSyncPreviewTable.RELATIVE_PATH, new JLabel(relativeFolderFilePath, folderIcon, JLabel.LEFT));

        } else {
          localNodeValue = localNode.getAttributes().getNamedItem("STATUS").getNodeValue();
          remoteNodeValue = remoteNode.getAttributes().getNamedItem("STATUS").getNodeValue();

          if (localNodeValue.equals(EnumSyncOperation.NEW_FILE) &&
              remoteNodeValue.equals(EnumSyncOperation.NEW_FILE)) {
            dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.NEW_FILE));
            dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncQuestion, JLabel.CENTER));
            dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.DO_NOTHING));
            dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.NEW_FILE));
          }

          if (localNodeValue.equals(EnumSyncOperation.FILE_CHANGED) &&
              remoteNodeValue.equals(EnumSyncOperation.FILE_CHANGED)) {
            dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.FILE_CHANGED));
            dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncQuestion, JLabel.CENTER));
            dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.DO_NOTHING));
            dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.FILE_CHANGED));
          }

          if (localNodeValue.equals(EnumSyncOperation.FILE_CHANGED) &&
              remoteNodeValue.equals(EnumSyncOperation.FILE_DELETED)) {
            dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.FILE_CHANGED));
            dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncForward, JLabel.CENTER));
            dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.UPDATE_FILE_REMOTE));
            dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.FILE_DELETED));
            dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.FILE_CHANGED));
          }

          if (localNodeValue.equals(EnumSyncOperation.FILE_CHANGED) &&
              remoteNodeValue.equals(EnumSyncOperation.FILE_UNCHANGED)) {
            dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.FILE_CHANGED));
            dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncForward, JLabel.CENTER));
            dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.UPDATE_FILE_REMOTE));
            dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.FILE_UNCHANGED));
            dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.FILE_CHANGED));
          }

          if (localNodeValue.equals(EnumSyncOperation.FILE_DELETED) &&
              remoteNodeValue.equals(EnumSyncOperation.FILE_CHANGED)) {
            dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.FILE_DELETED));
            dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncBack, JLabel.CENTER));
            dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.UPDATE_FILE_LOCAL));
            dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.FILE_CHANGED));
            dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.FILE_CHANGED));
          }

          if (localNodeValue.equals(EnumSyncOperation.FILE_DELETED) &&
              remoteNodeValue.equals(EnumSyncOperation.FILE_UNCHANGED)) {
            dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.FILE_DELETED));
            dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncForward, JLabel.CENTER));
            dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.DELETE_FILE_REMOTE));
            dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.FILE_UNCHANGED));
            dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.FILE_DELETED));
          }

          if (localNodeValue.equals(EnumSyncOperation.FILE_UNCHANGED) &&
              remoteNodeValue.equals(EnumSyncOperation.FILE_CHANGED)) {
            dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.FILE_UNCHANGED));
            dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncBack, JLabel.CENTER));
            dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.UPDATE_FILE_LOCAL));
            dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.FILE_CHANGED));
            dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.FILE_CHANGED));
          }

          if (localNodeValue.equals(EnumSyncOperation.FILE_UNCHANGED) &&
              remoteNodeValue.equals(EnumSyncOperation.FILE_DELETED)) {
            dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.FILE_UNCHANGED));
            dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncBack, JLabel.CENTER));
            dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.DELETE_FILE_LOCAL));
            dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.FILE_DELETED));
            dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.FILE_DELETED));
          }

          localNodeValue = localNode.getAttributes().getNamedItem("PATH").getNodeValue();
          relativeFolderFilePath = localNodeValue.substring(localFolderPath.length() + 1);
          localNodeValue = localNode.getAttributes().getNamedItem("NAME").getNodeValue();
          fileIcon = generalUtil.getFileIcon(localNodeValue);
          dataCol.set(EnumSyncPreviewTable.RELATIVE_PATH, new JLabel(relativeFolderFilePath, fileIcon, JLabel.LEFT));
        }
      } else if (remoteNode != null) {
        remoteNodeValue = remoteNode.getAttributes().getNamedItem("PATH").getNodeValue();
        dataCol.set(EnumSyncPreviewTable.PATH_REMOTE, new JLabel(remoteNodeValue, JLabel.RIGHT));

        dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(""));
        remoteNodeValue = remoteNode.getAttributes().getNamedItem("PATH").getNodeValue();
        relativeFolderFilePath = remoteNodeValue.substring(remoteFolderPath.length() + 1);

        remoteNodeValue = remoteNode.getNodeName();
        if (remoteNodeValue.equals("FOLDER")) {
          dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncBack, JLabel.CENTER));
          dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.NEW_FOLDER_LOCAL));
          dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.NEW_FOLDER));
          dataCol.set(EnumSyncPreviewTable.RELATIVE_PATH, new JLabel(relativeFolderFilePath, folderIcon, JLabel.LEFT));
          dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.NEW_FOLDER));
        } else {
          dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncBack, JLabel.CENTER));
          dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.NEW_FILE_LOCAL));
          dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(EnumSyncOperation.NEW_FILE));
          remoteNodeValue = remoteNode.getAttributes().getNamedItem("NAME").getNodeValue();
          fileIcon = generalUtil.getFileIcon(remoteNodeValue);
          dataCol.set(EnumSyncPreviewTable.RELATIVE_PATH, new JLabel(relativeFolderFilePath, fileIcon, JLabel.LEFT));
          dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.NEW_FILE));
        }
      } else {
        localNodeValue = localNode.getAttributes().getNamedItem("PATH").getNodeValue();
        dataCol.set(EnumSyncPreviewTable.PATH_LOCAL, new JLabel(localNodeValue, JLabel.RIGHT));
        dataCol.set(EnumSyncPreviewTable.STATUS_REMOTE, new JLabel(""));
        localNodeValue = localNode.getAttributes().getNamedItem("PATH").getNodeValue();
        relativeFolderFilePath = localNodeValue.substring(localFolderPath.length() + 1);

        localNodeValue = localNode.getNodeName();
        if (localNodeValue.equals("FOLDER")) {
          dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncForward, JLabel.CENTER));
          dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.NEW_FOLDER_REMOTE));
          dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.NEW_FOLDER));
          dataCol.set(EnumSyncPreviewTable.RELATIVE_PATH, new JLabel(relativeFolderFilePath, folderIcon, JLabel.LEFT));
          dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.NEW_FOLDER));
        } else {
          dataCol.set(EnumSyncPreviewTable.ACTION_DISPLAY, new JLabel("", FsImage.imgSyncForward, JLabel.CENTER));
          dataCol.set(EnumSyncPreviewTable.ACTION, new JLabel(EnumSyncOperation.NEW_FILE_REMOTE));
          dataCol.set(EnumSyncPreviewTable.STATUS_LOCAL, new JLabel(EnumSyncOperation.NEW_FILE));
          localNodeValue = localNode.getAttributes().getNamedItem("NAME").getNodeValue();
          fileIcon = generalUtil.getFileIcon(localNodeValue);
          dataCol.set(EnumSyncPreviewTable.RELATIVE_PATH, new JLabel(relativeFolderFilePath, fileIcon, JLabel.LEFT));
          dataCol.set(EnumSyncPreviewTable.STATUS, new JLabel(EnumSyncOperation.NEW_FILE));
        }
      }

      //      logger.debug("SyncOperation : " + fsSyncObject.getSyncOperation());
      dataRow.add(dataCol);
    }

    //    logger.debug("dataRow.size()" + dataRow.size());
    logger.debug("Setting data vector in the table model");
    
    fsTableModelSyncPreview = (FsTableModel)tblSyncPreview.getModel();
    fsTableModelSyncPreview.setDataVector(dataRow);
  }


  private void this_windowClosed() {
    FsFileSync.fileSyncPreviewStatus.remove(fsProfile.getProfileName());
    
    mainmdiFrame.getFsClient().removeSyncCommandListener(syncCommandListener);
  }



  private void butStart_actionPerformed() {
    FsTableModel fsTableModel = (FsTableModel)tblSyncPreview.getModel();
    rowCount = tblSyncPreview.getRowCount();
    rowCounter = -1;
    syncThreadCount = 0;
    syncThreadCounter = 1;
    String action;

    for (int index = 0; index < rowCount; index++) {
      action = ((JLabel)fsTableModel.getValueAt(index, EnumSyncPreviewTable.ACTION)).getText();
      if (!action.equals(EnumSyncOperation.DO_NOTHING)) {
        syncThreadCount++;
      }
    }
    if (syncThreadCount > 0) {
      syncProgressBar.setIndeterminate(true);
      butStart.setEnabled(false);
      butStop.setEnabled(true);
      butRefresh.setEnabled(false);
      butClose.setEnabled(false);
      syncNextItem();
    } else {
      butStop.setEnabled(false);
      butRefresh.setEnabled(true);
      butClose.setEnabled(true);
    }
  }

  /** This function performs synchronization of items one by one depending upon the hidden action displayed 
     * in tblSyncPreview.
     */
  private void syncNextItem() {
    String action;
    JLabel actionDisplay;
    String filePath;
    String syncOperation;
    FsTableModel fsTableModel = (FsTableModel)tblSyncPreview.getModel();

    if (rowCounter < rowCount) {
      rowCounter++;
      syncOperation = ((JLabel)fsTableModel.getValueAt(rowCounter, EnumSyncPreviewTable.STATUS)).getText();
      actionDisplay = (JLabel)fsTableModel.getValueAt(rowCounter, EnumSyncPreviewTable.ACTION_DISPLAY);
      action = ((JLabel)fsTableModel.getValueAt(rowCounter, EnumSyncPreviewTable.ACTION)).getText();
      if (action.equals(EnumSyncOperation.NEW_FOLDER_REMOTE) || action.equals(EnumSyncOperation.NEW_FILE_REMOTE) ||
          action.equals(EnumSyncOperation.UPDATE_FILE_REMOTE)) {

        filePath = ((JLabel)fsTableModel.getValueAt(rowCounter, EnumSyncPreviewTable.PATH_LOCAL)).getText();
        File currFolderLocalFile = new File(fsProfile.getLocalFolderPath());
        String currFolderRemotePath = fsProfile.getRemoteFolderPath();
        syncUploadItem(filePath, currFolderLocalFile, currFolderRemotePath, syncOperation);                
        logger.debug("Inside syncNextItem - this.currentSyncUploadCode " + this.currentSyncUploadCode);
      } else if (action.equals(EnumSyncOperation.DELETE_FILE_REMOTE) ||
                 (action.equals(EnumSyncOperation.DELETE_FOLDER_REMOTE))) {
        Integer percent = new Integer(0);
        int row = rowCounter;
        int col = EnumSyncPreviewTable.ACTION_DISPLAY;
        SwingUtilities.invokeLater(new UpdateProgress(row, col, percent));

        filePath = ((JLabel)fsTableModel.getValueAt(rowCounter, EnumSyncPreviewTable.PATH_REMOTE)).getText();
        
        mainmdiFrame.getFsClient().syncRemoteDelete(filePath, new Integer(rowCounter), new Integer(rowCounter), new Integer(EnumSyncPreviewTable.ACTION_DISPLAY), syncCommandCode); 
        
      } else if (action.equals(EnumSyncOperation.NEW_FOLDER_LOCAL) ||
                 action.equals(EnumSyncOperation.NEW_FILE_LOCAL) ||
                 action.equals(EnumSyncOperation.UPDATE_FILE_LOCAL)) {

        filePath = ((JLabel)fsTableModel.getValueAt(rowCounter, EnumSyncPreviewTable.PATH_REMOTE)).getText();
        File currFolderLocalFile = new File(fsProfile.getLocalFolderPath());
        String currFolderRemotePath = fsProfile.getRemoteFolderPath();
        syncDownloadItem(filePath, currFolderLocalFile, currFolderRemotePath, rowCounter, EnumSyncPreviewTable.ACTION_DISPLAY, syncOperation);
        logger.debug("syncNextItem -this.currentSyncDownloadCode " + this.currentSyncDownloadCode);
      } else if (action.equals(EnumSyncOperation.DELETE_FILE_LOCAL) ||
                 (action.equals(EnumSyncOperation.DELETE_FOLDER_LOCAL))) {
        filePath = ((JLabel)fsTableModel.getValueAt(rowCounter, EnumSyncPreviewTable.PATH_LOCAL)).getText();
        syncDeleteItem(filePath, rowCounter, rowCounter, EnumSyncPreviewTable.ACTION_DISPLAY);
      } else {
        syncNextItem();
      }
    }
  }

  /** Initializes Sync related upload. 
     * @param itemToUploadPath upload file path.
     * @param currFolderLocalFile current folder from where the file is to be uploaded.
     * @param currFolderRemotePath remote folder path to which the file is to be uploaded.
     * @param syncOperation status of a file.
     */
  private void syncUploadItem(String itemToUploadPath, File currFolderLocalFile, String currFolderRemotePath, String syncOperation) {
    
    this.currentSyncUploadCode=new Random().nextInt();
    mainmdiFrame.getFsClient().syncUpload((new SyncUploadListener(this,this.currentSyncUploadCode)),
                                         syncXMLDocumentLocal, itemToUploadPath, currFolderLocalFile, currFolderRemotePath, syncOperation, this.currentSyncUploadCode); 
    logger.debug("Inside Upload Item - this.currentSyncUploadCode " + this.currentSyncUploadCode);
  }

  /** Initializes Sync related download. 
     * @param itemToDownloadPath download file path.
     * @param currFolderLocalFile current folder to which the file is to be downloaded.
     * @param currFolderRemotePath remote folder path from where the file is to be downloaded.
     * @param row index of the row representing the item to be downloaded
     * @param col index of the column representing the progress of sync download.
     * @param syncOperation status of a file.
     */
  private void syncDownloadItem(String itemToDownloadPath, File currFolderLocalFile, String currFolderRemotePath, int row, int col, String syncOperation) {
  
    Integer percent = new Integer(0);
    SwingUtilities.invokeLater(new UpdateProgress(row, col, percent));
    this.currentSyncDownloadCode=new Random().nextInt();
    mainmdiFrame.getFsClient().syncDownload((new SyncDownloadListener(this,this.currentSyncDownloadCode)), 
                                            itemToDownloadPath, currFolderLocalFile, currFolderRemotePath, syncOperation, this.currentSyncDownloadCode); 
                                            
    logger.debug("Inside Download Item -this.currentSyncDownloadCode " + this.currentSyncDownloadCode);
  }

  /** Deletes a file from local filesystem.
     * @param filePath complete path of the file which is to be deleted.
     * @param index index of the FsSyncObject in the LinkedList linkedListOfFsSyncObject.
     * @param row row index of the row representing the item to be deleted
     * @param col index of the column representing the progress of sync deleted.
     */
  private void syncDeleteItem(String filePath, int index, int row, int col) {
    try {
      Integer percent = new Integer(0);
      SwingUtilities.invokeLater(new UpdateProgress(row, col, percent));

      FsFileSystemOperationLocal fsFileSystemOperationLocal = new FsFileSystemOperationLocal(mainmdiFrame);
      String xmlString = fsFileSystemOperationLocal.syncDeleteItem(filePath);
      Document document = commonUtil.getDocumentFromXMLString(xmlString);
      commonUtil.mergeTwoDocument(syncXMLDocumentLocal, document);

      //remove the node from the syncXMLDocumentRemote
      Node node = ((FsSyncObject)linkedListOfFsSyncObject.get(index)).getRemoteNode();
      commonUtil.removeNodeFromDocument(syncXMLDocumentRemote, node);

      percent = new Integer(100);
      SwingUtilities.invokeLater(new UpdateProgress(row, col, percent));

      checkSyncCompletion();
    } catch (Exception ex) {
      logger.error(clientUtil.getStackTrace(ex));
      checkSyncCompletion();
    }
  }

  /** Saves synced profiles in files as well as in fsProfile object on disk.
     */
  private void saveSyncedProfile() {
    String syncXMLLocal = commonUtil.getXMLStringFromDocument(syncXMLDocumentLocal);
    String syncXMLRemote = commonUtil.getXMLStringFromDocument(syncXMLDocumentRemote);
    
    //File fileLocal = new File("synxXMLLocal.xml");
    //commonUtil.saveXMLDocumentToFile(syncXMLDocumentLocal, fileLocal);

    //File fileRemote = new File("synxXMLRemote.xml");
    //commonUtil.saveXMLDocumentToFile(syncXMLDocumentRemote, fileRemote);

    fsProfile.setSyncXMLLocal(syncXMLLocal);
    fsProfile.setSyncXMLRemote(syncXMLRemote);
    fsProfile.setSyncStatus(true);
    fsFileSync.saveProfileOnDisk();
  }


  /** Constructs an updated sync xml document of local folder and compares it with the remote updated sync
     * xml to generate a linkedlist. LinkedList is then used to display the comparison result in a table.
     * @param fsResponse FsResponse object.
     */
  private void displaySyncPreview(FsResponse fsResponse) {
    File fileBaseLocal;
    
    Node baseNodeLocal;
    Node baseNodeLocalUpdated;
    
    Node baseNodeRemote;
    
    try {
      syncStatus.setText("Checking for changes...");

      //prepare document object from the remote sync xml stored in the profile 
      String syncXMLRemote = fsProfile.getSyncXMLRemote();
      syncXMLDocumentRemote = commonUtil.getDocumentFromXMLString(syncXMLRemote);

      //prepare document object from the local sync xml stored in the profile
      String syncXMLLocal = fsProfile.getSyncXMLLocal();
      syncXMLDocumentLocal = commonUtil.getDocumentFromXMLString(syncXMLLocal);
      
      fileBaseLocal = new File(fsProfile.getLocalFolderPath());
      
      //prepare document object of the updated local content
      syncXMLDocumentUpdatedLocal = commonUtil.getEmptyDocumentObject();
      baseNodeLocalUpdated = clientUtil.getDomElement(syncXMLDocumentUpdatedLocal, fileBaseLocal);
      syncXMLDocumentUpdatedLocal.getDocumentElement().appendChild(baseNodeLocalUpdated);
      
      baseNodeLocal = syncXMLDocumentLocal.getDocumentElement().getFirstChild();
      
      FsSynchronizerClient fsSynchronizerClient = new FsSynchronizerClient(syncXMLDocumentRemote, syncXMLDocumentLocal);
      fsSynchronizerClient.getSyncXML(syncXMLDocumentUpdatedLocal, baseNodeLocalUpdated, fileBaseLocal, baseNodeLocal);


      //File fileLocal = new File("synxXMLLocalUpdate.xml");
      //commonUtil.saveXMLDocumentToFile(syncXMLDocumentUpdatedLocal, fileLocal);

      //prepare document object of the syncXML string send by the client        
      String synxXMLUpdatedRemote = (String)fsResponse.getData();
      Document synxXMLDocumentUpdatedRemote = commonUtil.getDocumentFromXMLString(synxXMLUpdatedRemote);

      //File fileRemote = new File("synxXMLRemoteUpdate.xml");
      //commonUtil.saveXMLDocumentToFile(synxXMLDocumentUpdatedRemote, fileRemote);

      baseNodeLocal = syncXMLDocumentUpdatedLocal.getDocumentElement().getFirstChild();
      baseNodeRemote = synxXMLDocumentUpdatedRemote.getDocumentElement().getFirstChild();

      fsSynchronizerClient.generateSyncPreview(linkedListOfFsSyncObject, baseNodeRemote, baseNodeLocal);

      if (linkedListOfFsSyncObject.size() == 0) {
        syncProgressBar.setIndeterminate(false);
        butStart.setEnabled(false);
        butStop.setEnabled(false);
        butRefresh.setEnabled(true);
        butClose.setEnabled(true);

        syncStatus.setText("No change found");
        JOptionPane.showMessageDialog(this, "No change found");
      } else {
        syncStatus.setText("Preparing display...");
        displayComparisonResultInTable();
        syncStatus.setText("Changes found");
        saveSyncedProfile();
        syncProgressBar.setIndeterminate(false);
        butStart.setEnabled(true);
        butStop.setEnabled(false);
        butRefresh.setEnabled(true);
        butClose.setEnabled(true);
      }

      Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
      setCursor(cursor);

    } catch (Exception ex) {
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
    }
  }


  /** Checks the status of Sync operation and displays the appropriate messages.
     */
  private void checkSyncCompletion() {
    if (syncThreadCounter > syncThreadCount) {
      JOptionPane.showMessageDialog(this, "Synchronization Stopped");
      butStart.setEnabled(false);
      butStop.setEnabled(false);
      butRefresh.setEnabled(true);
      butClose.setEnabled(true);
    } else if (syncThreadCounter == syncThreadCount) {
      saveSyncedProfile();
      syncProgressBar.setIndeterminate(false);
      butStart.setEnabled(false);
      butStop.setEnabled(false);
      butRefresh.setEnabled(true);
      butClose.setEnabled(true);
      if (uploadFailure) {
        SyncErrorMsg syncErrorMsg = new SyncErrorMsg(mainmdiFrame, "Sync Error", false);
        syncErrorMsg.getTaSyncErrorMsg().setText(syncErrorMsgString.toString());
        syncErrorMsg.setVisible(true);
      } else {
        JOptionPane.showMessageDialog(this, "Synchronization complete");
      }
    } else {
      saveSyncedProfile();
      syncThreadCounter++;
      syncNextItem();
    }
  }

  private void tblSyncPreview_mouseClicked(MouseEvent e) {
    FsTableModel fsTableModel = (FsTableModel)tblSyncPreview.getModel();

    if (e.getClickCount() > 1) {
      // The index of the column whose header was clicked
      int vRowIndex = tblSyncPreview.getSelectedRow();
      JLabel label = (JLabel)fsTableModel.getValueAt(vRowIndex, EnumSyncPreviewTable.STATUS_LOCAL);
      String statusLocal = label.getText();
      label = (JLabel)fsTableModel.getValueAt(vRowIndex, EnumSyncPreviewTable.STATUS_REMOTE);
      String statusRemote = label.getText();
      label = (JLabel)fsTableModel.getValueAt(vRowIndex, EnumSyncPreviewTable.ACTION);
      String action = label.getText();

      if ((statusLocal.equals(EnumSyncOperation.NEW_FILE)) && (statusRemote.equals(EnumSyncOperation.NEW_FILE))) {
        if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.NEW_FILE_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.NEW_FILE), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.NEW_FILE_REMOTE)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.NEW_FILE_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.NEW_FILE), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.NEW_FILE_LOCAL)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        }
      } else if (statusLocal.equals(EnumSyncOperation.NEW_FILE)) {
        if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.NEW_FILE_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.NEW_FILE), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.NEW_FILE_REMOTE)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        }
      } else if (statusRemote.equals(EnumSyncOperation.NEW_FILE)) {
        if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.NEW_FILE_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.NEW_FILE), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.NEW_FILE_LOCAL)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        }
      }

      if (statusLocal.equals(EnumSyncOperation.FILE_CHANGED) && statusRemote.equals(EnumSyncOperation.FILE_CHANGED)) {
        if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.UPDATE_FILE_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_CHANGED), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.UPDATE_FILE_REMOTE)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.UPDATE_FILE_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_CHANGED), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.UPDATE_FILE_LOCAL)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        }
      }

      if (statusLocal.equals(EnumSyncOperation.FILE_CHANGED) && statusRemote.equals(EnumSyncOperation.FILE_DELETED)) {
        if (action.equals(EnumSyncOperation.UPDATE_FILE_REMOTE)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.DELETE_FILE_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_DELETED), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.DELETE_FILE_LOCAL)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        } else if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.UPDATE_FILE_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_CHANGED), vRowIndex, EnumSyncPreviewTable.STATUS);
        }
      }

      if ((statusLocal.equals(EnumSyncOperation.FILE_CHANGED) &&
           statusRemote.equals(EnumSyncOperation.FILE_UNCHANGED))) {
        if (action.equals(EnumSyncOperation.UPDATE_FILE_REMOTE)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.UPDATE_FILE_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_CHANGED), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.UPDATE_FILE_LOCAL)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        } else if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.UPDATE_FILE_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_CHANGED), vRowIndex, EnumSyncPreviewTable.STATUS);
        }
      }

      if (statusLocal.equals(EnumSyncOperation.FILE_DELETED) &&
          statusRemote.equals(EnumSyncOperation.FILE_UNCHANGED)) {
        if (action.equals(EnumSyncOperation.DELETE_FILE_REMOTE)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.UPDATE_FILE_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_CHANGED), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.UPDATE_FILE_LOCAL)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        } else if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.DELETE_FILE_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_DELETED), vRowIndex, EnumSyncPreviewTable.STATUS);
        }
      }

      if (statusLocal.equals(EnumSyncOperation.FILE_DELETED) && statusRemote.equals(EnumSyncOperation.FILE_CHANGED)) {
        if (action.equals(EnumSyncOperation.UPDATE_FILE_LOCAL)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.DELETE_FILE_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_DELETED), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.DELETE_FILE_REMOTE)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        }
        if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.UPDATE_FILE_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_CHANGED), vRowIndex, EnumSyncPreviewTable.STATUS);
        }
      }

      if (statusLocal.equals(EnumSyncOperation.FILE_UNCHANGED) &&
          statusRemote.equals(EnumSyncOperation.FILE_CHANGED)) {
        if (action.equals(EnumSyncOperation.UPDATE_FILE_LOCAL)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.UPDATE_FILE_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_CHANGED), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.UPDATE_FILE_REMOTE)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        }
        if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.UPDATE_FILE_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_CHANGED), vRowIndex, EnumSyncPreviewTable.STATUS);
        }
      }

      if (statusLocal.equals(EnumSyncOperation.FILE_UNCHANGED) &&
          statusRemote.equals(EnumSyncOperation.FILE_DELETED)) {
        if (action.equals(EnumSyncOperation.DELETE_FILE_LOCAL)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.UPDATE_FILE_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_CHANGED), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.UPDATE_FILE_REMOTE)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        }
        if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.DELETE_FILE_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.FILE_DELETED), vRowIndex, EnumSyncPreviewTable.STATUS);
        }
      }

      if (statusLocal.equals(EnumSyncOperation.FOLDER_DELETED) &&
          (statusRemote.equals(EnumSyncOperation.FOLDER_UNCHANGED))) {
        if (action.equals(EnumSyncOperation.DELETE_FOLDER_REMOTE)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.NEW_FOLDER_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.NEW_FOLDER), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.NEW_FOLDER_LOCAL)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        } else if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.DELETE_FOLDER_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.FOLDER_DELETED), vRowIndex, EnumSyncPreviewTable.STATUS);
        }
      }

      if (statusLocal.equals(EnumSyncOperation.FOLDER_UNCHANGED) &&
          (statusRemote.equals(EnumSyncOperation.FOLDER_DELETED))) {
        if (action.equals(EnumSyncOperation.DELETE_FOLDER_LOCAL)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.NEW_FOLDER_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.NEW_FOLDER), vRowIndex, EnumSyncPreviewTable.STATUS);
        } else if (action.equals(EnumSyncOperation.NEW_FOLDER_REMOTE)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        } else if (action.equals(EnumSyncOperation.DO_NOTHING)) {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncBack, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.DELETE_FOLDER_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.FOLDER_DELETED), vRowIndex, EnumSyncPreviewTable.STATUS);
        }
      }

      if (statusLocal.equals(EnumSyncOperation.NEW_FOLDER)) {
        if (action.equals(EnumSyncOperation.NEW_FOLDER_REMOTE)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        } else {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.NEW_FOLDER_REMOTE), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.NEW_FOLDER), vRowIndex, EnumSyncPreviewTable.STATUS);
        }
      }

      if (statusRemote.equals(EnumSyncOperation.NEW_FOLDER)) {
        if (action.equals(EnumSyncOperation.NEW_FOLDER_LOCAL)) {
          fsTableModel.setValueAt(new JLabel(""), vRowIndex, EnumSyncPreviewTable.ACTION_DISPLAY);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.DO_NOTHING), vRowIndex, EnumSyncPreviewTable.ACTION);
        } else {
          fsTableModel
          .setValueAt(new JLabel("", FsImage.imgSyncForward, JLabel.CENTER), vRowIndex, EnumSyncPreviewTable
                                  .ACTION_DISPLAY);
          fsTableModel
          .setValueAt(new JLabel(EnumSyncOperation.NEW_FOLDER_LOCAL), vRowIndex, EnumSyncPreviewTable.ACTION);
          fsTableModel.setValueAt(new JLabel(EnumSyncOperation.NEW_FOLDER), vRowIndex, EnumSyncPreviewTable.STATUS);
        }
      }

    }
    fillItemDetailTbl();
  }

  private void fillItemDetailTbl() {
    Vector dataRow = new Vector(1);
    // The index of the column whose header was clicked
    int vRowIndex = tblSyncPreview.getSelectedRow();
    FsSyncObject fsSyncObject = (FsSyncObject)linkedListOfFsSyncObject.get(vRowIndex);
    Node nodeLocal = fsSyncObject.getLocalNode();
    Node nodeRemote = fsSyncObject.getRemoteNode();

    Vector dataCol;
    dataCol = getNodeAttributeVector(nodeRemote);
    dataCol.set(EnumItemDetailTable.ROW_HEADING, "Remote");
    dataRow.add(dataCol);

    dataCol = getNodeAttributeVector(nodeLocal);
    dataCol.set(EnumItemDetailTable.ROW_HEADING, "Local");
    dataRow.add(dataCol);

    JLabel label = (JLabel)fsTableModelSyncPreview.getValueAt(vRowIndex, EnumSyncPreviewTable.ACTION);
    tfAction.setText(label.getText());

    FsTableModel tableModelItemDetail = (FsTableModel)tblItemDetail.getModel();
    tableModelItemDetail.setDataVector(dataRow);
  }

  private Vector getNodeAttributeVector(Node node) {
    Vector dataCol;

    dataCol = new Vector(EnumItemDetailTable.COLUMN_LENGTH);
    for (int intI = 1; intI <= EnumItemDetailTable.COLUMN_LENGTH; intI++) {
      dataCol.add(new JLabel(""));
    }

    if (node != null) {
      NamedNodeMap attributes = node.getAttributes();
      dataCol.set(EnumItemDetailTable.NAME, attributes.getNamedItem("PATH").getNodeValue());
      String status = attributes.getNamedItem("STATUS").getNodeValue();
      dataCol.set(EnumItemDetailTable.STATUS, status);
      String itemType = node.getNodeName();
      if (itemType.equals("FILE")) {
        dataCol.set(EnumItemDetailTable.TYPE, "File");
        long size = Integer.parseInt(attributes.getNamedItem("SIZE").getNodeValue());
        dataCol
        .set(EnumItemDetailTable.SIZE, dbsentry.filesync.clientgui.utility.GeneralUtil.getDocSizeForDisplay(size));
        Date modifiedDate = new Date(Long.parseLong(attributes.getNamedItem("MODIFIED_DATE").getNodeValue()));
        dataCol
        .set(EnumItemDetailTable.MODIFIED_DATE, dbsentry.filesync.clientgui.utility.GeneralUtil.getDateForDisplay(modifiedDate));
      } else {
        dataCol.set(EnumItemDetailTable.TYPE, "Folder");
      }

    }
    return dataCol;
  }


  private void tblSyncPreview_keyReleased() {
    fillItemDetailTbl();
  }

  private void butRefresh_actionPerformed() {
    initializeTheControls();
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    syncProgressBar.setIndeterminate(true);
    mainmdiFrame.getFsClient().getSyncPreview(fsProfile.getRemoteFolderPath(),fsProfile.getSyncXMLRemote(), syncCommandCode);
    currentSyncDownloadCode=-1;
    currentSyncUploadCode=-1;
  }

  private void initializeTheControls() {
    syncStatus.setText("Looking for changes");
    linkedListOfFsSyncObject.clear();
    initializeFolderFileList();
    initializeItemDetailList();
    butRefresh.setEnabled(false);
    butStart.setEnabled(false);
    butStop.setEnabled(false);
    butClose.setEnabled(false);
  }

  public void butStop_actionPerformed() {
    logger.debug("currentSyncUploadCode : " + currentSyncUploadCode);
    logger.debug("currentSyncDownloadCode : " + currentSyncDownloadCode);
    
    if(currentSyncUploadCode!=-1 || currentSyncDownloadCode!=-1){
      syncProgressBar.setIndeterminate(false);
      butStart.setEnabled(false);
      butStop.setEnabled(false);
      butRefresh.setEnabled(true);
      butClose.setEnabled(true);
     
      mainmdiFrame.getFsClient().stopSync(currentSyncUploadCode,currentSyncDownloadCode);
      syncThreadCounter = syncThreadCount + 1;
    }
  }

  private interface EnumSyncPreviewTable {
    public static final String[] COLUMN_NAMES =
    { "Sl No.", "Status (Local)", "Action", "Status (Remote)", "Name", "Action Hidden", "Path Local", "Path Remote",
                                                  "Status" };

    public static final int COLUMN_LENGTH = 9;

    public static final int SL_NO = 0;

    public static final int STATUS_LOCAL = 1;

    public static final int ACTION_DISPLAY = 2;

    public static final int STATUS_REMOTE = 3;

    public static final int RELATIVE_PATH = 4;

    public static final int ACTION = 5;

    public static final int PATH_LOCAL = 6;

    public static final int PATH_REMOTE = 7;

    public static final int STATUS = 8;
  }

  private interface EnumItemDetailTable {
    public static final String[] COLUMN_NAMES = { "", "Status", "Type", "Size", "Modified Date", "Name" };

    public static final int COLUMN_LENGTH = 6;

    public static final int ROW_HEADING = 0;

    public static final int STATUS = 1;

    public static final int TYPE = 2;

    public static final int SIZE = 3;

    public static final int MODIFIED_DATE = 4;

    public static final int NAME = 5;

  }

  /**
   * A class which handles the progress display of sync operation in the table.
   */
  private class UpdateProgress implements Runnable {
    private int col;

    private int row;

    private Integer percent;

    /**
     * constructs UpdateProgress object.
     * @param row row to update
     * @param col column to update
     * @param percent percentage to display
     */
    public UpdateProgress(int row, int col, Integer percent) {
      this.row = row;
      this.col = col;
      this.percent = percent;
    }

    /**
     * overrides run function.
     */
    public void run() {
      JLabel progressLabel = (JLabel)fsTableModelSyncPreview.getValueAt(row, col);
      progressLabel.setText(percent.toString() + "%");
      fsTableModelSyncPreview.setValueAt(progressLabel, row, col);
    }
  }

  private class SyncCommandListener implements FsSyncCommandListener{
    private Document document;
    
    private int row = rowCounter;
    
    private int col = EnumSyncPreviewTable.ACTION_DISPLAY;
    
    private Integer percent;
    
    private FsFolderDocInfoHolder fsFolderDocInfoHolder;
    
    private FileSyncClientMDI mainmdiFrame;
    
    public SyncCommandListener(FileSyncClientMDI mainmdiFrame){
      this.mainmdiFrame=mainmdiFrame;
    }
    
    public void propertyChange(PropertyChangeEvent evt){
      try{
        int propertyName=Integer.valueOf(evt.getPropertyName());
        FsResponse fsResponse;
        FsExceptionHolder fsExceptionHolder;
        fsResponse = (FsResponse)evt.getNewValue();
        
        logger.debug("propertyName : "+ propertyName);
        switch(propertyName){
          case PREVIEW:
            displaySyncPreview(fsResponse);
            break;
          case REMOTE_DELETE:
            document = commonUtil.getDocumentFromXMLString((String)fsResponse.getData());
            commonUtil.mergeTwoDocument(syncXMLDocumentRemote, document);
            //delete the item from the syncXMLDocumentLocal
            Object datas[] = fsResponse.getDatas();
            Integer index = (Integer)datas[0];

            //remove the node from the syncXMLDocumentRemote
            Node node = ((FsSyncObject)linkedListOfFsSyncObject.get(index.intValue())).getLocalNode();
            commonUtil.removeNodeFromDocument(syncXMLDocumentLocal, node);

            percent = new Integer(100);
            SwingUtilities.invokeLater(new UpdateProgress(row, col, percent));

            checkSyncCompletion();
            break;
          case FAILED:
            checkSyncCompletion();
            break;
          case ERROR_MESSAGE:
            Object objects[]=null; 
            objects = fsResponse.getDatas();
            fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
            fsExceptionHolder = fsResponse.getFsExceptionHolder();
            if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
              || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
              || fsExceptionHolder.getErrorCode() == 30041){
              //tfStatus.setText("Access denied");
              mainmdiFrame.fsStatusBar.setLblSetMessage("");
              mainmdiFrame.fsStatusBar.setLblSetMessage("Access denied");
              JOptionPane.showInternalMessageDialog(fsFileSyncPreview,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }else if(fsExceptionHolder.getErrorCode() == 68005){
              String errorMsg = fsExceptionHolder.getErrorMessage();
              logger.debug("Error Message :" + errorMsg);
              JOptionPane.showInternalMessageDialog(fsFileSyncPreview, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
            }else{
              JOptionPane.showInternalMessageDialog(fsFileSyncPreview,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                mainmdiFrame.fsStatusBar.setLblSetMessage("");
                mainmdiFrame.fsStatusBar.setLblSetMessage(fsExceptionHolder.getErrorMessage());
              //tfStatus.setText("" + fsExceptionHolder);
            }
            logger.error(fsExceptionHolder);
            break;
          case FETAL_ERROR:
            //setWaitCursorForRemoteBrowser();
            logger.error("Fetal Error");
            //tfStatus.setText("Fatal Error");
             mainmdiFrame.fsStatusBar.setLblSetMessage("");
             mainmdiFrame.fsStatusBar.setLblSetMessage("Fetal Error");
            break;
                  
        }
      }catch(Exception ex){
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
      //tfStatus.setText( ex.getMessage());
       mainmdiFrame.fsStatusBar.setLblSetMessage("");
       mainmdiFrame.fsStatusBar.setLblSetMessage(ex.getMessage());
     }
    }
  }

  private class SyncUploadListener implements FsSyncUploadListener{ 
   private FsFileSyncPreview fsFileSyncPreview;
   
   private int syncUploadCode;
   
   private ClientUtil clientUtil;
   
   private Document document;
   
   private Integer percent;
   
   private int row = rowCounter;
  
   private int col = EnumSyncPreviewTable.ACTION_DISPLAY;
   
  private FsFolderDocInfoHolder fsFolderDocInfoHolder;
   
    public SyncUploadListener(FsFileSyncPreview fsFileSyncPreview,int syncUploadCode){
      this.fsFileSyncPreview=fsFileSyncPreview;
      this.syncUploadCode=syncUploadCode;
      this.clientUtil=new ClientUtil(logger);
    }
  
    public void propertyChange(PropertyChangeEvent evt){
      int propertyName=Integer.valueOf(evt.getPropertyName());
      FsResponse fsResponse;
      FsExceptionHolder fsExceptionHolder;
      fsResponse = (FsResponse)evt.getNewValue();
      logger.debug("fsResponse " + fsResponse);
      logger.debug("fsResponse.getSuperResponseCode() " + fsResponse.getSuperResponseCode());
      if(fsResponse.getSuperResponseCode().equals(Integer.toString(syncUploadCode))){
        switch(propertyName){
          
          case COMPLETED:
            String xmlData = (String)fsResponse.getData();
            //        System.out.println(xmlData);
            document = commonUtil.getDocumentFromXMLString(xmlData);
            //        System.out.println(commonUtil.getXMLStringFromDocument(syncXMLDocumentRemote));
            commonUtil.mergeTwoDocument(syncXMLDocumentRemote, document);

            percent = new Integer(100);
            SwingUtilities.invokeLater(new UpdateProgress(row, col, percent));

            checkSyncCompletion();
            break;
          case FAILED:
            syncErrorMsgString = syncErrorMsgString + "\n" + (String)fsResponse.getData();
            logger.debug("Error Message : " + syncErrorMsgString);
            JOptionPane
            .showInternalMessageDialog(fsFileSyncPreview, "Upload Syncronization Failed Permision Denied." , "Error", JOptionPane.ERROR_MESSAGE);
            uploadFailure = true;
            checkSyncCompletion();
            break;
          case FILE_CURRUPTED:
            JOptionPane
            .showInternalMessageDialog(fsFileSyncPreview, "Upload Syncronization Failed Due to File Curruption.. Please Retry Again.." , "Error", JOptionPane.ERROR_MESSAGE);
            uploadFailure = true;
            checkSyncCompletion();
            break;
          case STOPPED:
            checkSyncCompletion();
            break;
          case PROGRESS:
            logger.debug("data r comming 4 progress.......");
            Integer percent = (Integer)fsResponse.getData();
            int row = rowCounter;
            int col = EnumSyncPreviewTable.ACTION_DISPLAY;
            SwingUtilities.invokeLater(new UpdateProgress(row, col, percent));
            break;
          case ERROR_MESSAGE:
            logger.debug("Error message from clent to client Gui................");
            //Object objects[] = fsResponse.getDatas();
            uploadFailure = true;
            fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
            fsExceptionHolder = fsResponse.getFsExceptionHolder();
            if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
              || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
              || fsExceptionHolder.getErrorCode() == 30041){
              //tfStatus.setText("Access denied");
               fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage("");
               fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage("Access denied");
              checkSyncCompletion();
              JOptionPane.showInternalMessageDialog(fsFileSyncPreview,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }else if(fsExceptionHolder.getErrorCode() == 68005){
              String errorMsg = fsExceptionHolder.getErrorMessage();
              logger.debug("Error Message :" + errorMsg);
              checkSyncCompletion();
              JOptionPane.showInternalMessageDialog(fsFileSyncPreview, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
            }else{
              checkSyncCompletion();
              JOptionPane.showInternalMessageDialog(fsFileSyncPreview,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
              //tfStatus.setText("" + fsExceptionHolder);
               fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage("");
               fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage(fsExceptionHolder.getErrorMessage());
              
            }
            butStop_actionPerformed();
            logger.error(fsExceptionHolder);
            break;
          case SOCKET_CLOSE_FILE:
            logger.debug("sending close file request..........................");
            Document documentOfChanges = (Document)fsResponse.getData();
            document = (Document)fsResponse.getData1();
            mainmdiFrame.getFsClient().syncUploadCloseFile(fsResponse.getSuperResponseCode(),documentOfChanges,document);
            break;
          case FETAL_ERROR:
            uploadFailure = true;
            checkSyncCompletion();
            logger.error("Fetal Error");
            //tfStatus.setText("Fatal Error");
             fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage("");
             fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage("Fetal Error");
            butStop_actionPerformed();
            break;
        }
      }
    }
  }
 
  private class SyncDownloadListener implements FsSyncDownloadListener{
    private FsFileSyncPreview fsFileSyncPreview;
    
    private int syncDownloadCode;
    
    private ClientUtil clientUtil;
    
    private Document document;
    
    private Integer percent;
    
    private int row = rowCounter;
    
    private int col = EnumSyncPreviewTable.ACTION_DISPLAY;
    
    private FsFolderDocInfoHolder fsFolderDocInfoHolder;
    
    public SyncDownloadListener(FsFileSyncPreview fsFileSyncPreview,int syncDownloadCode){
      this.fsFileSyncPreview=fsFileSyncPreview;
      this.syncDownloadCode=syncDownloadCode;
      this.clientUtil=new ClientUtil(logger);
    }
    
    public void propertyChange(PropertyChangeEvent evt){
      int propertyName=Integer.valueOf(evt.getPropertyName());
      FsResponse fsResponse;
      FsExceptionHolder fsExceptionHolder;
      fsResponse = (FsResponse)evt.getNewValue();
      logger.debug("fsResponse " + fsResponse);
      logger.debug("fsResponse.getSuperResponseCode() " + fsResponse.getSuperResponseCode());
      if(fsResponse.getSuperResponseCode().equals(Integer.toString(syncDownloadCode))){
        switch(propertyName){
          case COMPLETED:
            //update remoteSyncXMLDocument to reflect the changes
            String remoteXMLData = (String)fsResponse.getData();
            document = commonUtil.getDocumentFromXMLString(remoteXMLData);
            commonUtil.mergeTwoDocument(syncXMLDocumentRemote, document);
  
            //update localSyncXMLDocument to reflect the changes
            String localXMLData = (String)fsResponse.getData1();
            document = commonUtil.getDocumentFromXMLString(localXMLData);
            commonUtil.mergeTwoDocument(syncXMLDocumentLocal, document);
  
            percent = new Integer(100);
            SwingUtilities.invokeLater(new UpdateProgress(row, col, percent));
  
            checkSyncCompletion();
            break;
          case PROGRESS:
            Integer percent = (Integer)fsResponse.getData1();
            int row = rowCounter;
            int col = EnumSyncPreviewTable.ACTION_DISPLAY;
            SwingUtilities.invokeLater(new UpdateProgress(row, col, percent));
            break;
          case FAILED:
            checkSyncCompletion();
            break;
          case STOPPED:            
            checkSyncCompletion();
            break;
          case FILE_CURRUPTED:
            checkSyncCompletion();
            break;
          case ERROR_MESSAGE:
            logger.debug("Error message from clent to client Gui................");
            //Object objects[] = fsResponse.getDatas();
            fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
            fsExceptionHolder = fsResponse.getFsExceptionHolder();
            if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
              || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
              || fsExceptionHolder.getErrorCode() == 30041){
              //tfStatus.setText("Access denied");
              fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage("");
              fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage("Access denied");
              checkSyncCompletion();
              JOptionPane.showInternalMessageDialog(fsFileSyncPreview,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }else if(fsExceptionHolder.getErrorCode() == 68005){
              String errorMsg = fsExceptionHolder.getErrorMessage();
              logger.debug("Error Message :" + errorMsg);
              checkSyncCompletion();
              JOptionPane.showInternalMessageDialog(fsFileSyncPreview, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
            }else{
              checkSyncCompletion();
              JOptionPane.showInternalMessageDialog(fsFileSyncPreview,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
              //tfStatus.setText("" + fsExceptionHolder);
               fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage("");
               fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage(fsExceptionHolder.getErrorMessage());
            }
            butStop_actionPerformed();
            logger.error(fsExceptionHolder);
            break;
          case SOCKET_CLOSE_FILE:
            logger.debug("sending close file request..........................");
            mainmdiFrame.getFsClient().syncDownloadCloseFile(fsResponse.getSuperResponseCode());
            break;
          case FETAL_ERROR:
            checkSyncCompletion();
            logger.error("Fetal Error");
            //tfStatus.setText("Fatal Error");
             fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage("");
             fsFileSyncPreview.mainmdiFrame.fsStatusBar.setLblSetMessage("Fetal Error");
            butStop_actionPerformed();
            break;
        }
      }
    }
  }
  
}

