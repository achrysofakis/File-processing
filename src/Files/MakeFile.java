package Files;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import pck.DataClass;

public class MakeFile {
	
    
	public static void processArray(DataClass[] data,int FIELD_SIZE,int DataPageSize) throws IOException {
	    byte[] zeroBytes = new byte[FIELD_SIZE];
	    byte[] zeroPageBytes = new byte[DataPageSize];
		@SuppressWarnings("unused")
		int serialized_data_size = 0;
		//All of our instances div by (DataPageSize/(FIELD_SIZE+4) how many records we can write in a page
		double number=(double) Array.getLength(data)/(DataPageSize/(FIELD_SIZE+4));
		int pages= (int)Math.floor(number);
		int i,j;
		int remaining = Array.getLength(data)%(Math.round(DataPageSize/(FIELD_SIZE+4)));//we need this to write the remaining intances in our disk
		int sum=0;//We use this to go through our data array
		int pos=0;//We use this to seek in the write position in our disk
		java.nio.ByteBuffer WriteDataPageBuffer = java.nio.ByteBuffer.allocate(DataPageSize); // Create a buffer up to DataPageSize bytes
		
		// Encode this String into a sequence of bytes using the given charset, storing the result into a new byte array.
    	java.nio.ByteBuffer dst = java.nio.ByteBuffer.allocate(FIELD_SIZE); // up to FIELD_SIZE bytes
    	
		for(j=0; j<(pages); j++){
			
			for(i=0; i<(Math.round(DataPageSize/(FIELD_SIZE+4))); i++){//We full our buffer and need to clear it after this loop(1 page loop)
				
		    	
		    	WriteDataPageBuffer.putInt(data[sum].getKey());		// Put  our key(4 bytes) into DataPage
		    	serialized_data_size += Integer.SIZE/8;  // Integer.SIZE is 32 bits or 4 bytes
				
		    	// get array of bytes from string, as ASCII. put it into dst
		    	dst.put(data[sum].getData().getBytes(java.nio.charset.StandardCharsets.UTF_8));
		    	
		    	// Get byte array of ByteBuffer and put into the DataPage.
		    	// The byte array will have length the number of bytes allocated in the constructor
		    	WriteDataPageBuffer.put(dst.array());
		    	serialized_data_size += FIELD_SIZE;
		    	
		    	// reset the bytebuffer to use it for the next part. Need to erase the previous data
		        dst.rewind(); 
		        dst.put(zeroBytes);
		        dst.rewind();
		        //System.out.println("Total size of all serialized data in DataPage: " + serialized_data_size + " bytes (1 array of 55 bytes and 1 int of 4 bytes)");
		        sum=sum+1;
			}
			 // ================= Write to the file =====================================
	        RandomAccessFile MyFile = new RandomAccessFile ("TheFile", "rw");
	        MyFile.seek(pos);
	        MyFile.write(WriteDataPageBuffer.array());
	        pos=pos+DataPageSize;//We need this so we can seek to the right position in our disk(next page)
	        MyFile.close();
	        // ========================================================================
	        
	        serialized_data_size +=(DataPageSize-((FIELD_SIZE+4)*(Math.round(DataPageSize/(FIELD_SIZE+4)))));//These are the empty bytes in every page
	        
	        // reset the bytebuffer to use it for the next page. Need to erase the previous data
	        WriteDataPageBuffer.rewind(); 
	        WriteDataPageBuffer.put(zeroPageBytes);
	        WriteDataPageBuffer.rewind();
		}
        while (remaining!=0){
        	
	    	WriteDataPageBuffer.putInt(data[sum].getKey());		// Put  our key(4 bytes) into DataPage
	    	serialized_data_size += Integer.SIZE/8;  // Integer.SIZE is 32 bits or 4 bytes
			
	    	// get array of bytes from string, as ASCII. put it into dst
	    	dst.put(data[sum].getData().getBytes(java.nio.charset.StandardCharsets.UTF_8));
	    	
	    	// Get byte array of ByteBuffer and put into the DataPage.
	    	// The byte array will have length the number of bytes allocated in the constructor
	    	WriteDataPageBuffer.put(dst.array());
	    	serialized_data_size += FIELD_SIZE;
	    	
	    	// reset the bytebuffer to use it for the next part. Need to erase the previous data
	        dst.rewind(); 
	        dst.put(zeroBytes);
	        dst.rewind();
	        //System.out.println("Total size of all serialized data in DataPage: " + serialized_data_size + " bytes (1 array of 55 bytes and 1 int of 4 bytes)");
	        sum=sum+1;
	        remaining=remaining-1;
	        if(remaining==0){
		        // ================= Write to the file =====================================
		        RandomAccessFile MyFile = new RandomAccessFile ("TheFile", "rw");
		        MyFile.seek(pos);
		        MyFile.write(WriteDataPageBuffer.array());
		        pos=pos+DataPageSize;//We need this so we can seek to the right position in our disk(next page)
		        MyFile.close();
		        pages=pages+1;//This is the page in which we write the remaining
		        // ========================================================================
	        }
        }
		
	}
	
}
