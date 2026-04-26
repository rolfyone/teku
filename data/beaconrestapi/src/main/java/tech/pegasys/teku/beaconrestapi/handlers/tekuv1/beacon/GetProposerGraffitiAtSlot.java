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

package tech.pegasys.teku.beaconrestapi.handlers.tekuv1.beacon;

import static tech.pegasys.teku.beaconrestapi.BeaconRestApiTypes.PARAMETER_BLOCK_ID;
import static tech.pegasys.teku.ethereum.json.types.EthereumTypes.ETH_CONSENSUS_HEADER_TYPE;
import static tech.pegasys.teku.infrastructure.http.HttpStatusCodes.SC_OK;
import static tech.pegasys.teku.infrastructure.http.RestApiConstants.TAG_EXPERIMENTAL;
import static tech.pegasys.teku.infrastructure.http.RestApiConstants.TAG_TEKU;
import static tech.pegasys.teku.infrastructure.json.types.CoreTypes.BYTES32_TYPE;
import static tech.pegasys.teku.infrastructure.json.types.CoreTypes.UINT64_TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Optional;
import java.util.function.Function;
import tech.pegasys.teku.api.ChainDataProvider;
import tech.pegasys.teku.api.DataProvider;
import tech.pegasys.teku.infrastructure.async.SafeFuture;
import tech.pegasys.teku.infrastructure.json.types.SerializableTypeDefinition;
import tech.pegasys.teku.infrastructure.restapi.endpoints.AsyncApiResponse;
import tech.pegasys.teku.infrastructure.restapi.endpoints.EndpointMetadata;
import tech.pegasys.teku.infrastructure.restapi.endpoints.RestApiEndpoint;
import tech.pegasys.teku.infrastructure.restapi.endpoints.RestApiRequest;

public class GetProposerGraffitiAtSlot extends RestApiEndpoint {
  public static final String ROUTE = "/teku/v1/beacon/proposer_graffiti/{block_id}";

  @SuppressWarnings("unused")
  private final ChainDataProvider chainDataProvider;

  static final SerializableTypeDefinition<ChainDataProvider.ProposerGraffitiAtSlotResponse>
      PROPOSER_GRAFFITI_WRAPPER =
          SerializableTypeDefinition.<ChainDataProvider.ProposerGraffitiAtSlotResponse>object()
              .name("ProposerGraffitiAtSlotResponse")
              .description("Proposer Graffiti at slot response")
              .withField(
                  "slot", UINT64_TYPE, ChainDataProvider.ProposerGraffitiAtSlotResponse::slot)
              .withField(
                  "proposer",
                  UINT64_TYPE,
                  ChainDataProvider.ProposerGraffitiAtSlotResponse::proposer)
              .withOptionalField(
                  "graffiti",
                  BYTES32_TYPE,
                  ChainDataProvider.ProposerGraffitiAtSlotResponse::graffiti)
              .withField(
                  "withdrawal_credentials",
                  BYTES32_TYPE,
                  ChainDataProvider.ProposerGraffitiAtSlotResponse::withdrawalAddress)
              .build();
  static final SerializableTypeDefinition<ChainDataProvider.ProposerGraffitiAtSlotResponse>
      RESPONSE_TYPE =
          SerializableTypeDefinition.<ChainDataProvider.ProposerGraffitiAtSlotResponse>object()
              .name("GetProposerGraffitiResponseType")
              .withField("data", PROPOSER_GRAFFITI_WRAPPER, Function.identity())
              .build();

  public GetProposerGraffitiAtSlot(final DataProvider provider) {
    this(provider.getChainDataProvider());
  }

  GetProposerGraffitiAtSlot(final ChainDataProvider chainDataProvider) {
    super(
        EndpointMetadata.get(ROUTE)
            .operationId("getProposerGraffitiAtSlot")
            .summary("Get Proposer Graffiti")
            .description("Retrieves proposer graffiti data at a given slot.")
            .tags(TAG_TEKU, TAG_EXPERIMENTAL)
            .pathParam(PARAMETER_BLOCK_ID)
            .response(SC_OK, "Request successful", RESPONSE_TYPE, ETH_CONSENSUS_HEADER_TYPE)
            .withNotFoundResponse()
            .withChainDataResponses()
            .build());
    this.chainDataProvider = chainDataProvider;
  }

  @Override
  public void handleRequest(final RestApiRequest request) throws JsonProcessingException {
    final SafeFuture<Optional<ChainDataProvider.ProposerGraffitiAtSlotResponse>> future =
        chainDataProvider.getProposerGraffitiAtSlot(request.getPathParameter(PARAMETER_BLOCK_ID));
    request.respondAsync(
        future.thenApply(
            maybeResponse ->
                maybeResponse
                    .map(AsyncApiResponse::respondOk)
                    .orElse(AsyncApiResponse.respondNotFound())));
  }
}
