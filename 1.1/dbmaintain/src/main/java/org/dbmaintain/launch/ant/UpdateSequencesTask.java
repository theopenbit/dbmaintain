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

import java.util.Properties;

import org.dbmaintain.config.DbMaintainProperties;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE;
import org.dbmaintain.launch.DbMaintain;

/**
 * Task that updates all sequences and identity columns to a minimum value.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UpdateSequencesTask extends BaseDatabaseTask {

    private Long lowestAcceptableSequenceValue;
    
    protected void performTask(DbMaintain dbMaintain) {
        dbMaintain.updateSequences();
    }
    
    @Override
    protected void addTaskConfiguration(Properties configuration) {
        addTaskConfiguration(configuration, PROPERTY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE, lowestAcceptableSequenceValue);
    }

    public void setLowestAcceptableSequenceValue(Long lowestAcceptableSequenceValue) {
        this.lowestAcceptableSequenceValue = lowestAcceptableSequenceValue;
    }

}