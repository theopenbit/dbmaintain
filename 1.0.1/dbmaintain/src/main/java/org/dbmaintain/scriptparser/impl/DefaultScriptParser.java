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
package org.dbmaintain.scriptparser.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import org.dbmaintain.scriptparser.ScriptParser;
import org.dbmaintain.scriptparser.parsingstate.ParsingState;
import org.dbmaintain.scriptparser.parsingstate.impl.InBlockCommentParsingState;
import org.dbmaintain.scriptparser.parsingstate.impl.InDoubleQuotesParsingState;
import org.dbmaintain.scriptparser.parsingstate.impl.InLineCommentParsingState;
import org.dbmaintain.scriptparser.parsingstate.impl.InSingleQuotesParsingState;
import org.dbmaintain.scriptparser.parsingstate.impl.NormalParsingState;
import org.dbmaintain.util.DbMaintainException;

/**
 * A class for parsing statements out of sql scripts.
 * <p/>
 * All statements should be separated with a semicolon (;). The last statement will be
 * added even if it does not end with a semicolon. The semicolons will not be included in the returned statements.
 * <p/>
 * This parser also takes quoted literals, double quoted text and in-line (--comment) and block (/ * comment * /)
 * into account when parsing the statements.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Stefan Bangels
 */
public class DefaultScriptParser implements ScriptParser {


    /**
     * The reader for the script content stream.
     */
    protected Reader scriptReader;

    /**
     * Whether backslash escaping is enabled
     */
    protected boolean backSlashEscapingEnabled;

    /**
     * The starting state.
     */
    protected ParsingState initialParsingState;

    /**
     * The current state.
     */
    protected ParsingState currentParsingState;

    /**
     * The current parsed character
     */
    protected int currentChar;


    /**
     * Constructor for DefaultScriptParser.
     *
     * @param scriptReader             The reader that will provide the script content, not null
     * @param backSlashEscapingEnabled True if backslash escaping is enabled
     */
    public DefaultScriptParser(Reader scriptReader, boolean backSlashEscapingEnabled) {
        this.scriptReader = scriptReader;
        this.backSlashEscapingEnabled = backSlashEscapingEnabled;
        this.initialParsingState = createInitialParsingState();
        this.currentParsingState = initialParsingState;
        this.scriptReader = new BufferedReader(scriptReader);
    }


    /**
     * Parses the next statement out of the given script stream.
     *
     * @return the statements, null if no more statements
     */
    public String getNextStatement() {
        try {
            return getNextStatementImpl();
        } catch (IOException e) {
            throw new DbMaintainException("Unable to parse next statement from script.", e);
        }
    }


    /**
     * Actual implementation of getNextStatement.
     *
     * @return the statements, null if no more statements
     * @throws IOException
     */
    protected String getNextStatementImpl() throws IOException {
        currentChar = scriptReader.read();
        if (currentChar == -1) {
            // nothing more to read
            return null;
        }

        // set initial state
        char previousChar = 0;
        currentParsingState = initialParsingState;
        StatementBuilder statementBuilder = createStatementBuilder();

        // parse script
        while (currentChar != -1) {
            // skip leading whitespace (NOTE String.trim uses <= ' ' for whitespace)
            if (statementBuilder.getLength() == 0 && currentChar <= ' ') {
                currentChar = scriptReader.read();
                continue;
            }

            // peek next char
            int nextCharInt = scriptReader.read();
            char nextChar;
            if (nextCharInt == -1) {
                nextChar = 0;
            } else {
                nextChar = (char) nextCharInt;
            }

            // handle character
            currentParsingState = currentParsingState.handleNextChar(previousChar, (char) currentChar, nextChar, statementBuilder);
            previousChar = (char) currentChar;
            currentChar = nextCharInt;

            // if parsing state null, a statement end is found
            if (currentParsingState == null) {
                String statement = statementBuilder.createStatement();

                // reset initial state
                previousChar = 0;
                statementBuilder.clear();
                statementBuilder.setExecutable(false);
                currentParsingState = initialParsingState;

                if (statement != null) {
                    return statement;
                }
            }
        }

        // check whether there was still an executable statement in the script
        // or only whitespace was left
        if (statementBuilder.isExecutable()) {
            String finalStatement = statementBuilder.createStatement();
            if (finalStatement != null) {
                throw new DbMaintainException("Last statement in script was not ended correctly. Each statement should end with one of " + Arrays.toString(statementBuilder.getTrailingSeparatorCharsToRemove()));
            }
        }
        return null;
    }


    /**
     * Builds the initial parsing state.
     * This will create a normal, in-line-comment, in-block-comment, in-double-quotes and in-single-quotes state
     * and link them together.
     *
     * @return The initial parsing state, not null
     */
    protected ParsingState createInitialParsingState() {
        // create states
        NormalParsingState normalParsingState = createNormalParsingState();
        InLineCommentParsingState inLineCommentParsingState = createInLineCommentParsingState();
        InBlockCommentParsingState inBlockCommentParsingState = createInBlockCommentParsingState();
        InSingleQuotesParsingState inSingleQuotesParsingState = createInSingleQuotesParsingState();
        InDoubleQuotesParsingState inDoubleQuotesParsingState = createInDoubleQuotesParsingState();

        // initialize and link states
        inLineCommentParsingState.init(normalParsingState);
        inBlockCommentParsingState.init(normalParsingState);
        inSingleQuotesParsingState.init(normalParsingState, backSlashEscapingEnabled);
        inDoubleQuotesParsingState.init(normalParsingState, backSlashEscapingEnabled);
        normalParsingState.init(inLineCommentParsingState, inBlockCommentParsingState, inSingleQuotesParsingState, inDoubleQuotesParsingState, backSlashEscapingEnabled);

        // the normal state is the begin-state
        return normalParsingState;
    }


    /**
     * Factory method for the statement builder.
     *
     * @return The statement builder, not null
     */
    protected StatementBuilder createStatementBuilder() {
        return new StatementBuilder();
    }


    /**
     * Factory method for the normal parsing state.
     *
     * @return The normal state, not null
     */
    protected NormalParsingState createNormalParsingState() {
        return new NormalParsingState();
    }


    /**
     * Factory method for the in-line comment (-- comment) parsing state.
     *
     * @return The normal state, not null
     */
    protected InLineCommentParsingState createInLineCommentParsingState() {
        return new InLineCommentParsingState();
    }


    /**
     * Factory method for the in-block comment (/ * comment * /) parsing state.
     *
     * @return The normal state, not null
     */
    protected InBlockCommentParsingState createInBlockCommentParsingState() {
        return new InBlockCommentParsingState();
    }


    /**
     * Factory method for the single quotes ('text') parsing state.
     *
     * @return The normal state, not null
     */
    protected InSingleQuotesParsingState createInSingleQuotesParsingState() {
        return new InSingleQuotesParsingState();
    }


    /**
     * Factory method for the double quotes ("text") literal parsing state.
     *
     * @return The normal state, not null
     */
    protected InDoubleQuotesParsingState createInDoubleQuotesParsingState() {
        return new InDoubleQuotesParsingState();
    }

}