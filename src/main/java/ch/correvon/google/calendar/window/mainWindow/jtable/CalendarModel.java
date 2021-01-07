package ch.correvon.google.calendar.window.mainWindow.jtable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import com.google.api.services.calendar.model.CalendarListEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ch.correvon.google.calendar.object.MyCalendar;

public class CalendarModel extends AbstractTableModel implements ListModel<MyCalendar>, ColorableModel
{
	private static Log s_logger = LogFactory.getLog(CalendarModel.class);
	private static final String SPECIAL_NAME_ALL = "[Tous]";
	
	public CalendarModel()
	{
		this.calendars = new ArrayList<>();
		this.clear();
	}
	
	public void clear()
	{
		int size = this.calendars.size();
		this.calendars.clear();
		super.fireTableRowsDeleted(0, size);
		
//		CalendarListEntry all = new CalendarListEntry();
//		all.setSummary(SPECIAL_NAME_ALL);
//		this.add(new MyCalendar(all, true));
	}

	public List<MyCalendar> getCalendars()
	{
		return this.calendars;
	}
	
	public List<CalendarListEntry> getGoogleCalendars()
	{
		List<CalendarListEntry> googleCalendars = new ArrayList<CalendarListEntry>(this.calendars.size());
		for(MyCalendar calendar:this.calendars)
			googleCalendars.add(calendar.getCalendar());
		return googleCalendars;
	}
	
	public MyCalendar getCalendar(int row)
	{
		return this.calendars.get(row);
	}
	
	@Override public Color getColor(int row)
	{
		return this.getCalendar(row).getColor();
	}

	public List<MyCalendar> getSelectedCalendars()
	{
		List<MyCalendar> selectedCalendars = new ArrayList<MyCalendar>(this.calendars.size());
		
		for(MyCalendar calendar:this.calendars)
			if(calendar.isSelected())
				selectedCalendars.add(calendar);
		
		return selectedCalendars;
	}

	public void add(MyCalendar calendar)
	{
		// Header [v] Tous
		if(this.getSize() == 0 && !calendar.getSummary().equals(SPECIAL_NAME_ALL))
		{
			CalendarListEntry all = new CalendarListEntry();
			all.setSummary(SPECIAL_NAME_ALL);
			this.add(new MyCalendar(all, true));
		}
		
		this.calendars.add(calendar);

		int size = this.calendars.size();
		super.fireTableRowsInserted(size, size);
	}

	@Override public int getSize()
	{
		return this.calendars.size();
	}

	@Override public MyCalendar getElementAt(int i)
	{
		return this.calendars.get(i);
	}

	@Override public Class<?> getColumnClass(int column)
	{
		switch(column)
		{
			case 0 : return Boolean.class;
			case 1 : return String.class;
		}
		return null;
	}

	@Override public int getColumnCount()
	{
		return columns.length;
	}

	@Override public String getColumnName(int column)
	{
		return columns[column];
	}


	@Override public int getRowCount()
	{
		return this.calendars.size();
	}

	@Override public Object getValueAt(int row, int column)
	{
		MyCalendar calendar = this.calendars.get(row);
		switch(column)
		{
			case 0 : return calendar.isSelected();
			case 1 : return calendar.getSummary();
		}
		return null;
	}

	@Override public boolean isCellEditable(int row, int column)
	{
		switch(column)
		{
			case 0 : return true;
			case 1 : return false;
		}
		return false;
	}

	@Override public void setValueAt(Object value, int row, int column)
	{
		if(value == null)
			return;
		
		MyCalendar oldAgenda = this.calendars.get(row);
		switch(column)
		{
			case 0 : oldAgenda.setSelected((Boolean)value); break;
			case 1 : oldAgenda.setSummary((String)value); break;
		}
		
		if(row == 0 && column == 0)
		{
			boolean selected = oldAgenda.isSelected();
			for(MyCalendar calendar:this.calendars)
				calendar.setSelected(selected);
			super.fireTableRowsUpdated(0, this.getRowCount());
		}
	}

	@Override public void addListDataListener(ListDataListener arg0){}
	@Override public void removeListDataListener(ListDataListener arg0){}

	private List<MyCalendar> calendars;

	private static final String[] columns = {"", "Agenda"};
}
