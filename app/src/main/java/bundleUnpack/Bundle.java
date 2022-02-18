package bundleUnpack;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Data;
import lombok.Setter;
import lombok.Getter;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

//一种类型的包裹
@Data
public class Bundle {

    @Setter @Getter String typeB = "DIV";
    private ArrayList<NumPrice> bundles; 
    private boolean initFlg = false;
    private Logger logger; 
    @Setter @Getter private int nofSizes = 0;

     //订单数 - 最低金额 & 分包 基础数据
     private HashMap<Integer, DivideBundle> baseMinCostMap;

    public Bundle(String t, NumPrice[] nPriceS)
    {
        setTypeB(t);
        setNofSizes(nPriceS.length);

        //初始化 Bundle 使用的 Logger
        initLogger();

        logger.info("------ Bundle 对象初始化 length=" + nPriceS.length + " ------");
       
        if(t.equals("") || nPriceS.length==0)
        {
            logger.warning("Bundle 初始化 参数无效！");
        }
        bundles = new ArrayList<NumPrice>();

        //添加键值对
        //{{3, 570},{5, 900},{9, 1530}};
        logger.info("\t一共有 " + nPriceS.length + " 种规格：");
        for(int i=0; i<nPriceS.length; i++)
        {
            bundles.add(new NumPrice(nPriceS[i].num, nPriceS[i].price));
            logger.info("\t " + nPriceS[i].num + " - " + nPriceS[i].price);
        }
        //排序：按照从小到大的分包顺序
        //bundles.sort();
        int num = initBase();
        if(num>0) initFlg = true;
        else{
            //初始化 失败
            logger.warning("\t初始化失败，initBase 返回 0");
        }
    }

    private boolean initLogger()
    {
        boolean initFlg = false;
        logger = Logger.getLogger("LoggerLog" + getTypeB());

        logger.setLevel(Level.INFO);

        try {
            //add fileHandler
            FileHandler fileHandler = new FileHandler("log/logBundle" + getTypeB() + ".log");
            fileHandler.setLevel(Level.INFO); 
            fileHandler.setFormatter(new MyFormat());

            //add fileHandler to logger
            logger.addHandler(fileHandler);
            
            initFlg = true;

        } catch (SecurityException | IOException e) {
            e.printStackTrace();

        }
        
        return initFlg;
    }

   
    
    //根据输入 bundles 的值，初始化 基本的 <订单数-最低价> 数据
    //执行完成后：baseMinCostMap 看起来变成{{1,...}, {2, ...}, .... {最大包裹规格, ...}} 的样子
    private int initBase()
    {
        logger.info("---- " + getTypeB() + " 类型的 initBase，初始化 num-cost Map ----");
            
        baseMinCostMap = new HashMap<Integer, DivideBundle>();
     
        int l = getNofSizes();

        assert(l>=1);
        
        //第一种情况：订单数 == 某个包裹的规格，最低金额 = 该包裹定价
        for(int i=0; i<l; i++)
        {
            DivideBundle curBundle = new DivideBundle(l);
            curBundle.setOrderN(bundles.get(i).num);
            curBundle.setTotalPrice(bundles.get(i).price);
            curBundle.divArray[i] = 1;
            baseMinCostMap.put(bundles.get(i).num, curBundle);

            logger.info("\t" + bundles.get(i).num + " = 给定规格，cost= " + bundles.get(i).price);
        }

        //第二种情况：订单数 < 最小包裹的规格，最低金额 = 最小包裹定价
        int minSize = bundles.get(0).num;
        for(int curOrder=1; curOrder<minSize; curOrder++)
        {
            DivideBundle curBundle = new DivideBundle(getNofSizes());
            curBundle.setOrderN(curOrder);
            curBundle.setTotalPrice(bundles.get(0).price);
            curBundle.divArray[0] = 1;

            baseMinCostMap.put(curOrder, curBundle);
            logger.info("\t" + curOrder + " < 最小规格 " + minSize + " cost= " + bundles.get(0).price);
        }

        //第三种情况：订单数 != 某个包裹的规格 && 订单数 > MinS && 订单数 < MaxS 
        logger.info("\t计算介于之间的其他值");
        int maxSize = bundles.get(l-1).num;
        if(l>=2)
        {
            int curN = minSize+1;
            int preIndex = 0;
            int nextSize = bundles.get(preIndex+1).num;
            while(curN<maxSize)
            {
                if(curN != nextSize)
                {
                    if(curN > nextSize)
                    {
                        preIndex++;
                        nextSize = bundles.get(preIndex+1).num;
                    }
                    DivideBundle minBdTotal = minCostBtwIndex(curN, preIndex);
                    baseMinCostMap.put(curN, minBdTotal);
                    logger.info("\t" + curN + " cost= " + minBdTotal.getTotalPrice());
                }  
                curN++;
            }
        }
        //如果 l==1，不需要处理

        assert(baseMinCostMap.size()==maxSize);

        initFlg = true;

        baseMinCostMap.forEach((key,value)-> logger.info("\t--> base key = " + key + " " + value));

        return baseMinCostMap.size();
    }
    
    //第三种情况：订单数 != 某个包裹的规格 && 订单数 > MinS && 订单数 < MaxS 
    private DivideBundle minCostBtwIndex(int tgtN, int preIndex)
    {
        logger.info("---- minCostBtwIndex 入参 tgtN = " + tgtN + " preIndex = " + preIndex + " ----");

        DivideBundle curDivide = new DivideBundle(getNofSizes());

        curDivide.setOrderN(tgtN);
   
        //例子：包裹规格 3 - 5 - 9 时，输入是 4 或者 6 的情况
        double option1 = bundles.get(preIndex+1).price;
        DivideBundle option2Divide = minCostOverIndex(tgtN, preIndex);

        if(option1 < option2Divide.getTotalPrice()+0.001)
        {
            curDivide.setTotalPrice(option1);
            curDivide.divArray[preIndex+1] = 1;
        }
        else
        {
            curDivide.setTotalPrice(option2Divide.getTotalPrice());
            curDivide.divArray = option2Divide.divArray;
        }

        return curDivide;
    }

    //第四种情况：订单数 > preIndex 所指的规格
    private DivideBundle minCostOverIndex(int tgtN, int preIndex)
    {
        //例子：包裹规格 3 - 5 - 9 时，输入是 10、13 或者 18 的情况

        //情况1：preIndex > 0，前面至少还有两种规格的包
        //情况2：preIndex == 0，包裹只剩一种规格了

        logger.info("---- minCostOverIndex 入参 tgtN = " + tgtN + " preIndex = " + preIndex + " ----");

        DivideBundle curDivide = new DivideBundle(getNofSizes());

        curDivide.setOrderN(tgtN);

        int preSize = bundles.get(preIndex).num;
        double prePrice = bundles.get(preIndex).price;
        
        if(preIndex > 0)
        {
            //---------------------------------------------
            //Option1:用掉 一个最大规格Size 的情况
            double option1 = prePrice;
            DivideBundle curDivideBd1 = new DivideBundle(getNofSizes());
            curDivideBd1.setOrderN(tgtN);
            curDivideBd1.divArray[preIndex] = 1;
            
            //1-1：剩下的数量 > 最大规格的Size，gap 需要再计算一次
            //1-2：剩下的数量 <= 最大规格的Size，gap 从 base 中取数据
            DivideBundle gapBundle1 = tgtN-preSize > preSize ? minCostOverIndex(tgtN-preSize, preIndex) : baseMinCostMap.get(tgtN-preSize);
            option1 += gapBundle1.getTotalPrice();
            curDivideBd1.setTotalPrice(option1);
            //curDivideBd1 的 divArray += gapBundle1 对应的 分包 数据
            curDivideBd1.addDivArray(gapBundle1.divArray);

            //-----------------------------------------------
            //Option2:用掉 一个次大规格Size 的情况
            int preSize2 = bundles.get(preIndex-1).num;            
            double prePrice2 = bundles.get(preIndex-1).price;
            double option2 = prePrice2;
            DivideBundle curDivideBd2 = new DivideBundle(getNofSizes());
            curDivideBd2.setOrderN(tgtN);
            curDivideBd2.divArray[preIndex-1] = 1;
            
            //2-1：剩下的数量 > 次大规格的Size，gap 需要再计算一次
            //2-2：剩下的数量 <= 次大规格的Size，gap 从 base 中取数据
            DivideBundle gapBundle2 = tgtN-preSize2 > preSize2 ? minCostOverIndex(tgtN-preSize2, preIndex-1) : baseMinCostMap.get(tgtN-preSize2);
            option2 += gapBundle2.getTotalPrice();
            curDivideBd2.setTotalPrice(option2);
            //curDivideBd2 的 divArray += gapBundle2 对应的 分包 数据
            curDivideBd2.addDivArray(gapBundle2.divArray);

            //--------------------------------------------------
            curDivide.setTotalPrice(option1 < option2+0.001 ? option1 : option2);
            curDivide.divArray = option1 < option2+0.001 ? curDivideBd1.divArray : curDivideBd2.divArray;

            return curDivide;
        }

        //preIndex==0 的情形：只有一种规格的包裹       
        int nNeeded = tgtN / preSize;
        int leftN = tgtN % preSize;
        nNeeded += leftN>0 ? 1 : 0;
        curDivide.setTotalPrice(prePrice * nNeeded);
        curDivide.divArray[preIndex] = nNeeded;

        return curDivide;
    }

    public DivideBundle getTotalCost(int targetN)
    {
        logger.info("---- getTotalCost 收到订单：" + getTypeB() + " - " + targetN + " 件");
    
        if(!initFlg)
        {
            initBase();
        }

        int maxSize = bundles.get(bundles.size()-1).num;
        if(targetN<=maxSize)
        {          
            logger.info("\t小于 最大规格  " + maxSize + " 直接从 base 中获取结果并返回");
            return baseMinCostMap.get(targetN);
        }

        return minCostOverIndex(targetN, bundles.size()-1);
    }

}