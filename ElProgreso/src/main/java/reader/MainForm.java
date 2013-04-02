package reader;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.poi.hssf.usermodel.HSSFRow;

public class MainForm extends JFrame {
	private JTextField recibo = new JTextField();
	private JTextArea console = new JTextArea();
	private JButton terminar = new JButton("Finalizar carga");
	private JLabel label = new JLabel("Pase el recibo por el Scanner: ");
	private JScrollPane scroller;
	private int counter = 0;
	static double percentageCom = 0.128;

	public MainForm() throws HeadlessException {
		initComponents();
	}

	protected void initComponents() {
		setResizable(false);
		setSize(900, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));

		recibo.setPreferredSize(new Dimension(400, 20));
		terminar.setSize(new Dimension(100, 20));
		terminar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String folder = "Reports\\";
				console.append("Almacenando en archivo en " + folder + "\n");
				POIUtils.save(folder);
			}
		});
		recibo.setFocusTraversalKeysEnabled(true);
		recibo.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		recibo.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				int k = ke.getKeyCode();
				if (k == KeyEvent.VK_TAB) {
					console.append(process(recibo.getText().trim()));
				}
			}
		});
		scroller = new JScrollPane(console);
		scroller.setPreferredSize(new Dimension(880, 400));
		console.setAutoscrolls(true);
		getContentPane().add(label);
		getContentPane().add(recibo);
		getContentPane().add(scroller);
		getContentPane().add(terminar);
		setTitle("El Progreso: v." + ManifestUtil.getAppVersion());
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainForm().setVisible(true);
			}
		});
	}

	public void appendToConsole(String text) {
		console.append(text + "\n");
	}

	private String process(String barcode) {
		counter++;
		StringBuilder sb = new StringBuilder();

		if (BarCodeUtils.validateBarCode(barcode)) {
			POIUtils.createRow(counter);
			HSSFRow activeRow = POIUtils.getRow(counter);

			activeRow.createCell(0).setCellValue(barcode);
			sb.append(barcode);
			sb.append("\t");
			String policyNumber = BarCodeUtils.getPolicyNumber(barcode);
			sb.append(policyNumber);
			sb.append("\t");
			activeRow.createCell(1).setCellValue(policyNumber);
			int quoteNumber = BarCodeUtils.getQuoteNumber(barcode);
			sb.append(quoteNumber);
			sb.append("\t");
			activeRow.createCell(2).setCellValue(quoteNumber);
			double amount = formatDecimal(BarCodeUtils.getAmount(barcode));
			sb.append(amount);
			sb.append("\t");
			activeRow.createCell(3).setCellValue(amount);
			String policyExpiration = BarCodeUtils.getPolicyExpiration(barcode);
			sb.append(policyExpiration);
			sb.append("\t");
			activeRow.createCell(4).setCellValue(policyExpiration);
			double amountFixed = formatDecimal(processQuote(quoteNumber, amount));
			sb.append(amountFixed);
			sb.append("\t");
			activeRow.createCell(5).setCellValue(amountFixed);
			double comision = formatDecimal(amountFixed * percentageCom);
			sb.append(comision);
			sb.append("\t");
			activeRow.createCell(6).setCellValue(comision);
			double aRendir = formatDecimal(amountFixed - comision);
			sb.append(aRendir);
			sb.append("\t");
			activeRow.createCell(7).setCellValue(aRendir);
			sb.append("\n");
			recibo.setText("");
			recibo.setFocusable(true);
		}
		return sb.toString();
	}

	private double processQuote(int quote, double amount) {
		if (quote == 100) {
			if (amount == 149.76) {
				return amount - 4.76;
			}
			if (amount == 163.07) {
				return amount - 5.07;
			}
			if (amount == 98.17) {
				return amount - 3.17;
			}
		}
		return amount;
	}

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
}
