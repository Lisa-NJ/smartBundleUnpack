package bundle_unpack;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderResult {
    private List<BundleBreakdown> list;
}
