/*
 * Copyright (c) 2016, Salesforce.com, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Salesforce.com nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
     
package com.salesforce.dva.argus.service.metric.transform;

import com.salesforce.dva.argus.entity.Metric;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Scale_VTransformTest {

    private static final String TEST_SCOPE = "test-scope";
    private static final String TEST_METRIC = "test-metric";

    @Test(expected = IllegalArgumentException.class)
    public void testScale_VTransformWithoutMetrics() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        List<Metric> metrics = null;

        scale_vTransform.transform(metrics);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScale_VTransformWithOnlyOneMetric() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        List<Metric> metrics = new ArrayList<Metric>();
        Metric metric = new Metric(TEST_SCOPE, TEST_METRIC);

        metrics.add(metric);
        scale_vTransform.transform(metrics);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScale_VTransformWithConstants() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        List<Metric> metrics = new ArrayList<Metric>();
        Metric metric = new Metric(TEST_SCOPE, TEST_METRIC);

        metrics.add(metric);

        List<String> constants = new ArrayList<String>();

        scale_vTransform.transform(metrics, constants);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScale_VTransformVectorWithoutPoints() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        Map<Long, Number> datapoints = new HashMap<Long, Number>();

        datapoints.put(1000L, 1.0);

        Metric metric = new Metric(TEST_SCOPE, TEST_METRIC);

        metric.setDatapoints(datapoints);

        Metric vector = new Metric(TEST_SCOPE, TEST_METRIC);
        List<Metric> metrics = new ArrayList<Metric>();

        metrics.add(metric);
        metrics.add(vector);
        scale_vTransform.transform(metrics);
    }

    @Test
    public void testScale_VTransformWithSameLenVectorAgainstOneMetric() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        Map<Long, Number> datapoints = new HashMap<Long, Number>();

        datapoints.put(1000L, 1.0);
        datapoints.put(2000L, 2.0);
        datapoints.put(3000L, 3.0);

        Metric metric = new Metric(TEST_SCOPE, TEST_METRIC);

        metric.setDatapoints(datapoints);

        Map<Long, Number> vector_datapoints = new HashMap<Long, Number>();

        vector_datapoints.put(1000L, 1L);
        vector_datapoints.put(2000L, 1L);
        vector_datapoints.put(3000L, 1L);

        Metric vector = new Metric(TEST_SCOPE, TEST_METRIC);

        vector.setDatapoints(vector_datapoints);

        List<Metric> metrics = new ArrayList<Metric>();

        metrics.add(metric);
        metrics.add(vector);

        Map<Long, Number> expected = new HashMap<Long, Number>();

        expected.put(1000L, 1.0);
        expected.put(2000L, 2.0);
        expected.put(3000L, 3.0);

        List<Metric> result = scale_vTransform.transform(metrics);

        assertEquals(result.get(0).getDatapoints().size(), 3);
        assertEquals(expected, result.get(0).getDatapoints());
    }
    
    @Test
    public void testScale_VTransformWithLongerLenVectorAgainstOneMetric() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        Map<Long, Number> datapoints = new HashMap<Long, Number>();

        datapoints.put(1000L, 1.0);
        datapoints.put(2000L, 2.0);
        datapoints.put(3000L, 3L);

        Metric metric = new Metric(TEST_SCOPE, TEST_METRIC);

        metric.setDatapoints(datapoints);

        Map<Long, Number> vector_datapoints = new HashMap<Long, Number>();

        vector_datapoints.put(1000L, 1.0);
        vector_datapoints.put(2000L, 1.0);
        vector_datapoints.put(3000L, 1L);
        vector_datapoints.put(4000L, 1L);

        Metric vector = new Metric(TEST_SCOPE, TEST_METRIC);

        vector.setDatapoints(vector_datapoints);

        List<Metric> metrics = new ArrayList<Metric>();

        metrics.add(metric);
        metrics.add(vector);

        Map<Long, Number> expected = new HashMap<Long, Number>();

        expected.put(1000L, 1.0);
        expected.put(2000L, 2.0);
        expected.put(3000L, 3L);

        List<Metric> result = scale_vTransform.transform(metrics);

        assertEquals(result.get(0).getDatapoints().size(), 3);
        assertEquals(expected, result.get(0).getDatapoints());
    }

    @Test
    public void testScale_VTransformWithShorterLenVectorAgainstOneMetric() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        Map<Long, Number> datapoints = new HashMap<Long, Number>();

        datapoints.put(1000L, 1L);
        datapoints.put(2000L, 2L);
        datapoints.put(3000L, 3L);

        Metric metric = new Metric(TEST_SCOPE, TEST_METRIC);

        metric.setDatapoints(datapoints);

        Map<Long, Number> vector_datapoints = new HashMap<Long, Number>();

        vector_datapoints.put(1000L, 1L);
        vector_datapoints.put(2000L, 1L);

        Metric vector = new Metric(TEST_SCOPE, TEST_METRIC);

        vector.setDatapoints(vector_datapoints);

        List<Metric> metrics = new ArrayList<Metric>();

        metrics.add(metric);
        metrics.add(vector);

        Map<Long, Number> expected = new HashMap<Long, Number>();

        expected.put(1000L, 1L);
        expected.put(2000L, 2L);
        expected.put(3000L, 3L);

        List<Metric> result = scale_vTransform.transform(metrics);

        assertEquals(result.get(0).getDatapoints().size(), 3);
        assertEquals(expected, result.get(0).getDatapoints());
    }

    @Test
    public void testScale_VTransformWithMidMissingPointVectorAgainstOneMetric() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        Map<Long, Number> datapoints = new HashMap<Long, Number>();

        datapoints.put(1000L, 1.0);
        datapoints.put(2000L, 2.0);
        datapoints.put(3000L, 3.0);

        Metric metric = new Metric(TEST_SCOPE, TEST_METRIC);

        metric.setDatapoints(datapoints);

        Map<Long, Number> vector_datapoints = new HashMap<Long, Number>();

        vector_datapoints.put(1000L, 1.0);
        vector_datapoints.put(3000L, 1.0);

        Metric vector = new Metric(TEST_SCOPE, TEST_METRIC);

        vector.setDatapoints(vector_datapoints);

        List<Metric> metrics = new ArrayList<Metric>();

        metrics.add(metric);
        metrics.add(vector);

        Map<Long, Number> expected = new HashMap<Long, Number>();

        expected.put(1000L, 1.0);
        expected.put(2000L, 2.0);
        expected.put(3000L, 3.0);

        List<Metric> result = scale_vTransform.transform(metrics);

        assertEquals(result.get(0).getDatapoints().size(), 3);
        assertEquals(expected, result.get(0).getDatapoints());
    }
    
    @Test
    public void testScale_VTransformWithNullPointVectorAgainstOneMetric() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        Map<Long, Number> datapoints = new HashMap<Long, Number>();

        datapoints.put(1000L, 1L);
        datapoints.put(2000L, 2L);
        datapoints.put(3000L, 3L);

        Metric metric = new Metric(TEST_SCOPE, TEST_METRIC);

        metric.setDatapoints(datapoints);

        Map<Long, Number> vector_datapoints = new HashMap<Long, Number>();

        vector_datapoints.put(1000L, 1.0);
        vector_datapoints.put(2000L, null);
        vector_datapoints.put(3000L, 1.0);

        Metric vector = new Metric(TEST_SCOPE, TEST_METRIC);

        vector.setDatapoints(vector_datapoints);

        List<Metric> metrics = new ArrayList<Metric>();

        metrics.add(metric);
        metrics.add(vector);

        Map<Long, Number> expected = new HashMap<Long, Number>();

        expected.put(1000L, 1.0);
        expected.put(2000L, 2L);
        expected.put(3000L, 3.0);

        List<Metric> result = scale_vTransform.transform(metrics);

        assertEquals(result.get(0).getDatapoints().size(), 3);
        assertEquals(expected, result.get(0).getDatapoints());
    }

    @Test
    public void testScale_VTransformWithVectorAgainstOneNullPointMetric() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        Map<Long, Number> datapoints = new HashMap<Long, Number>();

        datapoints.put(1000L, 1.0);
        datapoints.put(2000L, null);
        datapoints.put(3000L, 3.0);

        Metric metric = new Metric(TEST_SCOPE, TEST_METRIC);

        metric.setDatapoints(datapoints);

        Map<Long, Number> vector_datapoints = new HashMap<Long, Number>();

        vector_datapoints.put(1000L, 1.0);
        vector_datapoints.put(2000L, 1.0);
        vector_datapoints.put(3000L, 1.0);

        Metric vector = new Metric(TEST_SCOPE, TEST_METRIC);

        vector.setDatapoints(vector_datapoints);

        List<Metric> metrics = new ArrayList<Metric>();

        metrics.add(metric);
        metrics.add(vector);

        Map<Long, Number> expected = new HashMap<Long, Number>();

        expected.put(1000L, 1.0);
        expected.put(2000L, 1.0);
        expected.put(3000L, 3.0);

        List<Metric> result = scale_vTransform.transform(metrics);

        assertEquals(result.get(0).getDatapoints().size(), 3);
        assertEquals(expected, result.get(0).getDatapoints());
    }

    @Test
    public void testScale_VTransformWithSameShorterLongerVectorAgainstMetricList() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        Map<Long, Number> datapoints_1 = new HashMap<Long, Number>();

        datapoints_1.put(1000L, 1.0);
        datapoints_1.put(2000L, 2.0);
        datapoints_1.put(3000L, 3L);

        Metric metric_1 = new Metric(TEST_SCOPE, TEST_METRIC);

        metric_1.setDatapoints(datapoints_1);

        Map<Long, Number> datapoints_2 = new HashMap<Long, Number>();

        datapoints_2.put(1000L, 10L);
        datapoints_2.put(2000L, 100L);
        datapoints_2.put(3000L, 1000L);
        datapoints_2.put(4000L, 10000L);

        Metric metric_2 = new Metric(TEST_SCOPE, TEST_METRIC);

        metric_2.setDatapoints(datapoints_2);

        Map<Long, Number> datapoints_3 = new HashMap<Long, Number>();

        datapoints_3.put(1000L, 0.1);
        datapoints_3.put(2000L, 0.01);

        Metric metric_3 = new Metric(TEST_SCOPE, TEST_METRIC);

        metric_3.setDatapoints(datapoints_3);

        Map<Long, Number> vector_datapoints = new HashMap<Long, Number>();

        vector_datapoints.put(1000L, 1L);
        vector_datapoints.put(2000L, 1L);
        vector_datapoints.put(3000L, 1L);

        Metric vector = new Metric(TEST_SCOPE, TEST_METRIC);

        vector.setDatapoints(vector_datapoints);

        List<Metric> metrics = new ArrayList<Metric>();

        metrics.add(metric_1);
        metrics.add(metric_2);
        metrics.add(metric_3);
        metrics.add(vector);

        Map<Long, Number> expected_1 = new HashMap<Long, Number>();

        expected_1.put(1000L, 1.0);
        expected_1.put(2000L, 2.0);
        expected_1.put(3000L, 3L);

        Map<Long, Number> expected_2 = new HashMap<Long, Number>();

        expected_2.put(1000L, 10L);
        expected_2.put(2000L, 100L);
        expected_2.put(3000L, 1000L);
        expected_2.put(4000L, 10000L);

        Map<Long, Number> expected_3 = new HashMap<Long, Number>();

        expected_3.put(1000L, 0.1);
        expected_3.put(2000L, 0.01);

        List<Metric> result = scale_vTransform.transform(metrics);

        assertEquals(result.get(0).getDatapoints().size(), 3);
        assertEquals(expected_1, result.get(0).getDatapoints());
        assertEquals(result.get(1).getDatapoints().size(), 4);
        assertEquals(expected_2, result.get(1).getDatapoints());
        assertEquals(result.get(2).getDatapoints().size(), 2);
        assertEquals(expected_3, result.get(2).getDatapoints());
    }

    @Test
    public void testScale_VTransformWithMissingPointNullPointVectorAgainstNullPointMetricList() {
        Transform scale_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        Map<Long, Number> datapoints_1 = new HashMap<Long, Number>();

        datapoints_1.put(1000L, 1.0);
        datapoints_1.put(2000L, 2.0);
        datapoints_1.put(3000L, 3.0);

        Metric metric_1 = new Metric(TEST_SCOPE, TEST_METRIC);

        metric_1.setDatapoints(datapoints_1);

        Map<Long, Number> datapoints_2 = new HashMap<Long, Number>();

        datapoints_2.put(1000L, 10.0);
        datapoints_2.put(2000L, 100.0);
        datapoints_2.put(4000L, 1000.0);
        datapoints_2.put(5000L, 10000.0);

        Metric metric_2 = new Metric(TEST_SCOPE, TEST_METRIC);

        metric_2.setDatapoints(datapoints_2);

        Map<Long, Number> datapoints_3 = new HashMap<Long, Number>();

        datapoints_3.put(1000L, 0.1);
        datapoints_3.put(2000L, 0.01);
        datapoints_3.put(4000L, 0.001);
        datapoints_3.put(5000L, null);

        Metric metric_3 = new Metric(TEST_SCOPE, TEST_METRIC);

        metric_3.setDatapoints(datapoints_3);

        Map<Long, Number> vector_datapoints = new HashMap<Long, Number>();

        vector_datapoints.put(1000L, 1.0);
        vector_datapoints.put(2000L, 1.0);
        vector_datapoints.put(4000L, 1.0);
        vector_datapoints.put(5000L, null);

        Metric vector = new Metric(TEST_SCOPE, TEST_METRIC);

        vector.setDatapoints(vector_datapoints);

        List<Metric> metrics = new ArrayList<Metric>();

        metrics.add(metric_1);
        metrics.add(metric_2);
        metrics.add(metric_3);
        metrics.add(vector);

        Map<Long, Number> expected_1 = new HashMap<Long, Number>();

        expected_1.put(1000L, 1.0);
        expected_1.put(2000L, 2.0);
        expected_1.put(3000L, 3.0);

        Map<Long, Number> expected_2 = new HashMap<Long, Number>();

        expected_2.put(1000L, 10.0);
        expected_2.put(2000L, 100.0);
        expected_2.put(4000L, 1000.0);
        expected_2.put(5000L, 10000.0);

        Map<Long, Number> expected_3 = new HashMap<Long, Number>();

        expected_3.put(1000L, 0.1);
        expected_3.put(2000L, 0.01);
        expected_3.put(4000L, 0.001);
        expected_3.put(5000L, 1L);

        List<Metric> result = scale_vTransform.transform(metrics);

        assertEquals(result.get(0).getDatapoints().size(), 3);
        assertEquals(expected_1, result.get(0).getDatapoints());
        assertEquals(result.get(1).getDatapoints().size(), 4);
        assertEquals(expected_2, result.get(1).getDatapoints());
        assertEquals(result.get(2).getDatapoints().size(), 4);
        assertEquals(expected_3, result.get(2).getDatapoints());
    }
    
    @Test
    public void testScale_VTransformWithSameShorterLongerVectorAgainstMetricList_fullJoinIndicator() {
        Transform sum_vTransform = new MetricZipperTransform(new ScaleValueZipper());
        Map<Long, Number> datapoints_1 = new HashMap<Long, Number>();

        datapoints_1.put(1000L, 1L);
        datapoints_1.put(2000L, 2L);
        datapoints_1.put(3000L, 3L);

        Metric metric_1 = new Metric(TEST_SCOPE, TEST_METRIC);

        metric_1.setDatapoints(datapoints_1);

        Map<Long, Number> datapoints_2 = new HashMap<Long, Number>();

        datapoints_2.put(1000L, 10L);
        datapoints_2.put(2000L, 100L);
        datapoints_2.put(3000L, 1000L);
        datapoints_2.put(4000L, 10000L);

        Metric metric_2 = new Metric(TEST_SCOPE, TEST_METRIC);

        metric_2.setDatapoints(datapoints_2);

        Map<Long, Number> datapoints_3 = new HashMap<Long, Number>();

        datapoints_3.put(1000L, 1.0);
        datapoints_3.put(2000L, 10.0);

        Metric metric_3 = new Metric(TEST_SCOPE, TEST_METRIC);

        metric_3.setDatapoints(datapoints_3);

        Map<Long, Number> vector_datapoints = new HashMap<Long, Number>();

        vector_datapoints.put(1000L, 1.0);
        vector_datapoints.put(2000L, 1.0);
        vector_datapoints.put(3000L, 1.0);

        Metric vector = new Metric(TEST_SCOPE, TEST_METRIC);

        vector.setDatapoints(vector_datapoints);

        List<Metric> metrics = new ArrayList<Metric>();

        metrics.add(metric_1);
        metrics.add(metric_2);
        metrics.add(metric_3);
        metrics.add(vector);

        Map<Long, Number> expected_1 = new HashMap<Long, Number>();

        expected_1.put(1000L, 1.0);
        expected_1.put(2000L, 2.0);
        expected_1.put(3000L, 3.0);

        Map<Long, Number> expected_2 = new HashMap<Long, Number>();

        expected_2.put(1000L, 10.0);
        expected_2.put(2000L, 100.0);
        expected_2.put(3000L, 1000.0);
        expected_2.put(4000L, 10000L);

        Map<Long, Number> expected_3 = new HashMap<Long, Number>();

        expected_3.put(1000L, 1.0);
        expected_3.put(2000L, 10.0);
        expected_3.put(3000L, 1.0);
        
        List<Metric> result = sum_vTransform.transform(metrics, Arrays.asList("UNION"));

        assertEquals(3, result.get(0).getDatapoints().size());
        assertEquals(expected_1, result.get(0).getDatapoints());
        assertEquals(4, result.get(1).getDatapoints().size());
        assertEquals(expected_2, result.get(1).getDatapoints());
        assertEquals(3, result.get(2).getDatapoints().size());
        assertEquals(expected_3, result.get(2).getDatapoints());
    }
}
/* Copyright (c) 2016, Salesforce.com, Inc.  All rights reserved. */
