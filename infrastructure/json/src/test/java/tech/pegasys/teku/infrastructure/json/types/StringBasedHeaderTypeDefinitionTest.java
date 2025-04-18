/*
 * Copyright Consensys Software Inc., 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.teku.infrastructure.json.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

public class StringBasedHeaderTypeDefinitionTest {

  @Test
  public void serializeOpenApiType() throws IOException {
    // Create a builder for StringBasedHeaderTypeDefinition
    final StringBasedHeaderTypeDefinition.Builder<String> builder =
        new StringBasedHeaderTypeDefinition.Builder<>();

    // Set the title, description, required, and example
    builder.title("String-Header");
    builder.description("This is a string header");
    builder.required(true);
    builder.example("test string");

    // Set the parser and formatter functions
    builder.parser(s -> s);
    builder.formatter(s -> s);

    // Build the StringBasedHeaderTypeDefinition
    final StringBasedHeaderTypeDefinition<String> definition = builder.build();

    // Create a StringWriter and a JsonGenerator
    final StringWriter writer = new StringWriter();
    final JsonGenerator gen = new ObjectMapper().getFactory().createGenerator(writer);

    gen.writeStartObject();
    definition.serializeOpenApiType(gen);
    gen.writeEndObject();
    gen.close();

    final String json = writer.toString();

    final String expectedJson =
        "{\"String-Header\":{\"description\":\"This is a string header\",\"required\":true,\"schema\":{\"type\":\"string\",\"example\":\"test string\"}}}";
    assertEquals(expectedJson, json);
  }
}
