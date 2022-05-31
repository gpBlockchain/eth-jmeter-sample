package com.nervos.benckmark.adapts;

import com.nervos.benckmark.contracts.BEP20;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ERC20TransferWithReceiptRequest extends Web3BasicRequest {

    private List<BEP20> bep20List;
    private static AtomicInteger currentSendUserIdx = new AtomicInteger(0);
    private BEP20 currentBep20;

    @Override
    public Arguments getConfigArguments() {
        Arguments arguments = new Arguments();
        arguments.addArgument(Constant.PRIVATE_KEYS, "0x723982bb19b3d9d36990fef21dbe88281bba7d67eb4ee85760d9566bcf9423d4\n" +
                "0x5aa9c3c53a68651524730ca3843314edeeebb9ef3ed55d7f08263d006a407ad0");
        arguments.addArgument(Constant.ERC20_Address, "");
        return arguments;
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {
        String privates = context.getParameter(Constant.PRIVATE_KEYS);

        //deploy contract
        this.bep20List = SingletonService.getSingletonBep20List(this.web3j, privates);

    }

    @Override
    public void prepareRun(JavaSamplerContext context) {
        int current = currentSendUserIdx.getAndAdd(1);
        currentBep20 = this.bep20List.get(current / this.bep20List.size());
    }

    @Override
    public boolean run(JavaSamplerContext context) {
        try {
            currentBep20.transfer(currentBep20.getContractAddress(), new BigInteger("0")).send().getTransactionHash();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
