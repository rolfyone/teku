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

import tech.pegasys.teku.infrastructure.unsigned.UInt64;

/**
 * EIP-8198: an entry of the {@code SLOT_DURATION_SCHEDULE} configuration field, from the earlier
 * form of the EIP that some network configs still carry. It is only validated, so that a slot
 * duration change is not silently ignored.
 */
public record SlotDurationScheduleEntry(UInt64 epoch, int slotDurationMs) {}
