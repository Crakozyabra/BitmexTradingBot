package bots.algoritms.ordermakers;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Signature {

  public byte[] createSignature(String verb, String path, String data, String expires, String apiSecret) {
    HMAC hmac = new HMAC();
    return hmac.calcHmacSha256(apiSecret.getBytes(StandardCharsets.UTF_8),
        (verb + path + expires + data).getBytes(StandardCharsets.UTF_8));
  }

  public String signatureToString(byte[] signature) {
    String signatureStr = "";
    signatureStr = String.format("%032x", new BigInteger(1, signature));
    return signatureStr;
  }

    public static class SignatureWS {
        // signature is hex(HMAC_SHA256(secret, 'GET/realtime' + expires)) for websocket connection
        public byte[] createSignature(String expires, String apiSecret) {
            HMAC hmac = new HMAC();
            return hmac.calcHmacSha256(apiSecret.getBytes(StandardCharsets.UTF_8),
                    ("GET/realtime" + expires).getBytes(StandardCharsets.UTF_8));
        }

        public String signatureToString(byte[] signature) {
            String signatureStr = "";
            signatureStr = String.format("%032x", new BigInteger(1, signature));
            return signatureStr;
        }
    }
}
