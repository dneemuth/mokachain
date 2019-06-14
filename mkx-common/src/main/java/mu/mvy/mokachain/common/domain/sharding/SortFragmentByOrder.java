package mu.mvy.mokachain.common.domain.sharding;

import java.util.Comparator;

public class SortFragmentByOrder implements Comparator<EncryptedFragment> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(EncryptedFragment a, EncryptedFragment b) 
    { 
        return a.getOrder() - b.getOrder(); 
    } 

}
