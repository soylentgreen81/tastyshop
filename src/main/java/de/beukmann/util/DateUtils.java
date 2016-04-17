package de.beukmann.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
	private DateUtils(){
		
	}
	public static LocalDate[] getWeek(int weekOfYear, int year){
		LocalDate[] result = new LocalDate[7];
		GregorianCalendar calender = new GregorianCalendar(Locale.getDefault());
		calender.set(Calendar.YEAR , year);
		calender.set(Calendar.WEEK_OF_YEAR , weekOfYear);
		calender.set(Calendar.DAY_OF_WEEK , Calendar.MONDAY);
		result[0]= calender.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		for (int i=1;i<7;i++){
			result[i] = result[0].plusDays(i);
		}
		return result;
	}
}
