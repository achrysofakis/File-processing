package Files;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import pck.DataPage;

public class MakeIndexFile {
	public static void processArray(DataPage[] pdata,int DataPageSize) throws IOException {
	  //  byte[] zeroBytes = new byte[FIELD_SIZE];
	    byte[] zeroPageBytes = new byte[DataPageSize];
		@SuppressWarnings("unused")
		int serialized_data_size = 0;
		//All of our instances div by (DataPageSize/(FIELD_SIZE+4) how many records we can write in a page
		double number=(double) Array.getLength(pdata)/(DataPageSize/(8));
		int pages= (int)Math.floor(number);
		int i,j;
		int remaining = Array.getLength(pdata)%(Math.round(DataPageSize/(8)));//we need this to write the remaining intances in our disk
		int sum=0;//We use this to go through our data array
		int pos=0;//We use this to seek in the write position in our disk		
		java.nio.ByteBuffer WriteDataPageBuffer = java.nio.ByteBuffer.allocate(DataPageSize); // Create a buffer up to DataPageSize bytes
		
    	
		for(j=0; j<(pages); j++){
			
			for(i=0; i<(Math.round(DataPageSize/(8))); i++){//We full our buffer and need to clear it after this loop(1 page loop)
				
		    	
		    	WriteDataPageBuffer.putInt(pdata[sum].getKey());		// Put  our key(4 bytes) into DataPage
		    	serialized_data_size += Integer.SIZE/8;  // Integer.SIZE is 32 bits or 4 bytes
		    	WriteDataPageBuffer.putInt(pdata[sum].getPageNumber());		// Put  our pageNumber(4 bytes) into DataPage
		    	serialized_data_size += Integer.SIZE/8;  // Integer.SIZE is 32 bits or 4 bytes
		        sum=sum+1;
			}
			 // ================= Write to the file =====================================
	        RandomAccessFile MyFile = new RandomAccessFile ("TheIndexFile", "rw");
	        MyFile.seek(pos);
	        MyFile.write(WriteDataPageBuffer.array());
	        pos=pos+DataPageSize;//We need this so we can seek to the right position in our disk(next page)
	        MyFile.close();
	        // ========================================================================
	        
	        // reset the bytebuffer to use it for the next page. Need to erase the previous data
	        WriteDataPageBuffer.rewind(); 
	        WriteDataPageBuffer.put(zeroPageBytes);
	        WriteDataPageBuffer.rewind();
		}
        while (remaining!=0){
        	
	    	WriteDataPageBuffer.putInt(pdata[sum].getKey());		// Put  our key(4 bytes) into DataPage
	    	serialized_data_size += Integer.SIZE/8;  // Integer.SIZE is 32 bits or 4 bytes
	    	WriteDataPageBuffer.putInt(pdata[sum].getPageNumber());		// Put  our PageNumber(4 bytes) into DataPage
	    	serialized_data_size += Integer.SIZE/8;  // Integer.SIZE is 32 bits or 4 bytes
	        sum=sum+1;
	        remaining=remaining-1;
	        if(remaining==0){
		        // ================= Write to the file =====================================
		        RandomAccessFile MyFile = new RandomAccessFile ("TheIndexFile", "rw");
		        MyFile.seek(pos);
		        MyFile.write(WriteDataPageBuffer.array());
		        pos=pos+DataPageSize;//We need this so we can seek to the right position in our disk(next page)
		        MyFile.close();
		        pages=pages+1;//This is the page in which we write the remainings
		        // ========================================================================
	        }
        }
		
	}
}
