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

//import dbsentry.filesync.client.images.FsClientImages;
import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.common.constants.FsUploadConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description
 * @Date of creation: 27-03-2006
 * @Last Modfied by :
 * @Last Modfied Date:
 */
public class OverwriteOptionDialog extends JDialog{
  
  private int overWriteValue;

  private BorderLayout borderLayout1 = new BorderLayout();

  private JPanel jPanel1 = new JPanel();

  private JLabel jLabel9 = new JLabel();

  private JLabel jLabel8 = new JLabel();

  private JLabel lblReplaceFileSize = new JLabel();

  private JLabel lblReplaceFileModifiedDate = new JLabel();

  private JLabel lblExistingFileModifiedDate = new JLabel();

  private JLabel lblExistingFileSize = new JLabel();

  private JLabel jLabel3 = new JLabel();

  private JLabel jLabel2 = new JLabel();

  private JLabel jLabel1 = new JLabel();

  private JTextArea taOverwriteMessage = new JTextArea();

  private JButton butOverwriteYes = new JButton();

  private JButton butOverwriteNo = new JButton();

  private JButton butOverwriteYesAll = new JButton();

  private JButton butOverwriteCancel = new JButton();

  /**
   * @description OverwriteOptionDialog Constructor.
   */
 

  /**
   * @param parent Frame
   * @param title String
   * @param modal Boolean
   * @description To create a dialog box.
   */
  public OverwriteOptionDialog(Frame parent, String title, boolean modal) {
    super(parent, title, modal);
    try {
      jbInit();
    } catch (Exception e) {
      ;

    }
  }
  
  private void jbInit() throws Exception {
    this.setSize(new Dimension(487, 269));
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(null);
    jPanel1.setEnabled(false);
    jLabel9.setText("with this one?");
    jLabel9.setBounds(new Rectangle(75, 125, 170, 15));
    jLabel8.setBounds(new Rectangle(100, 150, 30, 40));
    jLabel8.setIcon(FsImage.imageUnknownFile);
    lblReplaceFileSize.setText("26.0 KB");
    lblReplaceFileSize.setBounds(new Rectangle(135, 150, 285, 20));
    lblReplaceFileModifiedDate.setText("modified: Saturday, November 06, 2004, 12:49:30 PM");
    lblReplaceFileModifiedDate.setBounds(new Rectangle(135, 170, 340, 20));
    lblExistingFileModifiedDate.setText("modified: Saturday, November 06, 2004, 12:49:30 PM");
    lblExistingFileModifiedDate.setBounds(new Rectangle(135, 95, 340, 20));
    lblExistingFileSize.setText("26.0 KB");
    lblExistingFileSize.setBounds(new Rectangle(135, 75, 285, 20));
    jLabel3.setBounds(new Rectangle(100, 75, 30, 40));
    jLabel3.setIcon(FsImage.imageUnknownFile);
    jLabel2.setText("Would you like to replace the existing file");
    jLabel2.setBounds(new Rectangle(75, 50, 365, 20));
    jLabel1.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    jLabel1.setBounds(new Rectangle(15, 10, 35, 40));
    jLabel1.setIcon(FsImage.imageFileReplace);
    taOverwriteMessage.setText("This folder already contains a readonly file named ");
    taOverwriteMessage.setBounds(new Rectangle(75, 10, 370, 30));
    taOverwriteMessage.setWrapStyleWord(true);
    taOverwriteMessage.setLineWrap(true);
    taOverwriteMessage.setEditable(false);
    taOverwriteMessage.setOpaque(false);
    taOverwriteMessage.setFont(new Font("Dialog", 1, 12));
    butOverwriteYes.setText("Yes");
    butOverwriteYes.setBounds(new Rectangle(150, 205, 75, 25));
    butOverwriteYes.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                          butOverwriteYes_actionPerformed(e);
                                        }
                                      }
    );
    butOverwriteNo.setText("No");
    butOverwriteNo.setBounds(new Rectangle(310, 205, 75, 25));
    butOverwriteNo.addActionListener(new ActionListener() {
                                       public void actionPerformed(ActionEvent e) {
                                         butOverwriteNo_actionPerformed(e);
                                       }
                                     }
    );
    butOverwriteYesAll.setText("YesAll");
    butOverwriteYesAll.setBounds(new Rectangle(230, 205, 75, 25));
    butOverwriteYesAll.addActionListener(new ActionListener() {
                                           public void actionPerformed(ActionEvent e) {
                                             butOverwriteYesAll_actionPerformed(e);
                                           }
                                         }
    );
    butOverwriteCancel.setText("Cancel");
    butOverwriteCancel.setBounds(new Rectangle(390, 205, 75, 25));
    butOverwriteCancel.addActionListener(new ActionListener() {
                                           public void actionPerformed(ActionEvent e) {
                                             butOverwriteCancel_actionPerformed(e);
                                           }
                                         }
    );
    jPanel1.add(butOverwriteCancel, null);
    jPanel1.add(butOverwriteYesAll, null);
    jPanel1.add(butOverwriteNo, null);
    jPanel1.add(butOverwriteYes, null);
    jPanel1.add(taOverwriteMessage, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(jLabel3, null);
    jPanel1.add(lblExistingFileSize, null);
    jPanel1.add(lblExistingFileModifiedDate, null);
    jPanel1.add(lblReplaceFileModifiedDate, null);
    jPanel1.add(lblReplaceFileSize, null);
    jPanel1.add(jLabel8, null);
    jPanel1.add(jLabel9, null);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
  }
  
  private void butOverwriteYes_actionPerformed(ActionEvent e) {
    overWriteValue = FsUploadConstants.OVERWRITE_YES;
    setVisible(false);
  }

  /**
   * Returns the overWriteValue.
   * @return Returns the overWriteValue.
   */
  public int getOverWriteValue() {
    return overWriteValue;
  }

  /**
   * Sets the folder/file overWriteValue to OVERWRITE_YESALL.
   * @param  ae ActionEvent
   */
  private void butOverwriteYesAll_actionPerformed(ActionEvent ae) {
    overWriteValue = FsUploadConstants.OVERWRITE_YES_TO_ALL;
    setVisible(false);
  }

  /**
   * sets the folder/file overWriteValue to OVERWRITE_NO.
   * @param  ae ActionEvent
   */
  private void butOverwriteNo_actionPerformed(ActionEvent ae) {
    overWriteValue = FsUploadConstants.OVERWRITE_NO;
    setVisible(false);
  }

  /**
   * Sets the folder/file overWriteValue to OVERWRITE_CANCEL.
   * @param  ae ActionEvent
   */
  private void butOverwriteCancel_actionPerformed(ActionEvent ae) {
    overWriteValue = FsUploadConstants.OVERWRITE_CANCEL;
    setVisible(false);
  }

  /**
   * taOverwriteMessage.
   * @return JTextArea
   */
  public void setTaOverwriteMessage(String overwriteMessage) {
    taOverwriteMessage.setText(overwriteMessage);
  }


  /**
   * getter for lblReplaceFileSize.
   * @return returns the label lblReplaceFileSize
   */
  public void setLblReplaceFileSize(String replaceFileSize) {
    lblReplaceFileSize.setText(replaceFileSize);
  }


  /**
   * getter for lblReplaceFileModifiedDate.
   * @return returns the label lblReplaceFileModifiedDate
   */
  public void setLblReplaceFileModifiedDate(String replaceFileModifiedDate) {
    lblReplaceFileModifiedDate.setText(replaceFileModifiedDate);
  }


  /**
   * getter for lblExistingFileModifiedDate.
   * @return returns the label getLblExistingFileModifiedDate
   */
  public void setLblExistingFileModifiedDate(String existingFileModifiedDate) {
    lblExistingFileModifiedDate.setText(existingFileModifiedDate);
  }


  /**
   * getter for lblExistingFileSize.
   * @return returns the label lblExistingFileSize
   */
  public void setLblExistingFileSize(String existingFileSize) {
    lblExistingFileSize.setText(existingFileSize);
    ;
  }

  /**
   * Returns a string representation of the FileOverWriteOption object.
   * @return a string representation of the FileOverWriteOption object.
   */
  public String toString() {
    return this.toString();
  }
  
}
