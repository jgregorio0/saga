package com.saga.opencms.util

import org.apache.commons.lang3.StringUtils
import org.opencms.file.CmsObject
import org.opencms.file.CmsProperty
import org.opencms.file.CmsResource

public class SgProperties {

	CmsObject cmso;
	def properties = [:];

	public SgProperties(CmsObject cmso) {
		this.cmso = cmso;
	}

	/**
	 * Create a few CmsProperty and save into resource path
	 * @param props
	 * @return
	 */
	public def addProperties(String path, Map<String, String> props){
		props.each {key, value ->
			addProperty(key, value)
		}
		save(path)
		return this
	}

	/**
	 * Add a few properties. When resource is ready must run save method
	 * @param props
	 * @return
	 */
	public def addProperties(Map<String, String> props){
		props.each {key, value ->
			addProperty(key, value)
		}
		return this
	}

	/**
	 * Adds property identified by parameters key and value
	 * @param key
	 * @param value
	 * @return
	 */
	public def addProperty(String key, String value){
		if (!StringUtils.isEmpty(key) &&
				!StringUtils.isEmpty(value)) {
			CmsProperty property = new CmsProperty(key, value, value)
			addProperty(property)
		}
		return this
	}

	/**
	 * Adds property identified by parameters key and value
	 * @param key
	 * @param prop
	 * @return
	 */
	public def addProperty(CmsProperty prop){
		if (!prop.isNullProperty()){prop.getName()
			properties.put(prop.getName(), prop)
		}
		return this
	}

	public def copyProperties(
			String oldResPath, List<String> propList, boolean recursive){
		propList.each {
			CmsProperty prop = cmso.readPropertyObject(
					oldResPath, it, recursive)
			if (!prop.isNullProperty()){
				properties.put(prop.getName(), prop)
			}
		}
		save()
		return this
	}

	public def copyProperties(
			String oldResPath, List<String> propList,
			List<String> defPropList, boolean recursive){
		propList.eachWithIndex { propName, i ->
			CmsProperty prop = cmso.readPropertyObject(
					oldResPath, propName, recursive)
			addProperty(prop.getName(), prop, defPropList[i])
		}
		save()
	}

	public def copyProperty(
			String oldResPath, String key, String defValue, boolean recursive){
		CmsProperty prop = cmso.readPropertyObject(
				oldResPath, key, recursive)
		addProperty(prop.getName(), prop, defValue)
		save()
		return this
	}

	public def copyProperty(
			String oldResPath, String key, String defValue){
		copyProperty(oldResPath, key, defValue, false)
	}

	public def copyProperty(
			String oldResPath, String key, boolean recursive){
		copyProperty(oldResPath, key, null, recursive)
		return this
	}

	/**
	 * Adds Title property
	 * @param key
	 * @param value
	 * @return
	 */
	public def addPropertyTitle(String value){
		if (!StringUtils.isEmpty(value)) {
			if (value.length() > 255){
				value = value.substring(0, 255);
			}
			CmsProperty property = new CmsProperty("Title", value, value)
			addProperty(property)
		}
		return this
	}

	public def save(String path){
		cmso.lockResource(path)
		cmso.writePropertyObjects(path, properties.values().asList())
		cmso.unlockResource(path)
		return this;
	}

	public def save(CmsResource resource){
		String path = cmso.getSitePath(resource)
		save(path);
		return this;
	}

	public def clear(){
		properties.clear()
	}
}