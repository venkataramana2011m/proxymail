package proxymail.core.cqcomponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

public class UserGroupList extends WCMUsePojo {

	private ResourceResolver resourceResolver;

	/** The log. */
	private static final Logger log = LoggerFactory.getLogger(UserGroupList.class);

	private final Map<String, String> groups = new LinkedHashMap<String, String>();

	private Session session;

	@Override
	public void activate() throws Exception {
		try {
			log.info("----------< Processing starts >----------");

			resourceResolver = getResourceResolver();

			session = resourceResolver.adaptTo(Session.class);

			UserManager userManager = ((JackrabbitSession) session).getUserManager();

			Iterator<Authorizable> groupIterator = userManager.findAuthorizables("jcr:primaryType", "rep:Group");

			while (groupIterator.hasNext()) {

				log.info("Getting group");

				Authorizable group = groupIterator.next();

				if (group.isGroup()) {

					log.info("Group found {}", group.getID());
					
					String givenName=group.getProperty("./profile/givenName")!=null?group.getProperty("./profile/givenName")[0].getString():group.getPrincipal().getName().toString();
					
					groups.put(group.getID(), givenName);
					
				}
			}

			@SuppressWarnings("unchecked")
			DataSource ds = new SimpleDataSource(new TransformIterator(groups.keySet().iterator(), new Transformer() {

				@Override
				public Object transform(Object o) {
					String group = (String) o;

					ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());

					vm.put("value", group);
					vm.put("text", groups.get(group));

					return new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", vm);
				}
			}));

			this.getRequest().setAttribute(DataSource.class.getName(), ds);

		} catch (Exception e) {

			log.error(e.getMessage(), e);

		}

	}
}
