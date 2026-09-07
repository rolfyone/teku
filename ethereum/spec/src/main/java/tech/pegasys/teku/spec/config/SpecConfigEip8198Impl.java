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
import java.util.Objects;
import java.util.Optional;
import tech.pegasys.teku.spec.SpecMilestone;

public class SpecConfigEip8198Impl extends DelegatingSpecConfigHeze implements SpecConfigEip8198 {

  private final List<SlotTimingParameters> slotDurationSchedule;

  public SpecConfigEip8198Impl(
      final SpecConfigHeze specConfig, final List<SlotTimingParameters> slotDurationSchedule) {
    super(specConfig);
    this.slotDurationSchedule = slotDurationSchedule;
  }

  @Override
  public SpecMilestone getMilestone() {
    return SpecMilestone.EIP8198;
  }

  @Override
  public List<SlotTimingParameters> getSlotDurationSchedule() {
    return slotDurationSchedule;
  }

  @Override
  public Optional<SpecConfigEip8198> toVersionEip8198() {
    return Optional.of(this);
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    SpecConfigEip8198Impl that = (SpecConfigEip8198Impl) o;
    return Objects.equals(slotDurationSchedule, that.slotDurationSchedule);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), slotDurationSchedule);
  }
}
