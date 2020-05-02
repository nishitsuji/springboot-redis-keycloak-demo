package com.example.springbootrediskeycloakdemo.support;

import com.example.springbootrediskeycloakdemo.enums.AlgorithmCode;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtils {
  public static String encrypt(
      String target,
      String ivParameterSpecCode,
      String secretKeySpecCode,
      AlgorithmCode algorithmCode)
      throws Exception {

    final IvParameterSpec ivParameterSpec = new IvParameterSpec(ivParameterSpecCode.getBytes());
    final SecretKeySpec secretKeySpec =
        new SecretKeySpec(secretKeySpecCode.getBytes(), algorithmCode.getAlgorithmType());

    Cipher cipher = Cipher.getInstance(algorithmCode.getAlgorithm());
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
    byte[] encodeByte = cipher.doFinal(target.getBytes());

    return new String(Base64.getEncoder().encode(encodeByte));
  }

  public static String decrypt(
      String target, String ivParameterSpecCode, String secretKeySpecCode, AlgorithmCode algorithmCode)
      throws Exception {

    final IvParameterSpec ivParameterSpec = new IvParameterSpec(ivParameterSpecCode.getBytes());
    final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeySpecCode.getBytes(), algorithmCode.getAlgorithmType());

    Cipher cipher = Cipher.getInstance(algorithmCode.getAlgorithm());
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
    byte[] decodeByte = Base64.getDecoder().decode(target);
    return new String(cipher.doFinal(decodeByte));
  }
}
