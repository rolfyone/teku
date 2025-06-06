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

package tech.pegasys.teku.spec.config.builder;

import static com.google.common.base.Preconditions.checkNotNull;
import static tech.pegasys.teku.spec.config.SpecConfig.FAR_FUTURE_EPOCH;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import tech.pegasys.teku.infrastructure.bytes.Bytes4;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.spec.config.SpecConfig;
import tech.pegasys.teku.spec.config.SpecConfigAltair;
import tech.pegasys.teku.spec.config.SpecConfigAltairImpl;
import tech.pegasys.teku.spec.config.SpecConfigAndParent;

public class AltairBuilder implements ForkConfigBuilder<SpecConfig, SpecConfigAltair> {

  // Updated penalties
  private UInt64 inactivityPenaltyQuotientAltair;
  private Integer minSlashingPenaltyQuotientAltair;
  private Integer proportionalSlashingMultiplierAltair;

  // Misc
  private Integer syncCommitteeSize;
  private UInt64 inactivityScoreBias;
  private UInt64 inactivityScoreRecoveryRate;

  // Time
  private Integer epochsPerSyncCommitteePeriod;

  // Fork
  private Bytes4 altairForkVersion;
  private UInt64 altairForkEpoch;

  // Sync protocol
  private Integer minSyncCommitteeParticipants;
  private Integer updateTimeout;

  AltairBuilder() {}

  @Override
  public SpecConfigAndParent<SpecConfigAltair> build(
      final SpecConfigAndParent<SpecConfig> specConfigAndParent) {
    return SpecConfigAndParent.of(
        new SpecConfigAltairImpl(
            specConfigAndParent.specConfig(),
            inactivityPenaltyQuotientAltair,
            minSlashingPenaltyQuotientAltair,
            proportionalSlashingMultiplierAltair,
            syncCommitteeSize,
            inactivityScoreBias,
            inactivityScoreRecoveryRate,
            epochsPerSyncCommitteePeriod,
            altairForkVersion,
            altairForkEpoch,
            minSyncCommitteeParticipants,
            updateTimeout),
        specConfigAndParent);
  }

  @Override
  public void validate() {
    if (altairForkEpoch == null) {
      altairForkEpoch = FAR_FUTURE_EPOCH;
      altairForkVersion = SpecBuilderUtil.PLACEHOLDER_FORK_VERSION;
      inactivityScoreBias = UInt64.valueOf(4);
      inactivityScoreRecoveryRate = UInt64.valueOf(16);
    }

    // Fill default zeros if fork is unsupported
    if (altairForkEpoch.equals(FAR_FUTURE_EPOCH)) {
      SpecBuilderUtil.fillMissingValuesWithZeros(this);
    }

    validateConstants();
  }

  @Override
  public Map<String, Object> getValidationMap() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("inactivityPenaltyQuotientAltair", inactivityPenaltyQuotientAltair);
    constants.put("minSlashingPenaltyQuotientAltair", minSlashingPenaltyQuotientAltair);
    constants.put("proportionalSlashingMultiplierAltair", proportionalSlashingMultiplierAltair);
    constants.put("syncCommitteeSize", syncCommitteeSize);
    constants.put("inactivityScoreBias", inactivityScoreBias);
    constants.put("inactivityScoreRecoveryRate", inactivityScoreRecoveryRate);
    constants.put("epochsPerSyncCommitteePeriod", epochsPerSyncCommitteePeriod);
    constants.put("altairForkVersion", altairForkVersion);
    constants.put("altairForkEpoch", altairForkEpoch);
    constants.put("minSyncCommitteeParticipants", minSyncCommitteeParticipants);
    constants.put("updateTimeout", updateTimeout);
    return constants;
  }

  @Override
  public void addOverridableItemsToRawConfig(final BiConsumer<String, Object> rawConfig) {
    rawConfig.accept("ALTAIR_FORK_EPOCH", altairForkEpoch);
  }

  public AltairBuilder inactivityPenaltyQuotientAltair(
      final UInt64 inactivityPenaltyQuotientAltair) {
    checkNotNull(inactivityPenaltyQuotientAltair);
    this.inactivityPenaltyQuotientAltair = inactivityPenaltyQuotientAltair;
    return this;
  }

  public AltairBuilder minSlashingPenaltyQuotientAltair(
      final Integer minSlashingPenaltyQuotientAltair) {
    checkNotNull(minSlashingPenaltyQuotientAltair);
    this.minSlashingPenaltyQuotientAltair = minSlashingPenaltyQuotientAltair;
    return this;
  }

  public AltairBuilder proportionalSlashingMultiplierAltair(
      final Integer proportionalSlashingMultiplierAltair) {
    checkNotNull(proportionalSlashingMultiplierAltair);
    this.proportionalSlashingMultiplierAltair = proportionalSlashingMultiplierAltair;
    return this;
  }

  public AltairBuilder syncCommitteeSize(final Integer syncCommitteeSize) {
    checkNotNull(syncCommitteeSize);
    this.syncCommitteeSize = syncCommitteeSize;
    return this;
  }

  public AltairBuilder epochsPerSyncCommitteePeriod(final Integer epochsPerSyncCommitteePeriod) {
    checkNotNull(epochsPerSyncCommitteePeriod);
    this.epochsPerSyncCommitteePeriod = epochsPerSyncCommitteePeriod;
    return this;
  }

  public AltairBuilder inactivityScoreBias(final UInt64 inactivityScoreBias) {
    checkNotNull(inactivityScoreBias);
    this.inactivityScoreBias = inactivityScoreBias;
    return this;
  }

  public AltairBuilder inactivityScoreRecoveryRate(final UInt64 inactivityScoreRecoveryRate) {
    checkNotNull(inactivityScoreRecoveryRate);
    this.inactivityScoreRecoveryRate = inactivityScoreRecoveryRate;
    return this;
  }

  public AltairBuilder altairForkVersion(final Bytes4 altairForkVersion) {
    checkNotNull(altairForkVersion);
    this.altairForkVersion = altairForkVersion;
    return this;
  }

  public AltairBuilder altairForkEpoch(final UInt64 altairForkEpoch) {
    checkNotNull(altairForkEpoch);
    this.altairForkEpoch = altairForkEpoch;
    return this;
  }

  public AltairBuilder minSyncCommitteeParticipants(final Integer minSyncCommitteeParticipants) {
    checkNotNull(minSyncCommitteeParticipants);
    this.minSyncCommitteeParticipants = minSyncCommitteeParticipants;
    return this;
  }

  public AltairBuilder updateTimeout(final Integer updateTimeout) {
    checkNotNull(updateTimeout);
    this.updateTimeout = updateTimeout;
    return this;
  }
}
