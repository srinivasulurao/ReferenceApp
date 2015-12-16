package foodzu.com.adapters;


/*public class Expandablelist_adapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, ArrayList<String>> _listDataChild;
	Utils Util = new Utils(_context);

	public Expandablelist_adapter(Context context, List<String> listDataHeader,
			HashMap<String, ArrayList<String>> listChildData) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final String childText = (String) getChild(groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.adapter_sublist_menu,
					null);
		}

		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.txtsub_menu);

		txtListChild.setText(childText);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		final ViewHolder holder;
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.adapter_subcat_menu,
					null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.lblListHeader = (TextView) convertView
				.findViewById(R.id.txtsubCategory);
		holder.indicator = (ImageView) convertView.findViewById(R.id.ic_img);
		holder.llview = (LinearLayout) convertView.findViewById(R.id.grp_view);
		if (Util.GroupSID != groupPosition) {
			holder.llview.setBackgroundColor(_context.getResources().getColor(
					R.color.foodzu_green));
			holder.lblListHeader.setTextColor(_context.getResources().getColor(
					R.color.black));
		} else {

			holder.llview.setBackgroundColor(_context.getResources().getColor(
					R.color.orange));
			holder.lblListHeader.setTextColor(_context.getResources().getColor(
					R.color.White));
		}
		if (getChildrenCount(groupPosition) == 0) {
			holder.indicator.setImageResource(R.drawable.extend);

		} else {
			holder.indicator.setVisibility(View.VISIBLE);
			if (isExpanded)
				holder.indicator.setImageResource(R.drawable.down_up);
			else
				holder.indicator.setImageResource(R.drawable.down_drop);
		}
		holder.lblListHeader.setTypeface(null, Typeface.BOLD);
		holder.lblListHeader.setText(headerTitle);

		return convertView;
	}

	static class ViewHolder {
		TextView lblListHeader;
		ImageView indicator;
		LinearLayout llview;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}*/
