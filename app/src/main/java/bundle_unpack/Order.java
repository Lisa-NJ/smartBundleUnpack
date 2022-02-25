package bundle_unpack;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Order {
    @Getter @Setter private List<OrderItem> itemList ;
}
