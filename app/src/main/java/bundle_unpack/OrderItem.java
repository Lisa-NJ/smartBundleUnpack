package bundle_unpack;

import lombok.Getter;
import lombok.Setter;


public class OrderItem {
    @Getter @Setter private String type;
    @Getter @Setter private int num;
    OrderItem(String type1, int num1){
        type = type1;
        num = num1;
    }
}
