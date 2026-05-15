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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.pegasys.teku.infrastructure.http.HttpStatusCodes.SC_BAD_REQUEST;
import static tech.pegasys.teku.infrastructure.http.HttpStatusCodes.SC_INTERNAL_SERVER_ERROR;
import static tech.pegasys.teku.infrastructure.http.HttpStatusCodes.SC_OK;
import static tech.pegasys.teku.infrastructure.restapi.MetadataTestUtil.getResponseStringFromMetadata;
import static tech.pegasys.teku.infrastructure.restapi.MetadataTestUtil.verifyMetadataErrorResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.pegasys.teku.beaconrestapi.AbstractMigratedBeaconHandlerTest;
import tech.pegasys.teku.infrastructure.async.SafeFuture;

public class GetColumnCountsTest extends AbstractMigratedBeaconHandlerTest {
  @BeforeEach
  void setup() {
    setHandler(new GetColumnCounts(nodeDataProvider));
  }

  @Test
  public void shouldReturnColumnCounts() throws Exception {
    final Map<String, Long> counts =
        Map.of("HOT_BLOCKS_BY_ROOT", 3L, "SLOTS_BY_FINALIZED_ROOT", 5L);
    when(nodeDataProvider.getColumnCounts(Optional.empty()))
        .thenReturn(SafeFuture.completedFuture(counts));

    handler.handleRequest(request);

    assertThat(request.getResponseCode()).isEqualTo(SC_OK);
    assertThat(request.getResponseBody()).isEqualTo(new TreeMap<>(counts));
  }

  @Test
  public void shouldPassFilterToProvider() throws Exception {
    final Map<String, Long> counts = Map.of("SIDECAR_BY_COLUMN_SLOT_AND_IDENTIFIER", 2L);
    request.setOptionalQueryParameter("filter", "SIDECAR");
    when(nodeDataProvider.getColumnCounts(Optional.of("SIDECAR")))
        .thenReturn(SafeFuture.completedFuture(counts));

    handler.handleRequest(request);

    assertThat(request.getResponseCode()).isEqualTo(SC_OK);
    verify(nodeDataProvider).getColumnCounts(Optional.of("SIDECAR"));
  }

  @Test
  void metadata_shouldHandle400() throws JsonProcessingException {
    verifyMetadataErrorResponse(handler, SC_BAD_REQUEST);
  }

  @Test
  void metadata_shouldHandle500() throws JsonProcessingException {
    verifyMetadataErrorResponse(handler, SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  void metadata_shouldHandle200() throws JsonProcessingException {
    final Map<String, Long> responseData =
        new TreeMap<>(Map.of("HOT_BLOCKS_BY_ROOT", 3L, "SLOTS_BY_FINALIZED_ROOT", 5L));

    final String data = getResponseStringFromMetadata(handler, SC_OK, responseData);

    assertThat(data)
        .isEqualTo("{\"data\":{\"HOT_BLOCKS_BY_ROOT\":\"3\",\"SLOTS_BY_FINALIZED_ROOT\":\"5\"}}");
  }
}
