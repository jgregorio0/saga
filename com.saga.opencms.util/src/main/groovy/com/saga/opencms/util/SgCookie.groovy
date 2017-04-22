package com.saga.opencms.util

import org.apache.commons.logging.Log
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie
import org.opencms.main.CmsLog

import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SgCookie {
	
	private static final Log LOG = CmsLog.getLog(SgCookie.class);

	public SgCookie(){
	}

	/**
	 * Init cookie store with cookie given in request and identified by name
	 * @param request
	 * @param cName
     * @return
     */
	public BasicCookieStore initCookieStore(HttpServletRequest request, String cName){
		BasicCookieStore cookieStore = null;
		try {
			BasicClientCookie cookie = findCookie(request, cName);
			if (cookie != null) {
				cookieStore = addCookie(cookieStore, cookie);
				LOG.debug("incluimos cookie " + cName + ": " + cookie.getValue());
			} else {
				cookieStore = new BasicCookieStore();
				LOG.debug("no se ha encontrado cookie");
			}
		} catch (Exception e) {
			LOG.error("error inicializando cookies store", e);
		}
		return cookieStore;
	}

	/**
	 * Init cookies store with all cookies from request
	 * @param request
	 * @return
     */
	public BasicCookieStore initAllCookiesIntoStore(HttpServletRequest request){
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

	/**
	 * Find cookie from request
	 * @param request
	 * @param name
     * @return
     */
	private Cookie findCookie(HttpServletRequest request, String name) {
		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();
		boolean found = false;
		for (int i = 0; cookies != null && i < cookies.length && !found; i++) {
			Cookie iCookie = cookies[i];
			if (name.equals(iCookie.getName())) {
				cookie = iCookie;
				LOG.debug("encontramos cookie " + iCookie.getName());
			}
		}
		return cookie;
	}

	/**
	 * Expirate cookie
	 * @param request
	 * @param response
	 * @param name
	 * @throws IOException
     */
	private void expirateCookie(
			HttpServletRequest request, HttpServletResponse response, String name) throws IOException {
		Cookie cookie = findCookie(request, name);
		if (cookie != null) {
			expirateCookie(response, cookie);
		}
	}

	/**
	 * Expirate cookie
	 * @param response
	 * @param cookie
	 * @throws IOException
     */
	private void expirateCookie(HttpServletResponse response, Cookie cookie) throws IOException {
		cookie.setValue("");
		cookie.setMaxAge(1);
		cookie.setDomain(cookie.getDomain());
		cookie.setPath(cookie.getPath());
		response.addCookie(cookie);
	}
	


	/**
	 * Store given cookie
	 * @param cookieStore
	 * @param cookie
     * @return
     */
	private BasicCookieStore addCookie(BasicCookieStore cookieStore, Cookie cookie) {
		if (cookieStore == null) {
			cookieStore = new BasicCookieStore();
		}
		cookieStore.addCookie(cookie);
		return cookieStore;
	}

	/**
	 * Create and store cookie
	 * @param cookieStore
	 * @param cName
	 * @param cValue
	 * @param cDomain
	 * @param cPath
     * @return
     */
	private BasicCookieStore addCookie(
			@Nullable BasicCookieStore cookieStore,
			String cName, String cValue, String cDomain, String cPath) {
		if (cookieStore == null) {
			cookieStore = new BasicCookieStore();
		}
		BasicClientCookie newCookie = new BasicClientCookie(cName, cValue);
		newCookie.setDomain(cDomain);
		newCookie.setPath(cPath);
		cookieStore.addCookie(newCookie);
		return cookieStore;
	}
}