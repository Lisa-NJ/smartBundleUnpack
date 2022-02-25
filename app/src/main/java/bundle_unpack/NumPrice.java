package bundle_unpack;

import lombok.Getter;
import lombok.Setter;


public class NumPrice {
    @Getter @Setter private int num;
    @Getter @Setter private double price;

    NumPrice(int n, double p){
        num = n;
        price = p;
    }
}
