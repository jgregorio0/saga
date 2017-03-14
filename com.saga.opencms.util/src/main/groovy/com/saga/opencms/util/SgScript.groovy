package com.saga.opencms.util

import org.opencms.file.CmsObject

public class SgScript {

	GroovyClassLoader groovyClassLoader;

	public SgScript(){

	}

	/**
	 * Invoca el método de un script groovy
	 * by: rtinoco
	 *
	 * @param cmso CmsObject
	 * @param path Ruta del VFS donde se encuentra el script
	 * @param method Método que llamar
	 * @param args Argumentos del método
	 * @return
	 */
	def invokeScript(
			CmsObject cmso, String path,
			String method, Object[] args) {
		def loader = instanceGroovyClassLoader()

		// Obtenmos el contenido del script
		byte[] scriptContent = cmso.readFile(
				cmso.readResource(path)).contents;
		Class aClass = loader.parseClass(
				new String(scriptContent, "UTF-8"));
		GroovyObject groovyObject =
				(GroovyObject) aClass.newInstance();
		groovyObject.invokeMethod(method, args);
	}

	/**
	 * Devuelve instancia de la clase GroovyClassLoader
	 * @return
	 */
	def instanceGroovyClassLoader() {
		if (!groovyClassLoader) {
			groovyClassLoader = new GroovyClassLoader(
					getClass().getClassLoader());
		}
		return groovyClassLoader
	}
}