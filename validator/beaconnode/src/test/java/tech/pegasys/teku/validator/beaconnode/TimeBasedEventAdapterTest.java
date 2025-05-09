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

package tech.pegasys.teku.validator.beaconnode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import tech.pegasys.teku.infrastructure.async.SafeFuture;
import tech.pegasys.teku.infrastructure.async.StubAsyncRunner;
import tech.pegasys.teku.infrastructure.async.timed.RepeatingTaskScheduler;
import tech.pegasys.teku.infrastructure.time.StubTimeProvider;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.spec.Spec;
import tech.pegasys.teku.spec.SpecMilestone;
import tech.pegasys.teku.spec.TestSpecFactory;
import tech.pegasys.teku.spec.config.SpecConfig;
import tech.pegasys.teku.spec.networks.Eth2Network;
import tech.pegasys.teku.validator.api.ValidatorTimingChannel;

class TimeBasedEventAdapterTest {

  private final Spec spec = TestSpecFactory.createMinimalPhase0();
  private final Spec gnosisSpec = TestSpecFactory.create(SpecMilestone.PHASE0, Eth2Network.GNOSIS);

  private final GenesisDataProvider genesisDataProvider = mock(GenesisDataProvider.class);
  private final ValidatorTimingChannel validatorTimingChannel = mock(ValidatorTimingChannel.class);

  final int secondsPerSlot = spec.getSecondsPerSlot(SpecConfig.GENESIS_SLOT);
  final int secondsPerSlotGnosis = gnosisSpec.getSecondsPerSlot(SpecConfig.GENESIS_SLOT);

  private final StubTimeProvider timeProvider = StubTimeProvider.withTimeInSeconds(100);
  private final StubAsyncRunner asyncRunner = new StubAsyncRunner(timeProvider);
  private final RepeatingTaskScheduler repeatingTaskScheduler =
      new RepeatingTaskScheduler(asyncRunner, timeProvider);

  private final TimeBasedEventAdapter eventAdapter =
      new TimeBasedEventAdapter(
          genesisDataProvider, repeatingTaskScheduler, timeProvider, validatorTimingChannel, spec);

  private final TimeBasedEventAdapter eventAdapterGnosis =
      new TimeBasedEventAdapter(
          genesisDataProvider,
          repeatingTaskScheduler,
          timeProvider,
          validatorTimingChannel,
          gnosisSpec);

  @Test
  void shouldScheduleEventsOnceGenesisIsKnown() {
    final SafeFuture<UInt64> genesisTimeFuture = new SafeFuture<>();
    when(genesisDataProvider.getGenesisTime()).thenReturn(genesisTimeFuture);

    final SafeFuture<Void> startResult = eventAdapter.start();
    // Returned future completes immediately so startup can complete pre-genesis
    assertThat(startResult).isCompleted();
    // But no slot timings have been scheduled yet
    assertThat(asyncRunner.hasDelayedActions()).isFalse();

    // Once we get the genesis time, we can schedule the slot events
    genesisTimeFuture.complete(UInt64.ONE);
    assertThat(asyncRunner.hasDelayedActions()).isTrue();
  }

  @Test
  void shouldScheduleSlotStartEventsStartingFromNextSlot() {
    final UInt64 genesisTime = timeProvider.getTimeInSeconds();
    when(genesisDataProvider.getGenesisTime()).thenReturn(SafeFuture.completedFuture(genesisTime));
    final long nextSlot = 26;
    final UInt64 firstSlotToFire = UInt64.valueOf(nextSlot);
    final int timeUntilNextSlot = secondsPerSlot / 2;
    timeProvider.advanceTimeBySeconds(secondsPerSlot * nextSlot - timeUntilNextSlot);

    assertThat(eventAdapter.start()).isCompleted();

    // Should not fire any events immediately
    asyncRunner.executeDueActionsRepeatedly();
    verifyNoMoreInteractions(validatorTimingChannel);

    // Fire slot start events when the next slot is due to start
    timeProvider.advanceTimeBySeconds(timeUntilNextSlot);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel).onSlot(firstSlotToFire);
    verify(validatorTimingChannel).onBlockProductionDue(firstSlotToFire);
    verifyNoMoreInteractions(validatorTimingChannel);
  }

  @Test
  void shouldScheduleAttestationEventsStartingFromNextSlot() {
    final UInt64 genesisTime = timeProvider.getTimeInSeconds();
    when(genesisDataProvider.getGenesisTime()).thenReturn(SafeFuture.completedFuture(genesisTime));
    final long nextSlot = 25;
    // Starting time is before the aggregation for the current slot should happen, but should still
    // wait until the next slot to start
    final int timeUntilNextSlot = secondsPerSlot - 1;
    timeProvider.advanceTimeBySeconds(secondsPerSlot * nextSlot - timeUntilNextSlot);

    assertThat(eventAdapter.start()).isCompleted();

    // Should not fire any events immediately
    asyncRunner.executeDueActionsRepeatedly();
    verifyNoMoreInteractions(validatorTimingChannel);

    // Attestation should not fire at the start of the slot
    timeProvider.advanceTimeBySeconds(timeUntilNextSlot);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel, never()).onAttestationCreationDue(UInt64.valueOf(nextSlot));

    // But does fire 1/3rds through the slot
    timeProvider.advanceTimeBySeconds(secondsPerSlot / 3);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel, times(1)).onAttestationCreationDue(UInt64.valueOf(nextSlot));
  }

  @Test
  void shouldScheduleAttestationEventsStartingFromNextSlotForGnosis() {
    // ensure seconds per slot not divisible by 3 is tested
    assertThat(secondsPerSlotGnosis % 3).isNotZero();

    final UInt64 genesisTime = timeProvider.getTimeInSeconds();
    when(genesisDataProvider.getGenesisTime()).thenReturn(SafeFuture.completedFuture(genesisTime));
    final long nextSlot = 25;
    // Starting time is before the aggregation for the current slot should happen, but should still
    // wait until the next slot to start
    final int timeUntilNextSlot = secondsPerSlotGnosis - 1;
    timeProvider.advanceTimeBySeconds(secondsPerSlotGnosis * nextSlot - timeUntilNextSlot);

    assertThat(eventAdapterGnosis.start()).isCompleted();

    // Should not fire any events immediately
    asyncRunner.executeDueActionsRepeatedly();
    verifyNoMoreInteractions(validatorTimingChannel);

    // Attestation should not fire at the start of the slot
    timeProvider.advanceTimeBySeconds(timeUntilNextSlot);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel, never()).onAttestationCreationDue(UInt64.valueOf(nextSlot));

    // It should also not fire at 1/3 seconds (whole number)
    timeProvider.advanceTimeBySeconds(secondsPerSlotGnosis / 3);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel, never()).onAttestationCreationDue(UInt64.valueOf(nextSlot));

    // But does fire 1/3rds in millis through the slot
    long alreadyPassedMillis = (secondsPerSlotGnosis / 3) * 1000L;
    long oneThird = (secondsPerSlotGnosis * 1000L) / 3;
    timeProvider.advanceTimeByMillis(oneThird - alreadyPassedMillis);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel, times(1)).onAttestationCreationDue(UInt64.valueOf(nextSlot));
  }

  @Test
  void shouldScheduleAggregateEventsStartingFromNextSlot() {
    final UInt64 genesisTime = timeProvider.getTimeInSeconds();
    when(genesisDataProvider.getGenesisTime()).thenReturn(SafeFuture.completedFuture(genesisTime));
    final long nextSlot = 25;
    // Starting time is before the aggregation for the current slot should happen, but should still
    // wait until the next slot to start
    final int timeUntilNextSlot = secondsPerSlot - 1;
    timeProvider.advanceTimeBySeconds(secondsPerSlot * nextSlot - timeUntilNextSlot);

    assertThat(eventAdapter.start()).isCompleted();

    // Should not fire any events immediately
    asyncRunner.executeDueActionsRepeatedly();
    verifyNoMoreInteractions(validatorTimingChannel);

    // Aggregation should not fire at the start of the slot
    timeProvider.advanceTimeBySeconds(timeUntilNextSlot);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel, never()).onAttestationAggregationDue(UInt64.valueOf(nextSlot));

    // But does fire 2/3rds through the slot
    timeProvider.advanceTimeBySeconds(secondsPerSlot / 3 * 2);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel, times(1)).onAttestationAggregationDue(UInt64.valueOf(nextSlot));
  }

  @Test
  void shouldScheduleAggregateEventsStartingFromNextSlotForGnosis() {
    // ensure seconds per slot not divisible by 3 is tested
    assertThat(secondsPerSlotGnosis % 3).isNotZero();

    final UInt64 genesisTime = timeProvider.getTimeInSeconds();
    when(genesisDataProvider.getGenesisTime()).thenReturn(SafeFuture.completedFuture(genesisTime));
    final long nextSlot = 25;
    // Starting time is before the aggregation for the current slot should happen, but should still
    // wait until the next slot to start
    final int timeUntilNextSlot = secondsPerSlotGnosis - 1;
    timeProvider.advanceTimeBySeconds(secondsPerSlotGnosis * nextSlot - timeUntilNextSlot);

    assertThat(eventAdapterGnosis.start()).isCompleted();

    // Should not fire any events immediately
    asyncRunner.executeDueActionsRepeatedly();
    verifyNoMoreInteractions(validatorTimingChannel);

    // Aggregation should not fire at the start of the slot
    timeProvider.advanceTimeBySeconds(timeUntilNextSlot);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel, never()).onAttestationAggregationDue(UInt64.valueOf(nextSlot));

    // It should also not fire at 2/3 seconds (whole number)
    timeProvider.advanceTimeBySeconds((secondsPerSlotGnosis / 3) * 2L);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel, never()).onAttestationAggregationDue(UInt64.valueOf(nextSlot));

    // But does fire 2/3rds in millis through the slot
    long alreadyPassedMillis = (secondsPerSlotGnosis / 3) * 2000L;
    long twoThirds = (secondsPerSlotGnosis * 2000L) / 3;
    timeProvider.advanceTimeByMillis(twoThirds - alreadyPassedMillis);
    asyncRunner.executeDueActionsRepeatedly();
    verify(validatorTimingChannel, times(1)).onAttestationAggregationDue(UInt64.valueOf(nextSlot));
  }
}
