package com.saga.opencms.scripts

import groovy.util.slurpersupport.GPathResult
import org.opencms.json.JSONObject

import java.nio.file.Files
import java.nio.file.Paths

String manifestPath = "C:\\Users\\jgregorio\\IdeaProjects\\UpoSedeElectronica\\resources\\packages\\upo-expedientes-lic-otros_2016-07-08\\manifest.xml"
String jsonPath = "C:\\Users\\jgregorio\\IdeaProjects\\UpoSedeElectronica\\resources\\packages\\upo-expedientes-lic-otros_2016-07-08\\filename2Id.json"

String content = new String(Files.readAllBytes(Paths.get(manifestPath)), "UTF-8")

XmlSlurper slurper = new XmlSlurper(false, true, true)
GPathResult xml = slurper.parseText(content)

def fileList= xml.depthFirst().findAll{
	it.name() == 'file'
}

def filename2NavText = [:]
for (int i = 0; i < fileList.size(); i++) {
	def file = fileList.get(i)
	String path = file.source.text()
	if (path.endsWith(".html")){
		String filename = path.substring(path.lastIndexOf("/") + 1)
		def prop = file.properties.depthFirst().find{
			it.name == "NavText"
		}
		if (prop) {
			filename2NavText.put(filename, prop.value.text())
		}
	}
}
println filename2NavText
JSONObject json = new JSONObject(filename2NavText)
println json
//Files.createFile(Paths.get(jsonPath))
Files.write(Paths.get(jsonPath), json.toString(1).getBytes("UTF-8"))