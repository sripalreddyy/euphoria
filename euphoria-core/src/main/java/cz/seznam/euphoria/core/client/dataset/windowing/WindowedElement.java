/**
 * Copyright 2016 Seznam.cz, a.s.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.seznam.euphoria.core.client.dataset.windowing;

import cz.seznam.euphoria.core.client.functional.TernaryFunction;

/**
 * A single data element flowing in dataset. Every such element
 * is associated with a window identifier and timestamp.
 *
 * @param <W> type of the window
 * @param <T> type of the data element
 */
public interface WindowedElement<W extends Window, T> {

  W getWindow();

  void setWindow(W window);

  long getTimestamp();

  void setTimestamp(long timestamp);

  T getElement();

  /**
   * Creates a new instance of {@link WindowedElement}.
   *
   * @param <W> type of the window
   * @param <T> type of the data element
   */
  interface WindowedElementFactory<W extends Window, T>
          extends TernaryFunction<Window, Long, T, WindowedElement<W, T>> {}
}
