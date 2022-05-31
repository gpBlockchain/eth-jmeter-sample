//package com.nervos.benchmark.util;
//
//import com.nervos.benckmark.util.Web3Util;
//import org.junit.Assert;
//import org.testng.annotations.Test;
//import org.web3j.protocol.Web3j;
//
//import java.util.List;
//
//public class Web3UtilTest {
//
//
//    String url = "https://godwoken-betanet-v1.ckbapp.dev";
//    Web3j web3j = Web3Util.initWeb3j(url);
//
//    @Test
//    public void test_getLatestContractAddress() throws Exception {
//        List<String> datas = Web3Util.getLatestContractAddress(web3j, 10);
//        for (String data : datas) {
//            System.out.println(data);
//            Assert.assertEquals(data.length(),"0x2e8fd9dfe139a81fdf24f12246c36d0d5c93ff3e".length());
//        }
//        Assert.assertEquals(datas.size(),10);
//    }
//
//    @Test
//    public void test_getLatestSenderAddress() throws Exception{
//        List<String> datas = Web3Util.getLatestSenderAddress(web3j,10);
//        for (String data : datas) {
//            System.out.println(data);
//            Assert.assertEquals(data.length(),"0x2e8fd9dfe139a81fdf24f12246c36d0d5c93ff3e".length());
//        }
//        Assert.assertEquals(datas.size(),10);
//    }
//    @Test
//
//    public void test_getLatestTxHash() throws Exception{
//        List<String> datas = Web3Util.getLatestTxHash(web3j,10);
//        for (String data : datas) {
//            System.out.println(data);
//            Assert.assertEquals(data.length(),66);
//        }
//        Assert.assertEquals(datas.size(),10);
//    }
//}
