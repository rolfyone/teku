/*
 * Copyright Consensys Software Inc., 2024
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

package tech.pegasys.teku.infrastructure.async.stream;

class TransformAsyncIterator<S, T> extends AsyncIterator<T> {
  private final AsyncIterator<S> delegateIterator;
  private final AsyncStreamTransformer<S, T> streamTransformer;

  public TransformAsyncIterator(
      final AsyncIterator<S> delegateIterator,
      final AsyncStreamTransformer<S, T> streamTransformer) {
    this.delegateIterator = delegateIterator;
    this.streamTransformer = streamTransformer;
  }

  @Override
  public void iterate(final AsyncStreamHandler<T> callback) {
    delegateIterator.iterate(streamTransformer.process(callback));
  }
}
