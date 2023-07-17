package nz.roag.archerylogbook.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class HmacUtilTest {

    @Test
    void calculateHMACTest() {
        var hmac = new HmacUtil();

        var signature = hmac.calculateHMAC("test", "testdata");
        Assertions.assertEquals("TO51I1LuUVvX154opGSLPmNynr4+AmXGw34GF9uDajM=", signature, "Incorrect signature generated by HMAC");

        signature = hmac.calculateHMAC("secret", "testdata");
        Assertions.assertNotEquals("TO51I1LuUVvX154opGSLPmNynr4+AmXGw34GF9uDajM=", signature, "Incorrect signature generated by HMAC");
    }
}