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

package tech.pegasys.teku.validator.coordinator;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.ethereum.executionclient.ExecutionClientVersionChannel;
import tech.pegasys.teku.spec.datastructures.execution.ClientVersion;

/**
 * Builds the EIP-8359 {@code reporting} field for beacon block bodies.
 *
 * <p>Encoding (version 0x01): byte 0 = 0x01 (version), byte 1 = (setup << 3) | threshold, bytes
 * 2-15 = CL/EL client pairs one byte each: (cl_id << 4) | el_id, bytes 16-31 = 0x00 (reserved).
 */
public class ReportingBuilder implements ExecutionClientVersionChannel {

  private static final byte VERSION = 0x01;
  private static final int SETUP_SIMPLE = 3;

  // EIP-8359 numeric IDs keyed by 2-letter Engine API code (separate spaces for CL and EL)
  private static final Map<String, Integer> CL_CLIENT_IDS =
      Map.of(
          "LH", 1, // Lighthouse
          "LS", 2, // Lodestar
          "NB", 3, // Nimbus CL
          "PM", 4, // Prysm
          "TK", 8 // Teku
          );
  private static final Map<String, Integer> EL_CLIENT_IDS =
      Map.of(
          "BU", 3, // Besu
          "EG", 4, // Erigon
          "EX", 5, // Ethrex
          "GE", 6, // Geth
          "NM", 7, // Nethermind
          "NB", 8, // Nimbus EL
          "RH", 9 // Reth
          );

  private final boolean reportingEnabled;
  private volatile Optional<ClientVersion> executionClientVersion = Optional.empty();

  public ReportingBuilder(final boolean reportingEnabled) {
    this.reportingEnabled = reportingEnabled;
  }

  @Override
  public void onExecutionClientVersion(final ClientVersion executionClientVersion) {
    this.executionClientVersion = Optional.of(executionClientVersion);
  }

  @Override
  public void onExecutionClientVersionNotAvailable() {
    this.executionClientVersion = Optional.empty();
  }

  public Bytes32 buildReporting() {
    if (!reportingEnabled) {
      return Bytes32.ZERO;
    }
    final byte[] out = new byte[32];
    out[0] = VERSION;
    out[1] = (byte) ((SETUP_SIMPLE << 3) | 1); // setup=Simple, threshold=1-of-N
    out[2] = (byte) ((resolveClClientId() << 4) | resolveElClientId());
    return Bytes32.wrap(out);
  }

  private static int resolveClClientId() {
    return CL_CLIENT_IDS.getOrDefault(ClientVersion.TEKU_CLIENT_CODE, 0);
  }

  private int resolveElClientId() {
    return executionClientVersion
        .map(cv -> EL_CLIENT_IDS.getOrDefault(normalizeCode(cv.code()), 0))
        .orElse(0);
  }

  private static String normalizeCode(final String code) {
    if (code == null || code.length() < 2) {
      return "";
    }
    return code.substring(0, 2).toUpperCase(Locale.ROOT);
  }
}
