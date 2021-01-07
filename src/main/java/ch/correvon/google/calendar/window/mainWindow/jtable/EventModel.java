package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import com.google.api.services.calendar.model.Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.correvon.google.calendar.object.MyCalendar;
import ch.correvon.google.calendar.object.MyEvent;
import ch.correvon.google.calendar.window.mainWindow.MainWindow;

public class EventModel extends AbstractTableModel implements ColorableModel
{
	private static Log s_logger = LogFactory.getLog(EventModel.class);

	public EventModel()
	{
		this.events = new ArrayList<>();
	}

	public List<MyEvent> getEvents()
	{
		return this.events;
	}

	public MyEvent getEvent(int row)
	{
		return this.events.get(row);
	}

	@Override
	public Color getColor(int row)
	{
		return this.getEvent(row).getColor();
	}

	/**
	 * Sort the movies.
	 * 
	 * When called repeatedly, this method will toggle the sort between ascending and descending.
	 * 
	 * @param column index of the column by which to sort.
	 */
	public void sortByColumn(int column)
	{
		if(this.lastSortedColumn == column)
			this.sortingOrder = !this.sortingOrder;
		this.lastSortedColumn = column;

		Comparator<MyEvent> comparator = null;
		switch(column)
		{
			case 0 : comparator = MyEvent.CALENDAR_SORT; break;
			case 1 : comparator = MyEvent.DATE_SORT; break;
			case 2 : comparator = MyEvent.SUMMARY_SORT; break;
		}
		Collections.sort(this.events, comparator);
		
//		if(this.sortingOrder) // Comportement un peu incohérent quand on tape des dates dans le datePicker. Et c'est plus fluide pour l'utilisateur.
//			Collections.reverse(this.events);
		fireTableDataChanged();
	}
	
	/**
	 * Sort by last column sorted
	 */
	public void sortByColumn()
	{
		this.sortByColumn(this.lastSortedColumn);
	}

	public void add(MyCalendar calendar, Event event)
	{
		this.events.add(new MyEvent(calendar, event));
		int currentSize = this.getRowCount() - 1;
		super.fireTableRowsInserted(currentSize, currentSize);
	}

	public void remove(int row)
	{
		this.events.remove(row);
		super.fireTableRowsDeleted(row, row);
	}

	public void clear()
	{
		int currentSize = this.getRowCount() - 1;
		if(currentSize < 0)
			return;
		this.events.clear();
		super.fireTableRowsDeleted(0, currentSize);
	}

	@Override
	public Class<?> getColumnClass(int column)
	{
		switch(column)
		{
			case 0: return String.class;
			case 1: return Date.class;
			case 2: return String.class;
		}
		return null;
	}

	@Override
	public int getColumnCount()
	{
		return columns.length;
	}

	@Override
	public String getColumnName(int column)
	{
		return columns[column];
	}

	@Override
	public int getRowCount()
	{
		return this.events.size();
	}

	@Override
	public Object getValueAt(int row, int column)
	{
		MyEvent myEvent = this.events.get(row);
		switch(column)
		{
			case 0: return myEvent.getCalendar().getSummary();
			case 1: return MainWindow.DATE_SDF.format(myEvent.getEventDate());
			case 2: return myEvent.getEvent().getSummary();
		}
		return null;
	}
	
	public Object getTooltipAt(int row, int column)
	{
		MyEvent myEvent = this.events.get(row);
		switch(column)
		{
			case 0: return myEvent.getCalendar().getSummary();
			case 1: return MainWindow.DATE_SDF.format(myEvent.getEventDate());
			case 2: return myEvent.getEvent().getSummary();
		}
		return null;
	}
	

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	private List<MyEvent> events;
	private boolean sortingOrder = true;
	private int lastSortedColumn = 1;

	private static final String[] columns = { "Agenda", "Date", "Rendez-vous" };
}
