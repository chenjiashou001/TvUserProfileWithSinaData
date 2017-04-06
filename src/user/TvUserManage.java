package user;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tags.TagsUtil;
import tools.FileUtil;
import tvprograms.Program;
import tvprograms.ProgramsUtil;

public class TvUserManage {
	static final int MAX_EPG_TAG_CNT = 3;
	private static final double TAG_CHOICE_RATE = 0.7;//大于这个百分比的tag就选择
	private static final double AGE_TAG_CHOICE_RATE = 0.6;//
	static List<TvUser> tv_users = null;
	static String base_path_of_tv_user = "/Users/chenhuan001/Desktop/SinaHelpTvProfile/result/result_";
	static String path_of_tv_user_id = "/Users/chenhuan001/Desktop/SinaHelpTvProfile/TvData/TvUserName.txt";
	
	static String []possible_tags = {
				"0","1","2","3","4","5","6","7","8","9","Sex","Age"
			};
	
	static List<Double> limit_of_tags; 
	static List<List<Double>> user_p_tags = null;
	
	public static void main(String[] args) {
		init();
	}
	
	public static List<TvUser> getTv_users() {
		init();
		return tv_users;
	}

	private static void init() {
		if (null != tv_users) {
			return ;
		}
		List<String> tv_user_ids = FileUtil.readLogByList(path_of_tv_user_id);
		tv_users = new ArrayList<TvUser>();
		user_p_tags = new ArrayList<List<Double>>();
		for (String tv_user_id : tv_user_ids) {
			List<Double> user_p_tag = new ArrayList<Double>();
			user_p_tags.add(user_p_tag);
			TvUser tv_user = new TvUser();
			tv_user.setUser_id(tv_user_id);
			tv_users.add(tv_user);
		}
		limit_of_tags = new ArrayList<Double>();
		double sex_up_limit = 0;
		double sex_down_limit = 0;
		double age_limit = 0;
		for (int i = 0; i < possible_tags.length; i++) {
			List<String> strs = FileUtil.readLogByList(base_path_of_tv_user + possible_tags[i] + ".txt");
			for (int j = 1; j < strs.size(); j++) {
				user_p_tags.get(j - 1).add(Double.parseDouble(strs.get(j)));
			}
			double tmp_limit = 0;
			// get_limit_of_tag  从小到大 排序
			limit_of_tags.add(tmp_limit = get_limit_of_tag(strs, i, TAG_CHOICE_RATE));
			//System.out.println(possible_tags[i] + ":" + tmp_debug);
			if (i == 10) {//sex
				sex_up_limit = tmp_limit;
				sex_down_limit = get_limit_of_tag(strs, i, 1 - TAG_CHOICE_RATE);
			}
			if (i == 11) {//age
				age_limit = get_limit_of_tag(strs, i, 1 - AGE_TAG_CHOICE_RATE);//
			}
		}
		
		
		//get user care programs
		List<String> str_user_care_programs = FileUtil.readLogByList("/Users/chenhuan001/Desktop/SinaHelpTvProfile/TvData/TvUserData.csv"); 
		
		for (int i = 1; i < str_user_care_programs.size(); i++) {
			String str= str_user_care_programs.get(i);
			List<String> care_programs = new ArrayList<String>();
			String []pros = str.split(",");
			for (int j = 0; j < pros.length; j++) {
				if (pros[j].equals("1")) {
					care_programs.add(ProgramsUtil.getTvProgramNameById(j));
				}
			}
			tv_users.get(i - 1).setCare_programs(care_programs);
		}
		
		double allcnt = 0;
		//用策略选出标签。
		for (int i = 0; i < tv_users.size(); i++) {
			List<Double> user_p_tag = user_p_tags.get(i);
			List<String> pred_tags = new ArrayList<String>();
			for (int j = user_p_tag.size() - 1; j >= 0; j--) {
				
				if (possible_tags[j].equals("Sex")) {
					double tag_p = user_p_tag.get(j);
					if (tag_p < sex_down_limit) {
						pred_tags.add("女");
					} else if (tag_p > sex_up_limit) {
						pred_tags.add("男");
					}
					continue;
				}
				
				if (possible_tags[j].equals("Age")) {
					double tag_p = user_p_tag.get(j);
					if (tag_p < age_limit) {//最不能为1的 那些数
						pred_tags.add("older");
					}
					continue;
				}
				
				if (user_p_tag.get(j) > limit_of_tags.get(j)) {
					
					if (possible_tags[j].equals("0")) {//幽默搞笑不处理
						continue;
					}
					
					pred_tags.add(TagsUtil._TAG_NAME[Integer.parseInt(possible_tags[j])]);
					//测试一下
					if (pred_tags.size() >= 5) {
						break;
					}
					
				}
			}
			tv_users.get(i).setPred_tags(pred_tags);
			allcnt += pred_tags.size();
		}
		
		initEgptags();
		dump(allcnt);
	}

	private static Double get_limit_of_tag(List<String> strs, int tag_index, double tagChoiceRate) {
		Collections.sort(strs);
		return Double.parseDouble(strs.get((int)(strs.size() * tagChoiceRate)));
	}

	private static void initEgptags() {
		Map<String, Integer> map_tag_num = new HashMap<String, Integer>();
		double allsum = 0;
		for (TvUser user : tv_users) {
			for (String program_name : user.getTrain_care_programs()) {
				for (String p_egp_tag : ProgramsUtil.getProgramByTvProgramName(program_name).getEpg_tags()) {
					allsum++;
					if (map_tag_num.containsKey(p_egp_tag)) {
						map_tag_num.put(p_egp_tag, map_tag_num.get(p_egp_tag) + 1);
					} else {
						map_tag_num.put(p_egp_tag, 1);
					}
				}
			}
 		}
		//System.out.println(map_tag_num.toString());
		for (TvUser user : tv_users) {
			Map<String, Double> map_user_tag_num = new HashMap<String, Double>();
			double user_sum = 0;
			for (String program_name : user.getTrain_care_programs()) {
				for (String p_egp_tag : ProgramsUtil.getProgramByTvProgramName(program_name).getEpg_tags()) {
					user_sum++;
					if (map_user_tag_num.containsKey(p_egp_tag)) {
						map_user_tag_num.put(p_egp_tag, map_user_tag_num.get(p_egp_tag) + 1);
					} else {
						map_user_tag_num.put(p_egp_tag, 1.0);
					}
				}
			}
			
			//TF-IDF
			for (String key : map_user_tag_num.keySet()) {  
				map_user_tag_num.put(key, (map_user_tag_num.get(key)/(user_sum + 1)) * Math.log((allsum / (map_tag_num.get(key) + 1))) );  
			}
			
			//sort
			List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>();
			list.addAll(map_user_tag_num.entrySet());
			if (list.size()  == 0) {
				return ;
			}
			KeyComparator cmp = new KeyComparator();
			Collections.sort(list, cmp);
			List<String> tmp_epg_tags = new ArrayList<String>();
			for (Iterator<Map.Entry<String, Double>> it = list.iterator(); 
				    it.hasNext();) {
					Map.Entry<String, Double> now = it.next();
					
					tmp_epg_tags.add(now.getKey());
					if (tmp_epg_tags.size() > MAX_EPG_TAG_CNT) {//选择最大的
						break;
					}
				}
			user.setEpg_tags(tmp_epg_tags);
		}
	}


	private static void dump(double allcnt) {
		System.out.println("平均的" + allcnt / tv_users.size());
		
		for (int i = 0; i < possible_tags.length; i++) {
			String str_prt = possible_tags[i];
			if (possible_tags[i].charAt(0) >= '0' && possible_tags[i].charAt(0) <= '9') {
				str_prt = TagsUtil._TAG_NAME[possible_tags[i].charAt(0) - '0'];
			}
			System.out.print(str_prt + "\t\t");
		}
		System.out.println();
		DecimalFormat df = new DecimalFormat("######0.0000");
		for (int i = 0; i  < tv_users.size(); i++) {
			System.out.println(tv_users.get(i).getUser_id());
			TvUser user = tv_users.get(i);
			for (String care_program : tv_users.get(i).getCare_programs()) {
				System.out.print(care_program + ",");
			}
			System.out.println();
//			System.out.println(user.getTest_care_programs());
//			System.out.println(user.getTrain_care_programs());
			List<Double> user_p_tag = user_p_tags.get(i);
			for (Double p_tag : user_p_tag) {
				System.out.print(df.format(p_tag) + "\t\t");
			}
			System.out.println();
			for (String pred_tag : tv_users.get(i).getPred_tags()) {
				System.out.print(pred_tag + " | ");
			}
			System.out.println();
			System.out.println("epg_tags: " + tv_users.get(i).getEpg_tags().toString());
			if (i > 100) {
				break;
			}
		}
	}

}

class KeyComparator implements
	Comparator<Map.Entry<String, Double>> {
		public int compare(Map.Entry<String, Double> m,
		    Map.Entry<String, Double> n) {
		return n.getValue().compareTo(m.getValue());
	}
}

