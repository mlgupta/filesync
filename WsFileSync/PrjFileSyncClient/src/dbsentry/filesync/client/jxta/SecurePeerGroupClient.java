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
 * $Id: SecurePeerGroupClient.java,v 1.16 2006/02/23 07:14:38 sgupta Exp $
 *****************************************************************************
 */
/*
package dbsentry.filesync.client.jxta;
                                                                            THIS CLASS HAS BEEN REMOVED 
import dbsentry.filesync.client.ClientUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//import java.io.StringWriter;              saurabh_remove

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.net.URI;

import java.util.Enumeration;

//import java.util.Hashtable;               saurabh_remove
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.discovery.DiscoveryService;

//import net.jxta.document.Advertisement;                     saurabh_remove
//import net.jxta.document.AdvertisementFactory;              saurabh_remove
//import net.jxta.document.Element;                           saurabh_remove
//import net.jxta.document.MimeMediaType;                     saurabh_remove
import net.jxta.document.StructuredDocument;

//import net.jxta.document.StructuredDocumentFactory;         saurabh_remove
//import net.jxta.document.StructuredTextDocument;            saurabh_remove
//import net.jxta.document.TextElement;                       saurabh_remove
//import net.jxta.exception.PeerGroupException;               saurabh_remove
//import net.jxta.id.ID;                                      saurabh_remove
import net.jxta.id.IDFactory;

//import net.jxta.impl.membership.PasswdMembershipService;    saurabh_remove  
//import net.jxta.impl.peergroup.StdPeerGroupParamAdv;        saurabh_remove
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;

//import net.jxta.peergroup.PeerGroupFactory;                 saurabh_remove
import net.jxta.peergroup.PeerGroupID;

//import net.jxta.platform.ModuleSpecID;                      saurabh_remove
//import net.jxta.protocol.ModuleImplAdvertisement;           saurabh_remove  
import net.jxta.protocol.PeerGroupAdvertisement;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


public class SecurePeerGroupClient {


  private PeerGroup securePeerGroup = null;

  private static PeerGroupID fileSyncPeerGroupID;

  private String login;

  private String passwd;

  private String groupName;

  private String groupId;

  private String modSpecId;

  private Logger logger;

  private ClientUtil clientUtil;

  /** Creates new RootWS 
  public SecurePeerGroupClient(Logger logger, PeerGroup myNetPeerGroup, File secureGroupFile) {
    this.logger = logger;
    this.clientUtil = new ClientUtil(logger);
    readSecureGroupConfig(secureGroupFile);
    try {
      fileSyncPeerGroupID = (PeerGroupID)IDFactory.fromURI(new URI(groupId));
    } catch (java.net.URISyntaxException e) {
      logger.error(" Can't create fileSyncPeerGroupID:  MalformedURLException");
      logger.error(clientUtil.getStackTrace(e));
    }
    securePeerGroup = this.discoverPeerGroup(myNetPeerGroup, fileSyncPeerGroupID);
    if (securePeerGroup != null) {
      logger.info("Peer Group Found ...");
      this.joinPeerGroup(securePeerGroup, login, passwd);
      logger.info("Peer Group Joined ...");
    } else {
      logger.info("Unable To Join Peer Group ...");
    }
  }


  private PeerGroup discoverPeerGroup(PeerGroup myNetPeerGroup, PeerGroupID fileSyncPeerGroupID) {
    // First discover the peer group
    // In most cases we should use discovery listeners so that
    // we can do the discovery assynchroneously.
    // Here I won't, for increased simplicity and because
    // The Peer Group Advertisement is in the local cache for sure
    PeerGroup securePeerGroup;
    DiscoveryService myNetPeerGroupDiscoveryService = null;
    if (myNetPeerGroup != null) {
      myNetPeerGroupDiscoveryService = myNetPeerGroup.getDiscoveryService();
    } else {
      logger.error("Can't join  Peer Group since its parent is null");
      System.exit(1);
    }
    boolean isGroupFound = false;
    Enumeration localPeerGroupAdvertisementEnumeration = null;
    PeerGroupAdvertisement fileSyncPeerGroupAdvertisement = null;
    int limitCount = 0;
    final int LIMIT = 3;
    while (!isGroupFound && limitCount < LIMIT) {
      limitCount++;
      try {
        localPeerGroupAdvertisementEnumeration =
          myNetPeerGroupDiscoveryService.getLocalAdvertisements(DiscoveryService.GROUP, "GID",
                                                                                                       fileSyncPeerGroupID.toString());
      } catch (java.io.IOException e) {
        logger.error("Can't Discover Local Adv");
      }

      if (localPeerGroupAdvertisementEnumeration != null) {
        while (localPeerGroupAdvertisementEnumeration.hasMoreElements()) {
          PeerGroupAdvertisement pgAdv = null;
          pgAdv = (PeerGroupAdvertisement)localPeerGroupAdvertisementEnumeration.nextElement();
          if (pgAdv.getPeerGroupID().equals(fileSyncPeerGroupID)) {
            fileSyncPeerGroupAdvertisement = pgAdv;
            isGroupFound = true;
            break;
          }
        }
      }

      if (!isGroupFound) {
        logger.debug("Sending remote discovery message : " + limitCount);
        myNetPeerGroupDiscoveryService
        .getRemoteAdvertisements(null, DiscoveryService.GROUP, "GID", fileSyncPeerGroupID.toString(), 5);
      }
      try {
        logger.debug("Group Found : " + isGroupFound);
        Thread.sleep(3 * 1000);
      } catch (Exception e) {
        ;
      }
    }
    try {
      if (fileSyncPeerGroupAdvertisement != null) {
        securePeerGroup = myNetPeerGroup.newGroup(fileSyncPeerGroupAdvertisement);
      } else {
        return null;
      }
    } catch (net.jxta.exception.PeerGroupException e) {
      logger.error("Can't create Peer Group from Advertisement");
      logger.error(clientUtil.getStackTrace(e));
      return null;
    }
    logger.debug("Peer group name : " + securePeerGroup.getPeerGroupName());
    logger.debug("Peer group ID : " + securePeerGroup.getPeerGroupID());
    logger.debug("Peer Name : " + securePeerGroup.getPeerName());
    return securePeerGroup;
  }

  private void joinPeerGroup(PeerGroup securePeerGroup, String login, String passwd) {
    // Get the Heavy Weight Paper for the resume
    // Alias define the type of credential to be provided
    StructuredDocument creds = null;
    try {
      // Create the resume to apply for the Job
      // Alias generate the credentials for the Peer Group
      AuthenticationCredential authCred = new AuthenticationCredential(securePeerGroup, null, creds);

      // Create the resume to apply for the Job
      // Alias generate the credentials for the Peer Group
      MembershipService membershipService = securePeerGroup.getMembershipService();

      // Send the resume and get the  Job application form
      // Alias get the Authenticator from the Authentication creds
      Authenticator auth = membershipService.apply(authCred);

      // Fill in the Job Application Form
      // Alias complete the authentication
      completeAuth(auth, login, passwd);

      // Check if I got the Job
      // Alias Check if the authentication that was submitted was
      //accepted.
      if (!auth.isReadyForJoin()) {
        logger.error("Failure in authentication.");
        logger.error("Group was not joined. Does not know how to complete authenticator");
      }
      // I got the Job, Join the company
      // Alias I the authentication I completed was accepted,
      // therefore join the Peer Group accepted.
      membershipService.join(auth);
    } catch (Exception e) {
      logger.error("Failure in authentication.");
      logger.error("Group was not joined. Login was incorrect.");
      logger.error(clientUtil.getStackTrace(e));
    }
  }

  private void completeAuth(Authenticator auth, String login, String passwd) throws Exception {

    Method[] methods = auth.getClass().getMethods();
    Vector authMethods = new Vector();

    // Find out with fields of the application needs to be filled
    // Alias Go through the methods of the Authenticator class and copy
    // them sorted by name into a vector.
    for (int eachMethod = 0; eachMethod < methods.length; eachMethod++) {
      if (methods[eachMethod].getName().startsWith("setAuth")) {
        if (Modifier.isPublic(methods[eachMethod].getModifiers())) {

          // sorted insertion.
          for (int doInsert = 0; doInsert <= authMethods.size(); doInsert++) {
            int insertHere = -1;
            if (doInsert == authMethods.size())
              insertHere = doInsert;
            else {
              if (methods[eachMethod].getName().compareTo(((Method)authMethods.elementAt(doInsert)).getName()) <= 0)
                insertHere = doInsert;
            }
            // end else

            if (-1 != insertHere) {
              authMethods.insertElementAt(methods[eachMethod], insertHere);
              break;
            }
            // end if ( -1 != insertHere)
          }
          // end for (int doInsert=0
        }
        // end if (modifier.isPublic
      }
      // end if (methods[eachMethod]
    }
    // end for (int eachMethod)


    Object[] AuthId = { login };
    Object[] AuthPasswd = { passwd };

    for (int eachAuthMethod = 0; eachAuthMethod < authMethods.size(); eachAuthMethod++) {
      Method doingMethod = (Method)authMethods.elementAt(eachAuthMethod);

      //     String authStepName = doingMethod.getName().substring(7);     saurabh_remove
      if (doingMethod.getName().equals("setAuth1Identity")) {
        // Found identity Method, providing identity
        doingMethod.invoke(auth, AuthId);

      } else if (doingMethod.getName().equals("setAuth2_Password")) {
        // Found Passwd Method, providing passwd
        doingMethod.invoke(auth, AuthPasswd);
      }
    }
  }


  public PeerGroup getSecurePeerGroup() {
    return securePeerGroup;
  }

  private void readSecureGroupConfig(File configFile) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    FileInputStream fis = null;
    try {
      builder = factory.newDocumentBuilder();
      fis = new FileInputStream(configFile);
      Document document = builder.parse(fis);

      NodeList nodeList = document.getElementsByTagName("jxta-group-property");
      NamedNodeMap namedNodeMap = nodeList.item(0).getAttributes();
      login = namedNodeMap.getNamedItem("group_login").getNodeValue();
      passwd = namedNodeMap.getNamedItem("group_password").getNodeValue();
      groupName = namedNodeMap.getNamedItem("group_name").getNodeValue();
      groupId = namedNodeMap.getNamedItem("group_id").getNodeValue();
      modSpecId = namedNodeMap.getNamedItem("module_specs_id").getNodeValue();

    } catch (Exception ex) {
      logger.error(clientUtil.getStackTrace(ex));
    } finally {
      try {
        if (fis != null) {
          fis.close();
        }
      } catch (IOException e) {
        ;
      }
    }
  }
}
*/