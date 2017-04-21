package com.saga.sagasuite.scriptgroovy.log

@Grab('log4j:log4j:1.2.17')

import groovy.util.logging.Log4j
import org.apache.log4j.Appender
import org.apache.log4j.FileAppender
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.opencms.main.OpenCms

/**
 * Created by jesus on 24/03/2017.
 */
@Log4j
class   GLog {

    private final static String LOG_FOLDER = "logs";
//    private final static String LOG_FILE= "script.log";
    private final static String LOG_PATTERN = "%d{DATE} %5p [%30.30C:%4L] %m%n";

    def GLog() {
    }

    public static Logger getLog(Object o){
        return getLog(o.getClass().getName());
    }

    public static Logger getLog(Class clazz){
        return getLog(clazz.getName())
    }

    public static Logger getLog(String name){
        // Init appender
        Appender appender = getAppender(name);

        // Get logger and add appender
        Logger logger = log.getLoggerRepository().getLogger(name);
        if (logger.getAppender(name) == null){
            logger.setAdditivity(false);
            logger.addAppender(appender);
        }
        return logger;
    }

    private static Appender getAppender(String name){
        return getAppender(name, name + ".log");
    }

    private static Appender getAppender(String name, String relFilePath){
//        # Special appender configuration
//        log4j.appender.SCRIPT=org.apache.log4j.RollingFileAppender
//        log4j.appender.SCRIPT.File=${opencms.logfolder}script.log
//        log4j.appender.SCRIPT.MaxFileSize=2mb
//        log4j.appender.SCRIPT.MaxBackupIndex=5
//        log4j.appender.SCRIPT.layout=org.apache.log4j.PatternLayout
//        log4j.appender.SCRIPT.layout.ConversionPattern=%d{DATE} %5p [%30.30C:%4L] %m%n

        Appender appender = log.getAppender(name)
        if (appender == null) {
            appender = new FileAppender();
            appender.setName(name);
            String logFile =
                    OpenCms.getSystemInfo().getAbsoluteRfsPathRelativeToWebInf(LOG_FOLDER) +
                            '/' + relFilePath; //'/' + script.log'
            appender.setFile(logFile);
            appender.setLayout(new PatternLayout(LOG_PATTERN));
//        fa.setThreshold(Level.DEBUG);
            appender.setAppend(true);
            appender.activateOptions();
        }
        return appender;
    }
}