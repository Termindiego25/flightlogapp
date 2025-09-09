package es.diegosr.flightlogapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import org.w3c.dom.Text;

import java.io.IOException;

import es.diegosr.flightlogapp.BDAdapter;
import es.diegosr.flightlogapp.MainActivity;
import es.diegosr.flightlogapp.R;
import es.diegosr.flightlogapp.interfaces.OnUserListener;
import es.diegosr.flightlogapp.pojos.User;

public class FragmentProfile extends Fragment implements View.OnClickListener {
    BDAdapter bdAdapter;
    Cursor cursor;
    FloatingActionButton floatingProfile;
    ImageButton profileImage;
    TextInputEditText profileName, profileLastname, profileUserType, profileUsername, profilePassword, profileRepeatPassword;
    TextInputLayout profileUsernameLayout, profilePasswordLayout, profileRepeatPasswordLayout;
    Spinner profileLandingField;
    Boolean edit;
    private OnUserListener userListener;
    User localUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); bdAdapter = new BDAdapter(getContext());
        cursor = bdAdapter.getProfile();
        edit = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage = rootView.findViewById(R.id.profileImage);
        profileName = rootView.findViewById(R.id.profileName);
        profileLastname = rootView.findViewById(R.id.profileLastname);
        profileLandingField = rootView.findViewById(R.id.profileLandingField);
        profileUserType = rootView.findViewById(R.id.profileUserType);
        profileUsername = rootView.findViewById(R.id.profileUsername);
        profileUsernameLayout = rootView.findViewById(R.id.profileUsernameLayout);
        profilePassword = rootView.findViewById(R.id.profilePassword);
        profilePasswordLayout = rootView.findViewById(R.id.profilePasswordLayout);
        profileRepeatPassword = rootView.findViewById(R.id.profileRepeatPassword);
        profileRepeatPasswordLayout = rootView.findViewById(R.id.profileRepeatPasswordLayout);
        floatingProfile = rootView.findViewById(R.id.floatingProfile);
        localUser = bdAdapter.cursorToUser(cursor);

        if(cursor != null && cursor.moveToFirst()) {
            profileUsername.setText(localUser.getUsername());
            profilePassword.setText(localUser.getPassword());
            profileUserType.setText(localUser.getUserType());
            profileLandingField.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getLandingFields()));
            for (int i = 0; i < profileLandingField.getCount(); i++) {
                if(profileLandingField.getItemAtPosition(i).equals(localUser.getLandingField().getAcronym()) || profileLandingField.getItemAtPosition(i).equals(localUser.getLandingField().getName())) {
                    profileLandingField.setSelection(i);
                    i = profileLandingField.getCount();
                }
            }
            profileLandingField.setEnabled(false);
            profileName.setText(localUser.getName());
            profileLastname.setText(localUser.getLastName());
            profileImage.setImageBitmap(Bitmap.createScaledBitmap(BDAdapter.stringToBitmap(localUser.getImage()), 300, 300, false));
        }
        floatingProfile.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if(data != null) profileImage.setImageBitmap(Bitmap.createScaledBitmap((Bitmap)data.getExtras().get("data"), 300, 300, false));
        }
        else if(requestCode == 1) {
            if(data != null) {
                try {
                    profileImage.setImageBitmap(Bitmap.createScaledBitmap(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData()), 300, 300, false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.profileImage:
                final PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.camera:
                                // Assume thisActivity is the current activity
                                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                                    startActivityForResult(intentCamera, 0);
                                }
                                else {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 17);
                                }
                                break;
                            case R.id.gallery:
                                Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                                intentGallery.setType("image/*");
                                startActivityForResult(intentGallery, 1);
                                break;
                            case R.id.delete:
                                profileImage.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource (getResources(), R.drawable.contact_image), 300, 300, false));
                                break;
                            case R.id.cancel:
                                popup.dismiss();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
                break;
            case R.id.floatingProfile:
                if(edit) {
                    edit = false;
                    profileName.setEnabled(true);
                    profileLastname.setEnabled(true);
                    profileLandingField.setEnabled(true);
                    profileUsername.setText("");
                    profileUsernameLayout.setHint(getString(R.string.profileEditUsername));
                    profileUsername.setEnabled(true);
                    profilePasswordLayout.setHint(getString(R.string.profileEditPassword));
                    profilePassword.setText("");
                    profilePassword.setEnabled(true);
                    profileRepeatPasswordLayout.setHint(getResources().getString(R.string.profileRepeatPassword));
                    profileRepeatPassword.setVisibility(View.VISIBLE);
                    floatingProfile.setImageResource(R.mipmap.ic_save);
                    profileImage.setOnClickListener(this);
                }
                else {
                    if(!profilePassword.getText().toString().isEmpty() && !profilePassword.getText().toString().equals(profileRepeatPassword.getText().toString())) {
                        profilePassword.setError(getResources().getString(R.string.profilePasswordError));
                    }
                    else {
                        localUser = new User(cursor.getInt(0), profileUsername.getText().toString(), profilePassword.getText().toString(),null, bdAdapter.getLandingFieldByName(profileLandingField.getSelectedItem().toString()), profileName.getText().toString(), profileLastname.getText().toString(), bdAdapter.bitmapToString(((BitmapDrawable) profileImage.getDrawable()).getBitmap()));
                        bdAdapter.updateLocalUser(localUser);
                        userListener.userUpdated();
                    }
                }
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            userListener = (OnUserListener) context;
        } catch (ClassCastException e) {}
    }
}
