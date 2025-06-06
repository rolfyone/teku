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

package tech.pegasys.teku.spec.datastructures.builder.versions.deneb;

import java.util.Optional;
import tech.pegasys.teku.infrastructure.ssz.containers.Container2;
import tech.pegasys.teku.infrastructure.ssz.tree.TreeNode;
import tech.pegasys.teku.spec.datastructures.builder.BuilderPayload;
import tech.pegasys.teku.spec.datastructures.builder.versions.fulu.BlobsBundleFulu;
import tech.pegasys.teku.spec.datastructures.execution.ExecutionPayload;

public class ExecutionPayloadAndBlobsBundle
    extends Container2<ExecutionPayloadAndBlobsBundle, ExecutionPayload, BlobsBundleDeneb>
    implements BuilderPayload {

  ExecutionPayloadAndBlobsBundle(
      final ExecutionPayloadAndBlobsBundleSchema type, final TreeNode backingTreeNode) {
    super(type, backingTreeNode);
  }

  public ExecutionPayloadAndBlobsBundle(
      final ExecutionPayloadAndBlobsBundleSchema schema,
      final ExecutionPayload executionPayload,
      final BlobsBundleDeneb blobsBundle) {
    super(schema, executionPayload, blobsBundle);
  }

  @Override
  public ExecutionPayload getExecutionPayload() {
    return getField0();
  }

  public BlobsBundleDeneb getBlobsBundle() {
    return getField1();
  }

  @Override
  public Optional<BlobsBundleDeneb> getOptionalBlobsBundle() {
    return Optional.of(getBlobsBundle());
  }

  @Override
  public Optional<BlobsBundleFulu> getOptionalBlobsCellBundle() {
    return Optional.empty();
  }
}
