package com.saga.opencms.scripts

import org.opencms.configuration.CmsConfigurationManager
import org.opencms.file.CmsObject
import org.opencms.module.A_CmsModuleAction
import org.opencms.module.CmsModule
import org.opencms.module.I_CmsModuleAction

public class SgScriptModuleAction extends A_CmsModuleAction implements I_CmsModuleAction{

	public void initialize(CmsObject adminCms, CmsConfigurationManager configurationManager, CmsModule module){
		//Inicializamos la clase de Report
		SgReportManager report = new SgReportManager(adminCms);
	}

}
