#!/bin/bash
#############################################################################
#                       Confidentiality Information                         #
#                                                                           #
# This module is the confidential and proprietary information of            #
# DBSentry Corp.; it is not to be copied, reproduced, or transmitted in any #
# form, by any means, in whole or in part, nor is it to be used for any     #
# purpose other than that for which it is expressly provided without the    #
# written permission of DBSentry Corp.                                      #
#                                                                           #
# Copyright (c) 2004-2005 DBSentry Corp.  All Rights Reserved.              #
#                                                                           #
#############################################################################
# $Id$
#############################################################################
# DBSentry FileSync Server startup/shutdown script                          #
#############################################################################
echo ""
echo "DBSentry FileSync Server"
echo "Copyright (c) 2005 DBSentry. All rights reserved."
echo ""

JAVA_HOME=/usr/java/j2sdk1.4.2_09
JAVA=${JAVA_HOME}/bin/java

FILESYNC_HOME=/u01/app/filesync

cd ${FILESYNC_HOME}

export DBS_CLASSPATH=\
lib/jxta/bcprov-jdk14.jar:\
lib/jxta/javax.servlet.jar:\
lib/jxta/jaxen-core.jar:\
lib/jxta/jaxen-jdom.jar:\
lib/jxta/jdom.jar:\
lib/jxta/jxta.jar:\
lib/jxta/jxtaext.jar:\
lib/jxta/log4j.jar:\
lib/jxta/org.mortbay.jetty.jar:\
lib/jxta/saxpath.jar:\
lib/jxta/swixml.jar:\
lib/filesync/plugins/cmsdk/support/oracle/nls_charset12.jar:\
lib/filesync/plugins/cmsdk/support/oracle/ocrs12.jar:\
lib/filesync/plugins/cmsdk/support/oracle/ojdbc14.jar:\
lib/filesync/plugins/cmsdk/support/oracle/ojdbc14dms.jar:\
lib/filesync/plugins/cmsdk/support/oracle/orai18n.jar:\
lib/filesync/plugins/cmsdk/support/cmsdk.jar:\
lib/filesync/plugins/cmsdk/support/dms.jar:\
lib/filesync/plugins/cmsdk/FileSyncCmsdkPlugin.jar:\
lib/filesync/FileSyncCommon.jar:\
lib/filesync/FileSyncSpecs.jar:\
lib/filesync/FileSyncServer.jar


${JAVA} -server -cp ${DBS_CLASSPATH} dbsentry.filesync.server.FsServer $1 >> ${FILESYNC_HOME}/log/jvm.stdout 2>&1

