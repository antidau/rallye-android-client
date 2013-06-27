package de.stadtrallye.rallyesoft.uimodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import de.rallye.model.structures.GroupUser;
import de.rallye.model.structures.PictureSize;
import de.stadtrallye.rallyesoft.R;
import de.stadtrallye.rallyesoft.model.IModel;
import de.stadtrallye.rallyesoft.model.structures.ChatEntry;
import de.stadtrallye.rallyesoft.model.structures.ChatEntry.Sender;

/**
 * Wraps around ChatEntry List
 * Uses R.layout.chat_item / R.layout.chat_item_right, depending on $ChatEntry.getSender()
 * @author Ramon
 *
 */
public class ChatAdapter extends BaseAdapter {

	private List<ChatEntry> chats = new ArrayList<ChatEntry>();
	private ImageLoader loader;
	private DateFormat converter;
	private GroupUser user;
	private Context context;
	private LayoutInflater inflator;
	private IModel model;
	
	private class ViewMem {
		public ImageView img;
		public TextView msg;
		public TextView sender;
		public TextView time;
	}
	
	
	public ChatAdapter(Context context, IModel model) {
		this.context = context;
		this.user = model.getUser();
		this.model = model;
		
		this.inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		loader = ImageLoader.getInstance();
		DisplayImageOptions disp = new DisplayImageOptions.Builder()
			.cacheOnDisc()
			.cacheInMemory() //TODO: Still unlimited Cache on Disk
			.showStubImage(R.drawable.stub_image)
			.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
			.defaultDisplayImageOptions(disp)
			.build();
        loader.init(config);
        
        converter = SimpleDateFormat.getDateTimeInstance();
	}
	
	
	public ChatEntry getChatEntry(int pos) {
		return chats.get(pos);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final ChatEntry chatEntry = chats.get(position);
		
		ViewMem mem;
		
        if (v == null) {
            Sender s = chatEntry.getSender(user);
            
            v = inflator.inflate((s == Sender.Me)? R.layout.chat_item_right : R.layout.chat_item_left, null);
            
            mem = new ViewMem();
            
            mem.img = (ImageView) v.findViewById(R.id.sender_img);
            mem.sender = (TextView) v.findViewById(R.id.msg_sender);
            mem.msg = (TextView) v.findViewById(R.id.msg);
            mem.time = (TextView) v.findViewById(R.id.time_sent);
            
            v.setTag(mem);
            
        } else {
        	mem = (ViewMem) v.getTag();
        }
        
        if (chatEntry != null) {
            mem.sender.setText(chatEntry.getGroupName() +"."+ chatEntry.getUserName());
            mem.msg.setText(chatEntry.message);
            mem.time.setText(converter.format(new Date(chatEntry.timestamp * 1000L)));
            
            // ImageLoader jar
            // ImageLoader must apparently be called for _EVERY_ entry
            // When called with null or "" as URL, will display empty picture / default resource
            // Otherwise ImageLoader will not be stable and start swapping images
            if (chatEntry.pictureID != null) {
            	loader.displayImage(model.getUrlFromImageId(chatEntry.pictureID, PictureSize.Thumbnail), mem.img);
            } else {
            	loader.displayImage(null, mem.img);
            }
            
        }
        
        return v;
	}
	
	public boolean hasPicture(int pos) {
		return chats.get(pos).pictureID != 0;
	}

	@Override
	public int getCount() {
		return chats.size();
	}

	@Override
	public ChatEntry getItem(int position) {
		return chats.get(position);
	}


	@Override
	public long getItemId(int position) {
		return chats.get(position).chatID;
	}
	
	public void replaceChats(List<ChatEntry> chats) {
		this.chats = chats;
		notifyDataSetChanged();
	}
	
	public void addChats(List<ChatEntry> chats) {
		this.chats.addAll(chats);
		notifyDataSetChanged();
	}

	public void editChats(List<ChatEntry> chats) {

		for (ChatEntry c: chats) {
			this.chats.set(this.chats.indexOf(c), c);
		}
		notifyDataSetChanged();
	}
}
