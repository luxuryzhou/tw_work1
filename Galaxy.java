package testalg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Galaxy {

	private static String I = "I";
	private static String V = "V";
	private static String X = "X";//final
	private static String L = "L";
	private static String C = "C";
	private static String D = "D";
	private static String M = "M";
	
	private static Map<String, Integer> roma = new HashMap<String, Integer>();
	
		
	public Galaxy(){
		
	}
	public static void main(String[] args){
		Galaxy g = new Galaxy();
		g.convert();
		System.out.println();
		/*Map<String, String> galaxyroma = new HashMap<String, String>();
		galaxyroma.put("1", "M");
		galaxyroma.put("2", "C");
		galaxyroma.put("3", "X");
		galaxyroma.put("4", "V");
		galaxyroma.put("5", "I");
		galaxyroma.put("6", "L");
		LinkedList<String> gal = new LinkedList<String>();
		gal.add("1");
		gal.add("2");
		gal.add("1");
		gal.add("3");
		gal.add("6");
		gal.add("5");
		gal.add("4");
		int x = g.calculateDec(gal, galaxyroma);
		
		System.out.println(x);*/
		
	}
	
	public void convert(){
		List<String> inp = new LinkedList<String>();//need to convert to linked list from input lines
		inp.add("glob is I");
		inp.add("prok is V");
		inp.add("pish is X");
		inp.add("tegj is L");
		inp.add("glob glob Silver is 34 Credits");
		inp.add("glob prok Gold is 57800 Credits");
		inp.add("pish pish Iron is 3910 Credits");
		inp.add("how much is pish tegj glob glob ?");
		inp.add("how many Credits is glob prok Silver ?");
		inp.add("how many Credits is glob prok Gold ?");
		inp.add("how many Credits is glob prok Iron ?");
		inp.add("how much wood could a woodchuck chuck if a woodchuck could chuck wood ?");
		
		roma.put(I, 1);
		roma.put(V, 5);
		roma.put(X, 10);
		roma.put(L, 50);
		roma.put(C, 100);
		roma.put(D, 500);
		roma.put(M, 1000);
		
		Map<String, String> galaxyroma = new HashMap<String, String>();
		List<Object[]> creds = new ArrayList<Object[]>();
		
		//parse measurement
		for(String s : inp){
			parseline(s,galaxyroma,creds);
		}
		
		Map<String, BigDecimal> mc = calculateMetalCred(creds, galaxyroma);
		
		for(String s : inp){
			String[] ss = s.trim().split(" ");//need to delete useless white spaces between chars
			if(ss.length >= 4){
				if("how".equalsIgnoreCase(ss[0])&&"much".equalsIgnoreCase(ss[1])&&"is".equalsIgnoreCase(ss[2])){
					LinkedList<String> gal = new LinkedList<String>();
					int e = "?".equalsIgnoreCase(ss[ss.length - 1])?1:0;
					for(int i = 3; i < ss.length - e; i++){
						gal.add(ss[i]);
					}
					int rst = calculateDec(gal, galaxyroma);
					for(String sss : gal){
						System.out.print(sss + " ");
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
						System.out.print(sss + " ");
					}
					String metal = ss[ss.length - 2];
					
					System.out.println(metal + " is " + (int)((double)rst * mc.get(metal).doubleValue()) + " Credits");
				}else if(s.indexOf("is")<0 && s.indexOf("redits")<0){
					System.out.println("I have no idea what you are talking about");
				}
			}
		}
	}
	private boolean calculate(){
		
		return false;
	}
	//calculate every metal costs credits
	private Map<String, BigDecimal> calculateMetalCred(List<Object[]> creds, Map<String, String> galaxyroma){
		Map<String, BigDecimal> mc = new HashMap<String, BigDecimal>();
		for(Object[] arr : creds){
			int num = calculateDec((LinkedList<String>)arr[0], galaxyroma);
			BigDecimal sglcr = BigDecimal.valueOf((double)Integer.valueOf(String.valueOf(arr[2])) / num);
			mc.put(arr[1].toString(),sglcr);
		}
		return mc;
	}
	private int calculateDec(LinkedList<String> gal, Map<String, String> galaxyroma){
		String[] roms = new String[gal.size()];
		for(int i = 0; i < gal.size(); i++){
			roms[i] = galaxyroma.get(gal.get(i));
		}
		int rst = 0;
		for(int j = 0; j < roms.length; j++){
			String s = roms[j];
			int curdec = roma.get(s);
			if("IXC".indexOf(s) < 0){
				rst += curdec;
			}else{
				if(j == roms.length - 1){
					rst += curdec;
				}else{
					int nextdec = roma.get(roms[j+1]);
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
	private boolean parseline(String s, Map<String, String> galaxyroma, List<Object[]> creds){
		
		if(s!=null){
			String[] ss = s.trim().split(" ");
			//need to delete useless white spaces between chars
			//can convert this arrays to linked list (keep order) than filter space and than convert to string[]
			
			if(ss.length==3){
				if(ss[1].equalsIgnoreCase("is") && "IVXLCDM".indexOf(ss[2])>=0){
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
	private String[] reverse(String [] s){
		if(s!=null){
			int len = s.length;
			String[] ss = new String[len];
			for(int i = 0; i < s.length; i++){
				ss[i] = s[len - i - 1];
			}
			return ss;
		}
		return null;//need to dispose null
	}
	
}
