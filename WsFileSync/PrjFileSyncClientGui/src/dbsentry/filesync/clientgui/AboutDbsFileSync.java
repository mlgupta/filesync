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
 * $Id: AboutDbsFileSync.java,v 1.31 2006/09/11 11:20:15 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;


import dbsentry.filesync.clientgui.images.FsImage;
import dbsentry.filesync.clientgui.utility.GeneralUtil;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;


/**
 *	To create a about dialog.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    14-04-2005
 * 	Last Modfied by :    Saurabh Kumar
 * 	Last Modfied Date:   18-05-2006
 */
public class AboutDbsFileSync extends JDialog {
  private JLabel lblFileSyncIcon = new JLabel();

  private JLabel jLabel2 = new JLabel();

  private JLabel jLabel4 = new JLabel();

  private JButton butOk = new JButton();

  private JSeparator jSeparator1 = new JSeparator();

  private JLabel jLabel1 = new JLabel();

  private JLabel jLabel5 = new JLabel();

  private JLabel jLabel7 = new JLabel();
  
  private GeneralUtil generalUtil;

  private JSeparator jSeparator2 = new JSeparator();

  private JTextPane jTextPane1 = new JTextPane();

  private JLabel lblDbsLogo = new JLabel();

  private JLabel jLabel3 = new JLabel();

  private JLabel jLabel6 = new JLabel();

  private JLabel jLabel11 = new JLabel();

  private JLabel jLabel12 = new JLabel();

  private JLabel jLabel13 = new JLabel();

  /**
   * A constructor for AboutDbsFileSync.
   */
  public AboutDbsFileSync(JFrame parent) {
    this(parent , "", true);
  }

  /**
   * A constructor for AboutDbsFileSync.
   * @param parent the frame which will act as parent of this about dialog box
   * @param title title for this dialog box
   * @param modal boolean value indicating if this dialog is modal
   */
  public AboutDbsFileSync(JFrame parent, String title, boolean modal) {
    super(parent, title, modal);
    try {
      jbInit();
      generalUtil.centerForm(parent, this);
    } catch (Exception e) {
      ;
    }
  }

  private void jbInit() throws Exception {
    //this.setFont(new Font("SansSerif",Font.PLAIN,12));
    this.setSize(new Dimension(538, 393));
    this.getContentPane().setLayout(null);
    this.setTitle("About DBSentry FileSync");
    this.setResizable(false);
    lblFileSyncIcon.setBounds(new Rectangle(10, 45, 105, 215));
    lblFileSyncIcon.setIcon(FsImage.about_screen_composed);
    jLabel2.setFont(new FsFont());
    jLabel2.setText("Version 1.3.0");
    jLabel2.setBounds(new Rectangle(375, 10, 85, 25));
    jLabel4.setFont(new FsFont());
    jLabel4.setText("Copyright (c) 2004-2005 DBSentry Corp.");
    jLabel4.setBounds(new Rectangle(125, 310, 255, 15));
    butOk.setFont(new FsFont());
    butOk.setText("Ok");
    butOk.setBorder(BorderFactory.createEtchedBorder());
    butOk.setBounds(new Rectangle(405, 315, 80, 25));
    butOk.addActionListener(new ActionListener() {
                              public void actionPerformed(ActionEvent e) {
                                butOk_actionPerformed();
                              }
                            }
    );
    jSeparator1.setBounds(new Rectangle(9, 300, 515, 5));
    jLabel1.setFont(new FsFont());
    jLabel1.setText("Developer Team :");
    jLabel1.setBounds(new Rectangle(140, 195, 115, 15));
    jLabel5.setFont(new FsFont());
    jLabel5.setText("Sudheer Pujar");
    jLabel5.setBounds(new Rectangle(140, 210, 120, 15));
    jLabel7.setFont(new FsFont());
    jLabel7.setText("Saurabh Gupta");
    jLabel7.setBounds(new Rectangle(140, 228, 120, 15));
    
   // jSeparator2.setBounds(new Rectangle(140, 165, 315, 2));
    jTextPane1.setFont(new FsFont());
    jTextPane1
    .setText("DBSentry FileSync is a robust cross platform file synchronizer developed " + "using JXTA technology it syncs network files by moving new or updated " +
                       "files and file permissions and keeping mirrored directories up-to-date. " +
                       "It allows to interact with many document storage systems including " +
                       "Unix file system, Samhita Document Management System, and Oracle\'s " + "CMSDK.");
    jTextPane1.setBackground(this.getBackground());
    jTextPane1.setBounds(new Rectangle(140, 45, 320, 125));
    //jTextPane1.setFont(new Font("Dialog", 1, 12));
    jTextPane1.setOpaque(false);
    jTextPane1.setEditable(false);
    lblDbsLogo.setBounds(new Rectangle(15, 310, 90, 35));
    lblDbsLogo.setIcon(FsImage.about_screen_logo);
     jLabel3.setFont(new FsFont());
    jLabel3.setText("Saurabh Kumar");
    jLabel3.setBounds(new Rectangle(140, 247, 120, 15));
    jLabel6.setFont(new FsFont());
    jLabel6.setText("Maneesh Mishra");
    jLabel6.setBounds(new Rectangle(285, 247, 120, 15));
    jLabel11.setFont(new FsFont());
    jLabel11.setText("Brajendu Behera");
    jLabel11.setBounds(new Rectangle(140, 265, 120, 15));
    jLabel12.setFont(new FsFont());
    jLabel12.setText("Jeetendra Prasad");
    jLabel12.setBounds(new Rectangle(285, 210, 120, 15));
    jLabel13.setFont(new FsFont());
    jLabel13.setText("Deepali Chitkulwar");
    jLabel13.setBounds(new Rectangle(285, 228, 120, 15));
    this.getContentPane().add(jLabel13, null);
    this.getContentPane().add(jLabel12, null);
    this.getContentPane().add(jLabel11, null);
    this.getContentPane().add(jLabel6, null);
    this.getContentPane().add(jLabel3, null);
    this.getContentPane().add(lblDbsLogo, null);
    this.getContentPane().add(jTextPane1, null);
    this.getContentPane().add(jSeparator2, null);
    this.getContentPane().add(jLabel1, null);
    this.getContentPane().add(jSeparator1, null);
    this.getContentPane().add(butOk, null);
    this.getContentPane().add(jLabel4, null);
    this.getContentPane().add(jLabel2, null);
    this.getContentPane().add(lblFileSyncIcon, null);
    this.getContentPane().add(jLabel5, null);
    this.getContentPane().add(jLabel7, null);
    
  }

  private void butOk_actionPerformed() {
    this.dispose();
  }
  
}
