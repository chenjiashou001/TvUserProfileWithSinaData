package analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tags.TagsUtil;
import tools.ComUtil;
import tools.LogUtil;
import tools.SimilarDegreeByCos;
import tvprograms.Program;
import tvprograms.ProgramsUtil;
import user.TvUser;
import user.TvUserManage;

/**
 * EPG实验
 * @author chenhuan001
 *
 */
public class RecommendExperiment {
	static int TOP_N = 0;
	static int MIN_WATCH_CNT = 10;
	static int AUC_CNT = 1000;
	static void beginExperiment(ISimilarCalWay cal_way) {
		List<TvUser> users = TvUserManage.getTv_users();
		double pred_num = 0;
		double right_pred = 0;
		double rand_pred_num = 0;
		double rand_right_pred = 0;
		double all_need_pred = 0;
		double all_pos_num = 0;
		int debug_cnt = 10;
		int user_cnt = 0;
		double sum_auc = 0;
		for (TvUser user : users) {
			if (user.getCare_programs().size() < MIN_WATCH_CNT) {
				continue;
			}
			user_cnt++;
			Map<String, Double> map_program_result = new HashMap<String, Double>();
			for (Program program : ProgramsUtil.getProgramList()) {
//				System.out.println(user.getEpg_tags());
//				System.out.println(program.getEpg_tags());
//				System.out.println(SimilarDegreeByCos.getSimilarDegree(user.getEpg_tags(), program.getEpg_tags()));
				//System.out.println(cal_way.calSimilar(user, program));
				map_program_result.put(program.getTv_name(), cal_way.calSimilar(user, program));
			}
			
//			if (user_cnt > 1) {break;} //debug
			sum_auc += getAuc(user, map_program_result);
			//sort
			List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>();
			list.addAll(map_program_result.entrySet());
			if (list.size()  == 0) {
				return ;
			}
			KeyComparator cmp = new KeyComparator();
			Collections.sort(list, cmp);
			List<String> pred_programs = new ArrayList<String>();
			List<String> rand_pred_programs = getRandPred(user);
			//System.out.println(user.getTest_care_programs().toString());
			all_need_pred += user.getTest_care_programs().size();
			all_pos_num += ProgramsUtil.get_programs_size() - user.getTrain_care_programs().size();
			for (Iterator<Map.Entry<String, Double>> it = list.iterator(); 
				    it.hasNext();) {
					Map.Entry<String, Double> now = it.next();
					if (user.getTrain_care_programs().contains(now.getKey())) {//去除重复的
						continue;
					}
					pred_programs.add(now.getKey());
					//System.out.println(now.getKey());
					//System.out.println(now.getValue());
					if (pred_programs.size() >= TOP_N) {//选择最大的
						break;
					}
				}
			
			pred_num += pred_programs.size();
			rand_pred_num += rand_pred_programs.size();
			List<String> test_programs = user.getTest_care_programs();
			// if-idf 
			for (String pred_program : pred_programs) {
				if (test_programs.contains(pred_program)) {
					right_pred++;
				}
			}
			//rand
			for (String rand_pred_program : rand_pred_programs) {
				if (test_programs.contains(rand_pred_program)) {
					rand_right_pred++;
				}
			}
			
			//if(debug_cnt-- < 0) {break;}
		}
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("TOP_N: " + TOP_N + ", MIN_WATCH_CNT: " + MIN_WATCH_CNT);
		sb.append("\n auc : " + sum_auc / user_cnt);
		sb.append("\nuser_cnt:" + user_cnt + "\nprecise :" + right_pred / pred_num + "\nrandom  :" + rand_right_pred / rand_pred_num);
		sb.append("\nall_precise:" + all_need_pred / all_pos_num);
		sb.append("\nrate:" + (right_pred / pred_num) / (all_need_pred / all_pos_num));
		LogUtil.debug(sb.toString());
//		System.out.println("user_cnt:" + user_cnt);
//		System.out.println("precise :" + right_pred / pred_num);
//		System.out.println("random  :" + rand_right_pred / rand_pred_num);
	}
	
	private static double getAuc(TvUser user, Map<String, Double> map_program_result) {
		List<String> uncare_program = new ArrayList<String>();
		for (Program pro : ProgramsUtil.getProgramList()) {
			if (!user.getCare_programs().contains(pro.getTv_name())) {
				uncare_program.add(pro.getTv_name());
			}
		}
		//System.out.println(uncare_program.toString());
//		System.out.println(map_program_result.toString());
//		System.out.println(user.getCare_programs());
//		System.out.println(uncare_program.size());
		double n1 = 0, n2 = 0;
		for (int i = 0; i < AUC_CNT; i++) {
			int rand_like = ComUtil.getRand(user.getTest_care_programs().size());
			int rand_unlike = ComUtil.getRand(uncare_program.size());
			String like_pro = user.getTest_care_programs().get(rand_like);
			String unlike_pro = uncare_program.get(rand_unlike);
			//System.out.println(like_pro + ":" + unlike_pro);
			double d_like = map_program_result.get(like_pro);
			double d_unlike =  map_program_result.get(unlike_pro);
			if (map_program_result.get(like_pro).compareTo(map_program_result.get(unlike_pro)) > 0 ) {
				n1 += 1.0;
			} else if (map_program_result.get(like_pro).equals(map_program_result.get(unlike_pro))) {
				n2 += 0.5;
			}
		}
		
//		System.out.println("auc : " + (n1 + n2) / AUC_CNT);
		return (n1 + n2) / AUC_CNT;
	}


	public static void main(String[] args) {
		ProgramsUtil.initTags();
		beginExperiment(new EpgSimilarCalWay());
		beginExperiment(new JustSina());
		beginExperiment(new SinaSimilarCalWay());
//		for (TOP_N = 1; TOP_N < 30; TOP_N++) {
//			for (MIN_WATCH_CNT = 10; MIN_WATCH_CNT < 30; MIN_WATCH_CNT++) {
//				epg_pred_and_evl(users);
//			}
//		}
		
	}

	private static List<String> getRandPred(TvUser user) {
		List<String> no_train_programs = new ArrayList<String>();
		for (Program program : ProgramsUtil.getProgramList()) {
			if (!user.getTrain_care_programs().contains(program.getTv_name())) {
				no_train_programs.add(program.getTv_name());
			}
		}
		Collections.shuffle(no_train_programs);
		List<String> rand_programs = new ArrayList<String>();
		for (String p_name : no_train_programs) {
			rand_programs.add(p_name);
			if (rand_programs.size() > TOP_N) {
				break;
			}
		}
		return rand_programs;
	}

}

class SinaSimilarCalWay implements ISimilarCalWay {

	@Override
	public double calSimilar(TvUser user, Program program) {
		//那么只需要在这里返回
		List<String> user_tags = new ArrayList<String>();
		if (user.getEpg_tags() != null) {
			user_tags.addAll(user.getEpg_tags());
		}
		for (String pre_tag : user.getPred_tags()) {
//			if (pre_tag.charAt(0) >= '0' && pre_tag.charAt(0) <= '9') {
//				String tag_name = TagsUtil._TAG_NAME[pre_tag.charAt(0) - '0'];
//				user_tags.add(tag_name);
//			}
			user_tags.add(pre_tag);
		}
		List<String> program_tags = new ArrayList<>();
		program_tags.addAll(program.getEpg_tags());
		program_tags.addAll(program.getUser_profile_tags());
		return SimilarDegreeByCos.getSimilarDegree(user_tags, program_tags);
//		System.out.println(user.getEpg_tags() + "," + user.getPred_tags());
//		System.out.println(program.getEpg_tags() + "," + program.getUser_profile_tags());
//		System.out.println(user_tags.toString());
//		System.out.println(program_tags.toString());
//		System.out.println("-----------------------------");
	}
	
}

class JustSina implements ISimilarCalWay {

	@Override
	public double calSimilar(TvUser user, Program program) {
		//那么只需要在这里返回
		List<String> user_tags = new ArrayList<String>();
		if (user.getEpg_tags() != null) {
//			user_tags.addAll(user.getEpg_tags());
		}
		for (String pre_tag : user.getPred_tags()) {
//			if (pre_tag.charAt(0) >= '0' && pre_tag.charAt(0) <= '9') {
//				String tag_name = TagsUtil._TAG_NAME[pre_tag.charAt(0) - '0'];
//				user_tags.add(tag_name);
//			}
			user_tags.add(pre_tag);
		}
		List<String> program_tags = new ArrayList<>();
//		program_tags.addAll(program.getEpg_tags());
		program_tags.addAll(program.getUser_profile_tags());
		return SimilarDegreeByCos.getSimilarDegree(user_tags, program_tags);
//		System.out.println(user.getEpg_tags() + "," + user.getPred_tags());
//		System.out.println(program.getEpg_tags() + "," + program.getUser_profile_tags());
//		System.out.println(user_tags.toString());
//		System.out.println(program_tags.toString());
//		System.out.println("-----------------------------");
	}
	
}

class EpgSimilarCalWay implements ISimilarCalWay {

	@Override
	public double calSimilar(TvUser user, Program program) {
		return SimilarDegreeByCos.getSimilarDegree(user.getEpg_tags(), program.getEpg_tags());
	}
	
}

class KeyComparator implements
Comparator<Map.Entry<String, Double>> {
	public int compare(Map.Entry<String, Double> m,
	    Map.Entry<String, Double> n) {
	return n.getValue().compareTo(m.getValue());
}
}
