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

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tuweni.bytes.Bytes32;
import org.junit.jupiter.api.Test;
import tech.pegasys.teku.api.ChainDataProvider;
import tech.pegasys.teku.infrastructure.json.JsonUtil;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;

class GetProposerGraffitiAtSlotTest {

  private static final Logger LOG = LogManager.getLogger();
  private final Bytes32 graffiti = Bytes32.fromHexString("0xbaddbeef");
  private final ChainDataProvider.ProposerGraffitiAtSlotResponse data =
      new ChainDataProvider.ProposerGraffitiAtSlotResponse(
          UInt64.ONE, UInt64.ZERO, Optional.of(graffiti), Bytes32.ZERO);
  private final String graffitiResponse =
      "{\"slot\":\"1\",\"proposer\":\"0\",\"graffiti\":\"0x00000000000000000000000000000000000000000000000000000000baddbeef\",\"withdrawal_credentials\":\"0x0000000000000000000000000000000000000000000000000000000000000000\"}";

  @Test
  void shouldGenerateJson() throws JsonProcessingException {
    final String json =
        JsonUtil.serialize(data, GetProposerGraffitiAtSlot.PROPOSER_GRAFFITI_WRAPPER);
    assertThat(json).isEqualTo(graffitiResponse);
  }

  @Test
  void shouldGenerateResponseWrapper() throws JsonProcessingException {
    final String json = JsonUtil.serialize(data, GetProposerGraffitiAtSlot.RESPONSE_TYPE);
    LOG.info(json);
    assertThat(json).isEqualTo("{\"data\":" + graffitiResponse + "}");
  }
}
