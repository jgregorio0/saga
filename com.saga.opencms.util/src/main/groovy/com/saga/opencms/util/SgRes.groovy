package com.saga.opencms.util

import org.apache.commons.lang3.StringUtils
import org.apache.commons.logging.Log
import org.opencms.file.*
import org.opencms.file.types.I_CmsResourceType
import org.opencms.loader.CmsLoaderException
import org.opencms.main.CmsException
import org.opencms.main.CmsLog
import org.opencms.main.OpenCms
import org.opencms.xml.CmsXmlContentDefinition
import org.opencms.xml.CmsXmlEntityResolver
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.content.CmsXmlContentFactory
import org.opencms.xml.types.I_CmsXmlContentValue
/**
 *  Based on org.opencmshispano.module.resources.manager developed by sraposo
 */

public class SgRes {
	private static final Log LOG = CmsLog.getLog(SgRes.class);
	private static final String SCHEMA_PREFIX = "opencms:/";

	// Update content only if value is not empty
	// Update content only if value is not null
	// Update content and removes when value is null
	public static enum Mode {
		MODE_UPDATE_NON_EMPTY, MODE_UPDATE_NON_NULL, MODE_REMOVE_WHEN_NULL
	}

	CmsObject cmso;
	Mode mode;

	public SgRes() {
		mode = Mode.MODE_UPDATE_NON_EMPTY;
	}

	/**
	 * Initialize resource manager.
	 *
	 * @param cmso
	 */
	public SgRes(CmsObject cmso) {
		this();
		this.cmso = cmso;
	}

	/**
	 * Initialize resource manager.
	 *
	 * @param cmso
	 */
	public SgRes(CmsObject cmso, Mode mode) {
		this.cmso = cmso;
		this.mode = mode;
	}

	public CmsResource save(HashMap data, String resource, String resourceTypeName) {
		return save(data, resource, resourceTypeName, false, (String) null);
	}

	public CmsResource save(HashMap data, String resource, String type, boolean publish, String customLocale) {
		boolean success = true;
		Object cmsResource = null;

		try {
			boolean exc = cmso.existsResource(resource, CmsResourceFilter.ALL);
			CmsXmlContent content = null;
			Locale localizacion = cmso.getRequestContext().getLocale();
			if (customLocale != null) {
				localizacion = new Locale(customLocale);
			}

			if (exc) {
				CmsFile keys = cmso.readFile(resource);
				cmsResource = keys;
				content = CmsXmlContentFactory.unmarshal(cmso, keys);
				List itKeys = content.getLocales();
				if (!itKeys.contains(localizacion)) {
					content.copyLocale((Locale) itKeys.get(0), localizacion);
				}
			} else {
				String schema = getSchemaByType(type);
				CmsXmlContentDefinition schemaDef = CmsXmlContentDefinition.unmarshal(schema, new CmsXmlEntityResolver(cmso));
				content = CmsXmlContentFactory.createDocument(cmso, localizacion, "UTF-8", schemaDef);
			}

			Set keys2 = data.keySet();
			Iterator itKeys2 = keys2.iterator();
			boolean modified = false;

			while (itKeys2.hasNext()) {
				String key = (String) itKeys2.next();
				Object value = data.get(key);
				if (value instanceof List) {
					modified = this.manageMultipleContent((List) value, key, localizacion, content) || modified;
				} else if (value instanceof Map) {
					modified = this.manageNestedContent((Map) value, key, localizacion, content) || modified;
				} else if (value instanceof Choice) {
					modified = this.manageChoiceContent(((Choice) value).getSubfields(), key, localizacion, content) || modified;
				} else {
					modified = this.manageSimpleContent(key, (String) value, localizacion, content) || modified;
				}
			}

			if (modified) {
				cmsResource = this.createOrEditResource(resource, type, content, publish);
				if (cmsResource == null) {
					success = false;
				} else {
					success = true;
				}
			} else {
				success = true;
			}
		} catch (Exception var16) {
			var16.printStackTrace();
			LOG.error(var16.toString());
			success = false;
		}

		return (CmsResource) cmsResource;
	}

	public static String getSchemaByType(int type) throws UtilException {
		try {
			I_CmsResourceType e = OpenCms.getResourceManager().getResourceType(type);
			String schema = e.getConfiguration().get("schema");
			return "opencms:/" + schema;
		} catch (CmsLoaderException var3) {
			throw new UtilException("No existe el recurso con id " + type);
		}
	}

	public static String getSchemaByType(String typeName) throws UtilException {
		try {
			I_CmsResourceType e = OpenCms.getResourceManager().getResourceType(typeName);
			String schema = e.getConfiguration().get("schema");
			return "opencms:/" + schema;
		} catch (CmsLoaderException var3) {
			throw new UtilException("No existe el recurso con id " + typeName);
		}
	}

	public static class UtilException extends Exception {
		private static final long serialVersionUID = 1L;

		public UtilException() {
		}

		public UtilException(String msg) {
			super(msg);
		}
	}

	protected CmsResource createOrEditResource(String resource, String typeName, CmsXmlContent content, boolean publish) {
		boolean exists = cmso.existsResource(resource, CmsResourceFilter.ALL);
		boolean change = false;
		Object cmsResource = null;

		try {
			CmsProject e = cmso.getRequestContext().getCurrentProject();
			if (e.getName().equals("Online")) {
				cmso.getRequestContext().setCurrentProject(cmso.readProject("Offline"));
				change = true;
			}

			byte[] byteContent = content.marshal();
			CmsFile cmsFile;
			if (exists) {
				cmso.lockResource(resource);
				cmsFile = cmso.readFile(resource);
				cmsFile.setContents(byteContent);
				cmso.writeFile(cmsFile);
				cmsFile = cmso.readFile(resource);
				content = CmsXmlContentFactory.unmarshal(cmso, cmsFile);
				cmsFile = content.getHandler().prepareForWrite(cmso, content, cmsFile);
				cmso.unlockResource(resource);
				if (publish) {
					OpenCms.getPublishManager().publishResource(cmso, resource);
				}

				cmsResource = cmsFile;
			} else {
				cmsResource = cmso.createResource(resource, OpenCms.getResourceManager().getResourceType(typeName), byteContent, new ArrayList());
				cmsFile = cmso.readFile(resource);
				content = CmsXmlContentFactory.unmarshal(cmso, cmsFile);
				content.getHandler().prepareForWrite(cmso, content, cmsFile);
				cmso.unlockResource(resource);
				if (publish) {
					OpenCms.getPublishManager().publishResource(cmso, resource);
				}
			}

			if (change) {
				cmso.getRequestContext().setCurrentProject(e);
			}
		} catch (Exception var11) {
			var11.printStackTrace();
			LOG.error(var11.toString());
		}

		return (CmsResource) cmsResource;
	}

	protected CmsResource editResource(String resource, CmsXmlContent content) {
		return this.editResource((String) resource, (CmsXmlContent) content, true);
	}

	protected CmsResource editResource(String resource, CmsXmlContent content, boolean publish) {
		boolean exists = cmso.existsResource(resource, CmsResourceFilter.ALL);
		boolean change = false;
		CmsFile cmsResource = null;

		try {
			CmsProject e = cmso.getRequestContext().currentProject();
			if (e.getName().equals("Online")) {
				cmso.getRequestContext().setCurrentProject(cmso.readProject("Offline"));
				change = true;
			}

			byte[] byteContent = content.marshal();
			if (exists) {
				CmsFile cmsFile = cmso.readFile(resource);
				cmso.lockResource(resource);
				cmsFile.setContents(byteContent);
				cmso.writeFile(cmsFile);
				cmso.unlockResource(resource);
				if (publish) {
					OpenCms.getPublishManager().publishResource(cmso, resource);
				}

				cmsResource = cmsFile;
			}

			if (change) {
				cmso.getRequestContext().setCurrentProject(e);
			}
		} catch (Exception var10) {
			var10.printStackTrace();
			LOG.error(var10.toString());
		}

		return cmsResource;
	}

	@Deprecated
	public boolean editResource(Map data, String resource, int type, boolean publish) {
		CmsResource cmsResource = null;
		boolean success = true;

		try {
			/*get the schema*/
			//String schema = Schemas.getSchemaByType(type);

			/*Create the XmlContent associated to the new resource to access and manage the structured content */
			CmsFile cmsFile = cmso.readFile(resource);
			CmsXmlContent content = CmsXmlContentFactory.unmarshal(cmso, cmsFile);

			/*Get the locale*/
			Locale localizacion = cmso.getRequestContext().getLocale();

			/*Go through the MAP's list with all the data.*/
			Set keys = data.keySet();
			Iterator itKeys = keys.iterator();

			while (itKeys.hasNext()) {
				//The key is a field's name
				String key = (String) itKeys.next();
				//Value is the fild's value.
				Object value = data.get(key);

				/*
                 * Depending on the object's type an action is carried out:
                 * ArrayList = Meaning that there is more than one element of the same field.
                 * HashMap = Nested content
                 * String = Simple content
                 */

				if (value instanceof ArrayList) {
					manageMultipleContent((ArrayList) value, key, localizacion, content);
				} else if (value instanceof Map) {
					manageNestedContent((Map) value, key, localizacion, content);
				} else {
					manageSimpleContent(key, (String) value, localizacion, content);
				}
			}

			/*If everything went well, the resource will be edited or created*/
			try {
				I_CmsResourceType resType = OpenCms.getResourceManager().getResourceType(type);
				cmsResource = createOrEditResource(resource, resType.getTypeName(), content, publish);
			} catch (CmsLoaderException e) {
				e.printStackTrace();
			}

			if (cmsResource == null)
				success = false;
			else
				success = true;

		} catch (Exception exc) {
			exc.printStackTrace();
			LOG.error(exc.toString());
			success = false;
		}
		return success;
	}

	/**
	 * Deprecado en version 1.2: usar saveCmsResource
	 * This method edits any fields a resource existing one, and sets it's content according to the info passed by the HashMap data.
	 *
	 * @param data     - Data associated to the resource's content
	 * @param resource - Resources path+name
	 */
	@Deprecated
	public boolean editResource(Map data, String resource) {
		return editResource(data, resource, true);
	}

	/**
	 * Deprecado en version 1.2: usar saveCmsResource
	 *
	 * @param data
	 * @param resource
	 * @param publish
	 * @return
	 */
	@Deprecated
	public boolean editResource(Map data, String resource, boolean publish) {
		CmsResource cmsResource = null;
		boolean success = true;

		try {
			/*get the schema*/
			//String schema = Schemas.getSchemaByType(type);

			/*Create the XmlContent associated to the new resource to access and manage the structured content */
			CmsFile cmsFile = cmso.readFile(resource);
			CmsXmlContent content = CmsXmlContentFactory.unmarshal(cmso, cmsFile);

			/*Get the locale*/
			Locale localizacion = cmso.getRequestContext().getLocale();

			/*Go through the MAP's list with all the data.*/
			Set keys = data.keySet();
			Iterator itKeys = keys.iterator();

			while (itKeys.hasNext()) {
				//The key is a field's name
				String key = (String) itKeys.next();
				//Value is the fild's value.
				Object value = data.get(key);

				/*
                 * Depending on the object's type an action is carried out:
                 * ArrayList = Meaning that there is more than one element of the same field.
                 * HashMap = Nested content
                 * String = Simple content
                 */

				if (value instanceof ArrayList) {
					manageMultipleContent((ArrayList) value, key, localizacion, content);
				} else if (value instanceof Map) {
					manageNestedContent((Map) value, key, localizacion, content);
				} else {
					manageSimpleContent(key, (String) value, localizacion, content);
				}
			}

			/*If everything went well, the resource will be edited or created*/
			cmsResource = editResource(resource, content, publish);
			if (cmsResource == null)
				success = false;
			else
				success = true;

		} catch (Exception exc) {
			exc.printStackTrace();
			LOG.error(exc.toString());
			success = false;
		}
		return success;
	}

	protected boolean manageMultipleContent(List listaValores, String key, Locale localizacion, CmsXmlContent content) {
		boolean modified = false;
		if (listaValores != null && listaValores.size() > 0) {
			int i = 0;
			I_CmsXmlContentValue contentValue = null;
			Object contentValueInterno = null;
			if (content.hasValue(key, localizacion)) {
				contentValue = content.getValue(key, localizacion);
				int itList = contentValue.getMaxIndex();
				Iterator map2;
				HashMap map21;
				if (listaValores.size() == itList) {
					if (listaValores.get(0) instanceof HashMap) {
						for (map2 = listaValores.iterator(); map2.hasNext(); ++i) {
							map21 = (HashMap) map2.next();
							modified = this.manageNestedContent(map21, key, localizacion, content, i) || modified;
						}
					} else {
						while (i < listaValores.size()) {
							modified = this.manageSimpleContent(key, (String) listaValores.get(i), localizacion, content, i) || modified;
							++i;
						}
					}
				} else {
					for (int var13 = itList - 1; var13 > 0; --var13) {
						content.removeValue(key, localizacion, var13);
					}

					if (listaValores.get(0) instanceof HashMap) {
						for (map2 = listaValores.iterator(); map2.hasNext(); ++i) {
							map21 = (HashMap) map2.next();
							this.manageNestedContent(map21, key, localizacion, content, i);
						}
					} else {
						while (i < listaValores.size()) {
							this.manageSimpleContent(key, (String) listaValores.get(i), localizacion, content, i);
							++i;
						}
					}

					modified = true;
				}
			} else {
				if (listaValores.get(0) instanceof HashMap) {
					for (Iterator var12 = listaValores.iterator(); var12.hasNext(); ++i) {
						HashMap var14 = (HashMap) var12.next();
						this.manageNestedContent(var14, key, localizacion, content, i);
					}
				} else {
					while (i < listaValores.size()) {
						this.manageSimpleContent(key, (String) listaValores.get(i), localizacion, content, i);
						++i;
					}
				}

				modified = true;
			}
		}

		return modified;
	}

	public boolean manageChoiceContent(List<HashMap> listaValores, String key, Locale localizacion, CmsXmlContent content) {
		boolean modified = false;
		if (listaValores != null && listaValores.size() > 0) {
			boolean var19 = false;
			I_CmsXmlContentValue var20 = null;
			Object var21 = null;
			boolean borrarYCrear = false;
			Iterator contadorMap1;
			HashMap c;
			Iterator it;
			HashMap var23;
			if (content.hasValue(key, localizacion)) {
				var20 = content.getValue(key, localizacion);
				int xPath = var20.getMaxIndex();
				int contadorMap;
				String currentCont;
				if (xPath == listaValores.size()) {
					contadorMap = 1;

					for (contadorMap1 = listaValores.iterator(); contadorMap1.hasNext(); ++contadorMap) {
						c = (HashMap) contadorMap1.next();
						it = c.keySet().iterator();

						while (it.hasNext()) {
							Object key2 = it.next();
							currentCont = key + "[" + contadorMap + "]";
							List valor2 = content.getSubValues(currentCont, localizacion);
							if (valor2.size() > 0 && !("" + key2).equals(((I_CmsXmlContentValue) valor2.get(0)).getName())) {
								borrarYCrear = true;
							}
						}
					}
				} else {
					borrarYCrear = true;
				}

				Object valor21;
				Iterator var27;
				HashMap var28;
				Iterator var29;
				Integer var32;
				if (borrarYCrear) {
					for (contadorMap = xPath - 1; contadorMap >= 0; --contadorMap) {
						content.removeValue(key, localizacion, contadorMap);
					}

					var23 = new HashMap();
					var20 = content.addValue(cmso, key, localizacion, 0);
					String var24 = var20.getPath() + "/";
					var27 = listaValores.iterator();

					while (var27.hasNext()) {
						var28 = (HashMap) var27.next();
						var29 = var28.keySet().iterator();

						while (var29.hasNext()) {
							currentCont = (String) var29.next();
							var32 = Integer.valueOf(1);
							if (var23.containsKey(currentCont)) {
								var32 = (Integer) var23.get(currentCont);
								var32 = Integer.valueOf(var32.intValue() + 1);
								var23.put(currentCont, var32);
							} else {
								var23.put(currentCont, Integer.valueOf(1));
							}

							valor21 = var28.get(currentCont);
							if (valor21 instanceof ArrayList) {
								this.manageMultipleContent((ArrayList) valor21, var24 + currentCont + "[" + var32 + "]", localizacion, content);
							} else if (valor21 instanceof HashMap) {
								this.manageNestedContent((HashMap) valor21, var24 + currentCont, localizacion, content, var32.intValue() - 1);
							} else if (valor21 instanceof Choice) {
								this.manageChoiceContent(((Choice) valor21).getSubfields(), var24 + currentCont + "[" + var32 + "]", localizacion, content);
							} else if (valor21 instanceof String) {
								this.manageSimpleContent(var24 + currentCont, (String) valor21, localizacion, content, var32.intValue() - 1);
							}
						}
					}

					modified = true;
				} else {
					String var25 = key + "[1]" + "/";
					HashMap var26 = new HashMap();
					var27 = listaValores.iterator();

					while (var27.hasNext()) {
						var28 = (HashMap) var27.next();
						var29 = var28.keySet().iterator();

						while (var29.hasNext()) {
							currentCont = (String) var29.next();
							var32 = Integer.valueOf(1);
							if (var26.containsKey(currentCont)) {
								var32 = (Integer) var26.get(currentCont);
								var32 = Integer.valueOf(var32.intValue() + 1);
								var26.put(currentCont, var32);
							} else {
								var26.put(currentCont, Integer.valueOf(1));
							}

							valor21 = var28.get(currentCont);
							if (valor21 instanceof ArrayList) {
								this.manageMultipleContent((ArrayList) valor21, var25 + currentCont + "[" + var32 + "]", localizacion, content);
							} else if (valor21 instanceof HashMap) {
								this.manageNestedContent((HashMap) valor21, var25 + currentCont, localizacion, content, var32.intValue() - 1);
							} else if (valor21 instanceof Choice) {
								this.manageChoiceContent(((Choice) valor21).getSubfields(), var25 + currentCont + "[" + var32 + "]", localizacion, content);
							} else if (valor21 instanceof String) {
								this.manageSimpleContent(var25 + currentCont, (String) valor21, localizacion, content, var32.intValue() - 1);
							}
						}
					}
				}
			} else {
				var20 = content.addValue(cmso, key, localizacion, 0);
				String var22 = var20.getPath() + "/";
				var23 = new HashMap();
				contadorMap1 = listaValores.iterator();

				while (contadorMap1.hasNext()) {
					c = (HashMap) contadorMap1.next();
					it = c.keySet().iterator();

					while (it.hasNext()) {
						String var30 = (String) it.next();
						Integer var31 = Integer.valueOf(1);
						if (var23.containsKey(var30)) {
							var31 = (Integer) var23.get(var30);
							var31 = Integer.valueOf(var31.intValue() + 1);
							var23.put(var30, var31);
						} else {
							var23.put(var30, Integer.valueOf(1));
						}

						Object var33 = c.get(var30);
						if (var33 instanceof ArrayList) {
							this.manageMultipleContent((ArrayList) var33, var22 + var30 + "[" + var31 + "]", localizacion, content);
						} else if (var33 instanceof HashMap) {
							this.manageNestedContent((HashMap) var33, var22 + var30, localizacion, content, var31.intValue() - 1);
						} else if (var33 instanceof Choice) {
							this.manageChoiceContent(((Choice) var33).getSubfields(), var22 + var30 + "[" + var31 + "]", localizacion, content);
						} else if (var33 instanceof String) {
							this.manageSimpleContent(var22 + var30, (String) var33, localizacion, content, var31.intValue() - 1);
						}
					}
				}

				modified = true;
			}
		} else {
			if (content.hasValue(key, localizacion, 0)) {
				I_CmsXmlContentValue contentValue = content.getValue(key, localizacion);
				int numElementos = contentValue.getMaxIndex();

				for (int j = numElementos - 1; j >= contentValue.getMinOccurs(); --j) {
					content.removeValue(key, localizacion, j);
				}
			}

			modified = true;
		}

		return modified;
	}

	public boolean manageNestedContent(Map map2, String key, Locale localizacion, CmsXmlContent content) {
		return this.manageNestedContent(map2, key, localizacion, content, 0);
	}

	public boolean manageNestedContent(Map map2, String key, Locale localizacion, CmsXmlContent content, int i) {
		boolean modified = false;
		Object contentValueInterno = null;
		I_CmsXmlContentValue contentValue = null;
		if (!content.hasValue(key, localizacion, i)) {
			contentValue = content.addValue(cmso, key, localizacion, i);
			modified = true;
		} else {
			contentValue = content.getValue(key, localizacion, i);
		}

		String xPath = contentValue.getPath() + "/";
		Set keys2 = map2.keySet();
		Iterator itKeys2 = keys2.iterator();

		while (itKeys2.hasNext()) {
			String key2 = (String) itKeys2.next();
			Object valor2 = map2.get(key2);
			if (valor2 instanceof ArrayList) {
				modified = this.manageMultipleContent((ArrayList) valor2, xPath + key2, localizacion, content) || modified;
			} else if (valor2 instanceof HashMap) {
				modified = this.manageNestedContent((HashMap) valor2, xPath + key2, localizacion, content) || modified;
			} else if (valor2 instanceof Choice) {
				modified = this.manageChoiceContent(((Choice) valor2).getSubfields(), xPath + key, localizacion, content) || modified;
			} else {
				modified = this.manageSimpleContent(xPath + key2, (String) valor2, localizacion, content) || modified;
			}
		}

		return modified;
	}

	public boolean manageSimpleContent(String key, String valor, Locale localizacion, CmsXmlContent content) {
		return this.manageSimpleContent(key, valor, localizacion, content, 0);
	}

	public boolean manageSimpleContent(String key, String valor, Locale localizacion, CmsXmlContent content, int i) {
		boolean modified = false;

		try {
			if (mode.compareTo(Mode.MODE_UPDATE_NON_EMPTY) == 0) {

				modified = updateNotEmpty(key, valor, localizacion, content, i);
			} else if (mode.compareTo(Mode.MODE_UPDATE_NON_NULL) == 0) {
				modified = updateNotNull(key, valor, localizacion, content, i);
			} else if (mode.compareTo(Mode.MODE_REMOVE_WHEN_NULL) == 0) {
				modified = updateRmWhenNull(key, valor, localizacion, content, i);
			} else {
				throw new UtilException("Mode " + mode + " is not defined");
			}
		} catch (UtilException e) {
			LOG.error(e);
		}
		return modified;
	}

	private boolean updateRmWhenNull(String key, String valor, Locale localizacion, CmsXmlContent content, int i) {
		boolean modified = false;
		I_CmsXmlContentValue contentValue = null;

		if (content.hasValue(key, localizacion, i) && valor != null) {
			contentValue = content.getValue(key, localizacion, i);
			if (!valor.equals(contentValue.getStringValue(cmso))) {
				contentValue.setStringValue(cmso, valor);
				modified = true;
			}
		} else if (content.hasValue(key, localizacion, i) && valor == null) {
			content.removeValue(key, localizacion, i);
			modified = true;
		} else if (valor != null) {
			contentValue = content.addValue(cmso, key, localizacion, i);
			contentValue.setStringValue(cmso, valor);
			modified = true;
		}
		return modified;
	}

	private boolean updateNotNull(String key, String valor, Locale localizacion, CmsXmlContent content, int i) {
		boolean modified = false;
		I_CmsXmlContentValue contentValue = null;

		if (valor != null) {
			if (content.hasValue(key, localizacion, i)) {
				contentValue = content.getValue(key, localizacion, i);
				if (!valor.equals(contentValue.getStringValue(cmso))) {
					contentValue.setStringValue(cmso, valor);
				}
			} else {
				contentValue = content.addValue(cmso, key, localizacion, i);
				contentValue.setStringValue(cmso, valor);
			}
			modified = true;
		}
		return modified;
	}

	/**
	 * Update resource field if new value is not empty
	 *
	 * @param key
	 * @param valor
	 * @param localizacion
	 * @param content
	 * @param i
	 * @return
	 */
	public boolean updateNotEmpty(String key, String valor, Locale localizacion, CmsXmlContent content, int i) {
		boolean modified = false;
		I_CmsXmlContentValue contentValue = null;

		if (!StringUtils.isEmpty(valor)) {
			if (content.hasValue(key, localizacion, i)) {
				contentValue = content.getValue(key, localizacion, i);
				if (!valor.equals(contentValue.getStringValue(cmso))) {
					contentValue.setStringValue(cmso, valor);
				}
			} else {
				contentValue = content.addValue(cmso, key, localizacion, i);
				contentValue.setStringValue(cmso, valor);
			}
			modified = true;
		}
		return modified;
	}

	public class Choice {
		private String fieldName;
		private List subfields;

		public Choice(String fieldName, List subfields) {
			this.fieldName = fieldName;
			this.subfields = subfields;
		}

		public Choice(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return this.fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public List getSubfields() {
			return this.subfields;
		}

		public void setSubfields(List subfields) {
			this.subfields = subfields;
		}
	}

	public boolean copyToLocale(String resource, Locale fromLocale, Locale toLocales) {
		return this.copyToLocale(resource, fromLocale, (Locale) toLocales, true);
	}

	public boolean copyToLocale(String resource, Locale fromLocale, Locale toLocales, boolean publicar) {
		ArrayList l = new ArrayList();
		l.add(toLocales);
		return this.copyToLocale(resource, fromLocale, (List) l, publicar);
	}

	public boolean copyToLocale(String resource, Locale fromLocale, List<Locale> toLocales) {
		return this.copyToLocale(resource, fromLocale, (List) toLocales, true);
	}

	public boolean copyToLocale(String ruta, Locale fromLocale, List<Locale> toLocales, boolean publicar) {
		boolean b = true;

		try {
			CmsResource e = cmso.readResource(ruta);
			cmso.lockResource(ruta);
			CmsFile file = cmso.readFile(e);
			CmsXmlContent content = CmsXmlContentFactory.unmarshal(cmso, file);

			// Por cada locale de destino copiamos el de origen
			for (int i = 0; i < toLocales.size(); i++) {
				Locale to = toLocales.get(i);
				if (!to.equals(fromLocale)) {
					if (content.hasLocale(to)) {
						content.removeLocale(to);
					}
					content.copyLocale(fromLocale, to);
				}
			}

			// Guardamos los cambios
			String decodedContent1 = content.toString();
			file.setContents(decodedContent1.getBytes(content.getEncoding()));
			cmso.writeFile(file);
			cmso.unlockResource(ruta);
			if (publicar) {
				OpenCms.getPublishManager().publishResource(cmso, ruta);
			}
		} catch (UnsupportedEncodingException var11) {
			b = false;
			var11.printStackTrace();
			LOG.error("Error copiando de un idioma a otro");
		} catch (CmsException var12) {
			b = false;
			var12.printStackTrace();
			LOG.error("Error copiando de un idioma a otro");
		} catch (Exception var13) {
			b = false;
			LOG.error("Error copiando de un idioma a otro");
			var13.printStackTrace();
		}

		return b;
	}
}