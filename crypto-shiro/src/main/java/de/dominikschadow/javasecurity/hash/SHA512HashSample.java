/*
 * Copyright (C) 2014 Dominik Schadow, dominikschadow@gmail.com
 *
 * This file is part of the Java Security project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dominikschadow.javasecurity.hash;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * SHA512 hashing sample with Apache Shiro. Uses a private base salt, configures the number of ITERATIONS, automatically
 * generates a salt and calculates the hash value.
 *
 * @author Dominik Schadow
 */
public class SHA512HashSample {
    private static final Logger logger = LoggerFactory.getLogger(SHA512HashSample.class);
    /** Nothing up my sleeve number as private salt, not good for production. */
    private static final byte[] PRIVATE_SALT_BYTES = {3, 1, 4, 1, 5, 9, 2, 6, 5};
    private static final int ITERATIONS = 1000000;

    public static void main(String[] args) {
        SHA512HashSample hs = new SHA512HashSample();
        String initialText = "Java Forum Stuttgart 2014";

        Hash hash = hs.calculateHash(initialText);
        boolean correct = hs.verifyPassword(hash.getBytes(), hash.getSalt(), initialText);

        logger.info("Entered password is correct: {}", correct);
    }

    private Hash calculateHash(String initialText) {
        ByteSource privateSalt = ByteSource.Util.bytes(PRIVATE_SALT_BYTES);
        DefaultHashService hashService = new DefaultHashService();
        hashService.setPrivateSalt(privateSalt);
        hashService.setGeneratePublicSalt(true);
        hashService.setHashIterations(ITERATIONS);

        HashRequest.Builder builder = new HashRequest.Builder();
        builder.setSource(ByteSource.Util.bytes(initialText));

        Hash hash = hashService.computeHash(builder.build());

        logger.info("Hash algorithm {}, iterations {}, public salt {}", hash.getAlgorithmName(), hash.getIterations(), hash.getSalt());

        return hash;
    }

    private boolean verifyPassword(byte[] originalHash, ByteSource publicSalt, String initialText) {
        ByteSource privateSalt = ByteSource.Util.bytes(PRIVATE_SALT_BYTES);
        DefaultHashService hashService = new DefaultHashService();
        hashService.setPrivateSalt(privateSalt);
        hashService.setHashIterations(ITERATIONS);

        HashRequest.Builder builder = new HashRequest.Builder();
        builder.setSource(ByteSource.Util.bytes(initialText));
        builder.setSalt(publicSalt);

        Hash comparisonHash = hashService.computeHash(builder.build());

        logger.info("initialText {}", initialText);
        logger.info("1 hash: {}", Base64.encodeToString(originalHash));
        logger.info("2 hash: {}", comparisonHash.toBase64());

        return Arrays.equals(originalHash, comparisonHash.getBytes());
    }
}
