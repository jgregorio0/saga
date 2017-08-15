package com.jesus.opencms.util

import com.saga.opencms.util.SgCntMap
import com.saga.opencms.util.SgSlurper

def xmlContent =
"""
<?xml version="1.0" encoding="UTF-8"?>

<UPOProfesors xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="opencms://system/modules/com.saga.upo.centros.frontend/schemas/upofichaprofesor.xsd">
  <UPOProfesor language="en">
    <Nombre><![CDATA[PEREZ-PRAT DURBAN, LUIS]]></Nombre>
    <Codigo/>
    <Despacho/>
    <Email/>
    <Telefono/>
    <Area><![CDATA[DERECHO INTERNACIONAL PUBLICO Y RELAC.INTERNACIONA]]></Area>
    <Departamento>
      <link type="WEAK">
        <target><![CDATA[/sites/upo-demo-facultad/.content/upodepartamento/dpu.xml]]></target>
        <uuid>2b378fe2-20e5-11e6-aef9-7fb253176922</uuid>
      </link>
    </Departamento>
    <CargoAcademico/>
    <HorariosTutorias name="HorariosTutorias0">
      <links/>
      <content/>
    </HorariosTutorias>
    <Teaser/>
    <Keywords/>
  </UPOProfesor>
  <UPOProfesor language="de">
    <Nombre><![CDATA[PEREZ-PRAT DURBAN, LUIS]]></Nombre>
    <Codigo/>
    <Despacho/>
    <Email/>
    <Telefono/>
    <Area><![CDATA[DERECHO INTERNACIONAL PUBLICO Y RELAC.INTERNACIONA]]></Area>
    <Departamento>
      <link type="WEAK">
        <target><![CDATA[/sites/upo-demo-facultad/.content/upodepartamento/dpu.xml]]></target>
        <uuid>2b378fe2-20e5-11e6-aef9-7fb253176922</uuid>
      </link>
    </Departamento>
    <CargoAcademico/>
    <HorariosTutorias name="HorariosTutorias0">
      <links/>
      <content/>
    </HorariosTutorias>
    <Teaser/>
    <Keywords/>
  </UPOProfesor>
  <UPOProfesor language="es">
    <Nombre><![CDATA[PEREZ-PRAT DURBAN, LUIS]]></Nombre>
    <Codigo/>
    <Despacho/>
    <Email/>
    <Telefono/>
    <Area><![CDATA[DERECHO INTERNACIONAL PUBLICO Y RELAC.INTERNACIONA]]></Area>
    <Departamento>
      <link type="WEAK">
        <target><![CDATA[/sites/upo-demo-facultad/.content/upodepartamento/dpu.xml]]></target>
        <uuid>2b378fe2-20e5-11e6-aef9-7fb253176922</uuid>
      </link>
    </Departamento>
    <CargoAcademico/>
    <HorariosTutorias name="HorariosTutorias0">
      <links/>
      <content/>
    </HorariosTutorias>
    <LinkInteres>
      <Href>
        <link internal="false" type="WEAK">
          <target><![CDATA[https://www.google.es]]></target>
        </link>
      </Href>
      <Title/>
    </LinkInteres>
    <LinkInteres>
      <Href>
        <link internal="false" type="WEAK">
          <target><![CDATA[https://www.wikipedia.es]]></target>
        </link>
      </Href>
      <Title/>
    </LinkInteres>
    <LinkInteres>
      <Href>
        <link internal="false" type="WEAK">
          <target><![CDATA[https://www.saga.es]]></target>
        </link>
      </Href>
      <Title/>
    </LinkInteres>
    <LinkInteres>
        Hola
    </LinkInteres>
    <Teaser/>
    <Keywords/>
  </UPOProfesor>
</UPOProfesors>
"""

String jsonStr = SgCntMap.toJson(xmlContent)
println jsonStr

// create content
SgSlurper sgSlurper = new SgSlurper(jsonStr);
def map = sgSlurper.slurpJson();