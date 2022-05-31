package com.nervos.benckmark.adapts;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;

import java.util.List;

public class GetCodeRequest extends Web3BasicRequest {

    private List<String> contractAddress;
    private String currentContractAddress;
    private String defaultBlockParameterName;
    private Integer currentSendIdx = 0;

    @Override
    public Arguments getConfigArguments() {
        Arguments arguments = new Arguments();
        arguments.addArgument(Constant.SIZE, "1");
        arguments.addArgument(Constant.DefaultBlockParameterName, DefaultBlockParameterName.PENDING.getValue());
        return arguments;
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {
        this.contractAddress = SingletonService.getSingletonContractAddress(this.web3j, context.getIntParameter(Constant.SIZE));
        this.defaultBlockParameterName = context.getParameter(Constant.DefaultBlockParameterName);
    }

    @Override
    public void prepareRun(JavaSamplerContext context) {
        currentSendIdx++;
        this.currentContractAddress = contractAddress.get(currentSendIdx / contractAddress.size());
    }

    @Override
    public boolean run(JavaSamplerContext context) {
        try {
            this.web3j.ethGetCode(this.currentContractAddress, DefaultBlockParameter.valueOf(this.defaultBlockParameterName)).send().getCode();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
