package tags;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import tools.FileUtil;
import tools.LogUtil;
import user.SinaUser;

public class TagsUtil {
	public static String _TAG_NAME[] = {
		"搞笑幽默",//	id = 0  84%
		"电影",//		id = 1	85%
		"文艺",//		id = 2	74%
		"音乐",//		id = 3	73%
		"美食",//		id = 4	70%
		"军事",//		id = 5	95%
		"财经",//		id = 6	94%
		"动漫",// 		id = 7	91%
		"体育",//		id = 8	92%
		"明星",//		id = 9	76%
		null
	};//年龄段待定,性别待定,因为都是多选
	
	public static double P_TAG[] = {
			0.11196911196911197,
			0.07094594594594594,
			0.15717503217503218,
			0.18227155727155728,
			0.2121943371943372,
			0.013996138996138996,
			0.023809523809523808,
			0.057593307593307594,
			0.04858429858429859,
			0.12146074646074646,
	};
	
//	public static int NUM_TAG[] = {
//			696,
//			441,
//			977,
//			1133,
//			1319,
//			87,
//			148,
//			358,
//			302,
//			755,
//	};
//	public static final double ALL_TAG_NUM = 5688;
	
	private static String [][] _SIMILAR_TAG_ARRAYS = {
		{"搞笑幽默", "搞笑", "幽默", "笑话", "爱笑", null},
		{"电影", "视频电影", "爱电影", "电影控", "看电影", "电影迷", "电影爱好者", null},
		{"文艺", "读书分享", "读书", "小说", "看书", "艺术", "文学", "写作", "历史", "文化", "画画", null},
		{"音乐", "视频音乐", "听歌", "唱歌", "爱音乐", "音乐爱好者", "喜欢音乐", "音乐控", "听音乐", "喜欢听歌" , 
				"音乐迷", null},
		{"美食", "吃货", "吃", "美食爱好者", "吃货一枚", "爱美食", "美食控", "爱吃", null},
		{"军事", "军事天地", "战争", "抗战", null},
		{"财经", "投资理财", "财经资讯", "股票", "金融", "经济", "投资", null},
		{"动漫", "游戏动漫", "漫画", "柯南", "海贼王", null},
		{"体育", "体育咨询", "运动", "足球", "篮球", "羽毛球", "健身", "游泳", "NBA", null},
		{"明星", "名人明星", "八卦杂谈", "八卦", "综艺娱乐", "周杰伦", "追星", "王俊凯", "韩庚", "成龙", "陈学冬", 
				"巩俐", "李连杰", "李小龙","章子怡", "周润发", "井柏然", "马天宇", "蒋劲夫", "刘诗诗", 
				"迪丽热巴", "杨幂", "鹿晗", "赵丽颖", "郑爽", "薛之谦", "angelababy", "杨洋", "唐嫣", 
				"胡歌", "李易峰", "张杰", "周星驰", "范冰冰", "吴亦凡", "权志龙", "刘亦菲", "TFBOYS",
				"易烊千玺", "王源", "李宇春", null},
		null
	};
	
	private static int _tags_size = -1;
	private static List<Set<String>> _similar_tag_sets = null;
	public static int getTagsSize() {
		if (_tags_size != -1) {
			return _tags_size;
		}
		int _size = 0;
		for (int i = 0; _TAG_NAME[i] != null; i++) {
			_size++;
		}
		_tags_size = _size;
		return _tags_size;
	}
	
	public static int getIdByTagName(String name) {
		for (int i = 0; _TAG_NAME[i] != null; i++) {
			if (name.equals(_TAG_NAME[i])) {
				return i;
			}
		}
		return -1;
	}
	
	public static String getTagNameById(int tag_id) {
		if (tag_id >= getTagsSize()) {
			return null;//异常
		}
		return _TAG_NAME[tag_id];
	}
	
	public static boolean isSinaUserTagBelongTAG(String sina_user_tag, int TAG_ID) {
		initSimilarTagSets();
		return _similar_tag_sets.get(TAG_ID).contains(sina_user_tag);
	}

	private static void initSimilarTagSets() {
		if (null != _similar_tag_sets) {
			return ;
		}
		_similar_tag_sets = new ArrayList<Set<String>>();
		for (int i = 0; _TAG_NAME[i] != null; i++) {
			String SIMILAR_SET_ARRAY[] = _SIMILAR_TAG_ARRAYS[i];
			Set<String> similar_tag_set = new HashSet<String>();
			for (int j = 0; SIMILAR_SET_ARRAY[j] != null; j++) {
				similar_tag_set.add(SIMILAR_SET_ARRAY[j]);
			}
			_similar_tag_sets.add(similar_tag_set);
		}
		int _size = 0;
		for (int i = 0; _TAG_NAME[i] != null; i++) {
			_size++;
		}
		_tags_size = _size;
	}
	
	/**
	 * 将tag归入usefultag 
	 * 比如 将 篮球 －> 体育
	 * @param tag
	 * @return
	 */
	public static String getUsefulTagByTag(String tag) {
		initSimilarTagSets();
		for (int i = 0; i < getTagsSize(); i++) {
			if (isSinaUserTagBelongTAG(tag, i)) {
				return _TAG_NAME[i];
			}
		}
		return null;
	}
	
	/**
	 * 得到随机用户的标签分布情况
	 */
	private static void getRandomTags() {
		//"Data/random_user_info.txt";
		//
		String path1 = "Data/user_program_data.txt";
		String path2 = "Data/random_user_info.txt";
		initSimilarTagSets();
		List<Integer> usefultag_cnt = new ArrayList<Integer>();
		for (int i = 0; i < _tags_size; i++) {
			usefultag_cnt.add(0);
		}
		List<Document> rand_user_docs = FileUtil.readDocsFromFile(path2);
		int user_cnt = 0;
		double all_tag_cnt = 0;
		Set<String>  sina_user_mark = new HashSet<String>();
		for (Document doc : rand_user_docs) {
			SinaUser user = new SinaUser(doc);
			if (user.getFans_num() < 3000 && user.getTags().size() != 0 
					&& sina_user_mark.contains(user.getUser_id()) == false) {
				user_cnt++;
				sina_user_mark.add(user.getUser_id());
				for(String tag : user.getTags()) {
					String useful_tag = getUsefulTagByTag(tag);
					if (null == useful_tag) {
						continue;
					}
					int tmp_num = usefultag_cnt.get(getIdByTagName(useful_tag));
					usefultag_cnt.set(getIdByTagName(useful_tag), tmp_num + 1);
					all_tag_cnt++;
				}
			}
		}
		System.out.println(user_cnt);
		int id = 0;
		StringBuffer sb = new StringBuffer();
		for (Integer a : usefultag_cnt) {
			//sb.append("\n" + _TAG_NAME[id] + " : " + a/all_tag_cnt);
			System.out.println(a);
			id++;
		}
		//LogUtil.debug(sb.toString());
	}
	
	public static void main(String [] args) {
//		System.out.println(isSinaUserTagBelongTAG("搞笑", 0));
//		System.out.println(isSinaUserTagBelongTAG("搞笑幽默", 0));
//		System.out.println(isSinaUserTagBelongTAG("TFBOYS", 9));
		getRandomTags();
	}
}
