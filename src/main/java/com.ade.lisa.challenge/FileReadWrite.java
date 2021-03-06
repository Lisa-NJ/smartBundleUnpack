package com.ade.lisa.challenge;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ade.lisa.challenge.model.OrderItem;
import com.ade.lisa.challenge.model.Order;
import com.ade.lisa.challenge.model.NumPrice;
import com.ade.lisa.challenge.model.BundleBreakdown;
import com.ade.lisa.challenge.model.OrderResult;

public class FileReadWrite {

    //get order info from file orderInfo.cfg
    public Order readOrder() {
        List<OrderItem> itemList = new ArrayList<>();

        try {
            String fileName = "orderInfo.txt";

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

        return new Order(itemList);
    }


    OrderItem parseOneOrder(String strItem) {
        String[] parts = strItem.split(" ");
        return new OrderItem(parts[0], Integer.parseInt(parts[1]));
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

            return new Bundle(type, npS);
        }
        return null;
    }

    public Map<String, Bundle> readBundleFormat() {
        Map<String, Bundle> bundleCalMap = new HashMap<>();
        try {
            System.out.println(getClass().getClassLoader().getResource("bundleFormat.txt"));

            BufferedReader in = new BufferedReader(new FileReader("bundleFormat.txt"));
            String str;

            while ((str = in.readLine()) != null) {
                // parse current line into ：type + <bundleSize, price> 的形式
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

    public void printOrderResult(Order order, OrderResult outputBD) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("log/breakdownPlan.log"));

            out.write("\n\t--- Order Info ----");
            for (int i = 0; i < order.getItemList().size(); i++) {
                OrderItem item = order.getItemList().get(i);
                out.write("\n\t" + item.getType() + " " + item.getNum());
            }

            out.write("\n\n\t---- Bundle breakdown Info ----");
            for (int i = 0; i < outputBD.getList().size(); i++) {
                BundleBreakdown tmpBdBreakdown = outputBD.getList().get(i);
                out.write("\n\t" + tmpBdBreakdown.getOrderType() + " " + tmpBdBreakdown.getOrderNumber() + " ~ " + tmpBdBreakdown.getTotalNumber() + " total cost = " + tmpBdBreakdown.getTotalPrice() + " :\n");
                for (int j = 0; j < tmpBdBreakdown.getSolution().length; j++) {
                    out.write("\t" + tmpBdBreakdown.getSolution()[j]);
                }
            }

            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
