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
package cz.seznam.euphoria.spark;

import cz.seznam.euphoria.core.client.io.Context;

abstract class FunctionContext<T> implements Context<T> {

  protected KeyedWindow window;

  @Override
  public abstract void collect(T elem);

  @Override
  public Object getWindow() {
    return this.window.window();
  }

  public void setWindow(KeyedWindow window) {
    this.window = window;
  }
}
