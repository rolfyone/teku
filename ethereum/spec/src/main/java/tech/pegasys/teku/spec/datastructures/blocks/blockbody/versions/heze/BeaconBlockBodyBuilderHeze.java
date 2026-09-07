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

package tech.pegasys.teku.spec.datastructures.blocks.blockbody.versions.heze;

import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.infrastructure.ssz.primitive.SszBytes32;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.BeaconBlockBody;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.BeaconBlockBodyBuilder;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.BeaconBlockBodySchema;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.versions.gloas.BeaconBlockBodyBuilderGloas;
import tech.pegasys.teku.spec.datastructures.type.SszSignature;

public class BeaconBlockBodyBuilderHeze extends BeaconBlockBodyBuilderGloas {

  private Bytes32 reporting;

  public BeaconBlockBodyBuilderHeze(
      final BeaconBlockBodySchema<? extends BeaconBlockBodyHeze> schema) {
    super(schema);
  }

  @Override
  public boolean supportsReporting() {
    return true;
  }

  @Override
  public BeaconBlockBodyBuilder reporting(final Bytes32 reporting) {
    this.reporting = reporting;
    return this;
  }

  @Override
  protected void validate() {
    super.validate();
    if (reporting == null) {
      reporting = Bytes32.ZERO;
    }
  }

  @Override
  public BeaconBlockBody build() {
    validate();
    final BeaconBlockBodySchemaHezeImpl schema =
        getAndValidateSchema(false, BeaconBlockBodySchemaHezeImpl.class);
    return new BeaconBlockBodyHezeImpl(
        schema,
        new SszSignature(randaoReveal),
        eth1Data,
        SszBytes32.of(graffiti),
        proposerSlashings,
        attesterSlashings,
        attestations,
        deposits,
        voluntaryExits,
        syncAggregate,
        getBlsToExecutionChanges(),
        signedExecutionPayloadBid,
        payloadAttestations,
        parentExecutionRequests,
        SszBytes32.of(reporting));
  }
}
