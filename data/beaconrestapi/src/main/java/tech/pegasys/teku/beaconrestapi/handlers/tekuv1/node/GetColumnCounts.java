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

package tech.pegasys.teku.beaconrestapi.handlers.tekuv1.node;

import static tech.pegasys.teku.infrastructure.http.HttpStatusCodes.SC_OK;
import static tech.pegasys.teku.infrastructure.http.RestApiConstants.CACHE_NONE;
import static tech.pegasys.teku.infrastructure.http.RestApiConstants.TAG_TEKU;
import static tech.pegasys.teku.infrastructure.json.types.CoreTypes.LONG_TYPE;
import static tech.pegasys.teku.infrastructure.json.types.CoreTypes.STRING_TYPE;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.http.Header;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import tech.pegasys.teku.api.DataProvider;
import tech.pegasys.teku.api.NodeDataProvider;
import tech.pegasys.teku.infrastructure.json.types.DeserializableTypeDefinition;
import tech.pegasys.teku.infrastructure.json.types.SerializableTypeDefinition;
import tech.pegasys.teku.infrastructure.restapi.endpoints.AsyncApiResponse;
import tech.pegasys.teku.infrastructure.restapi.endpoints.EndpointMetadata;
import tech.pegasys.teku.infrastructure.restapi.endpoints.ParameterMetadata;
import tech.pegasys.teku.infrastructure.restapi.endpoints.RestApiEndpoint;
import tech.pegasys.teku.infrastructure.restapi.endpoints.RestApiRequest;

public class GetColumnCounts extends RestApiEndpoint {
  public static final String ROUTE = "/teku/v1/node/column_counts";
  private static final ParameterMetadata<String> FILTER_PARAMETER =
      new ParameterMetadata<>(
          "filter",
          STRING_TYPE.withDescription("Only count columns that contain the specified filter."));

  private final NodeDataProvider provider;

  private static final SerializableTypeDefinition<Map<String, Long>> COLUMN_COUNTS_TYPE =
      DeserializableTypeDefinition.mapOf(STRING_TYPE, LONG_TYPE, TreeMap::new);

  private static final SerializableTypeDefinition<Map<String, Long>> RESPONSE_TYPE =
      SerializableTypeDefinition.<Map<String, Long>>object()
          .name("GetColumnCountsResponse")
          .withField("data", COLUMN_COUNTS_TYPE, Function.identity())
          .build();

  public GetColumnCounts(final DataProvider provider) {
    this(provider.getNodeDataProvider());
  }

  GetColumnCounts(final NodeDataProvider provider) {
    super(
        EndpointMetadata.get(ROUTE)
            .operationId("getColumnCounts")
            .summary("Get column counts")
            .description("Retrieves storage column counts from the live node database.")
            .tags(TAG_TEKU)
            .queryParam(FILTER_PARAMETER)
            .response(SC_OK, "Request successful", RESPONSE_TYPE)
            .build());
    this.provider = provider;
  }

  @Override
  public void handleRequest(final RestApiRequest request) throws JsonProcessingException {
    request.header(Header.CACHE_CONTROL, CACHE_NONE);
    final Optional<String> maybeFilter = request.getOptionalQueryParameter(FILTER_PARAMETER);
    request.respondAsync(
        provider
            .getColumnCounts(maybeFilter)
            .thenApply(TreeMap::new)
            .thenApply(AsyncApiResponse::respondOk));
  }
}
