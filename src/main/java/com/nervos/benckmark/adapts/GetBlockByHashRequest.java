package com.nervos.benckmark.adapts;

import com.nervos.benckmark.model.BlkMsg;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.util.List;

public class GetBlockByHashRequest extends Web3BasicRequest {

    private static final Logger LOG = LoggerFactory.getLogger(GetBlockByHashRequest.class);

    private List<BlkMsg> blkMsgList;
    private BlkMsg currentBlockMsg;
    private Integer currentSendCount;
    private Boolean isDetail;



    @Override
    public Arguments getConfigArguments() {
        Arguments arguments = new Arguments();
        arguments.addArgument(Constant.SIZE, "5");
        arguments.addArgument(Constant.BLOCK_MSG_DETAIL, "true");
        return arguments;
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {
        Integer size = context.getIntParameter(Constant.SIZE);
        this.blkMsgList = SingletonService.getSingletonBlkMsgList(web3j,size);

    }

    @Override
    public void prepareRun(JavaSamplerContext context) {
        this.currentBlockMsg = this.blkMsgList.get(this.currentSendCount/this.blkMsgList.size());
    }

    @Override
    public boolean run(JavaSamplerContext context) {

        EthBlock.Block block;
        try {
            block = this.web3j.ethGetBlockByHash(currentBlockMsg.getBlockHash(), isDetail).send().getBlock();
            if(block.getNumber().compareTo(currentBlockMsg.getHeight())==0 && block.getHash().equals(currentBlockMsg.getBlockHash())){
                return true;
            }
            LOG.warn("query msg not eq:{},currentBlockMsg:{}",block,currentBlockMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



}
