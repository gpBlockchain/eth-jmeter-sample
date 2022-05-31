package com.nervos.benckmark.adapts;

import com.nervos.benckmark.contracts.BEP20;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

import java.math.BigInteger;

public class ERC20TransferCallRequest extends Web3BasicRequest{

    private BEP20 bep20;

    @Override
    public Arguments getConfigArguments() {
        return new Arguments();
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {
        int chainId;
        try {
            chainId = this.web3j.ethChainId().send().getChainId().intValue();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("get chain id failed");
        }
        bep20 = SingletonService.getSingletonBEP20(this.credentials,this.web3j,"",chainId);
    }

    @Override
    public void prepareRun(JavaSamplerContext context) {

    }

    @Override
    public boolean run(JavaSamplerContext context) {
        try {
            BigInteger balanceOfUser = bep20.balanceOf(this.credentials.getAddress()).send();
            return balanceOfUser.compareTo(new BigInteger("0")) >= 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
