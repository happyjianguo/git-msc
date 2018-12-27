package com.shyl.sys.service.realm;

import java.io.Serializable;

import org.apache.shiro.session.Session;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.CacheManager;

public class VerifyCodeMedicDAO {

	private Cache cache;
	private String cacheName = "sessionCache";
	
	public VerifyCodeMedicDAO(CacheManager cacheManager) {
		cache = cacheManager.getCache(cacheName);
	}
	
	public void save(Serializable sessionId, String verifyCode) {
		Element e = cache.get("session_"+sessionId);
		System.out.println("session中设置验证码设置为"+verifyCode+":"+sessionId);
		if (e == null) {
			System.out.println("session为空");
		} else {
			Session session = (Session)e.getValue();
			session.setAttribute("verifyCode", verifyCode);
		}
	}

	public String get(Serializable sessionId) {
		Element e = cache.get("session_"+sessionId);
		System.out.println("取出session中二维码"+sessionId+":"+e);
		if (e == null) {
			System.out.println("session为空");
			return "";
		} else {
			Session session = (Session)e.getValue();
			System.out.println("取出session中二维码"+sessionId+":"+session.getAttribute("verifyCode"));
			return (String)session.getAttribute("verifyCode");
		}
	}
}
