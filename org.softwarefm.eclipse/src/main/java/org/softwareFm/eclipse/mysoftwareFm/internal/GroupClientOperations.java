package org.softwareFm.eclipse.mysoftwareFm.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.response.IResponse;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.mysoftwareFm.IGroupClientOperations;
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

	public GroupClientOperations(IMasterDetailSocial masterDetailSocial, CardConfig cardConfig, IHttpClient client) {
		this.masterDetailSocial = masterDetailSocial;
		this.cardConfig = cardConfig;
		this.client = client;
	}

	@Override
	public Runnable createGroup(final UserData userData) {
		return new Runnable() {

			@Override
			public void run() {
				masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IHasControl>() {
					@Override
					public IHasControl apply(Composite from) throws Exception {
						if (userData.softwareFmId == null || userData.crypto == null || userData.email == null)
							throw new IllegalStateException(userData.toString());
						String email = userData.email();
						List<KeyAndEditStrategy> keyAndEditStrategy = Arrays.asList(//
								INamesAndValuesEditor.Utils.text(cardConfig, GroupConstants.groupNameKey),//
								INamesAndValuesEditor.Utils.styledText(cardConfig, GroupConstants.takeOnEmailListKey),//
								INamesAndValuesEditor.Utils.styledText(cardConfig, GroupConstants.takeOnEmailPattern)//
								);
						return INamesAndValuesEditor.Utils.editor(from, cardConfig, GroupConstants.myGroupsCardType, "Add new group", "", Maps.stringObjectMap(//
								GroupConstants.takeOnEmailListKey, "<Type here a comma separated list of people you would like to invite to the group\nThe Email below will be sent with $email$ and $group$ replaced by your email, and the group name>",//
								GroupConstants.takeOnFromKey, email,//
								GroupConstants.takeOnSubjectKey, "You are invited to join the SoftwareFM group $group$",//
								GroupConstants.takeOnEmailPattern, "Dear $email$,\nYou have been invited to join the SoftwareFm group $group$"), keyAndEditStrategy, new ICardEditorCallback() {
							@Override
							public void ok(ICardData cardData) {
								masterDetailSocial.setDetail(null);
								client.post(GroupConstants.takeOnCommandPrefix).//
										addParam(GroupConstants.groupNameKey, (String) cardData.data().get(GroupConstants.groupNameKey)).//
										addParam(GroupConstants.takeOnEmailPattern, (String) cardData.data().get(GroupConstants.takeOnEmailPattern)).//
										addParam(GroupConstants.takeOnEmailListKey, (String) cardData.data().get(GroupConstants.takeOnEmailListKey)).//
										addParam(GroupConstants.takeOnSubjectKey, (String) cardData.data().get(GroupConstants.takeOnSubjectKey)).//
										addParam(GroupConstants.takeOnFromKey, (String) cardData.data().get(GroupConstants.takeOnFromKey)).//
										execute(new IResponseCallback() {
											@Override
											public void process(IResponse response) {
												masterDetailSocial.createAndShowDetail(TextInBorderWithClick.makeTextFromString(SWT.WRAP | SWT.READ_ONLY, cardConfig, GroupConstants.myGroupsCardType, "Group Creation", response.asString(), Runnables.noRunnable));
											}
										});
							}

							@Override
							public void cancel(ICardData cardData) {
								masterDetailSocial.setDetail(null);
							}

							@Override
							public boolean canOk(Map<String, Object> data) {
								boolean groupNameOk = Strings.nullSafeToString(data.get(GroupConstants.groupNameKey)).length() > 0;
								String emailList = Strings.nullSafeToString(data.get(GroupConstants.takeOnEmailListKey));
								Boolean emailListOk = Iterables.fold(Functions.and(), Iterables.map(Strings.splitIgnoreBlanks(emailList, ","), Strings.isEmailFn()), true);
								return groupNameOk && emailListOk;
							}
						});
					}
				});

			}
		};
	}

	@Override
	public Runnable inviteToGroup(UserData userData) {
		return Runnables.sysout("inviteToGroup");
	}

	@Override
	public Runnable acceptInvitation(UserData userData) {
		return Runnables.sysout("acceptInvitation");
	}


}
