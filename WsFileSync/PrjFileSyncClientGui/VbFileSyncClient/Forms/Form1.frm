VERSION 5.00
Begin VB.Form Form1 
   Caption         =   "Form1"
   ClientHeight    =   3195
   ClientLeft      =   60
   ClientTop       =   345
   ClientWidth     =   4680
   Icon            =   "Form1.frx":0000
   LinkTopic       =   "Form1"
   ScaleHeight     =   3195
   ScaleWidth      =   4680
   StartUpPosition =   3  'Windows Default
End
Attribute VB_Name = "Form1"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Sub Form_Load()
ChDir App.Path
Shell App.Path & "\FileSyncJars\jre\bin\javaw -cp FileSyncJars\jxta\bcprov-jdk14.jar;FileSyncJars\jxta\javax.servlet.jar;FileSyncJars\jxta\jaxen-core.jar;FileSyncJars\jxta\jaxen-jdom.jar;FileSyncJars\jxta\jdom.jar;FileSyncJars\jxta\jxta.jar;FileSyncJars\jxta\jxtaext.jar;FileSyncJars\jxta\log4j-1.2.8.jar;FileSyncJars\jxta\org.mortbay.jetty.jar;FileSyncJars\jxta\saxpath.jar;FileSyncJars\filesync\FileSyncClient.jar;FileSyncJars\filesync\FileSyncCommon.jar;FileSyncJars\jdic\jdic.jar;FileSyncClientGui.jar dbsentry.filesync.clientgui.FsTray"
Unload Me
End Sub
