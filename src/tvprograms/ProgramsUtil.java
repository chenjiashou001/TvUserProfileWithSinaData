package tvprograms;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import tags.TagsUtil;
import tools.ComUtil;
import tools.FileUtil;
import tools.LogUtil;
import user.SinaUser;
import userprofile.SinaUserProfile;

public class ProgramsUtil {
	public static final String PROGRAM_FILE_PATH = "Data/program_info.txt";
	private static final String PATH_OF_EPG_TV_INFO = "Data/EpgTvInfo.txt";
	
	/*
	 * format 	: alpha * (K1 + K2 * beta)
	 * alpha  	: positive different rate -> program_one_tag_rate - average_one_tag_rate 
	 * beta	   	: program one tag rate -> program_one_tag_cnt / all_one_tag_cnt  
	 */
	private static final double K1 = 1, K2 = 1;//
	private static final int MAX_PROGRAM_TAG_CNT = 3;
	private static final double MIN_PROGRAM_TAGS_CNT = 20;
	private static final double SEX_SPLIT_RATE = 0.1;
	private static final double AGE_SPLIT_RATE = 0.1;// > === older
	
	private static final int CHOICE_FIRST_PROGRAM = 120;//选择前多少名的电视
	
	private static List<Program> _programs = null;
	private static Map<String, Integer> _map_name_id = null;
	private static int _programs_size = -1;
	private static String [] _BLACK_TV_PROGRAM_NAME= {"雪豹", "奋斗", "口述", "非洲", "秦始皇", "爱情公寓", 
			"生命", "一线", "生活圈", "纪录片", "悬崖", "春节", "见证", "功夫", "疯猴", "剧场", "再见", "等着我",
			"黑猩猩", "专题", "过年", "夕阳红", "来玩吧", "传家", "传奇", "乡土", "经典剧场", "过年啦", 
			"天天健康", null};
	
	public static int getIdByTvProgramName(String program_name) {
		init();
		if (_map_name_id.containsKey(program_name)) {
			return _map_name_id.get(program_name);
		} else {
			return -1;
		}
	}
	
	public static int get_programs_size() {
		init();
		return _programs_size;
	}

	private static void loadProgramsByFile(String path) {
		if (-1 != _programs_size) {//已经加载过就不管了
			return;
		}
		_programs = new ArrayList<Program>();
		_map_name_id = new HashMap<String, Integer>();
		List<Document> doc_programs = FileUtil.readDocsFromFile(path);
		int program_index = 0;
		for (int i = 0; i < doc_programs.size(); i++) {
			Document doc_program = doc_programs.get(i);
			Program program = new Program(doc_program);
			String tv_program_name = program.getTv_name();  
			if (ComUtil.isStringInArray(tv_program_name, _BLACK_TV_PROGRAM_NAME)) {
				continue;
			}
			//将document中的属性全部加载到class program中
			
			program.setId(program_index);
			_map_name_id.put(tv_program_name, program_index);
			program_index++;
			_programs.add(program);
		}
		_programs_size = program_index;
		//保证之前的调用loadProgramsByFile
		loadEpgTagsByFile(PATH_OF_EPG_TV_INFO);
		LogUtil.info("Program_info load 完成");
	}
	
	public static void initTags() {
		init();
		getTagsByUserProfile();
	}
	
	private static void getTagsByUserProfile() {
		List<int []> programs_tags_cnt = new ArrayList<int[]>() ;
		List<Integer> sex_programs_cnt = new ArrayList<Integer>();//每个节目中，有多少男性观众
		List<Integer> age_programs_cnt = new ArrayList<Integer>();//每个节目重，有多少0（年长）
		List<Integer> sex_programs_all_user = new ArrayList<Integer>();// 每个节目,有sex，的所有观众个数
		List<Integer> age_programs_all_user = new ArrayList<Integer>();// 每个节目，有age的所有观众
		int []all_tags_cnt = new int[TagsUtil.getTagsSize()];
		int sex_1 = 0, sex_0 = 0;
		int age_0 = 0, age_1 = 0;
		List<SinaUser> sinausers = SinaUserProfile.getSinaUsers();
		for (int i = 0; i < _programs_size; i++) {
			int[] program_tags_cnt = new int[TagsUtil.getTagsSize()];
			programs_tags_cnt.add(program_tags_cnt);
			sex_programs_cnt.add(0);
			sex_programs_all_user.add(0);
			age_programs_cnt.add(0);
			age_programs_all_user.add(0);
		}
		
		for (SinaUser user : sinausers) {
//			if (user.getCare_programs().contains("昆仑决")) {
//				System.out.println(user.getTags().toString());
//			}
			if (user.getSex() == 0) {
				sex_0++;//女
			} else if (user.getSex() == 1){
				sex_1++;//男
			}
			
			if (user.getAge() != null) {
				if (user.getAge().equals("0")) {
					age_0++;
				} else if (user.getAge().equals("1")) {
					age_1++;
				}
			}
			
 			List<String> tags = user.getUseful_tags();
			List<Integer> tag_ids = new ArrayList<Integer>();
			for (String tag : tags) {
//				System.out.println(tag);
				tag_ids.add(TagsUtil.getIdByTagName(tag));
				all_tags_cnt[TagsUtil.getIdByTagName(tag)]++;
			}
			
			for (String prog_name : user.getCare_programs()) {
				int prog_id = getIdByTvProgramName(prog_name);
				if (prog_id == -1) {
					continue;
				}
				for (Integer tag_id : tag_ids) {
					programs_tags_cnt.get(prog_id)[tag_id]++;
				}
				
				if (user.getSex() != -1) {
					sex_programs_all_user.set(prog_id, sex_programs_all_user.get(prog_id) + 1);
					sex_programs_cnt.set(prog_id, sex_programs_cnt.get(prog_id) + user.getSex());
				}
				if (user.getAge() != null) {
					age_programs_all_user.set(prog_id, age_programs_all_user.get(prog_id) + 1);
					if (user.getAge().equals("0")) {
						age_programs_cnt.set(prog_id, age_programs_cnt.get(prog_id) + 1);
					}
				}
			}
		}
		
		
		for (int i = 0; i < _programs_size; i++) {
			double []result = new double[TagsUtil.getTagsSize()];
			double p_sum = 0;
			int []program_tags_cnt = programs_tags_cnt.get(i);
			for (int j = 0; j < TagsUtil.getTagsSize(); j++) {
				p_sum += program_tags_cnt[j]; 
			}
			Map<Integer, Double> map_tagid_result = new HashMap<Integer, Double> ();
			for (int j = 0; j < TagsUtil.getTagsSize(); j++) {
				result[j] = cal_tag_importan_point((program_tags_cnt[j] / p_sum) - TagsUtil.P_TAG[j], 
												   program_tags_cnt[j] / all_tags_cnt[j]);
				map_tagid_result.put(j, result[j]);
			}
			
			//sort
			List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>();
			list.addAll(map_tagid_result.entrySet());
			if (list.size()  == 0) {
				return ;
			}
			KeyComparator cmp = new KeyComparator();
			Collections.sort(list, cmp);
			List<String> tmp_prog_tags = new ArrayList<String>();
			for (Iterator<Map.Entry<Integer, Double>> it = list.iterator(); 
				    it.hasNext();) {
					Map.Entry<Integer, Double> now = it.next();
					if (now.getValue() < 0.01 || now.getKey() == 0) {//删除搞笑幽默
						continue;
					}
					tmp_prog_tags.add(TagsUtil._TAG_NAME[now.getKey()]);
					if (tmp_prog_tags.size() >= MAX_PROGRAM_TAG_CNT) {//选择最大的
						break;
					}
				}
			//get sex tag
			double sex_diff = ((double)sex_programs_cnt.get(i) / (sex_programs_all_user.get(i)))
								- ((double)sex_1 / (sex_0 + sex_1));
			
			if (sex_diff > SEX_SPLIT_RATE) {
				tmp_prog_tags.add("男");
			} else if (sex_diff < -SEX_SPLIT_RATE) {
				tmp_prog_tags.add("女");
			}
			
			//get age tag
			double age_diff = ((double)age_programs_cnt.get(i) / (age_programs_all_user.get(i) + 1)) 
								- ((double)age_0 / (age_0 + age_1 + 1));
			if (age_diff > AGE_SPLIT_RATE) {
				tmp_prog_tags.add("older");
			}
			System.out.println("age_diff: " + age_diff);
			
			if (p_sum < MIN_PROGRAM_TAGS_CNT) {
				tmp_prog_tags.clear();
			}
			_programs.get(i).setUser_profile_tags(tmp_prog_tags);
			
			System.out.println(ProgramsUtil.getTvProgramNameById(i));
			System.out.println("sex_diff:" + sex_diff);
//			System.out.println(sex_programs_cnt.get(i) + "|||" + programs_all_user.get(i));
//			System.out.println(sex_1 + "|||" + sex_0);
			for (int j = 0; j < TagsUtil.getTagsSize(); j++) {
				System.out.print(TagsUtil._TAG_NAME[j] + ":" + programs_tags_cnt.get(i)[j] + ",");
			}
			System.out.println();
			DecimalFormat df = new DecimalFormat("######0.0000");
			for (int j = 0; j < TagsUtil.getTagsSize(); j++) {
				System.out.print(TagsUtil._TAG_NAME[j] + ":" + df.format(result[j]) + ",");
			}
			System.out.println();
			System.out.println(tmp_prog_tags);
			if (i > CHOICE_FIRST_PROGRAM) {
				break;
			}
		}
		
	}

	
	private static double cal_tag_importan_point(double alpha, int beta) {
		return alpha * (K1 + K2 * beta);
	}

	public static void init() {
		if (-1 == _programs_size)  {
			loadProgramsByFile(PROGRAM_FILE_PATH);
		}
	}
	
	public static void loadEpgTagsByFile(String path) {
		List<String> lines = FileUtil.readLogByList(path);
		Map<String, String> map_name_tags = new HashMap<String, String>();
		for (String line : lines) {
			String name = line.split("\t")[0];
			String tags = line.split("\t")[1];
			map_name_tags.put(name, tags);
		}
		
		int p_index = 0;
		for (Program program : _programs) {
			String name = program.getTv_name();
			if (map_name_tags.containsKey(name)) {
				String str_tags = map_name_tags.get(name);
				str_tags = ComUtil.str_trim_beg(str_tags);
				String[] tags = str_tags.split(" ");//' '
				List<String> tmp_epg_tags = new ArrayList<String>();
				for (int j = 0; j < tags.length; j++) {
					tmp_epg_tags.add(ComUtil.str_trim_beg(tags[j]));
				}
				program.setEpg_tags(tmp_epg_tags);
				//System.out.println(name + " : " + tmp_epg_tags.toString());
				//PrintClass.outObjPropertyString(program);
			}
			if (p_index > CHOICE_FIRST_PROGRAM) {
				break;
			}
			p_index++;
		}
	}
	
	public static String getTvProgramNameById(int id) {
		init();		
		return _programs.get(id).getTv_name();
	}
	
	public static List<Program> getProgramList() {
		init();
		return _programs;
	}
	
	public static Program getProgramById(int id) {
		init();
		return _programs.get(id);
	}
	
	public static Program getProgramByTvProgramName(String name) {
		init();
		return _programs.get(_map_name_id.get(name));
	}
	
	public static void main(String[] args) {
		//TEST
		init();
		getTagsByUserProfile();
//		int size = 0;
//		System.out.println(size = get_programs_size());
//		for (int i = 0; i < size; i++) {
//			Program program = getProgramById(i);
//			System.out.println(program.tv_name + " " + program.getTv_watch_time());
//			//LogUtil.debug(getTvProgramNameById(i));
//			//LogUtil.error(getProgramById(i));
//			//break;
//			//System.out.println(program.getId());
//			//System.out.println();
//			//PrintClass.print(program);
//			
//			//System.out.println(PrintClass.outObjPropertyString(program));
//			//LogUtil.debug(PrintClass.outObjPropertyString(program));
//			//break;
//		}
	}
}

class KeyComparator implements
Comparator<Map.Entry<Integer, Double>> {
	public int compare(Map.Entry<Integer, Double> m,
	    Map.Entry<Integer, Double> n) {
	return n.getValue().compareTo(m.getValue());
}
}
