package me.superckl.prayers.common.utility;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateHelper {

	public static String toDateString(final int ticks){
		if(ticks == 0)
			return "0s";

		final Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(ticks*50);
		final Calendar begin = new GregorianCalendar();
		begin.setTimeInMillis(0L);
		return DateHelper.formatDateDiff(begin, cal);
	}

	public static String formatDateDiff(final Calendar fromDate, final Calendar toDate)
	{
		boolean future = false;
		if (toDate.equals(fromDate))
			return "0s";
		if (toDate.after(fromDate))
			future = true;
		final StringBuilder sb = new StringBuilder();
		final int[] types = new int[]
				{
				Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND
				};
		final String[] names = new String[]
				{
				"y", "y", "m", "m", "d", "d", "h", "h", "m", "m", "s", "s"
				};
		int accuracy = 0;
		for (int i = 0; i < types.length; i++)
		{
			if (accuracy > 2)
				break;
			final int diff = DateHelper.dateDiff(types[i], fromDate, toDate, future);
			if (diff > 0)
			{
				accuracy++;
				sb.append(" ").append(diff).append(" ").append(names[(i * 2) + (diff > 1 ? 1 : 0)]);
			}
		}
		if (sb.length() == 0)
			return "0s";
		return sb.toString().trim();
	}

	static int dateDiff(final int type, final Calendar fromDate, final Calendar toDate, final boolean future)
	{
		int diff = 0;
		long savedDate = fromDate.getTimeInMillis();
		while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate)))
		{
			savedDate = fromDate.getTimeInMillis();
			fromDate.add(type, future ? 1 : -1);
			diff++;
		}
		diff--;
		fromDate.setTimeInMillis(savedDate);
		return diff;
	}

}
