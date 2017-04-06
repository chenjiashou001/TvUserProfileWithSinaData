package tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimilarDegreeByCos
{
	/*
	 * 计算两个字符串(英文字符)的相似度，简单的余弦计算，未添权重
	 */
     public static double getSimilarDegree(List<String> list_str1, List<String> list_str2)
     {
    	 if (list_str1 == null || list_str2 == null) {
    		 return 0;
    	 }
    	 if (list_str1.size() == 0 || list_str2.size() == 0) {
    		 return 0;
    	 }
    	//创建向量空间模型，使用map实现，主键为词项，值为长度为2的数组，存放着对应词项在字符串中的出现次数
    	 Map<String, int[]> vectorSpace = new HashMap<String, int[]>();
    	 int[] itemCountArray = null;//为了避免频繁产生局部变量，所以将itemCountArray声明在此
    	 
    	 String strArray[] = new String[list_str1.size()];
    	 list_str1.toArray(strArray);
    	 for(int i=0; i<strArray.length; ++i)
    	 {
    		 if(vectorSpace.containsKey(strArray[i]))
    			 ++(vectorSpace.get(strArray[i])[0]);
    		 else
    		 {
    			 itemCountArray = new int[2];
    	         itemCountArray[0] = 1;
    	         itemCountArray[1] = 0;
    	         vectorSpace.put(strArray[i], itemCountArray);
    		 }
    	 }
    	 
    	 strArray = new String[list_str2.size()];
    	 list_str2.toArray(strArray);
    	 for(int i=0; i<strArray.length; ++i)
    	 {
    		 if(vectorSpace.containsKey(strArray[i]))
    			 ++(vectorSpace.get(strArray[i])[1]);
    		 else
    		 {
    			 itemCountArray = new int[2];
    	         itemCountArray[0] = 0;
    	         itemCountArray[1] = 1;
    	         vectorSpace.put(strArray[i], itemCountArray);
    		 }
    	 }
    	 
    	 //计算相似度
    	 double vector1Modulo = 0.00;//向量1的模
    	 double vector2Modulo = 0.00;//向量2的模
    	 double vectorProduct = 0.00; //向量积
    	 Iterator iter = vectorSpace.entrySet().iterator();
    	 
    	 while(iter.hasNext())
    	 {
    		 Map.Entry entry = (Map.Entry)iter.next();
    		 itemCountArray = (int[])entry.getValue();
    		 
    		 vector1Modulo += itemCountArray[0]*itemCountArray[0];
    		 vector2Modulo += itemCountArray[1]*itemCountArray[1];
    		 
    		 vectorProduct += itemCountArray[0]*itemCountArray[1];
    	 }
    	 
    	 vector1Modulo = Math.sqrt(vector1Modulo);
    	 vector2Modulo = Math.sqrt(vector2Modulo);
    	 
    	 //返回相似度
		return (vectorProduct/(vector1Modulo*vector2Modulo));
     }
     
     /*
      * 
      */
     public static void main(String args[])
     {
    	 String str1 = "gold silver truck";
    	 String str2 = "Shipment of gold damaged in a fire";
    	 String str3 = "Delivery of silver arrived in a silver truck";
    	 String str4 = "Shipment of gold arrived in a truck";
    	 String str5 = "gold gold gold gold gold gold";
    	 
//    	 System.out.println(SimilarDegreeByCos.getSimilarDegree(str1, str2));
//    	 System.out.println(SimilarDegreeByCos.getSimilarDegree(str1, str3));
//    	 System.out.println(SimilarDegreeByCos.getSimilarDegree(str1, str4));
//    	 System.out.println(SimilarDegreeByCos.getSimilarDegree(str1, str5));
     }
}
