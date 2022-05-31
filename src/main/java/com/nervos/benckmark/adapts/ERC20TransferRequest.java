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
        arguments.addArgument(Constant.PRIVATE_KEYS, "0x723982bb19b3d9d36990fef21dbe88281bba7d67eb4ee85760d9566bcf9423d4\n" +
                "0x5aa9c3c53a68651524730ca3843314edeeebb9ef3ed55d7f08263d006a407ad0");
        arguments.addArgument(Constant.ERC20_Address, "");
        return arguments;
    }

    @Override
    public void setupOtherData(JavaSamplerContext context) {
        System.out.println("------setupOtherData------");
        String privates = context.getParameter(Constant.PRIVATE_KEYS);
        String contractAddress = context.getParameter(Constant.ERC20_Address);
        this.chainId = SingletonService.getChainId(this.web3j);
        this.accountList = SingletonService.getSingletonAccountList(privates);
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
        arguments.addArgument(Constant.PRIVATE_KEYS,
                "0x5ab921c297688f88cf0a1abe5bd1b1c0af03d787b0f2c5b1098bf7d8f6f8e723\n" +
                        "0x9e8381ab089c85c0ed2f1aabc95feee00c49143710feb979c9c3d3c9aec1d15a\n" +
                        "0xfc059b343294c7971c22f767813afdedf4c523bb4c1a133a276bc2ac6b428a06\n" +
                        "0x1f29480a4a6dd1cda09faabe797613afeec115d8772d5ce8281715c724505e6e\n" +
                        "0xdc1206909eacee110de3a175fb9ec8def7dc39fa344048ddbc86c9d66e89c005\n" +
                        "0x74ffc64c6775b2a21925ce8f45da6e2c9a5623644db9e713644ec478bf58b3da\n" +
                        "0x3fbb47acea9a1755d580a873a7ee9d34dc7d174f087146d0fc071610757f6fbc\n" +
                        "0x654b92f41b22d5a09eb8f3a45e22123836fa2cc351bdd00dbb3d70c7c70738ee\n" +
                        "0x5e7e747a22fe7c7a80a7e0cfb4d419a7e21a42d94ce32d50e1ed7991628ed9c3\n" +
                        "0xe4f90b5257574573981427b93921b48eebe74b26138e006ffd1df2930bc27fc5\n" +
                        "0xffe4ac6a7dd7e0e62457203df20bca58bcd0d2a07688e5b8d23b1106b0560e69\n" +
                        "0x2eca5cff5f99600ff9f5aca9e60a86354857ba1a5f017216a0b47ed9d3e8b7fa\n" +
                        "0x5610d6c3454250e405576b724e5e6d3659274976bfc15a2e7ff63e8593fe6b8d\n" +
                        "0x5370b124701b275d4ccd0b0c9f5631eee233993d3289eefb994d30d8cc9bca25\n" +
                        "0x2278cba6b4b4d9d1b2956ee04c7f7627f73d849f9a9d21772d1ec49d8b00a58f\n" +
                        "0x4fd3182793e2cf880d3f639f4037a96f678abcda8ba5ac9dda55390d7f2a305c\n" +
                        "0x16bd82b5a1637b7f81b2dc95dd43a68f4b45dcd419090efaea81589eaa11a445\n" +
                        "0xa1184e72bf986ef003d9bb15afc838303f14a3a54e996ba7485acc5841904d6e\n" +
                        "0x52849b0d663400616bb35a1bd7f01c737ed0a04b9337e64160ace393e261073f\n" +
                        "0x7c929c37d09d585bfeb7cba5ed525f9862e24e5d08a0d42900101723c760b160\n" +
                        "0xa552263daf33b3965afb72778228b8adfc102a256a905f6efd16f4ed36740053\n" +
                        "0x2f98527e71774930dec4304ac11c13c454b80e6e6b1e1828ed66ccf799368693\n" +
                        "0x4f1d774ee6dc6100f79c09de075e9703c887e5cb81760d4dbdcef3d22f4b090e\n" +
                        "0xa0d7cc557478d0a03bcaae6c36b0f7019edd78de744db60969f7efe534e85101\n" +
                        "0x9c597a3df4bcbf4dbcc88bb8735e334b640647ec298d0a5cf19c428fd3fb433f\n" +
                        "0x4f28fc65589f870398c890203f3bf7203bede3c9d38984e8b278c53501badc47\n" +
                        "0x3e7724406728f081ff73d9e4b436c66d9c4d105e49f17945b598f322559ea559\n" +
                        "0x34bdf21d9d11fbe6c648e3d52a4e82a9efc4087f4d6c9a3f13d24bc4fa2c25b1\n" +
                        "0x56f1e47afea9b567b5fd5e44c0a7f86358e72f82fd021bff3fd462bc1f2c6f56\n" +
                        "0x109e2a8c0635bc737edf19746bf21a2285871a301678d0aaa361dc149f7ebdf6\n" +
                        "0x28b3a82384670bdc00732d1049b0883acd04537761ac6255a19f758fa441b7c5\n" +
                        "0x51c5414112bfba62f5aa25c7ba536e76aedc3c3688da9664ec1291ac24e069b7\n" +
                        "0xee5795e64a101c542fd46328fc0f8a358008fd7c8a60a9837bd95101f6a8f416\n" +
                        "0xf0023f7c065ce3cde7d57b1e1c87cea7185478c71e2b1719ea5dfd63173a3218\n" +
                        "0xbd3de4eef5460fe9ae8ac2e6843fc1e7bd06289787bdca3b1566a334001f4802\n" +
                        "0xab2fa1e2012e18f018450d59faff04fca37c6c6afda0453863e00cc7ddc5bb7d\n" +
                        "0x5b43aa022124916ebd76262d7df0d818770fe0b0e27554937f147f495555d01e\n" +
                        "0x2cfb0c4c3d62ca6dc6c6e40f7ce63e6a9baf763a2a2c0c86a7ae73ff31092eec\n" +
                        "0x3567d7ef017b2f8bde2134d1c96c91889c6f5ebaee8dd3082330746991d0c9ea\n" +
                        "0x4bbdcb4ce7a8c8af7fab77fea99b7317e0360554c445a919de2515f56228abe6\n" +
                        "0x4d5170e80539b24f4a04a597239734eb90b151d3758828036f01b61364428d0b\n" +
                        "0xbf4e54e9618562f2e6cdc4816b536feae3369f64df29ae1988c8ab41f41443e1\n" +
                        "0xb07ca12dbdc47ce2a8b18a5ceecb28677aeacc5953889344a567341dd072ce7e\n" +
                        "0x9015d323c83f6527ad6954ca9afc4b610a1d06550b6370b4a9294c93532e7d55\n" +
                        "0xff0764f27f9887597cc9e848663c63b3d4282c357907b6595e3b3edfa0e48b0b\n" +
                        "0xe99b37fd711b335d475e0cef738f5eb5548c8e2808fb66c7df0e63a7d30aeed6\n" +
                        "0x0c61baf2222305d5f7e1acac6bd36679197e19b559d36b03545f536c136740d3\n" +
                        "0xc6e8495af6e0830bfb418fd1e61141c66cfd41d4de10647b495c1e4648bca6f9\n" +
                        "0xf8b34d27e4764cbec0f385fabff1629016dfb26027995f9518441220ae1bb63d\n" +
                        "0x6d19110f66be9ef216809bffd68607cf3d3427b893b329ff1a9629f722dad0d7\n" +
                        "0x9c638f611b844c16c09ae7eeb1db26bd276f50a48df1a197357697ce48b1136c\n" +
                        "0x0131925c68d8ec88aea7d460942a371b00d3ce74ef4d004c8a459c5cf4bebbbb\n" +
                        "0x67efb7569fce327000ef4cc0688bee2fd3c62a1faad901c785f11c24c79337b9\n" +
                        "0xb583a59aca1cb5379456a34ac7f3a6aa9b805622fc87c9e9268235492ce06ebc\n" +
                        "0x2c2f00b25d7617b78ad68ac2225fafc7dbc6fe387c0d16fcf468920e52602540\n" +
                        "0xe30e8bdeeb42ba02494a46a1e4c35301f14d0a2d4bed1c8d5c34ca2b69963506\n" +
                        "0x023450d89a8dde987a75151a99bec1670da173aabd5b7045e4a8f0bf9748b875\n" +
                        "0x1d65b2d2417418828d0c0e3855b9b40641668bb2a6c5f7058a8acf69b996ac5f\n" +
                        "0x741af102894976a10456910ae333e2d8d17271d8b2fb76cd672773f95a61af3e\n" +
                        "0x142da55a1a5afebf68daa101860eee04a4080be14569956d2058d86b609538ea\n" +
                        "0x41f9c72f398718a086fea2db65b0cbdb6c22caa7d1f0dd9cfaf898cf4377e460\n" +
                        "0xff29f158476391b86bec4f8907200f1c5b582ef79507b59e4350327dec45737b\n" +
                        "0xdcf309252e010a6b91d344a67b84df792b55cc25b99d266d20900dc5e3c00f6c\n" +
                        "0x10dab01bc395239110e0143726bfd40f80dbf209e13482c65094737fae6c9eb0\n" +
                        "0x8f14ebaa63928ff9fca21efcba1549565698058c4876e668ce33f7f0c73c54ce\n" +
                        "0xa11d8cc026038850b8b0d31fcecdc2b3e519370bd05e513f57e0ac108afef4da\n" +
                        "0xff60ea4f06a58f6d97c3315ce78840799b5e63ef30a33ae60434ec78d0e0a421\n" +
                        "0x5fd10e7a4c5af329dc818bf613a557b74061e4cbeb26dda916c0b2ddba6f9c09\n" +
                        "0x6687043397782f76f9f799022a42a1576278ef93192f91cb6e3c22f76f64bfd1\n" +
                        "0x572e6dfb64e8b07666a91a5c445aadf2a509df118024f2a4ee1fc711e27995de\n" +
                        "0x62dab9e08e7b06f358f42cca56d49d9972d38039c1bd8138d1920f0d6c705ef0\n" +
                        "0xb43d2d664f8fe2ea315a0eb553382a92d50f1effb2d458856977191e4d2432a0\n" +
                        "0x815665113b8e7ea7efa0034c23cfd4704bc0f8326e068736dea0685ace44f797\n" +
                        "0x41876daae33c73a2155e022590aecf25819b36f1f7ca11c8a9d148b51117f5c3\n" +
                        "0x682d37cdb444a4d19abefd071e28c5758e9a4abe2a6daf0989577961decbd58d\n" +
                        "0x9a0d8063df6a8cad4b7bb985022c56b2870251fcaa362bc375309ff5dc3fa968\n" +
                        "0xcb21aef86b70e033a9795c937d80af070e87fdf37d67cce3f4607a0e95a92a06\n" +
                        "0xdd6a3e5093ac26fa9102a7fa18a83404e75b7f664001197e32d5111a577019e5\n" +
                        "0x5043f087eac15ff86146d3c5f5bbd0cafab5232b2e0b8c52d6c972eb61039e82\n" +
                        "0x4ffc465a37c0bc798119aa9946e75ba871da197536125de99bb5de16129b2c62\n" +
                        "0x43d36b7b5ac95dd4533808a9605868e7c5f5ce3c65ea650b2a6fba12ff02cb5e\n" +
                        "0x0f6680529f34d69c7468ddfcefa2d1e853e42150d6947fcc2a228602143053fb\n" +
                        "0xcfddcc4ce7029018f2dae5eccbd28afcc103494f5941885c453f1f274c0cb120\n" +
                        "0x7b6dfb6df2d6c9452426af9f0ca6806d23c1a56eacb990b42496ab1eb58e7f58\n" +
                        "0x3d9ddf7d5b4e83e1d9039c8394feddd3cd8b3bde8d196b69494c58340cf89ccd\n" +
                        "0xa0379250ae7151a7ffd904a83482875a691c6ba105badacc99e5f72a334fa04b\n" +
                        "0xae74af9b2b2afaa7630b0df675900423ffc0b92cd37105ef903c9dcc19247803\n" +
                        "0x6758df2bccca7978f7470f40a0e7ceade32dc0e43f6bc9bfc696a039366c498e\n" +
                        "0x8ff3fa2bed8a6aa628b61e76ca05fc63c518febe251ce601888b515548449a9f");
        arguments.addArgument(Constant.ERC20_Address, "");
        System.out.println("!!!");
        JavaSamplerContext context = new JavaSamplerContext(arguments);
        ERC20TransferRequest sample = new ERC20TransferRequest();
        sample.setupTest(context);
        System.out.println("???");
        for (int i = 0; i < 10000; i++) {
            System.out.println("----");
            sample.runTest(context);
        }
        sample.teardownTest(context);
    }
}
