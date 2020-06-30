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
 * $Id: FsFolderPropertyPage.java,v 1.27 2006/04/14 17:38:34 skumar Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.ClientUtil;
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.FsFolderPropertyPageRemote;
import dbsentry.filesync.common.FsMessage;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;

import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;


/**
 *	Purpose: To provide an interface to view and manipulate the properties 
 *  of selected folders.
 *  @author              Deepali Chitkulwar
 *  @version             1.0
 * 	Date of creation:    13-04-2005
 * 	Last Modfied by :    Deepali Chitkulwar
 * 	Last Modfied Date:   08-07-2005 
 */
public class FsFolderPropertyPage extends JDialog {
  private JTabbedPane jTabbedPane1 = new JTabbedPane();

  private JPanel jpGeneral = new JPanel();

  private JTextField jtfFolderName = new JTextField();

  private JSeparator jSeparator1 = new JSeparator();

  private JLabel jLabel2 = new JLabel();

  private JLabel jLabel3 = new JLabel();

  private JLabel jLabel4 = new JLabel();

  private JSeparator jSeparator2 = new JSeparator();

  private JLabel jLabel6 = new JLabel();

  private JLabel jLabel7 = new JLabel();

  private JSeparator jSeparator3 = new JSeparator();

  private JLabel jLabel8 = new JLabel();

  private JButton btnOk = new JButton();

  private JButton btnCancel = new JButton();

  private JLabel jlblFolderIcon = new JLabel();

  private JLabel jlblType = new JLabel();

  private JLabel jlblLocation = new JLabel();

  private JLabel jlblSize = new JLabel();

  private JLabel jlblContains = new JLabel();

  private JLabel jlblCreated = new JLabel();

  private JLabel jlblPermissions = new JLabel();

  private PropertyChangeSupport propertyChangeSupport;

  /**
   * a string which indicates whether this property page is for local or remote folder.
   */
  private String propertyPageFor;

  private String oldFolderName;

  private Logger logger;

  private ClientUtil clientUtil;
  
  private GeneralUtil generalUtil;

  /**
   * To create a dialog box.
   * @param parent the frame that will act as parent of this dialog box
   * @param title title of this dialog box
   * @param modal boolean value which indicates if this dialog is modal
   */
  public FsFolderPropertyPage(Frame parent, String title, boolean modal) {
    super(parent, title, modal);
    this.logger = Logger.getLogger("ClientLogger");
    clientUtil = new ClientUtil(logger);
    generalUtil = new GeneralUtil();
    try {
      jbInit();
    } catch (Exception e) {
      logger.error(clientUtil.getStackTrace(e));
    }
    generalUtil.centerForm((JFrame)parent, this);
  }

  /**
   * To add the controls over the dialog and customize it
   * @throws Exception if operation fails.
	 */
  private

  void jbInit() throws Exception {
    this.getContentPane().setLayout(null);
    this.setSize(new Dimension(334, 375));
    this.setBounds(new Rectangle(10, 10, 335, 375));
    jTabbedPane1.setBounds(new Rectangle(10, 10, 300, 295));
    jpGeneral.setLayout(null);
    jtfFolderName.setText("File Name");
    jtfFolderName.setBounds(new Rectangle(90, 15, 195, 25));
    jSeparator1.setBounds(new Rectangle(5, 50, 285, 5));
    jlblFolderIcon.setBounds(new Rectangle(10, 15, 45, 30));
    jlblFolderIcon.setIcon(FsImage.imgFolderClosed);
    jLabel2.setText("Type:");
    jLabel2.setBounds(new Rectangle(10, 65, 80, 15));
    jLabel2.setSize(new Dimension(85, 13));
    jLabel2.setPreferredSize(new Dimension(85, 13));
    jLabel2.setMaximumSize(new Dimension(85, 13));
    jLabel2.setMinimumSize(new Dimension(85, 13));
    jLabel3.setText("Location:");
    jLabel3.setBounds(new Rectangle(10, 90, 75, 15));
    jLabel3.setSize(new Dimension(85, 13));
    jLabel3.setPreferredSize(new Dimension(85, 13));
    jLabel3.setMaximumSize(new Dimension(85, 13));
    jLabel3.setMinimumSize(new Dimension(85, 13));
    jLabel4.setText("Size:");
    jLabel4.setBounds(new Rectangle(10, 115, 75, 15));
    jLabel4.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel4.setHorizontalTextPosition(SwingConstants.LEFT);
    jLabel4.setPreferredSize(new Dimension(85, 13));
    jLabel4.setMinimumSize(new Dimension(85, 13));
    jLabel4.setMaximumSize(new Dimension(85, 13));
    jLabel4.setSize(new Dimension(85, 13));
    jSeparator2.setBounds(new Rectangle(10, 170, 275, 5));
    jLabel6.setText("Contains:");
    jLabel6.setBounds(new Rectangle(10, 140, 85, 13));
    jLabel6.setSize(new Dimension(85, 13));
    jLabel6.setPreferredSize(new Dimension(85, 13));
    jLabel6.setMinimumSize(new Dimension(85, 13));
    jLabel6.setMaximumSize(new Dimension(85, 13));
    jLabel7.setText("Created:");
    jLabel7.setBounds(new Rectangle(10, 190, 85, 13));
    jLabel7.setSize(new Dimension(85, 13));
    jLabel7.setMinimumSize(new Dimension(85, 13));
    jLabel7.setMaximumSize(new Dimension(85, 13));
    jLabel7.setPreferredSize(new Dimension(85, 13));
    jSeparator3.setBounds(new Rectangle(10, 220, 280, 2));
    jLabel8.setText("Attributes:");
    jLabel8.setBounds(new Rectangle(10, 235, 85, 13));
    jLabel8.setSize(new Dimension(85, 13));
    jLabel8.setPreferredSize(new Dimension(85, 13));
    jLabel8.setMinimumSize(new Dimension(85, 13));
    jLabel8.setMaximumSize(new Dimension(85, 13));
    btnOk.setText("Ok");
    btnOk.setBounds(new Rectangle(155, 310, 75, 25));
    btnOk.addActionListener(new ActionListener() {
                              public void actionPerformed(ActionEvent e) {
                                btnOk_actionPerformed(e);
                              }
                            }
    );
    btnCancel.setText("Cancel");
    btnCancel.setBounds(new Rectangle(235, 310, 75, 25));
    btnCancel.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                    btnCancel_actionPerformed(e);
                                  }
                                }
    );
    jlblType.setBounds(new Rectangle(90, 65, 200, 15));
    jlblType.setText("Folder File");
    jlblLocation.setBounds(new Rectangle(90, 90, 200, 15));
    jlblLocation.setText("/home/deepali");
    jlblSize.setBounds(new Rectangle(90, 115, 200, 15));
    jlblSize.setText("0 bytes");
    jlblContains.setText("0 Files, 0 Folders");
    jlblContains.setBounds(new Rectangle(90, 140, 200, 15));
    jlblCreated.setText(" Friday ,March 11,2005");
    jlblCreated.setBounds(new Rectangle(90, 190, 200, 15));
    jlblPermissions.setBounds(new Rectangle(95, 235, 175, 10));
    jpGeneral.add(jlblPermissions, null);
    jpGeneral.add(jlblCreated, null);
    jpGeneral.add(jlblContains, null);
    jpGeneral.add(jlblSize, null);
    jpGeneral.add(jlblLocation, null);
    jpGeneral.add(jlblType, null);
    jpGeneral.add(jLabel8, null);
    jpGeneral.add(jSeparator3, null);
    jpGeneral.add(jLabel7, null);
    jpGeneral.add(jLabel6, null);
    jpGeneral.add(jSeparator2, null);
    jpGeneral.add(jLabel4, null);
    jpGeneral.add(jLabel3, null);
    jpGeneral.add(jLabel2, null);
    jpGeneral.add(jlblFolderIcon, null);
    jpGeneral.add(jSeparator1, null);
    jpGeneral.add(jtfFolderName, null);
    this.getContentPane().add(btnCancel, null);
    this.getContentPane().add(btnOk, null);
    jTabbedPane1.addTab("General", jpGeneral);
    this.getContentPane().add(jTabbedPane1, null);
  }

  /**
   * setter for the folder type.
   * @param type type of this folder.
   */
  public void setType(String type) {
    jlblType.setText(type);
  }

  /**
   * setter for the folder location.
   * @param location absolute path.
   */
  public void setLocation(String location) {
    jlblLocation.setText(location);
  }

  /**
   * setter for the folder size.
   * @param size total size of folder.
   */
  public void setSize(String size) {
    jlblSize.setText(size);
  }

  /**
   * setter for the jlblContains text property which holds the number of files and folder.
   * @param fileCount no. of files.
   * @param folderCount no. of folders.   
   */
  public void setFileFolderCount(int fileCount, int folderCount) {
    jlblContains.setText(fileCount + "Files ," + folderCount + "Folders");
  }

  /**
   * setter for the creation date of the folder.
   * @param date Date object containing creation date.
   */
  public void setCreationDate(Date date) {
    jlblCreated.setText(date.toString());
  }

  /**
   * setter for the folder name.
   * @param name folder name.
   */
  public void setFolderName(String name) {
    jtfFolderName.setText(name);
    ;
  }

  /**
   * To add a listener which will listen when a propertyChange event is fired.
   * @param propertyChangeListener PropertyChangeListener object
   */
  public void addPropertyChangeSupport(PropertyChangeListener propertyChangeListener) {
    propertyChangeSupport = new PropertyChangeSupport(this);
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }

  private void btnOk_actionPerformed(ActionEvent e) {
    if (propertyPageFor.equals(FsMessage.FOR_LOCAL_FILESYSTEM)) {
      String refreshFolderPath = jlblLocation.getText();
      String oldFolderPath = jlblLocation.getText() + File.separator + oldFolderName;

      String newFolderName = jtfFolderName.getText();
      if (!oldFolderName.equals(newFolderName)) {
        File newFolderFile = new File(jlblLocation.getText() + File.separator + newFolderName);
        File oldFolderFile = new File(oldFolderPath);
        oldFolderFile.renameTo(newFolderFile);
        propertyChangeSupport.firePropertyChange("refreshFolderPath", null, refreshFolderPath);
      }
    } else {
      FsFolderPropertyPageRemote fsFolderPropertyPageRemote = new FsFolderPropertyPageRemote();
      fsFolderPropertyPageRemote.setOldFolderName(oldFolderName);
      fsFolderPropertyPageRemote.setLocation(jlblLocation.getText());
      String newFolderName = jtfFolderName.getText();
      fsFolderPropertyPageRemote.setName(newFolderName);
      propertyChangeSupport
      .firePropertyChange(new PropertyChangeEvent(propertyPageFor, "fsFolderPropertyPageRemote", null,
                                                                       fsFolderPropertyPageRemote));
    }
    this.dispose();
  }

  private void btnCancel_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  /**
   * Purpose   : To set oldFolderFile which will be useful to check whether 
   * the filename has changed by user.
   * @param oldFolderName folder old attribute
   */
  public void setOldAttributes(String oldFolderName) {
    this.oldFolderName = oldFolderName;
  }

  /**
   * setter for the folder permissions.
   * @param permissions String containing folder permissions.
   */
  public void setPermissions(String permissions) {
    jlblPermissions.setText(permissions);
  }

  public void setPropertyPageFor(String propertyPageFor) {
    this.propertyPageFor = propertyPageFor;
  }

  public String getPropertyPageFor() {
    return propertyPageFor;
  }
}
