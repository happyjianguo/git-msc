package com.shyl.sys.service.realm;

import com.shyl.sys.entity.Organization;
import com.shyl.sys.entity.User;
import com.shyl.sys.service.IOrganizationService;
import com.shyl.sys.service.IPermissionService;
import com.shyl.sys.service.IResourceService;
import com.shyl.sys.service.IUserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;

@SuppressWarnings({"rawtypes","unchecked"})
public class ShiroMedicRealm extends AuthorizingRealm {

	@Resource
	private IUserService userService;

	@Resource
	private IPermissionService permissionService;

	@Resource
	private IResourceService resourceService;

	@Resource
	private IOrganizationService organizationService;
	@Resource(name="verifyCodeDAO")
	private VerifyCodeMedicDAO verifyCodeDAO;

	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		System.out.println("in AuthorizationInfo======");
		System.out.println("doGetAuthorizationInfo");
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}

		if (!SecurityUtils.getSubject().isAuthenticated()) {
			doClearCache(principals);
			SecurityUtils.getSubject().logout();
			return null;
		}

		Long id = (Long) principals.fromRealm(getName()).iterator().next();
		this.userService.clear();
		User user = (User) this.userService.getById("SZ",id);
		if (user != null) {
			List authorities = new ArrayList();
			List roles = new ArrayList();
			List list = new ArrayList();

			if (!user.getEmpId().equals("admin"))
				list = this.permissionService.getMyPermission(user.getProjectCode(), user.getId());
			else {
				list = this.resourceService.getAllForShiro(user.getProjectCode());
			}

			for (int i = 0; i < list.size(); i++) {
				Map m = (Map) list.get(i);
				if ((m.get("PERMCODE") != null) && (!((String) m.get("PERMCODE")).toString().equals(""))) {
					authorities.add(((String) m.get("PERMCODE")).toString());
				}

			}

			SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
			authorizationInfo.addStringPermissions(authorities);
			authorizationInfo.addRoles(roles);

			return authorizationInfo;
		}

		return null;
	}

	protected AuthenticationInfo doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken authcToken)
			throws AuthenticationException {
		/*System.out.println("in auth");
		AuthenticationToken token = (AuthenticationToken) authcToken;
		System.out.println("t===认证回调函=" + token.getUsername() + "." + String.valueOf(token.getPassword()));

		Subject currentUser = SecurityUtils.getSubject();
		Session session = null;
		if (null != currentUser) {
			session = currentUser.getSession();
		}
		if (session == null) {
			throw new IncorrectCredentialsException();
		}
		User user = new User();
		user.setEmpId(token.getUsername());
		user.setPwd(String.valueOf(token.getPassword()));
		if (token.getType() != null) {
			Organization org = new Organization();
			org.setOrgType(Integer.valueOf(token.getType()));
			user.setOrganization(org);
		}

		checkVerifyCode(session, token);

		User userWithoutPwd = checkEmpId(user);

		System.out.println("user ===");
		System.out.println(user.getEmpId());
		System.out.println(user.getPwd());
		System.out.println(user.getOrganization().getOrgType());
		user = this.userService.getLogin("",user);

		if (user == null) {
			Integer errTimes = userWithoutPwd.getErrTimes();
			errTimes = Integer.valueOf(errTimes == null ? 0 : errTimes.intValue());
			userWithoutPwd.setErrTimes(errTimes = Integer.valueOf(errTimes.intValue() + 1));
			if (errTimes.intValue() >= 3) {
				userWithoutPwd.setIsLocked(Integer.valueOf(1));
			}
			this.userService.update("SZ",userWithoutPwd);
			throw new UnknownAccountException();
		}

		if ((user.getIsLocked() != null) && (user.getIsLocked().equals(Integer.valueOf(1)))) {
			throw new LockedAccountException();
		}

		if ((user.getIsDisabled() != null) && (user.getIsDisabled().equals(Integer.valueOf(1)))) {
			throw new DisabledAccountException();
		}

		AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getId(), user.getPwd(), getName());
		session.setAttribute("currentUser", user);

		if ((user.getOrganization().getOrgType() != null) && (user.getOrganization() != null)) {
			Organization o = user.getOrganization();
			String orgname = o.getOrgName();
			session.setAttribute("orgName", orgname);
		}

		user.setErrTimes(Integer.valueOf(0));
		this.userService.update(user.getProjectCode(), user);

		String md5pwd = DigestUtils.md5Hex(user.getEmpId());
		if (md5pwd.equals(user.getPwd())) {
			session.setAttribute("needChgPwd", Integer.valueOf(1));
		}*/
		//return authcInfo;
		return null;
	}

	private User checkEmpId(User user) {
		User userWithoutPwd = this.userService.findByEmpId(user.getProjectCode(),user.getEmpId());
		if (userWithoutPwd == null) {
			throw new UnknownAccountException();
		}
		return userWithoutPwd;
	}

	/*private void checkVerifyCode(Session session, AuthenticationToken token) {
		if (!verifyCodeDAO.get(session.getId()).equals(token.getVcode()))
			throw new IncorrectCredentialsException();
	}*/


	public void removeUserCache(String userId) {
		SimplePrincipalCollection pc = new SimplePrincipalCollection();
		pc.add(userId, super.getName());
		super.clearCachedAuthorizationInfo(pc);
	}
}