package bundleUnpack;

import lombok.Data;
import lombok.Setter;
import lombok.Getter;

@Data
public class DivideBundle{
    @Setter @Getter private int orderN;
    @Setter @Getter private double totalPrice;
    @Getter public int[] divArray;

    public DivideBundle(int length)
    {
        divArray = new int[length];
    }

    public int[] addDivArray(int[] inArray)
    {
        for(int i=0; i<divArray.length; i++)
        {
            divArray[i] += inArray[i];
        }

        return divArray;
    }

    
}