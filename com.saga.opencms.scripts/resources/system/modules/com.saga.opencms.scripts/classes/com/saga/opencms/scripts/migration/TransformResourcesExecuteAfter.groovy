package com.saga.sagasuite.scriptgroovy.migration

import com.saga.sagasuite.scriptgroovy.util.SgLog
import com.saga.sagasuite.scripts.SgReportManager

public class TransformResourcesExecuteAfter {

	public void init(def cmso, def idProceso, def jsonCnf) {

		/******* PROCESO ********/
		SgLog log = new SgLog(
				SgReportManager.getInstance(cmso),
				idProceso,
				cmso.getRequestContext().getCurrentUser().getName());
		log.init().add("hola desde ${this.getClass()}").print()
	}
}