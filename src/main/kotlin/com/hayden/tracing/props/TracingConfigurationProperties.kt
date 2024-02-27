package com.hayden.tracing.props

import com.hayden.tracing.model.ServiceIds
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix= "trace")
@Component
 class TracingConfigurationProperties {
    lateinit var serviceInstanceId: String
    lateinit var serviceId: String

    fun toServiceIds(): ServiceIds {
        return ServiceIds(serviceInstanceId, serviceId)
    }


}