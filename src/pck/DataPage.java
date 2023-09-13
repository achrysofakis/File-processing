package pck;

public class DataPage implements Comparable<DataPage> {
    private int key;
    private int pageNumber;
    
    public DataPage(int key, int pageNumber) {
        this.key = key;
        this.pageNumber = pageNumber;
    }
    
    public int getKey() {
        return key;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int compareTo(DataPage other) {
        return Integer.compare(this.key, other.key);
    }
}


