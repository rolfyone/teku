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

package tech.pegasys.teku.spec.config.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import tech.pegasys.teku.spec.config.SlotTimingParameters;
import tech.pegasys.teku.spec.config.SpecConfigAndParent;
import tech.pegasys.teku.spec.config.SpecConfigEip8198;
import tech.pegasys.teku.spec.config.SpecConfigEip8198Impl;
import tech.pegasys.teku.spec.config.SpecConfigHeze;

public class Eip8198Builder extends BaseForkBuilder
    implements ForkConfigBuilder<SpecConfigHeze, SpecConfigEip8198> {

  private List<SlotTimingParameters> slotDurationSchedule = Collections.emptyList();

  Eip8198Builder() {}

  @Override
  public SpecConfigAndParent<SpecConfigEip8198> build(
      final SpecConfigAndParent<SpecConfigHeze> specConfigAndParent) {
    return SpecConfigAndParent.of(
        new SpecConfigEip8198Impl(specConfigAndParent.specConfig(), slotDurationSchedule),
        specConfigAndParent);
  }

  public Eip8198Builder slotDurationSchedule(
      final List<SlotTimingParameters> slotDurationSchedule) {
    checkNotNull(slotDurationSchedule);
    this.slotDurationSchedule = slotDurationSchedule;
    return this;
  }

  @Override
  public void validate() {
    defaultValuesIfRequired(this);
    validateConstants();
  }

  @Override
  public Map<String, Object> getValidationMap() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("slotDurationSchedule", slotDurationSchedule);
    return constants;
  }

  @Override
  public void addOverridableItemsToRawConfig(final BiConsumer<String, Object> rawConfig) {}
}
