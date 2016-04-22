package de.beukmann.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	private DateUtils(){
		
	}
	public static LocalDate[] getWeek(int weekOfYear, int year){
		LocalDate[] result = new LocalDate[7];
		Calendar calender = Calendar.getInstance();
		calender.set(Calendar.YEAR , year);
		calender.set(Calendar.WEEK_OF_YEAR , weekOfYear);
		calender.set(Calendar.DAY_OF_WEEK , Calendar.MONDAY);
		result[0]= calender.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		for (int i=1;i<7;i++){
			result[i] = result[0].plusDays(i);
		}
		return result;
	}
	public static Date toDate(LocalDate date){
		return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	public static Tuple<Date, Date> getFirstLastDayOfMonth(int month, int year){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		final Date start = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		final Date end = calendar.getTime();
		return new Tuple<Date, Date>(start, end);
	}
	
	
}
