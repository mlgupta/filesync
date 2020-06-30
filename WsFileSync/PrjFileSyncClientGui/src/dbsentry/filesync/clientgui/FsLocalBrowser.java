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
 * $Id: FsLocalBrowser.java,v 1.26 2006/08/24 12:26:25 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.clientgui.listener.LocalWillTreeExpansionListener;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.FsFolderHolder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;


/**
 *	Purpose: To provide an interface to select a local folder.
 *  @author              Deepali Chitkulwar
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class FsLocalBrowser extends JDialog {
  private JScrollPane scrollPane4TreeView = new JScrollPane();

  private FsFileSync fsFileSync;

  private FsFolderHolder fsFolderHolder = new FsFolderHolder();

  private Logger logger;

  
  private GeneralUtil generalUtil;

  private JPanel panel4Widgets = new JPanel();

  private JPanel panel4FolderPath = new JPanel();

  private JLabel lblFolderPath = new JLabel();

  private JTextField txtFolderPath = new JTextField();

  private JPanel panel4Buttons = new JPanel();

  private JButton btnNewFolder = new JButton();

  private JButton btnCancel = new JButton();

  private JButton btnOk = new JButton();

  private JPanel panel4PackNorth = new JPanel();

  private JPanel panel4PackEast = new JPanel();

  private JPanel panel4PackWest = new JPanel();

  private JTree treeLocalTreeView = new JTree(new DefaultMutableTreeNode(null));

  /**
   * To create a dialog box.
   * @param parent frame which will act as parent of this dialog box.
   * @param title title of this dialog box.
   * @param modal boolean value which indicates if this dialog box is modal.
   */
  public FsLocalBrowser(FileSyncClientMDI parent, FsFileSync fsFileSync, String title, boolean modal) {
    super(parent, title, modal);
    try {
      jbInit();
      this.logger = Logger.getLogger("ClientLogger");
      this.generalUtil = new GeneralUtil();
      this.fsFileSync = fsFileSync;
      LocalWillTreeExpansionListener localWillTreeExpansionListener =
        new LocalWillTreeExpansionListener(treeLocalTreeView);
      localWillTreeExpansionListener.initializeLocalTreeView();
      generalUtil.centerForm(parent, this);
      //this.setVisible(true);
    } catch (Exception e) {
      logger.debug(generalUtil.getStackTrace(e));
    }
  }

  /**
   * To add the controls over the dialog and customize it
   * @throws Exception if operation fails.
	 */
  private void jbInit() throws Exception {
    
    treeLocalTreeView.setRootVisible(false);
    treeLocalTreeView.addTreeSelectionListener(new TreeSelectionListener() {
                                                 public void valueChanged(TreeSelectionEvent e) {
                                                   treeLocalTreeView_valueChanged(e);
                                                 }
                                               });
    
    scrollPane4TreeView.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
    scrollPane4TreeView.getViewport().add(treeLocalTreeView, null);
    
    lblFolderPath.setText("Folder");
    lblFolderPath.setBounds(new Rectangle(0, 5, 40, 19));
    lblFolderPath.setPreferredSize(new Dimension(50, 15));
    
    txtFolderPath.setBorder(BorderFactory.createEtchedBorder());
    txtFolderPath.setEditable(false);
    txtFolderPath.setPreferredSize(new Dimension(249, 40));
    
    panel4FolderPath.setLayout(new BorderLayout());
    panel4FolderPath.setSize(new Dimension(294, 15));
    panel4FolderPath.setPreferredSize(new Dimension(320, 15));
    panel4FolderPath.setBounds(new Rectangle(10, 5, 294, 15)); 
    panel4FolderPath.add(lblFolderPath, BorderLayout.WEST);
    panel4FolderPath.add(txtFolderPath, BorderLayout.CENTER);
    
    btnNewFolder.setText("New Folder");
    btnNewFolder.setPreferredSize(new Dimension(75, 25));
    btnNewFolder.setBorder(BorderFactory.createEtchedBorder());
    btnNewFolder.addActionListener(new ActionListener() {
                                           public void actionPerformed(ActionEvent e) {
                                             butCreateNewFolder_actionPerformed();
                                           }
                                         });
      
    btnOk.setText("Ok");
    btnOk.setPreferredSize(new Dimension(75, 25));
    btnOk.setBorder(BorderFactory.createEtchedBorder());
    btnOk.addActionListener(new ActionListener() {
                              public void actionPerformed(ActionEvent e) {
                                butOk_actionPerformed();
                              }
                            });
    
    btnCancel.setText("Cancel");
    btnCancel.setPreferredSize(new Dimension(75, 25));
    btnCancel.setBorder(BorderFactory.createEtchedBorder());
    btnCancel.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                    butCancel_actionPerformed();
                                  }
                                });
    
    panel4Buttons.setLayout( new FlowLayout(FlowLayout.RIGHT, 10, 5));
    panel4Buttons.setPreferredSize(new Dimension(274, 35));
    panel4Buttons.add(btnNewFolder, null);
    panel4Buttons.add(btnOk, null);
    panel4Buttons.add(btnCancel, null);
    
    panel4Widgets.setLayout(new BorderLayout());
    panel4Widgets.setPreferredSize(new Dimension(1, 60));
    panel4Widgets.add(panel4FolderPath, BorderLayout.CENTER);
    panel4Widgets.add(panel4PackNorth, BorderLayout.NORTH);
    panel4Widgets.add(panel4PackEast, BorderLayout.EAST);
    panel4Widgets.add(panel4PackWest, BorderLayout.WEST);
    panel4Widgets.add(panel4Buttons, BorderLayout.SOUTH);
    
    this.setTitle("Local Browser");
    this.setSize(new Dimension(331, 430));
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(panel4Widgets, BorderLayout.SOUTH);
    this.getContentPane().add(scrollPane4TreeView, BorderLayout.CENTER);
    
  }

  /**
   * Purpose :handles value change event of localtree. 
   * @param  
   * @logic  :If zero level node selected then do nothing else get the user object
   * associated with that node and retrieve folderpath from it.
   */
  private void treeLocalTreeView_valueChanged(TreeSelectionEvent e) {
    String nodePath = "";
    JTree tree = (JTree)e.getSource();
    TreePath treePath = tree.getSelectionPath();
    if (treePath == null) {
      return;
    }
    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
    int currentNodeLevel = currentNode.getLevel();
    if (currentNodeLevel != 0) {
      fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
      nodePath = fsFolderHolder.getPath();
      txtFolderPath.setText(nodePath);
    }
  }


  private void butOk_actionPerformed() {
    fsFileSync.getTxtLocalFolderPath().setText(fsFolderHolder.getPath());
    fsFileSync.setLocalFolderName(fsFolderHolder.getName());
    //this.setVisible(false);
    this.dispose();
    
  }

  /**
   * disposes this window on Cancel click.
   * 
   */
  private void butCancel_actionPerformed() {
    this.dispose();
  }

  /**
   * creates a folder with specified name inside the selected folder(treenode).
   * resfreshes the tree to visualize the same.
   * 
   */
  private void butCreateNewFolder_actionPerformed() {
    String folderName = JOptionPane.showInputDialog(this, "Folder");
    if (folderName != null && !folderName.trim().equals("")) {
      DefaultMutableTreeNode selectedTreeNode =
        (DefaultMutableTreeNode)treeLocalTreeView.getLastSelectedPathComponent();
      ;
      String selectedFolderPath = ((FsFolderHolder)selectedTreeNode.getUserObject()).getPath();
      File file = new File(selectedFolderPath + File.separator + folderName);
      if (file.exists()) {
        JOptionPane.showMessageDialog(this, "Folder with that name already exists");
      } else {
        file.mkdirs();
        TreeNode treeNodes[] = selectedTreeNode.getPath();
        TreePath treePath = new TreePath(treeNodes);
        FsFileSystemOperationLocal.refreshTreeNode(treeLocalTreeView, selectedTreeNode);
        treeLocalTreeView.expandPath(treePath);
        DefaultMutableTreeNode root =
          (DefaultMutableTreeNode)((DefaultTreeModel)treeLocalTreeView.getModel()).getRoot();
        DefaultMutableTreeNode nodeToSelect =
          dbsentry.filesync.clientgui.utility.GeneralUtil.findTreeNode(root, file.getAbsolutePath());
        treeNodes = nodeToSelect.getPath();
        treePath = new TreePath(treeNodes);
        treeLocalTreeView.setSelectionPath(treePath);
        treeLocalTreeView.scrollPathToVisible(treePath);
      }
    }
  }

}
