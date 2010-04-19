/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
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
 */

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.filter.AbstractFilter;
import net.sourceforge.atunes.kernel.modules.filter.FilterHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.I18nUtils;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginListener;
import org.commonjukebox.plugins.PluginSystemException;

public final class NavigationHandler extends AbstractHandler implements PluginListener {

    /**
     * Singleton instance
     */
    private static NavigationHandler instance;

    private List<AbstractNavigationView> navigationViews;

    /**
     * Filter for navigation table
     */
    private AbstractFilter tableFilter = new AbstractFilter() {

        @Override
        public String getName() {
            return "NAVIGATION_TABLE";
        }

        @Override
        public String getDescription() {
            return I18nUtils.getString("NAVIGATION_TABLE");
        }

        @Override
        public void applyFilter(String filter) {
            ControllerProxy.getInstance().getNavigationController().updateTableContent(getCurrentView().getTree());
        }
    };

    /**
     * Filter for tree
     */
    private AbstractFilter treeFilter = new AbstractFilter() {

        @Override
        public String getName() {
            return "NAVIGATION_TREE";
        };

        @Override
        public String getDescription() {
            return I18nUtils.getString("NAVIGATOR");
        };

        @Override
        public void applyFilter(String filter) {
            refreshCurrentView();
        };
    };

    /**
     * Getter of singleton instance
     * 
     * @return
     */
    public static NavigationHandler getInstance() {
        if (instance == null) {
            instance = new NavigationHandler();
        }
        return instance;
    }

    private NavigationHandler() {
    }

    @Override
    public void applicationFinish() {
    }

    @Override
    protected void initHandler() {
    }

    @Override
    public void applicationStarted() {
    }

    public List<AbstractNavigationView> getNavigationViews() {
        if (navigationViews == null) {
            navigationViews = new ArrayList<AbstractNavigationView>();
            // TODO: Dynamic load of navigation views, possibly from a config file
            navigationViews.add(new RepositoryNavigationView());
            navigationViews.add(new FavoritesNavigationView());
            navigationViews.add(new DeviceNavigationView());
            navigationViews.add(new RadioNavigationView());
            navigationViews.add(new PodcastNavigationView());
        }
        return navigationViews;
    }

    /**
     * Builds a map containing classes of navigation view as keys and references
     * to instances as values
     * 
     * @return
     */
    private Map<Class<? extends AbstractNavigationView>, AbstractNavigationView> getNavigationViewsMap() {
        Map<Class<? extends AbstractNavigationView>, AbstractNavigationView> navigationViewsMap = new HashMap<Class<? extends AbstractNavigationView>, AbstractNavigationView>();
        for (AbstractNavigationView view : getNavigationViews()) {
            navigationViewsMap.put(view.getClass(), view);
        }
        return navigationViewsMap;
    }

    public AbstractNavigationView getCurrentView() {
        return getView(getViewByName(ApplicationState.getInstance().getNavigationView()));
    }

    public ViewMode getCurrentViewMode() {
        return getCurrentView().getCurrentViewMode();
    }

    public AbstractNavigationView getView(Class<? extends AbstractNavigationView> navigationViewClass) {
        return getNavigationViewsMap().get(navigationViewClass);
    }

    /**
     * Refreshes current view to update data shown
     */
    public void refreshCurrentView() {
        getCurrentView().refreshView(ApplicationState.getInstance().getViewMode(),
                FilterHandler.getInstance().isFilterSelected(getTreeFilter()) ? FilterHandler.getInstance().getFilter() : null);
    }

    /**
     * Refreshes given view. To avoid unnecessary actions, given view is only
     * refreshed if it's the current view
     * 
     * @param navigationViewClass
     */
    public void refreshView(Class<? extends AbstractNavigationView> navigationViewClass) {
        if (getView(navigationViewClass).equals(getCurrentView())) {
            getView(navigationViewClass).refreshView(ApplicationState.getInstance().getViewMode(),
                    FilterHandler.getInstance().isFilterSelected(getTreeFilter()) ? FilterHandler.getInstance().getFilter() : null);
        }
    }

    public Class<? extends AbstractNavigationView> getViewByName(String className) {
        if (className == null) {
            return null;
        }
        for (Class<? extends AbstractNavigationView> viewFromMap : getNavigationViewsMap().keySet()) {
            if (viewFromMap.getName().equals(className)) {
                return viewFromMap;
            }
        }

        // If class is not found (maybe the view was a plugin and has been removed, return default view)
        return RepositoryNavigationView.class;
    }

    public int indexOf(Class<? extends AbstractNavigationView> view) {
        return getNavigationViews().indexOf(getNavigationViewsMap().get(view));
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            getNavigationViews().add((AbstractNavigationView) plugin.getInstance());
            // Set tabs and text for navigator
            ControllerProxy.getInstance().getNavigationController().getNavigationTreePanel().updateTabs();
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.PLUGINS, e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> views) {
        // Remove all views
        for (Plugin view : views) {
            getNavigationViews().remove(view);
        }
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        // TODO: Remove refreshing explicitly radio view
        refreshView(RadioNavigationView.class);
        refreshCurrentView();
    }

    /**
     * @return the tableFilter
     */
    public AbstractFilter getTableFilter() {
        return tableFilter;
    }

    /**
     * @return the treeFilter
     */
    public AbstractFilter getTreeFilter() {
        return treeFilter;
    }
}
