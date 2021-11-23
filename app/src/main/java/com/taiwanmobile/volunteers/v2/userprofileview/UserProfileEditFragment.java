package com.taiwanmobile.volunteers.v2.userprofileview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.api.FubonUnbindTask;
import com.taiwanmobile.volunteers.v1.api.TWMUnbindTask;
import com.taiwanmobile.volunteers.v1.loginview.FubonBindFragment;
import com.taiwanmobile.volunteers.v1.loginview.TWMBindFragment;
import com.taiwanmobile.volunteers.v1.util.PatternUtil;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.SupportFragment;
import com.taiwanmobile.volunteers.v2.api.DeleteUserLicenseTask;
import com.taiwanmobile.volunteers.v2.api.UpdateUserLicenseTask;
import com.taiwanmobile.volunteers.v2.api.UpdateUserProfileTask;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.database.UserLicenseImageDAO;
import com.taiwanmobile.volunteers.v2.dialog.UserPhotoDialog;
import com.taiwanmobile.volunteers.v2.dialog.UserSkillDialog;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import com.taiwanmobile.volunteers.v2.utils.ImageCache;
import com.taiwanmobile.volunteers.v2.utils.ImageUtils;
import com.taiwanmobile.volunteers.v2.utils.UriUtils;
import com.taiwanmobile.volunteers.v2.utils.roundedimageview.RoundedTransformationBuilder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

public class UserProfileEditFragment extends SupportFragment implements
		OnClickListener {
	final String TAG = getClass().getSimpleName();

	private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 2332;
    private static final int REQUEST_PERMISSION = 2333;
	private static final Pattern INVALID_CHARS_PATTERN = Pattern
			.compile("^.*[`;,./\\-=|:?~!$^&()#@*+%{}<>\\[\\]|\"\\_].*$");
	// public static UserProfileEditFragment fragmentObj;
	// private long userId;
	// public static ListView eventListView;
	private UserAccountDAO mUserAccountObject;

	private Uri outputFileUri;
	private Pair<Uri, Uri> cached;


    private static final int IMAGE_SELECT_TYPE_PROFILE = 0;
    private static final int IMAGE_SELECT_TYPE_LICENSE = 1;
	private int imageSelectType = IMAGE_SELECT_TYPE_PROFILE;

    UserPhotoDialog userPhotoDialog;

    TextView fubonBindState;
    TextView fubonBindLink;
    TextView twmBindState;
    TextView twmBindLink;

//	public UserProfileEditFragment(UserAccountDAO obj) {
//		mUserAccountObject = obj;
//	}

	public void setUserAccountObject(UserAccountDAO obj){
        Log.d(TAG, "setUserAccountObject:" + obj.toString());
        mUserAccountObject = obj;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();

		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_user_profile_edit, null);

		ImageView user_icon = (ImageView) head
				.findViewById(R.id.fragment_user_profile_edit_iv_photo);
		String imageFilePath = mUserAccountObject.icon;
		if (!imageFilePath.isEmpty()) {
			int image_size = (int) (100 * getResources().getDisplayMetrics().density);
			Transformation transformation = new RoundedTransformationBuilder()
					.oval(true).build();

			Picasso.with(getActivity())
					//.load(BackendContract.DEPLOYMENT_URL + imageFilePath)
					.load(StaticResUtil.checkUrl(imageFilePath, false, TAG))
					.resize(image_size, image_size).centerCrop()
					.transform(transformation).into(user_icon);
		}

		((EditText) head.findViewById(R.id.fragment_user_profile_edit_et_name))
				.setText(mUserAccountObject.displayName);
		((EditText) head.findViewById(R.id.fragment_user_profile_edit_et_phone))
				.setText(mUserAccountObject.phone);
		((EditText) head
				.findViewById(R.id.fragment_user_profile_edit_et_about_me))
				.setText(mUserAccountObject.aboutMe);
		((EditText) head
				.findViewById(R.id.fragment_user_profile_edit_et_service_item))
				.setText(mUserAccountObject.skills);
		((EditText) head
				.findViewById(R.id.fragment_user_profile_edit_et_service_area))
				.setText(mUserAccountObject.interest);
		((Switch) head
				.findViewById(R.id.fragment_user_profile_edit_switch_public))
				.setChecked(mUserAccountObject.isPublic);

		head.findViewById(R.id.fragment_user_profile_edit_iv_photo)
				.setOnClickListener(this);
		head.findViewById(R.id.fragment_user_profile_edit_btn_confirm)
				.setOnClickListener(this);
		head.findViewById(R.id.fragment_user_profile_edit_btn_license)
                .setOnClickListener(this);

        head.findViewById(R.id.fragment_user_profile_edit_et_service_area).setOnClickListener(this);
        head.findViewById(R.id.fragment_user_profile_edit_et_service_item).setOnClickListener(this);




        fubonBindState =  head.findViewById(R.id.fubon_bind_state);
        fubonBindLink = head.findViewById(R.id.fubon_bind_link);
        twmBindState = head.findViewById(R.id.twm_bind_state);
        twmBindLink = head.findViewById(R.id.twm_bind_link);
        fubonBindLink.setOnClickListener(this);
        twmBindLink.setOnClickListener(this);

        updateBindViews();

        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {


                if(getActivity() == null || getActivity().isFinishing() || isDetached()){
                    return;
                }
                switch (msg.what){
                    case MSG_TWM_UNBIND_SUCCESS:
                        MyPreferenceManager.setIsTwm(getActivity(), false);
                        updateBindViews();
                        break;
                    case MSG_FUBON_UNBIND_SUCCESS:
                        MyPreferenceManager.setIsFubon(getActivity(), false);
                        updateBindViews();
                        break;
                    case MSG_FUBON_UNBIND_FAILED:
                    case MSG_TWM_UNBIND_FAILED:
                        DialogFactory.TitleMessageButtonSmallDialog dialog = new DialogFactory.TitleMessageButtonSmallDialog(getActivity(), "錯誤",
                                "解除綁定失敗");
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                            }
                        });
                        dialog.show();
                        break;
                    default:
                        break;

                }
                super.handleMessage(msg);
            }
        };

		return head;
	}


	public void updateBindViews(){
        fubonBindState.setText(isFubonBind() ? R.string.state_bind : R.string.state_unbind);
        fubonBindState.setTextColor(isFubonBind() ? getResources().getColor(R.color.twmf_v1_green) : getResources().getColor(R.color.twmf_v1_red));

        fubonBindLink.setText(isFubonBind() ? R.string.link_unbind : R.string.link_bind);

        twmBindState.setText(isTWMBind() ? R.string.state_bind : R.string.state_unbind);
        twmBindState.setTextColor(isTWMBind() ? getResources().getColor(R.color.twmf_v1_green) : getResources().getColor(R.color.twmf_v1_red));

        twmBindLink.setText(isTWMBind() ? R.string.link_unbind : R.string.link_bind);
    }


    public static final int MSG_TWM_UNBIND_SUCCESS =  0x645;
	public static final int MSG_TWM_UNBIND_FAILED =  0x646;
    public static final int MSG_FUBON_UNBIND_SUCCESS =  0x647;
	public static final int MSG_FUBON_UNBIND_FAILED =  0x648;
	Handler mHandler;

	public boolean isFubonBind(){
       return MyPreferenceManager.isFubon(getActivity());
    }

    public boolean isTWMBind(){
        return MyPreferenceManager.isTwm(getActivity());
    }

    @Override
	public void onClick(View v) {
	    Log.d(TAG, "on touch");
		switch (v.getId()) {
		case R.id.fragment_user_profile_edit_iv_photo:
		    onUserPhotoDidClick(v);
			break;
		case R.id.fragment_user_profile_edit_btn_confirm:

            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);

			Boolean isDataValid = true;
			try {
				if (cached != null) {
                    File imageFile = new File(cached.second.getPath());
                    Uri uris = Uri.fromFile(imageFile);
                    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString());
                    String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

                    builder.addFormDataPart(
				            UserAccountDAO.POST_KEY_IMAGE,
                            UserAccountDAO.POST_KEY_IMAGE,
                            RequestBody.create(imageFile, MediaType.parse(mime))
                    );
				}

				EditText oldPasswordEditText = (EditText) getView()
						.findViewById(
								R.id.fragment_user_profile_edit_et_old_password);
				EditText passwordEditText = (EditText) getView().findViewById(
						R.id.fragment_user_profile_edit_et_password);
				EditText passwordConfirmEditText = (EditText) getView()
						.findViewById(
								R.id.fragment_user_profile_edit_et_password_confirm);
				if (StringUtils.isNotBlank(oldPasswordEditText.getText()
						.toString())
						|| StringUtils.isNotBlank(passwordEditText.getText()
								.toString())
						|| StringUtils.isNotBlank(passwordConfirmEditText
								.getText().toString())) {
                    if (StringUtils.isBlank(oldPasswordEditText.getText()
                            .toString())) {
                        oldPasswordEditText.setText("");
                        oldPasswordEditText.setHintTextColor(getResources()
                                .getColor(R.color.twmf_orange));
                        oldPasswordEditText.setHint("請輸入您的舊密碼");
                        isDataValid = false;
                    }
                    if (!StringUtils.equals(passwordEditText.getText()
                            .toString(), passwordConfirmEditText.getText()
                            .toString())) {
                        passwordEditText.setText("");
                        passwordEditText.setHintTextColor(getResources()
                                .getColor(R.color.twmf_orange));
                        passwordEditText.setHint("密碼不相符");

                        passwordConfirmEditText.setText("");
                        passwordConfirmEditText.setHintTextColor(getResources()
                                .getColor(R.color.twmf_orange));
                        passwordConfirmEditText.setHint("密碼不相符");
                        isDataValid = false;
                    } else if(!PatternUtil.checkPwd(passwordEditText.getText().toString())){
                        passwordEditText.setText("");
                        passwordEditText.setHintTextColor(getResources()
                                .getColor(R.color.twmf_orange));
                        passwordEditText.setHint("請輸入8-12碼英數字混合密碼");

                        passwordConfirmEditText.setText("");
                        passwordConfirmEditText.setHintTextColor(getResources()
                                .getColor(R.color.twmf_orange));
                        passwordConfirmEditText.setHint("請輸入8-12碼英數字混合密碼");
                        isDataValid = false;
                    }else {
                        builder.addFormDataPart(UserAccountDAO.POST_KEY_OLD_PASSWORD, oldPasswordEditText.getText().toString());
                        builder.addFormDataPart(UserAccountDAO.POST_KEY_PASSWORD, passwordConfirmEditText.getText().toString());
					}
				}
				// set user name
				EditText nameEditText = (EditText) getView().findViewById(
						R.id.fragment_user_profile_edit_et_name);
				if (nameEditText.getText().toString().matches(".*\\d.*")
						|| INVALID_CHARS_PATTERN.matcher(
								nameEditText.getText().toString()).matches()) {
					nameEditText.setText("");
					nameEditText.setHint("姓名不可有數字或符號");
					nameEditText.setHintTextColor(getResources().getColor(
							R.color.twmf_orange));
					isDataValid = false;
				} else {
                    builder.addFormDataPart(UserAccountDAO.POST_KEY_NAME, nameEditText.getText().toString());
				}
				// set user phone
				EditText phoneEditText = (EditText) getView().findViewById(
						R.id.fragment_user_profile_edit_et_phone);
                builder.addFormDataPart(UserAccountDAO.POST_KEY_PHONE, phoneEditText.getText().toString());

                // set user about_me
				EditText aboutMeEditText = (EditText) getView().findViewById(
						R.id.fragment_user_profile_edit_et_about_me);

                builder.addFormDataPart(UserAccountDAO.POST_KEY_ABOUT_ME, aboutMeEditText.getText().toString());

                // set user service item
				EditText serviceItemEditText = (EditText) getView().findViewById(
						R.id.fragment_user_profile_edit_et_service_item);
                builder.addFormDataPart(UserAccountDAO.POST_KEY_SERVICE_ITEM, serviceItemEditText.getText().toString());

                // set user service area
				EditText serviceAreaEditText = (EditText) getView().findViewById(
						R.id.fragment_user_profile_edit_et_service_area);
                builder.addFormDataPart(UserAccountDAO.POST_KEY_SERVICE_AREA, serviceAreaEditText.getText().toString());

                // set user is_public
				Switch isPublicSwitch = (Switch) getView().findViewById(
						R.id.fragment_user_profile_edit_switch_public);
                builder.addFormDataPart(UserAccountDAO.POST_KEY_IS_PUBLIC, String.valueOf(isPublicSwitch.isChecked()));

				if (!isDataValid) {
					break;
				} else {
					new UpdateUserProfileTask(getActivity(), this, builder.build())
							.execute();
				}

			} catch (Exception ex) {
				Log.e(TAG, Log.getStackTraceString(ex));
			}

			break;
            case R.id.fragment_user_profile_edit_et_service_area:
                onServiceAreaDidClick();
                break;

            case R.id.fragment_user_profile_edit_et_service_item:
                onServiceItemDidClick();
                break;
            case R.id.fragment_user_profile_edit_btn_license:
                onLicenseDidClick();
                break;
            case R.id.fubon_bind_link:
                if(!isFubonBind()){
                    FubonBindFragment fragment1 = new FubonBindFragment();
                    FragHelper.replace(getActivity(), fragment1);
                }else{
                    FubonUnbindTask task = new FubonUnbindTask(getActivity());
                    task.setCallback(mHandler);
                    task.execute();
                }
                break;
            case R.id.twm_bind_link:
                if(!isTWMBind()){
                    TWMBindFragment fragment2 = new TWMBindFragment();
                    FragHelper.replace(getActivity(), fragment2);
                }else{
                    TWMUnbindTask task = new TWMUnbindTask(getActivity());
                    task.setCallback(mHandler);
                    task.execute();
                }
                break;

        }
	}

	private void onUserPhotoDidClick(@NonNull  View v) {
	    imageSelectType = IMAGE_SELECT_TYPE_PROFILE;
	    showUserPhotoChooser();
    }

    private void showUserPhotoChooser(){
        outputFileUri = ImageUtils.createCaptureUri(getActivity());
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager
                .queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(
                    res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            Log.d(TAG, "outputFileUri: " + outputFileUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType(ImageUtils.INTENT_TYPE_SELECT_IMAGE);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent,
                getString(R.string.image_select_intent));
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[] {}));

        Log.d(TAG,"chooserIntent 1");
        startActivityForResult(chooserIntent,
                SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
        Log.d(TAG,"chooserIntent 2");
    }

    private void onLicenseDidClick(){

        userPhotoDialog = new UserPhotoDialog(getActivity(), "專業證照", mUserAccountObject.images);
        userPhotoDialog.setDeletable(true);
        userPhotoDialog.setActionListener(new UserPhotoDialog.OnActionListener() {
            @Override
            public void onDelete(UserLicenseImageDAO dao) {
                Log.d(TAG, "delete license: " + dao.id);
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                try {
                    builder.addFormDataPart(UserLicenseImageDAO.POST_KEY_DELETE_ID, dao.id.toString());

                    new DeleteUserLicenseTask(getActivity(), UserProfileEditFragment.this, builder.build(), mUserAccountObject)
                            .execute();
                } catch (Exception ex) {
                    Log.e(TAG, Log.getStackTraceString(ex));
                }
            }

            @Override
            public void onAdd() {
                imageSelectType = IMAGE_SELECT_TYPE_LICENSE;
                showUserPhotoChooser();
            }
        });
        userPhotoDialog.show();
    }

    private void onServiceAreaDidClick(){
        String[] mapping = getResources().getStringArray(R.array.county_mapping);
        if(getView() == null){
            return;
        }
        TextView tv = (TextView) getView().findViewById(R.id.fragment_user_profile_edit_et_service_area);
        onSkillSelectionDidClick(mapping, "可服務區域", tv);
    }

    private void onServiceItemDidClick(){
        String[] mapping = getResources().getStringArray(R.array.service_item_type);
        if(getView() == null){
            return;
        }
        TextView tv = (TextView) getView().findViewById(R.id.fragment_user_profile_edit_et_service_item);
        onSkillSelectionDidClick(mapping, "可服務項目", tv);
    }


    private void onSkillSelectionDidClick(@NonNull String[] mapping, @NonNull String title, @NonNull final TextView textView){
        Log.d(TAG, "onServiceAreaDidClick");

//		TextView tv = (TextView) getView().findViewById(R.id.fragment_event_register_et_service_area);
        UserSkillDialog dialog = new UserSkillDialog(getActivity(), title,
                textView.getText().toString().split(","), mapping);
        dialog.setSubTitle("最多複選三個");
        dialog.setSelectionLimit(3);
        dialog.setSelectable(true);
        dialog.setOnCompleteListener(new UserSkillDialog.OnCompleteListener() {
            @Override
            public void onComplete(DialogInterface dialog, String[] allItems, boolean[] selectedItems) {
                if(getView() == null){
                    return;
                }
//				TextView tv = (TextView) getView().findViewById(R.id.fragment_event_register_et_service_area);
                StringBuilder list = new StringBuilder();
                boolean first = true;
                for(int i = 0; i < allItems.length; i++){
                    if(!selectedItems[i]){
                        continue;
                    }
                    if(first){
                        first = false;
                    }else{
                        list.append(", ");
                    }
                    list.append(allItems[i].trim());
                }
                textView.setText(list);
                dialog.dismiss();
            }
        });
        dialog.show();

    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "oAR: requestCode: " + requestCode + " resultCode: " + resultCode + " data: " + data);

        if(requestCode != SELECT_IMAGE_ACTIVITY_REQUEST_CODE){
            return;
        }
        if(resultCode == 0){
            if(data == null){
                // no data
                if (PermissionChecker.checkSelfPermission(getActivity(), CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "no camera permission");
                    if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), CAMERA)){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("資訊")
                                .setMessage("需要相機權限才能拍照")
                                .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, REQUEST_PERMISSION);
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    }else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA}, REQUEST_PERMISSION);
                    }
                }
            }
            return;
        }

        if(getView() == null){
            return;
        }
        if(data == null){
            Crashlytics.log("data == null");
            return;
        }
        Bundle extra = data.getExtras();
        Log.d(TAG, "extra: " + extra);
        if (extra != null && extra.containsKey("data")) {
            // camera with data
            Bitmap bitmap = (Bitmap) extra.get("data");
            ImageView iv = getView().findViewById(
                    R.id.fragment_user_profile_edit_iv_photo);
            iv.setImageBitmap(bitmap);
            return;
        }
        File cameraFile = new File(outputFileUri.getPath());
        if(cameraFile.exists()){
            Log.d(TAG, "camera file exist");
            try {
                cached = ImageCache.put(getActivity(), outputFileUri);
                Boolean success = cameraFile.delete();
                if(!success){
                    Log.w(TAG, "delete file failure");
                }
                if(imageSelectType == IMAGE_SELECT_TYPE_PROFILE){
                    ImageView iv = getView().findViewById(
                            R.id.fragment_user_profile_edit_iv_photo);
                    iv.setImageURI(cached.second);
                }else{
                    uploadLicense();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        Log.d(TAG, "camera file not exist");
        Log.d(TAG, "action: " + data.getAction());
        Uri selectedImageUri = data.getData();
        if(selectedImageUri != null){
            Log.d(TAG, "selector");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                    PermissionChecker.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "no read file permission");
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), READ_EXTERNAL_STORAGE)){
                    new AlertDialog.Builder(getActivity())
                            .setTitle("資訊")
                            .setMessage("需要讀取權限才能讀取照片")
                            .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ){
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }else {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                    }
                }
            }
            Uri uri = Uri.fromFile(new File(UriUtils
                    .getRealPathFromURI(getActivity(),
                            selectedImageUri)));
            try {

                cached = ImageCache.put(getActivity(), uri);
                if(imageSelectType == IMAGE_SELECT_TYPE_PROFILE){
                    ImageView iv = getView().findViewById(
                            R.id.fragment_user_profile_edit_iv_photo);
                    iv.setImageURI(cached.second);
                }else{
                    uploadLicense();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        Crashlytics.log("not camera nor file selector");
	}

	private void uploadLicense(){
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        try {
            if (cached != null) {
                File imageFile = new File(cached.second.getPath());
                Uri uris = Uri.fromFile(imageFile);
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString());
                String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

                builder.addFormDataPart(
                        UserLicenseImageDAO.POST_KEY_ADD_LICENSE,
                        UserLicenseImageDAO.POST_KEY_ADD_LICENSE,
                        RequestBody.create(imageFile, MediaType.parse(mime))
                );
            }
            new UpdateUserLicenseTask(getActivity(), this, builder.build(), mUserAccountObject)
                    .execute();
        } catch (Exception ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
        }
    }

    public void onUploadLicenseComplete(){
	    if(userPhotoDialog != null) {
            userPhotoDialog.dismiss();
        }
        onLicenseDidClick();
    }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        showActionBarTabs();
	}


}
