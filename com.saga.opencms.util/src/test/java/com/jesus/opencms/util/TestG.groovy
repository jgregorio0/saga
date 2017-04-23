package com.jesus.opencms.util

import com.saga.opencms.util.SgRes
import com.saga.opencms.util.SgSlurper
import groovy.util.slurpersupport.GPathResult
import org.xml.sax.SAXException

import javax.xml.parsers.ParserConfigurationException

def xmlContent =
"""
<?xml version="1.0" encoding="UTF-8"?>

<BootstrapTexts xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="opencms://system/modules/com.alkacon.bootstrap.schemas/schemas/text.xsd">
  <BootstrapText language="de">
    <Headline><![CDATA[Hola Ula]]></Headline>
    <Text name="Text0">
      <links/>
      <content><![CDATA[<p>Esto es OpenCms</p>]]></content>
    </Text>
  </BootstrapText>
  <BootstrapText language="en">
    <Headline><![CDATA[Hola Ula]]></Headline>
    <Text name="Text0">
      <links/>
      <content><![CDATA[<p>Esto es OpenCms</p>]]></content>
    </Text>
  </BootstrapText>
</BootstrapTexts>
"""

//def Map<String, Object> xml2Map(String content)
//        throws IOException, SAXException, ParserConfigurationException {
//    GPathResult xml = new SgSlurper(content).cleanControlCode().slurpXml();
//    // Convert it to a Map containing a List of Maps
//    return xml2Map(xml);
//}
//
//def xml2Map(GPathResult xml) {
//    xml.children().collectEntries(){
//        def lang = it.@language.text();
//        if (lang) {
//            [lang, [it.name(), it.childNodes() ? xml2Map(it): it.text() ]]
//        } else {
//            [it.name(), it.childNodes() ? xml2Map(it): it.text() ]
//        }
//    }
//}

def map = SgRes.toMap(xmlContent)
println map

def json = SgRes.toJson(xmlContent)
println json