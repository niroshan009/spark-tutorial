package org.example.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConversionUtil {

    /**
     *
     * @param timeStamp ex: "2015-06-09 11:51:12,708"
     * @param timeStampFormat ex: "yyyy-MM-dd HH:mm:ss,SSS"
     * @return
     * @throws ParseException
     */
    public static Timestamp convertTimestamp(String timeStamp, String timeStampFormat) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(timeStampFormat);
        Date date = formatter.parse(timeStamp);
        Timestamp timeStampDate = new Timestamp(date.getTime());
        return timeStampDate;
    }
}
