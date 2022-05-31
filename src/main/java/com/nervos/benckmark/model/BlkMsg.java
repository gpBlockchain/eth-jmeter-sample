package com.nervos.benckmark.model;

import java.math.BigInteger;

public class BlkMsg {
    public BlkMsg(BigInteger height, String blockHash) {
        this.blockHash = blockHash;
        this.height = height;
    }

    BigInteger height;
    String blockHash;

    public BigInteger getHeight() {
        return height;
    }

    public String getBlockHash() {
        return blockHash;
    }
}