package com.project.ordernote.utils;

import static com.project.ordernote.utils.Constants.readableDate_Format;
import static com.project.ordernote.utils.Constants.timeZone_Format;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateParserClass {
    public static String getDate_ReadableFormat()
    {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(readableDate_Format, Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone(timeZone_Format));
        return df.format(c);
    }

}
