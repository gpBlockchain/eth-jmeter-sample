package com.nervos.benckmark.adapts;

import com.nervos.benckmark.model.TxMsg;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;

import java.util.List;


public class GetTransactionReceiptRequest extends Web3BasicRequest {
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
        txMsgs = BlkSingleton.getTxList(this.web3j, context.getIntParameter(Constant.SIZE));
    }

    @Override
    public void prepareRun(JavaSamplerContext context) {
        currentSendIdx++;
        currentTxMsg = txMsgs.get(currentSendIdx / txMsgs.size());
    }

    @Override
    public boolean run(JavaSamplerContext context) {
        TransactionReceipt receipt = null;
        try {
            receipt = this.web3j.ethGetTransactionReceipt(currentTxMsg.getTxHash()).send().getTransactionReceipt().get();
            if (receipt.getBlockNumber().compareTo(currentTxMsg.getBlockNum()) == 0
                    && receipt.getTransactionHash().equals(currentTxMsg.getTxHash())
                    && receipt.getTransactionIndex().compareTo(currentTxMsg.getIdx()) == 0
                    && receipt.getBlockHash().equals(currentTxMsg.getBlkHash())
            ) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
