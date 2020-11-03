package proxymail.core.workflows;

import java.util.List;

import javax.jcr.Session;

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

@Component(service = WorkflowProcess.class, property = { "process.label=Good Process" })
public class ApprovalStep implements WorkflowProcess {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private proxymail.core.workflows.services.ModNode theNode;

	@Override
	public void execute(WorkItem item, WorkflowSession wfsession, MetaDataMap args) throws WorkflowException {
		try {

			log.info("We have entered into the Execute Method of Approval Step of the Workflow .............. !!!!!!! ");

			WorkflowNode myNode = item.getNode();

			// Get the title from the Workflow Node
			// returns the title of the workflow step
			String title = myNode.getTitle();
			log.info("Retriving the title from the WorkflowNode :::: " + title);

			// Get the Workflow data from the workflow item
			// In other words gain access to the payload data
			WorkflowData wfData = item.getWorkflowData();

			// Get the payload path from the workflowdata
			String path = wfData.getPayload().toString();
			log.info("Retriving the Payload path :::: " + path);

			// to fetch the filename from the payload path
			int index = path.lastIndexOf("/");
			String fileName = path.substring(index + 1);
			log.info("Retriving the File Name :::: " + fileName);

			String approver = getUserWhomApproved(item, wfsession);
			
			String[] approverDetails = approver.split(",");
			String approvedby = approverDetails[0];
			String status = approverDetails[1];
			
			log.info("**** This asset was accepted " +fileName +" and approved by "+approvedby);
			
			theNode.updateNode(path , approvedby , status);

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public String getUserWhomApproved(WorkItem item, WorkflowSession wfsession) {
		try {
			
			//get the history of the workflow item 
			List<HistoryItem> historyList = wfsession.getHistory(item.getWorkflow());
			
			//get the size of the history list
			int historyListSize = historyList.size();
			log.info("Retriving the size of HistoryItem from the workflow ..... !!!!!! " + historyListSize);
			
			HistoryItem lastItem = historyList.get(historyListSize - 1);
			
			String lastAction = lastItem.getAction();
			String lastUser = lastItem.getUserId();			
			return lastUser + "," + lastAction;			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "error  - no user";

	}

}
