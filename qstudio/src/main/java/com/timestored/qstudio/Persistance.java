/*
 * qStudio - Free SQL Analysis Tool
 * Copyright C 2013-2023 TimeStored
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.timestored.qstudio;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.timestored.qstudio.model.KeyInterface;
import com.timestored.qstudio.model.PersistanceInterface;

/**
 * Saves persistance state between runs of the application.
 */
public enum Persistance implements PersistanceInterface {

	INSTANCE;

	
	
	private static final Preferences PREF = Preferences.userNodeForPackage(Persistance.class);
	
	
	public enum Key implements KeyInterface {
		WINDOW_POSITIONS,OPEN_DOCS,CONNECTIONS,FRAME_HEIGHT,FRAME_WIDTH, RECENT_DOCS, 
		QDOC_WINDOW_SIZE, FIRST_OPEN, LAST_AD, LAST_OPENED_FOLDER,
		SHOW_QDOC_WARNING, SHOW_DBM_WARNING, SIGNED_LICENSE,
		/** First Ever Run Date, obfuscated name to deter hackery **/
		FERDB,// FERD -> FERDB when qStudio 2.0 released to allow new free trial period
		QUERY_COUNT, FRAME_X, FRAME_Y, FRAME_EXTENDEDSTATE,
		SHOW_KDB_EXAMPLE_WARNING, SHOW_NOTEBOOK_WARNING; 
	}
	
	/**
	 * Associates the specified value with the specified key in this preference node.
	 */
	@Override public void put(KeyInterface key, String value) {
		PREF.put(key.name(), value);
	}

	/** {@inheritDoc} **/
	@Override public String get(KeyInterface key, String def) {
		return PREF.get(key.name(), def);
	}

	/** {@inheritDoc} **/
	@Override public void putBoolean(KeyInterface key, boolean value) {
		PREF.putBoolean(key.name(), value);
	}

	/** {@inheritDoc} **/
	@Override public boolean getBoolean(KeyInterface key, boolean def) {
		return PREF.getBoolean(key.name(), def);
	}

	/** {@inheritDoc} **/
	@Override public void putInt(KeyInterface key, int value) {
		PREF.putInt(key.name(), value);
	}

	/** {@inheritDoc} **/
	@Override public int getInt(KeyInterface key, int def) {
		return PREF.getInt(key.name(), def);
	}

	/** {@inheritDoc} **/
	@Override public void putLong(KeyInterface key, long val) {
		PREF.putLong(key.name(), val);
	}

	/** {@inheritDoc} **/
	@Override public long getLong(KeyInterface key, long def) {
		return PREF.getLong(key.name(), def);
	}

	/** {@inheritDoc} **/
	@Override public void clear(boolean wipeLicense) throws BackingStoreException  {
		if(wipeLicense) {
			PREF.clear();
		}
		for(Key k : Key.values()) {
			if(!k.equals(Key.FERDB)) {
				PREF.remove(k.name());
			}
		}
	}

	/** {@inheritDoc} **/
	@Override public Preferences getPref() { return PREF; }

}
