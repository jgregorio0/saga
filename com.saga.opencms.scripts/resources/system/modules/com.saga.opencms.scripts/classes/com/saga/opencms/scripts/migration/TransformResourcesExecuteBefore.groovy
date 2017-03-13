package com.saga.opencms.scripts


public class TransformResourcesExecuteBefore {

	public void init(def cmso, def idProceso, def jsonCnf) {

		/******* PROCESO ********/
		SgLog log = new SgLog(
				SgReportManager.getInstance(cmso),
				idProceso,
				cmso.getRequestContext().getCurrentUser().getName());
		log.init().add("hola desde ${this.getClass()}").print()
	}
}