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

import static junit.framework.Assert.assertEquals;

import org.dbmaintain.dbsupport.DbSupport;
import org.dbmaintain.dbsupport.DbItemIdentifier;
import org.dbmaintain.dbsupport.DbItemType;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public final class DbItemIdentifierTest {

	DbSupport dbSupport;
	
	Map<String, DbSupport> dbNameDbSupportMap;

	@Before
	public void init() {
		dbSupport = TestUtils.getDbSupport();
		dbNameDbSupportMap = new HashMap<String, DbSupport>();
		dbNameDbSupportMap.put("mydatabase", dbSupport);
	}
	
	@Test
	public void parseItemIdentifier_itemOnly() throws Exception {
		DbItemIdentifier parsedIdentifier = DbItemIdentifier.parseItemIdentifier(DbItemType.TABLE, "test", dbSupport, dbNameDbSupportMap);
		DbItemIdentifier identifier = DbItemIdentifier.getItemIdentifier(DbItemType.TABLE, "public", "test", dbSupport);
		assertEquals(identifier, parsedIdentifier);
	}
	
	@Test
	public void parseItemIdentifier_schemaAndItem() throws Exception {
		DbItemIdentifier parsedIdentifier = DbItemIdentifier.parseItemIdentifier(DbItemType.TABLE, "myschema.test", dbSupport, dbNameDbSupportMap);
		DbItemIdentifier identifier = DbItemIdentifier.getItemIdentifier(DbItemType.TABLE, "myschema", "test", dbSupport);
		assertEquals(identifier, parsedIdentifier);
	}
	
	@Test
	public void parseItemIdentifier_databaseSchemaAndItem() throws Exception {
		DbItemIdentifier parsedIdentifier = DbItemIdentifier.parseItemIdentifier(DbItemType.TABLE, "mydatabase.myschema.test", dbSupport, dbNameDbSupportMap);
		DbItemIdentifier identifier = DbItemIdentifier.getItemIdentifier(DbItemType.TABLE, "myschema", "test", dbSupport);
		assertEquals(identifier, parsedIdentifier);
	}
	
	@Test
	public void parseSchemaschemaOnly() throws Exception {
		DbItemIdentifier parsedIdentifier = DbItemIdentifier.parseSchemaIdentifier("public", dbSupport, dbNameDbSupportMap);
		DbItemIdentifier identifier = DbItemIdentifier.getSchemaIdentifier("public", dbSupport);
		assertEquals(identifier, parsedIdentifier);
	}
}