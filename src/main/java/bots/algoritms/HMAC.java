package bots.algoritms;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC {
  public byte[] calcHmacSha256(byte[] secretKey, byte[] message) {
    byte[] hmacSha256;
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
      mac.init(secretKeySpec);
      hmacSha256 = mac.doFinal(message);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
    return hmacSha256;
  }

  public String hex(byte[] signature) {
    String signatureStr = "";
    signatureStr = String.format("%032x", new BigInteger(1, signature));
    return signatureStr;
  }
}

