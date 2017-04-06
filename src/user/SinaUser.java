package user;

import java.util.ArrayList;

import org.bson.Document;

import tags.TagsUtil;
import tools.ComUtil;
import tools.LogUtil;
import tvprograms.Program;

public class SinaUser extends IUser {
	final String SPLITFLAG = "###";
	final int MAX_FANS_NUM = 10000;//粉丝数超过这个，则为非正常用户
	public static int AGE_SPLIT = 1981;
	String user_name;
	String user_summary;
	
	String residence;
	String birthday;
	String age;
	String register_time;
	
	String education_info;
	
	ArrayList<String> tags;
	ArrayList<String> useful_tags;//经过清理的tags
	String level;
	int sex;
	
	int fans_num;
	int weibo_num;
	int attention_num;
	
	boolean is_master;//是否为达人
	ArrayList<String> master_tags;//达人属性
	
	public SinaUser(Document doc_sina_user) {
		init();
		docToSinaUser(doc_sina_user);
	}
	
	public void docToSinaUser(Document userdoc) {
		user_id = userdoc.getString("id");
		user_name = userdoc.getString("name");
		user_summary = userdoc.getString("summary");
		residence = userdoc.getString("residence");
		birthday = userdoc.getString("birthday");
		//一下小处理
		birthday = ComUtil.str_trim_beg(birthday);
		age = getAgeByBirthday(birthday);
		
		//System.out.println(birthday + "," + age);
		
		sex = userdoc.getInteger("sex", -1);
		level = userdoc.getString("level");
		fans_num = userdoc.getInteger("fans_num", -1);
		weibo_num = userdoc.getInteger("weibo_num", -1);
		attention_num = userdoc.getInteger("attention_num", -1);
		String str_tags = userdoc.getString("tags");
		String [] tmp_tags = str_tags.split(SPLITFLAG);
		for (int i = 0; i < tmp_tags.length; i++) {
			if (tmp_tags[i].equals("")) {
				continue;
			}
			tags.add(tmp_tags[i]);
		}
		String str_care_programs = userdoc.getString("care_programs");
		if (str_care_programs != null) {
			String[] tmp_care_programs = str_care_programs.split(SPLITFLAG);
			for (int i = 0; i < tmp_care_programs.length; i++) {
				if (tmp_care_programs[i].equals("")) {
					continue;
				}
				care_programs.add(tmp_care_programs[i]);
			}
		}
		getUsefulTags();
		
	}

	private String getAgeByBirthday(String bsd) {
		if (bsd == null) {
			return null;
		}
		if (bsd.equals("") || bsd.length() < 4) {
			return null;
		}
		int year = 0;
		
		for (int i = 0; i < 4; i++) {
			char char_num = bsd.charAt(i);
			if (!ComUtil.charIsNumber(char_num)) {
				return null;//前四位不全为数字
			}
			year = year * 10 + (char_num - '0');
		}
		//LogUtil.debug(bsd + " : " + year);
		if (year < 1950 || year > 2010) {
			return null;
		}
		
//		if (year < 1980) {
//			return "0";
//		} else if (year < 1990) {
//			return "1";
//		} else if (year  < 2000) {
//			return "2";
//		} else {
//			return "3";
//		}
		//75 0.594012
		//80 0.640
		//82 0.658028
		//83 0.649401
		//84 0.672408
		//85 0.691943
		//86 0.653243
		//87 0.634515
		//90 0.6513
		//95 0.6448
		//00 0.664273
		//
		if (year < AGE_SPLIT) {
			return "0";
		} else {
			return "1"; 
		}
	}

	/**
	 * 处理tags，得到useful_tags 
	 */
	private void getUsefulTags() {
		if (useful_tags != null) {
			return ;
		}
		useful_tags = new ArrayList<String>();
		for (int i = 0; i < tags.size(); i++) {
			String tag = tags.get(i);
			String useful_tag = TagsUtil.getUsefulTagByTag(tag);
			if (null != useful_tag) {
				useful_tags.add(useful_tag);
			}
		}
	}
	
	/**
	 * 判断是不是一个可用的user
	 * @return
	 */
	public boolean isGoodSinaUser() {
		if (fans_num > MAX_FANS_NUM || useful_tags.size() == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 将user转换为document
	 * @return
	 */
	public Document getUserDoc(String come_from) {
		Document user_doc = new Document();
		user_doc.append("come_from", come_from);
		user_doc.append("id", user_id);
		user_doc.append("name", user_name);
		user_doc.append("summary", user_summary);
		user_doc.append("residence", residence);
		user_doc.append("birthday", birthday);
		//user_doc.append("education_info", education_info);
		user_doc.append("level", level);
		user_doc.append("sex", sex);
		user_doc.append("fans_num", fans_num);
		user_doc.append("weibo_num", weibo_num);
		user_doc.append("attention_num", attention_num);
		String str_tags = "";
		for (int i = 0; i < tags.size(); i++) {
			str_tags += tags.get(i) + SPLITFLAG;
		}
		user_doc.append("tags", str_tags);
		String str_care_programs = "";
		for (int i = 0; i < care_programs.size(); i++) {
			str_care_programs += care_programs.get(i) + SPLITFLAG;
		}
		user_doc.append("care_programs", str_care_programs);
		return user_doc;
	}
	private void init() {
		tags = new ArrayList<String>();		
		care_programs = new ArrayList<String>();
		residence = null;
		birthday = null;
		age = null;
		register_time = null;
		sex = -1;
		is_master = false;
	}
	public SinaUser() {
		init();
	}

	public String getSPLITFLAG() {
		return SPLITFLAG;
	}

	public String getUser_name() {
		return user_name;
	}

	public String getUser_summary() {
		return user_summary;
	}

	public String getResidence() {
		return residence;
	}

	public String getBirthday() {
		return birthday;
	}

	public String getRegister_time() {
		return register_time;
	}

	public String getEducation_info() {
		return education_info;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public ArrayList<String> getUseful_tags() {
		getUsefulTags();
		return useful_tags;
	}

	public String getLevel() {
		return level;
	}

	public int getSex() {
		return sex;
	}

	public int getFans_num() {
		return fans_num;
	}

	public int getWeibo_num() {
		return weibo_num;
	}

	public int getAttention_num() {
		return attention_num;
	}

	public boolean isIs_master() {
		return is_master;
	}

	public ArrayList<String> getMaster_tags() {
		return master_tags;
	}

	public boolean isGoodSexSinaUser() {
		if (fans_num > MAX_FANS_NUM || -1 == sex) {
			return false;
		}
//		return isGoodSinaUser();
		return true;
	}

	public String getAge() {
		return age;
	}

	public boolean isGoodAgeSinaUser() {
		if (fans_num > MAX_FANS_NUM || null == age) {
			return false;
		}
		return true;
	}
	
	
}
