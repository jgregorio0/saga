package com.jesus.opencms.util;

import org.bouncycastle.crypto.digests.SHA384Digest;

/**
 * Created by jgregorio on 02/02/2017.
 */
public class Sha3Test {
    public static void main(String args[]){
        String username = "jgregorio";
        String pass = "1234";
        String code = "123456789";

        String toCripto = username + pass + code;

        SHA384Digest sha = new SHA384Digest();
        sha.update(toCripto.getBytes(), 0, toCripto.length());
        final byte[] output = new byte[sha.getDigestSize()];
        sha.doFinal(output, 0);

        String outStr = new String(output);
        System.out.println(outStr);

        //2
        String username2 = "jgregorio";
        String pass2 = "1234";
        String code2 = "123456789";

        String toCripto2 = username2 + pass2 + code2;

        SHA384Digest sha2 = new SHA384Digest();
        sha.update(toCripto2.getBytes(), 0, toCripto2.length());
        final byte[] output2 = new byte[sha2.getDigestSize()];
        sha2.doFinal(output2, 0);

        String outStr2 = new String(output2);
        System.out.println(outStr2);

    }
}
