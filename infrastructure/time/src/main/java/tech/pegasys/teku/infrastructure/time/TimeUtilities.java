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

package tech.pegasys.teku.infrastructure.time;

import java.util.concurrent.TimeUnit;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;

public class TimeUtilities {
  public static UInt64 secondsToMillis(final UInt64 timeInSeconds) {
    return secondsToMillis(timeInSeconds.longValue());
  }

  public static UInt64 secondsToMillis(final long timeInSeconds) {
    return UInt64.valueOf(TimeUnit.SECONDS.toMillis(timeInSeconds));
  }

  public static UInt64 millisToSeconds(final UInt64 timeInMillis) {
    return millisToSeconds(timeInMillis.longValue());
  }

  public static UInt64 millisToSeconds(final long timeInMillis) {
    return UInt64.valueOf(TimeUnit.MILLISECONDS.toSeconds(timeInMillis));
  }
}
