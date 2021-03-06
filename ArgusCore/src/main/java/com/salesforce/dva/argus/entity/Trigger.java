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
	 
package com.salesforce.dva.argus.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.salesforce.dva.argus.entity.NumberOperations.ValueType;
import com.salesforce.dva.argus.system.SystemException;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import static com.salesforce.dva.argus.system.SystemAssert.requireArgument;

/**
 * Encapsulates information about an alert trigger. When a condition is triggered, it sends one or more notifications. The interval over which the
 * trigger conditions are evaluated is the entire interval specified by the alert expression.
 *
 * <p>Fields that determine uniqueness are:</p>
 *
 * <ul>
 *   <li>ALERT</li>
 *   <li>NAME</li>
 * </ul>
 *
 * <p>Fields that cannot be null are:</p>
 *
 * <ul>
 *   <li>ALERT</li>
 *   <li>NAME</li>
 * </ul>
 *
 * @author  Tom Valine (tvaline@salesforce.com)
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "TRIGGER", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "alert_id" }))
public class Trigger extends JPAEntity implements Serializable {
		
	public static class Serializer extends JsonSerializer<Trigger> {

		@Override
		public void serialize(Trigger trigger, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
			jgen.writeStartObject();
			
			jgen.writeStringField("id", trigger.getId().toString());
			jgen.writeStringField("name", trigger.getName());
			jgen.writeStringField("type", trigger.getType().name());
			
			ValueType type = ValueType.value(trigger.getThreshold());
			switch (type) {
				case LONG:
					jgen.writeNumberField("thresholdLong", trigger.getThreshold().longValue());
					jgen.writeNumberField("threshold", trigger.getThreshold().doubleValue());
					break;
				case DOUBLE:
					jgen.writeNumberField("threshold", trigger.getThreshold().doubleValue());
					break;
				default:
					throw new IllegalStateException();
			}
			
			if(trigger.getSecondaryThreshold() != null) {
				ValueType type2 = ValueType.value(trigger.getSecondaryThreshold());
				switch (type2) {
					case LONG:
						jgen.writeNumberField("secondaryThresholdLong", trigger.getSecondaryThreshold().longValue());
						jgen.writeNumberField("secondaryThreshold", trigger.getSecondaryThreshold().doubleValue());
						break;
					case DOUBLE:
						jgen.writeNumberField("secondaryThreshold", trigger.getSecondaryThreshold().doubleValue());
						break;
					default:
						throw new IllegalStateException();
				}
			}
			
			if(trigger.getInertia() != null) {
				jgen.writeNumberField("inertia", trigger.getInertia());
			}
			
			jgen.writeEndObject();
		}
		
	}

	
	public static class Deserializer extends JsonDeserializer<Trigger> {

		@Override
		public Trigger deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {	
			Trigger trigger = new Trigger();
			JsonNode rootNode = jp.getCodec().readTree(jp);
			
			BigInteger id = new BigInteger(rootNode.get("id").asText());
			trigger.id = id;
			
			String name = rootNode.get("name").asText();
			trigger.setName(name);
			
			TriggerType type = TriggerType.fromString(rootNode.get("type").asText());
			trigger.setType(type);
			
			// BigDecimal used here since JPA rounds Number like Double, but maintains precision for BigDecimal
			BigDecimal threshold;
			if (rootNode.get("thresholdLong") != null) {
				threshold = new BigDecimal(rootNode.get("thresholdLong").asLong());
			} else {
				threshold = new BigDecimal(rootNode.get("threshold").asDouble());
			}
			trigger.setThreshold(threshold);
			
			if(rootNode.get("secondaryThreshold") != null) {
				BigDecimal secondaryThreshold;
				if (rootNode.get("secondaryThresholdLong") != null) {
					secondaryThreshold = new BigDecimal(rootNode.get("secondaryThresholdLong").asLong());
				} else {
					secondaryThreshold = new BigDecimal(rootNode.get("secondaryThreshold").asDouble());
				}
				trigger.setSecondaryThreshold(secondaryThreshold);
			}
			
			if(rootNode.get("inertia") != null) {
				trigger.setInertia(rootNode.get("inertia").asLong());
			}
			
			return trigger;
		}
		
	}
	

    //~ Instance fields ******************************************************************************************************************************

	@Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TriggerType type;
    
    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    private String name;
    
    @Basic(optional = false)
    private BigDecimal threshold;
            
    private BigDecimal secondaryThreshold;
    
    private Long inertia;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, name = "alert_id")
    private Alert alert;
    
    @ManyToMany(mappedBy = "triggers", cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private List<Notification> notifications = new ArrayList<>(0);

    //~ Constructors *********************************************************************************************************************************

    /**
     * Creates a new Trigger object.
     *
     * @param  alert          The alert associated with the trigger. Cannot be null.
     * @param  type           The type of the alert. Cannot be null.
     * @param  name           The name of the alert. Cannot be null or empty.
     * @param  threshold      The threshold value for the alert.
     * @param  inertiaMillis  The amount of time in milliseconds a condition must exist for the trigger to fire. Cannot be negative.
     */
    public Trigger(Alert alert, TriggerType type, String name, Number threshold, long inertiaMillis) {
        this(alert, type, name, threshold, null, inertiaMillis);
    }

    /**
     * Creates a new Trigger object.
     *
     * @param  alert               The alert associated with the trigger. Cannot be null.
     * @param  type                The type of the alert. Cannot be null.
     * @param  name                The name of the alert. Cannot be null or empty.
     * @param  threshold           The threshold value for the alert.
     * @param  secondaryThreshold  The secondary threshold value for the alert. May be null for types that only require one threshold.
     * @param  inertiaMillis       The amount of time in milliseconds a condition must exist for the trigger to fire. Cannot be negative.
     */
    public Trigger(Alert alert, TriggerType type, String name, Number threshold, Number secondaryThreshold, long inertiaMillis) {
        super(alert.getOwner());
        setAlert(alert);
        setType(type);
        setName(name);
        // BigDecimal used here for thresholds since JPA rounds Number like double, but maintains precision for BigDecimal
        setThreshold(NumberOperations.bd(threshold));
        setSecondaryThreshold(NumberOperations.bd(secondaryThreshold));
        setInertia(inertiaMillis);
        preUpdate();
    }

    /** Creates a new Trigger object. */
    protected Trigger() {
        super(null);
    }

    //~ Methods **************************************************************************************************************************************

    /**
     * Evaluates the trigger against actualValue (passed as parameter).
     *
     * @param   trigger      trigger to be evaluated.
     * @param   actualValue  value against the trigger to be evaluated.
     *
     * @return  true if the trigger should be fired so that notification will be sent otherwise false.
     *
     * @throws  SystemException  If an error in evaluation occurs.
     */
    public static boolean evaluateTrigger(Trigger trigger, Number actualValue) {
        requireArgument(trigger != null, "Trigger cannot be null.");
        requireArgument(actualValue != null, "Trigger cannot be evaulated against null.");

        Number lowThreshold, highThreshold;

        switch (trigger.type) {
            case GREATER_THAN:
            	return NumberOperations.isGreaterThan(actualValue, trigger.getThreshold());
            case GREATER_THAN_OR_EQ:
            	return NumberOperations.isGreaterThanOrEqualTo(actualValue, trigger.getThreshold());
            case LESS_THAN:
            	return NumberOperations.isLessThan(actualValue, trigger.getThreshold());
            case LESS_THAN_OR_EQ:
            	return NumberOperations.isLessThanOrEqualTo(actualValue, trigger.getThreshold());
            case EQUAL:
            	return NumberOperations.isEqualTo(actualValue, trigger.getThreshold());
            case NOT_EQUAL:
            	return !NumberOperations.isEqualTo(actualValue, trigger.getThreshold());
            case BETWEEN:
            	lowThreshold = NumberOperations.getMin(trigger.getThreshold(), trigger.getSecondaryThreshold());
            	highThreshold = NumberOperations.getMax(trigger.getThreshold(), trigger.getSecondaryThreshold());
            	return (NumberOperations.isGreaterThanOrEqualTo(actualValue, lowThreshold) && NumberOperations.isLessThanOrEqualTo(actualValue, highThreshold));
            case NOT_BETWEEN:
            	lowThreshold = NumberOperations.getMin(trigger.getThreshold(), trigger.getSecondaryThreshold());
            	highThreshold = NumberOperations.getMax(trigger.getThreshold(), trigger.getSecondaryThreshold());
            	return (NumberOperations.isLessThan(actualValue, lowThreshold) || NumberOperations.isGreaterThan(actualValue, highThreshold));
            case NO_DATA:
            	return actualValue == null;
            default:
                throw new SystemException("Unsupported trigger type " + trigger.type);
        }
    }

    //~ Methods **************************************************************************************************************************************

    /**
     * Returns the notifications associated with the trigger.
     *
     * @return  The associated notifications.
     */
    public List<Notification> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    /**
     * Returns the trigger type.
     *
     * @return  The trigger type. Will never be null.
     */
    public TriggerType getType() {
        return type;
    }

    /**
     * Sets the trigger type.
     *
     * @param  type  The trigger type. Cannot be null.
     */
    public void setType(TriggerType type) {
        requireArgument(type != null, "The trigger type cannot be null.");
        this.type = type;
    }

    /**
     * Returns the trigger name.
     *
     * @return  The trigger name. Will never be null.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the trigger.
     *
     * @param  name  The trigger name. Cannot be null or empty.
     */
    public void setName(String name) {
        requireArgument(name != null && !name.isEmpty(), "Trigger name cannot be null or empty.");
        this.name = name;
    }

    /**
     * Returns the trigger threshold.
     *
     * @return  The trigger threshold.
     */
    public BigDecimal getThreshold() {
        return threshold;
    }

    /**
     * Sets the trigger threshold.
     *
     * @param  threshold  The trigger threshold. Cannot be null.
     */
    public void setThreshold(BigDecimal threshold) {
        requireArgument(threshold != null, "Trigger threshold cannot be null.");
        this.threshold = threshold;
    }

    /**
     * Returns the secondary threshold for the trigger.
     *
     * @return  The secondary threshold. Can return null for trigger types that only require a single threshold.
     */
    public BigDecimal getSecondaryThreshold() {
        return secondaryThreshold;
    }

    /**
     * Sets the secondary threshold for the trigger.
     *
     * @param  secondaryThreshold  The secondary threshold. Can be null for trigger types that only require a single threshold.
     */
    public void setSecondaryThreshold(BigDecimal secondaryThreshold) {
        this.secondaryThreshold = secondaryThreshold;
    }
    
    /**
     * Returns the inertia associated with the trigger in milliseconds.
     *
     * @return  The inertia in milliseconds.
     */
    public Long getInertia() {
        return inertia;
    }

    /**
     * Sets the inertia associated with the trigger in milliseconds.
     *
     * @param  inertiaMillis  The inertia associated with the trigger in milliseconds. Cannot be null or negative.
     */
    public void setInertia(Long inertiaMillis) {
        requireArgument(inertiaMillis != null && inertiaMillis >= 0, "Inertia cannot be negative.");
        this.inertia = inertiaMillis;
    }

    /**
     * Returns the alert with which the trigger is associated.
     *
     * @return  The alert with which the trigger is associated.
     */
    public Alert getAlert() {
        return alert;
    }

    /**
     * Sets the alert with which the trigger is associated.
     *
     * @param  alert  The alert with which the trigger is associated. Cannot be null.
     */
    public void setAlert(Alert alert) {
        requireArgument(alert != null, "The alert with which a trigger is associated cannot be null.");
        this.alert = alert;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.alert);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final Trigger other = (Trigger) obj;

        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.alert, other.alert)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (type == TriggerType.BETWEEN || type == TriggerType.NOT_BETWEEN)
            return "Trigger{" + "type=" + type + ", name=" + name + ", primaryThreshold=" + threshold + ", secondaryThreshold=" + secondaryThreshold +
            ", inertia=" + inertia + '}';
        else
            return "Trigger{" + "type=" + type + ", name=" + name + ", threshold=" + threshold + ", inertia=" + inertia + '}';
    }

    //~ Enums ****************************************************************************************************************************************

    /**
     * The type of trigger.
     *
     * @author  Tom Valine (tvaline@salesforce.com)
     */
    public enum TriggerType {

        /** Greater than. */
        GREATER_THAN,
        /** Greater than or equal to. */
        GREATER_THAN_OR_EQ,
        /** Less than. */
        LESS_THAN,
        /** Less than or equal to. */
        LESS_THAN_OR_EQ,
        /** Equal to. */
        EQUAL,
        /** Not equal to. */
        NOT_EQUAL,
        /** Between. */
        BETWEEN,
        /** Not between. */
        NOT_BETWEEN,
    	    /** Is NULL */
    	    NO_DATA;

        /**
         * Converts a string to a trigger type.
         *
         * @param   name  The trigger type name.
         *
         * @return  The corresponding trigger type.
         *
         * @throws  IllegalArgumentException  If no corresponding trigger type is found.
         */
        @JsonCreator
        public static TriggerType fromString(String name) {
            for (TriggerType t : TriggerType.values()) {
                if (t.toString().equalsIgnoreCase(name)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Trigger Type does not exist.");
        }

        /**
         * Returns the name of the trigger type.
         *
         * @return  The name of the trigger type.
         */
        @JsonValue
        public String value() {
            return this.toString();
        }

    }
}
/* Copyright (c) 2016, Salesforce.com, Inc.  All rights reserved. */
