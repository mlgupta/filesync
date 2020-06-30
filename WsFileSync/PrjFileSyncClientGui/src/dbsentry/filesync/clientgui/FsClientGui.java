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
 * $Id: FsClientGui.java,v 1.206 2006/06/27 14:20:30 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.ClientUtil;
import dbsentry.filesync.client.FsClient;
import dbsentry.filesync.client.enumconstants.EnumClipBoardOperation;
import dbsentry.filesync.clientgui.FolderDocInfoClient;
import dbsentry.filesync.clientgui.preference.FsPreferences;
import dbsentry.filesync.clientgui.Progress;
import dbsentry.filesync.clientgui.enumconstants.EnumLocalTable;
import dbsentry.filesync.clientgui.enumconstants.EnumRemoteTable;
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.clientgui.listener.LocalTableColumnHeaderListener;
import dbsentry.filesync.clientgui.listener.LocalWillTreeExpansionListener;
import dbsentry.filesync.clientgui.listener.RemoteTableColumnHeaderListener;
import dbsentry.filesync.clientgui.listener.RemoteWillTreeExpansionListener;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFileFolderPropertyPageRemote;
import dbsentry.filesync.common.FsFileHolder;
import dbsentry.filesync.common.FsFilePropertyPageRemote;
import dbsentry.filesync.common.FsFolderDocInfoHolder;
import dbsentry.filesync.common.FsFolderHolder;
import dbsentry.filesync.common.FsFolderPropertyPageRemote;
import dbsentry.filesync.common.FsMessage;
import dbsentry.filesync.common.FsObjectHolder;
import dbsentry.filesync.common.FsPermissionHolder;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.FsUser;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsAuthenticationListener;
import dbsentry.filesync.common.listeners.FsDisconnectionListener;
import dbsentry.filesync.common.listeners.FsDownloadListener;
import dbsentry.filesync.common.listeners.FsRemoteCommandListener;
import dbsentry.filesync.common.listeners.FsRemoteCopyListener;
import dbsentry.filesync.common.listeners.FsRemoteMoveListener;
import dbsentry.filesync.common.listeners.FsUploadListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DebugGraphics;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import oracle.help.CSHManager;
import oracle.help.Help;
import oracle.help.library.helpset.HelpSet;
import oracle.help.navigator.Navigator;

import org.apache.log4j.Logger;

import org.jdesktop.jdic.filetypes.Association;
import org.jdesktop.jdic.filetypes.AssociationService;


/**
 *	File sync client main frame.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Saurabh Gupta
 * 	Last Modfied Date:   21-03-2006
 */
public class FsClientGui extends JFrame{
  private Logger logger;  
  
  private Help helpObject;
  
  private CSHManager manager;
  
  private Navigator[] navigator;
  
  private JFileChooser fileChooser =  new JFileChooser();
  
  private FileSystemView fileSystemView = fileChooser.getFileSystemView();
  
  private AssociationService assoService = new AssociationService();
  
  //Menu bar
  private JMenuBar menuBar = new JMenuBar();

  //Menus   
  private JMenu menuFile = new JMenu();
  private JMenu menuView = new JMenu();
  private JMenu menuLocal = new JMenu();
  private JMenu menuRemote = new JMenu();
  private JMenu menuTools = new JMenu();
  private JMenu menuHelp = new JMenu();
  
  //Menu Items for File Menu
  private JMenuItem menuFileConnect = new JMenuItem();
  private JMenuItem menuFileDisconnect = new JMenuItem();
  private JMenuItem menuFilePreferences = new JMenuItem();
  private JMenuItem menuFileClose = new JMenuItem();
  private JMenuItem menuFileExit = new JMenuItem();
  
  //Menu Items for View Menu
  private JCheckBoxMenuItem menuTileVertically = new JCheckBoxMenuItem();
  private JCheckBoxMenuItem menuTileHorizontally = new JCheckBoxMenuItem();
  private JCheckBoxMenuItem menuRemoteTree = new JCheckBoxMenuItem();
  private JCheckBoxMenuItem menuLocalTree = new JCheckBoxMenuItem();
  private JCheckBoxMenuItem menuRemoteBrowser = new JCheckBoxMenuItem();
  private JCheckBoxMenuItem menuLocalBrowser = new JCheckBoxMenuItem();
  
  //Menu Items for Local Menu
  private JMenuItem menuLocalCut = new JMenuItem();
  private JMenuItem menuLocalCopy = new JMenuItem();
  private JMenuItem menuLocalPaste = new JMenuItem();
  private JMenuItem menuLocalNewFolder = new JMenuItem();
  private JMenuItem menuLocalRename = new JMenuItem();
  private JMenuItem menuLocalDelete = new JMenuItem();
  private JMenuItem menuLocalRefresh = new JMenuItem(); 
  private JMenuItem menuLocalProperties = new JMenuItem(); 
  
  //Menu Items for Remote Menu
  private JMenuItem menuRemoteCut = new JMenuItem();
  private JMenuItem menuRemoteCopy = new JMenuItem();
  private JMenuItem menuRemotePaste = new JMenuItem();
  private JMenuItem menuRemoteNewFolder = new JMenuItem();
  private JMenuItem menuRemoteRename = new JMenuItem();
  private JMenuItem menuRemoteDelete = new JMenuItem();
  private JMenuItem menuRemoteRefresh = new JMenuItem();
  private JMenuItem menuRemoteProperties = new JMenuItem();
   
  //Menu Items for Tools Menu
  private JMenuItem menuToolsUpload = new JMenuItem();
  private JMenuItem menuToolsDownload = new JMenuItem();
  private JMenuItem menuToolsSynchronize = new JMenuItem();
  
  //Menu Items for Help Menu
  private JMenuItem menuHelpTableofContents = new JMenuItem();
  private JMenuItem menuHelpFullTextSearch = new JMenuItem();
  private JMenuItem menuHelpIndexSearch = new JMenuItem();
  private JMenuItem menuHelpAbout = new JMenuItem();
  
  //General Toobar Buttons
  private JButton butConnect = new JButton();
  private JButton butDisconnect = new JButton();
  private JButton butSynchronize = new JButton();
  
  //Remove Toobar Buttons
  private JButton butRemoteNavigateBack = new JButton();
  private JButton butRemoteNavigateForward = new JButton();
  private JButton butRemoteNavigateUp = new JButton();
  private JButton butRemoteRefresh = new JButton();
  private JButton butRemoteNewFolder = new JButton();
  private JButton butRemoteRenameFolderFile = new JButton();
  private JButton butRemoteDelete = new JButton();
  private JButton butRemoteProperty = new JButton();
  private JButton butRemoteCut = new JButton();
  private JButton butRemoteCopy = new JButton();
  private JButton butRemotePaste = new JButton();
  private JButton butRemoteDownload = new JButton();
  private JButton butRemoteGo = new JButton();
  
  //Local Toolbar Buttons
  private JButton butLocalNavigateBack = new JButton();
  private JButton butLocalNavigateForward = new JButton();
  private JButton butLocalNavigateUp = new JButton();
  private JButton butLocalRefreshContent = new JButton();
  private JButton butLocalCreateNewFolder = new JButton();
  private JButton butLocalRenameFolderFile = new JButton();
  private JButton butLocalDeleteFolderFile = new JButton();
  private JButton butLocalCut = new JButton();
  private JButton butLocalCopy = new JButton();
  private JButton butLocalPaste = new JButton();
  private JButton butLocalPropertyFolderFile = new JButton();
  private JButton butLocalUpload = new JButton();
  private JButton butLocalGo = new JButton();
  
  //Remote/Local Addressbar[Drop Down]
  private JComboBox comboRemoteAdressBar = new JComboBox();
  private JComboBox comboLocalAddressBar = new JComboBox();
  
  //Scrollpane for Remote/Local Treeview
  private JScrollPane scrpRemoteTreeView = new JScrollPane();
  private JScrollPane scrpLocalTreeView = new JScrollPane();
  
  //Scrollpane for Remote/Local ListView
  private JScrollPane scrpRemoteFolderFileList = new JScrollPane();
  private JScrollPane scrpLocalFolderFileList = new JScrollPane();  
  
  //Remote/Local Tree
  private JTree treeRemoteTreeView = new JTree(new DefaultMutableTreeNode(null));
  private JTree treeLocalTreeView = new JTree(new DefaultMutableTreeNode(null));
  
  //Splitpane for Remote/Local Browers
  private JSplitPane splpRemoteSystem = new JSplitPane();
  private JSplitPane splpLocalSystem = new JSplitPane();
  
  //Splitpane for Complete  Brower
  private JSplitPane splpFileBrowser = new JSplitPane();
  
  //Panel for Remote/Local Toolbar
  private JPanel jpToolBarRemote = new JPanel();
  private JPanel jpToolBarLocal = new JPanel();
  
  //Panel for Remote/Local Addressbar
  private JPanel jpAddressBarRemote = new JPanel();
  private JPanel jpAddressBarLocal = new JPanel();
  
  //Panel for Remote/Local Treeview  
  private JPanel jpRemoteTreeView = new JPanel();
  private JPanel jpLocalTreeView = new JPanel();
  
  //Panel for Remote/Local Listview 
  private JPanel jpRemoteFolderFileList = new JPanel();
  private JPanel jpLocalFolderFileList = new JPanel();
  
  //Panel for Remote/Local Browser
  private  static JPanel jpRemoteSystem = new JPanel();
  public  static JPanel jpLocalSystem = new JPanel();
  
  //Panel for Complete browser 
  private JPanel jpFileBrowser = new JPanel(new BorderLayout());
  
  
  //Panel for Statusbar
  private JPanel jpStatus = new JPanel();
  
  //Label for Remote/Local Address bar
  private JLabel lbRemoteAddressbar = new JLabel();
  private JLabel lbLocalAddressbar = new JLabel();
  
  //BorderLayouts
  private BorderLayout layoutMain = new BorderLayout();
  private BorderLayout borderLayout1 = new BorderLayout();
  private BorderLayout borderLayout2 = new BorderLayout();
  private BorderLayout borderLayout3 = new BorderLayout();
  private BorderLayout borderLayout4 = new BorderLayout();
  private BorderLayout borderLayout5 = new BorderLayout();
  private BorderLayout borderLayout6 = new BorderLayout();
  private BorderLayout borderLayout7 = new BorderLayout();
  
  //FlowLayouts 
  private FlowLayout flowLayout1 = new FlowLayout();
  private FlowLayout flowLayout2 = new FlowLayout();
  private FlowLayout flowLayout3 = new FlowLayout();
  private FlowLayout flowLayout4 = new FlowLayout();
  
  //Table for Remote/Local Listview  
  private JTable tblRemoteFolderFileList = new JTable();
  private JTable tblLocalFolderFileList = new JTable();
  
  //General Toolbar
  private JToolBar toolBar = new JToolBar();
  
  //Toolbar for Remote/Local buttons
  private JToolBar tbRemoteSystem = new JToolBar();
  private JToolBar tbLocalSystem = new JToolBar();
  
  // Text Field for status 
  private JTextField tfStatus = new JTextField();
  
  //Propress bar
  private JProgressBar jProgressBar1 = new JProgressBar();
  
  //Button border
  private Border normalButtonBorder;
  
  //Cursor
  private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  private Cursor waitCursor    = new Cursor(Cursor.WAIT_CURSOR);
  
  //stack
  //private static Stack itemToUpload;
  private Stack clipBoardLocal = new Stack();
  
  private String itemPaths[];
  private String clipBoardRemote[];
  
  private File file;
  
  private Color selectionColor;
  
  private int clipBoardOperationLocal;
  private int currClipBoardOperationRemote;
  private int dividerLocationRemote = 250;
  private int dividerLocationLocal = 250;
  
  //public static int cursorCounterRemote = 0;
  private static int cursorCounterLocal = 0;
  
  //Declaration of Custom classes
  private UserLogin userLogin;
  
  private LocalWillTreeExpansionListener localWillTreeExpansionListener;
  private RemoteWillTreeExpansionListener remoteWillTreeExpansionListener;
  
  private ComboRemoteAdressBar_ActionListener actionListener4RemoteAddressBar= new ComboRemoteAdressBar_ActionListener();
  private ComboLocalAddressBar_ActionListener actionListener4LocalAddressBar = new ComboLocalAddressBar_ActionListener();
  
  private LocalTableColumnHeaderListener tableHeaderMouseListener=null;
  private RemoteTableColumnHeaderListener remoteTableHeaderMouseListener=null;
  
  private FsFileSystemOperationLocal fsFileSystemOperationLocal;
  
  private FolderDocInfoClient folderDocInfoClient;
  private FsFolderDocInfoHolder fsFolderDocInfoHolder;

  private FsTableModel fsTableModelRemote;
  private FsTableModel fsTableModelLocal;
  
  private FsFileSync fsFileSync = null;
  
  
  public static FsUser fsUser;
  
  public static FsPreferences fsPreferences;
  
//  public static FsFileSystemOperationsRemote fsFileSystemOperationsRemote;
  
  private FsClient fsClient;
  
 // private int remoteOperationConstant=FsRemoteOperationConstants.COMMAND;
  
  
  //public HandleJxtaConfiguration handleJxtaConfiguration;
  
  /**
   * Construct FsClientGui object.
   */
  public FsClientGui(FsClient fsClient) {
    logger = Logger.getLogger("ClientLogger");
    try {
      this.fsClient = fsClient;
     // handleJxtaConfiguration = new HandleJxtaConfiguration();
     // handleJxtaConfiguration.setGuiFrame(this); 
      
      preJbInitOperation();
      jbInit();
      postJbInitOperation();
      this.setIconImage(FsImage.imageTitle);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * System inserted function which handles all the operations related to GUI components.
   * @throws java.lang.Exception
   */
  private void jbInit() throws Exception {
    
    //Menu  Details

    //File Menu and its child menus
    menuFile.setText("File");
    menuFile.setMnemonic(KeyEvent.VK_F);
    menuFile.setIconTextGap(1);
    menuFile.setVerticalAlignment(SwingConstants.TOP);
    
    //File -> Connect
    menuFileConnect.setText("Connect");
    menuFileConnect.setIconTextGap(1);
    menuFileConnect.setVerticalTextPosition(SwingConstants.BOTTOM);
    menuFileConnect.setVerticalAlignment(SwingConstants.BOTTOM);
    menuFileConnect.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuFileConnect_actionPerformed(e);
        }
      });
    
    // File -> Disconnect
    menuFileDisconnect.setText("Disconnect");
    menuFileDisconnect.setIconTextGap(1);
    menuFileDisconnect.setVerticalTextPosition(SwingConstants.BOTTOM);
    menuFileDisconnect.setVerticalAlignment(SwingConstants.BOTTOM);
    menuFileDisconnect.setEnabled(false);
    menuFileDisconnect.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuFileDisconnect_actionPerformed(e);
        }
      });
    
    //File  - > Preference
    menuFilePreferences.setText("Preferences");
    menuFilePreferences.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           menuFilePreferences_actionPerformed(e);
         }
       });
    
    //File -> Close
    menuFileClose.setText("Close");
    menuFileClose.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuFileClose_actionPerformed(e);
        }
      });
    
    //File  -> Exit
    menuFileExit.setText("Exit");
    menuFileExit.setIconTextGap(1);
    menuFileExit.setVerticalTextPosition(SwingConstants.BOTTOM);
    menuFileExit.setVerticalAlignment(SwingConstants.BOTTOM);
    menuFileExit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuFileExit_actionPerformed(e);
        }
      });

    //Preparing File Menu
    menuFile.add(menuFileConnect);
    menuFile.add(menuFileDisconnect);
    menuFile.addSeparator();
    menuFile.add(menuFilePreferences);
    menuFile.addSeparator();
    menuFile.add(menuFileClose);
    menuFile.add(menuFileExit);
    
    //View Menu and its chid menus
    menuView.setText("View");
    menuView.setMnemonic(KeyEvent.VK_V);
    menuView.setIconTextGap(1);
    menuView.setContentAreaFilled(false);
    
    //View -> Tile Horizontally
    menuTileHorizontally.setText("Tile Horizontally");
    menuTileHorizontally.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           menuTileHorizontally_actionPerformed(e);
         }
       });
    
    //View -> Tile Vertically
    menuTileVertically.setText("Tile Vertically");
    menuTileVertically.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuTileVertically_actionPerformed(e);
        }
      });
      
    //View  -> Remote Browser
    menuRemoteBrowser.setText("Remote Browser");
    menuRemoteBrowser.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuRemoteBrowser_actionPerformed(e);
        }
      });
      
    //View -> Local Brower
    menuLocalBrowser.setText("Local Browser");
    menuLocalBrowser.setIconTextGap(1);
    menuLocalBrowser.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuLocalBrowser_actionPerformed(e);
        }
      });
     
    //View -> Remote Tree
    menuRemoteTree.setText("Remote Tree");
    menuView.setVerticalAlignment(SwingConstants.TOP);
    menuRemoteTree.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuRemoteTree_actionPerformed(e);
        }
      });
    
    //View -> Local Tree  
    menuLocalTree.setText("Local Tree");
    menuLocalTree.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuLocalTree_actionPerformed(e);
        }
      });

    //Preparing View Menu    
    menuView.add(menuTileHorizontally);
    menuView.add(menuTileVertically);
    menuView.addSeparator();
    menuView.add(menuRemoteBrowser);
    menuView.add(menuLocalBrowser);
    menuView.addSeparator();
    menuView.add(menuRemoteTree);
    menuView.add(menuLocalTree);
    
    //Local Menu and its child menus
    menuLocal.setText("Local");
    menuLocal.setMnemonic(KeyEvent.VK_L);
    menuLocal.setIconTextGap(1);
    menuLocal.setVerticalAlignment(SwingConstants.TOP);
    
    //Local - > Cut
    menuLocalCut.setText("Cut");
    menuLocalCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK, false));
    menuLocalCut.setIconTextGap(1);
    menuLocalCut.setPreferredSize(new Dimension(81, 19));
    menuLocalCut.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuLocalCut_actionPerformed(e);
        }
      });
    
    //Local - > Copy
    menuLocalCopy.setText("Copy");
    menuLocalCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK, false));
    menuLocalCopy.setIconTextGap(1);
    menuLocalCopy.setPreferredSize(new Dimension(91, 19));
    menuLocalCopy.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuLocalCopy_actionPerformed(e);
        }
      });
    
    //Local - > Paste
    menuLocalPaste.setText("Paste");
    menuLocalPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK, false));
    menuLocalPaste.setIconTextGap(1);
    menuLocalPaste.setPreferredSize(new Dimension(129, 19));
    menuLocalPaste.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuLocalPaste_actionPerformed(e);
        }
      });
      
    //Local -> New Folder
    menuLocalNewFolder.setText("New Folder ");
    menuLocalNewFolder.setPreferredSize(new Dimension(99, 19));
    menuLocalNewFolder.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
       menuLocalNewFolder_actionPerformed(e);
      }
    });
    
    // Local -> Rename
    menuLocalRename.setText("Rename");
    menuLocalRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
    menuLocalRename.setPreferredSize(new Dimension(77, 19));
    menuLocalRename.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        menuLocalRename_actionPerformed(e);
      }
    });
       
    //Local - > delete
    menuLocalDelete.setText("Delete");
    menuLocalDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false));
    menuLocalDelete.setIconTextGap(1);
    menuLocalDelete.setPreferredSize(new Dimension(101, 19));
    menuLocalDelete.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuEditDelete_actionPerformed(e);
        }
      });
      
    //Local  -> Refresh
    menuLocalRefresh.setText("Refresh");
    menuLocalRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false));
    menuLocalRefresh.setPreferredSize(new Dimension(75, 19));
    menuLocalRefresh.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuLocalRefresh_actionPerformed(e);
        }
      });
    
    //Local -> Properties
    menuLocalProperties.setText("Properties");
    menuLocalProperties.setIconTextGap(1);
    menuLocalProperties.setPreferredSize(new Dimension(91, 19));
    menuLocalProperties.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuLocalProperties_actionPerformed(e);
        }
      });

    //Preparing Local Menu
    menuLocal.add(menuLocalCut);
    menuLocal.add(menuLocalCopy);
    menuLocal.add(menuLocalPaste);
    menuLocal.addSeparator();
    menuLocal.add(menuLocalNewFolder);
    menuLocal.add(menuLocalRename);
    menuLocal.add(menuLocalDelete);
    menuLocal.addSeparator();
    menuLocal.add(menuLocalRefresh);
    menuLocal.addSeparator();
    menuLocal.add(menuLocalProperties);
    
    //Remote Menu and its child menus
    menuRemote.setText("Remote");
    menuRemote.setMnemonic(KeyEvent.VK_R);
    menuRemote.setVerticalAlignment(SwingConstants.TOP);
    
    //Remote -> Cut
    menuRemoteCut.setText("Cut");
    menuRemoteCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK, false));
    menuRemoteCut.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuRemoteCut_actionPerformed(e);
        }
      });
      
    //Remote  -> Copy
    menuRemoteCopy.setText("Copy");
    menuRemoteCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK, false));
    menuRemoteCopy.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuRemoteCopy_actionPerformed(e);
        }
      });
      
    //Remote -> Paste
    menuRemotePaste.setText("Paste");
    menuRemotePaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK, false));
    menuRemotePaste.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuRemotePaste_actionPerformed(e);
        }
      });
    
    //Remote  -> New Folder
    menuRemoteNewFolder.setText("New Folder");
    menuRemoteNewFolder.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           menuRemoteNewFolder_actionPerformed(e);
         }
       });
    
    //Remote -> Rename
    menuRemoteRename.setText("Rename");
    menuRemoteRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
    menuRemoteRename.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuRemoteRename_actionPerformed(e);
        }
      });
    
    //Remote -> Delete
    menuRemoteDelete.setText("Delete");
    menuRemoteDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false));
    menuRemoteDelete.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuRemoteDelete_actionPerformed(e);
        }
      });
    
    //Remote  -> Refresh
    menuRemoteRefresh.setText("Refresh");
    menuRemoteRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false));
    menuRemoteRefresh.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuRemoteRefresh_actionPerformed(e);
        }
      });
    
    //Remote  -> Properties
    menuRemoteProperties.setText("Properties");
    menuRemoteProperties.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           menuRemoteProperties_actionPerformed(e);
         }
       });
    
    //Preparing Remote Menu
    menuRemote.add(menuRemoteCut);
    menuRemote.add(menuRemoteCopy);
    menuRemote.add(menuRemotePaste);
    menuRemote.addSeparator();
    menuRemote.add(menuRemoteNewFolder);
    menuRemote.add(menuRemoteRename);
    menuRemote.add(menuRemoteDelete);
    menuRemote.addSeparator();
    menuRemote.add(menuRemoteRefresh);
    menuRemote.addSeparator();
    menuRemote.add(menuRemoteProperties);
    
    //Tools Menu and its child menus
    menuTools.setText("Tools");
    menuTools.setMnemonic(KeyEvent.VK_T);
    menuTools.setIconTextGap(1);
    menuTools.setVerticalAlignment(SwingConstants.TOP);
    
    //Tools -> Upload
    menuToolsUpload.setText("Upload");
    menuToolsUpload.setPreferredSize(new Dimension(129, 19));
    menuToolsUpload.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuToolsUpload_actionPerformed(e);
        }
      });
    
    //Tools -> Download
    menuToolsDownload.setText("Download");
    menuToolsDownload.setPreferredSize(new Dimension(129, 19));
    menuToolsDownload.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuToolsDownload_actionPerformed(e);
        }
      });
    
    //Tools -> Synchronize
    menuToolsSynchronize.setText("Synchronize");
    menuToolsSynchronize.setPreferredSize(new Dimension(129, 19));
    menuToolsSynchronize.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuToolsSynchronize_actionPerformed(e);
        }
      });
      
    //Preparing Tools Menu
    menuTools.add(menuToolsUpload);
    menuTools.add(menuToolsDownload);
    menuTools.add(menuToolsSynchronize);
    
    //Help and its child menus
    menuHelp.setText("Help");
    menuHelp.setMnemonic(KeyEvent.VK_H);
    menuHelp.setIconTextGap(1);
    menuHelp.setVerticalAlignment(SwingConstants.TOP);
        

        //Help -> Table  Of Contents
    menuHelpTableofContents.setText("Table of Contents ");
    menuHelpTableofContents.addActionListener(new ActionListener()      {
        public void actionPerformed(ActionEvent e)        {
          menuHelpTableofContents_actionPerformed(e);
        }
      });
      
    //Help -> Full Text Search
    menuHelpFullTextSearch.setText("Full Text Search");
    menuHelpFullTextSearch.addActionListener(new ActionListener()      {
        public void actionPerformed(ActionEvent e)        {
          menuHelpFullTextSearch_actionPerformed(e);
        }
      });
      
    //Help  -> Index Search
    menuHelpIndexSearch.setText("Index Search");
    menuHelpIndexSearch.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuHelpIndexSearch_actionPerformed(e);
        }
      });      
    
    //Help -> About
    menuHelpAbout.setText("About DBS FileSync.");
    menuHelpAbout.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          helpAbout_ActionPerformed(ae);
        }
      });
    
    //Preparing Help Menu
    menuHelp.add(menuHelpTableofContents);
    menuHelp.add(menuHelpIndexSearch);
    menuHelp.add(menuHelpFullTextSearch);
    menuHelp.add(menuHelpAbout);
    
    //Menubar
    menuBar.setBounds(new Rectangle(0, 0, 1018, 20));
    
    //Preparing Menu Bar
    menuBar.add(menuFile);
    menuBar.add(menuView);
    menuBar.add(menuLocal);
    menuBar.add(menuRemote);
    menuBar.add(menuTools);
    menuBar.add(menuHelp);
    
    //General Tool bar settings
    
    //Connect Button
    butConnect.setText("Connect");
    butConnect.setToolTipText("Connect");
    butConnect.setIcon(FsImage.imageConnect);
    butConnect.setOpaque(false);
    butConnect.setMargin(new Insets(0, 0, 0, 0));
    butConnect.setPreferredSize(new Dimension(100, 28));
    butConnect.setMinimumSize(new Dimension(91, 28));
    butConnect.setMaximumSize(new Dimension(105, 25));
    butConnect.setSize(new Dimension(100, 25));
    butConnect.setEnabled(false);
    butConnect.addMouseListener(new java.awt.event.MouseAdapter() {
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
    butConnect.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
    butConnect_actionPerformed();
    }
    });
    
    //Disconnect Button
    butDisconnect.setText("Disconnect");
    butDisconnect.setToolTipText("Disconnect");
    butDisconnect.setIcon(FsImage.imageDisconnect);
    butDisconnect.setOpaque(false);
    butDisconnect.setBounds(new Rectangle(107, 3, 107, 25));
    butDisconnect.setMargin(new Insets(0, 0, 0, 0));
    butDisconnect.setPreferredSize(new Dimension(120, 28));
    butDisconnect.setMinimumSize(new Dimension(91, 28));
    butDisconnect.setMaximumSize(new Dimension(115, 25));
    butDisconnect.setSize(new Dimension(100, 25));
    butDisconnect.setEnabled(false);
    butDisconnect.addMouseListener(new java.awt.event.MouseAdapter() {
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
    butDisconnect.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butDisconnect_actionPerformed(e);
        }
      });
    
    //Sychronize Button
    butSynchronize.setText("Synchronize");
    butSynchronize.setToolTipText("Synchronize");
    butSynchronize.setIcon(FsImage.imageFileSync);
    butSynchronize.setOpaque(false);
    butSynchronize.setMargin(new Insets(0, 0, 0, 0));
    butSynchronize.setPreferredSize(new Dimension(130, 28));
    butSynchronize.setMinimumSize(new Dimension(120, 28));
    butSynchronize.setMaximumSize(new Dimension(115, 25));
    butSynchronize.setSize(new Dimension(100, 25));
    butSynchronize.setEnabled(false);
    butSynchronize.addMouseListener(new MouseAdapter() {
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
    butSynchronize.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butSynchronize_actionPerformed(e);
        }
      });
    
    //Toolbar
    toolBar.setPreferredSize(new Dimension(273, 30));
    toolBar.setMinimumSize(new Dimension(283, 30));
    toolBar.setMaximumSize(new Dimension(310, 30));
    toolBar.setSize(new Dimension(1018, 30));
    toolBar.setFloatable(false);
    
    //Preparing Toolbar
    toolBar.add(butConnect);
    toolBar.add(butDisconnect, null);
    toolBar.add(butSynchronize, null);
    
    //Remote File Browser Setting 
    
    //Remote Navigate Back 
    butRemoteNavigateBack.setToolTipText("Back");
    butRemoteNavigateBack.setIcon(FsImage.imageNavigateBack);
    butRemoteNavigateBack.setOpaque(false);
    butRemoteNavigateBack.setMargin(new Insets(0, 0, 0, 0));
    butRemoteNavigateBack.setPreferredSize(new Dimension(30, 30));
    butRemoteNavigateBack.setMinimumSize(new Dimension(30, 30));
    butRemoteNavigateBack.setMaximumSize(new Dimension(30, 25));
    butRemoteNavigateBack.setSize(new Dimension(30, 30));
    butRemoteNavigateBack.setEnabled(false);
    butRemoteNavigateBack.addMouseListener(new MouseAdapter() {
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
    butRemoteNavigateBack.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteNavigateBack_actionPerformed(e);
        }
      });
    
    
    //Remote Navigate Forward
    butRemoteNavigateForward.setToolTipText("Forward");
    butRemoteNavigateForward.setIcon(FsImage.imageNavigateForward);
    butRemoteNavigateForward.setOpaque(false);
    butRemoteNavigateForward.setMargin(new Insets(0, 0, 0, 0));
    butRemoteNavigateForward.setPreferredSize(new Dimension(30, 30));
    butRemoteNavigateForward.setMinimumSize(new Dimension(30, 30));
    butRemoteNavigateForward.setMaximumSize(new Dimension(30, 25));
    butRemoteNavigateForward.setSize(new Dimension(30, 30));
    butRemoteNavigateForward.setEnabled(false);
    butRemoteNavigateForward.addMouseListener(new MouseAdapter() {
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
    butRemoteNavigateForward.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteNavigateForward_actionPerformed(e);
        }
      });
    
    //Remote Navigate Up
    butRemoteNavigateUp.setToolTipText("Go Up");
    butRemoteNavigateUp.setIcon(FsImage.imageNavigateUp);
    butRemoteNavigateUp.setOpaque(false);
    butRemoteNavigateUp.setPreferredSize(new Dimension(30, 30));
    butRemoteNavigateUp.setMinimumSize(new Dimension(30, 30));
    butRemoteNavigateUp.setMaximumSize(new Dimension(30, 25));
    butRemoteNavigateUp.addMouseListener(new MouseAdapter() {
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
    butRemoteNavigateUp.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteNavigateUp_actionPerformed(e);
        }
      });

    //Remote Refresh
    butRemoteRefresh.setToolTipText("Refresh (F5)");
    butRemoteRefresh.setIcon(FsImage.imageRefresh);
    butRemoteRefresh.setOpaque(false);
    butRemoteRefresh.setPreferredSize(new Dimension(30, 30));
    butRemoteRefresh.setMinimumSize(new Dimension(30, 30));
    butRemoteRefresh.setMaximumSize(new Dimension(30, 25));
    butRemoteRefresh.addMouseListener(new MouseAdapter() {
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
    butRemoteRefresh.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteRefresh_actionPerformed(e);
        }
      });
    
    //Remote New Folder
    butRemoteNewFolder.setToolTipText("Create New Folder");
    butRemoteNewFolder.setIcon(FsImage.imageNewFolder);
    butRemoteNewFolder.setOpaque(false);
    butRemoteNewFolder.setPreferredSize(new Dimension(30, 30));
    butRemoteNewFolder.setMinimumSize(new Dimension(30, 30));
    butRemoteNewFolder.setMaximumSize(new Dimension(30, 25));
    butRemoteNewFolder.addMouseListener(new MouseAdapter() {
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
    butRemoteNewFolder.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteNewFolder_actionPerformed(e);
        }
      });
    
    //Remote Rename
    butRemoteRenameFolderFile.setToolTipText("Rename (F5)");
    butRemoteRenameFolderFile.setIcon(FsImage.imageRename);
    butRemoteRenameFolderFile.setOpaque(false);
    butRemoteRenameFolderFile.setPreferredSize(new Dimension(30, 30));
    butRemoteRenameFolderFile.setMinimumSize(new Dimension(30, 30));
    butRemoteRenameFolderFile.setMaximumSize(new Dimension(30, 25));
    butRemoteRenameFolderFile.addMouseListener(new MouseAdapter() {
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
    butRemoteRenameFolderFile.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteRenameFolderFile_actionPerformed(e);
        }
      });

    //Remote Delete
    butRemoteDelete.setToolTipText("Delete(Delete)");
    butRemoteDelete.setIcon(FsImage.imageDelete);
    butRemoteDelete.setOpaque(false);
    butRemoteDelete.setPreferredSize(new Dimension(30, 30));
    butRemoteDelete.setMinimumSize(new Dimension(30, 30));
    butRemoteDelete.setMaximumSize(new Dimension(30, 25));
    butRemoteDelete.addMouseListener(new MouseAdapter() {
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
    butRemoteDelete.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteDelete_actionPerformed(e);
        }
      });
    
    //Remote Property 
    butRemoteProperty.setToolTipText("Property");
    butRemoteProperty.setIcon(FsImage.imageProperty);
    butRemoteProperty.setOpaque(false);
    butRemoteProperty.setPreferredSize(new Dimension(30, 30));
    butRemoteProperty.setMinimumSize(new Dimension(30, 30));
    butRemoteProperty.setMaximumSize(new Dimension(30, 25));
    butRemoteProperty.addMouseListener(new MouseAdapter() {
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
    butRemoteProperty.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteProperty_actionPerformed(e);
        }
      });
    
    //Remote Cut
    butRemoteCut.setToolTipText("Cut (Ctrl-X)");
    butRemoteCut.setIcon(FsImage.imageCut);
    butRemoteCut.setOpaque(false);
    butRemoteCut.setPreferredSize(new Dimension(30, 30));
    butRemoteCut.setMinimumSize(new Dimension(30, 30));
    butRemoteCut.setMaximumSize(new Dimension(30, 25));
    butRemoteCut.addMouseListener(new MouseAdapter() {
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
    butRemoteCut.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteCut_actionPerformed(e);
        }
      });
    
    //Remote Copy
    butRemoteCopy.setToolTipText("Copy (Ctrl-C)");
    butRemoteCopy.setIcon(FsImage.imageCopy);
    butRemoteCopy.setOpaque(false);
    butRemoteCopy.setPreferredSize(new Dimension(30, 30));
    butRemoteCopy.setMinimumSize(new Dimension(30, 30));
    butRemoteCopy.setMaximumSize(new Dimension(30, 25));
    butRemoteCopy.addMouseListener(new MouseAdapter() {
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
    butRemoteCopy.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteCopy_actionPerformed(e);
        }
      });
    
    //Remote Paste
    butRemotePaste.setToolTipText("Paste (Ctrl-V)");
    butRemotePaste.setIcon(FsImage.imagePaste);
    butRemotePaste.setOpaque(false);
    butRemotePaste.setPreferredSize(new Dimension(30, 30));
    butRemotePaste.setMinimumSize(new Dimension(30, 30));
    butRemotePaste.setMaximumSize(new Dimension(30, 25));
    butRemotePaste.addMouseListener(new MouseAdapter() {
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
    butRemotePaste.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemotePaste_actionPerformed(e);
        }
      });
    
    //Download
    butRemoteDownload.setToolTipText("Download ");
    butRemoteDownload.setIcon(FsImage.imageDownload);
    butRemoteDownload.setOpaque(false);
    butRemoteDownload.setPreferredSize(new Dimension(30, 30));
    butRemoteDownload.setMinimumSize(new Dimension(30, 30));
    butRemoteDownload.setMaximumSize(new Dimension(30, 25));
    butRemoteDownload.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteDownload_actionPerformed(e);
        }
      });
    butRemoteDownload.addMouseListener(new MouseAdapter() {
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
    
    //Remote Toolbar
    tbRemoteSystem.setFloatable(false);
    tbRemoteSystem.setBounds(new Rectangle(0, 0, 362, 34));
    tbRemoteSystem.setPreferredSize(new Dimension(362, 34));
    
    //Preparing Remote Toolbar
    tbRemoteSystem.add(butRemoteNavigateBack, null);
    tbRemoteSystem.add(butRemoteNavigateForward, null);
    tbRemoteSystem.add(butRemoteNavigateUp, null);
    tbRemoteSystem.add(butRemoteRefresh, null);
    tbRemoteSystem.add(butRemoteNewFolder, null);
    tbRemoteSystem.add(butRemoteRenameFolderFile, null);
    tbRemoteSystem.add(butRemoteDelete, null);
    tbRemoteSystem.add(butRemoteProperty, null);
    tbRemoteSystem.add(butRemoteCut, null);
    tbRemoteSystem.add(butRemoteCopy, null);
    tbRemoteSystem.add(butRemotePaste, null);
    tbRemoteSystem.add(butRemoteDownload, null);
    
    //Remote Addressbar
    
    //Label for Remote Address bar   
    lbRemoteAddressbar.setText("Address : ");
    lbRemoteAddressbar.setPreferredSize(new Dimension(62, 20));
    lbRemoteAddressbar.setMinimumSize(new Dimension(90, 14));
    lbRemoteAddressbar.setMaximumSize(new Dimension(90, 14));
    lbRemoteAddressbar.setHorizontalAlignment(SwingConstants.RIGHT);
    
    //Dropdown for Remote Address bar 
    comboRemoteAdressBar.setOpaque(false);
    comboRemoteAdressBar.setPreferredSize(new Dimension(420, 20));
    comboRemoteAdressBar.setMinimumSize(new Dimension(242, 20));
    comboRemoteAdressBar.setMaximumSize(new Dimension(610, 20));
    comboRemoteAdressBar.setSize(new Dimension(600, 28));
    comboRemoteAdressBar.setEditable(true);
    comboRemoteAdressBar.setMaximumRowCount(5);
    comboRemoteAdressBar.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        comboRemoteAdressBar_keyReleased(e);
      }
    });
    comboRemoteAdressBar.addActionListener(actionListener4RemoteAddressBar); 
    
    //Remote Button Go 
    butRemoteGo.setToolTipText("Go");
    butRemoteGo.setIcon(FsImage.imageGo);
    butRemoteGo.setOpaque(false);
    butRemoteGo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    butRemoteGo.setPreferredSize(new Dimension(30, 30));
    butRemoteGo.setMinimumSize(new Dimension(30, 30));
    butRemoteGo.setMaximumSize(new Dimension(30, 30));
    butRemoteGo.setSize(new Dimension(30, 30));
    butRemoteGo.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
          button_mouseEnteredNoFX(e);
        }
        public void mouseExited(MouseEvent e) {
          button_mouseExitedNoFX(e);
        }
      });
    butRemoteGo.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butRemoteGo_actionPerformed(e);
        }
      });
    
    //Panel for Remote Address Bar
    flowLayout4.setAlignment(0);
    flowLayout4.setHgap(0);
    flowLayout4.setVgap(0);
    jpAddressBarRemote.setLayout(flowLayout4);
    jpAddressBarRemote.add(lbRemoteAddressbar, null);
    jpAddressBarRemote.add(comboRemoteAdressBar, null);
    jpAddressBarRemote.add(butRemoteGo, null);
    
    //Panel  for Remote Toolbar
    flowLayout3.setVgap(0);
    flowLayout3.setHgap(0);
    flowLayout3.setAlignment(0);
    jpToolBarRemote.setLayout(flowLayout3);

    //Preparing Remote Tool bar
    jpToolBarRemote.add(tbRemoteSystem, null);
    jpToolBarRemote.add(jpAddressBarRemote, null);
    
    //Remote Treeview
    treeRemoteTreeView.setToolTipText("null");
    treeRemoteTreeView.setAutoscrolls(true);
    treeRemoteTreeView.setRootVisible(false);
    treeRemoteTreeView.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
    treeRemoteTreeView.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          treeRemoteTreeView_valueChanged(e);
        }
      });
    treeRemoteTreeView.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          treeRemoteTreeView_focusLost(e);
        }

        public void focusGained(FocusEvent e) {
          treeRemoteTreeView_focusGained(e);
        }
      });
      
    //Scroll Pane  for Remote Treeview 
    scrpRemoteTreeView.getViewport().add(treeRemoteTreeView, null);
    
    //Panel for Remote Treeview
    jpRemoteTreeView.setLayout(borderLayout1);
    jpRemoteTreeView.setPreferredSize(new Dimension(249, 279));
    jpRemoteTreeView.add(scrpRemoteTreeView, BorderLayout.CENTER);
    
    //Remote Listview  
    tblRemoteFolderFileList.setShowHorizontalLines(false);
    tblRemoteFolderFileList.setShowVerticalLines(false);
    tblRemoteFolderFileList.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          tblRemoteFolderFileList_keyReleased(e);
        }
      });
    tblRemoteFolderFileList.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusGained(FocusEvent e) {
          tblRemoteFolderFileList_focusGained(e);
        }

        public void focusLost(FocusEvent e) {
          tblRemoteFolderFileList_focusLost(e);
        }
      });
    tblRemoteFolderFileList.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          tblRemoteFolderFileList_mouseClicked(e);
        }
      });
    
    //Scrollpane for Remote Listview  
    scrpRemoteFolderFileList.getViewport().add(tblRemoteFolderFileList, null);

    //Panel for Remote Listview
    jpRemoteFolderFileList.setLayout(borderLayout2);
    jpRemoteFolderFileList.setBounds(new Rectangle(252, 1, 761, 272));
    jpRemoteFolderFileList.add(scrpRemoteFolderFileList, BorderLayout.CENTER); 
    
    //Split Panel for Remote File Browser
    splpRemoteSystem.setDividerLocation(250);
    splpRemoteSystem.setDividerSize(3);
    splpRemoteSystem.setBounds(new Rectangle(0, 30, 1016, 302));
    splpRemoteSystem.add(jpRemoteTreeView, JSplitPane.TOP); 
    splpRemoteSystem.add(jpRemoteFolderFileList, JSplitPane.BOTTOM);

    //Panel for Remote File Browser 
    jpRemoteSystem.setLayout(borderLayout5);
    jpRemoteSystem.setBounds(new Rectangle(1, 1, 1016, 332));
    jpRemoteSystem.setPreferredSize(new Dimension(506, 311));
    jpRemoteSystem.setMinimumSize(new Dimension(506, 62));
    jpRemoteSystem.add(jpToolBarRemote, BorderLayout.NORTH);    
    jpRemoteSystem.add(splpRemoteSystem, BorderLayout.CENTER);


    //Local File Browser Setting
    
    //Local Navigate Back
    butLocalNavigateBack.setToolTipText("Back");
    butLocalNavigateBack.setIcon(FsImage.imageNavigateBack);
    butLocalNavigateBack.setOpaque(false);
    butLocalNavigateBack.setPreferredSize(new Dimension(30, 30));
    butLocalNavigateBack.setMinimumSize(new Dimension(30, 30));
    butLocalNavigateBack.setMaximumSize(new Dimension(30, 25));
    butLocalNavigateBack.addMouseListener(new MouseAdapter() {
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
    butLocalNavigateBack.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
       butLocalNavigateBack_actionPerformed(e);
     }
    });
     
    //Local Navigate Forward 
    butLocalNavigateForward.setToolTipText("Forward");
    butLocalNavigateForward.setIcon(FsImage.imageNavigateForward);
    butLocalNavigateForward.setOpaque(false);
    butLocalNavigateForward.setPreferredSize(new Dimension(30, 30));
    butLocalNavigateForward.setMinimumSize(new Dimension(30, 30));
    butLocalNavigateForward.setMaximumSize(new Dimension(30, 25));
    butLocalNavigateForward.addMouseListener(new MouseAdapter() {
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
    butLocalNavigateForward.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
     butLocalNavigateForward_actionPerformed(e);
    }
    });
    
    //Local Navigate Up
    butLocalNavigateUp.setToolTipText("Go Up");
    butLocalNavigateUp.setIcon(FsImage.imageNavigateUp);
    butLocalNavigateUp.setOpaque(false);
    butLocalNavigateUp.setPreferredSize(new Dimension(30, 30));
    butLocalNavigateUp.setMinimumSize(new Dimension(30, 30));
    butLocalNavigateUp.setMaximumSize(new Dimension(30, 25));
    butLocalNavigateUp.addMouseListener(new MouseAdapter() {
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
    butLocalNavigateUp.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         btnLocalNavigateUp_actionPerformed(e);
       }
     });
    
    //Local Refresh 
    butLocalRefreshContent.setToolTipText("Refresh (F5)");
    butLocalRefreshContent.setIcon(FsImage.imageRefresh);
    butLocalRefreshContent.setOpaque(false);
    butLocalRefreshContent.setPreferredSize(new Dimension(30, 30));
    butLocalRefreshContent.setMinimumSize(new Dimension(30, 30));
    butLocalRefreshContent.setMaximumSize(new Dimension(30, 25));
    butLocalRefreshContent.addMouseListener(new MouseAdapter() {
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
    butLocalRefreshContent.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butLocalRefreshContent_actionPerformed(e);
        }
      });
     
    //Local  New Folder 
    butLocalCreateNewFolder.setToolTipText("Create New Folder");
    butLocalCreateNewFolder.setIcon(FsImage.imageNewFolder);
    butLocalCreateNewFolder.setOpaque(false);
    butLocalCreateNewFolder.setPreferredSize(new Dimension(30, 30));
    butLocalCreateNewFolder.setMinimumSize(new Dimension(30, 30));
    butLocalCreateNewFolder.setMaximumSize(new Dimension(30, 25));
    butLocalCreateNewFolder.addMouseListener(new MouseAdapter() {
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
    butLocalCreateNewFolder.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         butLocalCreateNewFolder_actionPerformed(e);
       }
     });
    
    //Local Rename
    butLocalRenameFolderFile.setToolTipText("Rename (F2)");
    butLocalRenameFolderFile.setIcon(FsImage.imageRename);
    butLocalRenameFolderFile.setOpaque(false);
    butLocalRenameFolderFile.setPreferredSize(new Dimension(30, 30));
    butLocalRenameFolderFile.setMinimumSize(new Dimension(30, 30));
    butLocalRenameFolderFile.setMaximumSize(new Dimension(30, 25));
    butLocalRenameFolderFile.addMouseListener(new MouseAdapter() {
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
    butLocalRenameFolderFile.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
    butLocalRenameFolderFile_actionPerformed(e);
    }
    });
    
    //Local Delete 
    butLocalDeleteFolderFile.setToolTipText("Delete (Delete)");
    butLocalDeleteFolderFile.setIcon(FsImage.imageDelete);
    butLocalDeleteFolderFile.setOpaque(false);
    butLocalDeleteFolderFile.setPreferredSize(new Dimension(30, 30));
    butLocalDeleteFolderFile.setMinimumSize(new Dimension(30, 30));
    butLocalDeleteFolderFile.setMaximumSize(new Dimension(30, 25));
    butLocalDeleteFolderFile.addMouseListener(new MouseAdapter() {
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
    butLocalDeleteFolderFile.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
         butLocalDeleteFolderFile_actionPerformed(e);
       }
     });

    //Local Property
    butLocalPropertyFolderFile.setToolTipText("Property");
    butLocalPropertyFolderFile.setIcon(FsImage.imageProperty);
    butLocalPropertyFolderFile.setOpaque(false);
    butLocalPropertyFolderFile.setPreferredSize(new Dimension(30, 30));
    butLocalPropertyFolderFile.setMinimumSize(new Dimension(30, 30));
    butLocalPropertyFolderFile.setMaximumSize(new Dimension(30, 25));
    butLocalPropertyFolderFile.addMouseListener(new MouseAdapter() {
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
    butLocalPropertyFolderFile.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butLocalPropertyFolderFile_actionPerformed(e);
        }
      });

    //Local Cut
    butLocalCut.setToolTipText("Cut (Ctrl-X)");
    butLocalCut.setIcon(FsImage.imageCut);
    butLocalCut.setOpaque(false);
    butLocalCut.setPreferredSize(new Dimension(30, 30));
    butLocalCut.setMaximumSize(new Dimension(30, 25));
    butLocalCut.setMinimumSize(new Dimension(30, 30));
    butLocalCut.addMouseListener(new MouseAdapter() {
        public void mouseExited(MouseEvent e) {
          button_mouseExited(e);
        }
        public void mouseEntered(MouseEvent e) {
          button_mouseEntered(e);
        }
        public void mousePressed(MouseEvent e) {
          button_mousePressed(e);
        }
        public void mouseReleased(MouseEvent e) {
          button_mouseReleased(e);
        }
      });
    butLocalCut.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butLocalCut_actionPerformed(e);
        }
      });
      
    //Local Copy
    butLocalCopy.setToolTipText("Copy (Ctrl-C)");
    butLocalCopy.setIcon(FsImage.imageCopy);
    butLocalCopy.setOpaque(false);
    butLocalCopy.setPreferredSize(new Dimension(30, 30));
    butLocalCopy.setMinimumSize(new Dimension(30, 30));
    butLocalCopy.setMaximumSize(new Dimension(30, 25));
    butLocalCopy.addMouseListener(new MouseAdapter() {
        public void mouseExited(MouseEvent e) {
          button_mouseExited(e);
        }
        public void mouseEntered(MouseEvent e) {
          button_mouseEntered(e);
        }
        public void mousePressed(MouseEvent e) {
          button_mousePressed(e);
        }
        public void mouseReleased(MouseEvent e) {
          button_mouseReleased(e);
        }
      });
    butLocalCopy.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butLocalCopy_actionPerformed(e);
        }
      });
    
    //Local Paste    
    butLocalPaste.setToolTipText("Paste (Ctrl-V)");
    butLocalPaste.setIcon(FsImage.imagePaste);
    butLocalPaste.setOpaque(false);
    butLocalPaste.setPreferredSize(new Dimension(30, 30));
    butLocalPaste.setMinimumSize(new Dimension(30, 30));
    butLocalPaste.setMaximumSize(new Dimension(30, 25));
    butLocalPaste.addMouseListener(new MouseAdapter() {
        public void mouseExited(MouseEvent e) {
          button_mouseExited(e);
        }

        public void mouseEntered(MouseEvent e) {
          button_mouseEntered(e);
        }

        public void mousePressed(MouseEvent e) {
          button_mousePressed(e);
        }

        public void mouseReleased(MouseEvent e) {
          button_mouseReleased(e);
        }
      });
    butLocalPaste.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          butLocalPaste_actionPerformed(e);
        }
      });
    
    //Upload
    butLocalUpload.setToolTipText("Upload");
    butLocalUpload.setIcon(FsImage.imageUpload);
    butLocalUpload.setOpaque(false);
    butLocalUpload.setPreferredSize(new Dimension(30, 30));
    butLocalUpload.setMinimumSize(new Dimension(30, 30));
    butLocalUpload.setMaximumSize(new Dimension(30, 25));
    butLocalUpload.addMouseListener(new MouseAdapter() {
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
    butLocalUpload.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
    butLocalUpload_actionPerformed(e);
    }
    });
    
    
    //Local Toolbar
    tbLocalSystem.setFloatable(false);
    tbLocalSystem.setPreferredSize(new Dimension(362, 34));
    tbLocalSystem.setMinimumSize(new Dimension(362, 30));
    tbLocalSystem.setMaximumSize(new Dimension(362, 30));
    tbLocalSystem.setSize(new Dimension(362, 30));

    //Preparing  Local Toolbar
    tbLocalSystem.add(butLocalNavigateBack, null);
    tbLocalSystem.add(butLocalNavigateForward, null);
    tbLocalSystem.add(butLocalNavigateUp, null);
    tbLocalSystem.add(butLocalRefreshContent, null);
    tbLocalSystem.add(butLocalCreateNewFolder, null);
    tbLocalSystem.add(butLocalRenameFolderFile, null);
    tbLocalSystem.add(butLocalDeleteFolderFile, null);
    tbLocalSystem.add(butLocalPropertyFolderFile, null);
    tbLocalSystem.add(butLocalCut, null);
    tbLocalSystem.add(butLocalCopy, null);
    tbLocalSystem.add(butLocalPaste, null);
    tbLocalSystem.add(butLocalUpload, null);

    //Local Addressbar
    
    //Label for Local Address bar   
    lbLocalAddressbar.setText("Address : ");
    lbLocalAddressbar.setHorizontalAlignment(SwingConstants.RIGHT);
    lbLocalAddressbar.setMaximumSize(new Dimension(90, 14));
    lbLocalAddressbar.setMinimumSize(new Dimension(90, 14));
    lbLocalAddressbar.setPreferredSize(new Dimension(62, 20));
    
    //Dropdown for Local Address bar 
    
    comboLocalAddressBar.setPreferredSize(new Dimension(420, 20));
    comboLocalAddressBar.setMinimumSize(new Dimension(242, 20));
    comboLocalAddressBar.setMaximumSize(new Dimension(610, 20));
    comboLocalAddressBar.setSize(new Dimension(700, 20));
    comboLocalAddressBar.setEditable(true);
    comboLocalAddressBar.setMaximumRowCount(5);
    comboLocalAddressBar.getEditor().getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          comboLocalAddressBar_keyReleased(e);
        }
      });
    comboLocalAddressBar.addActionListener(actionListener4LocalAddressBar);
    
    //Local Button Go
    butLocalGo.setToolTipText("Go");
    butLocalGo.setIcon(FsImage.imageGo);
    butLocalGo.setOpaque(false);
    butLocalGo.setPreferredSize(new Dimension(30, 30));
    butLocalGo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    butLocalGo.setMaximumSize(new Dimension(30, 30));
    butLocalGo.setMinimumSize(new Dimension(30, 30));
    butLocalGo.setSize(new Dimension(30, 30));
    butLocalGo.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e) {
        butLocalGo_actionPerformed(e);
       }  
     });
    butLocalGo.addMouseListener(new MouseAdapter() {
       public void mouseEnteredNoFX(MouseEvent e) {
         button_mouseEntered(e);
       }
       public void mouseExitedNoFX(MouseEvent e) {
         button_mouseExited(e);
       }
      }); 
      
    //Panel for Local Address Bar
    flowLayout2.setVgap(0);
    flowLayout2.setHgap(0);
    flowLayout2.setAlignment(0);
    jpAddressBarLocal.setLayout(flowLayout2);
    jpAddressBarLocal.setPreferredSize(new Dimension(512, 30));
    jpAddressBarLocal.setMinimumSize(new Dimension(362, 30));
    jpAddressBarLocal.setMaximumSize(new Dimension(362, 30));
    jpAddressBarLocal.setSize(new Dimension(5122, 30));
    jpAddressBarLocal.add(lbLocalAddressbar, null);
    jpAddressBarLocal.add(comboLocalAddressBar, null);
    jpAddressBarLocal.add(butLocalGo, null);
    
    //Panel  for Local Toolbar
    flowLayout1.setVgap(0);
    flowLayout1.setHgap(0);
    flowLayout1.setAlignment(0);
    
    jpToolBarLocal.setLayout(flowLayout1);
    jpToolBarLocal.setBounds(new Rectangle(0, 0, 1012, 40));
    jpToolBarLocal.setPreferredSize(new Dimension(1039, 34));
    jpToolBarLocal.setMinimumSize(new Dimension(506, 28));
    jpToolBarLocal.setSize(new Dimension(888, 34));
    jpToolBarLocal.add(tbLocalSystem, null);
    jpToolBarLocal.add(jpAddressBarLocal, null);
    
    //Local Treeview
    treeLocalTreeView.setAutoscrolls(true);
    treeLocalTreeView.addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusLost(FocusEvent e) {
           treeLocalTreeView_focusLost(e);
         }
         public void focusGained(FocusEvent e) {
           treeLocalTreeView_focusGained(e);
         }
       });
    treeLocalTreeView.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          treeLocalTreeView_valueChanged(e);
        }
      });
    treeLocalTreeView.setRootVisible(false);
    
    //Scroll Pane for Local Treeview
    scrpLocalTreeView.getViewport().add(treeLocalTreeView, null);
     
    //Panel for Local Treeeview
    jpLocalTreeView.setLayout(borderLayout3);
    jpLocalTreeView.setToolTipText("null");
    jpLocalTreeView.setPreferredSize(new Dimension(249, 283));
    jpLocalTreeView.add(scrpLocalTreeView, BorderLayout.CENTER);
     
    //Local Listview  
    tblLocalFolderFileList.setShowVerticalLines(false);
    tblLocalFolderFileList.setShowHorizontalLines(false);
    tblLocalFolderFileList.addKeyListener(new java.awt.event.KeyAdapter() {

         public void keyReleased(KeyEvent e) {
           tblLocalFolderFileList_keyReleased(e);
         }
       });
    tblLocalFolderFileList.addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusLost(FocusEvent e) {
           tblLocalFolderFileList_focusLost(e);
         }

         public void focusGained(FocusEvent e) {
           tblLocalFolderFileList_focusGained(e);
         }
       });
    tblLocalFolderFileList.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          tblLocalFolderFileList_mouseClicked(e);
        }
      });
    
    //Scrollpane for Local Listview  
    scrpLocalFolderFileList.getViewport().add(tblLocalFolderFileList, null);
    
    //Panel for Local Listview
    jpLocalFolderFileList.setLayout(borderLayout4);
    jpLocalFolderFileList.add(scrpLocalFolderFileList, BorderLayout.CENTER);
    
    //Split Panel for Local File Browser
    splpLocalSystem.setBackground(Color.white);
    splpLocalSystem.setDividerLocation(250);
    splpLocalSystem.setDividerSize(3);
    splpLocalSystem.add(jpLocalTreeView, JSplitPane.LEFT);
    splpLocalSystem.add(jpLocalFolderFileList, JSplitPane.RIGHT);
     
    //Panel for Local File Browser
    jpLocalSystem.setLayout(borderLayout6);
    jpLocalSystem.setBounds(new Rectangle(1, 310, 1016, 332));
    jpLocalSystem.setPreferredSize(new Dimension(506, 319));
    jpLocalSystem.setMinimumSize(new Dimension(506, 58));
    jpLocalSystem.add(jpToolBarLocal, BorderLayout.NORTH);
    jpLocalSystem.add(splpLocalSystem, BorderLayout.CENTER);
    
    //Split Panel For Total File Browser
    splpFileBrowser.setDividerLocation(265);
    splpFileBrowser.setDividerSize(3);
    splpFileBrowser.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splpFileBrowser.add(jpRemoteSystem, JSplitPane.LEFT);
    splpFileBrowser.add(jpLocalSystem, JSplitPane.RIGHT);
    
    //Panel for Total File Browser
    jpFileBrowser.setBackground(Color.white);
    jpFileBrowser.setBounds(new Rectangle(0, 30, 890, 530));
    jpFileBrowser.add(splpFileBrowser,BorderLayout.CENTER);
    
    // Status bar
    tfStatus.setEditable(false);
    tfStatus.setPreferredSize(new Dimension(868, 22));
    
    //Panel for status and Progress bar
    jpStatus.setLayout(borderLayout7);
    jpStatus.add(tfStatus, BorderLayout.CENTER);
    jpStatus.add(jProgressBar1, BorderLayout.EAST);
    
    
    this.setTitle("DBSentry File Synchronizer");
    this.setSize(new Dimension(924, 735));
    this.setBounds(new Rectangle(10, 10, 900, 635));
    this.setState(Frame.NORMAL);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setJMenuBar(menuBar);
    this.getContentPane().setLayout(layoutMain);
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
    this.getContentPane().add(jpFileBrowser, BorderLayout.CENTER);
    this.getContentPane().add(jpStatus, BorderLayout.SOUTH);
    this.addWindowListener(new java.awt.event.WindowAdapter() {
        public void windowOpened(WindowEvent e) {
          this_windowOpened(e);
        }
      });
      
  }
     
  /**
   * Initially customizes the table which displays the local file and folder list.
   * Hides the columns containing non displayable information.
   */
  private void initializeLocalFolderFileList(){
    Vector dataRow = new Vector(1);
    
    fsTableModelLocal = new FsTableModel(EnumLocalTable.COLUMN_NAMES,dataRow);
    tblLocalFolderFileList.setModel(fsTableModelLocal);
    tblLocalFolderFileList.setAutoCreateColumnsFromModel(false);
    tblLocalFolderFileList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    selectionColor = tblLocalFolderFileList.getSelectionBackground();
    //set default cell renderer
    FsTableCellRenderer fsTableCellRenderer = new FsTableCellRenderer();
    tblLocalFolderFileList.setDefaultRenderer(Object.class,fsTableCellRenderer);

    
    //add click event listener for table header
    JTableHeader tableHeader = tblLocalFolderFileList.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(),18));
    tableHeaderMouseListener=new LocalTableColumnHeaderListener();
    tableHeader.addMouseListener(tableHeaderMouseListener);
    
    FsTableHeaderCellRenderer iconHeaderRenderer=null;// = new FsTableHeaderCellRenderer();
    tblLocalFolderFileList.getTableHeader().getColumnModel().getColumn(EnumLocalTable
                                                                           .NAME).setHeaderRenderer(iconHeaderRenderer);

    DefaultTableColumnModel colModel = (DefaultTableColumnModel)tblLocalFolderFileList.getColumnModel();
    colModel.getColumn(EnumLocalTable.NAME).setPreferredWidth(200);
    colModel.getColumn(EnumLocalTable.SIZE).setPreferredWidth(100);
    colModel.getColumn(EnumLocalTable.TYPE).setPreferredWidth(150);
    colModel.getColumn(EnumLocalTable.MODIFIED).setPreferredWidth(175);
    colModel.getColumn(EnumLocalTable.DESCRIPTION).setPreferredWidth(150);
    colModel.getColumn(EnumLocalTable.PERMISSION).setPreferredWidth(150);

    //hide table coulmn contiaining non displayable info
    if(tblLocalFolderFileList.getColumnCount() > EnumLocalTable
            .COLUMN_DISPLAY_LENGTH){
      for(int index = EnumLocalTable.COLUMN_DISPLAY_LENGTH; index < EnumLocalTable
                 .COLUMN_LENGTH; index++){
        tblLocalFolderFileList.removeColumn(tableHeader.getColumnModel().getColumn(EnumLocalTable
                                                                     .COLUMN_DISPLAY_LENGTH));
      }
    } 
  }


  /**
   * Initially customizes the table which displays the remote file and folder list.
   * Hides the columns containing non displayable information.
   */
  private void initializeRemoteFolderFileList(){
    int itemLength = 1;
    Vector dataRow = new Vector((int)itemLength);

    fsTableModelRemote = new FsTableModel(EnumRemoteTable.COLUMN_NAMES,dataRow);
    tblRemoteFolderFileList.setModel(fsTableModelRemote);
    tblRemoteFolderFileList.setAutoCreateColumnsFromModel(false);
    tblRemoteFolderFileList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    //set default cell renderer
    FsTableCellRenderer fsTableCellRenderer = new FsTableCellRenderer();
    tblRemoteFolderFileList.setDefaultRenderer(JLabel.class,fsTableCellRenderer);

    //add click event listener for table header
    JTableHeader tableHeader = tblRemoteFolderFileList.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(),18));
    remoteTableHeaderMouseListener=new RemoteTableColumnHeaderListener();
    tableHeader.addMouseListener(remoteTableHeaderMouseListener);

    FsTableHeaderCellRenderer iconHeaderRenderer=null;// = new FsTableHeaderCellRenderer();
    tblRemoteFolderFileList.getTableHeader().getColumnModel().getColumn(EnumRemoteTable
                                                                            .NAME).setHeaderRenderer(iconHeaderRenderer);
    
    DefaultTableColumnModel colModel = (DefaultTableColumnModel)tblRemoteFolderFileList.getColumnModel();
    colModel.getColumn(EnumRemoteTable.NAME).setPreferredWidth(200);
    colModel.getColumn(EnumRemoteTable.SIZE).setPreferredWidth(100);
    colModel.getColumn(EnumRemoteTable.TYPE).setPreferredWidth(150);
    colModel.getColumn(EnumRemoteTable.MODIFIED).setPreferredWidth(175);
    colModel.getColumn(EnumRemoteTable.DESCRIPTION).setPreferredWidth(150);
    colModel.getColumn(EnumRemoteTable.PERMISSION).setPreferredWidth(150);

/*
    //hide table coulmn contiaining non displayable info
    logger.debug(" tblRemoteFolderFileList.getColumnCount() : " + tblRemoteFolderFileList.getColumnCount());
    for(int index = 6; index < tblRemoteFolderFileList.getColumnCount(); index++){
      tblRemoteFolderFileList.removeColumn(tableHeader.getColumnModel().getColumn(index));
    }
*/
    //hide table coulmn contiaining non displayable info
    if(tblRemoteFolderFileList.getColumnCount() > EnumRemoteTable
            .COLUMN_DISPLAY_LENGTH){
      for(int index = EnumRemoteTable.COLUMN_DISPLAY_LENGTH; index < EnumRemoteTable
                 .COLUMN_LENGTH; index++){
        tblRemoteFolderFileList.removeColumn(tableHeader.getColumnModel().getColumn(EnumRemoteTable
                                                                     .COLUMN_DISPLAY_LENGTH));
      }
    } 
    
    dbsentry.filesync.clientgui.utility.GeneralUtil.sortAllRowsBy(tblRemoteFolderFileList,EnumRemoteTable
                                                                      .NAME,true);
  }

  
  
  /**
   * Queries for the useid and password of cmsdk database and 
   * sends a connect request to server containing id and password through 
   * a function call. 
   * @param e ActionEvent object
   */
  private void butConnect_actionPerformed() {
    try{
      //FsAuthenticationListener authenticationListener;
      logger.info("Connecting to the server");
      tfStatus.setText("Connecting to the server");
      if(userLogin == null || !userLogin.isVisible()){
        //userLogin = new UserLogin(this,"User Login",false,new AuthenticationListener(this));
        /*if(fsPreferences.isRememberUIdPwd()){
          File keyFile = new File("config/enc_dec_key.txt");
          if(keyFile.exists()){
            FileInputStream fis = new FileInputStream(keyFile);
           // CryptographicUtil cryptographicUtil = new CryptographicUtil(logger);
            logger.debug("fsPreferences.getUserId() : " + fsPreferences.getUserId());
          //  String uId = (cryptographicUtil.decryptData(fis,fsPreferences.getUserId()));
           // String password = (cryptographicUtil.decryptData(fis,fsPreferences.getPassword()));
           // userLogin.getTxtUserId().setText(uId);
           // userLogin.getTxtUserPassword().setText(password);
            userLogin.getCbRememberUIdPwd().setSelected(true);
          }
        }*/
        userLogin.setVisible(true);
      }
    }catch(Exception ex){
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
    }
  }
 
  /**
   * fills the remote folderlist with specified items and sorts the list by name.
   * @param objects
   */
  public void fillRemoteFolderFileList(Object[] objects){
    logger.debug("In the function fillRemoteFolderFileList");
    Vector dataRow = new Vector();
    if(!splpRemoteSystem.getBottomComponent().equals(jpRemoteFolderFileList)){
      splpRemoteSystem.remove(splpRemoteSystem.getRightComponent());
      splpRemoteSystem.add(jpRemoteFolderFileList,JSplitPane.BOTTOM);
    }
    if(objects == null){
      fsTableModelRemote = (FsTableModel) tblRemoteFolderFileList.getModel();
      fsTableModelRemote.setDataVector(dataRow);
      return;
    }
    
    int itemLength = objects.length;
    tfStatus.setText(itemLength + " Object(s)");
    dataRow = new Vector((int)itemLength);
    Vector dataCol;
    
    FsObjectHolder fsObjectHolder;
    FsFolderHolder fsFolderHolder;
    FsFileHolder fsFileHolder;
    
    logger.debug("Preparing data vector");
    for(int index = 0; index < itemLength; index++){
      dataCol = new Vector(EnumRemoteTable.COLUMN_LENGTH);
      fsObjectHolder = (FsObjectHolder)objects[index];
      if(fsObjectHolder instanceof FsFolderHolder){
        fsFolderHolder = (FsFolderHolder)fsObjectHolder;
        file = new File(fsFolderHolder.getName());
        dataCol.add(EnumRemoteTable.NAME,new JLabel(fsFolderHolder.getName(),FsImage.imgFolderClosed,JLabel.LEFT));
        dataCol.add(EnumRemoteTable.SIZE,new JLabel("",JLabel.RIGHT));
        dataCol.add(EnumRemoteTable.TYPE,new JLabel("Folder" + "  "));
        dataCol.add(EnumRemoteTable.MODIFIED,new JLabel(dbsentry.filesync.clientgui.utility.GeneralUtil.getDateForDisplay(fsFolderHolder.getModifiedDate()) + "  " ));
        if(fsFolderHolder.getDescription() == null){
          dataCol.add(EnumRemoteTable.DESCRIPTION,new JLabel("  " ) );
        }else{
          dataCol.add(EnumRemoteTable.DESCRIPTION,new JLabel(fsFolderHolder.getDescription() + "  " ) );
        }
        dataCol.add(EnumRemoteTable.PERMISSION,new JLabel(""));
        dataCol.add(EnumRemoteTable.ITEM_TYPE,new JLabel("Folder"));
        dataCol.add(EnumRemoteTable.ABS_PATH, new JLabel(fsFolderHolder.getPath()));
      }else{
        fsFileHolder = (FsFileHolder)fsObjectHolder;
        String fileName = fsFileHolder.getName();
        
        dataCol.add(EnumRemoteTable.NAME, new JLabel(fsFileHolder.getName(),this.getFileIcon(fileName),JLabel.LEFT));
        dataCol.add(EnumRemoteTable.SIZE, new JLabel("  " + dbsentry.filesync.clientgui.utility.GeneralUtil.getDocSizeForDisplay(fsFileHolder.getSize()) + "  ",JLabel.RIGHT));
        dataCol.add(EnumRemoteTable.TYPE, new JLabel(fsFileHolder.getMimeType() + "  "));
        dataCol.add(EnumRemoteTable.MODIFIED, new JLabel(dbsentry.filesync.clientgui.utility.GeneralUtil.getDateForDisplay(fsFileHolder.getModifiedDate()) + "  "));
        if(fsFileHolder.getDescription() == null){
          dataCol.add(EnumRemoteTable.DESCRIPTION, new JLabel("  "));
        }else{
          dataCol.add(EnumRemoteTable.DESCRIPTION, new JLabel(fsFileHolder.getDescription() + "  "));
        }
        dataCol.add(EnumRemoteTable.PERMISSION, new JLabel(""));
        dataCol.add(EnumRemoteTable.ITEM_TYPE, new JLabel("File"));
        dataCol.add(EnumRemoteTable.ABS_PATH, new JLabel(fsFileHolder.getPath()));
      }
      
      dataRow.add(dataCol);
    }
    
    logger.debug("Setting data vector in the table model");
    fsTableModelRemote = (FsTableModel) tblRemoteFolderFileList.getModel();
    fsTableModelRemote.setDataVector(dataRow);
    dbsentry.filesync.clientgui.utility.GeneralUtil.sortAllRowsBy(tblRemoteFolderFileList,EnumRemoteTable
                                                                      .NAME,remoteTableHeaderMouseListener.isSortOrderNameAsc());
  }

  /**
   * returns the tfStatus property.
   * @return JTextField object
   */
  public JTextField getTfStatus() {
    return tfStatus;
  }
 
  /**
   * fills the local folderlist with the contents of specified folder and 
   * sorts the list by name.
   * @param currentFolder File object representring current folder
   */
  private void fillLocalFolderFileList(File currentFolder){
    try{
      logger.debug("In function fillLocalFolderFileList"); 
      Vector dataRow = new Vector(1);
      long currentFolderItemsCount = 0;
      File currentFolderItems[] = currentFolder.listFiles();
      if(currentFolderItems != null){
        currentFolderItemsCount = currentFolderItems.length;
        tfStatus.setText(currentFolderItemsCount + " Object(s)");
  
        dataRow = new Vector((int)currentFolderItemsCount);
        Vector dataCol;
        Association association = null;
        for(int index=0; index < currentFolderItemsCount; index++){
          dataCol = new Vector(EnumLocalTable.COLUMN_LENGTH);
          for(int counter=0; counter < EnumLocalTable
                         .COLUMN_LENGTH; counter++){
            dataCol.add(counter, "");
          }
        
          dataCol.set(EnumLocalTable.TYPE, new JLabel(fileChooser.getTypeDescription(currentFolderItems[index]) + "  "));
          if(currentFolderItems[index].isDirectory()){
            dataCol.set(EnumLocalTable.NAME, new JLabel(currentFolderItems[index].getName(),FsImage.imgFolderClosed,JLabel.LEFT));
            dataCol.set(EnumLocalTable.SIZE, new JLabel("",JLabel.RIGHT));
          }else{
            dataCol.set(EnumLocalTable.NAME, new JLabel(currentFolderItems[index].getName(),fileSystemView.getSystemIcon(currentFolderItems[index]),JLabel.LEFT));
            dataCol.set(EnumLocalTable.SIZE, new JLabel("  " + dbsentry.filesync.clientgui.utility.GeneralUtil.getDocSizeForDisplay(currentFolderItems[index].length()) + "  ",JLabel.RIGHT));
            association = assoService.getAssociationByContent(currentFolderItems[index].toURL());
            if(association != null){
              String typeDesc = association.getDescription();
              if(typeDesc != null){
                dataCol.set(EnumLocalTable.TYPE, new JLabel(typeDesc + "  "));
              }
            }
          }
          
          dataCol.set(EnumLocalTable.MODIFIED, new JLabel(dbsentry.filesync.clientgui.utility.GeneralUtil.getDateForDisplay(new Date(currentFolderItems[index].lastModified())) + "  "));
          dataCol.set(EnumLocalTable.DESCRIPTION, new JLabel(fileSystemView.getSystemDisplayName(currentFolderItems[index]) + "  "));
          if(currentFolder.canWrite()){
            dataCol.set(EnumLocalTable.PERMISSION, new JLabel("read, write"));  
          }else if(currentFolder.canRead()){
            dataCol.set(EnumLocalTable.PERMISSION, new JLabel("read-only"));  
          }
    
          if(currentFolderItems[index].isDirectory()){
            dataCol.set(EnumLocalTable.ITEM_TYPE,new JLabel("Folder"));
          }else{
            dataCol.set(EnumLocalTable.ITEM_TYPE, new JLabel("File"));
          }
          dataCol.set(EnumLocalTable.ABS_PATH, new JLabel(currentFolderItems[index].getAbsolutePath()));
          dataRow.add(dataCol);
        }
      }else{
        tfStatus.setText(currentFolderItemsCount + " Object(s)");
      }
      fsTableModelLocal = (FsTableModel)tblLocalFolderFileList.getModel();
      fsTableModelLocal.setDataVector(dataRow);
      
      dbsentry.filesync.clientgui.utility.GeneralUtil.sortAllRowsBy(tblLocalFolderFileList,EnumLocalTable
                                                                          .NAME,tableHeaderMouseListener.isSortOrderNameAsc());    
    }catch(Exception ex){
      logger.error(GeneralUtil.getStackTrace(ex));
    }
  }

  /**
   * To move back to the previous folder in local browser and sets the current folder to 
   * previous folder.
   * @param e
   */
  private void butLocalNavigateBack_actionPerformed(ActionEvent e) {
    String currentFolderPath = folderDocInfoClient.getPrevFolderPath();
    fillLocalFolderFileList(new File(currentFolderPath));
    if(!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)){
      splpLocalSystem.remove(splpLocalSystem.getRightComponent());
      splpLocalSystem.setRightComponent(jpLocalFolderFileList);
    }
    
    addItemToLocalComboBox(currentFolderPath);
    folderDocInfoClient.setCurrentFolderPath(currentFolderPath);
    butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
    butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
    butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());
    
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
    DefaultMutableTreeNode nodeToFill= GeneralUtil.findTreeNode(root,currentFolderPath);
    FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
    String nodePathToFill = fsFolderHolder.getPath();
    logger.debug("Current Folder Path : " + currentFolderPath);
    logger.debug("Path of Node To be Filled: " + nodePathToFill);
    
    TreeNode treeNodes[] = nodeToFill.getPath();
    TreePath treePathToSelect = new TreePath(treeNodes);
    treeLocalTreeView.setSelectionPath(treePathToSelect); 
    treeLocalTreeView.requestFocus();
    logger.debug("treePathToSelect " + treePathToSelect);
  }


  /**
   * To navigate to parent folder in local browser, will work only 
   * on folderlist not on tree structure
   * @param e
   *
   */
  private void btnLocalNavigateUp_actionPerformed(ActionEvent e) {
    boolean isRootFile = false;
    String currentFolderPath = folderDocInfoClient.getCurrentFolderPath();
    File[] roots = File.listRoots();
    int rootLength = roots.length;
    file = new File(currentFolderPath);    
    if(file.exists()){
      for(int index = 0; index < rootLength; index++){
        if(file.getAbsolutePath().equals(roots[index].getAbsolutePath())){
          isRootFile = true;
          break;
        }
      }
      if(!isRootFile){
        file = file.getParentFile();
      }
    }else{
      file = getParentFile(currentFolderPath);
    }
    
    addItemToLocalComboBox(file.toString());
    fillLocalFolderFileList(file);
    if(!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)){
      splpLocalSystem.remove(splpLocalSystem.getRightComponent());;
      splpLocalSystem.setRightComponent(jpLocalFolderFileList);
    }
    folderDocInfoClient.setCurrentFolderPath(file.getAbsolutePath());
    folderDocInfoClient.addFolderPath(file.getAbsolutePath());
    butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
    butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
    butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());
    
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
    DefaultMutableTreeNode nodeToFill= GeneralUtil.findTreeNode(root,file.getAbsolutePath());
    FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
    String nodePathToFill = fsFolderHolder.getPath();
    logger.debug("Current Folder Path : " + file.getAbsolutePath());
    logger.debug("Path of Node To be Filled: " + nodePathToFill);
    
    TreeNode treeNodes[] = nodeToFill.getPath();
    TreePath treePathToSelect = new TreePath(treeNodes);
    treeLocalTreeView.setSelectionPath(treePathToSelect); 
    treeLocalTreeView.requestFocus();
    logger.debug("treePathToSelect " + treePathToSelect);

  }
  
  /**
   * returns the parent of a folder with the specified folderpath.
   * @param currentFolderPath current folder path
   * @return File parent file
   */
  private File getParentFile(String currentFolderPath){
    String pathTemp;
    pathTemp = currentFolderPath.substring(0,currentFolderPath.lastIndexOf(File.separator));
    file = new File(pathTemp);    
    if(file.exists()){
      return file;
    }else{
      return getParentFile(pathTemp);
    }
  }

  /**
   * Handles the mouse-click of table showing the local folder and file list.
   * @param e MouseEvent object
   */
  private void tblLocalFolderFileList_mouseClicked(MouseEvent e) {
    JTable table = (JTable)e.getSource();
    FsTableModel fsTableModel = (FsTableModel)table.getModel();
        
    if(e.getClickCount() == 2){
      // The index of the column whose header was clicked
      int vRowIndex = table.getSelectedRow();
      if(((JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable
                                                 .ITEM_TYPE)).getText().equals("Folder")){
        JLabel label = (JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable
                                                               .ABS_PATH);
        String absFolderPath = label.getText();
        
        addItemToLocalComboBox(absFolderPath);
        File fileAbsFolderPath = new File(label.getText());
        fillLocalFolderFileList(fileAbsFolderPath);
        logger.debug("fileAbsFolderPath.getAbsolutePath() : " + fileAbsFolderPath.getAbsolutePath());
        
        /*folderDocInfoClient.setCurrentFolderPath(fileAbsFolderPath.getAbsolutePath());
        folderDocInfoClient.addFolderPath(fileAbsFolderPath.getAbsolutePath());
        butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
        subMenuViewBackInLocalBrowser.setEnabled(folderDocInfoClient.isBackButtonEnabled());
        butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
        subMenuViewForwardInLocalBrowser.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
        butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());
        subMenuViewUpInLocalBrowser.setEnabled(folderDocInfoClient.isParentButtonEnabled());*/
        
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
        DefaultMutableTreeNode nodeToFill= dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root,absFolderPath);
        logger.debug("Path of Node To be Filled: " + ((FsFolderHolder)nodeToFill.getUserObject()).getPath());
        logger.debug("Absolute Folder Path : " + absFolderPath );
                
        FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
        String nodePathToFill = fsFolderHolder.getPath();
        DefaultMutableTreeNode subTreeRoot = new DefaultMutableTreeNode();
        subTreeRoot.setUserObject(null);
        DefaultMutableTreeNode nodeToHighlight = new DefaultMutableTreeNode();
        logger.debug("Path of Node To be Filled:" + nodePathToFill);
        File filePathToFill = new File(nodePathToFill);
        File[] children =  filePathToFill.listFiles();
        DefaultMutableTreeNode childNode,subChildNode ;
                   
        if(fileAbsFolderPath.equals(filePathToFill)){
         nodeToHighlight = nodeToFill; 
         TreeNode treeNodes[] = nodeToHighlight.getPath();
         TreePath treePathToHighlight= new TreePath(treeNodes);
         treeLocalTreeView.setSelectionPath(treePathToHighlight);                                  
         treeLocalTreeView.requestFocus();          
        }else if(fileAbsFolderPath.getParentFile() != null){
          File subChildFiles[];
          int subChildFilesLength;
          if(fileAbsFolderPath.getParentFile().getAbsolutePath().equals(nodePathToFill)){
          int index = 0;
          nodeToFill.removeAllChildren();
          for(int i=0 ; i < children.length ; i++){
            if(children[i].isDirectory()){
              childNode = new DefaultMutableTreeNode() ;
              nodeToFill.add(childNode);
              fsFolderHolder = new FsFolderHolder();
              fsFolderHolder.setName(children[i].getName());
              fsFolderHolder.setPath(children[i].getAbsolutePath());
              childNode.setUserObject(fsFolderHolder);
              subChildFiles = children[i].listFiles();
              if(subChildFiles != null){
              subChildFilesLength = subChildFiles.length;
              logger.debug("subChildFilesLength : " + subChildFilesLength);
                for(int counter = 0; counter < subChildFilesLength; counter++){
                    if(subChildFiles[counter].isDirectory()){
                    subChildNode = new DefaultMutableTreeNode("");
                    fsFolderHolder = new FsFolderHolder();
                    fsFolderHolder.setName("");
                    fsFolderHolder.setPath("");
                    subChildNode.setUserObject(fsFolderHolder);
                    childNode.add(subChildNode);
                    break;
                  }
                }
              }    
              if(fileAbsFolderPath.equals(children[i])){
                 index = nodeToFill.getIndex((TreeNode)childNode);
                 logger.debug("treeNodeToSelect : " + nodeToFill.getChildAt(index));
                 logger.debug(" Index : " + index);
              }
            } 
          }
          TreeNode treeNodes[] = nodeToFill.getPath();
          TreePath treePathToExpand = new TreePath(treeNodes);
          logger.debug("Path To Expand : " + treePathToExpand);
          treeLocalTreeView.expandPath(treePathToExpand);
          DefaultMutableTreeNode treeNodeToSelect =(DefaultMutableTreeNode)nodeToFill.getChildAt(index);
          treeNodes = treeNodeToSelect.getPath();
          logger.debug("treeNodeToSelect : " + nodeToFill.getChildAt(index));
          TreePath treePathToSelect = new TreePath(treeNodes);
          treeLocalTreeView.setSelectionPath(treePathToSelect); 
          treeLocalTreeView.requestFocus();
          logger.debug("treePathToSelect " + treePathToSelect);
          }else if(!fileAbsFolderPath.equals(filePathToFill)){
            localWillTreeExpansionListener.constructSubTree(fileAbsFolderPath,filePathToFill,subTreeRoot,nodeToHighlight);
            logger.debug("Filling the Node");
            nodeToFill.removeAllChildren();                
            for(int i=0 ; i < children.length ; i++){
              if(((FsFolderHolder)subTreeRoot.getUserObject()).getName().equals(children[i].getName())){
                nodeToFill.add(subTreeRoot);
                logger.debug(" Adding Node : " + subTreeRoot);
              }else{
                childNode = new DefaultMutableTreeNode() ;
                nodeToFill.add(childNode);
                subChildFiles = children[i].listFiles();
                if(subChildFiles != null){
                subChildFilesLength = subChildFiles.length;
                logger.debug("subChildFilesLength : " + subChildFilesLength);
                  for(int counter = 0; counter < subChildFilesLength; counter++){
                      if(subChildFiles[counter].isDirectory()){
                      subChildNode = new DefaultMutableTreeNode("");
                      fsFolderHolder = new FsFolderHolder();
                      fsFolderHolder.setName("");
                      fsFolderHolder.setPath("");
                      subChildNode.setUserObject(fsFolderHolder);
                      childNode.add(subChildNode);
                      break;
                    }
                  }
                }             
                fsFolderHolder = new FsFolderHolder();
                fsFolderHolder.setName(children[i].getName());
                fsFolderHolder.setPath(children[i].getAbsolutePath());
                childNode.setUserObject(fsFolderHolder);
                logger.debug(" Adding Node : " + childNode);
              }  
            }
            TreeNode treeNodes[] = ((DefaultMutableTreeNode)nodeToHighlight.getParent()).getPath();
            logger.debug( " Node To Highlight Parent : " + (DefaultMutableTreeNode)nodeToHighlight.getParent());  
            TreePath treePathToExpand = new TreePath(treeNodes);
            logger.debug("Path To Expand : " + treePathToExpand);
            treeLocalTreeView.expandPath(treePathToExpand);
            treeNodes = nodeToHighlight.getPath();
            TreePath treePathToSelect = new TreePath(treeNodes);
            treeLocalTreeView.setSelectionPath(treePathToSelect); 
            treeLocalTreeView.requestFocus();
          } 
        }  
      }else{
//        JOptionPane.showMessageDialog(this,"Do you want open this file ");
      }
    }else{
           
      int selectedRows[] = table.getSelectedRows();
      int selectedRowsCount = selectedRows.length;
      
      logger.debug(" selectedRowsCount " + selectedRowsCount);
      if(selectedRowsCount > 1){
        tfStatus.setText(selectedRowsCount + " Object(s) selected");
      }else{
        int vRowIndex = table.getSelectedRow();
        
        if(((JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable
                                                     .ITEM_TYPE)).getText().equals("Folder")){
          tfStatus.setText(selectedRowsCount + " Object(s) selected");  
        }else{
          String type = ((JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable
                                                                   .TYPE)).getText();
          String size = ((JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable
                                                                   .SIZE)).getText();
          tfStatus.setText("Type: " + type + "Size:" + size);  
        }
      }
    }
  }

  /**
   * To move forward to the next folder in local browser and sets the current folder to 
   * next folder.
   * @param e ActionEvent object
   */
  private void butLocalNavigateForward_actionPerformed(ActionEvent e) {
    String currentFolderPath = folderDocInfoClient.getNextFolderPath();
    fillLocalFolderFileList(new File(currentFolderPath));
    if(!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)){
      splpLocalSystem.remove(splpLocalSystem.getRightComponent());;
      splpLocalSystem.setRightComponent(jpLocalFolderFileList);
    }
    
    addItemToLocalComboBox(currentFolderPath);
    folderDocInfoClient.setCurrentFolderPath(currentFolderPath);
    butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
    butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
    butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());
    
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
    DefaultMutableTreeNode nodeToFill= GeneralUtil.findTreeNode(root,currentFolderPath);
    FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
    String nodePathToFill = fsFolderHolder.getPath();
    logger.debug("Current Folder Path : " + currentFolderPath);
    logger.debug("Path of Node To be Filled: " + nodePathToFill);
    
    TreeNode treeNodes[] = nodeToFill.getPath();
    TreePath treePathToSelect = new TreePath(treeNodes);
    treeLocalTreeView.setSelectionPath(treePathToSelect); 
    treeLocalTreeView.requestFocus();
    logger.debug("treePathToSelect " + treePathToSelect);
  }

  /**
   * creates new folder with specified name
   * @param e ActionEvent object
   */
  private void butLocalCreateNewFolder_actionPerformed(ActionEvent e) {
    String folderName  = JOptionPane.showInputDialog(this,"Folder","New Folder");
  
    if(folderName != null && !folderName.trim().equals("")){
      String currentFolderPath = folderDocInfoClient.getCurrentFolderPath();
      file = new File(currentFolderPath + File.separator + folderName.trim());
      if(file.exists()){
        JOptionPane.showMessageDialog(this,"Folder with that name already exists");
      }else{
        file.mkdirs();
        localRefreshOperation();
      }
    }    
    
  }

  /**
   * To refresh the selected treenode, if node is not selected it refreshes the current
   * folderpath
   * @param e ActionEvent object
   */
  private void butLocalRefreshContent_actionPerformed(ActionEvent e) {
    localRefreshOperation();
  }

    
  /**
   * To delete a folder or file, deletes a folder recursively if it contains folders and files.
   * @param e ActionEvent object
   */
  private void butLocalDeleteFolderFile_actionPerformed(ActionEvent e) {
    localDeleteOperation();
  }

  /**
   * To rename the selected folder or file
   * @param e ActionEvent object
   */
  private void butLocalRenameFolderFile_actionPerformed(ActionEvent e) {
    localRenameOperation();
  }
  
  /**
   * searches the path specified inside the addressbar and expands the tree to that 
   * folder and highlights that folder.
   * @param e ActionEvent object
   */
  private void butLocalGo_actionPerformed(ActionEvent e){
    setWaitCursorForLocalBrowser();
    searchLocalPath();  
  }

  /**
   * searches the path specified in addressbar, if it already arrived then just highlights 
   * it if not then sends a request through a function call.
   * @param e ActionEvent object
   */
  private void butRemoteGo_actionPerformed(ActionEvent e){
    searchRemotePath();
  }


  /**
   * Gives a raised effect to button when mouse entered init.
   * @param e MouseEvent object
   */
  private void button_mouseEntered(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
  }

  
  /**
   * Makes button border normal when mouse exited from it.
   * @param e MouseEvent object
   */
  private void button_mouseExited(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(normalButtonBorder);
  }
  
  
  
  private void button_mouseEnteredNoFX(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
  }
  
  private void button_mouseExitedNoFX(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
  }

  
  /**
   * Navigates back in remote browser.
   * @param e ActionEvent object
   */
  private void butRemoteNavigateBack_actionPerformed(ActionEvent e) {
    //setWaitCursorForRemoteBrowser();
    //fsFileSystemOperationsRemote.navigateBack(FsMessage.FOR_CLIENTGUI);
    fsClient.navigateBack();
  }

  private void tblRemoteFolderFileList_mouseClicked(MouseEvent e) {
    
    JTable table = (JTable)e.getSource();
       
    FsTableModel fsTableModel = (FsTableModel)table.getModel();
    if(e.getClickCount() == 2){
      // The index of the column whose header was clicked
      int vRowIndex = table.getSelectedRow();
      JLabel label = (JLabel)fsTableModel.getValueAt(vRowIndex, EnumRemoteTable
                                                           .ABS_PATH);
      String absFolderPath = label.getText();
          
      if(((JLabel)fsTableModel.getValueAt(vRowIndex, EnumRemoteTable
                                                 .ITEM_TYPE)).getText().equals("Folder")){
        try{
          logger.info("Fetching content of folder " + absFolderPath);
          tfStatus.setText("Fetching content of folder " + absFolderPath);
          
          //setWaitCursorForRemoteBrowser();
          //fsFileSystemOperationsRemote.getContentOfFolder(absFolderPath,FsMessage.FOR_CLIENTGUI);
          fsClient.getContentOfFolder(absFolderPath);
                    
          DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeRemoteTreeView.getModel()).getRoot();
          DefaultMutableTreeNode nodeToFill =  dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root,absFolderPath);
          FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
          TreeNode treeNodes[];
          TreePath treePath;
          String nodeToFillPath =  fsFolderHolder.getPath();            
          if(nodeToFillPath.equals(absFolderPath)){ 
            logger.debug("Arrived Node Path" + nodeToFillPath);
            treeNodes =  nodeToFill.getPath();
            treePath = new TreePath(treeNodes);
            treeRemoteTreeView.setSelectionPath(treePath);
            treeRemoteTreeView.requestFocus();
          }else{
            //setWaitCursorForRemoteBrowser();
            //fsFileSystemOperationsRemote.getFlatFolderTree(absFolderPath,null,FsMessage.FOR_CLIENTGUI);
            fsClient.getFlatFolderTree(absFolderPath, null, FsRemoteOperationConstants.COMMAND);
          }
        }catch(Exception ex){
          logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
        }
      }else{
//          JOptionPane.showMessageDialog(this,"Do you want to download this file ");
      }
    }else{
      
      int selectedRows[] = table.getSelectedRows();
      int selectedRowsCount = selectedRows.length;
      
      logger.debug(" selectedRowsCount " + selectedRowsCount);
      if(selectedRows != null){
        if(selectedRowsCount > 1){
          tfStatus.setText(selectedRowsCount + " Object(s) selected");
        }else{
          int vRowIndex = table.getSelectedRow();
          
          if(((JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable
                                                         .ITEM_TYPE)).getText().equals("Folder")){
            tfStatus.setText(selectedRowsCount + " Object(s) selected");  
          }else{
            String type = ((JLabel)fsTableModel.getValueAt(vRowIndex,EnumLocalTable
                                                                       .TYPE)).getText();
            String size = ((JLabel)fsTableModel.getValueAt(vRowIndex,EnumLocalTable
                                                                       .SIZE)).getText();
            tfStatus.setText("Type: " + type + "Size:" + size);  
          }
        }  
      }
    }
  }

  /**
   * Navigates up to the parent of current folder in remote browser.
   * @param e ActionEvent object
   */
  private void butRemoteNavigateUp_actionPerformed(ActionEvent e) {
    //setWaitCursorForRemoteBrowser();
    butRemoteNavigateUp.setEnabled(false);
    //fsFileSystemOperationsRemote.navigateUp(FsMessage.FOR_CLIENTGUI);
    fsClient.navigateUp();
  }

  /**
   * Performs the create new folder operation in remote filesystem and refreshes the 
   * treeview and folderlist to show the same.
   * @param e ActionEvent object
   */
  private void butRemoteNewFolder_actionPerformed(ActionEvent e) {
    String folderName  = JOptionPane.showInputDialog(this,"Folder","New Folder");
    if(folderName != null && !folderName.trim().equals("")){
      //setWaitCursorForRemoteBrowser();  
      //fsFileSystemOperationsRemote.createFolder(fsFolderDocInfoHolder.getCurrentFolderPath(),folderName.trim(),FsMessage.FOR_CLIENTGUI);
      fsClient.makedir(fsFolderDocInfoHolder.getCurrentFolderPath(), folderName.trim(), FsRemoteOperationConstants.COMMAND);
      //setWaitCursorForRemoteBrowser();
      //fsFileSystemOperationsRemote.getFlatFolderTree(fsFolderDocInfoHolder.getCurrentFolderPath(),null,FsMessage.FOR_CLIENTGUI);
      fsClient.getFlatFolderTree(fsFolderDocInfoHolder.getCurrentFolderPath(), null, FsRemoteOperationConstants.COMMAND);
      //setWaitCursorForRemoteBrowser();
      //fsFileSystemOperationsRemote.getContentOfFolder(fsFolderDocInfoHolder.getCurrentFolderPath(),FsMessage.FOR_CLIENTGUI);
      fsClient.getContentOfFolder(fsFolderDocInfoHolder.getCurrentFolderPath());
    }    
  }

 

  /**
   * confirms the delete operation and stores the items to be deleted in an array.Calls
   * a function to perform actual delete.
   * @param e ActionEvent object
   */
  private void butRemoteDelete_actionPerformed(ActionEvent e) {
    remoteDeleteOperation();
  }

  /**
   * Navigates forward in remote browser.
   * @param e ActionEvent object
   */
  private void butRemoteNavigateForward_actionPerformed(ActionEvent e) {
    //setWaitCursorForRemoteBrowser();
    //fsFileSystemOperationsRemote.navigateForward(FsMessage.FOR_CLIENTGUI);
    fsClient.navigateForward();
  }

  /**
   * Refreshes the current folderpath(treeview and folderlist).
   * @param e ActionEvent object
   */
  private void butRemoteRefresh_actionPerformed(ActionEvent e) {
    remoteRefreshOperation();
  }

  /**
   * To rename a folder/file from remote filesystem.
   * @param e ActionEvent object
   */
  private void butRemoteRenameFolderFile_actionPerformed(ActionEvent e) {
    remoteRenameOperation();
  }

  /**
   * stores the items to be uploaded in an array and calls the function to perform actual download
   * @param e ActionEvent object
   */
  private void butRemoteDownload_actionPerformed(ActionEvent e) {
    int selectedRows[] = tblRemoteFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName="";
    String itemPath="";
    //initialize the overwrite value to 0
    if(selectedRowsCount <= 0){
      return;
    }
    //get items to delete
    itemPaths = new String[selectedRowsCount];
    for(int index = 0; index < selectedRowsCount; index++){
      itemName = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                              .NAME)).getText();
      itemPath = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                              .ABS_PATH)).getText();
      itemPaths[index] = itemPath;
    }
    //send request to the server to delete the selected items
    //fsFileSystemOperationsRemote.downloadItem(this,itemPaths,folderDocInfoClient.getCurrentFolderPath(),fsFolderDocInfoHolder.getCurrentFolderPath(),FsMessage.FOR_CLIENTGUI);
     int downloadCode=new Random().nextInt();
    //fsClient.download((new DownloadListener(this,downloadCode)), itemPaths, folderDocInfoClient.getCurrentFolderPath(), fsFolderDocInfoHolder.getCurrentFolderPath(), downloadCode);
  }
  
  /**
   * to push the selected items to upload onto a stack and calls a function which performs 
   * actual upload.
   * @param e ActionEvent object
   */
  private void butLocalUpload_actionPerformed(ActionEvent e) {
    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName="";
    String itemPath="";

    if(selectedRowsCount <= 0){
        return;
    }
    //get items to upload
    Stack itemToUpload = new Stack();
    for(int index = 0; index < selectedRowsCount; index++){
      itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index],EnumLocalTable
                                                             .NAME)).getText();
      itemPath = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index],EnumLocalTable
                                                             .ABS_PATH)).getText();
      itemToUpload.push(itemPath);
    }

    //fsFileSystemOperationsRemote.uploadItem(this,itemToUpload,folderDocInfoClient.getCurrentFolderPath(),fsFolderDocInfoHolder.getCurrentFolderPath(),FsMessage.FOR_CLIENTGUI);
    int uploadCode=new Random().nextInt();
    //fsClient.upload((new UploadListener(this,uploadCode)), itemToUpload, folderDocInfoClient.getCurrentFolderPath(), fsFolderDocInfoHolder.getCurrentFolderPath(), uploadCode);
  }

  /**
   * pops-up a window which handles synchronization.
   * @param e
   */
  private void butSynchronize_actionPerformed(ActionEvent e) {
    if(fsFileSync == null || !fsFileSync.isVisible()){
      //fsFileSync = new FsFileSync(this);
      //fsFileSync.setVisible(true);
    }else{
      //fsFileSync.setState(JFrame.NORMAL);
      //fsFileSync.toFront();
    }
  }

  
  /**
   * Listens to the property change event fired by FileSystemOperationLocal class.
   * @param evt PropertyChangeEvent object
   */
  public void propertyChangeFileSystemOperationLocal(PropertyChangeEvent evt){
    if(evt.getPropertyName().equals("localPasteOperationComplete")){
      ArrayList pasteOperationComplete = (ArrayList)evt.getNewValue();
      Integer clipBoardOperation = (Integer)pasteOperationComplete.get(0);
      String srcBasePath = (String)pasteOperationComplete.get(1);
      String destBasePath = (String)pasteOperationComplete.get(2);
      logger.debug("clipBoardOperation : " + clipBoardOperation);
      logger.debug("srcBasePath : " + srcBasePath);
      logger.debug("destBasePath : " + destBasePath);
      
      if(clipBoardOperation.intValue() == EnumClipBoardOperation.CUT){
        //refresh tree node for source folder
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot(); 
        DefaultMutableTreeNode treeNodeToRefresh = dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(rootNode,srcBasePath);
        logger.debug("treeNodeToRefresh  : " + treeNodeToRefresh);
        FsFileSystemOperationLocal.refreshTreeNode(treeLocalTreeView,treeNodeToRefresh);
      }
      //refresh tree node for destination folder
      DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot(); 
      DefaultMutableTreeNode treeNodeToRefresh = dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(rootNode,destBasePath);
      logger.debug("treeNodeToRefresh  : " + treeNodeToRefresh);
      FsFileSystemOperationLocal.refreshTreeNode(treeLocalTreeView,treeNodeToRefresh);
      fillLocalFolderFileList(new File(destBasePath));
      setDefaultCursorForLocalBrowser();
    }else if(evt.getPropertyName().equals("refreshFolderPath")){
      setDefaultCursorForLocalBrowser();
      file = new File((String)evt.getNewValue());
      fillLocalFolderFileList(file);
    }
  }

  
  
//  /**
//   * Listens to the property change event fired by FileSystemOperationRemote class.
//   * @param evt PropertyChangeEvent object
//   */
//  public void propertyChangeFileSystemOperationRemote(PropertyChangeEvent evt){
//    logger.debug("evt.getSource() : " + evt.getSource());
//    if(evt.getPropertyName().equals("remotePasteOperationComplete") || evt.getPropertyName().equals("remotePasteOperationCancelled")){
//      Vector tempVector = (Vector)evt.getNewValue();
//      Integer clipBoardOperation = (Integer)tempVector.get(0);
//    
//      logger.debug("clipBoardOperation : " + clipBoardOperation);
//     
//      if(clipBoardOperation.intValue() == EnumClipBoardOperation.CUT){
//        //refresh tree node for source folder
//        clipBoardRemote = null;
//        String srcBasePath = (String)tempVector.get(2);
//        logger.debug("srcBasePath : " + srcBasePath);
//        //setWaitCursorForRemoteBrowser();
//        fsFileSystemOperationsRemote.getFlatFolderTree(srcBasePath,null,FsMessage.FOR_CLIENTGUI);
//      }
//      //refresh tree node for destination folder
//      String destBasePath = (String)tempVector.get(1);
//      logger.debug("destBasePath : " + destBasePath);
//      //setWaitCursorForRemoteBrowser();
//      fsFileSystemOperationsRemote.getFlatFolderTree(destBasePath,null,FsMessage.FOR_CLIENTGUI);     
//      fsFileSystemOperationsRemote.getContentOfFolder(destBasePath,FsMessage.FOR_CLIENTGUI);
//    }else{
//      FsResponse fsResponse;
//      FsExceptionHolder fsExceptionHolder;
//      if(evt.getSource().equals(FsMessage.FOR_CLIENTGUI)){
//        //setDefaultCursorForRemoteBrowser();
//        if(evt.getPropertyName().equals("fsResponse")){
//          fsResponse = (FsResponse)evt.getNewValue();
//          logger.debug("fsResponse.getResponseCode() : " + fsResponse.getResponseCode());
//          //handleResponse(fsResponse);
//        }if(evt.getPropertyName().equals("fsExceptionHolder")){
//          fsExceptionHolder = (FsExceptionHolder)evt.getNewValue();
//          logger.debug(fsExceptionHolder);
//          //handle exception
//        }
//      }
//    }
//  }
  
//  /**
//   * Handles response from server
//   * @param fsResponse
//   */
//  private void handleResponse(FsResponse fsResponse) {
//    FsExceptionHolder fsExceptionHolder;
//    String homeFolder;
//    try{
//      int responseCode = fsResponse.getResponseCode();
//      switch (responseCode){
//      case FsResponse.CONNECT:
//        Boolean connectionSuccessFul = (Boolean)fsResponse.getData();
//        if(connectionSuccessFul.booleanValue()){
//          enableRemoteWindowControls(true);
//          remoteWillTreeExpansionListener = new RemoteWillTreeExpansionListener(fsClient, treeRemoteTreeView,FsRemoteOperationConstants.COMMAND);
//          logger.info("Connected to the server");
//          tfStatus.setText("Connected to the server");
//          //setWaitCursorForRemoteBrowser();
//          fsFileSystemOperationsRemote.getRootFolders(FsMessage.FOR_CLIENTGUI);
//          butSynchronize.setEnabled(true);
//          butDisconnect.setEnabled(true);
//          menuFileDisconnect.setEnabled(true);
//          userLogin.dispose();
//        }else{
//          fsExceptionHolder = fsResponse.getFsExceptionHolder();
//          logger.error(fsExceptionHolder.getErrorMessage());
//          tfStatus.setText(fsExceptionHolder.getErrorMessage());
//          JOptionPane.showMessageDialog(this,"Invalid userid/password","Login failed",JOptionPane.ERROR_MESSAGE);
//        }
//        break;
//      case FsMessage.DISCONNECT:
//        logger.info("User Disconnected");
//        break;
//      case FsMessage.GET_ROOT_FOLDERS:
//        {
//          String selectTreeNodePath = null;
//          Object[] treeNodes = fsResponse.getDatas();
//          String currentTreeNodePath = fsResponse.getCurrentTreeNodePath();
//          logger.debug("currentTreeNodePath : " + currentTreeNodePath);
//          selectTreeNodePath = fsResponse.getSelectTreeNodePath();
//          logger.debug("selectTreeNodePath : " + selectTreeNodePath);
//          remoteWillTreeExpansionListener.insertTreeNodesRemoteTreeView(treeNodes,currentTreeNodePath,selectTreeNodePath);
//          //setWaitCursorForRemoteBrowser();
//          fsFileSystemOperationsRemote.getHomeFolder(FsMessage.FOR_CLIENTGUI);
//        }
//        break;
//      case FsResponse.GETCONTENTOFFOLDER:
//        Object objects[] = fsResponse.getDatas();
//        fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
//        fillRemoteFolderFileList(objects);
//        String absFolderPath = fsFolderDocInfoHolder.getCurrentFolderPath();
//        logger.debug("absFolderPath" + absFolderPath);
//        addItemToRemoteComboBox(absFolderPath);
//        butRemoteNavigateBack.setEnabled(fsFolderDocInfoHolder.isEnableBackButton());
//        butRemoteNavigateForward.setEnabled(fsFolderDocInfoHolder.isEnableForwardButton());
//        butRemoteNavigateUp.setEnabled(fsFolderDocInfoHolder.isEnableParentButton());
//        break;
//      case FsMessage.NAVIGATEBACK:
//      case FsMessage.NAVIGATEFORWARD:
//      case FsMessage.GETCONTENTOFPARENTFOLDER:
//        objects = fsResponse.getDatas();
//        fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
//        fillRemoteFolderFileList(objects);
//        absFolderPath = fsFolderDocInfoHolder.getCurrentFolderPath();
//        logger.debug("absFolderPath" + absFolderPath);
//        
//        DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeRemoteTreeView.getModel()).getRoot();
//        DefaultMutableTreeNode nodeToFill = GeneralUtil.findTreeNode(root,absFolderPath);
//        FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
//        TreeNode nodesOfTree[];
//        TreePath treePath;
//        String nodeToFillPath =  fsFolderHolder.getPath();            
//      
//        logger.debug("Arrived Node Path" + nodeToFillPath);
//        nodesOfTree =  nodeToFill.getPath();
//        treePath = new TreePath(nodesOfTree);
//        treeRemoteTreeView.setSelectionPath(treePath);
//        treeRemoteTreeView.requestFocus();
//        
//        addItemToRemoteComboBox(absFolderPath);
//        butRemoteNavigateBack.setEnabled(fsFolderDocInfoHolder.isEnableBackButton());
//        butRemoteNavigateForward.setEnabled(fsFolderDocInfoHolder.isEnableForwardButton());
//        butRemoteNavigateUp.setEnabled(fsFolderDocInfoHolder.isEnableParentButton());
//        break;
//      case FsMessage.GET_FLAT_FOLDER_TREE:
//        String selectTreeNodePath = null;
//        Object[] treeNodes = fsResponse.getDatas();
//        displayTreeNode(treeNodes);
//        String currentTreeNodePath = fsResponse.getCurrentTreeNodePath();
//        logger.debug("currentTreeNodePath : " + currentTreeNodePath);
//        selectTreeNodePath = fsResponse.getSelectTreeNodePath();
//        logger.debug("selectTreeNodePath : " + selectTreeNodePath);
//        logger.debug(" treeNodes : " + treeNodes);
//        remoteWillTreeExpansionListener.insertTreeNodesRemoteTreeView(treeNodes,currentTreeNodePath,selectTreeNodePath);
//        break; 
//      case FsMessage.SEARCH_FAILURE:
//        if(splpRemoteSystem.getBottomComponent().equals(jpRemoteFolderFileList)){
//          splpRemoteSystem.remove(splpRemoteSystem.getBottomComponent());
//          JPanel panel = new JPanel();
//          panel.setBackground(Color.WHITE);
//          JLabel label = new JLabel("Invalid Path");
//          label.setBackground(Color.WHITE);
//          panel.add(label);
//          splpRemoteSystem.add(panel,JSplitPane.BOTTOM);
//        }  
//        break;
//      case FsResponse.DOWNLOAD_COMPLETED:
//        JOptionPane.showMessageDialog(this,"File(s) Downloaded successfully");
//        logger.info("Download Completed");
//        tfStatus.setText("Download Completed");
//        localRefreshOperation();
//        break;
//      case FsResponse.DOWNLOAD_FAILURE:
//        JOptionPane.showMessageDialog(this,"Download failed");
//        logger.info("Download failure");
//        tfStatus.setText("Download failure");
//        localRefreshOperation();
//        break;
//      case FsResponse.DOWNLOAD_CANCELED:
//        JOptionPane.showMessageDialog(this,"Download canceled");
//        logger.info("Download canceled");
//        tfStatus.setText("Download canceled");
//        localRefreshOperation();
//        break;
//      case FsResponse.UPLOAD_COMPLETED:
//        logger.info("Upload Complete");
//        tfStatus.setText("Upload Complete");
//        JOptionPane.showMessageDialog(this,"File(s) uploaded successfully");  
//        remoteRefreshOperation();
//        break;
//      case FsResponse.UPLOAD_FAILURE:
//        logger.info("Upload Failure");
//        tfStatus.setText("Upload Failure");
//    
//        JOptionPane optionPane = getNarrowOptionPane(50);
//        optionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
//        if(fsResponse.getFsExceptionHolder().getErrorCode() == 30002){
//          optionPane.setMessage("Failed to upload, access denied for the specified destination folder.");
//        }
//        JDialog dialog = optionPane.createDialog(this, "Upload Failure");        
//        dialog.setVisible(true);
//
//        butLocalUpload.setEnabled(true);
//        remoteRefreshOperation();
//        break;
//      case FsMessage.UPLOAD_CANCELED:
//        logger.info("Upload canceled");
//        tfStatus.setText("Upload canceled");
//        JOptionPane.showMessageDialog(this,"Upload Operation Cancelled");
//        butLocalUpload.setEnabled(true);
//        remoteRefreshOperation();
//        break;
//      case FsMessage.RENAME:
//        remoteRefreshOperation();
//        break;
//      case FsMessage.DELETEITEMS:
//        String parentOfDeletedItems = (String)fsResponse.getData();
//        //setWaitCursorForRemoteBrowser();
//        fsFileSystemOperationsRemote.getFlatFolderTree(parentOfDeletedItems,null,FsMessage.FOR_CLIENTGUI);
//        //setWaitCursorForRemoteBrowser();
//        fsFileSystemOperationsRemote.getContentOfFolder(parentOfDeletedItems,FsMessage.FOR_CLIENTGUI);
//        break;
//      case FsMessage.GET_HOME_FOLDER:
//        String homeFolderPath = (String)fsResponse.getData();
//        logger.debug("homeFolder : " + homeFolderPath);
//        //setWaitCursorForRemoteBrowser();
//        fsFileSystemOperationsRemote.getFolderRoot(homeFolderPath,FsMessage.FOR_CLIENTGUI);
//        break;
//      case FsMessage.GET_FOLDER_ROOT:
//        String homeFolderRoot = (String)fsResponse.getData();
//        logger.debug("homeFolderRoot : " + homeFolderRoot);
//        homeFolder = (String)fsResponse.getData1();
//        logger.debug("homeFolder : " + homeFolder);
//        //setWaitCursorForRemoteBrowser();
//        fsFileSystemOperationsRemote.getFlatFolderTree(homeFolderRoot, homeFolder,FsMessage.FOR_CLIENTGUI);
//        break;
//      case FsMessage.FILE_PROPERTYPAGE:
//        FsFilePropertyPageRemote fsFilePropertyPageRemote = (FsFilePropertyPageRemote)fsResponse.getData();
//        
//        FsFilePropertyPage fsFilePropertyPage = new FsFilePropertyPage(this,fsFilePropertyPageRemote.getName() + "properties",false);     
//        fsFilePropertyPage.setFileName(fsFilePropertyPageRemote.getName());  
//        fsFilePropertyPage.setJlblFileIcon(this.getFileIcon(fsFilePropertyPageRemote.getName()));
//        fsFilePropertyPage.setTypeOfFile(fsFilePropertyPageRemote.getFileType());          
//        fsFilePropertyPage.setLocation(fsFilePropertyPageRemote.getLocation());
//        fsFilePropertyPage.setSize(fsFilePropertyPageRemote.getSize()/1024 + " Kb");
//        fsFilePropertyPage.setCreationDate(fsFilePropertyPageRemote.getCreationDate());
//        fsFilePropertyPage.setModifiedDate(fsFilePropertyPageRemote.getModifiedDate().toString());
//        String permissions[] = fsFilePropertyPageRemote.getFsPermissionHolder().getPermissions();        
//        StringBuffer permission = new StringBuffer("");
//        for(int index = 0 ; index < permissions.length ; index++){
//          permission.append( permissions[index]);
//          if(permissions.length > 1){
//            permission.append(",");
//          }
//        }
//        fsFilePropertyPage.setPermissions(permission.toString());
//        fsFilePropertyPage.addPropertyChangeSupport(new PropertyChangeListener(){
//        public void propertyChange(PropertyChangeEvent evt){
//          //setWaitCursorForRemoteBrowser();
//          fsFileSystemOperationsRemote.changePropertiesOfFile(evt);
//          FsFilePropertyPageRemote  fsFilePropertyPageRemote =(FsFilePropertyPageRemote)evt.getNewValue();      
//          //setWaitCursorForRemoteBrowser();
//          fsFileSystemOperationsRemote.getContentOfFolder(fsFilePropertyPageRemote.getLocation(),FsMessage.FOR_CLIENTGUI);
//        }
//        });
//        fsFilePropertyPage.setPropertyPageFor(FsMessage.FOR_REMOTE_FILESYSTEM);
//        fsFilePropertyPage.setOldFileInfo(fsFilePropertyPageRemote.getName());
//        
//        fsFilePropertyPage.setVisible(true);
//        break;  
//      case FsMessage.FOLDER_PROPERTYPAGE:
//        FsFolderPropertyPageRemote fsFolderPropertyPageRemote = (FsFolderPropertyPageRemote)fsResponse.getData();
//
//        FsFolderPropertyPage fsFolderPropertyPage = new FsFolderPropertyPage(this, fsFolderPropertyPageRemote.getName() + " properties",false);
//        fsFolderPropertyPage.setFolderName(fsFolderPropertyPageRemote.getName());
//        //fsFolderPropertyPage.setJlblFolderIcon();                                                         saurabh_remove
//        fsFolderPropertyPage.setFileFolderCount(fsFolderPropertyPageRemote.getFileCount(), fsFolderPropertyPageRemote.getFolderCount());
//        fsFolderPropertyPage.setLocation(fsFolderPropertyPageRemote.getLocation());          
//        fsFolderPropertyPage.setType(fsFolderPropertyPageRemote.getFolderType());
//        fsFolderPropertyPage.setSize(fsFolderPropertyPageRemote.getSize()/1024 + " KB");
//        
//        FsPermissionHolder fsPermissionHolder = fsFolderPropertyPageRemote.getFsPermissionHolder();
//        permissions = fsPermissionHolder.getPermissions();
//        permission = new StringBuffer("");
//        for(int index = 0 ; index < permissions.length ; index++){
//          permission.append(permissions[index]);
//          if(permissions.length > 1){
//            permission.append(",");
//          }
//        }
//        fsFolderPropertyPage.setPermissions(permission.toString());
//        
//        fsFolderPropertyPage.addPropertyChangeSupport(new PropertyChangeListener(){
//        public void propertyChange(PropertyChangeEvent evt){
//          //setWaitCursorForRemoteBrowser();
//          fsFileSystemOperationsRemote.changePropertiesOfFolder(evt);
//          FsFolderPropertyPageRemote fsFolderPropertyPageRemote = (FsFolderPropertyPageRemote)evt.getNewValue();
//          //setWaitCursorForRemoteBrowser();
//          fsFileSystemOperationsRemote.getContentOfFolder(fsFolderPropertyPageRemote.getLocation(),FsMessage.FOR_CLIENTGUI);
//        }
//        });
//        fsFolderPropertyPage.setPropertyPageFor(FsMessage.FOR_REMOTE_FILESYSTEM);
//        fsFolderPropertyPage.setOldAttributes(fsFolderPropertyPageRemote.getName());
//        fsFolderPropertyPage.setVisible(true);
//        break;    
//      case FsMessage.FILE_FOLDER_PROPERTYPAGE:
//        FsFileFolderPropertyPage fsFileFolderPropertyPage = new FsFileFolderPropertyPage(this,"File(s) and Folder(s) properties",false);
//        FsFileFolderPropertyPageRemote fsFileFolderPropertyPageRemote = (FsFileFolderPropertyPageRemote)fsResponse.getData();
//        
//        fsFileFolderPropertyPage.setType(fsFileFolderPropertyPageRemote.getType());
//        fsFileFolderPropertyPage.setLocation(fsFileFolderPropertyPageRemote.getLocation());
//        fsFileFolderPropertyPage.setSize(fsFileFolderPropertyPageRemote.getSize()/1024 + " KB");
//        fsFileFolderPropertyPage.setNoOfFilesFolders(fsFileFolderPropertyPageRemote.getNoOfFiles(),fsFileFolderPropertyPageRemote.getNoOfFolders());
//        fsFileFolderPropertyPage.setPropertyPageFor(FsMessage.FOR_REMOTE_FILESYSTEM);
//        fsFileFolderPropertyPage.setVisible(true);
//        break;  
//      case FsMessage.ERROR_MESSAGE:
//        //setDefaultCursorForRemoteBrowser();
//        objects = fsResponse.getDatas();
//        fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
//        fsExceptionHolder = fsResponse.getFsExceptionHolder();
//        if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
//          || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
//          || fsExceptionHolder.getErrorCode() == 30041){
//          tfStatus.setText("Access denied");
//          JOptionPane.showMessageDialog(this,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//        }else if(fsExceptionHolder.getErrorCode() == 68005){
//          String errorMsg = fsExceptionHolder.getErrorMessage();
//          logger.debug("Error Message :" + errorMsg);
//          JOptionPane.showMessageDialog(this, "A file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
//        }else{
//          JOptionPane.showMessageDialog(this,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//          tfStatus.setText("" + fsExceptionHolder);
//          fillRemoteFolderFileList(objects);
//        }
//        logger.error(fsExceptionHolder);
//        break;
//      case FsMessage.FETAL_ERROR:
//        //setWaitCursorForRemoteBrowser();
//        logger.error("Fetal Error");
//        tfStatus.setText("Fatal Error");
//        break;
//      }
//    }catch(Exception ex){
//      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
//      tfStatus.setText( ex.getMessage());
//    }
//  }


  /**
   * It is written for the purpose of debugging,to print all the nodes and its child nodes
   * that are coming from the server are in proper order or not.
   * @param treeNode
   */
  private void displayTreeNode(Object[] treeNode){
    int treeNodeCount = treeNode.length;
    FsFolderHolder fsFolderHolder;
    for(int index = 0; index < treeNodeCount; index++){
      fsFolderHolder = (FsFolderHolder)treeNode[index];
      logger.debug("nodeName : " + fsFolderHolder.getName());
      Object[] tempObjects = fsFolderHolder.getItems();
      if(tempObjects.length != 0){
        displayTreeNode(tempObjects);
      }
    }   
  }
  
  private Icon getFileIcon(String fileName){
    String userHome = System.getProperty("user.home");
    File noExtensionFile = new File(userHome + "/.dbsfs/mimetype/abc");
    Icon noExtensionFileIcon = fileSystemView.getSystemIcon(noExtensionFile);
    String fileExtension;
    Icon fileIcon;
            
    int lastIndexOfDot = fileName.lastIndexOf(".");
    if(lastIndexOfDot == -1){
      fileIcon = noExtensionFileIcon;
    }else{
      fileExtension = fileName.substring(lastIndexOfDot + 1);
      file = new File(userHome + "/.dbsfs/mimetype/" + fileExtension + "." + fileExtension);
      if(file.exists()){
        fileIcon = fileSystemView.getSystemIcon(file);
      }else{
        try{
          file.createNewFile();
          fileIcon = fileSystemView.getSystemIcon(file);
        }catch(Exception ex){
          fileIcon = noExtensionFileIcon;
        }
      }
    }  
    return fileIcon;
  }
  
  /**
   * stores selected itempaths to cut, in a global array to use it in paste operation
   * @param e ActionEvent object
   */
  private void butRemoteCut_actionPerformed(ActionEvent e) {
    remoteCutOperation();
  }

  /**
   * stores selected itempaths to copy, in a global array to use it in paste operation
   * @param e ActionEvent object
   */
  private void butRemoteCopy_actionPerformed(ActionEvent e) {
    remoteCopyOperation();
  }

  /**
   * performs the paste operation depending upon whichever is the clipboard operation(cut/copy)
   * @param e ActionEvent object
   */
  private void butRemotePaste_actionPerformed(ActionEvent e){
    remotePasteOperation();
  }

  /**
   * will push the selected item paths onto a stack to use in paste operation
   * @param e ActionEvent object
   */
  private void butLocalCut_actionPerformed(ActionEvent e) {
    localCutOperation();      
  } 
  

  /**
   * will push the selected item paths onto a stack to use in paste operation.
   * @param e ActionEvent object
   */
  private void butLocalCopy_actionPerformed(ActionEvent e) {
    localCopyOperation();
  }

  /**
   * To perform paste operation depending upon whichever action copy or paste.
   * @param e ActionEvent object
   */
  private void butLocalPaste_actionPerformed(ActionEvent e) {
    setWaitCursorForLocalBrowser();
    localPasteOperation();
  }

  /**
   * to visualize the propertypage of selected item(s).
   * @param e ActionEvent object
   */
  private void butLocalPropertyFolderFile_actionPerformed(ActionEvent e) {
    setWaitCursorForLocalBrowser();        
    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemPaths[]= new String[selectedRowsCount];
    //File itemFiles[] = new File[selectedRowsCount] ;
    
     
    if(selectedRowsCount <= 0){
      return;
    }
    
    for(int index = 0; index < selectedRowsCount; index++){
      itemPaths[index] = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index],EnumRemoteTable
                                                                     .ABS_PATH)).getText();
      //itemFiles[index] = new File(itemPath);
    }
    fsFileSystemOperationLocal.showProperties(itemPaths);
    setDefaultCursorForLocalBrowser();        
  }
  /**
   * visualizes the propertypage of selected items from remote browser.
   * @param e ActionEvent object
   */
  private void butRemoteProperty_actionPerformed(ActionEvent e) {
    int selectedRows[] = tblRemoteFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemPath="";
    String itemPaths[] = new String[selectedRowsCount] ;
    
    if(selectedRowsCount <= 0){
      return;
    }
    
    for(int index = 0; index < selectedRowsCount; index++){
      itemPath = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                              .ABS_PATH)).getText();
      itemPaths[index] = itemPath;
    }
    //setWaitCursorForRemoteBrowser();
    //fsFileSystemOperationsRemote.showProperties(itemPaths,FsMessage.FOR_CLIENTGUI);
    fsClient.getProperties(itemPaths);
  }


  /**
   * sets the value fsUser object.
   * @param fsUser FsUser object
   */
  public void setFsUser(FsUser fsUser) {
    this.fsUser = fsUser;
  }


  /**
   * returns the object fsUser.
   * @return FsUser object
   */
  public FsUser getFsUser() {
    return fsUser;
  }

  /**
   * to handle the change in node selection of local treeview.
   * with change in node selection it fills the table showing folderlist with the 
   * contents of selected node(folder).
   * @param e TreeSelectionEvent object
   */
  private void treeLocalTreeView_valueChanged(TreeSelectionEvent e){
    
    String nodePath = "";
    FsFolderHolder fsFolderHolder =  new FsFolderHolder();
    JTree tree =  (JTree)e.getSource();
    logger.debug(" In valueChanged ");
    TreePath treePath = tree.getSelectionPath();
    if(treePath == null){
      return;
    }
    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();  
    fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
    nodePath =  fsFolderHolder.getPath(); 
    
    int currentNodeLevel = currentNode.getLevel();
    logger.debug("Level : " + currentNodeLevel);
    logger.debug("Current Node Path : " + folderDocInfoClient.getCurrentFolderPath());
    logger.debug("currentNode Path : " + nodePath);
    if(currentNodeLevel != 0 && !folderDocInfoClient.getCurrentFolderPath().equals(nodePath)){           
      file = new File(nodePath);
      fillLocalFolderFileList(file);
      if(!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)){
        splpLocalSystem.remove(splpLocalSystem.getRightComponent());;
        splpLocalSystem.setRightComponent(jpLocalFolderFileList);
      }
      logger.debug("nodePath : " + nodePath);
      //if(comboLocalAddressBar.getItemAt(0) != null ){
      //  comboLocalAddressBar.removeItemAt(0);
       // comboLocalAddressBar.addItem(nodePath);
       // comboLocalAddressBar.setSelectedItem(nodePath);
      //}else {
      // comboLocalAddressBar.setSelectedItem(nodePath);
      //}
      addItemToLocalComboBox(nodePath);
      folderDocInfoClient.setCurrentFolderPath(file.getAbsolutePath());
      folderDocInfoClient.addFolderPath(file.getAbsolutePath());
      butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
      butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
      butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());
    }
    //tblLocalFolderFileList.setSelectionBackground(SystemColor.textInactiveText);
  }

  /**
   * to handle the change in node selection of remote treeview.
   * with change in node selection it sends a request to server to fetch the contents of 
   * selected node(folder).
   * contents of selected node(folder).
   * @param e TreeSelectionEvent object
   */
  private void treeRemoteTreeView_valueChanged(TreeSelectionEvent e) {
    String nodePath = "";
    FsFolderHolder fsFolderHolder = new FsFolderHolder();
    JTree tree =  (JTree)e.getSource();
    TreePath treePath = tree.getSelectionPath();
    logger.debug(" TreePath : " + treePath);
    if(treePath == null){
      return;
    }
    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();  
    logger.debug(" currentNode : " + currentNode.toString());
    int currentNodeLevel = currentNode.getLevel();
    logger.debug("Level " + currentNodeLevel);
    fsFolderHolder =  (FsFolderHolder)currentNode.getUserObject();
    nodePath = fsFolderHolder.getPath();
    
    if(currentNodeLevel != 0 ){
      if(fsFolderDocInfoHolder == null){
        //setWaitCursorForRemoteBrowser();
        //fsFileSystemOperationsRemote.getContentOfFolder(nodePath,FsMessage.FOR_CLIENTGUI);
        fsClient.getContentOfFolder(nodePath);
      }else if(!fsFolderDocInfoHolder.getCurrentFolderPath().equals(nodePath)){
        //setWaitCursorForRemoteBrowser();
        //fsFileSystemOperationsRemote.getContentOfFolder(nodePath,FsMessage.FOR_CLIENTGUI);
        fsClient.getContentOfFolder(nodePath);
      }
    }
  }

  /**
   * 
   * @param e
   */
  private void butDisconnect_actionPerformed(ActionEvent e) {
    //setWaitCursorForRemoteBrowser();
    enableRemoteWindowControls(false);
    tfStatus.setText("User Disconnected");
    //fsClient.setDisconnectionListener(new DisconnectionListener(this));
    //fsClient.disconnect((new DisconnectionListener(this)));
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
    button.setBorder(normalButtonBorder);
  }

  /**
   * Handles the File-Connect menu click operation.
   * @param e ActionEvent object
   */
  private void menuFileConnect_actionPerformed(ActionEvent e) {
    butConnect_actionPerformed();
  }
  
  /**
   * Handles File-DisconnectLogout menu click operation.
   * @param e ActionEvent object
   */
  private void menuFileDisconnect_actionPerformed(ActionEvent e) {
    butDisconnect_actionPerformed(e);
  }
 
  /**
   * Hides the main window on File-Exit menu click operation.
   * @param e ActionEvent object
   */
  private void menuFileExit_actionPerformed(ActionEvent e) {
    System.exit(0);;
  }

  
  /**
   * Handles Tools - Synchronize menu click operation.
   * @param e ActionEvent object
   */
  private void menuToolsSynchronize_actionPerformed(ActionEvent e) {
    butSynchronize_actionPerformed(e);
  }

  /**
   *  Handles Tools - Download menu click operation.
   * @param e ActionEvent object
   */
  private void menuToolsDownload_actionPerformed(ActionEvent e) {
    butRemoteDownload_actionPerformed(e);
  }

  /**
   * Handles Tools - Upload menu click operation
   * @param e ActionEvent object
   */
  private void menuToolsUpload_actionPerformed(ActionEvent e) {
    butLocalUpload_actionPerformed(e);
  }
 
  
  /**
   * Handles Local-Cut menu click operation.
   * @param e ActionEvent object
   */
  private void menuLocalCut_actionPerformed(ActionEvent e) {
    butLocalCut_actionPerformed(e);  
  }
  /**
   * Handles Local-Copy menu click operation.
   * @param e ActionEvent object
   */
  private void menuLocalCopy_actionPerformed(ActionEvent e) {
    butLocalCopy_actionPerformed(e);  
  }

  /**
   * Handles Local-Paste menu click operation.
   * @param e ActionEvent object
   */
  private void menuLocalPaste_actionPerformed(ActionEvent e) {
    butLocalPaste_actionPerformed(e);  
  }
  /**
   * Handles Local-New Folder menu click operation.
   * @param e ActionEvent object
   */
  private void menuLocalNewFolder_actionPerformed(ActionEvent e) {
    butLocalCreateNewFolder_actionPerformed(e);
  }
  /**
   * Handles Local-Rename menu click operation.
   * @param e ActionEvent object
   */
  private void menuLocalRename_actionPerformed(ActionEvent e) {
    butLocalRenameFolderFile_actionPerformed(e);
  }
  /**
   * Handles Local-Delete menu click operation.
   * @param e ActionEvent object
   */
  private void menuEditDelete_actionPerformed(ActionEvent e) {
    butLocalDeleteFolderFile_actionPerformed(e);
  }
  /**
   * Handles Local-Properties menu click operation.
   * @param e ActionEvent object
   */
  private void menuLocalProperties_actionPerformed(ActionEvent e) {
    butLocalPropertyFolderFile_actionPerformed(e);
  }
  /**
   * Handles Remote-Cut menu click operation.
   * @param e ActionEvent object
   */
  private void menuRemoteCut_actionPerformed(ActionEvent e) {
    butRemoteCut_actionPerformed(e);
  }
  /**
   * Handles Remote-Copy menu click operation.
   * @param e ActionEvent object
   */
  private void menuRemoteCopy_actionPerformed(ActionEvent e) {
    butRemoteCopy_actionPerformed(e);
  }
  /**
   * Handles Remote-Paste menu click operation.
   * @param e ActionEvent object
   */
  private void menuRemotePaste_actionPerformed(ActionEvent e) {
    butRemotePaste_actionPerformed(e);
  }
  /**
   * Handles Remote-New Folder menu click operation.
   * @param e ActionEvent object
   */
  private void menuRemoteNewFolder_actionPerformed(ActionEvent e) {
    butRemoteNewFolder_actionPerformed(e);
  }
  /**
   * Handles Remote-Rename menu click operation.
   * @param e ActionEvent object
   */
  private void menuRemoteRename_actionPerformed(ActionEvent e) {
    butRemoteRenameFolderFile_actionPerformed(e);
  }
  /**
   * Handles Remote-Delete menu click operation.
   * @param e ActionEvent object
   */
  private void menuRemoteDelete_actionPerformed(ActionEvent e) {
    butRemoteDelete_actionPerformed(e);
  }
  /**
   * Handles Remote-Properties menu click operation.
   * @param e ActionEvent object
   */
  private void menuRemoteProperties_actionPerformed(ActionEvent e) {
    butRemoteProperty_actionPerformed(e);
  }
  /**
   * Handles Local-Refresh menu click operation.
   * @param e ActionEvent object
   */
  private void menuLocalRefresh_actionPerformed(ActionEvent e) {
    butLocalRefreshContent_actionPerformed(e);
  }
  /**
   * Handles Remote-Refresh menu click operation.
   * @param e ActionEvent object
   */
  private void menuRemoteRefresh_actionPerformed(ActionEvent e) {
    butRemoteRefresh_actionPerformed(e);
  }

  private void addItemToLocalComboBox(String path) {
    comboLocalAddressBar.removeActionListener(actionListener4LocalAddressBar);
    int count = comboLocalAddressBar.getItemCount();
    for(int index = 0 ; index < count ; index++){
      if(comboLocalAddressBar.getItemAt(index).equals(path)){
        comboLocalAddressBar.setSelectedIndex(index);
        comboLocalAddressBar.addActionListener(actionListener4LocalAddressBar);
        return;
      }
    }
    comboLocalAddressBar.addItem(path);
    comboLocalAddressBar.setSelectedItem(path);
    comboLocalAddressBar.addActionListener(actionListener4LocalAddressBar);
  }

  private void addItemToRemoteComboBox(String path) {
    comboRemoteAdressBar.removeActionListener(actionListener4RemoteAddressBar);
    int count = comboRemoteAdressBar.getItemCount();
    for(int index = 0 ; index < count ; index++){
      if(comboRemoteAdressBar.getItemAt(index).equals(path)){
        comboRemoteAdressBar.setSelectedIndex(index);
        comboRemoteAdressBar.addActionListener(actionListener4RemoteAddressBar);    
        return;
      }
    }
    comboRemoteAdressBar.addItem(path);
    comboRemoteAdressBar.setSelectedItem(path); 
    comboRemoteAdressBar.addActionListener(actionListener4RemoteAddressBar);
  }

  public void savePreferences(){
    String userHome = System.getProperty("user.home");
    File miscFolder = new File(userHome + "/.dbsfs/misc");

    if(!miscFolder.exists()){
      miscFolder.mkdir();
    }
    File  file = new File(userHome + "/.dbsfs/misc/preferences");
    try{
      if(file.exists()){
        file.delete();
      }
      FileOutputStream fos = new FileOutputStream(file);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(fsPreferences);
      oos.close();
    }catch(Exception ex){
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex)); 
    }
  }

  public void readPreferences(){
    String userHome = System.getProperty("user.home");
    File file = new File(userHome + "/.dbsfs/misc/preferences");
    ObjectInputStream ois = null;
    try{
      if(file.exists() && file.length() != 0 ){
        FileInputStream fis = new FileInputStream(file);
        ois = new ObjectInputStream(fis);
        fsPreferences = (FsPreferences)ois.readObject();
      }else{
        fsPreferences = new FsPreferences();
      }
    }catch(Exception ex){
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
      try{
        ois.close();
        file.delete();
        fsPreferences = new FsPreferences();
      }catch(IOException ioe){
        logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ioe));
      }
    }finally{
      try{
      if(ois != null){
        ois.close();  
      }        
      }catch(IOException ioe){
        logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ioe));
      }
    }
  }

  private void applyPreferences(){
    menuLocalBrowser.setSelected(fsPreferences.isBrowserLocalVisible());
    menuRemoteBrowser.setSelected(fsPreferences.isBrowserRemoteVisible());
    menuLocalTree.setSelected(fsPreferences.isTreeLocalVisible());
    menuRemoteTree.setSelected(fsPreferences.isTreeRemoteVisible());
    menuTileHorizontally.setSelected(fsPreferences.isTileHorizontally());
    menuTileVertically.setSelected(fsPreferences.isTileVertically());
    
    showLocalBrowser();
    showLocalTree();
    showRemoteBrowser();
    showRemoteTree();
    tileHorizontally();
    tileVertically();
  }

  private void menuTileHorizontally_actionPerformed(ActionEvent e) {
    fsPreferences.setTileHorizontally(true);
    fsPreferences.setTileVertically(false);
    tileHorizontally();
    savePreferences();
    menuTileHorizontally.setSelected(true);
    menuTileVertically.setSelected(false);
  }

  private void menuTileVertically_actionPerformed(ActionEvent e) {
    fsPreferences.setTileHorizontally(false);
    fsPreferences.setTileVertically(true);
    tileVertically();
    savePreferences();
    menuTileHorizontally.setSelected(false);
    menuTileVertically.setSelected(true);
  }

  private void menuRemoteBrowser_actionPerformed(ActionEvent e) {
    if(fsPreferences.isBrowserRemoteVisible()){
      splpFileBrowser.remove(splpFileBrowser.getLeftComponent());
    }else{
      splpFileBrowser.add(jpRemoteSystem, JSplitPane.LEFT);
    }
    fsPreferences.setBrowserRemoteVisible(!fsPreferences.isBrowserRemoteVisible());
    savePreferences();
  }

  private void menuLocalBrowser_actionPerformed(ActionEvent e) {
    if(fsPreferences.isBrowserLocalVisible()){
      splpFileBrowser.remove(splpFileBrowser.getRightComponent());
    }else{
      splpFileBrowser.add(jpLocalSystem, JSplitPane.RIGHT);
    }
    fsPreferences.setBrowserLocalVisible(!fsPreferences.isBrowserLocalVisible());
    savePreferences();
  }

  private void menuRemoteTree_actionPerformed(ActionEvent e) {
    if(fsPreferences.isTreeRemoteVisible()){
      dividerLocationRemote = splpRemoteSystem.getDividerLocation();
      splpRemoteSystem.remove(splpRemoteSystem.getLeftComponent());
    }else{
      splpRemoteSystem.add(jpRemoteTreeView, JSplitPane.LEFT);
      splpRemoteSystem.setDividerLocation(dividerLocationRemote);  
    }
    fsPreferences.setTreeRemoteVisible(!fsPreferences.isTreeRemoteVisible());
    savePreferences();
  }


  private void menuLocalTree_actionPerformed(ActionEvent e) {
    if(fsPreferences.isTreeLocalVisible()){
      dividerLocationLocal = splpLocalSystem.getDividerLocation();
      splpLocalSystem.remove(splpLocalSystem.getLeftComponent());
    }else{
      splpLocalSystem.add(jpLocalTreeView,JSplitPane.LEFT);
      splpLocalSystem.setDividerLocation(dividerLocationLocal);
    }
    fsPreferences.setTreeLocalVisible(!fsPreferences.isTreeLocalVisible());
    savePreferences();
  }

  private void tileHorizontally() {
    if(fsPreferences.isTileHorizontally()){
      splpFileBrowser.setOrientation(JSplitPane.VERTICAL_SPLIT);
      splpFileBrowser.setDividerLocation(jpFileBrowser.getHeight()/2);  
      jpToolBarLocal.setPreferredSize(new Dimension(jpToolBarLocal.getWidth(), 34));
      jpToolBarRemote.setPreferredSize(new Dimension(jpToolBarRemote.getWidth(), 34));
    }
  }

  private void tileVertically() {
    if(fsPreferences.isTileVertically()){
      splpFileBrowser.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
      splpFileBrowser.setDividerLocation(jpFileBrowser.getWidth()/2);  
      jpToolBarLocal.setPreferredSize(new Dimension(jpToolBarLocal.getWidth(), 60));
      jpToolBarRemote.setPreferredSize(new Dimension(jpToolBarRemote.getWidth(), 60));
    }
  }

  private void showRemoteBrowser() {
    if(!fsPreferences.isBrowserRemoteVisible()){
      splpFileBrowser.remove(splpFileBrowser.getLeftComponent());
    }
  }

  private void showLocalBrowser() {
    if(!fsPreferences.isBrowserLocalVisible()){
      splpFileBrowser.remove(splpFileBrowser.getRightComponent());
    }
  }

  private void showRemoteTree() {
    if(!fsPreferences.isTreeRemoteVisible()){
      splpRemoteSystem.remove(splpRemoteSystem.getLeftComponent());
    }
  }

  private void showLocalTree(){
    if(!fsPreferences.isTreeLocalVisible()){
      splpLocalSystem.remove(splpLocalSystem.getLeftComponent());
    }
  }

  private void enableRemoteWindowControls(boolean flag){
    menuRemote.setEnabled(flag);
    menuTools.setEnabled(flag);
    menuFileConnect.setEnabled(!flag);
    menuFileDisconnect.setEnabled(flag);
    butDisconnect.setEnabled(flag);
    butConnect.setEnabled(!flag);
    butSynchronize.setEnabled(flag);
    butRemoteCopy.setEnabled(flag);
    butRemoteCut.setEnabled(flag);
    butRemoteDelete.setEnabled(flag);
    butRemoteDownload.setEnabled(flag);
    butLocalUpload.setEnabled(flag);
    butRemoteGo.setEnabled(flag);
    butRemoteNavigateBack.setEnabled(flag);
    butRemoteNavigateForward.setEnabled(flag);
    butRemoteNavigateUp.setEnabled(flag);
    butRemoteNewFolder.setEnabled(flag);
    butRemotePaste.setEnabled(flag);
    butRemoteProperty.setEnabled(flag);
    butRemoteRefresh.setEnabled(flag);
    butRemoteRenameFolderFile.setEnabled(flag);
    comboRemoteAdressBar.setEditable(flag);
    treeRemoteTreeView.setEnabled(flag);
    tblRemoteFolderFileList.setEnabled(flag);
    comboRemoteAdressBar.setEnabled(flag);
    
    comboRemoteAdressBar.removeActionListener(actionListener4RemoteAddressBar);
    comboRemoteAdressBar.removeAllItems();
    fsTableModelRemote.setDataVector(new Vector(1));
    treeRemoteTreeView.setModel(null);
  }

  private void treeRemoteTreeView_focusLost(FocusEvent e) {
    DefaultTreeCellRenderer tcr = (DefaultTreeCellRenderer)treeRemoteTreeView.getCellRenderer();
    tcr.setBackgroundSelectionColor(new Color(204,204,204));
  }

  private void treeRemoteTreeView_focusGained(FocusEvent e) {
   DefaultTreeCellRenderer tcr = (DefaultTreeCellRenderer)treeRemoteTreeView.getCellRenderer();
   tcr.setBackgroundSelectionColor(selectionColor);
  }

  private void treeLocalTreeView_focusLost(FocusEvent e) {
    DefaultTreeCellRenderer tcr = (DefaultTreeCellRenderer)treeLocalTreeView.getCellRenderer();
    tcr.setBackgroundSelectionColor(new Color(204,204,204));
  }
  
  private void treeLocalTreeView_focusGained(FocusEvent e) {
    DefaultTreeCellRenderer tcr = (DefaultTreeCellRenderer)treeLocalTreeView.getCellRenderer();
    tcr.setBackgroundSelectionColor(selectionColor);
  }

  private void tblRemoteFolderFileList_focusLost(FocusEvent e) {
    tblRemoteFolderFileList.setSelectionBackground(new Color(204,204,204));
  }
  private void tblRemoteFolderFileList_focusGained(FocusEvent e) {
    tblRemoteFolderFileList.setSelectionBackground(selectionColor);
  }
  private void tblLocalFolderFileList_focusLost(FocusEvent e) {
    tblLocalFolderFileList.setSelectionBackground(new Color(204,204,204));
  }

  private void tblLocalFolderFileList_focusGained(FocusEvent e) {
    tblLocalFolderFileList.setSelectionBackground(selectionColor);
  }

  private void preJbInitOperation(){
    try {
      String userHome = System.getProperty("user.home");

      File mimeTypeFolder = new File(userHome + "/.dbsfs/mimetype");
      if(!mimeTypeFolder.exists()){
        mimeTypeFolder.mkdirs();
      }

      File file = new File(userHome + "/.dbsfs/mimetype/abc");
      if(!file.exists()){
        file.createNewFile();
      }
      
      readPreferences();
      //Initiate FileSyncHelp
      Class htmlBrowserClass = Class.forName("oracle.help.htmlBrowser.ICEBrowser");
      helpObject = new Help(htmlBrowserClass);
      manager = new CSHManager(helpObject);
      
      //URL helpUrl = new URL();
      //logger.debug(helpUrl);
      HelpSet myhelpset = new HelpSet(new File("help/FileSyncHelp.hs").toURL());
      helpObject.addBook(myhelpset);
      manager.setDefaultBook(myhelpset);    
      navigator = manager.getAllNavigators();
      
      manager.addComponent(treeRemoteTreeView,null,true,false);
      manager.addComponent(tblRemoteFolderFileList,"aboutDBSFileSync_html",true,false);
      manager.addComponent(treeLocalTreeView,"aboutDBSFileSync_html",true,false);
      manager.addComponent(tblLocalFolderFileList,"aboutDBSFileSync_html",true,false);
      
    }catch(Exception ex){
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
      JOptionPane.showMessageDialog(null,ex.getMessage(),"Exit",JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
  }
  private void postJbInitOperation(){ 
      applyPreferences();
      folderDocInfoClient = new FolderDocInfoClient();
      initializeLocalFolderFileList();
      file = new File(System.getProperty("user.home"));
      fillLocalFolderFileList(file);
      folderDocInfoClient.setCurrentFolderPath(file.getAbsolutePath());
      
      localWillTreeExpansionListener = new LocalWillTreeExpansionListener(treeLocalTreeView);
      localWillTreeExpansionListener.initializeLocalTreeView();
      //fsFileSystemOperationLocal = new FsFileSystemOperationLocal(this);
      fsFileSystemOperationLocal.addPropertyChangeSupport(new PropertyChangeListener() {
        public synchronized void propertyChange(PropertyChangeEvent evt){
          propertyChangeFileSystemOperationLocal(evt);
        }
      });
      
      initializeRemoteFolderFileList();
      tfStatus.setText("server Located...");
      //itemToUpload = new Stack();
      normalButtonBorder = butRemoteNavigateForward.getBorder();
  }

 

  private void searchLocalPath(){
    String addressPath = (String)comboLocalAddressBar.getSelectedItem();
    logger.debug("Local Search Path:" + addressPath);
    
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
    DefaultMutableTreeNode nodeToFill= dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root,addressPath);
       
    if(nodeToFill != null){
        File fileAddress = new File(addressPath);
        logger.debug("fileAddress.getParentFile() : " + fileAddress.getParentFile());
        if(fileAddress.exists()){
          if(fileAddress.isDirectory()){
            fillLocalFolderFileList(fileAddress);
            
            if(!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)){
              splpLocalSystem.remove(splpLocalSystem.getRightComponent());;
              splpLocalSystem.setRightComponent(jpLocalFolderFileList);
            }
            
            FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
            String nodePathToFill = fsFolderHolder.getPath();
            DefaultMutableTreeNode subTreeRoot = new DefaultMutableTreeNode();
            DefaultMutableTreeNode nodeToHighlight = new DefaultMutableTreeNode();
            logger.debug("Path of Node To be Filled:" + nodePathToFill);
            File filePathToFill = new File(nodePathToFill);
            File[] children =  filePathToFill.listFiles();
            DefaultMutableTreeNode childNode,subChildNode;
            if(fileAddress.equals(filePathToFill)){
               nodeToHighlight = nodeToFill; 
               TreeNode treeNodes[] = nodeToHighlight.getPath();
               TreePath treePathToHighlight= new TreePath(treeNodes);
               treeLocalTreeView.setSelectionPath(treePathToHighlight);
               treeLocalTreeView.requestFocus();
            }else if(fileAddress.getParentFile() !=null){
              File subChildFiles[];
              int subChildFilesLength;
              if(fileAddress.getParentFile().getAbsolutePath().equals(nodePathToFill)){
              int index = 0;
              nodeToFill.removeAllChildren();
              for(int i=0 ; i < children.length ; i++){
                if(children[i].isDirectory()){
                  childNode = new DefaultMutableTreeNode() ;
                  nodeToFill.add(childNode);
                  fsFolderHolder = new FsFolderHolder();
                  fsFolderHolder.setName(children[i].getName());
                  fsFolderHolder.setPath(children[i].getAbsolutePath());
                  childNode.setUserObject(fsFolderHolder);
                  subChildFiles = children[i].listFiles();
                  if(subChildFiles != null){
                  subChildFilesLength = subChildFiles.length;
                  logger.debug("subChildFilesLength : " + subChildFilesLength);
                    for(int counter = 0; counter < subChildFilesLength; counter++){
                        if(subChildFiles[counter].isDirectory()){
                        subChildNode = new DefaultMutableTreeNode("");
                        fsFolderHolder = new FsFolderHolder();
                        fsFolderHolder.setName("");
                        fsFolderHolder.setPath("");
                        subChildNode.setUserObject(fsFolderHolder);
                        childNode.add(subChildNode);
                        break;
                      }
                    }
                  }             
                  if(fileAddress.equals(children[i])){
                     index = nodeToFill.getIndex((TreeNode)childNode);
                     logger.debug("treeNodeToSelect : " + nodeToFill.getChildAt(index));
                     logger.debug(" Index : " + index);
                  }
                }
              }
              TreeNode treeNodes[] = nodeToFill.getPath();
              TreePath treePathToExpand = new TreePath(treeNodes);
              logger.debug("Path To Expand : " + treePathToExpand);
              treeLocalTreeView.expandPath(treePathToExpand);
              DefaultMutableTreeNode treeNodeToSelect =(DefaultMutableTreeNode)nodeToFill.getChildAt(index);
              treeNodes = treeNodeToSelect.getPath();
              logger.debug("treeNodeToSelect : " + nodeToFill.getChildAt(index));
              TreePath treePathToSelect = new TreePath(treeNodes);
              treeLocalTreeView.setSelectionPath(treePathToSelect); 
              treeLocalTreeView.requestFocus();
              logger.debug("treePathToSelect " + treePathToSelect);
              }else if(!fileAddress.equals(filePathToFill)){
                localWillTreeExpansionListener.constructSubTree(fileAddress,filePathToFill,subTreeRoot,nodeToHighlight);
                logger.debug("Filling the Node");
                nodeToFill.removeAllChildren();                
                for(int i=0 ; i < children.length ; i++){
                  if(((FsFolderHolder)subTreeRoot.getUserObject()).getName().equals(children[i].getName())){
                    nodeToFill.add(subTreeRoot);
                    logger.debug(" Adding Node : " + subTreeRoot);
                  }else{
                    childNode = new DefaultMutableTreeNode() ;
                    nodeToFill.add(childNode);
                    subChildFiles = children[i].listFiles();
                    if(subChildFiles != null){
                    subChildFilesLength = subChildFiles.length;
                    logger.debug("subChildFilesLength : " + subChildFilesLength);
                      for(int counter = 0; counter < subChildFilesLength; counter++){
                          if(subChildFiles[counter].isDirectory()){
                          subChildNode = new DefaultMutableTreeNode("");
                          fsFolderHolder = new FsFolderHolder();
                          fsFolderHolder.setName("");
                          fsFolderHolder.setPath("");
                          subChildNode.setUserObject(fsFolderHolder);
                          childNode.add(subChildNode);
                          break;
                        }
                      }
                    }             
                    fsFolderHolder = new FsFolderHolder();
                    fsFolderHolder.setName(children[i].getName());
                    fsFolderHolder.setPath(children[i].getAbsolutePath());
                    childNode.setUserObject(fsFolderHolder);
                    logger.debug(" Adding Node : " + childNode);
                  }  
                }
                TreeNode treeNodes[] = ((DefaultMutableTreeNode)nodeToHighlight.getParent()).getPath();
                logger.debug( " Node To Highlight Parent : " + (DefaultMutableTreeNode)nodeToHighlight.getParent());  
                TreePath treePathToExpand = new TreePath(treeNodes);
                logger.debug("Path To Expand : " + treePathToExpand);
                treeLocalTreeView.expandPath(treePathToExpand);
                treeNodes = nodeToHighlight.getPath();
                TreePath treePathToSelect = new TreePath(treeNodes);
                treeLocalTreeView.setSelectionPath(treePathToSelect); 
                treeLocalTreeView.requestFocus();
              } 
            }  
          }else{
              JOptionPane.showMessageDialog(this,"Do You want To DownLoad File");
          }
        }else{
        tfStatus.setText("Invalid Path");
        if(splpLocalSystem.getBottomComponent().equals(jpLocalFolderFileList)){
          splpLocalSystem.remove(splpLocalSystem.getBottomComponent());
          JPanel panel = new JPanel(new BorderLayout());
          panel.setBackground(Color.WHITE);
          JLabel label = new JLabel("Invalid Path",FsImage.iconWarning ,JLabel.CENTER);
          label.setBackground(Color.WHITE);
          panel.add(label,BorderLayout.CENTER);
          splpLocalSystem.add(panel,JSplitPane.BOTTOM);
        }  
      }  
    }else{
      comboLocalAddressBar.removeItemAt(0);
      comboLocalAddressBar.setSelectedItem(folderDocInfoClient.getCurrentFolderPath());
      tfStatus.setText("Invalid Path");
      if(splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)){
        splpLocalSystem.remove(splpLocalSystem.getRightComponent());
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel("Invalid Path",FsImage.iconWarning ,JLabel.CENTER);
        label.setBackground(Color.WHITE);
        panel.add(label,BorderLayout.CENTER);
        splpLocalSystem.add(panel,JSplitPane.BOTTOM);
      }  
     }  
     setDefaultCursorForLocalBrowser();
  }
  private void searchRemotePath(){
    String searchPath = (String)comboRemoteAdressBar.getSelectedItem();
    logger.debug("Remote Search Path:" + searchPath);
    
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeRemoteTreeView.getModel()).getRoot();
    DefaultMutableTreeNode arrivedTreeNode= dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root,searchPath);
    logger.debug("Arrived Tree Node" + arrivedTreeNode);
      
    if(arrivedTreeNode != null){
      
      FsFolderHolder fsFolderHolder = (FsFolderHolder)arrivedTreeNode.getUserObject();
      String arrivedNodePath = fsFolderHolder.getPath();
      logger.debug("Arrived Node Path" + arrivedNodePath);
      if(arrivedNodePath.equals(searchPath)){
        logger.debug("Node Already arrived : " + arrivedNodePath) ;
        //setWaitCursorForRemoteBrowser();
        //fsFileSystemOperationsRemote.getContentOfFolder(searchPath,FsMessage.FOR_CLIENTGUI);
        fsClient.getContentOfFolder(searchPath);
        TreeNode treeNode[] = arrivedTreeNode.getPath();
        TreePath treePath = new TreePath(treeNode);
        treeRemoteTreeView.setSelectionPath(treePath );
        treeRemoteTreeView.requestFocus();
      }else{ 
        //setWaitCursorForRemoteBrowser();
        //fsFileSystemOperationsRemote.searchPath(arrivedNodePath,searchPath,FsMessage.FOR_CLIENTGUI);
          fsClient.searchPath(arrivedNodePath, searchPath);
      }
    }else{
      //comboRemoteAdressBar.removeItemAt(0);
      //comboRemoteAdressBar.setSelectedItem(fsFolderDocInfoHolder.getCurrentFolderPath());
      if(splpRemoteSystem.getBottomComponent().equals(jpRemoteFolderFileList)){
        splpRemoteSystem.remove(splpRemoteSystem.getBottomComponent());
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel("Invalid Path",FsImage.iconWarning ,JLabel.CENTER);
        label.setBackground(Color.WHITE);
        panel.add(label,BorderLayout.CENTER);
        splpRemoteSystem.add(panel,JSplitPane.BOTTOM);
      }  
      tfStatus.setText("Invalid Path");
    }    
  }
  private void tblRemoteFolderFileList_keyReleased(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_DELETE){
      remoteDeleteOperation();
      e.setKeyCode(0);
    }else if(e.getKeyCode() == KeyEvent.VK_X){
      if(e.getModifiers() == KeyEvent.CTRL_MASK){
        remoteCutOperation();
      }
    }else if(e.getKeyCode() == KeyEvent.VK_C){
      if(e.getModifiers() == KeyEvent.CTRL_MASK){
        remoteCopyOperation();
      }
    }else if(e.getKeyCode() == KeyEvent.VK_V){
      if(e.getModifiers() == KeyEvent.CTRL_MASK){
       remotePasteOperation();
      }
    }else if(e.getKeyCode() == KeyEvent.VK_F2){
      remoteRenameOperation();    
    }else if(e.getKeyCode() == KeyEvent.VK_F5){
      remoteRefreshOperation();
    }  
  }
  private void tblLocalFolderFileList_keyReleased(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_DELETE){
      localDeleteOperation();
      e.setKeyCode(0);
    }else if(e.getKeyCode() == KeyEvent.VK_X){
      if(e.getModifiers() == KeyEvent.CTRL_MASK){
        localCutOperation();
      }
    }else if(e.getKeyCode() == KeyEvent.VK_C){
      if(e.getModifiers() == KeyEvent.CTRL_MASK){
        localCopyOperation();
      }
    }else if(e.getKeyCode() == KeyEvent.VK_V){
      if(e.getModifiers() == KeyEvent.CTRL_MASK){
        localPasteOperation();
      }
    }else if(e.getKeyCode() == KeyEvent.VK_F2){
      localRenameOperation();    
    }else if(e.getKeyCode() == KeyEvent.VK_F5){
      localRefreshOperation();
    }
  }
 
  private void localDeleteOperation() {
    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName="";
    String itemPath="";
    if(selectedRows.length > 0){
      if(selectedRowsCount == 1){
        itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[0],EnumLocalTable
                                                                 .NAME)).getText();
        int delete = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete item " + "\"" + itemName + "\"" ,"Delete",JOptionPane.YES_NO_OPTION);
        if(delete != JOptionPane.YES_OPTION){
          return;
        }
      }else{
        int delete = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete these " + selectedRowsCount + " items" ,"Delete",JOptionPane.YES_NO_OPTION);
        if(delete != JOptionPane.YES_OPTION){
          return;
        }
      }
      try{
        for(int index = 0; index < selectedRowsCount; index++){
          itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index],EnumLocalTable
                                                                     .NAME)).getText();
          itemPath = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index],EnumLocalTable
                                                                     .ABS_PATH)).getText();
          file = new File(itemPath);
          if(file.exists()){
            logger.debug("Deleting item : " + itemPath );
            if(!fsFileSystemOperationLocal.deleteItem(file)){
              break;
            }
          }
        }
      }catch(Exception ex){
        JOptionPane.showMessageDialog(this,"Insufficient right to delete this folder " + "\"" + itemName + "\"");
        logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
      }
      localRefreshOperation();
    } 
  }
  private void remoteDeleteOperation() {
    int selectedRows[] = tblRemoteFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName="";
    String itemPath="";

    if(selectedRows.length > 0){
      if(selectedRowsCount == 1){
        itemName = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[0],EnumRemoteTable
                                                                  .NAME)).getText();
        int delete = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete item " + "\"" + itemName + "\"" ,"Delete",JOptionPane.YES_NO_OPTION);
        if(delete != JOptionPane.YES_OPTION){
          return;
        }else{
          tfStatus.setText("Deleting : " + itemName );
        }
      }else{
        int delete = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete these " + selectedRowsCount + " items" ,"Delete",JOptionPane.YES_NO_OPTION);
        if(delete != JOptionPane.YES_OPTION){
          return;
        }else{
          tfStatus.setText("Deleting : " + selectedRowsCount + " items" );
        }
      }
      //get items to delete
      itemPaths = new String[selectedRowsCount];
      for(int index = 0; index < selectedRowsCount; index++){
        itemName = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                                  .NAME)).getText();
        itemPath = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                                  .ABS_PATH)).getText();
        itemPaths[index] = itemPath;
      }
      //setWaitCursorForRemoteBrowser();
      //fsFileSystemOperationsRemote.deleteItems(itemPaths,FsMessage.FOR_CLIENTGUI);
      fsClient.delete(itemPaths);
    }
  }
  private void localCutOperation(){
    clipBoardOperationLocal = EnumClipBoardOperation.CUT;
         
    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName="";
    String itemPath="";
     
    if(selectedRowsCount <= 0){
      return;
    }
    
    clipBoardLocal.clear();
    for(int index = 0; index < selectedRowsCount; index++){
      itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index],EnumRemoteTable
                                                             .NAME)).getText();
      itemPath = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index],EnumRemoteTable
                                                             .ABS_PATH)).getText();
      clipBoardLocal.push(itemPath);
    }
  }
  private void remoteCutOperation(){
    currClipBoardOperationRemote =  EnumClipBoardOperation.CUT;
  
    int selectedRows[] = tblRemoteFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName="";
    String itemPath="";
    
    if(selectedRowsCount <= 0){
      return;
    }
    
    itemPaths = new String[selectedRowsCount];
    for(int index = 0; index < selectedRowsCount; index++){
      itemName = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                              .NAME)).getText();
      itemPath = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                              .ABS_PATH)).getText();
      itemPaths[index] = itemPath;
    }
    
    clipBoardRemote = itemPaths;
  }
  private void localCopyOperation(){
    clipBoardOperationLocal = EnumClipBoardOperation.COPY;
       
    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName="";
    String itemPath="";
     
    if(selectedRowsCount <= 0){
      return;
    }
    clipBoardLocal.clear();
    for(int index = 0; index < selectedRowsCount; index++){
      itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index],EnumRemoteTable
                                                             .NAME)).getText();
      itemPath = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index],EnumRemoteTable
                                                             .ABS_PATH)).getText();
      clipBoardLocal.push(itemPath);
    }
  }
  private void remoteCopyOperation(){
    currClipBoardOperationRemote = EnumClipBoardOperation.COPY;
     
     int selectedRows[] = tblRemoteFolderFileList.getSelectedRows();
     int selectedRowsCount = selectedRows.length;
     String itemName="";
     String itemPath="";
     
    if(selectedRowsCount <= 0){
      return;
    }
    clipBoardRemote = new String[selectedRowsCount];
    itemPaths = new String[selectedRowsCount];
    for(int index = 0; index < selectedRowsCount; index++){
      itemName = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                              .NAME)).getText();
      itemPath = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                              .ABS_PATH)).getText();
      itemPaths [index] = itemPath;
    }      
    
    clipBoardRemote = itemPaths;
  }
  private void localPasteOperation(){
    String destBasePath;
    String srcBasePath;
    
    if(clipBoardLocal.isEmpty()){
      return;
    }
    try{
      srcBasePath = new File((String)clipBoardLocal.firstElement()).getParent();
      destBasePath = folderDocInfoClient.getCurrentFolderPath();
      fsFileSystemOperationLocal.pasteItem(clipBoardLocal,destBasePath,clipBoardOperationLocal);
    }catch(Exception ex){
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
    }
  }
  private void remotePasteOperation(){
    if(clipBoardRemote == null){
      return;
    }else{
      String srcBasePath = new File(clipBoardRemote[0]).getParent();
      String destBasePath = (new File(fsFolderDocInfoHolder.getCurrentFolderPath())).getPath();
      
      logger.debug("srcBasePath : " + srcBasePath);
      logger.debug("destBasePath : " + destBasePath);
      if(srcBasePath.equals(destBasePath)){
        logger.debug("Source and destination are same");
        return;
      }
      if(currClipBoardOperationRemote == EnumClipBoardOperation.CUT){
        logger.debug("Current ClipBoard Operation : " + currClipBoardOperationRemote);
        //setWaitCursorForRemoteBrowser();
        //fsFileSystemOperationsRemote.itemMove(this,fsFolderDocInfoHolder.getCurrentFolderPath(), clipBoardRemote,FsMessage.FOR_CLIENTGUI);
        int remoteMoveCode=new Random().nextInt();
        //fsClient.move((new RemoteMoveListener(this,remoteMoveCode)), fsFolderDocInfoHolder.getCurrentFolderPath(), clipBoardRemote, remoteMoveCode);
      }else{
        logger.debug("Current ClipBoard Operation : " + currClipBoardOperationRemote);
        //setWaitCursorForRemoteBrowser();
        //fsFileSystemOperationsRemote.itemCopy(this,fsFolderDocInfoHolder.getCurrentFolderPath(), clipBoardRemote,FsMessage.FOR_CLIENTGUI);
        int remoteCopyCode=new Random().nextInt();
        //fsClient.copy((new RemoteCopyListener(this,remoteCopyCode)), fsFolderDocInfoHolder.getCurrentFolderPath(), clipBoardRemote, remoteCopyCode); 
         
      }
    }
    
  }
  private void localRenameOperation(){
    int selectedRow = tblLocalFolderFileList.getSelectedRow();
    String itemName="";
    String itemPath="";
    
    if(selectedRow != -1){
      itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRow, EnumLocalTable
                                                             .NAME)).getText();
      itemPath = ((JLabel)fsTableModelLocal.getValueAt(selectedRow, EnumLocalTable
                                                             .ABS_PATH)).getText();
      
      String newItemName = JOptionPane.showInputDialog(this,"Rename " + itemName,itemName);
      
      file = new File(itemPath);
      if(newItemName != null && !newItemName.trim().equals("")){
        File renameFile = new File(file.getParentFile().getAbsolutePath() + File.separator + newItemName);
        if(!renameFile.exists()){
          boolean renameSuccess = file.renameTo(renameFile);
          if(renameSuccess){
            localRefreshOperation();
          }else{
            JOptionPane.showMessageDialog(this,"Insufficient right to rename " + "\"" + itemName + "\"");        
          }
        }else{
          JOptionPane.showMessageDialog(this,"Can not rename " + itemName + " :file with the name you specified already exists.");        
        }
      } 
    }
  }
  private void remoteRenameOperation(){
    int selectedRow = tblRemoteFolderFileList.getSelectedRow();
    String itemName="";
    String itemPath="";
    
    if(selectedRow != -1){
      try{
        itemName = ((JLabel)fsTableModelRemote.getValueAt(selectedRow, EnumRemoteTable
                                                                  .NAME)).getText();
        itemPath = ((JLabel)fsTableModelRemote.getValueAt(selectedRow, EnumRemoteTable
                                                                  .ABS_PATH)).getText();
        
        String newItemName = JOptionPane.showInputDialog(this,"Rename " + itemName,itemName);
        
        if(newItemName != null && !newItemName.trim().equals("")){
          //setWaitCursorForRemoteBrowser();
          //fsFileSystemOperationsRemote.renameItem(itemPath,newItemName,FsMessage.FOR_CLIENTGUI);
          fsClient.rename(itemPath, newItemName);
        }    
      }catch(Exception ex){
        JOptionPane.showMessageDialog(this,"Insufficient right to rename " + "\"" + itemName + "\"");    
        logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
      }
    }
  }
  private void localRefreshOperation(){
    DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode)treeLocalTreeView.getLastSelectedPathComponent();  
    if(selectedTreeNode != null){
      FsFileSystemOperationLocal.refreshTreeNode(treeLocalTreeView ,selectedTreeNode);  
    }else{
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
      selectedTreeNode = dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root,folderDocInfoClient.getCurrentFolderPath());
      TreeNode treeNodes[] = selectedTreeNode.getPath();
      TreePath treePathToSelect = new TreePath(treeNodes);
      treeLocalTreeView.setSelectionPath(treePathToSelect); 
      treeLocalTreeView.requestFocus();
    }
    fillLocalFolderFileList(new File(folderDocInfoClient.getCurrentFolderPath()));
    if(!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)){
      splpLocalSystem.remove(splpLocalSystem.getRightComponent());;
      splpLocalSystem.setRightComponent(jpLocalFolderFileList);
    }
  }
  private void remoteRefreshOperation(){
    DefaultMutableTreeNode selectedTreeNode;
    String selectedTreeNodePath = fsFolderDocInfoHolder.getCurrentFolderPath();
    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)((DefaultTreeModel)treeRemoteTreeView.getModel()).getRoot(); 
    selectedTreeNode = dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(rootNode,selectedTreeNodePath);
      
    logger.debug("Selected Component :" + selectedTreeNode);
    logger.debug("Selected Component Path :" + selectedTreeNodePath);

    //setWaitCursorForRemoteBrowser();
    //fsFileSystemOperationsRemote.getFlatFolderTree(selectedTreeNodePath,null,FsMessage.FOR_CLIENTGUI);
    fsClient.getFlatFolderTree(selectedTreeNodePath, null, FsRemoteOperationConstants.COMMAND);
    
    //setWaitCursorForRemoteBrowser();
    //fsFileSystemOperationsRemote.getContentOfFolder(selectedTreeNodePath,FsMessage.FOR_CLIENTGUI);
    fsClient.getContentOfFolder(selectedTreeNodePath);
  
  }
  private void comboRemoteAdressBar_keyReleased(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_ENTER){
      searchRemotePath();
    }
  }

  private void comboLocalAddressBar_keyReleased(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_ENTER){
      searchLocalPath();
    }
  }

  private void comboRemoteAdressBar_actionPerformed(ActionEvent e) {
    if(e.getModifiers() == KeyEvent.VK_SHIFT){
      searchRemotePath();    
    }
  }
  private void comboLocalAddressBar_actionPerformed(ActionEvent e) {
    if(e.getModifiers() == KeyEvent.VK_SHIFT){
      searchLocalPath();
    }
  }

  public FsClient getFsClient() {
    return fsClient;
  }

  /*  public fsFileSystemOperationsRemote getfsFileSystemOperationsRemote() {
        return fsFileSystemOperationsRemote;
    }*/

    /**
   * a action listener class for a combobox
   */
  class ComboRemoteAdressBar_ActionListener implements ActionListener{
    /**
     * handles actionPerformed on combobox.
     * @param e ActionEvent object
     */
    public void actionPerformed(ActionEvent e) {
      comboRemoteAdressBar_actionPerformed(e);
    }
  }

  /**
   * a action listener class for a combobox
   */
  
  class ComboLocalAddressBar_ActionListener implements ActionListener{
    /**
     * handles actionPerformed on combobox.
     * @param e ActionEvent object
     */
    public void actionPerformed(ActionEvent e) {
      comboLocalAddressBar_actionPerformed(e);
    }
  }

  private void this_windowOpened(WindowEvent e) {
    this.setState(JFrame.NORMAL);
  }

  private void helpAbout_ActionPerformed(ActionEvent e) {
    //AboutDbsFileSync aboutDbsFileSync = new AboutDbsFileSync();
   // aboutDbsFileSync.setVisible(true);
  }

  /**
   * sets the initial visual setting for this frame.
   */

  public void initialVisualSettings() {
    logger.debug("Visual settings initialized");
    scrpLocalFolderFileList.getViewport().setBackground(Color.WHITE); 
    scrpRemoteFolderFileList.getViewport().setBackground(Color.WHITE);
    butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
    butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
    butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());
    enableRemoteWindowControls(false);
    //butConnect.setEnabled(false);
    //menuFileConnect.setEnabled(false);
    treeLocalTreeView.requestFocus();
  }
  /**
   * sets default cursor for jpRemoteSystem
   */
   /*
  private void setDefaultCursorForRemoteBrowser(){
    cursorCounterRemote = cursorCounterRemote - 1;
    logger.debug(" cursorCounter : " + cursorCounterRemote);
    if(cursorCounterRemote <= 0){ 
      jpRemoteSystem.setCursor(defaultCursor); 
    }   
  }
  private void setWaitCursorForRemoteBrowser(){
    cursorCounterRemote++;
    jpRemoteSystem.setCursor(waitCursor);
  }
  */
  /**
   * sets default cursor for jpLocalSystem.
   */
  public void setDefaultCursorForLocalBrowser(){
    cursorCounterLocal = cursorCounterLocal - 1;
    logger.debug(" cursorCounterLocal : " + cursorCounterLocal);
    if(cursorCounterLocal <= 0){ 
      jpLocalSystem.setCursor(defaultCursor); 
    }   
  }
  private void setWaitCursorForLocalBrowser(){
    cursorCounterLocal++;
    jpLocalSystem.setCursor(waitCursor);
  }

  private void menuFilePreferences_actionPerformed(ActionEvent e){
    FsJxtaPreferences fsJxtaPreferences = new FsJxtaPreferences(this,"Jxta Preferences",false);
    fsJxtaPreferences.addPropertyChangeListener(new PropertyChangeListener(){
      public void propertyChange(PropertyChangeEvent evt){
        //fsPreferences.setDeleteJxta(((Boolean)evt.getNewValue()).booleanValue());
        savePreferences();
      }
    });
    fsJxtaPreferences.setVisible(true);
  }
  
  public static JOptionPane getNarrowOptionPane(int maxCharactersPerLineCount){ 
    // Our inner class definition
    class NarrowOptionPane extends JOptionPane { 
      int maxCharactersPerLineCount;
      NarrowOptionPane(int maxCharactersPerLineCount) { 
        this.maxCharactersPerLineCount = maxCharactersPerLineCount;
      } 
      public int getMaxCharactersPerLineCount() { 
        return maxCharactersPerLineCount;
      } 
    } 
    return new NarrowOptionPane(maxCharactersPerLineCount);
  }

  //marked for removal
  /*
  private class HandleJxtaConfiguration implements PropertyChangeListener {
      public Frame guiFrame;
      public void propertyChange(PropertyChangeEvent evt){
        Logger logger = Logger.getLogger("ClientLogger");
        jxtaClient = (JxtaClient)evt.getSource();
        Boolean jxtaConfigured = (Boolean)evt.getNewValue();
        if(jxtaConfigured.booleanValue()){
          tfStatus.setText("Server found.....");
          fsPreferences.setDeleteJxta(false);
          savePreferences();
          menuFileConnect.setEnabled(true);
          butConnect.setEnabled(true);
          fsFileSystemOperationsRemote = new FsFileSystemOperationsRemote(logger,jxtaClient);
          fsFileSystemOperationsRemote.addPropertyChangeListener(new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
            // TODO:  Implement this java.beans.PropertyChangeListener abstract method
            propertyChangeFileSystemOperationRemote(evt);
            }            
          });
        }else{
        jProgressBar1.setIndeterminate(false);
        int verify=JOptionPane.showConfirmDialog(null,"Do You want to Retry","Server Not Found....",JOptionPane.OK_CANCEL_OPTION);
          if(verify==JOptionPane.OK_OPTION){
             jProgressBar1.setIndeterminate(true);
             FsClientGui.jxtaClient.createJxtaBiDiPipe();     
           }
          else{
               System.exit(0);
           }
        }
      }
      
      public void setGuiFrame(Frame guiFrame){
        this.guiFrame = guiFrame;
      }
  }
  */

  private void menuFileClose_actionPerformed(ActionEvent e) {
    //this.setVisible(false);
    System.exit(0);
  }

  private void menuHelpTableofContents_actionPerformed(ActionEvent e){
    if(navigator != null){
      helpObject.showNavigatorWindow(navigator[0]);
    }
  }

  private void menuHelpIndexSearch_actionPerformed(ActionEvent e){
    if(navigator != null){
      helpObject.showNavigatorWindow(navigator[1]);
    }
  }

  private void menuHelpFullTextSearch_actionPerformed(ActionEvent e){
    if(navigator != null){
      helpObject.showNavigatorWindow(navigator[2]);
    }
  }
  
  
//  private class AuthenticationListener implements FsAuthenticationListener{
//    private FsClientGui fsClientGui;
//    public AuthenticationListener(FsClientGui fsClientGui){
//      this.fsClientGui=fsClientGui;
//    }
//    public void propertyChange(PropertyChangeEvent evt){
//      int propertyName=Integer.valueOf(evt.getPropertyName());
//      FsResponse fsResponse;
//      fsResponse = (FsResponse)evt.getNewValue();
//      switch (propertyName){
//        case AUTHORISED :
//          logger.info("Connected to the server");
//          
//          fsClient.setRemoteCommandListener(new RemoteCommandListener(fsClientGui));
//          
//          enableRemoteWindowControls(true);
//          remoteWillTreeExpansionListener = new RemoteWillTreeExpansionListener(fsClient,treeRemoteTreeView,FsRemoteOperationConstants.COMMAND);
//          
//          tfStatus.setText("Connected to the server");
//          
//          //setWaitCursorForRemoteBrowser();
//          
//          //fsFileSystemOperationsRemote.getRootFolders(FsMessage.FOR_CLIENTGUI);
//          
//          fsClient.getRootOfFolder(FsRemoteOperationConstants.COMMAND);
//          
//          butSynchronize.setEnabled(true);
//          butDisconnect.setEnabled(true);
//          menuFileDisconnect.setEnabled(true);
//          
//          userLogin.dispose();
//          break;
//        case UNAUTHORISED:
//          FsExceptionHolder fsExceptionHolder = fsResponse.getFsExceptionHolder();
//          logger.error(fsExceptionHolder.getErrorMessage());
//          tfStatus.setText(fsExceptionHolder.getErrorMessage());
//      
//          JOptionPane.showMessageDialog(fsClientGui,"Invalid userid/password","Login failed",JOptionPane.ERROR_MESSAGE);
//          break;
//      }
//    }
//  }
  
//  private class RemoteCommandListener implements FsRemoteCommandListener{
//    private FsClientGui fsClientGui;
//    public RemoteCommandListener(FsClientGui fsClientGui){
//      this.fsClientGui=fsClientGui;
//    }
//    
//    public void propertyChange(PropertyChangeEvent evt){
//      String homeFolder;
//      String selectTreeNodePath = null;
//      Object[] treeNodes=null;
//      String currentTreeNodePath;
//      try {
//        int propertyName=Integer.valueOf(evt.getPropertyName());
//        FsResponse fsResponse;
//        fsResponse = (FsResponse)evt.getNewValue();
//        FsExceptionHolder fsExceptionHolder;
//        logger.debug("propertyName : "+ propertyName);
//        
//        switch (propertyName){
//          case  GET_ROOT_FOLDERS:
//            treeNodes = fsResponse.getDatas();
//            currentTreeNodePath = fsResponse.getCurrentTreeNodePath();
//            logger.debug("currentTreeNodePath : " + currentTreeNodePath);
//            selectTreeNodePath = fsResponse.getSelectTreeNodePath();
//            logger.debug("selectTreeNodePath : " + selectTreeNodePath);
//            remoteWillTreeExpansionListener.insertTreeNodesRemoteTreeView(treeNodes,currentTreeNodePath,selectTreeNodePath);
//            //setWaitCursorForRemoteBrowser();
//            fsClient.getHomeFolder(FsRemoteOperationConstants.COMMAND);
//            break;
//          case  GET_HOME_FOLDER:
//            String homeFolderPath = (String)fsResponse.getData();
//            logger.debug("homeFolder : " + homeFolderPath);
//            //setWaitCursorForRemoteBrowser();
//            fsClient.getFolderRoot(homeFolderPath, FsRemoteOperationConstants.COMMAND);
//            break;
//          case  GET_FOLDER_ROOT:
//            String homeFolderRoot = (String)fsResponse.getData();
//            logger.debug("homeFolderRoot : " + homeFolderRoot);
//            homeFolder = (String)fsResponse.getData1();
//            logger.debug("homeFolder : " + homeFolder);
//            //setWaitCursorForRemoteBrowser();
//            fsClient.getFlatFolderTree(homeFolderRoot, homeFolder, FsRemoteOperationConstants.COMMAND);
//            break;
//          case  GET_FLAT_FOLDER_TREE:
//            treeNodes = fsResponse.getDatas();
//            displayTreeNode(treeNodes);
//            currentTreeNodePath = fsResponse.getCurrentTreeNodePath();
//            logger.debug("currentTreeNodePath : " + currentTreeNodePath);
//            selectTreeNodePath = fsResponse.getSelectTreeNodePath();
//            logger.debug("selectTreeNodePath : " + selectTreeNodePath);
//            logger.debug(" treeNodes : " + treeNodes);
//            remoteWillTreeExpansionListener.insertTreeNodesRemoteTreeView(treeNodes,currentTreeNodePath,selectTreeNodePath);
//            logger.debug("End of initialization");
//            break;
//          case GETCONTENTOFFOLDER:
//            Object objects[] = fsResponse.getDatas();
//            fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
//            fillRemoteFolderFileList(objects);
//            String absFolderPath = fsFolderDocInfoHolder.getCurrentFolderPath();
//            logger.debug("absFolderPath" + absFolderPath);
//            
//            addItemToRemoteComboBox(absFolderPath);
//            
//            butRemoteNavigateBack.setEnabled(fsFolderDocInfoHolder.isEnableBackButton());
//            butRemoteNavigateForward.setEnabled(fsFolderDocInfoHolder.isEnableForwardButton());
//            butRemoteNavigateUp.setEnabled(fsFolderDocInfoHolder.isEnableParentButton());
//            
//            break;
//          case NAVIGATE_FORWARD:
//          case NAVIGATE_BACK:
//          case GETCONTENTOFPARENTFOLDER:
//            objects = fsResponse.getDatas();
//            fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
//            fillRemoteFolderFileList(objects);
//            absFolderPath = fsFolderDocInfoHolder.getCurrentFolderPath();
//            logger.debug("absFolderPath" + absFolderPath);
//            
//            DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeRemoteTreeView.getModel()).getRoot();
//            DefaultMutableTreeNode nodeToFill = GeneralUtil.findTreeNode(root,absFolderPath);
//            FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
//            TreeNode nodesOfTree[];
//            TreePath treePath;
//            String nodeToFillPath =  fsFolderHolder.getPath();            
//          
//            logger.debug("Arrived Node Path" + nodeToFillPath);
//            nodesOfTree =  nodeToFill.getPath();
//            treePath = new TreePath(nodesOfTree);
//            treeRemoteTreeView.setSelectionPath(treePath);
//            treeRemoteTreeView.requestFocus();
//            
//            addItemToRemoteComboBox(absFolderPath);
//            butRemoteNavigateBack.setEnabled(fsFolderDocInfoHolder.isEnableBackButton());
//            butRemoteNavigateForward.setEnabled(fsFolderDocInfoHolder.isEnableForwardButton());
//            butRemoteNavigateUp.setEnabled(fsFolderDocInfoHolder.isEnableParentButton());
//            break;
//          case SEARCH_FAILED:
//            if(splpRemoteSystem.getBottomComponent().equals(jpRemoteFolderFileList)){
//              splpRemoteSystem.remove(splpRemoteSystem.getBottomComponent());
//              JPanel panel = new JPanel();
//              panel.setBackground(Color.WHITE);
//              JLabel label = new JLabel("Invalid Path");
//              label.setBackground(Color.WHITE);
//              panel.add(label);
//              splpRemoteSystem.add(panel,JSplitPane.BOTTOM);
//            } 
//            break;
//          case RENAME:
//            remoteRefreshOperation();
//            break;
//          case DELETE:
//            String parentOfDeletedItems = (String)fsResponse.getData();
//            //setWaitCursorForRemoteBrowser();
//            fsClient.getFlatFolderTree(parentOfDeletedItems,null, FsRemoteOperationConstants.COMMAND);
//            //setWaitCursorForRemoteBrowser();
//            //fsFileSystemOperationsRemote.getContentOfFolder(parentOfDeletedItems,FsMessage.FOR_CLIENTGUI);
//            fsClient.getContentOfFolder(parentOfDeletedItems);
//            break;
//          case FILE_PROPERTYPAGE:
//            logger.debug("Inside The ClientGui === FILE_PROPERTYPAGE..............");
//            FsFilePropertyPageRemote fsFilePropertyPageRemote = (FsFilePropertyPageRemote)fsResponse.getData();
//            
//            FsFilePropertyPage fsFilePropertyPage = new FsFilePropertyPage(fsClientGui,fsFilePropertyPageRemote.getName() + "properties",false);     
//            fsFilePropertyPage.setFileName(fsFilePropertyPageRemote.getName());  
//            fsFilePropertyPage.setJlblFileIcon(fsClientGui.getFileIcon(fsFilePropertyPageRemote.getName()));
//            fsFilePropertyPage.setTypeOfFile(fsFilePropertyPageRemote.getFileType());          
//            fsFilePropertyPage.setLocation(fsFilePropertyPageRemote.getLocation());
//            fsFilePropertyPage.setSize(fsFilePropertyPageRemote.getSize()/1024 + " Kb");
//            fsFilePropertyPage.setCreationDate(fsFilePropertyPageRemote.getCreationDate());
//            fsFilePropertyPage.setModifiedDate(fsFilePropertyPageRemote.getModifiedDate().toString());
//            String permissions[] = fsFilePropertyPageRemote.getFsPermissionHolder().getPermissions();        
//            StringBuffer permission = new StringBuffer("");
//            for(int index = 0 ; index < permissions.length ; index++){
//              permission.append( permissions[index]);
//              if(permissions.length > 1){
//                permission.append(",");
//              }
//            }
//            fsFilePropertyPage.setPermissions(permission.toString());
//            
//            fsFilePropertyPage.addPropertyChangeSupport(new PropertyChangeListener(){
//            public void propertyChange(PropertyChangeEvent evt){
//              //setWaitCursorForRemoteBrowser();
//              FsFilePropertyPageRemote  fsFilePropertyPageRemote =(FsFilePropertyPageRemote)evt.getNewValue();      
//              if (evt.getSource().equals(FsMessage.FOR_REMOTE_FILESYSTEM)) {
//                fsClient.setPropertiesOfFile(fsFilePropertyPageRemote);
//              }
//              //setWaitCursorForRemoteBrowser();
//              fsClient.getContentOfFolder(fsFilePropertyPageRemote.getLocation());
//            }
//            });
//            
//            fsFilePropertyPage.setPropertyPageFor(FsMessage.FOR_REMOTE_FILESYSTEM);
//            fsFilePropertyPage.setOldFileInfo(fsFilePropertyPageRemote.getName());
//            
//            fsFilePropertyPage.setVisible(true);
//            break;
//          case FOLDER_PROPERTYPAGE:
//            logger.debug("Inside The ClientGui === FOLDER_PROPERTYPAGE..............");
//            FsFolderPropertyPageRemote fsFolderPropertyPageRemote = (FsFolderPropertyPageRemote)fsResponse.getData();
//
//            FsFolderPropertyPage fsFolderPropertyPage = new FsFolderPropertyPage(fsClientGui, fsFolderPropertyPageRemote.getName() + " properties",false);
//            fsFolderPropertyPage.setFolderName(fsFolderPropertyPageRemote.getName());
//            //fsFolderPropertyPage.setJlblFolderIcon();                                                         saurabh_remove
//            fsFolderPropertyPage.setFileFolderCount(fsFolderPropertyPageRemote.getFileCount(), fsFolderPropertyPageRemote.getFolderCount());
//            fsFolderPropertyPage.setLocation(fsFolderPropertyPageRemote.getLocation());          
//            fsFolderPropertyPage.setType(fsFolderPropertyPageRemote.getFolderType());
//            fsFolderPropertyPage.setSize(fsFolderPropertyPageRemote.getSize()/1024 + " KB");
//            
//            FsPermissionHolder fsPermissionHolder = fsFolderPropertyPageRemote.getFsPermissionHolder();
//            permissions = fsPermissionHolder.getPermissions();
//            permission = new StringBuffer("");
//            for(int index = 0 ; index < permissions.length ; index++){
//              permission.append(permissions[index]);
//              if(permissions.length > 1){
//                permission.append(",");
//              }
//            }
//            fsFolderPropertyPage.setPermissions(permission.toString());
//            
//            fsFolderPropertyPage.addPropertyChangeSupport(new PropertyChangeListener(){
//            public void propertyChange(PropertyChangeEvent evt){
//              //setWaitCursorForRemoteBrowser();
//               FsFolderPropertyPageRemote fsFolderPropertyPageRemote = (FsFolderPropertyPageRemote)evt.getNewValue();
//               if (evt.getSource().equals(FsMessage.FOR_REMOTE_FILESYSTEM)) {
//                  fsClient.setPropertiesOfFolder(fsFolderPropertyPageRemote);
//               }
//              
//              //setWaitCursorForRemoteBrowser();
//              fsClient.getContentOfFolder(fsFolderPropertyPageRemote.getLocation());
//            }
//            });
//            fsFolderPropertyPage.setPropertyPageFor(FsMessage.FOR_REMOTE_FILESYSTEM);
//            fsFolderPropertyPage.setOldAttributes(fsFolderPropertyPageRemote.getName());
//            fsFolderPropertyPage.setVisible(true);
//            break;
//          case FILE_FOLDER_PROPERTYPAGE:
//            logger.debug("Inside The ClientGui === FILE_FOLDER_PROPERTYPAGE..............");
//            FsFileFolderPropertyPage fsFileFolderPropertyPage = new FsFileFolderPropertyPage(fsClientGui,"File(s) and Folder(s) properties",false);
//            FsFileFolderPropertyPageRemote fsFileFolderPropertyPageRemote = (FsFileFolderPropertyPageRemote)fsResponse.getData();
//            fsFileFolderPropertyPage.setType(fsFileFolderPropertyPageRemote.getType());
//            fsFileFolderPropertyPage.setLocation(fsFileFolderPropertyPageRemote.getLocation());
//            fsFileFolderPropertyPage.setSize(fsFileFolderPropertyPageRemote.getSize()/1024 + " KB");
//            fsFileFolderPropertyPage.setNoOfFilesFolders(fsFileFolderPropertyPageRemote.getNoOfFiles(),fsFileFolderPropertyPageRemote.getNoOfFolders());
//            fsFileFolderPropertyPage.setPropertyPageFor(FsMessage.FOR_REMOTE_FILESYSTEM);
//            fsFileFolderPropertyPage.setVisible(true);
//            break;
//          case ERROR_MESSAGE:
//            objects = fsResponse.getDatas();
//            fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
//            fsExceptionHolder = fsResponse.getFsExceptionHolder();
//            if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
//              || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
//              || fsExceptionHolder.getErrorCode() == 30041){
//              tfStatus.setText("Access denied");
//              JOptionPane.showMessageDialog(fsClientGui,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//            }else if(fsExceptionHolder.getErrorCode() == 68005){
//              String errorMsg = fsExceptionHolder.getErrorMessage();
//              logger.debug("Error Message :" + errorMsg);
//              JOptionPane.showMessageDialog(fsClientGui, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
//            }else{
//              JOptionPane.showMessageDialog(fsClientGui,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//              tfStatus.setText("" + fsExceptionHolder);
//              fillRemoteFolderFileList(objects);
//            }
//            logger.error(fsExceptionHolder);
//            break;
//          case FETAL_ERROR:
//            //setWaitCursorForRemoteBrowser();
//            logger.error("Fetal Error");
//            tfStatus.setText("Fatal Error");
//            break;
//        }
//      }catch(Exception ex){
//      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
//      tfStatus.setText( ex.getMessage());
//     }
//      
//    }
//    
//  }
//  
//  private class UploadListener implements FsUploadListener {
//    private FsClientGui fsClientGui;
//    
//    private int uploadCode;
//    
//    private Long totalSizeUpload=null;
//    
//    private ClientUtil clientUtil;
//    
//    //private Frame guiFrame;
//    
//    private Progress uploadProgress;
//    
//    private File fileUpload;
//    
//    public UploadListener(FsClientGui fsClientGui,int uploadCode){
//      this.fsClientGui=fsClientGui;
//      this.uploadCode=uploadCode;
//      this.clientUtil=new ClientUtil(logger);
//    }
//    public void propertyChange(PropertyChangeEvent evt){
//      int propertyName=Integer.valueOf(evt.getPropertyName());
//      FsResponse fsResponse;
//      FsExceptionHolder fsExceptionHolder;
//      fsResponse = (FsResponse)evt.getNewValue();
//      String uploadFilePath=null;
//      if(fsResponse.getSuperResponseCode().equals(Integer.toString(uploadCode))){
//         switch(propertyName){
//          case STARTED:
//           totalSizeUpload = (Long)fsResponse.getData();
//           //uploadProgress = new Progress(fsClientGui, "Upload", false, logger, fsResponse.getSuperResponseCode(), FsRemoteOperationConstants.UPLOAD);
//           uploadProgress.addPropertyChangeListener(this);
//           uploadProgress.setOperation("Uploading");
//           uploadProgress.setFilePath("");
//           // calculate the total size of selected items
//           uploadProgress.setTotalData(totalSizeUpload.longValue());
//           uploadProgress.setVisible(true);
//           break;
//          case COMPLETED:
//            logger.debug("Upload Completed....");
//            tfStatus.setText("Upload Complete");
//            JOptionPane.showMessageDialog(fsClientGui,"File(s) uploaded successfully");  
//            remoteRefreshOperation();
//            uploadProgress.dispose();
//            break;
//          case FAILED:
//            uploadProgress.dispose();
//            break;
//          case CANCLED:
//            logger.info("Upload canceled");
//            tfStatus.setText("Upload canceled");
//            JOptionPane.showMessageDialog(fsClientGui,"Upload Operation Cancelled");
//            butLocalUpload.setEnabled(true);
//            remoteRefreshOperation();
//            break;
//          case OVERWRITE_FOLDER:
//           try {
//               logger.debug("ask for overwrite Folder");
//               OverwriteOptionDialog overWriteDialog = new OverwriteOptionDialog(fsClientGui, "Folder Overwrite", true);
//               String folderName = (String)fsResponse.getData();
//               String msg = "This folder contains a folder named  " + folderName + " '";
//               overWriteDialog.setTaOverwriteMessage(msg);
//               overWriteDialog.setLblExistingFileSize(fsResponse.getData1() + " KB");
//               overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsResponse.getData2());
//               File[] itemFiles = new File[1];
//               itemFiles[0]=fileUpload;
//               overWriteDialog.setLblReplaceFileSize(clientUtil.findFoldersFilesInfo(itemFiles).getSize() + " KB");
//               overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + new Date(fileUpload.lastModified()));
//               overWriteDialog.setVisible(true);
//               Integer overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
//               fsClient.overWriteUploadFolder(overWriteValue,fsResponse.getSuperResponseCode());
//           }
//           catch (IOException e) {
//             ;
//           }
//            break;
//          case OVERWRITE_FILE:
//             logger.debug("ask for overwrite");
//             OverwriteOptionDialog overWriteDialog = new OverwriteOptionDialog(fsClientGui, "File Overwrite", true);
//             String fileName = (String)fsResponse.getData();
//             String msg = "This folder already contains a file named ' " + fileName + " '";
//             overWriteDialog.setTaOverwriteMessage(msg);
//             overWriteDialog.setLblExistingFileSize(fsResponse.getData1() + " KB");
//             overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsResponse.getData2());
//             overWriteDialog.setLblReplaceFileSize(fileUpload.length() + " KB");
//             overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + new Date(fileUpload.lastModified()));
//             overWriteDialog.setVisible(true);
//             Integer overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
//             fsClient.overWriteUploadFile(overWriteValue, fsResponse.getSuperResponseCode());
//           
//            break;
//          case CREATE_EMPTY_FILE:
//            fileUpload=(File)fsResponse.getData();
//            logger.debug("inside create empty File.........................." + fileUpload.getName());
//            break;
//          case EMPTY_FILE_CREATED:
//            
//            uploadProgress.setFilePath(""+ (String)fsResponse.getData());            
//            break;
//          case CREATE_FOLDER:
//            fileUpload=(File)fsResponse.getData();
//            logger.debug("inside create Folder.........................." + fileUpload.getName());
//            break;
//          case PROGRESS:
//            int byteRead;
//            uploadProgress.setOperation("Uploading");
//            byteRead= ((Integer)fsResponse.getData()).intValue();
//            uploadProgress.setPrevByteRead(byteRead);
//           break;
//          case PROGRESS_BUILDING:
//           uploadFilePath = fsResponse.getData().toString();
//           uploadProgress.setOperation("Building " + uploadFilePath + " on server please wait");
//           break;
//          case FILE_CURRUPTED:
//            uploadProgress.dispose();
//            JOptionPane
//            .showMessageDialog(fsClientGui, "Upload Failure Due to File Curruption.. Please Retry Again.." , "Error", JOptionPane.ERROR_MESSAGE);
//            break;
//          case PROGRESS_ERROR:
//           uploadFilePath = fsResponse.getData().toString();
//           
//            uploadProgress.dispose();
//            JOptionPane
//            .showMessageDialog(fsClientGui, "Can not access the file " + uploadFilePath, "Error", JOptionPane.ERROR_MESSAGE);
//            break;
//          case ERROR_MESSAGE:
//            Object objects[] = fsResponse.getDatas();
//            fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
//            fsExceptionHolder = fsResponse.getFsExceptionHolder();
//            if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
//              || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
//              || fsExceptionHolder.getErrorCode() == 30041){
//              tfStatus.setText("Access denied");
//              uploadProgress.dispose();
//              JOptionPane.showMessageDialog(fsClientGui,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//            }else if(fsExceptionHolder.getErrorCode() == 68005){
//              String errorMsg = fsExceptionHolder.getErrorMessage();
//              logger.debug("Error Message :" + errorMsg);
//              uploadProgress.dispose();
//              JOptionPane.showMessageDialog(fsClientGui, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
//            }else{
//              uploadProgress.dispose();
//              JOptionPane.showMessageDialog(fsClientGui,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//              tfStatus.setText("" + fsExceptionHolder);
//              fillRemoteFolderFileList(objects);
//            }
//            logger.error(fsExceptionHolder);
//            break;
//          case FETAL_ERROR:
//            uploadProgress.dispose();
//            logger.error("Fetal Error");
//            tfStatus.setText("Fatal Error");
//            break;
//          }
//       }
//     
//     }
//  }
//  
//  private class DownloadListener implements FsDownloadListener {
//    private FsClientGui fsClientGui;
//    
//    private int downloadCode;
//    
//    private Long totalSizeUpload=null;
//    
//    private ClientUtil clientUtil;
//   
//    private Progress downloadProgress;
//    
//    public DownloadListener(FsClientGui fsClientGui,int downloadCode){
//      this.fsClientGui=fsClientGui;
//      this.downloadCode=downloadCode;
//      this.clientUtil=new ClientUtil(logger);
//    }
//    
//    public void propertyChange(PropertyChangeEvent evt){
//      int propertyName=Integer.valueOf(evt.getPropertyName());
//      File fileDownload=null;
//      FsResponse fsResponse;
//      fsResponse = (FsResponse)evt.getNewValue();
//      
//      String downloadFilePath=null;
//      FsExceptionHolder fsExceptionHolder;
//      String msg=null;
//      Integer overWriteValue;
//      OverwriteOptionDialog overWriteDialog;
//      logger.debug("fsResponse " + fsResponse);
//      logger.debug("fsResponse.getSuperResponseCode() " + fsResponse.getSuperResponseCode());
//      if(fsResponse.getSuperResponseCode().equals(Integer.toString(downloadCode))){
//        switch(propertyName){
//          case STARTED:
////            downloadProgress = new Progress(fsClientGui, "Progress", false, logger, fsResponse.getSuperResponseCode(), FsRemoteOperationConstants.DOWNLOAD);
//            downloadProgress.addPropertyChangeListener(this);
//            Long totalData = (Long)fsResponse.getData();
//            downloadProgress.setTotalData(totalData.longValue());
//            downloadProgress.setOperation("Downloading");
//            downloadProgress.setFilePath("");
//            downloadProgress.setVisible(true);
//            break;
//          case COMPLETED:
//            logger.debug("Download Completed....");
//            tfStatus.setText("Download Complete");
//            JOptionPane.showMessageDialog(fsClientGui,"File(s) Download successfully");  
//            localRefreshOperation();
//            downloadProgress.dispose();
//            break;
//          case FAILED:
//            JOptionPane.showMessageDialog(fsClientGui,"Download failed");
//            logger.info("Download failure");
//            tfStatus.setText("Download failure");
//            localRefreshOperation();
//            downloadProgress.dispose();
//            break;
//          case CANCLED:
//            JOptionPane.showMessageDialog(fsClientGui,"Download canceled");
//            logger.info("Download canceled");
//            tfStatus.setText("Download canceled");
//            localRefreshOperation();
//            downloadProgress.dispose();
//            break;
//          case CREATE_EMPTY_FILE:
//            downloadFilePath = (fsResponse.getData()).toString();
//            
//            break;
//          case PROGRESS:
//            int byteRead;
//            byteRead= ((Integer)fsResponse.getData()).intValue();
//            downloadFilePath = (String)fsResponse.getData1();
//            downloadProgress.setFilePath("" + downloadFilePath);
//            downloadProgress.setPrevByteRead(byteRead);            
//            break;
//          case OVERWRITE_FILE:
//            overWriteDialog = new OverwriteOptionDialog(fsClientGui, "Overwrite File", true);
//            FsFileHolder fsFileHolder = (FsFileHolder)fsResponse.getData();
//            fileDownload=(File)fsResponse.getData1();
//            msg = "This folder already contains a file named ' " + fsFileHolder.getName() + " '";
//            overWriteDialog.setTaOverwriteMessage(msg);
//            overWriteDialog.setLblExistingFileSize(fileDownload.length() + " KB");
//            overWriteDialog.setLblExistingFileModifiedDate("Modified : " + new Date(fileDownload.lastModified()));
//            overWriteDialog.setLblReplaceFileSize(fsFileHolder.getSize() + " KB");
//            overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFileHolder.getModifiedDate());
//            overWriteDialog.setVisible(true);
//            overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
//            fsClient.overWriteDownloadFile(overWriteValue, fsResponse.getSuperResponseCode());
//            break;
//          case OVERWRITE_FOLDER:
//            overWriteDialog = new OverwriteOptionDialog(fsClientGui, "Overwrite Folder", true);
//            fileDownload=(File)fsResponse.getData1();
//            FsFolderHolder fsFolderHolder = (FsFolderHolder)fsResponse.getData();
//            msg = "This folder already contains a folder named ' "  + fsFolderHolder.getName() + " '";
//            overWriteDialog.setTaOverwriteMessage(msg);
//            overWriteDialog.setLblExistingFileSize(fileDownload.length() + " KB"); //TODO Calculated Size  should be displayed
//            overWriteDialog.setLblExistingFileModifiedDate("Modified : " + new Date(fileDownload.lastModified()));
//            overWriteDialog.setLblReplaceFileSize("4 KB"); //TODO Calculated Size  should be displayed
//            overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFolderHolder.getModifiedDate());
//            overWriteDialog.setVisible(true);
//            overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
//            fsClient.overWriteDownloadFolder(overWriteValue, fsResponse.getSuperResponseCode());
//            break;
//          case ERROR_MESSAGE:
//            logger.debug("Error message from clent to client Gui................");
//            //Object objects[] = fsResponse.getDatas();
//            fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
//            fsExceptionHolder = fsResponse.getFsExceptionHolder();
//            if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
//              || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
//              || fsExceptionHolder.getErrorCode() == 30041){
//              tfStatus.setText("Access denied");
//              downloadProgress.dispose();
//              JOptionPane.showMessageDialog(fsClientGui,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//            }else if(fsExceptionHolder.getErrorCode() == 68005){
//              String errorMsg = fsExceptionHolder.getErrorMessage();
//              logger.debug("Error Message :" + errorMsg);
//              downloadProgress.dispose();
//              JOptionPane.showMessageDialog(fsClientGui, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
//            }else{
//              downloadProgress.dispose();
//              JOptionPane.showMessageDialog(fsClientGui,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//              tfStatus.setText("" + fsExceptionHolder);
//              //fillRemoteFolderFileList(objects);
//            }
//            logger.error(fsExceptionHolder);
//            break;
//          case FETAL_ERROR:
//            downloadProgress.dispose();
//            logger.error("Fetal Error");
//            tfStatus.setText("Fatal Error");
//            break;
//        }
//      }
//    }
//  }
//  
//  private class DisconnectionListener implements FsDisconnectionListener{
//    private FsClientGui fsClientGui;
//    public DisconnectionListener(FsClientGui fsClientGui){
//      this.fsClientGui=fsClientGui;
//    }
//    public void propertyChange(PropertyChangeEvent evt){
//      int propertyName=Integer.valueOf(evt.getPropertyName());
//      FsResponse fsResponse;
//      fsResponse = (FsResponse)evt.getNewValue();
//      switch(propertyName){
//        case DISCONNECTED :
//        logger.info("User Disconnected");
//          break;  
//      }
//    }
//  }
//
//  private class RemoteCopyListener implements FsRemoteCopyListener{
//    private FsClientGui fsClientGui;
//    
//    private int remoteCopyCode;
//    
//    private Long totalSizeCopy=null;
//    
//    private ClientUtil clientUtil;
//    
//    private Progress copyProgress;
//    
//    public RemoteCopyListener(FsClientGui fsClientGui, int remoteCopyCode) {
//      this.fsClientGui=fsClientGui;
//      this.remoteCopyCode=remoteCopyCode;
//      this.clientUtil=new ClientUtil(logger);
//    }
//    
//    public void propertyChange(PropertyChangeEvent evt){
//      int propertyName=Integer.valueOf(evt.getPropertyName());
//      FsResponse fsResponse;
//      fsResponse = (FsResponse)evt.getNewValue();
//      OverwriteOptionDialog overWriteDialog;
//      Integer overWriteValue;
//      String destBasePath=null;
//      FsExceptionHolder fsExceptionHolder;
//      Integer clipBoardOperation;
//      if(fsResponse.getSuperResponseCode().equals(Integer.toString(remoteCopyCode))){
//        switch(propertyName){
//          case STARTED:
//            totalSizeCopy = (Long)fsResponse.getData();
// //           copyProgress = new Progress(fsClientGui, "Copy", false, logger, fsResponse.getSuperResponseCode(), FsRemoteOperationConstants.COPY);
//            copyProgress.addPropertyChangeListener(this);
//            copyProgress.setOperation("Copying.....");
//            copyProgress.setMaxProgress(100);
//            copyProgress.setFilePath("");
//            copyProgress.setTotalData(totalSizeCopy.longValue());
//            copyProgress.setVisible(true);
//            break;
//          case NEXT_ITEM:
//            String copyFilePath = (String)fsResponse.getData();
//            Long copyFileSize = (Long)fsResponse.getData1();
//            copyProgress.setFilePath(copyFilePath);
//            copyProgress.setPrevByteRead(copyFileSize.longValue());
//            break;
//          case PROMPT_OVERWRITE_FOLDER:
//            FsFolderHolder fsFolderHolderDest = (FsFolderHolder)fsResponse.getData();
//            FsFolderHolder fsFolderHolderSrc = (FsFolderHolder)fsResponse.getData1();
//            overWriteDialog = new OverwriteOptionDialog(fsClientGui, "Overwrite Folder", true);
//            overWriteDialog.setTaOverwriteMessage("This folder already contains a readonly file named '" + fsFolderHolderDest.getName() + "'");
//            overWriteDialog.setLblExistingFileSize("4 KB");
//            overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsFolderHolderDest.getModifiedDate());            
//            overWriteDialog.setLblReplaceFileSize("4 KB");
//            overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFolderHolderSrc.getModifiedDate());
//            overWriteDialog.setVisible(true);
//            overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
//            fsClient.overWriteCopyFolder(overWriteValue, fsResponse.getSuperResponseCode());
//            break;
//          case PROMPT_OVERWRITE_FILE:
//            FsFileHolder fsFileHolderDest = (FsFileHolder)fsResponse.getData();
//            FsFileHolder fsFileHolderSrc = (FsFileHolder)fsResponse.getData1();
//            overWriteDialog = new OverwriteOptionDialog(fsClientGui, "Overwrite File", true);
//            overWriteDialog.setTaOverwriteMessage("This folder already contains a readonly file named '" + fsFileHolderDest.getName() + "'");
//            overWriteDialog.setLblExistingFileSize(fsFileHolderDest.getSize() + "");
//            overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsFileHolderDest.getModifiedDate());
//            overWriteDialog.setLblReplaceFileSize(fsFileHolderSrc.getSize() + "");
//            overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFileHolderSrc.getModifiedDate());
//            overWriteDialog.setVisible(true);
//            overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
//            fsClient.overWriteCopyFile(overWriteValue, fsResponse.getSuperResponseCode());
//            break;
//          case FAILED:
//            copyProgress.dispose();
//            break;
//          case COMPLETED:
//            copyProgress.dispose();
//            destBasePath = (String)fsResponse.getData();
//            clipBoardOperation = (Integer)fsResponse.getData1();
//            
//            logger.debug("destBasePath : " + destBasePath);
//            logger.debug("clipBoardOperation : " + clipBoardOperation);
//            
//            fsClient.getFlatFolderTree(destBasePath, null, FsRemoteOperationConstants.COMMAND);
//            fsClient.getContentOfFolder(destBasePath);
//            break;
//          case CANCEL:
//            copyProgress.dispose();
//            destBasePath = (String)fsResponse.getData();
//            clipBoardOperation = (Integer)fsResponse.getData1();
//            
//            logger.debug("destBasePath : " + destBasePath);
//            logger.debug("clipBoardOperation : " + clipBoardOperation);
//            
//            fsClient.getFlatFolderTree(destBasePath, null, FsRemoteOperationConstants.COMMAND);
//            fsClient.getContentOfFolder(destBasePath);
//            break;
//          case ERROR_MESSAGE:
//            logger.debug("Error message from clent to client Gui................");
//            //Object objects[] = fsResponse.getDatas();
//            fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
//            fsExceptionHolder = fsResponse.getFsExceptionHolder();
//            if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
//              || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
//              || fsExceptionHolder.getErrorCode() == 30041){
//              tfStatus.setText("Access denied");
//              copyProgress.dispose();
//              JOptionPane.showMessageDialog(fsClientGui,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//            }else if(fsExceptionHolder.getErrorCode() == 68005){
//              String errorMsg = fsExceptionHolder.getErrorMessage();
//              logger.debug("Error Message :" + errorMsg);
//              copyProgress.dispose();
//              JOptionPane.showMessageDialog(fsClientGui, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
//            }else{
//              copyProgress.dispose();
//              JOptionPane.showMessageDialog(fsClientGui,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//              tfStatus.setText("" + fsExceptionHolder);
//              //fillRemoteFolderFileList(objects);
//            }
//            logger.error(fsExceptionHolder);
//            break;
//          case FETAL_ERROR:
//            copyProgress.dispose();
//            logger.error("Fetal Error");
//            tfStatus.setText("Fatal Error");
//            break;
//        }
//      }
//    }
//  }
//  
//  private class RemoteMoveListener implements FsRemoteMoveListener {
//    private FsClientGui fsClientGui;
//
//    private int remoteMoveCode;
//
//    private Long totalSizeCopy = null;
//
//    private ClientUtil clientUtil;
//
//    private Progress moveProgress;
//
//    public RemoteMoveListener(FsClientGui fsClientGui, int remoteMoveCode) {
//      this.fsClientGui = fsClientGui;
//      this.remoteMoveCode = remoteMoveCode;
//      this.clientUtil = new ClientUtil(logger);
//    }
//  
//  public void propertyChange(PropertyChangeEvent evt){
//    int propertyName=Integer.valueOf(evt.getPropertyName());
//    FsResponse fsResponse;
//    fsResponse = (FsResponse)evt.getNewValue();
//    OverwriteOptionDialog overWriteDialog;
//    Integer overWriteValue;
//    String srcBasePath =null;
//    Integer clipBoardOperation;
//    FsExceptionHolder fsExceptionHolder;
//    String destBasePath=null;
//    if(fsResponse.getSuperResponseCode().equals(Integer.toString(remoteMoveCode))){
//      switch(propertyName){
//        case STARTED:
//          totalSizeCopy = (Long)fsResponse.getData();
////          moveProgress = new Progress(fsClientGui, "Move", false, logger, fsResponse.getSuperResponseCode(), FsRemoteOperationConstants.COPY);
//          moveProgress.addPropertyChangeListener(this);
//          moveProgress.setOperation("Moving...");
//          moveProgress.setMaxProgress(100);
//          moveProgress.setFilePath("");
//          moveProgress.setTotalData(totalSizeCopy.longValue());
//          moveProgress.setVisible(true);
//          break;
//        case NEXT_ITEM:
//          String moveFilePath = (String)fsResponse.getData();
//          Long moveFileSize = (Long)fsResponse.getData1();
//          moveProgress.setFilePath(moveFilePath);
//          moveProgress.setPrevByteRead(moveFileSize.longValue());
//          break;
//        case PROMPT_OVERWRITE_FOLDER:
//          FsFolderHolder fsFolderHolderDest = (FsFolderHolder)fsResponse.getData();
//          FsFolderHolder fsFolderHolderSrc = (FsFolderHolder)fsResponse.getData1();
//          overWriteDialog = new OverwriteOptionDialog(fsClientGui, "Overwrite Folder", true);
//          overWriteDialog.setTaOverwriteMessage("This folder already contains a readonly file named '" + fsFolderHolderDest.getName() + "'");
//          overWriteDialog.setLblExistingFileSize("4 KB");
//          overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsFolderHolderDest.getModifiedDate());            
//          overWriteDialog.setLblReplaceFileSize("4 KB");
//          overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFolderHolderSrc.getModifiedDate());
//          overWriteDialog.setVisible(true);
//          overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
//          fsClient.overWriteMoveFolder(overWriteValue, fsResponse.getSuperResponseCode());
//          break;
//        case PROMPT_OVERWRITE_FILE:
//          FsFileHolder fsFileHolderDest = (FsFileHolder)fsResponse.getData();
//          FsFileHolder fsFileHolderSrc = (FsFileHolder)fsResponse.getData1();
//          overWriteDialog = new OverwriteOptionDialog(fsClientGui, "Overwrite File", true);
//          overWriteDialog.setTaOverwriteMessage("This folder already contains a readonly file named '" + fsFileHolderDest.getName() + "'");
//          overWriteDialog.setLblExistingFileSize(fsFileHolderDest.getSize() + "");
//          overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsFileHolderDest.getModifiedDate());
//          overWriteDialog.setLblReplaceFileSize(fsFileHolderSrc.getSize() + "");
//          overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFileHolderSrc.getModifiedDate());
//          overWriteDialog.setVisible(true);
//          overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
//          fsClient.overWriteMoveFile(overWriteValue, fsResponse.getSuperResponseCode());
//          break;
//        case FAILED:
//          moveProgress.dispose();
//          break;
//        case COMPLETED:
//          moveProgress.dispose();
//          srcBasePath = (String)fsResponse.getData1();
//          clipBoardOperation = (Integer)fsResponse.getData2();
//          logger.debug("srcBasePath : " + srcBasePath);
//          logger.debug("clipBoardOperation : " + clipBoardOperation);
//          fsClient.getFlatFolderTree(srcBasePath, null, FsRemoteOperationConstants.COMMAND);
//          
//          destBasePath = (String)fsResponse.getData();
//          fsClient.getFlatFolderTree(destBasePath, null, FsRemoteOperationConstants.COMMAND);
//          fsClient.getContentOfFolder(destBasePath);
//          break;
//        case CANCEL:
//          moveProgress.dispose();
//          srcBasePath = (String)fsResponse.getData1();
//          clipBoardOperation = (Integer)fsResponse.getData2();
//          logger.debug("srcBasePath : " + srcBasePath);
//          logger.debug("clipBoardOperation : " + clipBoardOperation);
//          fsClient.getFlatFolderTree(srcBasePath, null, FsRemoteOperationConstants.COMMAND);
//          
//          destBasePath = (String)fsResponse.getData();
//          fsClient.getFlatFolderTree(destBasePath, null, FsRemoteOperationConstants.COMMAND);
//          fsClient.getContentOfFolder(destBasePath);
//          break;
//        case ERROR_MESSAGE:
//          logger.debug("Error message from clent to client Gui................");
//          //Object objects[] = fsResponse.getDatas();
//          fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
//          fsExceptionHolder = fsResponse.getFsExceptionHolder();
//          if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
//            || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
//            || fsExceptionHolder.getErrorCode() == 30041){
//            tfStatus.setText("Access denied");
//            moveProgress.dispose();
//            JOptionPane.showMessageDialog(fsClientGui,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//          }else if(fsExceptionHolder.getErrorCode() == 68005){
//            String errorMsg = fsExceptionHolder.getErrorMessage();
//            logger.debug("Error Message :" + errorMsg);
//            moveProgress.dispose();
//            JOptionPane.showMessageDialog(fsClientGui, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
//          }else{
//            moveProgress.dispose();
//            JOptionPane.showMessageDialog(fsClientGui,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
//            tfStatus.setText("" + fsExceptionHolder);
//            //fillRemoteFolderFileList(objects);
//          }
//          logger.error(fsExceptionHolder);
//          break;
//        case FATEL_ERROR:
//          moveProgress.dispose();
//          logger.error("Fetal Error");
//          tfStatus.setText("Fatal Error");
//          break;
//      }
//    }
//  }
// }
}

