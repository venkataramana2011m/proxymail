package proxymail.core.cqcomponents;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import proxymail.core.cqcomponents.FetchGroupListBean;

public class FetchUserSelectedUserGroupList extends WCMUsePojo {

	private FetchGroupListBean fetchGroupListBean = null;
	private ResourceResolver resourceResolver;

	/** The log. */
	private static final Logger log = LoggerFactory.getLogger(FetchUserSelectedUserGroupList.class);

	private final Map<String, String> groups = new LinkedHashMap<String, String>();

	private Session session;

	@Override
	public void activate() throws Exception {
		try {
			log.info("----------< Processing starts >----------");
			fetchGroupListBean = new FetchGroupListBean();

			String completeddate = getProperties().get("researchcompletiondate", "");

			fetchGroupListBean.setGroup(getGroupName());
			fetchGroupListBean.setResearchCompletedDate(completeddate);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	public FetchGroupListBean getFetchGroupListBean() {
		return this.fetchGroupListBean;
	}

	public String getGroupName() throws Exception{
		try {

			resourceResolver = getResourceResolver();

			session = resourceResolver.adaptTo(Session.class);

			UserManager userManager = ((JackrabbitSession) session).getUserManager();

			Iterator<Authorizable> groupIterator = userManager.findAuthorizables("jcr:primaryType", "rep:Group");
			String selectedgroupId = getProperties().get("group", "");
			
			String groupName = null;
			while (groupIterator.hasNext()) {
				log.info("Getting group");
				Authorizable group = groupIterator.next();
				if (group.isGroup() && group.getID().equals(selectedgroupId)) {
					log.info("Group found {}", group.getID());
					groupName = group.getProperty("./profile/givenName") != null
							? group.getProperty("./profile/givenName")[0].getString()
							: group.getPrincipal().getName().toString();
				}
			}
			return groupName;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

}
