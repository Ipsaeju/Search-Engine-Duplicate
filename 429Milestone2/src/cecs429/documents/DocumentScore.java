package cecs429.documents;

public class DocumentScore implements Comparable<DocumentScore> {
    private double key;
    private int value;

    public DocumentScore(double key, int value) {
        this.key = key;
        this.value = value;
    }
    
    public double getKey() {
            return key;
    }

    public void setKey(double key) {
            this.key = key;
    }

    public int getValue() {
            return value;
    }

    public void setValue(int value) {
            this.value = value;
    }

    public int compareTo(DocumentScore other) {
            return Double.compare(this.key, other.getKey());
    }	
}