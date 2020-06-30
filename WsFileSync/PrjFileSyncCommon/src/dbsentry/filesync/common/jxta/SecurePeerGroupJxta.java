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
 package dbsentry.filesync.common.jxta;

import dbsentry.filesync.common.CommonUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.impl.membership.passwd.PasswdMembershipService;
import net.jxta.membership.Authenticator;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


/**
 * @author Saurabh Gupta
 * @version 1.0
 * @description This Class  illustrates how to joine SecurePeerGroupJxta And provide secureNetPeerGroup Id
 * To JxtaServer as well as JxtaClient.
 * @Date of creation: 14-02-2006
 * @Last Modfied by : Saurabh Gupta
 * @Last Modfied Date: 05-09-2006
 */
public class SecurePeerGroupJxta {

  private PeerGroup discoveredFileSyncPeerGroup = null;

  private static PeerGroupID fileSyncPeerGroupID;

  private String login;

  private String passwd;

  private String groupName;

  private String groupId;

  private String modSpecId;

  private Logger logger;

  private CommonUtil commonUtil;


  /**
   * @param logger Logger
   * @param myNetPeerGroup PeerGroup
   * @param document  Document
   * @param isServer boolean
   * @description Constructor For SecurePeerGroupJxta.
   */
  public SecurePeerGroupJxta(Logger logger, PeerGroup myNetPeerGroup, Document document, boolean isServer) {
    PeerGroup securePeerGroup = null;
    this.logger = logger;
    commonUtil = new CommonUtil(logger);
    readSecureGroupConfig(document);
    try {
      fileSyncPeerGroupID = (PeerGroupID)IDFactory.fromURI(new URI(groupId));
    } catch (URISyntaxException e) {
      logger.error(" Can't create fileSyncPeerGroupID:  MalformedURLException");
      logger.error(commonUtil.getStackTrace(e));
    }

    if (isServer) {
      
      this.discoveredFileSyncPeerGroup = this.discoverPeerGroupClient(myNetPeerGroup, fileSyncPeerGroupID);
      if (discoveredFileSyncPeerGroup == null) {
        securePeerGroup=createPeerGroup(myNetPeerGroup, groupName, login, passwd);
        if (securePeerGroup != null) {
          logger.info(" Peer Group Created ...");
          this.discoveredFileSyncPeerGroup = this.discoverPeerGroupServer(myNetPeerGroup, fileSyncPeerGroupID);
        }
      }
    } else {
      this.discoveredFileSyncPeerGroup = this.discoverPeerGroupClient(myNetPeerGroup, fileSyncPeerGroupID);
    }
    if (discoveredFileSyncPeerGroup != null) {
      logger.info("Peer Group Found ...");
      this.joinPeerGroup(discoveredFileSyncPeerGroup, login, passwd);
      logger.info(" Peer Group Joined ...");
    } else {
      logger.info(" Unable To joine Peer Group...");
    }
  }

  /**
   * @param rootPeerGroup PeerGroup
   * @param groupName String
   * @param login String
   * @param passwd String
   * @return securePeerGroup
   * @description Private Method for createing securePeerGroup.
   */
  private PeerGroup createPeerGroup(PeerGroup rootPeerGroup, String groupName, String login, String passwd) {
    PeerGroup securePeerGroup = null;
    PeerGroupAdvertisement fileSyncPeerGroupAdvertisement;

    // Create the PeerGroup Module Implementation Adv
    ModuleImplAdvertisement passwdMembershipModuleImplAdv;
    passwdMembershipModuleImplAdv = this.createPasswdMembershipPeerGroupModuleImplAdv(rootPeerGroup);
    // Publish it in the parent peer group
    DiscoveryService rootPeerGroupDiscoveryService = rootPeerGroup.getDiscoveryService();
    try {
      rootPeerGroupDiscoveryService
      .publish(passwdMembershipModuleImplAdv, PeerGroup.DEFAULT_LIFETIME, (long)1000 * 60 * 30);
      rootPeerGroupDiscoveryService.remotePublish(passwdMembershipModuleImplAdv, (long)1000 * 60 * 30);
    } catch (java.io.IOException e) {
      logger.error("Can't Publish passwdMembershipModuleImplAdv");
      System.exit(1);
    }
    // Now, Create the Peer Group Advertisement
    fileSyncPeerGroupAdvertisement =
      this.createPeerGroupAdvertisement(passwdMembershipModuleImplAdv, groupName, login, passwd);
    // Publish it in the parent peer group
    try {
      rootPeerGroupDiscoveryService
      .publish(fileSyncPeerGroupAdvertisement, PeerGroup.DEFAULT_LIFETIME, (long)1000 * 60 * 30);
      rootPeerGroupDiscoveryService.remotePublish(fileSyncPeerGroupAdvertisement, (long)1000 * 60 * 30);
    } catch (java.io.IOException e) {
      logger.error("Can't Publish fileSyncPeerGroupAdvertisement");
      System.exit(1);
    }
    // Finally Create the Peer Group
    if (fileSyncPeerGroupAdvertisement == null) {
      logger.error("fileSyncPeerGroupAdvertisement is null");
    }
    try {
      securePeerGroup = rootPeerGroup.newGroup(fileSyncPeerGroupAdvertisement);
    } catch (net.jxta.exception.PeerGroupException e) {
      logger.error("Can't create Satella Peer Group from Advertisement");
      logger.error(commonUtil.getStackTrace(e));
    }
    return securePeerGroup;
    
  }

  /**
   * @param passwdMembershipModuleImplAdv ModuleImplAdvertisement
   * @param groupName String
   * @param login String
   * @param passwd String 
   * @return fileSyncPeerGroupAdvertisement
   * @description Private Method for creating the PeerGroupAdvertisement.
   */
  private PeerGroupAdvertisement createPeerGroupAdvertisement(ModuleImplAdvertisement passwdMembershipModuleImplAdv,
                                                              String groupName, String login, String passwd) {
    // Create a PeerGroupAdvertisement for the peer group
    PeerGroupAdvertisement fileSyncPeerGroupAdvertisement =
      (PeerGroupAdvertisement)AdvertisementFactory.newAdvertisement(PeerGroupAdvertisement.getAdvertisementType());

    // Instead of creating a new group ID each time, by using the line below
    // fileSyncPeerGroupAdvertisement.setPeerGroupID(IDFactory.newPeerGroupID());
    // I use a fixed ID so that each time I start SecurePeerGroup,
    // it creates the same Group
    fileSyncPeerGroupAdvertisement.setPeerGroupID(fileSyncPeerGroupID);
    fileSyncPeerGroupAdvertisement.setModuleSpecID(passwdMembershipModuleImplAdv.getModuleSpecID());
    fileSyncPeerGroupAdvertisement.setName(groupName);
    fileSyncPeerGroupAdvertisement.setDescription("Peer Group using Password Authentication");

    // Now create the Structured Document Containing the login and passwd informations.
    // Login and passwd are put into the Param section of the peer Group
    if (login != null) {
      StructuredTextDocument loginAndPasswd =
        (StructuredTextDocument)StructuredDocumentFactory.newStructuredDocument(new MimeMediaType("text/xml"), "Parm");
      String loginAndPasswdString = login + ":" + PasswdMembershipService.makePsswd(passwd) + ":";
      TextElement loginElement = loginAndPasswd.createElement("login", loginAndPasswdString);
      loginAndPasswd.appendChild(loginElement);
      // All Right, now that loginAndPasswdElement (The strucuted document
      // that is the Param Element for The PeerGroup Adv
      // is done, include it in the Peer Group Advertisement
      fileSyncPeerGroupAdvertisement.putServiceParam(PeerGroup.membershipClassID, loginAndPasswd);
    }
    return fileSyncPeerGroupAdvertisement;
  }


  /**
   * @param myNetPeerGroup PeerGroup
   * @param fileSyncPeerGroupID PeerGroupID 
   * @return securePeerGroup
   * @description Private Method for Discovering the PeerGroupServer.
   */
  private PeerGroup discoverPeerGroupServer(PeerGroup myNetPeerGroup, PeerGroupID fileSyncPeerGroupID) {
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
    while (!isGroupFound) {
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
      try {
        Thread.sleep(5 * 1000);
      } catch (Exception e) {
        ;
      }
    }
    try {
      securePeerGroup = myNetPeerGroup.newGroup(fileSyncPeerGroupAdvertisement);
    } catch (net.jxta.exception.PeerGroupException e) {
      logger.error("Can't create Peer Group from Advertisement");
      logger.error(commonUtil.getStackTrace(e));
      return null;
    }
    return securePeerGroup;
  }

  /**
   * @param myNetPeerGroup PeerGroup
   * @param fileSyncPeerGroupID PeerGroupID
   * @return securePeerGroup
   * @description Private Method for discovering the PeerGroupClient.
   */
  private PeerGroup discoverPeerGroupClient(PeerGroup myNetPeerGroup, PeerGroupID fileSyncPeerGroupID) {
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
      logger.error(commonUtil.getStackTrace(e));
      return null;
    }
    logger.debug("Peer group name : " + securePeerGroup.getPeerGroupName());
    logger.debug("Peer group ID : " + securePeerGroup.getPeerGroupID());
    logger.debug("Peer Name : " + securePeerGroup.getPeerName());
    return securePeerGroup;
  }

  /**
   * @param rootPeerGroup PeerGroup
   * @return passwdMembershipPeerGroupModuleImplAdv
   * @description Private Method For creating Password Membership PeerGroup ImplAdvertisement. 
   */
  private ModuleImplAdvertisement createPasswdMembershipPeerGroupModuleImplAdv(PeerGroup rootPeerGroup) {
    // Create a ModuleImpl Advertisement for the Passwd Membership Service
    // Take a allPurposePeerGroupImplAdv ModuleImplAdvertisement parameter to
    // Clone some of its fields. It is easier than to recreate everything
    // from scratch

    // Try to locate where the PasswdMembership is within this ModuleImplAdvertisement.
    // For a PeerGroup Module Impl, the list of the services (including Membership)
    // are located in the Param section
    ModuleImplAdvertisement allPurposePeerGroupImplAdv = null;
    try {
      allPurposePeerGroupImplAdv = rootPeerGroup.getAllPurposePeerGroupImplAdvertisement();
    } catch (java.lang.Exception e) {
      logger.error("Can't Execute: getAllPurposePeerGroupImplAdvertisement();");
      System.exit(1);
    }
    ModuleImplAdvertisement passwdMembershipPeerGroupModuleImplAdv = allPurposePeerGroupImplAdv;
    ModuleImplAdvertisement passwdMembershipServiceModuleImplAdv = null;
    StdPeerGroupParamAdv passwdMembershipPeerGroupParamAdv = null;

    try {
      passwdMembershipPeerGroupParamAdv = new StdPeerGroupParamAdv(allPurposePeerGroupImplAdv.getParam());
    } catch (Exception e) {
      logger
      .error("Can't execute: StdPeerGroupParamAdv passwdMembershipPeerGroupParamAdv = new StdPeerGroupParamAdv (allPurposePeerGroupImplAdv.getParam());");
      System.exit(1);
    }

    Hashtable allPurposePeerGroupServicesHashtable = passwdMembershipPeerGroupParamAdv.getServices();
    Enumeration allPurposePeerGroupServicesEnumeration = allPurposePeerGroupServicesHashtable.keys();
    boolean membershipServiceFound = false;
    while ((!membershipServiceFound) && (allPurposePeerGroupServicesEnumeration.hasMoreElements())) {
      Object allPurposePeerGroupServiceID = allPurposePeerGroupServicesEnumeration.nextElement();
      if (allPurposePeerGroupServiceID.equals(PeerGroup.membershipClassID)) {
        // allPurposePeerGroupMemershipServiceModuleImplAdv is the
        // all Purpose Mermbership Service for the all purpose
        // Peer Group  Module Impl adv
        ModuleImplAdvertisement allPurposePeerGroupMemershipServiceModuleImplAdv =
          (ModuleImplAdvertisement)allPurposePeerGroupServicesHashtable.get(allPurposePeerGroupServiceID);
        //Create the passwdMembershipServiceModuleImplAdv
        passwdMembershipServiceModuleImplAdv =
          this.createPasswdMembershipServiceModuleImplAdv(allPurposePeerGroupMemershipServiceModuleImplAdv);
        //Remove the All purpose Membership Service implementation
        allPurposePeerGroupServicesHashtable.remove(allPurposePeerGroupServiceID);
        // And Replace it by the Passwd Membership Service Implementation
        allPurposePeerGroupServicesHashtable.put(PeerGroup.membershipClassID, passwdMembershipServiceModuleImplAdv);
        membershipServiceFound = true;
        // Now the Service Advertisements are complete
        // Let's update the passwdMembershipPeerGroupModuleImplAdv by
        // Updating its param
        passwdMembershipPeerGroupModuleImplAdv
        .setParam((Element)passwdMembershipPeerGroupParamAdv.getDocument(new MimeMediaType("text/xml")));
        // Update its Spec ID
        // This comes from the Instant P2P PeerGroupManager Code (Thanks !!!!)
        if (!passwdMembershipPeerGroupModuleImplAdv.getModuleSpecID().equals(PeerGroup.allPurposePeerGroupSpecID)) {
          passwdMembershipPeerGroupModuleImplAdv
          .setModuleSpecID(IDFactory.newModuleSpecID(passwdMembershipPeerGroupModuleImplAdv.getModuleSpecID()
                                                                                           .getBaseClass()));
        } else {
          ID passwdGrpModSpecID = ID.nullID;
          try {
            passwdGrpModSpecID = IDFactory.fromURI(new URI(modSpecId));
          } catch (URISyntaxException e) {
            logger.error(commonUtil.getStackTrace(e));
          }
          passwdMembershipPeerGroupModuleImplAdv.setModuleSpecID((ModuleSpecID)passwdGrpModSpecID);
        }
        //End Else
        membershipServiceFound = true;
      }
      //end if (allPurposePeerGroupServiceID.equals(PeerGroup.membershipClassID))
    }
    //end While

    return passwdMembershipPeerGroupModuleImplAdv;
  }

  /**
   * @param allPurposePeerGroupMemershipServiceModuleImplAdv ModuleImplAdvertisement
   * @return passwdMembershipServiceModuleImplAdv
   * @description Private Method For Creating a new ModuleImplAdvertisement for the Membership Service. 
   */
  private ModuleImplAdvertisement createPasswdMembershipServiceModuleImplAdv(ModuleImplAdvertisement allPurposePeerGroupMemershipServiceModuleImplAdv) {
    //Create a new ModuleImplAdvertisement for the Membership Service
    ModuleImplAdvertisement passwdMembershipServiceModuleImplAdv =
      (ModuleImplAdvertisement)AdvertisementFactory.newAdvertisement(ModuleImplAdvertisement.getAdvertisementType());
    passwdMembershipServiceModuleImplAdv.setModuleSpecID(PasswdMembershipService.passwordMembershipSpecID);
    passwdMembershipServiceModuleImplAdv.setCode(PasswdMembershipService.class.getName());
    passwdMembershipServiceModuleImplAdv.setDescription(" Module Impl Advertisement for the PasswdMembership Service");
    passwdMembershipServiceModuleImplAdv.setCompat(allPurposePeerGroupMemershipServiceModuleImplAdv.getCompat());
    passwdMembershipServiceModuleImplAdv.setUri(allPurposePeerGroupMemershipServiceModuleImplAdv.getUri());
    passwdMembershipServiceModuleImplAdv.setProvider(allPurposePeerGroupMemershipServiceModuleImplAdv.getProvider());
    return passwdMembershipServiceModuleImplAdv;
  }


  /**
   * @param securePeerGroup PeerGroup
   * @param login String
   * @param passwd String 
   * @description Private Method For Joing the Peer Group.
   */
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
      logger.error(commonUtil.getStackTrace(e));
    }
  }

  /**
   * @param auth Authenticator
   * @param login String
   * @param passwd String
   * @throws Exception
   * @description  Private Method For Setting Authentication Id And Password.
   */
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

      // String authStepName = doingMethod.getName().substring(7); 
      if (doingMethod.getName().equals("setAuth1Identity")) {
        // Found identity Method, providing identity
        doingMethod.invoke(auth, AuthId);

      } else if (doingMethod.getName().equals("setAuth2_Password")) {
        // Found Passwd Method, providing passwd
        doingMethod.invoke(auth, AuthPasswd);
      }
    }
  }

  /**
   * @return discoveredFileSyncPeerGroup
   * @description Getter Method for SecurePeerGroup
   */
  public PeerGroup getSecurePeerGroup() {
    return this.discoveredFileSyncPeerGroup;
  }

  /**
   * @param document Document
   * @description Private Method For Reading the securePeerGroupConfig File. 
   */
  private void readSecureGroupConfig(Document document) {
    try {
      NodeList nodeList = document.getElementsByTagName("jxta-group-property");
      NamedNodeMap namedNodeMap = nodeList.item(0).getAttributes();
      login = namedNodeMap.getNamedItem("group_login").getNodeValue();
      passwd = namedNodeMap.getNamedItem("group_password").getNodeValue();
      groupName = namedNodeMap.getNamedItem("group_name").getNodeValue();
      groupId = namedNodeMap.getNamedItem("group_id").getNodeValue();
      modSpecId = namedNodeMap.getNamedItem("module_specs_id").getNodeValue();
    } catch (Exception ex) {
      logger.error(commonUtil.getStackTrace(ex));
    }
  }


}
