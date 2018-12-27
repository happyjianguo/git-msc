package com.shyl.sys.service.realm;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.io.Serializable;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.subject.support.DefaultSubjectContext;

public class ShiroSessionMedicDAO extends EnterpriseCacheSessionDAO {
	
	private Cache cache;
	private String cacheName = "sessionCache";
	
	public ShiroSessionMedicDAO(CacheManager cacheManager) {
		cache = cacheManager.getCache(cacheName);
	}
	
	public void doUpdate(Session session) {
		System.out.println(session == null ? "null" : session.getId());
		if ((session == null) || (session.getId() == null)) {
			System.err.println("session argument cannot be null.");
		}
		cache.put(new Element("session_"+session.getId(), session));
	}

	public void doDelete(Session session) {
		if ((session == null) || (session.getId() == null)) {
			System.err.println("session argument cannot be null.");
		}
		cache.remove("session_"+session.getId());
	}

	public Serializable doCreate(Session session) {
		Serializable sessionId = generateSessionId(session);
		assignSessionId(session, sessionId);
		cache.put(new Element("session_"+sessionId, session));
		return sessionId;
	}

	public Session readSession(Serializable sessionId) throws UnknownSessionException {
		Session session = getCachedSession(sessionId);
		if ((session == null) || (session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null)) {
			session = doReadSession(sessionId);
			if (session == null) {
				throw new UnknownSessionException("There is no session with id [" + sessionId + "]");
			}

			cache(session, session.getId());
		}

		return session;
	}

	public Session doReadSession(Serializable id) {
	    if (id == null) {
	    	return null;
	    }
	    System.out.println("session id = " + id);
	    Element e = cache.get("session_" + id);
	    if (e != null) {
	    	Session session = (Session)e.getValue();
	    	return session;
	    }
	    return null;
	}
}