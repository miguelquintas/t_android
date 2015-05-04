package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

	public static String dateToString(Date date, String format){
		
		return new SimpleDateFormat(format).format(date);
	}
	
	public static Date stringToDate(String string, String format){
		
		Date date = new Date();
		
		try {
			date = new SimpleDateFormat(format).parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
}
