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

import com.salesforce.dva.argus.entity.NumberOperations;
import com.salesforce.dva.argus.system.SystemAssert;
import com.salesforce.dva.argus.system.SystemException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates an arithmetic product. If a constant is provided, it is multiplied with each data point in the set of input metrics, otherwise the data
 * point values of each time stamp for are multiplied together.
 *
 * @author  Ruofan Zhang (rzhang@salesforce.com)
 */
public class ScaleValueReducerOrMapping implements ValueReducerOrMapping {

    //~ Methods **************************************************************************************************************************************

    @Override
    public Number reduce(List<Number> values) {
        Number product = 1;

        for (Number value : values) {
            if (value == null) {
                continue;
            }
            product = NumberOperations.multiply(product, value);
        }
        return product;
    }
    
    @Override
    public Map<Long, Number> mapping(Map<Long, Number> originalDatapoints) {
        throw new UnsupportedOperationException("Scale Transform with mapping is not supposed to be used without a constant");
    }

    @Override
    public Map<Long, Number> mapping(Map<Long, Number> originalDatapoints, List<String> constants) {
        SystemAssert.requireArgument(constants != null && constants.size() == 1,
            "If constants provided for scale transform, only exactly one constant allowed.");

        Map<Long, Number> scaleDatapoints = new HashMap<>();
        Number multiplicand;
        try {
            multiplicand = NumberOperations.parseConstant(constants.get(0));
        } catch (IllegalArgumentException iae) {
            throw new SystemException("Illegal constant supplied to Scale Value Reducer or Mapping: " + constants.get(0));
        }
        
        for (Map.Entry<Long, Number> entry : originalDatapoints.entrySet()) {
        	scaleDatapoints.put(entry.getKey(), NumberOperations.multiply(entry.getValue(), multiplicand));
        }
        return scaleDatapoints;
    }

    @Override
    public Number reduce(List<Number> values, List<String> constants) {
        throw new UnsupportedOperationException("Scale Transform with reducer is not supposed to be used without a constant");
    }

    @Override
    public String name() {
        return TransformFactory.Function.SCALE.name();
    }
}
/* Copyright (c) 2016, Salesforce.com, Inc.  All rights reserved. */
