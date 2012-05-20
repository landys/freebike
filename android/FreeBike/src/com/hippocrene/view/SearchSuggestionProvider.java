package com.hippocrene.view;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {

	public final static String AUTHORITY = "com.hippocrene.view.SearchSuggestionProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public SearchSuggestionProvider(){
		super();
		setupSuggestions(AUTHORITY, MODE);
	}
}
