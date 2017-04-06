package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import tvprograms.Program;
import tvprograms.ProgramsUtil;
import user.SinaUser;
import userprofile.SinaUserProfile;

public class SimpleDataAnalyzer {

	public static void main(String[] args) {
		analyze_tvprogram_catch();
	}

	private static void analyze_tvprogram_catch() {
		List<Document> doc_sina_users = FileUtil.readDocsFromFile(SinaUserProfile.SINA_USER_DATA);
		List<SinaUser> sina_users = new ArrayList<SinaUser>();
		
		Set<String>  sina_user_mark = new HashSet<String>();
		for (int i = 0; i < doc_sina_users.size(); i++) {
			Document doc_sina_user = doc_sina_users.get(i);
			SinaUser sinauser = new SinaUser(doc_sina_user);
			if (sina_user_mark.contains(sinauser.getUser_id()) == false &&
					sinauser.getSex() != -1 && sinauser.getFans_num() < 2000) {
				sina_users.add(sinauser);
				sina_user_mark.add(sinauser.getUser_id());
			}
		}
		
		Map<String, Integer> program_cnt = new HashMap<String, Integer>();
		for (Program program : ProgramsUtil.getProgramList()) {
			program_cnt.put(program.getTv_name(), 1);
		}
		for (SinaUser sinauser : sina_users) {
			for (String program : sinauser.getCare_programs()) {
//				if (ProgramsUtil.getIdByTvProgramName(program) == -1) {
//					continue;
//				}
				if (program_cnt.containsKey(program)) {
					program_cnt.put(program, program_cnt.get(program) + 1);
				} else {
					program_cnt.put(program, 1);
				}
			}
		}
		Iterator<String> iterator = program_cnt.keySet().iterator();
//		while(iterator.hasNext()) {
//			String pro_name = iterator.next();
//			//LogUtil.debug(pro_name + ":" + program_cnt.get(pro_name));
//			System.out.println(pro_name );
//		}
		iterator = program_cnt.keySet().iterator();
		while(iterator.hasNext()) {
			String pro_name = iterator.next();
			//LogUtil.debug(pro_name + ":" + program_cnt.get(pro_name));
			System.out.println(pro_name);
		}
	}

}
