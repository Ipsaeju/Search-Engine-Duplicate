package cecs429.index;

import java.util.List;

public interface WeightingStrategy {
    //Weighs every document in the corpus and stores it in a bin file
    public void createDocWeightBin();
    
    //Gets the wdt for a particular document
    public double getWdt(double tftd);
    
    //Gets the wdt for a particular document
    public double getWdt(double tftd, int docID);
    
    //Gets the wdt for a particular document
    public double getWqt(List<Posting> dft);
    
    //Gets the Ld for a particular document
    public double getLd(int docID);
}
