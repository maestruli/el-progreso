package reader;

import java.awt.Desktop;
import java.io.File;
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

public class POIUtils {

	static HSSFWorkbook wb;
	static HSSFSheet sheet;
	static {
		try {
			InputStream file = ClassLoader.getSystemResourceAsStream("Template2003.xls");
			wb = new HSSFWorkbook(file);
			file.close();
			sheet = wb.getSheetAt(0);
		} catch (Exception e) {
			System.err.println("Error abriendo template de Excel");
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
			String fileName = folder + generateFileName();
			report = new File(fileName);
			fos = new FileOutputStream(report);
			HSSFFormulaEvaluator.evaluateAllFormulaCells(wb);
			wb.write(fos);
		} catch (IOException e) {
			System.err.println("Error guardando archivo de Reporte");
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			if (report != null) {
				open(report);
			}
		} catch (Exception e) {
			System.err.println("Error abriendo archivo de Reporte");
		}
	}

	private static String generateFileName() {
		Calendar calendar = Calendar.getInstance();
		Date now = new Date(calendar.getTimeInMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy-hhmm");
		return formatter.format(now) + ".xls";
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
