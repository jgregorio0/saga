package com.saga.opencms.util

import com.mitchellbosecke.pebble.PebbleEngine
import com.mitchellbosecke.pebble.error.LoaderException
import com.mitchellbosecke.pebble.error.PebbleException
import com.mitchellbosecke.pebble.loader.Loader
import com.mitchellbosecke.pebble.template.PebbleTemplate
import com.mitchellbosecke.pebble.utils.PathUtils
import org.opencms.file.CmsObject
import org.opencms.file.CmsResource
import org.opencms.main.CmsException

/**
 * Based on rtinoco developments
 */
class SgPebble {

    private static PebbleEngine engine;
    private static PebbleEngine cmsEngine;

    public SgPebble(){
    }

    private PebbleEngine getEngine() {
        if (engine == null) {
            engine = new PebbleEngine.Builder().build();
        }
        return engine;
    }

    private PebbleEngine getCmsEngine(CmsObject cmso) {
        if (cmsEngine == null) {
            cmsEngine = new PebbleEngine.Builder().loader(new OpenCmsPebbleLoader(cmso)).build();
        }
        return cmsEngine;
    }

    /**
     *
     * @param templatePath
     * @param ctx
     * @return
     * @throws IOException
     * @throws PebbleException
     */
    public String process(String templatePath, Map<String, Object> ctx)
            throws IOException, PebbleException {
        PebbleTemplate compiledTemplate = getEngine().getTemplate(templatePath);
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, ctx);
        return writer.toString();
    }

    /**
     *
     * @param templatePath
     * @param ctx
     * @return
     * @throws IOException
     * @throws PebbleException
     */
    public String process(CmsObject cmso, String templatePath, Map<String, Object> ctx)
            throws IOException, PebbleException {
        PebbleTemplate compiledTemplate = getCmsEngine(cmso).getTemplate(templatePath);
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, ctx);
        return writer.toString();
    }

    /**
     * Implementación del cargador de plantillas para Pebble
     * que permite obtener las plantillas del VFS
     */
    public class OpenCmsPebbleLoader implements Loader<String> {

        /**
         * CmsObject para leer del VFS
         */
        private CmsObject cmsObject;

        /**
         * Prefijo de las plantillas. Por defecto es el site root
         */
        private String prefix;

        /**
         * Sufijo o extensión de las plantillas. Por defecto es .peb
         */
        private String suffix;

        /**
         * Charset. Por defecto UTF-8
         */
        private String charset;

        /**
         * Carácter de separación para rutas
         */
        private static final char separator = '/';

        public OpenCmsPebbleLoader(CmsObject cmsObject) {
            this.cmsObject = cmsObject;
            this.charset = "UTF-8";
            this.suffix = ".peb";
            this.prefix = cmsObject.getRequestContext().getSiteRoot();
        }

        @Override
        public Reader getReader(String cacheKey) throws LoaderException {
            Reader reader = null;
            LoaderException ex = null;
            try {
                String path = cmsObject.getRequestContext().removeSiteRoot(cacheKey);
                final CmsResource resource = cmsObject.readResource(path);
                if (!resource.isFile()) {
                    ex = new LoaderException(null, "NO ES UN FICHERO: '" + path + "'");
                } else {
                    final byte[] contents = cmsObject.readFile(resource).getContents();
                    reader = new StringReader(new String(contents, charset));
                }
            } catch (CmsException e) {
                ex = new LoaderException(null, "NO SE HA PODIDO OBTENER LA PLANTILLA: '" + cacheKey + "'");
            } catch (UnsupportedEncodingException e) {
            }
            if (ex != null) throw ex;
            return reader;
        }

        @Override
        public void setCharset(String charset) {
            this.charset = charset;
        }

        @Override
        public void setPrefix(String prefix) {
            // Do nothing
        }

        @Override
        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public String resolveRelativePath(String relativePath, String anchorPath) {
            return PathUtils.resolveRelativePath(relativePath, anchorPath, separator);
        }

        @Override
        public String createCacheKey(String templateName) {
            // Si no es relativo a un sitio o siéndolo el contexto del CmsObject está ajustado a la raíz, no incluimos
            // el prefijo, si el contexto del CmsObject está a un sitio incluimos la ruta del sitio
            if (cmsObject.getRequestContext().getAdjustedSiteRoot(templateName).equals("")) {
                return templateName + (suffix != null ? suffix : "");
            } else {
                return (prefix != null ? prefix + templateName : templateName) + (suffix != null ? suffix : "");
            }
        }
    }

}