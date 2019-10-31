package platform.util.general;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralUtility {
	
	public static void finish() {
//		System.out.println("-----------------------------------\n执行完毕，输入任何字母，回车退出。");
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	
	public static String currTime()
	{
		return new SimpleDateFormat("[yyyy.MM.dd,HH:mm:ss]").format(Calendar.getInstance().getTime());
	}
	
	public long DJBHash(String str) {
		long hash = 5381;
		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) + hash) + str.charAt(i);
		}
		return hash;
	}
	
    // 没有考虑兆及更大单位情况 
    public static long chnNum2Digit(String chnNum) {  
        // initialize map  
        java.util.Map<String, Integer> unitMap = new java.util.HashMap<String, Integer>();  
        unitMap.put("个", 1);// 仅在数据存储时使用  
        unitMap.put("十", 10);  
        unitMap.put("百", 100);  
        unitMap.put("千", 1000);  
        unitMap.put("万", 10000);  
        unitMap.put("亿", 100000000);  
  
        java.util.Map<String, Integer> numMap = new java.util.HashMap<String, Integer>();  
        numMap.put("零", 0);  
        numMap.put("一", 1);  
        numMap.put("二", 2);  
        numMap.put("三", 3);  
        numMap.put("四", 4);  
        numMap.put("五", 5);  
        numMap.put("六", 6);  
        numMap.put("七", 7);  
        numMap.put("八", 8);  
        numMap.put("九", 9);  
  
        // 数据存储结构：  
        // 单位 数量  
        // "个" num  
        // "十" num  
        // "百" num  
        // "千" num  
        // "万" num  
        // "亿" num  
        java.util.Map<String, Long> dataMap = new java.util.LinkedHashMap<String, Long>();  
  
        // 保存"亿"或"万"之前存在的多位数  
        java.util.List<Long> multiNumList = new java.util.ArrayList<Long>();  
  
        long tempNum = 0;  
        for (int i = 0; i < chnNum.length(); i++) {  
            char bit = chnNum.charAt(i);  
            System.out.print(bit);  
  
            // 因为"亿"或"万"存在多位情况，所以需要进行判断  
            boolean isExist = false;  
            // 存在"亿"或"万"情况(不计算当前索引位)  
            if ((chnNum.indexOf('亿', i) != -1 || chnNum.indexOf('万', i) != -1)  
                    && chnNum.charAt(i) != '亿' && chnNum.charAt(i) != '万') {  
                isExist = true;  
            }  
  
            // 数字  
            if (numMap.containsKey(bit + "")) {  
                if (i != chnNum.length() - 1) {  
                    tempNum = tempNum + numMap.get(bit + "");  
                }  
                // 最末位数字情况  
                else {  
                    dataMap.put("个", Long.valueOf(numMap.get(bit + "") + ""));  
                    tempNum = 0;  
                }  
            } else if (bit == '亿') {  
                // 存在"万亿"情况，取出"万"位值*10000,0000后重新put到map  
                if (i - 1 >= 0 && chnNum.charAt(i - 1) == '万') {  
                    Long dataValue = dataMap.get("万");  
                    if (dataValue != null && dataValue > 0) {  
                        dataMap.put("万", dataValue * unitMap.get(bit + ""));  
                    }  
                    continue;  
                }  
  
                // 最后一位数进list等待处理  
                if (tempNum != 0) {  
                    multiNumList.add(tempNum);  
                }  
                // 处理"亿"之前的多位数  
                long sum = 0;  
                for (Long num : multiNumList) {  
                    sum += num;  
                }  
                multiNumList.clear();  
                dataMap.put("亿", sum);  
                tempNum = 0;  
            } else if (bit == '万') {  
                // 最后一位数进list等待处理  
                if (tempNum != 0) {  
                    multiNumList.add(tempNum);  
                }  
                // 处理"万"之前的多位数  
                long sum = 0;  
                for (Long num : multiNumList) {  
                    sum += num;  
                }  
                multiNumList.clear();  
                dataMap.put("万", sum);  
                tempNum = 0;  
            } else if (bit == '千' && tempNum > 0) {  
                // 存在"亿"或"万"情况，临时变量值*"千"单位值进list等待处理  
                if (isExist) {  
                    multiNumList.add(tempNum * unitMap.get(bit + ""));  
                    tempNum = 0;  
                }  
                // 不存在"亿"或"万"情况，临时变量值put到map  
                else {  
                    dataMap.put("千", tempNum);  
                    tempNum = 0;  
                }  
            } else if (bit == '百' && tempNum > 0) {  
                // 存在"亿"或"万"情况，临时变量值*"百"单位值进list等待处理  
                if (isExist) {  
                    multiNumList.add(tempNum * unitMap.get(bit + ""));  
                    tempNum = 0;  
                }  
                // 不存在"亿"或"万"情况，临时变量值put到map  
                else {  
                    dataMap.put("百", tempNum);  
                    tempNum = 0;  
                }  
            } else if (bit == '十') {  
                // 存在"亿"或"万"情况，临时变量值*"十"单位值进list等待处理  
                if (isExist) {  
                    if (tempNum != 0) {  
                        multiNumList.add(tempNum * unitMap.get(bit + ""));  
                        tempNum = 0;  
                    }  
                    // 将"十"转换成"一十"  
                    else {  
                        tempNum = 1 * unitMap.get(bit + "");  
                    }  
                }  
                // 不存在"亿"或"万"情况，临时变量值put到map  
                else {  
                    if (tempNum != 0) {  
                        dataMap.put("十", tempNum);  
                    }  
                    // 将"十"转换成"一十"  
                    else {  
                        dataMap.put("十", 1l);  
                    }  
                    tempNum = 0;  
                }  
            }  
        }  
  
        // output  
        System.out.println();  
        long sum = 0;  
        java.util.Set<String> keys = dataMap.keySet();  
        for (String key : keys) {  
            Integer unitValue = unitMap.get(key);  
            Long dataValue = dataMap.get(key);  
            sum += unitValue * dataValue;  
        }
        return sum;
    }

    public static void main(String[] args) {
		System.out.println( chnNum2Digit("五") );
	}
    
    
	public static String cleanString(String original_text){
		original_text = original_text.replaceAll("([\r\n]+[ ]*)+", "\r\n");
		original_text = original_text.replaceAll("[ ]+", " ").replaceAll("[\r\n ]+$", "").replaceAll("^[\r\n ]+", "");
		return original_text;
	}
	
	public static String cleanNumber(String number){
		if(number.matches(".*[0-9]+\\.[0-9]+.*")){
	        Pattern pattern = Pattern.compile("[0-9]+\\.[0-9]+");  
	        Matcher matcher = pattern.matcher(number);
	        while (matcher.find()) {
	            return matcher.group();
	        }
	        return null;
		}else{
			return number.replaceAll("[^0-9]+", "");
		}
	}
	
	public static String cleanBlock(String block){
		return block.replaceAll("^[\r\n ]+", "").replaceAll("[\r\n ]+$", "").replaceAll("[ ]+", " ").replaceAll("[\r\n ]+", "\r\n");
	}
	
	public static String cleanLine(String line){
		return line.replaceAll("[\r\n]+", "").replaceAll("^[ ]+", "").replaceAll("[ ]+$", "").replaceAll("[ ]+", " ");
	}
	
	public static long sleep(long max_seconds){
		double a = Math.random();
		long t = (long)(a*max_seconds*1000);
		System.out.println("sleep for "+(t/1000.0) + "s");
		try {
			Thread.sleep( t );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public static long sleep(long max_seconds, boolean if_random){
		if(if_random){
			return sleep(max_seconds);
		}
		long t = max_seconds*1000;
		System.out.println("sleep for "+(t/1000.0) + "s");
		try {
			Thread.sleep( t );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public static String urlToFilePath( String url, String base_path ){
//		System.out.println(url);
//		System.out.println(base_path);
		String path = base_path+"/"+url.replace("http://", "").replaceAll("[^a-zA-Z0-9_]", "/").replaceAll("[/]+$", "")+".html";
//		System.out.println(path);
//		System.out.println("-------------");
		return path.replaceAll("[\\\\/]+", "/");
	}
	
	public static String urlToFileUrl(String url, String base_path){
		return "file:///"+urlToFilePath(url, base_path);
	}
	/*
	 * 解析 n秒前，n分钟前，n小时前，n天前，n月前，n年前。
	 */
	public static String[] parseTime(String how_long_ago){
		String[] time_split=null;
		String date=null;
		String time=null;
		Calendar c = Calendar.getInstance();
		int temp=0;
		if(how_long_ago.contains("秒"))
		{
			temp = Integer.parseInt( GeneralUtility.cleanNumber( how_long_ago.substring(0, how_long_ago.indexOf("秒")) ));
			c.set(Calendar.SECOND, c.get(Calendar.SECOND)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split(" ");
			date = time_split[0];
			time = time_split[1];
		}else if( how_long_ago.contains("分钟") ){
			temp = Integer.parseInt( GeneralUtility.cleanNumber( how_long_ago.substring(0, how_long_ago.indexOf("分钟")) ));
			c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split(":");
			String n = time_split[0]+":"+time_split[1]+":00";
			time_split = n.split(" ");
			date = time_split[0];
			time = time_split[1];
		}else if( how_long_ago.contains("小时") ){
			temp = Integer.parseInt( GeneralUtility.cleanNumber( how_long_ago.substring(0, how_long_ago.indexOf("小时")) ));
			c.set(Calendar.HOUR, c.get(Calendar.HOUR)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split(":");
			String n = time_split[0]+":00:00";
			time_split = n.split(" ");
			date = time_split[0];
			time = time_split[1];
		}else if( how_long_ago.contains("天") ){
			temp = Integer.parseInt( GeneralUtility.cleanNumber( how_long_ago.substring(0, how_long_ago.indexOf("天")) ) );
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split(" ");
			date = time_split[0];
			time = null;
		}else if( how_long_ago.contains("月") ){
			temp = Integer.parseInt( GeneralUtility.cleanNumber( how_long_ago.substring(0, how_long_ago.indexOf("个月")) ) );
			c.set(Calendar.MONTH, c.get(Calendar.MONTH)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split("-");
			date = time_split[0]+"-"+time_split[1]+"-01";
			time = null;
		}else if( how_long_ago.contains("年") ){
			temp = Integer.parseInt( GeneralUtility.cleanNumber( how_long_ago.substring(0, how_long_ago.indexOf("年")) ));
			c.set(Calendar.YEAR, c.get(Calendar.YEAR)-temp);
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			time_split = sDateFormat.format(c.getTime()).split("-");
			date = time_split[0]+"-01-01";
			time = null;
		}
		String[] date_time = new String[2];
		date_time[0] = date;
		date_time[1] = time;
		return date_time;
	}
	
	public static String getFullUrl(String base_url, String relative_url){
		String final_url = null;
		try{
			URL baseurl = new URL(base_url);
			final_url =  new URL(baseurl, relative_url).toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return final_url;
	}

}
