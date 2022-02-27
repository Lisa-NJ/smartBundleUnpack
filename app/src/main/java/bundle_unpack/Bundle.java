package bundle_unpack;

import java.util.*;
import java.util.ArrayList;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Bundle {
    private String typeB = "DIV";
    private ArrayList<NumPrice> bundles;
    private boolean initFlg = false;

    private int nofSizes = 0;
    // OrderNumum - cost & breakdown base infomation
    private Map<Integer, BundleBreakdown> baseMinBdNumMap;

    public Bundle(String t, NumPrice[] nPriceS) {
        setTypeB(t);
        setNofSizes(nPriceS.length);
        bundles = new ArrayList<NumPrice>();

        // add given bundle item
        // {{3, 570},{5, 900},{9, 1530}};
        for (int i = 0; i < nPriceS.length; i++) {
            bundles.add(new NumPrice(nPriceS[i].getNum(), nPriceS[i].getPrice()));
        }
    }

    private BundleBreakdown minNumBtwIndex(int targetNum, int preIndex) {
        Log.logger.info("minNumBtwIndex targetNum = " + targetNum + " preIndex = " + preIndex);
        BundleBreakdown curBreakdown = new BundleBreakdown(getNofSizes());

        curBreakdown.setOrderType(getTypeB());
        curBreakdown.setOrderNumber(targetNum);

        // An example: 3 - 5 - 9，targetNum = 4 or 6
        int option1 = bundles.get(preIndex + 1).getNum();
        BundleBreakdown breakdown2 = minNumOverIndex(targetNum, preIndex);

        if (option1 < breakdown2.getTotalNumber()) {
            curBreakdown.setTotalNumber(option1);
            curBreakdown.setArrayItem(preIndex + 1, 1);
        } else {
            curBreakdown.setTotalNumber(breakdown2.getTotalNumber());
            curBreakdown.setSolution(breakdown2.getSolution());
        }

        curBreakdown.calTotalPrice(bundles);
        return curBreakdown;
    }

    private BundleBreakdown minNumOverIndex(int targetNum, int preIndex) {
        Log.logger.info("minNumOverIndex targetNum = " + targetNum + " preIndex = " + preIndex);
        BundleBreakdown curBreakdown = new BundleBreakdown(getNofSizes());

        curBreakdown.setOrderType(getTypeB());
        curBreakdown.setOrderNumber(targetNum);

        int preSize = bundles.get(preIndex).getNum();
        
        if(preIndex > 0)
        {
            //---------------------------------------------
            //Option1: take one maxSize
            int option1 = preSize;
            BundleBreakdown curBreakdownBd1 = new BundleBreakdown(getNofSizes());
            curBreakdownBd1.setOrderNumber(targetNum);
            curBreakdownBd1.setArrayItem(preIndex, 1);
            
            //1-1：if targetNum-preSize > preSize, gap = minNumOverIndex
            //1-2：if <=, gap is in base
            BundleBreakdown gapBundle1 = targetNum-preSize > preSize ? minNumOverIndex(targetNum-preSize, preIndex) : baseMinBdNumMap.get(targetNum-preSize);
            option1 += gapBundle1.getTotalNumber();
            curBreakdownBd1.setTotalNumber(option1);
            
            curBreakdownBd1.addSolution(gapBundle1.getSolution());
            Log.logger.info("option1 = " + curBreakdownBd1);

            //-----------------------------------------------
            //Option2: take one second max size
            int preSize2 = bundles.get(preIndex-1).getNum();            
            int preNum2 = bundles.get(preIndex-1).getNum();
            int option2 = preNum2;
            BundleBreakdown curBreakdownBd2 = new BundleBreakdown(getNofSizes());
            curBreakdownBd2.setOrderNumber(targetNum);
            curBreakdownBd2.setArrayItem(preIndex-1, 1);
            
            //2-1：if targetNum-preSize > preSize, similar to 1-1
            //2-2：if targetNum-preSize <= preSize, similar to 1-2
            BundleBreakdown gapBundle2 = targetNum-preSize2 > preSize2 ? minNumOverIndex(targetNum-preSize2, preIndex-1) : baseMinBdNumMap.get(targetNum-preSize2);
            option2 += gapBundle2.getTotalNumber();
            curBreakdownBd2.setTotalNumber(option2);
            //curBreakdownBd2 的 solution += gapBundle2 对应的 分包 数据
            curBreakdownBd2.addSolution(gapBundle2.getSolution());
            Log.logger.info("option2 = " + curBreakdownBd2);

            //--------------------------------------------------
            //if option1==option2, go for option1 -- option1 has used bigger bundles
            curBreakdown.setTotalNumber(option1 <= option2 ? option1 : option2);
            curBreakdown.setSolution(option1 <= option2 ? curBreakdownBd1.getSolution() : curBreakdownBd2.getSolution());
            Log.logger.info("min = " + (option1 <= option2 ? option1 : option2));

            curBreakdown.calTotalPrice(bundles);

            return curBreakdown;
        }

        //preIndex==0, only one size of bundle available       
        int nNeeded = targetNum / preSize;
        int leftN = targetNum % preSize;
        nNeeded += leftN>0 ? 1 : 0;
        curBreakdown.setTotalNumber(preSize * nNeeded);
        curBreakdown.setArrayItem(preIndex, nNeeded);

        curBreakdown.calTotalPrice(bundles);

        return curBreakdown;
    }

    public BundleBreakdown calBreakdown(int targetN) {
        Log.logger.info("calBreakdown targetNum = " + targetN);
        if (!initFlg) {
            initBaseNum();
        }

        int maxSize = bundles.get(bundles.size() - 1).getNum();
        if (targetN <= maxSize) {
            return baseMinBdNumMap.get(targetN);
        }

        return minNumOverIndex(targetN, bundles.size()-1);
    }

    private int initBaseNum() {
        Log.logger.info("initBaseNum, type = " + getTypeB());
        baseMinBdNumMap = new HashMap<Integer, BundleBreakdown>();

        int l = getNofSizes();

        assert (l >= 1);

        // 1：if OrderNumum == bundles(i).num, curBuddleNum = 1
        for (int i = 0; i < l; i++) {
            BundleBreakdown curBundle = new BundleBreakdown(l);
            curBundle.setOrderType(getTypeB());
            curBundle.setOrderNumber(bundles.get(i).getNum());
            curBundle.setTotalNumber(bundles.get(i).getNum());
            curBundle.setArrayItem(i, 1);
            curBundle.calTotalPrice(bundles);
            baseMinBdNumMap.put(bundles.get(i).getNum(), curBundle);
        }

        // 2：if OrderNumum < bundles(0).num, curBuddleNum = 1
        int minSize = bundles.get(0).getNum();
        for (int curOrder = 1; curOrder < minSize; curOrder++) {
            BundleBreakdown curBundle = new BundleBreakdown(getNofSizes());
            curBundle.setOrderType(getTypeB());
            curBundle.setOrderNumber(curOrder);
            curBundle.setTotalNumber(bundles.get(0).getNum());
            curBundle.setArrayItem(0, 1);
            curBundle.calTotalPrice(bundles);
            baseMinBdNumMap.put(curOrder, curBundle);
        }

        // OrderNumum != bundles(n).num && OrderNumum > MinS && OrderNumum < MaxS
        int maxSize = bundles.get(l - 1).getNum();
        if (l >= 2) {
            int curN = minSize + 1;
            int preIndex = 0;
            int nextSize = bundles.get(preIndex + 1).getNum();
            while (curN < maxSize) {
                if (curN != nextSize) {
                    if (curN > nextSize) {
                        preIndex++;
                        nextSize = bundles.get(preIndex + 1).getNum();
                    }
                    BundleBreakdown minBdTotal = minNumBtwIndex(curN, preIndex);
                    minBdTotal.calTotalPrice(bundles);
                    baseMinBdNumMap.put(curN, minBdTotal);
                }
                curN++;
            }
        }
        // if l==1，no action needed

        Log.logger.info(baseMinBdNumMap.toString());

        assert (baseMinBdNumMap.size() == maxSize);
        initFlg = true;
        return baseMinBdNumMap.size();
    }
}