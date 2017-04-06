package demo;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import tools.LogUtil;
import tools.WekaUtil;
import user.SinaUser;
import userprofile.SinaUserProfile;
import weka.core.Instance;
import weka.core.Instances;

//private static String _TAG_NAME[] = {
//		"搞笑幽默",//	id = 0  84%
//		"电影",//		id = 1	85%
//		"文艺",//		id = 2	74%
//		"音乐",//		id = 3	73%
//		"美食",//		id = 4	70%
//		"军事",//		id = 5	95%
//		"财经",//		id = 6	94%
//		"动漫",// 		id = 7	91%
//		"体育",//		id = 8	92%
//		"明星",//		id = 9	76%
//		null
//	};//年龄段待定,性别待定,因为都是多选

/**
 * @author chenhuan001
 *
 */
public class WekaDemo {
	
	private static void testClassifier() {
		// TODO Auto-generated method stub
//		try {
//	          
//	           Classifier classifier1;
//	           Classifier classifier2;
//	           Classifier classifier3;
//	           Classifier classifier4;
			
	           for (int i = 0;i <= 9; i++) {
		           int need_see_tagid = i;
		           Instances extra_insts = SinaUserProfile.getInstancesByTag(need_see_tagid); // 读入训练文件
		           extra_insts = WekaUtil.addExtraInstance(extra_insts);
		           extra_insts = WekaUtil.addExtraInstance(extra_insts);
		           
		           System.out.println(extra_insts.numInstances());
		           WekaUtil.generateArffFile(extra_insts, "/Users/chenhuan001/Desktop/SinaHelpTvProfile/SinaData/SinaInstances"+ need_see_tagid +".arff");
		           WekaUtil.generateCSVFile(extra_insts, "/Users/chenhuan001/Desktop/SinaHelpTvProfile/SinaData/SinaInstances"+ need_see_tagid +".csv");
				}
	           /*
	           // 朴素贝叶斯算法
	           classifier1 = (Classifier) Class.forName(
	                  "weka.classifiers.bayes.NaiveBayes").newInstance();
	           // 决策树
	           classifier2 = (Classifier) Class.forName(
	                  "weka.classifiers.trees.J48").newInstance();
	           // Zero
	           classifier3 = (Classifier) Class.forName(
	                  "weka.classifiers.rules.ZeroR").newInstance();
	           // LibSVM
//	           classifier4 = (Classifier) Class.forName(
//	                  "weka.classifiers.functions.LibSVM").newInstance();
	 
	          
//	           classifier4.buildClassifier(instancesTrain);
	           classifier1.buildClassifier(instancesTrain);
	           classifier2.buildClassifier(instancesTrain);
	           classifier3.buildClassifier(instancesTrain);
	          
	           Evaluation eval = new Evaluation(instancesTrain);
	           
//	           eval.crossValidateModel(classifier4, instancesTrain, 10, new Random(1));
//	           System.out.println(eval.errorRate());
	           //eval.crossValidateModel(classifier1, instancesTrain, 10, new Random(1));
	           //System.out.println(1.0 - eval.errorRate());
	           
	           eval.crossValidateModel(classifier2, instancesTrain, 10, new Random(1));
	           System.out.println(eval.toSummaryString());
	           System.out.println(eval.toMatrixString());
	           System.out.println(eval.toClassDetailsString());
	           System.out.println(1.0 - eval.errorRate());
	           
	           eval.crossValidateModel(classifier3, instancesTrain, 10, new Random(1));
	           System.out.println(1.0 - eval.errorRate());
	           
	           
//	           eval.evaluateModel(classifier4, instancesTest);
//	           System.out.println(eval.errorRate());
//	           eval.evaluateModel(classifier1, instancesTest);
//	           System.out.println(eval.errorRate());
//	           eval.evaluateModel(classifier2, instancesTest);
//	           System.out.println(eval.errorRate());
//	           eval.evaluateModel(classifier3, instancesTest);
//	           System.out.println(eval.errorRate());
	          
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
			*/
	    }
	
	private static void testClassifierSex() {
		Instances instancesTrain = SinaUserProfile.getInstancesBySex(); // 读入训练文件
        System.out.println(instancesTrain.numInstances());
//        instancesTrain = WekaUtil.addExtraInstance(instancesTrain);
        WekaUtil.generateArffFile(instancesTrain, "/Users/chenhuan001/Desktop/SinaHelpTvProfile/SinaData/SinaInstancesSex.arff");
        WekaUtil.generateCSVFile(instancesTrain, "/Users/chenhuan001/Desktop/SinaHelpTvProfile/SinaData/SinaInstancesSex.csv");
	}
	
	private static void testClassifierAge() {
		SinaUser.AGE_SPLIT = 1981;
		Instances instancesTrain = SinaUserProfile.getInstancesByAge(); // 读入训练文件
		System.out.println(instancesTrain.numInstances());
		int zero_num = 0;
		for (int i = 0; i < instancesTrain.numInstances(); i++) {
			Instance inst = instancesTrain.get(i);
			if (inst.classValue() == 0) zero_num++;
		}
		System.out.println("0 : " + zero_num);
		WekaUtil.generateArffFile(instancesTrain, "/Users/chenhuan001/Desktop/SinaHelpTvProfile/SinaData/SinaInstancesAge.arff");
        WekaUtil.generateCSVFile(instancesTrain, "/Users/chenhuan001/Desktop/SinaHelpTvProfile/SinaData/SinaInstancesAge.csv");
	}
	
	private static void multi_test_age() {
		// TODO Auto-generated method stub
		for (int i = 15; i <= 40; i++) {
			int test_time = 5;
			double sum = 0;
			SinaUser.AGE_SPLIT = 2017 - 15;
			for (int j = 0; j < test_time; j++) {
				testClassifierAge();
				try {
					Process proc = Runtime.getRuntime().exec(""
							+ "python /Users/chenhuan001/PycharmProjects/First"
							+ "/main.py"
							);
					InputStreamReader stdin = new InputStreamReader(proc.getInputStream());
					LineNumberReader input = new LineNumberReader(stdin);
					String line;
					while ((line = input.readLine()) != null) {
						System.out.println(line);
					}
					LogUtil.debug(line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) throws Exception {
		testClassifier();
		testClassifierAge();
		testClassifierSex();
		
//		multi_test_age();		
		/*
		DataSource source = new DataSource("/Users/chenhuan001/Desktop/date1.csv");
		Instances ins = source.getDataSet();
	    ArffSaver saver = new ArffSaver();
	    saver.setInstances(ins);
	    saver.setFile(new File("save.arff"));
	    //Instances tmp_ins = new Instances(arg0, arg1, arg2);
	  //saver.setDestination(new File(args[1]));
	  saver.writeBatch();
	  /*
	  Classifier m_classifier = new J48();
	  File inputFile = new File("/Users/chenhuan001/Desktop/weka-3-8-0/data/cpu.with.vendor.arff");//训练语料文件
	  ArffLoader atf = new ArffLoader(); 
	  atf.setFile(inputFile);
	  Instances instancesTrain = atf.getDataSet(); // 读入训练文件    
	  inputFile = new File("/Users/chenhuan001/Desktop/weka-3-8-0/data/cpu.with.vendor.arff");//测试语料文件
	  atf.setFile(inputFile);          
	  Instances instancesTest = atf.getDataSet(); // 读入测试文件
	  instancesTest.setClassIndex(0); //设置分类属性所在行号（第一行为0号），instancesTest.numAttributes()可以取得属性总数
	  double sum = instancesTest.numInstances(),//测试语料实例数
	  right = 0.0f;
	  instancesTrain.setClassIndex(0);
	j
	  m_classifier.buildClassifier(instancesTrain); //训练            
	  for(int  i = 0;i<sum;i++)//测试分类结果
	  {
	      if(m_classifier.classifyInstance(instancesTest.instance(i))==instancesTest.instance(i).classValue())//如果预测值和答案值相等（测试语料中的分类列提供的须为正确答案，结果才有意义）
	      {
	        right++;//正确值加1
	      }
	  }
	  System.out.println("J48 classification precision:"+(right/sum));
	*/
		
	  }




	
}