package pck;

import java.io.IOException;
import java.util.Arrays;

import Files.*;
import methods.MethodC;
import methods.MethodA;
import methods.MethodB;


public class App {
	public static int DataPageSize=256;//Our page size in bytes
	public static int FIELD_SIZE;//We gonna give this is value later
	static final Integer LOW=1;
	static int[] N={50, 100, 200, 500, 800, 1000, 2000, 5000, 10000, 50000, 100000, 200000};//The amount of records (key,data)
	public static int[] n={55,27};
	private static int i;
	private static int j,k;
	//private static int sumA=0,sumB=0,sumC=0;	//These are used in the MO
	//private static double timeA=0,timeB=0,timeC=0;//These are used in the MO
	static java.util.Random randomGenerator = new java.util.Random();
	
	
	public static void main(String[] args) throws IOException {
		int[] Lost_Key=new int[1000];
		int IndexPages=0;
		// Get the size n of Data(Strings) if k=0 then n=55 else n=27bytes
		for(k=0; k<2; k++){//****************
			

			if(k==0){
				
				for(j=0; j<12; j++){//*******************
					IndexPages=0;
					int sumA=0,sumB=0,sumC=0;	//These are used in the MO
					double timeA=0,timeB=0,timeC=0;//These are used in the MO
					//Create some random unique keys
					int[] randomUKeys = randomGenerator.ints(LOW,(2*N[j])+1).distinct().limit(N[j]).toArray();
					//Create the array of the DataClass
					DataClass[] data= new DataClass[N[j]];
					DataPage[] pdata= new DataPage[N[j]];
					int FIELD_SIZE=n[k];
					
					for (i=0; i<N[j]; i++){
						//Create N instances of DataClass
						data[i] =new DataClass(randomUKeys[i],RandomString.getAlphaNumericString(FIELD_SIZE));
						if(i%4==0){//4 keys in every page(this is used for the index file
							IndexPages=IndexPages+1;
						}
						pdata[i]=new DataPage(randomUKeys[i],IndexPages);						
					}
					
					MakeFile.processArray(data,FIELD_SIZE,DataPageSize);
					MakeIndexFile.processArray(pdata,DataPageSize);
					
					Arrays.sort(pdata);//We sort our keys
					MakeSortedIndexFile.processArray(pdata, DataPageSize);
					
					if(N[j]<1000){
						//Create 1000 random no unique keys
						int[] randomInts = randomGenerator.ints(LOW,(2*N[j])+1).limit(1000).toArray();
						//Here we make 1000 Lost_Keys
						for (int a=0; a<1000; a++){
							//Create our Lost_Key array
							Lost_Key[a] =randomInts[a];
							
							double startA=(double) System.nanoTime();
							int accessA=MethodA.searchKey(data,Lost_Key[a],FIELD_SIZE,DataPageSize);
							double endA=(double) System.nanoTime();
							timeA= (endA-startA);
							sumA=sumA+accessA;
							
							double startB=(double) System.nanoTime();
							int accessB=MethodB.searchKey(pdata, data, Lost_Key[a], FIELD_SIZE, DataPageSize);
							double endB=(double) System.nanoTime();
							timeB= (endB-startB);
							sumB=sumB+accessB;
							
							double startC=(double) System.nanoTime();
							int accessC=MethodC.binarySearch(pdata, data, Lost_Key[a], FIELD_SIZE, DataPageSize);
							double endC=(double) System.nanoTime();
							timeC= (endC-startC);
							sumC=sumC+accessC;
						}

					}
					else{
						//Create 1000 random unique keys
						int[] randomUInts = randomGenerator.ints(LOW,(2*N[j])+1).distinct().limit(1000).toArray();
						//Here we make 1000 Lost_Keys
						for (int a=0; a<1000; a++){
							//Create our Lost_Key array
							Lost_Key[a] =randomUInts[a];
							
							double startA=(double) System.nanoTime();
							int accessA=MethodA.searchKey(data,Lost_Key[a],FIELD_SIZE,DataPageSize);
							double endA=(double) System.nanoTime();
							timeA= (endA-startA);
							sumA=sumA+accessA;
							
							double startB=(double) System.nanoTime();
							int accessB=MethodB.searchKey(pdata, data, Lost_Key[a], FIELD_SIZE, DataPageSize);
							double endB=(double) System.nanoTime();
							timeB= (endB-startB);
							sumB=sumB+accessB;
							
							double startC=(double) System.nanoTime();
							int accessC=MethodC.binarySearch(pdata, data, Lost_Key[a], FIELD_SIZE, DataPageSize);
							double endC=(double) System.nanoTime();
							timeC= (endC-startC);
							sumC=sumC+accessC;
						}
					}
					double MOA=(double)sumA/(1000);
					timeA=timeA/(1000);
					String formattedA = String.format("%.1f", MOA);	
					String formattedTA = String.format("%.1f", timeA); 
					System.out.println("Number of N is:  "+N[j]+"  Field Size(in bytes) is:  "+FIELD_SIZE+"  MO of accesses in MethodA is:  "+formattedA+"  and MO of nanoseconds is:  "+formattedTA);
					
					double MOB=(double)sumB/(1000);
					timeB=timeB/(1000);
					String formattedB = String.format("%.1f", MOB);
					String formattedTB = String.format("%.1f", timeB);
					System.out.println("Number of N is:  "+N[j]+"  Field Size(in bytes) is:  "+FIELD_SIZE+"  MO of accesses in MethodB is:  "+formattedB+"  and MO of nanoseconds is:  "+formattedTB);
					
					double MOC=(double)sumC/(1000);
					timeC=timeC/(1000);
					String formattedC = String.format("%.1f", MOC);	
					String formattedTC = String.format("%.1f", timeC);
					System.out.println("Number of N is:  "+N[j]+"  Field Size(in bytes) is:  "+FIELD_SIZE+"  MO of accesses in MethodC is:  "+formattedC+"  and MO of nanoseconds is:  "+formattedTC);
				}
			}
			else if (k==1){
				//Create our DataClass for all of our N prices
				for(j=0; j<12; j++){
					IndexPages=0;
					int sumA=0,sumB=0,sumC=0;	//These are used in the MO
					double timeA=0,timeB=0,timeC=0;//These are used in the MO
					//Create some random unique keys
					int[] randomUKeys = randomGenerator.ints(LOW,(2*N[j])+1).distinct().limit(N[j]).toArray();
					//Create the array of the DataClass
					DataClass[] data= new DataClass[N[j]];
					DataPage[] pdata= new DataPage[N[j]];
					int FIELD_SIZE=n[k];
					//Here we make our instances to store in our files
					for (i=0; i<N[j]; i++){
						//Create N instances of DataClass
						data[i] =new DataClass(randomUKeys[i],RandomString.getAlphaNumericString(FIELD_SIZE));
						if(i%8==0){//8 records in every page(this is used for the index file to find the page of the main file)
							IndexPages=IndexPages+1;
						}
						pdata[i]=new DataPage(randomUKeys[i],IndexPages);												
					}
					
					MakeFile.processArray(data,FIELD_SIZE,DataPageSize);
					MakeIndexFile.processArray(pdata,DataPageSize);
					
					Arrays.sort(pdata);//We sort our keys
					MakeSortedIndexFile.processArray(pdata, DataPageSize);
					
					if(N[j]<1000){
						//Create 1000 random no unique keys
						int[] randomInts = randomGenerator.ints(LOW,(2*N[j])+1).limit(1000).toArray();
						//Here we make 1000 Lost_Keys
						for (int a=0; a<1000; a++){
							//Create our Lost_Key array
							Lost_Key[a] =randomInts[a];
							
							double startA=(double) System.nanoTime();
							int accessA=MethodA.searchKey(data,Lost_Key[a],FIELD_SIZE,DataPageSize);
							double endA=(double) System.nanoTime();
							timeA= (endA-startA);
							sumA=sumA+accessA;
							
							double startB=(double) System.nanoTime();
							int accessB=MethodB.searchKey(pdata, data, Lost_Key[a], FIELD_SIZE, DataPageSize);
							double endB=(double) System.nanoTime();
							timeB= (endB-startB);
							sumB=sumB+accessB;
							
							double startC=(double) System.nanoTime();
							int accessC=MethodC.binarySearch(pdata, data, Lost_Key[a], FIELD_SIZE, DataPageSize);
							double endC=(double) System.nanoTime();
							timeC= (endC-startC);
							sumC=sumC+accessC;
						}
					}
					else{
						//Create 1000 random unique keys
						int[] randomUInts = randomGenerator.ints(LOW,(2*N[j])+1).distinct().limit(1000).toArray();
						//Here we make 1000 Lost_Keys
						for (int a=0; a<1000; a++){
							//Create our Lost_Key array
							Lost_Key[a] =randomUInts[a];
							
							double startA=(double) System.nanoTime();
							int accessA=MethodA.searchKey(data,Lost_Key[a],FIELD_SIZE,DataPageSize);
							double endA=(double) System.nanoTime();
							timeA= (endA-startA);
							sumA=sumA+accessA;
							
							double startB=(double) System.nanoTime();
							int accessB=MethodB.searchKey(pdata, data, Lost_Key[a], FIELD_SIZE, DataPageSize);
							double endB=(double) System.nanoTime();
							timeB= (endB-startB);
							sumB=sumB+accessB;
							
							double startC=(double) System.nanoTime();
							int accessC=MethodC.binarySearch(pdata, data, Lost_Key[a], FIELD_SIZE, DataPageSize);
							double endC=(double) System.nanoTime();
							timeC= (endC-startC);
							sumC=sumC+accessC;
						}
					}
					double MOA=(double)sumA/(1000);
					timeA=timeA/(1000);
					String formattedA = String.format("%.1f", MOA);	
					String formattedTA = String.format("%.1f", timeA); 
					System.out.println("Number of N is:  "+N[j]+"  Field Size(in bytes) is:  "+FIELD_SIZE+"  MO of accesses in MethodA is:  "+formattedA+"  and MO of nanoseconds is:  "+formattedTA);
					
					double MOB=(double)sumB/(1000);
					timeB=timeB/(1000);
					String formattedB = String.format("%.1f", MOB);
					String formattedTB = String.format("%.1f", timeB);
					System.out.println("Number of N is:  "+N[j]+"  Field Size(in bytes) is:  "+FIELD_SIZE+"  MO of accesses in MethodB is:  "+formattedB+"  and MO of nanoseconds is:  "+formattedTB);
					
					double MOC=(double)sumC/(1000);
					timeC=timeC/(1000);
					String formattedC = String.format("%.1f", MOC);	
					String formattedTC = String.format("%.1f", timeC);
					System.out.println("Number of N is:  "+N[j]+"  Field Size(in bytes) is:  "+FIELD_SIZE+"  MO of accesses in MethodC is:  "+formattedC+"  and MO of nanoseconds is:  "+formattedTC);
				}

			}
		}
	}
}
	
