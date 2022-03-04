package bundle_unpack;

import java.util.Map;
import java.util.stream.Collectors;

public class BundleProcess {
    public OrderResult placeOrder(Order order, Map<String, Bundle> bundleCalMap) {
        return new OrderResult(order.getItemList().stream()
                .map(orderItem->processOrder(orderItem.getNum(), bundleCalMap.get(orderItem.getType())))
                .collect(Collectors.toList()));
    }

    private BundleBreakdown processOrder(int targetNum, Bundle bundle) {
        if (targetNum < 1 || bundle==null) {
            return null;
        }

        return bundle.calBreakdown(targetNum);
    }

}
