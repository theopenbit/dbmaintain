package org.dbmaintain;

import org.dbmaintain.executedscriptinfo.ExecutedScriptInfoSource;
import org.dbmaintain.script.Qualifier;
import org.dbmaintain.script.Script;
import org.dbmaintain.script.ScriptContentHandle;
import org.dbmaintain.scriptrunner.ScriptRunner;
import org.dbmaintain.util.DbMaintainException;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import java.util.Collections;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DefaultDbMaintainerScriptErrorTest extends UnitilsJUnit4 {

    protected Mock<ExecutedScriptInfoSource> executedScriptInfoSource;
    protected Mock<ScriptRunner> scriptRunner;

    protected Script script;


    @Before
    public void given() {
        script = new Script("01_filename.sql", 0L, new ScriptContentHandle.StringScriptContentHandle("content of script", "ISO-8859-1"), "@", "#", Collections.<Qualifier>emptySet(), singleton(new Qualifier("patch")), "postprocessing");
    }


    @Test
    public void errorMessageShouldContainFullScriptContents() throws Exception {
        try {
            DefaultDbMaintainer defaultDbMaintainer = createDefaultDbMaintainer(10000);
            scriptRunner.raises(new DbMaintainException("error message")).execute(script);

            defaultDbMaintainer.executeScript(script);
            fail("Expected DbMaintainException");

        } catch (DbMaintainException e) {
            assertEquals("Error while executing script 01_filename.sql:\n" +
                    "error message\n\n" +
                    "Full contents of failed script 01_filename.sql:\n" +
                    "----------------------------------------------------\n" +
                    "content of script\n" +
                    "----------------------------------------------------\n", e.getMessage());
        }
    }


    @Test
    public void loggingOfScriptContentsDisabledWhenMaxLengthIsSetTo0() throws Exception {
        try {
            DefaultDbMaintainer defaultDbMaintainer = createDefaultDbMaintainer(0);
            scriptRunner.raises(new DbMaintainException("error message")).execute(script);

            defaultDbMaintainer.executeScript(script);
            fail("Expected DbMaintainException");

        } catch (DbMaintainException e) {
            assertEquals("Error while executing script 01_filename.sql:\n" +
                    "error message\n\n", e.getMessage());
        }
    }


    @Test
    public void largeScriptContentIsTruncated() throws Exception {
        try {
            DefaultDbMaintainer defaultDbMaintainer = createDefaultDbMaintainer(5);
            scriptRunner.raises(new DbMaintainException("error message")).execute(script);

            defaultDbMaintainer.executeScript(script);
            fail("Expected DbMaintainException");

        } catch (DbMaintainException e) {
            assertEquals("Error while executing script 01_filename.sql:\n" +
                    "error message\n\n" +
                    "Full contents of failed script 01_filename.sql:\n" +
                    "----------------------------------------------------\n" +
                    "conte... <remainder of script is omitted>\n" +
                    "----------------------------------------------------\n", e.getMessage());
        }
    }


    private DefaultDbMaintainer createDefaultDbMaintainer(long maxNrOfCharsWhenLoggingScriptContent) {
        return new DefaultDbMaintainer(scriptRunner.getMock(), null, executedScriptInfoSource.getMock(), false, false, false, false, false, false, false, null, null, null, null, null, null, maxNrOfCharsWhenLoggingScriptContent);
    }

}