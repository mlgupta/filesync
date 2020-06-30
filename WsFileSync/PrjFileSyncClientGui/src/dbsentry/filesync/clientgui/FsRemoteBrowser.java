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
 * $Id: FsRemoteBrowser.java,v 1.37 2006/08/31 10:46:47 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.FsClient;
import dbsentry.filesync.clientgui.listener.RemoteWillTreeExpansionListener;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.FsExceptionHolder;
import dbsentry.filesync.common.FsFolderHolder;
import dbsentry.filesync.common.FsResponse;
import dbsentry.filesync.common.constants.FsRemoteOperationConstants;
import dbsentry.filesync.common.listeners.FsRemoteCommandListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;


/**
 *	To provide an interface to select a remote folder path.
 *  @author              Deepali Chitkulwar
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    Deepali Chitkulwar
 * 	Last Modfied Date:   11-07-2005
 */
public class FsRemoteBrowser extends JDialog{// implements PropertyChangeListener {
  private Logger logger;

  private JScrollPane scrollPane4Treeview = new JScrollPane();

  private JTree treeRemoteTreeView = new JTree(new DefaultMutableTreeNode(null));

  private FsFileSync fsFileSync;

  private RemoteWillTreeExpansionListener remoteWillTreeExpansionListener;

  private FsFolderHolder fsFolderHolder = new FsFolderHolder();
  
  private GeneralUtil generalUtil;

  private JPanel panel4Widgets = new JPanel();

  private JPanel panel4FolderPath = new JPanel();

  private JTextField txtFolderPath = new JTextField();

  private JLabel lblFolder = new JLabel();

  private JPanel panel4Buttons = new JPanel();

  private JButton btnOk = new JButton();

  private JButton btnCancel = new JButton();

  private JButton btnNewFolder = new JButton();

  private JPanel panel4PackNorth = new JPanel();

  private JPanel panel4PackEast = new JPanel();

  private JPanel panel4PackWest = new JPanel();

  
  private FsRemoteCommandListener remoteBrowserCommandListener;
  
  private FsRemoteBrowser fsRemoteBrowser=this;
  
  private FsClient fsClient;
  
  private FileSyncClientMDI mdiParent;
 
  /*
   * Construct FsRemoteBrowser object.
   * @param parent the frame which will act as parent of the dialog box
   * @param title thtle of the dialog box
   * @param modal indicates if the dialog box is modal
   */
  
   public FsRemoteBrowser(FileSyncClientMDI parent,FsFileSync fsFileSync,FsClient fsClient, String title, boolean modal) {
       super(parent, title, modal);
       try {
         jbInit();                                                                    //Testing Code 4 Remote Browser.....
         this.logger = Logger.getLogger("ClientLogger");
         this.fsClient=fsClient;
         this.fsFileSync = fsFileSync;
         this.mdiParent=parent;
         remoteBrowserCommandListener=new RemoteBrowserCommandListener();
         fsClient.addRemoteBrowserCommandListener(remoteBrowserCommandListener); 
         
         int remoteOperationConstant=FsRemoteOperationConstants.REMOTE_BROWSER;
         remoteWillTreeExpansionListener = new RemoteWillTreeExpansionListener(fsClient,treeRemoteTreeView,remoteOperationConstant);
         this.generalUtil = new GeneralUtil();
         generalUtil.centerForm(parent, this);
       } catch (Exception e) {
         logger.debug(generalUtil.getStackTrace(e));
       }
   }    
  /**
   * To add the controls over the dialog and customize it.
   * @throws Exception if operation fails.
	 */
  private void jbInit() throws Exception {
    treeRemoteTreeView.setRootVisible(false);
    treeRemoteTreeView.addTreeSelectionListener(new TreeSelectionListener() {
                                                  public void valueChanged(TreeSelectionEvent e) {
                                                    treeRemoteTreeView_valueChanged(e);
                                                  }
                                                });

    scrollPane4Treeview.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
    scrollPane4Treeview.getViewport().add(treeRemoteTreeView, null);
    
    lblFolder.setText("Folder");
    lblFolder.setPreferredSize(new Dimension(50, 15));
    
    txtFolderPath.setEditable(false);
    txtFolderPath.setPreferredSize(new Dimension(249, 40));
    txtFolderPath.setBorder(BorderFactory.createEtchedBorder());
    
    
    panel4FolderPath.setPreferredSize(new Dimension(310, 40));
    panel4FolderPath.setLayout(new BorderLayout());
    panel4FolderPath.setBounds(new Rectangle(0, 0, 310, 40));
    panel4FolderPath.add(lblFolder, BorderLayout.WEST);
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
    btnOk.setBorder(BorderFactory.createEtchedBorder());
    btnOk.setPreferredSize(new Dimension(75, 25));
    btnOk.addActionListener(new ActionListener() {
                              public void actionPerformed(ActionEvent e) {
                                butOk_actionPerformed();
                              }
                            });
    
    btnCancel.setText("Cancel");
    btnCancel.setBorder(BorderFactory.createEtchedBorder());
    btnCancel.setPreferredSize(new Dimension(75, 25));
    btnCancel.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                    butCancel_actionPerformed();
                                  }
                                });

    panel4Buttons.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    panel4Buttons.add(btnNewFolder, null);
    panel4Buttons.add(btnOk, null);
    panel4Buttons.add(btnCancel, null);
    
    panel4Widgets.setLayout(new BorderLayout());
    panel4Widgets.setBounds(new Rectangle(0, 397, 309, 35));
    panel4Widgets.setPreferredSize(new Dimension(1, 65));
    panel4Widgets.add(panel4FolderPath, BorderLayout.CENTER);
    panel4Widgets.add(panel4PackNorth, BorderLayout.NORTH);
    panel4Widgets.add(panel4PackEast, BorderLayout.EAST);
    panel4Widgets.add(panel4PackWest, BorderLayout.WEST);
    panel4Widgets.add(panel4Buttons, BorderLayout.SOUTH);
    
    this.setTitle("Remote Browser");
    this.setSize(new Dimension(331, 430));
    this.getContentPane().setLayout(new BorderLayout());
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setBounds(new Rectangle(10, 10, 319, 430));
    this.addWindowListener(new java.awt.event.WindowAdapter() {
                             public void windowClosed(WindowEvent e) {
                               this_windowClosed();
                             }
                           });
    this.getContentPane().add(scrollPane4Treeview, BorderLayout.CENTER);
    this.getContentPane().add(panel4Widgets, BorderLayout.SOUTH);
  }



  private void butCreateNewFolder_actionPerformed() {
    String folderName = JOptionPane.showInputDialog(this, "Folder");
    if (folderName != null && !folderName.trim().equals("")) {
      DefaultMutableTreeNode selectedTreeNode =
        (DefaultMutableTreeNode)treeRemoteTreeView.getLastSelectedPathComponent();
      String selectedTreeNodePath = ((FsFolderHolder)selectedTreeNode.getUserObject()).getPath();
      fsClient.makedir(selectedTreeNodePath, folderName, FsRemoteOperationConstants.REMOTE_BROWSER);
      fsClient.getFlatFolderTree(selectedTreeNodePath, null, FsRemoteOperationConstants.REMOTE_BROWSER);
    }
  }

  private void butOk_actionPerformed() {
    fsFileSync.getTxtRemoteFolderPath().setText(fsFolderHolder.getPath());
    fsFileSync.setRemoteFolderName(fsFolderHolder.getName());
    this.dispose();
  }

  private void butCancel_actionPerformed() {
    this.dispose();
  }

  private void this_windowClosed() {
    fsClient.removeRemoteBrowserCommandListener(remoteBrowserCommandListener);
  }

  private void treeRemoteTreeView_valueChanged(TreeSelectionEvent e) {
    String nodePath = "";
    JTree tree = (JTree)e.getSource();
    TreePath treePath = tree.getSelectionPath();
    if (treePath == null) {
      return;
    }
    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
    logger.debug("currentNode.toString() : " + currentNode.toString());
    int currentNodeLevel = currentNode.getLevel();
    logger.debug("Level " + currentNodeLevel);
    if (currentNodeLevel != 0) {
      fsFolderHolder = (FsFolderHolder)currentNode.getUserObject();
      nodePath = fsFolderHolder.getPath();
      txtFolderPath.setText(nodePath);
    }
  }

  private class RemoteBrowserCommandListener implements FsRemoteCommandListener{
   // private JTextField tfStatus = new JTextField();
    public void propertyChange(PropertyChangeEvent evt){
      String homeFolder;
      try{
        int propertyName=Integer.valueOf(evt.getPropertyName());
        FsResponse fsResponse;
        FsExceptionHolder fsExceptionHolder;
        fsResponse = (FsResponse)evt.getNewValue();
        
        logger.debug("propertyName : "+ propertyName);
        switch (propertyName){
          case GET_ROOT_FOLDERS:
            {
              String selectTreeNodePath = null;
              Object[] treeNodes = fsResponse.getDatas();
              String currentTreeNodePath = fsResponse.getCurrentTreeNodePath();
              logger.debug("currentTreeNodePath : " + currentTreeNodePath);
  
              selectTreeNodePath = fsResponse.getSelectTreeNodePath();
              logger.debug("selectTreeNodePath : " + selectTreeNodePath);
              remoteWillTreeExpansionListener.insertTreeNodesRemoteTreeView(treeNodes, currentTreeNodePath, selectTreeNodePath);
              fsClient.getHomeFolder(FsRemoteOperationConstants.REMOTE_BROWSER);
            }
            break;
          case GET_HOME_FOLDER:
            homeFolder = (String)fsResponse.getData();
            logger.debug("homeFolder : " + homeFolder);
            fsClient.getFolderRoot(homeFolder, FsRemoteOperationConstants.REMOTE_BROWSER);
            break;
          case GET_FOLDER_ROOT:
            String homeFolderRoot = (String)fsResponse.getData();
            logger.debug("homeFolderRoot : " + homeFolderRoot);
            homeFolder = (String)fsResponse.getData1();
            logger.debug("homeFolder : " + homeFolder);
            fsClient.getFlatFolderTree(homeFolderRoot, homeFolder, FsRemoteOperationConstants.REMOTE_BROWSER);
            break;
          case GET_FLAT_FOLDER_TREE:
            {
              String selectTreeNodePath = null;
              Object[] treeNodes = fsResponse.getDatas();
              String currentTreeNodePath = fsResponse.getCurrentTreeNodePath();
              logger.debug("currentTreeNodePath : " + currentTreeNodePath);

              selectTreeNodePath = fsResponse.getSelectTreeNodePath();
              logger.debug("selectTreeNodePath : " + selectTreeNodePath);
              remoteWillTreeExpansionListener.insertTreeNodesRemoteTreeView(treeNodes, currentTreeNodePath, selectTreeNodePath);
            }
            break;
          case ERROR_MESSAGE:
            fsExceptionHolder = fsResponse.getFsExceptionHolder();
            if(fsExceptionHolder.getErrorCode() == 30033 || fsExceptionHolder.getErrorCode() == 30041
              || fsExceptionHolder.getErrorCode() == 30030 || fsExceptionHolder.getErrorCode() == 30002 
              || fsExceptionHolder.getErrorCode() == 30041){
             // tfStatus.setText("Access denied");
              mdiParent.fsStatusBar.setLblSetMessage("");
              mdiParent.fsStatusBar.setLblSetMessage("Access denied");
              JOptionPane.showMessageDialog(fsRemoteBrowser,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }else if(fsExceptionHolder.getErrorCode() == 68005){
              String errorMsg = fsExceptionHolder.getErrorMessage();
              logger.debug("Error Message :" + errorMsg);
              JOptionPane.showMessageDialog(fsRemoteBrowser, "A Folder/file with the name you specified already exists specify a different filename.","Error Renaming File/Folder",JOptionPane.ERROR_MESSAGE);
            }else{
              JOptionPane.showMessageDialog(fsRemoteBrowser,fsExceptionHolder.getErrorMessage(),"Error",JOptionPane.ERROR_MESSAGE);
              //tfStatus.setText("" + fsExceptionHolder);
               mdiParent.fsStatusBar.setLblSetMessage("");
               mdiParent.fsStatusBar.setLblSetMessage(fsExceptionHolder.getErrorMessage());
            }
            logger.error(fsExceptionHolder);
            break;
          case FETAL_ERROR:
           // mdiParent.getExplorer().getFsRemoteView().setWaitCursorForRemoteBrowser();
            logger.error("Fetal Error");
           // tfStatus.setText("Fatal Error");
            mdiParent.fsStatusBar.setLblSetMessage("");
            mdiParent.fsStatusBar.setLblSetMessage("Fatal Error");
            break;
        }
      }catch(Exception ex){
      logger.error(dbsentry.filesync.clientgui.utility.GeneralUtil.getStackTrace(ex));
     // tfStatus.setText( ex.getMessage());
      mdiParent.fsStatusBar.setLblSetMessage("");
      mdiParent.fsStatusBar.setLblSetMessage(ex.getMessage());
     }
      
    }
  }

}
