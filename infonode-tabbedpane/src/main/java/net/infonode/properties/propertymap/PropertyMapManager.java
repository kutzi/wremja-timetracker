/*
 * Copyright (C) 2004 NNL Technology AB
 * Visit www.infonode.net for information about InfoNode(R) 
 * products and how to contact NNL Technology AB.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA 02111-1307, USA.
 */


// $Id$
package net.infonode.properties.propertymap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.infonode.properties.base.Property;
import net.infonode.properties.propertymap.value.PropertyValue;
import net.infonode.util.Utils;
import net.infonode.util.ValueChange;
import net.infonode.util.collection.map.base.ConstMap;
import net.infonode.util.collection.map.base.ConstMapIterator;

/**
 * Utility class for performing multiple modifications to {@link PropertyMap}'s and merging change notifications to
 * optimize performance.
 *
 * @author $Author$
 * @version $Revision$
 */
public class PropertyMapManager {
  private static final PropertyMapManager INSTANCE = new PropertyMapManager();

  private Map<PropertyMap, Map<Property, ValueChange>> changes;
  private int batchCounter;

  /**
   * Returns the only instance of this class.
   *
   * @return the only instance of this class
   */
  public static PropertyMapManager getInstance() {
    return INSTANCE;
  }

  void addMapChanges(PropertyMapImpl propertyMap, ConstMap mapChanges) {
    Map<Property, ValueChange> map = changes.get(propertyMap);

    if (map == null) {
      map = new HashMap<Property, ValueChange>();
      changes.put(propertyMap, map);
    }

    for (ConstMapIterator iterator = mapChanges.constIterator(); iterator.atEntry(); iterator.next()) {
      ValueChange vc = (ValueChange) iterator.getValue();
      Object key = iterator.getKey();
      Object newValue = vc.getNewValue() == null ?
                        null : ((PropertyValue) vc.getNewValue()).getWithDefault(propertyMap);
      Object value = map.get(key);
      Object oldValue = value == null ?
                        vc.getOldValue() == null ?
                        null : ((PropertyValue) vc.getOldValue()).getWithDefault(propertyMap) :
                        ((ValueChange) value).getOldValue();

      if (!Utils.equals(oldValue, newValue))
        map.put((Property) iterator.getKey(), new ValueChange(oldValue, newValue));
      else if (value != null)
        map.remove(key);
    }
  }

  /**
   * Executes a method inside a {@link #beginBatch()} - {@link #endBatch()} pair. See {@link #beginBatch()} for
   * more information. It's safe to call other batch methods from inside {@link Runnable#run}.
   *
   * @param runnable the runnable to invoke
   */
  public static void runBatch(Runnable runnable) {
    getInstance().beginBatch();

    try {
      runnable.run();
    }
    finally {
      getInstance().endBatch();
    }
  }

  /**
   * Begins a batch operation. This stores and merges all change notifications occuring in all property maps until
   * {@link #endBatch} is called. Each call to this method MUST be followed by a call to {@link #endBatch}.
   * This method can be called an unlimited number of times without calling {@link #endBatch} in between, but each
   * call must have a corresponding call to {@link #endBatch}. Only when exiting from the
   * outermost {@link #endBatch()} the changes be propagated to the listeners.
   */
  public void beginBatch() {
    if (batchCounter++ == 0)
      changes = new HashMap<PropertyMap, Map<Property, ValueChange>>();
  }

  private void addTreeChanges(PropertyMapImpl map, PropertyMapImpl modifiedMap,
		  Map<Property, ValueChange> changes,
		  Map<PropertyMap, Map<PropertyMap, Map<Property, ValueChange>>> treeChanges) {
    Map<PropertyMap, Map<Property, ValueChange>> changeMap = treeChanges.get(map);

    if (changeMap == null) {
      changeMap = new HashMap<PropertyMap, Map<Property, ValueChange>>();
      treeChanges.put(map, changeMap);
    }

    changeMap.put(modifiedMap, changes);

    if (map.getParent() != null)
      addTreeChanges(map.getParent(), modifiedMap, changes, treeChanges);
  }

  /**
   * Ends a batch operation. See {@link #beginBatch()} for more information.
   */
  public void endBatch() {
    if (--batchCounter == 0) {
      Map<PropertyMap, Map<PropertyMap, Map<Property, ValueChange>>> treeChanges =
    	  new HashMap<PropertyMap, Map<PropertyMap, Map<Property, ValueChange>>>();
      Map<PropertyMap, Map<Property, ValueChange>> localChanges = changes;
      changes = null;

      for (Map.Entry<PropertyMap, Map<Property, ValueChange>> entry : localChanges.entrySet()) {
        PropertyMapImpl object = (PropertyMapImpl) entry.getKey();
        Map<Property, ValueChange> objectChanges = entry.getValue();

        if (!objectChanges.isEmpty()) {
          object.firePropertyValuesChanged(Collections.unmodifiableMap(objectChanges));
          addTreeChanges(object, object, objectChanges, treeChanges);
        }
      }

      for (Map.Entry<PropertyMap, Map<PropertyMap, Map<Property, ValueChange>>> entry : treeChanges.entrySet()) {
        PropertyMapImpl object = (PropertyMapImpl) entry.getKey();
        Map<PropertyMap, Map<Property, ValueChange>> objectChanges = entry.getValue();

        if (!objectChanges.isEmpty())
          object.firePropertyTreeValuesChanged(Collections.unmodifiableMap(objectChanges));
      }
    }
  }
}
