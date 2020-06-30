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
 * $Id: FsPreferenceUI.java,v 1.20 2006/09/08 05:01:37 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.FsClient;
import dbsentry.filesync.clientgui.preference.FsPreferences;
import dbsentry.filesync.clientgui.preference.FsSession;
import dbsentry.filesync.clientgui.preference.UploadDnloadManagerPref;
import dbsentry.filesync.common.CommonUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.FsUser;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsAuthenticationListener;
import dbsentry.filesync.common.listeners.FsConnectionListener;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class FsPreferenceUI extends JDialog {

  private Dimension buttonSize = new Dimension(65,25);

  private CardLayout layOut4PreferenceDetails = new CardLayout();

  private JPanel panel4FsPreference=new JPanel();
  
  private JPanel panel4SessionList = new JPanel();

  private JPanel panel4PreferenceDetail = new JPanel();

  private JPanel panel4UploadDownload = new JPanel();

  private JPanel panel4RdvRelay = new JPanel();

  private JPanel panel4SessionDetail = new JPanel();

  private JPanel panel4SessionButtons = new JPanel();

  private JPanel panel4Session = new JPanel();

  private JTree tree4Preferences;

  private JButton btnConnect = new JButton();

  private JButton btnCancel = new JButton();

  private JList lstSession = new JList();

  private JLabel lblSessionName = new JLabel();

  private JTextField txtSessionName = new JTextField();

  private JLabel lblPipeId = new JLabel();

  private JTextField txtPipeId = new JTextField();

  private JLabel lblSocketId = new JLabel();

  private JTextField txtSocketId = new JTextField();

  private JLabel lblUserId = new JLabel();

  private JTextField txtUserId = new JTextField();

  private JLabel lblPassword = new JLabel();

  private JPasswordField txtPassword = new JPasswordField();

  private JButton btnNewSession = new JButton();

  private JButton btnEditSession = new JButton();

  private JButton btnCopySession = new JButton();

  private JButton btnDeleteSession = new JButton();

  private JButton btnSaveSession = new JButton();

  private JButton btnCancelSession = new JButton();

  private JCheckBox chkShowUploadDownLoadMgr = new JCheckBox();

  private JCheckBox chkCloseUploadDownloadMgr = new JCheckBox();

  private JButton btnSaveUploadDownloadMgr = new JButton();

  private JButton btnCancelUploaDownloadMgr = new JButton();

  private JTextField txtRdv = new JTextField();

  private JLabel lblRdv = new JLabel();

  private JList lstRdv = new JList();

  private JLabel lblRelay = new JLabel();

  private JTextField txtRelay = new JTextField();

  private JList lstRelay = new JList();

  private JButton btnAddRdv = new JButton();

  private JButton btnDeleteRdv = new JButton();

  private JButton btnAddRelay = new JButton();

  private JButton btnDeleteRelay = new JButton();

  private JButton btnSaveRdvRelay = new JButton();

  private JButton btnCancelRdvRelay = new JButton();

  private JPanel panel4Connection = new JPanel();

  private JPanel panel4ConnectionButtons = new JPanel();

  private JPanel panel4ConnectionProgressBar = new JPanel();

  private JTextArea txaLog = new JTextArea();

  private JProgressBar connectionProgressBar;

  private JScrollPane scrollPane4SessionList;
  
  private JScrollPane scrollPane4Tree4Preference;

  private JScrollPane scrollPane4RdvList;

  private JScrollPane scrollPane4RelayList;

  private JScrollPane scrollPane4TxaLog;

  private ActionListener restartPipeConnectionActionListener;

  private ActionListener restartJxtaActionListener;

  private FsPreferenceUI fsPreferenceUI;

  private String sessionName;

  private Logger logger;

  private CommonUtil commonUtil;

  private boolean isEdit = false;

  private FsTray fsTray;

  private UserLogin userLogin;

  private FsUser fsUser = null;

  private String currentSessionName = null;

  private FileSyncClientMDI mdiWindow = null;

  private FsClient fsClient = null;

  private String userCache = System.getProperty("user.home") + "/.dbsfs";

  private String peerId = null;

  private String pipeId = null;

  private String socketId = null;

  private JTextField txtPeerId = new JTextField();

  private JLabel lblPeerId = new JLabel();
  
  public FsPreferenceUI(Logger logger, FsTray fsTray, FileSyncClientMDI mdiWindow) {
    super(mdiWindow, true);
    try {
      fsPreferenceUI = this;
      this.mdiWindow = mdiWindow;
      this.mdiWindow.setFsPreferenceUI(this);
      this.fsTray = fsTray;
      this.setResizable(false);
      this.logger = logger;
      this.commonUtil = new CommonUtil(logger);
      jbInit();
      
      fillSessionList();

      fillRdvRelayList();
      
      fillUploadDownloadManager();
      
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void jbInit() throws Exception {

    panel4FsPreference.setLayout(new BorderLayout());
    
    panel4PreferenceDetail.setLayout(layOut4PreferenceDetails);
    
    restartJxtaActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          callUserLogin();
          if (!userLogin.isCanceled()) {
            
            FsPreferences fsPreferences = readPreferencesFromDisk();
            txaLog.setText("");
            prepareSession(fsPreferences);

            enableControls(false);
            
            ArrayList rdvList = fsPreferences.getRdvList();
            ArrayList relayList=fsPreferences.getRelayList();
            
            if(rdvList==null){
              rdvList=new ArrayList();
            }
            
            if(relayList==null){
              relayList=new ArrayList();
            }
            FsClient fsClientLocal = new FsClient(userCache,fsUser);
            logger.debug("Instantiating FsClient");
            fsClientLocal.launchClient(new ConnectionListener(), rdvList, relayList, socketId);
            connectionProgressBar.setVisible(true);
            btnConnect.setEnabled(false);
          }
        }
      }
    ;

    restartPipeConnectionActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          callUserLogin();
          if (!userLogin.isCanceled()) {
            txaLog.setText("");
            prepareSession(readPreferencesFromDisk());
            btnConnect.setEnabled(false);
            connectionProgressBar.setVisible(true);

            Thread jxtaPipeConnectThread = new Thread(new Runnable() {
                                                        public void run() {
                                                          fsClient.closePipe();
                                                          fsClient.connect(peerId, pipeId);
                                                        }
                                                      }
              );
            jxtaPipeConnectThread.start();
          }
        }
      }
    ;
    
    
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(null);
    renderer.setClosedIcon(null);
    renderer.setOpenIcon(null);
    
    DefaultMutableTreeNode top = new DefaultMutableTreeNode("  Preferences");
    createNodes(top);
    tree4Preferences = new JTree(top);
    tree4Preferences.setCellRenderer(renderer);
    tree4Preferences.setBorder(BorderFactory.createEmptyBorder());
    tree4Preferences.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree4Preferences.addTreeSelectionListener(new TreeSelectionListener() {
                                      public void valueChanged(TreeSelectionEvent e) {
                                        treeRemoteTreeView_valueChanged();
                                      }
                                    });
    
    scrollPane4Tree4Preference = new JScrollPane(tree4Preferences);
    scrollPane4Tree4Preference.setBorder(new TitledBorder(new EtchedBorder(), " Preferences "));
    scrollPane4Tree4Preference.setSize(200,400);
    
    lstSession.setFont(new FsFont());
    lstSession.setName("Session List");
    lstSession.setToolTipText("Session List");
    lstSession.setVisible(true);
    lstSession.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    lstSession.setSelectedIndex(0);
    lstSession.setBorder(BorderFactory.createEmptyBorder());

    scrollPane4SessionList = new JScrollPane(lstSession);
    scrollPane4SessionList.setSize(new Dimension(300, 200));
    scrollPane4SessionList.setPreferredSize(new Dimension(300, 200));
    scrollPane4SessionList.setMaximumSize(new Dimension(300, 200));
    scrollPane4SessionList.setMinimumSize(new Dimension(300, 200));
    scrollPane4SessionList.setBorder(BorderFactory.createEtchedBorder());
    
    btnNewSession.setFont(new FsFont());
    btnNewSession.setText("New");
    btnNewSession.setBorder(BorderFactory.createEtchedBorder());
    btnNewSession.setMargin(new Insets(0, 0, 0, 0));
    btnNewSession.setSize(buttonSize);
    btnNewSession.setPreferredSize(buttonSize);
    btnNewSession.setMaximumSize(buttonSize);
    btnNewSession.setMinimumSize(buttonSize);
    btnNewSession.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                           panel4SessionDetail
                                           .setBorder(new TitledBorder(new EtchedBorder(), "Create New  Session "));
                                           createNewSession();
                                           isEdit = false;
                                         }
                                       }
    );

    btnEditSession.setFont(new FsFont());
    btnEditSession.setText("Edit");
    btnEditSession.setMargin(new Insets(0, 0, 0, 0));
    btnEditSession.setBorder(BorderFactory.createEtchedBorder());
    btnEditSession.setSize(buttonSize);
    btnEditSession.setPreferredSize(buttonSize);
    btnEditSession.setMaximumSize(buttonSize);
    btnEditSession.setMinimumSize(buttonSize);
    btnEditSession.addActionListener(new ActionListener() {
                                          public void actionPerformed(ActionEvent e) {
                                            if (lstSession.getSelectedValue() == "<Default>") {
                                              logger.debug("Default is selected");
                                              return;
                                            }

                                            panel4SessionDetail
                                            .setBorder(new TitledBorder(new EtchedBorder(), "Edit Session "));
                                            createNewSession();
                                            editSessionDetail();
                                            isEdit = true;
                                          }
                                        }
    );


    btnCopySession.setFont(new FsFont());
    btnCopySession.setText("Copy");
    btnCopySession.setMargin(new Insets(0, 0, 0, 0));
    btnCopySession.setBorder(BorderFactory.createEtchedBorder());
    btnCopySession.setSize(buttonSize);
    btnCopySession.setPreferredSize(buttonSize);
    btnCopySession.setMaximumSize(buttonSize);
    btnCopySession.setMinimumSize(buttonSize);
    btnCopySession.addActionListener(new ActionListener() {
                                          public void actionPerformed(ActionEvent e) {
                                            copySession();
                                          }
                                        }
    );

    
    btnDeleteSession.setFont(new FsFont());
    btnDeleteSession.setText("Delete");
    btnDeleteSession.setMargin(new Insets(0, 0, 0, 0));
    btnDeleteSession.setBorder(BorderFactory.createEtchedBorder());
    btnDeleteSession.setSize(buttonSize);
    btnDeleteSession.setPreferredSize(buttonSize);
    btnDeleteSession.setMaximumSize(buttonSize);
    btnDeleteSession.setMinimumSize(buttonSize);
    btnDeleteSession.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                              deleteSession();
                                            }
                                          }
    );
    
    panel4SessionButtons.setLayout(new FlowLayout());
    panel4SessionButtons.setSize(new Dimension(80,200));
    panel4SessionButtons.setPreferredSize(new Dimension(80,200));
    panel4SessionButtons.setMaximumSize(new Dimension(80,200));
    panel4SessionButtons.setMinimumSize(new Dimension(80,200));
    panel4SessionButtons.add(btnNewSession);
    panel4SessionButtons.add(btnEditSession);
    panel4SessionButtons.add(btnDeleteSession);
    panel4SessionButtons.add(btnCopySession);
    
    panel4Session.setLayout(new BorderLayout());
    panel4Session.add(panel4SessionButtons, BorderLayout.EAST);
    panel4Session.add(scrollPane4SessionList, BorderLayout.CENTER);
        
    btnConnect.setFont(new FsFont());
    btnConnect.setText("Connect");
    btnConnect.setMargin(new Insets(0, 0, 0, 0));
    btnConnect.setBorder(BorderFactory.createEtchedBorder());
    btnConnect.setPreferredSize(new Dimension(64, 25));
    btnConnect.setSize(new Dimension(50, 25));
    btnConnect.setMaximumSize(new Dimension(52, 25));
    btnConnect.setHorizontalTextPosition(SwingConstants.CENTER);
    btnConnect.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnConnect.setToolTipText("Connect the Session");
    btnConnect.setOpaque(false);
    btnConnect.setBounds(new Rectangle(250, 15, 60, 25));
    btnConnect.addActionListener(restartJxtaActionListener);

    btnCancel.setFont(new FsFont());
    btnCancel.setText("Cancel");
    btnCancel.setMargin(new Insets(0, 0, 0, 0));
    btnCancel.setBorder(BorderFactory.createEtchedBorder());
    btnCancel.setPreferredSize(new Dimension(64, 25));
    btnCancel.setSize(new Dimension(50, 25));
    btnCancel.setMaximumSize(new Dimension(52, 25));
    btnCancel.setHorizontalTextPosition(SwingConstants.CENTER);
    btnCancel.setVerticalTextPosition(SwingConstants.BOTTOM);
    btnCancel.setToolTipText("Cancel Session");
    btnCancel.setOpaque(false);
    btnCancel.setBounds(new Rectangle(100, 15, 50, 25));
    btnCancel.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent evt) {
                                    closeFsPreferenceUI();
                                  }
                                }
    );
    
    panel4ConnectionButtons.setLayout(new FlowLayout());
    panel4ConnectionButtons.add(btnConnect, null);
    panel4ConnectionButtons.add(btnCancel, null);
    
    txaLog.setEditable(false);
    txaLog.setBorder(BorderFactory.createEmptyBorder());
    scrollPane4TxaLog = new JScrollPane(txaLog);
    scrollPane4TxaLog.setBorder(BorderFactory.createEtchedBorder());
    scrollPane4TxaLog.setSize(new Dimension(400, 80));
    scrollPane4TxaLog.setPreferredSize(new Dimension(400, 80));
    scrollPane4TxaLog.setMinimumSize(new Dimension(400, 80));
    scrollPane4TxaLog.setMaximumSize(new Dimension(400, 80));
    
    connectionProgressBar = new JProgressBar();
    connectionProgressBar.setIndeterminate(true);
    connectionProgressBar.setBorder(BorderFactory.createEtchedBorder());
    connectionProgressBar.setVisible(false);
    
    panel4ConnectionProgressBar.setLayout(new BorderLayout());
    panel4ConnectionProgressBar.add(connectionProgressBar, BorderLayout.CENTER);

    panel4Connection.setLayout(new BorderLayout());
    panel4Connection.setBounds(new Rectangle(5, 230, 400, 150));
    panel4Connection.setBorder(BorderFactory.createTitledBorder("Connect"));
    panel4Connection.add(panel4ConnectionButtons, BorderLayout.NORTH);
    panel4Connection.add(scrollPane4TxaLog, BorderLayout.CENTER);
    panel4Connection.add(panel4ConnectionProgressBar, BorderLayout.SOUTH);

    panel4SessionList.setLayout(new BorderLayout());
    panel4SessionList.setBorder(new TitledBorder(new EtchedBorder(), " Session List "));
    panel4SessionList.add(panel4Session, BorderLayout.CENTER);
    panel4SessionList.add(panel4Connection, BorderLayout.SOUTH);

    
    lblSessionName.setFont(new FsFont());
    lblSessionName.setText("Name");
    lblSessionName.setBounds(new Rectangle(0, 30, 65, 20));
    lblSessionName.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtSessionName.setBounds(new Rectangle(75, 30, 140, 25));
    txtSessionName.setBorder(BorderFactory.createEtchedBorder());
    
    
    lblPeerId.setFont(new FsFont());
    lblPeerId.setText("Peer ID");
    lblPeerId.setBounds(new Rectangle(0, 70, 65, 20));
    lblPeerId.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtPeerId.setBounds(new Rectangle(75, 65, 305, 25));
    txtPeerId.setBorder(BorderFactory.createEtchedBorder());
    
    lblPipeId.setFont(new FsFont());
    lblPipeId.setText("Pipe ID");
    lblPipeId.setBounds(new Rectangle(0, 105, 65, 20));
    lblPipeId.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtPipeId.setBounds(new Rectangle(75, 100, 305, 25));
    txtPipeId.setBorder(BorderFactory.createEtchedBorder());

    lblSocketId.setFont(new FsFont());
    lblSocketId.setText("Socket ID");
    lblSocketId.setBounds(new Rectangle(0, 140, 65, 20));
    lblSocketId.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtSocketId.setBounds(new Rectangle(75, 135, 305, 25));
    txtSocketId.setBorder(BorderFactory.createEtchedBorder());
    txtSocketId.setEnabled(true);

    lblUserId.setFont(new FsFont());
    lblUserId.setText("User ID");
    lblUserId.setBounds(new Rectangle(0, 175, 65, 20));
    lblUserId.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtUserId.setBounds(new Rectangle(75, 170, 145, 25));
    txtUserId.setBorder(BorderFactory.createEtchedBorder());
    
    lblPassword.setFont(new FsFont());
    lblPassword.setText("Password");
    lblPassword.setBounds(new Rectangle(0, 205, 65, 20));
    lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtPassword.setBounds(new Rectangle(75, 205, 145, 25));
    txtPassword.setBorder(BorderFactory.createEtchedBorder());
    
    btnSaveSession.setFont(new FsFont());
    btnSaveSession.setText("Save");
    btnSaveSession.setBorder(BorderFactory.createEtchedBorder());
    btnSaveSession.setBounds(new Rectangle(230, 330, 75, 25));
    btnSaveSession.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               saveSession();
                                             }
                                           }
    );
    
    btnCancelSession.setFont(new FsFont());
    btnCancelSession.setText("Cancel");
    btnCancelSession.setBorder(BorderFactory.createEtchedBorder());
    btnCancelSession.setBounds(new Rectangle(310, 330, 75, 25));
    btnCancelSession.addActionListener(new ActionListener() {
                                               public void actionPerformed(ActionEvent e) {
                                                 clearSessionDetailsFields();
                                                 forSession();
                                               }
                                             }
    );

    panel4SessionDetail.setLayout(null);

    panel4SessionDetail.add(txtSessionName, null);
    panel4SessionDetail.add(lblSessionName, null);
    panel4SessionDetail.add(lblPeerId, null);
    panel4SessionDetail.add(txtPeerId, null);
    panel4SessionDetail.add(lblPipeId, null);
    panel4SessionDetail.add(txtPipeId, null);
    panel4SessionDetail.add(lblSocketId, null);
    panel4SessionDetail.add(txtSocketId, null);
    panel4SessionDetail.add(lblUserId, null);
    panel4SessionDetail.add(txtUserId, null);
    panel4SessionDetail.add(lblPassword, null);
    panel4SessionDetail.add(txtPassword, null);


    panel4SessionDetail.add(btnSaveSession, null);
    panel4SessionDetail.add(btnCancelSession, null);
    lblRdv.setFont(new FsFont());
    lblRdv.setText("Rendezvous");
    lblRdv.setBounds(new Rectangle(5, 35, 80, 20));
    lblRdv.setHorizontalTextPosition(SwingConstants.LEFT);
    lblRdv.setHorizontalAlignment(SwingConstants.RIGHT);

    txtRdv.setBounds(new Rectangle(90, 30, 255, 25));
    txtRdv.setBorder(BorderFactory.createEtchedBorder());
    
    lstRdv.setFont(new FsFont());
    lstRdv.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    scrollPane4RdvList=new JScrollPane(lstRdv);
    scrollPane4RdvList.setBorder(BorderFactory.createEtchedBorder());
    scrollPane4RdvList.setBounds(new Rectangle(90, 70, 255, 80));

    btnAddRdv.setText("+");
    btnAddRdv.setBounds(new Rectangle(355, 30, 30, 30));
    btnAddRdv.setBorder(BorderFactory.createEtchedBorder());
    btnAddRdv.setSize(new Dimension(30, 30));
    btnAddRdv.setPreferredSize(new Dimension(30, 30));
    btnAddRdv.setMaximumSize(new Dimension(30, 30));
    btnAddRdv.setMinimumSize(new Dimension(30, 30));
    btnAddRdv.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e) {
                                       addRdv();
                                     }
                                   });
    
    btnDeleteRdv.setText("-");
    btnDeleteRdv.setBounds(new Rectangle(355, 70, 30, 30));
    btnDeleteRdv.setBorder(BorderFactory.createEtchedBorder());
    btnDeleteRdv.setSize(new Dimension(30, 30));
    btnDeleteRdv.setPreferredSize(new Dimension(30, 30));
    btnDeleteRdv.setMaximumSize(new Dimension(30, 30));
    btnDeleteRdv.setMinimumSize(new Dimension(30, 30));
    btnDeleteRdv.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                          deleteRdv();
                                        }
                                      });
                                      
    lblRelay.setFont(new FsFont());
    lblRelay.setText("Relay ");
    lblRelay.setBounds(new Rectangle(5, 165, 80, 20));
    lblRelay.setHorizontalTextPosition(SwingConstants.LEFT);
    lblRelay.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtRelay.setBounds(new Rectangle(90, 160, 255, 25));
    txtRelay.setBorder(BorderFactory.createEtchedBorder());
    
    lstRelay.setFont(new FsFont());
    lstRelay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    scrollPane4RelayList=new JScrollPane(lstRelay);
    scrollPane4RelayList.setBorder(BorderFactory.createEtchedBorder());
    scrollPane4RelayList.setBounds(new Rectangle(90, 200, 255, 80));
    
    btnAddRelay.setText("+");
    btnAddRelay.setBounds(new Rectangle(355, 160, 30, 30));
    btnAddRelay.setBorder(BorderFactory.createEtchedBorder());
    btnAddRelay.setSize(new Dimension(30, 30));
    btnAddRelay.setPreferredSize(new Dimension(30, 30));
    btnAddRelay.setMaximumSize(new Dimension(30, 30));
    btnAddRelay.setMinimumSize(new Dimension(30, 30));
    btnAddRelay.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                         addRelay();
                                       }
                                     });
    
    btnDeleteRelay.setText("-");
    btnDeleteRelay.setBounds(new Rectangle(355, 200, 30, 30));
    btnDeleteRelay.setBorder(BorderFactory.createEtchedBorder());
    btnDeleteRelay.setSize(new Dimension(30, 30));
    btnDeleteRelay.setPreferredSize(new Dimension(30, 30));
    btnDeleteRelay.setMaximumSize(new Dimension(30, 30));
    btnDeleteRelay.setMinimumSize(new Dimension(30, 30));
    btnDeleteRelay.addActionListener(new ActionListener() {
                                          public void actionPerformed(ActionEvent e) {
                                            deleteRelay();
                                          }
                                        }
    );
    
    btnSaveRdvRelay.setFont(new FsFont());
    btnSaveRdvRelay.setText("Save");
    btnSaveRdvRelay.setBorder(BorderFactory.createEtchedBorder());
    btnSaveRdvRelay.setBounds(new Rectangle(230, 330, 75, 25));
    btnSaveRdvRelay.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                           saveRdvRelaySettings();
                                         }
                                       }
    );

    btnCancelRdvRelay.setFont(new FsFont());
    btnCancelRdvRelay.setText("Cancel");
    btnCancelRdvRelay.setBorder(BorderFactory.createEtchedBorder());
    btnCancelRdvRelay.setBounds(new Rectangle(310, 330, 75, 25));
    btnCancelRdvRelay.addActionListener(new ActionListener() {
                                          public void actionPerformed(ActionEvent e) {
                                            cancelRdvRelaySettings();
                                          }
                                        }
    );
    
    panel4RdvRelay.setLayout(null);
    panel4RdvRelay.setBorder(new TitledBorder(new EtchedBorder(), "Rendezvous & Relay Server Settings"));
    
    panel4RdvRelay.add(lblRdv, null);
    panel4RdvRelay.add(txtRdv, null);
    panel4RdvRelay.add(btnAddRdv, null);
    panel4RdvRelay.add(btnDeleteRdv, null);
    panel4RdvRelay.add(scrollPane4RdvList, null);
    
    panel4RdvRelay.add(lblRelay, null);
    panel4RdvRelay.add(txtRelay, null);
    panel4RdvRelay.add(btnAddRelay, null);
    panel4RdvRelay.add(btnDeleteRelay, null);
    panel4RdvRelay.add(scrollPane4RelayList, null);


    panel4RdvRelay.add(btnSaveRdvRelay, null);
    panel4RdvRelay.add(btnCancelRdvRelay, null);
    
    chkShowUploadDownLoadMgr.setFont(new FsFont());
    chkShowUploadDownLoadMgr.setText("Show when an upload/download begins");
    chkShowUploadDownLoadMgr.setBorder(BorderFactory.createEtchedBorder());
    chkShowUploadDownLoadMgr.setBounds(new Rectangle(20, 30, 355, 20));
    chkShowUploadDownLoadMgr.setSelected(true);
    
    chkCloseUploadDownloadMgr.setFont(new FsFont());
    chkCloseUploadDownloadMgr.setText("Close when all downloads/uploads are completed");
    chkCloseUploadDownloadMgr.setBorder(BorderFactory.createEtchedBorder());
    chkCloseUploadDownloadMgr.setBounds(new Rectangle(20, 60, 355, 20));
    
   
    btnSaveUploadDownloadMgr.setFont(new FsFont());
    btnSaveUploadDownloadMgr.setText("Save");
    btnSaveUploadDownloadMgr.setBorder(BorderFactory.createEtchedBorder());
    btnSaveUploadDownloadMgr.setBounds(new Rectangle(230, 330, 75, 25));
    btnSaveUploadDownloadMgr.addActionListener(new ActionListener() {
                                 public void actionPerformed(ActionEvent e) {
                                   saveUploadDownloadManager();
                                 }
                               }
    );

    btnCancelUploaDownloadMgr.setFont(new FsFont());
    btnCancelUploaDownloadMgr.setText("Cancel");
    btnCancelUploaDownloadMgr.setBorder(BorderFactory.createEtchedBorder());
    btnCancelUploaDownloadMgr.setBounds(new Rectangle(310, 330, 75, 25));
    btnCancelUploaDownloadMgr.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                     cancelUploadDownloadManager();
                                   }
                                 }
    );
    
    
    panel4UploadDownload.setLayout(null);
    panel4UploadDownload.setBorder(new TitledBorder(new EtchedBorder(), "Upload & Download Manager Settings"));
    
    panel4UploadDownload.add(chkCloseUploadDownloadMgr, null);
    panel4UploadDownload.add(chkShowUploadDownLoadMgr, null);


    panel4UploadDownload.add(btnSaveUploadDownloadMgr, null);
    panel4UploadDownload.add(btnCancelUploaDownloadMgr, null);
    panel4PreferenceDetail.add(panel4SessionList, "panel4SessionList");
    panel4PreferenceDetail.add(panel4SessionDetail, "panel4SessionDetail");
    panel4PreferenceDetail.add(panel4RdvRelay, "panel4RdvRelay");
    panel4PreferenceDetail.add(panel4UploadDownload, "panel4UploadDownload");
    
    panel4FsPreference.add(scrollPane4Tree4Preference, BorderLayout.WEST);
    panel4FsPreference.add(panel4PreferenceDetail, BorderLayout.CENTER);

    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(panel4FsPreference, BorderLayout.CENTER);
    this.setTitle("DBSentry File Synchronizer Session");
    this.setSize(new Dimension(600,400));
    this.addWindowListener(new WindowAdapter() {
                             public void windowClosing(WindowEvent we) {
                               closeFsPreferenceUI();
                             }

                           }
    );
  }
  
  private void prepareSession(FsPreferences fsPreferences) {

    if (lstSession.getSelectedIndex() != 0) {
      
      FsSession value = (FsSession)fsPreferences.getSessionList().get(lstSession.getSelectedValue());
      logger.debug("Inside if current session .....");

      currentSessionName = value.getSessionName();

      peerId = value.getPeerId();
      pipeId = value.getPipeId();
      socketId = value.getSocketId();

    } else {
      try {

        currentSessionName = "Default";

        //Reading Peer Id

        FileInputStream fis = null;
        File fileJxtaPeerConfig = new File("config/jxta_peer_config.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        builder = factory.newDocumentBuilder();

        fis = new FileInputStream(fileJxtaPeerConfig);

        Document peerConfigDocument = builder.parse(fis);

        NodeList nodeList4PeerId = peerConfigDocument.getElementsByTagName("jxta-peer-property");
        int nodeListLength = nodeList4PeerId.getLength();
        if (nodeListLength != 0) {
          NamedNodeMap namedNodeMap = nodeList4PeerId.item(0).getAttributes();
          peerId = namedNodeMap.getNamedItem("peerid").getNodeValue().trim();
        } else {
          logger.error("jxta-peer-property tag missing in ");
          System.exit(-1);
        }

        //Reading Pipe Id

        File pipeAdvFile = new File("config/pipe.adv");

        Document pipeDocument = commonUtil.getDocumentFromFile(pipeAdvFile);

        NodeList nodeList4PipeId = pipeDocument.getDocumentElement().getChildNodes();
        if (nodeList4PipeId != null) {
          for (int i = 0; i < nodeList4PipeId.getLength(); i++) {
            if (nodeList4PipeId.item(i).getNodeName().equals("Id")) {
              Node node = nodeList4PipeId.item(i).getFirstChild();
              pipeId = node.getNodeValue().trim();
            }
          }
        }

        //Reading Socket Id

        File socketAdvFile = new File("config/socket.adv");

        Document socketDocument = commonUtil.getDocumentFromFile(socketAdvFile);

        NodeList nodeList4SocketId = socketDocument.getDocumentElement().getChildNodes();
        if (nodeList4PipeId != null) {
          for (int i = 0; i < nodeList4SocketId.getLength(); i++) {
            if (nodeList4SocketId.item(i).getNodeName().equals("Id")) {
              Node node = nodeList4SocketId.item(i).getFirstChild();
              socketId = node.getNodeValue().trim();
            }
          }
        }


      } catch (Exception ex) {
        ex.printStackTrace();
      }


    }

    logger.debug("currentSessionName " + currentSessionName);
    logger.debug("peerId " + peerId);
    logger.debug("pipeId " + pipeId);
    logger.debug("socketId " + socketId);

  }

  private void callUserLogin() {

    if (fsUser == null) {
      fsUser = new FsUser();
    }

    if (userLogin == null) {
      userLogin = new UserLogin(logger, fsPreferenceUI);
    }
    //userLogin.reset();

    if (lstSession.getSelectedIndex() == 0) {
      userLogin.setVisible(true);
      fsUser.setUserId(userLogin.getUserId().trim());
      fsUser.setUserPassword(userLogin.getPassword().trim());

    } else {
      FsSession value = (FsSession)readPreferencesFromDisk().getSessionList().get(lstSession.getSelectedValue());
      if (value.getUserId().equals("") || value.getPassword().equals("")) {
        userLogin.setVisible(true);
        fsUser.setUserId(userLogin.getUserId().trim());
        fsUser.setUserPassword(userLogin.getPassword().trim());
      } else {
        fsUser.setUserId(value.getUserId().trim());
        fsUser.setUserPassword(value.getPassword().trim());
      }

    }

  }

  private void closeFsPreferenceUI() {
    if (fsTray.isTrayLaunched()) {
      fsPreferenceUI.setVisible(false);
    } else {
      System.exit(0);
    }
  }

  private void createNodes(DefaultMutableTreeNode top) {
    DefaultMutableTreeNode category = null;
    category = new DefaultMutableTreeNode("Session");
    top.add(category);

    category = new DefaultMutableTreeNode("Rdv/Relay");
    top.add(category);

    category = new DefaultMutableTreeNode("Up/Download Manager");
    top.add(category);
  }
  
  private FsPreferences readPreferencesFromDisk(){
    
    FsPreferences fsPreferences=null;
    File preferencesFile = new File(userCache + File.separator + "preferences" + File.separator + "preferences.fs");
    
    logger.debug("preferences File : " + preferencesFile.toString());
    
    if(!preferencesFile.exists()){
      fsPreferences = new FsPreferences();
      
    }else{
      
      FileInputStream fis = null;
      ObjectInputStream ois = null;
      
      try {
        fis = new FileInputStream(preferencesFile);
        ois = new ObjectInputStream(fis);
        fsPreferences = (FsPreferences)ois.readObject();
      }
      catch (FileNotFoundException e) {
        ;
      }
      catch (IOException e) {
        ;
      }
      catch (ClassNotFoundException e) {
        ;
      }
      finally {
        try {
          if (fis != null) {
            fis.close();
          }
          if (ois != null) {
            ois.close();
          }
        }
        catch (IOException e) {
          ;
        }
      }
    }
    
    return fsPreferences;
  
  }
  
  private void writePreferencesToDisk(FsPreferences fsPreferences){
    
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    
    File preferencesfile = new File(userCache + File.separator + "preferences" + File.separator + "preferences.fs");
    if(preferencesfile.exists()){
      preferencesfile.delete();
    }
    try {
      fos = new FileOutputStream(preferencesfile);
      oos = new ObjectOutputStream(fos);
      oos.writeObject(fsPreferences);
      oos.flush();
    }
    catch (FileNotFoundException e) {
      ;
    }
    catch (IOException e) {
      ;
    }
    finally {
      try {
        if (fos != null) {
          fos.close();
        }
        if (oos != null) {
          oos.close();
        }
      }
      catch (IOException e) {
        ;
      }
    }
  
  }

  private void treeRemoteTreeView_valueChanged() {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree4Preferences.getLastSelectedPathComponent();

    if (node == null) {
      return;
    }
    Object nodeInfo = node.getUserObject();
    logger.debug("node Info " + nodeInfo);
    if (nodeInfo == "Session") {
      logger.debug("for session display");
      forSession();
    } else if (nodeInfo == "Up/Download Manager") {
      logger.debug("Up/Download Manager display");
      forUp_Download();
    } else if (nodeInfo == "Rdv/Relay") {
      logger.debug("Rdv/Relayr display");
      forRdvRelay();
    }
  }

  private void forSession() {
    layOut4PreferenceDetails.show(panel4PreferenceDetail, "panel4SessionList");
  }

  private void forUp_Download() {
    layOut4PreferenceDetails.show(panel4PreferenceDetail, "panel4UploadDownload");
  }

  private void forRdvRelay() {
    layOut4PreferenceDetails.show(panel4PreferenceDetail, "panel4RdvRelay");
  }

  private void createNewSession() {
    layOut4PreferenceDetails.show(panel4PreferenceDetail, "panel4SessionDetail");
  }

  
  private void saveRdvRelaySettings() {
    ArrayList rdvListLocal = new ArrayList();
    ArrayList relayListLocal = new ArrayList();
    
    DefaultListModel listModelRdv = (DefaultListModel)lstRdv.getModel();
    DefaultListModel listModelRelay = (DefaultListModel)lstRelay.getModel();

    
    try {
      
      if (listModelRdv.getSize()!=-1) {
        for(int indexRdv = 0; indexRdv < listModelRdv.getSize(); indexRdv++){
          rdvListLocal.add(new URI((String)listModelRdv.getElementAt(indexRdv)));
          logger.debug("List of rdvListLocal......"+rdvListLocal);
        }
      }
      
      if( listModelRelay.getSize() != -1){
        for(int indexRelay = 0; indexRelay < listModelRelay.getSize(); indexRelay++){
          relayListLocal.add(new URI((String)listModelRelay.getElementAt(indexRelay)));
          logger.debug("List of relayListLocal......"+relayListLocal);
        }
      }
      
      
    }
    catch (URISyntaxException e) {
      e.printStackTrace();
    }
    
    FsPreferences fsPreferences= readPreferencesFromDisk();
    
    fsPreferences.setRdvList(rdvListLocal);
    fsPreferences.setRelayList(relayListLocal);
    
    writePreferencesToDisk(fsPreferences);
    
    layOut4PreferenceDetails.show(panel4PreferenceDetail, "panel4SessionList");
  }

  private void cancelRdvRelaySettings() {
    layOut4PreferenceDetails.show(panel4PreferenceDetail, "panel4SessionList");
  }

  public void disableOptionWhileShowPreferences() {
    btnNewSession.setEnabled(false);
    btnEditSession.setEnabled(false);
    btnCopySession.setEnabled(false);
    btnDeleteSession.setEnabled(false);
    tree4Preferences.setEnabled(false);
    layOut4PreferenceDetails.show(panel4PreferenceDetail, "panel4UploadDownload");
  }


  public void enableControls(boolean enable) {
    btnNewSession.setEnabled(enable);
    btnEditSession.setEnabled(enable);
    btnCopySession.setEnabled(enable);
    btnDeleteSession.setEnabled(enable);
    tree4Preferences.setEnabled(enable);
    lstSession.setEnabled(enable);
    connectionProgressBar.setVisible(false);
  }


  private void saveSession() {
    if (txtSessionName.getText().trim().equals("")) {
      JOptionPane.showMessageDialog(this, "Enter Session name");
      txtSessionName.requestFocus();
      return;
    }

    if (txtPipeId.getText().trim().equals("")) {
      JOptionPane.showMessageDialog(this, "Enter Pipe Id");
      txtPipeId.requestFocus();
      return;
    }

//Don't Remove this Commented Lines.
    //    if (socketIDTextField.getText().trim().equals("")) {
    //      JOptionPane.showMessageDialog(this, "Enter Socket Id");
    //      socketIDTextField.requestFocus();
    //      return;
    //    }

    //Check for unique session name
    
    sessionName = txtSessionName.getText().trim();
    DefaultListModel listModelSession=(DefaultListModel)lstSession.getModel();
    if(!isEdit){
      for(int index=0; index<listModelSession.getSize();index++){
        if (lstSession.getSelectedIndex()!=index ){
          if(listModelSession.getElementAt(index).equals(sessionName)){
            JOptionPane.showMessageDialog(this, "This Session Name Already Exist");
            txtSessionName.setText("");
            txtSessionName.requestFocus();
            return;
          }
        }
      }
    }
    
    FsPreferences fsPreferences = readPreferencesFromDisk();
    
    Hashtable htSessionList =fsPreferences.getSessionList();
    
    if(htSessionList==null){
      htSessionList= new Hashtable();
    }
    
    FsSession fsSession = new FsSession();
    
    fsSession.setSessionName(txtSessionName.getText().trim());
    fsSession.setPeerId(txtPeerId.getText().trim());
    fsSession.setPipeId(txtPipeId.getText().trim());
    fsSession.setSocketId(txtSocketId.getText().trim());
    fsSession.setUserId((txtUserId.getText().trim()));
    fsSession.setPassword(String.valueOf(txtPassword.getPassword()));
    
    htSessionList.put(sessionName, fsSession);
    
    fsPreferences.setSessionList(htSessionList);
    
    writePreferencesToDisk(fsPreferences);
    
    layOut4PreferenceDetails.show(panel4PreferenceDetail, "panel4SessionList");
    
    clearSessionDetailsFields();

    fillSessionList();
  }


  private void fillSessionList() {
    try {
        
      FsPreferences fsPreferences = readPreferencesFromDisk();
      logger.debug("fsPreferences:...." + fsPreferences);
      
      DefaultListModel listModelSession = new DefaultListModel();
      
      listModelSession.addElement("<Default>");
      
      Hashtable htSession = fsPreferences.getSessionList();
    
      if (htSession != null) {
        Enumeration enumSessionKeys = htSession.keys();
        String key;
        logger.debug("Keys:  " + enumSessionKeys);
        while (enumSessionKeys.hasMoreElements()) {
          key = (String)enumSessionKeys.nextElement();
          FsSession value = (FsSession)htSession.get(key);
          listModelSession.addElement(value.getSessionName());
        }
      }
      
      lstSession.setModel(listModelSession);
      lstSession.setSelectedIndex(listModelSession.size()-1);
      
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void clearSessionDetailsFields() {
    txtSessionName.setText("");
    txtPeerId.setText("");
    txtPipeId.setText("");
    txtSocketId.setText("");
    txtUserId.setText("");
    txtPassword.setText("");
  }

  public void editSessionDetail() {
    FsSession value = (FsSession)readPreferencesFromDisk().getSessionList().get(lstSession.getSelectedValue());
    logger.debug("lstSession.getSelectedValue()" + lstSession.getSelectedValue());
    txtSessionName.setText(value.getSessionName());
    txtSocketId.setText(value.getSocketId());
    txtPeerId.setText(value.getPeerId());
    txtPipeId.setText(value.getPipeId());
    txtUserId.setText(value.getUserId());
    txtPassword.setText(String.valueOf(value.getPassword()));
  }


  private void addRdv() {
    int indexRdv=1;
    DefaultListModel listModelRdv = (DefaultListModel)lstRdv.getModel();
      if (listModelRdv.getSize()!=-1 ){
        for(; indexRdv < listModelRdv.getSize(); indexRdv++){
          if (!(txtRdv.getText().trim().equals(""))) {
            if(listModelRdv.getElementAt(indexRdv).equals(txtRdv.getText().trim())){
              logger.debug("Rdv Name alreay exist in the List......");
              JOptionPane.showMessageDialog(this, "Rdv Name alreay exist in the List......");
              txtRdv.requestFocus();
              return;
            }
          }
        }
       listModelRdv.addElement(txtRdv.getText().trim());
       lstRdv.setSelectedIndex(indexRdv);
       txtRdv.setText(""); 
     }

  }

  private void deleteRdv() {
    int lastIndex=lstRdv.getLastVisibleIndex();
    int selectedIndex = lstRdv.getSelectedIndex();
    DefaultListModel listModelRdv = (DefaultListModel)lstRdv.getModel();
    if (lstRdv.getSelectedIndex() != -1 && lstRdv.getSelectedIndex()!=0) {
      if(lstRdv.getSelectedIndex()==lastIndex){
        listModelRdv.remove(lstRdv.getSelectedIndex());
        lstRdv.setSelectedIndex(lstRdv.getLastVisibleIndex());
      }else{
      listModelRdv.remove(selectedIndex);
      lstRdv.setSelectedIndex(selectedIndex);
      }
    }
  }

  private void addRelay() {
    
    int indexRelay=1;
    DefaultListModel listModelRelay = (DefaultListModel)lstRelay.getModel();
    try {
      if (listModelRelay.getSize() != -1){
        for(; indexRelay < listModelRelay.getSize(); indexRelay++){
          if (!(txtRelay.getText().trim().equals(""))) {
            if(listModelRelay.getElementAt(indexRelay).equals(txtRelay.getText().trim())){
              logger.debug("Relay Name alreay exist in the List......");
              JOptionPane.showMessageDialog(this, "Relay Name alreay exist in the List......");
              txtRelay.requestFocus();
              return;
            }
          }
        }
        
        listModelRelay.addElement(txtRelay.getText().trim());
        lstRelay.setSelectedIndex(lstRelay.getLastVisibleIndex());
        txtRelay.setText("");
      }   
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void deleteRelay() {
      int lastIndex=lstRelay.getLastVisibleIndex();
      int selectedIndex = lstRelay.getSelectedIndex();
      DefaultListModel listModelRelay = (DefaultListModel)lstRelay.getModel();
      if (lstRelay.getSelectedIndex() != -1 && lstRelay.getSelectedIndex()!=0) {
        if(lastIndex==selectedIndex){
          listModelRelay.remove(lstRelay.getSelectedIndex());
          lstRelay.setSelectedIndex(lstRelay.getLastVisibleIndex());
        }else{
        listModelRelay.remove(selectedIndex);
        lstRelay.setSelectedIndex(selectedIndex);
        }
      }
  }

  private void deleteSession() {
    int lastIndex=lstSession.getLastVisibleIndex();
    int selectedIndex = lstSession.getSelectedIndex();
    FsPreferences fsPreferences=readPreferencesFromDisk();
    if(lstSession.getSelectedIndex() != -1 && lstSession.getSelectedIndex()!=0){
      if(lastIndex==selectedIndex){
        fsPreferences.getSessionList().remove(lstSession.getSelectedValue());
        ((DefaultListModel)lstSession.getModel()).remove(lstSession.getSelectedIndex());
        lstSession.setSelectedIndex(lstSession.getLastVisibleIndex());
      }else{
        fsPreferences.getSessionList().remove(selectedIndex);
        ((DefaultListModel)lstSession.getModel()).remove(lstSession.getSelectedIndex());
        lstSession.setSelectedIndex(selectedIndex);
      }
    fsPreferences.getSessionList().remove(lstSession.getSelectedValue());
    }
    writePreferencesToDisk(fsPreferences);
  }

  private void copySession() {
    
    sessionName=(String)lstSession.getSelectedValue();
    
    logger.debug("sessionName " + sessionName);
    
    FsPreferences fsPreferences=readPreferencesFromDisk();
    
    Hashtable htSession =fsPreferences.getSessionList();
    
    FsSession fsSession = (FsSession)(htSession.get(sessionName));
    
    FsSession copyOfFsSession = new FsSession();
    
    sessionName="Copy of " + sessionName;
    copyOfFsSession.setSessionName(sessionName);
    copyOfFsSession.setPipeId(fsSession.getPipeId());
    copyOfFsSession.setPeerId(fsSession.getPeerId());
    copyOfFsSession.setSocketId(fsSession.getSocketId());
    copyOfFsSession.setUserId(fsSession.getUserId());
    copyOfFsSession.setPassword(fsSession.getPassword());

    htSession.put(sessionName, copyOfFsSession);
    
    fsPreferences.setSessionList(htSession);
    
    writePreferencesToDisk(fsPreferences);
    
    fillSessionList();
    
  }


  private void fillRdvRelayList() {
    
    try {
    
      FsPreferences fsPreferences = readPreferencesFromDisk();
  
      logger.debug("fsPreferences:...." + fsPreferences);
      
      ArrayList rdvList = fsPreferences.getRdvList();
      ArrayList relayList = fsPreferences.getRelayList();
      
      DefaultListModel listModelRdv = new DefaultListModel();
      DefaultListModel listModelRelay = new DefaultListModel();
      
      
      
      if (rdvList!=null) {
        for(int index=0;index<rdvList.size();index++){
          listModelRdv.addElement(rdvList.get(index));
        }
      }else{
        listModelRdv.addElement("http://rdv.jxtahosts.net/cgi-bin/rendezvous.cgi?2");
      }
        
      lstRdv.setModel(listModelRdv);
      lstRdv.setSelectedIndex(0);
      
      
      
      
      if (relayList!=null) {
        for(int index=0;index<relayList.size();index++){
          listModelRelay.addElement(relayList.get(index));
        }
      }else{
        listModelRelay.addElement("http://rdv.jxtahosts.net/cgi-bin/relay.cgi?2");
      }
      
      lstRelay.setModel(listModelRelay);
      lstRelay.setSelectedIndex(0);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }
  
  private void  saveUploadDownloadManager(){
    FsPreferences fsPreferences = readPreferencesFromDisk();
    UploadDnloadManagerPref uploadDnloadManagerPref= new UploadDnloadManagerPref();
    uploadDnloadManagerPref.setShowUploadDnloadManager(chkShowUploadDownLoadMgr.isSelected());
    uploadDnloadManagerPref.setCloseUploadDnloadManager(chkCloseUploadDownloadMgr.isSelected());
    fsPreferences.setUploadDnloadManagerPref(uploadDnloadManagerPref);
    writePreferencesToDisk(fsPreferences);
    closeFsPreferenceUI();
  }
  
  private void fillUploadDownloadManager(){
    FsPreferences fsPreferences = readPreferencesFromDisk();
    UploadDnloadManagerPref uploadDnloadManagerPref = fsPreferences.getUploadDnloadManagerPref();
    if (uploadDnloadManagerPref != null) {
        logger.debug("uploadDnloadManagerPref.getShowUploadDnloadManager()..."+uploadDnloadManagerPref.getShowUploadDnloadManager());
        logger.debug("uploadDnloadManagerPref.getCloseUploadDnloadManager().."+uploadDnloadManagerPref.getCloseUploadDnloadManager());
        chkShowUploadDownLoadMgr.setSelected(uploadDnloadManagerPref.getShowUploadDnloadManager());
        chkCloseUploadDownloadMgr.setSelected(uploadDnloadManagerPref.getCloseUploadDnloadManager());
        
        mdiWindow.showUploadDnloadManager=uploadDnloadManagerPref.getShowUploadDnloadManager();
        mdiWindow.closeUploadDnloadManager=uploadDnloadManagerPref.getCloseUploadDnloadManager();
    }
  }
  
  private void  cancelUploadDownloadManager(){
    closeFsPreferenceUI();
  }

  public FsUser getFsUser() {
    return fsUser;
  }

  private class ConnectionListener implements FsConnectionListener {

    public void propertyChange(PropertyChangeEvent evt) {
      try {

        fsClient = (FsClient)evt.getSource();
        String propertyName = evt.getPropertyName();

        boolean propertyValue = ((Boolean)evt.getNewValue()).booleanValue();
        logger.debug("property " + propertyName + " " + propertyValue);
        if (propertyName.equals(Integer.toString(LOCATING_IN_LAN))) {
          if (propertyValue) {
            logger.info("Locating Server In LAN ");
            txaLog.append("Locating Server In LAN \n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());
          } else {
          }
        } else if (propertyName.equals(Integer.toString(EXIST_IN_LAN))) {
          if (propertyValue) {
            logger.info("Server Located In LAN ");
            txaLog.append("Server Located In LAN \n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());

            //create Bidipipe Here

            Thread jxtaPipeConnectThread = new Thread(new Runnable() {
                                                        public void run() {
                                                          fsClient.connect(peerId, pipeId);

                                                        }
                                                      }
              );
            jxtaPipeConnectThread.start();
          } else {
            logger.info("Unable Locate Server In LAN");
            txaLog.append("Unable Locate Server In LAN \n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());
          }
        } else if (propertyName.equals(Integer.toString(CONNECTING_TO_INTERNET))) {
          if (propertyValue) {
            logger.info("Connecting To Internet");
            txaLog.append("Connecting To Internet \n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());
          } else {
          }
        } else if (propertyName.equals(Integer.toString(IS_INTERNET_AVAILABLE))) {
          if (propertyValue) {
            logger.info("Internet is Available");
            txaLog.append("Internet is Available \n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());
          } else {
            logger.info("Internet is Not Available ");
            txaLog.append("Internet is Not Available \n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());

            btnConnect.removeActionListener(restartPipeConnectionActionListener);
            btnConnect.removeActionListener(restartJxtaActionListener);
            btnConnect.addActionListener(restartJxtaActionListener);
            enableControls(true);
          }
        } else if (propertyName.equals(Integer.toString(LOCATING_IN_INTERNET))) {
          if (propertyValue) {
            logger.info("Locating Server In Internet");
            txaLog.append("Locating Server In Internet\n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());
          } else {
          }
        } else if (propertyName.equals(Integer.toString(EXIST_IN_INTERNET))) {
          if (propertyValue) {
            logger.info("Server Located in Internet");
            txaLog.append("Server Located in Internet \n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());

            //create Bidipipe Here
            Thread jxtaPipeConnectThread = new Thread(new Runnable() {
                                                        public void run() {
                                                          fsClient.connect(peerId, pipeId);

                                                        }
                                                      }
              );
            jxtaPipeConnectThread.start();

          } else {
            logger.info("Unable To Locate Server in Internet");
            txaLog.append("Unable To Locate Server in Internet  \n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());

            btnConnect.removeActionListener(restartPipeConnectionActionListener);
            btnConnect.removeActionListener(restartJxtaActionListener);
            btnConnect.addActionListener(restartJxtaActionListener);
            btnConnect.setEnabled(true);
            btnConnect.setText("Retry");
            enableControls(true);

          }
        } else if (propertyName.equals(Integer.toString(CONNECTING))) {
          if (propertyValue) {
            logger.info("Connecting To Server ...");
            txaLog.append("Connecting To Server ...\n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());
          } else {
          }
        } else if (propertyName.equals(Integer.toString(CONNECTED))) {
          if (propertyValue) {
            logger.info("Connected  To Server");
            txaLog.append("Connected  To Server\n ");
            txaLog.append("Authenticating ......\n ");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());
            fsClient.authenticate(new AuthenticationListener(fsClient));
          } else {
            logger.info("Unable To Establish Connection  To Server ....");
            txaLog.append("Unable To Establish Connection  To Server .... \n");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());

            btnConnect.removeActionListener(restartPipeConnectionActionListener);
            btnConnect.removeActionListener(restartJxtaActionListener);

            btnConnect.addActionListener(restartPipeConnectionActionListener);
            btnConnect.setEnabled(true);
            btnConnect.setText("Retry");
            enableControls(true);

          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private class AuthenticationListener implements FsAuthenticationListener {
    private FsClient fsClient;

    public AuthenticationListener(FsClient fsClient) {
      this.fsClient = fsClient;
    }

    public void propertyChange(PropertyChangeEvent evt) {
      int propertyName = Integer.valueOf(evt.getPropertyName());
      FsResponse fsResponse;
      fsResponse = (FsResponse)evt.getNewValue();
      try {
        switch (propertyName) {
          case AUTHORISED :
            logger.info("Connected to the server");

            txaLog.append("Authenticated Successfully ......\n ");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());

            if (!fsTray.isTrayLaunched()) {
              fsTray.prepareTray(mdiWindow);
              fsTray.setTrayLaunched(true);
            }

            mdiWindow.setFsClient(fsClient);
            mdiWindow.getExplorer().launchRemoteView();
            mdiWindow.getExplorer().getFsRemoteView().setFsClient(fsClient);
            mdiWindow.getExplorer().getFsRemoteView().setRemoteCommandListener();
            mdiWindow.getExplorer().getFsLocalView().setFsClient(fsClient);
            fsClient.getRootOfFolder(FsRemoteOperationConstants.COMMAND);

            mdiWindow.setVisible(true);
            fsPreferenceUI.setVisible(false);

            btnConnect.removeActionListener(restartPipeConnectionActionListener);
            btnConnect.removeActionListener(restartJxtaActionListener);
            btnConnect.addActionListener(restartPipeConnectionActionListener);
            btnConnect.setText("Connect");
            btnConnect.setEnabled(true);
            enableControls(true);

            mdiWindow.setSessionName(currentSessionName);
            mdiWindow.fsStatusBar.setLblSetSesionName(currentSessionName);
            mdiWindow.fsStatusBar.setLblSetUserName(fsUser.getUserId());
            mdiWindow.enableMDIControls(true);
            mdiWindow.fsStatusBar.setLblSetMessage("");
            mdiWindow.fsStatusBar.setLblSetMessage("User Connected.......");
           // mdiWindow.tfStatus.setText("User Connected");
            break;
          case UNAUTHORISED :
            txaLog.append("Authentication Failed ......\n ");
            txaLog.setCaretPosition(txaLog.getDocument().getLength());

            FsExceptionHolder fsExceptionHolder = fsResponse.getFsExceptionHolder();
            logger.error(fsExceptionHolder.getErrorMessage());
            JOptionPane
            .showMessageDialog(fsPreferenceUI, "Invalid userid/password", "Login failed", JOptionPane.ERROR_MESSAGE);

            btnConnect.removeActionListener(restartPipeConnectionActionListener);
            btnConnect.removeActionListener(restartJxtaActionListener);
            btnConnect.addActionListener(restartPipeConnectionActionListener);
            btnConnect.setText("Connect");
            btnConnect.setEnabled(true);
            enableControls(true);
            break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
