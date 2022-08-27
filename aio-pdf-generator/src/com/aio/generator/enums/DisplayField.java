package com.aio.generator.enums;

public enum DisplayField {
	
	PATIENT_NAME("Patient Name"),
	AGE("Age"),
	GENDER("Gender"),
	SAMPLE_ID("Sample ID"),
	PATIENT_ID("Patient ID"),
	PATIENT_TYPE("Patient Type"),
	DATE("Date"),
	REF_GROUP("Ref. group"),
	COLLECTION_DATE("Collection Date Time"),
	REPORTING_DATE("Reporting Date Time"),
	CLINICIAN("Clinician"),
	OPERATOR("Operator");
	
	private String displayName;

	private DisplayField(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

}
