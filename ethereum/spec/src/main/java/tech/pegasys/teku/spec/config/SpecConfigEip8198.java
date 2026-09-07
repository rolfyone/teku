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

package tech.pegasys.teku.spec.config;

import java.util.List;
import java.util.Optional;
import tech.pegasys.teku.infrastructure.bytes.Bytes4;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;

public interface SpecConfigEip8198 extends SpecConfigHeze {

  static SpecConfigEip8198 required(final SpecConfig specConfig) {
    return specConfig
        .toVersionEip8198()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Expected EIP-8198 spec config but got: "
                        + specConfig.getClass().getSimpleName()));
  }

  @Override
  UInt64 getEip8198ForkEpoch();

  @Override
  Bytes4 getEip8198ForkVersion();

  List<SlotTimingParameters> getSlotDurationSchedule();

  @Override
  default Optional<SpecConfigEip8198> toVersionEip8198() {
    return Optional.of(this);
  }
}
