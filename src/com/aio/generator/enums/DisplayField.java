package com.aio.generator.enums;

public enum DisplayField {
	
	PATIENT_NAME("Patient Name"),
	AGE("Age"),
	GENDER("Gender"),
	PATIENT_ID("Patient ID"),
	REF_GROUP("Ref. group"),
	
	DATE("Date"),
	SAMPLE_ID("Sample ID"),
	COLLECTION_DATE("Collection Date"),
	REPORTING_DATE("Reporting Date"),
	CLINICIAN("Clinician");
	
	private String displayName;

	private DisplayField(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

}
