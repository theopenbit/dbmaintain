/*
 * Copyright DbMaintain.org
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
package org.dbmaintain.util;

import org.dbmaintain.database.*;
import org.dbmaintain.database.impl.DefaultSQLHandler;
import org.dbmaintain.database.impl.HsqldbDatabase;
import org.dbmaintain.script.ExecutedScript;
import org.dbmaintain.script.Script;
import org.dbmaintain.script.ScriptContentHandle;
import org.dbmaintain.script.executedscriptinfo.ExecutedScriptInfoSource;
import org.dbmaintain.script.executedscriptinfo.ScriptIndexes;
import org.dbmaintain.script.executedscriptinfo.impl.DefaultExecutedScriptInfoSource;
import org.dbmaintain.script.qualifier.Qualifier;
import org.dbmaintain.script.qualifier.QualifierEvaluator;
import org.dbmaintain.script.repository.ScriptLocation;
import org.dbmaintain.script.repository.ScriptRepository;
import org.dbmaintain.script.repository.impl.ArchiveScriptLocation;
import org.dbmaintain.script.repository.impl.FileSystemScriptLocation;

import javax.sql.DataSource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.dbmaintain.database.SimpleDataSource.createDataSource;
import static org.dbmaintain.util.CollectionUtils.asSet;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public abstract class TestUtils {


    public static Databases getDatabases() {
        return getDatabases("PUBLIC");
    }

    public static DatabaseInfo getHsqlDatabaseInfo(String... schemaNames) {
        if (schemaNames == null || schemaNames.length == 0) {
            schemaNames = new String[]{"PUBLIC"};
        }
        return new DatabaseInfo("mydatabase", "hsqldb", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils", "sa", "", asList(schemaNames), false);
    }

    public static Databases getDatabases(String... schemaNames) {
        DatabaseInfo databaseInfo = getHsqlDatabaseInfo(schemaNames);
        DataSource dataSource = createDataSource(databaseInfo);
        SQLHandler sqlHandler = new DefaultSQLHandler();
        DatabaseConnection databaseConnection = new DatabaseConnection(databaseInfo, sqlHandler, dataSource);
        Database database = new HsqldbDatabase(databaseConnection, null, null);
        return new Databases(asList(database), new ArrayList<String>());
    }

    public static DefaultExecutedScriptInfoSource getDefaultExecutedScriptInfoSource(Database database, boolean autoCreateExecutedScriptsTable) {
        return getDefaultExecutedScriptInfoSource(database, autoCreateExecutedScriptsTable, null);
    }

    public static DefaultExecutedScriptInfoSource getDefaultExecutedScriptInfoSource(Database database, boolean autoCreateExecutedScriptsTable, ScriptIndexes baselineRevision) {
        return new DefaultExecutedScriptInfoSource(autoCreateExecutedScriptsTable,
                "dbmaintain_scripts", "file_name", 150, "file_last_modified_at", "checksum", 50, "executed_at", 50, "succeeded",
                new SimpleDateFormat("dd/MM/yyyy"), database, new DefaultSQLHandler(), "@", "#", Collections.<Qualifier>emptySet(),
                asSet(new Qualifier("patch")), "postprocessing", baselineRevision);
    }

    public static Script createScript(String fileName) {
        return createScript(fileName, (ScriptIndexes) null);
    }

    public static Script createScript(String fileName, ScriptIndexes baseLineRevision) {
        return new Script(fileName, 1L, "xxxxx", "@", "#", Collections.<Qualifier>emptySet(), asSet(new Qualifier("patch")), "postprocessing", baseLineRevision);
    }

    public static Script createScript(String fileName, String content) {
        return new Script(fileName, 1L, new ScriptContentHandle.StringScriptContentHandle(content, "ISO-8859-1", false), "@", "#",
                Collections.<Qualifier>emptySet(), singleton(new Qualifier("patch")), "postprocessing", null);
    }

    public static FileSystemScriptLocation createFileSystemLocation(File scriptRootLocation) {
        return new FileSystemScriptLocation(scriptRootLocation, "ISO-8859-1", "postprocessing", Collections.<Qualifier>emptySet(),
                asSet(new Qualifier("patch")), "#", "@", asSet("sql"), null, false);
    }


    public static ScriptRepository getScriptRepository(SortedSet<Script> scriptsToReturn) {
        ScriptLocation scriptLocation = new ArchiveScriptLocation(scriptsToReturn, null, null, null, null, null, null, null, null, false);
        QualifierEvaluator qualifierEvaluator = getTrivialQualifierEvaluator();
        return new ScriptRepository(asSet(scriptLocation), qualifierEvaluator);
    }


    public static QualifierEvaluator getTrivialQualifierEvaluator() {
        return new QualifierEvaluator() {
            public boolean evaluate(Set<Qualifier> qualifiers) {
                return true;
            }
        };
    }

    public static ExecutedScriptInfoSource getExecutedScriptInfoSource(final SortedSet<ExecutedScript> executedScripts) {
        return new ExecutedScriptInfoSource() {

            public void registerExecutedScript(ExecutedScript executedScript) {
            }

            public void updateExecutedScript(ExecutedScript executedScript) {
            }

            public void clearAllExecutedScripts() {
            }

            public void deleteExecutedScript(ExecutedScript executedScript) {
            }

            public void renameExecutedScript(ExecutedScript executedScript, Script renamedToScript) {
            }

            public void deleteAllExecutedPostprocessingScripts() {
            }

            public void markErrorScriptsAsSuccessful() {
            }

            public void removeErrorScripts() {
            }

            public Set<ExecutedScript> getExecutedScripts() {
                return executedScripts;
            }
        };
    }
}