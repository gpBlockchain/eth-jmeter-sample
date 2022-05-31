package com.nervos.benckmark.adapts;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

import java.io.IOException;
import java.math.BigInteger;


public class BlockNumberRequest extends Web3BasicRequest {


    @Override
    public Arguments getConfigArguments() {
        return new Arguments();
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {

    }

    @Override
    public void prepareRun(JavaSamplerContext context) {

    }

    @Override
    public boolean run(JavaSamplerContext context) {
        try {
            BigInteger bigInteger = this.web3j.ethBlockNumber().send().getBlockNumber();
            if (bigInteger.compareTo(new BigInteger("0")) > 0) {
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
