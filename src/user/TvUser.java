package user;

import java.util.List;

import tvprograms.Program;

public class TvUser extends IUser {
	List<String> pred_tags;
	List<String> epg_tags;
	
	public List<String> getEpg_tags() {
		return epg_tags;
	}

	public void setEpg_tags(List<String> epg_tags) {
		this.epg_tags = epg_tags;
	}

	public List<String> getPred_tags() {
		return pred_tags;
	}

	public void setPred_tags(List<String> pred_tags) {
		this.pred_tags = pred_tags;
	}
	
}
