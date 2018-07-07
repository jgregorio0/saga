<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<div>Usuario "/Admin" NO pertenece al grupo "/AbsorcioAtomicaFlama": ${cms:getCmsObject(request).userInGroup("/Admin", "/AbsorcioAtomicaFlama")}</div>
<div>Usuario "/l1224084" SI pertenece al grupo "/AbsorcioAtomicaFlama": ${cms:getCmsObject(request).userInGroup("/l1224084", "/AbsorcioAtomicaFlama")}</div>