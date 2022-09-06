package com.aio.generator.enums;

public enum ReferenceValue {

	WBC("WBC (10^3/uL)", "4.00 - 10.00"),
	NEU_NO("Neu# (10^3/uL)",  "2.00 - 7.00"),
	LYM_NO("Lym# (10^3/uL)", "0.80 - 4.00"),
	MON_NO("Mon# (10^3/uL)", "0.12 - 1.20"),
	EOS_NO("Eos# (10^3/uL)", "0.02 - 0.50"),
	BAS_NO("Bas# (10^3/uL)", "0.00 - 0.10"),
	NEU_PER("Neu% (%)", "50.0 - 70.0"),
	LYM_PER("Lym% (%)", "20.0 - 40.0"),
	MON_PER("Mon% (%)", "3.0 - 12.0"),
	EOS_PER("Eos% (%)", "0.5 - 5.0"),
	BAS_PER("Bas% (%)", "0.0 - 1.0"),
	RBC("RBC (10^6/uL)", "Male: 4.00 - 5.50 Female: 3.50 - 5.00"),
	HGB("HGB (g/dL)", "Male: 12.0 - 16.0 Female: 11.0 - 15.0"),
	HCT("HCT (%)", "Male : 40.0 - 54.0 Female: 37.0 - 47.0"),
	MCV("MCV (fL)", "80.0 - 100.0"),
	MCH("MCH (pg)", "27.0 - 34.0"),
	MCHC("MCHC (g/dL)", "32.0 - 36.0"),
	RDW_CV("RDW-CV (%)", "11.0 - 16.0"),
	RDW_SD("RDW-SD (fL)", "35.0 - 56.0"),
	PLT("PLT (10^3/uL)", "150 - 450"),
	MPV("MPV (fL)", "6.5 - 12.0"),
	PDW("PDW ( )", "9.0 - 17.0"),
	PCT("PCT (mL/L)", "1.08 - 2.82"),
	P_LCC("P-LCC (10^3/uL)", "30 - 90"),
	P_LCR("P-LCR (%)", "11.0 - 45.0");

	private String fieldName;
	private String referenceValues;
	
	private ReferenceValue(String fieldName, String referenceValues) {
		this.fieldName = fieldName;
		this.referenceValues = referenceValues;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getReferenceValues() {
		return referenceValues;
	}

}
