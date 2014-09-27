/*
 * Copyright (c) 2014 Jakob Wenzel, Ramon Wirsch.
 *
 * This file is part of RallyeSoft.
 *
 * RallyeSoft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RallyeSoft. If not, see <http://www.gnu.org/licenses/>.
 */

package de.stadtrallye.rallyesoft.net.retrofit;

import java.util.List;

import de.rallye.model.structures.Map;
import de.rallye.model.structures.MapConfig;
import de.rallye.model.structures.SimpleChatEntry;
import de.rallye.model.structures.SimpleSubmission;
import de.rallye.model.structures.Submission;
import de.rallye.model.structures.User;
import de.stadtrallye.rallyesoft.model.chat.ChatEntry;
import de.stadtrallye.rallyesoft.model.chat.Chatroom;
import de.stadtrallye.rallyesoft.model.structures.Task;
import de.stadtrallye.rallyesoft.net.Paths;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by Ramon on 21.09.2014.
 */
public interface RetroAuthCommunicator {

	@DELETE(Paths.GROUPS_WITH_ID)
	void logout(@Path("groupID") int groupID, Callback<Response> callback);

	@GET(Paths.CHATROOMS)
	void getAvailableChatrooms(Callback<List<Chatroom>> callback);

	@GET(Paths.CHATROOM_CHATS)
	void getChats(@Path(Paths.PARAM_CHATROOM_ID) int chatroomID, Callback<List<ChatEntry>> callback);

	@GET(Paths.CHATROOM_CHATS_SINCE)
	void getChatsSince(@Path(Paths.PARAM_CHATROOM_ID) int chatroomID, @Path(Paths.PARAM_SINCE) long since, Callback<List<ChatEntry>> callback);

	@GET(Paths.MAP)
	void getMap(Callback<Map> callback);

	@GET(Paths.MAP_CONFIG)
	void getMapConfig(Callback<MapConfig> callback);

	@GET(Paths.USERS)
	void getAllUsers(Callback<List<User>> callback);

	@GET(Paths.TASKS)
	void getTasks(Callback<List<Task>> callback);

	@GET(Paths.TASK_SUBMISSIONS)
	void getSubmissionsForTask(@Path(Paths.PARAM_TASK_ID) int taskID, Callback<List<Submission>> callback);

	@GET(Paths.TASKS_SUBMISSIONS_ALL)
	void getAllSubmissionsForGroup(@Path(Paths.PARAM_GROUP_ID) int groupID, Callback<List<Submission>> callback);

	@PUT(Paths.CHATROOM_CHATS)
	void postMessage(@Path(Paths.PARAM_CHATROOM_ID) int chatroomID, @Body SimpleChatEntry message, Callback<ChatEntry> callback);

	@PUT(Paths.TASK_SUBMISSIONS)
	void postSubmission(@Path(Paths.PARAM_TASK_ID) int taskID, @Body SimpleSubmission submission, Callback<Submission> callback);
}
