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

package tech.pegasys.teku.spec.datastructures.blocks.blockbody.versions.heze;

import java.util.Optional;
import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.infrastructure.ssz.primitive.SszBytes32;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.BeaconBlockBody;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.versions.gloas.BeaconBlockBodyGloas;

public interface BeaconBlockBodyHeze extends BeaconBlockBodyGloas {
  static BeaconBlockBodyHeze required(final BeaconBlockBody body) {
    return body.toVersionHeze()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Expected Heze block body but got " + body.getClass().getSimpleName()));
  }

  @Override
  BeaconBlockBodySchemaHeze<?> getSchema();

  SszBytes32 getReportingValue();

  default Bytes32 getReporting() {
    return getReportingValue().get();
  }

  @Override
  default Optional<BeaconBlockBodyHeze> toVersionHeze() {
    return Optional.of(this);
  }
}
