package com.saga.opencms.scripts
import java.nio.file.Files
import java.nio.file.Paths

String textosPath = "C:\\Users\\jgregorio\\IdeaProjects\\UpoSedeElectronica\\resources\\packages\\upo-expedientes-lic-otros_2016-07-08\\perfil_contratante\\exp_licitacion\\otros_exp_licitacion"

Files.walk(Paths.get(textosPath)).forEach({ path ->
	String filename = path.getFileName()

	if (filename.equals("texto_libre.html")){
		String parent = path.getParent()
		String parent2 = path.getParent().getParent()
		String folderParent = parent.substring(parent.lastIndexOf("\\"))
		String id = folderParent.substring(folderParent.indexOf("_") + 1)
		String newpath = parent2 + "\\" + id + ".html"
		Files.move(path, Paths.get(newpath))
	}
})