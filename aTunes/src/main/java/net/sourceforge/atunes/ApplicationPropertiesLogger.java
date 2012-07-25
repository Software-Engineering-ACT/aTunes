/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

public class ApplicationPropertiesLogger {
	
	private IApplicationArguments applicationArguments;
	private IOSManager osManager;

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
    /**
     * Log some properties
     * 
     * @param arguments
     * @param osManager
     */
    public void logProgramProperties() {
    	for (Map.Entry<String, String> entry : getApplicationProperties().entrySet()) {
    		Logger.info(entry.getKey(), ": ", entry.getValue());
    	}
    }
    
    /**
     * @return application properties
     */
    public Map<String, String> getApplicationProperties() {
    	Map<String, String> properties = new HashMap<String, String>();
        properties.put("Version", StringUtils.getString(Constants.APP_NAME, " ", Constants.VERSION));
        properties.put("Java Virtual Machine", System.getProperty("java.version"));
        properties.put("Operating System", StringUtils.getString(System.getProperty("os.name"), " ", System.getProperty("os.version"), " (", System.getProperty("os.arch"), ")"));
        properties.put("Arguments", applicationArguments.getOriginalArguments().toString());
        properties.put("Debug mode", Boolean.toString(applicationArguments.isDebug()));
        properties.put("Execution path", osManager.getWorkingDirectory());
    	return properties;
    }
}