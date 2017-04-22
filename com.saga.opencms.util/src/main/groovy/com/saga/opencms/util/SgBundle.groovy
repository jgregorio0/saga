package com.saga.opencms.util

import org.opencms.i18n.CmsMessages


public class SgBundle {

	CmsMessages messages;
	String bundle;
	Locale locale;

	SgBundle(String bundle, String locale){
		this(bundle, new Locale(locale))
	}

	SgBundle(String bundle, Locale locale){
		this.bundle = bundle;
		this.locale = locale;
	}

	String key(String key){
		messages.key(key);
	}
}