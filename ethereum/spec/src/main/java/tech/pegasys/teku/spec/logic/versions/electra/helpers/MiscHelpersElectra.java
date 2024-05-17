/*
 * Copyright Consensys Software Inc., 2024
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

package tech.pegasys.teku.spec.logic.versions.electra.helpers;

import static com.google.common.base.Preconditions.checkArgument;
import static tech.pegasys.teku.infrastructure.crypto.Hash.getSha256Instance;
import static tech.pegasys.teku.spec.logic.common.helpers.MathHelpers.uint64ToBytes;

import com.google.common.primitives.UnsignedBytes;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Optional;
import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.infrastructure.crypto.Sha256;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.spec.config.SpecConfig;
import tech.pegasys.teku.spec.config.SpecConfigDeneb;
import tech.pegasys.teku.spec.config.SpecConfigElectra;
import tech.pegasys.teku.spec.datastructures.state.beaconstate.BeaconState;
import tech.pegasys.teku.spec.datastructures.state.beaconstate.versions.electra.BeaconStateElectra;
import tech.pegasys.teku.spec.logic.common.helpers.MiscHelpers;
import tech.pegasys.teku.spec.logic.common.helpers.Predicates;
import tech.pegasys.teku.spec.logic.versions.deneb.helpers.MiscHelpersDeneb;
import tech.pegasys.teku.spec.schemas.SchemaDefinitions;
import tech.pegasys.teku.spec.schemas.SchemaDefinitionsDeneb;

public class MiscHelpersElectra extends MiscHelpersDeneb {

  public MiscHelpersElectra(
      final SpecConfig specConfig,
      final Predicates predicates,
      final SchemaDefinitions schemaDefinitions) {
    super(
        SpecConfigDeneb.required(specConfig),
        predicates,
        SchemaDefinitionsDeneb.required(schemaDefinitions));
  }

  public static MiscHelpersElectra required(final MiscHelpers miscHelpers) {
    return miscHelpers
        .toVersionElectra()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Expected Electra misc helpers but got: "
                        + miscHelpers.getClass().getSimpleName()));
  }

  @Override
  public Optional<MiscHelpersElectra> toVersionElectra() {
    return Optional.of(this);
  }

  @Override
  public int computeProposerIndex(
      final BeaconState state, final IntList indices, final Bytes32 seed) {
    checkArgument(!indices.isEmpty(), "compute_proposer_index indices must not be empty");

    final Sha256 sha256 = getSha256Instance();

    int i = 0;
    final int total = indices.size();
    byte[] hash = null;
    final UInt64 maxEffectiveBalanceElectra =
        SpecConfigElectra.required(specConfig).getMaxEffectiveBalanceElectra();
    while (true) {
      final int candidateIndex = indices.getInt(computeShuffledIndex(i % total, total, seed));
      if (i % 32 == 0) {
        hash = sha256.digest(seed, uint64ToBytes(Math.floorDiv(i, 32L)));
      }
      final int randomByte = UnsignedBytes.toInt(hash[i % 32]);
      final UInt64 effectiveBalance =
          state.getValidators().get(candidateIndex).getEffectiveBalance();
      if (effectiveBalance
          .times(MAX_RANDOM_BYTE)
          .isGreaterThanOrEqualTo(maxEffectiveBalanceElectra.times(randomByte))) {
        return candidateIndex;
      }
      i++;
    }
  }

  @Override
  public boolean isFormerDepositMechanismDisabled(final BeaconState state) {
    // if the next deposit to be processed by Eth1Data poll has the index of the first deposit
    // processed with the new deposit flow, i.e. `eth1_deposit_index ==
    // deposit_receipts_start_index`, we should stop Eth1Data deposits processing
    return state
        .getEth1DepositIndex()
        .equals(BeaconStateElectra.required(state).getDepositReceiptsStartIndex());
  }
}
