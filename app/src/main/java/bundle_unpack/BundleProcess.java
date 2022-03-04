package bundle_unpack;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BundleProcess {
    public List<BundleBreakdown> placeOrder(Order order, Map<String, Bundle> bundleCalMap) {
        return order.getItemList().stream()
                .map(orderItem->processOrder(orderItem.getNum(), bundleCalMap.get(orderItem.getType())))
                .collect(Collectors.toList());
    }

    private BundleBreakdown processOrder(int targetNum, Bundle bundle) {
        if (targetNum < 1 || bundle==null) {
            return null;
        }

        return bundle.calBreakdown(targetNum);
    }

}
