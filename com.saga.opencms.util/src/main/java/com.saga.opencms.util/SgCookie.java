package com.saga.opencms.util;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SgCookie {


	public SgCookie(){
	}


	public BasicCookieStore initCookieStore(HttpServletRequest request){
		BasicCookieStore cookieStore = null;
		try {
            Cookie[] cookies = request.getCookies();
            for (int i = 0; cookies != null && i < cookies.length; i++) {
                Cookie cookie = cookies[i];
				BasicClientCookie basicClientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
				cookieStore = addCookie(cookieStore, basicClientCookie);
            }
		} catch (Exception e) {
			LOG.error("error inicializando cookies store", e);
		}
		return cookieStore;
	}

	public BasicCookieStore initCookieStore(HttpServletRequest request, String cName){
		BasicCookieStore cookieStore = null;
		try {
			BasicClientCookie cookie = findCookie(request, cName);
			if (cookie != null) {
				cookieStore = addCookie(cookieStore, cookie);
				LOG.debug("incluimos cookie " + cName + ": " + cookieValue);
			} else {
				LOG.debug("no se ha encontrado cookie " + COOKIE_NAME);
			}
		} catch (Exception e) {
			LOG.error("error inicializando cookies store", e);
		}
		return cookieStore;
	}

	public BasicCookieStore initCookieStore(HttpServletRequest request, String cName, String cDomain, String cPath){
		BasicCookieStore cookieStore = null;
		try {
			BasicClientCookie cookie = findCookie(request, cName);
			if (cookie != null) {
				String cookieValue = cookie.getValue();
				cookieStore = addCookie(cookieStore, cName, cookieValue, cDomain, cPath);
				LOG.debug("incluimos cookie " + cName + ": " + cookieValue);
			} else {
				LOG.debug("no se ha encontrado cookie " + COOKIE_NAME);
			}
		} catch (Exception e) {
			LOG.error("error inicializando cookies store", e);
		}
		return cookieStore;
	}

	private BasicClientCookie findCookie(HttpServletRequest request, String name) {
		BasicClientCookie cookie = null;
		Cookie[] cookies = request.getCookies();
		boolean found = false;
		for (int i = 0; cookies != null && i < cookies.length && !found; i++) {
			Cookie iCookie = cookies[i];
			if (name.equals(iCookie.getName())) {
				cookie = new BasicClientCookie(iCookie.getName(), iCookie.getValue());
				LOG.debug("encontramos cookie y generamos " + cookie);
			}
		}
		return cookie;
	}


	private BasicCookieStore addCookie(
			BasicCookieStore cookieStore, String cookieName,
			String cookieVal, String domain, String path) {
		if (cookieStore == null) {
			cookieStore = new BasicCookieStore();
		}
		BasicClientCookie newCookie = new BasicClientCookie(cookieName, cookieVal);
		newCookie.setDomain(domain);
		newCookie.setPath(path);
		cookieStore.addCookie(newCookie);
		return cookieStore;
	}

	private BasicCookieStore addCookie(BasicCookieStore cookieStore, BasicClientCookie cookie) {
		if (cookieStore == null) {
			cookieStore = new BasicCookieStore();
		}
		cookieStore.addCookie(cookie);
		return cookieStore;
	}



	private void expirateCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie cookie = findCookie(request, name);
		if (cookie != null) {
			cookie.setValue(null);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}

}