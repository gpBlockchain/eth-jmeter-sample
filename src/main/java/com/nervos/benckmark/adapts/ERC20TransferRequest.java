package com.nervos.benckmark.adapts;

import com.nervos.benckmark.contracts.BEP20;
import com.nervos.benckmark.model.Account;
import com.nervos.benckmark.util.TransactionUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ERC20TransferRequest extends Web3BasicRequest{

    public BEP20 bep20;
    private List<Account> accountList;
    private static AtomicInteger curSendIdx = new AtomicInteger(0);
    private  String data = "";
    private Credentials currentSendCredentials;
    private BigInteger chainId;



    @Override
    public Arguments getConfigArguments() {
        Arguments arguments = new Arguments();
        arguments.addArgument(Constant.Mnemonic, Constant.DEFAULT_MNEMONIC);
        arguments.addArgument(Constant.SIZE,"10");
        arguments.addArgument(Constant.ERC20_Address, "");
        return arguments;
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {
        System.out.println("------setupOtherData------");
        String privates = context.getParameter(Constant.Mnemonic);
        String contractAddress = context.getParameter(Constant.ERC20_Address);
        int size = context.getIntParameter(Constant.SIZE);
        this.chainId = SingletonService.getChainId(this.web3j);
        this.accountList = SingletonService.getSingletonAccountList(privates,size);
        //deploy contract
        this.bep20 = SingletonService.getSingletonBEP20(this.accountList.get(0).getCredentials(),this.web3j,contractAddress,this.chainId.intValue());
        System.out.println("contract address:"+this.bep20.getContractAddress());
    }

    @Override
    public void prepareRun(JavaSamplerContext context) {
        this.data = this.bep20.transfer(this.bep20.getContractAddress(),new BigInteger("0")).encodeFunctionCall();
        int currentIdx = curSendIdx.getAndAdd(1) % this.accountList.size();
        System.out.println("currentIdx:"+currentIdx);
        this.currentSendCredentials =  this.accountList.get(currentIdx).getCredentials();
    }

    @Override
    public boolean run(JavaSamplerContext context) {
        return sendTx(this.web3j,this.currentSendCredentials,this.bep20.getContractAddress(),new BigInteger("0"),this.data);
    }


    private boolean sendTx(Web3j web3j, Credentials fromCredentials, String contractAddress, BigInteger bigInteger, String payload) {
        try {
            String hexStr = TransactionUtil.signTx(this.web3j, fromCredentials, contractAddress, bigInteger, payload);
            String txHash = web3j.ethSendRawTransaction(hexStr).send().getTransactionHash();
            System.out.println("txHash:"+txHash);
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
        Arguments arguments = new Arguments();
        arguments.addArgument(Constant.RPC_URL, "https://godwoken-betanet-v1.ckbapp.dev");
        arguments.addArgument(Constant.DEFAULT_PRIVATE_KEY,"0xd326ae3a6708b3f1ad08cefe5a429c313369a98e0a4533c5798be8458d405b31");
        arguments.addArgument(Constant.Mnemonic, Constant.DEFAULT_MNEMONIC);
        arguments.addArgument(Constant.SIZE,"10");
        arguments.addArgument(Constant.ERC20_Address, "");

        JavaSamplerContext context = new JavaSamplerContext(arguments);
        ERC20TransferRequest sample = new ERC20TransferRequest();
        sample.setupTest(context);

        for (int i = 0; i < 10000; i++) {
            System.out.println("----");
            sample.runTest(context);
        }
        sample.teardownTest(context);
    }
}
