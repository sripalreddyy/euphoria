/**
 * Copyright 2016-2017 Seznam.cz, a.s.
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
package cz.seznam.euphoria.flink.streaming;

import cz.seznam.euphoria.core.client.functional.UnaryFunctor;
import cz.seznam.euphoria.core.client.operator.ExtractEventTime;
import cz.seznam.euphoria.core.client.operator.FlatMap;
import cz.seznam.euphoria.core.util.Settings;
import cz.seznam.euphoria.flink.FlinkOperator;
import cz.seznam.euphoria.flink.accumulators.FlinkAccumulatorFactory;
import org.apache.flink.streaming.api.datastream.DataStream;

class FlatMapTranslator implements StreamingOperatorTranslator<FlatMap> {

  @Override
  @SuppressWarnings("unchecked")
  public DataStream<?> translate(FlinkOperator<FlatMap> operator,
                                 StreamingExecutorContext context) {
    Settings settings = context.getSettings();
    FlinkAccumulatorFactory accumulatorFactory = context.getAccumulatorFactory();

    DataStream input = context.getSingleInputStream(operator);
    UnaryFunctor mapper = operator.getOriginalOperator().getFunctor();
    ExtractEventTime evtTimeFn = operator.getOriginalOperator().getEventTimeExtractor();
    if (evtTimeFn != null) {
      input = input.assignTimestampsAndWatermarks(
          new EventTimeAssigner(context.getAllowedLateness(), evtTimeFn))
          .returns((Class) StreamingElement.class);
    }
    return input
        .flatMap(new StreamingUnaryFunctorWrapper(mapper, accumulatorFactory, settings))
        .returns((Class) StreamingElement.class)
        .name(operator.getName())
        .setParallelism(operator.getParallelism());
  }
}
