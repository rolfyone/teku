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

package tech.pegasys.teku.validator.client.signer;

import tech.pegasys.teku.infrastructure.async.SafeFuture;
import tech.pegasys.teku.spec.Spec;
import tech.pegasys.teku.spec.datastructures.blocks.BeaconBlock;
import tech.pegasys.teku.spec.datastructures.blocks.BlockContainer;
import tech.pegasys.teku.spec.datastructures.blocks.SignedBeaconBlock;
import tech.pegasys.teku.spec.datastructures.blocks.SignedBlockContainer;
import tech.pegasys.teku.spec.datastructures.state.ForkInfo;
import tech.pegasys.teku.validator.client.Validator;

public class BlockContainerSignerPhase0 implements BlockContainerSigner {

  private final Spec spec;

  public BlockContainerSignerPhase0(final Spec spec) {
    this.spec = spec;
  }

  @Override
  public SafeFuture<SignedBlockContainer> sign(
      final BlockContainer unsignedBlockContainer,
      final Validator validator,
      final ForkInfo forkInfo) {
    final BeaconBlock unsignedBlock = unsignedBlockContainer.getBlock();
    return validator
        .getSigner()
        .signBlock(unsignedBlock, forkInfo)
        .thenApply(signature -> SignedBeaconBlock.create(spec, unsignedBlock, signature));
  }
}
