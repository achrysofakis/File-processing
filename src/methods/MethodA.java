package methods;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;

import pck.DataClass;

public class MethodA {
			
	public static int searchKey(DataClass[] data,int Lost_Key,int FIELD_SIZE,int DataPageSize) throws IOException {
		//byte[] zeroPageBytes = new byte[DataPageSize];
		int CurrentKey;//This is the current key from the file
		int i,j;//These are for our loops
		int access=0;//count the amount of accesses in the file
		//All of our instances div by (DataPageSize/(FIELD_SIZE+4) how many records we can write in a page
		double number=(double) Array.getLength(data)/(DataPageSize/(FIELD_SIZE+4));
		int pages= (int)Math.floor(number);
		int remaining = Array.getLength(data)%(Math.round(DataPageSize/(FIELD_SIZE+4)));//we need this to read the remaining intances in our disk
		boolean found=false;
		
		 // ================= Get access to file =======================================
		RandomAccessFile MyFile = new RandomAccessFile ("TheFile", "r");
  
        
		//this is used to read our string
        byte bb[] = new byte[FIELD_SIZE];
        
        for(j=0; j<pages; j++){
        	access=access+1;
        	byte[] ReadDataPage = new byte[DataPageSize];
        	MyFile.seek(j * DataPageSize); // Seek to the start of the page
            MyFile.readFully(ReadDataPage); // Read the page into the byte array
            
            // Create a new ByteArrayInputStream for the page data
            ByteArrayInputStream bis = new ByteArrayInputStream(ReadDataPage);
            DataInputStream din = new DataInputStream(bis);
            
	        for(i=0; i<(Math.round(DataPageSize/(FIELD_SIZE+4))); i++){//Find the amount of records in a page and read them one by one
	        	CurrentKey=din.readInt();
	        	din.read(bb);
	            String ss = new String(bb,java.nio.charset.StandardCharsets.US_ASCII);
		        data[i] =new DataClass(CurrentKey,ss);
		        	        
		        //This happens if we find the key
		        if(data[i].getKey()==Lost_Key){
		        	//System.out.println("Key  "+data[i].getKey()+"  Found in page  "+(j+1)+"  with this string:"+data[i].getData()+"  This amount of accesses in methodA:"+access);
		        	found=true;
		        	remaining=0;
		        	break;
		        }		        			          
	        }
	        if(found){
	        	break;
	        }
	        din.skipBytes((DataPageSize-((FIELD_SIZE+4)*(Math.round(DataPageSize/(FIELD_SIZE+4))))));//These are the empty bytes in every page
	        
	        // Close the current ByteArrayInputStream and DataInputStream
	        din.close();
	        bis.close();

        }
        if(remaining!=0 && found==false){
        	access=access+1;
        	byte[] ReadDataPage = new byte[DataPageSize];
        	MyFile.seek(pages * DataPageSize); // Seek to the start of the page
 	        MyFile.readFully(ReadDataPage); // Read the page into the byte array
 	        
 	        // Create a new ByteArrayInputStream for the page data
            ByteArrayInputStream bis = new ByteArrayInputStream(ReadDataPage);
            DataInputStream din = new DataInputStream(bis);
            
	        while (remaining!=0 && MyFile.getFilePointer() <= MyFile.length()){//This loop will stop when we get all the remaining records or when we will get out of the file area
	        	CurrentKey=din.readInt();
	        	din.read(bb);
	            String ss = new String(bb,java.nio.charset.StandardCharsets.US_ASCII);
		        data[remaining] =new DataClass(CurrentKey,ss);
		        
		        //This happens if we find the key
		        if(data[remaining].getKey()==Lost_Key){
		        	//System.out.println("Key  "+data[remaining].getKey()+"  Found in page  "+(j+1)+"  with this string:"+data[remaining].getData()+"  this amount of accesses in methodA:"+access);
		        	found=true;
		        }     
		        if(found){
		        	// Close the current ByteArrayInputStream and DataInputStream
			        din.close();
			        bis.close();
		        	break;
		        }
		        else if(remaining==1){
		        	//System.out.println("The Key does not exists in this file for sure,Amount of accesses in methodA:  "+access);
		        	// Close the current ByteArrayInputStream and DataInputStream
			        din.close();
			        bis.close();
		        }
		        remaining=remaining-1;
	        }
	    }
        else if(found==false){
        	//System.out.println("The Key does not exists in this file for sure,Amount of accesses in methodA:  "+access);
        }
        MyFile.close();
		return access;
	}

}
