package net.smartworks.model.hvm;

import net.smartworks.model.Condition;

public class HvmAttributeCond extends Condition {

	public static final String viewType_value = "value";
	public static final String viewType_activity = "activity";
	public static final String viewType_attribute = "attribute";
	
	private String id = null;
	private String prjId = null;
	private String valueId = null;
	private String valueName = null;
	private String sbpId = null;
	private String sbpName = null;
	private String activityId = null;
	private String activityName = null;
	private String attributeType = null;
	private String attributeName = null;
	
	//pss 프로젝트를 검색할때 attribute 도 같은 검색 condition 을 적용받지만 pssPrjName, sbpPrjName은 검색에서 빠져야 한다..그를 위한 플래그 
	private boolean searchByProject = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrjId() {
		return prjId;
	}

	public void setPrjId(String prjId) {
		this.prjId = prjId;
	}

	public String getValueId() {
		return valueId;
	}

	public void setValueId(String valueId) {
		this.valueId = valueId;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public String getSbpId() {
		return sbpId;
	}

	public void setSbpId(String sbpId) {
		this.sbpId = sbpId;
	}

	public String getSbpName() {
		return sbpName;
	}

	public void setSbpName(String sbpName) {
		this.sbpName = sbpName;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public boolean isSearchByProject() {
		return searchByProject;
	}

	public void setSearchByProject(boolean searchByProject) {
		this.searchByProject = searchByProject;
	}

	public static String getViewtypeValue() {
		return viewType_value;
	}

	public static String getViewtypeActivity() {
		return viewType_activity;
	}

	public static String getViewtypeAttribute() {
		return viewType_attribute;
	}
	
	
}
