package com.nervos.benckmark.adapts;

import com.google.common.collect.ImmutableList;
import com.nervos.benckmark.contracts.BEP20;
import com.nervos.benckmark.contracts.LogContract;
import com.nervos.benckmark.model.Account;
import com.nervos.benckmark.model.BlkMsg;
import com.nervos.benckmark.model.TxMsg;
import com.nervos.benckmark.util.TransactionUtil;
import com.nervos.benckmark.util.Web3Util;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SingletonService {
    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    private volatile static List<String> addressList;
    private volatile static  List<BlkMsg> blkMsgList;
    private volatile static List<String> contractAddress;
    private volatile static List<TxMsg> txMsgs;
    private volatile static List<String> sendAddressList;
    private volatile static List<Account> accountList;
    private volatile static List<BEP20> bep20List;
    private volatile static BEP20 bep20;
    private volatile static LogContract logContract;
    private volatile static BigInteger chainId;
    private static ContractGasProvider staticGasProvider = new StaticGasProvider(new BigInteger("10000000000"),new BigInteger("9000000"));



    public static List<String> getSingletonAddressList(Web3j web3j, Integer number) {
        if (SingletonService.addressList == null) {
            synchronized (SingletonService.class) {
                if ( SingletonService.addressList== null) {
                    SingletonService.addressList = Web3Util.getLatestSenderAddress(web3j,number);
                }
            }
        }
        return addressList;
    }

    public static List<BlkMsg> getSingletonBlkMsgList(Web3j web3j, Integer number){
        if (SingletonService.blkMsgList == null) {
            synchronized (SingletonService.class) {
                if ( SingletonService.blkMsgList== null) {
                    SingletonService.blkMsgList = getBlkMsgList(web3j,number);
                }
            }
        }
        return SingletonService.blkMsgList;
    }

    public static List<String> getSingletonContractAddress(Web3j web3j,Integer number){
        if (SingletonService.blkMsgList == null) {
            synchronized (SingletonService.class) {
                if ( SingletonService.blkMsgList== null) {
                    SingletonService.contractAddress = Web3Util.getLatestContractAddress(web3j,number);
                }
            }
        }
        return SingletonService.contractAddress;
    }

    public static List<TxMsg> getSingletonTxList(Web3j web3j,Integer num){
        if (SingletonService.txMsgs == null) {
            synchronized (SingletonService.class) {
                if ( SingletonService.txMsgs== null) {
                    SingletonService.txMsgs = getTxList(web3j,num);
                }
            }
        }
        return SingletonService.txMsgs;
    }

    public static List<BEP20> getSingletonBep20List(Web3j web3j,String rawPrivateKeys){
        if (SingletonService.bep20List == null) {
            synchronized (SingletonService.class) {
                if ( SingletonService.bep20List== null) {
                    SingletonService.bep20List = initBep20List(web3j,rawPrivateKeys);
                }
            }
        }
        return SingletonService.bep20List;
    }

    public static List<BEP20> initBep20List(Web3j web3j,String rawPrivateKeys){
        List<Account> accountList;
        BEP20 bep20;
        List<BEP20> bep20List = new ArrayList<>();
        try {
            accountList = getAccountList(rawPrivateKeys);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("parse rawPrivateKeys failed");
        }
        try {
            bep20 = initBEP20(accountList.get(0).getCredentials(),web3j,"",getChainId(web3j).intValue());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("deploy bep 20 failed");
        }
        for(Account account:accountList){
            try {
                bep20List.add(initBEP20(account.getCredentials(),web3j,bep20.getContractAddress(),getChainId(web3j).intValue()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bep20List;
    }

    public static BigInteger getChainId(Web3j web3j){
        if (SingletonService.chainId == null) {
            synchronized (SingletonService.class) {
                if ( SingletonService.chainId== null) {
                    try {
                        SingletonService.chainId = web3j.ethChainId().send().getChainId();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("ethChainId failed ");
                    }
                }
            }
        }
        return SingletonService.chainId;
    }

    public static List<TxMsg> getTxList(Web3j web3j,Integer num){
        List<String> txHashs = Web3Util.getLatestTxHash(web3j, num);
        List<TxMsg> txMsgs = new ArrayList<>();
        assert txHashs != null;
        for (String txHash : txHashs) {
            Optional<Transaction> optionalTransaction;
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
        if (SingletonService.sendAddressList == null) {
            synchronized (SingletonService.class) {
                if ( SingletonService.sendAddressList== null) {
                    SingletonService.sendAddressList = Web3Util.getLatestSenderAddress(web3j,num);
                }
            }
        }
        return SingletonService.sendAddressList;

    }
    public static List<Account> getSingletonAccountList(String hd,int size ){

        if (SingletonService.accountList == null) {
            synchronized (SingletonService.class) {
                if ( SingletonService.accountList== null) {
                    try {
                        SingletonService.accountList = getAccountListByHD(hd,size);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("parse rawPrivateKeys failed");
                    }
                }
            }
        }
        return SingletonService.accountList;
    }

    public static LogContract getSingletonLogContract(Credentials credentials,Web3j web3j,String contractAddress,Integer chainId){
        if (SingletonService.logContract == null) {
            synchronized (SingletonService.class) {
                if ( SingletonService.logContract== null) {
                    try {
                        SingletonService.logContract = initLogContract(credentials,web3j,contractAddress,chainId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("deploy failed");
                    }
                }
            }
        }
        return SingletonService.logContract;
    }

    private static LogContract initLogContract(Credentials credentials, Web3j web3j, String contractAddress, Integer chainId) throws Exception{
        RawTransactionManager cutomerTokenTxManager = TransactionUtil.getTxManage(web3j,credentials,chainId);
        if (contractAddress.equals("")) {
            return LogContract.deploy(web3j, cutomerTokenTxManager, staticGasProvider).send();
        }
        return LogContract.load(contractAddress, web3j, cutomerTokenTxManager, staticGasProvider);

    }

    public static BEP20 getSingletonBEP20(Credentials credentials, Web3j web3j,String contractAddress,Integer chainId){
        if (SingletonService.bep20 == null) {
            synchronized (SingletonService.class) {
                if ( SingletonService.bep20== null) {
                    try {
                        SingletonService.bep20 = initBEP20(credentials,web3j,contractAddress,chainId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("deploy failed");
                    }
                }
            }
        }
        return SingletonService.bep20;
    }

    private static BEP20 initBEP20(Credentials credentials, Web3j web3j, String contractAddress,Integer chainId) throws Exception {
        RawTransactionManager cutomerTokenTxManager = TransactionUtil.getTxManage(web3j,credentials,chainId);
        if (contractAddress.equals("")) {
            return BEP20.deploy(web3j, cutomerTokenTxManager, staticGasProvider).send();
        }
        return BEP20.load(contractAddress, web3j, cutomerTokenTxManager, staticGasProvider);
    }

    private static List<Account> getAccountListByHD(String mnstr,int size){

        SecureRandom secureRandom = new SecureRandom();
        byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8];
        secureRandom.nextBytes(entropy);

        //生成12位助记词
        List<String> str = Arrays.asList(mnstr.split(" "));

        //使用助记词生成钱包种子
        byte[] seed = MnemonicCode.toSeed(str, "");
        DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);
        List<Account> accountList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            DeterministicKey deterministicKey = deterministicHierarchy
                    .deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(i));
            byte[] bytes = deterministicKey.getPrivKeyBytes();
            ECKeyPair keyPair = ECKeyPair.create(bytes);
            keyPair.getPrivateKey().toString(16);
            Credentials credentials = Credentials.create("0x"+keyPair.getPrivateKey().toString(16));
            accountList.add(new Account(credentials,new BigInteger("1")));

        }
        return accountList;
    }

    private static List<Account> getAccountList(String rawPrivateKeys) {
        List<Account> accountList = new ArrayList<>();
        String[] privateKeys = rawPrivateKeys.split("\n");

        List<Credentials> credentials = getCredentialsList(privateKeys);
        for (Credentials credentials1 : credentials) {
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

            int step = blockHeight.divide(new BigInteger(number.toString())).intValue();
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
