package nz.roag.archerylogbook.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class HmacUtil {

    private final Logger logger = LoggerFactory.getLogger(HmacUtil.class);

    public String calculateHMAC(String secret, String data) {
        return calculateHMAC("HmacSHA256", secret, data);
    }

    String calculateHMAC(String algorithm, String secret, String data) {
        try {
            logger.trace("Generating HMAC for {}", data);
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), algorithm);

            Mac mac = Mac.getInstance(algorithm);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());

            String base64HmacSha256 = Base64.getEncoder().encodeToString(rawHmac);
            logger.trace("base64HmacSha256: {}", base64HmacSha256);

            return base64HmacSha256;
        } catch (GeneralSecurityException e) {
            logger.warn("Unexpected error while creating hash: " + e.getMessage(), e);
            throw new IllegalArgumentException();
        }
    }
}
