package at.fhhgb.findbuddy.firebase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyCalendar {

    private Calendar cal;
    private SimpleDateFormat dateFormat;

    public MyCalendar() {
        cal = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    }

    public String convertDateToString(Date date) {
        return dateFormat.format(date);
    }

    public String getDateAsString(Date startDate, Date endDate) {
        String dateString;
        dateString = dateFormat.format(startDate);
        if (sameDay(startDate, endDate)) {
            dateString += " - " + getHour(endDate) + ":" + String.format("%02d", getMinute(endDate));
        }
        else {
            dateString += " - " + dateFormat.format((endDate));
        }
        return dateString;
    }

    public int getDay(Date date) {
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth(Date date) {
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public int getYear(Date date) {
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public int getHour(Date date) {
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute(Date date) {
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }

    public boolean sameDay(Date firstDate, Date secondDate) {
        Calendar secondCal = Calendar.getInstance();
        cal.setTime(firstDate);
        secondCal.setTime(secondDate);

        return cal.get(Calendar.YEAR) == secondCal.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == secondCal.get(Calendar.DAY_OF_YEAR);
    }

    public Date setDate(int day, int month, int year, int hour, int minute) {
        cal.set(year, month-1, day, hour, minute);
        return cal.getTime();
    }
}
