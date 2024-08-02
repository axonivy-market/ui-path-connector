package com.axonivy.connector.uipath.builder;

import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.axonivy.connector.uipath.enums.FilterProperty;

public class FilterBuilder {

	public static final String EQUALS_PATTERN = "%s eq '%s'";
	private String filterQuery = EMPTY;

	public static FilterBuilder newInstance() {
		return new FilterBuilder();
	}

	public FilterBuilder name(String name) {
		if (StringUtils.isNotBlank(name)) {
			var filterByName = EQUALS_PATTERN.formatted(FilterProperty.NAME.getKey(), name);
			filterQuery = filterQuery.concat(filterByName);
		}
		return this;
	}

	public String build() {
		return StringUtils.isBlank(filterQuery) ? null : filterQuery;
	}
}
