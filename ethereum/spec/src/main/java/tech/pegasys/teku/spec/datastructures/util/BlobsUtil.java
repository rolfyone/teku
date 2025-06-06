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

package tech.pegasys.teku.spec.datastructures.util;

import static tech.pegasys.teku.spec.config.SpecConfigDeneb.BLS_MODULUS;
import static tech.pegasys.teku.spec.config.SpecConfigDeneb.VERSIONED_HASH_VERSION_KZG;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.units.bigints.UInt256;
import tech.pegasys.teku.infrastructure.crypto.Hash;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.kzg.KZG;
import tech.pegasys.teku.kzg.KZGCellAndProof;
import tech.pegasys.teku.kzg.KZGCommitment;
import tech.pegasys.teku.kzg.KZGProof;
import tech.pegasys.teku.spec.Spec;
import tech.pegasys.teku.spec.datastructures.blobs.versions.deneb.Blob;
import tech.pegasys.teku.spec.datastructures.blobs.versions.deneb.BlobSchema;

public class BlobsUtil {

  private static final int RANDOM_SEED = 5566;
  private static final Random RND = new Random(RANDOM_SEED);

  // the following are raw bytes of a SignedBlobTransaction ssz object. These bytes precede the
  // actual list of versioned hashes representing the commitments
  private static final Bytes MAGIC_BLOB_TRANSACTION_PREFIX =
      Bytes.fromHexString(
          "0x03450000000000000000000000000000000000000000000000000000000000000000"
              + "0000000000000000000000000000000000000000000000000000000000000000000000"
              + "0000000000000000000000000000000000000000000000000000000000000000000000"
              + "0000000000000000000000000000000000000000000000000000000000000000000000"
              + "0000000000000000000000000000000000000000000000000000000000000000000000"
              + "0000000000000000c00000000000000000000000000000000000000000000000000000"
              + "000000000000000000c1000000c1000000000000000000000000000000000000000000"
              + "0000000000000000000000000000c100000000");

  private final Spec spec;
  private final KZG kzg;

  public BlobsUtil(final Spec spec, final KZG kzg) {
    this.spec = spec;
    this.kzg = kzg;
  }

  public Bytes generateRawBlobTransactionFromKzgCommitments(
      final List<KZGCommitment> kzgCommitments) {

    Bytes blobTransaction = MAGIC_BLOB_TRANSACTION_PREFIX;

    for (final KZGCommitment kzgCommitment : kzgCommitments) {
      blobTransaction =
          Bytes.concatenate(
              blobTransaction,
              VERSIONED_HASH_VERSION_KZG,
              Hash.sha256(kzgCommitment.getBytesCompressed()).slice(1));
    }
    return blobTransaction;
  }

  public List<KZGCommitment> blobsToKzgCommitments(final List<Blob> blobs) {
    return blobs.stream().parallel().map(Blob::getBytes).map(kzg::blobToKzgCommitment).toList();
  }

  public KZGProof computeKzgProof(final Blob blob, final KZGCommitment kzgCommitment) {
    return kzg.computeBlobKzgProof(blob.getBytes(), kzgCommitment);
  }

  @VisibleForTesting
  @SuppressWarnings("deprecation")
  public List<KZGProof> computeKzgCellProofs(final Blob blob) {
    return kzg.computeCellsAndProofs(blob.getBytes()).stream().map(KZGCellAndProof::proof).toList();
  }

  public List<KZGProof> computeKzgProofs(
      final List<Blob> blobs, final List<KZGCommitment> kzgCommitments) {
    return Streams.zip(blobs.stream(), kzgCommitments.stream(), Pair::of)
        .parallel()
        .map(
            blobAndCommitment ->
                computeKzgProof(blobAndCommitment.getLeft(), blobAndCommitment.getRight()))
        .toList();
  }

  public List<Blob> generateBlobs(final UInt64 slot, final int count) {
    return IntStream.range(0, count).mapToObj(__ -> generateBlob(slot)).toList();
  }

  private Blob generateBlob(final UInt64 slot) {
    final int fieldElementsPerBlob = getFieldElementsPerBlob(slot);
    final Bytes rawBlob =
        IntStream.range(0, fieldElementsPerBlob)
            .mapToObj(__ -> randomBLSFieldElement())
            .map(fieldElement -> Bytes.wrap(fieldElement.toArray(ByteOrder.BIG_ENDIAN)))
            .reduce(Bytes::wrap)
            .orElse(Bytes.EMPTY);

    return getBlobSchema(slot).create(rawBlob);
  }

  private static UInt256 randomBLSFieldElement() {
    while (true) {
      final BigInteger attempt = new BigInteger(BLS_MODULUS.bitLength(), RND);
      if (attempt.compareTo(BLS_MODULUS) < 0) {
        return UInt256.valueOf(attempt);
      }
    }
  }

  private int getFieldElementsPerBlob(final UInt64 slot) {
    return spec.atSlot(slot).getConfig().toVersionDeneb().orElseThrow().getFieldElementsPerBlob();
  }

  private BlobSchema getBlobSchema(final UInt64 slot) {
    return spec.atSlot(slot).getSchemaDefinitions().toVersionDeneb().orElseThrow().getBlobSchema();
  }
}
