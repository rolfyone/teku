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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.tuweni.bytes.Bytes32;
import org.junit.jupiter.api.Test;
import tech.pegasys.teku.spec.datastructures.execution.ClientVersion;

class ReportingBuilderTest {

  @Test
  void returnsZeroWhenReportingDisabled() {
    final ReportingBuilder builder = new ReportingBuilder(false);
    assertThat(builder.buildReporting()).isEqualTo(Bytes32.ZERO);
  }

  @Test
  void returnsNonZeroWhenReportingEnabled() {
    final ReportingBuilder builder = new ReportingBuilder(true);
    assertThat(builder.buildReporting()).isNotEqualTo(Bytes32.ZERO);
  }

  @Test
  void versionByteIsOne() {
    final ReportingBuilder builder = new ReportingBuilder(true);
    assertThat(builder.buildReporting().get(0)).isEqualTo((byte) 0x01);
  }

  @Test
  void clIdIsTekuWhenNoElVersion() {
    final ReportingBuilder builder = new ReportingBuilder(true);
    final Bytes32 reporting = builder.buildReporting();
    // byte 2: (CL_TEKU << 4) | el_id; el_id=0 when EL not known
    assertThat(reporting.get(2)).isEqualTo((byte) (8 << 4));
  }

  @Test
  void elIdEncodedForGeth() {
    final ReportingBuilder builder = new ReportingBuilder(true);
    final ClientVersion geth = mock(ClientVersion.class);
    when(geth.code()).thenReturn("GE");
    builder.onExecutionClientVersion(geth);

    final Bytes32 reporting = builder.buildReporting();
    // byte 2: (8 << 4) | 6 (Geth=6)
    assertThat(reporting.get(2)).isEqualTo((byte) ((8 << 4) | 6));
  }

  @Test
  void elIdEncodedForNethermind() {
    final ReportingBuilder builder = new ReportingBuilder(true);
    final ClientVersion nm = mock(ClientVersion.class);
    when(nm.code()).thenReturn("NM");
    builder.onExecutionClientVersion(nm);

    assertThat(builder.buildReporting().get(2)).isEqualTo((byte) ((8 << 4) | 7));
  }

  @Test
  void unknownElClientProducesZeroElId() {
    final ReportingBuilder builder = new ReportingBuilder(true);
    final ClientVersion unknown = mock(ClientVersion.class);
    when(unknown.code()).thenReturn("XX");
    builder.onExecutionClientVersion(unknown);

    assertThat(builder.buildReporting().get(2)).isEqualTo((byte) (8 << 4));
  }

  @Test
  void notAvailableResetsToZeroElId() {
    final ReportingBuilder builder = new ReportingBuilder(true);
    final ClientVersion geth = mock(ClientVersion.class);
    when(geth.code()).thenReturn("GE");
    builder.onExecutionClientVersion(geth);
    builder.onExecutionClientVersionNotAvailable();

    assertThat(builder.buildReporting().get(2)).isEqualTo((byte) (8 << 4));
  }

  @Test
  void reservedBytesAreZero() {
    final ReportingBuilder builder = new ReportingBuilder(true);
    final Bytes32 reporting = builder.buildReporting();
    for (int i = 3; i < 32; i++) {
      assertThat(reporting.get(i)).as("byte %d should be 0x00", i).isEqualTo((byte) 0x00);
    }
  }
}
