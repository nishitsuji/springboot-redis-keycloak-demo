package com.example.springbootrediskeycloakdemo.enums;

import lombok.Getter;

@Getter
public enum AlgorithmCode {
  AES_CBC_NOPADDING(128, "AES", "AES/CBC/NoPadding"),
  AES_CBC_PKCS5PADDING(128, "AES", "AES/CBC/PKCS5Padding"),
  AES_ECB_NOPADDING(128, "AES", "AES/ECB/NoPadding"),
  AES_ECB_PKCS5PADDING(128, "AES", "AES/ECB/PKCS5Padding"),
  DES_CBC_NOPADDING(56, "DES", "DES/CBC/NoPadding"),
  DES_CBC_PKCS5PADDING(56, "DES", "DES/CBC/PKCS5Padding"),
  DES_ECB_NOPADDING(56, "DES", "DES/ECB/NoPadding"),
  DES_ECB_PKCS5PADDING(56, "DES", "DES/ECB/PKCS5Padding"),
  DESEDE_CBC_NOPADDING(168, "DESede", "DESede/CBC/NoPadding"),
  DESEDE_CBC_PKCS5PADDING(168, "DESede", "DESede/CBC/PKCS5Padding"),
  DESEDE_ECB_NOPADDING(168, "DESede", "DESede/ECB/NoPadding"),
  DESEDE_ECB_PKCS5PADDING(168, "DESede", "DESede/ECB/PKCS5Padding"),
  RSA_ECB_PKCS1PADDING_1024(1024, "RSA", "RSA/ECB/PKCS1Padding"),
  RSA_ECB_PKCS1PADDING_2048(2048, "RSA", "RSA/ECB/PKCS1Padding"),
  RSA_ECB_OAEPWITHSHA_1_1024(1024, "RSA", "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),
  RSA_ECB_OAEPWITHSHA_1_2048(2048, "RSA", "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),
  RSA_ECB_OAEPWITHSHA_256_1024(1024, "RSA", "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"),
  RSA_ECB_OAEPWITHSHA_256_2048(2048, "RSA", "RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

  private final Integer algorithmByte;
  private final String algorithmType;
  private String algorithm;
  private int keyLen;

  AlgorithmCode(final Integer algorithmByte, final String algorithmType, final String algorithm) {
    this.algorithmByte = algorithmByte;
    this.algorithmType = algorithmType;
    this.algorithm = algorithm;
  }
}