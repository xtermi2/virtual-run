package com.github.xtermi2.virtualrun.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.quarkus.jackson.ObjectMapperCustomizer
import javax.inject.Singleton


@Singleton
class RegisterCustomModuleCustomizer : ObjectMapperCustomizer {
    override fun customize(mapper: ObjectMapper) {
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
}