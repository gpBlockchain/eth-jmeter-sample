package com.nervos.benckmark.adapts;

import com.nervos.benckmark.util.Web3Util;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.util.Map;

public abstract class Web3BasicRequest extends AbstractJavaSamplerClient {



    private SampleResult result;
    protected Web3j web3j;
    protected Credentials credentials;



    public abstract Arguments getConfigArguments();
    public abstract void setupOtherData(JavaSamplerContext context);

    public abstract void prepareRun(JavaSamplerContext context);
    public abstract boolean  run(JavaSamplerContext context);

    @Override
    public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument(Constant.RPC_URL, "1");
        arguments.addArgument(Constant.DEFAULT_PRIVATE_KEY,"0xd326ae3a6708b3f1ad08cefe5a429c313369a98e0a4533c5798be8458d405b31");
        Arguments newArgument = getConfigArguments();
        for(Map.Entry<String,String> entry: newArgument.getArgumentsAsMap().entrySet()){
            arguments.addArgument(entry.getKey(),entry.getValue());
        }
        return arguments;
    }


    @Override
    public void setupTest(JavaSamplerContext context) {
        result = new SampleResult();
        String url = context.getParameter(Constant.RPC_URL);
        this.web3j = Web3Util.initWeb3j(url);
        System.out.println("priv:"+context.getParameter(Constant.DEFAULT_PRIVATE_KEY));
        this.credentials = Credentials.create(context.getParameter(Constant.DEFAULT_PRIVATE_KEY));
        setupOtherData(context);
    }


    @Override
    public void teardownTest(JavaSamplerContext context) {
        System.out.println("teardownTest");
    }




    @Override
    public SampleResult runTest(JavaSamplerContext context) {

        prepareRun(context);
        SampleResult result = new SampleResult();
        result.sampleStart(); // Jmeter 开始计时
        boolean success = run(context);
        result.setSuccessful(success); // 是否成功
        result.sampleEnd(); // Jmeter 结束计时
        return result;
    }


}
