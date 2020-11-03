package proxymail.core.workflows;

import java.util.List;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowNode;

@Component(service = WorkflowProcess.class, property = { "process.label=Deny Process" })
public class DenyStep implements WorkflowProcess {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
	private proxymail.core.workflows.services.ModNode theNode;

	@Override
	public void execute(WorkItem item, WorkflowSession wfSession, MetaDataMap args) throws WorkflowException {
		try {

			log.info(
					"We have entered into the Execute Method of Rejected Step of the Workflow .............. !!!!!!! ");

			WorkflowNode wfNode = item.getNode();

			// Get the title from the Workflow Node
			// returns the title of the workflow step
			String title = wfNode.getTitle();
			log.info("Retriving the title from the WorkflowNode :::: " + title);

			WorkflowData wfData = item.getWorkflowData();
			String path = wfData.getPayload().toString();
			log.info("Retriving the Payload path :::: " + path);

			// to fetch the filename from the payload path
			int index = path.lastIndexOf("/");
			String fileName = path.substring(index + 1);
			log.info("Retriving the File Name :::: " + fileName);
			
			String rejectedBy = getUserWhomRejected(item, wfSession);
			
			String[] rejectedDetails = rejectedBy.split(",");
			String rejectedUser = rejectedDetails[0];
			String status = rejectedDetails[1];
			
			
			log.info("**** This asset was accepted " +fileName +" and rejected by " + rejectedBy);
			
			theNode.updateNode(path, rejectedUser, status);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public String getUserWhomRejected(WorkItem item, WorkflowSession wfSession) {
		
		try {
			//Get the user who rejected the asset uploading permission
			List<HistoryItem> historyList = wfSession.getHistory(item.getWorkflow());
			int historyListSize = historyList.size();
			
			HistoryItem lastItem = historyList.get(historyListSize - 1);
			
			String lastUser = lastItem.getUserId();
			String lastAction = lastItem.getAction();
			return lastUser+","+lastAction;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "error  - no user";
		
	}

}
