package com.alexshabanov.booklib.web.mapping

import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import org.springframework.http.MediaType
import com.truward.ascetic.Request
import com.truward.ascetic.Response
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.JsonSerializer
import com.alexshabanov.booklib.service.GetUserProfilesResponse
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.alexshabanov.booklib.service.UserProfile
import com.alexshabanov.booklib.service.GetUserProfilesRequest
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext

//
// Mappers
//

private class UserProfileSerializer: JsonSerializer<UserProfile>() {
  override fun serialize(v: UserProfile, jg: JsonGenerator, p: SerializerProvider) {
    jg.writeStartObject()
    if (v.id != null) {
      jg.writeNumberField("id", v.id)
    }
    jg.writeStringField("n", v.name)
    jg.writeStringField("a", v.avatarUrl)
    jg.writeEndObject()
  }
}

private class GetUserProfilesResponseSerializer: JsonSerializer<GetUserProfilesResponse>() {
  override fun serialize(v: GetUserProfilesResponse, jg: JsonGenerator, p: SerializerProvider) {
    jg.writeStartObject()
    jg.writeObjectField("profiles", v.profiles)
    jg.writeEndObject()
  }
}

//
// Configuration code
//

private fun configMapper(mapper: ObjectMapper) {
  mapper.disable(MapperFeature.AUTO_DETECT_CREATORS, MapperFeature.AUTO_DETECT_SETTERS,
      MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_FIELDS,
      MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)

  // register marshallers
  val mod = SimpleModule("DomainModel", Version(1, 0, 0, ""))

  // - deserializers
  //mod.addDeserializer(javaClass<Foo>(), FooDeserializer())

  // - serializers
  mod.addSerializer(javaClass<UserProfile>(), UserProfileSerializer())
  mod.addSerializer(javaClass<GetUserProfilesResponse>(), GetUserProfilesResponseSerializer())

  mapper.registerModule(mod)
}

private class CustomJsonHttpMessageConverter(): MappingJackson2HttpMessageConverter() {

  override fun setObjectMapper(objectMapper: ObjectMapper?) {
    throw UnsupportedOperationException("Changing mapper is not supported")
  }

  override fun canRead(clazz: Class<out Any?>?, mediaType: MediaType?): Boolean {
    return canRead(mediaType) && javaClass<Request>().isAssignableFrom(clazz)
  }

  override fun canWrite(clazz: Class<out Any?>?, mediaType: MediaType?): Boolean {
    return canWrite(mediaType) && javaClass<Response>().isAssignableFrom(clazz)
  }
}

fun createDomainModelConverter(): MappingJackson2HttpMessageConverter {
  val result = CustomJsonHttpMessageConverter()
  configMapper(result.getObjectMapper())
  return result
}