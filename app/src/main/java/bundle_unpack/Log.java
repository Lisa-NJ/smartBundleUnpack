package bundle_unpack;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    static Logger  logger = null;
    static {
        try {
            logger = Logger.getLogger("LoggerLog");
            logger.setLevel( Level.INFO);
            //add fileHandler
            FileHandler fileHandler = new FileHandler("log/process.log");
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new DateTimeFormat());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}




