package ru.curs.showcase.model;

import org.xml.sax.Attributes;

import ru.curs.showcase.app.api.event.*;

/**
 * Фабрика для создания действий (Action) из XML. Не вызывается самостоятельно,
 * а только из других фабрик при работе SAX парсера.
 * 
 * @author den
 * 
 */
public class ActionFactory extends GeneralXMLHelper implements SAXTagHandler {
	static final String SHOW_CLOSE_BOTTOM_BUTTON_TAG = "show_close_bottom_button";
	static final String KEEP_USER_SETTINGS_TAG = "keep_user_settings";
	static final String SKIP_REFRESH_CONTEXT_ONLY_TAG = "skip_refresh_context_only";
	static final String SHOW_IN_MODE_TAG = "show_in";
	static final String REFRESH_CONTEXT_ONLY_TAG = "refresh_context_only";

	/**
	 * Текущее действие.
	 */
	private Action current;

	private CompositeContext setupContext(final Attributes attrs) {
		CompositeContext context = null;
		if ((attrs.getIndex(MAIN_CONTEXT_ATTR_NAME) > -1)
				|| (attrs.getIndex(ADD_CONTEXT_ATTR_NAME) > -1)) {
			context = new CompositeContext();
			if (attrs.getIndex(MAIN_CONTEXT_ATTR_NAME) > -1) {
				context.setMain(attrs.getValue(MAIN_CONTEXT_ATTR_NAME));
			}
			if (attrs.getIndex(ADD_CONTEXT_ATTR_NAME) > -1) {
				context.setAdditional(attrs.getValue(ADD_CONTEXT_ATTR_NAME));
			}
		}
		return context;
	}

	@Override
	public boolean canHandle(final String aTagName, final SaxEventType aSaxEventType) {
		return ACTION_TAG.equalsIgnoreCase(aTagName) || DP_TAG.equalsIgnoreCase(aTagName)
				|| NAVIGATOR_TAG.equalsIgnoreCase(aTagName)
				|| ELEMENT_TAG.equalsIgnoreCase(aTagName)
				|| MODAL_WINDOW_TAG.equalsIgnoreCase(aTagName);
	}

	@Override
	public Action handleStartTag(final String namespaceURI, final String lname,
			final String qname, final Attributes attrs) {
		String value;
		if (qname.equalsIgnoreCase(ACTION_TAG)) {
			Action action = new Action();
			action.setDataPanelActionType(DataPanelActionType.RELOAD_PANEL);
			action.setNavigatorActionType(NavigatorActionType.DO_NOTHING);
			if (attrs.getIndex(SHOW_IN_MODE_TAG) > -1) {
				action.setShowInMode(ShowInMode.valueOf(attrs.getValue(SHOW_IN_MODE_TAG)));
			}
			if (attrs.getIndex(KEEP_USER_SETTINGS_TAG) > -1) {
				value = attrs.getValue(KEEP_USER_SETTINGS_TAG);
				action.setKeepUserSettings(Boolean.parseBoolean(value));
			}
			current = action;
		}
		if (qname.equalsIgnoreCase(DP_TAG)) {
			DataPanelLink link = new DataPanelLink();
			link.setDataPanelId(attrs.getValue(DP_ID_ATTR_NAME));

			ActionTabFinder finder = AppRegistry.getActionTabFinder();
			link.setTabId(finder.findTabForAction(link, attrs.getValue(TAB_TAG)));

			CompositeContext context = setupContext(attrs);
			link.setContext(context);
			current.setDataPanelLink(link);
		}
		if (qname.equalsIgnoreCase(NAVIGATOR_TAG)) {
			NavigatorElementLink link = new NavigatorElementLink();
			if (attrs.getIndex(ELEMENT_TAG) > -1) {
				link.setId(attrs.getValue(ELEMENT_TAG));
			}
			if (attrs.getIndex(REFRESH_TAG) > -1) {
				value = attrs.getValue(REFRESH_TAG);
				link.setRefresh(Boolean.parseBoolean(value));
			}
			current.setNavigatorElementLink(link);
		}
		if (qname.equalsIgnoreCase(MODAL_WINDOW_TAG)) {
			ModalWindowInfo mwi = new ModalWindowInfo();
			if (attrs.getIndex(CAPTION_TAG) > -1) {
				mwi.setCaption(attrs.getValue(CAPTION_TAG));
			}
			if (attrs.getIndex(WIDTH_TAG) > -1) {
				value = attrs.getValue(WIDTH_TAG);
				mwi.setWidth(Integer.valueOf(value));
			}
			if (attrs.getIndex(HEIGHT_TAG) > -1) {
				value = attrs.getValue(HEIGHT_TAG);
				mwi.setHeight(Integer.valueOf(value));
			}
			if (attrs.getIndex(SHOW_CLOSE_BOTTOM_BUTTON_TAG) > -1) {
				value = attrs.getValue(SHOW_CLOSE_BOTTOM_BUTTON_TAG);
				mwi.setShowCloseBottomButton(Boolean.parseBoolean(value));
			}
			current.setModalWindowInfo(mwi);
		}
		if (qname.equalsIgnoreCase(ELEMENT_TAG)) {
			DataPanelElementLink link = new DataPanelElementLink();
			link.setId(attrs.getValue(ID_TAG));
			CompositeContext context = setupContext(attrs);
			context.assignNullValues(current.getDataPanelLink().getContext());
			link.setContext(context);
			if (attrs.getIndex(REFRESH_CONTEXT_ONLY_TAG) > -1) {
				value = attrs.getValue(REFRESH_CONTEXT_ONLY_TAG);
				link.setRefreshContextOnly(Boolean.parseBoolean(value));
			}
			if (attrs.getIndex(SKIP_REFRESH_CONTEXT_ONLY_TAG) > -1) {
				value = attrs.getValue(SKIP_REFRESH_CONTEXT_ONLY_TAG);
				link.setSkipRefreshContextOnly(Boolean.parseBoolean(value));
			}
			if (attrs.getIndex(KEEP_USER_SETTINGS_TAG) > -1) {
				value = attrs.getValue(KEEP_USER_SETTINGS_TAG);
				link.setKeepUserSettings(Boolean.parseBoolean(value));
			}
			current.getDataPanelLink().getElementLinks().add(link);
		}
		return current;
	}

	@Override
	public void handleEndTag(final String aNamespaceURI, final String aLname, final String aQname) {
	}

	@Override
	public void handleCharacters(final char[] aArg0, final int aArg1, final int aArg2) {
	}

}
