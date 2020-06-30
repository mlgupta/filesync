<?xml version='1.0' ?>
<helpset version="1.1">

<title>DBSentry FileSync Help Guide</title>
<maps>
    <homeId>aboutDBSFileSync_html</homeId>
    <mapref location="FileSyncmap.xml" />
</maps>

<view>
    <label>TOC</label>
    <type>oracle.help.navigator.tocNavigator.TOCNavigator</type>
    <data engine="oracle.help.engine.XMLTOCEngine">FsFileToc.xml</data>
</view>

<view>
    <label>Index</label>
    <type>oracle.help.navigator.keywordNavigator.KeywordNavigator</type>
    <title>DBSentry FileSync Help Guide</title>
    <data engine="oracle.help.engine.XMLIndexEngine">FileSyncIndex.xml</data>
</view>

<view>
    <label>Search</label>
    <title>DBSentry FileSync Help Guide</title>
    <type>oracle.help.navigator.searchNavigator.SearchNavigator</type>
    <data engine="oracle.help.engine.SearchEngine">FileSyncIndex.idx</data>
</view>

<wintype default="true">
   <height>500</height>
   <width>400</width>
   <x>-10</x>
   <y>-10</y>
   <textfg>#000000</textfg>
   <linkfg>#0000FF</linkfg>
   <bg>#FFF09E</bg>
   <title>DBSentry FileSync Help</title>
   <toolbar>00004</toolbar>
   <toolbar>00040</toolbar>
   <toolbar>02000</toolbar>
   <toolbar>08000</toolbar>
   <toolbar>00040</toolbar>
   <toolbar>04000</toolbar>
</wintype>
</helpset>

