package de.stadtrallye.rallyesoft.model;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import de.stadtrallye.rallyesoft.util.JSONArray;
import de.stadtrallye.rallyesoft.util.JSONConverter;

public class MapNode {
	public int ID;
	public String name;
	public int lat;
	public int lon;
	public String description;

	public MapNode(int ID, String name, double lat, double lon, String description) {
		this.ID = ID;
		this.name = name;
		this.lat = (int) (lat * 1000000);
		this.lon = (int) (lon * 1000000);
		this.description = description;
	}
	
	
	@Override
	public String toString() {
		return name +" ( "+lat+" , "+lon+" )";
	}


	public static List<MapNode> translateJSON(String js) {
		return JSONArray.toList(new NodeConverter(), js);
	}
	
	private static class NodeConverter extends JSONConverter<MapNode> {
		
		@Override
		public MapNode doConvert(JSONObject o) throws JSONException {
			return new MapNode(
					o.getInt("nodeID"),
					o.getString("name"),
					o.getDouble("lat"),
					o.getDouble("lon"),
					o.getString("description"));
		}
		
	}
}
