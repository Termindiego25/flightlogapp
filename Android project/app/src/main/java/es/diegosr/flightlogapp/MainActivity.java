package es.diegosr.flightlogapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Calendar;
import java.util.Locale;

import es.diegosr.flightlogapp.fragments.FragmentProfile;
import es.diegosr.flightlogapp.fragments.FragmentRecycler;
import es.diegosr.flightlogapp.interfaces.OnBookingListener;
import es.diegosr.flightlogapp.interfaces.OnFlightListener;
import es.diegosr.flightlogapp.interfaces.OnUserListener;
import es.diegosr.flightlogapp.pojos.Booking;
import es.diegosr.flightlogapp.pojos.Flight;

public class MainActivity extends AppCompatActivity implements OnUserListener, OnFlightListener, OnBookingListener {
    Toolbar toolbar;
    ActionBar actionBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu optionsMenu;
    BDAdapter bdAdapter;
    Cursor cursor;
    AlertDialog dialogOrder, dialogFilter;
    AlertDialog.Builder builderOrder, builderFilter;

    FragmentManager fm;
    FragmentTransaction ft;
    FragmentRecycler fragmentRecycler;
    FragmentProfile fragmentProfile;

    String recyclerList, orderBy, filterBy;
    RadioGroup orderField, orderScope;
    TextInputEditText filterDateFrom, filterDateTo;
    Button orderButton, filterButton;
    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bdAdapter = new BDAdapter(this);
        fm = getSupportFragmentManager();
        builderOrder = new AlertDialog.Builder(this);
        builderFilter = new AlertDialog.Builder(this);
        orderBy = null;
        filterBy = null;

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        cursor = bdAdapter.getLocalUser();
        if(cursor != null && cursor.moveToFirst()) {
            ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.lateralHeaderImage)).setImageBitmap(Bitmap.createScaledBitmap(BDAdapter.stringToBitmap(cursor.getString(0)), 125, 125, false));
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.lateralheaderUserName)).setText(cursor.getString(1) + " " + cursor.getString(2));
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.lateralheaderUserType)).setText(cursor.getString(3));
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                for (int i = 0; i < navigationView.getMenu().size(); i++) {
                    if(navigationView.getMenu().getItem(i).isChecked()) navigationView.getMenu().getItem(i).setChecked(false);
                }
                item.setChecked(true);
                drawerLayout.closeDrawers();
                lateralMenuAction(item.getItemId(), null, null);
                return true;
            }
        });

        lateralMenuAction(R.id.lateralFlights, null, null);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        optionsMenu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.optionsFilter:
                final View viewFilter = getLayoutInflater().inflate(R.layout.dialog_filterby, null);
                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                myCalendar = Calendar.getInstance();
                filterDateFrom = viewFilter.findViewById(R.id.filterDateFrom);
                filterDateTo = viewFilter.findViewById(R.id.filterDateTo);
                filterButton = viewFilter.findViewById(R.id.filterButton);

                filterDateFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                filterDateFrom.setText(dateFormat.format(myCalendar.getTime()));
                            }
                        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                filterDateTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                filterDateTo.setText(dateFormat.format(myCalendar.getTime()));
                            }
                        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                filterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!filterDateFrom.getText().toString().isEmpty() && !filterDateTo.getText().toString().isEmpty()) {
                            if(recyclerList.equals("flights")) {
                                try {
                                    filterBy = "flights.departureDate >= '" + dateTimeFormat.format(dateFormat.parse(filterDateFrom.getText().toString())) + "' AND flights.arrivalDate <= '" + dateTimeFormat.format(dateFormat.parse(filterDateTo.getText().toString())) + "'";
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                lateralMenuAction(R.id.lateralFlights, orderBy, filterBy);
                            }
                            else if(recyclerList.equals("bookings")) {
                                try {
                                    filterBy = "bookings.date >= '" + dateTimeFormat.format(dateFormat.parse(filterDateFrom.getText().toString())) + "' AND bookings.date <= '" + dateTimeFormat.format(dateFormat.parse(filterDateTo.getText().toString())) + "'";
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                lateralMenuAction(R.id.lateralBookings, orderBy, filterBy);
                            }
                            dialogFilter.cancel();
                        }
                    }
                });

                dialogFilter = builderFilter.create();
                dialogFilter.setView(viewFilter);
                dialogFilter.setCancelable(true);
                dialogFilter.show();
                //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.optionsSort:
                final View viewOrder = getLayoutInflater().inflate(R.layout.dialog_orderby, null);
                orderField = viewOrder.findViewById(R.id.orderField);
                orderScope = viewOrder.findViewById(R.id.orderScope);
                orderButton = viewOrder.findViewById(R.id.orderButton);
                if(recyclerList.equals("flights")) {
                    ((RadioButton)viewOrder.findViewById(R.id.orderDate)).setText(getString(R.string.orderDepartureDate));
                    viewOrder.findViewById(R.id.orderPilot).setEnabled(false);
                }

                orderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (orderField.getCheckedRadioButtonId()) {
                            case R.id.orderDate:
                                if(recyclerList.equals("flights")) orderBy = "flights.departureDate ";
                                else if(recyclerList.equals("bookings")) orderBy = "bookings.date ";
                                break;
                            case R.id.orderPlane:
                                orderBy = "planes.model ";
                                break;
                            case R.id.orderDuration:
                                if(recyclerList.equals("flights")) orderBy = "flights.duration ";
                                else if(recyclerList.equals("bookings")) orderBy = "bookings.duration ";
                                break;
                            case R.id.orderPilot:
                                orderBy = "users.name ";
                                break;
                            default:
                                orderBy = null;
                                break;
                        }
                        if(orderBy != null) {
                            switch (orderScope.getCheckedRadioButtonId()) {
                                case R.id.orderAsc:
                                    orderBy += "ASC";
                                    break;
                                case R.id.orderDesc:
                                    orderBy += "DESC";
                                    break;
                            }
                            if(recyclerList.equals("flights")) lateralMenuAction(R.id.lateralFlights, orderBy, filterBy);
                            else if(recyclerList.equals("bookings")) lateralMenuAction(R.id.lateralBookings, orderBy, filterBy);
                            dialogOrder.cancel();
                        }
                    }
                });

                dialogOrder = builderOrder.create();
                dialogOrder.setView(viewOrder);
                dialogOrder.setCancelable(true);
                dialogOrder.show();
        }
        return super.onOptionsItemSelected(item);
    }
    public void lateralMenuAction(int item, String orderBy, String filterBy) {
        Bundle bundle = new Bundle();
        ft = fm.beginTransaction();

        switch(item) {
            case R.id.lateralFlights:
                recyclerList = "flights";
                if(optionsMenu != null) optionsMenu.findItem(R.id.moreOptions).setVisible(true);
                bundle.putString("recyclerList", recyclerList);
                bundle.putString("orderBy", orderBy);
                bundle.putString("filterBy", filterBy);
                fragmentRecycler = FragmentRecycler.createFragment(bundle);
                ft.replace(R.id.fragment, fragmentRecycler);
                break;
            case R.id.lateralBookings:
                recyclerList = "bookings";
                optionsMenu.findItem(R.id.moreOptions).setVisible(true);
                bundle.putString("recyclerList", recyclerList);
                bundle.putString("orderBy", orderBy);
                bundle.putString("filterBy", filterBy);
                fragmentRecycler = FragmentRecycler.createFragment(bundle);
                ft.replace(R.id.fragment, fragmentRecycler);
                break;
            case R.id.lateralProfile:
                recyclerList = "";
                optionsMenu.findItem(R.id.moreOptions).setVisible(false);
                fragmentProfile = new FragmentProfile();
                ft.replace(R.id.fragment, fragmentProfile);
                break;
            case R.id.lateralLogout:
                optionsMenu.findItem(R.id.moreOptions).setVisible(false);
                bdAdapter.deleteLocalUser();
                finish();
                break;
        }

        fm.popBackStack();
        ft.commit();
    }

    public void flightAdded(Flight flightData){
        bdAdapter.addFlight(flightData);
        lateralMenuAction(R.id.lateralFlights, null, null);
        Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.flightAdded), Snackbar.LENGTH_SHORT)
                .show();
    }
    public void flightUpdated(Flight flightData) {
        bdAdapter.updateFlight(flightData);
        lateralMenuAction(R.id.lateralFlights, null, null);
        Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.flightUpdated), Snackbar.LENGTH_SHORT)
                .show();
    }
    public void flightDeleted() {
        lateralMenuAction(R.id.lateralFlights, null, null);
        navigationView.getMenu().getItem(0).setChecked(true);
        Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.flightDeleted), Snackbar.LENGTH_SHORT)
                .show();
    }

    public void bookingAdded(Booking bookingData){
        bdAdapter.addBooking(bookingData);
        lateralMenuAction(R.id.lateralBookings, null, null);
        Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.bookingAdded), Snackbar.LENGTH_SHORT)
                .show();
    }
    public void bookingUpdated(Booking bookingData) {
        bdAdapter.updateBooking(bookingData);
        lateralMenuAction(R.id.lateralBookings, null, null);
        Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.bookingUpdated), Snackbar.LENGTH_SHORT)
                .show();
    }
    public void bookingDeleted() {
        lateralMenuAction(R.id.lateralBookings, null, null);
        navigationView.getMenu().getItem(1).setChecked(true);
        Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.bookingDeleted), Snackbar.LENGTH_SHORT)
                .show();
    }

    public void userUpdated() {
        cursor = bdAdapter.getLocalUser();
        if(cursor != null && cursor.moveToFirst()) {
            ((ImageView) navigationView.getHeaderView(0).findViewById(R.id.lateralHeaderImage)).setImageBitmap(BDAdapter.stringToBitmap(cursor.getString(0)));
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.lateralheaderUserName)).setText(String.format("? ?", new Object[]{cursor.getString(1), cursor.getString(2)}));
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.lateralheaderUserType)).setText(cursor.getString(3));
        }

        ft = fm.beginTransaction();
        fragmentProfile = new FragmentProfile();
        ft.replace(R.id.fragment, fragmentProfile);
        fm.popBackStack();
        ft.commit();
        Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.userUpdated), Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 17: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.permissionGranted), Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    Snackbar.make(findViewById(R.id.mainCoordinator), getString(R.string.permissionDenied), Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        moveTaskToBack(true);
    }
}