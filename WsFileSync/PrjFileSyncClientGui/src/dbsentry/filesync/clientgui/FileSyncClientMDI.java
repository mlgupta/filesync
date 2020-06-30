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
  * $Id$
  *****************************************************************************
  */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.FsClient;
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.FsUser;
import dbsentry.filesync.common.listeners.FsDisconnectionListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import oracle.help.CSHManager;
import oracle.help.Help;
import oracle.help.library.helpset.HelpSet;
import oracle.help.navigator.Navigator;

import org.apache.log4j.Logger;


/**
 * @author Saurabh Gupta
 * @version 1.0
 * @Date of creation:02-05-2006
 * @Last Modfied by : Saurabh Gupta
 * @Last Modfied Date: 15-06-2006
 */
public class FileSyncClientMDI
  extends JFrame {

  private static int FRAME_OFFSET = 20;

  public static int cursorCounterRemote = 0;

  public static int cursorCounterLocal = 0;

  private Logger logger;

  private Navigator[] navigator;

  private Help helpObject;

  private CSHManager manager;

  public Boolean flag = false;

  private BorderLayout layoutMain = new BorderLayout();

  private JDesktopPane panelCenter = new JDesktopPane();

  private JMenuBar menuBar = new JMenuBar();

  private JMenu menuFile = new JMenu();

  public JMenu menuEdit = new JMenu();

  public JMenu menuView = new JMenu();

  private JMenu menuWindow = new JMenu();

  private JMenu menuHelp = new JMenu();

  //public JLabel tfStatus = new JLabel();
  public FsStatusBar fsStatusBar;

  private JToolBar toolBar = new JToolBar();

  private JButton butConnect = new JButton();

  private JButton butDisconnect = new JButton();

  private JButton butExplorer = new JButton();

  private JButton butSynchronize = new JButton();

  private JButton butUpDownManager = new JButton();

  private JMenuItem menuFileConnect = new JMenuItem();

  private JMenuItem menuFileDisconnect = new JMenuItem();

  private JMenuItem menuFileExpl = new JMenuItem();

  private JMenuItem menuFileSync = new JMenuItem();

  private JMenuItem menuFileUpDownManager = new JMenuItem();

  private JMenuItem menuFilePreferences = new JMenuItem();

  private JMenuItem menuFileClose = new JMenuItem();

  private JMenuItem menuFileExit = new JMenuItem();


  private JMenuItem menuEditCut = new JMenuItem();
  
  private JMenuItem menuEditCopy = new JMenuItem();
  
  private JMenuItem menuEditPaste = new JMenuItem();
  
  private JMenuItem menuEditRename = new JMenuItem();
  
  private JMenuItem menuEditNewFolder = new JMenuItem();
  
  private JMenuItem menuEditDelete = new JMenuItem();
  
  private JMenuItem menuEditRefresh = new JMenuItem();
  
  private JMenuItem menuEditProperty = new JMenuItem();
  
  public JMenuItem menuEditUpload = new JMenuItem();
  
  public JMenuItem menuEditDownload = new JMenuItem();
  
  

  //Menu Items for View Menu

  private JMenuItem menuWinTile = new JMenuItem("Tile",FsImage.imageMenuTile);

  private JMenuItem menuWinCascade = new JMenuItem("Cascade",FsImage.imageMenuCascade);


  //Menu Items for View Menu

  private JCheckBoxMenuItem menuTileHorizontally = new JCheckBoxMenuItem();

  private JCheckBoxMenuItem menuTileVertically = new JCheckBoxMenuItem();

  private JCheckBoxMenuItem menuRemoteBrowser = new JCheckBoxMenuItem();

  private JCheckBoxMenuItem menuLocalBrowser = new JCheckBoxMenuItem();

  private JCheckBoxMenuItem menuRemoteTree = new JCheckBoxMenuItem();

  private JCheckBoxMenuItem menuLocalTree = new JCheckBoxMenuItem();


  //Menu Items for Help Menu

  private JMenuItem menuHelpTableofContents = new JMenuItem();

  private JMenuItem menuHelpFullTextSearch = new JMenuItem();

  private JMenuItem menuHelpIndexSearch = new JMenuItem();

  private JMenuItem menuHelpAbout = new JMenuItem();

  private FsUploadDownloadManager uploadDownloadManager;

  private FsExplorer explorer;
  
  private String currentsessionName;
  
  public boolean showUploadDnloadManager=true;
  
  public boolean closeUploadDnloadManager=true;
  
  private FsPreferenceUI fsPreferenceUI;
  
  private FsClient fsClient;
  
 

  public FileSyncClientMDI(Logger logger) {
    try {
      this.logger = logger;
      
      preJbInitOperation();
      jbInit();      
      
      createExplorer();
      createUploadDownloadManager();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  

  private void jbInit()
    throws Exception {
    this.setTitle("DBSentry File Synchronizer");
    this.setJMenuBar(menuBar);
    this.getContentPane().setLayout(layoutMain);
    this.setSize(new Dimension(800, 600));
    this.setIconImage(FsImage.imageTitle);
    
    panelCenter.setLayout(null);
    panelCenter.setBackground(Color.LIGHT_GRAY);
    panelCenter.setBorder(BorderFactory.createEmptyBorder());
    

    menuFileConnect.setIcon(FsImage.imageMenuConnect);
    menuFileConnect.setFont(new FsFont());
    menuFileConnect.setText("Connect");
    menuFileConnect.setVerticalTextPosition(SwingConstants.BOTTOM);
    menuFileConnect.setVerticalAlignment(SwingConstants.BOTTOM);
    menuFileConnect.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                          connect();
                                        }
                                      });
    
    menuFileDisconnect.setIcon(FsImage.imageMenuDisconnect);
    menuFileDisconnect.setFont(new FsFont());
    menuFileDisconnect.setText("Disconnect");
    menuFileDisconnect.setVerticalTextPosition(SwingConstants.BOTTOM);
    menuFileDisconnect.setVerticalAlignment(SwingConstants.BOTTOM);
    menuFileDisconnect.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                          disconnect();
                                        }
                                      });
    
    menuFileExpl.setIcon(FsImage.imageMenuExplorer);
    menuFileExpl.setFont(new FsFont());
    menuFileExpl.setText("Explorer");
    menuFileExpl.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent ae) {
                                       popExplorer();
                                     }
                                   });

    menuFileSync.setIcon(FsImage.imageMenuSync);
    menuFileSync.setFont(new FsFont());
    menuFileSync.setText("Syncronizer");
    menuFileSync.setEnabled(false);
    menuFileSync.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e) {
                                       menuFileSync_actionPerformed();
                                     }
                                   });

    menuFileUpDownManager.setIcon(FsImage.imageMenuManager);
    menuFileUpDownManager.setFont(new FsFont());
    menuFileUpDownManager.setText("Up/Down Load Manager");
    menuFileUpDownManager.setEnabled(false);
    menuFileUpDownManager.addActionListener(new ActionListener() {
                                              public void actionPerformed(ActionEvent e) {
                                                popUploadDownloadManager();
                                              }
                                            });

    menuFilePreferences.setIcon(FsImage.imageMenuPreferences);
    menuFilePreferences.setFont(new FsFont());
    menuFilePreferences.setText("Preference");
    menuFilePreferences.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                              menuFilePreferences_actionPerformed();
                                            }
                                          });

    menuFileClose.setIcon(FsImage.imageMenuClose);
    menuFileClose.setFont(new FsFont());
    menuFileClose.setText("Close");
    menuFileClose.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e) {
                                        menuFileClose_actionPerformed();
                                      }
                                    });
    
    menuFileExit.setIcon(FsImage.imageMenuExit);
    menuFileExit.setFont(new FsFont());
    menuFileExit.setText("Exit");
    menuFileExit.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent ae) {
                                       fileExit_ActionPerformed();
                                     }
                                   });
                                   
    menuFile.setFont(new FsFont());
    menuFile.setHorizontalAlignment(SwingConstants.CENTER);
    menuFile.setVerticalAlignment(SwingConstants.CENTER);
    menuFile.setText("File  ");
    menuFile.setBorder(BorderFactory.createEtchedBorder());
    menuFile.setPreferredSize(new Dimension(40, 20));
    menuFile.setMinimumSize(new Dimension(40,20));
    menuFile.setMaximumSize(new Dimension(40, 20));
    menuFile.setSize(new Dimension(40,20));
    menuFile.addMouseListener(new MouseAdapter() {
                                            public void mouseEntered(MouseEvent e) {
                                                   menu_mouseEntered(e);
                                            }

                                            public void mouseExited(MouseEvent e) {
                                                   menu_mouseExited(e);
                                            }
                                        } );
    menuFile.setMnemonic(KeyEvent.VK_F);
    menuFile.add(menuFileConnect);
    menuFile.add(menuFileDisconnect);
    menuFile.addSeparator();
    menuFile.add(menuFileExpl);
    menuFile.add(menuFileSync);
    menuFile.add(menuFileUpDownManager);
    menuFile.addSeparator();
    menuFile.add(menuFilePreferences);
    menuFile.add(menuFileClose);
    menuFile.addSeparator();
    menuFile.add(menuFileExit);

    
    menuEditCut.setIcon(FsImage.imageMenuCut);
    menuEditCut.setFont(new FsFont());
    menuEditCut.setText("Cut");
    menuEditCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK, false));
    menuEditCut.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.cutOperation();
                                             }
                                           });
    
    menuEditCopy.setIcon(FsImage.imageMenuCopy);
    menuEditCopy.setFont(new FsFont());
    menuEditCopy.setText("Copy");
    menuEditCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK, false));
    menuEditCopy.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.copyOperation();
                                             }
                                           });
    
    menuEditPaste.setIcon(FsImage.imageMenuPaste);
    menuEditPaste.setFont(new FsFont());
    menuEditPaste.setText("Paste");
    menuEditPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK, false));
    menuEditPaste.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.pasteOperation();
                                             }
                                           });
   
    menuEditNewFolder.setIcon(FsImage.imageMenuNewFolder);
    menuEditNewFolder.setFont(new FsFont());
    menuEditNewFolder.setText("NewFolder");
    menuEditNewFolder.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK, false));
    menuEditNewFolder.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.newFolderOperation();
                                             }
                                           });
    
    menuEditRename.setIcon(FsImage.imageMenuRename);
    menuEditRename.setFont(new FsFont());
    menuEditRename.setText("Rename");
    menuEditRename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
    menuEditRename.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.renameOperation();
                                             }
                                           });
                                           
    menuEditDelete.setIcon(FsImage.imageMenuDelete);
    menuEditDelete.setFont(new FsFont());
    menuEditDelete.setText("Delete");
    menuEditDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false));
    menuEditDelete.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.deleteOperation();
                                             }
                                           });
                                           
    menuEditRefresh.setIcon(FsImage.imageMenuRefresh);
    menuEditRefresh.setFont(new FsFont());
    menuEditRefresh.setText("Refresh");
    menuEditRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false));
    menuEditRefresh.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.refreshOperation();
                                             }
                                           });
                                           
    menuEditProperty.setIcon(FsImage.imageMenuProperty);
    menuEditProperty.setFont(new FsFont());
    menuEditProperty.setText("Property");
    menuEditProperty.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,Event.ALT_MASK, false));
    menuEditProperty.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.propertyOperation();
                                             }
                                           });
                                           
    menuEditUpload.setIcon(FsImage.imageUpload);
    menuEditUpload.setFont(new FsFont());
    menuEditUpload.setText("Upload");
    menuEditUpload.setEnabled(false);
   // menuEditUpload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,Event.ALT_MASK, false));
    menuEditUpload.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.menuUploadDownloadOperation();
                                             }
                                           });

    menuEditDownload.setIcon(FsImage.imageDownload);
    menuEditDownload.setFont(new FsFont());
    menuEditDownload.setText("Download");
    menuEditDownload.setEnabled(false);
   // menuEditDownload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,Event.ALT_MASK, false));
    menuEditDownload.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.menuUploadDownloadOperation();
                                             }
                                           });                                            
                                           
    
    menuEdit.setFont(new FsFont());
    menuEdit.setHorizontalAlignment(SwingConstants.CENTER);
    menuEdit.setVerticalAlignment(SwingConstants.CENTER);
    menuEdit.setText("Edit  ");
    menuEdit.setMnemonic(KeyEvent.VK_E);
    menuEdit.setBorder(BorderFactory.createEtchedBorder());
    menuEdit.setPreferredSize(new Dimension(40, 20));
    menuEdit.setMinimumSize(new Dimension(40,20));
    menuEdit.setMaximumSize(new Dimension(40, 20));
    menuEdit.setSize(new Dimension(40,20));
    menuEdit.addMouseListener(new MouseAdapter() {
                                            public void mouseEntered(MouseEvent e) {
                                                   menu_mouseEntered(e);
                                            }

                                            public void mouseExited(MouseEvent e) {
                                                   menu_mouseExited(e);
                                            }
                                        } );
    menuEdit.add(menuEditCut);
    menuEdit.add(menuEditCopy);
    menuEdit.add(menuEditPaste);
    menuEdit.addSeparator();
    menuEdit.add(menuEditNewFolder);
    menuEdit.add(menuEditRename);
    menuEdit.addSeparator();
    menuEdit.add(menuEditDelete);
    menuEdit.add(menuEditRefresh);
    menuEdit.addSeparator();
    menuEdit.add(menuEditProperty);
    menuEdit.addSeparator();
    menuEdit.add(menuEditUpload);
    menuEdit.add(menuEditDownload);
    
    menuTileHorizontally.setFont(new FsFont());
    menuTileHorizontally.setText("Horizontally");
    menuTileHorizontally.setSelected(true);
    menuTileHorizontally.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               explorer.menuTileHorizontally_actionPerformed();
                                               menuTileHorizontally.setSelected(true);
                                               menuTileVertically.setSelected(false);
                                             }
                                           });
    
    menuTileVertically.setFont(new FsFont());
    menuTileVertically.setText("Vertically");
    menuTileVertically.addActionListener(new ActionListener() {
                                           public void actionPerformed(ActionEvent e) {
                                             explorer.menuTileVertically_actionPerformed();
                                             menuTileHorizontally.setSelected(false);
                                             menuTileVertically.setSelected(true);
                                           }
                                         });
                                         
    menuLocalBrowser.setFont(new FsFont());
    menuLocalBrowser.setText("Local Browser");
    menuLocalBrowser.setSelected(true);
    menuLocalBrowser.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                           explorer.menuLocalBrowser_actionPerformed();
                                         }
                                       });
                                       
    menuRemoteBrowser.setFont(new FsFont());
    menuRemoteBrowser.setText("Remote Browser");
    menuRemoteBrowser.setSelected(true);
    menuRemoteBrowser.addActionListener(new ActionListener() {
                                          public void actionPerformed(ActionEvent e) {
                                            explorer.menuRemoteBrowser_actionPerformed();
                                          }
                                        });
                                        
    menuLocalTree.setFont(new FsFont());
    menuLocalTree.setText("Locale Tree");
    menuLocalTree.setSelected(true);
    menuLocalTree.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e) {
                                        explorer.menuLocalTree_actionPerformed();
                                      }
                                    });
    
    menuRemoteTree.setFont(new FsFont());
    menuRemoteTree.setText("Remote Tree");
    menuRemoteTree.setSelected(true);
    menuRemoteTree.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                         explorer.menuRemoteTree_actionPerformed();
                                       }
                                     });

    menuView.setFont(new FsFont());
    menuView.setHorizontalAlignment(SwingConstants.CENTER);
    menuView.setVerticalAlignment(SwingConstants.CENTER);
    menuView.setText("View  ");
    menuView.setMnemonic(KeyEvent.VK_V);
    menuView.setBorder(BorderFactory.createEtchedBorder()); 
    menuView.setPreferredSize(new Dimension(40, 20));
    menuView.setMinimumSize(new Dimension(40,20));
    menuView.setMaximumSize(new Dimension(40, 20));
    menuView.setSize(new Dimension(40,20));
    menuView.addMouseListener(new MouseAdapter() {
                                                public void mouseEntered(MouseEvent e) {
                                                       menu_mouseEntered(e);
                                                }

                                                public void mouseExited(MouseEvent e) {
                                                       menu_mouseExited(e);
                                                }
                                            } );
    menuView.add(menuTileHorizontally);
    menuView.add(menuTileVertically);
    menuView.addSeparator();
    menuView.add(menuLocalBrowser);
    menuView.add(menuRemoteBrowser);
    menuView.addSeparator();
    menuView.add(menuLocalTree);
    menuView.add(menuRemoteTree);
   
    
    
    menuWinCascade.setFont(new FsFont());
    menuWinCascade.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent ae) {
                                         cascadeFrames();
                                       }
                                     });
    
    menuWinTile.setFont(new FsFont());
    menuWinTile.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent ae) {
                                      tileFrames();
                                    }
                                  });

    
    menuWindow.setFont(new FsFont());
    menuWindow.setHorizontalAlignment(SwingConstants.CENTER);
    menuWindow.setVerticalAlignment(SwingConstants.CENTER);
    menuWindow.setText("Window ");
    menuWindow.setMnemonic(KeyEvent.VK_W);
    menuWindow.setBorder(BorderFactory.createEtchedBorder());
    menuWindow.setPreferredSize(new Dimension(65, 20));
    menuWindow.setMinimumSize(new Dimension(65,20));
    menuWindow.setMaximumSize(new Dimension(65, 20));
    menuWindow.setSize(new Dimension(65,20));
    menuWindow.addMouseListener(new MouseAdapter() {
                                                    public void mouseEntered(MouseEvent e) {
                                                           menu_mouseEntered(e);
                                                    }

                                                    public void mouseExited(MouseEvent e) {
                                                           menu_mouseExited(e);
                                                    }
                                                } );
    menuWindow.addMenuListener(new MenuListener() {
                                 public void menuCanceled(MenuEvent e) {
                                 }

                                 public void menuDeselected(MenuEvent e) {
                                   menuWindow.removeAll();
                                 }

                                 public void menuSelected(MenuEvent e) {
                                   buildChildMenus();
                                 }
                               });
    menuWindow.add(menuWinCascade);
    menuWindow.add(menuWinTile);

    menuHelpTableofContents.setIcon(FsImage.imageMenuContent);
    menuHelpTableofContents.setFont(new FsFont());
    menuHelpTableofContents.setText("Table of Contents");
    menuHelpTableofContents.addActionListener(new ActionListener() {
                                                public void actionPerformed(ActionEvent e) {
                                                  menuHelpTableofContents_actionPerformed();
                                                }
                                              });

    menuHelpIndexSearch.setIcon(FsImage.imageMenuIndex);
    menuHelpIndexSearch.setFont(new FsFont());
    menuHelpIndexSearch.setText("Index Search");
    menuHelpIndexSearch.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                              menuHelpIndexSearch_actionPerformed();
                                            }
                                          });
    
    menuHelpFullTextSearch.setIcon(FsImage.imageMenuSearch);
    menuHelpFullTextSearch.setFont(new FsFont());
    menuHelpFullTextSearch.setText("Full Text Search");
    menuHelpFullTextSearch.addActionListener(new ActionListener() {
                                               public void actionPerformed(ActionEvent e) {
                                                 menuHelpFullTextSearch_actionPerformed();
                                               }
                                             });
    
    menuHelpAbout.setIcon(FsImage.imageMenuAbout);
    menuHelpAbout.setFont(new FsFont());
    menuHelpAbout.setText("About FileSync");
    menuHelpAbout.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent ae) {
                                        helpAbout_ActionPerformed();
                                      }
                                    });

    menuHelp.setFont(new FsFont());
    menuHelp.setText("Help  ");
    menuHelp.setMnemonic(KeyEvent.VK_H);
    menuHelp.setBorder(BorderFactory.createEtchedBorder());
    menuHelp.setPreferredSize(new Dimension(40, 20));
    menuHelp.setMinimumSize(new Dimension(40,20));
    menuHelp.setMaximumSize(new Dimension(40, 20));
    menuHelp.setSize(new Dimension(40,20));
    menuHelp.setHorizontalAlignment(SwingConstants.CENTER);
    menuHelp.setVerticalAlignment(SwingConstants.CENTER);
    menuHelp.addMouseListener(new MouseAdapter() {
                                                        public void mouseEntered(MouseEvent e) {
                                                               menu_mouseEntered(e);
                                                        }

                                                        public void mouseExited(MouseEvent e) {
                                                               menu_mouseExited(e);
                                                        }     
                                                    } );
    menuHelp.add(menuHelpTableofContents);
    menuHelp.addSeparator();
    menuHelp.add(menuHelpFullTextSearch);
    menuHelp.add(menuHelpIndexSearch);
    menuHelp.addSeparator();
    menuHelp.add(menuHelpAbout);
    
    menuBar.setFont(new FsFont());
    menuBar.add(menuFile);
    menuBar.add(menuEdit);
    menuBar.add(menuView);
    menuBar.add(menuWindow);
    menuBar.add(menuHelp);
    
    butConnect.setPreferredSize(new Dimension(40, 30));
    butConnect.setSize(new Dimension(40, 30));
    butConnect.setMaximumSize(new Dimension(40, 30));
    butConnect.setHorizontalTextPosition(SwingConstants.CENTER);
    butConnect.setIcon(FsImage.imageConnect);
    butConnect.setVerticalTextPosition(SwingConstants.BOTTOM);
    butConnect.setIconTextGap(0);
    butConnect.setToolTipText("Connect");
    butConnect.setOpaque(false);
    butConnect.setBorder(BorderFactory.createEmptyBorder());
    butConnect.addMouseListener(new ButtonMouseAdapter());
    butConnect.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                     connect();
                                   }
                                 }
    );
    
    butDisconnect.setPreferredSize(new Dimension(40, 30));
    butDisconnect.setSize(new Dimension(40, 30));
    butDisconnect.setMaximumSize(new Dimension(40, 30));
    butDisconnect.setHorizontalTextPosition(SwingConstants.CENTER);
    butDisconnect.setIcon(FsImage.imageDisconnect);
    butDisconnect.setVerticalTextPosition(SwingConstants.BOTTOM);
    butDisconnect.setIconTextGap(0);
    butDisconnect.setToolTipText("Disconnect");
    butDisconnect.setOpaque(false);
    butDisconnect.setBorder(BorderFactory.createEmptyBorder());
    butDisconnect.addMouseListener(new ButtonMouseAdapter());
    butDisconnect.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                     disconnect();
                                   }
                                 }
    );
    

    butExplorer.setPreferredSize(new Dimension(40, 30));
    butExplorer.setSize(new Dimension(40, 30));
    butExplorer.setMaximumSize(new Dimension(40, 30));
    butExplorer.setHorizontalTextPosition(SwingConstants.CENTER);
    butExplorer.setIcon(FsImage.imageFileExplorer);
    butExplorer.setVerticalTextPosition(SwingConstants.BOTTOM);
    butExplorer.setIconTextGap(0);
    butExplorer.setToolTipText("Explorer");
    butExplorer.setOpaque(false);
    butExplorer.setBorder(BorderFactory.createEmptyBorder());
    butExplorer.addMouseListener(new ButtonMouseAdapter());
    butExplorer.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                      popExplorer();
                                    }
                                  }
    );

    
    butSynchronize.setEnabled(false);
    butSynchronize.setPreferredSize(new Dimension(40, 30));
    butSynchronize.setSize(new Dimension(40, 30));
    butSynchronize.setMaximumSize(new Dimension(40, 30));
    butSynchronize.setHorizontalTextPosition(SwingConstants.CENTER);
    butSynchronize.setIcon(FsImage.imageFileSync32x32);
    butSynchronize.setVerticalTextPosition(SwingConstants.BOTTOM);
    butSynchronize.setIconTextGap(0);
    butSynchronize.setToolTipText("Synchronizer");
    butSynchronize.setOpaque(false);
    butSynchronize.setBorder(BorderFactory.createEmptyBorder());
    butSynchronize.addMouseListener(new ButtonMouseAdapter());
    butSynchronize.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                         butSynchronize_actionPerformed();
                                       }
                                     }
    );


    butUpDownManager.setEnabled(false);
    butUpDownManager.setPreferredSize(new Dimension(40, 30));
    butUpDownManager.setSize(new Dimension(40, 30));
    butUpDownManager.setMaximumSize(new Dimension(40, 30));
    butUpDownManager.setHorizontalTextPosition(SwingConstants.CENTER);
    butUpDownManager.setIcon(FsImage.imageUploadDownloadManager);
    butUpDownManager.setVerticalTextPosition(SwingConstants.BOTTOM);
    butUpDownManager.setIconTextGap(0);
    butUpDownManager.setToolTipText("Upload Download Manager");
    butUpDownManager.setOpaque(false);
    butUpDownManager.setBorder(BorderFactory.createEmptyBorder());
    butUpDownManager.addMouseListener(new ButtonMouseAdapter());
    butUpDownManager.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                           popUploadDownloadManager();
                                         }
                                       }
    );

    toolBar.add(butConnect);
    toolBar.add(butDisconnect);
    toolBar.add(butExplorer);
    toolBar.add(butSynchronize);
    toolBar.add(butUpDownManager);
    toolBar.setFloatable(false);
   // toolBar.setBorder(BorderFactory.createEmptyBorder());
    toolBar.setMargin(new Insets(0,0,0,0));
    toolBar.setSize(new Dimension(795, 40));
    
    //tfStatus.setText("");
     fsStatusBar = new FsStatusBar(logger);
     fsStatusBar.setLblSetMessage("");
     fsStatusBar.setLblSetSesionName("");
     fsStatusBar.setLblSetUserName("");
     
    this.getContentPane().setLayout(layoutMain);
    
   // this.getContentPane().add(tfStatus, BorderLayout.SOUTH);

    this.getContentPane().add(toolBar, BorderLayout.NORTH);
    this.getContentPane().add(panelCenter, BorderLayout.CENTER);
    this.getContentPane().add(fsStatusBar, BorderLayout.SOUTH);
    

  }

  private void fileExit_ActionPerformed() {
    System.exit(0);
  }


  private void popExplorer() {

    try {
      logger.debug("Entering Pop Explorer ");
      explorer.setIcon(false);
      explorer.setSelected(true);
      explorer.setMaximum(true);
      explorer.toFront();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    logger.debug("Exiting Pop Explorer ");
  }

  private void createExplorer() {
    logger.debug("Entering Create Explorer ");
    try {

      explorer = new FsExplorer(logger, this);
      
      panelCenter.add(explorer);
      explorer.addInternalFrameListener(new InternalFrameAdapter() {
                                          public void internalFrameActivated(InternalFrameEvent e) {
                                            logger.debug("i m in activated of explorer");
                                            menuEdit.setVisible(true);
                                            menuView.setVisible(true);
                                            fsStatusBar.setLblSetMessage("");                                            
                                          }

                                          public void internalFrameDeactivated(InternalFrameEvent e) {
                                           logger.debug("i m in deactivated of explorer");
                                            menuEdit.setVisible(false);
                                            menuView.setVisible(false);
                                            fsStatusBar.setLblSetMessage(""); 
                                          }
                                        });

      explorer.setFrameIcon(FsImage.imageFileExplorer16x16);
      explorer.setClosable(false);
      explorer.setVisible(true);
      explorer.setLocation(0, 0);
      explorer.getFsLocalView().setVisible(true);
      explorer.setMaximum(true);
      
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    logger.debug("Exisitng Create Explorer ");

  }
  


  public void popUploadDownloadManager() {
    try {
      logger.debug("Entering Pop Upload Download Mangager ");
      uploadDownloadManager.setVisible(true);
      uploadDownloadManager.setIcon(false);
      uploadDownloadManager.setSelected(true);
      uploadDownloadManager.toFront();
      uploadDownloadManager.setMaximum(true);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    logger.debug("Entering Pop Upload Download Mangager");

  }

  private void createUploadDownloadManager() {
    try {
      uploadDownloadManager = new FsUploadDownloadManager(logger, explorer);

      panelCenter.add(uploadDownloadManager);
      uploadDownloadManager.addInternalFrameListener(new InternalFrameAdapter() {
                                                       public void internalFrameIconified(InternalFrameEvent e) {
                                                         //  this_internalFrameIconified(e);
                                                       }

                                                       public void internalFrameDeiconified(InternalFrameEvent e) {
                                                         // this_internalFrameDeiconified(e);
                                                       }

                                                       public void internalFrameClosed(InternalFrameEvent e) {
                                                         // this_internalFrameClosed(e);
                                                         // menuWinCascade.setEnabled(true);
                                                       }
                                                     }
      );

      uploadDownloadManager.setFrameIcon(FsImage.imageUploadDownloadManager16x16);
      uploadDownloadManager.setClosable(false);
      uploadDownloadManager.setLocation(0, 0);
      
//      showUploadDnloadManager=fsPreferenceUI.getChkShowUploadDownLoadMgr().isSelected();
//      closeUploadDnloadManager=fsPreferenceUI.getChkCloseUploadDownloadMgr().isSelected();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  public FsUser getFsUser() {
    return fsPreferenceUI.getFsUser();
  }

  /**
   * Gives a raised effect to button when mouse entered init.
   * @param e MouseEvent object
   */
  private void menu_mouseEntered(MouseEvent e) {
    JMenu button = (JMenu)e.getSource();
    button.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    
  }


  /**
   * Makes button border normal when mouse exited from it.
   * @param e MouseEvent object
   */
  private void menu_mouseExited(MouseEvent e) {
    JMenu button = (JMenu)e.getSource();
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


  
  private void connect() {
    fsPreferenceUI.setTitle("Preferences");
    fsPreferenceUI.setVisible(true);
  }

  private void disconnect() {
  
    int cancel =
      JOptionPane.showConfirmDialog(this, "Do you want to Disconnect from Server..?", "Disconnect", JOptionPane
                                               .YES_NO_OPTION);

    if (cancel == JOptionPane.NO_OPTION) {
      return;
    }
    
    //Check of Synchronization Process  Running
    if(FsFileSync.fileSyncPreviewStatus.size()>0){
      JOptionPane.showMessageDialog(this, "Close All Synchronization Windows", "Disconnect Error",JOptionPane.ERROR_MESSAGE);
      return;          
    }
  
    //Check for Running Processes in Upload Download Manager 
    //TODO   
      

    //Disconnecting 
    //explorer.getFsRemoteView().setWaitCursorForRemoteBrowser();
    butDisconnect.setEnabled(false);  
  
    //tfStatus.setText("User Disconnected");
    fsStatusBar.setLblSetMessage("");
    fsStatusBar.setLblSetMessage("User Disconnected....");
    fsStatusBar.setLblSetUserName("");
    getFsClient().disconnect(new DisconnectionListener());

  }
  
  public void enableMDIControls(boolean enable){
    butConnect.setVisible(!enable);
    butDisconnect.setVisible(enable);
    butDisconnect.setEnabled(true);
    
    menuFileConnect.setVisible(!enable);
    menuFileDisconnect.setVisible(enable);
    
    menuFileSync.setEnabled(enable);
    menuFileUpDownManager.setEnabled(enable);
    butSynchronize.setEnabled(enable);
    butUpDownManager.setEnabled(enable);
    
  
  }


  private void menuFilePreferences_actionPerformed() {
    fsPreferenceUI.setVisible(true);
  }


  private void menuFileSync_actionPerformed() {
    butSynchronize_actionPerformed();
  }


  private void butSynchronize_actionPerformed() {
    FsFileSync fsFileSync = null;
    try {
      if (fsFileSync == null || !fsFileSync.isVisible()) {
        fsFileSync = new FsFileSync(this);
        fsFileSync.setVisible(true);

        panelCenter.add(fsFileSync);
        fsFileSync.setLocation(20, 20);
      }
      fsFileSync.toFront();
      fsFileSync.setIcon(false);
      fsFileSync.setSelected(true);
      fsFileSync.setFrameIcon(FsImage.imageFileSync16x16);
      fsFileSync.setMaximum(true);
    }
    catch (Exception ae) {
      ;
    }

  }

  private void menuFileClose_actionPerformed() {
    this.setVisible(false);
  }

  private void preJbInitOperation() {
    try {
      
      //Initiate FileSyncHelp
      Class htmlBrowserClass = Class.forName("oracle.help.htmlBrowser.ICEBrowser");
      helpObject = new Help(htmlBrowserClass);
      manager = new CSHManager(helpObject);

      HelpSet myhelpset = new HelpSet(new File("help/FileSyncHelp.hs").toURL());
      helpObject.addBook(myhelpset);
      manager.setDefaultBook(myhelpset);
      navigator = manager.getAllNavigators();


    }
    catch (Exception ex) {
      logger.error(GeneralUtil.getStackTrace(ex));
      JOptionPane.showMessageDialog(null, ex.getMessage(), "Exit", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
  }

  private void menuHelpTableofContents_actionPerformed() {
    if (navigator != null) {
      helpObject.showNavigatorWindow(navigator[0]);
    }
  }

  private void menuHelpIndexSearch_actionPerformed() {
    if (navigator != null) {
      helpObject.showNavigatorWindow(navigator[1]);
    }
  }

  private void menuHelpFullTextSearch_actionPerformed() {
    if (navigator != null) {
      helpObject.showNavigatorWindow(navigator[2]);
    }
  }


  private void helpAbout_ActionPerformed() {
    AboutDbsFileSync aboutDbsFileSync = new AboutDbsFileSync(this);
    aboutDbsFileSync.setVisible(true);
  }


  //For Cascading of Window

  private void cascadeFrames() {
    int x = 0;
    int y = 0;
    JInternalFrame allFrames[] = panelCenter.getAllFrames();

    logger.debug("i m in Cascade Frame " + allFrames.length);
    int frameHeight = (getBounds().height - 5) - allFrames.length * FRAME_OFFSET;
    int frameWidth = (getBounds().width - 5) - allFrames.length * FRAME_OFFSET;
    for (int i = allFrames.length - 1; i >= 0; i--) {
      allFrames[i].setSize(frameWidth, frameHeight);
      allFrames[i].setLocation(x, y);
      x = x + FRAME_OFFSET;
      y = y + FRAME_OFFSET;
    }
  }

  //For Tiling of Windows

  private void tileFrames() {
    Component allFrames[] = panelCenter.getAllFrames();
    int frameHeight = getBounds().height / allFrames.length;
    int y = 0;
    for (int i = 0; i < allFrames.length; i++) {
      allFrames[i].setSize(getBounds().width, frameHeight);
      allFrames[i].setLocation(0, y);
      y = y + frameHeight;
    }
  }


  private void buildChildMenus() {

    int i;
    ChildMenuItem menu;
    JInternalFrame[] array = panelCenter.getAllFrames();

    menuWindow.add(menuWinCascade);
    menuWindow.add(menuWinTile);
    if (array.length > 0)
      menuWindow.addSeparator();
    menuWinCascade.setEnabled(array.length > 0);
    menuWinTile.setEnabled(array.length > 0);

    for (i = 0; i < array.length; i++) {
      menu = new ChildMenuItem(array[i]);
      menu.setFont(new FsFont());
      menu.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent ae) {
                                 JInternalFrame frame = ((ChildMenuItem)ae.getSource()).getFrame();
                                 frame.toFront();
                                 try {
                                   frame.setIcon(false);
                                 }
                                 catch (PropertyVetoException e) {
                                   e.printStackTrace();
                                 }
                               }
                             }
      );
      menu.setIcon(array[i].getFrameIcon());
      menuWindow.add(menu);
    }
  }

  public FsClient getFsClient() {
    return fsClient;
  }

  public FsUploadDownloadManager getUploadDownloadManager() {
    return uploadDownloadManager;
  }

  public FsExplorer getExplorer() {
    return explorer;
  }

  public boolean isMenuFileConnectEnabled() {
  if(menuFileConnect.isEnabled())
    return true;
    else
    return false;
    
  }
  
  public void enableMenuView(boolean flag){
  
    menuLocalBrowser.setEnabled(flag);
    menuRemoteBrowser.setEnabled(flag);
    menuLocalTree.setEnabled(flag);
    menuRemoteTree.setEnabled(flag);
    menuTileHorizontally.setEnabled(flag);
    menuTileVertically.setEnabled(flag);
  }

 public void enableMenuEdit(boolean flag){
   menuEdit.setEnabled(flag);
 }
 
  public JButton getButDisconnect() {
    return butDisconnect;
  }

  
  public void setSessionName(String currentsessionName){
    this.currentsessionName=currentsessionName;
  }

  public void setFsClient(FsClient fsClient) {
    this.fsClient = fsClient;
  }

  public void setFsPreferenceUI(FsPreferenceUI fsPreferenceUI) {
    this.fsPreferenceUI = fsPreferenceUI;
  }


  /* This JCheckBoxMenuItem descendant is used to track the child frame that corresponds
       to a give menu. */

  private class ChildMenuItem    extends JMenuItem {
    private JInternalFrame frame;

    public ChildMenuItem(JInternalFrame frame) {
      super(frame.getTitle());
      this.frame = frame;
    }

    public JInternalFrame getFrame() {
      return frame;
    }
  }



  private class DisconnectionListener implements FsDisconnectionListener {
    
    public void propertyChange(PropertyChangeEvent evt) {
      int propertyName = Integer.valueOf(evt.getPropertyName());
      FsResponse fsResponse;
      fsResponse = (FsResponse)evt.getNewValue();
      switch (propertyName) {
      case DISCONNECTED :
        logger.info("User Disconnected");
        explorer.disposeRemoteView();
        enableMDIControls(false);
        break;
      }
    }
  }

}
