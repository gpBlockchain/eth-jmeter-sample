package com.nervos.benckmark.adapts;

import com.nervos.benckmark.contracts.LogContract;
import com.nervos.benckmark.model.Account;
import com.nervos.benckmark.util.TransactionUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LogContractRequest extends Web3BasicRequest {
    public LogContract logContract;
    private List<Account> accountList;
    private static AtomicInteger curSendIdx = new AtomicInteger(0);
    private String data = "";
    private Credentials currentSendCredentials;
    private BigInteger chainId;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private BigInteger logLoopCount;

    @Override
    public Arguments getConfigArguments() {
        Arguments arguments = new Arguments();
        arguments.addArgument(Constant.Mnemonic, Constant.DEFAULT_MNEMONIC);
        arguments.addArgument(Constant.SIZE,"100");
        arguments.addArgument(Constant.ERC20_Address, "");
        arguments.addArgument(Constant.GasLimit, "1000000");
        arguments.addArgument(Constant.GasPrice, "10000");
        arguments.addArgument(Constant.LogLoopCount,"10");
        return arguments;
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {
        String mnstr = context.getParameter(Constant.Mnemonic);
        System.out.println("nm str;"+mnstr);
        String contractAddress = context.getParameter(Constant.ERC20_Address);
        int size= context.getIntParameter(Constant.SIZE);
        this.chainId = SingletonService.getChainId(this.web3j);

        this.accountList = SingletonService.getSingletonAccountList(mnstr,size);
        //deploy contract
        this.logContract = SingletonService.getSingletonLogContract(this.accountList.get(0).getCredentials(), this.web3j, contractAddress, this.chainId.intValue());
        System.out.println("logContract:"+this.logContract.getContractAddress());
        this.gasLimit = new BigInteger(context.getParameter(Constant.GasLimit));
        this.gasPrice = new BigInteger(context.getParameter(Constant.GasPrice));
        this.logLoopCount = new BigInteger(context.getParameter(Constant.LogLoopCount));
    }

    @Override
    public void prepareRun(JavaSamplerContext context) {
        this.data = this.logContract.testLog(this.logLoopCount).encodeFunctionCall();
        int currentIdx = curSendIdx.getAndAdd(1) % this.accountList.size();
        System.out.println("currentIdx:" + currentIdx);
        this.currentSendCredentials = this.accountList.get(currentIdx).getCredentials();
    }

    @Override
    public boolean run(JavaSamplerContext context) {
        return sendTx(this.web3j, this.currentSendCredentials, this.logContract.getContractAddress(), new BigInteger("0"), this.data);
    }


    private boolean sendTx(Web3j web3j, Credentials fromCredentials, String contractAddress, BigInteger bigInteger, String payload) {
        try {
            String hexStr = TransactionUtil.signTx(this.web3j, fromCredentials, gasPrice, gasLimit, contractAddress, bigInteger, payload);
            String txHash = web3j.ethSendRawTransaction(hexStr).send().getTransactionHash();
            System.out.println("txHash:" + txHash);
            if (txHash.length() > 10) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void main(String[] args) {
        LogContractRequest sample = new LogContractRequest();

        Arguments arguments = sample.getConfigArguments();
        arguments.addArgument(Constant.RPC_URL, "https://godwoken-alphanet-v1.ckbapp.dev");
        arguments.addArgument(Constant.DEFAULT_PRIVATE_KEY,"0xdd50cac37ec6dd12539a968c1a2cbedda75bd8724f7bcad486548eaabb87fc8b");
        arguments.addArgument(Constant.Mnemonic,Constant.DEFAULT_MNEMONIC);
        arguments.addArgument(Constant.SIZE,"10");

        arguments.addArgument(Constant.ERC20_Address, "");
        System.out.println("!!!");
        JavaSamplerContext context = new JavaSamplerContext(arguments);

        sample.setupTest(context);
        System.out.println("???");
        for (int i = 0; i < 1000; i++) {
            System.out.println("----");
            sample.runTest(context);
        }
        sample.teardownTest(context);
    }
}
