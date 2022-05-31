package com.nervos.benckmark.adapts;

import com.nervos.benckmark.contracts.BEP20;
import com.nervos.benckmark.model.Account;
import com.nervos.benckmark.model.BlkMsg;
import com.nervos.benckmark.model.TxMsg;
import com.nervos.benckmark.util.TransactionUtil;
import com.nervos.benckmark.util.Web3Util;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlkSingleton {

    private volatile static List<String> addressList;
    private volatile static  List<BlkMsg> blkMsgList;
    private volatile static List<String> contractAddress;
    private volatile static List<TxMsg> txMsgs;
    private volatile static List<String> sendAddressList;
    private volatile static List<Account> accountList;
    private volatile static BEP20 bep20;
    private volatile static BigInteger chainId;




    public static List<String> getSingletonAddressList(Web3j web3j, Integer number) {
        if (BlkSingleton.addressList == null) {
            synchronized (BlkSingleton.class) {
                if ( BlkSingleton.addressList== null) {
                    BlkSingleton.addressList = Web3Util.getLatestSenderAddress(web3j,number);
                }
            }
        }
        return addressList;
    }

    public static List<BlkMsg> getSingletonBlkMsgList(Web3j web3j, Integer number){
        if (BlkSingleton.blkMsgList == null) {
            synchronized (BlkSingleton.class) {
                if ( BlkSingleton.blkMsgList== null) {
                    BlkSingleton.blkMsgList = getBlkMsgList(web3j,number);
                }
            }
        }
        return BlkSingleton.blkMsgList;
    }

    public static List<String> getSingletonContractAddress(Web3j web3j,Integer number){
        if (BlkSingleton.blkMsgList == null) {
            synchronized (BlkSingleton.class) {
                if ( BlkSingleton.blkMsgList== null) {
                    BlkSingleton.contractAddress = Web3Util.getLatestContractAddress(web3j,number);
                }
            }
        }
        return BlkSingleton.contractAddress;
    }

    public static List<TxMsg> getSingletonTxList(Web3j web3j,Integer num){
        if (BlkSingleton.txMsgs == null) {
            synchronized (BlkSingleton.class) {
                if ( BlkSingleton.txMsgs== null) {
                    BlkSingleton.txMsgs = getTxList(web3j,num);
                }
            }
        }
        return BlkSingleton.txMsgs;
    }

    public static BigInteger getChainId(Web3j web3j){
        if (BlkSingleton.chainId == null) {
            synchronized (BlkSingleton.class) {
                if ( BlkSingleton.chainId== null) {
                    try {
                        BlkSingleton.chainId = web3j.ethChainId().send().getChainId();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("ethChainId failed ");
                    }
                }
            }
        }
        return BlkSingleton.chainId;
    }

    public static List<TxMsg> getTxList(Web3j web3j,Integer num){
        List<String> txHashs = Web3Util.getLatestTxHash(web3j, num);
        List<TxMsg> txMsgs = new ArrayList<>();
        for (String txHash : txHashs) {
            Optional<Transaction> optionalTransaction = null;
            try {
                optionalTransaction = web3j.ethGetTransactionByHash(txHash).send().getTransaction();
                if (optionalTransaction.isPresent()) {
                    Transaction tx = optionalTransaction.get();
                    TxMsg txMsg = new TxMsg(tx.getBlockNumber(), tx.getBlockHash(), tx.getTransactionIndex(), tx.getHash());
                    txMsgs.add(txMsg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return txMsgs;
    }

    public static List<String> getSingletonSendAddressList(Web3j web3j,Integer num){
        if (BlkSingleton.sendAddressList == null) {
            synchronized (BlkSingleton.class) {
                if ( BlkSingleton.sendAddressList== null) {
                    BlkSingleton.sendAddressList = Web3Util.getLatestSenderAddress(web3j,num);
                }
            }
        }
        return BlkSingleton.sendAddressList;

    }
    public static List<Account> getSingletonAccountList(String rawPrivateKeys){

        if (BlkSingleton.accountList == null) {
            synchronized (BlkSingleton.class) {
                if ( BlkSingleton.accountList== null) {
                    try {
                        BlkSingleton.accountList = getAccountList(rawPrivateKeys);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("parse rawPrivateKeys failed");
                    }
                }
            }
        }
        return BlkSingleton.accountList;
    }

    public static BEP20 getSingletonBEP20(Credentials credentials, Web3j web3j,String contractAddress,Integer chainId){
        if (BlkSingleton.bep20 == null) {
            synchronized (BlkSingleton.class) {
                if ( BlkSingleton.bep20== null) {
                    try {
                        BlkSingleton.bep20 = initBEP20(credentials,web3j,contractAddress,chainId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("deploy failed");
                    }
                }
            }
        }
        return BlkSingleton.bep20;
    }

    private static BEP20 initBEP20(Credentials credentials, Web3j web3j, String contractAddress,Integer chainId) throws Exception {
        RawTransactionManager cutomerTokenTxManager = TransactionUtil.getTxManage(web3j,credentials,chainId);
        if (contractAddress.equals("")) {
            return BEP20.deploy(web3j, cutomerTokenTxManager, new DefaultGasProvider()).send();
        }
        return BEP20.load(contractAddress, web3j, cutomerTokenTxManager, new DefaultGasProvider());
    }

    private static List<Account> getAccountList(String rawPrivateKeys) throws Exception {
        List<Account> accountList = new ArrayList<>();
        String[] privateKeys = rawPrivateKeys.split("\n");

        List<Credentials> credentials = getCredentialsList(privateKeys);
        for (int i = 0; i < credentials.size(); i++) {
            Credentials credentials1 = credentials.get(i);
            // todo : nonce update
            Account account = new Account(credentials1, new BigInteger("1"));
            accountList.add(account);
        }
        return accountList;

    }


    private static List<Credentials> getCredentialsList(String[] privateKeys) {
        List<Credentials> credentialsList = new ArrayList<>();
        for (int i = 0; i < privateKeys.length; i++) {
            System.out.println("cur:"+i+",priv:"+privateKeys[i]);
            Credentials credentials = Credentials.create(privateKeys[i]);
            credentialsList.add(credentials);
        }
        return credentialsList;

    }

    private static List<BlkMsg> getBlkMsgList(Web3j web3j, Integer number) {
        List<BlkMsg> blkMsgList = new ArrayList<>();
        try {
            BigInteger blockHeight = web3j.ethBlockNumber().send().getBlockNumber();

            Integer step = blockHeight.divide(new BigInteger(number.toString())).intValue();
            for (int i = 1; i < blockHeight.intValue(); i += step) {
                BigInteger queryNum = new BigInteger(i + "");
                String blkHash = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(queryNum), false).send().getBlock().getHash();
                blkMsgList.add(new BlkMsg(queryNum, blkHash));
            }
            return blkMsgList;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("setUp failed");
        }
    }



}
