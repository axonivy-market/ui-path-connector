package com.axonivy.connector.uipath.enums;

public enum FilterProperty {
	KEY("Key"), PROCESS_KEY("ProcessKey"), LATEST_VERSION("IsLatestVersion"), NAME("Name"), PROCESS_TYPE("ProcessType"),
	ATTENDED("IsAttended"), COMPILED("IsCompiled"), JOB_PRIORITY("JobPriority"), ID("Id"), TAGS("Tags");

	private String key;

	private FilterProperty(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
