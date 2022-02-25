package bundle_unpack;
import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CfgReader {
    
    //get order info from file orderInfo.cfg
    public Order readOrder(){
        List<OrderItem> itemList = new ArrayList<OrderItem>();

        try {
            String fileName = "cfg/orderInfo.cfg";;

            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String str;
            // order item
            while ((str = in.readLine()) != null) {
                // parse current line to : type  num
                OrderItem item = parseOneOrder(str);
                if (item != null) {
                    itemList.add(item);   
                }
                
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Order order = new Order(itemList);

        return order;
    }


    OrderItem parseOneOrder(String strItem){
        
        String[] parts = strItem.split(" ");
        OrderItem item = new OrderItem(parts[0], Integer.parseInt(parts[1]));

        return item;
    }
        
    // IN：string like: IMG 5 @ $450 10 @ $800
    // OUT：Bundle object，bd.type="IMG", bd.bundles={{5,450}, {10, 800}}
    public Bundle parseOneBdFormat(String origin) {
        String[] parts = origin.split(" ");
        String type = parts[0];
        int rN = (parts.length - 1) / 3;
        NumPrice[] npS = new NumPrice[rN];

        if (parts.length >= 4) {
            int j = 0;

            for (int i = 1; i < parts.length; i++) {
                if (parts[i].equals("@")) {
                    // parts[i-1]  bundle Size
                    // parts[i+1]  bundle Price
                    int n = Integer.parseInt(parts[i - 1]);
                    String str = parts[i + 1];
                    double p = Double.parseDouble(str.substring(1));

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

    public Map<String, Bundle> readBundleFormat()
    {
        Map<String, Bundle> bundleCalMap = new HashMap<String, Bundle>();
        try {

            BufferedReader in = new BufferedReader(new FileReader("cfg/bundleFormat.cfg"));
            String str;
        
            while ((str = in.readLine()) != null) {

                System.out.println(str);

                // 解析 当前行 字符串 为：类型 + <包裹大小, 价格> 的形式
                Bundle bd = parseOneBdFormat(str);
                if (bd != null) {    
                    bundleCalMap.put(bd.getTypeB(), bd);
                }
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return bundleCalMap;
    }
}
