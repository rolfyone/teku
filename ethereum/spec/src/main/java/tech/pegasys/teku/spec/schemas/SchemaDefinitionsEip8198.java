/*
 * Copyright Consensys Software Inc., 2026
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

package tech.pegasys.teku.spec.schemas;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Optional;
import tech.pegasys.teku.spec.schemas.registry.SchemaRegistry;

public class SchemaDefinitionsEip8198 extends SchemaDefinitionsHeze {

  public SchemaDefinitionsEip8198(final SchemaRegistry schemaRegistry) {
    super(schemaRegistry);
  }

  public static SchemaDefinitionsEip8198 required(final SchemaDefinitions schemaDefinitions) {
    checkArgument(
        schemaDefinitions instanceof SchemaDefinitionsEip8198,
        "Expected definitions of type %s but got %s",
        SchemaDefinitionsEip8198.class,
        schemaDefinitions.getClass());
    return (SchemaDefinitionsEip8198) schemaDefinitions;
  }

  @Override
  public Optional<SchemaDefinitionsEip8198> toVersionEip8198() {
    return Optional.of(this);
  }
}
