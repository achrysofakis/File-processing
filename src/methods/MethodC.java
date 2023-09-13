package methods;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;

import pck.DataClass;
import pck.DataPage;

public class MethodC {
	public static int binarySearch(DataPage[] pdata, DataClass[] data, int Lost_Key, int FIELD_SIZE, int DataPageSize) throws IOException {
		//All of our instances div by (DataPageSize/(8) how many records we can write in a page
		double number=(double) Array.getLength(data)/(DataPageSize/(8));
		int pages= (int)Math.floor(number);
		int remaining = Array.getLength(pdata)%(Math.round(DataPageSize/(8)));//we need this to write the remaining intances in our disk
		if(remaining!=0)pages=pages+1;
		int CurrentKey=0;
		int CurrentPage=0;
	    int left = 0;
	    int right = pages;
	    int middle=1;
	    int access = 0;
	    boolean found = false;
	    int myPage = 0;
	    // ================= Get access to file =======================================
	 	@SuppressWarnings("resource")
		RandomAccessFile MyFile = new RandomAccessFile ("TheIndexSortedFile", "r");
	 	
	    while (left <= right && found==false) {
	        middle = (left + right) / 2;
	        if(middle==0)MyFile.seek((middle) * DataPageSize); // Seek to the middle page;
	        else MyFile.seek((middle-1) * DataPageSize); // Seek to the middle page;
	        access=access+1;
        	byte[] ReadDataPage = new byte[DataPageSize];
            MyFile.readFully(ReadDataPage); // Read the page into the byte array
            
            // Create a new ByteArrayInputStream for the page data
            ByteArrayInputStream bis = new ByteArrayInputStream(ReadDataPage);
            DataInputStream din = new DataInputStream(bis);
            
	        for(int i=0; i<(Math.round(DataPageSize/(8))); i++){//Find the amount of records in a page and read them one by one
	        	
	            CurrentKey=din.readInt();
	        	CurrentPage=din.readInt();
		        pdata[i] =new DataPage(CurrentKey,CurrentPage);        
		        //This happens if we find the key
		        if(pdata[i].getKey()==Lost_Key){
		        	myPage=pdata[i].getPageNumber();
		        	found=true;
		        	break;
		        }	
	        }
	        	if(found){
	        		break;
	        	}
		        else if (CurrentKey < Lost_Key) {
		            left = middle + 1;
		        }
		        else{
		            right = middle - 1;
		        }
	        	
	        }
	    
	        
	    
	    if (found) {
	    	access=access+1;
	        // Get access to the main file
	        @SuppressWarnings("resource")
			RandomAccessFile MainFile = new RandomAccessFile("TheFile", "r");
	        byte[] readDataPage = new byte[DataPageSize];
	        
	        // Seek to the start of the page and read the page into the byte array
	        MainFile.seek((myPage-1) * DataPageSize);
	        MainFile.readFully(readDataPage);
	        
	        // Create a new ByteArrayInputStream for the page data
	        ByteArrayInputStream bi = new ByteArrayInputStream(readDataPage);
	        DataInputStream di = new DataInputStream(bi);
	        
	        for (int i = 0; i < Math.round(DataPageSize / (FIELD_SIZE + 4)); i++) {
	            int currentKey = di.readInt();
	            byte[] bb = new byte[FIELD_SIZE];
	            di.read(bb);
	            String ss = new String(bb, java.nio.charset.StandardCharsets.US_ASCII);
	            data[i] = new DataClass(currentKey, ss);
	            if (data[i].getKey() == Lost_Key) {
	               // System.out.println("Key " + data[i].getKey() + " found in main file in page " + myPage + " with this string: " + data[i].getData() + " and this amount of accesses in MethodC: " + access);
	                break;
	            }
	        }
	        
	        // Close the current ByteArrayInputStream and DataInputStream
	        di.close();
	        bi.close();
	    }
	    else {
	        //System.out.println("The key does not exist in the sorted index file with accesses in MethodC:"+access);
	    }
	    return access;
	}

}
