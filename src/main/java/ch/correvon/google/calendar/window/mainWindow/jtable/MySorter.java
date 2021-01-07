package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

public final class MySorter extends MouseAdapter
{
	private EventModel model;
	private JTable table;
	
	public MySorter(JTable table, EventModel model)
	{
		this.table = table;
		this.model = model;
	}
	
	@Override
	public void mouseClicked(MouseEvent aEvent)
	{
	    int colIdx = this.table.getColumnModel().getColumnIndexAtX(aEvent.getX());
	    this.model.sortByColumn(colIdx);
	}
}