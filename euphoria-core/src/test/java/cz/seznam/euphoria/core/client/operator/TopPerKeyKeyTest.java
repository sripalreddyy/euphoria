package cz.seznam.euphoria.core.client.operator;

import cz.seznam.euphoria.core.client.dataset.Dataset;
import cz.seznam.euphoria.core.client.dataset.HashPartitioner;
import cz.seznam.euphoria.core.client.dataset.HashPartitioning;
import cz.seznam.euphoria.core.client.dataset.windowing.Time;
import cz.seznam.euphoria.core.client.flow.Flow;
import cz.seznam.euphoria.core.client.util.Triple;
import cz.seznam.euphoria.guava.shaded.com.google.common.collect.Iterables;
import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TopPerKeyKeyTest {
  @Test
  public void testBuild() {
    Flow flow = Flow.create("TEST");
    Dataset<String> dataset = Util.createMockDataset(flow, 2);

    Time<String> windowing = Time.of(Duration.ofHours(1));
    Dataset<Triple<String, Long, Long>> result = TopPerKey.named("TopPerKey1")
            .of(dataset)
            .keyBy(s -> s)
            .valueBy(s -> 1L)
            .scoreBy(s -> 1L)
            .windowBy(windowing)
            .output();

    assertEquals(flow, result.getFlow());
    assertEquals(1, flow.size());

    TopPerKey tpk = (TopPerKey) Iterables.getOnlyElement(flow.operators());
    assertEquals(flow, tpk.getFlow());
    assertEquals("TopPerKey1", tpk.getName());
    assertNotNull(tpk.getKeyExtractor());
    assertNotNull(tpk.getValueExtractor());
    assertNotNull(tpk.getScoreExtractor());
    assertEquals(result, tpk.output());
    assertSame(windowing, tpk.getWindowing());
    assertNull(tpk.getEventTimeAssigner());

    // default partitioning used
    assertTrue(tpk.getPartitioning().hasDefaultPartitioner());
    assertEquals(2, tpk.getPartitioning().getNumPartitions());
  }

  @Test
  public void testBuild_ImplicitName() {
    Flow flow = Flow.create("TEST");
    Dataset<String> dataset = Util.createMockDataset(flow, 2);

    Dataset<Triple<String, Long, Long>> result = TopPerKey.of(dataset)
            .keyBy(s -> s)
            .valueBy(s -> 1L)
            .scoreBy(s -> 1L)
            .output();

    TopPerKey tpk = (TopPerKey) Iterables.getOnlyElement(flow.operators());
    assertEquals("TopPerKey", tpk.getName());
  }

  @Test
  public void testBuild_Windowing() {
    Flow flow = Flow.create("TEST");
    Dataset<String> dataset = Util.createMockDataset(flow, 3);

    Dataset<Triple<String, Long, Long>> result = TopPerKey.of(dataset)
            .keyBy(s -> s)
            .valueBy(s -> 1L)
            .scoreBy(s -> 1L)
            .windowBy(Time.of(Duration.ofHours(1)), (s -> 0L))
            .output();

    TopPerKey tpk = (TopPerKey) Iterables.getOnlyElement(flow.operators());
    assertNotNull(tpk.getEventTimeAssigner());
  }

  @Test
  public void testBuild_Partitioning() {
    Flow flow = Flow.create("TEST");
    Dataset<String> dataset = Util.createMockDataset(flow, 2);

    Time<String> windowing = Time.of(Duration.ofHours(1));
    Dataset<Triple<String, Long, Long>> result = TopPerKey.named("TopPerKey1")
            .of(dataset)
            .keyBy(s -> s)
            .valueBy(s -> 1L)
            .scoreBy(s -> 1L)
            .setPartitioning(new HashPartitioning<>(1))
            .windowBy(windowing)
            .output();

    TopPerKey tpk = (TopPerKey) Iterables.getOnlyElement(flow.operators());
    assertTrue(!tpk.getPartitioning().hasDefaultPartitioner());
    assertTrue(tpk.getPartitioning().getPartitioner() instanceof HashPartitioner);
    assertEquals(1, tpk.getPartitioning().getNumPartitions());
  }

  @Test
  public void testBuild_Partitioner() {
    Flow flow = Flow.create("TEST");
    Dataset<String> dataset = Util.createMockDataset(flow, 2);

    Time<String> windowing = Time.of(Duration.ofHours(1));
    Dataset<Triple<String, Long, Long>> result = TopPerKey.named("TopPerKey1")
            .of(dataset)
            .keyBy(s -> s)
            .valueBy(s -> 1L)
            .scoreBy(s -> 1L)
            .windowBy(windowing)
            .setPartitioner(new HashPartitioner<>())
            .setNumPartitions(5)
            .output();

    TopPerKey tpk = (TopPerKey) Iterables.getOnlyElement(flow.operators());
    assertTrue(!tpk.getPartitioning().hasDefaultPartitioner());
    assertTrue(tpk.getPartitioning().getPartitioner() instanceof HashPartitioner);
    assertEquals(5, tpk.getPartitioning().getNumPartitions());
  }
}