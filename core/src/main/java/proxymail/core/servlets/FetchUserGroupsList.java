package proxymail.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.crx.JcrConstants;

import proxymail.core.constants.AppConstants;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import javax.servlet.Servlet;

@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Dynamic Drop Down",
                "sling.servlet.resourceTypes=" + "/apps/dropDownLIsting"
        })
public class FetchUserGroupsList extends SlingSafeMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3981711599568014501L;

	/** The log. */
	private static final Logger log = LoggerFactory.getLogger(FetchUserGroupsList.class);

	private ResourceResolver resourceResolver;

	private List<Resource> groups;

	private Session session;

	private AppConstants appConstants;

	private ValueMap vm = null;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			log.info("----------< Processing starts >----------");
			resourceResolver = request.getResourceResolver();

			session = resourceResolver.adaptTo(Session.class);

			UserManager userManager = ((JackrabbitSession) session).getUserManager();

			request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

			Iterator<Authorizable> groupIterator = userManager.findAuthorizables(appConstants.JCR_PRIMARYTYPE,
					appConstants.REP_GROUP);

			groups = new ArrayList<Resource>();

			while (groupIterator.hasNext()) {

				vm = new ValueMapDecorator(new HashMap<String, Object>());

				log.info("Getting group");

				Authorizable group = groupIterator.next();

				if (group.isGroup()) {
					log.info("Group found {}", group.getID());
					vm.put("value", group.getID());
					vm.put("text", group.getID());
					//groups.add(group.getID());
					groups.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm));

				}
			}
			DataSource dataSource = new SimpleDataSource(groups.iterator());
			request.setAttribute(DataSource.class.getName(), dataSource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}
	
}
