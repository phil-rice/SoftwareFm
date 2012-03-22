package org.softwareFm.eclipse.mysoftwareFm.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.api.ICrowdSourceReadWriteApi;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.IResponse;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFoldFunction;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.eclipse.mysoftwareFm.IGroupClientOperations;
import org.softwareFm.eclipse.mysoftwareFm.IdNameAndStatus;
import org.softwareFm.jarAndClassPath.api.UserData;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.composites.TextInBorderWithClick;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.INamesAndValuesEditor;
import org.softwareFm.swt.editors.KeyAndEditStrategy;
import org.softwareFm.swt.explorer.IMasterDetailSocial;

@SuppressWarnings("Need to externalise these string")
public class GroupClientOperations implements IGroupClientOperations {

	private final IMasterDetailSocial masterDetailSocial;
	private final CardConfig cardConfig;
	private final IHttpClient client;
	private final IResourceGetter resourceGetter;

	public GroupClientOperations(IMasterDetailSocial masterDetailSocial, CardConfig cardConfig, ICrowdSourceReadWriteApi readWriteApi) {
		this.masterDetailSocial = masterDetailSocial;
		this.cardConfig = cardConfig;
		this.client = readWriteApi.access(IHttpClient.class, Functions.<IHttpClient, IHttpClient>identity());
		this.resourceGetter = Functions.call(cardConfig.resourceGetterFn, GroupConstants.myGroupsCardType);
	}

	@Override
	public Runnable createGroup(final UserData userData, final ICallback<String> added) {
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

			protected void tryAndInviteToGroup(final UserData userData, final ICallback<String> added, final Map<String, Object> initialData) {
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
													ICallback.Utils.call(added, response.asString());
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
		boolean emailOk = Strings.nullSafeToString(data.get(GroupConstants.takeOnEmailPattern)).length() > 0;
		boolean subjectOk = Strings.nullSafeToString(data.get(GroupConstants.takeOnSubjectKey)).length() > 0;
		String emailList = Strings.nullSafeToString(data.get(GroupConstants.takeOnEmailListKey));
		Boolean emailListOk = Iterables.fold(Functions.and(), Iterables.map(Strings.splitIgnoreBlanks(emailList, ","), Strings.isEmailFn()), true);
		return groupNameOk && emailListOk && emailOk && subjectOk;
	}

	@Override
	public Runnable inviteToGroup(final UserData userData, final Callable<IdNameAndStatus> idNameStatusGetter, final ICallback<String> invited) {
		return new Runnable() {

			@Override
			public void run() {
				String email = userData.email();
				IdNameAndStatus idNameAndStatus = Callables.call(idNameStatusGetter);
				if (idNameAndStatus == null)
					throw new NullPointerException("Cannot invite unless a group is selected.");
				final Map<String, Object> initialData = Maps.stringObjectMap(//
						GroupConstants.groupIdKey, idNameAndStatus.id,//
						GroupConstants.groupNameKey, idNameAndStatus.name,//
						GroupConstants.takeOnEmailListKey, "<Type here a comma separated list of people you would like to invite to the group\nThe Email below will be sent with $email$ and $group$ replaced by your email, and the group name>",//
						GroupConstants.takeOnFromKey, email,//
						GroupConstants.takeOnSubjectKey, "You are invited to join the SoftwareFM group $group$",//
						GroupConstants.takeOnEmailPattern, IResourceGetter.Utils.getOrException(resourceGetter, GroupConstants.takeOnEmailDefault));
				tryAndInviteToGroup(userData, invited, initialData);

			}

			protected void tryAndInviteToGroup(final UserData userData, final ICallback<String> invited, final Map<String, Object> initialData) {
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
								final String groupId = (String) cardData.data().get(GroupConstants.groupIdKey);
								client.post(GroupConstants.inviteCommandPrefix).//
										addParam(LoginConstants.softwareFmIdKey, userData.softwareFmId).//
										addParam(GroupConstants.groupIdKey, groupId).//
										addParam(GroupConstants.takeOnEmailPattern, (String) cardData.data().get(GroupConstants.takeOnEmailPattern)).//
										addParam(GroupConstants.takeOnEmailListKey, (String) cardData.data().get(GroupConstants.takeOnEmailListKey)).//
										addParam(GroupConstants.takeOnSubjectKey, (String) cardData.data().get(GroupConstants.takeOnSubjectKey)).//
										addParam(GroupConstants.takeOnFromKey, (String) cardData.data().get(GroupConstants.takeOnFromKey)).//
										execute(new IResponseCallback() {
											@Override
											public void process(IResponse response) {
												if (CommonConstants.okStatusCodes.contains(response.statusCode()))
													ICallback.Utils.call(invited, groupId);
												else
													masterDetailSocial.createAndShowDetail(TextInBorderWithClick.makeTextFromString(SWT.WRAP | SWT.READ_ONLY, cardConfig, GroupConstants.myGroupsCardType, "Group Invitation", "Exception inviting to group. Click to try again\n" + response.asString(), new Runnable() {
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
	public Runnable acceptInvitation(final UserData userData, final Callable<IdNameAndStatus> idNameStatusGetter, final ICallback<String> showMyGroups) {
		return new Runnable() {
			@Override
			public void run() {
				IdNameAndStatus idNameAndStatus = Callables.call(idNameStatusGetter);
				if (idNameAndStatus == null)
					throw new NullPointerException("IdNameAndStatus is null");
				final String groupId = idNameAndStatus.id;
				if (groupId == null)
					throw new NullPointerException("group id is null");
				client.post(GroupConstants.acceptInvitePrefix).//
						addParam(LoginConstants.softwareFmIdKey, userData.softwareFmId).//
						addParam(GroupConstants.groupIdKey, groupId).//
						addParam(GroupConstants.membershipStatusKey, GroupConstants.memberStatus).//
						execute(new IResponseCallback() {
							@Override
							public void process(IResponse response) {

								if (CommonConstants.okStatusCodes.contains(response.statusCode()))
									ICallback.Utils.call(showMyGroups, groupId);
								else
									masterDetailSocial.createAndShowDetail(TextInBorderWithClick.makeTextFromString(SWT.WRAP | SWT.READ_ONLY, cardConfig, GroupConstants.myGroupsCardType, "Accept", "Exception accepting. Click to try again\n\n" + response.asString(), new Runnable() {
										@Override
										public void run() {
											ICallback.Utils.call(showMyGroups, groupId);
										}
									}));
							}
						});
			}
		};
	}

	@Override
	public Runnable kickMember(final UserData userData, final Callable<IdNameAndStatus> idNameStatusGetter, final Callable<List<Map<String, Object>>> objectMembershipGetter, final ICallback<String> showMyGroups) {
		return new Runnable() {
			@Override
			public void run() {
				IdNameAndStatus idNameAndStatus = Callables.call(idNameStatusGetter);
				List<Map<String, Object>> objectMembershipDetails = Callables.call(objectMembershipGetter);
				if (objectMembershipDetails == null)
					throw new NullPointerException("objectMembershipDetails is null");
				String otherIds = Iterables.fold(new IFoldFunction<Map<String, Object>, String>() {
					@Override
					public String apply(Map<String, Object> value, String initial) {
						String id = (String) value.get(LoginConstants.softwareFmIdKey);
						if (initial.length() == 0)
							return id;
						else
							return initial + "," + id;
					}
				}, objectMembershipDetails, "");
				if (otherIds == null)
					throw new NullPointerException("otherId is null, " + objectMembershipDetails);
				if (idNameAndStatus == null)
					throw new NullPointerException("idNameAndStatus is null");
				final String groupId = idNameAndStatus.id;
				if (groupId == null)
					throw new NullPointerException("group id is null");
				client.post(GroupConstants.kickFromGroupPrefix).//
						addParam(LoginConstants.softwareFmIdKey, userData.softwareFmId).//
						addParam(GroupConstants.objectSoftwareFmId, otherIds).//
						addParam(GroupConstants.groupIdKey, groupId).//
						execute(new IResponseCallback() {
							@Override
							public void process(IResponse response) {

								if (CommonConstants.okStatusCodes.contains(response.statusCode()))
									ICallback.Utils.call(showMyGroups, groupId);
								else
									masterDetailSocial.createAndShowDetail(TextInBorderWithClick.makeTextFromString(SWT.WRAP | SWT.READ_ONLY, cardConfig, GroupConstants.myGroupsCardType, "Kick", "Exception kicking. Click to try again\n\n" + response.asString(), new Runnable() {
										@Override
										public void run() {
											ICallback.Utils.call(showMyGroups, groupId);
										}
									}));
							}
						});
			}
		};
	}

	@Override
	public Runnable leaveGroup(final UserData userData, final ICallback<String> showMyGroups, final Callable<IdNameAndStatus> idNameStatusGetter) {
		return new Runnable() {
			@Override
			public void run() {
				IdNameAndStatus idNameAndStatus = Callables.call(idNameStatusGetter);
				if (idNameAndStatus == null)
					throw new NullPointerException("idNameAndStatus is null");
				final String groupId = idNameAndStatus.id;
				if (groupId == null)
					throw new NullPointerException("group id is null");
				client.post(GroupConstants.leaveGroupPrefix).//
						addParam(LoginConstants.softwareFmIdKey, userData.softwareFmId).//
						addParam(GroupConstants.groupIdKey, groupId).//
						execute(new IResponseCallback() {
							@Override
							public void process(IResponse response) {
								if (CommonConstants.okStatusCodes.contains(response.statusCode()))
									ICallback.Utils.call(showMyGroups, groupId);
								else
									masterDetailSocial.createAndShowDetail(TextInBorderWithClick.makeTextFromString(SWT.WRAP | SWT.READ_ONLY, cardConfig, GroupConstants.myGroupsCardType, "Kick", "Exception Leaving. Click to try again\n\n" + response.asString(), new Runnable() {
										@Override
										public void run() {
											ICallback.Utils.call(showMyGroups, groupId);
										}
									}));
							}
						});
			}
		};
	}
}
