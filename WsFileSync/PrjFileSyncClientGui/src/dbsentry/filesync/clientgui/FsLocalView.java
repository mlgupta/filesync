package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.FsClient;
import dbsentry.filesync.client.enumconstants.EnumClipBoardOperation;
import dbsentry.filesync.clientgui.enumconstants.EnumLocalTable;
import dbsentry.filesync.clientgui.enumconstants.EnumRemoteTable;
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.clientgui.listener.LocalTableColumnHeaderListener;
import dbsentry.filesync.clientgui.listener.LocalWillTreeExpansionListener;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.FsFolderHolder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

import javax.swing.BorderFactory;
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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
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

import org.jdesktop.jdic.filetypes.Association;
import org.jdesktop.jdic.filetypes.AssociationService;


/**
 * @author Saurabh Gupta
 * @version1.2
 * @Date of creation:
 * @Last Modfied by :31-08-2006
 * @Last Modfied Date:
 */
public class FsLocalView extends JInternalFrame {

  private Border normalButtonBorder;
  //Local Toolbar Buttons

  private File file;

  private int cursorCounterLocal = 0;

  private JFileChooser fileChooser = new JFileChooser();

  private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

  private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);

  private Stack clipBoardLocal = new Stack();

  private AssociationService assoService = new AssociationService();

  private JButton butLocalNavigateBack = new JButton();

  private JButton butLocalNavigateForward = new JButton();

  private JButton butLocalNavigateUp = new JButton();

  private JButton butLocalRefreshContent = new JButton();

  private JButton butLocalHome = new JButton();

  private JButton butLocalCreateNewFolder = new JButton();

  private JButton butLocalRenameFolderFile = new JButton();

  private JButton butLocalDeleteFolderFile = new JButton();

  private JButton butLocalCut = new JButton();

  private JButton butLocalCopy = new JButton();

  private JButton butLocalPaste = new JButton();

  private JButton butLocalPropertyFolderFile = new JButton();

  private JButton butLocalUpload = new JButton();

  private JButton butLocalGo = new JButton();

  private JScrollPane scrpLocalFolderFileList = new JScrollPane();

  public JTree treeLocalTreeView = new JTree(new DefaultMutableTreeNode(null));

  private JPanel jpToolBarLocal = new JPanel();

  private JPanel jpLocalTreeView = new JPanel();

  private JPanel jpLocalFolderFileList = new JPanel();

  private JToolBar tbLocalSystem = new JToolBar();

  //private JTextField tfStatus = new JTextField();

  private JComboBox comboLocalAddressBar = new JComboBox();

  public JTable tblLocalFolderFileList = new JTable();

  private LocalWillTreeExpansionListener localWillTreeExpansionListener;

  private JLabel lbLocalAddressbar = new JLabel();

  public JSplitPane splpLocalSystem = new JSplitPane();

  private JScrollPane scrpLocalTreeView = new JScrollPane();

  private JPanel jpLocalSystem = new JPanel();

  private int clipBoardOperationLocal;

  private FolderDocInfoClient folderDocInfoClient;

  private FsTableModel fsTableModelLocal;

  private ComboLocalAddressBar_ActionListener actionListener4LocalAddressBar =
    new ComboLocalAddressBar_ActionListener();

  private LocalTableColumnHeaderListener tableHeaderMouseListener = null;

  private Color selectionColor;

  private FsFileSystemOperationLocal fsFileSystemOperationLocal;

  private FileSystemView fileSystemView = fileChooser.getFileSystemView();

  private Logger logger;

  private FileSyncClientMDI mdiParent;

  private FsClient fsClient;

  private JPopupMenu popup = new JPopupMenu();

  private JMenuItem cutMenuItem = new JMenuItem("Cut");

  private JMenuItem copyMenuItem = new JMenuItem("Copy ");

  private JMenuItem pasteMenuItem = new JMenuItem("Paste");

  private JMenuItem renameMenuItem = new JMenuItem("Rename");

  private JMenuItem newFolderMenuItem = new JMenuItem("NewFolder");

  private JMenuItem deleteMenuItem = new JMenuItem("Delete");

  private JMenuItem refreshMenuItem = new JMenuItem("Refresh");

  private JMenuItem uploadMenuItem = new JMenuItem("Upload...");

  private JMenuItem propertyMenuItem = new JMenuItem("Property");

  public FsLocalView(Logger logger, FileSyncClientMDI mdiParent) {
    super("Local View", false, false, false, false);

    try {
      this.logger = logger;
      this.mdiParent = mdiParent;
      jbInit();
      preJbInitOperation();
      postJbInitOperation();
      setResizable(false);
      this.setBorder(BorderFactory.createEmptyBorder());
      setFrameIcon(null);

    } catch (Exception e) {
      ;
    }
  }

  private void jbInit() throws Exception {
    normalButtonBorder = ButtonMouseAdapter.normalButtonBorder;
    //Local Navigate Back
    butLocalNavigateBack.setToolTipText("Back");
    butLocalNavigateBack.setIcon(FsImage.imageNavigateBack);
    butLocalNavigateBack.setOpaque(false);
    butLocalNavigateBack.setPreferredSize(new Dimension(30, 30));
    butLocalNavigateBack.setMinimumSize(new Dimension(30, 30));
    butLocalNavigateBack.setMaximumSize(new Dimension(30, 25));
    butLocalNavigateBack.setSize(new Dimension(30, 30));
    butLocalNavigateBack.setEnabled(false);
    butLocalNavigateBack.setBorder(normalButtonBorder);
    butLocalNavigateBack.addMouseListener(new ButtonMouseAdapter());
    butLocalNavigateBack.addActionListener(new ActionListener() {
                                             public void actionPerformed(ActionEvent e) {
                                               butLocalNavigateBack_actionPerformed();

                                             }
                                           }
    );

    //Local Navigate Forward 
    butLocalNavigateForward.setToolTipText("Forward");
    butLocalNavigateForward.setIcon(FsImage.imageNavigateForward);
    butLocalNavigateForward.setOpaque(false);
    butLocalNavigateForward.setPreferredSize(new Dimension(30, 30));
    butLocalNavigateForward.setMinimumSize(new Dimension(30, 30));
    butLocalNavigateForward.setMaximumSize(new Dimension(30, 25));
    butLocalNavigateForward.setSize(new Dimension(30, 30));
    butLocalNavigateForward.setEnabled(false);
    butLocalNavigateForward.setBorder(normalButtonBorder);
    butLocalNavigateForward.addMouseListener(new ButtonMouseAdapter());
    butLocalNavigateForward.addActionListener(new ActionListener() {
                                                public void actionPerformed(ActionEvent e) {
                                                  butLocalNavigateForward_actionPerformed();
                                                }
                                              }
    );

    //Local Navigate Up
    butLocalNavigateUp.setToolTipText("Go Up");
    butLocalNavigateUp.setIcon(FsImage.imageNavigateUp);
    butLocalNavigateUp.setOpaque(false);
    butLocalNavigateUp.setPreferredSize(new Dimension(30, 30));
    butLocalNavigateUp.setMinimumSize(new Dimension(30, 30));
    butLocalNavigateUp.setMaximumSize(new Dimension(30, 25));
    butLocalNavigateUp.setSize(new Dimension(30, 30));
    butLocalNavigateUp.setBorder(normalButtonBorder);
    butLocalNavigateUp.addMouseListener(new ButtonMouseAdapter());
    butLocalNavigateUp.addActionListener(new ActionListener() {
                                           public void actionPerformed(ActionEvent e) {
                                             btnLocalNavigateUp_actionPerformed();
                                           }
                                         }
    );

    //Local Refresh 
    butLocalRefreshContent.setToolTipText("Refresh (F5)");
    butLocalRefreshContent.setIcon(FsImage.imageRefresh);
    butLocalRefreshContent.setOpaque(false);
    butLocalRefreshContent.setPreferredSize(new Dimension(30, 30));
    butLocalRefreshContent.setMinimumSize(new Dimension(30, 30));
    butLocalRefreshContent.setMaximumSize(new Dimension(30, 25));
    butLocalRefreshContent.setSize(new Dimension(30, 30));
    butLocalRefreshContent.setBorder(normalButtonBorder);
    butLocalRefreshContent.addMouseListener(new ButtonMouseAdapter());
    butLocalRefreshContent.addActionListener(new ActionListener() {
                                               public void actionPerformed(ActionEvent e) {
                                                 butLocalRefreshContent_actionPerformed();
                                               }
                                             }
    );

    //Local Home 
    butLocalHome.setToolTipText("Home");
    butLocalHome.setIcon(FsImage.imageHome);
    butLocalHome.setOpaque(false);
    butLocalHome.setPreferredSize(new Dimension(30, 30));
    butLocalHome.setMinimumSize(new Dimension(30, 30));
    butLocalHome.setMaximumSize(new Dimension(30, 25));
    butLocalHome.setSize(new Dimension(30, 30));
    butLocalHome.setBorder(normalButtonBorder);
    butLocalHome.addMouseListener(new ButtonMouseAdapter());
    butLocalHome.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e) {
                                       butLocalHome_actionPerformed();
                                     }
                                   }
    );


    //Local  New Folder 
    butLocalCreateNewFolder.setToolTipText("Create New Folder");
    butLocalCreateNewFolder.setIcon(FsImage.imageNewFolder);
    butLocalCreateNewFolder.setOpaque(false);
    butLocalCreateNewFolder.setPreferredSize(new Dimension(30, 30));
    butLocalCreateNewFolder.setMinimumSize(new Dimension(30, 30));
    butLocalCreateNewFolder.setMaximumSize(new Dimension(30, 25));
    butLocalCreateNewFolder.setSize(new Dimension(30, 30));
    butLocalCreateNewFolder.setBorder(normalButtonBorder);
    butLocalCreateNewFolder.addMouseListener(new ButtonMouseAdapter());
    butLocalCreateNewFolder.addActionListener(new ActionListener() {
                                                public void actionPerformed(ActionEvent e) {
                                                  butLocalCreateNewFolder_actionPerformed();
                                                }
                                              }
    );

    //Local Rename
    butLocalRenameFolderFile.setToolTipText("Rename (F2)");
    butLocalRenameFolderFile.setIcon(FsImage.imageRename);
    butLocalRenameFolderFile.setOpaque(false);
    butLocalRenameFolderFile.setPreferredSize(new Dimension(30, 30));
    butLocalRenameFolderFile.setMinimumSize(new Dimension(30, 30));
    butLocalRenameFolderFile.setMaximumSize(new Dimension(30, 25));
    butLocalRenameFolderFile.setSize(new Dimension(30, 30));
    butLocalRenameFolderFile.setBorder(normalButtonBorder);
    butLocalRenameFolderFile.addMouseListener(new ButtonMouseAdapter());
    butLocalRenameFolderFile.addActionListener(new ActionListener() {
                                                 public void actionPerformed(ActionEvent e) {
                                                   butLocalRenameFolderFile_actionPerformed();
                                                 }
                                               }
    );

    //Local Delete 
    butLocalDeleteFolderFile.setToolTipText("Delete (Delete)");
    butLocalDeleteFolderFile.setIcon(FsImage.imageDelete);
    butLocalDeleteFolderFile.setOpaque(false);
    butLocalDeleteFolderFile.setPreferredSize(new Dimension(30, 30));
    butLocalDeleteFolderFile.setMinimumSize(new Dimension(30, 30));
    butLocalDeleteFolderFile.setMaximumSize(new Dimension(30, 25));
    butLocalDeleteFolderFile.setSize(new Dimension(30, 30));
    butLocalDeleteFolderFile.setBorder(normalButtonBorder);
    butLocalDeleteFolderFile.addMouseListener(new ButtonMouseAdapter());
    butLocalDeleteFolderFile.addActionListener(new ActionListener() {
                                                 public void actionPerformed(ActionEvent e) {
                                                   butLocalDeleteFolderFile_actionPerformed();
                                                 }
                                               }
    );

    //Local Property
    butLocalPropertyFolderFile.setToolTipText("Property");
    butLocalPropertyFolderFile.setIcon(FsImage.imageProperty);
    butLocalPropertyFolderFile.setOpaque(false);
    butLocalPropertyFolderFile.setPreferredSize(new Dimension(30, 30));
    butLocalPropertyFolderFile.setMinimumSize(new Dimension(30, 30));
    butLocalPropertyFolderFile.setMaximumSize(new Dimension(30, 25));
    butLocalPropertyFolderFile.setSize(new Dimension(30, 30));
    butLocalPropertyFolderFile.setBorder(normalButtonBorder);
    butLocalPropertyFolderFile.addMouseListener(new ButtonMouseAdapter());
    butLocalPropertyFolderFile.addActionListener(new ActionListener() {
                                                   public void actionPerformed(ActionEvent e) {
                                                     butLocalPropertyFolderFile_actionPerformed();
                                                   }
                                                 }
    );

    //Local Cut
    butLocalCut.setToolTipText("Cut (Ctrl-X)");
    butLocalCut.setIcon(FsImage.imageCut);
    butLocalCut.setOpaque(false);
    butLocalCut.setPreferredSize(new Dimension(30, 30));
    butLocalCut.setMinimumSize(new Dimension(30, 30));
    butLocalCut.setMaximumSize(new Dimension(30, 25));
    butLocalCut.setSize(new Dimension(30, 30));
    butLocalCut.setBorder(normalButtonBorder);
    butLocalCut.addMouseListener(new ButtonMouseAdapter());
    butLocalCut.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                      butLocalCut_actionPerformed();
                                    }
                                  }
    );

    //Local Copy
    butLocalCopy.setToolTipText("Copy (Ctrl-C)");
    butLocalCopy.setIcon(FsImage.imageCopy);
    butLocalCopy.setOpaque(false);
    butLocalCopy.setPreferredSize(new Dimension(30, 30));
    butLocalCopy.setMinimumSize(new Dimension(30, 30));
    butLocalCopy.setMaximumSize(new Dimension(30, 25));
    butLocalCopy.setSize(new Dimension(30, 30));
    butLocalCopy.setBorder(normalButtonBorder);
    butLocalCopy.addMouseListener(new ButtonMouseAdapter());
    butLocalCopy.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e) {
                                       butLocalCopy_actionPerformed();
                                     }
                                   }
    );

    //Local Paste    
    butLocalPaste.setToolTipText("Paste (Ctrl-V)");
    butLocalPaste.setIcon(FsImage.imagePaste);
    butLocalPaste.setOpaque(false);
    butLocalPaste.setPreferredSize(new Dimension(30, 30));
    butLocalPaste.setMinimumSize(new Dimension(30, 30));
    butLocalPaste.setMaximumSize(new Dimension(30, 25));
    butLocalPaste.setSize(new Dimension(30, 30));
    butLocalPaste.setBorder(normalButtonBorder);
    butLocalPaste.addMouseListener(new ButtonMouseAdapter());
    butLocalPaste.addActionListener(new ActionListener() {
                                      public void actionPerformed(ActionEvent e) {
                                        butLocalPaste_actionPerformed();
                                      }
                                    }
    );

    //Upload
    butLocalUpload.setToolTipText("Upload");
    butLocalUpload.setIcon(FsImage.imageUpload);
    butLocalUpload.setOpaque(false);
    butLocalUpload.setPreferredSize(new Dimension(30, 30));
    butLocalUpload.setMinimumSize(new Dimension(30, 30));
    butLocalUpload.setMaximumSize(new Dimension(30, 25));
    butLocalUpload.setSize(new Dimension(30, 30));
    butLocalUpload.setBorder(normalButtonBorder);
    butLocalUpload.addMouseListener(new ButtonMouseAdapter());
    butLocalUpload.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                         butLocalUpload_actionPerformed();
                                       }
                                     }
    );


    //Local Addressbar

    //Label for Local Address bar   
    lbLocalAddressbar.setText("Address : ");
    lbLocalAddressbar.setHorizontalAlignment(SwingConstants.RIGHT);
    lbLocalAddressbar.setMaximumSize(new Dimension(90, 14));
    lbLocalAddressbar.setMinimumSize(new Dimension(90, 14));
    lbLocalAddressbar.setPreferredSize(new Dimension(62, 20));

    //Dropdown for Local Address bar 

    comboLocalAddressBar.setFont(new FsFont());
    comboLocalAddressBar.setPreferredSize(new Dimension(420, 20));
    comboLocalAddressBar.setMinimumSize(new Dimension(242, 20));
    comboLocalAddressBar.setMaximumSize(new Dimension(610, 20));
    comboLocalAddressBar.setSize(new Dimension(700, 28));
    comboLocalAddressBar.setEditable(true);
    comboLocalAddressBar.setMaximumRowCount(5);
    comboLocalAddressBar.setBorder(BorderFactory.createEtchedBorder());
    comboLocalAddressBar.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
                                                                           public void keyReleased(KeyEvent e) {
                                                                             comboLocalAddressBar_keyReleased(e);
                                                                           }
                                                                         }
    );
    comboLocalAddressBar.addActionListener(actionListener4LocalAddressBar);

    //Local Button Go
    butLocalGo.setToolTipText("Go");
    butLocalGo.setIcon(FsImage.imageGo);
    butLocalGo.setOpaque(false);
    butLocalGo.setPreferredSize(new Dimension(30, 20));
    butLocalGo.setMinimumSize(new Dimension(30, 20));
    butLocalGo.setMaximumSize(new Dimension(30, 20));
    butLocalGo.setSize(new Dimension(30, 28));
    butLocalGo.setBorder(normalButtonBorder);
    butLocalGo.addMouseListener(new ButtonMouseAdapter());
    butLocalGo.addActionListener(new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                     butLocalGo_actionPerformed();
                                   }
                                 }
    );

    
    //Preparing  Local Toolbar
    tbLocalSystem.setFloatable(false);
    tbLocalSystem.setBorder(BorderFactory.createEtchedBorder());
    tbLocalSystem.add(butLocalNavigateBack, null);
    tbLocalSystem.add(butLocalNavigateForward, null);
    tbLocalSystem.add(butLocalNavigateUp, null);
    //tbLocalSystem.addSeparator();
    tbLocalSystem.add(butLocalRefreshContent, null);
    tbLocalSystem.add(butLocalHome, null);
    //tbLocalSystem.addSeparator();
    tbLocalSystem.add(butLocalCreateNewFolder, null);
    tbLocalSystem.add(butLocalRenameFolderFile, null);
    tbLocalSystem.add(butLocalDeleteFolderFile, null);
    tbLocalSystem.add(butLocalPropertyFolderFile, null);
   // tbLocalSystem.addSeparator();
    tbLocalSystem.add(butLocalCut, null);
    tbLocalSystem.add(butLocalCopy, null);
    tbLocalSystem.add(butLocalPaste, null);
   // tbLocalSystem.addSeparator();
    tbLocalSystem.add(butLocalUpload, null);
    tbLocalSystem.add(lbLocalAddressbar, null);
    tbLocalSystem.add(comboLocalAddressBar, null);
    tbLocalSystem.add(butLocalGo, null);
    

    //Panel  for Local Toolbar

    jpToolBarLocal.setLayout(new BorderLayout());
    jpToolBarLocal.setBorder(BorderFactory.createEmptyBorder());
    jpToolBarLocal.setBounds(new Rectangle(0, 0, 1012, 40));
    jpToolBarLocal.setPreferredSize(new Dimension(1039, 34));
    jpToolBarLocal.setMinimumSize(new Dimension(506, 28));
    jpToolBarLocal.setSize(new Dimension(888, 34));

    //Local Treeview
    treeLocalTreeView.setAutoscrolls(true);
    treeLocalTreeView.setRootVisible(false);
    treeLocalTreeView.setAutoscrolls(true);
    treeLocalTreeView.setBorder(BorderFactory.createEmptyBorder());
    treeLocalTreeView.addFocusListener(new FocusAdapter() {
                                         public void focusLost(FocusEvent e) {
                                           treeLocalTreeView_focusLost();
                                         }

                                         public void focusGained(FocusEvent e) {
                                           treeLocalTreeView_focusGained();
                                         }
                                       });
    treeLocalTreeView.addTreeSelectionListener(new TreeSelectionListener() {
                                                 public void valueChanged(TreeSelectionEvent e) {
                                                   treeLocalTreeView_valueChanged(e);
                                                 }
                                               });
    treeLocalTreeView.addMouseWheelListener(new MouseWheelListener() {
                                              public void mouseWheelMoved(MouseWheelEvent e) {
                                                logger.debug("mouse wheel event");
                                              }
                                            });
    treeLocalTreeView.addMouseListener(new MouseAdapter() {
                                         public void mouseClicked(MouseEvent e) {
                                         }

                                         public void mousePressed(MouseEvent e) {
                                           localRefreshOperation();
                                         }
                                       });

    

    //Scroll Pane for Local Treeview
    scrpLocalTreeView.setBorder(BorderFactory.createEmptyBorder());
    scrpLocalTreeView.getViewport().add(treeLocalTreeView, null);

    //Panel for Local Treeeview
    jpLocalTreeView.setLayout(new BorderLayout());
    jpLocalTreeView.setBorder(BorderFactory.createEmptyBorder());
    jpLocalTreeView.setToolTipText("null");
    jpLocalTreeView.setPreferredSize(new Dimension(249, 283));
    jpLocalTreeView.add(scrpLocalTreeView, BorderLayout.CENTER);

    //Local Listview  
    tblLocalFolderFileList.setShowVerticalLines(false);
    tblLocalFolderFileList.setShowHorizontalLines(false);
    tblLocalFolderFileList.setBorder(BorderFactory.createEmptyBorder());
    tblLocalFolderFileList.setDragEnabled(true);
    tblLocalFolderFileList.setTransferHandler(new TableTransferHandler());
    tblLocalFolderFileList.addKeyListener(new KeyAdapter() {

                                            public void keyReleased(KeyEvent e) {
                                              logger.debug("near mouse listener keyReleased");
                                              tblLocalFolderFileList_keyReleased(e);
                                            }
                                          });
    tblLocalFolderFileList.addFocusListener(new FocusAdapter() {
                                              public void focusLost(FocusEvent e) {
                                                logger.debug("Local table focusLost");
                                                tblLocalFolderFileList_focusLost();
                                              }

                                              public void focusGained(FocusEvent e) {
                                                logger.debug("Local table focusGained");
                                                tblLocalFolderFileList_focusGained();
                                              }
                                            });
    tblLocalFolderFileList.addMouseListener(new MouseAdapter() {
                                              public void mousePressed(MouseEvent e) {
                                                logger.debug("tblLocalFolderFileList mouseClicked event fire");
                                                //                                                  mdiParent.getExplorer().getFsRemoteView().getTblRemoteFolderFileList().setRowSelectionAllowed(false);
                                                //                                                  mdiParent.getExplorer().getFsRemoteView().getTblRemoteFolderFileList().setRowSelectionAllowed(true);
                                                tblLocalFolderFileList_mouseClicked(e);
                                              }
                                            });
    
    
    //Scrollpane for Local Listview  
    scrpLocalFolderFileList.setBorder(BorderFactory.createEmptyBorder());
    scrpLocalFolderFileList.getViewport().add(tblLocalFolderFileList, null);

    //Panel for Local Listview
    jpLocalFolderFileList.setLayout(new BorderLayout());
    jpLocalFolderFileList.setBorder(BorderFactory.createEmptyBorder());
    jpLocalFolderFileList.add(scrpLocalFolderFileList, BorderLayout.CENTER);

    //Split Panel for Local File Browser
    splpLocalSystem.setBackground(Color.white);
    splpLocalSystem.setDividerLocation(250);
    splpLocalSystem.setDividerSize(6);
    splpLocalSystem.setBorder(BorderFactory.createEmptyBorder());
    splpLocalSystem.add(jpLocalTreeView, JSplitPane.LEFT);
    splpLocalSystem.add(jpLocalFolderFileList, JSplitPane.RIGHT);

    //Panel for Local File Browser
    jpLocalSystem.setLayout(new BorderLayout());
    jpLocalSystem.setBorder(BorderFactory.createEmptyBorder());
    jpLocalSystem.setBounds(new Rectangle(1, 310, 1016, 332));
    jpLocalSystem.setPreferredSize(new Dimension(506, 319));
    jpLocalSystem.setMinimumSize(new Dimension(506, 58));
    jpLocalSystem.add(splpLocalSystem, BorderLayout.CENTER);
    
    this.setBorder(BorderFactory.createEmptyBorder());
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(tbLocalSystem, BorderLayout.NORTH);
    this.getContentPane().add(jpLocalSystem, BorderLayout.CENTER);


    ActionListener actionListener4Controls = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (e.getSource() == cutMenuItem) {
            localCutOperation();
          } else if (e.getSource() == copyMenuItem) {
            localCopyOperation();
          } else if (e.getSource() == pasteMenuItem) {
            localPasteOperation();
          } else if (e.getSource() == renameMenuItem) {
            localRenameOperation();
          } else if (e.getSource() == refreshMenuItem) {
            localRefreshOperation();
          } else if (e.getSource() == newFolderMenuItem) {
            localNewFolderOperation();
          } else if (e.getSource() == deleteMenuItem) {
            localDeleteOperation();
          } else if (e.getSource() == propertyMenuItem) {
            localPropertyOperation();
          } else if (e.getSource() == uploadMenuItem) {
            localUploadOperation();
          }

        }
      }
    ;

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

    uploadMenuItem.setFont(new FsFont());
    uploadMenuItem.setIcon(FsImage.imageMenuUpload);
    uploadMenuItem.addActionListener(actionListener4Controls);
    popup.add(uploadMenuItem);
    popup.addSeparator();

    propertyMenuItem.setFont(new FsFont());
    propertyMenuItem.setIcon(FsImage.imageMenuProperty);
    propertyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.ALT_MASK, false));
    propertyMenuItem.addActionListener(actionListener4Controls);
    popup.add(propertyMenuItem);
    
    popup.setBorder(BorderFactory.createEtchedBorder());

    PopupListener pl = new PopupListener();
    tblLocalFolderFileList.addMouseListener(pl);
    treeLocalTreeView.addMouseListener(pl);

  }

  private void disableButtonOperation(boolean flag) {

    butLocalCreateNewFolder.setEnabled(flag);
    butLocalRenameFolderFile.setEnabled(flag);
    butLocalDeleteFolderFile.setEnabled(flag);
    butLocalPropertyFolderFile.setEnabled(flag);
    butLocalCut.setEnabled(flag);
    butLocalCopy.setEnabled(flag);
    if (clipBoardLocal.isEmpty()) {
      butLocalPaste.setEnabled(flag);
    }
    butLocalUpload.setEnabled(flag);

  }

  private void enableButtonOperation(boolean flag) {
    butLocalCreateNewFolder.setEnabled(flag);
    butLocalRenameFolderFile.setEnabled(flag);
    butLocalDeleteFolderFile.setEnabled(flag);
    butLocalPropertyFolderFile.setEnabled(flag);
    butLocalCut.setEnabled(flag);
    butLocalCopy.setEnabled(flag);
    butLocalUpload.setEnabled(flag);
  }

  public JMenuItem getUploadMenuItem() {
    return uploadMenuItem;
  }

  private void preJbInitOperation() {
    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    boolean flag = false;
    if (selectedRowsCount == 0) {
      disableButtonOperation(flag);
    } else {
      flag = true;
      enableButtonOperation(true);
    }
  }

  public void setFsClient(FsClient fsClient) {
    this.fsClient = fsClient;
  }

  private class PopupListener extends MouseAdapter {
    public void mousePressed(MouseEvent e) {
      //System.out.println("in mouse Pressed event..............................................");
      int selectRows[];
      int selectRowCount;
      selectRows = tblLocalFolderFileList.getSelectedRows();
      selectRowCount = selectRows.length;
      if (selectRowCount == 0) {
        disablePopUp();
      } else {
        enablePopUp();
      }
      maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
      //System.out.println("in mouse Released  event.............................................");
      int selectRows[];
      int selectRowCount;

      selectRows = tblLocalFolderFileList.getSelectedRows();
      selectRowCount = selectRows.length;
      if (selectRowCount == 0) {
        disablePopUp();
      } else {
        enablePopUp();
      }
      maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
      //      logger.debug("E.getSource---------------------------"+e.getSource().toString());  
      //      logger.debug("E.getSource---------------------------"+e.getComponent());  
      if (e.isPopupTrigger()) {
        popup.show((e.getComponent()), e.getX(), e.getY());
      }
    }

    private void disablePopUp() {
      cutMenuItem.setEnabled(false);
      copyMenuItem.setEnabled(false);
      if (clipBoardLocal.isEmpty()) {
        pasteMenuItem.setEnabled(false);
      } else {
        pasteMenuItem.setEnabled(true);
      }
      renameMenuItem.setEnabled(false);
      newFolderMenuItem.setEnabled(false);
      deleteMenuItem.setEnabled(false);
      uploadMenuItem.setEnabled(false);
      propertyMenuItem.setEnabled(false);
    }

    private void enablePopUp() {
      cutMenuItem.setEnabled(true);
      copyMenuItem.setEnabled(true);
      //      if(!clipBoardLocal.isEmpty()){
      //      pasteMenuItem.setEnabled(true);
      //      }else{
      //        pasteMenuItem.setEnabled(false);
      //      }
      //      pasteMenuItem.setEnabled(true);
      renameMenuItem.setEnabled(true);
      newFolderMenuItem.setEnabled(true);
      deleteMenuItem.setEnabled(true);
      uploadMenuItem.setEnabled(true);
      propertyMenuItem.setEnabled(true);

    }
  }

  public void initialVisualSettings() {
    logger.debug("Visual settings initialized");
    scrpLocalFolderFileList.getViewport().setBackground(Color.WHITE);
    butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
    butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
    butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());
    treeLocalTreeView.requestFocus();
  }


  public void initializeLocalFolderFileList() {
    setWaitCursor4Local();
    Vector dataRow = new Vector(1);

    fsTableModelLocal = new FsTableModel(EnumLocalTable.COLUMN_NAMES, dataRow);
    tblLocalFolderFileList.setModel(fsTableModelLocal);
    tblLocalFolderFileList.setAutoCreateColumnsFromModel(false);
    tblLocalFolderFileList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    selectionColor = tblLocalFolderFileList.getSelectionBackground();
    //set default cell renderer
    FsTableCellRenderer fsTableCellRenderer = new FsTableCellRenderer();
    tblLocalFolderFileList.setDefaultRenderer(Object.class, fsTableCellRenderer);


    //add click event listener for table header
    JTableHeader tableHeader = tblLocalFolderFileList.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 18));
    tableHeaderMouseListener = new LocalTableColumnHeaderListener();
    tableHeader.addMouseListener(tableHeaderMouseListener);

    tableHeader.getColumnModel().getColumn(EnumLocalTable.NAME).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumLocalTable.COLUMN_NAMES[EnumLocalTable.NAME]));
    tableHeader.getColumnModel().getColumn(EnumLocalTable.SIZE).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumLocalTable.COLUMN_NAMES[EnumLocalTable.SIZE]));
    tableHeader.getColumnModel().getColumn(EnumLocalTable.TYPE).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumLocalTable.COLUMN_NAMES[EnumLocalTable.TYPE]));
    tableHeader.getColumnModel().getColumn(EnumLocalTable.MODIFIED).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumLocalTable.COLUMN_NAMES[EnumLocalTable.MODIFIED]));
    tableHeader.getColumnModel().getColumn(EnumLocalTable.EMPTY_COLUMN).setHeaderRenderer(new FsTableHeaderCellRenderer(EnumLocalTable.COLUMN_NAMES[EnumLocalTable.EMPTY_COLUMN]));

    DefaultTableColumnModel colModel = (DefaultTableColumnModel)tblLocalFolderFileList.getColumnModel();
    colModel.getColumn(EnumLocalTable.NAME).setPreferredWidth(200);
    colModel.getColumn(EnumLocalTable.SIZE).setPreferredWidth(100);
    colModel.getColumn(EnumLocalTable.TYPE).setPreferredWidth(150);
    colModel.getColumn(EnumLocalTable.MODIFIED).setPreferredWidth(175);
    colModel.getColumn(EnumLocalTable.EMPTY_COLUMN).setPreferredWidth(150);
    colModel.getColumn(EnumLocalTable.DESCRIPTION).setPreferredWidth(150);
    colModel.getColumn(EnumLocalTable.PERMISSION).setPreferredWidth(150);

    //hide table coulmn contiaining non displayable info
    if (tblLocalFolderFileList.getColumnCount() > EnumLocalTable.COLUMN_DISPLAY_LENGTH) {
      for (int index = EnumLocalTable.COLUMN_DISPLAY_LENGTH; index < EnumLocalTable.COLUMN_LENGTH; index++) {
        tblLocalFolderFileList
        .removeColumn(tableHeader.getColumnModel().getColumn(EnumLocalTable.COLUMN_DISPLAY_LENGTH));
      }
    }
    setDefaultCursor4Local();
  }

  /**
   * fills the local folderlist with the contents of specified folder and 
   * sorts the list by name.
   * @param currentFolder File object representring current folder
   */
  public void fillLocalFolderFileList(File currentFolder) {
    try {
      //setWaitCursor4Local();
      logger.debug("In function fillLocalFolderFileList");
      Vector dataRow = new Vector(1);
      long currentFolderItemsCount = 0;
      File currentFolderItems[] = currentFolder.listFiles();
      if (currentFolderItems != null) {
        currentFolderItemsCount = currentFolderItems.length;
       // tfStatus.setText(currentFolderItemsCount + " Object(s)");
        mdiParent.fsStatusBar.setLblSetMessage("");
        mdiParent.fsStatusBar.setLblSetMessage(currentFolderItemsCount + " Object(s)");
        dataRow = new Vector((int)currentFolderItemsCount);
        Vector dataCol;
        Association association = null;
        for (int index = 0; index < currentFolderItemsCount; index++) {
          dataCol = new Vector(EnumLocalTable.COLUMN_LENGTH);
          for (int counter = 0; counter < EnumLocalTable.COLUMN_LENGTH; counter++) {
            dataCol.add(counter, "");
          }

          dataCol
          .set(EnumLocalTable.TYPE, new JLabel(fileChooser.getTypeDescription(currentFolderItems[index]) + "  "));
          if (currentFolderItems[index].isDirectory()) {
            dataCol
            .set(EnumLocalTable.NAME, new JLabel(currentFolderItems[index].getName(), FsImage.imgFolderClosed, JLabel
                                                        .LEFT));
            dataCol.set(EnumLocalTable.SIZE, new JLabel("  4.0 KB  ", JLabel.RIGHT));
            dataCol.set(EnumLocalTable.ABS_SIZE, new JLabel(Long.toString(currentFolderItems[index].length())));
          } else {
            dataCol
            .set(EnumLocalTable.NAME, new JLabel(currentFolderItems[index].getName(), fileSystemView.getSystemIcon(currentFolderItems[index]),
                                                        JLabel.LEFT));
            dataCol
            .set(EnumLocalTable.SIZE, new JLabel("  " + dbsentry.filesync.clientgui.utility.GeneralUtil.getDocSizeForDisplay(currentFolderItems[index]
                                                                                                                               .length()) +
                                                        "  ", JLabel.RIGHT));
            dataCol.set(EnumLocalTable.ABS_SIZE, new JLabel(Long.toString(currentFolderItems[index].length())));
            association = assoService.getAssociationByContent(currentFolderItems[index].toURL());
            if (association != null) {
              String typeDesc = association.getDescription();
              if (typeDesc != null) {
                dataCol.set(EnumLocalTable.TYPE, new JLabel(typeDesc + "  "));
              }
            }
          }

          dataCol
          .set(EnumLocalTable.MODIFIED, new JLabel(dbsentry.filesync.clientgui.utility.GeneralUtil.getDateForDisplay(new Date(currentFolderItems[index]
                                                                                                                               .lastModified())) +
                                                          "  "));
          dataCol.set(EnumLocalTable.ABS_MODIFIED, new JLabel(currentFolderItems[index].lastModified() + "  "));
          dataCol
          .set(EnumLocalTable.DESCRIPTION, new JLabel(fileSystemView.getSystemDisplayName(currentFolderItems[index]) +
                                                             "  "));
          if (currentFolder.canWrite()) {
            dataCol.set(EnumLocalTable.PERMISSION, new JLabel("read, write"));
          } else if (currentFolder.canRead()) {
            dataCol.set(EnumLocalTable.PERMISSION, new JLabel("read-only"));
          }

          if (currentFolderItems[index].isDirectory()) {
            dataCol.set(EnumLocalTable.ITEM_TYPE, new JLabel("Folder"));
          } else {
            dataCol.set(EnumLocalTable.ITEM_TYPE, new JLabel("File"));
          }
          dataCol.set(EnumLocalTable.ABS_PATH, new JLabel(currentFolderItems[index].getAbsolutePath()));
          dataRow.add(dataCol);
        }
      } else {
        //tfStatus.setText(currentFolderItemsCount + " Object(s)");
         mdiParent.fsStatusBar.setLblSetMessage("");
         mdiParent.fsStatusBar.setLblSetMessage(currentFolderItemsCount + " Object(s)");
      }
      fsTableModelLocal = (FsTableModel)tblLocalFolderFileList.getModel();
      fsTableModelLocal.setDataVector(dataRow);

      dbsentry.filesync.clientgui.utility.GeneralUtil
      .sortAllRowsBy(tblLocalFolderFileList, EnumLocalTable.NAME, tableHeaderMouseListener.isSortOrderNameAsc());
      
    } catch (Exception ex) {
      logger.error(GeneralUtil.getStackTrace(ex));
    }
    setDefaultCursor4Local();
  }

  private void postJbInitOperation() {
    //applyPreferences();
    folderDocInfoClient = new FolderDocInfoClient();
    initializeLocalFolderFileList();
    file = new File(System.getProperty("user.home"));
    setWaitCursor4Local();
    fillLocalFolderFileList(file);
    addItemToLocalComboBox(file.getAbsolutePath());
    folderDocInfoClient.setCurrentFolderPath(file.getAbsolutePath());
    folderDocInfoClient.addFolderPath(file.getAbsolutePath());
    butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
    butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
    butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());

    localWillTreeExpansionListener = new LocalWillTreeExpansionListener(treeLocalTreeView);
    localWillTreeExpansionListener.initializeLocalTreeView();
    fsFileSystemOperationLocal = new FsFileSystemOperationLocal(mdiParent);
    fsFileSystemOperationLocal.addPropertyChangeSupport(new PropertyChangeListener() {
                                                          public synchronized void propertyChange(PropertyChangeEvent evt) {
                                                            propertyChangeFileSystemOperationLocal(evt);
                                                          }
                                                        }
    );

  }

  /**
        * Listens to the property change event fired by FileSystemOperationLocal class.
        * @param evt PropertyChangeEvent object
        */
  public void propertyChangeFileSystemOperationLocal(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("localPasteOperationComplete")) {
      ArrayList pasteOperationComplete = (ArrayList)evt.getNewValue();
      Integer clipBoardOperation = (Integer)pasteOperationComplete.get(0);
      String srcBasePath = (String)pasteOperationComplete.get(1);
      String destBasePath = (String)pasteOperationComplete.get(2);
      logger.debug("clipBoardOperation : " + clipBoardOperation);
      logger.debug("srcBasePath : " + srcBasePath);
      logger.debug("destBasePath : " + destBasePath);

      if (clipBoardOperation.intValue() == EnumClipBoardOperation.CUT) {
        //refresh tree node for source folder
        DefaultMutableTreeNode rootNode =
          (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
        DefaultMutableTreeNode treeNodeToRefresh =
          dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(rootNode, srcBasePath);
        logger.debug("treeNodeToRefresh  : " + treeNodeToRefresh);
        FsFileSystemOperationLocal.refreshTreeNode(treeLocalTreeView, treeNodeToRefresh);
      }
      //refresh tree node for destination folder
      DefaultMutableTreeNode rootNode =
        (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
      DefaultMutableTreeNode treeNodeToRefresh =
        dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(rootNode, destBasePath);
      logger.debug("treeNodeToRefresh  : " + treeNodeToRefresh);
      FsFileSystemOperationLocal.refreshTreeNode(treeLocalTreeView, treeNodeToRefresh);
      setWaitCursor4Local();
      fillLocalFolderFileList(new File(destBasePath));
      //setDefaultCursorForLocalBrowser();
    } else if (evt.getPropertyName().equals("refreshFolderPath")) {
      //setDefaultCursorForLocalBrowser();
      file = new File((String)evt.getNewValue());
      setWaitCursor4Local();
      fillLocalFolderFileList(file);
    }
  }


  /**
   * returns the parent of a folder with the specified folderpath.
   * @param currentFolderPath current folder path
   * @return File parent file
   */
  private File getParentFile(String currentFolderPath) {
    String pathTemp;
    pathTemp = currentFolderPath.substring(0, currentFolderPath.lastIndexOf(File.separator));
    file = new File(pathTemp);
    if (file.exists()) {
      return file;
    } else {
      return getParentFile(pathTemp);
    }
  }


  /**
    * To move back to the previous folder in local browser and sets the current folder to 
    * previous folder.
   
    */
  private void butLocalNavigateBack_actionPerformed() {
    //setWaitCursor4Local();
    String currentFolderPath = folderDocInfoClient.getPrevFolderPath();
    setWaitCursor4Local();
    fillLocalFolderFileList(new File(currentFolderPath));
    if (!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)) {
      splpLocalSystem.remove(splpLocalSystem.getRightComponent());
      splpLocalSystem.setRightComponent(jpLocalFolderFileList);
    }

    addItemToLocalComboBox(currentFolderPath);
    folderDocInfoClient.setCurrentFolderPath(currentFolderPath);
    butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
    butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
    butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());

    DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
    DefaultMutableTreeNode nodeToFill = GeneralUtil.findTreeNode(root, currentFolderPath);
    FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
    String nodePathToFill = fsFolderHolder.getPath();
    logger.debug("Current Folder Path : " + currentFolderPath);
    logger.debug("Path of Node To be Filled: " + nodePathToFill);

    TreeNode treeNodes[] = nodeToFill.getPath();
    TreePath treePathToSelect = new TreePath(treeNodes);
    treeLocalTreeView.setSelectionPath(treePathToSelect);
    treeLocalTreeView.requestFocus();
    logger.debug("treePathToSelect " + treePathToSelect);
    setDefaultCursor4Local();
  }


  /**
   * To move forward to the next folder in local browser and sets the current folder to 
   * next folder.
  
   */
  private void butLocalNavigateForward_actionPerformed() {
    //setWaitCursor4Local();
    String currentFolderPath = folderDocInfoClient.getNextFolderPath();
    setWaitCursor4Local();
    fillLocalFolderFileList(new File(currentFolderPath));
    if (!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)) {
      splpLocalSystem.remove(splpLocalSystem.getRightComponent());
      ;
      splpLocalSystem.setRightComponent(jpLocalFolderFileList);
    }

    addItemToLocalComboBox(currentFolderPath);
    folderDocInfoClient.setCurrentFolderPath(currentFolderPath);
    butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
    butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
    butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());

    DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
    DefaultMutableTreeNode nodeToFill = GeneralUtil.findTreeNode(root, currentFolderPath);
    FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
    String nodePathToFill = fsFolderHolder.getPath();
    logger.debug("Current Folder Path : " + currentFolderPath);
    logger.debug("Path of Node To be Filled: " + nodePathToFill);

    TreeNode treeNodes[] = nodeToFill.getPath();
    TreePath treePathToSelect = new TreePath(treeNodes);
    treeLocalTreeView.setSelectionPath(treePathToSelect);
    treeLocalTreeView.requestFocus();
    logger.debug("treePathToSelect " + treePathToSelect);
    setDefaultCursor4Local();
  }


  /**
    * To navigate to parent folder in local browser, will work only 
    * on folderlist not on tree structure
   
    *
    */
  private void btnLocalNavigateUp_actionPerformed() {
    //setWaitCursor4Local();
    boolean isRootFile = false;
    String currentFolderPath = folderDocInfoClient.getCurrentFolderPath();
    File[] roots = File.listRoots();
    int rootLength = roots.length;
    file = new File(currentFolderPath);
    if (file.exists()) {
      for (int index = 0; index < rootLength; index++) {
        if (file.getAbsolutePath().equals(roots[index].getAbsolutePath())) {
          isRootFile = true;
          break;
        }
      }
      if (!isRootFile) {
        file = file.getParentFile();
      }
    } else {
      file = getParentFile(currentFolderPath);
    }

    addItemToLocalComboBox(file.toString());
    setWaitCursor4Local();
    fillLocalFolderFileList(file);
    if (!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)) {
      splpLocalSystem.remove(splpLocalSystem.getRightComponent());
      splpLocalSystem.setRightComponent(jpLocalFolderFileList);
    }
    folderDocInfoClient.setCurrentFolderPath(file.getAbsolutePath());
    folderDocInfoClient.addFolderPath(file.getAbsolutePath());
    butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
    butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
    butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());

    DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
    DefaultMutableTreeNode nodeToFill = GeneralUtil.findTreeNode(root, file.getAbsolutePath());
    FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
    String nodePathToFill = fsFolderHolder.getPath();
    logger.debug("Current Folder Path : " + file.getAbsolutePath());
    logger.debug("Path of Node To be Filled: " + nodePathToFill);

    TreeNode treeNodes[] = nodeToFill.getPath();
    TreePath treePathToSelect = new TreePath(treeNodes);
    treeLocalTreeView.setSelectionPath(treePathToSelect);
    treeLocalTreeView.requestFocus();
    logger.debug("treePathToSelect " + treePathToSelect);
    setDefaultCursor4Local();
  }


  /**
   * To refresh the selected treenode, if node is not selected it refreshes the current
   * folderpath
  
   */
  private void butLocalRefreshContent_actionPerformed() {
    setWaitCursor4Local();
    localRefreshOperation();
  }


  private void butLocalHome_actionPerformed() {
    setWaitCursor4Local();
    localHomeOperation();
  }

  private void localHomeOperation() {

    file = new File(System.getProperty("user.home"));
    setWaitCursor4Local();
    fillLocalFolderFileList(file);
    addItemToLocalComboBox(file.getAbsolutePath());
    folderDocInfoClient.setCurrentFolderPath(file.getAbsolutePath());

    localWillTreeExpansionListener = new LocalWillTreeExpansionListener(treeLocalTreeView);
    localWillTreeExpansionListener.initializeLocalTreeView();
    setDefaultCursor4Local();
  }

  /**
    * creates new folder with specified name
    
    */
  public void butLocalCreateNewFolder_actionPerformed() {
    setWaitCursor4Local();
    localNewFolderOperation();

  }

  public void localNewFolderOperation() {
    String folderName = JOptionPane.showInputDialog(this, "Folder", "New Folder");
    
    if (folderName != null && !folderName.trim().equals("")) {
      String currentFolderPath = folderDocInfoClient.getCurrentFolderPath();
      file = new File(currentFolderPath + File.separator + folderName.trim());
      if (file.exists()) {
        JOptionPane.showMessageDialog(this, "Folder with that name already exists");
      } else {
        file.mkdirs();
        localRefreshOperation();
      }
    }
    setDefaultCursor4Local();
  }


  /**
    * To rename the selected folder or file
  
    */
  private void butLocalRenameFolderFile_actionPerformed() {
    setWaitCursor4Local();
    localRenameOperation();
    
  }


  /**
    * To delete a folder or file, deletes a folder recursively if it contains folders and files.
   
    */
  private void butLocalDeleteFolderFile_actionPerformed() {
    setWaitCursor4Local();
    localDeleteOperation();
    
  }


  public void butLocalPropertyFolderFile_actionPerformed() {
    localPropertyOperation();
  }

  public void localPropertyOperation() {
    //setWaitCursorForLocalBrowser();
    setWaitCursor4Local();
    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemPaths[] = new String[selectedRowsCount];
    //File itemFiles[] = new File[selectedRowsCount] ;


    if (selectedRowsCount != 0) {


      for (int index = 0; index < selectedRowsCount; index++) {
        itemPaths[index] =
          ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index], EnumRemoteTable.ABS_PATH)).getText();
        logger.debug("Inside property function" + itemPaths[index]);
        //itemFiles[index] = new File(itemPath);
      }
      fsFileSystemOperationLocal.showProperties(itemPaths);
      //setDefaultCursorForLocalBrowser();
      setDefaultCursor4Local();

    } else {
      //For tree Property
      String nodePath = "";
      FsFolderHolder fsFolderHolder = new FsFolderHolder();
      TreePath treePath = treeLocalTreeView.getSelectionPath();
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      logger.debug("Current node..........................." + currentNode);
      fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
      nodePath = fsFolderHolder.getPath();
      String itemPathTree[] = new String[1];
      itemPathTree[0] = nodePath;
      fsFileSystemOperationLocal.showProperties(itemPathTree);
      //setDefaultCursorForLocalBrowser();
      setDefaultCursor4Local();
    }

  }


  /**
    * will push the selected item paths onto a stack to use in paste operation
   
    */
  private void butLocalCut_actionPerformed() {
    localCutOperation();
  }


  /**
    * will push the selected item paths onto a stack to use in paste operation.
   
    */
  private void butLocalCopy_actionPerformed() {
    localCopyOperation();
  }

  /**
    * To perform paste operation depending upon whichever action copy or paste.
   
    */
  private void butLocalPaste_actionPerformed() {
    //setWaitCursorForLocalBrowser();
    localPasteOperation();
  }

  /**
   * to push the selected items to upload onto a stack and calls a function which performs 
   * actual upload.
   
   */
  private void butLocalUpload_actionPerformed() {
    localUploadOperation();
  }

  public void localUploadOperation() {

    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName = "";
    String itemPath = "";

    if (selectedRowsCount != 0) {

      //get items to upload
      
      for (int index = 0; index < selectedRowsCount; index++) {
        Stack itemToUpload = new Stack();
        itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index], EnumLocalTable.NAME)).getText();
        itemPath = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index], EnumLocalTable.ABS_PATH)).getText();
        
        itemToUpload.push(itemPath);
        logger.debug("i m in butLocalUpload_actionPerformed " + itemName + itemPath);
        int uploadCode = new Random().nextInt();
        logger
        .debug("Inside UploadDownload folderDocInfoClient.getCurrentFolderPath()" + folderDocInfoClient.getCurrentFolderPath());
        fsClient.upload((mdiParent.getUploadDownloadManager().createUploadListener(uploadCode)), itemToUpload, folderDocInfoClient
                        .getCurrentFolderPath(),
                        mdiParent.getExplorer().getFsRemoteView().getFsFolderDocInfoHolder().getCurrentFolderPath(),
                        uploadCode);
      }
      
      if (mdiParent.showUploadDnloadManager) {
        if(mdiParent.showUploadDnloadManager){
        mdiParent.popUploadDownloadManager();
        }
      }
      
      
    } else {
//      String nodePath = "";
//      Stack itemToUpload = new Stack();
//      FsFolderHolder fsFolderHolder = new FsFolderHolder();
//      TreePath treePath = treeLocalTreeView.getSelectionPath();
//      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
//      logger.debug("Current node..........................." + currentNode);
//      fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
//      nodePath = fsFolderHolder.getPath();
//      itemName = currentNode + "";
//      itemPath = nodePath;
//      itemToUpload.push(itemPath);
//      int uploadCode = new Random().nextInt();
//      if (mdiParent.showUploadDnloadManager) {
//        mdiParent.popUploadDownloadManager();
//      }
//
//      localTreeRefreshOperation();
//
//      fsClient
//      .upload((mdiParent.getUploadDownloadManager().createUploadListener(uploadCode)), itemToUpload, folderDocInfoClient
//                      .getCurrentFolderPath(),
//                      mdiParent.getExplorer().getFsRemoteView().getFsFolderDocInfoHolder().getCurrentFolderPath(),
//                      uploadCode);

    }
  }

  private void comboLocalAddressBar_keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      setWaitCursor4Local();
      searchLocalPath();
    }
  }

  /**
   * searches the path specified inside the addressbar and expands the tree to that 
   * folder and highlights that folder.
  
   */
  private void butLocalGo_actionPerformed() {
    //setWaitCursorForLocalBrowser();
    setWaitCursor4Local();
    searchLocalPath();
  }


  private void treeLocalTreeView_focusLost() {
    DefaultTreeCellRenderer tcr = (DefaultTreeCellRenderer)treeLocalTreeView.getCellRenderer();
    tcr.setBackgroundSelectionColor(new Color(204, 204, 204));
  }


  private void treeLocalTreeView_focusGained() {
    DefaultTreeCellRenderer tcr = (DefaultTreeCellRenderer)treeLocalTreeView.getCellRenderer();
    tcr.setBackgroundSelectionColor(selectionColor);
  }

  /**
   * to handle the change in node selection of local treeview.
   * with change in node selection it fills the table showing folderlist with the 
   * contents of selected node(folder).
   * @param e TreeSelectionEvent object
   */
  private void treeLocalTreeView_valueChanged(TreeSelectionEvent e) {

    String nodePath = "";
    FsFolderHolder fsFolderHolder = new FsFolderHolder();
    JTree tree = (JTree)e.getSource();
    logger.debug(" In valueChanged ");
    TreePath treePath = tree.getSelectionPath();
    if (treePath == null) {
      return;
    }
    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
    logger.debug("Current node..........................." + currentNode);
    fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
    nodePath = fsFolderHolder.getPath();

    int currentNodeLevel = currentNode.getLevel();
    logger.debug("Level : " + currentNodeLevel);
    logger.debug("Current Node Path : " + folderDocInfoClient.getCurrentFolderPath());
    logger.debug("currentNode Path with selected folder:  " + nodePath);
    if (currentNodeLevel != 0 && !folderDocInfoClient.getCurrentFolderPath().equals(nodePath)) {
      file = new File(nodePath);
      fillLocalFolderFileList(file);
      if (!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)) {
        splpLocalSystem.remove(splpLocalSystem.getRightComponent());
        ;
        splpLocalSystem.setRightComponent(jpLocalFolderFileList);
      }
      logger.debug("nodePath : " + nodePath);
 
      addItemToLocalComboBox(nodePath);
      folderDocInfoClient.setCurrentFolderPath(file.getAbsolutePath());
      folderDocInfoClient.addFolderPath(file.getAbsolutePath());
      butLocalNavigateBack.setEnabled(folderDocInfoClient.isBackButtonEnabled());
      butLocalNavigateForward.setEnabled(folderDocInfoClient.isForwardButtonEnabled());
      butLocalNavigateUp.setEnabled(folderDocInfoClient.isParentButtonEnabled());
    }
 
  }


  private void tblLocalFolderFileList_keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
      localDeleteOperation();
      e.setKeyCode(0);
    } else if (e.getKeyCode() == KeyEvent.VK_X) {
      if (e.getModifiers() == KeyEvent.CTRL_MASK) {
        localCutOperation();
      }
    } else if (e.getKeyCode() == KeyEvent.VK_C) {
      if (e.getModifiers() == KeyEvent.CTRL_MASK) {
        localCopyOperation();
      }
    } else if (e.getKeyCode() == KeyEvent.VK_V) {
      if (e.getModifiers() == KeyEvent.CTRL_MASK) {
        localPasteOperation();
      }
    } else if (e.getKeyCode() == KeyEvent.VK_F2) {
      localRenameOperation();
    } else if (e.getKeyCode() == KeyEvent.VK_F5) {
      localRefreshOperation();
    }
  }

  private void tblLocalFolderFileList_focusLost() {
    tblLocalFolderFileList.setSelectionBackground(new Color(204, 204, 204));
  }

  private void tblLocalFolderFileList_focusGained() {
    tblLocalFolderFileList.setSelectionBackground(selectionColor);
  }

  /**
   * Handles the mouse-click of table showing the local folder and file list.
   * @param e MouseEvent object
   */
  private void tblLocalFolderFileList_mouseClicked(MouseEvent e) {
    logger.debug("In mouseClicked of local Folder File List");
    JTable table = (JTable)e.getSource();
    FsTableModel fsTableModel = (FsTableModel)table.getModel();
    logger.debug("In mouseClicked of local Folder File List");
    if (e.getClickCount() == 2) {
      logger.debug("In mouseClicked of local Folder File List mouseClicked two times");
      // The index of the column whose header was clicked
      int vRowIndex = table.getSelectedRow();
      if (((JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable.ITEM_TYPE)).getText().equals("Folder")) {
        JLabel label = (JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable.ABS_PATH);
        String absFolderPath = label.getText();

        addItemToLocalComboBox(absFolderPath);
        File fileAbsFolderPath = new File(label.getText());
        fillLocalFolderFileList(fileAbsFolderPath);
        logger.debug("fileAbsFolderPath.getAbsolutePath() : " + fileAbsFolderPath.getAbsolutePath());

        DefaultMutableTreeNode root =
          (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
        DefaultMutableTreeNode nodeToFill =
          dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root, absFolderPath);
        logger.debug("Path of Node To be Filled: " + ((FsFolderHolder)nodeToFill.getUserObject()).getPath());
        logger.debug("Absolute Folder Path : " + absFolderPath);

        FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
        String nodePathToFill = fsFolderHolder.getPath();
        DefaultMutableTreeNode subTreeRoot = new DefaultMutableTreeNode();
        subTreeRoot.setUserObject(null);
        DefaultMutableTreeNode nodeToHighlight = new DefaultMutableTreeNode();
        logger.debug("Path of Node To be Filled:" + nodePathToFill);
        File filePathToFill = new File(nodePathToFill);
        File[] children = filePathToFill.listFiles();
        DefaultMutableTreeNode childNode, subChildNode;

        if (fileAbsFolderPath.equals(filePathToFill)) {
          nodeToHighlight = nodeToFill;
          TreeNode treeNodes[] = nodeToHighlight.getPath();
          TreePath treePathToHighlight = new TreePath(treeNodes);
          treeLocalTreeView.setSelectionPath(treePathToHighlight);
          treeLocalTreeView.requestFocus();
        } else if (fileAbsFolderPath.getParentFile() != null) {
          File subChildFiles[];
          int subChildFilesLength;
          if (fileAbsFolderPath.getParentFile().getAbsolutePath().equals(nodePathToFill)) {
            int index = 0;
            nodeToFill.removeAllChildren();
            for (int i = 0; i < children.length; i++) {
              if (children[i].isDirectory()) {
                childNode = new DefaultMutableTreeNode();
                nodeToFill.add(childNode);
                fsFolderHolder = new FsFolderHolder();
                fsFolderHolder.setName(children[i].getName());
                fsFolderHolder.setPath(children[i].getAbsolutePath());
                childNode.setUserObject(fsFolderHolder);
                subChildFiles = children[i].listFiles();
                if (subChildFiles != null) {
                  subChildFilesLength = subChildFiles.length;
                  logger.debug("subChildFilesLength : " + subChildFilesLength);
                  for (int counter = 0; counter < subChildFilesLength; counter++) {
                    if (subChildFiles[counter].isDirectory()) {
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
                if (fileAbsFolderPath.equals(children[i])) {
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
            DefaultMutableTreeNode treeNodeToSelect = (DefaultMutableTreeNode)nodeToFill.getChildAt(index);
            treeNodes = treeNodeToSelect.getPath();
            logger.debug("treeNodeToSelect : " + nodeToFill.getChildAt(index));
            TreePath treePathToSelect = new TreePath(treeNodes);
            treeLocalTreeView.setSelectionPath(treePathToSelect);
            treeLocalTreeView.requestFocus();
            logger.debug("treePathToSelect " + treePathToSelect);
          } else if (!fileAbsFolderPath.equals(filePathToFill)) {
            localWillTreeExpansionListener
            .constructSubTree(fileAbsFolderPath, filePathToFill, subTreeRoot, nodeToHighlight);
            logger.debug("Filling the Node");
            nodeToFill.removeAllChildren();
            for (int i = 0; i < children.length; i++) {
              if (((FsFolderHolder)subTreeRoot.getUserObject()).getName().equals(children[i].getName())) {
                nodeToFill.add(subTreeRoot);
                logger.debug(" Adding Node : " + subTreeRoot);
              } else {
                childNode = new DefaultMutableTreeNode();
                nodeToFill.add(childNode);
                subChildFiles = children[i].listFiles();
                if (subChildFiles != null) {
                  subChildFilesLength = subChildFiles.length;
                  logger.debug("subChildFilesLength : " + subChildFilesLength);
                  for (int counter = 0; counter < subChildFilesLength; counter++) {
                    if (subChildFiles[counter].isDirectory()) {
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
            logger.debug(" Node To Highlight Parent : " + (DefaultMutableTreeNode)nodeToHighlight.getParent());
            TreePath treePathToExpand = new TreePath(treeNodes);
            logger.debug("Path To Expand : " + treePathToExpand);
            treeLocalTreeView.expandPath(treePathToExpand);
            treeNodes = nodeToHighlight.getPath();
            TreePath treePathToSelect = new TreePath(treeNodes);
            treeLocalTreeView.setSelectionPath(treePathToSelect);
            treeLocalTreeView.requestFocus();
          }
        }
      } else {
        //        JOptionPane.showMessageDialog(this,"Do you want open this file ");
      }
    } else {

      int selectedRows[] = table.getSelectedRows();
      int selectedRowsCount = selectedRows.length;
      boolean flag = false;
      logger.debug(" selectedRowsCount " + selectedRowsCount);
      if (selectedRowsCount == 0) {
        disableButtonOperation(flag);

        return;
      }
      if (selectedRowsCount >= 1) {
        //tfStatus.setText(selectedRowsCount + " Object(s) selected");
         mdiParent.fsStatusBar.setLblSetMessage("");
         mdiParent.fsStatusBar.setLblSetMessage(selectedRowsCount + " Object(s) selected");
        flag = true;
        enableButtonOperation(flag);

      } else {
        int vRowIndex = table.getSelectedRow();

        if (((JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable.ITEM_TYPE)).getText().equals("Folder")) {
         // tfStatus.setText(selectedRowsCount + " Object(s) selected");
          mdiParent.fsStatusBar.setLblSetMessage("");
          mdiParent.fsStatusBar.setLblSetMessage(selectedRowsCount + " Object(s) selected");
        } else {
          String type = ((JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable.TYPE)).getText();
          String size = ((JLabel)fsTableModel.getValueAt(vRowIndex, EnumLocalTable.SIZE)).getText();
         // tfStatus.setText("Type: " + type + "Size:" + size);
          mdiParent.fsStatusBar.setLblSetMessage("");
          mdiParent.fsStatusBar.setLblSetMessage("Type: " + type + "Size:" + size);
        }
      }
    }
  }

  private void searchLocalPath() {
    //setWaitCursor4Local();
    String addressPath = (String)comboLocalAddressBar.getSelectedItem();
    logger.debug("Local Search Path:" + addressPath);

    DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
    DefaultMutableTreeNode nodeToFill =
      dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root, addressPath);

    if (nodeToFill != null) {
      File fileAddress = new File(addressPath);
      logger.debug("fileAddress.getParentFile() : " + fileAddress.getParentFile());
      if (fileAddress.exists()) {
        if (fileAddress.isDirectory()) {
          fillLocalFolderFileList(fileAddress);

          if (!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)) {
            splpLocalSystem.remove(splpLocalSystem.getRightComponent());
            ;
            splpLocalSystem.setRightComponent(jpLocalFolderFileList);
          }

          FsFolderHolder fsFolderHolder = (FsFolderHolder)nodeToFill.getUserObject();
          String nodePathToFill = fsFolderHolder.getPath();
          DefaultMutableTreeNode subTreeRoot = new DefaultMutableTreeNode();
          DefaultMutableTreeNode nodeToHighlight = new DefaultMutableTreeNode();
          logger.debug("Path of Node To be Filled:" + nodePathToFill);
          File filePathToFill = new File(nodePathToFill);
          File[] children = filePathToFill.listFiles();
          DefaultMutableTreeNode childNode, subChildNode;
          if (fileAddress.equals(filePathToFill)) {
            nodeToHighlight = nodeToFill;
            TreeNode treeNodes[] = nodeToHighlight.getPath();
            TreePath treePathToHighlight = new TreePath(treeNodes);
            treeLocalTreeView.setSelectionPath(treePathToHighlight);
            treeLocalTreeView.requestFocus();
          } else if (fileAddress.getParentFile() != null) {
            File subChildFiles[];
            int subChildFilesLength;
            if (fileAddress.getParentFile().getAbsolutePath().equals(nodePathToFill)) {
              int index = 0;
              nodeToFill.removeAllChildren();
              for (int i = 0; i < children.length; i++) {
                if (children[i].isDirectory()) {
                  childNode = new DefaultMutableTreeNode();
                  nodeToFill.add(childNode);
                  fsFolderHolder = new FsFolderHolder();
                  fsFolderHolder.setName(children[i].getName());
                  fsFolderHolder.setPath(children[i].getAbsolutePath());
                  childNode.setUserObject(fsFolderHolder);
                  subChildFiles = children[i].listFiles();
                  if (subChildFiles != null) {
                    subChildFilesLength = subChildFiles.length;
                    logger.debug("subChildFilesLength : " + subChildFilesLength);
                    for (int counter = 0; counter < subChildFilesLength; counter++) {
                      if (subChildFiles[counter].isDirectory()) {
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
                  if (fileAddress.equals(children[i])) {
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
              DefaultMutableTreeNode treeNodeToSelect = (DefaultMutableTreeNode)nodeToFill.getChildAt(index);
              treeNodes = treeNodeToSelect.getPath();
              logger.debug("treeNodeToSelect : " + nodeToFill.getChildAt(index));
              TreePath treePathToSelect = new TreePath(treeNodes);
              treeLocalTreeView.setSelectionPath(treePathToSelect);
              treeLocalTreeView.requestFocus();
              logger.debug("treePathToSelect " + treePathToSelect);
            } else if (!fileAddress.equals(filePathToFill)) {
              localWillTreeExpansionListener
              .constructSubTree(fileAddress, filePathToFill, subTreeRoot, nodeToHighlight);
              logger.debug("Filling the Node");
              nodeToFill.removeAllChildren();
              for (int i = 0; i < children.length; i++) {
                if (((FsFolderHolder)subTreeRoot.getUserObject()).getName().equals(children[i].getName())) {
                  nodeToFill.add(subTreeRoot);
                  logger.debug(" Adding Node : " + subTreeRoot);
                } else {
                  childNode = new DefaultMutableTreeNode();
                  nodeToFill.add(childNode);
                  subChildFiles = children[i].listFiles();
                  if (subChildFiles != null) {
                    subChildFilesLength = subChildFiles.length;
                    logger.debug("subChildFilesLength : " + subChildFilesLength);
                    for (int counter = 0; counter < subChildFilesLength; counter++) {
                      if (subChildFiles[counter].isDirectory()) {
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
              logger.debug(" Node To Highlight Parent : " + (DefaultMutableTreeNode)nodeToHighlight.getParent());
              TreePath treePathToExpand = new TreePath(treeNodes);
              logger.debug("Path To Expand : " + treePathToExpand);
              treeLocalTreeView.expandPath(treePathToExpand);
              treeNodes = nodeToHighlight.getPath();
              TreePath treePathToSelect = new TreePath(treeNodes);
              treeLocalTreeView.setSelectionPath(treePathToSelect);
              treeLocalTreeView.requestFocus();
            }
          }
        } else {
          JOptionPane.showMessageDialog(this, "Do You want To DownLoad File");
        }
      } else {
        //tfStatus.setText("Invalid Path");
         mdiParent.fsStatusBar.setLblSetMessage("");
         mdiParent.fsStatusBar.setLblSetMessage("Invalid Path");
        if (splpLocalSystem.getBottomComponent().equals(jpLocalFolderFileList)) {
          splpLocalSystem.remove(splpLocalSystem.getBottomComponent());
          JPanel panel = new JPanel(new BorderLayout());
          panel.setBackground(Color.WHITE);
          JLabel label = new JLabel("Invalid Path", FsImage.iconWarning, JLabel.CENTER);
          label.setBackground(Color.WHITE);
          panel.add(label, BorderLayout.CENTER);
          splpLocalSystem.add(panel, JSplitPane.BOTTOM);
        }
      }
    } else {
      comboLocalAddressBar.removeItemAt(0);
      comboLocalAddressBar.setSelectedItem(folderDocInfoClient.getCurrentFolderPath());
      //tfStatus.setText("Invalid Path");
       mdiParent.fsStatusBar.setLblSetMessage("");
       mdiParent.fsStatusBar.setLblSetMessage("Invalid Path");
      if (splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)) {
        splpLocalSystem.remove(splpLocalSystem.getRightComponent());
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel("Invalid Path", FsImage.iconWarning, JLabel.CENTER);
        label.setBackground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER);
        splpLocalSystem.add(panel, JSplitPane.BOTTOM);
      }
    }
    //setDefaultCursorForLocalBrowser();
    setDefaultCursor4Local();
  }

  private void addItemToLocalComboBox(String path) {
    comboLocalAddressBar.removeActionListener(actionListener4LocalAddressBar);
    int count = comboLocalAddressBar.getItemCount();
    for (int index = 0; index < count; index++) {
      if (comboLocalAddressBar.getItemAt(index).equals(path)) {
        comboLocalAddressBar.setSelectedIndex(index);
        comboLocalAddressBar.addActionListener(actionListener4LocalAddressBar);
        return;
      }
    }
    comboLocalAddressBar.addItem(path);
    comboLocalAddressBar.setSelectedItem(path);
    comboLocalAddressBar.addActionListener(actionListener4LocalAddressBar);
  }

  public void localRefreshOperation() {
    //setWaitCursor4Local();
    DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode)treeLocalTreeView.getLastSelectedPathComponent();
    if (selectedTreeNode != null) {
      FsFileSystemOperationLocal.refreshTreeNode(treeLocalTreeView, selectedTreeNode);
    } else {
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
      selectedTreeNode =
        dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root, folderDocInfoClient.getCurrentFolderPath());
      TreeNode treeNodes[] = selectedTreeNode.getPath();
      TreePath treePathToSelect = new TreePath(treeNodes);
      treeLocalTreeView.setSelectionPath(treePathToSelect);
      treeLocalTreeView.requestFocus();
    }
    setWaitCursor4Local(); 
    fillLocalFolderFileList(new File(folderDocInfoClient.getCurrentFolderPath()));
    if (!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)) {
      splpLocalSystem.remove(splpLocalSystem.getRightComponent());
      ;
      splpLocalSystem.setRightComponent(jpLocalFolderFileList);
    }
    setDefaultCursor4Local();
  }

  public void localTreeRefreshOperation() {
    //setWaitCursor4Local();
    DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode)treeLocalTreeView.getLastSelectedPathComponent();
    if (selectedTreeNode != null) {
      FsFileSystemOperationLocal
      .refreshTreeNode(treeLocalTreeView, (DefaultMutableTreeNode)selectedTreeNode.getParent());
    } else {
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
      selectedTreeNode =
        dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root, folderDocInfoClient.getCurrentFolderPath());
      TreeNode treeNodes[] = selectedTreeNode.getPath();
      TreePath treePathToSelect = new TreePath(treeNodes);
      treeLocalTreeView.setSelectionPath(treePathToSelect);
      treeLocalTreeView.requestFocus();
    }
    setWaitCursor4Local();
    fillLocalFolderFileList(new File(folderDocInfoClient.getCurrentFolderPath()));
    if (!splpLocalSystem.getRightComponent().equals(jpLocalFolderFileList)) {
      splpLocalSystem.remove(splpLocalSystem.getRightComponent());
      ;
      splpLocalSystem.setRightComponent(jpLocalFolderFileList);
    }
    setDefaultCursor4Local();
  }

  public void localDeleteOperation() {
    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName = "";
    String itemPath = "";
    if (selectedRows.length > 0) {
      if (selectedRowsCount == 1) {
        itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[0], EnumLocalTable.NAME)).getText();
        int delete =
          JOptionPane.showConfirmDialog(this, "Are you sure you want to delete item " + "\"" + itemName + "\"",
                                                   "Delete", JOptionPane.YES_NO_OPTION);
        if (delete != JOptionPane.YES_OPTION) {
          return;
        }
      } else {
        int delete =
          JOptionPane.showConfirmDialog(this, "Are you sure you want to delete these " + selectedRowsCount + " items",
                                                   "Delete", JOptionPane.YES_NO_OPTION);
        if (delete != JOptionPane.YES_OPTION) {
          return;
        }
      }
      try {
        for (int index = 0; index < selectedRowsCount; index++) {
          itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index], EnumLocalTable.NAME)).getText();
          itemPath = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index], EnumLocalTable.ABS_PATH)).getText();
          file = new File(itemPath);
          if (file.exists()) {
            logger.debug("Deleting item : " + itemPath);
            if (!fsFileSystemOperationLocal.deleteItem(file)) {
              break;
            }
          }
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Insufficient right to delete this folder " + "\"" + itemName + "\"");
        logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
      }
      setWaitCursor4Local();
      localRefreshOperation();
    } else {
      String nodePath = "";
      FsFolderHolder fsFolderHolder = new FsFolderHolder();
      TreePath treePath = treeLocalTreeView.getSelectionPath();
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      logger.debug("Current node..........................." + currentNode);
      fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
      nodePath = fsFolderHolder.getPath();
      itemName = currentNode + "";
      int delete =
        JOptionPane.showConfirmDialog(this, "Are you sure you want to delete item " + "\"" + itemName + "\"", "Delete",
                                                 JOptionPane.YES_NO_OPTION);
      if (delete != JOptionPane.YES_OPTION) {
        return;
      }

      try {
        itemPath = nodePath;
        file = new File(itemPath);
        if (file.exists()) {
          logger.debug("Deleting item : " + itemPath);
          if (!fsFileSystemOperationLocal.deleteItem(file)) {
            //break;
          }
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Insufficient right to delete this folder " + "\"" + itemName + "\"");
        logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(e));
      }
      setWaitCursor4Local();
      localTreeRefreshOperation();
    }
    setDefaultCursor4Local();
  }

  public void localCutOperation() {
    clipBoardOperationLocal = EnumClipBoardOperation.CUT;

    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName = "";
    String itemPath = "";

    if (selectedRowsCount != 0) {
      clipBoardLocal.clear();
      butLocalPaste.setEnabled(false);
      pasteMenuItem.setEnabled(false);
      for (int index = 0; index < selectedRowsCount; index++) {
        itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index], EnumRemoteTable.NAME)).getText();
        itemPath = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index], EnumRemoteTable.ABS_PATH)).getText();
        clipBoardLocal.push(itemPath);
        butLocalPaste.setEnabled(true);
        pasteMenuItem.setEnabled(true);
      }
    } else {
      String nodePath = "";
      FsFolderHolder fsFolderHolder = new FsFolderHolder();
      TreePath treePath = treeLocalTreeView.getSelectionPath();
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      logger.debug("Current node..........................." + currentNode);
      fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
      nodePath = fsFolderHolder.getPath();
      itemName = currentNode + "";
      itemPath = nodePath;
      clipBoardLocal.push(itemPath);
      butLocalPaste.setEnabled(true);
      pasteMenuItem.setEnabled(true);

    }
  }

  public void localCopyOperation() {
    clipBoardOperationLocal = EnumClipBoardOperation.COPY;

    int selectedRows[] = tblLocalFolderFileList.getSelectedRows();
    int selectedRowsCount = selectedRows.length;
    String itemName = "";
    String itemPath = "";

    if (selectedRowsCount != 0) {

      clipBoardLocal.clear();
      butLocalPaste.setEnabled(false);
      pasteMenuItem.setEnabled(false);
      for (int index = 0; index < selectedRowsCount; index++) {
        itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index], EnumRemoteTable.NAME)).getText();
        itemPath = ((JLabel)fsTableModelLocal.getValueAt(selectedRows[index], EnumRemoteTable.ABS_PATH)).getText();
        clipBoardLocal.push(itemPath);
        butLocalPaste.setEnabled(true);
        pasteMenuItem.setEnabled(true);
      }
    } else {
      clipBoardLocal.clear();
      butLocalPaste.setEnabled(false);
      pasteMenuItem.setEnabled(false);
      String nodePath = "";
      FsFolderHolder fsFolderHolder = new FsFolderHolder();
      TreePath treePath = treeLocalTreeView.getSelectionPath();
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      logger.debug("Current node..........................." + currentNode);
      fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
      nodePath = fsFolderHolder.getPath();
      itemName = currentNode + "";
      itemPath = nodePath;
      clipBoardLocal.push(itemPath);
      butLocalPaste.setEnabled(true);
      pasteMenuItem.setEnabled(true);

    }
  }

  public void localPasteOperation() {
    String destBasePath;
    String srcBasePath;

    if (clipBoardLocal.isEmpty()) {
      butLocalPaste.setEnabled(false);
      pasteMenuItem.setEnabled(false);
      return;
    }
    try {
      srcBasePath = new File((String)clipBoardLocal.firstElement()).getParent();
      destBasePath = folderDocInfoClient.getCurrentFolderPath();
      fsFileSystemOperationLocal.pasteItem(clipBoardLocal, destBasePath, clipBoardOperationLocal);
    } catch (Exception ex) {
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
    }
  }

  public void localRenameOperation() {
    int selectedRow = tblLocalFolderFileList.getSelectedRow();
    String itemName = "";
    String itemPath = "";
    //For tree rename
    String nodePath = "";
    FsFolderHolder fsFolderHolder = new FsFolderHolder();
    TreePath treePath = treeLocalTreeView.getSelectionPath();

    if (selectedRow != -1) {
      itemName = ((JLabel)fsTableModelLocal.getValueAt(selectedRow, EnumLocalTable.NAME)).getText();
      itemPath = ((JLabel)fsTableModelLocal.getValueAt(selectedRow, EnumLocalTable.ABS_PATH)).getText();

      String newItemName = JOptionPane.showInputDialog(this, "Rename " + itemName, itemName);

      file = new File(itemPath);
      if (newItemName != null && !newItemName.trim().equals("")) {
        File renameFile = new File(file.getParentFile().getAbsolutePath() + File.separator + newItemName);
        if (!renameFile.exists()) {
          boolean renameSuccess = file.renameTo(renameFile);
          if (renameSuccess) {
            setWaitCursor4Local();
            localRefreshOperation();
          } else {
            JOptionPane.showMessageDialog(this, "Insufficient right to rename " + "\"" + itemName + "\"");
          }
        } else {
          JOptionPane
          .showMessageDialog(this, "Can not rename " + itemName + " :file with the name you specified already exists.");
        }
      }
    } else if (treePath == null) {
      return;
    } else {
      logger.debug(" In rename of tree ");
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
      logger.debug("Current node..........................." + currentNode);
      fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
      nodePath = fsFolderHolder.getPath();

      itemName = currentNode + "";
      itemPath = nodePath;

      String newItemName = JOptionPane.showInputDialog(this, "Rename " + itemName, itemName);

      file = new File(itemPath);
      if (newItemName != null && !newItemName.trim().equals("")) {
        File renameFile = new File(file.getParentFile().getAbsolutePath() + File.separator + newItemName);
        if (!renameFile.exists()) {
          boolean renameSuccess = file.renameTo(renameFile);
          if (renameSuccess) {
            setWaitCursor4Local();
            localTreeRefreshOperation();
          } else {
            JOptionPane.showMessageDialog(this, "Insufficient right to rename " + "\"" + itemName + "\"");
          }
        } else {
          JOptionPane
          .showMessageDialog(this, "Can not rename " + itemName + " :file with the name you specified already exists.");
        }
      }
    }
      setDefaultCursor4Local();
  }

  public void setDefaultCursor4Local() {
    cursorCounterLocal = -1;
    logger.debug(" cursorCounterLocal : " + cursorCounterLocal);
    if (cursorCounterLocal <= 0) {
      this.setCursor(defaultCursor);
    }
  }


  private void setWaitCursor4Local() {
    cursorCounterLocal++;
    this.setCursor(waitCursor);
  }


  private void comboLocalAddressBar_actionPerformed(ActionEvent e) {
    if (e.getModifiers() == KeyEvent.VK_SHIFT) {
      setWaitCursor4Local();
      searchLocalPath();
    }
  }

  public void setJpLocalTreeView(JPanel jpLocalTreeView) {
    this.jpLocalTreeView = jpLocalTreeView;
  }

  public JPanel getJpLocalTreeView() {
    return jpLocalTreeView;
  }

  public JButton getButLocalUpload() {
    return butLocalUpload;
  }

  public FolderDocInfoClient getFolderDocInfoClient() {
    return folderDocInfoClient;
  }

  private class ComboLocalAddressBar_ActionListener implements ActionListener {
    /**
       * handles actionPerformed on combobox.
       * @param e ActionEvent object
       */
    public void actionPerformed(ActionEvent e) {
      comboLocalAddressBar_actionPerformed(e);
    }
  }

  class TableTransferHandler extends StringTransferHandler {
    protected String exportString(JComponent c) {
      System.out.println("Inside export function of local view ");
      return "";
    }

    protected void importString(JComponent c, String str) {
      System.out.println("Inside import function of local view ");
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
          String str = (String)t.getTransferData(DataFlavor.stringFlavor);
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

