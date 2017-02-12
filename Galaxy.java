package testalg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author zhou
 * 
 * @environment JKD1.5 or higher
 * 
 * @input the program need to place a named "inp.txt" file near the Galaxy.java file as beginning by run the main function
 * 
 * the content of the input text need to obey the following rules:
 * 1. words separated by white space only
 * 2. should define the galaxy and roma like: [galaxy] is [roma]
 * 3. should define the metal costs like: [galaxy] [metal] is [number] Credits
 * 4. when you want to calculate galaxy you should write like: how much is [galaxy]
 * 5. when you want to calculate metal you should write like: how many Credits is [galaxy] [metal]
 * 
 * @return currently only print the result to console in this version
 *
 */
public class Galaxy {

	private final static String I = "I";
	private final static String V = "V";
	private final static String X = "X";
	private final static String L = "L";
	private final static String C = "C";
	private final static String D = "D";
	private final static String M = "M";
	private final static String IVXLCDM = "IVXLCDM";
	private final static String IXC = "IXC";
	private final static String SPACE = " ";
	private final static String EMP = "";
	
	private static Map<String, Integer> ROMA = new HashMap<String, Integer>();
	
		
	public Galaxy(){
		ROMA.put(I, 1);
		ROMA.put(V, 5);
		ROMA.put(X, 10);
		ROMA.put(L, 50);
		ROMA.put(C, 100);
		ROMA.put(D, 500);
		ROMA.put(M, 1000);
	}
	
	public static void main(String[] args){
		Galaxy g = new Galaxy();
		g.convert();
	}
	
	public void convert(){
		//get input note string arrays
		List<String> inp = this.readInput();
		
		//initialize galaxy-roma and credits variables
		Map<String, String> galaxyroma = new HashMap<String, String>();
		List<Object[]> creds = new ArrayList<Object[]>();
		
		//parse measurement
		for(String s : inp){
			parseline(s,galaxyroma,creds);
		}
		
		//calculate the credits cost of metal
		Map<String, BigDecimal> mc = calculateMetalCred(creds, galaxyroma);
		
		//loop input note to parse and print results
		//TODO need to output into file or not?
		for(String s : inp){
			String[] ss = this.filterSpareSplit(s);
			if(ss.length >= 4){
				if("how".equalsIgnoreCase(ss[0])&&"much".equalsIgnoreCase(ss[1])&&"is".equalsIgnoreCase(ss[2])){
					LinkedList<String> gal = new LinkedList<String>();
					int e = "?".equalsIgnoreCase(ss[ss.length - 1])?1:0;
					for(int i = 3; i < ss.length - e; i++){
						gal.add(ss[i]);
					}
					int rst = calculateDec(gal, galaxyroma);
					for(String sss : gal){
						System.out.print(sss + SPACE);
					}
					System.out.println("is "+rst);
				}else if(ss.length >= 6&&"how".equalsIgnoreCase(ss[0])&&"many".equalsIgnoreCase(ss[1])&&"Credits".equalsIgnoreCase(ss[2])&&"is".equalsIgnoreCase(ss[3])){
					int e = "?".equalsIgnoreCase(ss[ss.length - 1])?2:1;
					LinkedList<String> gal = new LinkedList<String>();
					for(int i = 4; i < ss.length - e; i++){
						gal.add(ss[i]);
					}
					int rst = calculateDec(gal, galaxyroma);
					for(String sss : gal){
						System.out.print(sss + SPACE);
					}
					String metal = ss[ss.length - 2];
					
					System.out.println(metal + " is " + (int)((double)rst * mc.get(metal).doubleValue()) + " Credits");
				}else if(s.indexOf("is")<0 && s.indexOf("redits")<0){
					System.out.println("I have no idea what you are talking about");
				}
			}else{
				System.out.println("I have no idea what you are talking about");
			}
		}
	}
	//use for parsing the measurement in the note which is used for calculating 
	private boolean parseline(String line, Map<String, String> galaxyroma, List<Object[]> creds){
		if(line != null){
			String[] ss = this.filterSpareSplit(line);
			
			if(ss.length==3){
				if(ss[1].equalsIgnoreCase("is") && IVXLCDM.indexOf(ss[2])>=0){
					galaxyroma.put(ss[0], ss[2]);
				}
			}
			
			if(ss.length>=5){
				String[] ssr = reverse(ss);//the reversed order
				if(ssr[0].equalsIgnoreCase("Credits")&&ssr[2].equalsIgnoreCase("is")){
					Object[] e = new Object[3];
					List<String> gal = new LinkedList<String>();
						String[] gals = new String[ss.length - 4];
						for(int i = 0; i < gals.length; i++){
							gals[i] = ssr[ss.length - i - 1];
						}
						for(String g : gals){
							gal.add(g);
						}
					e[0] = gal;//galaxy numbers of metal
					e[1] = ssr[3];//name of metal
					e[2] = ssr[1];//numbers of credits
					creds.add(e);
				}
			}
		}
		return false;
	}
	
	//calculate every metal costs credits
	@SuppressWarnings("unchecked")
	private Map<String, BigDecimal> calculateMetalCred(List<Object[]> creds, Map<String, String> galaxyroma){
		Map<String, BigDecimal> mc = new HashMap<String, BigDecimal>();
		for(Object[] arr : creds){
			int num = calculateDec((LinkedList<String>)arr[0], galaxyroma);
			BigDecimal sglcr = BigDecimal.valueOf((double)Integer.valueOf(String.valueOf(arr[2])) / num);
			mc.put(arr[1].toString(),sglcr);
		}
		return mc;
	}
	//calculate decimal from ROMA
	private int calculateDec(LinkedList<String> gal, Map<String, String> galaxyroma){
		String[] roms = new String[gal.size()];
		for(int i = 0; i < gal.size(); i++){
			roms[i] = galaxyroma.get(gal.get(i));
		}
		int rst = 0;
		for(int j = 0; j < roms.length; j++){
			String s = roms[j];
			int curdec = ROMA.get(s);
			if(IXC.indexOf(s) < 0){
				rst += curdec;
			}else{
				if(j == roms.length - 1){
					rst += curdec;
				}else{
					int nextdec = ROMA.get(roms[j+1]);
					if(curdec < nextdec){
						rst += (nextdec - curdec);
						j++;
					}else{
						rst += curdec;
					}
				}
			}
		}
		return rst;
	}
	//reverse the string in the array
	private String[] reverse(String [] s){
		if(s!=null){
			int len = s.length;
			String[] ss = new String[len];
			for(int i = 0; i < s.length; i++){
				ss[i] = s[len - i - 1];
			}
			return ss;
		}
		return null;//TODO need to dispose null
	}
	//read input file
	private List<String> readInput(){
		String pathname = "inp.txt";
		List<String> rst = new ArrayList<String>();
		//get the path of the inp.txt file which is placed near source file and delete the beginning "file:/"
		String path = Galaxy.class.getResource(EMP).toString().substring(5);
		
		File filename = new File(path+pathname);
        
        if(filename.isFile() && filename.exists()){
	        BufferedReader br = null;
	        try{
	        	InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
	            br = new BufferedReader(reader);
	            String line = br.readLine();
	            rst.add(line);
		        while (line != null) {
		            line = br.readLine();
		            if(line != null){
		            	rst.add(line);
		            }
		        }
	        }catch(IOException e){
	        	e.printStackTrace();
	        }finally{
	        	if(br != null){
	        		try{
	        			br.close();
	        		}catch(IOException e){
	        			e.printStackTrace();
	        		}
	        	}
	        }
        }else{
        	System.out.println("please place inp.txt correctlly");
        }
        return rst;
	}
	private String[] filterSpareSplit(String line){
		LinkedList<String> ll = new LinkedList<String>();
		if(line != null){
			if(line.length() > 0){
				String[] arr = line.split(SPACE);
				for(String s : arr){
					if(!s.trim().equals(EMP)){
						ll.add(s);
					}
				}
			}
		}
		int len = ll.size();
		String[] rst = new String[len];
		if(len > 0){
			for(int i = 0; i < len; i++){
				rst[i] = ll.get(i);
			}
		}
		return rst;
	}
}
