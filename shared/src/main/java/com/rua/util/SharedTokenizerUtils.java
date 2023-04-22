package com.rua.util;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.EncodingType;

public class SharedTokenizerUtils {

    private SharedTokenizerUtils() {
    }

    // TODO: Waiting for chinese tokenizer support
    public static int tokenize() {
        final var registry = Encodings.newDefaultEncodingRegistry();
        final var enc = registry.getEncoding(EncodingType.CL100K_BASE);
        final var encoded = enc.encode("你好，世界Hello, world!");
        return encoded.size();
    }

}