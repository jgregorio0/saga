package com.saga.opencms.util

import org.opencms.i18n.CmsMessages


public class SgMsgs {

	CmsMessages messages;
	String bundle;
	Locale locale;

	SgMsgs(String bundle, String locale){
		this(bundle, new Locale(locale))
	}

	SgMsgs(String bundle, Locale locale){
		this.bundle = bundle;
		this.locale = locale;
	}

	String key(String key){
		messages.key(key);
	}
}