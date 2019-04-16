
<%@ page import="org.opencms.i18n.CmsMessages" %>
<%@ page import="com.github.mustachejava.DefaultMustacheFactory" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.Writer" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="com.github.mustachejava.MustacheFactory" %>
<%@ page import="com.github.mustachejava.Mustache" %>
<%@ page import="java.io.StringReader" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%

  String bundleName = "com.saga.sagasuite.textcontent.workplace";//Aquí el nombre del bundle que se desea convertir
  String locale = "en";

  CmsMessages bundle = new CmsMessages(bundleName, Locale.US);


  String template = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<XmlVfsBundles xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"opencms://system/modules/org.opencms.ade.config/schemas/xmlvfsbundle.xsd\">\n" +
    "  <Bundle language=\"{{locale}}\">\n" +
    "    {{#properties}}\n" +
    "    <Message>\n" +
    "      <Key><![CDATA[{{key}}]]></Key>\n" +
    " <Value><![CDATA[{{{value}}}]]></Value>\n" +
    "    </Message>\n" +
    "    {{/properties}}\n" +
    "  </Bundle>\n" +
    "</XmlVfsBundles>";

  Map<String, Object> ctx = new HashMap<String, Object>();
  ctx.put("locale", locale);

  final Set<String> keys = bundle.getResourceBundle().keySet();
  List<Map<String, String>> properties = new ArrayList<Map<String, String>>(keys.size());

  for (String key : keys) {
    Map<String, String> property = new HashMap<String, String>(2);
    property.put("key", key);
    property.put("value", bundle.key(key));
    properties.add(property);
  }
  ctx.put("properties", properties);


  Writer writer = new StringWriter();
  MustacheFactory mf = new DefaultMustacheFactory();
  Mustache mustache = mf.compile(new StringReader(template), "default");
  mustache.execute(writer, ctx);
  writer.flush();
  final String xmlbundle = writer.toString();

%>
<%=xmlbundle%>

