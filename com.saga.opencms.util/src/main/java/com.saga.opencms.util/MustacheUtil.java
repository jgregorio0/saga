package com.saga.opencms.util;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by jgregorio on 07/05/2015.
 */
public class MustacheUtil {

    private CmsObject cmso;

    public MustacheUtil(CmsObject cmso) {
        this.cmso = cmso;
    }

    /**
     * Obtenemos el contenido segun indica la plantilla Mustache
     * y el valor de los campos de nuestro contexto (ctx)
     * @param templatePath
     * @param ctx
     * @return
     * @throws CmsException
     * @throws java.io.UnsupportedEncodingException
     */
    public String process(String templatePath, Map<String, Object> ctx)
            throws CmsException, UnsupportedEncodingException {
        // Cargamos los contenidos de la plantilla
        byte[] contents = cmso.readFile(templatePath).getContents();
        String contentStr = new String(contents, "UTF-8");

        // Creamos el objeto Mustache con la plantilla y el contexto
        DefaultMustacheFactory defaultMustacheFactory = new DefaultMustacheFactory();
        StringWriter sw = new StringWriter();
        Mustache def = defaultMustacheFactory.compile(new StringReader(contentStr), "def");
        def.execute(sw, ctx);
        sw.flush();
        return sw.toString();
    }
}
