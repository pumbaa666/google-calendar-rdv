package ch.correvon.google.calendar.window.mainWindow.jtable;

import com.google.api.services.calendar.model.CalendarListEntry;

public class SelectedCalendarListEntry //extends CalendarListEntry
{
	public SelectedCalendarListEntry()
	{
		super();
	}
	
	public SelectedCalendarListEntry(boolean isSelected)
	{
		this();
		this.isSelected = isSelected;
	}
	
	public boolean isSelected()
	{
		return this.isSelected;
	}
	
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}

	private boolean isSelected;
}
