package goronald.web.id.githubusersearchapp.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

    public static long covertFullDateToMillis(String fullDate) {
        // Mon, 04 Sep 2017 17:26:43 GMT
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        long millis = 0;
        try {
            if (fullDate != null) {
                Date date = inputFormat.parse(fullDate);
                millis = date.getTime();
            }
            else
                return 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    public long epochToMillis(long epoch) {
        long millis = 0;
        return millis;
    }
}
