package tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tvprograms.ProgramsUtil;
import user.IUser;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;

public class WekaUtil {
	private static ArrayList<Attribute> _attributes = null;
	public static ArrayList<Attribute> getAttributes() {
		if (_attributes != null) {
			return _attributes;
		}
		_attributes = new ArrayList<>();
    	List<String> have_nohave = new ArrayList<String>(2);
    	have_nohave.add("0");
    	have_nohave.add("1");
        int attributes_size = ProgramsUtil.get_programs_size();
        for (int i = 0; i < attributes_size; i++) {
        	String attribute_name = ProgramsUtil.getTvProgramNameById(i);

        	_attributes.add(new Attribute(attribute_name, have_nohave));
        }
        _attributes.add(new Attribute("key_class", have_nohave));
        attributes_size++;
        return _attributes;
	}
	
	public static Instances getBaseInstancesByUsers(List<IUser> users, ArrayList<Attribute> attributes) {
        Instances instances = new Instances("base_instances", attributes, 0);
        instances.setClassIndex(instances.numAttributes() - 1);//设置最后一列为key_class
        for (int i = 0; i < users.size(); i++) {
        	Instance instance = new DenseInstance(attributes.size());
        	IUser user =  users.get(i);
        	//初始化
        	for (int j = 0; j< attributes.size() - 1; j++) {
        		instance.setValue(j, 0);
        	}
        	for (int j = 0; j < user.getCare_programs().size(); j++) {
        		String care_program = user.getCare_programs().get(j);
        		int program_id = ProgramsUtil.getIdByTvProgramName(care_program);
        		if (program_id != -1) {
        			instance.setValue(program_id, 1);
        		}
        	}
        	instances.add(instance);
        	
//        	if (i > 5) {
//        		break;
//        	}
        }
//        LogUtil.debug(instances.toString());
		return instances;
	}
	
	/**
     * generate weka dataSource file
     * @param instances weka Instances
     */
    public static void generateArffFile(Instances instances, String path) {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);
        try {
            saver.setFile(new File(path));
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void generateCSVFile(Instances instances, String path) {
        CSVSaver saver = new CSVSaver();
        saver.setInstances(instances);
        try {
            saver.setFile(new File(path));
            saver.writeBatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 返回一个新的instances，补充了正样例，且打乱顺序
     * @param instances
     */
    public static Instances addExtraInstance(Instances instances) {
    	ArrayList<Attribute> attributes = getAttributes();
		Instances new_insts = new Instances("base_instances", attributes, 0);
        instances.setClassIndex(instances.numAttributes() - 1);//设置最后一列为key_class
        ArrayList<Instance> array_insts = new ArrayList<Instance>();
        
    	for (int i = 0; i < instances.numInstances(); i++) {
    		Instance inst = instances.get(i);
    		if (inst.stringValue(inst.numAttributes() - 1).equals("1")) {
//    			System.out.println(inst.toString());
//    			break;
    			array_insts.add(inst);
    		}
    		array_insts.add(inst);
    	}
    	Collections.shuffle(array_insts);
    	for (int i = 0; i < array_insts.size(); i++) {
    		new_insts.add(array_insts.get(i));
    	}
    	return new_insts;
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
