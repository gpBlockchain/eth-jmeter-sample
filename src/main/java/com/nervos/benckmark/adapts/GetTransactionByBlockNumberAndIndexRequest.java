package com.nervos.benckmark.adapts;

import com.nervos.benckmark.model.TxMsg;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.Transaction;

import java.io.IOException;
import java.util.List;


public class GetTransactionByBlockNumberAndIndexRequest extends Web3BasicRequest{
    List<TxMsg> txMsgs;
    Integer currentSendIdx = 0;
    TxMsg currentTxMsg;


    @Override
    public Arguments getConfigArguments() {
        Arguments arguments = new Arguments();
        arguments.addArgument(Constant.SIZE, "1");
        return arguments;
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {
        txMsgs = BlkSingleton.getSingletonTxList(web3j,context.getIntParameter(Constant.SIZE));
    }

    @Override
    public void prepareRun(JavaSamplerContext context) {
        currentSendIdx++;
        currentTxMsg = txMsgs.get(currentSendIdx / txMsgs.size());
    }

    @Override
    public boolean run(JavaSamplerContext context) {
        Transaction tx = null;
        try {
            tx = this.web3j.ethGetTransactionByBlockNumberAndIndex(DefaultBlockParameterName.valueOf(currentTxMsg.getBlockNum().toString()), currentTxMsg.getIdx()).send().getTransaction().get();
            if (tx.getBlockNumber().compareTo(currentTxMsg.getBlockNum()) == 0
                    && tx.getHash().equals(tx.getHash())
                    && tx.getTransactionIndex().compareTo(currentTxMsg.getIdx()) == 0
                    && tx.getBlockHash().equals(currentTxMsg.getBlkHash())
            ) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
