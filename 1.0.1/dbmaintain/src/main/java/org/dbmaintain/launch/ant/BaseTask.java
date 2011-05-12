/*
 * Copyright 2006-2007,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dbmaintain.launch.ant;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.apache.tools.ant.Task;
import org.dbmaintain.config.DbMaintainConfigurationLoader;
import org.dbmaintain.config.PropertiesDbMaintainConfigurer;
import org.dbmaintain.launch.DbMaintain;
import org.dbmaintain.util.FileUtils;

/**
 * Base DbMaintain task
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class BaseTask extends Task {

    /**
     * Optional custom configuration file. Is usually not needed, since all applicable properties are configurable
     * using task attributes.
     */
    private String configFile;

    
    /**
     * @return The {@link DbMaintain} instance for this task
     */
    protected DbMaintain getDbMaintain() {
        PropertiesDbMaintainConfigurer dbMaintainConfigurer = getDbMaintainConfigurer();
        DbMaintain dbMaintain = new DbMaintain(dbMaintainConfigurer);
        return dbMaintain;
    }
    
    
    protected PropertiesDbMaintainConfigurer getDbMaintainConfigurer() {
        PropertiesDbMaintainConfigurer dbMaintainConfigurer = new PropertiesDbMaintainConfigurer(getConfiguration(), null);
        return dbMaintainConfigurer;
    }
    
    
    /**
     * @return The DbMaintain configuration for this task
     */
    protected Properties getConfiguration() {
        URL configFileUrl = null;
        if (configFile != null) {
            configFileUrl = FileUtils.getUrl(new File(configFile));
        }
        Properties configuration = new DbMaintainConfigurationLoader().loadConfiguration(configFileUrl);
        addTaskConfiguration(configuration);
        
        return configuration;
    }


    /**
     * @return Properties object that defines the default values for all properties
     */
    protected Properties getDefaultConfiguration() {
        return new DbMaintainConfigurationLoader().loadDefaultConfiguration();
    }

    
    /**
     * Hook method for adding specific configuration for this ant task
     * @param configuration
     */
    protected void addTaskConfiguration(Properties configuration) {
    }


    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }
    
}