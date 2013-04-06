package reader.gui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BillTable extends JTable {
	DefaultTableModel model = new DefaultTableModel();

	public BillTable() {
		model.addColumn("Codigo");
		model.addColumn("N° Poliza");
		model.addColumn("N° Cuota");
		model.addColumn("Monto");
		model.addColumn("Vencimiento");
		model.addColumn("Correccion");
		model.addColumn("Comision");
		model.addColumn("A Rendir");
		super.setModel(model);
	}

	public DefaultTableModel getModel() {
		return model;
	}

	@Override
	public void setAutoResizeMode(int mode) {
		super.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

}
