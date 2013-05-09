package org.pgist.wfengine;

import java.util.Date;
import java.util.List;

public class WorkflowAgendaItem {
	private Long workflowId = null;
	private int type;  
	private String status = "";
	private String description = "";
	private String title = "";
	private Long contextId = null;
	private Long activityId = null;
	private Date beginTime; 
	private Date endTime;
	private List pgameActivityList;
	 

	public WorkflowAgendaItem() {
			
	}
	public Long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
	public Long getActivityId() {
		return activityId;
	}
	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}
	
	public List getPgameActivityList() {
		return pgameActivityList;
	}
	public void setPgameActivityList(List pgameActivityList) {
		this.pgameActivityList = pgameActivityList;
	}
}
