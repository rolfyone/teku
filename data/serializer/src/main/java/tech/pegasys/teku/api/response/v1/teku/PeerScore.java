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

package tech.pegasys.teku.api.response.v1.teku;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeerScore {

  @JsonProperty("peer_id")
  @Schema(
      type = "string",
      description =
          "Cryptographic hash of a peer’s public key. "
              + "'[Read more](https://docs.libp2p.io/concepts/peer-id/)",
      example = "QmYyQSo1c1Ym7orWxLYvCrM2EmxFTANf8wXmmE7DWjhx5N")
  public final String peerId;

  @JsonProperty("gossip_score")
  @Schema(
      type = "string",
      format = "number",
      description = "Gossip score for the associated peer.",
      example = "1.2")
  public final Double gossipScore;

  @JsonCreator
  public PeerScore(
      @JsonProperty("peer_id") final String peerId,
      @JsonProperty("gossip_score") final Double gossipScore) {
    this.peerId = peerId;
    this.gossipScore = gossipScore;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PeerScore)) {
      return false;
    }
    PeerScore peerScore = (PeerScore) o;
    return Objects.equals(peerId, peerScore.peerId)
        && Objects.equals(gossipScore, peerScore.gossipScore);
  }

  @Override
  public int hashCode() {
    return Objects.hash(peerId, gossipScore);
  }
}
