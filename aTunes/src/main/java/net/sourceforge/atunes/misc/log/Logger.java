/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.misc.log;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * A custom logger for aTunes, using log4j.
 */
public class Logger {

    /** Categories to filter, i.e. will not be logged */
    private static Set<String> filteredCategories;

    /**
     * Initialize logger
     */
    static {
        // Read filtered categories
        filteredCategories = new HashSet<String>();
        try {
            PropertyResourceBundle bundle = new PropertyResourceBundle(Logger.class.getResourceAsStream(Constants.EXTENDED_LOG_FILE));
            String value = bundle.getString("log.filter");
            StringTokenizer st = new StringTokenizer(value, ",");
            while (st.hasMoreTokens()) {
                String v = st.nextToken().trim();
                filteredCategories.add(v);
            }

        } catch (Exception e) {
            System.out.println(StringUtils.getString(Constants.EXTENDED_LOG_FILE, " not found or incorrect. No filters will be applied to log"));
        }
    }

    /** Internal logger. */
    private org.apache.log4j.Logger logger;

    /**
     * Instantiates a new logger.
     */
    public Logger() {
        // Get invoker class and call Log4j getLogger
        Throwable t = new Throwable();
        logger = org.apache.log4j.Logger.getLogger(t.getStackTrace()[1].getClassName());
    }

    /**
     * Logs a debug event.
     * 
     * @param cat
     *            the cat
     * @param objects
     *            the objects to show in log
     */
    public void debug(String cat, Object... objects) {
        if (!Kernel.isDebug()) {
            return;
        }

        if (filteredCategories.contains(cat.trim())) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[').append(cat).append("] ");
        for (Object object : objects) {
            sb.append(object);
        }
        logger.debug(sb.toString());
    }

    /**
     * Logs an error event.
     * 
     * @param cat
     *            the cat
     * @param o
     *            the o
     */
    public void error(String cat, Object o) {
        // Find calling method name and class
        Throwable t = new Throwable();
        StackTraceElement[] s = t.getStackTrace();
        String className = s[1].getClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        String methodName = s[1].getMethodName();

        long timer = LoggerTimer.getTimer();

        // Build string
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(cat).append("] ").append("--> ").append(className).append('.').append(methodName).append(" [").append(timer).append("] ").append(o);

        logger.error(sb.toString());

        if (o instanceof Throwable) {
        	Throwable throwable = (Throwable) o;
            StackTraceElement[] trace = throwable.getStackTrace();

            for (StackTraceElement element : trace) {
                error(cat, className, methodName, timer, element);
            }
            
            if (throwable.getCause() != null) {
            	error(cat,  StringUtils.getString(throwable.getCause().getClass().getName(), ": ", throwable.getCause().getMessage()));

                StackTraceElement[] causeTrace = throwable.getCause().getStackTrace();

                for (StackTraceElement element : causeTrace) {
                    error(cat, className, methodName, timer, element);
                }
            }
            
            if (o instanceof InvocationTargetException && ((InvocationTargetException)o).getTargetException() != null) {
            	Throwable target = ((InvocationTargetException) o).getTargetException();
            	error(cat,  StringUtils.getString(target.getClass().getName(), ": ", target.getMessage()));

                StackTraceElement[] causeTrace = target.getStackTrace();

                for (StackTraceElement element : causeTrace) {
                    error(cat, className, methodName, timer, element);
                }
            }
        }
    }

    /**
     * Logs an error event.
     * 
     * @param cat
     *            the cat
     * @param className
     *            the class name
     * @param methodName
     *            the method name
     * @param timer
     *            the timer
     * @param o
     *            the o
     */
    private void error(String cat, String className, String methodName, long timer, StackTraceElement o) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(cat).append("] ").append("--> ").append(className).append('.').append(methodName).append(" [").append(timer).append("]\t ").append(o);

        logger.error(sb.toString());
    }

    /**
     * Logs an info event.
     * 
     * @param cat
     *            the cat
     * @param objs
     *            the objects
     */
    public void info(String cat, Object... objs) {
        if (filteredCategories.contains(cat.trim())) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : objs) {
        	sb.append(o.toString());
        }
        logger.info(StringUtils.getString("[", cat, "] ", sb.toString()));
    }

    /**
     * Logs an internal error.
     * 
     * @param o
     *            the o
     */
    public void internalError(Object o) {
        error(LogCategories.INTERNAL_ERROR, o);
        if (o instanceof Throwable && ((Throwable)o).getCause() != null) {
        	error(LogCategories.INTERNAL_ERROR,  StringUtils.getString(((Throwable)o).getCause().getClass().getName(), ": ", ((Throwable)o).getCause().getMessage()));
            StackTraceElement[] causeTrace = ((Throwable) o).getCause().getStackTrace();

            for (StackTraceElement element : causeTrace) {
                error(LogCategories.INTERNAL_ERROR,  element);
            }
        }
        if (o instanceof InvocationTargetException) {
            error(LogCategories.INTERNAL_ERROR, ((InvocationTargetException) o).getTargetException());
            error(LogCategories.INTERNAL_ERROR, ((InvocationTargetException) o).getTargetException().getCause());
        }
    }
}
