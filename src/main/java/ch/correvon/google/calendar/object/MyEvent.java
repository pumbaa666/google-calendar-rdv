package ch.correvon.google.calendar.object;

import java.awt.Color;
import java.util.Comparator;
import java.util.Date;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyEvent
{
	private MyCalendar calendar;
	private Event event;
	private static Log s_logger = LogFactory.getLog(MyEvent.class);

	/** Sort by Comment. */
	private static final int EQUAL = 0;
	private static final int DESCENDING = -1;
	public static Comparator<MyEvent> SUMMARY_SORT = new Comparator<MyEvent>()
	{
		@Override
		public int compare(MyEvent aThis, MyEvent aThat)
		{
			if(aThis == aThat)
				return EQUAL;

			int comparison = aThis.getEvent().getSummary().toLowerCase().trim().compareTo(aThat.getEvent().getSummary().toLowerCase().trim());
			if(comparison != EQUAL)
				return comparison;

			comparison = comparePossiblyNull(aThis.getEventDate(), aThat.getEventDate());
//			comparison = DESCENDING * comparePossiblyNull(aThis.getEventDate(), aThat.getEventDate()); // To reverse the order
			if(comparison != EQUAL)
				return comparison;

			comparison = comparePossiblyNull(aThis.getCalendar().getSummary().toLowerCase().trim(), aThat.getCalendar().getSummary().toLowerCase().trim());
			if(comparison != EQUAL)
				return comparison;

			return EQUAL;
		};
	};
	
	public static Comparator<MyEvent> DATE_SORT = new Comparator<MyEvent>()
	{
		@Override
		public int compare(MyEvent aThis, MyEvent aThat)
		{
			if(aThis == aThat)
				return EQUAL;

			int comparison = comparePossiblyNull(aThis.getEventDate(), aThat.getEventDate());
			if(comparison != EQUAL)
				return comparison;

			comparison = comparePossiblyNull(aThis.getCalendar().getSummary().toLowerCase().trim(), aThat.getCalendar().getSummary().toLowerCase().trim());
			if(comparison != EQUAL)
				return comparison;

			comparison = aThis.getEvent().getSummary().toLowerCase().trim().compareTo(aThat.getEvent().getSummary().toLowerCase().trim());
			if(comparison != EQUAL)
				return comparison;

			return EQUAL;
		};
	};
	
	public static Comparator<MyEvent> CALENDAR_SORT = new Comparator<MyEvent>()
	{
		@Override
		public int compare(MyEvent aThis, MyEvent aThat)
		{
			if(aThis == aThat)
				return EQUAL;

			int comparison = comparePossiblyNull(aThis.getCalendar().getSummary().toLowerCase().trim(), aThat.getCalendar().getSummary().toLowerCase().trim());
			if(comparison != EQUAL)
				return comparison;

			comparison = comparePossiblyNull(aThis.getEventDate(), aThat.getEventDate());
			if(comparison != EQUAL)
				return comparison;

			comparison = aThis.getEvent().getSummary().toLowerCase().trim().compareTo(aThat.getEvent().getSummary().toLowerCase().trim());
			if(comparison != EQUAL)
				return comparison;

			return EQUAL;
		};
	};
	
	private static <T extends Comparable<T>> int comparePossiblyNull(T aThis, T aThat)
	{
		int result = EQUAL;
		int BEFORE = -1;
		int AFTER = 1;

		if(aThis != null && aThat != null)
			result = aThis.compareTo(aThat);
		else
		{
			//at least one reference is null - special handling
			if(aThis == null && aThat == null){}
				//do nothing - they are not distinct 
			else if(aThis == null && aThat != null)
				result = BEFORE;
			else if(aThis != null && aThat == null)
				result = AFTER;
		}
		return result;
	}

	public MyEvent(MyCalendar calendar, Event event)
	{
		super();
		this.calendar = calendar;
		this.event = event;
	}

	public MyCalendar getCalendar()
	{
		return calendar;
	}

	public void setCalendar(MyCalendar calendar)
	{
		this.calendar = calendar;
	}

	public Event getEvent()
	{
		return event;
	}

	public void setEvent(Event event)
	{
		this.event = event;
	}

	public Color getColor()
	{
		Color color = null;
		CalendarListEntry googleCalendar = this.calendar.getCalendar();
		try
		{
			color = Color.decode(googleCalendar.getBackgroundColor());
		}
		catch(NumberFormatException e)
		{
			s_logger.warn("Unable to parse color " + googleCalendar.getColorId());
		}
		return color;
	}
	
	public Date getEventDate()
	{
		if(this.event == null || this.event.getStart() == null)
			return null;
		
		DateTime dateTime = this.event.getStart().getDate();
		if(dateTime == null)
			dateTime = this.event.getStart().getDateTime();
		if(dateTime == null)
			return null;
		
		return new Date(dateTime.getValue());
	}
}
