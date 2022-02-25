package bundle_unpack;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BundleBreakdown {
    private String type;
    private int orderN;
    private double totalPrice;
    private int totalNum;
    private int[] divArray;

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