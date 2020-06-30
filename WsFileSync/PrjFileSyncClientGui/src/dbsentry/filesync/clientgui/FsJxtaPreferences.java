
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
 * $Id: FsJxtaPreferences.java,v 1.26 2006/04/14 14:01:24 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.clientgui;

import dbsentry.filesync.client.ClientUtil;
import dbsentry.filesync.clientgui.utility.GeneralUtil;
import dbsentry.filesync.common.CommonUtil;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class FsJxtaPreferences extends JDialog {
  private Logger logger;

 // private ClientUtil clientUtil;
  
  private GeneralUtil generalUtil;

  private CommonUtil commonUtil;

  private Document jxtaDocument;

  private Document socketDocument;

  private PropertyChangeSupport propertyChangeSupport;

  private JButton btnCancel = new JButton();

  private JButton btnOk = new JButton();

  private JLabel lblMessage = new JLabel();

  private JTextField tfRendezvousAddr = new JTextField();

  private JLabel jLabel2 = new JLabel();

  private JTextField tfSocketAddress = new JTextField();

  private JLabel jLabel1 = new JLabel();

  private JButton btnAddToList = new JButton();

  private JButton btnRemoveFrmList = new JButton();

  private JScrollPane jScrollPane1 = new JScrollPane();

  private JList rdvRelayAddrList = new JList();

  private DefaultListModel listModel = new DefaultListModel();

  /**
   * 
   * @param modal
   * @param title
   * @param parent
   */
  public FsJxtaPreferences(Frame parent, String title, boolean modal) {
    super(parent, title, modal);
    try {
      jbInit();
      generalUtil = new GeneralUtil();
      this.logger = Logger.getLogger("ClientLogger");
      this.generalUtil.centerForm((JFrame)parent, this);
      this.commonUtil = new CommonUtil(logger);
      readJxtaPreferences();
    } catch (Exception e) {
      logger.error(commonUtil.getStackTrace(e));
    }
  }

  private void jbInit() throws Exception {
    this.setSize(new Dimension(513, 264));
    this.getContentPane().setLayout(null);
    this.setTitle("Jxta Preferences");
    this.setResizable(false);
    btnCancel.setText("Cancel");
    btnCancel.setMargin(new Insets(0, 0, 0, 0));
    btnCancel.addActionListener(new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                    btnCancel_actionPerformed(e);
                                  }
                                }
    );
    btnCancel.setBounds(new Rectangle(435, 200, 60, 25));
    btnOk.setText("Ok");
    btnOk.setMargin(new Insets(0, 0, 0, 0));
    btnOk.setPreferredSize(new Dimension(45, 22));
    btnOk.addActionListener(new ActionListener() {
                              public void actionPerformed(ActionEvent e) {
                                btnOk_actionPerformed(e);
                              }
                            }
    );
    btnOk.setBounds(new Rectangle(365, 200, 60, 25));
    lblMessage.setText("Please restart the application for any changes to take effect");
    lblMessage.setBounds(new Rectangle(10, 205, 350, 15));
    tfRendezvousAddr.setPreferredSize(new Dimension(335, 30));
    tfRendezvousAddr.setMinimumSize(new Dimension(335, 30));
    tfRendezvousAddr.setBounds(new Rectangle(10, 85, 450, 25));
    jLabel2.setText(" Rendezvous & Relay Address :");
    jLabel2.setPreferredSize(new Dimension(129, 25));
    jLabel2.setVerticalAlignment(SwingConstants.BOTTOM);
    jLabel2.setBounds(new Rectangle(10, 60, 480, 25));
    tfSocketAddress.setBounds(new Rectangle(10, 30, 485, 25));
    jLabel1.setText("Socket Address : ");
    jLabel1.setPreferredSize(new Dimension(126, 25));
    jLabel1.setBounds(new Rectangle(10, 5, 480, 25));
    jLabel1.setVerticalAlignment(SwingConstants.BOTTOM);
    btnAddToList.setText("+");
    btnAddToList.setBounds(new Rectangle(465, 85, 30, 25));
    btnAddToList.setMargin(new Insets(0, 0, 0, 0));
    btnAddToList.setFont(new Font("Dialog", 1, 13));
    btnAddToList.addActionListener(new ActionListener() {
                                     public void actionPerformed(ActionEvent e) {
                                       btnAddToList_actionPerformed(e);
                                     }
                                   }
    );
    btnRemoveFrmList.setText("-");
    btnRemoveFrmList.setBounds(new Rectangle(465, 115, 30, 25));
    btnRemoveFrmList.setMargin(new Insets(0, 0, 0, 0));
    btnRemoveFrmList.setFont(new Font("Dialog", 1, 13));
    btnRemoveFrmList.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent e) {
                                           btnRemoveFrmList_actionPerformed(e);
                                         }
                                       }
    );
    jScrollPane1.setBounds(new Rectangle(10, 115, 450, 65));
    rdvRelayAddrList.setFont(new Font("Dialog", 0, 12));
    rdvRelayAddrList.addListSelectionListener(new ListSelectionListener() {
                                                public void valueChanged(ListSelectionEvent e) {
                                                  rdvRelayAddrList_valueChanged(e);
                                                }
                                              }
    );
    jScrollPane1.getViewport().add(rdvRelayAddrList, null);
    this.getContentPane().add(jScrollPane1, null);
    this.getContentPane().add(btnRemoveFrmList, null);
    this.getContentPane().add(btnAddToList, null);
    this.getContentPane().add(jLabel1, null);
    this.getContentPane().add(tfSocketAddress, null);
    this.getContentPane().add(jLabel2, null);
    this.getContentPane().add(tfRendezvousAddr, null);
    this.getContentPane().add(lblMessage, null);
    this.getContentPane().add(btnOk, null);
    this.getContentPane().add(btnCancel, null);
  }

  private void saveJxtaPreferences() {
    String userHome = System.getProperty("user.home");
    File configFolder = new File(userHome + "/.dbsfs/config");
    Element element;
    if (!configFolder.exists()) {
      configFolder.mkdir();
    }
    String socketAddress;
    socketAddress = "urn:jxta:uuid-" + tfSocketAddress.getText();
    NodeList nodeList = socketDocument.getDocumentElement().getChildNodes();
    if (nodeList != null) {
      for (int i = 0; i < nodeList.getLength(); i++) {
        if (nodeList.item(i).getNodeName().equals("Id")) {
          Node node = nodeList.item(i).getFirstChild();
          node.setNodeValue(socketAddress);
        }
      }
    }

    File socketAdvFile = new File(userHome + "/.dbsfs/config/socket.adv");
    commonUtil.saveXMLDocumentToFile(socketDocument, socketAdvFile);

    File jxtaConfig = new File(userHome + "/.dbsfs/config/jxta_config.xml");
    nodeList = jxtaDocument.getElementsByTagName("jxta-rdv-relay-addr");
    Node parentNode;

    if (nodeList.getLength() > 0) {
      parentNode = nodeList.item(0);
      nodeList = parentNode.getChildNodes();
      int length = nodeList.getLength();
      for (int index = 0; index < length; index++) {
        parentNode.removeChild(nodeList.item(0));
      }
    } else {
      Element rootElement = jxtaDocument.getDocumentElement();
      element = jxtaDocument.createElement("jxta-rdv-relay-addr");
      rootElement.appendChild(element);
    }

    int size = listModel.getSize();
    if (size >= 1) {
      nodeList = jxtaDocument.getElementsByTagName("jxta-rdv-relay-addr");
      parentNode = nodeList.item(0);

      for (int index = 0; index < size; index++) {
        element = jxtaDocument.createElement("rdv-relay");
        element.setAttribute("addr", (String)listModel.get(index));
        parentNode.appendChild(element);
      }
    }

    commonUtil.saveXMLDocumentToFile(jxtaDocument, jxtaConfig);
  }

  private void readJxtaPreferences() {
    String userHome = System.getProperty("user.home");

    String defaultSocket = "";

    try {
      File socketAdvFile = new File(userHome + "/.dbsfs/config/socket.adv");
      if (!socketAdvFile.exists()) {
        socketAdvFile = new File("config/socket.adv");
      }

      socketDocument = commonUtil.getDocumentFromFile(socketAdvFile);
      NodeList nodeList = socketDocument.getDocumentElement().getChildNodes();
      for (int index = 0; index < nodeList.getLength(); index++) {
        logger.debug("nodeList : " + nodeList.item(index).getNodeName());
      }
      if (nodeList != null) {
        for (int i = 0; i < nodeList.getLength(); i++) {
          if (nodeList.item(i).getNodeName().equals("Id")) {
            Node node = nodeList.item(i).getFirstChild();
            defaultSocket = node.getNodeValue().trim();
            defaultSocket = defaultSocket.substring("urn:jxta:uuid-".length());
            tfSocketAddress.setText(defaultSocket);
          }
        }
      }
    } catch (Exception ex) {
      logger.error(commonUtil.getStackTrace(ex));
    }


    File jxtaConfig = new File(userHome + "/.dbsfs/config/jxta_config.xml");

    if (!jxtaConfig.exists()) {
      jxtaConfig = new File("config/jxta_config.xml");
    }

    jxtaDocument = commonUtil.getDocumentFromFile(jxtaConfig);

    NodeList nodeList = jxtaDocument.getElementsByTagName("rdv-relay");
    if (nodeList.getLength() != 0) {
      int nodeListLength = nodeList.getLength();
      NamedNodeMap namedNodeMap;
      for (int index = 0; index < nodeListLength; index++) {
        namedNodeMap = nodeList.item(index).getAttributes();
        listModel.addElement(namedNodeMap.getNamedItem("addr").getNodeValue());
      }
      rdvRelayAddrList.setModel(listModel);
      rdvRelayAddrList.setSelectedIndex(0);
    }
  }

  private void btnCancel_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  private void btnOk_actionPerformed(ActionEvent e) {
    saveJxtaPreferences();
    propertyChangeSupport.firePropertyChange("jxtaPreferencesChanged", false, true);
    this.dispose();
  }

  public void setMessageVisible(boolean visible) {
    lblMessage.setVisible(visible);
  }

  private void btnAddToList_actionPerformed(ActionEvent e) {
    String rdvRelayAddr = (tfRendezvousAddr.getText()).trim();
    if (!rdvRelayAddr.equals("")) {
      listModel.addElement(rdvRelayAddr);
      rdvRelayAddrList.setModel(listModel);
    }
  }

  private void btnRemoveFrmList_actionPerformed(ActionEvent e) {
    int size = listModel.capacity();
    if (size >= 1) {
      listModel.remove(rdvRelayAddrList.getSelectedIndex());
    }
  }

  private void rdvRelayAddrList_valueChanged(ListSelectionEvent e) {
    String selection = (String)rdvRelayAddrList.getSelectedValue();
    tfRendezvousAddr.setText(selection);
  }

  public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    propertyChangeSupport = new PropertyChangeSupport(this);
    propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
  }
}
