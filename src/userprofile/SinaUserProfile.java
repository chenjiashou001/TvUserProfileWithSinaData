package userprofile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import tags.TagsUtil;
import tools.FileUtil;
import tools.LogUtil;
import tools.PrintClass;
import tools.WekaUtil;
import user.IUser;
import user.SinaUser;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * 用于处理新浪微博用户数据，传入一个标签，返回以该标签作为分类的instances
 * @author chenhuan001
 *
 */
public class SinaUserProfile {
	public static final String SINA_USER_DATA = "Data/user_program_data.txt";
	
	private static Instances _base_sina_instances = null;
	private static List<SinaUser> _sina_users = null;
	
	/**
	 * 对某一个标签，返回训练数据
	 * @param tag_id
	 * @return
	 */
	public static Instances getInstancesByTag(int tag_id) {
		init();
		for (int i = 0; i < _sina_users.size(); i++) {
			SinaUser sinauser = _sina_users.get(i);
			int is_user_has_tag_id = 0;
			for (int j = 0; j < sinauser.getUseful_tags().size(); j++) {
				String tag = sinauser.getUseful_tags().get(j);
				if (TagsUtil.isSinaUserTagBelongTAG(tag, tag_id)) {
					is_user_has_tag_id = 1;
					break;
				}
			}
			Instance instance = _base_sina_instances.get(i);
			//instance.setValue(instance.numAttributes() - 1, is_user_has_tag_id);
			instance.setClassValue(is_user_has_tag_id);
		}
		return _base_sina_instances;
	}
	
	public static Instances getInstancesBySex() {
		init();

		List<Document> doc_sina_users = FileUtil.readDocsFromFile(SINA_USER_DATA);
		List<SinaUser> sex_sina_users = new ArrayList<SinaUser>();
		Set<String>  sina_user_mark = new HashSet<String>();
		for (int i = 0; i < doc_sina_users.size(); i++) {
			Document doc_sina_user = doc_sina_users.get(i);
			SinaUser sinauser = new SinaUser(doc_sina_user);
			if (sinauser.isGoodSexSinaUser() && 
					sina_user_mark.contains(sinauser.getUser_id()) == false) {
					sex_sina_users.add(sinauser);
					sina_user_mark.add(sinauser.getUser_id());
				}
		}
		Collections.shuffle(sex_sina_users);
		List<IUser> IUsers  = new ArrayList<IUser>();
		IUsers.addAll(sex_sina_users);
		Instances sex_sina_instances = WekaUtil.getBaseInstancesByUsers(IUsers, WekaUtil.getAttributes());
		for (int i = 0; i < sex_sina_users.size(); i++) {
			SinaUser sinauser = sex_sina_users.get(i);
			Instance instance = sex_sina_instances.get(i);
			instance.setClassValue(sinauser.getSex());
		}
		return sex_sina_instances;
	}
	
	public static Instances getInstancesByAge() {
		init();
		//得到符合要求的用户
		List<Document> doc_sina_users = FileUtil.readDocsFromFile(SINA_USER_DATA);
		List<SinaUser> age_sina_users = new ArrayList<SinaUser>();
		Set<String>  sina_user_mark = new HashSet<String>();
		for (int i = 0; i < doc_sina_users.size(); i++) {
			Document doc_sina_user = doc_sina_users.get(i);
			SinaUser sinauser = new SinaUser(doc_sina_user);
			if (sinauser.isGoodAgeSinaUser() && 
					sina_user_mark.contains(sinauser.getUser_id()) == false) {
					age_sina_users.add(sinauser);
					sina_user_mark.add(sinauser.getUser_id());
				}
		}
		Collections.shuffle(age_sina_users);
		ArrayList<Attribute> attributes = WekaUtil.getAttributes();
//		List<String> age_list = new ArrayList<String>(4);
//    	age_list.add("0");
//    	age_list.add("1");
//    	age_list.add("2");
//    	age_list.add("3");
		List<String> age_list = new ArrayList<String>(2);
		age_list.add("0");
		age_list.add("1");
		
    	attributes.set(attributes.size() - 1, new Attribute("age_key_class", age_list));
    	List<IUser> IUsers  = new ArrayList<IUser>();
		IUsers.addAll(age_sina_users);
    	Instances instances = WekaUtil.getBaseInstancesByUsers(IUsers, attributes);
		for (int i = 0; i < age_sina_users.size(); i++) {
			SinaUser sinauser = age_sina_users.get(i);
			Instance instance = instances.get(i);
			instance.setClassValue(sinauser.getAge());
			//LogUtil.debug(instance.toString());
		}
    	return instances;
	}
	/**
	 * 1. 加载sina_user
	 * 2. 生成base_sina_instances
	 */
	public static void init() {
		if (null != _sina_users) {
			return ;
		}
		List<Document> doc_sina_users = FileUtil.readDocsFromFile(SINA_USER_DATA);
		_sina_users = new ArrayList<SinaUser>();
		
		Set<String>  sina_user_mark = new HashSet<String>();
		for (int i = 0; i < doc_sina_users.size(); i++) {
			Document doc_sina_user = doc_sina_users.get(i);
			SinaUser sinauser = new SinaUser(doc_sina_user);
			if (sinauser.isGoodSinaUser() && 
				sina_user_mark.contains(sinauser.getUser_id()) == false) {
				_sina_users.add(sinauser);
				sina_user_mark.add(sinauser.getUser_id());
			}
		}
		
		LogUtil.info("加载SinaUserInfo结束，可用的数据有" + _sina_users.size() + "条");
		//打乱顺序
		 Collections.shuffle(_sina_users); // 混乱的意思 
		
		//debug_sinauser(0, 20);
		//然后就是构建base_instances
		List<IUser> IUsers  = new ArrayList<IUser>();//这个多态实现的不好啊。。
		IUsers.addAll(_sina_users);
		_base_sina_instances = WekaUtil.getBaseInstancesByUsers(IUsers, WekaUtil.getAttributes());
	}
	
	public static List<SinaUser> getSinaUsers() {
		init();
		return _sina_users;
	}
	
	/**
	 * 调试用，打印sina_users[b,d]
	 * @param b
	 * @param d
	 */
	public static void debug_sinauser(int b, int d) {
		for (int i = b; i < Math.min(_sina_users.size(), d); i++) {
			SinaUser sinauser = _sina_users.get(i);
			LogUtil.debug(PrintClass.outObjPropertyString((IUser)sinauser));
		}
	}
	
	
	public static void main(String[] args) {
		init();
		//getInstancesByAge();
		//getInstancesBySex();
		//WekaUtil.addExtraInstance(getInstancesByTag(0));
		//Instances instances = getInstancesByTag(0);
		//LogUtil.debug(instances.toString());
//		for (int i = 0; i < 20; i++) {
//			LogUtil.debug(i + " : " + instances.get(i).classValue());
//		}
		
	}

}
