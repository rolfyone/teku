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

package tech.pegasys.teku.services.beaconchain;

import static tech.pegasys.teku.networks.EphemeryNetwork.MAX_EPHEMERY_SLOT;

import tech.pegasys.teku.ethereum.events.SlotEventsChannel;
import tech.pegasys.teku.infrastructure.async.SafeFuture;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.service.serviceutils.Service;

public class EphemerySlotValidationService extends Service implements SlotEventsChannel {

  @Override
  public void onSlot(final UInt64 slot) {
    if (slot.isGreaterThan(MAX_EPHEMERY_SLOT)) {
      throw new EphemeryLifecycleException(
          String.format(
              "Slot %s exceeds maximum allowed slot %s for ephemery network",
              slot, MAX_EPHEMERY_SLOT));
    }
  }

  @Override
  protected SafeFuture<?> doStart() {
    return SafeFuture.COMPLETE;
  }

  @Override
  protected SafeFuture<?> doStop() {
    return SafeFuture.COMPLETE;
  }
}
