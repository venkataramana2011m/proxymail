package proxymail.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;

import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.framework.Constants;

@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths="+ "/bin/invokeWF"
   })
public class WorkflowServlet extends SlingSafeMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2972956371711543133L;
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
    private WorkflowService workflowService;
	
	private Session session;
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "datawrite");
		ResourceResolver resolver = null;
		try {			
			
			resolver = resourceResolverFactory.getServiceResourceResolver(param);
            session = resolver.adaptTo(Session.class);
            
            log.info("Inside the doGet Method of a servlet !!!!!!!! ..............");
            
            String assetPath = request.getParameter("assetPath");
            String email = request.getParameter("email");
            log.info("fetching Asset Path from the front end :::: " + assetPath);
            log.info("fetching Email from the front end :::: " + email);
            
            WorkflowSession wfSession = workflowService.getWorkflowSession(session);
            
            String workflowName = "/var/workflow/models/approveasset";
            
            WorkflowModel wfModel = wfSession.getModel(workflowName);
            WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", assetPath);
            
            updateAssetProperties(assetPath, email);
            
            wfSession.startWorkflow(wfModel, wfData);
            
            response.getWriter().write("Servlet invoked");
            
            session.save(); 
            session.logout();
                        
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateAssetProperties(String assetPath, String email) {
		try {
			
			Node root = session.getRootNode();
			String metaDataPath = "/jcr:content/metadata"; 
			
			String newPath = assetPath.replaceFirst("/", "");
			
			String finalPath = newPath + metaDataPath;
			
			Node rootContent = root.getNode(finalPath);
			
			rootContent.setProperty("email", email);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
