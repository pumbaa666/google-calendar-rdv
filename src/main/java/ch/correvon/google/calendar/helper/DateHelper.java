package ch.correvon.google.calendar.helper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/*
 * https://www.baeldung.com/java-date-to-localdate-and-localdatetime
 */
public class DateHelper
{
	public static Date localDateToDate(LocalDate localDate)
	{
		return Date.from(localDate.atStartOfDay()
			      .atZone(ZoneId.systemDefault())
			      .toInstant());
	}
	
	public static LocalDate dateToLocalDate(Date date)
	{
		return date.toInstant()
			      .atZone(ZoneId.systemDefault())
			      .toLocalDate();
	}
}
