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

package tech.pegasys.teku.spec.logic.versions.eip8198.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.spec.config.SpecConfigEip8198;
import tech.pegasys.teku.spec.config.SlotTimingParameters;
import tech.pegasys.teku.spec.datastructures.forkchoice.MutableStore;
import tech.pegasys.teku.spec.datastructures.forkchoice.ReadOnlyStore;
import tech.pegasys.teku.spec.logic.versions.eip8198.helpers.MiscHelpersEip8198;
import tech.pegasys.teku.spec.logic.versions.gloas.block.BlockProcessorGloas;
import tech.pegasys.teku.spec.logic.versions.gloas.helpers.BeaconStateAccessorsGloas;
import tech.pegasys.teku.spec.logic.versions.gloas.helpers.BeaconStateMutatorsGloas;
import tech.pegasys.teku.spec.logic.versions.gloas.statetransition.epoch.EpochProcessorGloas;
import tech.pegasys.teku.spec.logic.versions.gloas.util.AttestationUtilGloas;
import tech.pegasys.teku.spec.logic.versions.gloas.util.ForkChoiceUtilGloas;
import tech.pegasys.teku.spec.logic.versions.gloas.withdrawals.WithdrawalsHelpersGloas;

public class ForkChoiceUtilEip8198 extends ForkChoiceUtilGloas {

  private static final Logger LOG = LogManager.getLogger();

  private final MiscHelpersEip8198 miscHelpersEip8198;

  public ForkChoiceUtilEip8198(
      final SpecConfigEip8198 specConfig,
      final BeaconStateAccessorsGloas beaconStateAccessors,
      final BeaconStateMutatorsGloas beaconStateMutators,
      final EpochProcessorGloas epochProcessor,
      final AttestationUtilGloas attestationUtil,
      final MiscHelpersEip8198 miscHelpers,
      final WithdrawalsHelpersGloas withdrawalsHelpers,
      final BlockProcessorGloas blockProcessor) {
    super(
        specConfig,
        beaconStateAccessors,
        beaconStateMutators,
        epochProcessor,
        attestationUtil,
        miscHelpers,
        withdrawalsHelpers,
        blockProcessor);
    this.miscHelpersEip8198 = miscHelpers;
  }

  @Override
  protected UInt64 getCurrentSlot(final ReadOnlyStore store) {
    return miscHelpersEip8198.computeSlotAtTimeMillis(
        store.getGenesisTimeMillis(), store.getTimeInMillis());
  }

  @Override
  public void onTick(final MutableStore store, final UInt64 timeMillis) {
    final UInt64 previousSlot = getCurrentSlot(store);
    super.onTick(store, timeMillis);
    final UInt64 currentSlot = getCurrentSlot(store);

    if (currentSlot.isGreaterThan(previousSlot)) {
      final UInt64 previousEpoch = miscHelpersEip8198.computeEpochAtSlot(previousSlot);
      final UInt64 currentEpoch = miscHelpersEip8198.computeEpochAtSlot(currentSlot);
      final SlotTimingParameters previousParams =
          miscHelpersEip8198.getSlotTimingParameters(previousEpoch);
      final SlotTimingParameters currentParams =
          miscHelpersEip8198.getSlotTimingParameters(currentEpoch);
      if (previousParams.slotDurationMs() != currentParams.slotDurationMs()) {
        LOG.info(
            "Slot duration changed from {} ms to {} ms at epoch {} (slot {})",
            previousParams.slotDurationMs(),
            currentParams.slotDurationMs(),
            currentEpoch,
            currentSlot);
      }
    }
  }
}
