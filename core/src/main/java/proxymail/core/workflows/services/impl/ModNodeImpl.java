package proxymail.core.workflows.services.impl;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proxymail.core.workflows.services.ModNode;

@Component
public class ModNodeImpl implements ModNode {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	protected Session session;

	@Override
	public void updateNode(String path, String userId, String status) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "datawrite");
		ResourceResolver resolver = null;
		try {
			
			resolver = resourceResolverFactory.getServiceResourceResolver(param);
            session = resolver.adaptTo(Session.class);

			// Create a node that represents the root node
			Node root = session.getRootNode();
			String metaPath = path + "/jcr:content/metadata";

			log.info("**** ABOUT TO GET PATH");
			// Remove the first / char - JCR API does not like that
			String newPath = path.replaceFirst("/", "");

			String finalPath = newPath + metaPath;
			
			Node rootcontent = root.getNode(finalPath);
			
			String ttt = rootcontent.getPath();
			log.info("**** This meta path is " + ttt);
            
			
			/*
			 * if(status == "Approved") { }
			 */
			rootcontent.setProperty("xmpRights:UsageTerms", "");
			session.save(); 
            session.logout();
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
