package ubc.cs.cpsc210.sustainabilityapp.model;

import java.util.Arrays;
import java.util.List;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Android-specific implementation of KeyValueStore interface.  Uses Android's SharedPreferences class.
 */
public class SharedPreferencesKeyValueStore implements KeyValueStore {
	private final String LIST_DELIMITER = ";";
	
	private SharedPreferences preferences;
	
	public SharedPreferencesKeyValueStore(Context context, String name) {
		preferences = context.getSharedPreferences(name, 0);
	}
	
	/**
	 * Set the value for the specified key.
	 * 
	 * @param key Key for the value to be set.
	 * @param value The new string list to be assigned to this key (may be null).
	 * 
	 * @throws IllegalArgumentException If a string in the list is null or contains the reserved delimiter 
	 *                                 character.
	 */
	@Override
	public void putStringList(String key, List<String> list) {
		String stringValue = null;
		if (list != null) {
			for (String value: list) {
				if (value == null) {
					throw new IllegalArgumentException("List may not contain null values");
				}
				if (value.contains(LIST_DELIMITER)) {
					throw new IllegalArgumentException("Value in string list cannot contain delimiter ('" + 
							LIST_DELIMITER + "')");
				}
			}
			StringBuilder builder = new StringBuilder();
			for (int index = 0; index < list.size(); index++) {
				if (index > 0) {
					builder.append(LIST_DELIMITER);
				}
				builder.append(list.get(index));
			}
			stringValue = builder.toString();
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, stringValue);
		editor.commit();
	}

	/**
	 * Get the string list corresponding to the specified key.
	 *   
	 * @param key The key whose value is to be retrieved.
	 * @return The corresponding string list, or null if a null value has been assigned to the key,
	 *         or if no value has yet been assigned to the key.
	 * 
	 * @throws ClassCastException If the entry for the specified key is not convertible to a string list.
	 */
	@Override
	public List<String> getStringList(String key) {
		String value = preferences.getString(key, null);
		if (value == null) {
			return null;
		}
		String[] elements = value.split(LIST_DELIMITER);
		return Arrays.asList(elements);
	}

}
