package com.example.springbootrediskeycloakdemo.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;

public class JsonUtils {

  public static <T> List<T> decodeToList(final String json, final TypeReference<List<T>> valueTypeRef) {

    final ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(json, valueTypeRef);
    } catch (final Exception e) {
      throw new RuntimeException("FATAL: jsonの変換に失敗しました。", e);
    }
  }

  public static <T> T decodeToObject(final String json, final TypeReference<T> valueTypeRef) {

    final ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(json, valueTypeRef);
    } catch (final Exception e) {
      throw new RuntimeException("FATAL: jsonの変換に失敗しました。", e);
    }
  }

  public static <T> String encodeToList(final List<T> origin) {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    try {
      return mapper.writeValueAsString(origin);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException("FATAL: jsonの生成に失敗しました。", e);
    }
  }

  public static <T> String encode(final T origin) {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    try {
      return mapper.writeValueAsString(origin);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException("FATAL: jsonの生成に失敗しました。", e);
    }
  }
}
