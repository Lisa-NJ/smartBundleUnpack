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
    private Logger logger;
    // orderNum - cost & breakdown base infomation
    private Map<Integer, BundleBreakdown> baseMinBdNumMap;

    private void initLogger(){
        logger = Logger.getLogger("LoggerLog");
        logger.setLevel(Level.INFO);
        try {
            //add fileHandler
            FileHandler fileHandler = new FileHandler("log/process.log");
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new DateTimeFormat());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public Bundle(String t, NumPrice[] nPriceS) {
        setTypeB(t);
        setNofSizes(nPriceS.length);
        initLogger();

        bundles = new ArrayList<NumPrice>();

        // add given bundle item
        // {{3, 570},{5, 900},{9, 1530}};
        for (int i = 0; i < nPriceS.length; i++) {
            bundles.add(new NumPrice(nPriceS[i].getNum(), nPriceS[i].getPrice()));
        }
    }

    private BundleBreakdown minNumBtwIndex(int targetNum, int preIndex) {
        logger.info("minNumBtwIndex targetNum = " + targetNum + " preIndex = " + preIndex);
        BundleBreakdown curBreakdown = new BundleBreakdown(getNofSizes());

        curBreakdown.setType(getTypeB());
        curBreakdown.setOrderN(targetNum);

        // An example: 3 - 5 - 9，targetNum = 4 or 6
        int option1 = bundles.get(preIndex + 1).getNum();
        BundleBreakdown breakdown2 = minNumOverIndex(targetNum, preIndex);

        if (option1 < breakdown2.getTotalNum()) {
            curBreakdown.setTotalNum(option1);
            curBreakdown.setArrayItem(preIndex + 1, 1);
        } else {
            curBreakdown.setTotalNum(breakdown2.getTotalNum());
            curBreakdown.setDivArray(breakdown2.getDivArray());
        }

        curBreakdown.calTotalPrice(bundles);
        return curBreakdown;
    }

    private BundleBreakdown minNumOverIndex(int targetNum, int preIndex) {
        logger.info("minNumOverIndex targetNum = " + targetNum + " preIndex = " + preIndex);
        BundleBreakdown curBreakdown = new BundleBreakdown(getNofSizes());

        curBreakdown.setType(getTypeB());
        curBreakdown.setOrderN(targetNum);

        int preSize = bundles.get(preIndex).getNum();
        
        if(preIndex > 0)
        {
            //---------------------------------------------
            //Option1: take one maxSize
            int option1 = preSize;
            BundleBreakdown curBreakdownBd1 = new BundleBreakdown(getNofSizes());
            curBreakdownBd1.setOrderN(targetNum);
            curBreakdownBd1.setArrayItem(preIndex, 1);
            
            //1-1：if targetNum-preSize > preSize, gap = minNumOverIndex
            //1-2：if <=, gap is in base
            BundleBreakdown gapBundle1 = targetNum-preSize > preSize ? minNumOverIndex(targetNum-preSize, preIndex) : baseMinBdNumMap.get(targetNum-preSize);
            option1 += gapBundle1.getTotalNum();
            curBreakdownBd1.setTotalNum(option1);
            
            curBreakdownBd1.addDivArray(gapBundle1.getDivArray());
            logger.info("option1 = " + curBreakdownBd1);

            //-----------------------------------------------
            //Option2: take one second max size
            int preSize2 = bundles.get(preIndex-1).getNum();            
            int preNum2 = bundles.get(preIndex-1).getNum();
            int option2 = preNum2;
            BundleBreakdown curBreakdownBd2 = new BundleBreakdown(getNofSizes());
            curBreakdownBd2.setOrderN(targetNum);
            curBreakdownBd2.setArrayItem(preIndex-1, 1);
            
            //2-1：if targetNum-preSize > preSize, similar to 1-1
            //2-2：if targetNum-preSize <= preSize, similar to 1-2
            BundleBreakdown gapBundle2 = targetNum-preSize2 > preSize2 ? minNumOverIndex(targetNum-preSize2, preIndex-1) : baseMinBdNumMap.get(targetNum-preSize2);
            option2 += gapBundle2.getTotalNum();
            curBreakdownBd2.setTotalNum(option2);
            //curBreakdownBd2 的 divArray += gapBundle2 对应的 分包 数据
            curBreakdownBd2.addDivArray(gapBundle2.getDivArray());
            logger.info("option2 = " + curBreakdownBd2);

            //--------------------------------------------------
            //if option1==option2, go for option1 -- option1 has used bigger bundles
            curBreakdown.setTotalNum(option1 <= option2 ? option1 : option2);
            curBreakdown.setDivArray(option1 <= option2 ? curBreakdownBd1.getDivArray() : curBreakdownBd2.getDivArray());
            logger.info("min = " + (option1 <= option2 ? option1 : option2));

            curBreakdown.calTotalPrice(bundles);

            return curBreakdown;
        }

        //preIndex==0, only one size of bundle available       
        int nNeeded = targetNum / preSize;
        int leftN = targetNum % preSize;
        nNeeded += leftN>0 ? 1 : 0;
        curBreakdown.setTotalNum(preSize * nNeeded);
        curBreakdown.setArrayItem(preIndex, nNeeded);

        curBreakdown.calTotalPrice(bundles);

        return curBreakdown;
    }

    public BundleBreakdown calBreakdown(int targetN) {
        logger.info("calBreakdown targetNum = " + targetN);
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
        logger.info("initBaseNum, type = " + getTypeB());
        baseMinBdNumMap = new HashMap<Integer, BundleBreakdown>();

        int l = getNofSizes();

        assert (l >= 1);

        // 1：if orderNum == bundles(i).num, curBuddleNum = 1
        for (int i = 0; i < l; i++) {
            BundleBreakdown curBundle = new BundleBreakdown(l);
            curBundle.setType(getTypeB());
            curBundle.setOrderN(bundles.get(i).getNum());
            curBundle.setTotalNum(bundles.get(i).getNum());
            curBundle.setArrayItem(i, 1);
            curBundle.calTotalPrice(bundles);
            baseMinBdNumMap.put(bundles.get(i).getNum(), curBundle);
        }

        // 2：if orderNum < bundles(0).num, curBuddleNum = 1
        int minSize = bundles.get(0).getNum();
        for (int curOrder = 1; curOrder < minSize; curOrder++) {
            BundleBreakdown curBundle = new BundleBreakdown(getNofSizes());
            curBundle.setType(getTypeB());
            curBundle.setOrderN(curOrder);
            curBundle.setTotalNum(bundles.get(0).getNum());
            curBundle.setArrayItem(0, 1);
            curBundle.calTotalPrice(bundles);
            baseMinBdNumMap.put(curOrder, curBundle);
        }

        // orderNum != bundles(n).num && orderNum > MinS && orderNum < MaxS
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

        logger.info(baseMinBdNumMap.toString());

        assert (baseMinBdNumMap.size() == maxSize);
        initFlg = true;
        return baseMinBdNumMap.size();
    }
}