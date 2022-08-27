package com.aio.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import com.aio.generator.constants.CommonConstants;
import com.aio.generator.enums.DisplayField;
import com.aio.generator.enums.PatientDetails;
import com.aio.generator.enums.ReferenceValue;
import com.aio.generator.utils.CommonUtils;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import static com.aio.generator.constants.CommonConstants.COMMA;

public class PdfGenerator {
	
	public static void main(String[] args) {
		File[] files = new File(".").listFiles();
		String line;
		for (File file : files) {
		    if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) {
				try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
					boolean headingIndex = true;
					Map<String, Integer> heading = new HashMap<>();
					while ((line = bufferedReader.readLine()) != null) {
						if(headingIndex) {
							processHeadings(line, heading);
							headingIndex = false;
						} else {
							generatePdf(line.split(COMMA), heading);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}  
		    }
		}
	}

	private static void processHeadings(String line, Map<String, Integer> heading) {
		String[] headings = line.split(COMMA);
		for (int i = 0; i < headings.length; i++) {
			heading.put(CommonUtils.formatField(headings[i]), i);
		}
	}

	private static void generatePdf(String[] values, Map<String, Integer> heading) {
		String firstName = CommonUtils.formatField(values[heading.get(PatientDetails.FIRST_NAME.getFieldName())]);
		String lastName = CommonUtils.formatField(values[heading.get(PatientDetails.LAST_NAME.getFieldName())]);
		String sampleId = CommonUtils.formatField(values[heading.get(PatientDetails.SAMPLE_ID.getFieldName())]);
		String fileName = String.format("%s_%s%s.pdf", sampleId, firstName, lastName);
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(fileName));
			document.open();
			document.newPage();
			addRunningSections(document, CommonConstants.HEADER_PATH, CommonConstants.HEADER_POSITION);
			//addHeaderSpace(document);
			addInfoTable(values, heading, document, String.format("%s %s", firstName, lastName), sampleId);
			addValuesTable(values, heading, document);
			addCommentBox(document);
			addApproval(document);
			addRunningSections(document, CommonConstants.FOOTER_PATH, CommonConstants.FOOTER_POSITION);
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		} finally {
			document.close();
		}
	}

	private static void addHeaderSpace(Document document) throws DocumentException {
		document.add(Chunk.NEWLINE);
	}

	private static void addInfoTable(String[] values, Map<String, Integer> heading, Document document, String patientName, String sampleId) throws DocumentException {
		PdfPTable infoTable = new PdfPTable(4);
		infoTable.setWidthPercentage(90);
		infoTable.setSpacingBefore(0f);
		infoTable.setSpacingAfter(0f);
		infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		infoTable.setKeepTogether(true);
		
		formatAndAddCell(createValueCell(DisplayField.PATIENT_NAME.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(patientName), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.DATE.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.DATE.getFieldName())])), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.AGE.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.AGE.getFieldName())])), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.REF_GROUP.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.REF_GROUP.getFieldName())])), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.GENDER.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.GENDER.getFieldName())])), infoTable, Element.ALIGN_LEFT, false);
		
		String collectionDate = String.format("%s %s", CommonUtils.formatField(values[heading.get(PatientDetails.DRAW_DATE.getFieldName())]), CommonUtils.formatField(values[heading.get(PatientDetails.DRAW_TIME.getFieldName())]));
		formatAndAddCell(createValueCell(DisplayField.COLLECTION_DATE.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(collectionDate), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.SAMPLE_ID.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(sampleId), infoTable, Element.ALIGN_LEFT, false);
		
		String deliveryDate = String.format("%s %s", CommonUtils.formatField(values[heading.get(PatientDetails.DELIVERY_DATE.getFieldName())]), CommonUtils.formatField(values[heading.get(PatientDetails.DELIVERY_TIME.getFieldName())]));
		formatAndAddCell(createValueCell(DisplayField.REPORTING_DATE.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(deliveryDate), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.PATIENT_ID.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.PATIENT_ID.getFieldName())])), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.CLINICIAN.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.CLINICIAN.getFieldName())])), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.PATIENT_TYPE.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.PATIENT_TYPE.getFieldName())])), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.OPERATOR.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.OPERATOR.getFieldName())])), infoTable, Element.ALIGN_LEFT, false);
		
		Paragraph referenceTableParagraph = new Paragraph();
		referenceTableParagraph.setSpacingBefore(50f);
		infoTable.setSpacingAfter(0f);
		referenceTableParagraph.add(infoTable);
		document.add(referenceTableParagraph);
	}

	private static void addValuesTable(String[] values, Map<String, Integer> heading, Document document)
			throws DocumentException {
		PdfPTable resultTable = new PdfPTable(3);
		resultTable.setWidthPercentage(90);
		resultTable.setSpacingBefore(0f);
		resultTable.setSpacingAfter(0f);
		resultTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		resultTable.setKeepTogether(true);
		PdfPCell headingCell = createHeadingCell(CommonConstants.VALUES_TITLE);
		headingCell.setColspan(3);
		headingCell.setBackgroundColor(new BaseColor(204, 229, 255));
		formatAndAddCell(headingCell, resultTable, Element.ALIGN_CENTER, true);
		formatAndAddCell(createHeadingCell(CommonConstants.PARAMETERS), resultTable, Element.ALIGN_CENTER, true);
		formatAndAddCell(createHeadingCell(CommonConstants.VALUES), resultTable, Element.ALIGN_CENTER, true);
		formatAndAddCell(createHeadingCell(CommonConstants.REFERENCE_VALUES), resultTable, Element.ALIGN_CENTER, true);
		for (ReferenceValue referenceValue: ReferenceValue.values()) {
			formatAndAddCell(createHeadingCell(referenceValue.getFieldName()), resultTable, Element.ALIGN_CENTER, true);
			formatAndAddCell(createValueCell(CommonUtils.formatField(values[heading.get(referenceValue.getFieldName())])), resultTable, Element.ALIGN_CENTER, true);
			formatAndAddCell(createValueCell(referenceValue.getReferenceValues()), resultTable, Element.ALIGN_CENTER, true);
		}
		Paragraph referenceTableParagraph = new Paragraph();
		referenceTableParagraph.setSpacingBefore(5f);
		resultTable.setSpacingAfter(0f);
		referenceTableParagraph.add(resultTable);
		document.add(referenceTableParagraph);
	}

	private static PdfPCell createValueCell(String text) {
		return new PdfPCell(new Phrase(text, new Font(FontFamily.HELVETICA, 8f)));
	}

	private static PdfPCell createHeadingCell(String text) {
		return new PdfPCell(new Phrase(text, new Font(FontFamily.HELVETICA, 8f, Font.BOLD)));
	}

	private static void formatAndAddCell(PdfPCell cell, PdfPTable table, int horizontalAlignment, boolean border) {
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(horizontalAlignment);
		cell.setPadding(5f);
		if(!border) {
			cell.setBorder(Rectangle.NO_BORDER);
		}
		table.addCell(cell);
	}

	private static void addCommentBox(Document document) throws DocumentException {
		Paragraph referenceTableParagraph = new Paragraph(CommonConstants.COMMENTS_TEXT, new Font(FontFamily.HELVETICA, 8.0f, Font.BOLD));
		referenceTableParagraph.setSpacingBefore(20f);
		referenceTableParagraph.setIndentationLeft(30f);
		document.add(referenceTableParagraph);
	}

	private static void addApproval(Document document) throws DocumentException, MalformedURLException, IOException {
		Image image = Image.getInstance(CommonConstants.SIGN_PATH);
		image.scalePercent(12.5f);
		image.setAbsolutePosition(400,65);
		document.add(image);
		Paragraph approverNameParagraph = new Paragraph(CommonConstants.APPROVAL_OFFICER, new Font(FontFamily.HELVETICA, 8.0f, Font.BOLD));
		approverNameParagraph.setSpacingBefore(25f);
		approverNameParagraph.setIndentationRight(30f);
		approverNameParagraph.setAlignment(Element.ALIGN_RIGHT);
		document.add(approverNameParagraph);
		Paragraph approverTextPara = new Paragraph(CommonConstants.APPROVAL_TEXT, new Font(FontFamily.HELVETICA, 8.0f, Font.BOLD));
		approverTextPara.setSpacingBefore(3f);
		approverTextPara.setIndentationRight(30f);
		approverTextPara.setAlignment(Element.ALIGN_RIGHT);
		document.add(approverTextPara);
		
	}

	private static void addRunningSections(Document document, String imagePath, int position)
			throws BadElementException, MalformedURLException, IOException, DocumentException {
		Image image = Image.getInstance(imagePath);
		image.scalePercent(66.0f);
		image.setAbsolutePosition(0,position);
		document.add(image);
	}

}
