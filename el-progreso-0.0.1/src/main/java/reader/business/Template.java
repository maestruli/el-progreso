package reader.business;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Template {

	static String TEMPLATE_FILE_NAME = "./Template2003.xls";
	static String EXCEL_EXT = ".xls";
	static HSSFWorkbook wb;
	static HSSFSheet sheet;
	static Logger LOGGER = LoggerFactory.getLogger(Template.class);

	static {
		try {
			File file = new File(TEMPLATE_FILE_NAME);
			InputStream is = new FileInputStream(file);
			wb = new HSSFWorkbook(is);
			is.close();
			sheet = wb.getSheetAt(0);
		} catch (Exception e) {
			LOGGER.error("Could not open file: " + TEMPLATE_FILE_NAME);
		}
	}

	public static HSSFSheet getSheet() {
		return sheet;
	}

	public static HSSFRow getRow(int rowIndex) {
		return sheet.getRow(rowIndex);
	}

	public static void createRow(int rownum) {
		sheet.createRow(rownum);
	}

	public static void save(String folder) {
		FileOutputStream fos = null;
		File report = null;
		try {
			validateFolder(folder);
			String fileName = folder + File.separator + generateFileName();
			report = new File(fileName);
			fos = new FileOutputStream(report);
			HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
			wb.write(fos);
		} catch (IOException e) {
			LOGGER.error("Error saving report" + " " + e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					LOGGER.error("Error: " + e.getMessage());
				}
			}
		}
		try {
			if (report != null) {
				open(report);
			}
		} catch (Exception e) {
			LOGGER.error("Could not open generated report: " + report.getName()
					+ " " + e.getMessage());
		}
	}

	private static String generateFileName() {
		Calendar calendar = Calendar.getInstance();
		Date now = new Date(calendar.getTimeInMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy-hhmm");
		return formatter.format(now) + EXCEL_EXT;
	}

	public static void open(File file) throws IOException {
		Desktop.getDesktop().open(file);
	}

	public static void validateFolder(String folder) {
		File file = new File(folder);
		if (!file.exists()) {
			file.mkdir();
		}
	}
}
