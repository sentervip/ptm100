package com.hndw.smartlibrary.until;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class LoggerUnit {
    private static final Logger ljhLogger = LogManager.getLogger("droneLogger");
    public static Logger logger;
    static LoggerUnit loggerUnit;

    public static LoggerUnit getInstance() {
        if (loggerUnit == null) {
            synchronized (LoggerUnit.class) {
                loggerUnit = new LoggerUnit();
            }
        }
        return loggerUnit;
    }

    public LoggerUnit() {
        ConfigureLog4J configureLog4J = new ConfigureLog4J();
        configureLog4J.configure();
        logger = Logger.getLogger(this.getClass());
    }

    public void writeLogger(String logInfo) {
        ljhLogger.log(Priority.DEBUG, "ljh : " + logInfo);
    }
}
