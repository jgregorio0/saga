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
	 * Create a few CmsProperty and save into resource path
	 * @param props
	 * @return
	 */
	public def addProperties(String path, List<CmsProperty> propList){
		addProperties(path, toMap(propList));
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
			String oldResPath, String newResPath,
			List<String> propList, boolean recursive){
		propList.each {
			CmsProperty prop = cmso.readPropertyObject(
					oldResPath, it, recursive)
			if (!prop.isNullProperty()){
				properties.put(prop.getName(), prop)
			}
		}
		save(newResPath)
		return this
	}

	public def copyAllProperties(
			String oldResPath, String newResPath, boolean recursive){
		clear();
		def propList = cmso.readPropertyObjects(oldResPath, recursive);
		propList.each {
			properties.put(it.getName(), it)
		}
		save(newResPath)
		return this
	}

	public def copyProperties(
			String oldResPath, String newResPath,
			List<String> propList, List<String> defPropList, boolean recursive){
		propList.eachWithIndex { propName, i ->
			CmsProperty prop = cmso.readPropertyObject(
					oldResPath, propName, recursive)
			addProperty(prop.getName(), prop, defPropList[i])
		}
		save(newResPath)
		return this;
	}

	/**
	 * Map properties
	 * @param oldResPath
	 * @param newResPath
	 * @param mapping
	 * @param recursive
	 * @return
	 */
	public def mapProperties(
			String oldResPath, String newResPath,
			Map<String, String> mapping, boolean recursive){
		mapping.each { oldName, newName ->
			CmsProperty prop = cmso.readPropertyObject(
					oldResPath, oldName, recursive)
			addProperty(newName, prop.getValue())
		}
		save(newResPath)
		return this;
	}

//	public def copyProperty(
//			String oldResPath, String key, String defValue, boolean recursive){
//		CmsProperty prop = cmso.readPropertyObject(
//				oldResPath, key, recursive)
//		addProperty(prop.getName(), prop, defValue)
//		save()
//		return this
//	}
//
//	public def copyProperty(
//			String oldResPath, String key, String defValue){
//		copyProperty(oldResPath, key, defValue, false)
//	}
//
//	public def copyProperty(
//			String oldResPath, String key, boolean recursive){
//		copyProperty(oldResPath, key, null, recursive)
//		return this
//	}

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

	/**
	 * Return all properties
	 * @param path
	 * @param recursive
     * @return
     */
	public Map<String, String> findAll(String path, boolean recursive){
		List<CmsProperty> cmsProperties = cmso.readPropertyObjects(path, recursive)
		Map<String, String> props = toMap(cmsProperties);
		return props;
	}

	/**
	 * Remove properties
	 * @param path
	 * @param properties
	 * @return
	 */
	public def rmProperty(String path, String propName){
		CmsProperty rmProp = new CmsProperty(propName, "", null)
		addProperty(rmProp);
		save(path);
		return this;
	}

	/**
	 * Remove list of properties
	 * @param path target resource path
	 * @param properties list of properties to remove
	 * @return
	 */
	public def rmProperties(String path, List<String> properties){
		properties.each{
			CmsProperty rmProp = new CmsProperty(it, "", null)
			addProperty(rmProp);
		}
		save(path);
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
		this;
	}

	/**
	 * Read property value
	 * @param cmso
	 * @param path
	 * @param propertyName
	 * @param recursive
	 * @return
	 */
	public static String readProperty(
			CmsObject cmso, String path, String propertyName, boolean recursive){
		CmsProperty prop = cmso.readPropertyObject(path, propertyName, recursive)
		if (prop.isNullProperty()){
			return null;
		} else {
			return prop.getValue()
		}

	}

	/**
	 * Return a Map [Name: Value] of properties
	 * @param cmsProps
	 * @return
	 */
	public static Map<String, String> toMap(List<CmsProperty> cmsProps){
		Map props = new HashMap<String, String>()
		cmsProps.each{ cmsProp ->
			if (!cmsProp.isNullProperty()){cmsProp.getName()
				props.put(cmsProp.getName(), cmsProp.getValue())
			}
		}
		return props;
	}

	/**
	 * Return a Map [Name: Value] of properties
	 * @param cmsProps
	 * @return
	 */
	public static List<CmsProperty> toList(Map<String, String> props){
		List<CmsProperty> cmsProps = [];
		props.each{ pName, pValue ->
			CmsProperty cmsProp = null;
			try {
				if (StringUtils.isNotBlank(pValue)) {
					cmsProp = new CmsProperty(pName, pValue, pValue)
				} else {
					cmsProp = new CmsProperty(pName, "", null)
				}
			} catch (Exception e){}

			if (cmsProp){
				cmsProps.add(cmsProp);
			}
		}
		return cmsProps;
	}
}