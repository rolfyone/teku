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

package tech.pegasys.teku.spec.datastructures.networking.libp2p.rpc.bodyselector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.Map;
import org.junit.jupiter.api.Test;
import tech.pegasys.teku.spec.datastructures.networking.libp2p.rpc.RpcRequest;

class VersionBasedRpcRequestBodySelectorTest {

  @Test
  public void applyingVersionStringToBodySelectorFunctionShouldReturnCorrectRequest() {
    final RpcRequest request1 = mock(RpcRequest.class);
    final RpcRequest request2 = mock(RpcRequest.class);

    final String key1 = "/eth2/beacon_chain/req/status/1/";
    final String key2 = "/eth2/beacon_chain/req/status/2/";
    final VersionBasedRpcRequestBodySelector<RpcRequest> rpcRequestBodySelector =
        new VersionBasedRpcRequestBodySelector<>(
            Map.of(
                key1, request1,
                key2, request2));

    assertThat(rpcRequestBodySelector.getBody().apply(key1)).contains(request1);
    assertThat(rpcRequestBodySelector.getBody().apply(key2)).contains(request2);
  }

  @Test
  @SuppressWarnings({"unchecked"})
  public void cannotCreateBodySelectorWithNullMap() {
    assertThatThrownBy(() -> new VersionBasedRpcRequestBodySelector<>((Map) null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  public void whenMappingForKeyDoesNotExistShouldReturnEmpty() {
    final VersionBasedRpcRequestBodySelector<RpcRequest> rpcRequestBodySelector =
        new VersionBasedRpcRequestBodySelector<>(Map.of());

    assertThat(rpcRequestBodySelector.getBody().apply("foo")).isEmpty();
  }
}
