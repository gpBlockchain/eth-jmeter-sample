package com.nervos.benckmark.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Web3Util {

    public static Web3j initWeb3j(String url) {
        return Web3j.build(new HttpService(url));
    }

    private static final Logger LOG = LoggerFactory.getLogger(Web3Util.class);

    /**
     * 获取最近发送交易的用户
     *
     * @param number
     * @return
     */
    public static List<String> getLatestSenderAddress(Web3j web3j, Integer number) {
        List<String> addressUser = new ArrayList<>();
        BigInteger height = getHeight(web3j);
        if (height == null) {
            return null;
        }
        for (int i = height.intValue(); i > 0; i--) {
            List<EthBlock.TransactionObject> txs = getBlockTxs(web3j, new BigInteger(i + ""));
            if (txs == null) {
                return null;
            }
            for (int j = 0; j < txs.size(); j++) {
                EthBlock.TransactionObject transactionObject = txs.get(j);
                if (!addressUser.contains(transactionObject.getFrom()) && transactionObject.getFrom() != null) {
                    addressUser.add(transactionObject.getFrom());
                    if (addressUser.size() == number) {
                        return addressUser;
                    }
                }
            }
        }
        LOG.error("not enough addressUser:{} ,expected :{}", addressUser.size(), number);
        return addressUser;
    }

    /**
     * 获取 合约地址
     *
     * @param number
     * @return
     */
    public static List<String> getLatestContractAddress(Web3j web3j, Integer number) {
        List<String> addressUser = new ArrayList<>();
        BigInteger height = getHeight(web3j);
        if (height == null) {
            return null;
        }
        for (int i = height.intValue(); i > 0; i--) {
            List<EthBlock.TransactionObject> txs = getBlockTxs(web3j, new BigInteger(i + ""));
            if (txs == null) {
                return null;
            }
            for (int j = 0; j < txs.size(); j++) {
                EthBlock.TransactionObject transactionObject = txs.get(j);
                if (!addressUser.contains(transactionObject.getTo()) && transactionObject.getTo() != null) {
                    addressUser.add(transactionObject.getTo());
                    if (addressUser.size() == number) {
                        return addressUser;
                    }
                }
            }
        }
        LOG.error("not enough addressUser:{} ,expected :{}", addressUser.size(), number);
        return addressUser;
    }


    /**
     * 查询最近的交易hash
     *
     * @param web3j
     * @param number
     * @return
     */
    public static List<String> getLatestTxHash(Web3j web3j, Integer number) {
        List<String> txs = new ArrayList<>();
        BigInteger height = getHeight(web3j);
        if (height == null) {
            return null;
        }
        for (int i = 0; i < height.intValue(); i++) {
            List<EthBlock.TransactionObject> transactionObjectList = getBlockTxs(web3j, new BigInteger(i + ""));
            if (transactionObjectList == null) {
                break;
            }
            for (int j = 0; j < transactionObjectList.size(); j++) {
                String txHash = transactionObjectList.get(j).getHash();
                if (txHash != null) {
                    txs.add(transactionObjectList.get(j).getHash());
                    if (txs.size() == number) {
                        return txs;
                    }
                }

            }
        }
        LOG.error("not enough txs:{} ,expected :{}", txs.size(), number);
        return txs;
    }

    private static BigInteger getHeight(Web3j web3j) {
        try {
            return web3j.ethBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            LOG.error("getBlockNumber failed :{}", e.getMessage());
        }
        return null;
    }

    private static List<EthBlock.TransactionObject> getBlockTxs(Web3j web3j, BigInteger queryNum) {

        try {
            EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(queryNum), true).send().getBlock();
            List<EthBlock.TransactionObject> transactions = new ArrayList<>();
            for (int i = 0; i < block.getTransactions().size(); i++) {
                transactions.add((EthBlock.TransactionObject) block.getTransactions().get(i));
            }
            return transactions;
        } catch (Exception e) {
            LOG.warn("ethGetBlockByNumber:{} failed :{}", queryNum, e.getMessage());
            return null;
        }

    }


}
