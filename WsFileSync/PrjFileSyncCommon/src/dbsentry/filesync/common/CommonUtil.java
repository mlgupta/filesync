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
 * $Id: CommonUtil.java,v 1.51 2006/08/10 13:11:26 sgupta Exp $
 *****************************************************************************
 */
package dbsentry.filesync.common;

import dbsentry.filesync.common.enumconstants.EnumSyncOperation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.security.MessageDigest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *	To provide the utilities that are common on client and server.
 *  @author              Jeetendra Prasad
 *  @version             1.0
 * 	Date of creation:    07-05-2005
 * 	Last Modfied by :    
 * 	Last Modfied Date:   
 */
public class

CommonUtil {
  public static final int JXTA_BUFFER_SIZE = 60 * 1024;

  public static final int FILE_BUFFER_SIZE = 512 * 1024;
  
  public static final int JXTA_SOCKET_BUFFER_SIZE = 128 * 1024;

  private Logger logger;

  /**
   * Constructs a CommonUtil object which takes Logger object to log appropriate messages.
   * @param logger Logger object.
   */
  public CommonUtil(Logger logger) {
    this.logger = logger;
  }

  /**
   * Extracts stackTrace String from the generic Exception object.
   * @param ex Generic Exception object.
   * @return String containing stack trace.
   */
  public String getStackTrace(Exception ex) {
    ByteArrayOutputStream baos = null;
    PrintWriter pw = null;
    String pwstackTrace = null;
    try {
      baos = new ByteArrayOutputStream();
      pw = new PrintWriter(baos);
      ex.printStackTrace(pw);
      pw.flush();
      baos.flush();
      pwstackTrace = baos.toString();
    } catch (Exception e) {
      ;
    } finally {
      try {

        if (pw != null) {
          pw.close();
        }
        if (baos != null) {
          baos.close();
        }
      } catch (IOException e) {
        ;
      }
      return pwstackTrace;
    }

  }

  /**
   * 
   * @return 
   * @param io
   */
  public String generateMD5Sum(InputStream io) {
    byte[] md5Sum = null;
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] inData = new byte[1024];
      int i = io.read(inData);
      while (i != -1) {
        digest.update(inData, 0, i);
        i = io.read(inData);
      }
      md5Sum = digest.digest();
    } catch (Exception ex) {
      logger.debug(getStackTrace(ex));
    } finally {
      try {
        if (io != null) {
          io.close();
        }
      } catch (IOException e) {
        ;
      }
      return hexEncode(md5Sum);
    }

  }

  /**
   * The byte[] returned by MessageDigest does not have a nice
   * textual representation, so some form of encoding is usually performed.
   * @param aInput byte array which is to be encoded.
   * @return encoded String.
   */
  private String hexEncode(byte[] aInput) {
    StringBuffer result = new StringBuffer();
    char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    for (int idx = 0; idx < aInput.length; ++idx) {
      byte b = aInput[idx];
      result.append(digits[(b & 0xf0) >> 4]);
      result.append(digits[b & 0x0f]);
    }
    return result.toString();
  }


  /**
   * 
   * @param document
   * @param node
   * @return 
   */
  public Element getDomElement(Document document, Node node) {
    Element element = document.createElement(node.getNodeName());
    element.setAttribute("PATH", node.getAttributes().getNamedItem("PATH").getNodeValue());
    element.setAttribute("NAME", node.getAttributes().getNamedItem("NAME").getNodeValue());
    if (node.getNodeName().equals("FILE")) {
      element.setAttribute("SIZE", node.getAttributes().getNamedItem("SIZE").getNodeValue());
      element.setAttribute("MD5SUM", node.getAttributes().getNamedItem("MD5SUM").getNodeValue());
      element.setAttribute("MODIFIED_DATE", node.getAttributes().getNamedItem("MODIFIED_DATE").getNodeValue());
    }
    element.setAttribute("ID", node.getAttributes().getNamedItem("ID").getNodeValue());
    return element;
  }

  /**
   * 
   * @return 
   */
  public Document getEmptyDocumentObject() {
    try {
      //prepare document object of the Updated Content
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.newDocument();
      Element root = document.createElement("ROOT");
      document.appendChild(root);
      return document;
    } catch (Exception ex) {
      logger.error(getStackTrace(ex));
    }
    return null;
  }

  /**
   * 
   * @param document
   * @return 
   */
  public String getXMLStringFromDocument(Document document) {
    String xmlfrmDocs = null;
    try {
      //prepare string from document object of the Updated Content
      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer();
      if (document.getDoctype() != null) {
        String systemValue = (new File(document.getDoctype().getSystemId())).getName();
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemValue);
      }

      DOMSource source = new DOMSource(document);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(JXTA_BUFFER_SIZE);
      StreamResult result = new StreamResult(baos);
      transformer.transform(source, result);
      baos.flush();
      xmlfrmDocs = baos.toString();
      baos.close();
      return xmlfrmDocs;
    } catch (Exception ex) {
      logger.error(getStackTrace(ex));
    }
    return "";
  }

  /**
   * 
   * @param xmlString
   * @return 
   */
  public Document getDocumentFromXMLString(String xmlString) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      ByteArrayOutputStream baos = new ByteArrayOutputStream(xmlString.length());
      PrintWriter printWriter = new PrintWriter(baos);
      printWriter.write(xmlString);
      printWriter.flush();
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      Document document = builder.parse(bais);
      baos.close();
      bais.close();
      return document;
    } catch (Exception ex) {
      logger.debug(getStackTrace(ex));
    }
    return null;
  }


  public Document getDocumentFromFile(File docFile) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      FileInputStream fis = new FileInputStream(docFile);
      Document document = builder.parse(fis);
      fis.close();
      return document;
    } catch (Exception ex) {
      logger.debug(getStackTrace(ex));
    }
    return null;
  }

  /**
   * 
   * @param string
   * @return 
   */
  public String generateMD5Sum(String string) {
    try {
      if (string != null) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(string.length());
        PrintWriter printWriter = new PrintWriter(baos);
        printWriter.write(string);
        printWriter.flush();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        baos.close();
        return generateMD5Sum(bais);
      }
    } catch (Exception ex) {
      logger.debug(getStackTrace(ex));
    }
    return null;
  }

  /**
   * 
   * @param document
   * @param file
   */
  public void saveXMLDocumentToFile(Document document, File file) {
    FileOutputStream fos = null;
    try {
      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer();
      DOMSource source = new DOMSource(document);
      fos = new FileOutputStream(file);
      StreamResult result = new StreamResult(fos);
      transformer.transform(source, result);
    } catch (Exception ex) {
      logger.debug(getStackTrace(ex));
    } finally {
      try {
        if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
        ;
      }
    }
  }

  /**
   * 
   * @param remoteNode
   * @param localNode
   * @return 
   */
  public int compareTwoNode(Node remoteNode, Node localNode) {
    String remoteNodeValue = remoteNode.getNodeName();
    String localNodeValue = localNode.getNodeName();
    if (remoteNodeValue.equals("FOLDER") && localNodeValue.equals("FOLDER")) {
      remoteNodeValue = remoteNode.getAttributes().getNamedItem("NAME").getNodeValue();
      localNodeValue = localNode.getAttributes().getNamedItem("NAME").getNodeValue();
      return remoteNodeValue.compareToIgnoreCase(localNodeValue);
    } else if (remoteNodeValue.equals("FILE") && localNodeValue.equals("FILE")) {
      remoteNodeValue = remoteNode.getAttributes().getNamedItem("NAME").getNodeValue();
      localNodeValue = localNode.getAttributes().getNamedItem("NAME").getNodeValue();
      return remoteNodeValue.compareToIgnoreCase(localNodeValue);
    } else if (remoteNodeValue.equals("FOLDER") && localNodeValue.equals("FILE")) {
      return -1;
    } else {
      return 1;
    }
  }

  /**
   * 
   * @param mainDocument
   * @param subDocument
   */
  public synchronized void mergeTwoDocument(Document mainDocument, Document subDocument) {
    Node baseNode = subDocument.getDocumentElement().getFirstChild();
    String status = baseNode.getAttributes().getNamedItem("STATUS").getNodeValue();
    if (status.equals(EnumSyncOperation.NEW_FOLDER) || status.equals(EnumSyncOperation.NEW_FILE)) {
      addNodeToDocument(mainDocument, baseNode.getFirstChild(), true);
    } else if (status.equals(EnumSyncOperation.FILE_CHANGED)) {
      updateNodeInDocument(mainDocument, baseNode.getFirstChild());
    } else if (status.equals(EnumSyncOperation.FILE_DELETED) || (status.equals(EnumSyncOperation.FOLDER_DELETED))) {
      removeNodeFromDocument(mainDocument, baseNode.getFirstChild());
    }
  }

  /**
   * 
   * @param mainDocument
   * @param nodeToAdd
   * @param recursive
   */
  public synchronized void addNodeToDocument(Document mainDocument, Node nodeToAdd, boolean recursive) {
    int comparisionResult;
    boolean replacedOrInserted = false;
    Node childNode = mainDocument.importNode(nodeToAdd, recursive);
    Node parentNode = nodeToAdd.getParentNode();
    Node node =
      getElementByID(parentNode.getAttributes().getNamedItem("ID").getNodeValue(), mainDocument.getDocumentElement()
                               .getFirstChild());
    NodeList nodeList = node.getChildNodes();
    int nodeListLength = nodeList.getLength();
    for (int index = 0; index < nodeListLength; index++) {
      comparisionResult = compareTwoNode(nodeList.item(index), childNode);
      if (comparisionResult > 0) {
        node.insertBefore(childNode, nodeList.item(index));
        replacedOrInserted = true;
        break;
      } else if (comparisionResult == 0) {
        replacedOrInserted = true;
        break;
      }
    }
    if (!replacedOrInserted) {
      node.appendChild(childNode);
    }
  }

  /**
   * 
   * @param mainDocument
   * @param nodeToRemove
   */
  public synchronized void removeNodeFromDocument(Document mainDocument, Node nodeToRemove) {
    String elementId = nodeToRemove.getAttributes().getNamedItem("ID").getNodeValue();
    Node root = mainDocument.getDocumentElement().getFirstChild();
    Node node = getElementByID(elementId, root);
    Node parentNode = node.getParentNode();
    parentNode.removeChild(node);
  }

  /**
   * 
   * @param mainDocument
   * @param nodeToupdate
   */
  private synchronized void updateNodeInDocument(Document mainDocument, Node nodeToupdate) {
    String elementId = nodeToupdate.getAttributes().getNamedItem("ID").getNodeValue();
    Node root = mainDocument.getDocumentElement().getFirstChild();
    Node childElementToUpdate = getElementByID(elementId, root);
    Node parentNode = childElementToUpdate.getParentNode();
    Node childNode = mainDocument.importNode(nodeToupdate, true);
    parentNode.replaceChild(childNode, childElementToUpdate);
  }

  /**
   * 
   * @param elementId
   * @param root
   * @return 
   */
  public Node getElementByID(String elementId, Node root) {
    Node matchingNode = null;
    //Check to see if root is the desired element. If so return root.

    if (root == null) {
      return null;
    }
    String nodeId = root.getAttributes().getNamedItem("ID").getNodeValue();
    if (nodeId.equals(elementId)) {
      return root;
    }

    //Check to see if root has any children if not return null
    if (!(root.hasChildNodes())) {
      return null;
    }
    //Root has children, so continue searching for them
    NodeList childNodes = root.getChildNodes();
    int noChildren = childNodes.getLength();
    for (int i = 0; i < noChildren; i++) {
      if (matchingNode == null) {
        Node child = childNodes.item(i);
        matchingNode = getElementByID(elementId, child);
      } else {
        break;
      }
    }
    return matchingNode;
  }

  /**
   * gives String representation of CommonUtil object.
   * @return String repersentation of CommonUtil object.
   */
  public String toString() {
    return this.toString();
  }

}
