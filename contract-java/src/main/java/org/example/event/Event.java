package org.example.event;

import static java.nio.charset.StandardCharsets.UTF_8;
import org.json.JSONObject;

public class Event {
  public byte[] serialize() {
    String jsonStr = new JSONObject(this).toString();
    return jsonStr.getBytes(UTF_8);
  }
}
