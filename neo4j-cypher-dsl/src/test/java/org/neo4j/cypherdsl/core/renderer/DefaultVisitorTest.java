/*
 * Copyright (c) 2019-2022 "Neo4j,"
 * Neo4j Sweden AB [https://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.cypherdsl.core.renderer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * @author Michael J. Simons
 */
class DefaultVisitorTest {

	private final DefaultVisitor visitor = new DefaultVisitor(null);

	@ParameterizedTest
	@CsvSource({
		"ALabel, `ALabel`",
		"A Label, `A Label`",
		"A `Label, `A ``Label`",
		"`A `Label, ```A ``Label`",
		"Spring Data Neo4j⚡️RX, `Spring Data Neo4j⚡️RX`"
	})
	void shouldCorrectlyEscapeNames(String name, String expectedEscapedName) {

		assertThat(visitor.escapeName(name)).hasValue(expectedEscapedName);
	}

	@Test
	void shouldNotTryToEscapeNullNames() {

		assertThat(visitor.escapeName(null)).isEmpty();
	}

	enum TestEnum {
		NO("NO"),
		ONE_UNDERSCORE("ONE UNDERSCORE"),
		THOSE_ARE_MORE_UNDERSCORES("THOSE ARE MORE UNDERSCORES");

		String expected;

		TestEnum(String expected) {
			this.expected = expected + " ";
		}
	}

	@EnumSource(TestEnum.class)
	@ParameterizedTest
	void underscoresInEnumsShouldBeRemoved(TestEnum testEnum) {

		DefaultVisitor visitorUnderTest = new DefaultVisitor(null);
		visitorUnderTest.enter(testEnum);
		assertThat(visitorUnderTest.builder).hasToString(testEnum.expected);
	}

	@ParameterizedTest
	@CsvSource({
		"ALabel, ALabel",
		"A Label, `A Label`",
		"A `Label, `A ``Label`",
		"`A `Label, ```A ``Label`",
		"Spring Data Neo4j⚡️RX, `Spring Data Neo4j⚡️RX`"
	})
	void shouldEscapeIfNecessary(String name, String expectedEscapedName) {

		assertThat(visitor.escapeIfNecessary(name)).isEqualTo(expectedEscapedName);
	}

	@Test
	void shouldNotUnnecessaryEscape() {

		assertThat(visitor.escapeIfNecessary(" ")).isEqualTo(" ");
		assertThat(visitor.escapeIfNecessary(null)).isNull();
		assertThat(visitor.escapeIfNecessary("a")).isEqualTo("a");
	}
}
