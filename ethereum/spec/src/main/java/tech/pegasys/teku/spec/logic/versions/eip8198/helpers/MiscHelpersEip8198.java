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

package tech.pegasys.teku.spec.logic.versions.eip8198.helpers;

import java.util.List;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.spec.config.SlotTimingParameters;
import tech.pegasys.teku.spec.config.SpecConfigEip8198;
import tech.pegasys.teku.spec.logic.versions.gloas.helpers.MiscHelpersGloas;
import tech.pegasys.teku.spec.logic.versions.gloas.helpers.PredicatesGloas;
import tech.pegasys.teku.spec.schemas.SchemaDefinitionsGloas;

public class MiscHelpersEip8198 extends MiscHelpersGloas {

  private final SpecConfigEip8198 eip8198Config;

  public MiscHelpersEip8198(
      final SpecConfigEip8198 specConfig,
      final PredicatesGloas predicates,
      final SchemaDefinitionsGloas schemaDefinitions) {
    super(specConfig, predicates, schemaDefinitions);
    this.eip8198Config = specConfig;
  }

  public SlotTimingParameters getSlotTimingParameters(final UInt64 epoch) {
    final List<SlotTimingParameters> schedule = eip8198Config.getSlotDurationSchedule();
    SlotTimingParameters result = schedule.get(0);
    for (final SlotTimingParameters entry : schedule) {
      if (entry.epoch().isGreaterThan(epoch)) {
        break;
      }
      result = entry;
    }
    return result;
  }

  @Override
  public UInt64 computeSlotAtTimeMillis(
      final UInt64 genesisTimeMillis, final UInt64 currentTimeMillis) {
    if (currentTimeMillis.isLessThan(genesisTimeMillis)) {
      return UInt64.ZERO;
    }
    final List<SlotTimingParameters> schedule = eip8198Config.getSlotDurationSchedule();
    if (schedule.isEmpty()) {
      return super.computeSlotAtTimeMillis(genesisTimeMillis, currentTimeMillis);
    }

    UInt64 elapsedMs = currentTimeMillis.minus(genesisTimeMillis);
    UInt64 slotCount = UInt64.ZERO;
    final long slotsPerEpoch = specConfig.getSlotsPerEpoch();

    for (int i = 0; i < schedule.size(); i++) {
      final SlotTimingParameters entry = schedule.get(i);
      final UInt64 eraStartSlot = entry.epoch().times(slotsPerEpoch);
      final long eraSlotDurationMs = entry.slotDurationMs();

      if (i + 1 < schedule.size()) {
        final UInt64 nextEraStartSlot = schedule.get(i + 1).epoch().times(slotsPerEpoch);
        final UInt64 eraSlotsCount = nextEraStartSlot.minus(eraStartSlot);
        final UInt64 eraMaxMs = eraSlotsCount.times(eraSlotDurationMs);

        if (elapsedMs.isLessThan(eraMaxMs)) {
          return slotCount.plus(elapsedMs.dividedBy(eraSlotDurationMs));
        }
        elapsedMs = elapsedMs.minus(eraMaxMs);
        slotCount = slotCount.plus(eraSlotsCount);
      } else {
        return slotCount.plus(elapsedMs.dividedBy(eraSlotDurationMs));
      }
    }
    return slotCount;
  }

  @Override
  public UInt64 computeTimeMillisAtSlot(final UInt64 genesisTimeMillis, final UInt64 slot) {
    final List<SlotTimingParameters> schedule = eip8198Config.getSlotDurationSchedule();
    if (schedule.isEmpty()) {
      return super.computeTimeMillisAtSlot(genesisTimeMillis, slot);
    }

    final long slotsPerEpoch = specConfig.getSlotsPerEpoch();
    UInt64 accumulatedMs = UInt64.ZERO;
    UInt64 remainingSlots = slot;

    for (int i = 0; i < schedule.size(); i++) {
      final SlotTimingParameters entry = schedule.get(i);
      final UInt64 eraStartSlot = entry.epoch().times(slotsPerEpoch);
      final long eraSlotDurationMs = entry.slotDurationMs();

      if (i + 1 < schedule.size()) {
        final UInt64 nextEraStartSlot = schedule.get(i + 1).epoch().times(slotsPerEpoch);
        final UInt64 eraSlotsCount = nextEraStartSlot.minus(eraStartSlot);

        if (remainingSlots.isLessThanOrEqualTo(eraSlotsCount)) {
          return genesisTimeMillis
              .plus(accumulatedMs)
              .plus(remainingSlots.times(eraSlotDurationMs));
        }
        accumulatedMs = accumulatedMs.plus(eraSlotsCount.times(eraSlotDurationMs));
        remainingSlots = remainingSlots.minus(eraSlotsCount);
      } else {
        return genesisTimeMillis.plus(accumulatedMs).plus(remainingSlots.times(eraSlotDurationMs));
      }
    }
    return genesisTimeMillis.plus(accumulatedMs);
  }
}
