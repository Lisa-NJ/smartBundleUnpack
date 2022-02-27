package bundle_unpack;

import java.util.ArrayList;
import java.util.Map;

public class BundleProcess {
    public ArrayList<BundleBreakdown> placeOrder(Order order, Map<String, Bundle> bundleCalMap) {
        ArrayList<BundleBreakdown> breakdownList = new ArrayList<>();

        for (int i = 0; i < order.getItemList().size(); i++) {
            BundleBreakdown curBreakdown = processOrder(order.getItemList().get(i).getType(), order.getItemList().get(i).getNum(), bundleCalMap);
            breakdownList.add(curBreakdown);
        }

        return breakdownList;
    }

    private BundleBreakdown processOrder(String type, int targetNum, Map<String, Bundle> bundleCalMap) {
        if (targetNum < 1) {
            return null;
        }

        if (bundleCalMap.containsKey(type)) {
            Bundle bundleCal = bundleCalMap.get(type);
            BundleBreakdown ddB = bundleCal.calBreakdown(targetNum);

            return ddB;
        }

        return null;
    }

}
