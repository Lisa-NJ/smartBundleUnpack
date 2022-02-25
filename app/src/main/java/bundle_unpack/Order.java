package bundle_unpack;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
    private List<OrderItem> itemList ;
}
