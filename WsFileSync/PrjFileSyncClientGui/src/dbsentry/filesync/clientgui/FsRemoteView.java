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
 
import dbsentry.filesync.client.ClientUtil;
import dbsentry.filesync.client.FsClient;
import dbsentry.filesync.client.enumconstants.EnumClipBoardOperation;
import dbsentry.filesync.clientgui.enumconstants.EnumLocalTable;
import dbsentry.filesync.clientgui.enumconstants.EnumRemoteTable;
import dbsentry.filesync.clientgui.images.FsImage;
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
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsRemoteCommandListener;
import dbsentry.filesync.common.listeners.FsRemoteCopyListener;
import dbsentry.filesync.common.listeners.FsRemoteMoveListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.IOException;

import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DebugGraphics;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
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

import org.apache.log4j.Logger;


public class FsRemoteView extends JInternalFrame{

 // private String itemPaths[];
  
  private int currClipBoardOperationRemote;
  
  private String clipBoardRemote[];
  
  private File file;
  
  private JFileChooser fileChooser = new JFileChooser();
  
  private FileSystemView fileSystemView = fileChooser.getFileSystemView();
  
  private Color selectionColor;
  
  private Border normalButtonBorder;
  
  private int cursorCounterRemote = 0;

  //public static int cursorCounterLocal = 0;

  private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
  
  private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
  
  private JButton butRemoteNavigateBack = new JButton();

  private JButton butRemoteNavigateForward = new JButton();

  private JButton butRemoteNavigateUp = new JButton();

  private JButton butRemoteRefresh = new JButton();
  
  private JButton butRemoteHome = new JButton();

  private JButton butRemoteNewFolder = new JButton();

  private JButton butRemoteRenameFolderFile = new JButton();

  private JButton butRemoteDelete = new JButton();

  private JButton butRemoteProperty = new JButton();

  private JButton butRemoteCut = new JButton();

  private JButton butRemoteCopy = new JButton();

  private JButton butRemotePaste = new JButton();

  private JButton butRemoteDownload = new JButton();

  private JButton butRemoteGo = new JButton();
  
  private JLabel lbRemoteAddressbar = new JLabel();
  
  private JComboBox comboRemoteAdressBar = new JComboBox();
    
  private JToolBar tbRemoteSystem = new JToolBar();
  
 public JTree treeRemoteTreeView =new JTree(new DefaultMutableTreeNode(null));
  
  public JTable tblRemoteFolderFileList = new JTable();
  
  public JSplitPane splpRemoteSystem = new JSplitPane();
  
  private JScrollPane scrpRemoteFolderFileList = new JScrollPane();
  
  private JScrollPane scrpRemoteTreeView = new JScrollPane();
  
  private  JPanel jpRemoteSystem = new JPanel();   
  
  private JPanel jpRemoteFolderFileList = new JPanel();
  
  private JPanel jpRemoteTreeView = new JPanel();
  
  private JPanel jpToolBarRemote = new JPanel();
  
  //private JTextField tfStatus = new JTextField();
  
  private RemoteWillTreeExpansionListener remoteWillTreeExpansionListener;
  
  private ComboRemoteAdressBar_ActionListener actionListener4RemoteAddressBar= new ComboRemoteAdressBar_ActionListener();
  
  private RemoteTableColumnHeaderListener remoteTableHeaderMouseListener = null;
  
  private FsFolderDocInfoHolder fsFolderDocInfoHolder;
  
  private FolderDocInfoClient folderDocInfoClient;
  
  private FsTableModel fsTableModelRemote;//= new FsTableModel(EnumRemoteTable.COLUMN_NAMES,dataRow);
  
  private Logger logger;
  
  private FileSyncClientMDI mdiParent;
  
  private FsClient fsClient;
  
  private String parentFolderPath;
  
  private JPopupMenu popup = new JPopupMenu();
  
  private JMenuItem cutMenuItem = new JMenuItem("Cut");
  
  private JMenuItem copyMenuItem = new JMenuItem("Copy ");
  
  private JMenuItem pasteMenuItem = new JMenuItem("Paste");
  
  private JMenuItem renameMenuItem = new JMenuItem("Rename");
  
  private JMenuItem newFolderMenuItem = new JMenuItem("NewFolder");
  
  private JMenuItem deleteMenuItem = new JMenuItem("Delete");
  
  private JMenuItem refreshMenuItem = new JMenuItem("Refresh");
  
  private JMenuItem downloadMenuItem = new JMenuItem("Download...");
  
  private JMenuItem propertyMenuItem = new JMenuItem("Property");

  public FsRemoteView(Logger logger,FileSyncClientMDI mdiParent) {
    super("Remote View", false,false , false, false);
    
    try {
      this.logger=logger;
      this.mdiParent=mdiParent;
      jbInit();
      disableButtonOperation();
      folderDocInfoClient = mdiParent.getExplorer().getFsLocalView().getFolderDocInfoClient();
      file = new File(System.getProperty("user.home"));
      
      this.setResizable(false);
      this.setBorder(BorderFactory.createEmptyBorder());
      this.setFrameIcon(null);
      
    }
    catch (Exception e) {
      ;
    }
  
  }
  
  private void jbInit() throws Exception {
    
    normalButtonBorder=BorderFactory.createEmptyBorder();
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
    butRemoteNavigateBack.setBorder(normalButtonBorder);
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
                                           } );
    butRemoteNavigateBack.addActionListener(new ActionListener() {
                                                public void actionPerformed(ActionEvent e) {
                                                    butRemoteNavigateBack_actionPerformed();
                                                }
                                            } );


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
    butRemoteNavigateForward.setBorder(normalButtonBorder);
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
                                              } );
    butRemoteNavigateForward.addActionListener(new ActionListener() {
                                                   public void actionPerformed(ActionEvent e) {
                                                        butRemoteNavigateForward_actionPerformed();
                                                   }
                                               } );

    //Remote Navigate Up
    butRemoteNavigateUp.setToolTipText("Go Up");
    butRemoteNavigateUp.setIcon(FsImage.imageNavigateUp);
    butRemoteNavigateUp.setOpaque(false);
    butRemoteNavigateUp.setPreferredSize(new Dimension(30, 30));
    butRemoteNavigateUp.setMinimumSize(new Dimension(30, 30));
    butRemoteNavigateUp.setMaximumSize(new Dimension(30, 25));
    butRemoteNavigateUp.setSize(new Dimension(30, 30));
    butRemoteNavigateUp.setBorder(normalButtonBorder);
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
                                         } );
    butRemoteNavigateUp.addActionListener(new ActionListener() {
                                              public void actionPerformed(ActionEvent e) {
                                                    butRemoteNavigateUp_actionPerformed();
                                              }
                                          } );

    //Remote Refresh
    butRemoteRefresh.setToolTipText("Refresh (F5)");
    butRemoteRefresh.setIcon(FsImage.imageRefresh);
    butRemoteRefresh.setOpaque(false);
    butRemoteRefresh.setPreferredSize(new Dimension(30, 30));
    butRemoteRefresh.setMinimumSize(new Dimension(30, 30));
    butRemoteRefresh.setMaximumSize(new Dimension(30, 25));
    butRemoteRefresh.setSize(new Dimension(30, 30));
    butRemoteRefresh.setBorder(normalButtonBorder);
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
                                      } );
    butRemoteRefresh.addActionListener(new ActionListener() {
                                           public void actionPerformed(ActionEvent e) {
                                                 butRemoteRefresh_actionPerformed();
                                           }
                                       } );
                                       
    //Remote Home 
    butRemoteHome.setToolTipText("Home");
    butRemoteHome.setIcon(FsImage.imageHome);
    butRemoteHome.setOpaque(false);
    butRemoteHome.setPreferredSize(new Dimension(30, 30));
    butRemoteHome.setMinimumSize(new Dimension(30, 30));
    butRemoteHome.setMaximumSize(new Dimension(30, 25));
    butRemoteHome.setSize(new Dimension(30, 30));
    butRemoteHome.setBorder(normalButtonBorder);
    butRemoteHome.addMouseListener(new MouseAdapter() {
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
                                      } );
    butRemoteHome.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                               butRemoteHome_actionPerformed();
                                         }
                                     } );

    //Remote New Folder
    butRemoteNewFolder.setToolTipText("Create New Folder");
    butRemoteNewFolder.setIcon(FsImage.imageNewFolder);
    butRemoteNewFolder.setOpaque(false);
    butRemoteNewFolder.setPreferredSize(new Dimension(30, 30));
    butRemoteNewFolder.setMinimumSize(new Dimension(30, 30));
    butRemoteNewFolder.setMaximumSize(new Dimension(30, 25));
    butRemoteNewFolder.setSize(new Dimension(30, 30));
    butRemoteNewFolder.setBorder(normalButtonBorder);
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
                                        } );
    butRemoteNewFolder.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                                     butRemoteNewFolder_actionPerformed();
                                             }
                                         });

    //Remote Rename
    butRemoteRenameFolderFile.setToolTipText("Rename (F5)");
    butRemoteRenameFolderFile.setIcon(FsImage.imageRename);
    butRemoteRenameFolderFile.setOpaque(false);
    butRemoteRenameFolderFile.setPreferredSize(new Dimension(30, 30));
    butRemoteRenameFolderFile.setMinimumSize(new Dimension(30, 30));
    butRemoteRenameFolderFile.setMaximumSize(new Dimension(30, 25));
    butRemoteRenameFolderFile.setSize(new Dimension(30, 30));
    butRemoteRenameFolderFile.setBorder(normalButtonBorder);
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
                                               } );
    butRemoteRenameFolderFile.addActionListener(new ActionListener() {
                                                    public void actionPerformed(ActionEvent e) {
                                                          butRemoteRenameFolderFile_actionPerformed();
                                                    }
                                                } );

    //Remote Delete
    butRemoteDelete.setToolTipText("Delete(Delete)");
    butRemoteDelete.setIcon(FsImage.imageDelete);
    butRemoteDelete.setOpaque(false);
    butRemoteDelete.setPreferredSize(new Dimension(30, 30));
    butRemoteDelete.setMinimumSize(new Dimension(30, 30));
    butRemoteDelete.setMaximumSize(new Dimension(30, 25));
    butRemoteDelete.setSize(new Dimension(30, 30));
    butRemoteDelete.setBorder(normalButtonBorder);
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
                                     } );
    butRemoteDelete.addActionListener(new ActionListener() {
                                          public void actionPerformed(ActionEvent e) {
                                               butRemoteDelete_actionPerformed();
                                          }
                                      } );

    //Remote Property 
    butRemoteProperty.setToolTipText("Property");
    butRemoteProperty.setIcon(FsImage.imageProperty);
    butRemoteProperty.setOpaque(false);
    butRemoteProperty.setPreferredSize(new Dimension(30, 30));
    butRemoteProperty.setMinimumSize(new Dimension(30, 30));
    butRemoteProperty.setMaximumSize(new Dimension(30, 25));
    butRemoteProperty.setSize(new Dimension(30, 30));
    butRemoteProperty.setBorder(normalButtonBorder);
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
                                       } );
    butRemoteProperty.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                                 butRemoteProperty_actionPerformed();
                                                
                                            }
                                        } );

    //Remote Cut
    butRemoteCut.setToolTipText("Cut (Ctrl-X)");
    butRemoteCut.setIcon(FsImage.imageCut);
    butRemoteCut.setOpaque(false);
    butRemoteCut.setPreferredSize(new Dimension(30, 30));
    butRemoteCut.setMinimumSize(new Dimension(30, 30));
    butRemoteCut.setMaximumSize(new Dimension(30, 25));
    butRemoteCut.setSize(new Dimension(30, 30));
    butRemoteCut.setBorder(normalButtonBorder);
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
                                  }  );
    butRemoteCut.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                            butRemoteCut_actionPerformed();
                                       }
                                   } );

    //Remote Copy
    butRemoteCopy.setToolTipText("Copy (Ctrl-C)");
    butRemoteCopy.setIcon(FsImage.imageCopy);
    butRemoteCopy.setOpaque(false);
    butRemoteCopy.setPreferredSize(new Dimension(30, 30));
    butRemoteCopy.setMinimumSize(new Dimension(30, 30));
    butRemoteCopy.setMaximumSize(new Dimension(30, 25));
    butRemoteCopy.setSize(new Dimension(30, 30));
    butRemoteCopy.setBorder(normalButtonBorder);
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
                                   }  );
    butRemoteCopy.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                               butRemoteCopy_actionPerformed();
                                        }
                                    } );

    //Remote Paste
    butRemotePaste.setToolTipText("Paste (Ctrl-V)");
    butRemotePaste.setIcon(FsImage.imagePaste);
    butRemotePaste.setOpaque(false);
    butRemotePaste.setPreferredSize(new Dimension(30, 30));
    butRemotePaste.setMinimumSize(new Dimension(30, 30));
    butRemotePaste.setMaximumSize(new Dimension(30, 25));
    butRemotePaste.setSize(new Dimension(30, 30));
    butRemotePaste.setBorder(normalButtonBorder);
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
                                    } );
    butRemotePaste.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                                butRemotePaste_actionPerformed();
                                         }
                                     } );

    //Download
    butRemoteDownload.setToolTipText("Download ");
    butRemoteDownload.setIcon(FsImage.imageDownload);
    butRemoteDownload.setOpaque(false);
    butRemoteDownload.setPreferredSize(new Dimension(30, 30));
    butRemoteDownload.setMinimumSize(new Dimension(30, 30));
    butRemoteDownload.setMaximumSize(new Dimension(30, 25));
    butRemoteDownload.setSize(new Dimension(30, 30));
    butRemoteDownload.setBorder(normalButtonBorder);
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
                                     } );
    butRemoteDownload.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                                 butRemoteDownload_actionPerformed();
                                            }
                                        } );
    

    //Remote Addressbar

    //Label for Remote Address bar   
    lbRemoteAddressbar.setText("Address : ");
    lbRemoteAddressbar.setPreferredSize(new Dimension(62, 20));
    lbRemoteAddressbar.setMinimumSize(new Dimension(90, 14));
    lbRemoteAddressbar.setMaximumSize(new Dimension(90, 14));
    lbRemoteAddressbar.setHorizontalAlignment(SwingConstants.RIGHT);

    //Dropdown for Remote Address bar 
    comboRemoteAdressBar.setFont(new FsFont());
    comboRemoteAdressBar.setOpaque(false);
    comboRemoteAdressBar.setPreferredSize(new Dimension(420, 20));
    comboRemoteAdressBar.setMinimumSize(new Dimension(242, 20));
    comboRemoteAdressBar.setMaximumSize(new Dimension(610, 20));
    comboRemoteAdressBar.setSize(new Dimension(600, 28));
    comboRemoteAdressBar.setEditable(true);
    comboRemoteAdressBar.setMaximumRowCount(5);
    comboRemoteAdressBar.setBorder(BorderFactory.createEtchedBorder());
    comboRemoteAdressBar.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
                                                                             public void keyReleased(KeyEvent e) {
                                                                                 comboRemoteAdressBar_keyReleased(e);
                                                                             }
                                                                         } );
    comboRemoteAdressBar.addActionListener(actionListener4RemoteAddressBar);              

    //Remote Button Go 
    butRemoteGo.setToolTipText("Go");
    butRemoteGo.setIcon(FsImage.imageGo);
    butRemoteGo.setOpaque(false);
    butRemoteGo.setPreferredSize(new Dimension(30, 20));
    butRemoteGo.setMinimumSize(new Dimension(30, 20));
    butRemoteGo.setMaximumSize(new Dimension(30, 20));
    butRemoteGo.setSize(new Dimension(30, 28));
    butRemoteGo.setBorder(normalButtonBorder);
    butRemoteGo.addMouseListener(new ButtonMouseAdapter());
    butRemoteGo.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e) {
                                            butRemoteGo_actionPerformed();
                                      }
                                  } );
    
    //Preparing Remote Tool bar
    tbRemoteSystem.setFloatable(false);
    tbRemoteSystem.setBorder(BorderFactory.createEtchedBorder());
    tbRemoteSystem.add(butRemoteNavigateBack, null);
    tbRemoteSystem.add(butRemoteNavigateForward, null);
    tbRemoteSystem.add(butRemoteNavigateUp, null);
    
//    tbRemoteSystem.addSeparator(new Dimension(1,3));
//    tbRemoteSystem.add(new JSeparator(JSeparator.VERTICAL),null);
   // tbRemoteSystem.addSeparator(new Dimension(1,3));
    
    tbRemoteSystem.add(butRemoteRefresh, null);
    tbRemoteSystem.add(butRemoteHome, null);
    
//    tbRemoteSystem.addSeparator(new Dimension(1,3));
//    tbRemoteSystem.add(new JSeparator(JSeparator.VERTICAL),null);
    //tbRemoteSystem.addSeparator(new Dimension(1,3));
    
    tbRemoteSystem.add(butRemoteNewFolder, null);
    tbRemoteSystem.add(butRemoteRenameFolderFile, null);
    tbRemoteSystem.add(butRemoteDelete, null);
    tbRemoteSystem.add(butRemoteProperty, null);
    
//    tbRemoteSystem.addSeparator(new Dimension(1,3));
//    tbRemoteSystem.add(new JSeparator(JSeparator.VERTICAL),null);
    //tbRemoteSystem.addSeparator(new Dimension(1,3));
    
    tbRemoteSystem.add(butRemoteCut, null);
    tbRemoteSystem.add(butRemoteCopy, null);
    tbRemoteSystem.add(butRemotePaste, null);
    
//    tbRemoteSystem.addSeparator(new Dimension(1,3));
//    tbRemoteSystem.add(new JSeparator(JSeparator.VERTICAL),null);
//    tbRemoteSystem.addSeparator(new Dimension(1,3));
    
    tbRemoteSystem.add(butRemoteDownload, null);
    tbRemoteSystem.add(lbRemoteAddressbar, null);
    tbRemoteSystem.add(comboRemoteAdressBar, null);
    tbRemoteSystem.add(butRemoteGo, null);
    
    //Panel  for Remote Toolbar
    jpToolBarRemote.setLayout(new BorderLayout());
    jpToolBarRemote.setBorder(BorderFactory.createEmptyBorder());
    jpToolBarRemote.add(tbRemoteSystem, BorderLayout.CENTER);
    
    //Remote Treeview
    treeRemoteTreeView.setToolTipText("null");
    treeRemoteTreeView.setAutoscrolls(true);
    treeRemoteTreeView.setRootVisible(false);
    treeRemoteTreeView.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);
    treeRemoteTreeView.setBorder(BorderFactory.createEmptyBorder());
    treeRemoteTreeView.addTreeSelectionListener(new TreeSelectionListener() {
                                                    public void valueChanged(TreeSelectionEvent e) {
                                                        treeRemoteTreeView_valueChanged(e);
                                                    }
                                                } );
    treeRemoteTreeView.addFocusListener(new FocusAdapter() {
                                            public void focusLost(FocusEvent e) {
                                                 treeRemoteTreeView_focusLost();
                                                 
                                            }

                                            public void focusGained(FocusEvent e) {
                                                  treeRemoteTreeView_focusGained();
                                            }
                                        } );
    

    //Scroll Pane  for Remote Treeview 
    scrpRemoteTreeView.getViewport().add(treeRemoteTreeView, null);
    scrpRemoteTreeView.setBorder(BorderFactory.createEmptyBorder());
    
    //Panel for Remote Treeview
    jpRemoteTreeView.setLayout(new BorderLayout());
    jpRemoteTreeView.setPreferredSize(new Dimension(249, 279));
    jpRemoteTreeView.add(scrpRemoteTreeView, BorderLayout.CENTER);
    jpRemoteTreeView.setBorder(BorderFactory.createEmptyBorder());
    
    //Remote Listview  
    tblRemoteFolderFileList.setShowHorizontalLines(false);
    tblRemoteFolderFileList.setShowVerticalLines(false);
    tblRemoteFolderFileList.addKeyListener(new KeyAdapter() {
                                               public void keyReleased(KeyEvent e) {
                                                     tblRemoteFolderFileList_keyReleased(e);
                                               }
                                           } );
    tblRemoteFolderFileList.addFocusListener(new FocusAdapter() {
                                                 public void focusGained(FocusEvent e) {
                                                        tblRemoteFolderFileList_focusGained();
                                                 }

                                                 public void focusLost(FocusEvent e) {
                                                       tblRemoteFolderFileList_focusLost();
                                                 }
                                             } );
    tblRemoteFolderFileList.addMouseListener(new MouseAdapter() {
                                                 public void mouseReleased(MouseEvent e) {
                                                       logger.debug("mouse drag event fire");
                                                       tblRemoteFolderFileList_mouseClicked(e);
                                                 }
                                             } );
    tblRemoteFolderFileList.setDragEnabled(true); 
    tblRemoteFolderFileList.setTransferHandler(new TableTransferHandler());
    tblRemoteFolderFileList.setBorder(BorderFactory.createEmptyBorder());
    
    //Scrollpane for Remote Listview  
    scrpRemoteFolderFileList.getViewport().add(tblRemoteFolderFileList, null);
    scrpRemoteFolderFileList.setBorder(BorderFactory.createEmptyBorder());
    
    //Panel for Remote Listview
    jpRemoteFolderFileList.setLayout(new BorderLayout());
    jpRemoteFolderFileList.setBounds(new Rectangle(252, 1, 761, 272));
    jpRemoteFolderFileList.setBorder(BorderFactory.createEmptyBorder());
    jpRemoteFolderFileList.add(scrpRemoteFolderFileList, BorderLayout.CENTER);
    

    //Split Panel for Remote File Browser
    splpRemoteSystem.setDividerLocation(250);
    splpRemoteSystem.setDividerSize(6);
    splpRemoteSystem.setBounds(new Rectangle(0, 30, 1016, 302));
    splpRemoteSystem.setBorder(BorderFactory.createEmptyBorder());
    splpRemoteSystem.add(jpRemoteTreeView, JSplitPane.TOP);
    splpRemoteSystem.add(jpRemoteFolderFileList, JSplitPane.BOTTOM);
    
    
    //Panel for Remote File Browser 
    jpRemoteSystem.setLayout(new BorderLayout());
    jpRemoteSystem.setBounds(new Rectangle(1, 1, 1016, 332));
    jpRemoteSystem.setPreferredSize(new Dimension(506, 311));
    jpRemoteSystem.setMinimumSize(new Dimension(506, 62));
    jpRemoteSystem.setBorder(BorderFactory.createEmptyBorder());
    jpRemoteSystem.add(jpToolBarRemote, BorderLayout.NORTH);
    jpRemoteSystem.add(splpRemoteSystem, BorderLayout.CENTER);
    
    this.setBorder(BorderFactory.createEmptyBorder());
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(tbRemoteSystem, BorderLayout.NORTH); 
    this.getContentPane().add(jpRemoteSystem, BorderLayout.CENTER);
  
    ActionListener actionListener4Controls = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(e.getSource()==cutMenuItem){
          remoteCutOperation();
        }else if(e.getSource()==copyMenuItem){  
          remoteCopyOperation();
        }else if(e.getSource()==pasteMenuItem){
          remotePasteOperation();
        }else if(e.getSource()==renameMenuItem){
          remoteRenameOperation();
        }else if(e.getSource()==refreshMenuItem){
          remoteRefreshOperation();
        }else if(e.getSource()==newFolderMenuItem){
          remoteNewFolderOperation();
        }else if(e.getSource()==deleteMenuItem){
          remoteDeleteOperation();
        }else if(e.getSource()==propertyMenuItem){
          remotePropertyOperation();
        }else if(e.getSource()==downloadMenuItem){
          remoteDownloadOperation();
        }
        
      }
    };
    
    cutMenuItem.setFont(new FsFont());
    cutMenuItem.setIcon(FsImage.imageMenuCut);
    cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK, false));
    cutMenuItem.addActionListener(actionListener4Controls);
    popup.add(cutMenuItem);

    copyMenuItem.setFont(new FsFont());
    copyMenuItem.setIcon(FsImage.imageMenuCopy);
    copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK, false));
    copyMenuItem.addActionListener(actionListener4Controls);
    popup.add(copyMenuItem);
    
    pasteMenuItem.setFont(new FsFont());
    pasteMenuItem.setIcon(FsImage.imageMenuPaste);
    pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK, false));
    pasteMenuItem.addActionListener(actionListener4Controls);
    popup.add(pasteMenuItem);
    popup.addSeparator();
    
    newFolderMenuItem.setFont(new FsFont());
    newFolderMenuItem.setIcon(FsImage.imageMenuNewFolder);
    newFolderMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK, false));
    newFolderMenuItem.addActionListener(actionListener4Controls);
    popup.add(newFolderMenuItem);
    
    renameMenuItem.setFont(new FsFont());
    renameMenuItem.setIcon(FsImage.imageMenuRename);
    renameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
    renameMenuItem.addActionListener(actionListener4Controls);
    popup.add(renameMenuItem);
    popup.addSeparator();
    
    deleteMenuItem.setFont(new FsFont());
    deleteMenuItem.setIcon(FsImage.imageMenuDelete);
    deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false));
    deleteMenuItem.addActionListener(actionListener4Controls);
    popup.add(deleteMenuItem);
    
    refreshMenuItem.setFont(new FsFont());
    refreshMenuItem.setIcon(FsImage.imageMenuRefresh);
    refreshMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false));
    refreshMenuItem.addActionListener(actionListener4Controls);
    popup.add(refreshMenuItem);
    popup.addSeparator();
    
    downloadMenuItem.setFont(new FsFont());
    downloadMenuItem.setIcon(FsImage.imageMenuDownload);
    downloadMenuItem.addActionListener(actionListener4Controls);
    popup.add(downloadMenuItem);
    popup.addSeparator();
    
    propertyMenuItem.setFont(new FsFont());
    propertyMenuItem.setIcon(FsImage.imageMenuProperty);
    propertyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,Event.ALT_MASK, false));
    propertyMenuItem.addActionListener(actionListener4Controls);
    popup.add(propertyMenuItem);
    popup.setBorder(BorderFactory.createEtchedBorder());
    
    PopupListener pl = new PopupListener();
    tblRemoteFolderFileList.addMouseListener(pl);
    treeRemoteTreeView.addMouseListener(pl);
  
  }

  

 private void disableButtonOperation(){
   logger.debug("inside disable menu item if no row selected.......................");
   butRemoteNewFolder.setEnabled(false);
   butRemoteRenameFolderFile.setEnabled(false);
   butRemoteDelete.setEnabled(false);
   butRemoteProperty.setEnabled(false);
   butRemoteCut.setEnabled(false);
   butRemoteCopy.setEnabled(false);
   if(clipBoardRemote == null){
     butRemotePaste.setEnabled(false);
   }
   
   butRemoteDownload.setEnabled(false);
 }
 
 private void enableButtonOperation(){
   butRemoteNewFolder.setEnabled(true);
   butRemoteRenameFolderFile.setEnabled(true);
   butRemoteDelete.setEnabled(true);
   butRemoteProperty.setEnabled(true);
   butRemoteCut.setEnabled(true);
   butRemoteCopy.setEnabled(true);
   butRemoteDownload.setEnabled(true);
 }

  public void setFsClient(FsClient fsClient) {
    this.fsClient = fsClient;
  }

  private  class PopupListener extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
    //System.out.println("in mouse Pressed event..............................................");
     int selectRows[];
     int selectRowCount;
      
     selectRows=tblRemoteFolderFileList.getSelectedRows();
     selectRowCount=selectRows.length;
     if(selectRowCount==0){
       disablePopUp();
     }else{
       enablePopUp();
     }
      maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      //System.out.println("in mouse Released  event.............................................");
       int selectRows[];
       int selectRowCount;
       selectRows=tblRemoteFolderFileList.getSelectedRows();
       selectRowCount=selectRows.length;
       if(selectRowCount==0){
         disablePopUp();
       }else{
         enablePopUp();
       }
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()){
        popup.show((e.getComponent()), e
                   .getX(), e.getY());
      }
    }

    private void disablePopUp() {
      cutMenuItem.setEnabled(false);
      copyMenuItem.setEnabled(false);
      pasteMenuItem.setEnabled(false);
      renameMenuItem.setEnabled(false);
      newFolderMenuItem.setEnabled(false);
      deleteMenuItem.setEnabled(false);
      downloadMenuItem.setEnabled(false);
      propertyMenuItem.setEnabled(false);
    }

    private void enablePopUp() {
      cutMenuItem.setEnabled(true);
      copyMenuItem.setEnabled(true);
     // pasteMenuItem.setEnabled(true);
      renameMenuItem.setEnabled(true);
      newFolderMenuItem.setEnabled(true);
      deleteMenuItem.setEnabled(true);
      downloadMenuItem.setEnabled(true);
      propertyMenuItem.setEnabled(true);
    
    }
  }
  
  public void initialVisualSettings() {
    logger.debug("Visual settings initialized");
    scrpRemoteFolderFileList.getViewport().setBackground(Color.WHITE);
    initializeRemoteFolderFileList();    
  }
  
  
  
  /**
   * Initially customizes the table which displays the remote file and folder list.
   * Hides the columns containing non displayable information.
   */
  private void initializeRemoteFolderFileList(){
    setWaitCursor4Remote();
    int itemLength = 1;
    Vector dataRow = new Vector((int)itemLength);

    fsTableModelRemote = new FsTableModel(EnumRemoteTable.COLUMN_NAMES,dataRow);
    tblRemoteFolderFileList.setModel(fsTableModelRemote);
    tblRemoteFolderFileList.setAutoCreateColumnsFromModel(false);
    tblRemoteFolderFileList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    
    //set default cell renderer
    FsTableCellRenderer fsTableCellRenderer = new FsTableCellRenderer();
    tblRemoteFolderFileList.setDefaultRenderer(JLabel.class,fsTableCellRenderer);
    
    selectionColor = tblRemoteFolderFileList.getSelectionBackground();

    //add click event listener for table header
    JTableHeader tableHeader = tblRemoteFolderFileList.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(),18));
    remoteTableHeaderMouseListener=new RemoteTableColumnHeaderListener();
    tableHeader.addMouseListener(remoteTableHeaderMouseListener);

    tableHeader.getColumnModel().getColumn(EnumRemoteTable.NAME).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumRemoteTable.COLUMN_NAMES[EnumRemoteTable.NAME]));
    tableHeader.getColumnModel().getColumn(EnumRemoteTable.SIZE).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumRemoteTable.COLUMN_NAMES[EnumRemoteTable.SIZE]));
    tableHeader.getColumnModel().getColumn(EnumRemoteTable.TYPE).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumRemoteTable.COLUMN_NAMES[EnumRemoteTable.TYPE]));
    tableHeader.getColumnModel().getColumn(EnumRemoteTable.MODIFIED).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumRemoteTable.COLUMN_NAMES[EnumRemoteTable.MODIFIED]));                                                                        
    tableHeader.getColumnModel().getColumn(EnumRemoteTable.EMPTY_COLUMN).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumRemoteTable.COLUMN_NAMES[EnumRemoteTable.EMPTY_COLUMN]));

    DefaultTableColumnModel colModel = (DefaultTableColumnModel)tblRemoteFolderFileList.getColumnModel();
    colModel.getColumn(EnumRemoteTable.NAME).setPreferredWidth(200);
    colModel.getColumn(EnumRemoteTable.SIZE).setPreferredWidth(100);
    colModel.getColumn(EnumRemoteTable.TYPE).setPreferredWidth(150);
    colModel.getColumn(EnumRemoteTable.EMPTY_COLUMN).setPreferredWidth(150);
    colModel.getColumn(EnumRemoteTable.MODIFIED).setPreferredWidth(175);
    colModel.getColumn(EnumRemoteTable.DESCRIPTION).setPreferredWidth(150);
    colModel.getColumn(EnumRemoteTable.PERMISSION).setPreferredWidth(150);

  
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
    setDefaultCursor4Remote();                                                                      
  }
  
  
  
  
  /**
   * fills the remote folderlist with specified items and sorts the list by name.
   * @param objects
   */
  public void fillRemoteFolderFileList(Object[] objects){
    logger.debug("In the function fillRemoteFolderFileList");
    setWaitCursor4Remote();
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
   // tfStatus.setText(itemLength + " Object(s)");
    mdiParent.fsStatusBar.setLblSetMessage("");
    mdiParent.fsStatusBar.setLblSetMessage(itemLength + " Object(s)");
    dataRow = new Vector((int)itemLength);
    Vector dataCol;
    
    FsObjectHolder fsObjectHolder;
    FsFolderHolder fsFolderHolder;
    FsFileHolder fsFileHolder;
    
    logger.debug("Preparing data vector");
    
    for(int index = 0; index < itemLength; index++){
      dataCol = new Vector(EnumRemoteTable.COLUMN_LENGTH);
      for(int counter=0; counter < EnumLocalTable
                     .COLUMN_LENGTH; counter++){
        dataCol.add(counter, "");
      }
      
      fsObjectHolder = (FsObjectHolder)objects[index];
      if(fsObjectHolder instanceof FsFolderHolder){
        fsFolderHolder = (FsFolderHolder)fsObjectHolder;
        file = new File(fsFolderHolder.getName());
        dataCol.set(EnumRemoteTable.NAME,new JLabel(fsFolderHolder.getName(),FsImage.imgFolderClosed,JLabel.LEFT));
        dataCol.set(EnumRemoteTable.SIZE,new JLabel("4.0 KB",JLabel.RIGHT));
        dataCol.set(EnumRemoteTable.ABS_SIZE,new JLabel("4096"));
        dataCol.set(EnumRemoteTable.TYPE,new JLabel("Directory" + "  "));
        dataCol.set(EnumRemoteTable.MODIFIED,new JLabel(dbsentry.filesync.clientgui.utility.GeneralUtil.getDateForDisplay(fsFolderHolder.getModifiedDate()) + "  " ));
        
        dataCol.set(EnumRemoteTable.ABS_MODIFIED,new JLabel(fsFolderHolder.getModifiedDate().getTime()+" "));
        if(fsFolderHolder.getDescription() == null){
          dataCol.set(EnumRemoteTable.DESCRIPTION,new JLabel("  " ) );
        }else{
          dataCol.set(EnumRemoteTable.DESCRIPTION,new JLabel(fsFolderHolder.getDescription() + "  " ) );
        }
        dataCol.set(EnumRemoteTable.PERMISSION,new JLabel(""));
        dataCol.set(EnumRemoteTable.ITEM_TYPE,new JLabel("Folder"));
        dataCol.set(EnumRemoteTable.ABS_PATH, new JLabel(fsFolderHolder.getPath()));
      }else{
        fsFileHolder = (FsFileHolder)fsObjectHolder;
        String fileName = fsFileHolder.getName();
        
        dataCol.set(EnumRemoteTable.NAME, new JLabel(fsFileHolder.getName(),this.getFileIcon(fileName),JLabel.LEFT));
        dataCol.set(EnumRemoteTable.SIZE, new JLabel("  " + dbsentry.filesync.clientgui.utility.GeneralUtil.getDocSizeForDisplay(fsFileHolder.getSize()) + "  ",JLabel.RIGHT));
        
        dataCol.set(EnumRemoteTable.ABS_SIZE, new JLabel("  " +fsFileHolder.getSize() + "  ",JLabel.RIGHT));
        dataCol.set(EnumRemoteTable.TYPE, new JLabel(fsFileHolder.getMimeType() + "  "));
        dataCol.set(EnumRemoteTable.MODIFIED, new JLabel(dbsentry.filesync.clientgui.utility.GeneralUtil.getDateForDisplay(fsFileHolder.getModifiedDate()) + "  "));        
        dataCol.set(EnumRemoteTable.ABS_MODIFIED, new JLabel(fsFileHolder.getModifiedDate().getTime() + "  "));
        if(fsFileHolder.getDescription() == null){
          dataCol.set(EnumRemoteTable.DESCRIPTION, new JLabel("  "));
        }else{
          dataCol.set(EnumRemoteTable.DESCRIPTION, new JLabel(fsFileHolder.getDescription() + "  "));
        }
        dataCol.set(EnumRemoteTable.PERMISSION, new JLabel(""));
        dataCol.set(EnumRemoteTable.ITEM_TYPE, new JLabel("File"));
        dataCol.set(EnumRemoteTable.ABS_PATH, new JLabel(fsFileHolder.getPath()));
      }
      
      dataRow.add(dataCol);
    }
    
    logger.debug("Setting data vector in the table model");
    fsTableModelRemote = (FsTableModel) tblRemoteFolderFileList.getModel();
    fsTableModelRemote.setDataVector(dataRow);
    dbsentry.filesync.clientgui.utility.GeneralUtil.sortAllRowsBy(tblRemoteFolderFileList,EnumRemoteTable
                                                                      .NAME,remoteTableHeaderMouseListener.isSortOrderNameAsc());
    setDefaultCursor4Remote();
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

   /**
    * Gives a raised effect to button when mouse entered init.
    * @param e MouseEvent object
    */
   private void button_mouseEntered(MouseEvent e) {
     JButton button = (JButton)e.getSource();
     if(button.isEnabled()){
        button.setBorder(BorderFactory.createEtchedBorder());
     }
   }

   
   /**
    * Makes button border normal when mouse exited from it.
    * @param e MouseEvent object
    */
   private void button_mouseExited(MouseEvent e) {
     JButton button = (JButton)e.getSource();
     button.setBorder(normalButtonBorder);
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
   
   
  private void comboRemoteAdressBar_keyReleased(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_ENTER){
      searchRemotePath();
    }
  }
  
  private void comboRemoteAdressBar_actionPerformed(ActionEvent e) {
    if(e.getModifiers() == KeyEvent.VK_SHIFT){
      searchRemotePath();    
    }
  }
  
  private void button_mouseEnteredNoFX(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
  }
  
  private void button_mouseExitedNoFX(MouseEvent e) {
    JButton button = (JButton)e.getSource();
    button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
  }
  
//  public void setWaitCursorForRemoteBrowser() {
//    cursorCounterRemote++;
//    jpRemoteSystem.setCursor(waitCursor);
//  }
  
  public void setRemoteCommandListener(){
      fsClient.setRemoteCommandListener(new RemoteCommandListener());
      remoteWillTreeExpansionListener = new RemoteWillTreeExpansionListener(fsClient,treeRemoteTreeView,FsRemoteOperationConstants.COMMAND);
      //setWaitCursorForRemoteBrowser();
  } 
  
  /** ------------------------------Function Related to Remote ToolBar Button------------------------------*/



   /**
    * Navigates back in remote browser.
    */
   private void butRemoteNavigateBack_actionPerformed() {
     //setWaitCursorForRemoteBrowser();
     setWaitCursor4Remote();
     fsClient.navigateBack();
   }

    /**
     * Navigates forward in remote browser.
     */
    private void butRemoteNavigateForward_actionPerformed() {
      //setWaitCursorForRemoteBrowser();
      setWaitCursor4Remote();
      fsClient.navigateForward();
    }
    
    /**
     * Navigates up to the parent of current folder in remote browser.
     */
    private void butRemoteNavigateUp_actionPerformed() {
      //setWaitCursorForRemoteBrowser();
      butRemoteNavigateUp.setEnabled(false);
      setWaitCursor4Remote();
      fsClient.navigateUp();
    }
    
    /**
     * Refreshes the current folderpath(treeview and folderlist).
     */
    private void butRemoteRefresh_actionPerformed() {
      remoteRefreshOperation();
    }

    private void butRemoteHome_actionPerformed() {
        setWaitCursor4Remote();
        remoteHomeOperation();
    }
    /**
     * Performs the create new folder operation in remote filesystem and refreshes the 
     * treeview and folderlist to show the same.
     */
    public void butRemoteNewFolder_actionPerformed() {
      remoteNewFolderOperation();
    }
    
    
    public void remoteNewFolderOperation(){
      String folderName  = JOptionPane.showInputDialog(this,"Folder","New Folder");
      if(folderName != null && !folderName.trim().equals("")){
        //setWaitCursorForRemoteBrowser();
        
        fsClient.makedir(fsFolderDocInfoHolder.getCurrentFolderPath(), folderName.trim(),FsRemoteOperationConstants.COMMAND);
        //setWaitCursorForRemoteBrowser();
        setWaitCursor4Remote();
        fsClient.getFlatFolderTree(fsFolderDocInfoHolder.getCurrentFolderPath(), null,FsRemoteOperationConstants.COMMAND);
        //setWaitCursorForRemoteBrowser();
        setWaitCursor4Remote();
        fsClient.getContentOfFolder(fsFolderDocInfoHolder.getCurrentFolderPath());
      }  
    
    }

    
    /**
     * confirms the delete operation and stores the items to be deleted in an array.Calls
     * a function to perform actual delete.
     */
    private void butRemoteDelete_actionPerformed() {
      remoteDeleteOperation();
    }
    

    /**
     * To rename a folder/file from remote filesystem.
     */
    private void butRemoteRenameFolderFile_actionPerformed() {
      remoteRenameOperation();
    }
    
    
    /**
     * visualizes the propertypage of selected items from remote browser.
     */
    public void butRemoteProperty_actionPerformed() {
     remotePropertyOperation();
    }
    
    public void remotePropertyOperation(){
      int selectedRows[] = tblRemoteFolderFileList.getSelectedRows();
      int selectedRowsCount = selectedRows.length;
      String itemPath="";
      String itemPaths[] ;
      
      if(selectedRowsCount!= 0){
       itemPaths = new String[selectedRowsCount] ;
        for(int index = 0; index < selectedRowsCount; index++){
          itemPath = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                                  .ABS_PATH)).getText();
          itemPaths[index] = itemPath;
        }
        //setWaitCursorForRemoteBrowser();
        setWaitCursor4Remote();
        fsClient.getProperties(itemPaths);
      }else{
        String nodePath = "";
        FsFolderHolder fsFolderHolder = new FsFolderHolder();
        TreePath treePath = treeRemoteTreeView.getSelectionPath();
        logger.debug(" TreePath : " + treePath);
        
        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();  
        logger.debug(" currentNode : " + currentNode.toString());
        int currentNodeLevel = currentNode.getLevel();
        logger.debug("Level " + currentNodeLevel);
        fsFolderHolder =  (FsFolderHolder)currentNode.getUserObject();
        nodePath = fsFolderHolder.getPath();
        itemPath=nodePath;
        itemPaths = new String[1];
        itemPaths[0]=itemPath;
        //setWaitCursorForRemoteBrowser();
         setWaitCursor4Remote();
        fsClient.getProperties(itemPaths);
      }
    }
    

    /**
     * stores selected itempaths to cut, in a global array to use it in paste operation
     */
    private void butRemoteCut_actionPerformed() {
      remoteCutOperation();
    }

    /**
     * stores selected itempaths to copy, in a global array to use it in paste operation
     */
    private void butRemoteCopy_actionPerformed() {
      remoteCopyOperation();
    }

    /**
     * performs the paste operation depending upon whichever is the clipboard operation(cut/copy)
     */
    private void butRemotePaste_actionPerformed(){
      remotePasteOperation();
    }
    
    /**
     * stores the items to be uploaded in an array and calls the function to perform actual download
     */
    private void butRemoteDownload_actionPerformed() {
      remoteDownloadOperation();    
    }
    
    
    public void remoteDownloadOperation(){
      
        folderDocInfoClient = mdiParent.getExplorer().getFsLocalView().getFolderDocInfoClient();
        int selectedRows[] = tblRemoteFolderFileList.getSelectedRows();
        int selectedRowsCount = selectedRows.length;
        String itemName="";
        String itemPath="";
        //initialize the overwrite value to 0
        if(selectedRowsCount != 0){
          
            //get items to delete
             //String itemPaths[];
            for(int index = 0; index < selectedRowsCount; index++){
              String itemPaths[] = new String[1];
              itemName = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                                      .NAME)).getText();
              itemPath = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                                      .ABS_PATH)).getText();
              itemPaths[0] = itemPath;
              int downloadCode=new Random().nextInt();
              
              logger.debug("folderDocInfoClient.getCurrentFolderPath()......................."+folderDocInfoClient.getCurrentFolderPath());
              logger.debug("fsFolderDocInfoHolder.getCurrentFolderPath()........................"+fsFolderDocInfoHolder.getCurrentFolderPath());
              fsClient.download((mdiParent.getUploadDownloadManager().createDownloadListener(downloadCode)), itemPaths, folderDocInfoClient.getCurrentFolderPath(), fsFolderDocInfoHolder.getCurrentFolderPath(), downloadCode);
            }
            if(mdiParent.showUploadDnloadManager){
              mdiParent.popUploadDownloadManager();
            }
           
        }else{
//          String nodePath = "";
//          FsFolderHolder fsFolderHolder = new FsFolderHolder();
//          TreePath treePath = treeRemoteTreeView.getSelectionPath();
//          logger.debug(" TreePath : " + treePath);
//          
//          DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();  
//          logger.debug(" currentNode : " + currentNode.toString());
//          int currentNodeLevel = currentNode.getLevel();
//          logger.debug("Level " + currentNodeLevel);
//          fsFolderHolder =  (FsFolderHolder)currentNode.getUserObject();
//          nodePath = fsFolderHolder.getPath();
//          itemPath=nodePath;
//          itemPaths = new String[1];
//          itemPaths[0]=itemPath;
//         
//          if(mdiParent.showUploadDnloadManager){
//            mdiParent.popUploadDownloadManager();
//          }
//          int downloadCode=new Random().nextInt();
//          remoteTreeRefreshOperation();
//          fsClient.download((mdiParent.getUploadDownloadManager().createDownloadListener(downloadCode)), itemPaths, folderDocInfoClient.getCurrentFolderPath(), fsFolderDocInfoHolder.getCurrentFolderPath(), downloadCode);
        } 
      
      
    }
    
    /**
     * searches the path specified in addressbar, if it already arrived then just highlights 
     * it if not then sends a request through a function call.
     */
    private void butRemoteGo_actionPerformed(){
      searchRemotePath();
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
      logger.debug("nodePath is ..............................."+ nodePath);
      
      if(currentNodeLevel != 0 ){
        if(fsFolderDocInfoHolder == null){
         // setWaitCursorForRemoteBrowser();
          fsClient.getContentOfFolder(nodePath);
        }else if(!fsFolderDocInfoHolder.getCurrentFolderPath().equals(nodePath)){
         // setWaitCursorForRemoteBrowser();
          fsClient.getContentOfFolder(nodePath);
        }
      }
    }
    
    private void treeRemoteTreeView_focusLost() {
      DefaultTreeCellRenderer tcr = (DefaultTreeCellRenderer)treeRemoteTreeView.getCellRenderer();
      tcr.setBackgroundSelectionColor(new Color(204,204,204));
    }
    

    private void treeRemoteTreeView_focusGained() {
     DefaultTreeCellRenderer tcr = (DefaultTreeCellRenderer)treeRemoteTreeView.getCellRenderer();
     tcr.setBackgroundSelectionColor(selectionColor);
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
    
      
    private void tblRemoteFolderFileList_mouseClicked(MouseEvent e) {
    logger.debug("Mouse clicked on Remote table ");
      
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
           // tfStatus.setText("Fetching content of folder " + absFolderPath);
            mdiParent.fsStatusBar.setLblSetMessage("");
            mdiParent.fsStatusBar.setLblSetMessage("Fetching content of folder " + absFolderPath);
            
            //setWaitCursorForRemoteBrowser();
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
             // setWaitCursorForRemoteBrowser();

              fsClient.getFlatFolderTree(absFolderPath, null,FsRemoteOperationConstants.COMMAND);
            }
          }catch(Exception ex){
            logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
          }
        }
      }else{
        
        int selectedRows[] = table.getSelectedRows();
        int selectedRowsCount = selectedRows.length;
        if(selectedRowsCount==0){
          logger.debug("disabling the button objects.....................");
          disableButtonOperation();
          
          return ;
        }
        logger.debug(" selectedRowsCount " + selectedRowsCount);
        if(selectedRows != null){
          if(selectedRowsCount >= 1){
            //tfStatus.setText(selectedRowsCount + " Object(s) selected");
             mdiParent.fsStatusBar.setLblSetMessage("");
             mdiParent.fsStatusBar.setLblSetMessage(selectedRowsCount + " Object(s) selected");
            
            enableButtonOperation();
            
          }else{
            int vRowIndex = table.getSelectedRow();
            
            if(((JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable
                                                           .ITEM_TYPE)).getText().equals("Folder")){
              //tfStatus.setText(selectedRowsCount + " Object(s) selected");
               mdiParent.fsStatusBar.setLblSetMessage("");
               mdiParent.fsStatusBar.setLblSetMessage(selectedRowsCount + " Object(s) selected");
            }else{
              String type = ((JLabel)fsTableModel.getValueAt(vRowIndex,EnumLocalTable
                                                                         .TYPE)).getText();
              String size = ((JLabel)fsTableModel.getValueAt(vRowIndex,EnumLocalTable
                                                                         .SIZE)).getText();
             // tfStatus.setText("Type: " + type + "Size:" + size);
              mdiParent.fsStatusBar.setLblSetMessage("");
              mdiParent.fsStatusBar.setLblSetMessage("Type: " + type + "Size:" + size);
            }
          }  
        }
      }
    }

    
    private void tblRemoteFolderFileList_focusGained() {
      logger.debug("Remote table got Focus");
      tblRemoteFolderFileList.setSelectionBackground(selectionColor);
    }
    
    
    private void tblRemoteFolderFileList_focusLost() {
      logger.debug("Remote table Lost Focus");
      tblRemoteFolderFileList.setSelectionBackground(new Color(204,204,204));
       
    }
    
  public void remoteCutOperation(){
    currClipBoardOperationRemote =  EnumClipBoardOperation.CUT;
    String itemPaths[];
    int selectedRows[] = tblRemoteFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName="";
    String itemPath="";
    
    if(selectedRowsCount != 0){
      
    
    itemPaths = new String[selectedRowsCount];
    for(int index = 0; index < selectedRowsCount; index++){
      itemName = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                              .NAME)).getText();
      itemPath = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[index],EnumRemoteTable
                                                              .ABS_PATH)).getText();
      itemPaths[index] = itemPath;
    }
    
    clipBoardRemote = itemPaths;
      butRemotePaste.setEnabled(true);
      pasteMenuItem.setEnabled(true);
    }else{
      String nodePath = "";
      FsFolderHolder fsFolderHolder = new FsFolderHolder();
      TreePath treePath = treeRemoteTreeView.getSelectionPath();
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
      itemPath=nodePath;
      itemPaths = new String[1];
      itemPaths[0]=itemPath;
      clipBoardRemote = itemPaths;
      butRemotePaste.setEnabled(true);
      pasteMenuItem.setEnabled(true);
    }
    
  }
  
  public void remoteCopyOperation(){
    currClipBoardOperationRemote = EnumClipBoardOperation.COPY;
     
     int selectedRows[] = tblRemoteFolderFileList.getSelectedRows();
     int selectedRowsCount = selectedRows.length;
     String itemName="";
     String itemPath="";
     String itemPaths[];
     
    if(selectedRowsCount != 0){
    
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
      butRemotePaste.setEnabled(true);
      pasteMenuItem.setEnabled(true);
    }else{
      String nodePath = "";
      FsFolderHolder fsFolderHolder = new FsFolderHolder();
      TreePath treePath = treeRemoteTreeView.getSelectionPath();
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
      itemPath=nodePath;
      itemPaths = new String[1];
      clipBoardRemote = new String[1];
      itemPaths[0]=itemPath;
      clipBoardRemote = itemPaths;
      butRemotePaste.setEnabled(true);
      pasteMenuItem.setEnabled(true);
    
    }
  }
  
  public void remotePasteOperation(){
    if(clipBoardRemote == null){
      butRemotePaste.setEnabled(false);
      pasteMenuItem.setEnabled(false);
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
        //fsFileSystemOperationsRemote.itemMove(mdiParent,fsFolderDocInfoHolder.getCurrentFolderPath(), clipBoardRemote,FsMessage.FOR_CLIENTGUI);
        //TODO Attention - 
        logger.debug("fsFolderDocInfoHolder.getCurrentFolderPath().....In cut operation "+fsFolderDocInfoHolder.getCurrentFolderPath());
         int remoteMoveCode=new Random().nextInt();
         fsClient.move((new RemoteMoveListener(remoteMoveCode)), fsFolderDocInfoHolder.getCurrentFolderPath(), clipBoardRemote, remoteMoveCode);
        
      }else{
        logger.debug("Current ClipBoard Operation : " + currClipBoardOperationRemote);
        //setWaitCursorForRemoteBrowser();
        //fsFileSystemOperationsRemote.itemCopy(mdiParent,fsFolderDocInfoHolder.getCurrentFolderPath(), clipBoardRemote,FsMessage.FOR_CLIENTGUI);
        //TODO Attention - 
         int remoteCopyCode=new Random().nextInt();
         fsClient.copy((new RemoteCopyListener(remoteCopyCode)), fsFolderDocInfoHolder.getCurrentFolderPath(), clipBoardRemote, remoteCopyCode); 
      }
    }
    
  }
  
  public void remoteRenameOperation(){
    int selectedRow = tblRemoteFolderFileList.getSelectedRow();
    String itemName="";
    String itemPath="";
    
    if(selectedRow >0){
      try{
        itemName = ((JLabel)fsTableModelRemote.getValueAt(selectedRow, EnumRemoteTable
                                                                  .NAME)).getText();
        itemPath = ((JLabel)fsTableModelRemote.getValueAt(selectedRow, EnumRemoteTable
                                                                  .ABS_PATH)).getText();
        
        String newItemName = JOptionPane.showInputDialog(this,"Rename " + itemName,itemName);
        
        if(newItemName != null && !newItemName.trim().equals("")){
         // setWaitCursorForRemoteBrowser();
          fsClient.rename(itemPath, newItemName);
        }    
      }catch(Exception ex){
        JOptionPane.showMessageDialog(this,"Insufficient right to rename " + "\"" + itemName + "\"");    
        logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
      }
    }else{
      String nodePath = "";
      FsFolderHolder fsFolderHolder = new FsFolderHolder();
      TreePath treePath = treeRemoteTreeView.getSelectionPath();
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
      itemName =currentNode+"";
      itemPath = nodePath;
      
      String newItemName = JOptionPane.showInputDialog(this,"Rename " + itemName,itemName);
      if(newItemName != null && !newItemName.trim().equals("")){
       // setWaitCursorForRemoteBrowser();
        fsClient.rename(itemPath, newItemName);
        remoteTreeRefreshOperation();
      }
    
    }
  }
  
  public void remoteDeleteOperation() {
    int selectedRows[] = tblRemoteFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName="";
    String itemPath="";
    String itemPaths[];

    if(selectedRows.length > 0){
      if(selectedRowsCount == 1){
        itemName = ((JLabel)fsTableModelRemote.getValueAt(selectedRows[0],EnumRemoteTable
                                                                  .NAME)).getText();
        int delete = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete item " + "\"" + itemName + "\"" ,"Delete",JOptionPane.YES_NO_OPTION);
        if(delete != JOptionPane.YES_OPTION){
          return;
        }else{
          //tfStatus.setText("Deleting : " + itemName );
           mdiParent.fsStatusBar.setLblSetMessage("");
           mdiParent.fsStatusBar.setLblSetMessage("Deleting : " + itemName);
        }
      }else{
        int delete = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete these " + selectedRowsCount + " items" ,"Delete",JOptionPane.YES_NO_OPTION);
        if(delete != JOptionPane.YES_OPTION){
          return;
        }else{
         // tfStatus.setText("Deleting : " + selectedRowsCount + " items" );
          mdiParent.fsStatusBar.setLblSetMessage("");
          mdiParent.fsStatusBar.setLblSetMessage("Deleting : " + selectedRowsCount + " items"); 
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
      fsClient.delete(itemPaths);
    }else{
      String nodePath = "";
      FsFolderHolder fsFolderHolder = new FsFolderHolder();
      TreePath treePath = treeRemoteTreeView.getSelectionPath();
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
      itemName =currentNode+"";
      itemPath = nodePath;
      itemPaths = new String[1];
      itemPaths[0] = itemPath;
      remoteTreeRefreshOperation();
      int delete = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete item " + "\"" + itemName + "\"" ,"Delete",JOptionPane.YES_NO_OPTION);
      if(delete != JOptionPane.YES_OPTION){
        return;
      }
      //setWaitCursorForRemoteBrowser();
      
      fsClient.delete(itemPaths);
      
    }
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
        fsClient.getContentOfFolder(searchPath);
        TreeNode treeNode[] = arrivedTreeNode.getPath();
        TreePath treePath = new TreePath(treeNode);
        treeRemoteTreeView.setSelectionPath(treePath );
        treeRemoteTreeView.requestFocus();
      }else{ 
        //setWaitCursorForRemoteBrowser();
        fsClient.searchPath(arrivedNodePath, searchPath);
      }
    }else{
      if(splpRemoteSystem.getBottomComponent().equals(jpRemoteFolderFileList)){
        splpRemoteSystem.remove(splpRemoteSystem.getBottomComponent());
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel("Invalid Path",FsImage.iconWarning ,JLabel.CENTER);
        label.setBackground(Color.WHITE);
        panel.add(label,BorderLayout.CENTER);
        splpRemoteSystem.add(panel,JSplitPane.BOTTOM);
      }  
     // tfStatus.setText("Invalid Path");
      mdiParent.fsStatusBar.setLblSetMessage("");
      mdiParent.fsStatusBar.setLblSetMessage("Invalid Path");
    }    
  }
  
  public void remoteRefreshOperation(){
    DefaultMutableTreeNode selectedTreeNode;
    String selectedTreeNodePath = fsFolderDocInfoHolder.getCurrentFolderPath();
    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)((DefaultTreeModel)treeRemoteTreeView.getModel()).getRoot(); 
    selectedTreeNode = dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(rootNode,selectedTreeNodePath);
      
    logger.debug("Selected Component :" + selectedTreeNode);
    logger.debug("Selected Component Path :" + selectedTreeNodePath);

    //setWaitCursorForRemoteBrowser();
    setWaitCursor4Remote();
    fsClient.getFlatFolderTree(selectedTreeNodePath, null,FsRemoteOperationConstants.COMMAND);
    
    //setWaitCursorForRemoteBrowser();
    setWaitCursor4Remote();
    fsClient.getContentOfFolder(selectedTreeNodePath);
  
  }
  
  public void remoteTreeRefreshOperation(){
    
    logger.debug("parentFolderPath..........................................."+parentFolderPath);
    DefaultMutableTreeNode selectedTreeNode;
    String selectedTreeNodePath = fsFolderDocInfoHolder.getCurrentFolderPath();
    logger.debug("selectedTreeNodePath in remotetreerefreshOperation"+selectedTreeNodePath);
    String selectedTreeNodeParentPath = fsFolderDocInfoHolder.getCurrentFolderPathParent();
     
    logger.debug("selectedTreeNode_Parent_Path in remotetreerefreshOperation"+selectedTreeNodeParentPath);
    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)((DefaultTreeModel)treeRemoteTreeView.getModel()).getRoot(); 
    selectedTreeNode = dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(rootNode,selectedTreeNodeParentPath);
      
    logger.debug("Selected Component :" + selectedTreeNode);
    logger.debug("Selected Component Path :" + selectedTreeNodePath);

    
    fsFolderDocInfoHolder .setCurrentFolderPath(selectedTreeNodeParentPath);
    //setWaitCursorForRemoteBrowser();
    setWaitCursor4Remote();
    fsClient.getFlatFolderTree(selectedTreeNodeParentPath, null,FsRemoteOperationConstants.COMMAND);
    
   // setWaitCursorForRemoteBrowser();
    setWaitCursor4Remote();
    fsClient.getContentOfFolder(selectedTreeNodeParentPath);
  
  }
  
  private void remoteHomeOperation(){
    fsClient.getHomeFolder(FsRemoteOperationConstants.COMMAND);
  }

  public void setFsFolderDocInfoHolder(FsFolderDocInfoHolder fsFolderDocInfoHolder) {
    this.fsFolderDocInfoHolder = fsFolderDocInfoHolder;
  }

  public FsFolderDocInfoHolder getFsFolderDocInfoHolder() {
    return fsFolderDocInfoHolder;
  }

  public void setJpRemoteTreeView(JPanel jpRemoteTreeView) {
    this.jpRemoteTreeView = jpRemoteTreeView;
  }

  public JPanel getJpRemoteTreeView() {
    return jpRemoteTreeView;
  }

  public JTable getTblRemoteFolderFileList() {
    return tblRemoteFolderFileList;
  }
  
  public void setWaitCursor4Remote(){
    cursorCounterRemote++;
    this.setCursor(waitCursor);
  }

  public void setDefaultCursor4Remote(){
    cursorCounterRemote =-1;
    if(cursorCounterRemote<=0){
      this.setCursor(defaultCursor);
    }
  }
  
  private class RemoteCopyListener implements FsRemoteCopyListener{
    
    
    private int remoteCopyCode;
    
    private Long totalSizeCopy=null;
    
    private ClientUtil clientUtil;
    
    private Progress copyProgress;
    
    public RemoteCopyListener( int remoteCopyCode) {
      this.remoteCopyCode=remoteCopyCode;
      this.clientUtil=new ClientUtil(logger);
    }
    
    public void propertyChange(PropertyChangeEvent evt){
      int propertyName=Integer.valueOf(evt.getPropertyName());
      FsResponse fsResponse;
      FsExceptionHolder fsExceptionHolder;
      fsResponse = (FsResponse)evt.getNewValue();
      OverwriteOptionDialog overWriteDialog;
      Integer overWriteValue;
      if(fsResponse.getSuperResponseCode().equals(Integer.toString(remoteCopyCode))){
        switch(propertyName){
          case STARTED:
            totalSizeCopy = (Long)fsResponse.getData();
            copyProgress = new Progress(mdiParent, "Copy", false, logger, fsResponse.getSuperResponseCode(), FsRemoteOperationConstants.COPY);
            copyProgress.addPropertyChangeListener(this);
            copyProgress.setOperation("Copying.....");
            copyProgress.setMaxProgress(100);
            copyProgress.setFilePath("");
            copyProgress.setTotalData(totalSizeCopy.longValue());
            copyProgress.setVisible(true);
            break;
          case NEXT_ITEM:
            String copyFilePath = (String)fsResponse.getData();
            Long copyFileSize = (Long)fsResponse.getData1();
            copyProgress.setFilePath(copyFilePath);
            copyProgress.setPrevByteRead(copyFileSize.longValue());
            break;
          case PROMPT_OVERWRITE_FOLDER:
            FsFolderHolder fsFolderHolderDest = (FsFolderHolder)fsResponse.getData();
            FsFolderHolder fsFolderHolderSrc = (FsFolderHolder)fsResponse.getData1();
            overWriteDialog = new OverwriteOptionDialog(mdiParent, "Overwrite Folder", true);
            overWriteDialog.setTaOverwriteMessage("This folder already contains a readonly file named '" + fsFolderHolderDest.getName() + "'");
            overWriteDialog.setLblExistingFileSize("4 KB");
            overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsFolderHolderDest.getModifiedDate());            
            overWriteDialog.setLblReplaceFileSize("4 KB");
            overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFolderHolderSrc.getModifiedDate());
            overWriteDialog.setVisible(true);
            overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
            fsClient.overWriteCopyFolder(overWriteValue, fsResponse.getSuperResponseCode());
            break;
          case PROMPT_OVERWRITE_FILE:
            FsFileHolder fsFileHolderDest = (FsFileHolder)fsResponse.getData();
            FsFileHolder fsFileHolderSrc = (FsFileHolder)fsResponse.getData1();
            overWriteDialog = new OverwriteOptionDialog(mdiParent, "Overwrite File", true);
            overWriteDialog.setTaOverwriteMessage("This folder already contains a readonly file named '" + fsFileHolderDest.getName() + "'");
            overWriteDialog.setLblExistingFileSize(fsFileHolderDest.getSize() + "");
            overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsFileHolderDest.getModifiedDate());
            overWriteDialog.setLblReplaceFileSize(fsFileHolderSrc.getSize() + "");
            overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFileHolderSrc.getModifiedDate());
            overWriteDialog.setVisible(true);
            overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
            fsClient.overWriteCopyFile(overWriteValue, fsResponse.getSuperResponseCode());
            break;
          case FAILED:
            copyProgress.dispose();
            break;
          case COMPLETED:
            copyProgress.dispose();
            String destBasePath = (String)fsResponse.getData();
            Integer clipBoardOperation = (Integer)fsResponse.getData1();
            
            logger.debug("destBasePath : " + destBasePath);
            logger.debug("clipBoardOperation : " + clipBoardOperation);
            
            fsClient.getFlatFolderTree(destBasePath, null, FsRemoteOperationConstants.COMMAND);
            fsClient.getContentOfFolder(destBasePath);
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
             mdiParent.fsStatusBar.setLblSetMessage("");
             mdiParent.fsStatusBar.setLblSetMessage("Access denied");
            copyProgress.dispose();
            JOptionPane.showMessageDialog(mdiParent,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
          }else if(fsExceptionHolder.getErrorCode() == 68005){
            String errorMsg = fsExceptionHolder.getErrorMessage();
            logger.debug("Error Message :" + errorMsg);
            copyProgress.dispose();
            JOptionPane.showMessageDialog(mdiParent, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
          }else{
            copyProgress.dispose();
            JOptionPane.showMessageDialog(mdiParent,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
           // tfStatus.setText("" + fsExceptionHolder);
            mdiParent.fsStatusBar.setLblSetMessage("");
            mdiParent.fsStatusBar.setLblSetMessage(fsExceptionHolder.getErrorMessage());
            //fillRemoteFolderFileList(objects);
          }
          logger.error(fsExceptionHolder);
          break;
        case FETAL_ERROR:
          copyProgress.dispose();
          logger.error("Fetal Error");
          //tfStatus.setText("Fatal Error");
           mdiParent.fsStatusBar.setLblSetMessage("");
           mdiParent.fsStatusBar.setLblSetMessage("Fetal Error");
          break;
        }
      }
    }
  }

  private class RemoteMoveListener implements FsRemoteMoveListener {
    

    private int remoteMoveCode;

    private Long totalSizeCopy = null;

    private ClientUtil clientUtil;

    private Progress moveProgress;

    public RemoteMoveListener( int remoteMoveCode) {
      this.remoteMoveCode = remoteMoveCode;
      this.clientUtil = new ClientUtil(logger);
    }
  
  public void propertyChange(PropertyChangeEvent evt){
    int propertyName=Integer.valueOf(evt.getPropertyName());
    FsResponse fsResponse;
    FsExceptionHolder fsExceptionHolder;
    fsResponse = (FsResponse)evt.getNewValue();
    OverwriteOptionDialog overWriteDialog;
    Integer overWriteValue;
    if(fsResponse.getSuperResponseCode().equals(Integer.toString(remoteMoveCode))){
      switch(propertyName){
        case STARTED:
          totalSizeCopy = (Long)fsResponse.getData();
          moveProgress = new Progress(mdiParent, "Move", false, logger, fsResponse.getSuperResponseCode(), FsRemoteOperationConstants.COPY);
          moveProgress.addPropertyChangeListener(this);
          moveProgress.setOperation("Moving...");
          moveProgress.setMaxProgress(100);
          moveProgress.setFilePath("");
          moveProgress.setTotalData(totalSizeCopy.longValue());
          moveProgress.setVisible(true);
          break;
        case NEXT_ITEM:
          String moveFilePath = (String)fsResponse.getData();
          Long moveFileSize = (Long)fsResponse.getData1();
          moveProgress.setFilePath(moveFilePath);
          moveProgress.setPrevByteRead(moveFileSize.longValue());
          break;
        case PROMPT_OVERWRITE_FOLDER:
          FsFolderHolder fsFolderHolderDest = (FsFolderHolder)fsResponse.getData();
          FsFolderHolder fsFolderHolderSrc = (FsFolderHolder)fsResponse.getData1();
          overWriteDialog = new OverwriteOptionDialog(mdiParent, "Overwrite Folder", true);
          overWriteDialog.setTaOverwriteMessage("This folder already contains a readonly file named '" + fsFolderHolderDest.getName() + "'");
          overWriteDialog.setLblExistingFileSize("4 KB");
          overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsFolderHolderDest.getModifiedDate());            
          overWriteDialog.setLblReplaceFileSize("4 KB");
          overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFolderHolderSrc.getModifiedDate());
          overWriteDialog.setVisible(true);
          overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
          fsClient.overWriteMoveFolder(overWriteValue, fsResponse.getSuperResponseCode());
          break;
        case PROMPT_OVERWRITE_FILE:
          FsFileHolder fsFileHolderDest = (FsFileHolder)fsResponse.getData();
          FsFileHolder fsFileHolderSrc = (FsFileHolder)fsResponse.getData1();
          overWriteDialog = new OverwriteOptionDialog(mdiParent, "Overwrite File", true);
          overWriteDialog.setTaOverwriteMessage("This folder already contains a readonly file named '" + fsFileHolderDest.getName() + "'");
          overWriteDialog.setLblExistingFileSize(fsFileHolderDest.getSize() + "");
          overWriteDialog.setLblExistingFileModifiedDate("Modified : " + fsFileHolderDest.getModifiedDate());
          overWriteDialog.setLblReplaceFileSize(fsFileHolderSrc.getSize() + "");
          overWriteDialog.setLblReplaceFileModifiedDate("Modified : " + fsFileHolderSrc.getModifiedDate());
          overWriteDialog.setVisible(true);
          overWriteValue = new Integer(overWriteDialog.getOverWriteValue());
          fsClient.overWriteMoveFile(overWriteValue, fsResponse.getSuperResponseCode());
          break;
        case FAILED:
          moveProgress.dispose();
          break;
        case COMPLETED:
          moveProgress.dispose();
          String srcBasePath = (String)fsResponse.getData1();
          Integer clipBoardOperation = (Integer)fsResponse.getData2();
          logger.debug("srcBasePath : " + srcBasePath);
          logger.debug("clipBoardOperation : " + clipBoardOperation);
          fsClient.getFlatFolderTree(srcBasePath, null, FsRemoteOperationConstants.COMMAND);
          
          String destBasePath = (String)fsResponse.getData();
          fsClient.getFlatFolderTree(destBasePath, null, FsRemoteOperationConstants.COMMAND);
          fsClient.getContentOfFolder(destBasePath);
          break;
        case ERROR_MESSAGE:
        logger.debug("Error message from clent to client Gui................");
        //Object objects[] = fsResponse.getDatas();
        fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
        fsExceptionHolder = fsResponse.getFsExceptionHolder();
        if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
          || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
          || fsExceptionHolder.getErrorCode() == 30041){
         // tfStatus.setText("Access denied");
          mdiParent.fsStatusBar.setLblSetMessage("");
          mdiParent.fsStatusBar.setLblSetMessage("Access denied");
          moveProgress.dispose();
          JOptionPane.showMessageDialog(mdiParent,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }else if(fsExceptionHolder.getErrorCode() == 68005){
          String errorMsg = fsExceptionHolder.getErrorMessage();
          logger.debug("Error Message :" + errorMsg);
          moveProgress.dispose();
          JOptionPane.showMessageDialog(mdiParent, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
        }else{
          moveProgress.dispose();
          JOptionPane.showMessageDialog(mdiParent,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
          //tfStatus.setText("" + fsExceptionHolder);
           mdiParent.fsStatusBar.setLblSetMessage("");
           mdiParent.fsStatusBar.setLblSetMessage(fsExceptionHolder.getErrorMessage());
          //fillRemoteFolderFileList(objects);
        }
        logger.error(fsExceptionHolder);
        break;
      case FATEL_ERROR:
        moveProgress.dispose();
        logger.error("Fetal Error");
        //tfStatus.setText("Fatal Error");
         mdiParent.fsStatusBar.setLblSetMessage("");
         mdiParent.fsStatusBar.setLblSetMessage("Fetal Error");
        break;
      }
    }
  }
  }

  private class RemoteCommandListener implements FsRemoteCommandListener{
   
   public void propertyChange(PropertyChangeEvent evt){
     String homeFolder;
     String selectTreeNodePath = null;
     Object[] treeNodes=null;
     String currentTreeNodePath;
     try {
       logger.debug("Inside Property change of remote command Listener: ");
       int propertyName=Integer.valueOf(evt.getPropertyName());
       FsResponse fsResponse;
       FsExceptionHolder fsExceptionHolder;
       fsResponse = (FsResponse)evt.getNewValue();
       
       logger.debug("propertyName : "+ propertyName);
       
       switch (propertyName){
         case  GET_ROOT_FOLDERS:
           setWaitCursor4Remote();
           treeNodes = fsResponse.getDatas();
           currentTreeNodePath = fsResponse.getCurrentTreeNodePath();
           logger.debug("currentTreeNodePath : " + currentTreeNodePath);
           selectTreeNodePath = fsResponse.getSelectTreeNodePath();
           logger.debug("selectTreeNodePath : " + selectTreeNodePath);
           remoteWillTreeExpansionListener.insertTreeNodesRemoteTreeView(treeNodes,currentTreeNodePath,selectTreeNodePath);
           //setWaitCursorForRemoteBrowser();
           
           fsClient.getHomeFolder(FsRemoteOperationConstants.COMMAND);
           break;
         case  GET_HOME_FOLDER:
            setDefaultCursor4Remote();
           String homeFolderPath = (String)fsResponse.getData();
           logger.debug("homeFolder : " + homeFolderPath);
           //setWaitCursorForRemoteBrowser();
           setWaitCursor4Remote();
           fsClient.getFolderRoot(homeFolderPath,FsRemoteOperationConstants.COMMAND);
           break;
         case  GET_FOLDER_ROOT:
           String homeFolderRoot = (String)fsResponse.getData();
           logger.debug("homeFolderRoot : " + homeFolderRoot);
           homeFolder = (String)fsResponse.getData1();
           logger.debug("homeFolder : " + homeFolder);
           //setWaitCursorForRemoteBrowser();
           fsClient.getFlatFolderTree(homeFolderRoot, homeFolder,FsRemoteOperationConstants.COMMAND);
           break;
         case  GET_FLAT_FOLDER_TREE:
           treeNodes = fsResponse.getDatas();
           Object objects[] = fsResponse.getDatas();
           displayTreeNode(treeNodes);
           currentTreeNodePath = fsResponse.getCurrentTreeNodePath();
           logger.debug("currentTreeNodePath : " + currentTreeNodePath);
           selectTreeNodePath = fsResponse.getSelectTreeNodePath();
           logger.debug("selectTreeNodePath : " + selectTreeNodePath);
           logger.debug(" treeNodes : " + treeNodes);
           remoteWillTreeExpansionListener.insertTreeNodesRemoteTreeView(treeNodes,currentTreeNodePath,selectTreeNodePath);
           fillRemoteFolderFileList(objects);
           logger.debug("End of initialization");
           setDefaultCursor4Remote();
           break;
         case GETCONTENTOFFOLDER:
           objects = fsResponse.getDatas();
           parentFolderPath=fsResponse.getData1().toString();
           fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
           fillRemoteFolderFileList(objects);
           String absFolderPath = fsFolderDocInfoHolder.getCurrentFolderPath();
           
           logger.debug("absFolderPath" + absFolderPath);
           
           addItemToRemoteComboBox(absFolderPath);
           
           butRemoteNavigateBack.setEnabled(fsFolderDocInfoHolder.isEnableBackButton());
           butRemoteNavigateForward.setEnabled(fsFolderDocInfoHolder.isEnableForwardButton());
           butRemoteNavigateUp.setEnabled(fsFolderDocInfoHolder.isEnableParentButton());
           setDefaultCursor4Remote();
           break;
         case NAVIGATE_FORWARD:
         case NAVIGATE_BACK:
         case GETCONTENTOFPARENTFOLDER:
           objects = fsResponse.getDatas();
           fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
           fillRemoteFolderFileList(objects);
           absFolderPath = fsFolderDocInfoHolder.getCurrentFolderPath();
           
           logger.debug("absFolderPath" + absFolderPath);
           
           DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeRemoteTreeView.getModel()).getRoot();
           DefaultMutableTreeNode nodeToFill = GeneralUtil.findTreeNode(root,absFolderPath);
           FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
           TreeNode nodesOfTree[];
           TreePath treePath;
           String nodeToFillPath =  fsFolderHolder.getPath();       
           
           
           logger.debug("Arrived Node Path" + nodeToFillPath);
           nodesOfTree =  nodeToFill.getPath();
           treePath = new TreePath(nodesOfTree);
           treeRemoteTreeView.setSelectionPath(treePath);
           treeRemoteTreeView.requestFocus();
           
           addItemToRemoteComboBox(absFolderPath);
           butRemoteNavigateBack.setEnabled(fsFolderDocInfoHolder.isEnableBackButton());
           butRemoteNavigateForward.setEnabled(fsFolderDocInfoHolder.isEnableForwardButton());
           butRemoteNavigateUp.setEnabled(fsFolderDocInfoHolder.isEnableParentButton());
           setDefaultCursor4Remote();
           break;
         case SEARCH_FAILED:
           if(splpRemoteSystem.getBottomComponent().equals(jpRemoteFolderFileList)){
             splpRemoteSystem.remove(splpRemoteSystem.getBottomComponent());
             JPanel panel = new JPanel();
             panel.setBackground(Color.WHITE);
             JLabel label = new JLabel("Invalid Path");
             label.setBackground(Color.WHITE);
             panel.add(label);
             splpRemoteSystem.add(panel,JSplitPane.BOTTOM);
           } 
           break;
         case RENAME:
           remoteRefreshOperation();
           break;
         case DELETE:
           String parentOfDeletedItems = (String)fsResponse.getData();
           //setWaitCursorForRemoteBrowser();
           setWaitCursor4Remote();
           fsClient.getFlatFolderTree(parentOfDeletedItems,null,FsRemoteOperationConstants.COMMAND);
           //setWaitCursorForRemoteBrowser();
           //fsFileSystemOperationsRemote.getContentOfFolder(parentOfDeletedItems,FsMessage.FOR_CLIENTGUI);
           setWaitCursor4Remote();
           fsClient.getContentOfFolder(parentOfDeletedItems);
           break;
         case FILE_PROPERTYPAGE:
           logger.debug("Inside The ClientGui === FILE_PROPERTYPAGE..............");
           FsFilePropertyPageRemote fsFilePropertyPageRemote = (FsFilePropertyPageRemote)fsResponse.getData();
           
           FsFilePropertyPage fsFilePropertyPage = new FsFilePropertyPage(mdiParent,fsFilePropertyPageRemote.getName() + "properties",false);     
           fsFilePropertyPage.setFileName(fsFilePropertyPageRemote.getName());  
           fsFilePropertyPage.setJlblFileIcon(getFileIcon(fsFilePropertyPageRemote.getName()));
           fsFilePropertyPage.setTypeOfFile(fsFilePropertyPageRemote.getFileType());          
           fsFilePropertyPage.setLocation(fsFilePropertyPageRemote.getLocation());
           fsFilePropertyPage.setSize(fsFilePropertyPageRemote.getSize()/1024 + " Kb");
           fsFilePropertyPage.setCreationDate(fsFilePropertyPageRemote.getCreationDate());
           fsFilePropertyPage.setModifiedDate(fsFilePropertyPageRemote.getModifiedDate().toString());
           String permissions[] = fsFilePropertyPageRemote.getFsPermissionHolder().getPermissions();        
           StringBuffer permission = new StringBuffer("");
           for(int index = 0 ; index < permissions.length ; index++){
             permission.append( permissions[index]);
             if(permissions.length > 1){
               permission.append(",");
             }
           }
           fsFilePropertyPage.setPermissions(permission.toString());
           
           fsFilePropertyPage.addPropertyChangeSupport(new PropertyChangeListener(){
           public void propertyChange(PropertyChangeEvent evt){
             //setWaitCursorForRemoteBrowser();
             setWaitCursor4Remote();
             FsFilePropertyPageRemote  fsFilePropertyPageRemote =(FsFilePropertyPageRemote)evt.getNewValue();      
             if (evt.getSource().equals(FsMessage.FOR_REMOTE_FILESYSTEM)) {
               fsClient.setPropertiesOfFile(fsFilePropertyPageRemote);
             }
             //setWaitCursorForRemoteBrowser();
             setWaitCursor4Remote();
             fsClient.getContentOfFolder(fsFilePropertyPageRemote.getLocation());
             setDefaultCursor4Remote();
           }
           });
           
           fsFilePropertyPage.setPropertyPageFor(FsMessage.FOR_REMOTE_FILESYSTEM);
           fsFilePropertyPage.setOldFileInfo(fsFilePropertyPageRemote.getName());
           
           fsFilePropertyPage.setVisible(true);
           setDefaultCursor4Remote();
           break;
         case FOLDER_PROPERTYPAGE:
           
           FsFolderPropertyPageRemote fsFolderPropertyPageRemote = (FsFolderPropertyPageRemote)fsResponse.getData();
  
           FsFolderPropertyPage fsFolderPropertyPage = new FsFolderPropertyPage(mdiParent, fsFolderPropertyPageRemote.getName() + " properties",false);
           fsFolderPropertyPage.setFolderName(fsFolderPropertyPageRemote.getName());
           //fsFolderPropertyPage.setJlblFolderIcon();                                                         saurabh_remove
           fsFolderPropertyPage.setFileFolderCount(fsFolderPropertyPageRemote.getFileCount(), fsFolderPropertyPageRemote.getFolderCount());
           fsFolderPropertyPage.setLocation(fsFolderPropertyPageRemote.getLocation());          
           fsFolderPropertyPage.setType(fsFolderPropertyPageRemote.getFolderType());
           fsFolderPropertyPage.setSize(fsFolderPropertyPageRemote.getSize()/1024 + " KB");
           
           FsPermissionHolder fsPermissionHolder = fsFolderPropertyPageRemote.getFsPermissionHolder();
           permissions = fsPermissionHolder.getPermissions();
           permission = new StringBuffer("");
           for(int index = 0 ; index < permissions.length ; index++){
             permission.append(permissions[index]);
             if(permissions.length > 1){
               permission.append(",");
             }
           }
           fsFolderPropertyPage.setPermissions(permission.toString());
           
           fsFolderPropertyPage.addPropertyChangeSupport(new PropertyChangeListener(){
           public void propertyChange(PropertyChangeEvent evt){
             //setWaitCursorForRemoteBrowser();
              setWaitCursor4Remote();
              FsFolderPropertyPageRemote fsFolderPropertyPageRemote = (FsFolderPropertyPageRemote)evt.getNewValue();
              if (evt.getSource().equals(FsMessage.FOR_REMOTE_FILESYSTEM)) {
                 fsClient.setPropertiesOfFolder(fsFolderPropertyPageRemote);
              }
             
             //setWaitCursorForRemoteBrowser();
             setWaitCursor4Remote();
             fsClient.getContentOfFolder(fsFolderPropertyPageRemote.getLocation());
             setDefaultCursor4Remote();
           }
           });
           fsFolderPropertyPage.setPropertyPageFor(FsMessage.FOR_REMOTE_FILESYSTEM);
           fsFolderPropertyPage.setOldAttributes(fsFolderPropertyPageRemote.getName());
           fsFolderPropertyPage.setVisible(true);
           setDefaultCursor4Remote();
           break;
         case FILE_FOLDER_PROPERTYPAGE:
           
           FsFileFolderPropertyPage fsFileFolderPropertyPage = new FsFileFolderPropertyPage(mdiParent,"File(s) and Folder(s) properties",false);
           FsFileFolderPropertyPageRemote fsFileFolderPropertyPageRemote = (FsFileFolderPropertyPageRemote)fsResponse.getData();
           fsFileFolderPropertyPage.setType(fsFileFolderPropertyPageRemote.getType());
           fsFileFolderPropertyPage.setLocation(fsFileFolderPropertyPageRemote.getLocation());
           fsFileFolderPropertyPage.setSize(fsFileFolderPropertyPageRemote.getSize()/1024 + " KB");
           fsFileFolderPropertyPage.setNoOfFilesFolders(fsFileFolderPropertyPageRemote.getNoOfFiles(),fsFileFolderPropertyPageRemote.getNoOfFolders());
           fsFileFolderPropertyPage.setPropertyPageFor(FsMessage.FOR_REMOTE_FILESYSTEM);
           fsFileFolderPropertyPage.setVisible(true);
           setDefaultCursor4Remote();
           break;
        case ERROR_MESSAGE:
         objects = fsResponse.getDatas();
         fsFolderDocInfoHolder = fsResponse.getFsFolderDocInfoHolder();
         fsExceptionHolder = fsResponse.getFsExceptionHolder();
         if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
           || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
           || fsExceptionHolder.getErrorCode() == 30041){
          // tfStatus.setText("Access denied");
           mdiParent.fsStatusBar.setLblSetMessage("");
           mdiParent.fsStatusBar.setLblSetMessage("Fetal Error");
           JOptionPane.showMessageDialog(mdiParent,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
         }else if(fsExceptionHolder.getErrorCode() == 68005){
           String errorMsg = fsExceptionHolder.getErrorMessage();
           logger.debug("Error Message :" + errorMsg);
           JOptionPane.showMessageDialog(mdiParent, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
         }else{
           JOptionPane.showMessageDialog(mdiParent,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
          // tfStatus.setText("" + fsExceptionHolder);
           mdiParent.fsStatusBar.setLblSetMessage("");
           mdiParent.fsStatusBar.setLblSetMessage(fsExceptionHolder.getErrorMessage());
           fillRemoteFolderFileList(objects);
           
         }
         logger.error(fsExceptionHolder);
         break;
        case FETAL_ERROR:
         //setWaitCursorForRemoteBrowser();
         logger.error("Fetal Error");
         //tfStatus.setText("Fatal Error");
          mdiParent.fsStatusBar.setLblSetMessage("");
          mdiParent.fsStatusBar.setLblSetMessage("Fatal Error");
         break;
       }
     }catch(Exception ex){
     logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
     //tfStatus.setText( ex.getMessage());
      mdiParent.fsStatusBar.setLblSetMessage("");
      mdiParent.fsStatusBar.setLblSetMessage(ex.getMessage());
    }
     
   }
   
  }
  
  /**
    * a action listener class for a combobox
    */
  private class ComboRemoteAdressBar_ActionListener implements ActionListener{
      /**
       * handles actionPerformed on combobox.
       * @param e ActionEvent object
       */
      public void actionPerformed(ActionEvent e) {
        comboRemoteAdressBar_actionPerformed(e);
      }
    }

  class TableTransferHandler extends StringTransferHandler{
    protected String exportString(JComponent c) {
      System.out.println("Inside export function of remote view");
      return "";
    }
    protected void importString(JComponent c, String str) {
      System.out.println("Inside import function of remote view ");
    }
    protected void cleanup(JComponent c, boolean remove) {
      
    }
  }
  abstract class StringTransferHandler extends TransferHandler {

    protected abstract String exportString(JComponent c);

    protected abstract void importString(JComponent c, String str);

    protected abstract void cleanup(JComponent c, boolean remove);

    protected Transferable createTransferable(JComponent c) {
      return new StringSelection(exportString(c));
    }

    public int getSourceActions(JComponent c) {
      return COPY_OR_MOVE;
    }

    public boolean importData(JComponent c, Transferable t) {
      if (canImport(c, t.getTransferDataFlavors())) {
        try {
          String str = (String) t
              .getTransferData(DataFlavor.stringFlavor);
          importString(c, str);
          return true;
        } catch (UnsupportedFlavorException ufe) {
        ;
        } catch (IOException ioe) {
        ;
        }
      }

      return false;
    }

    protected void exportDone(JComponent c, Transferable data, int action) {
      cleanup(c, action == MOVE);
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
      for (int i = 0; i < flavors.length; i++) {
        if (DataFlavor.stringFlavor.equals(flavors[i])) {
          return true;
        }
      }
      return false;
    }
  }
}
