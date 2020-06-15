private static void checkUniqueness(String[] list) throws Exception {
		
		File file[]=new File[list.length];
		
		for(int i=0;i<file.length;i++) {
			file[i]=new File(list[i]);
			if(!file[i].exists()) {
				System.out.println("File doesn't exist "+file[i]);
				System.exit(0);
			}
		}
		
		Map<String, Integer> map=new HashMap<>();
		String str;
		int count=0;
		
		BufferedReader br[]=new BufferedReader[file.length];
		for(int i=0;i<file.length; i++) {
			br[i]=new BufferedReader(new InputStreamReader(new FileInputStream(file[i].getAbsoluteFile())));
			while(br[i].ready()) {
				str=br[i].readLine();
				if(str==null || str.trim().length()==0) {System.out.println("----read string is -- "+str+" in "+i+"th file");}
				else {
					str=str.split(" ")[0];
				
					if(map.get(str)!=null) {
						System.out.println("--FATAL ERROR--\n\tDuplicate VId:: "+str);
						System.exit(0);
					}
					else {
						map.put(str, 1);
						count++; 
					}
				}
				
			}
			br[i].close();
			System.out.println("File processed is "+file[i]+" total count till now is "+count);
			
		}
		System.out.println("\n--All are Unique--");
		
	}
	