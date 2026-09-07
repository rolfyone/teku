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

package tech.pegasys.teku.spec.config;

import java.util.Optional;

public class DelegatingSpecConfigHeze extends DelegatingSpecConfigGloas implements SpecConfigHeze {
  private final SpecConfigHeze delegate;

  public DelegatingSpecConfigHeze(final SpecConfigHeze specConfig) {
    super(specConfig);
    this.delegate = SpecConfigHeze.required(specConfig);
  }

  @Override
  public int getInclusionListDueBps() {
    return delegate.getInclusionListDueBps();
  }

  @Override
  public int getMaxRequestInclusionList() {
    return delegate.getMaxRequestInclusionList();
  }

  @Override
  public int getMaxTransactionsBytesPerInclusionList() {
    return delegate.getMaxTransactionsBytesPerInclusionList();
  }

  @Override
  public int getInclusionListCommitteeSize() {
    return delegate.getInclusionListCommitteeSize();
  }

  @Override
  public Optional<SpecConfigHeze> toVersionHeze() {
    return delegate.toVersionHeze();
  }
}
