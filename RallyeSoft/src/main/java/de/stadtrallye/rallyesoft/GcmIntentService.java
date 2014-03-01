package de.stadtrallye.rallyesoft;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.rallye.model.structures.Chatroom;
import de.rallye.model.structures.PushEntity;
import de.stadtrallye.rallyesoft.model.Model;
import de.stadtrallye.rallyesoft.model.converters.JsonConverters;
import de.stadtrallye.rallyesoft.model.structures.ChatEntry;
import de.wirsch.gcm.GcmBaseIntentService;

public class GcmIntentService extends GcmBaseIntentService {
	
	private static final String THIS = GcmIntentService.class.getSimpleName();

	@Override
	protected void onMessage(Bundle message) {
		Log.i(THIS, "Received Push: " +message);
		Context context = getApplicationContext();

		try {
			PushEntity.Type type = PushEntity.Type.valueOf(message.getString(PushEntity.TYPE));
			JSONObject payload = new JSONObject(message.getString(PushEntity.PAYLOAD));

			switch (type) {
				case newMessage:
					ChatEntry chat = new JsonConverters.ChatConverter().doConvert(payload);
					int roomID = payload.getInt(Chatroom.CHATROOM_ID);
					Model.getInstance(context).getChatroom(roomID).pushChat(chat);
					break;
				case messageChanged:
					chat = new JsonConverters.ChatConverter().doConvert(payload);
					roomID = payload.getInt(Chatroom.CHATROOM_ID);
					Model.getInstance(context).getChatroom(roomID).editChat(chat);
					break;
				default:
			}
		} catch (JSONException e) {
			Log.e(THIS, "Push Message not compatible", e);
		}
	}

	@Override
	protected void onError(String errorId) {
		Log.e(THIS, "Error: "+ errorId);
	}

	@Override
	protected void onDeleted(String message) {
		Log.e(THIS, "Deleted: "+ message);
	}

	/*@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(THIS, "Registered GCM!");
		
		GCMRegistrar.setRegisteredOnServer(getApplicationContext(), false);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.w(THIS, "Unregistered GCM!");
//		GCMRegistrar.setRegisteredOnServer(getApplicationContext(), false);
		
		Model.getInstance(getApplicationContext()).logout();
	}*/

}