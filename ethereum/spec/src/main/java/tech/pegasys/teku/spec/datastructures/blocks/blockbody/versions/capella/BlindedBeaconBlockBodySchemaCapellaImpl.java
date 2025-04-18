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

package tech.pegasys.teku.spec.datastructures.blocks.blockbody.versions.capella;

import static tech.pegasys.teku.spec.schemas.registry.SchemaTypes.ATTESTATION_SCHEMA;
import static tech.pegasys.teku.spec.schemas.registry.SchemaTypes.ATTESTER_SLASHING_SCHEMA;
import static tech.pegasys.teku.spec.schemas.registry.SchemaTypes.EXECUTION_PAYLOAD_HEADER_SCHEMA;
import static tech.pegasys.teku.spec.schemas.registry.SchemaTypes.SIGNED_BLS_TO_EXECUTION_CHANGE_SCHEMA;

import it.unimi.dsi.fastutil.longs.LongList;
import java.util.Optional;
import java.util.function.Function;
import tech.pegasys.teku.infrastructure.async.SafeFuture;
import tech.pegasys.teku.infrastructure.ssz.SszList;
import tech.pegasys.teku.infrastructure.ssz.containers.ContainerSchema11;
import tech.pegasys.teku.infrastructure.ssz.primitive.SszBytes32;
import tech.pegasys.teku.infrastructure.ssz.schema.SszListSchema;
import tech.pegasys.teku.infrastructure.ssz.schema.SszPrimitiveSchemas;
import tech.pegasys.teku.infrastructure.ssz.tree.GIndexUtil;
import tech.pegasys.teku.infrastructure.ssz.tree.TreeNode;
import tech.pegasys.teku.spec.config.SpecConfigCapella;
import tech.pegasys.teku.spec.datastructures.blocks.Eth1Data;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.BeaconBlockBody;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.BeaconBlockBodyBuilder;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.common.BlockBodyFields;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.versions.altair.SyncAggregate;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.versions.altair.SyncAggregateSchema;
import tech.pegasys.teku.spec.datastructures.execution.versions.capella.ExecutionPayloadHeaderCapellaImpl;
import tech.pegasys.teku.spec.datastructures.execution.versions.capella.ExecutionPayloadHeaderSchemaCapella;
import tech.pegasys.teku.spec.datastructures.operations.Attestation;
import tech.pegasys.teku.spec.datastructures.operations.AttesterSlashing;
import tech.pegasys.teku.spec.datastructures.operations.Deposit;
import tech.pegasys.teku.spec.datastructures.operations.ProposerSlashing;
import tech.pegasys.teku.spec.datastructures.operations.SignedBlsToExecutionChange;
import tech.pegasys.teku.spec.datastructures.operations.SignedVoluntaryExit;
import tech.pegasys.teku.spec.datastructures.type.SszSignature;
import tech.pegasys.teku.spec.datastructures.type.SszSignatureSchema;
import tech.pegasys.teku.spec.schemas.registry.SchemaRegistry;

public class BlindedBeaconBlockBodySchemaCapellaImpl
    extends ContainerSchema11<
        BlindedBeaconBlockBodyCapellaImpl,
        SszSignature,
        Eth1Data,
        SszBytes32,
        SszList<ProposerSlashing>,
        SszList<AttesterSlashing>,
        SszList<Attestation>,
        SszList<Deposit>,
        SszList<SignedVoluntaryExit>,
        SyncAggregate,
        ExecutionPayloadHeaderCapellaImpl,
        SszList<SignedBlsToExecutionChange>>
    implements BlindedBeaconBlockBodySchemaCapella<BlindedBeaconBlockBodyCapellaImpl> {

  private BlindedBeaconBlockBodySchemaCapellaImpl(
      final String containerName,
      final NamedSchema<SszSignature> randaoReveal,
      final NamedSchema<Eth1Data> eth1Data,
      final NamedSchema<SszBytes32> graffiti,
      final NamedSchema<SszList<ProposerSlashing>> proposerSlashings,
      final NamedSchema<SszList<AttesterSlashing>> attesterSlashings,
      final NamedSchema<SszList<Attestation>> attestations,
      final NamedSchema<SszList<Deposit>> deposits,
      final NamedSchema<SszList<SignedVoluntaryExit>> voluntaryExits,
      final NamedSchema<SyncAggregate> syncAggregate,
      final NamedSchema<ExecutionPayloadHeaderCapellaImpl> executionPayloadHeader,
      final NamedSchema<SszList<SignedBlsToExecutionChange>> blsToExecutionChanges) {
    super(
        containerName,
        randaoReveal,
        eth1Data,
        graffiti,
        proposerSlashings,
        attesterSlashings,
        attestations,
        deposits,
        voluntaryExits,
        syncAggregate,
        executionPayloadHeader,
        blsToExecutionChanges);
  }

  public static BlindedBeaconBlockBodySchemaCapellaImpl create(
      final SpecConfigCapella specConfig,
      final String containerName,
      final SchemaRegistry schemaRegistry) {
    return new BlindedBeaconBlockBodySchemaCapellaImpl(
        containerName,
        namedSchema(BlockBodyFields.RANDAO_REVEAL, SszSignatureSchema.INSTANCE),
        namedSchema(BlockBodyFields.ETH1_DATA, Eth1Data.SSZ_SCHEMA),
        namedSchema(BlockBodyFields.GRAFFITI, SszPrimitiveSchemas.BYTES32_SCHEMA),
        namedSchema(
            BlockBodyFields.PROPOSER_SLASHINGS,
            SszListSchema.create(
                ProposerSlashing.SSZ_SCHEMA, specConfig.getMaxProposerSlashings())),
        namedSchema(
            BlockBodyFields.ATTESTER_SLASHINGS,
            SszListSchema.create(
                schemaRegistry.get(ATTESTER_SLASHING_SCHEMA),
                specConfig.getMaxAttesterSlashings())),
        namedSchema(
            BlockBodyFields.ATTESTATIONS,
            SszListSchema.create(
                schemaRegistry.get(ATTESTATION_SCHEMA), specConfig.getMaxAttestations())),
        namedSchema(
            BlockBodyFields.DEPOSITS,
            SszListSchema.create(Deposit.SSZ_SCHEMA, specConfig.getMaxDeposits())),
        namedSchema(
            BlockBodyFields.VOLUNTARY_EXITS,
            SszListSchema.create(
                SignedVoluntaryExit.SSZ_SCHEMA, specConfig.getMaxVoluntaryExits())),
        namedSchema(
            BlockBodyFields.SYNC_AGGREGATE,
            SyncAggregateSchema.create(specConfig.getSyncCommitteeSize())),
        namedSchema(
            BlockBodyFields.EXECUTION_PAYLOAD_HEADER,
            schemaRegistry.get(EXECUTION_PAYLOAD_HEADER_SCHEMA).toVersionCapellaRequired()),
        namedSchema(
            BlockBodyFields.BLS_TO_EXECUTION_CHANGES,
            SszListSchema.create(
                schemaRegistry.get(SIGNED_BLS_TO_EXECUTION_CHANGE_SCHEMA),
                specConfig.getMaxBlsToExecutionChanges())));
  }

  @Override
  public SafeFuture<BeaconBlockBody> createBlockBody(
      final Function<BeaconBlockBodyBuilder, SafeFuture<Void>> bodyBuilder) {
    final BeaconBlockBodyBuilderCapella builder = new BeaconBlockBodyBuilderCapella(null, this);
    return bodyBuilder.apply(builder).thenApply(__ -> builder.build());
  }

  @Override
  public BlindedBeaconBlockBodyCapellaImpl createEmpty() {
    return new BlindedBeaconBlockBodyCapellaImpl(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public SszListSchema<ProposerSlashing, ?> getProposerSlashingsSchema() {
    return (SszListSchema<ProposerSlashing, ?>) getFieldSchema3();
  }

  @SuppressWarnings("unchecked")
  @Override
  public SszListSchema<AttesterSlashing, ?> getAttesterSlashingsSchema() {
    return (SszListSchema<AttesterSlashing, ?>) getFieldSchema4();
  }

  @SuppressWarnings("unchecked")
  @Override
  public SszListSchema<Attestation, ?> getAttestationsSchema() {
    return (SszListSchema<Attestation, ?>) getFieldSchema5();
  }

  @SuppressWarnings("unchecked")
  @Override
  public SszListSchema<Deposit, ?> getDepositsSchema() {
    return (SszListSchema<Deposit, ?>) getFieldSchema6();
  }

  @SuppressWarnings("unchecked")
  @Override
  public SszListSchema<SignedVoluntaryExit, ?> getVoluntaryExitsSchema() {
    return (SszListSchema<SignedVoluntaryExit, ?>) getFieldSchema7();
  }

  @Override
  public SyncAggregateSchema getSyncAggregateSchema() {
    return (SyncAggregateSchema) getFieldSchema8();
  }

  @Override
  public BlindedBeaconBlockBodyCapellaImpl createFromBackingNode(final TreeNode node) {
    return new BlindedBeaconBlockBodyCapellaImpl(this, node);
  }

  @Override
  public ExecutionPayloadHeaderSchemaCapella getExecutionPayloadHeaderSchema() {
    return (ExecutionPayloadHeaderSchemaCapella) getFieldSchema9();
  }

  @SuppressWarnings("unchecked")
  @Override
  public SszListSchema<SignedBlsToExecutionChange, ?> getBlsToExecutionChanges() {
    return (SszListSchema<SignedBlsToExecutionChange, ?>) getFieldSchema10();
  }

  @Override
  public Optional<BlindedBeaconBlockBodySchemaCapella<?>> toBlindedVersionCapella() {
    return Optional.of(this);
  }

  @Override
  public LongList getBlindedNodeGeneralizedIndices() {
    return GIndexUtil.gIdxComposeAll(
        getChildGeneralizedIndex(getFieldIndex(BlockBodyFields.EXECUTION_PAYLOAD_HEADER)),
        getExecutionPayloadHeaderSchema().getBlindedNodeGeneralizedIndices());
  }
}
