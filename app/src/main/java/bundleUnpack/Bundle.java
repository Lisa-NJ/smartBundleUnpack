package bundleUnpack;

import java.util.ArrayList;
import java.util.HashMap;

//一种类型的包裹

public class Bundle {

    public String typeB = "DIV";
    private ArrayList<NumPrice> bundles; 
    private boolean initFlg = false;
 
    public Bundle(String t, NumPrice[] nPriceS)
    {
        System.out.println("\tBundle 对象初始化 length=" + nPriceS.length);
       
        if(t.equals("") || nPriceS.length==0)
        {
            System.out.println("\tWarning：Bundle 初始化 参数无效！");
        }
        typeB = t;
        bundles = new ArrayList<NumPrice>();

        //添加键值对
        //{{3, 570},{5, 900},{9, 1530}};
        for(int i=0; i<nPriceS.length; i++)
        {
            bundles.add(new NumPrice(nPriceS[i].num, nPriceS[i].price));
        }
        //排序：按照从小到大的分包顺序
        //bundles.sort();
        int num = initBase();
        if(num>0) initFlg = true;
        else{
            //初始化 失败
        }
    }

    //订单数 - 最低金额 基础数据
    private HashMap<Integer, Double> baseMinCostMap;
    
    //根据输入 bundles 的值，初始化 基本的 <订单数-最低价> 数据
    //执行完成后：baseMinCostMap 看起来变成{{1,...}, {2, ...}, .... {最大包裹规格, ...}} 的样子
    private int initBase()
    {
        //baseMinCostMap 重新 付值
        baseMinCostMap = new HashMap<Integer, Double>();
     
        int l = bundles.size();

        assert(l>=1);
        
        //第一种情况：订单数 == 某个包裹的规格，最低金额 = 该包裹定价
        for(int i=0; i<l; i++)
        {
            baseMinCostMap.put(bundles.get(i).num, bundles.get(i).price);
        }

        //第二种情况：订单数 < 最小包裹的规格，最低金额 = 最小包裹定价
        int minSize = bundles.get(0).num;
        for(int i=1; i<minSize; i++)
        {
            baseMinCostMap.put(i, bundles.get(0).price);
        }

        //第三种情况：订单数 != 某个包裹的规格 && 订单数 > MinS && 订单数 < MaxS 
        int maxSize = bundles.get(l-1).num;
        if(l>=2)
        {
            int curN = minSize+1;
            int curStandardIndex = 1;
            int preIndex = 0;
            int nextSize = bundles.get(1).num;
            while(curN<maxSize)
            {
                if(curN != nextSize)
                {
                    double minTotal = minCostBtwIndex(curN, preIndex);
                    baseMinCostMap.put(curN, minTotal);
                    if(curN > nextSize)
                    {
                        curStandardIndex++;
                        nextSize = bundles.get(curStandardIndex).num;
                    }
                }  
                curN++;
            }
        }
        //如果 l==1，不需要处理

        assert(baseMinCostMap.size()==maxSize);

        initFlg = true;
        return baseMinCostMap.size();
    }
    
    //第三种情况：订单数 != 某个包裹的规格 && 订单数 > MinS && 订单数 < MaxS 
    private double minCostBtwIndex(int tgtN, int preIndex)
    {
        //例子：包裹规格 3 - 5 - 9 时，输入是 4 或者 6 的情况
        double option1 = bundles.get(preIndex+1).price;
        double option2 = minCostOverIndex(tgtN, preIndex);
        return option1 < option2 ? option1 : option2;
    }

    //第四种情况：订单数 > MaxS 
    private double minCostOverIndex(int tgtN, int preIndex)
    {
        //例子：包裹规格 3 - 5 - 9 时，输入是 10、13 或者 18 的情况

        //情况1：preIndex > 0，前面至少还有两种规格的包
        //情况2：preIndex == 0，包裹只剩一种规格了

        int preSize = bundles.get(preIndex).num;
        double prePrice = bundles.get(preIndex).price;
        
        if(preIndex > 0)
        {
            double option1 = prePrice;
            if(tgtN-preSize > preSize)
            {
                option1 += minCostOverIndex(tgtN-preSize, preIndex);
            }
            else
            {
                option1 += baseMinCostMap.get(tgtN-preSize);
            }
    
            int preSize2 = bundles.get(preIndex-1).num;            
            double prePrice2 = bundles.get(preIndex-1).price;
            double option2 = prePrice2;
            if(tgtN-preSize2 > preSize2)
            {
                option2 += minCostOverIndex(tgtN-preSize2, preIndex-1);
            }
            else
            {
                option2 += baseMinCostMap.get(tgtN-preSize2);
            }

            return option1 <= option2 ? option1 : option2;
        }

        //preIndex==0 的情形：只有一种规格的包裹       
        int nNeeded = tgtN / preSize;
        int leftN = tgtN % preSize;
        nNeeded += leftN>0 ? 1 : 0;

        return prePrice * nNeeded;
    }

    public double getTotalCost(int targetN)
    {
        if(!initFlg)
        {
            initBase();
        }

        int maxSize = bundles.get(bundles.size()-1).num;
        if(targetN<=maxSize)
        {
            return baseMinCostMap.get(targetN);
        }

        return minCostOverIndex(targetN, bundles.size()-1);
    }

}