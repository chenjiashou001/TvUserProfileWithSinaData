package tvprograms;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class Program {
	int id;
	String tv_name;
	String sina_id;//比int大
	String sina_name;
	String tv_watch_time;
	int sina_fans;
	List<String> sina_tags;//sina自带的tags
	List<String> epg_tags;//EPG得出的tags
	List<String> user_profile_tags;//做用户画像实验得出的tags
	
	public Program() {
		init_program();
	}
		
	public Program(Document doc_program) {
		init_program();
		setProgramByDocument(doc_program);
	}
	
	private void init_program() {
		tv_name = null;
		sina_id = null;
		sina_name = null;
		tv_watch_time = null;
		sina_fans = -1;
		sina_tags = new ArrayList<String>();
		epg_tags = new ArrayList<String>();
		user_profile_tags = new ArrayList<String>();
	}
	
	public void setProgramByDocument(Document doc_program) {
		tv_name = doc_program.getString("tv_name");
		sina_id = doc_program.getString("sina_id");
		sina_name = doc_program.getString("sina_name");
		tv_watch_time =  doc_program.getString("watch_tim");
	}
	
	public void DEBUG() {
		System.out.println("\n--------debug--------");
		//TODO 可以考虑自己写一个
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTv_name() {
		return tv_name;
	}
	public void setTv_name(String tv_name) {
		this.tv_name = tv_name;
	}
	public String getSina_id() {
		return sina_id;
	}
	public void setSina_id(String sina_id) {
		this.sina_id = sina_id;
	}
	public String getSina_name() {
		return sina_name;
	}
	public void setSina_name(String sina_name) {
		this.sina_name = sina_name;
	}
	public String getTv_watch_time() {
		return tv_watch_time;
	}
	public void setTv_watch_time(String tv_watch_time) {
		this.tv_watch_time = tv_watch_time;
	}
	public int getSina_fans() {
		return sina_fans;
	}
	public void setSina_fans(int sina_fans) {
		this.sina_fans = sina_fans;
	}
	public List<String> getSina_tags() {
		return sina_tags;
	}
	public void setSina_tags(List<String> sina_tags) {
		this.sina_tags = sina_tags;
	}
	public List<String> getEpg_tags() {
		return epg_tags;
	}
	public void setEpg_tags(List<String> epg_tags) {
		this.epg_tags = epg_tags;
	}
	public List<String> getUser_profile_tags() {
		return user_profile_tags;
	}
	public void setUser_profile_tags(List<String> user_profile_tags) {
		this.user_profile_tags = user_profile_tags;
	}
	
}
