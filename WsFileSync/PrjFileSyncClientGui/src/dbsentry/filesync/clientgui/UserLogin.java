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
  * $Id: UserLogin.java,v 1.48 2006/06/30 14:17:51 sudheer Exp $
  *****************************************************************************
  */
 package dbsentry.filesync.clientgui;

import dbsentry.filesync.clientgui.utility.GeneralUtil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;


/**
  *  Purpose: To create a login dialog.
  *  @author              Jeetendra Prasad
  *  @version             1.0
  *  Date of creation:    14-04-2005
  *  Last Modfied by :    Saurabh Gupta
  *  Last Modfied Date:   13-03-2006
  */
  public class UserLogin extends JDialog {
    private Logger logger;
    
    private GeneralUtil generalUtil;
    
    private BorderLayout borderLayout1 = new BorderLayout();
    
    private JPanel panel4UserLogin = new JPanel();
    
    
    private JButton btnCancel = new JButton();
    
    private JButton btnConnect = new JButton();
    
    private JLabel lblUserPassword = new JLabel();
    
    private JPasswordField txtUserPassword = new JPasswordField();
    
    private JLabel lblUserId = new JLabel();
    
    private JTextField txtUserId = new JTextField();
    
    private boolean canceled=false;

   
   /**
    * Construct UserLogin object.
    * @param logger
    * @param parent of this dialog box.
    */
   public UserLogin(Logger logger,JDialog parent) {
     super(parent, "Login", true);
     try {
       jbInit();
       this.logger = logger;
       this.generalUtil = new GeneralUtil();
       this.generalUtil.centerForm(parent, this);
     } catch (Exception e) {
       logger.debug(generalUtil.getStackTrace(e));
     }
   }

   /**
    * Purpose : To add the controls over the dialog and customize it
    * @throws  Exception - if operation fails.
    */
  private void jbInit() throws Exception {
    this.setSize(new Dimension(335, 185));
    this.setResizable(true);
    this.getContentPane().setLayout(borderLayout1);
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    lblUserId.setFont(new FsFont());
    lblUserId.setText("User Id");
    lblUserId.setBounds(new Rectangle(25, 40, 65, 25));
    lblUserId.setHorizontalAlignment(SwingConstants.RIGHT);
    lblUserId.setHorizontalTextPosition(SwingConstants.LEFT);
    lblUserId.setFocusable(false);
    
    txtUserId.setBounds(new Rectangle(95, 40, 185, 25));
    txtUserId.setText("");
    txtUserId.setBorder(BorderFactory.createEtchedBorder());
    txtUserId.addKeyListener(new KeyAdapter() {
    
                              public void keyReleased(KeyEvent e) {
                                txtUserId_keyReleased(e);
                              }
                            });
                            
    lblUserPassword.setFont(new FsFont());
    lblUserPassword.setText("Password");
    lblUserPassword.setBounds(new Rectangle(25, 70, 65, 25));
    lblUserPassword.setFocusable(false);
    lblUserPassword.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtUserPassword.setBounds(new Rectangle(95, 70, 185, 25));
    txtUserPassword.setText("");
    txtUserPassword.setBorder(BorderFactory.createEtchedBorder());
    txtUserPassword.addKeyListener(new KeyAdapter() {
                                   public void keyReleased(KeyEvent e) {
                                     txtUserPassword_keyReleased(e);
                                   }
                                 });

    btnConnect.setFont(new FsFont());
    btnConnect.setText("Connect");
    btnConnect.setBounds(new Rectangle(95, 105, 85, 25));
    btnConnect.setBorder(BorderFactory.createEtchedBorder());
    btnConnect.addKeyListener(new KeyAdapter() {
                         public void keyReleased(KeyEvent e) {
                           butOk_keyReleased(e);
                         }
                       });           
    btnConnect.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                              butOk_actionPerformed();
                            }
                          });
                          
    btnCancel.setFont(new FsFont());
    btnCancel.setText("Cancel");
    btnCancel.setBorder(BorderFactory.createEtchedBorder());    
    btnCancel.setBounds(new Rectangle(185, 105, 85, 25));
    btnCancel.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) {
                                 butCancel_actionPerformed();
                               }
                             });
    
    panel4UserLogin.setLayout(null);
    panel4UserLogin.setSize(new Dimension(264, 144));
    panel4UserLogin.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Enter User Id And Password",TitledBorder.CENTER,TitledBorder.BELOW_TOP));
    panel4UserLogin.add(lblUserId, null);
    panel4UserLogin.add(txtUserId, null);
    panel4UserLogin.add(lblUserPassword, null);
    panel4UserLogin.add(txtUserPassword, null);
    panel4UserLogin.add(btnConnect, null);
    panel4UserLogin.add(btnCancel, null);
    
    this.getContentPane().add(panel4UserLogin, BorderLayout.CENTER);
   }

   
   private void butOk_actionPerformed() {
     canceled=false;
     this.setVisible(false);
   }
   
   private void butCancel_actionPerformed() {
     canceled=true;
     this.setVisible(false);
   }


   private void txtUserId_keyReleased(KeyEvent e) {
     if (e.getKeyCode() == KeyEvent.VK_ENTER) {
       btnConnect.doClick();
     }
   }

   private void txtUserPassword_keyReleased(KeyEvent e) {
     if (e.getKeyCode() == KeyEvent.VK_ENTER) {
       btnConnect.doClick();
     }
   }

   private void butOk_keyReleased(KeyEvent e) {
     if (e.getKeyCode() == KeyEvent.VK_ENTER) {
       btnConnect.doClick();
     }
   }

   private void cbRememberUIdPwd_keyReleased(KeyEvent e) {
     if (e.getKeyCode() == KeyEvent.VK_ENTER) {
       btnConnect.doClick();
     }
   }

   /**
    * getter of user password.
    * @return password 
    */
   public String getPassword() {
     return String.valueOf(txtUserPassword.getPassword());
   }


   /**
    * getter of user id.
    * @return userid 
    */
   public String getUserId() {
     return txtUserId.getText();
   }

  public boolean isCanceled() {
    return canceled;
  }
  
  public void reset(){
    canceled=false;
    txtUserId.setText("");
    txtUserPassword.setText("");    
    txtUserId.requestFocus();
  }
}
