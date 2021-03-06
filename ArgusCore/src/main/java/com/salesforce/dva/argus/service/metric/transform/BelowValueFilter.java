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
import com.salesforce.dva.argus.entity.NumberOperations;
import com.salesforce.dva.argus.system.SystemAssert;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to filter metrics whose evaluation result is below the
 * limit.
 *
 * @author Ruofan Zhang (rzhang@salesforce.com)
 */
public class BelowValueFilter implements ValueFilter {

    // ~ Methods
    // **************************************************************************************************************************************

    @Override
    public List<Metric> filter(Map<Metric, String> extendedSortedMap, String limit) {
        SystemAssert.requireArgument(extendedSortedMap != null && !extendedSortedMap.isEmpty(), "New map is not constructed successfully!");
        SystemAssert.requireArgument(limit != null && !limit.equals(""), "Limit must be provided!");

        List<Metric> result = new ArrayList<Metric>();

        Number lim = NumberOperations.parseConstant(limit);
        for (Map.Entry<Metric, String> entry : extendedSortedMap.entrySet()) {
            Number val = NumberOperations.parseConstant(entry.getValue());
            
            if (NumberOperations.isGreaterThan(lim, val)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    @Override
    public String name() {
        return TransformFactory.Function.BELOW.name();
    }
}
/* Copyright (c) 2016, Salesforce.com, Inc.  All rights reserved. */
