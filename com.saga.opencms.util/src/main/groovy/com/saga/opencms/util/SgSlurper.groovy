package com.saga.opencms.util

import groovy.json.JsonSlurper
import groovy.util.slurpersupport.GPathResult
import org.opencms.file.CmsObject

public class SgSlurper {

	String url;
	String text;
	Class type;
	Object result;

	public SgSlurper(){
	}

	public SgSlurper(CmsObject cmso, String path){
		initCmsFile(cmso, path)
	}

	public SgSlurper(String content){
		initText(content)
	}

	def initCmsFile (CmsObject cmso, String path) {
		def contents = cmso.readFile(path).getContents();
		text = new String(contents, "UTF-8");
		return this;
	}

	def initUrl (String url){
		this.url = url
		text = new URL(url).getText()
		return this;
	}

	def initText (String text){
		this.text = text;
		return this;
	}

	public slurpObject(){
		type = Object.class;
		result = new JsonSlurper().parseText(text);
		return result;
	}

	public cleanComments(){
		text = text.replaceAll("--(-)+", "--")
		return this
	}

	public segment(String tagFrom, String tagTo){
		int iFrom = text.indexOf(tagFrom)
		int iTo = text.indexOf(tagTo, iFrom) + tagTo.length()
		text = text.substring(iFrom, iTo)
		return this
	}

	public slurpXml(){
		type = GPathResult.class;
		result = new XmlSlurper(false, true, true).parseText(text)
		return result;
	}
}