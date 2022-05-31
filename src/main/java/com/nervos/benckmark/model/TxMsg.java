package com.nervos.benckmark.model;

import java.math.BigInteger;

public class TxMsg {
    public TxMsg(BigInteger blockNum, String blkHash, BigInteger idx, String txHash) {
        this.blockNum = blockNum;
        BlkHash = blkHash;
        Idx = idx;
        this.txHash = txHash;
    }

    BigInteger blockNum;
    String BlkHash;

    public BigInteger getBlockNum() {
        return blockNum;
    }

    public String getBlkHash() {
        return BlkHash;
    }

    public BigInteger getIdx() {
        return Idx;
    }

    public String getTxHash() {
        return txHash;
    }

    BigInteger Idx;
    String txHash;
}