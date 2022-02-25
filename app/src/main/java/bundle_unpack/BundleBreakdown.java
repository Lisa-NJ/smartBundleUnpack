package bundle_unpack;

import lombok.Getter;
import lombok.Setter;

public class BundleBreakdown {
    @Getter @Setter private String type;
    @Getter @Setter private int orderN;
    @Getter @Setter private double totalPrice;
    @Getter @Setter private int totalNum;
    @Getter @Setter private int[] divArray;

    public BundleBreakdown(int length) {
        divArray = new int[length];
    }

    public int[] addDivArray(int[] inArray) {
        for (int i = 0; i < divArray.length; i++) {
            divArray[i] += inArray[i];
        }

        return divArray;
    }

    public void setArrayItem(int i, int v){
        divArray[i] = v;
    }

}