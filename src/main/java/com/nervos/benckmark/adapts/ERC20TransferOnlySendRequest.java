package com.nervos.benckmark.adapts;


import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.testng.annotations.Test;

public class ERC20TransferOnlySendRequest extends Web3BasicRequest {



    @Override
    public Arguments getConfigArguments() {
        return null;
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {}

    @Override
    public void prepareRun(JavaSamplerContext context) {}

    @Override
    public boolean run(JavaSamplerContext context) {
        return false;
    }

    @Test
    public void test1() throws Exception {

        System.out.println("--1--");

        System.out.println("--2--");

    }
}
