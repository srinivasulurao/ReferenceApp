package foodzu.com.models;

import java.util.ArrayList;

public class MainMenu {
	String menu_id, menu_name, menu_image;
	
	public ArrayList<SubMenu_Cat> submenu = new ArrayList<SubMenu_Cat>();
	
	public ArrayList<SubMenu_Cat> getsubmenu_list() {
		return submenu;
	}

	public void setsubmenu_list(ArrayList<SubMenu_Cat> submenu) {
		this.submenu = submenu;
	}
	

	public String getmenu_id() {
		return menu_id;
	}

	public void setmenu_id(String menu_id) {
		this.menu_id = menu_id;
	}

	public String getmenu_name() {
		return menu_name;
	}

	public void setmenu_name(String menu_name) {
		this.menu_name = menu_name;
	}

}
