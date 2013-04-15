package reader.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reader.business.Bill;
import reader.business.Template;
import reader.utils.ManifestUtil;
import reader.utils.PropertiesUtil;

public class MainForm extends JFrame {
	
	private JTextField readBill = new JTextField();
	private BillTable table = new BillTable();
	private TableColumnAdjuster tca = new TableColumnAdjuster(table);
	private JButton btnFinish = new JButton("Finalizar carga");
	private JLabel lblScanner = new JLabel("Pase el recibo por el Scanner: ");
	private JLabel lblEvents = new JLabel("...");
	private JScrollPane scroller;
	private int counter = 0;
	private static String REPORT_FOLDER = PropertiesUtil.getValue("REPORT_FOLDER");
	private static String READ_CODE = "Codigo Leido: ";
	private static String INVALID_READ_CODE = "El Codigo leido no es valido!";
	private Map<Integer, Bill> bills = new HashMap<Integer, Bill>();
	
	static Logger LOGGER=LoggerFactory.getLogger(MainForm.class);
	
	public MainForm() throws HeadlessException {
		initComponents();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainForm().setVisible(true);
			}
		});
	}

	protected void initComponents() {
		setResizable(false);
		setSize(900, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));

		readBill.setPreferredSize(new Dimension(400, 20));

		btnFinish.setSize(new Dimension(100, 20));
		btnFinish.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lblEvents.setText("Almacenando en archivo en " + REPORT_FOLDER);
				save(bills, REPORT_FOLDER);
			}
		});
		readBill.setFocusTraversalKeysEnabled(true);
		readBill.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		readBill.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				int k = ke.getKeyCode();
				if (k == KeyEvent.VK_TAB) {
					lblEvents.setText(process(readBill.getText().trim()));
				}
			}
		});

		table.setVisible(true);
		table.setAutoscrolls(true);
		table.getModel().addTableModelListener(new TableModelListener() {
		    public void tableChanged(TableModelEvent e) {
		    	tca.adjustColumns();
		    }
		});
		scroller = new JScrollPane(table);
		scroller.setPreferredSize(new Dimension(880, 400));
		scroller.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
	        public void adjustmentValueChanged(AdjustmentEvent e) {  
	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
	        }
	    });
		getContentPane().add(lblScanner);
		getContentPane().add(readBill);
		getContentPane().add(scroller);
		getContentPane().add(lblEvents);
		getContentPane().add(btnFinish);
		setTitle("El Progreso: v." + ManifestUtil.getAppVersion());
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private String process(String barCode) {
		Bill bill = new Bill(barCode);
		if (bill.validateBarCode()) {
			counter++;
			bills.put(counter, bill);
			table.getModel().addRow(bill.toObject());
		} else {
			LOGGER.info(INVALID_READ_CODE + barCode);
			return INVALID_READ_CODE;
		}
		table.setVisible(true);
		readBill.setText("");
		readBill.setFocusable(true);
		LOGGER.info(READ_CODE + barCode);
		return READ_CODE + barCode;
	}
	
	private void save(Map<Integer, Bill> bills, String folder) {
		Iterator it = bills.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			int currentRow = ((Integer) entry.getKey()).intValue();
			Bill currentBill = (Bill) entry.getValue();
			Template.createRow(currentRow);
			HSSFRow activeRow = Template.getRow(currentRow);
			activeRow.createCell(0).setCellValue(currentBill.getBarCode());
			activeRow.createCell(1).setCellValue(currentBill.getPolicyNumber());
			activeRow.createCell(2).setCellValue(currentBill.getQuoteNumber());
			activeRow.createCell(3).setCellValue(currentBill.getAmount());
			activeRow.createCell(4).setCellValue(currentBill.getPolicyExpiration());
			activeRow.createCell(5).setCellValue(currentBill.getAmountFixed());
			activeRow.createCell(6).setCellValue(currentBill.getCommission());
			activeRow.createCell(7).setCellValue(currentBill.getToPay());
		}
		LOGGER.info("Save");
		Template.save(folder);
	}
}
