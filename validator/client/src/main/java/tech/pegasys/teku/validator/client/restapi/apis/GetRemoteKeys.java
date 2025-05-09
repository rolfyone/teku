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

package tech.pegasys.teku.validator.client.restapi.apis;

import static tech.pegasys.teku.infrastructure.http.HttpStatusCodes.SC_OK;
import static tech.pegasys.teku.validator.client.restapi.ValidatorRestApi.TAG_KEY_MANAGEMENT;

import com.fasterxml.jackson.core.JsonProcessingException;
import tech.pegasys.teku.infrastructure.restapi.endpoints.EndpointMetadata;
import tech.pegasys.teku.infrastructure.restapi.endpoints.RestApiEndpoint;
import tech.pegasys.teku.infrastructure.restapi.endpoints.RestApiRequest;
import tech.pegasys.teku.validator.client.KeyManager;
import tech.pegasys.teku.validator.client.restapi.ValidatorTypes;

public class GetRemoteKeys extends RestApiEndpoint {

  public static final String ROUTE = "/eth/v1/remotekeys";
  private final KeyManager keyManager;

  public GetRemoteKeys(final KeyManager keyManager) {
    super(
        EndpointMetadata.get(ROUTE)
            .operationId("ListRemoteKeys")
            .summary("List Remote Keys")
            .tags(TAG_KEY_MANAGEMENT)
            .withBearerAuthSecurity()
            .description("List all remote keys in use by a validator client")
            .response(SC_OK, "Success response", ValidatorTypes.LIST_REMOTE_KEYS_RESPONSE_TYPE)
            .withAuthenticationResponses()
            .build());
    this.keyManager = keyManager;
  }

  @Override
  public void handleRequest(final RestApiRequest request) throws JsonProcessingException {
    request.respondOk(keyManager.getRemoteValidatorKeys());
  }
}
