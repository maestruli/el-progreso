package reader.business;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reader.utils.PropertiesUtil;

/**
 * The Class Bill.
 */
public class Bill {

	/** The bar code. */
	private String barCode;
	private static double COMMISSION = PropertiesUtil
			.getDoubleValue("COMMISSION");
	static Logger LOGGER = LoggerFactory.getLogger(Bill.class);

	/**
	 * Instantiates a new bill.
	 * 
	 * @param barCode
	 *            the bar code
	 */
	public Bill(String barCode) {
		super();
		LOGGER.info("New Bill built from barCode: " + barCode);
		this.barCode = barCode;
	}

	/**
	 * @return
	 */
	public String getBarCode() {
		return barCode;
	}

	/**
	 * Validate bar code.
	 * 
	 * @return true, if successful
	 */
	public boolean validateBarCode() {
		if (barCode.length() != 32) {
			return false;
		} else {
			String pattern = "71601(.*)";
			return barCode.matches(pattern);
		}
	}

	/**
	 * Gets the policy number.
	 * 
	 * @return the policy number
	 */
	public String getPolicyNumber() {
		return insertChar(barCode.substring(6, 14), '/', 7);
	}

	/**
	 * Gets the quote number.
	 * 
	 * @return the quote number
	 */
	public int getQuoteNumber() {
		return new Integer(barCode.substring(17, 20)).intValue();
	}

	/**
	 * Gets the amount.
	 * 
	 * @return the amount
	 */
	public double getAmount() {
		String amount = barCode.substring(20, 25);
		String amountFormatted = insertChar(amount, '.', amount.length() - 2);
		return new Double(amountFormatted).doubleValue();
	}

	/**
	 * Gets the amount fixed.
	 * 
	 * @return the amount fixed
	 */
	public double getAmountFixed() {
		double amount = this.getAmount();
		if (this.getQuoteNumber() == 100) {
			return PropertiesUtil.getDoubleValue(String.valueOf(amount));
		}
		return amount;
	}

	/**
	 * Gets the commission.
	 * 
	 * @return the commission
	 */
	public double getCommission() {
		return formatDecimal(getAmountFixed() * COMMISSION);
	}

	public double getToPay() {
		return formatDecimal(getAmountFixed() - getCommission());
	}

	/**
	 * Gets the policy expiration.
	 * 
	 * @return the policy expiration
	 */
	public String getPolicyExpiration() {
		String expiration = barCode.substring(25, 31);
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new SimpleDateFormat("ddMMyy").parse(expiration));
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			return sdf.format(cal.getTime());
		} catch (ParseException e) {
			LOGGER.error("Error getting Policy Expiration");
		}
		return "";
	}

	/**
	 * Insert char.
	 * 
	 * @param text
	 *            the text
	 * @param character
	 *            the character
	 * @param position
	 *            the position
	 * @return the string
	 */
	private static String insertChar(String text, char character, int position) {
		return text.substring(0, position) + character
				+ text.substring(position, text.length());
	}

	/**
	 * Format decimal.
	 * 
	 * @param value
	 *            the value
	 * @return the double
	 */
	private double formatDecimal(double value) {
		try {
			DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
			symbols.setDecimalSeparator('.');
			DecimalFormat format = new DecimalFormat("##.00", symbols);
			return format.parse(format.format(value)).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * To object.
	 * 
	 * @return the object[]
	 */
	public Object[] toObject() {
		return new Object[] { getBarCode(), getPolicyNumber(),
				getQuoteNumber(), getAmount(), getPolicyExpiration(),
				getAmountFixed(), getCommission(), getToPay() };
	}
}
