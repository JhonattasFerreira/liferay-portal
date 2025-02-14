/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.content.dashboard.web.internal.item;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.content.dashboard.web.internal.item.type.ContentDashboardItemType;
import com.liferay.info.display.url.provider.InfoEditURLProvider;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
public class JournalArticleContentDashboardItem
	implements ContentDashboardItem<JournalArticle> {

	public JournalArticleContentDashboardItem(
		List<AssetCategory> assetCategories, List<AssetTag> assetTags,
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		ContentDashboardItemType contentDashboardItemType, Group group,
		InfoEditURLProvider<JournalArticle> infoEditURLProvider,
		JournalArticle journalArticle, Language language,
		JournalArticle latestApprovedJournalArticle,
		ModelResourcePermission<JournalArticle> modelResourcePermission,
		User user) {

		if (ListUtil.isEmpty(assetCategories)) {
			_assetCategories = Collections.emptyList();
		}
		else {
			_assetCategories = Collections.unmodifiableList(assetCategories);
		}

		if (ListUtil.isEmpty(assetTags)) {
			_assetTags = Collections.emptyList();
		}
		else {
			_assetTags = Collections.unmodifiableList(assetTags);
		}

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_contentDashboardItemType = contentDashboardItemType;
		_group = group;
		_infoEditURLProvider = infoEditURLProvider;
		_journalArticle = journalArticle;
		_language = language;

		if (!journalArticle.equals(latestApprovedJournalArticle)) {
			_latestApprovedJournalArticle = latestApprovedJournalArticle;
		}
		else {
			_latestApprovedJournalArticle = null;
		}

		_modelResourcePermission = modelResourcePermission;
		_user = user;
	}

	@Override
	public List<AssetCategory> getAssetCategories() {
		return _assetCategories;
	}

	@Override
	public List<AssetCategory> getAssetCategories(long assetVocabularyId) {
		Stream<AssetCategory> stream = _assetCategories.stream();

		return stream.filter(
			assetCategory ->
				assetCategory.getVocabularyId() == assetVocabularyId
		).collect(
			Collectors.toList()
		);
	}

	@Override
	public List<AssetTag> getAssetTags() {
		return _assetTags;
	}

	@Override
	public String getClassName() {
		return JournalArticle.class.getName();
	}

	@Override
	public Long getClassPK() {
		return _journalArticle.getResourcePrimKey();
	}

	@Override
	public ContentDashboardItemType getContentDashboardItemType() {
		return _contentDashboardItemType;
	}

	@Override
	public Date getCreateDate() {
		return _journalArticle.getCreateDate();
	}

	@Override
	public Map<String, Object> getData(Locale locale) {
		return HashMapBuilder.<String, Object>put(
			"display-date", _journalArticle.getDisplayDate()
		).put(
			"expiration-date", _journalArticle.getExpirationDate()
		).put(
			"review-date", _journalArticle.getReviewDate()
		).build();
	}

	@Override
	public Locale getDefaultLocale() {
		return LocaleUtil.fromLanguageId(
			_journalArticle.getDefaultLanguageId());
	}

	@Override
	public String getEditURL(HttpServletRequest httpServletRequest) {
		try {
			return Optional.ofNullable(
				_infoEditURLProvider.getURL(_journalArticle, httpServletRequest)
			).orElse(
				StringPool.BLANK
			);
		}
		catch (Exception exception) {
			_log.error(exception, exception);

			return StringPool.BLANK;
		}
	}

	@Override
	public Date getExpirationDate() {
		return _journalArticle.getExpirationDate();
	}

	@Override
	public Date getModifiedDate() {
		return _journalArticle.getModifiedDate();
	}

	@Override
	public Date getPublishDate() {
		return _journalArticle.getDisplayDate();
	}

	@Override
	public String getScopeName(Locale locale) {
		return Optional.ofNullable(
			_group
		).map(
			group -> {
				try {
					return group.getDescriptiveName(locale);
				}
				catch (PortalException portalException) {
					_log.error(portalException, portalException);

					return StringPool.BLANK;
				}
			}
		).orElse(
			StringPool.BLANK
		);
	}

	@Override
	public String getTitle(Locale locale) {
		return _journalArticle.getTitle(locale);
	}

	@Override
	public long getUserId() {
		return _user.getUserId();
	}

	@Override
	public String getUserName() {
		return _user.getFullName();
	}

	@Override
	public String getUserPortraitURL(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			return _user.getPortraitURL(themeDisplay);
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);

			return StringPool.BLANK;
		}
	}

	@Override
	public List<Version> getVersions(Locale locale) {
		return Stream.of(
			_toVersionOptional(_journalArticle, locale),
			_toVersionOptional(_latestApprovedJournalArticle, locale)
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).sorted(
			Comparator.comparing(Version::getVersion)
		).collect(
			Collectors.toList()
		);
	}

	@Override
	public String getViewURL(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _getViewURL(themeDisplay);
	}

	@Override
	public Map<Locale, String> getViewURLs(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return Stream.of(
			_journalArticle.getAvailableLanguageIds()
		).map(
			LocaleUtil::fromLanguageId
		).map(
			locale -> new AbstractMap.SimpleEntry<>(
				locale, _getViewURL(themeDisplay))
		).collect(
			Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
		);
	}

	@Override
	public boolean isEditURLEnabled(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			return _modelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), _journalArticle,
				ActionKeys.UPDATE);
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);

			return false;
		}
	}

	@Override
	public boolean isViewURLEnabled(HttpServletRequest httpServletRequest) {
		if (!_journalArticle.hasApprovedVersion()) {
			return false;
		}

		if (Validator.isNull(getViewURL(httpServletRequest))) {
			return false;
		}

		return true;
	}

	private String _getViewURL(ThemeDisplay themeDisplay) {
		try {
			Locale locale = themeDisplay.getLocale();

			ThemeDisplay clonedThemeDisplay =
				(ThemeDisplay)themeDisplay.clone();

			clonedThemeDisplay.setI18nPath(
				StringPool.SLASH + locale.toLanguageTag());

			String languageId = _language.getLanguageId(locale);

			clonedThemeDisplay.setI18nLanguageId(languageId);
			clonedThemeDisplay.setLanguageId(languageId);

			clonedThemeDisplay.setLocale(locale);
			clonedThemeDisplay.setScopeGroupId(_journalArticle.getGroupId());

			return Optional.ofNullable(
				_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
					JournalArticle.class.getName(),
					_journalArticle.getResourcePrimKey(), clonedThemeDisplay)
			).orElse(
				StringPool.BLANK
			);
		}
		catch (CloneNotSupportedException | PortalException exception) {
			_log.error(exception, exception);

			return StringPool.BLANK;
		}
	}

	private Optional<Version> _toVersionOptional(
		JournalArticle journalArticle, Locale locale) {

		return Optional.ofNullable(
			journalArticle
		).map(
			curJournalArticle -> new Version(
				_language.get(
					locale,
					WorkflowConstants.getStatusLabel(
						curJournalArticle.getStatus())),
				WorkflowConstants.getStatusStyle(curJournalArticle.getStatus()),
				curJournalArticle.getVersion())
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleContentDashboardItem.class);

	private final List<AssetCategory> _assetCategories;
	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final List<AssetTag> _assetTags;
	private final ContentDashboardItemType _contentDashboardItemType;
	private final Group _group;
	private final InfoEditURLProvider<JournalArticle> _infoEditURLProvider;
	private final JournalArticle _journalArticle;
	private final Language _language;
	private final JournalArticle _latestApprovedJournalArticle;
	private final ModelResourcePermission<JournalArticle>
		_modelResourcePermission;
	private final User _user;

}