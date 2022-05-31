package com.nervos.benckmark.adapts;

import com.nervos.benckmark.contracts.BEP20;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.web3j.protocol.core.methods.request.Transaction;

import java.math.BigInteger;

public class ERC20TransferEsGetGasRequest extends Web3BasicRequest{

    private BEP20 bep20;
    private Transaction ethEstimateGasTx;
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
        String data = bep20.transfer(bep20.getContractAddress(),new BigInteger("0")).encodeFunctionCall();
        ethEstimateGasTx = Transaction.createEthCallTransaction(credentials.getAddress(), bep20.getContractAddress(),data);
    }

    @Override
    public void prepareRun(JavaSamplerContext context) {
    }

    @Override
    public boolean run(JavaSamplerContext context) {
        try {
            BigInteger AmountUsed = this.web3j.ethEstimateGas(ethEstimateGasTx).send().getAmountUsed();
            return AmountUsed.compareTo(new BigInteger("0")) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
