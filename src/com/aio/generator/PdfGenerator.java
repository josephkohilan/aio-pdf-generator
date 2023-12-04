package com.aio.generator;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.aio.generator.constants.CommonConstants;
import com.aio.generator.enums.DisplayField;
import com.aio.generator.enums.PatientDetails;
import com.aio.generator.enums.ReferenceValue;
import com.aio.generator.utils.CommonUtils;
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
import com.opencsv.CSVReader;

import static com.aio.generator.constants.CommonConstants.CONSULTANT_PATHOLOGIST;
import static com.aio.generator.constants.CommonConstants.DIRECTOR;

public class PdfGenerator {

	public static void main(String[] args) {
		File[] files = new File(".").listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) {
				try (CSVReader reader = new CSVReader(new FileReader(file))) {
					Map<String, Integer> headings = new HashMap<>();
					String [] line;
					boolean headingIndex = true;
					while ((line = reader.readNext()) != null) {
						int index = 0;
						String[] values = headingIndex? null: new String[headings.size()];
						for(String element : line) {
							if (headingIndex) {
								processHeadings(element, index, headings);
							} else {
								values[index] = CommonUtils.formatField(element);
							}
							index++;
						}
						if (!headingIndex) {
							generatePdf(values, headings);
						}
						headingIndex = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void processHeadings(String element, int index, Map<String, Integer> heading) {
		heading.put(CommonUtils.formatField(element), index);
	}

	private static void generatePdf(String[] values, Map<String, Integer> heading) throws Exception {
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
			addCommentBox(values, heading,  document);
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

	private static void addInfoTable(String[] values, Map<String, Integer> heading, Document document,
			String patientName, String sampleId) throws DocumentException {
		PdfPTable infoTable = new PdfPTable(4);
		infoTable.setWidthPercentage(90);
		infoTable.setSpacingBefore(0f);
		infoTable.setSpacingAfter(0f);
		infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		infoTable.setKeepTogether(true);

		formatAndAddCell(
				createValueCell(DisplayField.PATIENT_NAME.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(
				createValueCell(patientName), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(
				createValueCell(DisplayField.DATE.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(
				createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.DATE.getFieldName())])),
				infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(
				createValueCell(DisplayField.AGE.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(
				createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.AGE.getFieldName())])),
				infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.SAMPLE_ID.getDisplayName()), infoTable, Element.ALIGN_LEFT,
				false);
		formatAndAddCell(createValueCell(sampleId), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(
				createValueCell(DisplayField.GENDER.getDisplayName()), infoTable, Element.ALIGN_LEFT, false);
		formatAndAddCell(
				createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.GENDER.getFieldName())])),
				infoTable, Element.ALIGN_LEFT, false);
		
		String collectionDate = String.format("%s %s",
				CommonUtils.formatField(values[heading.get(PatientDetails.DRAW_DATE.getFieldName())]),
				CommonUtils.formatField(values[heading.get(PatientDetails.DRAW_TIME.getFieldName())]));
		formatAndAddCell(createValueCell(DisplayField.COLLECTION_DATE.getDisplayName()), infoTable, Element.ALIGN_LEFT,
				false);
		formatAndAddCell(createValueCell(collectionDate), infoTable, Element.ALIGN_LEFT, false);
		
		formatAndAddCell(createValueCell(DisplayField.PATIENT_ID.getDisplayName()), infoTable, Element.ALIGN_LEFT,
				false);
		formatAndAddCell(
				createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.PATIENT_ID.getFieldName())])),
				infoTable, Element.ALIGN_LEFT, false);
		
		String deliveryDate = String.format("%s %s",
				CommonUtils.formatField(values[heading.get(PatientDetails.DELIVERY_DATE.getFieldName())]),
				CommonUtils.formatField(values[heading.get(PatientDetails.DELIVERY_TIME.getFieldName())]));
		formatAndAddCell(createValueCell(DisplayField.REPORTING_DATE.getDisplayName()), infoTable, Element.ALIGN_LEFT,
				false);
		formatAndAddCell(createValueCell(deliveryDate), infoTable, Element.ALIGN_LEFT, false);

		formatAndAddCell(createValueCell(DisplayField.REF_GROUP.getDisplayName()), infoTable, Element.ALIGN_LEFT,
				false);
		formatAndAddCell(
				createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.REF_GROUP.getFieldName())])),
				infoTable, Element.ALIGN_LEFT, false);

		formatAndAddCell(createValueCell(DisplayField.CLINICIAN.getDisplayName()), infoTable, Element.ALIGN_LEFT,
				false);
		formatAndAddCell(
				createValueCell(CommonUtils.formatField(values[heading.get(PatientDetails.CLINICIAN.getFieldName())])),
				infoTable, Element.ALIGN_LEFT, false);

		Paragraph referenceTableParagraph = new Paragraph();
		referenceTableParagraph.setSpacingBefore(75f);
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
		for (ReferenceValue referenceValue : ReferenceValue.values()) {
			formatAndAddCell(createHeadingCell(referenceValue.getFieldName()), resultTable, Element.ALIGN_CENTER, true);
			formatAndAddCell(
					createValueCell(CommonUtils.formatField(values[heading.get(referenceValue.getFieldName())])),
					resultTable, Element.ALIGN_CENTER, true);
			formatAndAddCell(createValueCell(referenceValue.getReferenceValues()), resultTable, Element.ALIGN_CENTER,
					true);
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
		cell.setPadding(3f);
		if (!border) {
			cell.setBorder(Rectangle.NO_BORDER);
		}
		table.addCell(cell);
	}

	private static void addCommentBox(String[] values,
									  Map<String, Integer> heading, Document document) throws Exception {
		Paragraph commentHeading = new Paragraph(CommonConstants.COMMENTS_TEXT,
				new Font(FontFamily.HELVETICA, 8.0f, Font.BOLD));
		commentHeading.setSpacingBefore(20f);
		commentHeading.setIndentationLeft(30f);
		document.add(commentHeading);
		String comment = heading.containsKey(PatientDetails.COMMENT.getFieldName()) && values.length >= heading.get(PatientDetails.COMMENT.getFieldName()) + 1? 
				values[heading.get(PatientDetails.COMMENT.getFieldName())]: null;
		float spacing = null == comment || comment.trim().isEmpty() ? 40f: 10f;
		Paragraph commentParagraph = new Paragraph(CommonUtils.formatField(comment),
				new Font(FontFamily.HELVETICA, 8.0f));
		commentParagraph.setIndentationLeft(30f);
		commentParagraph.setSpacingAfter(spacing);
		document.add(commentParagraph);
	}

	private static void addApproval(Document document) throws Exception {
		PdfPTable approvalTable = new PdfPTable(2);
		approvalTable.setWidthPercentage(90);
		approvalTable.setSpacingBefore(0f);
		approvalTable.setSpacingAfter(0f);
		approvalTable.setHorizontalAlignment(Element.ALIGN_CENTER);
		approvalTable.setKeepTogether(true);

		Image consultantSignatureImage = Image.getInstance(CommonConstants.PATHOLOGIST_SIGN_PATH);
		consultantSignatureImage.scalePercent(50f);
		consultantSignatureImage.setAlignment(Element.ALIGN_CENTER);
		PdfPCell consultantSignatureCell = new PdfPCell();
		consultantSignatureCell.setBorderWidth(0);
		consultantSignatureCell.addElement(consultantSignatureImage);
		approvalTable.addCell(consultantSignatureCell);

		Image directorSignatureImage = Image.getInstance(CommonConstants.DIRECTOR_SIGN_PATH);
		directorSignatureImage.scalePercent(50f);
		directorSignatureImage.setAlignment(Element.ALIGN_CENTER);
		PdfPCell directorSignatureCell = new PdfPCell();
		directorSignatureCell.setBorderWidth(0);
		directorSignatureCell.addElement(directorSignatureImage);
		approvalTable.addCell(directorSignatureCell);

		PdfPCell pathologistDetailsCell = createValueCell(CONSULTANT_PATHOLOGIST);
		pathologistDetailsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pathologistDetailsCell.setBorderWidth(0);
		approvalTable.addCell(pathologistDetailsCell);

		PdfPCell directorDetailsCell = createValueCell(DIRECTOR);
		directorDetailsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		directorDetailsCell.setBorderWidth(0);
		approvalTable.addCell(directorDetailsCell);

		Paragraph referenceTableParagraph = new Paragraph();
		referenceTableParagraph.setSpacingBefore(5f);
		approvalTable.setSpacingAfter(0f);
		referenceTableParagraph.add(approvalTable);
		document.add(referenceTableParagraph);

	}

	private static void addRunningSections(Document document, String imagePath, int position) throws Exception {
		Image image = Image.getInstance(imagePath);
		image.scalePercent(24.0f);
		image.setAbsolutePosition(0, position);
		document.add(image);
	}

}
