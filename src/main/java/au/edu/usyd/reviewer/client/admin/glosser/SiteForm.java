package au.edu.usyd.reviewer.client.admin.glosser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SiteForm implements IsSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	private long id;
	private boolean autoHarvest = false;
	private long indexPeriod = 15000;
	private String referringUrl;
	private String title;
	private List<String> formTools = new LinkedList<String>();
	private Map<String, String> formMessages = new HashMap<String, String>();
	private String name;
	private String harvesterType;
	private String harvesterUsername;
	private String harvesterPassword;

	private HashMap<String, String> fieldANDvalue = new HashMap<String, String>();

	public SiteForm() {

	}

	public void addFieldValue(String field, String value) {
		fieldANDvalue.put(field, value);
	}

	public void clearFieldValue() {
		fieldANDvalue.clear();
	}

	public HashMap<String, String> getFieldANDvalue() {
		return fieldANDvalue;
	}

	public Map<String, String> getFormMessages() {
		return formMessages;
	}

	public List<String> getFormTools() {
		return formTools;
	}

	public String getHarvesterPassword() {
		return harvesterPassword;
	}

	public String getHarvesterType() {
		return harvesterType;
	}

	public String getHarvesterUsername() {
		return harvesterUsername;
	}

	public long getId() {
		return id;
	}

	public long getIndexPeriod() {
		return indexPeriod;
	}

	public String getName() {
		return name;
	}

	public String getReferringUrl() {
		return referringUrl;
	}

	public String getTitle() {
		return title;
	}

	public boolean isAutoHarvest() {
		return autoHarvest;
	}

	public void setAutoHarvest(boolean autoHarvest) {
		this.autoHarvest = autoHarvest;
	}

	public void setFormMessages(Map<String, String> formMessages) {
		this.formMessages = formMessages;
	}

	public void setFormTools(List<String> formTools) {
		this.formTools = formTools;
	}

	public void setHarvesterPassword(String harvesterPassword) {
		this.harvesterPassword = harvesterPassword;
	}

	public void setHarvesterType(String harvesterType) {
		this.harvesterType = harvesterType;
	}

	public void setHarvesterUsername(String harvesterUsername) {
		this.harvesterUsername = harvesterUsername;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setIndexPeriod(long indexPeriod) {
		this.indexPeriod = indexPeriod;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReferringUrl(String referringUrl) {
		this.referringUrl = referringUrl;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
