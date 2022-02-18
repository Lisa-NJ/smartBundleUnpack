package bundleUnpack;

import lombok.Data;
import lombok.Setter;

import java.util.HashMap;
import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 包裹配置文件格式如下 ----------------------
 * IMG   5 @ $450 10 @ $800
 * FLAC  3 @ $427.50 6 @ $810 9 @ $1147.50
 * VID   3 @ $570 5 @ $900 9 @ $1530
 *-----------------------------------------*/

 //指定文件中 读出的 所有类型的包裹
@Data
public class BundleS {  
    
    @Setter private String fileName;   
    private HashMap<String, Bundle> bundleCalMap;
    private Logger logger;
    
    public BundleS(String fN){
        
        logger = Logger.getLogger("LoggerLog");

        logger.setLevel(Level.INFO);

        try {
            //add fileHandler
            FileHandler fileHandler = new FileHandler("log/logOrder.log");
            fileHandler.setLevel(Level.INFO); 
            fileHandler.setFormatter(new MyFormat());

            //add fileHandler to logger
            logger.addHandler(fileHandler);
            
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
 
        setFileName(fN);     
        initBundles();
    }

    //main 函数 来调用，传入文件全路径名，返回有效 记录 条数
    public int initBundles()
    {
        logger.info("--- initBundles 从文件 setBundles.log 读取：产品类型 及 Bundle 规格价格");
     
        bundleCalMap = new HashMap<String, Bundle>();

        int recordN = 0;
        
        try {
            File f = new File(fileName);

            System.out.println(f.getAbsolutePath());
            
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String str;
            //逐行读取 包裹 信息
            while ((str = in.readLine()) != null) {
                
                System.out.println(str);

                //解析 当前行 字符串 为：类型 + <包裹大小, 价格> 的形式
                Bundle bd = extractOneRecord(str);
                if(bd != null)
                {  
                    logger.info("\t类型：" + bd.getTypeB());
                    bundleCalMap.put(bd.getTypeB(), bd);
                    for(int i=0; i<bd.getBundles().size(); i++)
                    {
                        logger.info("\t" + bd.getBundles().get(i).num + " @ " + bd.getBundles().get(i).price);
                    }
                    
                }
                recordN += 1;
            }
            in.close();
           
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recordN;
    }

    //false：异常，输入错误类型 或者 ...
    public DivideBundle placeOrder(String typ, int tgtN)
    {
        logger.info("---- 下单：类型 " + typ + " 数量 " + tgtN + " ----");

        if(tgtN < 1)
        {
            //该分支 没有计算
            logger.warning("\t无效订单 tgtN= " + tgtN + " 不计算，返回 null");
            return null;
        }

        //如果 typ 有效，调用对应的 Bundle 对象计算；
        if(bundleCalMap.containsKey(typ))
        {
            Bundle bundleCal = bundleCalMap.get(typ);
            DivideBundle ddB = bundleCal.getTotalCost(tgtN);

            logger.info("---- 花费：" + ddB.getTotalPrice() + " 分解如下：");   

            int[] divArray = ddB.getDivArray();
            for(int i=0; i<divArray.length; i++)
            {
                if(divArray[i]!=0)
                    logger.info("\t\t" + divArray[i] + " * " + bundleCal.getBundles().get(i).num + " ~ " +  bundleCal.getBundles().get(i).price);
            }   
            
            return ddB;
        } 
        //该分支 没有计算
        logger.warning("\t" + typ + " 类型不存在，placeOrder 没有计算");

        return null;
    }

    //输入：符合格式的字符串 IMG   5 @ $450 10 @ $800
    //输出：Bundle 对象，bd.type="IMG", bd.bundles={{5,450}, {10, 800}}
    private Bundle extractOneRecord(String origin)
    {   
        String[] parts = origin.split(" "); 
        String type = parts[0];
        int rN = (parts.length - 1) / 3;
        NumPrice[] npS = new NumPrice[rN];

        if(parts.length>=4)
        {
            int j = 0;

            for(int i=1; i<parts.length; i++)
            {
                if(parts[i].equals("@"))
                {
                    //parts[i-1] 表 bundle 规格
                    //parts[i+1] 表 bundle 价格
                    int n = Integer.parseInt(parts[i-1]);
                    String str = parts[i+1];
                    double p = Double.parseDouble(str.substring(1));
                    System.out.println("\tn= " + n + " p= " + p);
                    NumPrice item = new NumPrice(n, p);
                    npS[j] = item;
                    j++;

                }
            }   
            Bundle bd = new Bundle(type, npS);   
            
            return bd;
        }        
        return null;
    }
}
