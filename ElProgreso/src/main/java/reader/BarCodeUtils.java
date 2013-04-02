package reader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BarCodeUtils {

	public static boolean validateBarCode(String barcode) {
		if (barcode.length() != 32) {
			return false;
		} else {
			String pattern = "71601(.*)";
			return barcode.matches(pattern);
		}
	}

	public static String getPolicyNumber(String barcode) {
		return insertChar(barcode.substring(6, 14), '/', 7);
	}

	public static int getQuoteNumber(String barcode) {
		return new Integer(barcode.substring(17, 20)).intValue();
	}

	public static double getAmount(String barcode) {
		String amount = barcode.substring(20, 25);
		String amountFixed = insertChar(amount, '.', amount.length() - 2);
		return new Double(amountFixed).doubleValue();
	}

	public static String getPolicyExpiration(String barcode) {
		String expiration = barcode.substring(25, 31);
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("ddMMyy").parse(expiration));
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			return sdf.format(cal.getTime());
		} catch (ParseException e) {
			System.out
					.println("Error obteniendo fecha de vencimiento de la poliza");
		}
		return "";
	}

	private static String insertChar(String text, char character, int position) {
		return text.substring(0, position) + character
				+ text.substring(position, text.length());
	}

}
