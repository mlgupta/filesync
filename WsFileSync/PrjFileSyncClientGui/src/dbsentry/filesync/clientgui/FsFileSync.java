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
 * $Id: FsFileSync.java,v 1.106 2006/08/24 12:26:25 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.clientgui.enumconstants.EnumRemoteTable;
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyVetoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *	To provide an interface to create edit and sync a profile.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    Saurabh Gupta
 * 	Last Modfied Date:   03-04-2006 
 */

public class FsFileSync extends JInternalFrame {
  private Border normalButtonBorder;
  
  private Logger logger;
  
  private FsTableModel fsTableModelProfile;
  private FsTableModel fsTableModelProfileDetail;

  private Vector profileVector = new Vector();

  private int profileOperation;
  
  private JToolBar tbSync = new JToolBar();
  
  private JButton btnNew = new JButton();
  private JButton btnEdit = new JButton();
  private JButton btnDelete = new JButton();
  private JButton btnRunSyncPreview = new JButton();
  
  private JButton btnSave = new JButton();
  private JButton btnCancel = new JButton();
  private JButton btnExit = new JButton();
  
  private JButton btnLocalFolderBrowse = new JButton();
  private JButton btnRemoteFolderBrowse = new JButton();
  
  private CardLayout layout4Panel4Sync = new CardLayout();
  
  private JPanel panel4Sync = new JPanel();
  private JPanel panel4ProfileDetail = new JPanel();
  private JPanel panel4ProfileList = new JPanel();
  private JPanel panel4ProfileTable = new JPanel();
  private JPanel panel4ProfileEntry = new JPanel();


  private JLabel lblRemoteFolder = new JLabel();
  private JLabel lblLocalFolder = new JLabel();
  private JLabel lblProfileName = new JLabel();

  private JSplitPane splitProfileList = new JSplitPane();
  
  private JScrollPane scrollPane4ProfileList = new JScrollPane();
  private JScrollPane scrollPane4ProfileDetail = new JScrollPane();
  
  private JTextField txtProfileName = new JTextField();
  private JTextField txtLocalFolderPath = new JTextField();
  private JTextField txtRemoteFolderPath = new JTextField();
  
  private JTable tblProfile = new JTable();
  private JTable tblProfileDetail = new JTable();
  
  private JCheckBox chkIncludeSubFolder = new JCheckBox();

  private String localFolderName;
  private String remoteFolderName;

  private GeneralUtil generalUtil;
  private CommonUtil commonUtil;
  
  public static Hashtable fileSyncPreviewStatus = new Hashtable();
  
  private FileSyncClientMDI mainmdiframe;
  
  private String userHome = System.getProperty("user.home");
  
  //private FsLocalBrowser fsLocalBrowser;
  
/**
   * Construct FsFileSync object.
   * @param parent a frame which will act as parent of this frame.
   */
  public FsFileSync(FileSyncClientMDI parent){
      super("File Sync Synchronizer" , 
            true, //resizable
            true, //closable
            true, //maximizable
            true);//iconifiable
            setSize(100,100);
      
    try {
      this.mainmdiframe=parent;
      jbInit();
      this.logger = Logger.getLogger("ClientLogger");
      this.generalUtil = new GeneralUtil();
      this.generalUtil.centerForm(parent,this);
      this.commonUtil = new CommonUtil(logger);
      this.profileOperation = EnumProfileOperation.VIEW;
      enableDisableTabs();
      enableDisableButtons();
      initializeProfileTable();
      initializeProfileDetailTable();
      readProfileFromDisk();
      normalButtonBorder = btnNew.getBorder();
      if(!profileVector.isEmpty()){
        setSelectProfileDetail(0);
      }
    } catch(Exception e) {
      logger.error(generalUtil.getStackTrace(e));
    }
  }

  private void jbInit() throws Exception {
    
    
    //Button To Create New Synchronization Profile
    btnNew.setFont(new FsFont());
    btnNew.setPreferredSize(new Dimension(56, 39));
    btnNew.setSize(new Dimension(44, 38));
    btnNew.setMaximumSize(new Dimension(44, 40));
    btnNew.setHorizontalTextPosition(SwingConstants.CENTER);
    btnNew.setIcon(FsImage.iconNew);
    btnNew.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnNew.setIconTextGap(0);
    btnNew.setToolTipText("Create New Profile");
    btnNew.setOpaque(false);
    btnNew.setBorder(BorderFactory.createEmptyBorder());
    btnNew.addMouseListener(new MouseAdapter() {
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
    btnNew.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butNew_actionPerformed();
        }
      });
    
    //Button To Edit Synchrnization Profile
    btnEdit.setFont(new FsFont());
    btnEdit.setToolTipText("Edit Profile");
    btnEdit.setIcon(FsImage.iconEdit);
    btnEdit.setIconTextGap(0);
    btnEdit.setOpaque(false);
    btnEdit.setPreferredSize(new Dimension(56, 39));
    btnEdit.setMaximumSize(new Dimension(44, 40));
    btnEdit.setSize(new Dimension(56, 27));
    btnEdit.setHorizontalTextPosition(SwingConstants.CENTER);
    btnEdit.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnEdit.setBorder(BorderFactory.createEmptyBorder());
    btnEdit.addMouseListener(new MouseAdapter() {
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
    btnEdit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butEditProfile_actionPerformed();
        }
      });
    
    //Button for Delete Synchronization Profile
    btnDelete.setFont(new FsFont());
    btnDelete.setToolTipText("Delete Profile");
    btnDelete.setIcon(FsImage.iconDelete);
    btnDelete.setIconTextGap(0);
    btnDelete.setOpaque(false);
    btnDelete.setPreferredSize(new Dimension(56, 39));
    btnDelete.setMinimumSize(new Dimension(33, 44));
    btnDelete.setMaximumSize(new Dimension(44, 40));
    btnDelete.setSize(new Dimension(56, 27));
    btnDelete.setHorizontalTextPosition(SwingConstants.CENTER);
    btnDelete.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnDelete.setBorder(BorderFactory.createEmptyBorder());
    btnDelete.addMouseListener(new MouseAdapter() {
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
    btnDelete.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butDeleteProfile_actionPerformed();
        }
      });
    
    //Button To Run Synhronization Preview
    btnRunSyncPreview.setFont(new FsFont());
    btnRunSyncPreview.setToolTipText("Run Profile");
    btnRunSyncPreview.setIcon(FsImage.iconPreview);
    btnRunSyncPreview.setIconTextGap(0);
    btnRunSyncPreview.setOpaque(false);
    btnRunSyncPreview.setPreferredSize(new Dimension(56, 39));
    btnRunSyncPreview.setMaximumSize(new Dimension(44, 40));
    btnRunSyncPreview.setSize(new Dimension(56, 27));
    btnRunSyncPreview.setHorizontalTextPosition(SwingConstants.CENTER);
    btnRunSyncPreview.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnRunSyncPreview.setBorder(BorderFactory.createEmptyBorder());
    btnRunSyncPreview.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
          button_mouseEntered(e);
        }

        public void mouseExited(MouseEvent e) {
          button_mouseExited(e);
        }

        public void mouseReleased(MouseEvent e) {
          button_mouseReleased(e);
        }

        public void mousePressed(MouseEvent e) {
          button_mousePressed(e);
        }
      });
    btnRunSyncPreview.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRunProfile_actionPerformed();
        }
      });

    //Button to Exit File Synchronization
    btnExit.setFont(new FsFont());
    btnExit.setToolTipText("Exit");
    btnExit.setIcon(FsImage.iconExit);
    btnExit.setIconTextGap(0);
    btnExit.setOpaque(false);
    btnExit.setPreferredSize(new Dimension(56, 39));
    btnExit.setMaximumSize(new Dimension(44, 40));
    btnExit.setSize(new Dimension(56, 27));
    btnExit.setHorizontalTextPosition(SwingConstants.CENTER);
    btnExit.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnExit.setBorder(BorderFactory.createEmptyBorder());
    btnExit.addMouseListener(new MouseAdapter() {
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
    btnExit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butExit_actionPerformed();
        }
      });
      
      
    //Toolbar for All Buttons
    tbSync.setFloatable(false);
    tbSync.setPreferredSize(new Dimension(410, 35));
    tbSync.setMinimumSize(new Dimension(252, 40));
    tbSync.setMaximumSize(new Dimension(394, 50));
    tbSync.setSize(new Dimension(604, 45));
    tbSync.setBorder(BorderFactory.createEtchedBorder());
    tbSync.add(btnNew, null);
    tbSync.add(btnEdit, null);
    tbSync.add(btnDelete, null);
    tbSync.add(btnRunSyncPreview, null);
    tbSync.add(btnExit, null);
    
    
    tblProfile.setOpaque(false);
    tblProfile.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          tblProfile_mouseClicked(e);
        }
      });
    
    scrollPane4ProfileList.getViewport().add(tblProfile, null);
    scrollPane4ProfileList.setBorder(BorderFactory.createEmptyBorder());
    
    panel4ProfileTable.setLayout(new BorderLayout());
    panel4ProfileTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Profile List"));
    panel4ProfileTable.add(scrollPane4ProfileList, BorderLayout.CENTER);
    
    tblProfileDetail.setOpaque(false);
    tblProfileDetail.setFont(new Font("Tahoma", 1, 13));
    
    scrollPane4ProfileDetail.setBorder(BorderFactory.createEmptyBorder());
    scrollPane4ProfileDetail.getViewport().add(tblProfileDetail, null);
    
    panel4ProfileDetail.setLayout(new BorderLayout());
    panel4ProfileDetail.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Profile Detail"));
    panel4ProfileDetail.add(scrollPane4ProfileDetail, BorderLayout.CENTER);
    
    splitProfileList.setDividerLocation(250);
    splitProfileList.setLastDividerLocation(80);
    splitProfileList.setDividerSize(6);
    splitProfileList.setBorder(BorderFactory.createEmptyBorder());
    splitProfileList.add(panel4ProfileDetail, JSplitPane.BOTTOM);
    splitProfileList.add(panel4ProfileTable, JSplitPane.TOP);
    
    panel4ProfileList.setLayout(new BorderLayout());
    panel4ProfileList.setFont(new Font("Tahoma", 1, 12));
    panel4ProfileList.setPreferredSize(new Dimension(262, 68));
    panel4ProfileList.setBorder(BorderFactory.createEmptyBorder());
    panel4ProfileList.add(tbSync, BorderLayout.NORTH);
    panel4ProfileList.add(splitProfileList, BorderLayout.CENTER);
    
    
    lblProfileName.setText("Profile Name");
    lblProfileName.setBounds(new Rectangle(55, 30, 80, 20));
    
    txtProfileName.setText("Profile Name");
    txtProfileName.setBorder(BorderFactory.createEtchedBorder());
    txtProfileName.setBounds(new Rectangle(160, 30, 305, 25));
    
    lblRemoteFolder.setText("Remote Folder");
    lblRemoteFolder.setBounds(new Rectangle(55, 89, 90, 25));
    
    txtRemoteFolderPath.setText("Remote Folder Path");
    txtRemoteFolderPath.setBorder(BorderFactory.createEtchedBorder());
    txtRemoteFolderPath.setBounds(new Rectangle(160, 88, 305, 25));
    txtRemoteFolderPath.setEditable(false);
    
    btnRemoteFolderBrowse.setText("Browse...");
    btnRemoteFolderBrowse.setBounds(new Rectangle(485, 88, 90, 25));
    btnRemoteFolderBrowse.setBorder(BorderFactory.createEtchedBorder());
    btnRemoteFolderBrowse.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          btnRemoteFolderBrowse_actionPerformed();
        }
      });
      
//    btnLocalFolderBrowse.setText("Browse...");
//    btnLocalFolderBrowse.setBounds(new Rectangle(485, 146, 90, 25));
//    btnLocalFolderBrowse.setBorder(BorderFactory.createEtchedBorder());
//    btnLocalFolderBrowse.addActionListener(new ActionListener() {
//        public void actionPerformed(ActionEvent e) {
//          btnLocalFolderBrowse_actionPerformed();
//        }
//      });
      
    lblLocalFolder.setText("Local Folder");
    lblLocalFolder.setBounds(new Rectangle(55, 148, 90, 25));
    
    txtLocalFolderPath.setText("Local Folder Path");
    txtLocalFolderPath.setBounds(new Rectangle(160, 146, 305, 25));
    txtLocalFolderPath.setBorder(BorderFactory.createEtchedBorder());
    txtLocalFolderPath.setEditable(false);
    
    btnLocalFolderBrowse.setText("Browse...");
    btnLocalFolderBrowse.setBounds(new Rectangle(485, 146, 90, 25));
    btnLocalFolderBrowse.setBorder(BorderFactory.createEtchedBorder());
    btnLocalFolderBrowse.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          btnLocalFolderBrowse_actionPerformed();
        }
      });


    chkIncludeSubFolder.setText("Include Subfolders");
    chkIncludeSubFolder.setBounds(new Rectangle(160, 210, 145, 20));
    chkIncludeSubFolder.setSelected(true);
    
    btnSave.setText("Save");
    btnSave.setIcon(FsImage.iconSave);
    btnSave.setBounds(new Rectangle(415, 310, 70, 25));
    btnSave.setPreferredSize(new Dimension(80, 25));
    btnSave.setMinimumSize(new Dimension(80, 25));
    btnSave.setMaximumSize(new Dimension(80, 25));
    btnSave.setMargin(new Insets(0, 0, 0, 0));
    btnSave.setBorder(BorderFactory.createEtchedBorder());
    btnSave.setHorizontalAlignment(SwingConstants.LEFT);
    btnSave.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butSave_actionPerformed();
        }
      });
      
    btnCancel.setText("Cancel");
    btnCancel.setIcon(FsImage.iconCancel);
    btnCancel.setBounds(new Rectangle(490, 310, 70, 25));
    btnCancel.setPreferredSize(new Dimension(80, 25));
    btnCancel.setMinimumSize(new Dimension(80, 25));
    btnCancel.setMaximumSize(new Dimension(80, 25));
    btnCancel.setMargin(new Insets(0, 0, 0, 0));
    btnCancel.setBorder(BorderFactory.createEtchedBorder());
    btnCancel.setHorizontalAlignment(SwingConstants.LEFT);
    btnCancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butCancel_actionPerformed();
        }
      });
    
    
    panel4ProfileEntry.setLayout(null);
    panel4ProfileEntry.setPreferredSize(new Dimension(262, 80));
    panel4ProfileEntry.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Create Synchronization Profile"));
    panel4ProfileEntry.add(lblProfileName, null);
    panel4ProfileEntry.add(txtProfileName, null);
    panel4ProfileEntry.add(lblRemoteFolder, null);
    panel4ProfileEntry.add(txtRemoteFolderPath, null);
    panel4ProfileEntry.add(btnRemoteFolderBrowse, null);
    panel4ProfileEntry.add(lblLocalFolder, null);
    panel4ProfileEntry.add(txtLocalFolderPath, null);
    panel4ProfileEntry.add(btnLocalFolderBrowse, null);
    panel4ProfileEntry.add(chkIncludeSubFolder, null);
    panel4ProfileEntry.add(btnSave, null);
    panel4ProfileEntry.add(btnCancel, null);

    panel4Sync.setLayout(layout4Panel4Sync);
    panel4Sync.setBorder(BorderFactory.createEmptyBorder());
    panel4Sync.add(panel4ProfileList, "jpList");
    panel4Sync.add(panel4ProfileEntry, "jpEntry");
    
    //this.setFrameIcon(FsImage.imageTitle);
    this.setSize(new Dimension(593, 465));
    this.setTitle("Sync Profile");
    this.setResizable(true);
    this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(panel4Sync, BorderLayout.CENTER);
    this.setBorder(BorderFactory.createEmptyBorder());
  }

    /**
     * Reads the Vector object containing FsProfile objects from a file stored on disk. It displays 
     * all the profiles stored in vector in a table named tblProfile.
     */
    private void readProfileFromDisk(){
    ObjectInputStream ois = null;
    FileInputStream fis = null;
        
    File profile = new File(userHome + "/.dbsfs/syncprofile/" + mainmdiframe.getFsUser().getUserId() +".fs");
    
    Vector dataRow = new Vector();
    Vector dataCol = new Vector();
    FsProfile fsProfile;
    try{
      if(profile.exists()){
        fis = new FileInputStream(profile);
        ois = new ObjectInputStream(fis);
        profileVector = (Vector)ois.readObject();        
        if(profileVector != null){
          int profileVectorSize = profileVector.size();
          if(profileVector.size() == 0){
            btnDelete.setEnabled(false);
            btnEdit.setEnabled(false);
            btnRunSyncPreview.setEnabled(false);  
          }
          for(int index = 0; index < profileVectorSize; index++){
            fsProfile = (FsProfile)profileVector.get(index);
            dataCol = new Vector(EnumProfileTable.COLUMN_LENGTH);
            dataCol.add(EnumProfileTable.SL_NO, new JLabel("" + (index + 1) ,JLabel.RIGHT));        
            dataCol.add(EnumProfileTable.PROFILE_NAME, new JLabel(" " + fsProfile.getProfileName()));        
            dataRow.add(dataCol);
          }
        }else{
          btnDelete.setEnabled(false);
          btnEdit.setEnabled(false);
          btnRunSyncPreview.setEnabled(false);
        }
      }
      fsTableModelProfile = (FsTableModel)tblProfile.getModel();
      fsTableModelProfile.setDataVector(dataRow);
    
    }catch(Exception ex){
      logger.error(GeneralUtil.getStackTrace(ex));
    }finally{
      try{
        if(ois != null){
          ois.close();
        }
        if(fis != null){
          fis.close();
        }
      }catch(Exception ex1){
        logger.error(GeneralUtil.getStackTrace(ex1));
      }
    }
  }

  /**
     * It sets the properties of table displaying the profiles named tblProfile.
     */
  private void initializeProfileTable(){
    int itemLength = 1;
    Vector dataRow = new Vector((int)itemLength);

    fsTableModelProfile = new FsTableModel(EnumProfileTable.COLUMN_NAMES,dataRow);
    tblProfile.setModel(fsTableModelProfile);
    tblProfile.setAutoCreateColumnsFromModel(false);
    tblProfile.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    //set default cell renderer
    FsTableCellRenderer fsTableCellRenderer = new FsTableCellRenderer();
    tblProfile.setDefaultRenderer(JLabel.class,fsTableCellRenderer);

    DefaultTableColumnModel colModel = (DefaultTableColumnModel)tblProfile.getColumnModel();
    colModel.getColumn(EnumProfileTable.SL_NO).setPreferredWidth(40);
    colModel.getColumn(EnumProfileTable.PROFILE_NAME).setPreferredWidth(185);
    
    JTableHeader tableHeader = tblProfile.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(),18));
    tableHeader.getColumnModel().getColumn(EnumProfileTable.SL_NO).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumProfileTable.COLUMN_NAMES[EnumProfileTable.SL_NO]));
    tableHeader.getColumnModel().getColumn(EnumProfileTable.PROFILE_NAME).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumProfileTable.COLUMN_NAMES[EnumProfileTable.PROFILE_NAME]));
  }

  /**
     * It sets the properties of table displaying the profile details named tblProfileDetail. 
     */
  private void initializeProfileDetailTable(){
    int itemLength = 1;
    Vector dataRow = new Vector((int)itemLength);

    fsTableModelProfileDetail = new FsTableModel(EnumProfileDetailTable.COLUMN_NAMES,dataRow);
    tblProfileDetail.setModel(fsTableModelProfileDetail);
    tblProfileDetail.setAutoCreateColumnsFromModel(false);
    tblProfileDetail.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    //set default cell renderer
    FsTableCellRenderer fsTableCellRenderer = new FsTableCellRenderer();
    tblProfileDetail.setDefaultRenderer(JLabel.class,fsTableCellRenderer);

    DefaultTableColumnModel colModel = (DefaultTableColumnModel)tblProfileDetail.getColumnModel();
    colModel.getColumn(EnumProfileDetailTable.PROPERTY).setPreferredWidth(150);
    colModel.getColumn(EnumProfileDetailTable.VALUE).setPreferredWidth(170);
    
    JTableHeader tableHeader = tblProfileDetail.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(),18));
    tableHeader.getColumnModel().getColumn(EnumProfileDetailTable.PROPERTY).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumProfileDetailTable.COLUMN_NAMES[EnumProfileDetailTable.PROPERTY]));
    tableHeader.getColumnModel().getColumn(EnumProfileDetailTable.VALUE).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumProfileDetailTable.COLUMN_NAMES[EnumProfileDetailTable.VALUE]));
  }

  /**It stores newly created or edited profile in FsProfile object and then saves it on disk.
     */
  private void butSave_actionPerformed() {
    FsProfile fsProfile = null;
    int profileVectorSize;
       
    if(txtProfileName.getText().trim().equals("")){
      JOptionPane.showMessageDialog(this,"Enter profile name");
      txtProfileName.requestFocus();
      return;
    }

    if(txtLocalFolderPath.getText().trim().equals("")){
      JOptionPane.showMessageDialog(this,"Enter path of the local folder to synchronize");
      txtLocalFolderPath.requestFocus();
      return;
    }

    if(txtRemoteFolderPath.getText().trim().equals("")){
      JOptionPane.showMessageDialog(this,"Enter path of the remote folder to synchronize");
      txtRemoteFolderPath.requestFocus();
      return;
    }
    
    try{
      profileVectorSize = profileVector.size();
      int selectRow = 0;
      switch (profileOperation){
      case EnumProfileOperation.NEW:
        if(!checkForProfileExistance(txtProfileName.getText())){
          fsProfile = new FsProfile();
          setProfile(fsProfile);
          profileVector.add(fsProfile);
        }else{
          JOptionPane.showMessageDialog(this,"A profile with this name already exists","Profile Exists",JOptionPane.OK_OPTION);
          return;
        }
        break;
      case EnumProfileOperation.EDIT:
        selectRow = tblProfile.getSelectedRow();
        fsProfile = (FsProfile)profileVector.get(selectRow);
        setProfile(fsProfile);
        break;
      }
      saveProfileOnDisk();
      readProfileFromDisk();
      
      switch (profileOperation){
      case EnumProfileOperation.NEW:
        setSelectProfileDetail( tblProfile.getRowCount() - 1);
        break;
      case EnumProfileOperation.EDIT:
        setSelectProfileDetail(selectRow);
        break;
      }
      
      profileOperation = EnumProfileOperation.VIEW;
      enableDisableTabs();
      enableDisableButtons();
    }catch(Exception ex){
      logger.error(GeneralUtil.getStackTrace(ex));
    }
  }

  /** Checks whether the profile with the given name already exists.
     * @param profileName name of profile whose existence is to be checked.
     * @return true if profileName already exists and false if not.
     */
  private boolean checkForProfileExistance(String profileName){
    Iterator iterator = profileVector.iterator();
    FsProfile fsProfile;
    boolean profileExists = false;
    while(iterator.hasNext()){
      fsProfile = (FsProfile)iterator.next();
      if(fsProfile.getProfileName().equals(profileName)){
        profileExists = true;
        break;
      }
    }
    return profileExists;
  }
  
  /**
   * Saves the profile on disk.
   */
  public void saveProfileOnDisk(){
    ObjectOutputStream oos = null;
    FileOutputStream fos = null;
    try{
      
      File profile = new File(userHome + "/.dbsfs/syncprofile/" + mainmdiframe.getFsUser().getUserId() +".fs");
      
      if(profile.exists()){
        profile.delete();
      }
      
      profile.createNewFile();
      fos = new FileOutputStream(profile);
      oos = new ObjectOutputStream(fos);
      oos.writeObject(profileVector);
      
    }catch(Exception ex){
      logger.error(GeneralUtil.getStackTrace(ex));
    }finally{
      try{
        if(oos != null){
         oos.close();
        }
        if(fos != null){
         oos.close();
        }
      }catch(Exception ex1){
        logger.error(GeneralUtil.getStackTrace(ex1));
      }
    }
  }

  /** Sets all the parameters of FsProfile object with the values selected by user.
     * @param fsProfile FsProfile object whose parameters is to be set.
     * @throws Exception
     */
  private void setProfile(FsProfile fsProfile) throws Exception{
    try{
      fsProfile.setProfileName(txtProfileName.getText().trim());
      fsProfile.setRemoteFolderPath(txtRemoteFolderPath.getText().trim());
      fsProfile.setLocalFolderPath(txtLocalFolderPath.getText().trim());
      fsProfile.setIncludeSubFolder(chkIncludeSubFolder.isSelected());
              
      //create empty xml file
      Document document = commonUtil.getEmptyDocumentObject();
      Element element = document.createElement("FOLDER"); 
      element.setAttribute("PATH", txtRemoteFolderPath.getText().trim());
      element.setAttribute("NAME", remoteFolderName);
      element.setAttribute("ID", commonUtil.generateMD5Sum(txtRemoteFolderPath.getText().trim()));
      document.getDocumentElement().appendChild(element);
      String syncXML = commonUtil.getXMLStringFromDocument(document);
      fsProfile.setSyncXMLRemote(syncXML);

      //create empty xml file
      document = commonUtil.getEmptyDocumentObject();
      element = document.createElement("FOLDER"); 
      element.setAttribute("PATH", txtLocalFolderPath.getText().trim());
      element.setAttribute("NAME", localFolderName);
      element.setAttribute("ID", commonUtil.generateMD5Sum(txtLocalFolderPath.getText().trim()));
      document.getDocumentElement().appendChild(element);
      syncXML = commonUtil.getXMLStringFromDocument(document);
      fsProfile.setSyncXMLLocal(syncXML);
    }catch(Exception ex){
      logger.error(GeneralUtil.getStackTrace(ex));
      throw ex;
    }
  }

  /** Allows user to edit profile details of selected profile .
   */
  private void butEditProfile_actionPerformed() {
    FsProfile fsProfile;
    if(tblProfile.getSelectedRow() >= 0){
      fsProfile = (FsProfile)profileVector.get(tblProfile.getSelectedRow());
      chkIncludeSubFolder.setSelected(fsProfile.isIncludeSubFolder());
      txtLocalFolderPath.setText(fsProfile.getLocalFolderPath());
      txtProfileName.setText(fsProfile.getProfileName());
      txtRemoteFolderPath.setText(fsProfile.getRemoteFolderPath());
      profileOperation = EnumProfileOperation.EDIT;
      enableDisableTabs();
      enableDisableButtons();
    }
    
  }

  /**Allows user to delete a profile from the list of profiles.
   */
  private void butDeleteProfile_actionPerformed() {
    if (tblProfile.getSelectedRow() >= 0) {
      if(JOptionPane.showConfirmDialog(this,"Delete the profile?","Delete Profile",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
        profileOperation = EnumProfileOperation.DELETE;
        profileVector.remove(tblProfile.getSelectedRow());
        saveProfileOnDisk();
        readProfileFromDisk();
        if(profileVector.isEmpty()){
          initializeProfileDetailTable();      
        }else{
          setSelectProfileDetail(0);
        }
      }
    }
  }

  /**Allows user to create a new profile with the parameters set to its default value.
   */
  private void butNew_actionPerformed() {
    chkIncludeSubFolder.setSelected(true);
    txtLocalFolderPath.setText("");
    txtProfileName.setText("");
    txtRemoteFolderPath.setText("");
    profileOperation = EnumProfileOperation.NEW;
    enableDisableTabs();
    enableDisableButtons();
  }

  private void butExit_actionPerformed() {
    this.dispose();
  }

  /** It shows the different panels depending upon the profile operation chosen such as VIEW,EDIT,NEW.
    */
  private void enableDisableTabs(){
    switch (profileOperation){
    case EnumProfileOperation.VIEW:
      layout4Panel4Sync.show(panel4Sync,"jpList");
      break;
    case EnumProfileOperation.NEW:
    case EnumProfileOperation.EDIT:
      layout4Panel4Sync.show(panel4Sync,"jpEntry");
      txtProfileName.requestFocus();
      break;
    }
  }
  
  /** It enables disables buttons depending upon the profile operation chosen such as VIEW,NEW,EDIT.
    */
  private void enableDisableButtons(){
    switch (profileOperation){
    case EnumProfileOperation.NEW:
      btnEdit.setEnabled(false);
      btnDelete.setEnabled(false);
      btnRunSyncPreview.setEnabled(false);
      btnNew.setEnabled(false);
      btnExit.setEnabled(false);
      btnRemoteFolderBrowse.setEnabled(true);
      btnLocalFolderBrowse.setEnabled(true);
      break;
    case EnumProfileOperation.VIEW:
      btnEdit.setEnabled(true);
      btnDelete.setEnabled(true);
      btnNew.setEnabled(true);
      btnRunSyncPreview.setEnabled(true);
      btnExit.setEnabled(true);
      btnRemoteFolderBrowse.setEnabled(false);
      btnLocalFolderBrowse.setEnabled(false);
      break;
    case EnumProfileOperation.EDIT:
      btnDelete.setEnabled(false);
      btnEdit.setEnabled(false);
      btnRunSyncPreview.setEnabled(false);
      btnExit.setEnabled(false);
      btnNew.setEnabled(false);
      break;
    } 
      
  }


  /** It executes the selected profile to view the differences between the selected local and remote folder.
    */
  private void butRunProfile_actionPerformed() {
    int vRowIndex = tblProfile.getSelectedRow();
    FsProfile fsProfile =  (FsProfile)profileVector.get(vRowIndex);
    String profileName = fsProfile.getProfileName();
    FsFileSyncPreview fsFileSyncPreview=null;
    if(fileSyncPreviewStatus.containsKey(profileName)){
      logger.debug("FsFileSyncPreview is already opened"); 
      
      fsFileSyncPreview = ((FsFileSyncPreview)fileSyncPreviewStatus.get(profileName));
      //fsFileSyncPreview.setsetState(JFrame.NORMAL);                                         //TODO 
      fsFileSyncPreview.toFront();
    }else{
      File file = new File(fsProfile.getLocalFolderPath());
      if(!file.exists()){
        JOptionPane.showMessageDialog(this, "\"" + file.getAbsolutePath() + "\" does not exists");
        return;
      }
      fsFileSyncPreview = new FsFileSyncPreview(mainmdiframe,this,fsProfile);
      
      this.getParent().add(fsFileSyncPreview);
      fsFileSyncPreview.setVisible(true);
      try {
        fsFileSyncPreview.setFrameIcon(FsImage.imageFileSync16x16);
        fsFileSyncPreview.toFront();
        fsFileSyncPreview.setResizable(true);
        fsFileSyncPreview.setMaximum(true);
        
      }
      catch (PropertyVetoException e) {
        ;
      }
      fileSyncPreviewStatus.put(profileName,fsFileSyncPreview);
    }
  }

  private void tblProfile_mouseClicked(MouseEvent e) {
    if(e.getClickCount() == 1){     
      JTable table = (JTable)e.getSource();
      int vRowIndex = table.getSelectedRow();
      setSelectProfileDetail(vRowIndex);
    }
  }
  
  private void  setSelectProfileDetail(int vRowIndex){
    Vector dataRow = new Vector();
    Vector dataCol;
    FsProfile fsProfile =  (FsProfile)profileVector.get(vRowIndex);
    JLabel label;
    
    dataCol = new Vector(2);   
    label = new JLabel(" Profile name " ,JLabel.LEFT);
    dataCol.add(EnumProfileDetailTable.PROPERTY, label );
    label = new JLabel(" " + fsProfile.getProfileName(),JLabel.LEFT);
    dataCol.add(EnumProfileDetailTable.VALUE, label);
    dataRow.add(dataCol);   
    
    dataCol = new Vector(2);
    label = new JLabel(" Remote path ",JLabel.LEFT);
    dataCol.add(EnumProfileDetailTable.PROPERTY, label);
    label = new JLabel(" " + fsProfile.getRemoteFolderPath(),JLabel.LEFT);
    dataCol.add(EnumProfileDetailTable.VALUE, label);
    dataRow.add(dataCol);
    
    dataCol = new Vector(2);
    label = new JLabel(" Local path " ,JLabel.LEFT);
    dataCol.add(EnumProfileDetailTable.PROPERTY, label);
    label = new JLabel(" " + fsProfile.getLocalFolderPath(),JLabel.LEFT);
    dataCol.add(EnumProfileDetailTable.VALUE, label);
    dataRow.add(dataCol);   
    
    dataCol = new Vector(2);
    label = new JLabel(" Include Subfolder ",JLabel.LEFT);
    dataCol.add(EnumProfileDetailTable.PROPERTY, label);
    label = new JLabel(" " + fsProfile.isIncludeSubFolder(),JLabel.LEFT);
    dataCol.add(EnumProfileDetailTable.VALUE, label);
    dataRow.add(dataCol);
    
    if(fsProfile.isSyncStatus()){
      btnRemoteFolderBrowse.setEnabled(false);
      btnLocalFolderBrowse.setEnabled(false);
    }else{
      btnRemoteFolderBrowse.setEnabled(true);
      btnLocalFolderBrowse.setEnabled(true);
    }
    tblProfile.setRowSelectionInterval(vRowIndex,vRowIndex); 
    tblProfile.repaint();
    fsTableModelProfileDetail = (FsTableModel)tblProfileDetail.getModel();
    fsTableModelProfileDetail.setDataVector(dataRow);
  }
  /**
   * getter for the txtLocalFolderPath property which contains the local folder path.
   * @return  JTextField object containing the local folder path
   */
  public JTextField getTxtLocalFolderPath(){
    return txtLocalFolderPath;
  }

  /**
   * getter for the txtRemoteFolderPath property which contains the local folder path.
   * @return  JTextField object containing the remote folder path
   */
   public JTextField getTxtRemoteFolderPath(){
    return txtRemoteFolderPath;
  }

 

  private void btnRemoteFolderBrowse_actionPerformed() {                      // testing Code 4 Remote Browser.....
      FsRemoteBrowser fsRemoteBrowser = new FsRemoteBrowser (mainmdiframe,this, mainmdiframe.getFsClient(), "Browser", false);
      mainmdiframe.getFsClient().getRootOfFolder(FsRemoteOperationConstants.REMOTE_BROWSER);
      fsRemoteBrowser.setVisible(true);
    }
    
  /** Prepares to view local filesystem so that user can choose a local folder for 
    * synchronization.
    */
  private void btnLocalFolderBrowse_actionPerformed() {
    FsLocalBrowser fsLocalBrowser = new FsLocalBrowser (mainmdiframe,this,"Browser",false);
    fsLocalBrowser.setVisible(true);
  }
  
  private void butCancel_actionPerformed() {
    profileOperation = EnumProfileOperation.VIEW;
    enableDisableTabs();
    enableDisableButtons();
    btnRunSyncPreview.setEnabled(true);
  }

  private void button_mouseEntered(MouseEvent e) {
    JButton button = (JButton)e.getSource();
     if(button.isEnabled()){
       button.setBorder(BorderFactory.createEtchedBorder());
     }
  }

  private void button_mouseExited(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(normalButtonBorder);
  }

  private void button_mousePressed(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
  }

  private void button_mouseReleased(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(normalButtonBorder);
  }

  /**
   * getter for local folder name.
   * @return local folder name
   */
  public String getLocalFolderName() {
    return localFolderName;
  }

  /**
   * setter for local folder name.
   * @param localFolderName local folder name.
   */
  public void setLocalFolderName(String localFolderName) {
    this.localFolderName = localFolderName;

  }

  /**
   * getter for remote folder name.
   * @return remote folder name
   */
  public String getRemoteFolderName() {
    return remoteFolderName;
  }

  /**
   * setter for remote folder name.
   * @param remoteFolderName remote folder name
   */
  public void setRemoteFolderName(String remoteFolderName) {
    this.remoteFolderName = remoteFolderName;
  }

  
  /** It's an interface containing the constants for column names, no. of columns and 
 * index of each column of tblProfile table for easier access.
 */
  private interface EnumProfileTable {
    public static final String[] COLUMN_NAMES = { "Sl No.", "Profile Name" };

    public static final int COLUMN_LENGTH = 2;

    public static final int SL_NO = 0;

    public static final int PROFILE_NAME = 1;
  }

  /** It's an interface containing the constants for column names, no. of columns and index of each column of 
 * tblProfileDetail table, for easier access.
 */
  private interface EnumProfileDetailTable {
    public static final String[] COLUMN_NAMES = { "Property", "Value" };

    public static final int COLUMN_LENGTH = 2;

    public static final int PROPERTY = 0;

    public static final int VALUE = 1;
  }

  /** It's an interface containing the constants to define various profile operaitions, for their easier
 * access.
 */
  private interface EnumProfileOperation {
    public static final int VIEW = 1;

    public static final int NEW = 2;

    public static final int EDIT = 3;

    public static final int DELETE = 4;
  }
}


