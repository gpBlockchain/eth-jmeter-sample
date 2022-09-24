package com.nervos.benckmark.adapts;

import com.nervos.benckmark.model.Account;
import com.nervos.benckmark.util.TransactionUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TxBuildRequest extends Web3BasicRequest{

    private List<Account> accountList;
    private static AtomicInteger curSendIdx = new AtomicInteger(0);
    private Credentials currentSendCredentials;
    private BigInteger chainId;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private String to;
    private BigInteger value;
    private String payload;

    @Override
    public Arguments getConfigArguments() {
        Arguments arguments = new Arguments();
        arguments.addArgument(Constant.Mnemonic, Constant.DEFAULT_MNEMONIC);
        arguments.addArgument(Constant.SIZE,"100");
        arguments.addArgument(Constant.TO, "");
        arguments.addArgument(Constant.GasLimit, "1000000");
        arguments.addArgument(Constant.GasPrice, "10000");
        arguments.addArgument(Constant.VALUE,"10");
        arguments.addArgument(Constant.PAYLOAD,"");
        return arguments;
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {
        String mnstr = context.getParameter(Constant.Mnemonic);
        System.out.println("nm str;"+mnstr);
        int size= context.getIntParameter(Constant.SIZE);
        this.chainId = SingletonService.getChainId(this.web3j);

        this.accountList = SingletonService.getSingletonAccountList(mnstr,size);
        this.gasLimit = new BigInteger(context.getParameter(Constant.GasLimit));
        this.gasPrice = new BigInteger(context.getParameter(Constant.GasPrice));
        this.payload = context.getParameter(Constant.PAYLOAD);
        this.to = context.getParameter(Constant.TO);
        this.value = new BigInteger(context.getParameter(Constant.VALUE));

    }

    @Override
    public void prepareRun(JavaSamplerContext context) {
        int currentIdx = curSendIdx.getAndAdd(1) % this.accountList.size();
        System.out.println("currentIdx:" + currentIdx);
        this.currentSendCredentials = this.accountList.get(currentIdx).getCredentials();
    }


    @Override
    public boolean run(JavaSamplerContext context) {
        return sendTx(this.web3j, this.currentSendCredentials, this.to, this.value, this.payload);
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

}
