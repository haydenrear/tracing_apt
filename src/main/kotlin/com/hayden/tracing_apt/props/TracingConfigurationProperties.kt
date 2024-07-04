package com.hayden.tracing_apt.props

import com.hayden.tracing_apt.model.ServiceIds
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix= "trace")
@Component
open class TracingConfigurationProperties {

    var serviceInstanceId: String? = null;
    var serviceId: String? = null;

    fun toServiceIds(): ServiceIds {
        return ServiceIds(serviceInstanceId.orEmpty(), serviceId.orEmpty())
    }
}