package com.nervos.benckmark.util;

import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

/**
 * 1. @description:
 * 2. @author: Dawn
 * 3. @time: 2022/7/12
 */
public class DefaultGasProvisder extends StaticGasProvider {
    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(9000000L);
    public static final BigInteger GAS_PRICE = BigInteger.valueOf(90000000000000L);

    public DefaultGasProvisder() {
        super(GAS_PRICE, GAS_LIMIT);
    }
}

