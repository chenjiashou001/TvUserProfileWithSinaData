package user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tvprograms.Program;

/**
 * 基础用户类
 * @author chenhuan001
 *
 */
public class IUser {
	String user_id = null;
	List<String> care_programs = null;
	
	List<String> train_care_programs = null;//测试集
	List<String> test_care_programs = null;//训练集
	
	
	void splitTrainAndTest() {
		if (care_programs == null) {
			return ;
		}
		if (care_programs.size() == 0) {
			return ;
		}
		train_care_programs = new ArrayList<String>();
		test_care_programs = new ArrayList<String>();
		List<Integer> index = new ArrayList<Integer>();
		for (int i = 0; i < care_programs.size(); i++) {
			index.add(i);
		}
		Collections.shuffle(index);
		int test_size = Math.max(1, (int)(index.size() * 0.2));
		for (int i = 0; i < test_size; i++) {
			test_care_programs.add(care_programs.get(index.get(i)));
		}
		for (int i = test_size; i < care_programs.size(); i++) {
			train_care_programs.add(care_programs.get(index.get(i)));
		}
	}
	
	public List<String> getTrain_care_programs() {
		return train_care_programs;
	}
	public void setTrain_care_programs(List<String> train_care_programs) {
		this.train_care_programs = train_care_programs;
	}
	public List<String> getTest_care_programs() {
		return test_care_programs;
	}
	public void setTest_care_programs(List<String> test_care_programs) {
		this.test_care_programs = test_care_programs;
	}
	public String getUser_id() {
		return user_id;
	}
	public List<String> getCare_programs() {
		return care_programs;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public void setCare_programs(List<String> care_programs) {
		this.care_programs = care_programs;
		splitTrainAndTest();
	}
	
}
