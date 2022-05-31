package com.nervos.benckmark.util;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.tx.RawTransactionManager;

import java.math.BigInteger;

public class TransactionUtil {

    public static String signTx(Web3j web3j, Credentials credentials, String to, BigInteger value, String data) throws Exception {
        BigInteger nonce;
        BigInteger gasPrice;
        BigInteger gasLimit;
        nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount();
        gasPrice = web3j.ethGasPrice().send().getGasPrice();
        gasLimit = web3j.ethEstimateGas(Transaction.createEthCallTransaction(credentials.getAddress(), to, data)).send().getAmountUsed();
        return signTx(web3j, credentials, nonce, gasPrice, gasLimit, to, value, data);
    }
    public static String signTx(Web3j web3j, Credentials credentials,  BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data) throws Exception {
        BigInteger nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount();
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
        return getTxManage(web3j, credentials).sign(rawTransaction);
    }

    public static String signTx(Web3j web3j, Credentials credentials, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, String data) throws Exception {
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
        return getTxManage(web3j, credentials).sign(rawTransaction);
    }

    public static RawTransactionManager getTxManage(Web3j web3j, Credentials credentials) throws Exception {
        BigInteger chainId = web3j.ethChainId().send().getChainId();
        return getTxManage(
                web3j, credentials, chainId.intValue());
    }

    public static RawTransactionManager getTxManage(Web3j web3j, Credentials credentials, Integer chainId) throws Exception {
        return new RawTransactionManager(
                web3j, credentials, chainId);
    }

}
