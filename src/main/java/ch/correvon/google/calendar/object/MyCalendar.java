package ch.correvon.google.calendar.object;

import java.awt.Color;
import com.google.api.services.calendar.model.CalendarListEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyCalendar
{
	private CalendarListEntry calendar;
	private boolean isSelected;
	private static Log s_logger = LogFactory.getLog(MyEvent.class);

	public MyCalendar(CalendarListEntry calendar)
	{
		this(calendar, true);
	}

	public MyCalendar(CalendarListEntry calendar, boolean isSelected)
	{
		this.calendar = calendar;
		this.isSelected = isSelected;
	}

	public String getSummary()
	{
		return this.calendar.getSummary();
	}
	
	public void setSummary(String summary)
	{
		this.calendar.setSummary(summary);
	}

	public boolean isSelected()
	{
		return isSelected;
	}

	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}
	
	public CalendarListEntry getCalendar()
	{
		return this.calendar;
	}
	
	public Color getColor()
	{
		Color color = null;
		try
		{
			String bgColor = this.calendar.getBackgroundColor();
			if(bgColor == null)
				return color;
			color = Color.decode(bgColor);
		}
		catch(NumberFormatException e)
		{
			s_logger.warn("Unable to parse color " + this.calendar.getColorId());
		}
		return color;
	}
}
