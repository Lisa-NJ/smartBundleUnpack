package bundle_unpack;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DateTimeFormat extends Formatter {

    @Override
    public String format(LogRecord log) {
        
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss S");
        
        return log.getLevel() + ": " + format.format(log.getMillis())+" " + log.getMessage() +"\n";
    }
}