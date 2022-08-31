package com.aio.generator.enums;

public enum PatientDetails {

	SAMPLE_ID("Sample ID"),
	FIRST_NAME("First Name"),
	LAST_NAME("Last Name"),
	DATE("Date"),
	PATIENT_ID("Patient ID"),
	GENDER("Gender"),
	REF_GROUP("Ref. group"),
	AGE("Age"),
	DRAW_DATE("Draw Date"),
	DRAW_TIME("Draw Time"),
	DELIVERY_DATE("Delivery Date"),
	DELIVERY_TIME("Delivery Time"),
	CLINICIAN("Clinician"),
	COMMENT("Comment");
	
	private String fieldName;

	private PatientDetails(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

}
