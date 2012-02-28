package org.softwareFm.eclipse.mysoftwareFm.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.response.IResponse;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.mysoftwareFm.IGroupClientOperations;
import org.softwareFm.eclipse.mysoftwareFm.IdAndName;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.composites.TextInBorderWithClick;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.editors.KeyAndEditStrategy;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.internal.UserData;

public class GroupClientOperations implements IGroupClientOperations {

	private final IMasterDetailSocial masterDetailSocial;
	private final CardConfig cardConfig;
	private final IHttpClient client;
	private final IResourceGetter resourceGetter;

	public GroupClientOperations(IMasterDetailSocial masterDetailSocial, CardConfig cardConfig, IHttpClient client) {
		this.masterDetailSocial = masterDetailSocial;
		this.cardConfig = cardConfig;
		this.client = client;
		this.resourceGetter = Functions.call(cardConfig.resourceGetterFn, GroupConstants.myGroupsCardType);
	}

	@Override
	public Runnable createGroup(final UserData userData, final Runnable added) {
		return new Runnable() {

			@Override
			public void run() {
				String email = userData.email();
				final Map<String, Object> initialData = Maps.stringObjectMap(//
						GroupConstants.takeOnEmailListKey, "<Type here a comma separated list of people you would like to invite to the group\nThe Email below will be sent with $email$ and $group$ replaced by your email, and the group name>",//
						GroupConstants.takeOnFromKey, email,//
						GroupConstants.takeOnSubjectKey, "You are invited to join the SoftwareFM group $group$",//
						GroupConstants.takeOnEmailPattern, IResourceGetter.Utils.getOrException(resourceGetter, GroupConstants.takeOnEmailDefault));
				tryAndInviteToGroup(userData, added, initialData);

			}

			protected void tryAndInviteToGroup(final UserData userData, final Runnable added, final Map<String, Object> initialData) {
				masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IHasControl>() {
					@Override
					public IHasControl apply(Composite from) throws Exception {
						if (userData.softwareFmId == null || userData.crypto == null || userData.email == null)
							throw new IllegalStateException(userData.toString());
						List<KeyAndEditStrategy> keyAndEditStrategy = Arrays.asList(//
								INamesAndValuesEditor.Utils.text(cardConfig, GroupConstants.groupNameKey),//
								INamesAndValuesEditor.Utils.styledText(cardConfig, GroupConstants.takeOnEmailListKey),//
								INamesAndValuesEditor.Utils.text(cardConfig, GroupConstants.takeOnSubjectKey),//
								INamesAndValuesEditor.Utils.styledText(cardConfig, GroupConstants.takeOnEmailPattern));
						return INamesAndValuesEditor.Utils.editor(from, cardConfig, GroupConstants.myGroupsCardType, "Add new group", "", initialData, keyAndEditStrategy, new ICardEditorCallback() {
							@Override
							public void ok(final ICardData cardData) {
								client.post(GroupConstants.takeOnCommandPrefix).//
										addParam(LoginConstants.softwareFmIdKey, userData.softwareFmId).//
										addParam(GroupConstants.groupNameKey, (String) cardData.data().get(GroupConstants.groupNameKey)).//
										addParam(GroupConstants.takeOnEmailPattern, (String) cardData.data().get(GroupConstants.takeOnEmailPattern)).//
										addParam(GroupConstants.takeOnEmailListKey, (String) cardData.data().get(GroupConstants.takeOnEmailListKey)).//
										addParam(GroupConstants.takeOnSubjectKey, (String) cardData.data().get(GroupConstants.takeOnSubjectKey)).//
										addParam(GroupConstants.takeOnFromKey, (String) cardData.data().get(GroupConstants.takeOnFromKey)).//
										execute(new IResponseCallback() {
											@Override
											public void process(IResponse response) {
												if (CommonConstants.okStatusCodes.contains(response.statusCode()))
													added.run();
												else
													masterDetailSocial.createAndShowDetail(TextInBorderWithClick.makeTextFromString(SWT.WRAP | SWT.READ_ONLY, cardConfig, GroupConstants.myGroupsCardType, "Group Creation", "Exception creating group. Click to try again\n" + response.asString(), new Runnable() {
														@Override
														public void run() {
															tryAndInviteToGroup(userData, added, cardData.data());
														}
													}));
											}
										});
							}

							@Override
							public void cancel(ICardData cardData) {
								masterDetailSocial.setDetail(null);
							}

							@Override
							public boolean canOk(Map<String, Object> data) {
								return sharedCanOk(data);
							}

						});
					}
				});
			}
		};
	}

	protected boolean sharedCanOk(Map<String, Object> data) {
		boolean groupNameOk = Strings.nullSafeToString(data.get(GroupConstants.groupNameKey)).length() > 0;
		String emailList = Strings.nullSafeToString(data.get(GroupConstants.takeOnEmailListKey));
		Boolean emailListOk = Iterables.fold(Functions.and(), Iterables.map(Strings.splitIgnoreBlanks(emailList, ","), Strings.isEmailFn()), true);
		return groupNameOk && emailListOk;
	}

	@Override
	public Runnable inviteToGroup(final UserData userData, final Callable<IdAndName> idAndNameGetter, final Runnable invited) {
		return new Runnable() {

			@Override
			public void run() {
				String email = userData.email();
				IdAndName idAndName = Callables.call(idAndNameGetter);
				if (idAndName == null)
					throw new NullPointerException("Cannot invite unless a group is selected.");
				final Map<String, Object> initialData = Maps.stringObjectMap(//
						GroupConstants.groupIdKey, idAndName.id,//
						GroupConstants.groupNameKey, idAndName.name,//
						GroupConstants.takeOnEmailListKey, "<Type here a comma separated list of people you would like to invite to the group\nThe Email below will be sent with $email$ and $group$ replaced by your email, and the group name>",//
						GroupConstants.takeOnFromKey, email,//
						GroupConstants.takeOnSubjectKey, "You are invited to join the SoftwareFM group $group$",//
						GroupConstants.takeOnEmailPattern, IResourceGetter.Utils.getOrException(resourceGetter, GroupConstants.takeOnEmailDefault));
				tryAndInviteToGroup(userData, invited, initialData);

			}

			protected void tryAndInviteToGroup(final UserData userData, final Runnable invited, final Map<String, Object> initialData) {
				masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IHasControl>() {
					@Override
					public IHasControl apply(Composite from) throws Exception {
						if (userData.softwareFmId == null || userData.crypto == null || userData.email == null)
							throw new IllegalStateException(userData.toString());
						List<KeyAndEditStrategy> keyAndEditStrategy = Arrays.asList(//
								INamesAndValuesEditor.Utils.readOnlyText(cardConfig, GroupConstants.groupNameKey),//
								INamesAndValuesEditor.Utils.styledText(cardConfig, GroupConstants.takeOnEmailListKey),//
								INamesAndValuesEditor.Utils.text(cardConfig, GroupConstants.takeOnSubjectKey),//
								INamesAndValuesEditor.Utils.styledText(cardConfig, GroupConstants.takeOnEmailPattern));
						return INamesAndValuesEditor.Utils.editor(from, cardConfig, GroupConstants.myGroupsCardType, "Add new group", "", initialData, keyAndEditStrategy, new ICardEditorCallback() {
							@Override
							public void ok(final ICardData cardData) {
								client.post(GroupConstants.inviteCommandPrefix).//
										addParam(LoginConstants.softwareFmIdKey, userData.softwareFmId).//
										addParam(GroupConstants.groupIdKey, (String) cardData.data().get(GroupConstants.groupIdKey)).//
										addParam(GroupConstants.takeOnEmailPattern, (String) cardData.data().get(GroupConstants.takeOnEmailPattern)).//
										addParam(GroupConstants.takeOnEmailListKey, (String) cardData.data().get(GroupConstants.takeOnEmailListKey)).//
										addParam(GroupConstants.takeOnSubjectKey, (String) cardData.data().get(GroupConstants.takeOnSubjectKey)).//
										addParam(GroupConstants.takeOnFromKey, (String) cardData.data().get(GroupConstants.takeOnFromKey)).//
										execute(new IResponseCallback() {
											@Override
											public void process(IResponse response) {
												if (CommonConstants.okStatusCodes.contains(response.statusCode()))
													invited.run();
												else
													masterDetailSocial.createAndShowDetail(TextInBorderWithClick.makeTextFromString(SWT.WRAP | SWT.READ_ONLY, cardConfig, GroupConstants.myGroupsCardType, "Group Creation", "Exception creating group. Click to try again\n" + response.asString(), new Runnable() {
														@Override
														public void run() {
															tryAndInviteToGroup(userData, invited, cardData.data());
														}
													}));
											}
										});
							}

							@Override
							public void cancel(ICardData cardData) {
								masterDetailSocial.setDetail(null);
							}

							@Override
							public boolean canOk(Map<String, Object> data) {
								return sharedCanOk(data);
							}

						});
					}
				});
			}
		};
	}

	@Override
	public Runnable acceptInvitation(UserData userData) {
		return Runnables.sysout("acceptInvitation");
	}

	@Override
	public Runnable deleteGroup(UserData userData) {
		return Runnables.sysout("deleteGroup");
	}

}
