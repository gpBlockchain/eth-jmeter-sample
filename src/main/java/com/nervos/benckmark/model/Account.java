package com.nervos.benckmark.model;

import org.web3j.crypto.Credentials;

import java.math.BigInteger;


public class Account {
    private Credentials credentials;
    private BigInteger latestSendTxNonce;

    public Credentials getCredentials() {
        return credentials;
    }

    public BigInteger getLatestSendTxNonce() {
        return latestSendTxNonce;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public void setLatestSendTxNonce(BigInteger latestSendTxNonce) {
        this.latestSendTxNonce = latestSendTxNonce;
    }

    public Account(Credentials credentials, BigInteger latestSendTxNonce) {
        this.credentials = credentials;
        this.latestSendTxNonce = latestSendTxNonce;
    }
}
