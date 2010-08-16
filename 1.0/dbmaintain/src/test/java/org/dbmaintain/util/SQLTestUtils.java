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
package org.dbmaintain.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.dbmaintain.dbsupport.DbSupport;
import org.apache.commons.dbutils.DbUtils;
import static org.apache.commons.dbutils.DbUtils.closeQuietly;

/**
 * Utilities for creating and dropping test tables, views....
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SQLTestUtils {


    /**
     * Drops the test tables
     *
     * @param dbSupport  The db support, not null
     * @param tableNames The tables to drop
     */
    public static void dropTestTables(DbSupport dbSupport, String... tableNames) {
        for (String tableName : tableNames) {
            try {
                String correctCaseTableName = dbSupport.toCorrectCaseIdentifier(tableName);
                dbSupport.dropTable(dbSupport.getDefaultSchemaName(), correctCaseTableName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test views
     *
     * @param dbSupport The db support, not null
     * @param viewNames The views to drop
     */
    public static void dropTestViews(DbSupport dbSupport, String... viewNames) {
        for (String viewName : viewNames) {
            try {
                String correctCaseViewName = dbSupport.toCorrectCaseIdentifier(viewName);
                dbSupport.dropView(dbSupport.getDefaultSchemaName(), correctCaseViewName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test materialized views
     *
     * @param dbSupport             The db support, not null
     * @param materializedViewNames The views to drop
     */
    public static void dropTestMaterializedViews(DbSupport dbSupport, String... materializedViewNames) {
        for (String materializedViewName : materializedViewNames) {
            try {
                String correctCaseViewName = dbSupport.toCorrectCaseIdentifier(materializedViewName);
                dbSupport.dropMaterializedView(dbSupport.getDefaultSchemaName(), correctCaseViewName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test synonyms
     *
     * @param dbSupport    The db support, not null
     * @param synonymNames The views to drop
     */
    public static void dropTestSynonyms(DbSupport dbSupport, String... synonymNames) {
        for (String synonymName : synonymNames) {
            try {
                String correctCaseSynonymName = dbSupport.toCorrectCaseIdentifier(synonymName);
                dbSupport.dropSynonym(dbSupport.getDefaultSchemaName(), correctCaseSynonymName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test sequence
     *
     * @param dbSupport     The db support, not null
     * @param sequenceNames The sequences to drop
     */
    public static void dropTestSequences(DbSupport dbSupport, String... sequenceNames) {
        for (String sequenceName : sequenceNames) {
            try {
                String correctCaseSequenceName = dbSupport.toCorrectCaseIdentifier(sequenceName);
                dbSupport.dropSequence(dbSupport.getDefaultSchemaName(), correctCaseSequenceName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test triggers
     *
     * @param dbSupport    The db support, not null
     * @param triggerNames The triggers to drop
     */
    public static void dropTestTriggers(DbSupport dbSupport, String... triggerNames) {
        for (String triggerName : triggerNames) {
            try {
                String correctCaseTriggerName = dbSupport.toCorrectCaseIdentifier(triggerName);
                dbSupport.dropTrigger(dbSupport.getDefaultSchemaName(), correctCaseTriggerName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test types
     *
     * @param dbSupport The db support, not null
     * @param typeNames The types to drop
     */
    public static void dropTestTypes(DbSupport dbSupport, String... typeNames) {
        for (String typeName : typeNames) {
            try {
                String correctCaseTypeName = dbSupport.toCorrectCaseIdentifier(typeName);
                dbSupport.dropType(dbSupport.getDefaultSchemaName(), correctCaseTypeName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }
    
    
    /**
     * Executes the given update statement.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The nr of updates
     */
    public static int executeUpdate(String sql, DataSource dataSource) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (Exception e) {
            throw new DbMaintainException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, null);
        }
    }


    /**
     * Executes the given statement ignoring all exceptions.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The nr of updates, -1 if not succesful
     */
    public static int executeUpdateQuietly(String sql, DataSource dataSource) {
        try {
            return executeUpdate(sql, dataSource);
        } catch (DbMaintainException e) {
            // Ignored
            return -1;
        }
    }


    /**
     * Returns the long extracted from the result of the given query. If no value is found, a {@link DbMaintainException}
     * is thrown.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The long item value
     */
    public static long getItemAsLong(String sql, DataSource dataSource) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception e) {
            throw new DbMaintainException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }

        // in case no value was found, throw an exception
        throw new DbMaintainException("No item value found: " + sql);
    }


    /**
     * Returns the value extracted from the result of the given query. If no value is found, a {@link DbMaintainException}
     * is thrown.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The string item value
     */
    public static String getItemAsString(String sql, DataSource dataSource) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (Exception e) {
            throw new DbMaintainException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }

        // in case no value was found, throw an exception
        throw new DbMaintainException("No item value found: " + sql);
    }


    /**
     * Returns the items extracted from the result of the given query.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The items, not null
     */
    public static Set<String> getItemsAsStringSet(String sql, DataSource dataSource) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            Set<String> result = new HashSet<String>();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
            return result;

        } catch (Exception e) {
            throw new DbMaintainException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }
    }


    /**
     * Utility method to check whether the given table is empty.
     *
     * @param tableName  The table, not null
     * @param dataSource The data source, not null
     * @return True if empty
     */
    public static boolean isEmpty(String tableName, DataSource dataSource) {
        return getItemAsLong("select count(1) from " + tableName, dataSource) == 0;
    }


}