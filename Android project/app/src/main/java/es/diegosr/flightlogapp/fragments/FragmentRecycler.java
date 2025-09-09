package es.diegosr.flightlogapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import es.diegosr.flightlogapp.BDAdapter;
import es.diegosr.flightlogapp.R;
import es.diegosr.flightlogapp.RecyclerAdapter;
import es.diegosr.flightlogapp.interfaces.OnBookingListener;
import es.diegosr.flightlogapp.interfaces.OnFlightListener;
import es.diegosr.flightlogapp.pojos.Booking;
import es.diegosr.flightlogapp.pojos.Flight;
import es.diegosr.flightlogapp.pojos.User;

public class FragmentRecycler extends Fragment {
    BDAdapter bdAdapter;
    String recyclerList, orderBy, filterBy;
    Cursor cursor;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    FloatingActionButton floatingAdd;
    User localUser;

    Boolean editFlight, editBooking;
    Flight flightData;
    Booking bookingData;
    AlertDialog dialogFlight, dialogBooking, dialogDelete;
    AlertDialog.Builder builderFlight, builderBooking, builderDelete;
    Calendar myCalendar;
    OnFlightListener flightListener;
    OnBookingListener bookingListener;

    TextInputEditText fDepartureDate, fArrivalDate, fDuration, fDayLandings, fNightLandings;
    Spinner fDepartureSite, fArrivalSite, fPlane, fPilot;
    Switch fMonoPilot;
    TextInputEditText bDate, bDuration;
    Spinner bPlane, bPilot;
    Button fsendButton, bsendButton;

    public static FragmentRecycler createFragment(Bundle bundle) {
        FragmentRecycler fragmentRecycler = new FragmentRecycler();
        if(bundle != null) {
            fragmentRecycler.recyclerList = bundle.getString("recyclerList");
            fragmentRecycler.orderBy = bundle.getString("orderBy");
            fragmentRecycler.filterBy = bundle.getString("filterBy");
        }
        return fragmentRecycler;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bdAdapter = new BDAdapter(getContext());
        cursor = bdAdapter.getRecyclerList(recyclerList, orderBy, filterBy);
        localUser = bdAdapter.cursorToUser(bdAdapter.getProfile());
        builderFlight = new AlertDialog.Builder(getContext());
        builderBooking = new AlertDialog.Builder(getContext());
        builderDelete = new AlertDialog.Builder(getContext());
        editFlight = false;
        editBooking = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
        floatingAdd = rootView.findViewById(R.id.floatingAdd);
        recyclerView = rootView.findViewById(R.id.recycler);

        recyclerAdapter = new RecyclerAdapter(cursor, recyclerList, getContext());
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        floatingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerList.equals("flights")) {
                    final View view = getLayoutInflater().inflate(R.layout.dialog_flight, null);
                    myCalendar = Calendar.getInstance();
                    fDepartureDate = view.findViewById(R.id.dfDepartureDate);
                    fArrivalDate= view.findViewById(R.id.dfArrivalDate);
                    fDuration = view.findViewById(R.id.dfDuration);
                    fDayLandings = view.findViewById(R.id.dfDayLandings);
                    fNightLandings = view.findViewById(R.id.dfNightLandings);
                    fDepartureSite = view.findViewById(R.id.dfDepartureSite);
                    fArrivalSite = view.findViewById(R.id.dfArrivalSite);
                    fPlane = view.findViewById(R.id.dfPlane);
                    fPilot = view.findViewById(R.id.dfPilot);
                    fMonoPilot = view.findViewById(R.id.dfMonoPilot);
                    fsendButton = view.findViewById(R.id.dfSendButton);

                    fDepartureSite.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getLandingFields()));
                    fArrivalSite.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getLandingFields()));
                    fPlane.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getPlanes()));
                    fPilot.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getUsers()));
                    fMonoPilot.setChecked(true);
                    for (int i = 0; i < fPilot.getCount(); i++) {
                        if(fPilot.getItemAtPosition(i).equals(localUser.getName() + " " + localUser.getLastName())) {
                            fPilot.setSelection(i);
                            i = fPilot.getCount();
                        }
                    }
                    if(!localUser.getUserType().equals("controller")){
                        fPilot.setEnabled(false);
                    }
                    fDuration.setText("01:00");
                    fDayLandings.setText("1");
                    fNightLandings.setText("0");

                    fDepartureDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, monthOfYear);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            myCalendar.set(Calendar.MINUTE, minute);
                                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            fDepartureDate.setText(dateTimeFormat.format(myCalendar.getTime()));
                                        }
                                    }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
                                }
                            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });
                    fArrivalDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, monthOfYear);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            myCalendar.set(Calendar.MINUTE, minute);
                                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            fArrivalDate.setText(dateTimeFormat.format(myCalendar.getTime()));
                                        }
                                    }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
                                }
                            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });
                    fsendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                            if (editFlight) {
                                try {
                                    flightData.setDepartureSite(bdAdapter.getLandingFieldByName(fDepartureSite.getSelectedItem().toString()));
                                    flightData.setDepartureDate(dateTimeFormat.parse(fDepartureDate.getText().toString()));
                                    flightData.setArrivalSite(bdAdapter.getLandingFieldByName(fArrivalSite.getSelectedItem().toString()));
                                    flightData.setArrivalDate(dateTimeFormat.parse(fArrivalDate.getText().toString()));
                                    flightData.setPlane(bdAdapter.getPlaneByName(fPlane.getSelectedItem().toString()));
                                    flightData.setDuration(new Time(timeFormat.parse(fDuration.getText().toString()).getTime()));
                                    flightData.setPilot(bdAdapter.getUserByName(fPilot.getSelectedItem().toString()));
                                    flightData.setMonoPilot(Boolean.valueOf(fMonoPilot.getText().toString()));
                                    flightData.setDayLandings(Integer.parseInt(fDayLandings.getText().toString()));
                                    flightData.setNightLandings(Integer.parseInt(fNightLandings.getText().toString()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                try {
                                    flightData = new Flight(
                                            bdAdapter.getLandingFieldByName(fDepartureSite.getSelectedItem().toString()),
                                            dateTimeFormat.parse(fDepartureDate.getText().toString()),
                                            bdAdapter.getLandingFieldByName(fArrivalSite.getSelectedItem().toString()),
                                            dateTimeFormat.parse(fArrivalDate.getText().toString()),
                                            bdAdapter.getPlaneByName(fPlane.getSelectedItem().toString()),
                                            new Time(timeFormat.parse(fDuration.getText().toString()).getTime()),
                                            bdAdapter.getUserByName(fPilot.getSelectedItem().toString()),
                                            fMonoPilot.isChecked(),
                                            Integer.parseInt(fDayLandings.getText().toString()),
                                            Integer.parseInt(fNightLandings.getText().toString())
                                    );
                                } catch (ParseException e) {
                                    Snackbar.make(rootView.findViewById(R.id.recyclerCoordinator), "Incorrect data format.\nPlease check duration format is 00:00 and day or night landings are numbers.", Snackbar.LENGTH_SHORT)
                                            .show();
                                }
                            }
                            dialogFlight.cancel();
                            flightListener.flightAdded(flightData);
                        }
                    });

                    dialogFlight = builderFlight.create();
                    dialogFlight.setView(view);
                    dialogFlight.setCancelable(true);
                    dialogFlight.show();
                }
                else if(recyclerList.equals("bookings")) {
                    final View view = getLayoutInflater().inflate(R.layout.dialog_booking, null);
                    myCalendar = Calendar.getInstance();
                    bPilot = view.findViewById(R.id.dbPilot);
                    bPlane = view.findViewById(R.id.dbPlane);
                    bDate = view.findViewById(R.id.dbDate);
                    bDuration = view.findViewById(R.id.dbDuration);
                    bsendButton = view.findViewById(R.id.dbSendButton);

                    bPlane.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getPlanes()));
                    bPilot.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getUsers()));
                    for (int i = 0; i < bPilot.getCount(); i++) {
                        if(bPilot.getItemAtPosition(i).equals(localUser.getName() + " " + localUser.getLastName())) {
                            bPilot.setSelection(i);
                            i = bPilot.getCount();
                        }
                    }
                    if(!localUser.getUserType().equals("controller")){
                        bPilot.setEnabled(false);
                    }
                    bDuration.setText("01:00");

                    bDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, monthOfYear);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            myCalendar.set(Calendar.MINUTE, minute);
                                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            bDate.setText(dateTimeFormat.format(myCalendar.getTime()));
                                        }
                                    }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
                                }
                            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });
                    bsendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                            if (editBooking) {
                                try {
                                    bookingData.setPilot(bdAdapter.getUserByName(bPilot.getSelectedItem().toString()));
                                    bookingData.setPlane(bdAdapter.getPlaneByName(bPlane.getSelectedItem().toString()));
                                    bookingData.setDate(dateTimeFormat.parse(bDate.getText().toString()));
                                    bookingData.setDuration(new Time(timeFormat.parse(bDuration.getText().toString()).getTime()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                try {
                                    bookingData = new Booking(
                                            bdAdapter.getUserByName(bPilot.getSelectedItem().toString()),
                                            bdAdapter.getPlaneByName(bPlane.getSelectedItem().toString()),
                                            dateTimeFormat.parse(bDate.getText().toString()),
                                            new Time(timeFormat.parse(bDuration.getText().toString()).getTime())
                                    );
                                } catch (ParseException e) {
                                    Snackbar.make(rootView.findViewById(R.id.recyclerCoordinator), "Incorrect duration format.\nPlease provide 00:00 format.", Snackbar.LENGTH_SHORT)
                                        .show();
                                }
                            }
                            dialogBooking.cancel();
                            bookingListener.bookingAdded(bookingData);
                        }
                    });

                    dialogBooking = builderBooking.create();
                    dialogBooking.setView(view);
                    dialogBooking.setCancelable(true);
                    dialogBooking.show();
                }
            }
        });
        recyclerAdapter.MyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerList.equals("flights")) {
                    final View view = getLayoutInflater().inflate(R.layout.dialog_flight, null);
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    myCalendar = Calendar.getInstance();
                    fDepartureDate = view.findViewById(R.id.dfDepartureDate);
                    fArrivalDate= view.findViewById(R.id.dfArrivalDate);
                    fDuration = view.findViewById(R.id.dfDuration);
                    fDayLandings = view.findViewById(R.id.dfDayLandings);
                    fNightLandings = view.findViewById(R.id.dfNightLandings);
                    fDepartureSite = view.findViewById(R.id.dfDepartureSite);
                    fArrivalSite = view.findViewById(R.id.dfArrivalSite);
                    fPlane = view.findViewById(R.id.dfPlane);
                    fPilot = view.findViewById(R.id.dfPilot);
                    fMonoPilot = view.findViewById(R.id.dfMonoPilot);
                    fsendButton = view.findViewById(R.id.dfSendButton);
                    cursor.moveToPosition(recyclerView.getChildAdapterPosition(v));
                    flightData = bdAdapter.cursorToFlight(cursor);

                    fDepartureSite.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getLandingFields()));
                    for (int i = 0; i < fDepartureSite.getCount(); i++) {
                        if(fDepartureSite.getItemAtPosition(i).equals(flightData.getDepartureSite().getAcronym()) || fDepartureSite.getItemAtPosition(i).equals(flightData.getDepartureSite().getName())) {
                            fDepartureSite.setSelection(i);
                            i = fDepartureSite.getCount();
                        }
                    }
                    fDepartureDate.setText(dateTimeFormat.format(flightData.getDepartureDate()));
                    fArrivalSite.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getLandingFields()));
                    for (int i = 0; i < fArrivalSite.getCount(); i++) {
                        if(fArrivalSite.getItemAtPosition(i).equals(flightData.getArrivalSite().getAcronym()) || fArrivalSite.getItemAtPosition(i).equals(flightData.getArrivalSite().getName())) {
                            fArrivalSite.setSelection(i);
                            i = fArrivalSite.getCount();
                        }
                    }
                    fArrivalDate.setText(dateTimeFormat.format(flightData.getArrivalDate()));
                    fPlane.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getPlanes()));
                    for (int i = 0; i < fPlane.getCount(); i++) {
                        if(fPlane.getItemAtPosition(i).equals(flightData.getPlane().getPlate() + " - " + flightData.getPlane().getModel())) {
                            fPlane.setSelection(i);
                            i = fPlane.getCount();
                        }
                    }
                    fPilot.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getUsers()));
                    for (int i = 0; i < fPilot.getCount(); i++) {
                        if(fPilot.getItemAtPosition(i).equals(localUser.getName() + " " + localUser.getLastName())) {
                            fPilot.setSelection(i);
                            i = fPilot.getCount();
                        }
                    }
                    fMonoPilot.setChecked(flightData.getMonoPilot());
                    fDuration.setText(timeFormat.format(flightData.getDuration()));
                    fDayLandings.setText(Integer.toString(flightData.getDayLandings()));
                    fNightLandings.setText(Integer.toString(flightData.getNightLandings()));

                    fDepartureDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, monthOfYear);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            myCalendar.set(Calendar.MINUTE, minute);
                                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            fDepartureDate.setText(dateTimeFormat.format(myCalendar.getTime()));
                                        }
                                    }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
                                }
                            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });
                    fArrivalDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, monthOfYear);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                    new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            myCalendar.set(Calendar.MINUTE, minute);
                                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                            fArrivalDate.setText(dateTimeFormat.format(myCalendar.getTime()));
                                        }
                                    }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
                                }
                            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        }
                    });
                    fsendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                            try {
                                    flightData.setDepartureSite(bdAdapter.getLandingFieldByName(fDepartureSite.getSelectedItem().toString()));
                                    flightData.setDepartureDate(dateTimeFormat.parse(fDepartureDate.getText().toString()));
                                    flightData.setArrivalSite(bdAdapter.getLandingFieldByName(fArrivalSite.getSelectedItem().toString()));
                                    flightData.setArrivalDate(dateTimeFormat.parse(fArrivalDate.getText().toString()));
                                    flightData.setPlane(bdAdapter.getPlaneByName(fPlane.getSelectedItem().toString()));
                                    flightData.setDuration(new Time(timeFormat.parse(fDuration.getText().toString()).getTime()));
                                    flightData.setPilot(bdAdapter.getUserByName(fPilot.getSelectedItem().toString()));
                                    flightData.setMonoPilot(Boolean.valueOf(fMonoPilot.getText().toString()));
                                    flightData.setDayLandings(Integer.parseInt(fDayLandings.getText().toString()));
                                    flightData.setNightLandings(Integer.parseInt(fNightLandings.getText().toString()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            dialogFlight.cancel();
                            flightListener.flightUpdated(flightData);
                        }
                    });

                    dialogFlight = builderFlight.create();
                    dialogFlight.setView(view);
                    dialogFlight.setCancelable(true);
                    dialogFlight.show();
                }
                else if(recyclerList.equals("bookings")) {
                    bookingData = bdAdapter.cursorToBooking(cursor);
                    if(bookingData.getPilot().getId() == localUser.getId() || localUser.getUserType().equals("controller")) {
                        final View view = getLayoutInflater().inflate(R.layout.dialog_booking, null);
                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        myCalendar = Calendar.getInstance();
                        bPilot = view.findViewById(R.id.dbPilot);
                        bPlane = view.findViewById(R.id.dbPlane);
                        bDate = view.findViewById(R.id.dbDate);
                        bDuration = view.findViewById(R.id.dbDuration);
                        bsendButton = view.findViewById(R.id.dbSendButton);
                        cursor.moveToPosition(recyclerView.getChildAdapterPosition(v));

                        bPlane.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getPlanes()));
                        for (int i = 0; i < bPlane.getCount(); i++) {
                            if (bPlane.getItemAtPosition(i).equals(bookingData.getPlane().getPlate() + " - " + bookingData.getPlane().getModel())) {
                                bPlane.setSelection(i);
                                i = bPlane.getCount();
                            }
                        }
                        bPilot.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bdAdapter.getUsers()));
                        for (int i = 0; i < bPilot.getCount(); i++) {
                            if (bPilot.getItemAtPosition(i).equals(bookingData.getPilot().getName() + " " + bookingData.getPilot().getLastName())) {
                                bPilot.setSelection(i);
                                i = bPilot.getCount();
                            }
                        }
                        if (!localUser.getUserType().equals("controller")) {
                            bPilot.setEnabled(false);
                        }
                        bDate.setText(dateTimeFormat.format(bookingData.getDate()));
                        bDuration.setText(timeFormat.format(bookingData.getDuration()));

                        bDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        myCalendar.set(Calendar.YEAR, year);
                                        myCalendar.set(Calendar.MONTH, monthOfYear);
                                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                myCalendar.set(Calendar.MINUTE, minute);
                                                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                bDate.setText(dateTimeFormat.format(myCalendar.getTime()));
                                            }
                                        }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
                                    }
                                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                            }
                        });
                        bsendButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                try {
                                    bookingData.setPilot(bdAdapter.getUserByName(bPilot.getSelectedItem().toString()));
                                    bookingData.setPlane(bdAdapter.getPlaneByName(bPlane.getSelectedItem().toString()));
                                    bookingData.setDate(dateTimeFormat.parse(bDate.getText().toString()));
                                    bookingData.setDuration(new Time(timeFormat.parse(bDuration.getText().toString()).getTime()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                dialogBooking.cancel();
                                bookingListener.bookingUpdated(bookingData);
                            }
                        });

                        dialogBooking = builderBooking.create();
                        dialogBooking.setView(view);
                        dialogBooking.setCancelable(true);
                        dialogBooking.show();
                    }
                    else Snackbar.make(rootView.findViewById(R.id.recyclerCoordinator), getString(R.string.editBookingPermissionsError), Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
        recyclerAdapter.MyLongClick(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                cursor.moveToPosition(recyclerView.getChildAdapterPosition(view));
                if(recyclerList.equals("flights")) {
                    flightData = bdAdapter.cursorToFlight(cursor);
                    builderDelete.setMessage(String.format(getString(R.string.deleteFlight), flightData.getDepartureSite().getName(), flightData.getArrivalSite().getName()))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.deleteButtonText), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bdAdapter.deleteFlight(flightData.getId());
                                    flightListener.flightDeleted();
                                }
                            })
                            .setNegativeButton(getString(R.string.cancelButtonText), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                }
                else if(recyclerList.equals("bookings")) {
                    bookingData = bdAdapter.cursorToBooking(cursor);
                    builderDelete.setMessage(String.format(getString(R.string.deleteBooking), bookingData.getPlane().getPlate()))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.deleteButtonText), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    bdAdapter.deleteBooking(bookingData.getId());
                                    bookingListener.bookingDeleted();
                                }
                            })
                            .setNegativeButton(getString(R.string.cancelButtonText), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                }
                dialogDelete = builderDelete.create();
                dialogDelete.show();
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            flightListener = (OnFlightListener) context;
            bookingListener = (OnBookingListener) context;
        } catch (ClassCastException e) {}
    }
}
