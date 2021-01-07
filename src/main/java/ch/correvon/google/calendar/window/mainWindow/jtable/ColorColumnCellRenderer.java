package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * https://stackoverflow.com/questions/5673430/java-jtable-change-cell-color
 */
public class ColorColumnCellRenderer extends DefaultTableCellRenderer
{
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
	{
		//Cells are by default rendered as a JLabel.
		JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

		//Get the status for the current row.
		ColorableModel tableModel = (ColorableModel)table.getModel();
		Color color = tableModel.getColor(row);
		label.setBackground(color);

		//Return the JLabel which renders the cell.
		return label;

	}
}