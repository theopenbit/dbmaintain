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

import static org.apache.commons.lang.StringUtils.isEmpty;
import org.dbmaintain.scriptparser.parsingstate.ParsingState;

/**
 * A class for building statements.
 *
 * @author Stefan Bangels
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class StatementBuilder {

    /* The current statement content */
    private StringBuilder statement = new StringBuilder();

    /* Executable means that the statement contains other content than comments */
    private boolean executable = false;

    private ParsingState currentParsingState;

    private char previousChar;

    public StatementBuilder(ParsingState initialParsingState) {
        currentParsingState = initialParsingState;
    }


    /**
     * @return True if the statement contains other content than comments
     */
    public boolean isExecutable() {
        return executable;
    }


    /**
     * Mark the statement as being executable, i.e. that it contains other content than comments
     */
    public void setExecutable() {
        this.executable = true;
    }


    /**
     * Returns the length (character count) of the statement.
     *
     * @return The length (character count) of the statement.
     */
    public int getLength() {
        return statement.length();
    }


    /**
     * Clear the statement.
     */
    public void clear() {
        statement.setLength(0);
        executable = false;
    }


    /**
     * Returns the characters that should be removed from the statements. Semi-colons are not part of a statement and
     * should therefore be removed from the statement.
     *
     * @return The separator characters to remove, not null
     */
    public char[] getTrailingSeparatorCharsToRemove() {
        return new char[]{';'};
    }


    /**
     * Creates the resulting statement out of the given characters.
     * This will trim the statement and remove any trailing separtors if needed.
     *
     * @return The resulting statement, null if no statement is left
     */
    public String createStatement() {
        // get built statement to return
        String trimmedStatement = statement.toString().trim();

        // ignore empty statements
        if (isEmpty(trimmedStatement)) {
            return null;
        }

        // remove trailing separator character (eg ;)
        int lastIndex = trimmedStatement.length() - 1;
        char lastChar = trimmedStatement.charAt(lastIndex);
        for (char trailingChar : getTrailingSeparatorCharsToRemove()) {
            if (lastChar == trailingChar) {
                trimmedStatement = trimmedStatement.substring(0, lastIndex);
                break;
            }
        }

        // trim and see if anything is left after removing the trailing separator (eg ;)
        trimmedStatement = trimmedStatement.trim();
        if (isEmpty(trimmedStatement)) {
            return null;
        }
        return trimmedStatement;
    }

    public void addCharacter(char currentChar, char nextChar) {
        statement.append(currentChar);
        HandleNextCharacterResult handleNextCharacterResult = currentParsingState.handleNextChar(previousChar, currentChar, nextChar, this);
        currentParsingState = handleNextCharacterResult.getNextState();
        if (!executable && handleNextCharacterResult.isExecutable()) {
            executable = true;
        }
        previousChar = currentChar;
    }

    protected boolean isWhitespace(char character) {
        return Character.isWhitespace(character) || character == 0;
    }

    protected boolean isEndOfStatementChar(char character) {
        return character == ';';
    }

    public boolean isComplete() {
        return currentParsingState == null;
    }
}
